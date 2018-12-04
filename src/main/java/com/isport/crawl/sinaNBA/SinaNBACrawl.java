package com.isport.crawl.sinaNBA;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.geccocrawler.gecco.GeccoEngine;
import com.geccocrawler.gecco.dynamic.DynamicGecco;
import com.geccocrawler.gecco.redis.RedisStartScheduler;
import com.geccocrawler.gecco.spring.SpringPipelineFactory;
import com.isport.crawl.goal.GoalListPipeline;

@Service
public class SinaNBACrawl {
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
	SinaNBAList sinaNBAList;
	public void register() {
		// 新闻块spiderBean
				Class<?> newsBriefs = DynamicGecco.html().stringField("docUrl").csspath("a").attr("href").build()
						.register();
				Class<?> newsPubBriefs = DynamicGecco.html().stringField("pubDate").csspath("span").text().build()
						.register();
				// 新闻列表spiderBean
				DynamicGecco.html()
						.gecco(new String[] { "http://sports.sina.com.cn/nba/{num}.shtml"}, "sinaNBAList")
						.requestField("request").request().build().listField("newsUrlList", newsBriefs)
						.csspath("#right a").build().listField("newsSpanList", newsPubBriefs)
						.csspath("#right span").build().register();
		Class<?> keyword = DynamicGecco.html().stringField("keyword").csspath("a").text().build().register();
		// 详情spiderBean
		DynamicGecco.html().gecco("http://sports.sina.com.cn/basketball/nba/{newstime}/{id}.shtml", "sinaNBADetail").requestField("request").request().build()
				.stringField("content").csspath("html").build().stringField("title").csspath("body > div.main-content.w1240 > h1")
				.text().build().listField("keywords", keyword).csspath("#keywords a").build().stringField("pubDate")
				.csspath("#top_bar > div > div.date-source > span").text().build().register();
	}

}
