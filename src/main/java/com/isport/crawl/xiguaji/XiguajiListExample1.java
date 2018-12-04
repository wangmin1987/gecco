package com.isport.crawl.xiguaji;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.geccocrawler.gecco.request.HttpRequest;
import com.isport.crawl.AbstractListPipeLine;

/**
 * 
 * @author wangyuanxi
 * 
 * Example1List规则：
 * 1.资讯列表规则：HTML列表（JSONArray对象）
 * 2.发布时间规则："2018/11/11 12:57:49"
 * 3.翻页规则：无翻页
 * 
 */
@Service
public class XiguajiListExample1 extends AbstractListPipeLine {

	private static final Logger LOGGER = LoggerFactory.getLogger(XiguajiListExample1.class);
	// 当前站点的发布时间格式
	public static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

	@Override
	protected Object getList(JSONObject jo) {
		return jo.getJSONArray("newsList");
	}

	@Override
	protected long getNewsTime(Object obj) {
		JSONObject item = JSONObject.parseObject(obj.toString());
		try {
			String strPubDate = item.getString("pubDate");
			return SDF.parse(strPubDate).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
			LOGGER.error("日期格式化错误：" + item.toJSONString());
		}
		return 0;
	}

	/**
	 * 获取详情页的请求地址
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
	 * 获取列表下一页的请求地址
	 * 
	 * @param url 当前页地址
	 * @param nextUrl 下一页地址
	 * @param page 下一页页码
	 * @return
	 */
	@Override
	protected String getNextUrl(String url, String nextUrl, int page) {
		return null;
	}

	@Override
	protected void setRequestParameter(HttpRequest subRequest, Object obj) {
		subRequest.addParameter("pub_date", DEFAULT_SDF.format(new Date(getNewsTime(obj))));
	}

}
