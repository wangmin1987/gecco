package com.isport.crawl.tengxunNBA;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.geccocrawler.gecco.GeccoEngine;
import com.geccocrawler.gecco.dynamic.DynamicGecco;
import com.geccocrawler.gecco.redis.RedisStartScheduler;
import com.geccocrawler.gecco.spring.SpringPipelineFactory;
import com.isport.crawl.goal.GoalListPipeline;

@Service
public class TengxunNBACrawl {
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
	TengxunNBAList tengxunNBAList;
	public void register() {
		// 新闻列表spiderBean
		DynamicGecco.html()
				.gecco(new String[] { "http://www.wildto.com/v/index_{page}.html"}, "tengxunNBAList")
				.requestField("request").request().build().stringField("TXunjson").csspath("body").build().register();
		Class<?> keyword = DynamicGecco.html().stringField("keyword").csspath("a").text().build().register();
		// 详情spiderBean
		DynamicGecco.html().gecco("http://sports.qq.com/a/{date}/{filename}.htm", "tengxunNBADetail").requestField("request").request().build()
				.stringField("content").csspath("#Cnt-Main-Article-QQ").build().stringField("title").csspath("#Main-Article-QQ > div > div.qq_main > div.qq_article > div.hd > h1")
				.text().build().listField("keywords", keyword).csspath("#videokg a").build().stringField("pubDate")
				.csspath(".a_time").text().build().register();
	}

}
