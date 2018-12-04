package com.isport.crawl.tengxun.esports;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.isport.crawl.AbstractListPipeLine;

@Service
public class TxLplList extends AbstractListPipeLine {

	@Override
	protected Object getList(JSONObject jo) throws Exception {
		String newsList = "";
		try {
			newsList = jo.getObject("newsList", String.class);
			// var NewsObj= 头部
			newsList = newsList.substring(12, newsList.length() - 1);
		} catch (Exception e) {
			e.printStackTrace(); 
			throw new Exception("页面下载错误");
		}
		JSONObject jsonObject = JSONObject.parseObject(newsList);
		JSONArray jsonArray = jsonObject.getJSONObject("msg").getJSONArray("result");
		return jsonArray;

	}

	@Override
	protected long getNewsTime(Object obj) throws Exception {
		// 转换对象为JSONObject
		JSONObject item = JSONObject.parseObject(obj.toString());
		String strPubDate = item.getString("sCreated");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			// 日期字符串转换为时间戳
			return sdf.parse(strPubDate).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	protected String getNewsDocUrl(String baseUrl, Object obj) {
		// 对象类型转换
		JSONObject item = JSONObject.parseObject(obj.toString());
		// 组装url
		String combineUrl = "http://apps.game.qq.com/cmc/zmMcnContentInfo?r0=jsonp&source=web_pc&type=0&docid=";
		return combineUrl + item.getString("iDocID");
	}

	@Override
	protected String getNextUrl(String url, String nextUrl, int page) {
		return url.replaceAll("page=\\d+", "page=" + page);
	}

}
