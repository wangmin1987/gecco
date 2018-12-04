package com.isport.crawl.tengxun;

import java.util.ArrayList;
import java.util.List; 
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.isport.Constants;
import com.isport.bean.NewsInfoBean; 

@Service
public class TxSADetail extends TxAbstractDetail { 
	@Override
	protected void setParseParameter(NewsInfoBean newsInfoBean, JSONObject jo) {
		// 获取标题
		String title = jo.getString("title");
		// 获取发布时间
		String pubDate = jo.getString("pubDate");
		// 获取关键字
		JSONArray keywords = jo.getJSONArray("keywords");
		StringBuilder keywordBuilder = new StringBuilder();
//		LOGGER.info("keywords size:" + keywords.size());
		for (Object keyword : keywords) {
			JSONObject item = JSONObject.parseObject(keyword.toString());
			keywordBuilder.append(item.getString("keyword"));
			keywordBuilder.append(" ");
		}
//		LOGGER.info("Detail:" + title + "," + keywordBuilder.toString() + "," + pubDate);
		newsInfoBean.setTitle(title);
		newsInfoBean.setKey_word(keywordBuilder.toString());
		newsInfoBean.setPub_date(pubDate);
	}

	@Override
	protected void setConstant(NewsInfoBean newsInfoBean) {
		newsInfoBean.setSource(Constants.NEWS_SOURCE_QQ.value);
		newsInfoBean.setSource_icon(Constants.NEWS_ICON_QQ.value);
	} 
	@Override
	protected void processImage(final NewsInfoBean newsInfoBean) {
		List<String> titleImgs = newsInfoBean.getTitle_imgs();
		List<String> newsTitleImgs = new ArrayList<String>();
		for (String img : titleImgs) {
			if (img.startsWith("//")) {
				// 添加协议
				img = "http:" + img;
			}
			newsTitleImgs.add(img);
		}
		newsInfoBean.setTitle_imgs(newsTitleImgs);
	}
	 
}
