package com.isport.crawl.onesoccer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.geccocrawler.gecco.GeccoEngine;
import com.geccocrawler.gecco.dynamic.DynamicGecco;
import com.geccocrawler.gecco.redis.RedisStartScheduler;
import com.geccocrawler.gecco.spring.SpringPipelineFactory;

@Service
public class SoccerCrawl {
	// spring pipeline 工厂
	@Autowired
	SpringPipelineFactory springPipelineFactory;
	// redis 配置
	@Value("${spring.redis.host}")
	private String redis_host;

	@Value("${spring.redis.port}")
	private String redis_port;
	// 列表页过滤器 提供详情页的队列
	@Autowired
	SoccerList soccerList;

	public void register() {
		// 新闻块spiderBean 截取详情地址和时间
		Class<?> newsBriefs = DynamicGecco.html().stringField("docUrl").csspath("a").attr("href").build()
				.stringField("pubDate").csspath("a").text().build().register();
		// 列表页 spiderBean
		DynamicGecco.html().requestField("request").request().build()
				// 匹配地址
				.gecco(new String[] { "http://www.1soccer.com/news/section/typeid/{id}/page/{pageNum}",
						"http://www.1soccer.com/news/section/typeid/{id}" }, "soccerList")
				.listField("newsList", newsBriefs).csspath("div.news_list_box1 ul li").build().register();
		// 详情spiderBean
		DynamicGecco.html().gecco("http://www.1soccer.com/views/newstxt/{id1}/{id2}/{id3}.shtml", "soccerDetail")
				.requestField("request").request().build().stringField("content").csspath("html").build()
				// 标题
				.stringField("title").csspath("div.left_noborder h2").text().build()
				// 发布日期
				.stringField("pubDate").csspath("#content_dec").text().build().register();

	}

	@SuppressWarnings("unchecked")
	public void crawl() {
		System.out.println("SoccerCrawl start");
		// 新闻块spiderBean 截取详情地址和时间
		Class<?> newsBriefs = DynamicGecco.html().stringField("docUrl").csspath("a").attr("href").build()
				.stringField("pubDate").csspath("a").text().build().register();
		// 列表页 spiderBean
		DynamicGecco.html().requestField("request").request().build()
				// 匹配地址
				.gecco(new String[] { "http://www.1soccer.com/news/section/typeid/{id}/page/{pageNum}",
						"http://www.1soccer.com/news/section/typeid/{id}" }, "soccerList")
				.listField("newsList", newsBriefs).csspath("div.news_list_box1 ul li").build().register();

		// 线程数量 和 延迟等待时间
		int thread = 1, interval = 1000;
		GeccoEngine.create()
				// 自定义下载url调度
				.scheduler(new RedisStartScheduler(redis_host + ":" + redis_port))
				// 自定义管道过滤器工厂
				.pipelineFactory(springPipelineFactory)
				// 扫描包路径
				.classpath("com.isport.crawl.onesoccer")
//				.start("http://www.1soccer.com/news/section/typeid/111/page/1")
				// 启动spider数量
				.thread(thread)
				// 延迟等待事件
				.interval(interval)
				// 阻塞启动
				.run();

		// 详情spiderBean
		DynamicGecco.html().gecco("http://www.1soccer.com/views/newstxt/{id1}/{id2}/{id3}.shtml", "soccerDetail")
				.requestField("request").request().build().stringField("content").csspath("html").build()
				// 标题
				.stringField("title").csspath("div.left_noborder h2").text().build()
				// 发布日期
				.stringField("pubDate").csspath("#content_dec").text().build().register();

		GeccoEngine.create()
				// 自定义下载url调度
				.scheduler(new RedisStartScheduler(redis_host + ":" + redis_port))
				// 自定义管道过滤器工厂
				.pipelineFactory(springPipelineFactory)
				// 扫描包路径
				.classpath("com.isport.crawl.onesoccer").thread(thread)
				// 延迟等待事件
				.interval(interval).start(soccerList.getSortRequests())
				// 阻塞启动
				.run();
		System.out.println("SoccerCrawl end");
	}
}
