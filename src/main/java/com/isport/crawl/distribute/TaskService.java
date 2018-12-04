package com.isport.crawl.distribute;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream; 
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.eclipse.jetty.util.security.Credential.MD5;

import com.alibaba.fastjson.JSON;
import com.geccocrawler.gecco.request.HttpRequest;
import com.geccocrawler.gecco.request.StartRequestList;
import com.geccocrawler.gecco.scheduler.Scheduler;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import com.isport.utils.StringUtils;

public class TaskService extends Thread {
	// 任务调度器
	private Scheduler scheduler;
	private static Log log = LogFactory.getLog(TaskService.class);

	/**
	 * 动态配置规则不能使用该方法构造
	 * 
	 * @return
	 */
	public static TaskService create() {
		TaskService taskService = new TaskService();
		return taskService;
	}

	/**
	 * 设置调度器
	 * 
	 * @param scheduler
	 * @return
	 */
	public TaskService scheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
		return this;
	}

	/**
	 * 插入任务
	 * 
	 * @param request
	 */
	public void insert(HttpRequest request) {
		scheduler.into(request);
	}

	/**
	 * 初始启动
	 */
	public void start() {
		run();
	}

	@Override
	public void run() {
		startsJson();
	}

	private TaskService startsJson() {
		try {
//			URL url = Resources.getResource("starts.json");
			File file = new File("starts.json");
			if (file.exists()) {
				List<String> jsonLine = Files.readLines(file, Charset.forName("UTF-8"));
				String json = "";
				for (String string : jsonLine) {
					json += string;
				}
				// String json = Files.newReader(file, charset).toString(file,
				// Charset.forName("UTF-8"));
				List<StartRequestList> list = JSON.parseArray(json, StartRequestList.class);
				for (StartRequestList start : list) {
					try {
						checkStartRequest(start);
						String cookie_file = start.getCustom_param().get("cookie");
						if (cookie_file != null && !cookie_file.equals("")) {
							start.setCookies(readCookieStore(cookie_file));
						}
						insert(start.toRequest());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		} catch (IllegalArgumentException ex) {
			log.info("starts.json not found");
		} catch (IOException ioex) {
			log.error(ioex);
		} catch (RuntimeException e) {
			log.error(e.getMessage());
		}
		return this;
	}

	static Set<String> set = new HashSet<String>();

	// 校验json文件
	private void checkStartRequest(StartRequestList request) throws RuntimeException {
		String url = request.getUrl();
		Map<String, String> params = request.getCustom_param();
		if (StringUtils.isNUll(url) || !set.add("url=" + url)) {
//			throw new RuntimeException("starts.json error:url is empty or repeated! url=" + url);
		}
		// source_key -> md5 url
		String sourceKey = getSoureKey(request);
		params.put("source_key", MD5.digest(sourceKey));
		if (StringUtils.isNUll(params.get("source_key")) || set.add("source_key=" + params.get("source_key"))) {
//			throw new RuntimeException("starts.json error: source_key is empty or repeated! url=" + url);
		}
		if (StringUtils.isNUll(params.get("author_id"))) {
			throw new RuntimeException("starts.json error: author_id is empty! url=" + url);
		}
		if (StringUtils.isNUll(params.get("channel_id"))) {
//			throw new RuntimeException("starts.json error: channel_id is empty! url=" + url);
		}
		if (StringUtils.isNUll(params.get("tag"))) {
			throw new RuntimeException("starts.json error: tag is empty! url=" + url);
		}
	}

	private String getSoureKey(StartRequestList request) {
		StringBuilder souceKeyBuilder = new StringBuilder();
		souceKeyBuilder.append(request.getUrl());
		souceKeyBuilder.append(request.getCharset()); 
		Iterator<String> keyIterator = request.getCookies().keySet().iterator();
		while (keyIterator.hasNext()) {
			String key = keyIterator.next();
			String value = request.getCookies().get(key);
			souceKeyBuilder.append(key);
			souceKeyBuilder.append(value);
		}
		keyIterator = request.getCustom_param().keySet().iterator();
		while (keyIterator.hasNext()) {
			String key = keyIterator.next();
			String value = request.getCustom_param().get(key);
			souceKeyBuilder.append(key);
			souceKeyBuilder.append(value);
		}
		keyIterator = request.getHeaders().keySet().iterator();
		while (keyIterator.hasNext()) {
			String key = keyIterator.next();
			String value = request.getHeaders().get(key);
			souceKeyBuilder.append(key);
			souceKeyBuilder.append(value);
		}
		
		keyIterator = request.getPosts().keySet().iterator();
		while (keyIterator.hasNext()) {
			String key = keyIterator.next();
			String value = request.getPosts().get(key);
			souceKeyBuilder.append(key);
			souceKeyBuilder.append(value);
		}
		return souceKeyBuilder.toString();
	}

	public static void main(String[] args) {
		System.out.println(MD5.digest("123"));
	}

	// 读取Cookie的序列化文件，读取后可以直接使用
	private Map<String, String> readCookieStore(String file) throws IOException {
		Map<String, String> cookies = new HashMap<String, String>();
		try {
			FileInputStream fs = new FileInputStream(file);
			ObjectInputStream ois = new ObjectInputStream(fs);
			CookieStore cookieStore = (CookieStore) ois.readObject();
			ois.close();
			for (Cookie cookie : cookieStore.getCookies()) {
				cookies.put(cookie.getName(), cookie.getValue());
			}
		} catch (ClassNotFoundException e) {
			log.error("readCookieStore: " + e.getMessage());
		} catch (FileNotFoundException e) {

		}
		return cookies;
	}
}
