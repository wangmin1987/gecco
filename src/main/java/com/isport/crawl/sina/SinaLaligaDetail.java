package com.isport.crawl.sina;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.isport.Constants;
import com.isport.bean.NewsInfoBean;
import com.isport.crawl.AbstractDetailPipeLine;

@Service
public class SinaLaligaDetail extends AbstractDetailPipeLine {
	private static final Logger LOGGER = LoggerFactory.getLogger(SinaLaligaDetail.class);

	@Override
	protected void setParseParameter(NewsInfoBean newsInfoBean, JSONObject jo) {
		String title = jo.getString("title");
		JSONArray keywords = jo.getJSONArray("keywords");
		StringBuilder keywordBuilder = new StringBuilder(); 
		for (Object keyword : keywords) {
			JSONObject item = JSONObject.parseObject(keyword.toString());
			keywordBuilder.append(item.getString("keyword"));
			keywordBuilder.append(" ");
		}
		String pubDate = jo.getString("pubDate");
		pubDate = pubDate.replace("年", "-");
		pubDate = pubDate.replace("月", "-");
		pubDate = pubDate.replace("日", "");
		newsInfoBean.setTitle(title);
		newsInfoBean.setKey_word(keywordBuilder.toString());
		newsInfoBean.setPub_date(pubDate);
	}

	@Override
	protected void setConstant(NewsInfoBean newsInfoBean) {
		newsInfoBean.setSource(Constants.NEWS_SOURCE_SINA.value);
		newsInfoBean.setSource_icon(Constants.NEWS_ICON_SINA.value);
	}
	
	@Override
	protected void processImage(final NewsInfoBean newsInfoBean) {
		 List<String> titleImgs =newsInfoBean.getTitle_imgs();
		 List<String> newsTitleImgs = new ArrayList<String>();
		 for(String img:titleImgs) {
			 if(img.startsWith("//")) {
				 //添加协议
				 img = "http:"+img;
			 }
			 newsTitleImgs.add(img);
		 }
		 newsInfoBean.setTitle_imgs(newsTitleImgs);
	}
	
	@Override
	protected String getBodyExpession() {
		return "#artibody";
	}

}
