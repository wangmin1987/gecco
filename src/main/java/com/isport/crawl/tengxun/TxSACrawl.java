package com.isport.crawl.tengxun;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.geccocrawler.gecco.GeccoEngine;
import com.geccocrawler.gecco.dynamic.DynamicGecco;
import com.geccocrawler.gecco.redis.RedisStartScheduler;
import com.geccocrawler.gecco.spring.SpringPipelineFactory;

@Service
public class TxSACrawl {
	// spring pipeline 工厂
	@Autowired
	SpringPipelineFactory springPipelineFactory;
	// redis 配置
	@Value("${spring.redis.host}")
	private String redis_host;

	@Value("${spring.redis.port}")
	private String redis_port;
	
	@Autowired
	TxSAList txSAList;

	@SuppressWarnings("unchecked")
	public void crawl() {
		System.out.println("TxSACrawl start");
		// 新闻块spiderBean
		Class<?> newsBriefs = DynamicGecco.html().stringField("docUrl").csspath("div.txt a").attr("href").build()
				.stringField("pubDate").csspath("div.toolbar > span").text().build().register();
		// 列表页 spiderBean
		DynamicGecco.html().requestField("request").request().build()
				// 匹配地址
				.gecco(new String[] { "http://sports.qq.com/c/yijia2016_toutiao_{page}.htm" }, "txSAList")
				.listField("newsList", newsBriefs).csspath("div.ptlist").build().register();

		// 定义线程数量和延迟等待时间
		int thread = 1, interval = 1000;
		GeccoEngine.create()
		// 自定义下载url调度
							.scheduler(new RedisStartScheduler(redis_host + ":" + redis_port))
				// 自定义管道过滤器工厂
				.pipelineFactory(springPipelineFactory)
				// 扫描包路径
				.classpath("com.isport.crawl.tengxun")
//				.start("http://sports.qq.com/c/yijia2016_toutiao_1.htm")
				// 启动spider数量
				.thread(thread)
				// 延迟等待事件
				.interval(interval)
				// 阻塞启动
				.run();
		
		// 关键词对象
		Class<?> keyword = DynamicGecco.html().stringField("keyword").csspath("a").text().build().register();
		// 详情spiderBean
		DynamicGecco.html().gecco(new String[] { "http://sports.qq.com/a/{date}/{id}.htm" }, "txSADetail")
				// 请求对象
				.requestField("request").request().build()
				// 网页内容
				.stringField("content").csspath("html").build()
				// 新闻标题
				.stringField("title").csspath("div.qq_article h1").text().build()
				// 新闻关键词
				.listField("keywords", keyword).csspath("#videokg span").build()
				// 发布日期
				.stringField("pubDate").csspath("span.a_time").text().build().register();

		GeccoEngine.create()
		// 自定义下载url调度
		.scheduler(new RedisStartScheduler(redis_host + ":" + redis_port))
				// 自定义管道过滤器工厂
				.pipelineFactory(springPipelineFactory)
				// 扫描包路径
				.classpath("com.isport.crawl.tengxun").thread(thread)
				// 延迟等待事件
				.interval(interval).start(txSAList.getSortRequests())
				// 阻塞启动
				.run();
		System.out.println("TxSACrawl end");
	}
}
