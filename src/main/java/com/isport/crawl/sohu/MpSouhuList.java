package com.isport.crawl.sohu;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.isport.crawl.AbstractListPipeLine;

@Service
public class MpSouhuList extends AbstractListPipeLine {

	@Override
	protected Object getList(JSONObject jo) throws Exception {
		String newsList = jo.getString("newsList"); 
		newsList =newsList.substring(1, newsList.length()-1);
		newsList =newsList.replace("\\", ""); 
		JSONObject jsonObject= JSONObject.parseObject(newsList); 
		JSONArray jsonArray = jsonObject.getJSONArray("data"); 
		return jsonArray;
	}

	@Override
	protected long getNewsTime(Object obj) throws Exception {
		JSONObject item = JSONObject.parseObject(obj.toString());
		// 日期格式化 (08/15 09:42)
		String strPubDate = item.getString("postTime");
		if (strPubDate == null) {
			throw new Exception("空串");
		} 
		return Long.valueOf(strPubDate);
	}

	@Override
	protected String getNewsDocUrl(String baseUrl, Object obj) {
		JSONObject item = JSONObject.parseObject(obj.toString()); 
		String docUrl = "http:" + item.getString("url"); 
		return docUrl;
	}

	@Override
	protected String getNextUrl(String url, String nextUrl, int page) {
		return url.replaceAll("pageNumber=\\d+", "pageNumber=" + page);
	}

}
