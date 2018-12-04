package com.isport.crawl.goal;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.NullableUtils;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
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
public class GoalDetailPipeline extends JsonPipeline {

	private static final Logger LOGGER = LoggerFactory.getLogger(GoalDetailPipeline.class);
	// mongo 操作工具
	@Autowired
	MongoTemUtil mongoTemUtil;
	// 新闻解析服务
	@Autowired
	NewsParseService newsParseService;
	// 文本去重工具
	@Autowired
	SimilarityUtils similarityUtils;
	// kafka 操作
	@Autowired
	Producer producer;

	@Override
	public void process(JSONObject jo) {
		try {
			// infoBean 对象
			NewsInfoBean newsInfoBean = new NewsInfoBean();
			HttpRequest request = HttpGetRequest.fromJson(jo.getJSONObject("request"));
			String url = request.getUrl();
			newsInfoBean.setUrl(url);
			// 网页内容
			newsInfoBean.setHtml("<html>" + jo.getString("content") + "</html>");
			// 解析新闻内容
			boolean parsed = parseContent(newsInfoBean);
			int disposeState = parsed ? 1 : 2;
			newsInfoBean.setDispose_state(disposeState);

			// 正文相似性去重
			String id = StringUtils.md5(url);
			String mainBody = newsInfoBean.getContent();
			String similar = similarityUtils.distinguish(id, mainBody);
			if (!similar.equals("true")) {
				LOGGER.info("查重失败: " + similar);
				return;
			}
			newsInfoBean.setId(id);

			// constant 变量设置
			newsInfoBean.setSource(Constants.NEWS_SOURCE_GOAL.value);
			newsInfoBean.setSource_icon(Constants.NEWS_ICON_GOAL.value);

			// request 参数
			String indexUrl = request.getParameter("index_url");
			String tag = request.getParameter("tag");
			String authorId = request.getParameter("author_id");
			String channelId = request.getParameter("channel_id");
			newsInfoBean.setIndex_url(indexUrl);
			newsInfoBean.setTag(tag);
			newsInfoBean.setAuthor_id(authorId);
			newsInfoBean.setChannel_id(channelId);
			// html 文本解析
			String title = jo.getString("title");
			JSONArray keywords = jo.getJSONArray("keywords");
			StringBuilder keywordBuilder = new StringBuilder();
			LOGGER.info("keywords size:"+keywords.size());
			for (Object keyword : keywords) {
				JSONObject item = JSONObject.parseObject(keyword.toString());
				keywordBuilder.append(item.getString("keyword"));
				keywordBuilder.append(" ");
			}
			String pubDate = jo.getString("pubDate");
			LOGGER.info("Detail:" + title + "," + keywordBuilder.toString() + "," + pubDate);
			// 日期格式化
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			pubDate = StringUtils.isNUll(pubDate) ? null : sdf.format(Long.valueOf(pubDate));
			newsInfoBean.setTitle(title);
			newsInfoBean.setKey_word(keywordBuilder.toString());
			newsInfoBean.setPub_date(pubDate);

			// 其他参数
			newsInfoBean.setCreate_date(DateUtils.getStrYYYYMMDDHHmmss(new Date()));
//			mongoTemUtil.save(newsInfoBean);
//			producer.send(id, new Gson().toJson(newsInfoBean));
			LOGGER.info(newsInfoBean.toString());
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Goal 详情抽取错误:" + e.getMessage());
		}
	}

	/**
	 * 解析内容
	 * 
	 * @param newsInfoBean
	 * @return
	 */
	private boolean parseContent(NewsInfoBean newsInfoBean) {
		try {
			String html = newsInfoBean.getHtml();
			// 解析html
			Document doc = Jsoup.parse(html);
			// 获取body体标签
			Elements bodyDivs = doc.select("div.body");
			Element bodyDiv = bodyDivs.get(0);
			// 修改内容区IMG标签的地址
			newsParseService.uploadImg(bodyDiv);
			// 删除A标签保留文本
			String cleanContent = Jsoup.clean(bodyDiv.html(), Whitelist.relaxed().removeTags("a"));
			// 清洁内容
			newsInfoBean.setContent(cleanContent);
			// 内容摘要
			newsInfoBean.setSummary(newsParseService.getSummary(cleanContent));
			// 咨询配图
			newsInfoBean.setTitle_imgs(newsParseService.getImage(bodyDiv));
			// 咨询非法内容过滤
			newsParseService.filter(cleanContent);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Goal parseContent解析内容错误:" + e.getMessage());
		}
		return false;
	}

}
