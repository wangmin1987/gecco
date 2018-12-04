package com.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.geccocrawler.gecco.redis.RedisStartScheduler;
import com.google.gson.Gson;
import com.isport.Constants;
import com.isport.CrawlServiceApplication;
import com.isport.bean.NewsInfoBean;
import com.isport.crawl.cctv.CctvvideoCrawl;
import com.isport.crawl.distribute.CrawlService;
import com.isport.crawl.distribute.TaskService;
import com.isport.crawl.dongqiudi.DongQiuDiFACrawl;
import com.isport.crawl.dongqiudi.DongQiuDiGBCrawl;
import com.isport.crawl.goal.GoalCrawl;
import com.isport.crawl.hupu.HupuCrawl;
import com.isport.crawl.hupu.HupuSoccerCrawl;
import com.isport.crawl.iqiyi.IqiyiCrawl;
import com.isport.crawl.netease.NTESCrawl;
import com.isport.crawl.onesoccer.SoccerCrawl;
import com.isport.crawl.sina.Crawl;
import com.isport.crawl.sina.SinaGBCrawl;
import com.isport.crawl.sina.SinaLaligaCrawl;
import com.isport.crawl.sohu.SohuCrawl;
import com.isport.crawl.tengxun.TXunCrawl;
import com.isport.crawl.tengxun.TxCrawl;
import com.isport.crawl.tengxun.esports.Txfo4Crawl;
import com.isport.crawl.tengxun.video.FavideoCrawlv2;
import com.isport.crawl.tengxun.video.GBCrawl;
import com.isport.crawl.tengxun.video.GBCrawlv2;
import com.isport.crawl.tengxun.video.UefavideoCrawl;
import com.isport.crawl.tengxun.video.ZcvideoCrawlv2;
import com.isport.utils.EsUtils;
import com.isport.utils.OssUtils;
import com.isport.utils.SimilarityUtils;
import com.isport.utils.StringUtils;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { CrawlServiceApplication.class })
@SuppressWarnings("all")
public class CrawlTest {
	@Autowired
	Crawl crawl;

	@Autowired
	com.isport.crawl.dongqiudi.DongQiuDICrawl dongQiuDICrawl;
	@Autowired
	HupuCrawl hupuCrawl;

	@Autowired
	TXunCrawl tXunCrawl;
	@Autowired
	com.isport.crawl.zhibo8.Zhibo8Crawl zhibo8Crawl;

	@Autowired
	GoalCrawl goalCrawl;
	@Autowired
	IqiyiCrawl iqiyiCrawl;
	@Autowired
	HupuSoccerCrawl hupuSoccerCrawl;
	@Autowired
	DongQiuDiFACrawl dongQiuDiFACrawl;
	@Autowired
	SoccerCrawl soccerCrawl;
	@Autowired
	TxCrawl txCrawl;

	@Autowired
	NTESCrawl nTESCrawl;
	@Autowired
	SinaLaligaCrawl sinaLaligaCrawl;
	@Autowired
	SohuCrawl sohuCrawl;

	@Autowired
	DongQiuDiGBCrawl dongQiuDiGBCrawl;

	@Autowired
	SinaGBCrawl sinaGBCrawl;

	@Autowired
	com.isport.crawl.tengxun.TxSACrawl txSACrawl;

	@Autowired
	com.isport.crawl.footballclub.RenHeCrawl renHeCrawl;

	@Autowired
	com.isport.crawl.tengxun.esports.TxLplCrawl txLplCrawl;
	@Autowired
	com.isport.crawl.tengxun.esports.Tx2k2Crawl tx2k2Crawl;

	@Autowired
	Txfo4Crawl txfo4Crawl;

	@Autowired
	com.isport.crawl.tengxun.esports.TxKplCrawl txKplCrawl;

	@Autowired
	OssUtils ossUtils;

	@Autowired
	CrawlService crawlService;
	// redis 主机
	@Value("${spring.redis.host}")
	private String redis_host;
	// redis 端口
	@Value("${spring.redis.port}")
	private String redis_port;

	@Autowired
	com.isport.crawl.tengxun.video.FavideoCrawl favideoCrawl;
	@Autowired
	com.isport.crawl.tengxun.video.NbavideoCrawl nbavideoCrawl;

	@Autowired
	UefavideoCrawl uefavideoCrawl;
	
//	@Autowired
//	GBCrawl gBCrawl;
	
	@Autowired
	com.isport.crawl.tengxun.video.ZcvideoCrawl zcvideoCrawl;
	
	@Autowired
	com.isport.crawl.aiyuke.video.PyVideoCrawl pyVideoCrawl;
	
	@Autowired
	CctvvideoCrawl cctvvideoCrawl;
	@Autowired
	ZcvideoCrawlv2 zcvideoCrawlv2;
	
	@Autowired
	com.isport.crawl.tengxun.video.GBCrawlv2 gBCrawlv2;
	
	@Autowired
	FavideoCrawlv2 favideoCrawlv2;
	
	@Autowired
	com.isport.crawl.tengxun.video.UefavideoCrawl2 uefavideoCrawl2;
	@Test
	public void insertNews() {
//		crawl.sinaListCrawl(); 
//		dongQiuDICrawl.DongQiuDiListCrawl();
//		hupuCrawl.run();
//		neteaseCrawl.run();
//		tXunCrawl.tXunListCrawl();
//		zhibo8Crawl.run();

//		tXunCrawl.tXunListCrawl();

		// 最新添加网站
//		goalCrawl.crawl();
//		iqiyiCrawl.crawl();
//		hupuSoccerCrawl.crawl();
//		dongQiuDiFACrawl.crawl();
//		soccerCrawl.crawl();
//		txCrawl.crawl();
//		nTESCrawl.crawl();
//		sinaLaligaCrawl.crawl();
//		sohuCrawl.crawl();
//		dongQiuDiGBCrawl.crawl();
//		sinaGBCrawl.crawl();
//		txSACrawl.crawl();
//		ntesSACrawl.crawl();
//		gACrawl.crawl();
//		renHeCrawl.crawl();
//		pyCrawl.crawl();
//		baiduBJHCrawl.crawl();
//		txLplCrawl.crawl();
//		tx2k2Crawl.crawl();

//		txfo4Crawl.crawl();
//		txKplCrawl.crawl();
//		txdnfCrawl.crawl();
//		txfifaCrawl.crawl();
//		txgicpCrawl.crawl();
//		crawlService.crawl();
//		TaskService.create().scheduler(new RedisStartScheduler(redis_host + ":" + redis_port)).start();
//		crawlService.crawl();

//		nbavideoCrawl.crawl();
//		favideoCrawl.crawl();
//		uefavideoCrawl.crawl();
		
//		gBCrawl.crawl();
//		zcvideoCrawlv2.crawl();
//		gBCrawlv2.crawl();
//		favideoCrawlv2.crawl();
		uefavideoCrawl2.crawl();
//		pyVideoCrawl.crawl("http://www.aiyuke.com/video_list/1------------.html");
//		pyVideoCrawl.crawl("http://www.aiyuke.com/video_list/2------------.html");
		
//		cctvvideoCrawl.crawl("http://sports.cctv.com/tennis/videos/");
		 
	}
}
