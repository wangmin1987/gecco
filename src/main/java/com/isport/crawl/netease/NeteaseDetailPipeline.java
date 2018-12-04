package com.isport.crawl.netease;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
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
public class NeteaseDetailPipeline extends JsonPipeline {

	private static final Logger LOGGER = LoggerFactory.getLogger(NeteaseDetailPipeline.class);

	@Autowired
	MongoTemUtil mongoTemUtil;

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
			boolean res = this.getNeteaseContent(news);
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
			if (StringUtils.isNUll(title)) {
				title = job.getString("title2");
			}
			String keywords = job.getString("keywords");
			keywords = StringUtils.isNUll(keywords) ? "" : keywords.replaceAll(",", " ");
			String pubDate = job.getString("pubdate");
			if (!StringUtils.isNUll(pubDate)) {
				pubDate = pubDate.substring(0, 16).replace("T", " ");
			} else {
				pubDate = currRequest.getParameter("pubdate");
			}

			news.setId(id);
			news.setSource(Constants.NEWS_SOURCE_NETEASE.value);
			news.setSource_icon(Constants.NEWS_ICON_NETEASE.value);
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
			LOGGER.info("Mongo写入成功：" + title);
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
	private boolean getNeteaseContent(NewsInfoBean news) {
		String html = news.getHtml();
		try {
			Document doc = Jsoup.parse(html);
			Elements div = doc.select("#endText");
			if (div == null || div.isEmpty()) {
				throw new Exception("HTML解析失败，找不到正文指定DIV");
			}
			List<String> title_imgs = new ArrayList<String>();
			String content = "";
			if (div.size() == 1) {
				Element element = div.get(0);
				// 删除内容区的尾部信息区
				element.select(".ep-source").remove();
				// 删除内容区中间的广告位
				element.select(".gg200x300").remove();
				// 修改内容区的IMG标签的SRC地址
				newsParseService.uploadImg(element);
				// 删除内容区的A标签并保留标签内的文本内容
				content = Jsoup.clean(element.html(), Whitelist.relaxed().removeTags("a"));
				title_imgs = newsParseService.getImage(element);
			} else {
				for (Element element : div) {
					// 删除内容区的尾部信息区
					element.select(".ep-source").remove();
					// 删除内容区中间的广告位
					element.select(".gg200x300").remove();
					// 修改内容区的IMG标签的SRC地址
					newsParseService.uploadImg(element);
					// 删除内容区的A标签并保留标签内的文本内容
					content = content + Jsoup.clean(element.html(), Whitelist.relaxed().removeTags("a"));
					if (title_imgs.size() == 0) {
						title_imgs = newsParseService.getImage(element);
					}
				}
				// 获取轮播图的前三张图片作为新闻配图
				if (title_imgs.size() == 0) {
					Elements textareas = doc.select(".nph_gallery textarea");
					if (textareas.size() > 0) {
						String photolist = StringEscapeUtils.unescapeHtml(textareas.first().text());
						Elements imgs = Jsoup.parse(photolist).select("img");
						for (int i = 0; i < 3 && i < imgs.size(); i++) {
							String href = imgs.get(i).attr("src");
							if (!StringUtils.isNUll(href)) {
								title_imgs.add(ossUtils.uploadImage(href.substring(0, href.indexOf("?"))));
							}
						}
					}
				}
			}
			news.setTitle_imgs(title_imgs);
			news.setContent(content);
			news.setSummary(newsParseService.getSummary(content));

			newsParseService.filter(content);
			return true;
		} catch (Exception e) {
			LOGGER.info("解析正文失败：" + e.getMessage());
			news.setParse_error(e.getMessage().length() < 255 ? e.getMessage() : e.getMessage().substring(0, 254));
			return false;
		}
	}

}
