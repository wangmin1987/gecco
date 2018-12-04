package com.isport.crawl.dongqiudi;

import java.text.ParseException;
import java.text.SimpleDateFormat; 
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject; 
import com.google.gson.Gson;
import com.isport.bean.DongQiuBean;
import com.isport.crawl.AbstractListPipeLine;

@Service
public class DongQiuDiFAList extends AbstractListPipeLine{
	
	@Override
	protected String getNextUrl(String url, String nextUrl,int page) {
		return  url.replaceAll("page=\\d+", "page="+page);
	}

	@Override
	protected String getNewsDocUrl(String baseUrl,Object obj) {
		@SuppressWarnings("unchecked")
		Map<String, Object> map = (Map<String, Object>) obj; 
		return map.get("web_url").toString();
	}

	@Override
	protected long getNewsTime(Object obj) {
		@SuppressWarnings("unchecked")
		Map<String, Object> map = (Map<String, Object>) obj; 
		String displayTime =  map.get("display_time").toString();
		// 日期格式化
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// 新闻发布时间 
		try {
			return sdf2.parse(displayTime).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}  
		return 0;
	}

	@Override
	protected Object getList(JSONObject jo) {
		// 截取data json串 
		String json = jo.getString("dongQiuDiJson").toString();
		int d = json.indexOf(",", json.indexOf(",", json.indexOf(",") + 1) + 1) + 1;
		String replace = "{" + json.substring(d, json.lastIndexOf("}") + 1);
		Gson gs = new Gson();
		DongQiuBean bean = gs.fromJson(replace, DongQiuBean.class);
		//Json 转为 List 对象
		List<Map<String, Object>> data = bean.getData();
		return data;
	}
 
}
