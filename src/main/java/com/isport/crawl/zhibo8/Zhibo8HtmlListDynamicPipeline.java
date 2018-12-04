package com.isport.crawl.zhibo8;

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
import com.isport.crawl.distribute.SchedulerSingle;
import com.isport.crawl.distribute.TaskService;
import com.isport.service.NewsService;
import com.isport.utils.StringUtils;

/**
 * 说明：直播8的新闻列表，首页显示最近10天的数据，每次加载更多，自动加载前一天的新闻（若前一天无数据，自动加载再前一天的，依此往复）
 * 因此：1.直播8只爬取首页新闻
 */
@Service
public class Zhibo8HtmlListDynamicPipeline extends JsonPipeline {

	private static final Logger LOGGER = LoggerFactory.getLogger(Zhibo8HtmlListDynamicPipeline.class);

	public static List<HttpRequest> sortRequests = new ArrayList<HttpRequest>();

	// 默认设置：第一次爬取时，默认爬取的最大页数
	public static int DEFAULT_MAX_PAGE = 1;

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
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			try {
				boolean nextFlag = true;
				HttpRequest currRequest = HttpGetRequest.fromJson(job.getJSONObject("request"));
				String sourceKey = currRequest.getParameter("source_key");
				String currUrl = currRequest.getUrl();
				long updateStamp, endStamp;
				int page = StringUtils.isNUll(job.getString("page")) ? 1
						: Integer.parseInt(job.getString("page").substring(1));
				if (page == 1) {
					updateStamp = new Date().getTime();
					String lastDate = newsService.getLastUpdateTime(sourceKey);
					endStamp = StringUtils.isNUll(lastDate) ? 0 : df.parse(lastDate).getTime();
					LOGGER.info(currUrl + " start...", Zhibo8HtmlListDynamicPipeline.class);
				} else {
					updateStamp = Long.parseLong(currRequest.getParameter("stime"));
					endStamp = Long.parseLong(currRequest.getParameter("etime"));
				}
				// 新闻板块
				JSONArray jsonArray = job.getJSONArray("newslist");
				here: for (int i = 0; i < jsonArray.size(); i++) {
					JSONObject itemObject = JSONObject.parseObject(jsonArray.get(i).toString());
					// LOGGER.info(itemObject.toString());
					// 新闻发布时间过滤（首次爬取不进行时间过滤endStamp=0）
					String pubDate = itemObject.getString("pubdate");
					if (endStamp > 0 && df.parse(pubDate).getTime() <= endStamp) {
						nextFlag = false;
						break here;
					}
					String docurl = "https:" + itemObject.getString("docurl");
					HttpRequest detailRequest = currRequest.subRequest(docurl);
					detailRequest.addParameter("index_url", currUrl);
					detailRequest.addParameter("pubdate", pubDate);
					//sortRequests.add(detailRequest);
//					DeriveSchedulerContext.into(detailRequest);
					taskService.insert(detailRequest);
				}
				// 下一页继续抓取
				String nextUrl = this.getNextUrl(currUrl, page);
				if (nextFlag && page < DEFAULT_MAX_PAGE && !StringUtils.isNUll(nextUrl)) {
					HttpRequest nextRequest = currRequest.subRequest(nextUrl);
					if (page == 1) {
						nextRequest.addParameter("stime", String.valueOf(updateStamp));
						nextRequest.addParameter("etime", String.valueOf(endStamp));
					}
//					DeriveSchedulerContext.into(nextRequest);
					taskService.insert(nextRequest);
					LOGGER.info(nextRequest + " next...", Zhibo8HtmlListDynamicPipeline.class);
				} else {
					newsService.setLastUpdateTime(sourceKey, df.format(updateStamp));
					LOGGER.info(currUrl + " end...", Zhibo8HtmlListDynamicPipeline.class);
				}
			} catch (ParseException e) {
				e.printStackTrace();
				LOGGER.error(e.getMessage());
			}
		}
	}

	private String getNextUrl(String currUrl, int page) {
		return "";
	}

}
