package com.isport.crawl.formula;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import com.isport.utils.OssUtils;
import com.isport.utils.SimilarityUtils;
import com.isport.utils.StringUtils;

/**
 * 抽象详情抽取页 定义抽取流程
 * 
 * @author 八斗体育
 *
 */
public abstract class FormulabstractDetailPipeLine extends JsonPipeline {
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
	@Autowired
	OssUtils ossUtils;
	private static final Logger LOGGER = LoggerFactory.getLogger(FormulabstractDetailPipeLine.class);

	@Override
	public void process(JSONObject jo) {
		// infoBean 对象
		NewsInfoBean newsInfoBean = new NewsInfoBean();
		HttpRequest request = HttpGetRequest.fromJson(jo.getJSONObject("request"));
		String url = request.getUrl(), parent_url = "";
		try {
			newsInfoBean.setUrl(url);

			// html 文本解析 变量设置
			setParseParameter(newsInfoBean, jo); 
			// request 参数设置
			setRequestParameters(request,newsInfoBean); 
			parent_url = newsInfoBean.getIndex_url();
			String html = jo.getString("content");
			String vidioContent =html.substring(html.indexOf("player_a",html.indexOf("player_a")+1),html.length());
			vidioContent =vidioContent.substring(vidioContent.indexOf("\"")+1,vidioContent.indexOf("\"",vidioContent.indexOf("\"")+1));

			// 正文相似性去重
			String id = StringUtils.md5(url);
			String mainBody = newsInfoBean.getTitle();
			String similar = similarityUtils.distinguish(id, mainBody);
			if (!similar.equals("true")) {
				LOGGER.info("查重失败: " + similar);
				return;
			}
			//上传视频
			String uploadVideo = ossUtils.uploadVideo(vidioContent);
			if(!StringUtils.isNUll(uploadVideo)) {
				newsInfoBean.setVideo_url(uploadVideo);
				newsInfoBean.setDispose_state(1);
				newsInfoBean.setId(id);
				
				// constant 变量设置
				setConstant(newsInfoBean); 
				// 设置其他字段
				newsInfoBean.setCreate_date(DateUtils.getStrYYYYMMDDHHmmss(new Date()));
				// 数据类型咨询
				newsInfoBean.setData_type(0);
				// 发布日期类型转换
				translatePubDate(newsInfoBean);
				// 下载图片地址处理
				processImage(newsInfoBean); 
				// 装配完bean对象处理
				newsInfoBeanPackageAfter(newsInfoBean);
//			LOGGER.info(newsInfoBean.toString());
				mongoTemUtil.save(newsInfoBean);
				producer.send(id, new Gson().toJson(newsInfoBean));
			}
			// 视频内容内容
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("index_url:" + parent_url + ",url:" + url + "," + this.getClass().getName() + ",详情抽取错误:"
					+ e.getMessage());
		}
	}
 
	//request 参数设置
	protected void setRequestParameters(HttpRequest request, NewsInfoBean newsInfoBean) {
		String indexUrl = request.getParameter("index_url"); 
		String tag = request.getParameter("tag");
		String authorId = request.getParameter("author_id");
		String channelId = request.getParameter("channel_id"); 
		String docUrlImg = request.getParameter("docUrlImg"); 
		newsInfoBean.setIndex_url(indexUrl);
		newsInfoBean.setTag(tag);
		newsInfoBean.setAuthor_id(authorId);
		newsInfoBean.setChannel_id(channelId);
		List<String> titleImg =new ArrayList<>();
		//上传照片
		String uploadImg = ossUtils.uploadImage(docUrlImg);
		titleImg.add(uploadImg);
		newsInfoBean.setTitle_imgs(titleImg);
		}


	// 装配完bean后对象的后续处理
	protected void newsInfoBeanPackageAfter(final NewsInfoBean newsInfoBean) throws Exception {

	}

	// 图片地址处理
	protected void processImage(final NewsInfoBean newsInfoBean) {

	}

	// 日期格式统一
	protected void translatePubDate(final NewsInfoBean newsInfoBean) {
		String pubDate = newsInfoBean.getPub_date();
		if (pubDate.length() >= 19) {
			final int cronIndex = pubDate.lastIndexOf(":");
			pubDate = pubDate.substring(0, cronIndex);
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
//		String url = newsInfoBean.getUrl(),parent_url =newsInfoBean.getIndex_url();
//		try {
//			String html = newsInfoBean.getHtml(); 
//			// 解析html
//			Document doc = Jsoup.parse(html);
//			// 获取body体标签
//			Elements bodyDivs = doc.select(getBodyExpession());
//			Element bodyDiv = bodyDivs.get(0);
//			// 修改内容区IMG标签的地址
//			newsParseService.uploadImg(bodyDiv);
//			// 删除A标签保留文本
//			String cleanContent = Jsoup.clean(bodyDiv.html(), Whitelist.relaxed().removeTags("a"));
//			// 清洁内容
//			newsInfoBean.setContent(cleanContent);
//			// 内容摘要
//			newsInfoBean.setSummary(newsParseService.getSummary(cleanContent));
//			// 咨询配图
//			newsInfoBean.setTitle_imgs(newsParseService.getImage(bodyDiv));
//			// 咨询非法内容过滤
//			newsParseService.filter(cleanContent);
			return true;
//		} catch (Exception e) {
//			e.printStackTrace();
//			LOGGER.error("parent_url:"+parent_url+",url:" + url + ",解析内容错误:" + e.getMessage());
//		}
//		return false;
	}
}
