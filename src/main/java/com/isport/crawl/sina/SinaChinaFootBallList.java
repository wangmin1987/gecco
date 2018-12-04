package com.isport.crawl.sina;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSONObject;
import com.geccocrawler.gecco.pipeline.JsonPipeline;
import com.geccocrawler.gecco.request.HttpGetRequest;
import com.geccocrawler.gecco.request.HttpRequest;
import com.geccocrawler.gecco.scheduler.DeriveSchedulerContext;
import com.google.gson.Gson;
import com.isport.bean.JsonBean;
import com.isport.bean.JsonData;
import com.isport.bean.NewsInfoBean;
import com.isport.bean.SinaJsonBean;
import com.isport.service.NewsService;
import com.isport.utils.MongoTemUtil;

@Service
public class SinaChinaFootBallList extends JsonPipeline {
	@Autowired
	NewsService newsService;
	// 默认设置：第一次爬取时，默认爬取3天内的数据
	public static int DEFAULT_TIME = 3600000 * 24 * 3;
	@Autowired
	MongoTemUtil mongoTemUtil;
	public static List<HttpRequest> sortRequests = new ArrayList<HttpRequest>();
	private static final Logger LOGGER = LoggerFactory.getLogger(SinaChinaFootBallList.class);
	@Override
	public void process(JSONObject job) {
		if(job != null) {
			HttpRequest currRequest = HttpGetRequest.fromJson(job.getJSONObject("request"));
			String json=job.getString("sinajson").toString();
			String block=job.getString("block").toString();
			String page=job.getString("page").toString();
			//数据库最新发布时间
			long pubDate =0;
			boolean isNext =true;
			Gson gs =new Gson();
			SinaJsonBean sport = gs.fromJson(json, SinaJsonBean.class);
			List<Map<String, Object>> data = sport.getResult().getData();
			for (Map<String, Object> map : data) {
				sortRequests.add(currRequest.subRequest(map.get("url").toString()+"?block="+block));
			}
//			String substring = json.substring(json.indexOf(":") + 1, json.lastIndexOf("}"));
//			Gson gs = new Gson();
//			List<Map<String, String>> sport = gs.fromJson(substring,new ArrayList<Map<String, String>>().getClass());
//			Query query =new Query();
//			 Criteria c= Criteria.where("block").is(block);
//		        query.addCriteria(c);
//			List<NewsInfoBean> findByCommand = mongoTemUtil.findByCommand(1, 10, "pubDate",query);
//			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");// 设置日期格式
//			if (findByCommand.size()==0) {
//				pubDate=new Date().getTime()-DEFAULT_TIME;
//			}else {
//				try {
//					pubDate =df.parse(findByCommand.get(0).getPubDate()).getTime();
//				} catch (ParseException e) {
//					e.printStackTrace();
//				}
//			}
//			outer:for (Map<String, String> map : sport) {
//					long notTime = 0;
//					try {
//						notTime = df.parse(map.get("pubtime")).getTime();
//					} catch (ParseException e) {
//						e.printStackTrace();
//					}
//					if (notTime <= pubDate) {
//						isNext=false;
//						LOGGER.info("没有最新文章");
//						break outer;
//					}
//				String updateTime = map.get("pubtime");
//				String kidUrl = map.get("url")+"?block="+block;
//				System.out.println(kidUrl);
//				 sortRequests.add(currRequest.subRequest(kidUrl));
//			}
		
				if(Integer.valueOf(page)<1&&isNext) {
					String	nextUrl="http://interface.sina.cn/pc_zt_api/pc_zt_press_news_doc.d.json?subjectID=61903&cat=&size=4&page="+(Integer.valueOf(page)+1)+"&channel=sports&block="+block;
					DeriveSchedulerContext.into(currRequest.subRequest(nextUrl));
				}
		}
	}
}