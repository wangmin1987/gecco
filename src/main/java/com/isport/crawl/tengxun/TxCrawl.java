package com.isport.crawl.tengxun;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.geccocrawler.gecco.GeccoEngine;
import com.geccocrawler.gecco.dynamic.DynamicGecco;
import com.geccocrawler.gecco.redis.RedisStartScheduler;
import com.geccocrawler.gecco.spring.SpringPipelineFactory; 

@Service
public class TxCrawl {
	@Autowired
	SpringPipelineFactory springPipelineFactory;
	// redis 主机
	@Value("${spring.redis.host}")
	private String redis_host;
	// redis 端口
	@Value("${spring.redis.port}")
	private String redis_port;

	@Autowired
	TxList txList;
	
	public void register() {
		// 列表页spiderBean
		DynamicGecco.html().gecco(new String[] {"http://sports.qq.com/l/isocce/xijia/laliganews.htm","http://sports.qq.com/l/isocce/xijia/laliganews_{page}.htm"}, "txList")
				// 请求对象
				.requestField("request").request().build()
				.field("newsList", JSONArray.class).jsvar("ARTICLE_LIST","$.listInfo")
				.build().register();
		
		// 详情spiderBean
//		DynamicGecco.html().gecco("http://sports.qq.com/a/{id1}/{id2}.htm", "txDetail")
//				// 定义请求对象
//				.requestField("request").request().build().stringField("content").csspath("html").build()
//				// 定义标题
//				.stringField("title").csspath("div.hd h1").text().build()
//				// 定义发布日期
//				.stringField("pubDate").csspath("span.a_time").text().build().register();

	}

	@SuppressWarnings("unchecked")
	public void crawl() { 
		System.out.println("TxCrawl start");
		// 列表页spiderBean
		DynamicGecco.html().gecco(new String[] {"http://sports.qq.com/l/isocce/xijia/laliganews.htm","http://sports.qq.com/l/isocce/xijia/laliganews_{page}.htm"}, "txList")
				// 请求对象
				.requestField("request").request().build()
				.field("newsList", JSONArray.class).jsvar("ARTICLE_LIST","$.listInfo")
				.build().register();

		// 定义线程数量和延迟等待时间
		int thread = 1, interval = 1000;
		GeccoEngine.create()
		// 自定义下载url调度
		.scheduler(new RedisStartScheduler(redis_host+":"+redis_port))
				// 自定义管道过滤器工厂
				.pipelineFactory(springPipelineFactory)
				// 扫描包路径
				.classpath("com.isport.crawl.tengxun")
//				.start("http://sports.qq.com/l/isocce/xijia/laliganews.htm")
				// 启动spider数量
				.thread(thread)
				// 延迟等待事件
				.interval(interval)
				// 阻塞启动
				.run(); 
		// 详情spiderBean
		DynamicGecco.html().gecco("http://sports.qq.com/a/{id1}/{id2}.htm", "txDetail")
				// 定义请求对象
				.requestField("request").request().build().stringField("content").csspath("html").build()
				// 定义标题
				.stringField("title").csspath("div.hd h1").text().build()
				// 定义发布日期
				.stringField("pubDate").csspath("span.a_time").text().build().register();

		GeccoEngine.create()
		// 自定义下载url调度
		.scheduler(new RedisStartScheduler(redis_host+":"+redis_port))
				// 自定义管道过滤器工厂
				.pipelineFactory(springPipelineFactory)
				// 扫描包路径
				.classpath("com.isport.crawl.tengxun").thread(thread)
				// 延迟等待事件
				.interval(interval).start(txList.getSortRequests())
				// 阻塞启动
				.run();
		System.out.println("TxCrawl end");
	}
}
