package com.isport.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.aliyun.oss.OSSClient;

@Configuration
public class OssConfig {
	private static final Logger LOGGER = LoggerFactory.getLogger(OssConfig.class);

	/**
	 * Bucket所在数据中心的访问域名，此处需要填写外网Endpoint
	 */
	@Value("${oss.endpoint}")
	private String endpoint;

	/**
	 * 访问身份验证机制中的用户标识
	 */
	@Value("${oss.accessKeyId}")
	private String accessKeyId;

	/**
	 * 访问身份验证机制中的加密密钥
	 */
	@Value("${oss.accessKeySecret}")
	private String accessKeySecret;

	@Bean("oss")
	public OSSClient init() {
		LOGGER.info("initializing...");
		OSSClient ossClient = null;
		try {
			// 创建OSSClient实例
			ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
		} catch (Exception e) {
			LOGGER.error("oss OSSClient create error!!!", e);
		}
		return ossClient;
	}
}
