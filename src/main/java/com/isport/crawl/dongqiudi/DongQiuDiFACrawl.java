package com.isport.crawl.dongqiudi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.geccocrawler.gecco.GeccoEngine;
import com.geccocrawler.gecco.dynamic.DynamicGecco;
import com.geccocrawler.gecco.redis.RedisStartScheduler;
import com.geccocrawler.gecco.spring.SpringPipelineFactory;

@Service
public class DongQiuDiFACrawl {
	// spring pipeline 工厂
	@Autowired
	SpringPipelineFactory springPipelineFactory;
	// redis 配置
	@Value("${spring.redis.host}")
	private String redis_host;

	@Value("${spring.redis.port}")
	private String redis_port;
	// spring 管理对象 提供获取详情页队列
	@Autowired
	DongQiuDiFAList dongQiuDiFAList;
	
	public void register() { 
		// 列表页 spiderBean
		DynamicGecco.html()
				// 请求对象
				.requestField("request").request().build() 				
				.gecco("https://www.dongqiudi.com/data/team/archive?team={id}&page={page}", "dongQiuDiFAList")
				.stringField("dongQiuDiJson").csspath("body").build().stringField("page").requestParameter().build()
				.register();
		Class<?> keywordsBriefs = DynamicGecco.html().stringField("keyword").csspath("span").text().build().register();
		// 详情页 spiderBean
		DynamicGecco.html()
				.gecco(new String[] { "https://www.dongqiudi.com/archive/{id}.html",
						"http://www.dongqiudi.com/archive/{id}.html" }, "dongQiuDiFADetail")
				.requestField("request").request().build().stringField("content").csspath("html").build()
				.stringField("title").csspath("div.detail > h1").build().stringField("pubDate")
				.csspath("div.detail > h4 span.time").build().listField("keywords", keywordsBriefs)
				.csspath("div.lists a").build().register(); 
	}
	@SuppressWarnings("unchecked")
	// @Scheduled(fixedDelay = 1000 * 60 * 30)
	public void crawl() {
		System.out.println("DongQiuDiFACrawl start");
		// 列表页 spiderBean
		DynamicGecco.html()
				// 请求对象
				.requestField("request").request().build()
				.gecco("https://www.dongqiudi.com/data/team/archive?team={id}&page={page}", "dongQiuDiFAList")
				.stringField("dongQiuDiJson").csspath("body").build().stringField("page").requestParameter().build()
				.register();

		// 线程数量 和 等待延迟设置
		final int thread = 10, interval = 1000;
		GeccoEngine.create()
				// spring pipelineFactory
				.pipelineFactory(springPipelineFactory)
				// redis scheduler
				.scheduler(new RedisStartScheduler(redis_host + ":" + redis_port))
				.classpath("com.isport.crawl.dongqiudi")
//		.start("https://www.dongqiudi.com/team/50000330.html")
				.thread(thread).interval(interval).run();

		Class<?> keywordsBriefs = DynamicGecco.html().stringField("keyword").csspath("span").text().build().register();
		// 详情页 spiderBean
		DynamicGecco.html()
				.gecco(new String[] { "https://www.dongqiudi.com/archive/{id}.html",
						"http://www.dongqiudi.com/archive/{id}.html" }, "dongQiuDiFADetail")
				.requestField("request").request().build().stringField("content").csspath("html").build()
				.stringField("title").csspath("div.detail > h1").build().stringField("pubDate")
				.csspath("div.detail > h4 span.time").build().listField("keywords", keywordsBriefs)
				.csspath("div.lists a").build().register();

		GeccoEngine.create().scheduler(new RedisStartScheduler(redis_host + ":" + redis_port))
				.pipelineFactory(springPipelineFactory).classpath("com.isport.crawl.dongqiudi")
				.start(dongQiuDiFAList.getSortRequests()).thread(thread).interval(interval).run();
		System.out.println("DongQiuDiFACrawl end");
	}
}
