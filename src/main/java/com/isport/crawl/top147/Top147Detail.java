package com.isport.crawl.top147;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.geccocrawler.gecco.request.HttpRequest;
import com.isport.Constants;
import com.isport.bean.NewsInfoBean;
import com.isport.crawl.AbstractDetailPipeLine;
import com.isport.utils.StringUtils;

@Service
public class Top147Detail extends AbstractDetailPipeLine {

	@Override
	protected void setRequestParameters(HttpRequest request, NewsInfoBean newsInfoBean) {
		String indexUrl = request.getParameter("index_url");
		String tag = request.getParameter("tag");
		String authorId = request.getParameter("author_id");
		String channelId = request.getParameter("channel_id");
		newsInfoBean.setIndex_url(indexUrl);
		newsInfoBean.setTag(tag);
		newsInfoBean.setAuthor_id(authorId);
		newsInfoBean.setChannel_id(channelId);
		// 设置发布时间
		String pubDate = request.getParameter("pub_date");
		newsInfoBean.setPub_date(pubDate);
	}

	@Override
	protected void setParseParameter(NewsInfoBean newsInfoBean, JSONObject jo) {
		// 设置标题
		String title = jo.getString("title");
		newsInfoBean.setTitle(title);
		// 设置网站关键字
		String keywords = jo.getString("keywords");
		if (!StringUtils.isNUll(keywords)) {
			int index = keywords.lastIndexOf(",");
			keywords = index >= 0 ? keywords.substring(index + 1).replaceAll("，", " ") : "";
		} else {
			keywords = "";
		}
		newsInfoBean.setKey_word(keywords);
	}

	@Override
	protected void setConstant(NewsInfoBean newsInfoBean) {
		newsInfoBean.setSource(Constants.NEWS_SOURCE_TOP147.value);
		newsInfoBean.setSource_icon(Constants.NEWS_ICON_TOP147.value);
	}

	@Override
	protected String getBodyExpession() {
		return "#newsContent";
	}
}
