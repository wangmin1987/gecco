package com.isport.crawl.sina;


import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.geccocrawler.gecco.annotation.PipelineName;
import com.geccocrawler.gecco.pipeline.JsonPipeline;
import com.geccocrawler.gecco.request.HttpGetRequest;
import com.geccocrawler.gecco.request.HttpRequest;
import com.geccocrawler.gecco.scheduler.DeriveSchedulerContext;

@Service
public class SinaHotJsonList extends JsonPipeline {
	public static List<HttpRequest> sortRequests = new ArrayList<HttpRequest>();
	public static int curpage = 0;
	@Override
	public void process(JSONObject job) {
		if(job != null) {
			HttpRequest currRequest = HttpGetRequest.fromJson(job.getJSONObject("request"));
			String json=job.getString("sinajson").toString();
			String page=job.getString("page").toString();
			json=com.isport.utils.StringUtils.getSubString(json, "(", ")");
			job=JSONObject.parseObject(json);
			JSONArray jsonArray = job.getJSONArray("data");
			String time="";
			for(int i = 0; i < jsonArray.size(); i++) {
				String itemStr = jsonArray.get(i).toString();
	            JSONObject itemObject = JSONObject.parseObject(itemStr);
	            String url = itemObject.getString("url");
	            url=url.substring(0,url.indexOf("?"));
	           // DeriveSchedulerContext.into(currRequest.subRequest(url));
	            sortRequests.add(currRequest.subRequest(url));
	            
			}
			int curpage = Integer.parseInt(page)+1;

			if(curpage<10) {
				String url=currRequest.getUrl();
				String nextUrl=StringUtils.replaceOnce(url, "up=" + page, "up=" + curpage);
//				System.out.println(currRequest.getUrl());
				System.out.println(nextUrl);
				DeriveSchedulerContext.into(currRequest.subRequest(nextUrl));
			}
		}
	}
}