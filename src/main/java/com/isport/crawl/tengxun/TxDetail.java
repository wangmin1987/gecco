package com.isport.crawl.tengxun;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.isport.Constants;
import com.isport.bean.NewsInfoBean;
import com.isport.utils.StringUtils;

@Service
public class TxDetail extends TxAbstractDetail {

	@Override
	protected void setParseParameter(NewsInfoBean newsInfoBean, JSONObject jo) {
		// 获取标题
		String title = jo.getString("title");
		// 获取发布时间
		String strPubDate = jo.getString("pubDate");
		if (StringUtils.isNUll(strPubDate)) {
			strPubDate = jo.getString("pubDate2");
		}
		// 设置标题
		newsInfoBean.setTitle(title);
		// 设置抽取时间
		newsInfoBean.setPub_date(strPubDate);
	}

	@Override
	protected void setConstant(NewsInfoBean newsInfoBean) {
		newsInfoBean.setSource(Constants.NEWS_SOURCE_QQ.value);
		newsInfoBean.setSource_icon(Constants.NEWS_ICON_QQ.value);
	}

}
