package com.isport.crawl.netease;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.geccocrawler.gecco.pipeline.JsonPipeline;
import com.geccocrawler.gecco.redis.RedisStartScheduler;
import com.geccocrawler.gecco.request.HttpGetRequest;
import com.geccocrawler.gecco.request.HttpRequest;
import com.geccocrawler.gecco.scheduler.DeriveSchedulerContext;
import com.isport.Constants;
import com.isport.crawl.distribute.SchedulerSingle;
import com.isport.crawl.distribute.TaskService;
import com.isport.service.NewsService;
import com.isport.utils.InitParamService;
import com.isport.utils.StringUtils;

@Service
public class NeteaseJsonListDynamicPipeline extends JsonPipeline {

	private static final Logger LOGGER = LoggerFactory.getLogger(NeteaseJsonListDynamicPipeline.class);

	public static List<HttpRequest> sortRequests = new ArrayList<HttpRequest>();

	// 默认设置：第一次爬取时，默认爬取的最大页数
	public static int DEFAULT_MAX_PAGE = 2;

	@Autowired
	NewsService newsService;

	// redis 主机
	@Value("${spring.redis.host}")
	private String redis_host;
	// redis 端口
	@Value("${spring.redis.port}")
	private String redis_port;
	@Autowired
	SchedulerSingle schedulerSingle;
	@Override
	public void process(JSONObject job) {
		// 分布式任务管理
		TaskService taskService = TaskService.create()
				.scheduler(schedulerSingle.getInstance());
		if (job != null) {
			SimpleDateFormat olddf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss"),
					df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			try {
				boolean nextFlag = true;
				HttpRequest currRequest = HttpGetRequest.fromJson(job.getJSONObject("request"));
				String sourceKey = currRequest.getParameter("source_key");
				String currUrl = currRequest.getUrl();
				LOGGER.info(currUrl);
				long updateStamp, endStamp;
				int page = StringUtils.isNUll(job.getString("page")) ? 1
						: Integer.parseInt(job.getString("page").substring(1));
				if (page == 1) {
					updateStamp = new Date().getTime();
					String lastDate = newsService.getLastUpdateTime(sourceKey);
					endStamp = StringUtils.isNUll(lastDate) ? 0 : df.parse(lastDate).getTime();
				} else {
					updateStamp = Long.parseLong(currRequest.getParameter("stime"));
					endStamp = Long.parseLong(currRequest.getParameter("etime"));
				}
				String json = StringUtils.getJsonFromString(job.getString("content"), "(", ")");
				json = json.replaceAll("<a href[^>]*>", "").replaceAll("</a>", "").replaceAll("\n", "");
				JSONArray jsonArray = JSONObject.parseArray(json);
				here: for (int i = 0; i < jsonArray.size(); i++) {
					JSONObject itemObject = JSONObject.parseObject(jsonArray.get(i).toString());
					Date date = olddf.parse(itemObject.getString("time"));
					// 新闻发布时间过滤（首次爬取不进行时间过滤endStamp=0）
					if (endStamp > 0 && date.getTime() <= endStamp) {
						if (page == 1 && i < 20) {
							continue;
						} else {
							nextFlag = false;
							break here;
						}
					}
					// 新闻类型过滤
					String label = itemObject.getString("label");
					String sport_item = InitParamService.neteaseSportItemMap.get(label);
					String newstype = itemObject.getString("newstype");
					if (!newstype.equals(Constants.NETEASE_NEWSSTYLE_ARTICLE.value) || sport_item == null) {
						LOGGER.info("Skip current item, it's not an article.");
						continue;
					}
					String docurl = itemObject.getString("docurl");
					HttpRequest detailRequest = currRequest.subRequest(docurl);
					detailRequest.addParameter("index_url", currUrl);
					detailRequest.addParameter("pubdate", df.format(date));
					//sortRequests.add(detailRequest);
					//DeriveSchedulerContext.into(detailRequest);
					taskService.insert(detailRequest);
				}
				// 下一页继续抓取
				if (nextFlag && page < DEFAULT_MAX_PAGE) {
					String nextUrl = currUrl.replace(page == 1 ? ".js" : "_" + String.format("%02d", page) + ".js",
							"_" + String.format("%02d", page + 1) + ".js");
					HttpRequest nextRequest = currRequest.subRequest(nextUrl);
					if (page == 1) {
						nextRequest.addParameter("stime", String.valueOf(updateStamp));
						nextRequest.addParameter("etime", String.valueOf(endStamp));
					}
					//DeriveSchedulerContext.into(nextRequest);
					taskService.insert(nextRequest);
				} else {
					newsService.setLastUpdateTime(sourceKey, df.format(updateStamp));
				}
			} catch (ParseException e) {
				e.printStackTrace();
				LOGGER.error(e.getMessage());
			}
		}
	}

}
