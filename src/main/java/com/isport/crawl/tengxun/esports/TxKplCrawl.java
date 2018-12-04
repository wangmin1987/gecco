package com.isport.crawl.tengxun.esports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.geccocrawler.gecco.GeccoEngine;
import com.geccocrawler.gecco.dynamic.DynamicGecco;
import com.geccocrawler.gecco.spring.SpringPipelineFactory;

/**
 * 王者荣耀
 * 
 * @author 八斗体育
 *
 */
@Service
public class TxKplCrawl {
	// spring 工厂
	@Autowired
	SpringPipelineFactory springPipelineFactory;
	// redis 主机
	@Value("${spring.redis.host}")
	private String redis_host;
	// redis 端口
	@Value("${spring.redis.port}")
	private String redis_port;

	@Autowired
	private TxKplList txKplList;

	public void register() {
		// 新闻块spiderBean
		Class<?> newsBriefs = DynamicGecco.html()
				// 详情页地址
				.stringField("docUrl").csspath("a").attr("href").build()
				// 发布日期
				.stringField("pubDate").csspath("span.tag_data").text().build().register();
		// 列表页spiderBean
		DynamicGecco.html()
				.gecco(new String[] {
						"http://pvp.qq.com/webplat/info/news_version3/15592/22661/22664/25563/m14593/list_{page}.shtml",
						"http://pvp.qq.com/webplat/info/news_version3/15592/22661/22664/m14593/list_{page}.shtml" },
						"txKplList")
				// 请求对象
				.requestField("request").request().build()
				// 新闻列表
				.listField("newsList", newsBriefs).csspath("ul.newspage_list_ul li").build().register();

		// 详情spiderBean
		DynamicGecco.html().gecco(new String[] {
				"http://pvp.qq.com/webplat/info/news_version3/{id1}/{id2}/{id3}/{id4}/{id5}/{id6}/{id7}.shtml",
				"http://pvp.qq.com/webplat/info/news_version3/{id1}/{id2}/{id3}/{id4}/{id5}/{id6}/{id7}/{id8}.shtml" },
				"txKplDetail")
				// 请求对象
				.requestField("request").request().build()
				// 网页内容
				.stringField("content").csspath("html").build()
				// 新闻标题
				.stringField("title").csspath("div.article_title h3").text().build().register();

	}

	@SuppressWarnings("unchecked")
	public void crawl() {
		System.out.println("TxKplCrawl start");
		// 新闻块spiderBean
		Class<?> newsBriefs = DynamicGecco.html()
				// 详情页地址
				.stringField("docUrl").csspath("a").attr("href").build()
				// 发布日期
				.stringField("pubDate").csspath("span.tag_data").text().build().register();
		// 列表页spiderBean
		DynamicGecco.html()
				.gecco(new String[] {
						"http://pvp.qq.com/webplat/info/news_version3/15592/22661/22664/25563/m14593/list_{page}.shtml",
						"http://pvp.qq.com/webplat/info/news_version3/15592/22661/22664/m14593/list_{page}.shtml" },
						"txKplList")
				// 请求对象
				.requestField("request").request().build()
				// 新闻列表
				.listField("newsList", newsBriefs).csspath("ul.newspage_list_ul li").build().register();

		// 定义线程数量和延迟等待时间
		int thread = 1, interval = 1000;
		GeccoEngine.create()
		// 自定义下载url调度
//								.scheduler(new RedisStartScheduler(redis_host + ":" + redis_port))
				// 自定义管道过滤器工厂
				.pipelineFactory(springPipelineFactory)
				// 扫描包路径
				.classpath("com.isport.crawl.tengxun.esports")
//						.start("http://nba2k2.qq.com/webplat/info/news_version3/30714/33557/m19596/list_1.shtml")
				// 启动spider数量
				.thread(thread)
				// 延迟等待事件
				.interval(interval)
				// 阻塞启动
				.run();

		// 详情spiderBean
		DynamicGecco.html().gecco(new String[] {
				"http://pvp.qq.com/webplat/info/news_version3/{id1}/{id2}/{id3}/{id4}/{id5}/{id6}/{id7}.shtml",
				"http://pvp.qq.com/webplat/info/news_version3/{id1}/{id2}/{id3}/{id4}/{id5}/{id6}/{id7}/{id8}.shtml" },
				"txKplDetail")
				// 请求对象
				.requestField("request").request().build()
				// 网页内容
				.stringField("content").csspath("html").build()
				// 新闻标题
				.stringField("title").csspath("div.article_title h3").text().build().register();

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
				.start(txKplList.getSortRequests())
				// 阻塞启动
				.run();
		System.out.println("TxKplCrawl end");
	}
}
