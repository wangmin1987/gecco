package com.isport.crawl.tengxun;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.geccocrawler.gecco.GeccoEngine;
import com.geccocrawler.gecco.dynamic.DynamicGecco;
import com.geccocrawler.gecco.redis.RedisStartScheduler;
import com.geccocrawler.gecco.spring.SpringPipelineFactory;
import com.isport.crawl.distribute.SchedulerSingle;

/**
 * 综合体育模块
 * 台球资讯、跑步资讯
 */
@Service
@SuppressWarnings("all")
public class TxOthersCrawl {

	@Autowired
	SpringPipelineFactory springPipelineFactory;

	@Autowired
	SchedulerSingle schedulerSingle;

	public void register() {
		// 资讯List对象注册
		Class<?> newsBrief = DynamicGecco.html()
				// 定义详情页url
				.stringField("docUrl").csspath("a").attr("href").build()
				// 定义发布时间
				.stringField("pubDate").csspath(".pub_time").text().build()
				// 注册bean
				.register();

		// 列表页对象注册
		DynamicGecco.html()
				// 列表模板-1
				.gecco(new String[] { "http://sports.qq.com/l/others/run/runnews/roll{page}.htm", // 跑步
						"http://sports.qq.com/l/tennis/list2013111611516{page}.htm", // 网球
						"http://sports.qq.com/l/others/paiq/list201203611739{page}.htm", // 排球
						"http://sports.qq.com/l/f1/list20131122142623{page}.htm", // 赛车
						"http://sports.qq.com/l/others/dongji/dongjinews/bxnews{page}.htm", // 冰雪
						"http://sports.qq.com/l/others/tableb/list201203611650{page}.htm", // 台球
						"http://sports.qq.com/l/others/tableb/snookernews/zhongshibaqiu/list20150227152823{page}.htm", // 台球 > 中式八球
				}, "txListExample1")
				// reqeust对象
				.requestField("request").request().build()
				// 资讯List
				.listField("newsList", newsBrief).csspath("div.leftList li").build()
				// 页码
				.stringField("page").requestParameter().build()
				// 注册动态bean
				.register();

		// 列表页对象注册
		DynamicGecco.html()
				// 列表模板-2
				.gecco(new String[] {
						"http://sports.qq.com/l/others/tableb/snookernews/dingjunhuinews/list2017073135624{page}.htm", // 台球 > 丁俊晖
						"http://sports.qq.com/l/others/tableb/snookernews/panxiaotingnews/list2017073135708{page}.htm", // 台球 > 潘晓婷
				}, "txListExample2")
				// reqeust对象
				.requestField("request").request().build()
				// 资讯List
				.field("newsList", JSONArray.class).jsvar("ARTICLE_LIST", "$.listInfo").build()
				// 页码
				.stringField("page").requestParameter().build()
				// 注册动态bean
				.register();

		// 详情页对象注册
		DynamicGecco.html()
				// 详情模板
				.gecco("http://sports.qq.com/a/{date}/{filename}.htm", "txDetail")
				// 定义请求对象
				.requestField("request").request().build()
				// 定义内容
				.stringField("content").csspath("html").build()
				// 定义标题
				.stringField("title").csspath("div.hd h1").text().build()
				// 定义发布日期
				.stringField("pubDate").csspath("div.hd span.a_time").text().build()
				// 定义发布日期
				.stringField("pubDate2").csspath("div.hd span.article-time").text().build()
				// 注册
				.register();
	}

	public void test() {
		register();
		// 定义线程数量和延迟等待时间
		int thread = 1, interval = 1000;
		GeccoEngine.create()
				// 自定义下载url调度
				.scheduler(schedulerSingle.getInstance())
				// 自定义管道过滤器工厂
				.pipelineFactory(springPipelineFactory)
				// 扫描包路径
				.classpath("com.isport.crawl")
				// 启动spider数量
				.thread(thread)
				// 延迟等待事件
				.interval(interval)
				// 阻塞启动
				.run();
	}

}
