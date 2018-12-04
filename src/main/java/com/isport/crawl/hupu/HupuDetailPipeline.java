package com.isport.crawl.hupu;

import java.util.Date;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;
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
import com.isport.utils.EsInit;
import com.isport.utils.MongoTemUtil;
import com.isport.utils.OssUtils;
import com.isport.utils.SimilarityUtils;
import com.isport.utils.StringUtils;

/**
 * 网易新闻：正文解析
 * 
 * @author wangyuanxi
 */
@Service
public class HupuDetailPipeline extends JsonPipeline {

	private static final Logger LOGGER = LoggerFactory.getLogger(HupuDetailPipeline.class);

	@Autowired
	MongoTemUtil mongoTemUtil;
	@Autowired
	EsInit esInit;

	@Autowired
	NewsParseService newsParseService;

	@Autowired
	OssUtils ossUtils;

	@Autowired
	SimilarityUtils similarityUtils;
	@Autowired
	Producer producer;

	@Override
	public void process(JSONObject job) {
		try {
			HttpRequest currRequest = HttpGetRequest.fromJson(job.getJSONObject("request"));
			String currUrl = currRequest.getUrl();
			LOGGER.info(currUrl);

			// 正文内容查重过滤
			String id = StringUtils.md5(currUrl);
			String content = job.getString("content");
			NewsInfoBean news = new NewsInfoBean();
			news.setHtml("<html>" + content + "</html>");
			boolean res = this.getHpContent(news);
			news.setDispose_state(res == true ? 1 : 2);
			String result = similarityUtils.distinguish(id, news.getContent());
			if (!result.equals("true")) {
				LOGGER.info("查重失败: " + result);
				return;
			}

			// HttpRequst传参
			String indexUrl = currRequest.getParameter("index_url");
			String tag = currRequest.getParameter("tag");
			String author_id = currRequest.getParameter("author_id");
			String channel_id = currRequest.getParameter("channel_id");
			// HTML文本直接解析
			String title = job.getString("title");
			String keywords = job.getString("keywords");
			String pubDate = job.getString("pubdate");
			keywords = StringUtils.isNUll(keywords) ? "" : keywords.replaceAll("-", " ");

			news.setId(id);
			news.setSource(Constants.NEWS_SOURCE_HUPU.value);
			news.setSource_icon(Constants.NEWS_ICON_HUPU.value);
			news.setTitle(title);
			news.setIndex_url(indexUrl);
			news.setUrl(currUrl);
			news.setPub_date(pubDate);
			news.setAuthor_id(author_id);
			news.setKey_word(keywords);
			news.setTag(tag);
			news.setCreate_date(DateUtils.getStrYYYYMMDDHHmmss(new Date()));
			news.setData_type(Constants.NEWS_DATA_TYPE_XINWEN.intValue);
			news.setChannel_id(channel_id);
			mongoTemUtil.save(news);
			producer.send(id, new Gson().toJson(news));
			LOGGER.info("Mongo，Kafka写入成功：" + title);
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("新闻处理异常: " + e.getMessage());
		}
	}

	/**
	 * 解析正文的HTML
	 * 
	 * @return
	 */
	private boolean getHpContent(NewsInfoBean news) {
		String html = news.getHtml();
		try {
			Document doc = Jsoup.parse(html);
			Elements div = doc.select(".artical-content-read");
			if (div == null || div.isEmpty()) {
				throw new Exception("HTML解析失败，找不到正文指定DIV");
			}
			Element element = div.get(0);
			// 修改内容区的IMG标签的SRC地址
			newsParseService.uploadImg(element);
			// 删除内容区的A标签并保留标签内的文本内容
			String content = Jsoup.clean(element.html(), Whitelist.relaxed().removeTags("a"));
			news.setContent(content);
			//内容摘要
			news.setSummary(newsParseService.getSummary(content));
			//资讯配图
			news.setTitle_imgs(newsParseService.getImage(element));
			newsParseService.filter(content);
			return true;
		} catch (Exception e) {
			news.setParse_error(e.getMessage().length() < 255 ? e.getMessage() : e.getMessage().substring(0, 254));
			LOGGER.info("解析正文失败：" + e.getMessage());
		}
		return false;
	}

}
