package com.isport.crawl.tengxun;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.geccocrawler.gecco.GeccoEngine;
import com.geccocrawler.gecco.dynamic.DynamicGecco;
import com.geccocrawler.gecco.redis.RedisStartScheduler;
import com.geccocrawler.gecco.spring.SpringGeccoEngine;
import com.geccocrawler.gecco.spring.SpringPipelineFactory;
import com.isport.crawl.sina.SinaHeaderList;

@Service
@SuppressWarnings("all")
public class TXunCrawl {
	@Autowired
	SpringPipelineFactory springPipelineFactory;

//	@Async
//	@Scheduled(fixedDelay = 1000 * 60 * 30)
	public void tXunListCrawl() {
		// 足球 综合 竞技
//		DynamicGecco.html()
//		.gecco(new String[] {"http://sports.qq.com/l/others/jingying/jynews/list20170320171444{page}.htm?block={block}",
//				"http://sports.qq.com/l/csocce/list20160112153548{page}.htm?block={block}"
//				,"http://sports.qq.com/l/others/electronic/list2017091416645{page}.htm?block={block}"}, "tenxunJsonListDynamicPipeline")
//		.requestField("request").request().build()
//		.stringField("TXunjson").csspath("#articleLiInHidden").build() 
//		.stringField("block").requestParameter().build()
//		.stringField("page").requestParameter().build()
//		.register();
//		
//		GeccoEngine.create()
//		.pipelineFactory(springPipelineFactory)
////		.scheduler(new RedisStartScheduler("dev.59isport.com:4157"))
//		.classpath("com.isport.crawl.tengxun")
//		.start(new String[] {"http://sports.qq.com/l/others/jingying/jynews/list20170320171444.htm?block=综合",
//				"http://sports.qq.com/l/csocce/list20160112153548.htm?block=足球"
//				,"http://sports.qq.com/l/others/electronic/list2017091416645.htm?block=竞技"})
//		.thread(1)
//		.interval(2000)
//		.run();
		// 篮球
		DynamicGecco.html().requestField("request").request().build().gecco(
				"http://tags.open.qq.com/interface/tag/articles.php?callback=tagListCb&tag={team}&p={page}&l=20&oe=utf-8&ie=utf-8&source=web&site=sports&block={block}",
				"tenxunJsonBasketBallListDynamicPipeline").stringField("TXunjson").csspath("body").build()
				.stringField("team").requestParameter().build().stringField("block").requestParameter().build()
				.stringField("page").requestParameter().build().register();

		GeccoEngine.create().pipelineFactory(springPipelineFactory)
				.scheduler(new RedisStartScheduler("dev.59isport.com:4157")).classpath("com.isport.crawl.tengxun")
		//.start("http://tags.open.qq.com/interface/tag/articles.php?callback=tagListCb&tag=火箭&p=1&l=20&oe=utf-8&ie=utf-8&source=web&site=sports&block=竞技")
				.thread(5).interval(2000).start();
		// 足球
		DynamicGecco.html().requestField("request").request().build()
				.gecco("http://sports.qq.com/l/csocce/jiaa/list20120912114353{page}.htm?block={block}",
						"tenxunJsonFootBallListDynamicPipeline")
				.stringField("TXunjson").csspath("body").build().stringField("team")
				.csspath("body > div.wrapper.wrapperBg > div > div.main > div.mod.titbox > h1").build()
				.stringField("block").requestParameter().build().stringField("page").requestParameter().build()
				.register();

//		GeccoEngine.create().pipelineFactory(springPipelineFactory)
//				.scheduler(new RedisStartScheduler("dev.59isport.com:4157")).classpath("com.isport.crawl.tengxun")
//				.start("http://sports.qq.com/l/csocce/jiaa/list20120912114353.htm?block=足球")
//				.thread(1).interval(2000).run();

//		#listInfo
		DynamicGecco.html()
				.gecco("http://sports.qq.com/a/{date}/{filename}.htm?block={block}&team={team}&sk={sk}",
						"tengXunDetailPipeline")
				.requestField("request").request().build().stringField("content").csspath("#Cnt-Main-Article-QQ")
				.build().stringField("title")
				.csspath("#Main-Article-QQ > div > div.qq_main > div.qq_article > div.hd > h1").build()
				.stringField("pubdate").csspath(".a_time").build().stringField("block").requestParameter().build()
				.stringField("team").requestParameter().build().stringField("sk").requestParameter().build()
//		.stringField("tag").csspath(".mark").build()
//		.stringField("content").csspath("#Cnt-Main-Article-QQ").build()
				.register();

//		GeccoEngine.create()
//		.scheduler(new RedisStartScheduler("dev.59isport.com:4157"))
//				.pipelineFactory(springPipelineFactory).classpath("com.isport.crawl.tengxun")
//				.start(TenxunJsonListDynamicPipeline.sortRequests).run();
	}
}
