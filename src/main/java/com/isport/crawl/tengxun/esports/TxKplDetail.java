package com.isport.crawl.tengxun.esports;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.geccocrawler.gecco.request.HttpGetRequest;
import com.geccocrawler.gecco.request.HttpRequest;
import com.isport.Constants;
import com.isport.bean.NewsInfoBean;
import com.isport.crawl.AbstractDetailPipeLine;
import com.isport.utils.OssUtils;
import com.isport.utils.StringUtils;

@Service
public class TxKplDetail extends AbstractDetailPipeLine {
	private static final Logger LOGGER = LoggerFactory.getLogger(TxKplDetail.class);
	@Override
	protected void setParseParameter(NewsInfoBean newsInfoBean, JSONObject jo) throws Exception {
		// 获取标题
		String title = jo.getString("title");
		// 过滤广告
		if (StringUtils.isNUll(title)) {
			throw new Exception("广告页");
		}
		// json 转 HttpRequest 对象
		HttpRequest request = HttpGetRequest.fromJson(jo.getJSONObject("request"));
		// 获取pubDate
		String pubDate = request.getParameter("pubDate"); 
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
		return "div.article_detail";
	}
	
	@Autowired
	OssUtils ossUtils;
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
