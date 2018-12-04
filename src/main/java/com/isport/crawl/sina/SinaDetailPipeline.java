package com.isport.crawl.sina;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
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
public class SinaDetailPipeline extends JsonPipeline {
	@Autowired
	MongoTemUtil empRepository;
	@Autowired 
	SimilarityUtils similarityUtils;
	@Autowired
	NewsParseService newsParseService;
	@Autowired
	Producer producer;
	@Override
	public void process(JSONObject allSort) {
		HttpRequest currRequest = HttpGetRequest.fromJson(allSort.getJSONObject("request"));
//		System.out.println("title"+allSort.getString("title"));
//		System.out.println("pubdate"+allSort.getString("pubdate"));
//		System.out.println("tag"+allSort.getString("tag"));
//		System.out.println("content"+allSort.getString("content"));
//		System.out.println("url"+currRequest.getUrl());
//		String htmltxt = ParseUtils.getHtmlBody(html);
//		if (htmltxt.length() < Constants.NEWS_CONTENT_MIN_LENGTH.intValue) {
//			return;
//		}
		NewsInfoBean bean=new NewsInfoBean();
		String id = StringUtils.md5(currRequest.getUrl());
		bean.setId(id);
		bean.setHtml("<html>"+allSort.getString("content")+"</html>");
		boolean checkVideoImg = newsParseService.checkVideoImg(bean.getHtml());
		if(!checkVideoImg) {
			
			return;
		}
		bean.setUrl(currRequest.getUrl());
		bean.setIndex_url(currRequest.getHeaders().get("Referer")); 
		if(allSort.getString("block").equals("足球")) { 
			bean.setTag(allSort.getString("match"));
		}else if(allSort.getString("block").equals("篮球")){ 
			bean.setTag(allSort.getString("team"));
		} 
		getSportsContent(bean);
		bean.setSource(Constants.NEWS_SOURCE_SINA.value);
		bean.setCreate_date(DateUtils.getCurrentTime()); 
		
		//调整 channel_id author_id
		String author_id = currRequest.getParameter("author_id");
		String channel_id = currRequest.getParameter("channel_id");
		bean.setAuthor_id(author_id);
		bean.setChannel_id(channel_id);
		
		String distinguish = similarityUtils.distinguish(id,bean.getContent());
		if ("true".equals(distinguish)) {
			producer.send(id, new Gson().toJson(bean));
			empRepository.save(bean);
		}
	
	}
	public void getSportsContent(NewsInfoBean bean) {
		try {
			NewsParseService newsParseService=(NewsParseService)SpringContextUtils.getBean("newsParseServiceImpl");
			Document doc = Jsoup.parse(bean.getHtml());

			if (StringUtils.isNUll(bean.getTitle())) {
				Element ele = doc.select("h1.main-title").first();
				if (ele != null) {
					bean.setTitle(ele.text());
				} else {
					ele = doc.select("title").first();
					if (ele != null) {
						bean.setTitle(ele.text());
					}
				}
			}
			Element fragDoc = doc.select("div#artibody").first();
			fragDoc.select("img[src*=/s3.pfp.sina.net/]").remove();
			
			newsParseService.uploadImg(fragDoc);
			String content = Jsoup.clean(fragDoc.html(), "http:", Whitelist.relaxed());
			bean.setContent(content);
			String htmltxt = fragDoc.text();
			if(htmltxt.length()>Constants.NEWS_SUMMARY_MAX_LENGTH.intValue) {
				bean.setSummary(htmltxt.substring(0, Constants.NEWS_SUMMARY_MAX_LENGTH.intValue));
			}else {
				bean.setSummary(htmltxt);
			}
			bean.setTitle_imgs(newsParseService.getImage(fragDoc));

			if (StringUtils.isNUll(bean.getPub_date())) {
				Element ele = doc.select("span.date").first();
				if (ele != null) {
					bean.setPub_date(ele.text().replaceFirst("年", "-").replaceFirst("月", "-").replaceFirst("日", ""));
				}
			}
			if (!StringUtils.isNUll(bean.getPub_date()) && bean.getPub_date().length() > 16) {
				bean.setPub_date(bean.getPub_date().substring(0, 16));
			}
			if (StringUtils.isNUll(bean.getKey_word())) {
				Element ele = doc.select("div.keywords").first();
				if (ele == null) {
					ele = doc.select("meta[name=tags]").first();
					if (ele != null) {
						bean.setKey_word(ele.attr("content"));
					}
				} else {
					bean.setKey_word(ele.attr("data-wbkey"));
				}
			}
			if (!StringUtils.isNUll(bean.getKey_word())) {
				bean.setKey_word(bean.getKey_word().replaceAll(",", ""));
			}
			if(!StringUtils.isNUll(bean.getRemark())) {
				bean.setTag(bean.getRemark());
			} 
			bean.setDispose_state(1);
		} catch (Exception e) {
			e.getStackTrace();
			bean.setDispose_state(2);
			bean.setParse_error("解析报错");
		} finally {

		}
	}
}