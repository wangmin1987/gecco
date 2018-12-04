package com.isport.crawl.aiyuke;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.isport.Constants;
import com.isport.bean.NewsInfoBean;
import com.isport.crawl.AbstractDetailPipeLine;

@Service
public class AiyukeDetail extends AbstractDetailPipeLine{
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractDetailPipeLine.class);
	@Override
	protected void setParseParameter(NewsInfoBean newsInfoBean, JSONObject jo) throws Exception {
		String title = jo.getString("title");
		String pubDate = jo.getString("pubDate");  
		newsInfoBean.setTitle(title);
		newsInfoBean.setPub_date(pubDate); 
	}

	@Override
	protected void setConstant(NewsInfoBean newsInfoBean) {
		newsInfoBean.setSource(Constants.NEWS_SOURCE_AYK.value);
		newsInfoBean.setSource_icon(Constants.NEWS_ICON_AYK.value);
	}

	@Override
	protected String getBodyExpession() { 
		return "#js-news-txtbody";
	} 
	
	/**
	 * 解析内容
	 * 
	 * @param newsInfoBean
	 * @return
	 * @throws Exception 
	 */
	protected boolean parseContent(final NewsInfoBean newsInfoBean) throws Exception {
		String url = newsInfoBean.getUrl(),parent_url =newsInfoBean.getIndex_url();
		try {
			String html = newsInfoBean.getHtml(); 
			// 解析html
			Document doc = Jsoup.parse(html); 
			// 获取body体标签
			Elements bodyDivs = doc.select(getBodyExpession());
			Element bodyDiv = bodyDivs.get(0);
			bodyDiv.select("p.declare").remove(); 
			// 修改内容区IMG标签的地址
			newsParseService.uploadImg(bodyDiv);
			// 删除A标签保留文本
			String cleanContent = Jsoup.clean(bodyDiv.html(), Whitelist.relaxed().removeTags("a"));
			// 清洁内容
			newsInfoBean.setContent(cleanContent);
			// 内容摘要
			newsInfoBean.setSummary(newsParseService.getSummary(cleanContent));
			// 咨询配图
			newsInfoBean.setTitle_imgs(newsParseService.getImage(bodyDiv));
			// 咨询非法内容过滤
			newsParseService.filter(cleanContent);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("parent_url:"+parent_url+",url:" + url + ",解析内容错误:" + e.getMessage());
		}
		return false;
	}
}
