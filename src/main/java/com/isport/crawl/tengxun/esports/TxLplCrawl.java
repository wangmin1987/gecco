package com.isport.crawl.tengxun.esports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.geccocrawler.gecco.GeccoEngine;
import com.geccocrawler.gecco.dynamic.DynamicGecco;
import com.geccocrawler.gecco.redis.RedisStartScheduler;
import com.geccocrawler.gecco.spring.SpringPipelineFactory;

@Service
public class TxLplCrawl {
	// redis 主机
	@Value("${spring.redis.host}")
	private String redis_host;
	// redis 端口
	@Value("${spring.redis.port}")
	private String redis_port;

	// 扩展工厂
	@Autowired
	SpringPipelineFactory springPipelineFactory;
	@Autowired
	TxLplList txLplList;

	public void register() {
		// 列表页spiderBean
		DynamicGecco.json().gecco(new String[] {
				"http://apps.game.qq.com/wmp/v3.1/?p0=3&p1=searchNewsKeywordsList&page={pageNum}&pagesize=16&order=sIdxTime&r0=script&r1=NewsObj&type=iTarget&id=30,35,36&source=web_pc" },
				"txLplList")
				// 请求对象
				.requestField("request").request().build().field("newsList", String.class).renderName("txBJHRender")
				.build().register();
		// 详情spiderBean
		DynamicGecco.html()
				.gecco("http://apps.game.qq.com/cmc/zmMcnContentInfo?r0=jsonp&source=web_pc&type=0&docid={docid}",
						"txLplDetail")
				// 定义请求对象
				.requestField("request").request().build().field("newsDetail", String.class).renderName("txBJHRender")
				.build().register();
	}

	@SuppressWarnings("unchecked")
	public void cralw() {
		System.out.println("TxLplCrawl start");
		// 列表页spiderBean
		DynamicGecco.json().gecco(new String[] {
				"http://apps.game.qq.com/wmp/v3.1/?p0=3&p1=searchNewsKeywordsList&page={pageNum}&pagesize=16&order=sIdxTime&r0=script&r1=NewsObj&type=iTarget&id=30,35,36&source=web_pc" },
				"txLplList")
				// 请求对象
				.requestField("request").request().build().field("newsList", String.class).renderName("txBJHRender")
				.build().register();

		// 定义线程数量和延迟等待时间
		int thread = 1, interval = 1000;
		GeccoEngine.create()
				// 自定义下载url调度
				.scheduler(new RedisStartScheduler(redis_host + ":" + redis_port))
				// 自定义管道过滤器工厂
				.pipelineFactory(springPipelineFactory)
				// 扫描包路径
				.classpath("com.isport.crawl.tengxun.esports")
//				.start("http://apps.game.qq.com/wmp/v3.1/?p0=3&p1=searchNewsKeywordsList&page=1&pagesize=16&order=sIdxTime&r0=script&r1=NewsObj&type=iTarget&id=30,35,36&source=web_pc")
				// 启动spider数量
				.thread(thread)
				// 延迟等待事件
				.interval(interval)
				// 阻塞启动
				.run();

		// 详情spiderBean
		DynamicGecco.html()
				.gecco("http://apps.game.qq.com/cmc/zmMcnContentInfo?r0=jsonp&source=web_pc&type=0&docid={docid}",
						"txLplDetail")
				// 定义请求对象
				.requestField("request").request().build().field("newsDetail", String.class).renderName("txBJHRender")
				.build().register();

		GeccoEngine.create()
				// 自定义下载url调度
				.scheduler(new RedisStartScheduler(redis_host + ":" + redis_port))
				// 自定义管道过滤器工厂
				.pipelineFactory(springPipelineFactory)
				// 扫描包路径
				.classpath("com.isport.crawl.tengxun.esports").thread(thread)
				// 延迟等待事件
				.interval(interval).start(txLplList.getSortRequests())
				// 阻塞启动
				.run();
		System.out.println("TxLplCrawl end");
	}
}
