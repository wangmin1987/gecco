package com.isport.crawl.tengxun.esports;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.isport.Constants;
import com.isport.bean.NewsInfoBean;
import com.isport.crawl.AbstractDetailPipeLine;
import com.isport.utils.StringUtils;

@Service
public class Txfo4Detail extends AbstractDetailPipeLine {

	@Override
	protected void setParseParameter(NewsInfoBean newsInfoBean, JSONObject jo) throws Exception {
		// 获取标题
		String title = jo.getString("title");
		// 获取发布时间
		String pubDate = jo.getString("pubDate");
		//过滤广告
		if(StringUtils.isNUll(title)&&StringUtils.isNUll(pubDate)) {
			throw new Exception("广告页");
		}
		// 去除头部标签/ 
		pubDate = pubDate.substring(2, pubDate.length());
		// 设置标题
		newsInfoBean.setTitle(title);
		// 设置时间
		newsInfoBean.setPub_date(pubDate);
	}

	@Override
	protected void setConstant(NewsInfoBean newsInfoBean) {
		// 设置来源
		newsInfoBean.setSource(Constants.NEWS_SOURCE_QQ.value);
		// 设置ico
		newsInfoBean.setSource_icon(Constants.NEWS_ICON_QQ.value);
	}

	@Override
	protected String getBodyExpession() {
		// 内容选择表达式
		return "div.con";
	}

}
