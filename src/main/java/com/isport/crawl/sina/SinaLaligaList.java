package com.isport.crawl.sina;
 

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.geccocrawler.gecco.request.HttpGetRequest;
import com.geccocrawler.gecco.request.HttpRequest;
import com.isport.crawl.AbstractListPipeLine; 

@Service
public class SinaLaligaList extends AbstractListPipeLine {
	private static final Logger LOGGER = LoggerFactory.getLogger(SinaLaligaList.class);
	
	@Override
	protected HttpRequest getRequest(HttpRequest request, String nextUrl) {
//		LOGGER.info("nextRequest produce");
		//重新生成request， 去掉额外参数
		HttpRequest nextRequest =new HttpGetRequest(nextUrl);
		return nextRequest;
	}
	@Override
	protected Object getList(JSONObject jo) { 
		String newsList = jo.getObject("newsList", String.class);
		//截取出json对象，进行json转换
		final int startIndex = newsList.indexOf("{\"result\"");
		final int endIndex = newsList.indexOf(");}catch(e)");
		String json = newsList.substring(startIndex, endIndex);
		// json 对象解析
		JSONObject jsonObject = JSONObject.parseObject(json); 
		JSONArray jsonArray = jsonObject.getJSONObject("result").getJSONArray("data");
		return jsonArray;
	} 
	
	@Override
	protected long getNewsTime(Object obj) {
		JSONObject item = JSONObject.parseObject(obj.toString());
		String pubDate = item.getString("ctime");
		return Long.valueOf(pubDate+"000");
	}

	@Override
	protected String getNewsDocUrl(String baseUrl, Object obj) {
		JSONObject item = JSONObject.parseObject(obj.toString());
		String url  = item.getString("url");
		return url;
	}

	@Override
	protected String getNextUrl(String url, String nextUrl, int page) {
		return url.replaceAll("page=\\d+", "page="+page);
	}

}
