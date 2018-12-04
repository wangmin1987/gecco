package com.isport.crawl;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.geccocrawler.gecco.pipeline.JsonPipeline;
import com.geccocrawler.gecco.request.HttpGetRequest;
import com.geccocrawler.gecco.request.HttpRequest;
import com.google.gson.Gson;
import com.isport.bean.NewsInfoBean;
import com.isport.kafka.Producer;
import com.isport.service.NewsParseService;
import com.isport.utils.DateUtils;
import com.isport.utils.MongoTemUtil;
import com.isport.utils.SimilarityUtils;
import com.isport.utils.StringUtils;

/**
 * 抽象详情抽取页 定义抽取流程
 * 
 * @author 八斗体育
 *
 */
public abstract class AbstractDetailPipeLine extends JsonPipeline {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractDetailPipeLine.class);
	// 系统发布时间格式
	public static final SimpleDateFormat DEFAULT_SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm");

	// mongo 操作工具
	@Autowired
	private MongoTemUtil mongoTemUtil;
	// 新闻解析服务
	@Autowired
	protected NewsParseService newsParseService;
	// 文本去重工具
	@Autowired
	private SimilarityUtils similarityUtils;
	// kafka 操作
	@Autowired
	private Producer producer;

	@Override
	public void process(JSONObject jo) {
		// infoBean 对象
		NewsInfoBean newsInfoBean = new NewsInfoBean();
		HttpRequest request = HttpGetRequest.fromJson(jo.getJSONObject("request"));
		String url = request.getUrl(), parent_url = "";
		try {
			newsInfoBean.setUrl(url);
			// infoBean属性设置：网页内容
			newsInfoBean.setHtml("<html>" + jo.getString("content") + "</html>");
			// infoBean属性设置：html网页文本解析
			setParseParameter(newsInfoBean, jo);
			// infoBean属性设置：request参数（在html网页文本解析之后进行，已赋值的属性跳过）
			setRequestParameters(request, newsInfoBean);
			parent_url = newsInfoBean.getIndex_url();

			// 解析新闻内容
			boolean parsed = parseContent(newsInfoBean);
			// 解析状态设置：1-解析成功 2-解析失败
			int disposeState = parsed ? 1 : 2;
			newsInfoBean.setDispose_state(disposeState);

			// 正文相似性去重
			String id = StringUtils.md5(url);
			String mainBody = newsInfoBean.getContent();
			String similar = similarityUtils.distinguish(id, mainBody);
			if (!similar.equals("true")) {
				LOGGER.info("查重失败: " + similar);
				return;
			}

			// 发布日期类型转换
			translatePubDate(newsInfoBean);
			// 下载图片地址处理
			processImage(newsInfoBean);
			// infoBean属性设置：constant变量设置
			setConstant(newsInfoBean);
			// infoBean属性设置：其他字段
			newsInfoBean.setId(id);
			newsInfoBean.setCreate_date(DateUtils.getStrYYYYMMDDHHmmss(new Date()));
			newsInfoBean.setData_type(0); // 数据类型：0-资讯 1-快讯 2-视频
			// 装配完bean对象处理
			newsInfoBeanPackageAfter(newsInfoBean);
			// 验证过滤非法数据
			boolean validated = filter(newsInfoBean);
//			LOGGER.info(newsInfoBean.toString());
			mongoTemUtil.save(newsInfoBean);
			if (validated) {
				producer.send(id, new Gson().toJson(newsInfoBean));
				LOGGER.info("Mongo,kafka写入成功：" + id);
			}
		} catch (Exception e) {
			LOGGER.error("index_url:" + parent_url + ",url:" + url + ",详情抽取错误:" + StringUtils.getStackTrace(e));
		}
	}

	protected boolean filter(NewsInfoBean newsInfoBean) {
		String title = newsInfoBean.getTitle();
		int disposeState = newsInfoBean.getDispose_state();
		if (StringUtils.isNUll(title) || disposeState != 1) {
			return false;
		}
		return true;
	}

	// request 参数设置
	protected void setRequestParameters(HttpRequest request, NewsInfoBean newsInfoBean) {
		newsInfoBean.setIndex_url(request.getParameter("index_url"));
		newsInfoBean.setAuthor_id(request.getParameter("author_id"));
		newsInfoBean.setChannel_id(request.getParameter("channel_id"));
		newsInfoBean.setTag(request.getParameter("tag"));
		// 发布时间赋值
		if (StringUtils.isNUll(newsInfoBean.getPub_date())) {
			newsInfoBean.setPub_date(request.getParameter("pub_date"));
		}
	}

	// 装配完bean后对象的后续处理
	protected void newsInfoBeanPackageAfter(final NewsInfoBean newsInfoBean) throws Exception {

	}

	// 图片地址处理
	protected void processImage(final NewsInfoBean newsInfoBean) {

	}

	// 日期格式统一（16位）：yyyy-MM-dd HH:mm
	protected void translatePubDate(final NewsInfoBean newsInfoBean) {
		String pubDate = newsInfoBean.getPub_date();
		if (pubDate.length() > 16) {
			pubDate = pubDate.substring(0, 16);
			newsInfoBean.setPub_date(pubDate);
		}
	}

	/**
	 * 设置新闻内容抽取字段
	 * 
	 * @param newsInfoBean
	 * @param jo
	 * @throws Exception
	 */
	protected abstract void setParseParameter(final NewsInfoBean newsInfoBean, final JSONObject jo) throws Exception;

	/**
	 * 常量设置
	 * 
	 * @param newsInfoBean
	 */
	protected abstract void setConstant(final NewsInfoBean newsInfoBean);

	/**
	 * 获取body体的 jsoup表达式
	 * 
	 * @return
	 */
	protected abstract String getBodyExpession();

	/**
	 * 解析内容
	 * 
	 * @param newsInfoBean
	 * @return
	 * @throws Exception
	 */
	protected boolean parseContent(final NewsInfoBean newsInfoBean) throws Exception {
		String url = newsInfoBean.getUrl(), parent_url = newsInfoBean.getIndex_url();
		try {
			String html = newsInfoBean.getHtml();
			// 解析html
			Document doc = Jsoup.parse(html);
			// 获取body体标签
			Elements bodyDivs = doc.select(getBodyExpession());
			if (bodyDivs.size() == 0) {
				throw new Exception("找不到正文区域，jsoup表达式有误：" + getBodyExpession());
			}
			Element bodyDiv = bodyDivs.get(0);
			// 清洗目标内容区域内的无关数据
			parseCleanData(bodyDiv);
			// 修改内容区IMG标签的地址
			newsParseService.uploadImg(bodyDiv);
			// 删除A标签保留文本
			String cleanContent = Jsoup.clean(bodyDiv.html(), Whitelist.relaxed().removeTags("a"));
			// infoBean属性设置：正文内容
			newsInfoBean.setContent(cleanContent);
			// infoBean属性设置：正文摘要
			newsInfoBean.setSummary(newsParseService.getSummary(cleanContent));
			// infoBean属性设置：资讯配图
			newsInfoBean.setTitle_imgs(newsParseService.getImage(bodyDiv));
			// 资讯非法内容过滤：1.正文内容过短；2.无配图的视频资讯
			newsParseService.filter(cleanContent);
			return true;
		} catch (Exception e) {
			String message = e.getMessage();
			newsInfoBean.setParse_error(message.length() < 255 ? message : message.substring(0, 254));
			LOGGER.error("parent_url:" + parent_url + ",url:" + url + " 内容解析错误：" + StringUtils.getStackTrace(e));
		}
		return false;
	}

	/**
	 * 清洗目标内容区域内的无关数据：例如广告、视频等HTML元素
	 * 
	 * @author wangyuanxi
	 * @param element
	 */
	protected void parseCleanData(Element element) {

	}

}
