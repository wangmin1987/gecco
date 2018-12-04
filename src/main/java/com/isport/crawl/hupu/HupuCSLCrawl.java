package com.isport.crawl.hupu;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.geccocrawler.gecco.GeccoEngine;
import com.geccocrawler.gecco.dynamic.DynamicGecco;
import com.geccocrawler.gecco.redis.RedisStartScheduler;
import com.geccocrawler.gecco.spring.SpringPipelineFactory;

@Service
public class HupuCSLCrawl {
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
	HupuCSLList hupuCSLList;

	public void register() {
		// 详情页SpiderBean
//		DynamicGecco.html().gecco(new String[] { "https://voice.hupu.com/china/{id}.html" }, "hupuSoccerDetail")
//				.requestField("request").request().build().stringField("content").csspath("html").build()
//				.stringField("title").csspath("div.artical-title > h1").text().build().stringField("pubDate")
//				.csspath("#pubtime_baidu").text().build().register();
		// 列表页spiderBean
		Class<?> newsBriefs = DynamicGecco.html().stringField("docUrl").csspath("a").attr("href").build()
				.stringField("pubDate").csspath("a.time").text().build().register();
		DynamicGecco.html().gecco(new String[] { "https://voice.hupu.com/china/1","https://voice.hupu.com/china/2" }, "hupuCSLList")
				.requestField("request").request().build().listField("newsList", newsBriefs).csspath("div.news-list li")
				.build().register();

	}

	@SuppressWarnings("unchecked")
	public void crawl() {
		System.out.println("HupuCSLCrawl start");
		// 列表页spiderBean
		Class<?> newsBriefs = DynamicGecco.html().stringField("docUrl").csspath("a").attr("href").build()
				.stringField("pubDate").csspath("a.time").text().build().register();
		DynamicGecco.html().gecco(new String[] { "https://voice.hupu.com/china/{page}" }, "hupuCSLList")
				.requestField("request").request().build().listField("newsList", newsBriefs).csspath("div.news-list li")
				.build().register();
		// 线程数 等待时间
		int thread = 1, interval = 1000;
		GeccoEngine.create()
				// 自定义下载url调度
				.scheduler(new RedisStartScheduler(redis_host + ":" + redis_port))
				// 自定义管道过滤器工厂
				.pipelineFactory(springPipelineFactory)
				// 扫描包路径
				.classpath("com.isport.crawl.hupu")
				// 启动地址
//				.start("https://voice.hupu.com/china/1")
				// 启动spider数量
				.thread(thread)
				// 延迟等待事件
				.interval(interval)
				// 阻塞启动
				.run();

		// 详情页SpiderBean
		DynamicGecco.html().gecco(new String[] { "https://voice.hupu.com/china/{id}.html" }, "hupuSoccerDetail")
				.requestField("request").request().build().stringField("content").csspath("html").build()
				.stringField("title").csspath("div.artical-title > h1").text().build().stringField("pubDate")
				.csspath("#pubtime_baidu").text().build().register();

		GeccoEngine.create()
				// 自定义下载url调度
				.scheduler(new RedisStartScheduler(redis_host + ":" + redis_port))
				// 自定义管道过滤器工厂
				.pipelineFactory(springPipelineFactory)
				// 扫描包路径
				.classpath("com.isport.crawl.hupu").thread(thread)
				// 延迟等待事件
				.interval(interval).start(hupuCSLList.getSortRequests())
				// 阻塞启动
				.run();
		System.out.println("HupuCSLCrawl end");
	}
}
