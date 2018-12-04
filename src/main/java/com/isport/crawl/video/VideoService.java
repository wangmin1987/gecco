package com.isport.crawl.video;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.isport.crawl.cctv.CctvvideoCrawl;
import com.isport.crawl.dianjinghu.CSGOvideoCrawl;
import com.isport.crawl.dianjinghu.LOLvideoCrawl;
import com.isport.crawl.dianjinghu.PUBGvideoCrawl;
import com.isport.crawl.dianjinghu.PVPvideoCrawl;
import com.isport.crawl.tengxun.video.GBCrawl;
import com.isport.crawl.tengxun.video.GBCrawlv2;
import com.isport.crawl.tengxun.video.UefavideoCrawl;
import com.isport.crawl.tengxun.video.UefavideoCrawl2;

/**
 * 视频抓取服务
 * 
 * @author 八斗体育
 *
 */
@Service
public class VideoService {

	@Autowired
	CctvvideoCrawl cctvvideoCrawl;

	@Autowired
	com.isport.crawl.aiyuke.video.PyVideoCrawl pyVideoCrawl;

	@Autowired
	com.isport.crawl.tengxun.video.ZcvideoCrawlv2 zcvideoCrawlv2;

	@Autowired
	GBCrawlv2 gBCrawlv2;

	@Autowired
	com.isport.crawl.tengxun.video.FavideoCrawlv2 favideoCrawlv2;
	@Autowired
	com.isport.crawl.tengxun.video.NbavideoCrawl nbavideoCrawl;

	@Autowired
	UefavideoCrawl2 uefavideoCrawl2;
	
	@Autowired
	LOLvideoCrawl lOLvideoCrawl;
	
	@Autowired
	PVPvideoCrawl pVPvideoCrawl;
	 
	@Autowired
	PUBGvideoCrawl pUBGvideoCrawl;
	
	@Autowired
	CSGOvideoCrawl cSGOvideoCrawl;
	
	public void crawllq() {
		try {
			nbavideoCrawl.crawl();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void crawlzq() {
		try {
			zcvideoCrawlv2.crawl();
		} catch (Exception e) {
			e.printStackTrace();
		}
		 
		try {
			uefavideoCrawl2.crawl();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			favideoCrawlv2.crawl();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			gBCrawlv2.crawl();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public void crawldj() {
		lOLvideoCrawl.crawl("");
		pVPvideoCrawl.crawl("");
		pUBGvideoCrawl.crawl("");
		cSGOvideoCrawl.crawl("");
	}

	public void crawlcctv() {
		try {
			cctvvideoCrawl.crawl("http://sports.cctv.com/tennis/videos/");
		} catch (Exception e) {
			e.printStackTrace();
		}
//		try {
//			pyVideoCrawl.crawl("http://www.aiyuke.com/video_list/1------------.html");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		try {
//			pyVideoCrawl.crawl("http://www.aiyuke.com/video_list/2------------.html");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

	}
}
