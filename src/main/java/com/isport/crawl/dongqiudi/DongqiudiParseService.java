//package com.isport.crawl.dongqiudi;
//
//import java.util.List;
//
//import org.apache.commons.lang.StringEscapeUtils;
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//import org.jsoup.nodes.Element;
//import org.jsoup.safety.Whitelist;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.jdbc.core.BeanPropertyRowMapper;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//
//import com.isport.Constants;
//import com.isport.bean.NewsInfoBean;
//import com.isport.service.NewsParseService;
//import com.isport.service.NewsService;
//import com.isport.utils.EsUtils;
//import com.isport.utils.OssUtils;
//import com.isport.utils.ParseUtils;
//import com.isport.utils.StringUtils;
//
//@Service
//public class DongqiudiParseService {
//
//	@Autowired
//	@Qualifier("primaryJdbcTemplate")
//	JdbcTemplate jdbcTemplate;
//
//	@Autowired
//	NewsService newsService;
//
//	@Autowired
//	EsUtils esUtils;
//	
//	@Autowired
//	NewsParseService newsParseService;
//
//	@Autowired
//	OssUtils ossUtils;
//	private static final Logger LOGGER = LoggerFactory.getLogger(DongqiudiParseService.class);
//
//	//@Scheduled(fixedDelay = 1000 * 60 * 10)
//	public void getMessage() {
//		String sql = "select * from news_data_info where SOURCE = '懂球帝' and DISPOSE_STATE = 0 limit 20";
//		for(int i=0;i< 53;i++) {
//			List<NewsInfoBean> list = jdbcTemplate.query(sql, new Object[] {},
//					new BeanPropertyRowMapper<NewsInfoBean>(NewsInfoBean.class));
//			if(list==null || list.size()==0) {
//				LOGGER.info("没有最新数据");
//				return;
//			}
//			for (NewsInfoBean bean : list) {
//				try {
//					parseContent(bean);
////					System.out.println(bean.getUrl());
//					if(bean.getDispose_state() == 1) {
//						if(esUtils.createIndex(bean)) {
//							newsService.updateStatus(bean);
//						};
////							System.out.println(bean.toString());
//					}else {
//						newsService.updateStatus(bean);
//					}
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		}
//
//	}
//	
//	public void parseContent(NewsInfoBean bean) {
//		try {
//			Document doc = Jsoup.parse(StringEscapeUtils.unescapeHtml(bean.getContent()));
//
//			if (StringUtils.isNUll(bean.getTitle())) {
//				Element ele = doc.select("h1").first();
//				if (ele != null) {
//					bean.setTitle(ele.text());
//				} else {
//					ele = doc.select("title").first();
//					if (ele != null) {
//						bean.setTitle(ele.text());
//					}
//				}
//			}
//			Element fragDoc = doc.select("div.detail div").first();
//			if(newsParseService.checkVideoImg(fragDoc)) {
//				bean.setParse_error("视频新闻");
//				bean.setDispose_state(2);
//				return;
//			}
//			newsParseService.uploadImg(fragDoc);
//			String content = Jsoup.clean(fragDoc.html(), "", Whitelist.relaxed());
//			bean.setContent(content);
//			
//			if(StringUtils.isNUll(bean.getSummary())) {
//				bean.setSummary(newsParseService.getSummary(content));
//			}
//			bean.setTitle_img(newsParseService.getImage(fragDoc));
////			if (StringUtils.isNUll(bean.getPubDate())) {
////				Element ele = doc.select("span.date").first();
////				if (ele != null) {
////					bean.setPubDate(ele.text().replaceFirst("年", "-").replaceFirst("月", "-").replaceFirst("日", ""));
////				}
////			}
//			if(!StringUtils.isNUll(bean.getPubDate()) && bean.getPubDate().length()>16) {
//				bean.setPubDate(bean.getPubDate().substring(0, 16));
//			}
//			if(StringUtils.isNUll(bean.getKey_word())) {
//				Element ele = doc.select("div.lists").first();
//				if (ele == null) {
//					
//				} else {
//					bean.setKey_word(ele.text());
//				}
//			}
////			if(!StringUtils.isNUll(bean.getKey_word())) {
////				bean.setKey_word(bean.getKey_word().replaceAll(",", ""));
////			}
//			if(!StringUtils.isNUll(bean.getRemark())) {
//				bean.setTag(bean.getBlock()+","+bean.getRemark());
//			}else {
//				bean.setTag(bean.getBlock());
//			}
//			bean.setSource_icon(Constants.NEWS_ICON_DQD.value);
//			bean.setDispose_state(1);
//		} catch (Exception e) {
//			e.getStackTrace();
//			bean.setDispose_state(2);
//			bean.setParse_error("解析报错");
//		} finally {
//
//		}
//	}
//}
