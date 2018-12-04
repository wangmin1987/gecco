package com.isport.crawl.tengxunNBA;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.isport.bean.JsonBean;
import com.isport.crawl.AbstractListPipeLine;

@Service
public class TengxunNBAList extends TengxunAbstractListPipeLine {
	private static final Logger LOGGER = LoggerFactory.getLogger(TengxunNBAList.class);

	@Override
	protected Object getList(JSONObject jo) {
		String json=jo.getString("tXunjson").toString();
		String substring = json.substring(json.indexOf("(") + 1, json.lastIndexOf(")"));
		String selectStatus = substring.substring(substring.indexOf(":", substring.indexOf(":")+1)+1,substring.indexOf(","));
		if(Integer.valueOf(selectStatus)==403) {
			return null;
		}
		Gson gs = new Gson();
		JsonBean sport = gs.fromJson(substring, JsonBean.class);
		List<Map> articles = sport.getData().getArticles();
		return articles;
	}

	@Override
	protected long getNewsTime(Map<String, String> obj) throws Exception {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");// 设置日期格式
		// 日期格式化 (08/15 09:42)
		long strPubDate = df.parse(obj.get("pubtime").replace("T", " ")).getTime();
		return strPubDate;
	}

	@Override
	protected String getNewsDocUrl(String baseUrl, Map<String, String> obj) {
		return obj.get("url");
	}

	@Override
	protected String getNextUrl(String url,  int page) {
		// 获取最大地址  
		String nextUrl =url.substring(0,url.indexOf("&", url.indexOf("&")+1))+"&p="+page+"&l=20&oe=utf-8&ie=utf-8&source=web&site=sports";
		return nextUrl;
	}
}
