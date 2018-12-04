package com.isport.crawl.tengxun.esports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.geccocrawler.gecco.GeccoEngine;
import com.geccocrawler.gecco.dynamic.DynamicGecco;
import com.geccocrawler.gecco.redis.RedisStartScheduler;
import com.geccocrawler.gecco.spring.SpringPipelineFactory;

@Service
public class Tx2k2Crawl {
	@Autowired
	SpringPipelineFactory springPipelineFactory;
	// redis 主机
	@Value("${spring.redis.host}")
	private String redis_host;
	// redis 端口
	@Value("${spring.redis.port}")
	private String redis_port;

	@Autowired
	private Tx2k2List tx2k2List;

	public void register() {
		// 新闻块spiderBean
		Class<?> newsBriefs = DynamicGecco.html().stringField("docUrl").csspath("a + a").attr("href").build()
				.stringField("pubDate").csspath("span").text().build().register();
		// 列表页spiderBean
		DynamicGecco.html()
				.gecco(new String[] {
						"http://nba2k2.qq.com/webplat/info/news_version3/30714/33557/m19596/list_{page}.shtml" },
						"tx2k2List")
				// 请求对象
				.requestField("request").request().build().listField("newsList", newsBriefs).csspath("ul.news_list li")
				.build().register();

		// 详情spiderBean
		DynamicGecco.html().gecco(new String[] {
				"http://nba2k2.qq.com/webplat/info/news_version3/{num1}/{num2}/{num3}/{id1}/{id2}/{id3}.shtml" },
				"tx2k2Detail")
				// 请求对象
				.requestField("request").request().build()
				// 网页内容
				.stringField("content").csspath("html").build()
				// 新闻标题
				.stringField("title").csspath("div.box_news_detail h3").text().build()
				// 发布日期
				.stringField("pubDate").csspath("div.data span.date").text().build().register();

	}

	@SuppressWarnings("unchecked")
	public void crawl() {
		System.out.println("Tx2K2Crawl start");
		// 新闻块spiderBean
		Class<?> newsBriefs = DynamicGecco.html().stringField("docUrl").csspath("a + a").attr("href").build()
				.stringField("pubDate").csspath("span").text().build().register();
		// 列表页spiderBean
		DynamicGecco.html()
				.gecco(new String[] {
						"http://nba2k2.qq.com/webplat/info/news_version3/30714/33557/m19596/list_{page}.shtml" },
						"tx2k2List")
				// 请求对象
				.requestField("request").request().build().listField("newsList", newsBriefs).csspath("ul.news_list li")
				.build().register();

		// 定义线程数量和延迟等待时间
		int thread = 1, interval = 1000;
		GeccoEngine.create()
				// 自定义下载url调度
				.scheduler(new RedisStartScheduler(redis_host + ":" + redis_port))
				// 自定义管道过滤器工厂
				.pipelineFactory(springPipelineFactory)
				// 扫描包路径
				.classpath("com.isport.crawl.tengxun.esports")
//				.start("http://nba2k2.qq.com/webplat/info/news_version3/30714/33557/m19596/list_1.shtml")
				// 启动spider数量
				.thread(thread)
				// 延迟等待事件
				.interval(interval)
				// 阻塞启动
				.run();

		// 详情spiderBean
		DynamicGecco.html().gecco(new String[] {
				"http://nba2k2.qq.com/webplat/info/news_version3/{num1}/{num2}/{num3}/{id1}/{id2}/{id3}.shtml" },
				"tx2k2Detail")
				// 请求对象
				.requestField("request").request().build()
				// 网页内容
				.stringField("content").csspath("html").build()
				// 新闻标题
				.stringField("title").csspath("div.box_news_detail h3").text().build()
				// 发布日期
				.stringField("pubDate").csspath("div.data span.date").text().build().register();

		GeccoEngine.create()
				// 自定义下载url调度
				.scheduler(new RedisStartScheduler(redis_host + ":" + redis_port))
				// 自定义管道过滤器工厂
				.pipelineFactory(springPipelineFactory)
				// 扫描包路径
				.classpath("com.isport.crawl.tengxun.esports").thread(thread)
				// 延迟等待事件
				.interval(interval).start(tx2k2List.getSortRequests())
				// 阻塞启动
				.run();
		System.out.println("Tx2k2Crawl end");

	}
}
