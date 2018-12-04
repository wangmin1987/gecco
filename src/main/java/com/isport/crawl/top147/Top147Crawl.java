package com.isport.crawl.top147;

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
public class Top147Crawl {

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
				.stringField("pubDate").csspath("span").text().build()
				// 注册bean
				.register();

		// 列表页对象注册
		DynamicGecco.html()
				// 列表模板-1
				.gecco(new String[] { "http://www.top147.com/newslist/1_{page}.shtml", // 桌球-国际
						"http://www.top147.com/newslist/2_{page}.shtml", // 桌球-国内
						"http://www.top147.com/newslist/10_{page}.shtml", // 桌球-人物
						"http://www.top147.com/newslist/11_{page}.shtml", // 桌球-赛事
						"http://www.top147.com/newslist/13_{page}.shtml", // 桌球-俱乐部
				}, "top147ListExample1")
				// reqeust对象
				.requestField("request").request().build()
				// 资讯List
				.listField("newsList", newsBrief).csspath("div.sonews li:not(.fgf)").build()
				// 页码
				.stringField("page").requestParameter().build()
				// 注册动态bean
				.register();

		// 详情页对象注册
		DynamicGecco.html()
				// 详情模板
				.gecco(new String[] { "http://news.top147.com/{year}/{month}/{filename}.shtml" }, "top147Detail")
				// 定义请求对象
				.requestField("request").request().build()
				// 定义内容
				.stringField("content").csspath("html").build()
				// 定义标题
				.stringField("title").csspath("div.News h1").text().build()
				// 定义关键字
				.stringField("keywords").csspath("meta[name=\"keywords\"]").attr("content").build()
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
