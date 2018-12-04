package com.isport.crawl.tengxunNBA;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.alibaba.fastjson.JSONObject;
import com.geccocrawler.gecco.pipeline.JsonPipeline;
import com.geccocrawler.gecco.redis.RedisStartScheduler;
import com.geccocrawler.gecco.request.HttpGetRequest;
import com.geccocrawler.gecco.request.HttpRequest;
import com.isport.crawl.distribute.TaskService;
import com.isport.service.NewsService;
import com.isport.utils.MongoTemUtil;
import com.isport.utils.StringUtils;

/**
 * 抽象列表页定义抽取流程
 * 
 * @author 八斗体育
 *
 */
public abstract class TengxunAbstractListPipeLine extends JsonPipeline {
	@Autowired
	NewsService newsService;

	// 默认设置：第一次爬取时，默认爬取的最大页数
	private static final int DEFAULT_MAX_PAGE = 2;
	// 终止派生地址 不进一步抓取下一页的标志位
	protected static final String ILLEGAL_URL = "about:to";
	@Autowired
	MongoTemUtil mongoTemUtil;
	private List<HttpRequest> sortRequests = new ArrayList<HttpRequest>();
	private static final Logger LOGGER = LoggerFactory.getLogger(TengxunAbstractListPipeLine.class);

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
				// 来源key
				String sourceKey = request.getParameter("source_key");
				// 上次数据库更新时间 ,本次抓取事件
				long lastUpdateTime = 0, crawlTime = 0;
				int page = StringUtils.isNUll(strPage) ? 1 : Integer.valueOf(strPage);
				// 抓取时间设置
				if (page == 1) {
					// 当前时间
					crawlTime = new Date().getTime();
//					String strLastUpdateTime = newsService.getLastUpdateTime(sourceKey);
					String strLastUpdateTime = "";
					lastUpdateTime = StringUtils.isNUll(strLastUpdateTime) ? 0 : sdf.parse(strLastUpdateTime).getTime();
				} else {
					crawlTime = Long.valueOf(request.getParameter("crawlTime"));
					lastUpdateTime = Long.valueOf(request.getParameter("lastUpdateTime"));
				}
				@SuppressWarnings("unchecked")
				List<Map<String, String>> list = (List<Map<String, String>>) getList(jo);
				if(list !=null) {
					for (Map<String, String>  obj : list) {
						try {
							long newsTime = getNewsTime(obj);
							// 非第一次抓去并且新闻时间小于最近更新抓取时间
							if (lastUpdateTime > 0 && newsTime < lastUpdateTime) {
								if (page == 1) {
									// 第一页继续比对
									continue;
								} else {
									// 非第一页不在抓取详情和派生页
									newsService.setLastUpdateTime(sourceKey, sdf.format(crawlTime));
									LOGGER.info("抓取提前结束，无法抓到更多新的新闻");
									return;
								}
							}
							String docUrl = getNewsDocUrl(url, obj);
							if (docUrl.equals(ILLEGAL_URL)) {
								continue;
							}
//						LOGGER.info(docUrl);
							// 生成详情请求对象
							HttpRequest subRequest = getDetailRequest(request, docUrl);
							setRequestParameter(subRequest, obj);
							subRequest.addParameter("index_url", url);
//						sortRequests.add(subRequest);
							taskService.insert(subRequest);
						} catch (Exception e) {
							LOGGER.error(e.getMessage() + "url:" + url);
							continue;
						}
					}
				}
				// 派生地址
				if (page < DEFAULT_MAX_PAGE) {
					// url 拼接
					++page;
					String nextUrl = getNextUrl(url, page);
					if (nextUrl.equals(ILLEGAL_URL)) {
						return;
					}
//					LOGGER.info("nextUrl:" + nextUrl);
					// request 派生对象
//					HttpRequest nextRequest = request.subRequest(nextUrl);
					HttpRequest nextRequest = getRequest(request, nextUrl);
					nextRequest.addParameter("page", String.valueOf(page));
					nextRequest.addParameter("crawlTime", String.valueOf(crawlTime));
					nextRequest.addParameter("lastUpdateTime", String.valueOf(lastUpdateTime));
//					DeriveSchedulerContext.into(nextRequest);
					taskService.insert(nextRequest);
				}
				newsService.setLastUpdateTime(sourceKey, sdf.format(crawlTime));
			} catch (Exception e) {
				e.printStackTrace();
				LOGGER.error(e.getMessage() + "url:" + joUrl);
			}
		}
	}

	// 设置请求参数传递
	protected void setRequestParameter(HttpRequest subRequest, Object obj) {

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

	// 获取列表一条新闻时间
	protected abstract long getNewsTime(Map<String, String> obj) throws Exception;

	// 获取列表一条新闻详情地址
	protected abstract String getNewsDocUrl(String baseUrl, Map<String, String> obj);

	// 获取派生页地址
	protected abstract String getNextUrl(String url, int page);

}
