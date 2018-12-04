package com.isport.crawl.zhibo8;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.geccocrawler.gecco.GeccoEngine;
import com.geccocrawler.gecco.dynamic.DynamicGecco;
import com.geccocrawler.gecco.redis.RedisStartScheduler;
import com.geccocrawler.gecco.spring.SpringPipelineFactory;

@Service
@SuppressWarnings("all")
public class Zhibo8Crawl {

	@Value("${spring.redis.host}")
	private String redis_host;

	@Value("${spring.redis.port}")
	private String redis_port;

	@Autowired
	SpringPipelineFactory springPipelineFactory;

	public void register() {
		// 列表页爬取
				Class<?> newsBrief = DynamicGecco.html()
						.stringField("docurl").csspath(".articleTitle > a").attr("href").build()
						.stringField("pubdate").csspath(".postTime").text().build()
						.register();
				DynamicGecco.html()
				.gecco(new String[] {
						"https://news.zhibo8.cc/nba/more.htm",
						"https://news.zhibo8.cc/zuqiu/more.htm",
						"https://news.zhibo8.cc/zuqiu/more.htm?label={team}"
						}, "zhibo8HtmlListDynamicPipeline")
				.requestField("request").request().build()
				.listField("newslist", newsBrief).csspath(".dataList > ul > li").build()
				.stringField("page").requestParameter().build()
				.register();
				// 详情页爬取
				DynamicGecco.html()
				.gecco(new String[] {
						"https://news.zhibo8.cc/nba/{date}/{filename}.htm",
						"https://news.zhibo8.cc/zuqiu/{date}/{filename}.htm"
						}, "zhibo8DetailPipeline")
				.requestField("request").request().build()
				.stringField("content").csspath("html").build()
				.stringField("title").csspath("div.title h1").text().build()
				.stringField("keywords").csspath("meta[name=\"keywords\"]").attr("content").build()
				.register();
		
	}
//	@Async
//	@Scheduled(fixedDelay = 1000 * 60 * 10)
	public void run() {
		System.out.println("Zhibo8Crawl start");
		// 列表页爬取
		Class<?> newsBrief = DynamicGecco.html()
				.stringField("docurl").csspath(".articleTitle > a").attr("href").build()
				.stringField("pubdate").csspath(".postTime").text().build()
				.register();
		
		DynamicGecco.html()
		.gecco(new String[] {
				"https://news.zhibo8.cc/nba/more.htm",
				"https://news.zhibo8.cc/zuqiu/more.htm",
				"https://news.zhibo8.cc/zuqiu/more.htm?label={team}"
				}, "zhibo8HtmlListDynamicPipeline")
		.requestField("request").request().build()
		.listField("newslist", newsBrief).csspath(".dataList > ul > li").build()
		.stringField("page").requestParameter().build()
		.register();

		// 详情页爬取
				DynamicGecco.html()
				.gecco(new String[] {
						"https://news.zhibo8.cc/nba/{date}/{filename}.htm",
						"https://news.zhibo8.cc/zuqiu/{date}/{filename}.htm"
						}, "zhibo8DetailPipeline")
				.requestField("request").request().build()
				.stringField("content").csspath("html").build()
				.stringField("title").csspath("#main .title > h1").text().build()
				.stringField("keywords").csspath("meta[name=\"keywords\"]").attr("content").build()
				.register();
		
		GeccoEngine.create()
		.scheduler(new RedisStartScheduler(redis_host + ":" + redis_port))
		.pipelineFactory(springPipelineFactory)
		.classpath("com.isport.crawl.zhibo8") 
//		.start("https://news.zhibo8.cc/zuqiu/more.htm?label=%E8%A5%BF%E7%94%B2")
		.thread(2)
		.interval(2000)
//		.start();
		.run();
		
		System.out.println("Zhibo8Crawl end");
		
//		GeccoEngine.create()
//		.scheduler(new RedisStartScheduler(redis_host + ":" + redis_port))
//		.pipelineFactory(springPipelineFactory)
//		.classpath("com.isport.crawl.zhibo8")
//		.start(Zhibo8HtmlListDynamicPipeline.sortRequests)
//		.run();
	}

}
