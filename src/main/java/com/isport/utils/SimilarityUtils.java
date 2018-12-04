package com.isport.utils;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.isport.analyzer.Simlar;
import com.isport.analyzer.Train;
import com.isport.bean.MessageBean;

@Service
public class SimilarityUtils {
	@Autowired
	Simlar simlar;
	@Autowired
	Train train;
	@Value("${similarity.url}")
	private String similarityUrl;
	/**
	 * 调用文本查重服务
	 * 
	 * @author wangyuanxi
	 * @param id
	 * @param content
	 * @return true-查重通过  其它-查重失败（返回已存在的资讯ID）
	 */
	public String distinguish(String id, String content) {
		try {
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("id", id);
			paramMap.put("content", content);
			String json = HttpClientUtil.doPost(similarityUrl,paramMap);
			MessageBean message = new Gson().fromJson(json, MessageBean.class);
			if (message.getCode() == 200) {
				return message.getData().getSimilar_result().equals("true") ? "true"
						: message.getData().getSimilar_id();
			}
		} catch (Exception e) {
			System.out.println(StringUtils.getStackTrace(e));
		}
		return "false";
	}
}
