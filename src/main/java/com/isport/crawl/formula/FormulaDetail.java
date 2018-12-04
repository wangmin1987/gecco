package com.isport.crawl.formula;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.isport.Constants;
import com.isport.bean.NewsInfoBean;
import com.isport.crawl.AbstractDetailPipeLine; 
@Service
public class FormulaDetail extends FormulabstractDetailPipeLine{
	private static final Logger LOGGER = LoggerFactory.getLogger(FormulaDetail.class);

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
		newsInfoBean.setSource(Constants.NEWS_SOURCE_FORMULA.value);
		newsInfoBean.setSource_icon(Constants.NEWS_ICON_FORMULA.value);
	}

	@Override
	protected String getBodyExpession() {
		return "div#artibody";
	}
}
