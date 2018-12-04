package com.isport.crawl.sohu;

import org.springframework.stereotype.Service;
 
import com.geccocrawler.gecco.dynamic.DynamicGecco;
/**
 * 篮球 自媒体 搜狐号
 * @author 八斗体育
 *
 */
@Service
public class MpSouhuCrawl {
	public void register() {
		// 新闻列表spiderBean
		DynamicGecco.json().gecco(new String[] {
				"http://mp.sohu.com/apiV2/profile/newsListAjax?xpt={xpt}&pageNumber={pageNum}&pageSize=10&categoryId=" },
				"mpSouhuList").requestField("request").request().build().field("newsList",  String.class).renderName("sohuRender").build().register();
		
	}
}
