package com.isport.crawl.goal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.geccocrawler.gecco.GeccoEngine;
import com.geccocrawler.gecco.dynamic.DynamicGecco;
import com.geccocrawler.gecco.redis.RedisStartScheduler;
import com.geccocrawler.gecco.spring.SpringPipelineFactory;

@Service
public class GoalCrawl {
	// redis 主机
	@Value("${spring.redis.host}")
	private String redis_host;
	// redis 端口
	@Value("${spring.redis.port}")
	private String redis_port;

	// 扩展工厂
	@Autowired
	SpringPipelineFactory springPipelineFactory;
	
	public void register() {
		System.out.print("GoalCrawl start");
		// 新闻块spiderBean
		Class<?> newsBriefs = DynamicGecco.html().stringField("docUrl").csspath("a").attr("href").build()
				.stringField("pubDate").csspath("footer > time").attr("data-utc").build().register();
		// 新闻列表spiderBean
		DynamicGecco.html().gecco("https://www.goal.com/zh-cn/{team}/{team}/{page}/{chars}", "goalListPipeline")
				.requestField("request").request().build().listField("newsList", newsBriefs).csspath("div.type-article")
				.build().stringField("nextUrl").csspath("footer.btn-group > a.btn.btn--older").attr("href").build()
				.register();
		
		Class<?> keyword = DynamicGecco.html().stringField("keyword").csspath("a").text().build().register();
		// 详情spiderBean
		DynamicGecco.html().gecco("https://www.goal.com/zh-cn/{new}/{title}/{chars}", "goalDetail")
				.requestField("request").request().build().stringField("content").csspath("html").build()
				.stringField("title").csspath("div.article-header > header > h1").text().build()
				.listField("keywords", keyword).csspath("ul.tags-list__list > li").build().stringField("pubDate")
				.csspath("time.actions-bar__time > span").attr("data-utc").build().register();
	}

	@SuppressWarnings("unchecked")
	public void crawl() {
		System.out.print("GoalCrawl start");
		// 新闻块spiderBean
		Class<?> newsBriefs = DynamicGecco.html().stringField("docUrl").csspath("a").attr("href").build()
				.stringField("pubDate").csspath("footer > time").attr("data-utc").build().register();
		// 新闻列表spiderBean
		DynamicGecco.html().gecco("https://www.goal.com/zh-cn/{team}/{team}/{page}/{chars}", "goalListPipeline")
				.requestField("request").request().build().listField("newsList", newsBriefs).csspath("div.type-article")
				.build().stringField("nextUrl").csspath("footer.btn-group > a.btn.btn--older").attr("href").build()
				.register();

		int thread = 1, interval = 1000;
		GeccoEngine.create()
				// 自定义下载url调度
				.scheduler(new RedisStartScheduler(redis_host + ":" + redis_port))
				// 自定义管道过滤器工厂
				.pipelineFactory(springPipelineFactory)
				// 扫描包路径
				.classpath("com.isport.crawl.goal")
//				.start("http://www.goal.com/zh-cn/%E7%90%83%E9%98%9F/%E5%B7%B4%E5%A1%9E%E7%BD%97%E9%82%A3/1/agh9ifb2mw3ivjusgedj7c3fe")
				// 启动spider数量
				.thread(thread)
				// 延迟等待事件
				.interval(interval)
				// 阻塞启动
				.run();

		Class<?> keyword = DynamicGecco.html().stringField("keyword").csspath("a").text().build().register();
		// 详情spiderBean
		DynamicGecco.html().gecco("https://www.goal.com/zh-cn/{new}/{title}/{chars}", "goalDetail")
				.requestField("request").request().build().stringField("content").csspath("html").build()
				.stringField("title").csspath("div.article-header > header > h1").text().build()
				.listField("keywords", keyword).csspath("ul.tags-list__list > li").build().stringField("pubDate")
				.csspath("time.actions-bar__time > span").attr("data-utc").build().register();

		GeccoEngine.create()
				// 自定义下载url调度
				.scheduler(new RedisStartScheduler(redis_host + ":" + redis_port))
				// 自定义管道过滤器工厂
				.pipelineFactory(springPipelineFactory)
				// 扫描包路径
				.classpath("com.isport.crawl.goal").thread(thread)
				// 延迟等待事件
				.interval(interval).start(GoalListPipeline.sortRequests)
				// 阻塞启动
				.run();
		System.out.println("GoalCrawl end");
	}
}
