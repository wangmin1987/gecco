package com.isport.crawl.tengxun;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.geccocrawler.gecco.dynamic.DynamicGecco;

/**
 * 腾讯乒乓球和羽毛球
 * 
 * @author 八斗体育
 *
 */
@Service
public class PyCrawl {

	public void register() {
		// 列表页spiderBean
		DynamicGecco.html() 
				.gecco(new String[] { "http://sports.qq.com/l/others/ppq/ppqxw/{type}/list{datetime}.htm",
						"http://sports.qq.com/l/others/ppq/ppqxw/{type}/list{datetime}_{page}.htm", 						
						"http://sports.qq.com/l/others/ymq/badmintonnews/{type}/list{datetime}.htm",
						"http://sports.qq.com/l/others/ymq/badmintonnews/{type}/list{datetime}_{page}.htm" },
						"pyList")
				// 请求对象
				.requestField("request").request().build().field("newsList", JSONArray.class)
				.jsvar("ARTICLE_LIST", "$.listInfo").build().register();
		// 关键词对象
		Class<?> keyword = DynamicGecco.html().stringField("keyword").csspath("a").text().build().register();
		// 详情spiderBean
		DynamicGecco.html().gecco("http://sports.qq.com/a/{date}/{id}.htm", "pyDetail")
				// 定义请求对象
				.requestField("request").request().build().stringField("content").csspath("html").build()
				// 定义标题
				.stringField("title").csspath("div.hd h1").text().build()
				// 新闻关键词
				.listField("keywords", keyword).csspath("#videokg span").build()
				// 定义发布日期
				.stringField("pubDate").csspath("span.a_time").text().build().register();

	}

}
