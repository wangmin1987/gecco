package com.isport.crawl.sina;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.geccocrawler.gecco.GeccoEngine;
import com.geccocrawler.gecco.dynamic.DynamicGecco;
import com.geccocrawler.gecco.redis.RedisStartScheduler;
import com.geccocrawler.gecco.spring.SpringPipelineFactory;

@Service
public class SinaGBCrawl {
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
	SinaGBList sinaGBList;

	@SuppressWarnings("unchecked")
	public void crawl() {
		System.out.println("SinaGBCrawl start");
		// 新闻块spiderBean
		Class<?> newsBriefs = DynamicGecco.html().stringField("docUrl").csspath("a").attr("href").build()
				.stringField("pubDate").csspath("span.dnt").text().build().register();
		// 新闻列表spiderBean
		DynamicGecco.html().gecco(new String[] { "http://sports.sina.com.cn/global/germany/" }, "sinaGBList")
				.requestField("request").request().build().listField("newsList", newsBriefs).csspath("div.mleft li")
				.build().register();

		// 线程数量 和 延迟等待时间
		int thread = 1, interval = 1000;
		GeccoEngine.create()
				// 自定义下载url调度
				.scheduler(new RedisStartScheduler(redis_host + ":" + redis_port))
				// 自定义管道过滤器工厂
				.pipelineFactory(springPipelineFactory)
				// 扫描包路径
				.classpath("com.isport.crawl.sina")
//				.start("http://sports.sina.com.cn/global/germany/")
				// 启动spider数量
				.thread(thread)
				// 延迟等待事件
				.interval(interval)
				// 阻塞启动
				.run();

		Class<?> keyword = DynamicGecco.html().stringField("keyword").csspath("a").text().build().register();
		// 详情spiderBean
		DynamicGecco.html().gecco("http://sports.sina.com.cn/global/germany/{date}/{id}.shtml", "sinaGBDetail")
				.requestField("request").request().build().stringField("content").csspath("html").build()
				.stringField("title").csspath("h1.main-title").text().build().listField("keywords", keyword)
				.csspath("#keywords a").build().stringField("pubDate").csspath("span.date").text().build().register();

		// 调度引擎
		GeccoEngine.create()
				// 自定义下载url调度
				.scheduler(new RedisStartScheduler(redis_host + ":" + redis_port))
				// 自定义管道过滤器工厂
				.pipelineFactory(springPipelineFactory)
				// 扫描包路径
				.classpath("com.isport.crawl.sina").thread(thread)
				// 延迟等待事件
				.interval(interval).start(sinaGBList.getSortRequests())
				// 阻塞启动
				.run();
	}
}
