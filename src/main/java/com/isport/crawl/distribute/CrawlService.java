package com.isport.crawl.distribute;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.geccocrawler.gecco.GeccoEngine;
import com.geccocrawler.gecco.spring.SpringPipelineFactory;
import com.isport.crawl.aiyuke.AiyukeCrawl;
import com.isport.crawl.baidu.BaiduBJHCrawl;
import com.isport.crawl.dongqiudi.DongQiuDICrawl;
import com.isport.crawl.dongqiudi.DongQiuDiFACrawl;
import com.isport.crawl.footballclub.RenHeCrawl;
import com.isport.crawl.formula.FormulaCrawl;
import com.isport.crawl.formula.FormulabstractDetailPipeLine;
import com.isport.crawl.goal.GoalCrawl;
import com.isport.crawl.hupu.HupuCSLCrawl;
import com.isport.crawl.hupu.HupuCrawl;
import com.isport.crawl.hupu.HupuSoccerCrawl;
import com.isport.crawl.iqiyi.IqiyiCrawl;
import com.isport.crawl.netease.NTESCrawl;
import com.isport.crawl.netease.NeteaseCrawl;
import com.isport.crawl.onesoccer.SoccerCrawl;
import com.isport.crawl.sina.SinaCrawl;
import com.isport.crawl.sinaNBA.SinaNBACrawl;
import com.isport.crawl.sohu.MpSouhuCrawl;
import com.isport.crawl.sohu.SohuCrawl;
import com.isport.crawl.tengxun.PyCrawl;
import com.isport.crawl.tengxun.TxCrawl;
import com.isport.crawl.tengxun.TxOthersCrawl;
import com.isport.crawl.tengxun.esports.Tx2k2Crawl;
import com.isport.crawl.tengxun.esports.TxKplCrawl;
import com.isport.crawl.tengxun.esports.TxLplCrawl;
import com.isport.crawl.tengxun.esports.Txfo4Crawl;
import com.isport.crawl.top147.Top147Crawl;
import com.isport.crawl.xiguaji.XiguajiCrawl;
import com.isport.crawl.yidian.YidianCrawl;
import com.isport.crawl.zhibo8.Zhibo8Crawl;

@Service
public class CrawlService {

	@Autowired
	SpringPipelineFactory springPipelineFactory;

	@Autowired
	SchedulerSingle schedulerSingle;

	@Autowired
	TxKplCrawl txKplCrawl;
	@Autowired
	Txfo4Crawl txfo4Crawl;
	@Autowired
	DongQiuDiFACrawl dongQiuDiFACrawl;
	@Autowired
	GoalCrawl goalCrawl;
	@Autowired
	HupuSoccerCrawl hupuSoccerCrawl;
	@Autowired
	IqiyiCrawl iqiyiCrawl;
	@Autowired
	SoccerCrawl soccerCrawl;
	@Autowired
	TxCrawl txCrawl;
	@Autowired
	Zhibo8Crawl zhibo8Crawl;
	@Autowired
	HupuCrawl hupuCrawl;
	@Autowired
	DongQiuDICrawl dongQiuDICrawl;
	@Autowired
	NeteaseCrawl neteaseCrawl;
	@Autowired
	SohuCrawl sohuCrawl;
	@Autowired
	HupuCSLCrawl hupuCSLCrawl;
	@Autowired
	RenHeCrawl renHeCrawl;
	@Autowired
	Tx2k2Crawl tx2k2Crawl;
	@Autowired
	TxLplCrawl txLplCrawl;
	@Autowired
	TxOthersCrawl txOthersCrawl;
	@Autowired
	Top147Crawl top147Crawl;
	@Autowired
	SinaCrawl sinaCrawl;
	@Autowired
	AiyukeCrawl aiyukeCrawl;
	@Autowired
	YidianCrawl yidianCrawl;
	@Autowired
	MpSouhuCrawl mpSouhuCrawl;
	@Autowired
	BaiduBJHCrawl baiduBJHCrawl;
	@Autowired
	PyCrawl pyCrawl;
	@Autowired
	NTESCrawl nTESCrawl;
	@Autowired
	XiguajiCrawl xiguajiCrawl;
	@Autowired
	SinaNBACrawl sinaNBACrawl;
	@Autowired
	FormulaCrawl formulaCrawl; 
	public void crawl() {
		// 注册模板
		txKplCrawl.register();
		txfo4Crawl.register();
		dongQiuDiFACrawl.register();
		hupuSoccerCrawl.register();
		soccerCrawl.register();
		txCrawl.register();
		zhibo8Crawl.register();
		hupuCrawl.register();
		goalCrawl.register();
		//dongQiuDICrawl.register();
		neteaseCrawl.register();
		sohuCrawl.register();
		hupuCSLCrawl.register();
		tx2k2Crawl.register();
		renHeCrawl.register();
		iqiyiCrawl.register();
		txLplCrawl.register();
		txOthersCrawl.register();
		top147Crawl.register();
		sinaCrawl.register();
		aiyukeCrawl.register();
		yidianCrawl.register();
		mpSouhuCrawl.register();
		xiguajiCrawl.register();
//		formulaCrawl.register();
		sinaNBACrawl.register();
		// 启动引擎
		startEngine();
	}

	private void startEngine() {
		// 定义线程数量和延迟等待时间
		int thread = 10, interval = 1000;
		GeccoEngine.create()
				// 自定义下载url调度
				.scheduler(schedulerSingle.getInstance())
				// 自定义管道过滤器工厂
				.pipelineFactory(springPipelineFactory)
				// 扫描包路径
				.classpath("com.isport.crawl").thread(thread)
				// 延迟等待事件
				.interval(interval)
				// 非阻塞执行
				.start();
	}
}
