package com.isport.crawl.sina;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.isport.Constants;
import com.isport.bean.NewsInfoBean;
import com.isport.crawl.AbstractDetailPipeLine;
import com.isport.utils.CrawlDateUtil;

@Service
public class SinaGBDetail extends AbstractDetailPipeLine {
	private static final Logger LOGGER = LoggerFactory.getLogger(SinaGBDetail.class);

	@Override
	protected void setParseParameter(NewsInfoBean newsInfoBean, JSONObject jo) {
		// 获取标题
		String title = jo.getString("title");
		// 获取发布时间
		String pubDate = jo.getString("pubDate");
		pubDate = CrawlDateUtil.dateConvert3(pubDate);
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
		newsInfoBean.setSource(Constants.NEWS_SOURCE_SINA.value);
		newsInfoBean.setSource_icon(Constants.NEWS_ICON_SINA.value);
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

	@Override
	protected String getBodyExpession() {
		return "#artibody";
	}

	@Override
	protected void newsInfoBeanPackageAfter(NewsInfoBean newsInfoBean) throws Exception {
//		LOGGER.info("FILTER VEIDO");
		// 过滤视频详情页
		String html = newsInfoBean.getHtml(); 
		if (newsInfoBean.getTitle_imgs().size() == 0 && checkVideoImg(html)) {
			throw new Exception("视频新闻，无图片");
		}
	}

	private boolean checkVideoImg(String content) {
		try {
			Element ele = Jsoup.parse(content);
			Elements videoDivs = ele.select("div.playBox");
			Element videoDiv = videoDivs.get(0);
			if (videoDiv != null)
				return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}
