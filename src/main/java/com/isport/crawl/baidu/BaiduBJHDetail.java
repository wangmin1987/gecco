package com.isport.crawl.baidu;

import java.util.HashMap;
import java.util.Map;

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
import com.isport.Constants;
import com.isport.bean.NewsInfoBean;
import com.isport.crawl.AbstractDetailPipeLine;
import com.isport.utils.OssUtils;

@Service
public class BaiduBJHDetail extends AbstractDetailPipeLine {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractDetailPipeLine.class);

	@Override
	protected void setParseParameter(NewsInfoBean newsInfoBean, JSONObject jo) {
		// 获取标题
		String title = jo.getString("title");
		// 获取发布时间
		String strPubDate = jo.getString("pubDate");
		// 设置标题
		newsInfoBean.setTitle(title);
		// 设置抽取时间
		newsInfoBean.setPub_date(strPubDate);
	}

	@Override
	protected void setConstant(NewsInfoBean newsInfoBean) {
		newsInfoBean.setSource(Constants.NEWS_SOURCE_BJH.value);
		newsInfoBean.setSource_icon(Constants.NEWS_ICON_BJH.value);
	}

	@Override
	protected String getBodyExpession() {
		return "div.mainContent";
	}

	/**
	 * 解析内容
	 * 
	 * @param newsInfoBean
	 * @return
	 * @throws Exception
	 */
	protected boolean parseContent(final NewsInfoBean newsInfoBean) throws Exception {
		String url = newsInfoBean.getUrl(), parent_url = newsInfoBean.getIndex_url();
		try {
			String html = newsInfoBean.getHtml();
			// 解析html
			Document doc = Jsoup.parse(html);
			// 获取body体标签
			Elements bodyDivs = doc.select(getBodyExpession());
			Element bodyDiv = bodyDivs.get(0);
			// 修改内容区IMG标签的地址
//			newsParseService.uploadImg(bodyDiv);
			Map<String, String> headers = new HashMap<String, String>();
			headers.put("Referer", url);
			uploadImg(bodyDiv, headers);
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
			LOGGER.error("parent_url:" + parent_url + ",url:" + url + ",解析内容错误:" + e.getMessage());
		}
		return false;
	}

	@Autowired
	OssUtils ossUtils;

	/**
	 * 资讯图片上传
	 */
	public boolean uploadImg(Element element, Map<String, String> headers) {
		try {
			Elements imgs = element.select("img");
			for (Element ele : imgs) {
				if (ele.attr("src") != "") {
					String href = ele.attr("src");
					if (href.indexOf("http") == -1) {
						href = "http:" + href;
					}
					String filePath = ossUtils.uploadImage(href,headers);
					if (filePath == null) {
						return false;
					}
					ele.attr("src", filePath);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
}
