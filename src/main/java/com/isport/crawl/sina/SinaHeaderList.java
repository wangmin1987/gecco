package com.isport.crawl.sina;


import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.geccocrawler.gecco.annotation.PipelineName;
import com.geccocrawler.gecco.pipeline.JsonPipeline;
import com.geccocrawler.gecco.request.HttpGetRequest;
import com.geccocrawler.gecco.request.HttpRequest;
import com.geccocrawler.gecco.scheduler.DeriveSchedulerContext;

@Service
public class SinaHeaderList extends JsonPipeline {
	public static List<HttpRequest> sortRequests = new ArrayList<HttpRequest>();
	public static int curpage = 0;
	@Override
	public void process(JSONObject job) {
		if(job != null) {
			HttpRequest currRequest = HttpGetRequest.fromJson(job.getJSONObject("request"));
			String html=job.getString("sinajson").toString();
			Document doc = Jsoup.parse(html);
			Elements eles = doc.select("div#ty-top-ent0 a[href]");
			
			for(int i = 0; i < eles.size(); i++) {
				Element ele = eles.get(i);
	            String href = ele.attr("href");
	            if (!href.contains("http:")) {
					href = "http:" + href;
				}
//	            System.out.println(href);
	            sortRequests.add(currRequest.subRequest(href));
	            
			}
			
		}
	}
}