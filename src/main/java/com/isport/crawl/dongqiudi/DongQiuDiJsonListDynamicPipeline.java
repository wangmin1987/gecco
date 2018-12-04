package com.isport.crawl.dongqiudi;


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
import com.isport.bean.DongQiuBean;
import com.isport.bean.DongQiuBean.Data;
import com.isport.bean.NewsInfoBean;
import com.isport.service.NewsService;
import com.isport.utils.MongoTemUtil;

@Service
public class DongQiuDiJsonListDynamicPipeline extends JsonPipeline {
	@Autowired
	NewsService newsService;
	// 默认设置：第一次爬取时，默认爬取3天内的数据
	public static int DEFAULT_TIME = 3600000 * 24 * 3;
	@Autowired
	MongoTemUtil mongoTemUtil;
	public static List<HttpRequest> sortRequests = new ArrayList<HttpRequest>();
	private static final Logger LOGGER = LoggerFactory.getLogger(DongQiuDiJsonListDynamicPipeline.class);
	@Override
	public void process(JSONObject job) {
		if(job != null) {
			HttpRequest currRequest = HttpGetRequest.fromJson(job.getJSONObject("request"));
			String json=job.getString("dongQiuDiJson").toString();
			String page=job.getString("page").toString();
			String match=job.getString("match").toString();
			String block=job.getString("block").toString();
			String num=job.getString("num").toString();
			int d =json.indexOf(",", json.indexOf(",", json.indexOf(",")+1)+1)+1;
			String replace = "{"+json.substring(d,json.lastIndexOf("}")+1);
			Gson gs = new Gson();
			DongQiuBean bean = gs.fromJson(replace, DongQiuBean.class);
			List<Map<String, Object>> data = bean.getData();
			outer:for (Map<String, Object> map : data) {
				String kidUrl = map.get("web_url")+"?block="+block+"&match="+match;
				if(kidUrl.contains("video")) {
				}else {
					System.out.println(kidUrl);
					//sortRequests.add(currRequest.subRequest(kidUrl));
					DeriveSchedulerContext.into(currRequest.subRequest(kidUrl));
				}
			}
				if(Integer.valueOf(page)<10) {
					 String nextUrl  ="http://www.dongqiudi.com/archives/"+num+"?page="+(Integer.valueOf(page)+1)+"&block="+block+"&match="+match;
					DeriveSchedulerContext.into(currRequest.subRequest(nextUrl));
				}
		}
	}
}