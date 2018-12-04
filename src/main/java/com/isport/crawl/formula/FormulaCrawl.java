package com.isport.crawl.formula;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.geccocrawler.gecco.GeccoEngine;
import com.geccocrawler.gecco.dynamic.DynamicGecco;
import com.geccocrawler.gecco.redis.RedisStartScheduler;
import com.geccocrawler.gecco.spring.SpringPipelineFactory;
import com.isport.crawl.goal.GoalListPipeline;

@Service
public class FormulaCrawl {
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
	FormulaList formulaList;
	public void register() {
		// 新闻块spiderBean
				Class<?> newsBriefs = DynamicGecco.html().stringField("docUrl").csspath("a").attr("href").build()
						.stringField("docUrlImg").csspath("a > p >img").attr("src").build()
						.register();
				// 新闻列表spiderBean
				DynamicGecco.html()
						.gecco(new String[] { "http://www.wildto.com/v/index_{page}.html"}, "formulaList")
						.requestField("request").request().build().listField("newsUrlList", newsBriefs)
						.csspath("#container > div > div.leftBox > div.videoBlk > ul > li").build().stringField("totalPage")
						.csspath("#container > div > div.leftBox > div.pageBox > a.cur").text().build().register();
		// 详情spiderBean
		DynamicGecco.html().gecco("http://www.wildto.com/v/{id}.html", "formulaDetail").requestField("request").request().build()
				.stringField("content").csspath("html").build().stringField("title").csspath("#container > div > div.leftBox > h2")
				.text().build().stringField("pubDate")
				.csspath("#container > div > div.leftBox > div.detailConTip > span.time").text().build().register();
	}

}
