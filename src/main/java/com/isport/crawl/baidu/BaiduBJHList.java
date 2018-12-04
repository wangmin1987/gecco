package com.isport.crawl.baidu;
 
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.isport.crawl.AbstractListPipeLine;
import com.isport.utils.CrawlDateUtil;

@Service
public class BaiduBJHList extends AbstractListPipeLine {

	@Override
	protected Object getList(JSONObject jo) {
		String newsList = jo.getObject("newsList", String.class);  
		// BigPipe.onPageletArrive( 头部
		newsList = newsList.substring(24, newsList.length() - 2);
		JSONObject obj = JSONObject.parseObject(newsList);
		String html = obj.getString("html");
		Document document = Jsoup.parse(html);
		Elements elements = document.select("li");
		return elements;
	}

	@Override
	protected long getNewsTime(Object obj) throws Exception {
		Element element = (Element) obj;
		String pubDate = element.select("div.time").get(0).text();
		return CrawlDateUtil.timeConvert2(pubDate);
	}

	@Override
	protected String getNewsDocUrl(String baseUrl, Object obj) {
		Element element = (Element) obj;
		String docUrl = element.select("a.typeNews").get(0).attr("href");
		return docUrl;
	}

	@Override
	protected String getNextUrl(String url, String nextUrl, int page) {
		return ILLEGAL_URL;
	}

}
