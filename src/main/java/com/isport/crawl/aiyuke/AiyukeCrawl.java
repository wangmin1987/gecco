package com.isport.crawl.aiyuke;

import org.springframework.stereotype.Service;

import com.geccocrawler.gecco.dynamic.DynamicGecco;
/**
 * 爱羽客
 * @author 八斗体育
 *
 */
@Service
public class AiyukeCrawl {

	public void register() {
		// 列表页spiderBean
		Class<?> newsBriefs = DynamicGecco.html().stringField("docUrl").csspath("div.desc a").attr("href").build()
				.stringField("pubDate").csspath("p.info").text().build().register();
		DynamicGecco.html()
				.gecco(new String[] { "http://www.aiyuke.com/view/cate/index.htm",
						"http://www.aiyuke.com/view/cate/index.htm?page={page}" }, "aiyukeList")
				.requestField("request").request().build().listField("newsList", newsBriefs)
				.csspath("div.news_list_box").build().register();

		// 详情页SpiderBean
		DynamicGecco.html()
				.gecco(new String[] { "https://www.aiyuke.com/news/{year}/{month}/{id}.html" },
						"aiyukeDetail")
				.requestField("request").request().build().stringField("content").csspath("html").build()
				.stringField("title").csspath("div.news_content > h1").text().build().stringField("pubDate")
				.csspath("span.news_date").text().build().register();
	}
}
