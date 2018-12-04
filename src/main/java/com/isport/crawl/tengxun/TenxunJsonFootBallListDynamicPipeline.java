package com.isport.crawl.tengxun;

 
import java.util.ArrayList; 
import java.util.List; 

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired; 
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSONObject;
import com.geccocrawler.gecco.pipeline.JsonPipeline;
import com.geccocrawler.gecco.request.HttpGetRequest;
import com.geccocrawler.gecco.request.HttpRequest;
import com.geccocrawler.gecco.scheduler.DeriveSchedulerContext; 
import com.isport.service.NewsService;
import com.isport.utils.MongoTemUtil;

@Service
public class TenxunJsonFootBallListDynamicPipeline extends JsonPipeline {
	@Autowired
	NewsService newsService;
	// 默认设置：第一次爬取时，默认爬取3天内的数据
	public static int DEFAULT_TIME = 3600000 * 24 * 3;
	@Autowired
	MongoTemUtil mongoTemUtil;
	public static List<HttpRequest> sortRequests = new ArrayList<HttpRequest>();
	private static final Logger LOGGER = LoggerFactory.getLogger(TenxunJsonFootBallListDynamicPipeline.class);
	@Override
	public void process(JSONObject job) {
		if(job != null) {
			HttpRequest currRequest = HttpGetRequest.fromJson(job.getJSONObject("request"));
			String json=job.getString("tXunjson").toString();
			String team=job.getString("team").toString();
			String page=job.getString("page").toString();
			String block=job.getString("block").toString();
			//数据库最新发布时间 
			boolean isNext =true;
			Document parse = Jsoup.parse(json);
 			Elements select = parse.select("div.main > div.mod.newslist li");
			for (Element element : select) { 
				String url=element.select("a").attr("href")+"?block="+block+"&team="+"&sk="+team;
				LOGGER.info(url); 
				sortRequests.add(currRequest.subRequest(url));
//				String pubTime=element.select("span").text();
			}
//			String substring = json.substring(json.indexOf(":") + 1, json.lastIndexOf("}"));
//			Gson gs = new Gson();
//			List<Map<String, String>> sport = gs.fromJson(substring,new ArrayList<Map<String, String>>().getClass());
//			Query query =new Query();
//			 Criteria c= Criteria.where("block").is(block);
//		        query.addCriteria(c);
//			List<NewsInfoBean> findByCommand = mongoTemUtil.findByCommand(1, 10, "pubDate",query);
//			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");// 设置日期格式
//			if (findByCommand.size()==0) {
//				pubDate=new Date().getTime()-DEFAULT_TIME;
//			}else {
//				try {
//					pubDate =df.parse(findByCommand.get(0).getPubDate()).getTime();
//				} catch (ParseException e) {
//					e.printStackTrace();
//				}
//			}
//			outer:for (Map<String, String> map : sport) {
//					long notTime = 0;
//					try {
//						notTime = df.parse(map.get("pubtime")).getTime();
//					} catch (ParseException e) {
//						e.printStackTrace();
//					}
//					if (notTime <= pubDate) {
//						isNext=false;
//						LOGGER.info("没有最新文章");
//						break outer;
//					}
//				String updateTime = map.get("pubtime");
//				String kidUrl = map.get("url")+"?block="+block;
//				System.out.println(kidUrl);
//				 sortRequests.add(currRequest.subRequest(kidUrl));
//			}
			if(com.isport.utils.StringUtils.isNUll(page)) {
				page="_1";
			}
				String pag=page.split("_")[1];
				if(Integer.valueOf(pag)<1&&isNext) {
					String nextUrl="";
					if(currRequest.getUrl().contains("_")) {
						nextUrl=currRequest.getUrl().split("_")[0]+(Integer.valueOf(pag)+1)+".htm";
					}else {
					 nextUrl  =nextUrl.substring(0,currRequest.getUrl().lastIndexOf("."))+"_"+(Integer.valueOf(pag)+1)+".htm";
					}
					System.out.println(currRequest.getUrl());
					System.out.println(nextUrl);
					DeriveSchedulerContext.into(currRequest.subRequest(nextUrl));
				}
		}
	}
}