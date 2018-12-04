package com.isport.crawl.hupu;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.geccocrawler.gecco.pipeline.JsonPipeline;
import com.geccocrawler.gecco.request.HttpGetRequest;
import com.geccocrawler.gecco.request.HttpRequest;
import com.geccocrawler.gecco.scheduler.DeriveSchedulerContext;
import com.isport.crawl.goal.GoalListPipeline;
import com.isport.service.NewsService;
import com.isport.utils.DateUtils;
import com.isport.utils.StringUtils;

@Service
public class HupuSoccerListPipeline extends JsonPipeline {
	// 私有日志组件
	private static final Logger LOGGER = LoggerFactory.getLogger(GoalListPipeline.class);
	// 详情页队列
	public static List<HttpRequest> sortRequests = new ArrayList<HttpRequest>();

	// 默认设置：第一次爬取时，默认爬取的最大页数
	private static final int DEFAULT_MAX_PAGE = 2;

	@Autowired
	NewsService newsService;

	@Override
	public void process(JSONObject jo) {
		if (jo != null) {
			try {
				// 请求对象
				HttpRequest request = HttpGetRequest.fromJson(jo.getJSONObject("request"));
				// 来源key 更新时间查询关键字, 当前请求页
				String sourceKey = request.getParameter("source_key"), strPage = request.getParameter("page");
				// 请求地址 父地址
				String url = request.getUrl();
				// 上次数据库更新时间 ,本次抓取事件
				long lastUpdateTime = 0, crawlTime = 0;
				// 当前页数
				int page = StringUtils.isNUll(strPage) ? 1 : Integer.valueOf(strPage);
				// 日期格式化
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
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
				for (Object news : newsList) {
					JSONObject item = JSONObject.parseObject(news.toString());
//					LOGGER.info("item:" + item.toString());
					// 过滤广告
					if (item.isEmpty()) {
						continue;
					}
					// 新闻发布时间
					long newsTime = 0;
					try {
						newsTime = timeConvert(item.getString("pubDate"));
					} catch (Exception e) {
						LOGGER.info("日期获取失败");
						return;
					}
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

					// 详情地址设置
					String docUrl = item.getString("docUrl");
					HttpRequest subRequest = request.subRequest(docUrl);
					subRequest.addParameter("index_url", url);
					sortRequests.add(subRequest);
				}

				// 派生地址生成
				if (page < DEFAULT_MAX_PAGE) {
					++page;
					String nextUrl = "", subUrl = "";
					// https://voice.hupu.com/soccer/germany https://voice.hupu.com/soccer/italy
					if (url.contains("germany") || url.contains("italy")) {
						if (page == 2) {
							nextUrl = url + "/" + page;
						} else {
							final int cronIndex = url.lastIndexOf("/");
							subUrl = url.substring(0, cronIndex);
							nextUrl = subUrl + "/" + page;
						}
				    // https://voice.hupu.com/china/tag/72134.html
					} else if (url.contains("china")) {
						final int cronIndex = url.lastIndexOf(".");
						subUrl = url.substring(0, cronIndex);
						nextUrl = subUrl + "-" + page + ".html";
					} else {
						final int cronIndex = url.indexOf("-");
						final int pointIndex = url.lastIndexOf(".");
						if (cronIndex > 0) {
							subUrl = url.substring(0, cronIndex);
						} else {
							subUrl = url.substring(0, pointIndex);
						}
						nextUrl = subUrl + "-" + page + ".html";
					}

					LOGGER.info("nextUrl:" + nextUrl);
					HttpRequest nextRequest = request.subRequest(nextUrl);
					nextRequest.addParameter("page", String.valueOf(page));
					nextRequest.addParameter("crawlTime", String.valueOf(crawlTime));
					nextRequest.addParameter("lastUpdateTime", String.valueOf(lastUpdateTime));
					DeriveSchedulerContext.into(nextRequest);
				}
			} catch (Exception e) {
				e.printStackTrace();
				LOGGER.error(e.getMessage());
			}
		}
	}

	// 时间转换
	public long timeConvert(final String strDate) throws ParseException {
		// 下标结束地址
		int endIndex = strDate.length();
		// 分钟前的下标
		final int minuteIndex = strDate.indexOf("分钟前");
		// 日期格式化
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// 截取并转换时间
		if (minuteIndex > 0) {
			// 截取分钟值
			String strMinute = strDate.substring(0, minuteIndex);
			int minute = Integer.valueOf(strMinute);
			String time = DateUtils.getbeforeMinute(minute);
			return sdf.parse(time).getTime();
		}
		final int hourIndex = strDate.indexOf("小时前");
		if (hourIndex > 0) {
			// 截取小时
			String strHour = strDate.substring(0, hourIndex);
			int hour = Integer.valueOf(strHour);
			String time = DateUtils.getbeforeHour(hour);
			return sdf.parse(time).getTime();
		}
		// 截取月/日 值
		final int monthIndex = strDate.indexOf("月");
		String strMonth = strDate.substring(0, monthIndex);
		final int dayIndex = strDate.indexOf("日");
		String strDay = strDate.substring(monthIndex + 1, dayIndex);
		String strhm = strDate.substring(dayIndex + 1, endIndex).trim();
		String strTime = strMonth + "-" + strDay + " " + strhm;
		SimpleDateFormat sdf2 = new SimpleDateFormat("MM-dd HH:mm");
		return sdf2.parse(strTime).getTime();
	}

}
