package com.isport.crawl.sina;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.geccocrawler.gecco.GeccoEngine;
import com.geccocrawler.gecco.dynamic.DynamicGecco;
import com.geccocrawler.gecco.redis.RedisStartScheduler;
import com.geccocrawler.gecco.request.HttpGetRequest;
import com.geccocrawler.gecco.spring.SpringGeccoEngine;
import com.geccocrawler.gecco.spring.SpringPipelineFactory;

@Service
@SuppressWarnings("all")
public class Crawl {

	@Autowired
	SpringPipelineFactory springPipelineFactory;

//	@Scheduled(fixedDelay=1000)
	public void sinaListCrawl() {
		DynamicGecco.html().gecco(
				"http://cre.mix.sina.com.cn/get/cms/feed?callback=jQuery111304905414743559764_1536287123025&pcProduct=29&ctime={ctime}&merge=3&mod=pcsptw&cre=tianyi&statics=1&length=12",
				"sinaJsonListDynamicPipeline").requestField("request").request().build().stringField("sinajson")
				.csspath("body").build().stringField("ctime").requestParameter().build().register();

		GeccoEngine.create().pipelineFactory(springPipelineFactory)
				.scheduler(new RedisStartScheduler("dev.59isport.com:4157")).classpath("com.isport.crawl.sina")
				.start("http://cre.mix.sina.com.cn/get/cms/feed?callback=jQuery111304905414743559764_1536287123025&pcProduct=29&ctime=&merge=3&mod=pcsptw&cre=tianyi&statics=1&length=12")
				.thread(2).interval(2000).run();
//      综合体育
//		DynamicGecco.html().gecco(
//				"http://cre.mix.sina.com.cn/api/v3/get?callback=jQuery1113049483303148696023_1535340038250&cateid=2L&cre=tianyi&mod=pcyy&merge=3&statics=1&tm=1535340038&length=6&offset=0&action=0&up={page}",
//				"sinaHotJsonList").requestField("request").request().build().stringField("sinajson").csspath("body")
//				.build().stringField("page").requestParameter().build().register();
//
//		HttpGetRequest req = new HttpGetRequest(
//				"http://cre.mix.sina.com.cn/api/v3/get?callback=jQuery1113049483303148696023_1535340038250&cateid=2L&cre=tianyi&mod=pcyy&merge=3&statics=1&tm=1535340038&length=6&offset=0&action=0&up=0");
//		req.setCharset("utf-8");
//		GeccoEngine.create().pipelineFactory(springPipelineFactory)
//				.scheduler(new RedisStartScheduler("dev.59isport.com:4157")).classpath("com.isport.crawl.sina")
//				.start(req).thread(1).interval(2000).run();
 
		DynamicGecco.html().gecco("http://sports.sina.com.cn/yayun2018/?from=wap", "sinaHeaderList")
				.requestField("request").request().build().stringField("sinajson").csspath("body").build().register();

		GeccoEngine.create().pipelineFactory(springPipelineFactory)
				.scheduler(new RedisStartScheduler("dev.59isport.com:4157")).classpath("com.isport.crawl.sina")
				.start("http://sports.sina.com.cn/yayun2018/?from=wap").thread(1).interval(2000).run();
		// 篮球
		DynamicGecco.html().gecco("http://sports.sina.com.cn/nba/{num}.shtml?block={block}", "sinaBasketBallList")
				.requestField("request").request().build().stringField("sinajson").csspath("body").build()
				.stringField("block").requestParameter().build().register();

		GeccoEngine.create().pipelineFactory(springPipelineFactory)
				.scheduler(new RedisStartScheduler("dev.59isport.com:4157")).classpath("com.isport.crawl.sina")
				.start(new String[] { "http://sports.sina.com.cn/nba/1.shtml?block=篮球", // 火箭
						"http://sports.sina.com.cn/nba/25.shtml?block=篮球", // 勇士
						"http://sports.sina.com.cn/nba/5.shtml?block=篮球", // 湖人
						"http://sports.sina.com.cn/nba/6.shtml?block=篮球", // 朱强
						"http://sports.sina.com.cn/nba/7.shtml?block=篮球", // 我为鞋狂
						"http://sports.sina.com.cn/nba/3.shtml?block=篮球"// 花边
				}).thread(1).interval(2000).run();
		// 中国足球
		DynamicGecco.html().gecco(
				"http://interface.sina.cn/pc_zt_api/pc_zt_press_news_doc.d.json?subjectID=61903&cat=&size=40&page={page}&channel=sports&block={block}",
				"sinaChinaFootBallList").requestField("request").request().build().stringField("sinajson")
				.csspath("body").build().stringField("block").requestParameter().build().stringField("page")
				.requestParameter().build().register();

		GeccoEngine.create().pipelineFactory(springPipelineFactory)
				.scheduler(new RedisStartScheduler("dev.59isport.com:4157")).classpath("com.isport.crawl.sina")
				.start("http://interface.sina.cn/pc_zt_api/pc_zt_press_news_doc.d.json?subjectID=61903&cat=&size=40&page=1&channel=sports&block=足球")
				.thread(1).interval(2000).run();
		// 足球联赛
		DynamicGecco.html().gecco(
				"http://feed.mix.sina.com.cn/api/roll/get?pageid={pageid}&lid={lid}&num=30&versionNumber=1.2.4&page={page}&encode=utf-8&block={block}",
				"sinaLeagueFootBallList").requestField("request").request().build().stringField("sinajson")
				.csspath("body").build().stringField("block").requestParameter().build().stringField("page")
				.requestParameter().build().stringField("pageid").requestParameter().build().stringField("lid")
				.requestParameter().build().register();

		GeccoEngine.create().pipelineFactory(springPipelineFactory)
				.scheduler(new RedisStartScheduler("dev.59isport.com:4157")).classpath("com.isport.crawl.sina")
				.start(new String[] {
						"http://feed.mix.sina.com.cn/api/roll/get?pageid=46&lid=510&num=30&versionNumber=1.2.4&page=1&encode=utf-8&block=足球", // 西甲
						"http://feed.mix.sina.com.cn/api/roll/get?pageid=45&lid=489&num=30&versionNumber=1.2.4&page=1&encode=utf-8&block=足球", // 英超
						"http://feed.mix.sina.com.cn/api/roll/get?pageid=44&lid=470&num=30&versionNumber=1.2.4&page=1&encode=utf-8&block=足球", // 德甲
						"http://feed.mix.sina.com.cn/api/roll/get?pageid=43&lid=307&num=30&versionNumber=1.2.4&page=1&encode=utf-8&block=足球"// 欧冠
				}).thread(1).interval(2000).run();
		// 德甲
		DynamicGecco.html().gecco("http://sports.sina.com.cn/global/germany/?block={block}", "sinaGermanFootBallList")
				.requestField("request").request().build().stringField("sinajson").csspath("body").build()
				.stringField("block").requestParameter().build().register();

		GeccoEngine.create().pipelineFactory(springPipelineFactory)
				.scheduler(new RedisStartScheduler("dev.59isport.com:4157")).classpath("com.isport.crawl.sina")
				.start("http://sports.sina.com.cn/global/germany/?block=足球").thread(1).interval(2000).run();

		DynamicGecco.html()
				.gecco(new String[] { "http://k.sina.com.cn/article_{temp}.html?block={block}",
						"http://sports.sina.com.cn/{G}/{S}/{date}/{filename}.shtml?block={block}",
						"http://sports.sina.com.cn/{G}/{S}/{date}/{filename}.shtml?block={block}&team={team}",
						"http://sports.sina.com.cn/{G}/{date}/{filename}.shtml?block={block}", 
						"http://sports.sina.com.cn/{G}/{date}.shtml?block={block}" }, "sinaDetailPipeline")
				.requestField("request").request().build()

				.stringField("content").csspath("html").build().stringField("match")
				.csspath("body > div.main-content.w1240 > div.path-search > div.path > div > a:nth-child(5)").build()
				.stringField("block").requestParameter().build().stringField("team").requestParameter().build()
				.register();

//		GeccoEngine.create().scheduler(new RedisStartScheduler("dev.59isport.com:4157"))
//				.pipelineFactory(springPipelineFactory).classpath("com.isport.crawl.sina")
//				.start(SinaHeaderList.sortRequests).run();
	}
}
