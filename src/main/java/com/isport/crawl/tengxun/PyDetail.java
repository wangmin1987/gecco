package com.isport.crawl.tengxun;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.isport.Constants;
import com.isport.bean.NewsInfoBean;

@Service
public class PyDetail extends TxAbstractDetail {

	@Override
	protected void setParseParameter(NewsInfoBean newsInfoBean, JSONObject jo) {
		// 获取标题
		String title = jo.getString("title");
		// 获取发布时间
		String strPubDate = jo.getString("pubDate");
		// 获取关键字
		JSONArray keywords = jo.getJSONArray("keywords");
		StringBuilder keywordBuilder = new StringBuilder();
//				LOGGER.info("keywords size:" + keywords.size());
		for (Object keyword : keywords) {
			JSONObject item = JSONObject.parseObject(keyword.toString());
			keywordBuilder.append(item.getString("keyword"));
			keywordBuilder.append(" ");
		}
		// 设置标题
		newsInfoBean.setTitle(title);
		//设置关键字
		newsInfoBean.setKey_word(keywordBuilder.toString());
		// 设置抽取时间
		newsInfoBean.setPub_date(strPubDate);

	}

	@Override
	protected void setConstant(NewsInfoBean newsInfoBean) {
		newsInfoBean.setSource(Constants.NEWS_SOURCE_QQ.value);
		newsInfoBean.setSource_icon(Constants.NEWS_ICON_QQ.value);
	}

}
