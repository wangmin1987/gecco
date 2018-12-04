package com.isport.crawl.aiyuke;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.isport.crawl.AbstractListPipeLine;
import com.isport.utils.CrawlDateUtil;
import com.isport.utils.StringUtils;

@Service
public class AiyukeList extends AbstractListPipeLine {

	@Override
	protected Object getList(JSONObject jo) throws Exception {
		return jo.getJSONArray("newsList");
	}

	@Override
	protected long getNewsTime(Object obj) throws Exception {
		JSONObject item = JSONObject.parseObject(obj.toString());
		String strPubDate = item.getString("pubDate");
		if(StringUtils.isNUll(strPubDate)) {
			throw new Exception("新闻目录");
		} 
		String[] splits = strPubDate.split("·");
		strPubDate = splits[1].trim();
		return CrawlDateUtil.timeConvert2(strPubDate);
	}

	@Override
	protected String getNewsDocUrl(String baseUrl, Object obj) {
		JSONObject item = JSONObject.parseObject(obj.toString());
		String docUrl = item.getString("docUrl");
		return docUrl;
	}

	@Override
	protected String getNextUrl(String url, String nextUrl, int page) {
		if(page==2) {
			return url+"?page="+page;
		}else {
			return url.replaceAll("page=\\d+", "page="+page);
		}
	}
	
}
