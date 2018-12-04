package com.isport.crawl.yidian;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
 
import com.alibaba.fastjson.JSONObject;
import com.geccocrawler.gecco.request.HttpGetRequest;
import com.geccocrawler.gecco.request.HttpRequest;
import com.isport.Constants;
import com.isport.bean.NewsInfoBean;
import com.isport.crawl.AbstractDetailPipeLine;

@Service
public class YidianDetail extends AbstractDetailPipeLine {
	
	//request 参数设置
	protected void setRequestParameters(HttpRequest request, NewsInfoBean newsInfoBean) {
		String indexUrl = request.getParameter("index_url"); 
		String tag = request.getParameter("tag");
		String authorId = request.getParameter("author_id");
		String channelId = request.getParameter("custom_channel_id"); 
		newsInfoBean.setIndex_url(indexUrl);
		newsInfoBean.setTag(tag);
		newsInfoBean.setAuthor_id(authorId);
		newsInfoBean.setChannel_id(channelId);
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractDetailPipeLine.class);
	@Override
	protected void setParseParameter(NewsInfoBean newsInfoBean, JSONObject jo) throws Exception {
		HttpRequest request = HttpGetRequest.fromJson(jo.getJSONObject("request"));
		// 获取标题
		String title = jo.getString("title");
		// 获取发布时间
		String strPubDate = request.getParameter("date"); 
		// 设置标题
		newsInfoBean.setTitle(title); 
		// 设置抽取时间
		newsInfoBean.setPub_date(strPubDate);
	}

	@Override
	protected void setConstant(NewsInfoBean newsInfoBean) {
		newsInfoBean.setSource(Constants.NEWS_SOURCE_YDZX.value);
		newsInfoBean.setSource_icon(Constants.NEWS_ICON_YDZX.value);
	}

	@Override
	protected String getBodyExpession() {
		return "div.content-bd";
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
			if(bodyDivs==null||bodyDivs.size()==0) {
				throw new Exception("视频页");
			}
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
