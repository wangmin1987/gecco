package com.isport.crawl.tengxun;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.isport.crawl.AbstractListPipeLine;

@Service
public class TxList extends AbstractListPipeLine {
	private static final Logger LOGGER = LoggerFactory.getLogger(TxList.class);

	@Override
	protected Object getList(JSONObject jo) {
//		LOGGER.info(jo.toJSONString());
		return jo.getObject("newsList", List.class);
	}

	@Override
	protected long getNewsTime(Object obj) {
		// 转换对象为JSONObject
		JSONObject item = JSONObject.parseObject(obj.toString());
		String strPubDate = item.getString("pubtime");
//		LOGGER.info("strPubDate:" + strPubDate);
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
		//获取最后一个冒号的地址
		final int colonIndex = url.lastIndexOf(".");
		String subUrl = url.substring(0, colonIndex);
		return subUrl+"_"+page+".htm";
	}

}
