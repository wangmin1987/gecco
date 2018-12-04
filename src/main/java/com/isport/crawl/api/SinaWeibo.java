package com.isport.crawl.api;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.isport.Constants;
import com.isport.bean.NewsInfoBean;
import com.isport.kafka.Producer;
import com.isport.service.NewsService;
import com.isport.utils.DateUtils;
import com.isport.utils.MongoTemUtil;
import com.isport.utils.OssUtils;
import com.isport.utils.SimilarityUtils;
import com.isport.utils.SinaMidHelper;
import com.isport.utils.StringUtils;

import weibo4j.Timeline;
import weibo4j.model.Status;
import weibo4j.model.StatusWapper;
import weibo4j.model.User;
import weibo4j.model.WeiboException;

@Service
public class SinaWeibo {

	private static final Logger LOGGER = LoggerFactory.getLogger(SinaWeibo.class);

	// 默认设置：第一次爬取时，默认爬取的最大页数
	public static int DEFAULT_MAX_PAGE = 2;

	@Autowired
	OssUtils ossUtils;

	@Autowired
	SimilarityUtils similarityUtils;

	@Autowired
	MongoTemUtil mongoTemUtil;

	@Autowired
	NewsService newsService;

	@Autowired
	Producer producer;

	/**
	 * 新浪微博：获取当前用户以及所关注用户的最新数据
	 * 
	 * @author wangyuanxi
	 */
	//@Async
	//@Scheduled(fixedDelay = 1000 * 60 * 60*3)
	public void weibo() {
		String access_token = "2.00wOsaKH0WF1y2c415dbc781pLyppC";
		Timeline tm = new Timeline(access_token);
		try {
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			String sourceKey = "news_sina_weibo";
			String since_id = newsService.getLastUpdateTime(sourceKey);

			Map<String, String> map = new HashMap<String, String>();
			if (!StringUtils.isNUll(since_id)) {
				map.put("since_id", since_id);
			}
			map.put("count", "20");
			here: for (int page = 1; page <= DEFAULT_MAX_PAGE; page++) {
				map.put("page", String.valueOf(page));
				StatusWapper status = tm.getHomeTimeline(map);
				if (page == 1) {
					since_id = String.valueOf(status.getSinceId());
				}
				List<Status> statuses = status.getStatuses();
				if (statuses.size() == 0) {
					break here;
				}
				ListIterator<Status> ite = statuses.listIterator();
				while (ite.hasNext()) {
					Status sta = ite.next();
					String content = sta.getText();
					content = filterContent(content);
					User user = sta.getUser();
					String url = SinaMidHelper.getUrl(sta.getMid(), sta.getUser().getId());
					String id = StringUtils.md5(url);
					// HTML内容查重过滤
					String result = similarityUtils.distinguish(id, content);
					if (!result.equals("true")) {
						LOGGER.info("查重失败: " + user.getName() + " " + result);
						continue;
					}

					NewsInfoBean news = new NewsInfoBean();
					List<String> title_imgs = new ArrayList<String>(sta.getPicUrls().size());
					for (String imageUrl : sta.getPicUrls()) {
						imageUrl = imageUrl.replaceAll("/thumbnail/", "/bmiddle/");
						String title_img = ossUtils.uploadImage(imageUrl);
						if (!StringUtils.isNUll(title_img)) {
							title_imgs.add(title_img);
						}
					}
					news.setId(id);
					news.setAuthor_id("8f5accf2ff544367848973e61b683538");
					news.setSource(Constants.NEWS_SOURCE_SINA_WEIBO.value);
					news.setSource_icon(Constants.NEWS_ICON_SINA.value);
					news.setTitle_imgs(title_imgs);
					news.setUrl(url);
					news.setAuthor(user.getName());
					news.setContent(content);
					news.setPub_date(df.format(sta.getCreatedAt()));
					news.setCreate_date(DateUtils.getStrYYYYMMDDHHmmss(new Date()));
					news.setDispose_state(1);
					news.setData_type(Constants.NEWS_DATA_TYPE_KUAIXUN.intValue);

					mongoTemUtil.save(news);
					producer.send(news.getId(), new Gson().toJson(news));
					LOGGER.info("Mongo写入成功！");
				}
			}
			newsService.setLastUpdateTime(sourceKey, since_id);
		} catch (WeiboException e) {
			e.printStackTrace();
		}
	}

	private String filterContent(String content) {
		int t1 = content.indexOf("全文： http://m.weibo.cn");
		if (t1 != -1) {
			content = content.substring(0, t1);
		}
		int t2 = content.indexOf("http://t.cn");
		if (t2 != -1) {
			content = content.substring(0, t2);
		}
		return content.trim();
	}

}
