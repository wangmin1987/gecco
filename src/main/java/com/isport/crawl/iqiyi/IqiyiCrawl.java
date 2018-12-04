package com.isport.crawl.iqiyi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.geccocrawler.gecco.GeccoEngine;
import com.geccocrawler.gecco.dynamic.DynamicGecco;
import com.geccocrawler.gecco.redis.RedisStartScheduler;
import com.geccocrawler.gecco.spring.SpringPipelineFactory;

@Service
public class IqiyiCrawl {
	// redis 主机
	@Value("${spring.redis.host}")
	private String redis_host;
	// redis 端口
	@Value("${spring.redis.port}")
	private String redis_port;

	public void register() {
		Class<?> lisBriefs = DynamicGecco.html().stringField("docUrl").csspath("a").attr("href").build()
				.stringField("pubDate").csspath("a > span").text().build().register();
		Class<?> newsBriefs = DynamicGecco.html().listField("ulList", lisBriefs).csspath("li").build().register();
		DynamicGecco.html().gecco(new String[] {"http://www.ssports.com/news/info{detail}.html"}
				, "iqiyiListPipeline")
				.requestField("request").request().build().listField("newsList", newsBriefs).csspath("div.new_l_r ul")
				.build().register();

		Class<?> keywords = DynamicGecco.html().stringField("keyword").csspath("a").text().build().register();
		DynamicGecco.html().gecco(new String[] {"http://www.ssports.com/news/0{index}.html"
				,"http://www.ssports.com/news/1{index}.html","http://www.ssports.com/news/2{index}.html"
				,"http://www.ssports.com/news/3{index}.html","http://www.ssports.com/news/4{index}.html"
				,"http://www.ssports.com/news/5{index}.html","http://www.ssports.com/news/6{index}.html"
				,"http://www.ssports.com/news/7{index}.html","http://www.ssports.com/news/8{index}.html"
				,"http://www.ssports.com/news/9{index}.html"
		}, "iqiyiDetail").requestField("request")
				.request().build().stringField("content").csspath("html").build().stringField("title")
				.csspath("div.new_l_yt > h2").text().build().listField("keywords", keywords).csspath("div.new_g span a")
				.build().stringField("pubDate").csspath("div.new_l_yt > span > i").text().build().register();
	}

	// 扩展工厂
	@Autowired
	SpringPipelineFactory springPipelineFactory;

	@SuppressWarnings("unchecked")
	public void crawl() {
		System.out.println("IqiyiCrawl start");
		Class<?> lisBriefs = DynamicGecco.html().stringField("docUrl").csspath("a").attr("href").build()
				.stringField("pubDate").csspath("a > span").text().build().register();
		Class<?> newsBriefs = DynamicGecco.html().listField("ulList", lisBriefs).csspath("li").build().register();
		DynamicGecco.html().gecco("http://www.ssports.com/news/{team}.html", "iqiyiListPipeline")
				.requestField("request").request().build().listField("newsList", newsBriefs).csspath("div.new_l_r ul")
				.build().register();
		// 线程数 延迟时间
		int thread = 1, interval = 1000;
		GeccoEngine.create()
				// 自定义下载url调度
				.scheduler(new RedisStartScheduler(redis_host + ":" + redis_port))
				// 自定义管道过滤器工厂
				.pipelineFactory(springPipelineFactory)
				// 扫描包路径
				.classpath("com.isport.crawl.IqiyiCrawl")
				// 设置初始地址
//				.start("http://www.ssports.com/news/info%E6%9B%BC%E8%81%94_1.html")
				// 启动spider数量
				.thread(thread)
				// 延迟等待事件
				.interval(interval)
				// 阻塞启动
				.run();

		Class<?> keywords = DynamicGecco.html().stringField("keyword").csspath("a").text().build().register();
		DynamicGecco.html().gecco("http://www.ssports.com/news/{id}.html", "iqiyiDetail").requestField("request")
				.request().build().stringField("content").csspath("html").build().stringField("title")
				.csspath("div.new_l_yt > h2").text().build().listField("keywords", keywords).csspath("div.new_g span a")
				.build().stringField("pubDate").csspath("div.new_l_yt > span > i").text().build().register();

		GeccoEngine.create()
				// 自定义下载url调度
				.scheduler(new RedisStartScheduler(redis_host + ":" + redis_port))
				// 自定义管道过滤器工厂
				.pipelineFactory(springPipelineFactory)
				// 扫描包路径
				.classpath("com.isport.crawl.IqiyiCrawl").thread(thread)
				// 延迟等待事件
				.interval(interval).start(IqiyiListPipeline.sortRequests).run();
		System.out.println("IqiyiCrawl end");
	}
}
