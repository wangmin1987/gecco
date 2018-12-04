package com.isport.crawl.onesoccer;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.gargoylesoftware.htmlunit.util.UrlUtils;
import com.isport.crawl.AbstractListPipeLine;

@Service
public class SoccerList extends AbstractListPipeLine {
	private static final Logger LOGGER = LoggerFactory.getLogger(SoccerList.class);

	// 获取下一页
	@Override
	protected String getNextUrl(String url, String nextUrl, int page) {
		// 最后一个斜杠的地址
		final int cronIndex = url.lastIndexOf("/");
		// 获取页面参数之前的子url
		String subUrl = url.substring(0, cronIndex);
		return subUrl +"/"+ page;
	}

	// 抽取详情页地址
	@Override
	protected String getNewsDocUrl(String baseUrl,Object obj) {
		JSONObject item = JSONObject.parseObject(obj.toString());
		String path= item.getString("docUrl");
		return UrlUtils.resolveUrl(baseUrl, path);
	}

	// 新闻事件
	@Override
	protected long getNewsTime(Object obj) {
		JSONObject item = JSONObject.parseObject(obj.toString());
		String strPubDate = item.getString("pubDate");
		// 定义时间截取的起始和结束地址
		final int startIndex = strPubDate.indexOf("(");
		final int endIndex = strPubDate.indexOf(")");
		strPubDate = strPubDate.substring(startIndex + 1, endIndex);
//		LOGGER.info("strPubDate:" + strPubDate);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			return sdf.parse(strPubDate).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0;
	}

	// 列表页list
	@Override
	protected Object getList(JSONObject jo) {
		return jo.getJSONArray("newsList");
	}

}
