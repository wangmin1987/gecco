package com.isport.crawl.tengxun;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.isport.crawl.AbstractListPipeLine;

/**
 * 
 * @author Admin
 *
 */
@Service
public class TxExample1List extends AbstractListPipeLine {

	private static final Logger LOGGER = LoggerFactory.getLogger(TxExample1List.class);

	@Override
	protected Object getList(JSONObject jo) {
		return jo.getJSONArray("newsList");
	}

	@Override
	protected long getNewsTime(Object obj) throws Exception {
		JSONObject item = JSONObject.parseObject(obj.toString());
		String docUrl = item.getString("docUrl");
		String pubDate = item.getString("pubDate");
		try {
			int i = docUrl.lastIndexOf("/");
			String day = docUrl.substring(docUrl.substring(0, i).lastIndexOf("/") + 1, i);
			String strPubDate = day + " " + pubDate.split(" ")[1];
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm");
			return sdf.parse(strPubDate).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
			LOGGER.error("日期格式化错误：" + item.toJSONString());
		}
		return 0;
	}

	@Override
	protected String getNewsDocUrl(String baseUrl, Object obj) {
		JSONObject item = JSONObject.parseObject(obj.toString());
		return item.getString("docUrl");
	}

	@Override
	protected String getNextUrl(String url, String nextUrl, int page) {
		int index = url.lastIndexOf(".");
		return url.substring(0, index) + "_" + page + url.substring(index);
	}

}
