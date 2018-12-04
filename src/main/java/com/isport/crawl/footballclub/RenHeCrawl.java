package com.isport.crawl.footballclub;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.geccocrawler.gecco.GeccoEngine;
import com.geccocrawler.gecco.dynamic.DynamicGecco;
import com.geccocrawler.gecco.redis.RedisStartScheduler;
import com.geccocrawler.gecco.spring.SpringPipelineFactory;

@Service
public class RenHeCrawl {
	// redis 主机
	@Value("${spring.redis.host}")
	private String redis_host;
	// redis 端口
	@Value("${spring.redis.port}")
	private String redis_port;
	// 扩展工厂
	@Autowired
	SpringPipelineFactory springPipelineFactory;
	// 列表页
	@Autowired
	RenHeList renHeList;

	public void register() {
		// 新闻列表spiderBean
		DynamicGecco.json().gecco("http://api-9h.9h-sports.com/renhe/api/News/GetList", "renHeList")
				.requestField("request").request().build().field("newsList", Object.class).jsonpath("$.tag.list")
				.build().register();
		// 详情spiderBean
		DynamicGecco.html()
				.gecco(new String[] { "https://news-renhe.9h-sports.com/statics/news/{id}.html" }, "renHeDetail")
				// 请求对象
				.requestField("request").request().build()
				// 网页内容
				.stringField("content").csspath("html").build()
				// 新闻标题
				.stringField("title").csspath("section h1").text().build()
				// 发布日期
				.stringField("pubDate").csspath("p.time > span").text().build().register();

	}

	@SuppressWarnings("unchecked")
	public void crawl() {
		System.out.println("RenHeCrawl start");
		// 新闻列表spiderBean
		DynamicGecco.json().gecco("http://api-9h.9h-sports.com/renhe/api/News/GetList", "renHeList")
				.requestField("request").request().build().field("newsList", Object.class).jsonpath("$.tag.list")
				.build().register();

		// 定义线程数量和延迟等待时间
		int thread = 1, interval = 1000;
		GeccoEngine.create()
				// 自定义下载url调度
				.scheduler(new RedisStartScheduler(redis_host + ":" + redis_port))
				// 自定义管道过滤器工厂
				.pipelineFactory(springPipelineFactory)
				// 扫描包路径
				.classpath("com.isport.crawl.footballclub")
				// 启动spider数量
				.thread(thread)
				// 延迟等待事件
				.interval(interval)
				// 阻塞启动
				.run();

		// 详情spiderBean
		DynamicGecco.html()
				.gecco(new String[] { "https://news-renhe.9h-sports.com/statics/news/{id}.html" }, "renHeDetail")
				// 请求对象
				.requestField("request").request().build()
				// 网页内容
				.stringField("content").csspath("html").build()
				// 新闻标题
				.stringField("title").csspath("section h1").text().build()
				// 发布日期
				.stringField("pubDate").csspath("p.time > span").text().build().register();

		GeccoEngine.create()
				// 自定义下载url调度
				.scheduler(new RedisStartScheduler(redis_host + ":" + redis_port))
				// 自定义管道过滤器工厂
				.pipelineFactory(springPipelineFactory)
				// 扫描包路径
				.classpath("com.isport.crawl.footballclub").thread(thread)
				// 延迟等待事件
				.interval(interval).start(renHeList.getSortRequests())
				// 阻塞启动
				.run();
		System.out.println("RenHeList end");

	}
}
