package com.isport.crawl.netease;
 
import org.springframework.stereotype.Service;
 
import com.geccocrawler.gecco.dynamic.DynamicGecco; 

@Service
public class NTESCrawl {

	public void register() {
		System.out.println("NTESCrawl start");
		// 新闻块spiderBean 截取详情地址和时间
		Class<?> newsBriefs = DynamicGecco.html().stringField("docUrl").csspath("h3 a").attr("href").build()
				.stringField("pubDate").csspath("div.info div.post_date").text().build().register();
		// 列表页 spiderBean
		DynamicGecco.html().requestField("request").request().build()
				// 匹配地址
				.gecco(new String[] { "http://sports.163.com/special/00051F1O/morexjnews.html",
						"http://sports.163.com/special/00051L23/ppq09.html",
						"http://sports.163.com/special/00051L23/ppq09_{page}.html",
						"http://sports.163.com/special/00051L24/ymq09.html",
						"http://sports.163.com/special/00051L24/ymq09_{page}.html",
						"http://sports.163.com/special/00051F1O/morexjnews_{page}.html", "http://sports.163.com/dj/",
						"http://sports.163.com/dj_{page}.html" }, "ntesList")
				.listField("newsList", newsBriefs).csspath("div.new_list div.news_item").build().register();

		// 详情spiderBean
		DynamicGecco.html().gecco("http://sports.163.com/{date}/{id1}/{id2}/{id3}.html", "ntesDetail")
				.requestField("request").request().build().stringField("content").csspath("html").build()
				// 标题
				.stringField("title").csspath("#epContentLeft h1").text().build()
				// 发布日期
				.stringField("pubDate").csspath("div.post_time_source").text().build().register();

	}

}
