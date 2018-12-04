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
import com.isport.crawl.distribute.SchedulerSingle;
import com.isport.crawl.distribute.TaskService;
import com.isport.service.NewsService;
import com.isport.utils.StringUtils;

@Service
public class NeteaseHtmlListDynamicPipeline extends JsonPipeline {

	private static final Logger LOGGER = LoggerFactory.getLogger(NeteaseHtmlListDynamicPipeline.class);

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
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
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
				// 新闻板块
				JSONArray jsonArray = job.getJSONArray("newslist");
				here: for (int i = 0; i < jsonArray.size(); i++) {
					JSONObject itemObject = JSONObject.parseObject(jsonArray.get(i).toString());
					Date date = df.parse(itemObject.getString("pubdate"));
					// 新闻发布时间过滤（首次爬取不进行时间过滤endStamp=0）
					if (endStamp > 0 && date.getTime() <= endStamp) {
						if (page == 1 && i < 20) {
							continue;
						} else {
							nextFlag = false;
							break here;
						}
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
				String nextUrl = this.getNextUrl(currUrl, page);
				if (nextFlag && page < DEFAULT_MAX_PAGE && !StringUtils.isNUll(nextUrl)) {
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

	private String getNextUrl(String currUrl, int page) {
		String nextUrl = "";
		String lastChr = currUrl.substring(currUrl.length() - 1);
		switch (lastChr) {
		// /
		case "/":
			nextUrl = page == 1 ? currUrl.substring(0, currUrl.length() - 1) + "_02/"
					: currUrl.replace("_" + String.format("%02d", page), "_" + String.format("%02d", page + 1));
			break;
		// html
		case "l":
			nextUrl = currUrl.replace(page == 1 ? ".html" : "_" + String.format("%02d", page) + ".html",
					"_" + String.format("%02d", page + 1) + ".html");
			break;
		default:
			break;
		}
		return nextUrl;
	}

}
