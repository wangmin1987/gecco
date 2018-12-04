package com.isport.crawl.tengxun;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import com.isport.bean.NewsInfoBean;
import com.isport.crawl.AbstractDetailPipeLine;

/**
 * 腾讯网站的通用的处理程序
 * 
 * @author 八斗体育
 *
 */
public abstract class TxAbstractDetail extends AbstractDetailPipeLine {
	/**
	 * 解析内容
	 * 
	 * @param newsInfoBean
	 * @return
	 * @throws Exception
	 */
	protected boolean parseContent(final NewsInfoBean newsInfoBean) throws Exception {
		String html = newsInfoBean.getHtml();
		// 解析html
		Document doc = Jsoup.parse(html);
		// 清除视频
		doc.select("div.rv-root-v2").remove();
		// 获取body体标签
		Elements bodyDivs = doc.select("#Cnt-Main-Article-QQ");
		if (bodyDivs.size() == 0) {
			throw new Exception("非法的页面");
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
	}

	@Override
	protected String getBodyExpession() {
		return "#Cnt-Main-Article-QQ";
	}

}
