package com.isport.crawl.entrance;
 

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.isport.crawl.distribute.CrawlService;
import com.isport.crawl.distribute.SchedulerSingle; 
import com.isport.crawl.video.VideoService;

@Service
public class CrawlEntrance {
	@Autowired
	CrawlService crawlService;

	@Autowired
	SchedulerSingle schedulerSingle;

	@Autowired
	VideoService videoService;
//
//	@Async
//	//@Scheduled(fixedDelay = 1000 * 60 * 60*300)
//	@PostConstruct
//	public void crawlServiceEntrance() {
//		System.out.println("crawlService CrawlEntrance....");
//		crawlService.crawl();
//	}
//	
//	@Async
//	@Scheduled(fixedDelay = 1000 * 60*30)
//	public void taskServiceCrawlEntrance() {
//		System.out.println("TaskService.create()....");
//		TaskService.create()
//		.scheduler(schedulerSingle.getInstance())
//		.start();
//	}
	
	
	@Async
	@Scheduled(fixedDelay = 1000 * 60 * 60 * 300)
//	@PostConstruct
	public void cctvcrawlServiceEntrance() {
		System.out.println("cctv crawlService CrawlEntrance....");
//		videoService.crawlzq();
		videoService.crawlcctv();
	}

//	@Async
//	@Scheduled(fixedDelay = 1000 * 60 * 60 * 300)
////	@PostConstruct
//	public void zqcrawlServiceEntrance() {
//		System.out.println("zq crawlService CrawlEntrance....");
//		videoService.crawlzq();
//	}
//
//	@Async
//	@Scheduled(fixedDelay = 1000 * 60 * 60 * 300)
////	@PostConstruct
//	public void lqcrawlServiceEntrance() {
//		System.out.println("lq crawlService CrawlEntrance....");
//		videoService.crawllq();
//	}

//	@Async
//	@Scheduled(fixedDelay = 1000 * 60 * 60*300)
////	@PostConstruct
//	public void djcrawlServiceEntrance() {
//		System.out.println("dj crawlService CrawlEntrance....");
//		videoService.crawldj();
//	}
}
