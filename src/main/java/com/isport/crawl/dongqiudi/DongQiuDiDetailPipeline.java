package com.isport.crawl.dongqiudi;


 

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document; 
import org.jsoup.safety.Whitelist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.isport.utils.StringUtils;

@Service
public class DongQiuDiDetailPipeline extends JsonPipeline {
	private static final Logger LOGGER = LoggerFactory.getLogger(DongQiuDiDetailPipeline.class);
	@Autowired
	MongoTemUtil mongoTemUtils;
	@Autowired 
	SimilarityUtils similarityUtils;
	@Autowired
	Producer producer;
	@Autowired
	NewsParseService newsParseService;
	@Override
	public void process(JSONObject allSort) {
		HttpRequest currRequest = HttpGetRequest.fromJson(allSort.getJSONObject("request"));
		NewsInfoBean bean=new NewsInfoBean();
		String id = StringUtils.md5(currRequest.getUrl());
		bean.setId(id);
		bean.setHtml("<html>"+allSort.getString("content")+"</html>");
		bean.setIndex_url(currRequest.getHeaders().get("Referer"));  
		Document doc = Jsoup.parse(bean.getHtml());
		boolean checkVideoImg = newsParseService.checkVideoImg(bean.getHtml());
		if(checkVideoImg) {
			return;
		}
		newsParseService.uploadImg(doc);
		// 标题
		bean.setTitle(allSort.getString("title"));
		// 标题图片
		bean.setTitle_imgs(newsParseService.getImage(doc));
		// 文章图片
		// List<String> imgUrl = StringUtils.getImgUrl(doc1);
		// 文章内容
		String fragDoc = doc.getElementsByTag("p").toString();
		String content = Jsoup.clean(fragDoc, "", Whitelist.relaxed());
		String distinguish = similarityUtils.distinguish(id,content);
		if (!"true".equals(distinguish)) {
			LOGGER.info(currRequest.getUrl()+" 查重失败: " + distinguish);
			return;
		}
		bean.setContent(content);
		// 发布时间
		bean.setPub_date(allSort.getString("pubdate").substring(0, 16));
		// 发布来源图标
		bean.setSource(Constants.NEWS_SOURCE_DONGQIUDI.value);
		bean.setSource_icon(Constants.NEWS_ICON_DQD.value);
		bean.setUrl(currRequest.getUrl());
		//摘要
		bean.setSummary(newsParseService.getSummary(content));
		bean.setDispose_state(1);
		bean.setData_type(0); 
		bean.setChannel_id(currRequest.getParameter("channel_id"));
		bean.setTag(currRequest.getParameter("tag"));
		bean.setCreate_date(DateUtils.getCurrentTime());
		bean.setAuthor_id(currRequest.getParameter("author_id"));
		producer.send(bean.getId(), new Gson().toJson(bean));
		mongoTemUtils.save(bean);
	}
}