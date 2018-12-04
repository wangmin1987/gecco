package com.isport.crawl.hupu;

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
public class HupuCrawl {

	@Value("${spring.redis.host}")
	private String redis_host;

	@Value("${spring.redis.port}")
	private String redis_port;

	@Autowired
	SpringPipelineFactory springPipelineFactory;

//	@Async
//	@Scheduled(fixedDelay = 1000 * 60 * 30)
	public void register() {
		Class<?> newsBrief = DynamicGecco.html()
				.stringField("docurl").csspath(".list-hd > h4 > a").attr("href").build()
				.stringField("pubdate").csspath(".time").attr("title").build()
				.register();
		
		DynamicGecco.html()
		.gecco(new String[] {
				"https://voice.hupu.com/nba/{page}?",
				"https://voice.hupu.com/cba/{page}?" },
				"hupuJsonListDynamicPipeline")
		.requestField("request").request().build()
		.listField("newslist", newsBrief).csspath(".news-list li").build()
		.stringField("page").requestParameter().build()
		.register();
		
		DynamicGecco.html()
		.gecco(new String[] { "https://voice.hupu.com/nba/{filename}.html",
				"https://voice.hupu.com/cba/{filename}.html" },
				"hupuDetailPipeline")
		.requestField("request").request().build()
		.stringField("content").csspath("html").build()
		.stringField("title").csspath(".artical-title > h1").text().build()
		.stringField("keywords").csspath("meta[http-equiv=\"Keywords\"]").attr("content").build()
		.stringField("pubdate").csspath("meta[name=\"weibo:webpage:create_at\"]").attr("content").build()
		.register();
		
		//开始抓取
//		GeccoEngine.create()
//		.scheduler(new RedisStartScheduler(redis_host + ":" + redis_port))
//		.pipelineFactory(springPipelineFactory)
//		//工程的包路径
//		.classpath("com.isport.crawl.hupu")
//		//开启几个爬虫线程
//		.thread(1)
//		//单个爬虫每次抓取完一个请求后的间隔时间
//		.interval(2000)
//		//.debug(true)
//		.start();
		
//		GeccoEngine.create()
//		.scheduler(new RedisStartScheduler(redis_host + ":" + redis_port))
//		.pipelineFactory(springPipelineFactory)
//		.classpath("com.isport.crawl.hupu")
//		.start(HupuJsonListDynamicPipeline.sortRequests)
//		.debug(true)
//		.start();
	}

}
