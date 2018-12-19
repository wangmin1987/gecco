package com.isport.crawl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.geccocrawler.gecco.pipeline.JsonPipeline;
import com.geccocrawler.gecco.request.HttpGetRequest;
import com.geccocrawler.gecco.request.HttpRequest;
import com.isport.crawl.distribute.SchedulerSingle;
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
public abstract class AbstractListPipeLine extends JsonPipeline {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractListPipeLine.class);
	// 默认设置：第一次爬取时，默认爬取的最大页数
	private static final int DEFAULT_MAX_PAGE = 2;
	// 终止派生地址 不进一步抓取下一页的标志位
	protected static final String ILLEGAL_URL = "about:to";

	protected static final String URL_KEY = "Gecco-StartScheduler-Redis-UrlKey";
	// 系统发布时间格式
	public static final SimpleDateFormat DEFAULT_SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	// 需要采集的详情页request集合
	@Deprecated
	public List<HttpRequest> sortRequests = new ArrayList<HttpRequest>();

	@Autowired
	NewsService newsService;

	@Autowired
	MongoTemUtil mongoTemUtil;

	@Autowired
	SchedulerSingle schedulerSingle;

	@Autowired
	RedisUtils redisUtils;

	@Deprecated
	public List<HttpRequest> getSortRequests() {
		return sortRequests;
	}

	@Override
	public void process(JSONObject jo) {
		// 分布式任务管理
		TaskService taskService = TaskService.create().scheduler(schedulerSingle.getInstance());
		if (jo != null) {
			String url = "";
			try {
				// 获取request 对象
				final HttpRequest request = initRequest(jo);
				// 请求地址
				url = request.getUrl();
				// 请求参数设置
				String strPage = request.getParameter("customer_page");
				int page = StringUtils.isNUll(strPage) ? 1 : Integer.valueOf(strPage.replaceAll("_", ""));
				// 来源key
				String sourceKey = request.getParameter("source_key");
				// 抓取时间设置：上次抓取时间 ,本次抓取时间
				long lastUpdateTime = 0, crawlTime = 0;
				if (page == 1) {
					crawlTime = new Date().getTime();
					String strLastUpdateTime = newsService.getLastUpdateTime(sourceKey);
					lastUpdateTime = StringUtils.isNUll(strLastUpdateTime) ? 0
							: DEFAULT_SDF.parse(strLastUpdateTime).getTime();
				} else {
					crawlTime = Long.valueOf(request.getParameter("crawlTime"));
					lastUpdateTime = Long.valueOf(request.getParameter("lastUpdateTime"));
				}
				@SuppressWarnings("unchecked")
				List<Object> list = (List<Object>) getList(jo);
				for (Object obj : list) {
					try {
						long newsTime = getNewsTime(obj);
						// 非第一次抓去并且新闻时间小于最近更新抓取时间
						if (lastUpdateTime > 0 && newsTime < lastUpdateTime) {
							if (page == 1) {
								// 第一页继续比对
								continue;
							} else {
								// 非第一页不在抓取详情和派生页
								newsService.setLastUpdateTime(sourceKey, DEFAULT_SDF.format(crawlTime));
								LOGGER.info("抓取提前结束，无更多新资讯  url=" + url);
								return;
							}
						}
						String docUrl = getNewsDocUrl(url, obj);

						if (StringUtils.isNUll(docUrl) || docUrl.equals(ILLEGAL_URL)||!redisUtils.setIfAbsent(docUrl, 1L, 30L, TimeUnit.DAYS)) {
							continue;
						}
						// 生成详情请求对象
						HttpRequest subRequest = getDetailRequest(request, docUrl);
						setRequestParameter(subRequest, obj);
						subRequest.addParameter("index_url", url);
						// sortRequests.add(subRequest);
						taskService.insert(subRequest);
					} catch (Exception e) {
						LOGGER.error(e.getMessage() + " obj=" + obj.toString());
						continue;
					}
				}
				// 派生地址
				if (page++ <= DEFAULT_MAX_PAGE) {
					// url 拼接
					String nextUrl = getNextUrl(url, jo.getString("nextUrl"), page);
					if (!StringUtils.isNUll(nextUrl) && !nextUrl.equals(ILLEGAL_URL)) {
						// request 派生对象
						HttpRequest nextRequest = getRequest(request, nextUrl);
						nextRequest.addParameter("customer_page", String.valueOf(page));
						nextRequest.addParameter("crawlTime", String.valueOf(crawlTime));
						nextRequest.addParameter("lastUpdateTime", String.valueOf(lastUpdateTime));
						// DeriveSchedulerContext.into(nextRequest);
						taskService.insert(nextRequest);
						return;
					}
				}
				newsService.setLastUpdateTime(sourceKey, DEFAULT_SDF.format(crawlTime));
				LOGGER.info("抓取正常结束  url=" + url);
			} catch (Exception e) {
				LOGGER.error(StringUtils.getStackTrace(e) + " url=" + url);
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
	protected abstract long getNewsTime(Object obj) throws Exception;

	// 获取列表一条新闻详情地址
	protected abstract String getNewsDocUrl(String baseUrl, Object obj);

	// 获取派生页地址
	protected abstract String getNextUrl(String url, String nextUrl, int page);

}