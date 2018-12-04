package com.isport.crawl.video;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.isport.bean.NewsInfoBean;
import com.isport.utils.RedisUtils;
import com.isport.utils.SimilarityUtils;
import com.isport.utils.StringUtils;

/**
 * 视频重复判断
 * 
 * @return
 */
@Service
public class Duplicate {

	@Autowired
	RedisUtils redisUtils;

	// 文本去重工具
	@Autowired
	private SimilarityUtils similarityUtils;

	public boolean isContentDuplicate(NewsInfoBean newsInfoBean) {
		String docUrl = newsInfoBean.getUrl();
		String mainBody = newsInfoBean.getContent();
		String id = StringUtils.md5(docUrl);
		// content 是否重复
		String similar = similarityUtils.distinguish(id, mainBody);
		boolean duplicate= similar.equals("true");
		if(duplicate) {
			System.out.println("content duplicate:"+duplicate);
		}
		return false;
	}

	public boolean isUrlDuplicate(String docUrl) { 
		boolean duplicate= !redisUtils.setIfAbsent(docUrl, 1L, 30L, TimeUnit.DAYS);
		if(duplicate) {
			System.out.println("url dupliate:"+docUrl);
		}
		return duplicate;
	}
}
