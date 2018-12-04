package com.isport.crawl.sogo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.geccocrawler.gecco.dynamic.DynamicGecco;
import com.geccocrawler.gecco.spring.SpringPipelineFactory;

@Service
public class SogoCrawl {
	@Autowired
	SpringPipelineFactory springPipelineFactory;

	public void register() {
		// 新闻块spiderBean
		Class<?> newsBriefs = DynamicGecco.html().stringField("docUrl").csspath("a").attr("href").build().register();
		// 列表页spiderBean
		DynamicGecco.html()
				.gecco(new String[] {
						"http://nba2k2.qq.com/webplat/info/news_version3/30714/33557/m19596/list_{page}.shtml" },
						"sogoList")
				// 请求对象
				.requestField("request").request().build().listField("newsList", newsBriefs)
				.csspath("div.dict_dl_btn a").build()
				.stringField("nextUrl").csspath("a:contains(下一页)").build()
				.register();

	}
}
