/**
 * 
 */
package com.isport.crawl.dongqiudi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.geccocrawler.gecco.GeccoEngine;
import com.geccocrawler.gecco.dynamic.DynamicGecco;
import com.geccocrawler.gecco.redis.RedisStartScheduler;
import com.geccocrawler.gecco.spring.SpringPipelineFactory;

/**
 * @author 八斗体育
 *
 */
@Service
public class DongQiuDiGBCrawl {
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
	DongQiuDiGBList dongQiuDiGBList;
	
	@SuppressWarnings("unchecked")
	public void crawl() {
		System.out.println("DongQiuDiGBCrawl start");
		// 新闻块spiderBean
		Class<?> newsBriefs = DynamicGecco.html().stringField("docUrl").csspath("a").attr("href").build()
				.stringField("pubDate").csspath("div.info > span").text().build().register();
		// 新闻列表spiderBean
		DynamicGecco.html().gecco(new String[] { "https://www.dongqiudi.com/?tab={tab}&page={page}" }, "dongQiuDiGBList")
				.requestField("request").request().build().listField("newsList", newsBriefs)
				.csspath("#news_list > ol > li").build().register();

		int thread = 1, interval = 1000;
		GeccoEngine.create()
		// 自定义下载url调度
				.scheduler(new RedisStartScheduler(redis_host + ":" + redis_port))
				// 自定义管道过滤器工厂
				.pipelineFactory(springPipelineFactory)
				// 扫描包路径
				.classpath("com.isport.crawl.dongqiudi")
//				.start("https://www.dongqiudi.com/?tab=6&page=1")
				// 启动spider数量
				.thread(thread)
				// 延迟等待事件
				.interval(interval)
				// 阻塞启动
				.run();

		Class<?> keywordsBriefs = DynamicGecco.html().stringField("keyword").csspath("span").text().build()
				.register();
		// 详情页 spiderBean
		DynamicGecco.html()
				.gecco(new String[] { "https://www.dongqiudi.com/archive/{id}.html",
						"http://www.dongqiudi.com/archive/{id}.html" }, "dongQiuDiFADetail")
				.requestField("request").request().build().stringField("content").csspath("html").build()
				.stringField("title").csspath("div.detail > h1").build().stringField("pubDate")
				.csspath("div.detail > h4 span.time").build().listField("keywords", keywordsBriefs)
				.csspath("div.lists a").build().register();

		GeccoEngine.create()
				.scheduler(new RedisStartScheduler(redis_host + ":" + redis_port))
				.pipelineFactory(springPipelineFactory).classpath("com.isport.crawl.dongqiudi").start(dongQiuDiGBList.getSortRequests()).thread(thread)
				.interval(interval).run();
		System.out.println("DongQiuDiGBCrawl end");
	}
}
