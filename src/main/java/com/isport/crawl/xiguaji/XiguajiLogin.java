package com.isport.crawl.xiguaji;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.geccocrawler.gecco.pipeline.JsonPipeline;
import com.geccocrawler.gecco.request.HttpGetRequest;
import com.geccocrawler.gecco.request.HttpRequest;
import com.isport.crawl.distribute.SchedulerSingle;
import com.isport.crawl.distribute.TaskService;

@Service
public class XiguajiLogin extends JsonPipeline {

	@Autowired
	SchedulerSingle schedulerSingle;

	@Override
	public void process(JSONObject jo) {
		// 分布式任务管理
		TaskService taskService = TaskService.create().scheduler(schedulerSingle.getInstance());
		try {
			HttpRequest request = HttpGetRequest.fromJson(jo.getJSONObject("request"));
			String content = jo.getString("content");
			boolean res = XiguajiService.userLogin(request, content);
			if (res) {
				String redirect_url = request.getParameter("original_url");
				redirect_url = redirect_url.substring(0, redirect_url.indexOf(","));
				taskService.insert(request.subRequest(redirect_url));
			}
		} catch (Exception e) {

		}
	}

}
