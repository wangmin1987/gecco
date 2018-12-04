package com.isport.crawl.formula;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.geccocrawler.gecco.pipeline.JsonPipeline;
import com.geccocrawler.gecco.redis.RedisStartScheduler;
import com.geccocrawler.gecco.request.HttpGetRequest;
import com.geccocrawler.gecco.request.HttpRequest;
import com.isport.crawl.distribute.TaskService;
import com.isport.service.NewsService;
import com.isport.utils.MongoTemUtil;
import com.isport.utils.RedisUtils;
import com.isport.utils.StringUtils;

/**
 * 抽象列表页定义抽取流程
 * 
 * @author 八斗体育
 *
 */
public abstract class FormulabstractListPipeLine extends JsonPipeline {
	@Autowired
	NewsService newsService;

	// 默认设置：第一次爬取时，默认爬取的最大页数
	private static final int DEFAULT_MAX_PAGE = 2;
	// 终止派生地址 不进一步抓取下一页的标志位
	protected static final String ILLEGAL_URL = "about:to";
	@Autowired
	MongoTemUtil mongoTemUtil;
	private List<HttpRequest> sortRequests = new ArrayList<HttpRequest>();
	private static final Logger LOGGER = LoggerFactory.getLogger(FormulabstractListPipeLine.class);
	@Autowired
	RedisUtils redisUtils;
	public List<HttpRequest> getSortRequests() {
		return sortRequests;
	}

	// redis 主机
	@Value("${spring.redis.host}")
	private String redis_host;
	// redis 端口
	@Value("${spring.redis.port}")
	private String redis_port;

	@Override
	public void process(JSONObject jo) {
		// 分布式任务管理
		TaskService taskService = TaskService.create()
				.scheduler(new RedisStartScheduler(redis_host + ":" + redis_port));
		if (jo != null) {
			String joUrl = "";
			try {
				// 日期格式化
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				// 获取request 对象
				final HttpRequest request = initRequest(jo);
				// 请求地址
				String url = request.getUrl();
				joUrl = url;
				// 请求参数设置
				String strPage = request.getParameter("page"); 
				String totalPage = jo.getString("totalPage");
				// 来源key
				String sourceKey = request.getParameter("source_key");
				int page = StringUtils.isNUll(strPage) ? 1 : Integer.valueOf(strPage);
				@SuppressWarnings("unchecked")
				List<Object> urllist = (List<Object>) getList(jo);
				for (Object obj : urllist) {
					String docUrl = getNewsDocUrl(url, obj);
					// 生成详情请求对象
					if (StringUtils.isNUll(docUrl) || docUrl.equals(ILLEGAL_URL)||!redisUtils.setIfAbsent(docUrl, 1L, 30L, TimeUnit.DAYS)) {
						continue;
					}
					HttpRequest subRequest = getDetailRequest(request, docUrl);
					setRequestParameter(subRequest, obj);
					subRequest.addParameter("index_url", url);
					taskService.insert(subRequest);
				}
				// 派生地址
				if (page < DEFAULT_MAX_PAGE ) {
					// url 拼接
					++page;
					String nextUrl = getNextUrl(url, page);
					if (nextUrl.equals(ILLEGAL_URL)) {
						return;
					}
					HttpRequest nextRequest = getRequest(request, nextUrl);
					nextRequest.addParameter("page", String.valueOf(page));
					taskService.insert(nextRequest);
				}
			} catch (Exception e) {
				e.printStackTrace();
				LOGGER.error(e.getMessage() + "url:" + joUrl);
			}
		}
	}

	// 设置请求参数传递
	protected void setRequestParameter(HttpRequest subRequest, Object obj) {
		JSONObject item = JSONObject.parseObject(obj.toString());
		subRequest.addParameter("docUrlImg", item.getString("docUrlImg"));
	}

	// 初始化HttpRequest 对象
	protected HttpRequest initRequest(JSONObject jo) {
		return HttpGetRequest.fromJson(jo.getJSONObject("request"));
	}

	// 详情 request 生成
	protected HttpRequest getDetailRequest(final HttpRequest request, String docUrl) {
		return request.subRequest(docUrl);
	}

	// 派生 request 生成
	protected HttpRequest getRequest(final HttpRequest request, String nextUrl) {
		return request.subRequest(nextUrl);
	}

	// 获取列表页
	protected abstract Object getList(JSONObject jo) throws Exception;


	// 获取列表一条新闻详情地址
	protected abstract String getNewsDocUrl(String baseUrl, Object obj);

	// 获取派生页地址
	protected abstract String getNextUrl(String url,  int page);

}
