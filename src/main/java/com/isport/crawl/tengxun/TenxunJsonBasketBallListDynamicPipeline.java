package com.isport.crawl.tengxun;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date; 
import java.util.List;
import java.util.Map;

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
import com.isport.bean.NewsInfoBean;
import com.isport.service.NewsService;
import com.isport.utils.MongoTemUtil; 

@Service
public class TenxunJsonBasketBallListDynamicPipeline extends JsonPipeline {
	@Autowired
	NewsService newsService;
	// 默认设置：第一次爬取时，默认爬取3天内的数据
	public static int DEFAULT_TIME = 3600000 * 24 * 3;
	@Autowired
	MongoTemUtil mongoTemUtil;
	public static List<HttpRequest> sortRequests = new ArrayList<HttpRequest>();
	private static final Logger LOGGER = LoggerFactory.getLogger(TenxunJsonBasketBallListDynamicPipeline.class);
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void process(JSONObject job) {
		if(job != null) {
			HttpRequest currRequest = HttpGetRequest.fromJson(job.getJSONObject("request"));
			String json=job.getString("tXunjson").toString();
			String team=job.getString("team").toString();
			String page=job.getString("page").toString();
			String block=job.getString("block").toString();
			//数据库最新发布时间
			long pubDate =0;
			boolean isNext =true;
			String substring = json.substring(json.indexOf("(") + 1, json.lastIndexOf(")"));
			Gson gs = new Gson();
			Query query =new Query();
			 Criteria c= Criteria.where("block").is(block);
			 Criteria d= Criteria.where("sport_team").is(team);
		        query.addCriteria(c);
		        query.addCriteria(d);
			List<NewsInfoBean> findByCommand = mongoTemUtil.findByCommand(1, 10, "pubDate",query);
			String selectStatus = substring.substring(substring.indexOf(":", substring.indexOf(":")+1)+1,substring.indexOf(","));
			if(Integer.valueOf(selectStatus)==403) {
				getNextPage(page, isNext, currRequest, block);
				return;
			}
			JsonBean sport = gs.fromJson(substring, JsonBean.class);
			List<Map> articles = sport.getData().getArticles();
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");// 设置日期格式
			if (findByCommand.size()==0) {
				pubDate=new Date().getTime()-DEFAULT_TIME;
			}else {
				try {
					pubDate =df.parse(findByCommand.get(0).getPub_date()).getTime();
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			outer:for (Map<String, String> map : articles) {
					long notTime = 0;
					try {
						notTime = df.parse(map.get("pubtime").substring(0, 16).replace("T", " ")).getTime();
					} catch (ParseException e) {
						e.printStackTrace();
					}
					if (notTime <= pubDate) {
						isNext=false;
						LOGGER.info("没有最新文章");
						break outer;
					}
//				String updateTime = map.get("pubtime");
				String kidUrl = map.get("url")+"?block="+block+"&team="+team;
				System.out.println(kidUrl);
				 sortRequests.add(currRequest.subRequest(kidUrl));
			}
			
		}
	}
	private void getNextPage(String page,boolean isNext,HttpRequest currRequest,String block) {
		if(Integer.valueOf(page)<30&&isNext) {
			String url = currRequest.getUrl();
			String nextUrl =url.substring(0,url.indexOf("&", url.indexOf("&")+1))+"&p="+page+"&l=20&oe=utf-8&ie=utf-8&source=web&site=sports&block="+block;
			DeriveSchedulerContext.into(currRequest.subRequest(nextUrl));
		}
	}
}