package com.isport.crawl.goal;

import java.text.SimpleDateFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.isport.Constants;
import com.isport.bean.NewsInfoBean;
import com.isport.crawl.AbstractDetailPipeLine;
import com.isport.utils.StringUtils;
@Service
public class GoalDetail extends AbstractDetailPipeLine{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(GoalDetailPipeline.class);
	@Override
	protected void setParseParameter(NewsInfoBean newsInfoBean, JSONObject jo) {
		String title = jo.getString("title");
		JSONArray keywords = jo.getJSONArray("keywords");
		StringBuilder keywordBuilder = new StringBuilder();
//		LOGGER.info("keywords size:"+keywords.size());
		for (Object keyword : keywords) {
			JSONObject item = JSONObject.parseObject(keyword.toString());
			keywordBuilder.append(item.getString("keyword"));
			keywordBuilder.append(" ");
		}
		String pubDate = jo.getString("pubDate");
//		LOGGER.info("Detail:" + title + "," + keywordBuilder.toString() + "," + pubDate);
		// 日期格式化
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		pubDate = StringUtils.isNUll(pubDate) ? null : sdf.format(Long.valueOf(pubDate));
		newsInfoBean.setTitle(title);
		newsInfoBean.setKey_word(keywordBuilder.toString());
		newsInfoBean.setPub_date(pubDate);
	}

	@Override
	protected void setConstant(NewsInfoBean newsInfoBean) {
		newsInfoBean.setSource(Constants.NEWS_SOURCE_GOAL.value);
		newsInfoBean.setSource_icon(Constants.NEWS_ICON_GOAL.value);
	}

	@Override
	protected String getBodyExpession() {
		return "div.body";
	}

}
