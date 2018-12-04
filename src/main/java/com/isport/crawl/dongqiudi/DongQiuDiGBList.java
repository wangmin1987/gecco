package com.isport.crawl.dongqiudi;

import java.text.SimpleDateFormat;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.isport.crawl.AbstractListPipeLine;

@Service
public class DongQiuDiGBList extends AbstractListPipeLine {

	@Override
	protected Object getList(JSONObject jo) {
		return jo.getJSONArray("newsList");
	}

	@Override
	protected long getNewsTime(Object obj) throws Exception {
		JSONObject item = JSONObject.parseObject(obj.toString());
		// 日期格式化 (08/15 09:42)
		String strPubDate = item.getString("pubDate");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.parse(strPubDate).getTime();
	}

	@Override
	protected String getNewsDocUrl(String baseUrl, Object obj) {
		JSONObject item = JSONObject.parseObject(obj.toString());
		return item.getString("docUrl");
	}

	@Override
	protected String getNextUrl(String url, String nextUrl, int page) {
		final int pageIndex = url.indexOf("&");
		return url.substring(0, pageIndex) + "&page=" + page;
	}

}
