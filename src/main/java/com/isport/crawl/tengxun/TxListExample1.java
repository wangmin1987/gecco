package com.isport.crawl.tengxun;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.isport.crawl.AbstractListPipeLine;
import com.isport.utils.StringUtils;

/**
 * 
 * @author wangyuanxi
 * 
 * Example1List规则：
 * 1.资讯列表规则：HTML列表（JSONArray对象）
 * 2.发布时间规则："11月20日 11:31" 需要从详情页URL中获取“年月日”
 * 3.翻页规则：第1页无页码，从第二页开始页码格式为“{filename}_{page}.{suffix}”
 * 
 */
@Service
public class TxListExample1 extends AbstractListPipeLine {

	private static final Logger LOGGER = LoggerFactory.getLogger(TxListExample1.class);

	@Override
	protected Object getList(JSONObject jo) {
		return jo.getJSONArray("newsList");
	}

	@Override
	protected long getNewsTime(Object obj) throws Exception {
		JSONObject item = JSONObject.parseObject(obj.toString());
		try {
			String docUrl = item.getString("docUrl");
			String pubDate = item.getString("pubDate");
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

	/**
	 * 获取下一页的请求地址
	 * 
	 * @param baseUrl 当前页地址
	 * @param obj 当前资讯对象
	 * @return
	 */
	@Override
	protected String getNewsDocUrl(String baseUrl, Object obj) {
		JSONObject item = JSONObject.parseObject(obj.toString());
		return item.getString("docUrl");
	}

	/**
	 * 获取下一页的请求地址
	 * 
	 * @param url 当前页地址
	 * @param nextUrl 下一页地址
	 * @param page 下一页页码
	 * @return
	 */
	@Override
	protected String getNextUrl(String url, String nextUrl, int page) {
		if (!StringUtils.isNUll(nextUrl)) {
			return nextUrl;
		}
		String suffix = url.substring(url.lastIndexOf("."));
		return url.replace(page == 2 ? suffix : "_" + (page - 1) + suffix, "_" + page + suffix);
	}

}
