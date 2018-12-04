package com.isport.crawl.netease;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.isport.Constants;
import com.isport.bean.NewsInfoBean;
import com.isport.crawl.AbstractDetailPipeLine;
import com.isport.crawl.onesoccer.SoccerDetail;

@Service
public class NtesDetail extends AbstractDetailPipeLine {
	private static final Logger LOGGER = LoggerFactory.getLogger(SoccerDetail.class);

	@Override
	protected void setParseParameter(NewsInfoBean newsInfoBean, JSONObject jo) {
		// 获取标题
		String title = jo.getString("title");
		// 获取发布时间
		String strPubDate = jo.getString("pubDate"); 
		strPubDate = strPubDate.substring(0, 19).trim();
//		LOGGER.info("Detail:" + title + "," + strPubDate);
		// 设置标题
		newsInfoBean.setTitle(title);
		// 设置抽取时间
		newsInfoBean.setPub_date(strPubDate);
	} 

	@Override
	protected void setConstant(NewsInfoBean newsInfoBean) {
		newsInfoBean.setSource(Constants.NEWS_SOURCE_NETEASE.value);
		newsInfoBean.setSource_icon(Constants.NEWS_ICON_NETEASE.value);
	}

	@Override
	protected String getBodyExpession() {
		return "div.post_text";
	}

}
