package com.isport.crawl.sina;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.isport.Constants;
import com.isport.bean.NewsInfoBean;
import com.isport.crawl.AbstractDetailPipeLine;
import com.isport.utils.StringUtils;

@Service
public class SinaDetail extends AbstractDetailPipeLine {

	@Override
	protected void setParseParameter(NewsInfoBean newsInfoBean, JSONObject jo) {
		// 设置标题
		String title = jo.getString("title");
		newsInfoBean.setTitle(title);
		// 设置发布时间 格式：2018年11月21日 14:08
		String pub_date = jo.getString("pubDate");
		pub_date = pub_date.replace("年", "-").replace("月", "-").replace("日", "");
		newsInfoBean.setPub_date(pub_date);
		// 设置网站关键字
		String keywords = jo.getString("keywords");
		if (!StringUtils.isNUll(keywords)) {
			keywords = keywords.substring(keywords.indexOf(",") + 1);
			keywords = keywords.replaceAll(",", " ");
		} else {
			keywords = "";
		}
		newsInfoBean.setKey_word(keywords);
	}

	@Override
	protected void setConstant(NewsInfoBean newsInfoBean) {
		newsInfoBean.setSource(Constants.NEWS_SOURCE_SINA.value);
		newsInfoBean.setSource_icon(Constants.NEWS_ICON_SINA.value);
	}

	@Override
	protected String getBodyExpession() {
		return "#artibody";
	}
}
