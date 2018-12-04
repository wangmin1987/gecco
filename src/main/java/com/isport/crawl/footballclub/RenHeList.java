package com.isport.crawl.footballclub;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.geccocrawler.gecco.request.HttpGetRequest;
import com.geccocrawler.gecco.request.HttpPostRequest;
import com.geccocrawler.gecco.request.HttpRequest;
import com.isport.crawl.AbstractListPipeLine;

@Service
public class RenHeList extends AbstractListPipeLine {

	@Override
	protected Object getList(JSONObject jo) {
		String newsList = jo.getObject("newsList", String.class);
		JSONArray array = JSONArray.parseArray(newsList);
		return array;
	}

	@Override
	protected long getNewsTime(Object obj) throws Exception {
		JSONObject item = JSONObject.parseObject(obj.toString());
		String pubDate = item.getString("createTime"); 
		return Long.valueOf(pubDate);
	}

	@Override
	protected String getNewsDocUrl(String baseUrl, Object obj) {
		JSONObject item = JSONObject.parseObject(obj.toString());
		String url = item.getString("links"); 
		return url;
	}

	@Override
	protected HttpRequest getRequest(HttpRequest request, String nextUrl) {
		//post request 生成下一页对象
		HttpPostRequest postRequest = (HttpPostRequest) request.subRequest(nextUrl);
		int page = Integer.valueOf(postRequest.getFields().get("PageNo"));
		page = page + 1;
		postRequest.getFields().put("PageNo", String.valueOf(page));
		return postRequest;
	}

	@Override
	protected String getNextUrl(String url, String nextUrl, int page) {
		return url;
	}
	
	@Override
	protected HttpRequest initRequest(JSONObject jo) {
		//重新初始化request 对象
		return HttpPostRequest.fromJson(jo.getJSONObject("request"));
	}
	
	@Override
	protected HttpRequest getDetailRequest(HttpRequest request, String docUrl) {
		//生成详情页request 对象
		HttpRequest httpGetRequest = new HttpGetRequest(docUrl);
		return httpGetRequest;
	}
}
