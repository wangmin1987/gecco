package com.isport.crawl.tengxun;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.isport.crawl.AbstractListPipeLine;

@Service
public class PyList extends AbstractListPipeLine {

	@Override
	protected Object getList(JSONObject jo) {
		return jo.getObject("newsList", List.class);
	}

	@Override
	protected long getNewsTime(Object obj) throws Exception {
		// 转换对象为JSONObject
		JSONObject item = JSONObject.parseObject(obj.toString());
		String strPubDate = item.getString("pubtime");
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
		return item.getString("url");
	}

	@Override
	protected String getNextUrl(String url, String nextUrl, int page) {
		// 获取最后一个冒号的地址
		int colonIndex = 0;
		if (page == 2) {
			colonIndex = url.lastIndexOf(".");
		} else {
			colonIndex = url.lastIndexOf("_");
		}
		String subUrl = url.substring(0, colonIndex);
		return subUrl + "_" + page + ".htm";
	}

}
