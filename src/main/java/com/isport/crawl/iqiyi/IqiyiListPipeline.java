package com.isport.crawl.iqiyi;

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
import com.isport.crawl.goal.GoalListPipeline;
import com.isport.service.NewsService;
import com.isport.utils.StringUtils;

@Service
public class IqiyiListPipeline extends JsonPipeline {
	// 私有日志组件
	private static final Logger LOGGER = LoggerFactory.getLogger(GoalListPipeline.class);
	// 详情页队列
	public static List<HttpRequest> sortRequests = new ArrayList<HttpRequest>();
	// 默认设置：第一次爬取时，默认爬取的最大页数
	private static final int DEFAULT_MAX_PAGE = 2;
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
	public void process(JSONObject jo) {
		// 分布式任务管理
		TaskService taskService = TaskService.create()
				.scheduler(schedulerSingle.getInstance());

		if (jo != null) {
			try {
				// 日期格式化
				final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				HttpRequest request = HttpGetRequest.fromJson(jo.getJSONObject("request"));
				// 来源key 更新时间查询关键字, 当前请求页
				String sourceKey = request.getParameter("source_key"), strPage = request.getParameter("page");
				// 请求地址 父地址
				String url = request.getUrl();
				// 上次数据库更新时间 ,本次抓取事件
				long lastUpdateTime = 0, crawlTime = 0;
				// 类型转换
				int page = StringUtils.isNUll(strPage) ? 1 : Integer.valueOf(strPage);
				// 抓取时间设置
				if (page == 1) {
					// 当前时间
					crawlTime = new Date().getTime();
					String strLastUpdateTime = newsService.getLastUpdateTime(sourceKey);
					lastUpdateTime = StringUtils.isNUll(strLastUpdateTime) ? 0 : sdf.parse(strLastUpdateTime).getTime();
				} else {
					crawlTime = Long.valueOf(request.getParameter("crawlTime"));
					lastUpdateTime = Long.valueOf(request.getParameter("lastUpdateTime"));
				}

				// 新闻时间判断
				JSONArray newsList = jo.getJSONArray("newsList");
//				LOGGER.info("newsList size:" + newsList.size());
				for (Object news : newsList) {
//					LOGGER.info(news.toString()); 
					JSONObject ulList = JSONObject.parseObject(news.toString());
					JSONArray lisList = ulList.getJSONArray("ulList");
//					LOGGER.info("lisList size:" + lisList.size());
					for (Object li : lisList) {
						JSONObject item = JSONObject.parseObject(li.toString());
//						LOGGER.info("item:" + item.toString());
						// 过滤广告
						if (item.isEmpty()) {
							continue;
						}
						// 新闻发布时间
						SimpleDateFormat sdf2 = new SimpleDateFormat("MM-dd HH:mm");
						String pubDate = item.getString("pubDate");
						long newsTime = sdf2.parse(pubDate).getTime();
						// 非第一次抓去并且新闻时间小于最近更新抓取时间
						if (lastUpdateTime > 0 && newsTime < lastUpdateTime) {
							if (page == 1) {
								// 第一页继续比对
								continue;
							} else {
								// 非第一页不在抓取详情和派生页
								newsService.setLastUpdateTime(sourceKey, sdf.format(crawlTime));
								LOGGER.info("抓取提前结束，无法抓到更多新的新闻");
								return;
							}
						}
						String docUrl = item.getString("docUrl");
						HttpRequest subRequest = request.subRequest(docUrl);
						subRequest.addParameter("index_url", url);
//						sortRequests.add(subRequest);
						taskService.insert(subRequest);
					}
				}

				// 派生地址
				if (page < DEFAULT_MAX_PAGE) {
					// 生成下一页
					++page;
					String nextUrl = url.replaceAll("_\\d+.html", "_" + String.valueOf(page) + ".html");
					LOGGER.info("nextUrl:" + nextUrl);
					HttpRequest nextRequest = request.subRequest(nextUrl);
					nextRequest.addParameter("page", String.valueOf(page));
					nextRequest.addParameter("crawlTime", String.valueOf(crawlTime));
					nextRequest.addParameter("lastUpdateTime", String.valueOf(lastUpdateTime));
//					DeriveSchedulerContext.into(nextRequest);
					taskService.insert(nextRequest);
				}
				newsService.setLastUpdateTime(sourceKey, sdf.format(crawlTime));
			} catch (Exception e) {
				e.printStackTrace();
				LOGGER.error(e.getMessage());
			}
		}
	}

}
