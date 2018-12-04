package com.isport.crawl.netease;

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
public class NeteaseCrawl {

	@Value("${spring.redis.host}")
	private String redis_host;

	@Value("${spring.redis.port}")
	private String redis_port;

	@Autowired
	SpringPipelineFactory springPipelineFactory;

//	@Async
//	@Scheduled(fixedDelay = 1000 * 60 * 30)
	public void register() {
		// 列表页爬取：NBA球队新闻
		Class<?> newsBrief = DynamicGecco.html()
				.stringField("docurl").csspath("h3 > a").attr("href").build()
				.stringField("pubdate").csspath(".post_date").text().build()
				.stringField("tienum").csspath(".share_join .icon").text().build()
				.register();
		
		DynamicGecco.html()
		.gecco(new String[] {
				"http://sports.163.com/special/warriorsgd2016{page}/",
				"http://sports.163.com/special/lakersgd2016{page}/",
				"http://sports.163.com/special/rocketsgd2016{page}/",
				"http://sports.163.com/special/okcgd2016{page}/",
				"http://sports.163.com/special/celticsnews{page}/",
				"http://sports.163.com/special/spursgd2016{page}/",
				"http://sports.163.com/special/nbagd2016{page}/",
				"http://sports.163.com/special/blindness{page}/",
				"http://sports.163.com/special/cbagd2016{page}/",
				"http://sports.163.com/special/00051F15/moreycgd{page}.html",
				"http://sports.163.com/special/00051F1O/morexjnews{page}.html",
				"http://sports.163.com/special/00051C97/dj{page}.html",
				"http://sports.163.com/special/00051C9E/gjb{page}.html",
				"http://sports.163.com/special/00051C89/zc{page}.html" 
				}, "neteaseHtmlListDynamicPipeline")
		.requestField("request").request().build()
		.listField("newslist", newsBrief).csspath(".new_list > .news_item").build()
		.stringField("page").requestParameter().build()
		.register();
		// 详情页爬取
				DynamicGecco.html()
				.gecco(new String[] { "http://sports.163.com/{year}/{monthday}/{hour}/{filename}.html",
						"http://data.2018.163.com/match_detail.html#/prospect/{matchid}",
						"http://2018.163.com/{year}/{monthday}/{hour}/{filename}.html"
						}, "neteaseDetailPipeline")
				.requestField("request").request().build()
				.stringField("content").csspath("html").build()
				.stringField("title").csspath("#epContentLeft > h1").text().build()
				.stringField("title2").csspath(".atc_title > h1").text().build()
				.stringField("keywords").csspath("meta[name=\"keywords\"]").attr("content").build()
				.stringField("pubdate").csspath("meta[property=\"article:published_time\"]").attr("content").build()
				.register();
//		// 列表页爬取：综合新闻
//		DynamicGecco.html()
//		.gecco(new String[] {
//				"http://sports.163.com/special/000587PN/newsdata_world_yj{page}.js?callback=data_callback",
//				"http://sports.163.com/special/000587PR/newsdata_n_index{page}.js?callback=data_callback",
//				"http://sports.163.com/special/000587PQ/newsdata_allsports_index{page}.js?callback=data_callback"
//				}, "neteaseJsonListDynamicPipeline")
//		.requestField("request").request().build()
//		.stringField("content").csspath("body").build()
//		.stringField("page").requestParameter().build()
//		.register();

//		GeccoEngine.create()
//		.scheduler(new RedisStartScheduler(redis_host + ":" + redis_port))
//		.pipelineFactory(springPipelineFactory)
//		.classpath("com.isport.crawl.netease")
//		.thread(2)
//		.interval(2000)
//		.start();

//		GeccoEngine.create()
//		.scheduler(new RedisStartScheduler(redis_host + ":" + redis_port))
//		.pipelineFactory(springPipelineFactory)
//		.classpath("com.isport.crawl.netease")
//		.start(NeteaseHtmlListDynamicPipeline.sortRequests)
//		.start(NeteaseJsonListDynamicPipeline.sortRequests)
//		.run();
	}

}
