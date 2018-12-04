package com.isport.crawl.tengxun.esports;  
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.isport.Constants;
import com.isport.bean.NewsInfoBean;
import com.isport.crawl.AbstractDetailPipeLine;

@Service
public class TxLplDetail extends AbstractDetailPipeLine {

	@Override
	protected void setParseParameter(NewsInfoBean newsInfoBean, JSONObject jo) {
		String newsList = jo.getObject("newsDetail", String.class);
		// callback( 头部
		newsList = newsList.substring(9, newsList.length() - 2);
		JSONObject jsonObject = JSONObject.parseObject(newsList);
		JSONObject jsonResult = jsonObject.getJSONObject("data").getJSONObject("result");
		// 获取标题
		String title = jsonResult.getString("sTitle");
		// 获取发布时间
		String strPubDate = jsonResult.getString("sIdxTime");
		// 新闻内容
		String content = jsonResult.getString("sContent");
		// 设置标题
		newsInfoBean.setTitle(title);
		// 设置抽取时间
		newsInfoBean.setPub_date(strPubDate); 
		// 设置详情url
		String url = newsInfoBean.getUrl();
		final int yuIndex = url.lastIndexOf("&");
		url = url.substring(yuIndex + 1, url.length());
		url = "http://lol.qq.com/news/detail.shtml?" + url;
		newsInfoBean.setUrl(url); 
		// 设置新闻正文
		newsInfoBean.setHtml("<html>" + content + "</html>");
	}

	@Override
	protected void setConstant(NewsInfoBean newsInfoBean) {
		newsInfoBean.setSource(Constants.NEWS_SOURCE_QQ.value);
		newsInfoBean.setSource_icon(Constants.NEWS_ICON_QQ.value);
	}

	@Override
	protected String getBodyExpession() {
		return "*";
	}

}
