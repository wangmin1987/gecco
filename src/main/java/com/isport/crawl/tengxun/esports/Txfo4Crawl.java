package com.isport.crawl.tengxun.esports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.geccocrawler.gecco.GeccoEngine;
import com.geccocrawler.gecco.dynamic.DynamicGecco;
import com.geccocrawler.gecco.spring.SpringPipelineFactory;

/**
 * FIFAOL4官网
 * 
 * @author 八斗体育
 *
 */
@Service
public class Txfo4Crawl {
	@Autowired
	SpringPipelineFactory springPipelineFactory;
	// redis 主机
	@Value("${spring.redis.host}")
	private String redis_host;
	// redis 端口
	@Value("${spring.redis.port}")
	private String redis_port;

	@Autowired
	Txfo4List txfo4List;

	public void register() {
		// 新闻块spiderBean
		Class<?> newsBriefs = DynamicGecco.html()
				// 详情页地址
				.stringField("docUrl").csspath("a").attr("href").build()
				// 发布日期
				.stringField("pubDate").csspath("div.updatetime").text().build().register();
		// 列表页spiderBean
		DynamicGecco.html()
				.gecco(new String[] {
						"http://fo4.qq.com/webplat/info/news_version3/33965/34617/34618/m20100/list_{page}.shtml" },
						"txfo4List")
				// 请求对象
				.requestField("request").request().build()
				// 新闻列表
				.listField("newsList", newsBriefs).csspath("div.news-box ul li").build().register();

		// 详情spiderBean
		DynamicGecco.html().gecco(new String[] {
				"http://fo4.qq.com/webplat/info/news_version3/{id1}/{id2}/{id3}/{id4}/{id5}/{id6}/{id7}.shtml" },
				"txfo4Detail")
				// 请求对象
				.requestField("request").request().build()
				// 网页内容
				.stringField("content").csspath("html").build()
				// 新闻标题
				.stringField("title").csspath("div.newscon h2").text().build()
				// 发布日期
				.stringField("pubDate").csspath("div.newscon h3 span").text().build().register();

	}

	@SuppressWarnings("unchecked")
	public void crawl() {
		System.out.println("Txfo4Crawl start");
		// 新闻块spiderBean
		Class<?> newsBriefs = DynamicGecco.html()
				// 详情页地址
				.stringField("docUrl").csspath("a").attr("href").build()
				// 发布日期
				.stringField("pubDate").csspath("div.updatetime").text().build().register();
		// 列表页spiderBean
		DynamicGecco.html()
				.gecco(new String[] {
						"http://fo4.qq.com/webplat/info/news_version3/33965/34617/34618/m20100/list_{page}.shtml" },
						"txfo4List")
				// 请求对象
				.requestField("request").request().build()
				// 新闻列表
				.listField("newsList", newsBriefs).csspath("div.news-box ul li").build().register();

		// 定义线程数量和延迟等待时间
		int thread = 1, interval = 1000;
		GeccoEngine.create()
		// 自定义下载url调度
//						.scheduler(new RedisStartScheduler(redis_host + ":" + redis_port))
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
				"http://fo4.qq.com/webplat/info/news_version3/{id1}/{id2}/{id3}/{id4}/{id5}/{id6}/{id7}.shtml" },
				"txfo4Detail")
				// 请求对象
				.requestField("request").request().build()
				// 网页内容
				.stringField("content").csspath("html").build()
				// 新闻标题
				.stringField("title").csspath("div.newscon h2").text().build()
				// 发布日期
				.stringField("pubDate").csspath("div.newscon h3 span").text().build().register();

		GeccoEngine.create()
		// 自定义下载url调度
//				.scheduler(new RedisStartScheduler(redis_host + ":" + redis_port))
				// 自定义管道过滤器工厂
				.pipelineFactory(springPipelineFactory)
				// 扫描包路径
				.classpath("com.isport.crawl.tengxun.esports").thread(thread)
				// 延迟等待事件
				.interval(interval)
				// 列表过滤器中取出详情抓去
				.start(txfo4List.getSortRequests())
				// 阻塞启动
				.run();
		System.out.println("Txfo4Crawl end");
	}
}
