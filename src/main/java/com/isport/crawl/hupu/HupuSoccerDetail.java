package com.isport.crawl.hupu;
 
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.isport.Constants;
import com.isport.bean.NewsInfoBean;
import com.isport.crawl.AbstractDetailPipeLine; 

@Service
public class HupuSoccerDetail extends AbstractDetailPipeLine{ 
	@Override
	protected void setParseParameter(NewsInfoBean newsInfoBean, JSONObject jo) {
		String title = jo.getString("title");
		String pubDate = jo.getString("pubDate");  
		newsInfoBean.setTitle(title);
		newsInfoBean.setPub_date(pubDate); 
	}

	@Override
	protected void setConstant(NewsInfoBean newsInfoBean) {
		newsInfoBean.setSource(Constants.NEWS_SOURCE_HUPU.value);
		newsInfoBean.setSource_icon(Constants.NEWS_ICON_HUPU.value);
	}

	@Override
	protected String getBodyExpession() {
		return "div.artical-content-read";
	}

}
