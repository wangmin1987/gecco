package com.isport.crawl.baidu;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.springframework.beans.factory.annotation.Autowired; 
import org.springframework.stereotype.Service;
 
import com.geccocrawler.gecco.dynamic.DynamicGecco;
import com.geccocrawler.gecco.spring.SpringPipelineFactory;

@Service
public class BaiduBJHCrawl {
	// 扩展工厂
	@Autowired
	SpringPipelineFactory springPipelineFactory;

	public static void main(String[] args) {
		try {
			System.out.println(URLEncoder.encode("{\"from\":\"ugc_share\",\"app_id\":\"1562391583989380\"}", "utf-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void register() {
		System.out.println("BaiduBJHCrawl start");
		// 新闻列表spiderBean
		DynamicGecco.json().gecco(new String[] { "https://author.baidu.com/pipe?{param}", "" }, "baiduBJHList")
				.requestField("request").request().build().field("newsList", String.class).renderName("baiduBJHRender")
				.build().register();

		// 详情spiderBean
		DynamicGecco.html()
				.gecco(new String[] { "https://mbd.baidu.com/newspage/data/landingshare?{param}" }, "baiduBJHDetail")
				// 请求对象
				.requestField("request").request().build()
				// 网页内容
				.stringField("content").csspath("html").build()
				// 新闻标题
				.stringField("title").csspath("title").text().build()
				// 发布日期
				.stringField("pubDate").csspath("div.infoSet > span + span").text().build().register();

	}

}
