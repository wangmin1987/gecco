package com.isport.utils;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.isport.Constants;

@Service
public class InitParamService {

	private static final Logger LOGGER = LoggerFactory.getLogger(InitParamService.class);

	public static Map<String, String> neteaseSportItemMap = new HashMap<String, String>();

	/**
	 * 网易新闻：板块映射MAP初始化
	 * 
	 * @author wangyuanxi
	 */
	@PostConstruct
	public void newsNeteaseLable() {
		LOGGER.info("init param start ...");
		neteaseSportItemMap.put("NBA", Constants.NEWS_BLOCK_BASKETBALL.value);
		neteaseSportItemMap.put("CBA", Constants.NEWS_BLOCK_BASKETBALL.value);
		neteaseSportItemMap.put("国际足球", Constants.NEWS_BLOCK_FOOTBALL.value);
		neteaseSportItemMap.put("国内足球", Constants.NEWS_BLOCK_FOOTBALL.value);
		neteaseSportItemMap.put("中国足球", Constants.NEWS_BLOCK_FOOTBALL.value);
		neteaseSportItemMap.put("综合体育", "");
		LOGGER.info("init param end ...");
	}

}