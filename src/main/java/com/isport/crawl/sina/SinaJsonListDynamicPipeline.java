package com.isport.crawl.sina;


import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.geccocrawler.gecco.pipeline.JsonPipeline;
import com.geccocrawler.gecco.request.HttpGetRequest;
import com.geccocrawler.gecco.request.HttpRequest;
import com.geccocrawler.gecco.scheduler.DeriveSchedulerContext;

@Service
public class SinaJsonListDynamicPipeline extends JsonPipeline {
	
	public static List<HttpRequest> sortRequests = new ArrayList<HttpRequest>();
	@Override
	public void process(JSONObject job) {
		if(job != null) {
			HttpRequest currRequest = HttpGetRequest.fromJson(job.getJSONObject("request"));
			String json=job.getString("sinajson").toString();
			String ctime=job.getString("ctime").toString();
			json=json.substring(42, json.length()-2).trim();
			job=JSONObject.parseObject(json);
			JSONArray jsonArray = job.getJSONArray("data");
			String time="";
			for(int i = 0; i < jsonArray.size(); i++) {
				String itemStr = jsonArray.get(i).toString();
	            JSONObject itemObject = JSONObject.parseObject(itemStr);
	            String url = itemObject.getString("url");
	            url=url.substring(0,url.indexOf("?"));
	            DeriveSchedulerContext.into(currRequest.subRequest(url));
	           // sortRequests.add(currRequest.subRequest(url));
	            time=itemObject.getString("ctime");
			}
			if(ctime!=null&&!"".equals(ctime)) {
				String url=currRequest.getUrl();
				String nextUrl=StringUtils.replaceOnce(url, "ctime=" + ctime, "ctime=" + time);
				System.out.println(currRequest.getUrl());
				System.out.println(nextUrl);
				DeriveSchedulerContext.into(currRequest.subRequest(nextUrl));
			}
		}
	}
}