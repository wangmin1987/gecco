package com.isport.crawl.footballclub;

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
import com.isport.utils.CrawlDateUtil;

@Service
public class RenHeDetail extends AbstractDetailPipeLine {

	private static final Logger LOGGER = LoggerFactory.getLogger(RenHeDetail.class);

	@Override
	protected void setParseParameter(NewsInfoBean newsInfoBean, JSONObject jo) {
		String title = jo.getString("title");
		String pubDate = jo.getString("pubDate");
		pubDate = translate(pubDate);
		newsInfoBean.setTitle(title);
		newsInfoBean.setPub_date(pubDate);
	}

	private String translate(String date) {
		String pubDate = CrawlDateUtil.dateConvert4(date); 
		String[] tt = pubDate.split("\\s+");
		final int first = tt[0].indexOf("-");
		final int last = tt[0].lastIndexOf("-");
		String year = tt[0].substring(0, first);
		String month = tt[0].substring(first + 1, last);
		String day = tt[0].substring(last + 1, tt[0].length());
		String hsm = tt[1].length() == 8 ? " " + tt[1] : " 0" + tt[1]; 
		if (Integer.valueOf(month) < 10) {
			month = "0" + month;
		}
		if (Integer.valueOf(day) < 10) {
			day = "0" + day;
		}
		pubDate = year + "-" + month + "-" + day + hsm;
		return pubDate;
	}

	public static void main(String[] args) {
		String pubDate = "2018/10/25 16:45";
		pubDate = CrawlDateUtil.dateConvert4(pubDate);
		String[] tt = pubDate.split("\\s+");
		final int first = tt[0].indexOf("-");
		final int last = tt[0].lastIndexOf("-");
		String year = tt[0].substring(0, first);
		String month = tt[0].substring(first + 1, last);
		String day = tt[0].substring(last + 1, tt[0].length());
		String hsm = tt[1].length() == 8 ? " " + tt[1] : " 0" + tt[1]; 
		if (Integer.valueOf(month) < 10) {
			month = "0" + month;
		}
		if (Integer.valueOf(day) < 10) {
			day = "0" + day;
		}
		pubDate = year + "-" + month + "-" + day + hsm;
		System.out.println(pubDate);
	}

	@Override
	protected void setConstant(NewsInfoBean newsInfoBean) {
		newsInfoBean.setSource(Constants.NEWS_SOURCE_RENHE.value);
		newsInfoBean.setSource_icon(Constants.NEWS_ICON_RENHE.value);
	}

	@Override
	protected String getBodyExpession() {
		return "section";
	}

	@Override
	protected boolean parseContent(NewsInfoBean newsInfoBean) throws Exception {
		String url = newsInfoBean.getUrl(), parent_url = newsInfoBean.getIndex_url();
		try {
			String html = newsInfoBean.getHtml();
			// 解析html
			Document doc = Jsoup.parse(html);
			// 获取body体标签
			Elements bodyDivs = doc.select(getBodyExpession());
			Element bodyDiv = bodyDivs.get(0);
			// 删除标题
			bodyDiv.select("h1").remove();
			// 删除发布时间
			bodyDiv.select("p.time").remove();
			// 删除视频
			bodyDiv.select("div.jp-video").remove();
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
			LOGGER.error("parent_url:" + parent_url + ",url:" + url + ",解析内容错误:" + e.getMessage());
		}
		return false;
	}

}
