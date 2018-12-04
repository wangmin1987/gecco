package com.isport.crawl.tengxun.esports;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.geccocrawler.gecco.request.HttpGetRequest;
import com.geccocrawler.gecco.request.HttpRequest;
import com.isport.crawl.AbstractListPipeLine; 

@Service
public class Tx2k2List extends AbstractListPipeLine {
	private static final Logger LOGGER = LoggerFactory.getLogger(Tx2k2List.class);

	@Override
	protected Object getList(JSONObject jo) { 
		return jo.getJSONArray("newsList");
	}

	@Override
	protected long getNewsTime(Object obj) throws Exception {
		JSONObject item = JSONObject.parseObject(obj.toString());
		// 日期格式化 (2018-10-11)
		String strPubDate = item.getString("pubDate");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			return sdf.parse(strPubDate).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
			LOGGER.error("日期格式化错误" + strPubDate);
		}
		return 0;
	}

	@Override
	protected String getNewsDocUrl(String baseUrl, Object obj) {
		JSONObject item = JSONObject.parseObject(obj.toString());
		return com.gargoylesoftware.htmlunit.util.UrlUtils.resolveUrl(baseUrl,item.getString("docUrl"));
	}

	@Override
	protected String getNextUrl(String url, String nextUrl, int page) { 
		final int underline = url.lastIndexOf("_");
		return url.substring(0, underline) +"_"+ page + ".shtml";
	}
	
	@Override
	protected HttpRequest getDetailRequest(HttpRequest request, String docUrl) {
		 HttpGetRequest httpGetRequest =(HttpGetRequest) request.subRequest(docUrl);
		 httpGetRequest.setCharset("gbk");
		 return httpGetRequest;
	}
	
}
