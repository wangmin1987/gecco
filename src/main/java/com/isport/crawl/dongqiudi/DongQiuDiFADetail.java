package com.isport.crawl.dongqiudi;

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

@Service
public class DongQiuDiFADetail extends AbstractDetailPipeLine {
	private static final Logger LOGGER = LoggerFactory.getLogger(DongQiuDiFADetail.class);

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
			Elements bodyDivs = ele.select(getBodyExpession());
			Element bodyDiv = bodyDivs.get(0);
//			LOGGER.info(bodyDiv.html());
			return this.checkVideoImg(bodyDiv);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private boolean checkVideoImg(Element ele) {
		try {
			Elements eles = ele.select("div.video");
			if (eles != null && eles.size() > 0) {
				Elements imgEles = ele.select("img");
				if (imgEles == null || imgEles.size() == 0) {
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	protected void setParseParameter(NewsInfoBean newsInfoBean, JSONObject jo) {
		String title = jo.getString("title");
		String pubDate = jo.getString("pubDate");
		// 关键字生成
//		LOGGER.info("newsInfoBean.getUrl:"+newsInfoBean.getUrl());
		JSONArray keywords = jo.getJSONArray("keywords");
		StringBuilder keywordBuilder = new StringBuilder();
//		LOGGER.info("keywords size:" + keywords.size());
		for (Object keyword : keywords) {
			JSONObject item = JSONObject.parseObject(keyword.toString());
			keywordBuilder.append(item.getString("keyword"));
			keywordBuilder.append(" ");
		}
//		LOGGER.info("Detail:" + title + "," + pubDate);
		// 日期格式化
		newsInfoBean.setTitle(title);
		newsInfoBean.setPub_date(pubDate);
		newsInfoBean.setKey_word(keywordBuilder.toString());
	}

	@Override
	protected void setConstant(NewsInfoBean newsInfoBean) {
		// 设置source 常量
		newsInfoBean.setSource(Constants.NEWS_SOURCE_DONGQIUDI.value);
		newsInfoBean.setSource_icon(Constants.NEWS_ICON_DQD.value);
	}

	@Override
	protected String getBodyExpession() {
		return "div.detail > div";
	}

}
