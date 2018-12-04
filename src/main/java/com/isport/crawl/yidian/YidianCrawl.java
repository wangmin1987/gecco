package com.isport.crawl.yidian;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.geccocrawler.gecco.dynamic.DynamicGecco;

/**
 * 一点咨询
 * 
 * @author 八斗体育
 *
 */
@Service
public class YidianCrawl {

	public void register() {
		// 列表页 spiderBean
		DynamicGecco.json().requestField("request").request().build()
				// 匹配地址
				.gecco(new String[] { 
						"http://www.yidianzixun.com/home/q/news_list_for_channel?channel_id={channel_id}&infinite=true&refresh=1&__from__=pc&multi=5&appid=web_yidian&cstart={start}&cend={end}&_spt={paramterCompute}" },
						"yidianList")
				.field("newsList", JSONArray.class).jsonpath("$.result").build().register();

		// 详情spiderBean
		DynamicGecco.html()
				.gecco(new String[] { "http://www.yidianzixun.com/article/{id}" }, "yidianDetail")
				// 请求对象
				.requestField("request").request().build()
				// 网页内容
				.stringField("content").csspath("html").build()
				// 新闻标题
				.stringField("title").csspath("div.left-wrapper h2").text().build() 
				// 发布日期
				.stringField("pubDate").csspath("div.meta span.wm-copyright + span").text().build().register();

	}

}
