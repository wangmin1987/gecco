package com.isport.crawl.dongqiudi;

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
public class DongQiuDICrawl {
	@Autowired
	SpringPipelineFactory springPipelineFactory;
	@Value("${spring.redis.host}")
	private String redis_host;

	@Value("${spring.redis.port}")
	private String redis_port;
//	@Async
//	@Scheduled(fixedDelay = 1000 * 60 * 30)
	public void DongQiuDiListCrawl() {
		//足球
				DynamicGecco.html()
				.requestField("request").request().build()
				.gecco("http://www.dongqiudi.com/archives/{num}?page={page}&block={block}&match={match}", "dongQiuDiJsonListDynamicPipeline")
				.stringField("dongQiuDiJson").csspath("body").build() 
				.stringField("match").requestParameter().build()
				.stringField("block").requestParameter().build()
				.stringField("page").requestParameter().build()
				.stringField("num").requestParameter().build()
				.register(); 
				
//				#listInfo
				DynamicGecco.html()
				.gecco("https://www.dongqiudi.com/{a}/{filename}.html?block={block}&match={match}", "dongQiuDiDetailPipeline")
				.requestField("request").request().build()
				.stringField("content").csspath("#con > div.left > div.detail").build()
				.stringField("title").csspath("#con > div.left > div.detail > h1").build()
				.stringField("pubdate").csspath("#con > div.left > div.detail > h4 > span.time").build()
				.stringField("block").requestParameter().build()
				.stringField("match").requestParameter().build()
//				.stringField("tag").csspath(".mark").build()
				.register();
				
				GeccoEngine.create()
				.pipelineFactory(springPipelineFactory)
				.scheduler(new RedisStartScheduler(redis_host + ":" + redis_port))
				.classpath("com.isport.crawl.dongqiudi")
//				.start(new String[] {"http://www.dongqiudi.com/archives/56?page=1&block=足球&match=中超",
//						"http://www.dongqiudi.com/archives/3?page=1&block=足球&match=英超",
//						"http://www.dongqiudi.com/archives/5?page=1&block=足球&match=西甲",
//						"http://www.dongqiudi.com/archives/4?page=1&block=足球&match=意甲",
//						"http://www.dongqiudi.com/archives/55?page=1&block=足球&match=深度",
//				})
				.thread(2)
				.interval(2000)
				.start();
		
		
//		GeccoEngine.create()
//		.scheduler(new RedisStartScheduler(redis_host + ":" + redis_port))
//		.pipelineFactory(springPipelineFactory)
//		.classpath("com.isport.crawl.dongqiudi")
//		.start(DongQiuDiJsonListDynamicPipeline.sortRequests)
//		.run();
	}
}
