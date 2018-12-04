package com.isport.crawl.distribute;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.geccocrawler.gecco.redis.RedisStartScheduler;
import com.geccocrawler.gecco.scheduler.Scheduler;
/**
 * redis 链接管理
 * @author 八斗体育
 *
 */
@Service
public class SchedulerSingle {
	// redis 主机
	@Value("${spring.redis.host}")
	private String redis_host;
	// redis 端口
	@Value("${spring.redis.port}")
	private String redis_port;
	private static Scheduler scheduler = null;

	public Scheduler getInstance() {
		if (scheduler == null) {
			scheduler = new RedisStartScheduler("redis://"+redis_host + ":" + redis_port);
		}
		return scheduler;
	}
}
