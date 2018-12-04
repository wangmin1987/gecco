package com.isport.crawl.formula;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.isport.crawl.AbstractListPipeLine;

@Service
public class FormulaList extends FormulabstractListPipeLine {
	private static final Logger LOGGER = LoggerFactory.getLogger(FormulaList.class);

	@Override
	protected Object getList(JSONObject jo) {
		 return jo.getJSONArray("newsUrlList");
	}

	@Override
	protected String getNewsDocUrl(String baseUrl, Object obj) {
		JSONObject item = JSONObject.parseObject(obj.toString());
		return "http://www.wildto.com"+item.getString("docUrl");
	}

	@Override
	protected String getNextUrl(String url,int page) {
		String nextUrl="http://www.wildto.com/v/index_"+page+".html";
		return nextUrl;
	}


}
