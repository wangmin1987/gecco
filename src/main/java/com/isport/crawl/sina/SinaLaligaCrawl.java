package com.isport.crawl.sina;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.geccocrawler.gecco.GeccoEngine;
import com.geccocrawler.gecco.dynamic.DynamicGecco;
import com.geccocrawler.gecco.redis.RedisStartScheduler;
import com.geccocrawler.gecco.spring.SpringPipelineFactory;

@Service
public class SinaLaligaCrawl {
	// spring pipeline 工厂
	@Autowired
	SpringPipelineFactory springPipelineFactory;
	// redis 配置
	@Value("${spring.redis.host}")
	private String redis_host;

	@Value("${spring.redis.port}")
	private String redis_port;

	@Autowired
	private SinaLaligaList sinaLaligaList;

	@SuppressWarnings("unchecked")
	public void crawl() {
		System.out.println("SinaLaligaCrawl start");
		// 列表页 spiderBean
		DynamicGecco.html().requestField("request").request().build()
				// 匹配地址
				.gecco(new String[] {
						"http://feed.mix.sina.com.cn/api/roll/get?pageid={pageid}&lid={lid}&num=30&versionNumber=1.2.4&page={page}&encode=utf-8&callback=feedCardJsonpCallback" },
						"sinaLaligaList")
				.field("newsList", String.class).csspath("body").build().register();

		// 定义线程数量和延迟等待时间
		int thread = 1, interval = 1000;
		GeccoEngine.create()
				// 自定义下载url调度
				.scheduler(new RedisStartScheduler(redis_host + ":" + redis_port))
				// 自定义管道过滤器工厂
				.pipelineFactory(springPipelineFactory)
				// 扫描包路径
				.classpath("com.isport.crawl.sina")
//				.start("http://feed.mix.sina.com.cn/api/roll/get?pageid=44&lid=470&num=30&versionNumber=1.2.4&page=1&encode=utf-8&callback=feedCardJsonpCallback")
				// 启动spider数量
				.thread(thread)
				// 延迟等待事件
				.interval(interval)
				// 阻塞启动
				.run();

		// 关键词对象
		Class<?> keyword = DynamicGecco.html().stringField("keyword").csspath("a").text().build().register();
		// 详情spiderBean
		DynamicGecco.html()
				.gecco(new String[] { "http://sports.sina.com.cn/g/{team}/{datetime}/{docId}.shtml",
						"http://sports.sina.com.cn/global/france/{datetime}/{docId}.shtml",
						"http://sports.sina.com.cn/g/pl/{datetime}/{docId}.shtml" }, "sinaLaligaDetail")
				// 请求对象
				.requestField("request").request().build()
				// 网页内容
				.stringField("content").csspath("html").build()
				// 新闻标题
				.stringField("title").csspath("h1.main-title").text().build()
				// 新闻关键词
				.listField("keywords", keyword).csspath("#keywords a").build()
				// 发布日期
				.stringField("pubDate").csspath("span.date").text().build().register();

		GeccoEngine.create()
				// 自定义下载url调度
				.scheduler(new RedisStartScheduler(redis_host + ":" + redis_port))
				// 自定义管道过滤器工厂
				.pipelineFactory(springPipelineFactory)
				// 扫描包路径
				.classpath("com.isport.crawl.sina").thread(thread)
				// 延迟等待事件
				.interval(interval).start(sinaLaligaList.getSortRequests())
				// 阻塞启动
				.run();
		System.out.println("SinaLaligaCrawl end");
	}
}
