package com.isport.crawl.sohu;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.isport.crawl.AbstractListPipeLine;

@Service
public class SohuList extends AbstractListPipeLine {
	private static final Logger LOGGER = LoggerFactory.getLogger(SohuList.class);

	@Override
	protected Object getList(JSONObject jo) {
		return jo.getJSONArray("newsList");
	}

	@Override
	protected long getNewsTime(Object obj) throws Exception {
		JSONObject item = JSONObject.parseObject(obj.toString());
		// 日期格式化 (08/15 09:42)
		String strPubDate = item.getString("pubDate");
		if (strPubDate == null) {
			throw new Exception("空串");
		}
//		LOGGER.info("strPubDate:" + strPubDate);
		strPubDate = strPubDate.replace("(", "");
		strPubDate = strPubDate.replace(")", "");
		strPubDate = strPubDate.replace("/", "-");
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
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
		// 生成下一页地址， 生成规则是，第二页是下标 _maxPage(40).shtml, 没加一页 下标减一 如 _39.shtml, _38.shtml
		// 获取最大地址  
		int maxPage = Integer.valueOf(getScriptMaxPage(nextUrl));
//		LOGGER.info("maxPage" + maxPage); 
		final int underLineIndex = url.indexOf("_");
		if (underLineIndex > 0) {
			final int pointIndex = url.lastIndexOf(".");
			String strPage = url.substring(underLineIndex + 1, pointIndex);
			// 计算新的下一页页号
			int newPage = Integer.valueOf(strPage) - page;
			String subUrl = url.substring(0, pointIndex);
			return subUrl + "_" + newPage + ".shtml";
		} else {
			final int pointIndex = url.lastIndexOf(".");
			String subUrl = url.substring(0, pointIndex);
			return subUrl + "_" + (maxPage-1) + ".shtml";
		}
	}

	private String getScriptMaxPage(String nextUrl) {
		Pattern p = Pattern.compile("var maxPage = (\\d+);");
		Matcher m = p.matcher(nextUrl);
		while (m.find()) {
			return m.group(1);
		}
		return null;
	}

}
