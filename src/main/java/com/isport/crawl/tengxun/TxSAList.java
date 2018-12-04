package com.isport.crawl.tengxun;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.isport.crawl.AbstractListPipeLine;

@Service
public class TxSAList extends AbstractListPipeLine {
	private static final Logger LOGGER = LoggerFactory.getLogger(TxSAList.class);

	@Override
	protected Object getList(JSONObject jo) {
		return jo.getJSONArray("newsList");
	}

	@Override
	protected long getNewsTime(Object obj) throws Exception {
		JSONObject item = JSONObject.parseObject(obj.toString());
		// 日期格式化 (2018-10-11 09:42)
		String strPubDate = item.getString("pubDate");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
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
		return item.getString("docUrl");
	}

	@Override
	protected String getNextUrl(String url, String nextUrl, int page) {
		final int underline = url.lastIndexOf("_");
		return url.substring(0, underline) +"_"+ page + ".htm";
	}

}
