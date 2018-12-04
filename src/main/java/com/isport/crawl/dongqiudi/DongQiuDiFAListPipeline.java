package com.isport.crawl.dongqiudi;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.geccocrawler.gecco.pipeline.JsonPipeline;
import com.geccocrawler.gecco.request.HttpGetRequest;
import com.geccocrawler.gecco.request.HttpRequest;
import com.geccocrawler.gecco.scheduler.DeriveSchedulerContext;
import com.google.gson.Gson;
import com.isport.bean.DongQiuBean;
import com.isport.service.NewsService;
import com.isport.utils.MongoTemUtil;
import com.isport.utils.StringUtils;

@Service
public class DongQiuDiFAListPipeline extends JsonPipeline {
	@Autowired
	NewsService newsService;

	// 默认设置：第一次爬取时，默认爬取的最大页数
	private static final int DEFAULT_MAX_PAGE = 2;
	@Autowired
	MongoTemUtil mongoTemUtil;
	public static List<HttpRequest> sortRequests = new ArrayList<HttpRequest>();
	private static final Logger LOGGER = LoggerFactory.getLogger(DongQiuDiJsonListDynamicPipeline.class);

	@Override
	public void process(JSONObject job) {
		if (job != null) {
			try {
				// 日期格式化
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				final HttpRequest request = HttpGetRequest.fromJson(job.getJSONObject("request"));
				String url = request.getUrl(); 
				// 请求参数设置
				String strPage = request.getParameter("page");
				// 来源key
				String sourceKey = request.getParameter("source_key");
				// 上次数据库更新时间 ,本次抓取事件
				long lastUpdateTime = 0, crawlTime = 0;
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

				// 截取data json串 
				String json = job.getString("dongQiuDiJson").toString();
				int d = json.indexOf(",", json.indexOf(",", json.indexOf(",") + 1) + 1) + 1;
				String replace = "{" + json.substring(d, json.lastIndexOf("}") + 1);
				Gson gs = new Gson();
				DongQiuBean bean = gs.fromJson(replace, DongQiuBean.class);
				//Json 转为 List 对象
				List<Map<String, Object>> data = bean.getData();
				for (Map<String, Object> map : data) {
					String displayTime =  map.get("display_time").toString();
					// 日期格式化
					SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					// 新闻发布时间
					long newsTime = sdf2.parse(displayTime).getTime();  
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
					String docUrl = map.get("web_url").toString();
					LOGGER.info(docUrl);
					HttpRequest subRequest = request.subRequest(docUrl);
					subRequest.addParameter("index_url", url);
					sortRequests.add(subRequest);
				}
				
				// 派生地址
				if (page < DEFAULT_MAX_PAGE) {
					//url 拼接 
					++page;
					String nextUrl = url.replaceAll("page=\\d+", "page="+page);
					LOGGER.info("nextUrl:"+nextUrl);
					// request 派生对象
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

}
