//package com.isport.crawl.dongqiudi;
//
//import java.io.BufferedReader;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.net.MalformedURLException;
//import java.net.URL;
//import java.nio.charset.Charset;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Set;
//import java.util.stream.Collectors;
//
//import org.apache.http.HttpEntity;
//import org.apache.http.HttpResponse;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.entity.StringEntity;
//import org.apache.http.impl.client.HttpClients;
//import org.apache.http.util.EntityUtils;
//import org.junit.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//
//import com.google.gson.Gson;
//import com.isport.Constants;
//import com.isport.bean.BodyBean;
//import com.isport.bean.DongQiuBean;
//import com.isport.bean.NewsInfoBean;
//import com.isport.service.NewsService;
//import com.isport.utils.HttpClientUtil;
//import com.isport.utils.OssUtils;
//import com.isport.utils.SimilarityUtils;
//import com.isport.utils.StringUtils;
//
//@Service
//public class DongqiudiService {
//	
//	@Autowired
//	@Qualifier("primaryJdbcTemplate")
//	JdbcTemplate jdbcTemplate;
//
//	@Autowired
//	NewsService newsService;
//
//	@Autowired
//	OssUtils ossUtils;
//	@Autowired
//	SimilarityUtils similarityUtils;
//
//	//@Scheduled(fixedDelay = 1000 * 60 * 60)
//	public void getMessage() {
//		
//		getBody();
//
//	}
//
//	private void getBody() {
//		Map<String,String> maps = new HashMap<String,String>();
//		maps.put("1", Constants.NEWS_BLOCK_HOT.value);
//		maps.put("56", Constants.NEWS_TAG_ZHONGCHOU.value);
//		maps.put("3", Constants.NEWS_TAG_YINGCHOU.value);
//		maps.put("5", Constants.NEWS_TAG_XIJIA.value);
//		maps.put("6", Constants.NEWS_TAG_DEJIA.value);
//		maps.put("4", Constants.NEWS_TAG_YIJIA.value);
//		maps.put("57", Constants.NEWS_TAG_WUZHOU.value);
//		String url = "http://www.dongqiudi.com/archives/{}?";
//		String realUrl = "";
//		Gson gson = new Gson();
//		Set<String> keys = maps.keySet();
//		for(String key:keys) {
//			for(int i=1;i<10;i++) {
//				realUrl = url.replace("{}", key) + "page="+i;
////				System.out.println(realUrl);
//				URL weburl;
//				try {
//					weburl = new URL(realUrl);
//				InputStream is = weburl.openStream();
//				String result = new BufferedReader(new InputStreamReader(is)).lines()
//						.collect(Collectors.joining(System.lineSeparator()));
////				System.out.println(result);
//				DongQiuBean bean = gson.fromJson(result, DongQiuBean.class);
//				if(bean == null || bean.getData()==null || bean.getData().size()==0) {
//					break;
//				}
//				for(DongQiuBean.Data data:bean.getData()) {
//					try {
//						if (newsService.checkUrl(data.getWeb_url())) {
//							NewsInfoBean newsBean = new NewsInfoBean();
//							newsBean.setTitle(data.getTitle());
//							newsBean.setPubDate(data.getDisplay_time());
//							newsBean.setSummary(data.getDescription());
//							newsBean.setUrl(data.getWeb_url());
//							newsBean.setIndex_url(realUrl);
//							newsBean.setBlock(Constants.NEWS_BLOCK_FOOTBALL.value);
//							newsBean.setRemark(maps.get(key));
//	
//							if(newsBean.getUrl().indexOf("video")==-1) {
//								getArticle(newsBean);
//							}
//						}
//						
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//
//				}
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//
//			}
//		}
//	}
//	
//	private void getArticle(NewsInfoBean bean) {
//		URL articleurl;
//		try {
////			System.out.println(bean.getUrl());
//			String result = HttpClientUtil.doGetHeader(bean.getUrl());
////			System.out.println(result);
//			if (result.length() < Constants.NEWS_CONTENT_MIN_LENGTH.intValue) {
//				return;
//			}
//			if (similarityUtils.distinguish(result).equals("true")) {
//				bean.setContent(result);
//				bean.setSource(Constants.NEWS_SOURCE_DONGQIUDI.value);
//				newsService.save(bean);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//	
////	@Test
////	public void testArticle() {
////		NewsInfoBean bean = new NewsInfoBean();
////		bean.setUrl("https://www.dongqiudi.com/archive/782225.html");
////		getArticle(bean);
////	}
//	
//
//}
