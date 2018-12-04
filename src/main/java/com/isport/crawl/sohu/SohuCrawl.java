package com.isport.crawl.sohu;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.geccocrawler.gecco.GeccoEngine;
import com.geccocrawler.gecco.dynamic.DynamicGecco;
import com.geccocrawler.gecco.redis.RedisStartScheduler;
import com.geccocrawler.gecco.spring.SpringPipelineFactory;
import com.isport.crawl.goal.GoalListPipeline;

@Service
public class SohuCrawl {
	// redis 主机
	@Value("${spring.redis.host}")
	private String redis_host;
	// redis 端口
	@Value("${spring.redis.port}")
	private String redis_port;

	// 扩展工厂
	@Autowired
	SpringPipelineFactory springPipelineFactory;

	@Autowired
	SohuList sohuList;

	public void register() {
		// 新闻块spiderBean
		Class<?> newsBriefs = DynamicGecco.html().stringField("docUrl").csspath("a").attr("href").build()
				.stringField("pubDate").csspath("span").text().build().register();
		// 新闻列表spiderBean
		DynamicGecco.html()
				.gecco(new String[] { "http://sports.sohu.com/{game}/index.shtml",
						"http://sports.sohu.com/{game}/index_{pageNum}.shtml" }, "sohuList")
				.requestField("request").request().build().listField("newsList", newsBriefs)
				.csspath("div.f14list >ul li").build().stringField("nextUrl").csspath("script").build().register();
		Class<?> keyword = DynamicGecco.html().stringField("keyword").csspath("a").text().build().register();
		// 详情spiderBean
		DynamicGecco.html().gecco(new String[] {"https://www.sohu.com/a/{id}","http://www.sohu.com/a/{id}"}, "sohuDetail").requestField("request").request().build()
				.stringField("content").csspath("html").build().stringField("title").csspath("div.text-title > h1")
				.text().build().listField("keywords", keyword).csspath("span.tag a").build().stringField("pubDate")
				.csspath("#news-time").text().build().register();

	}

	 
}
