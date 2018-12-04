package com.isport.crawl.sina;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSONObject;
import com.geccocrawler.gecco.pipeline.JsonPipeline;
import com.geccocrawler.gecco.request.HttpGetRequest;
import com.geccocrawler.gecco.request.HttpRequest;
import com.isport.bean.NewsInfoBean;
import com.isport.utils.MongoTemUtil;

@Service
public class SinaGermanFootBallList extends JsonPipeline {
	public static List<HttpRequest> sortRequests = new ArrayList<HttpRequest>();
	// 默认设置：第一次爬取时，默认爬取3天内的数据
	public static int DEFAULT_TIME = 3600000 * 24 * 3;
	@Autowired
	MongoTemUtil mongoTemUtil;

	@Override
	public void process(JSONObject job) {
		if (job != null) {
			HttpRequest currRequest = HttpGetRequest.fromJson(job.getJSONObject("request"));
			String html = job.getString("sinajson").toString();
			String block = job.getString("block").toString();
			Document doc = Jsoup.parse(html);
			Elements eles = doc.select(".mleft li");
			// 数据库最新发布时间
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");// 设置日期格式
			long pubDate = 0;
			Query query = new Query();
			Criteria c = Criteria.where("block").is(block);
			Criteria d = Criteria.where("sport_team").is("德甲专题");
			query.addCriteria(c);
			query.addCriteria(d);
			List<NewsInfoBean> findByCommand = mongoTemUtil.findByCommand(1, 10, "pubDate", query);
			if (findByCommand.size() == 0) {
				pubDate =new Date().getTime() - DEFAULT_TIME;
			} else {
				try {
					pubDate = df.parse(findByCommand.get(0).getPub_date()).getTime();
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			outer:for (Element element : eles) {
				String url = element.select("a").attr("href");
				try {
					Long noTime = df.parse(element.select(".dnt").text()).getTime();
					 if (noTime <= pubDate) {
					 break outer;
					 }
				} catch (ParseException e) {
					e.printStackTrace();
				}
				sortRequests.add(currRequest.subRequest(url+"?block="+block));
			}
		}

	}
}
