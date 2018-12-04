package com.isport.crawl.tengxun.esports;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.isport.Constants;
import com.isport.bean.NewsInfoBean;
import com.isport.crawl.AbstractDetailPipeLine;

@Service
public class Tx2k2Detail extends AbstractDetailPipeLine {

	@Override
	protected void setParseParameter(NewsInfoBean newsInfoBean, JSONObject jo) {
		// 获取标题
		String title = jo.getString("title");
		// 获取发布时间
		String pubDate = jo.getString("pubDate");
		newsInfoBean.setTitle(title);  
		newsInfoBean.setPub_date(pubDate);
	}

	@Override
	protected void setConstant(NewsInfoBean newsInfoBean) {
		newsInfoBean.setSource(Constants.NEWS_SOURCE_QQ.value);
		newsInfoBean.setSource_icon(Constants.NEWS_ICON_QQ.value);
	}

	@Override
	protected String getBodyExpession() {
		return "#newscontent";
	}

}
