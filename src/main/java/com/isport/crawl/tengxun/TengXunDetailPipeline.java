 package com.isport.crawl.tengxun;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.geccocrawler.gecco.pipeline.JsonPipeline;
import com.geccocrawler.gecco.request.HttpGetRequest;
import com.geccocrawler.gecco.request.HttpRequest;
import com.google.gson.Gson;
import com.isport.Constants;
import com.isport.bean.NewsInfoBean;
import com.isport.kafka.Producer;
import com.isport.service.NewsParseService;
import com.isport.utils.DateUtils;
import com.isport.utils.MongoTemUtil;
import com.isport.utils.SimilarityUtils;
import com.isport.utils.SpringContextUtils;
import com.isport.utils.StringUtils;

@Service
public class TengXunDetailPipeline extends JsonPipeline {
	@Autowired
	MongoTemUtil mongoTemUtils;
	@Autowired 
	SimilarityUtils similarityUtils;
	@Autowired
	NewsParseService newsParseService;
	//增加kafka消息存储
	@Autowired
	Producer producer;
	@Override
	public void process(JSONObject allSort) {
		HttpRequest currRequest = HttpGetRequest.fromJson(allSort.getJSONObject("request"));
		NewsInfoBean bean=new NewsInfoBean();
		String id = StringUtils.md5(currRequest.getUrl());
		bean.setId(id);
		bean.setHtml("<html>"+allSort.getString("content")+"</html>");
		boolean checkVideoImg = newsParseService.checkVideoImg(bean.getHtml());
		if(!checkVideoImg) { 
			return;
		}
		bean.setIndex_url(currRequest.getHeaders().get("Referer"));  
		bean.setTag(allSort.getString("block")); 
		bean.setTag(allSort.getString("team")+" "+allSort.getString("sk"));
		bean.setCreate_date(DateUtils.getCurrentTime());
		NewsParseService newsParseService=(NewsParseService)SpringContextUtils.getBean("newsParseServiceImpl");
		Document doc = Jsoup.parse(bean.getHtml());
		try {
			if (doc.getElementsByAttributeValue("type", "application/vnd.wap.xhtml+xml").isEmpty()) {
				return;
			}
		} catch (Exception e) {
			return;
		}
		// 标题
		bean.setTitle(allSort.getString("title"));
		newsParseService.uploadImg(doc);
		// 标题图片
		bean.setTitle_imgs(newsParseService.getImage(doc));
		// 文章图片
		// List<String> imgUrl = StringUtils.getImgUrl(doc1);
		// 文章内容
		String select4 = doc.getElementById("Cnt-Main-Article-QQ").getElementsByTag("p").toString();
		bean.setContent(select4);
		// 发布时间
		bean.setPub_date(allSort.getString("pubdate"));
		// 发布来源图标
		bean.setSource_icon(Constants.NEWS_ICON_QQ.value);
		bean.setUrl(currRequest.getUrl());
		//摘要
		bean.setSummary(newsParseService.getSummary(select4));
		bean.setDispose_state(1);
		//调整 channel_id author_id
		String author_id = currRequest.getParameter("author_id");
		String channel_id = currRequest.getParameter("channel_id");
		bean.setAuthor_id(author_id);
		bean.setChannel_id(channel_id);
		
		String distinguish = similarityUtils.distinguish(id,bean.getContent());
		if ("true".equals(distinguish)) {
			//kafka
			producer.send(id, new Gson().toJson(bean));
			mongoTemUtils.save(bean); 
		}
	}
}