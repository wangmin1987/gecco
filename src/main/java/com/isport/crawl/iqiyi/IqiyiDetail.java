package com.isport.crawl.iqiyi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.isport.Constants;
import com.isport.bean.NewsInfoBean;
import com.isport.crawl.AbstractDetailPipeLine; 

@Service
public class IqiyiDetail extends AbstractDetailPipeLine{
	private static final Logger LOGGER = LoggerFactory.getLogger(IqiyiDetail.class);
	@Override
	protected void setParseParameter(NewsInfoBean newsInfoBean, JSONObject jo) {
		String title = jo.getString("title");
		JSONArray keywords = jo.getJSONArray("keywords");
		StringBuilder keywordBuilder = new StringBuilder();
//		LOGGER.info("keywords size:" + keywords.size());
		for (int i = 0; i < keywords.size() / 2; i++) {
			Object keyword = keywords.get(i);
//			LOGGER.info("keyword json:" + keyword.toString());
			JSONObject item = JSONObject.parseObject(keyword.toString());
			keywordBuilder.append(item.getString("keyword"));
			keywordBuilder.append(" ");
		}
		String pubDate = jo.getString("pubDate");
//		LOGGER.info("Detail:" + title + "," + keywordBuilder.toString() + "," + pubDate);
		newsInfoBean.setTitle(title);
		newsInfoBean.setKey_word(keywordBuilder.toString());
		newsInfoBean.setPub_date(pubDate);
	}

	@Override
	protected void setConstant(NewsInfoBean newsInfoBean) {
		newsInfoBean.setSource(Constants.NEWS_SOURCE_IQIYI.value);
		newsInfoBean.setSource_icon(Constants.NEWS_ICON_IQIYI.value);
	}

	@Override
	protected String getBodyExpession() {
		return "div.new_i_cent";
	}

}
