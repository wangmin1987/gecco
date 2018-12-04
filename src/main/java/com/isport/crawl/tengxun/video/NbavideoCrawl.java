package com.isport.crawl.tengxun.video;

import java.util.ArrayList;
import java.util.Date;
import java.util.List; 

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.isport.bean.NewsInfoBean;
import com.isport.crawl.video.Duplicate;
import com.isport.kafka.Producer;
import com.isport.utils.CrawlDateUtil;
import com.isport.utils.DateUtils;
import com.isport.utils.MongoTemUtil;
import com.isport.utils.OssUtils;
import com.isport.utils.StringUtils;

/**
 * 腾讯 NBA 抓取
 * 
 * @author 八斗体育
 *
 */
@Service
public class NbavideoCrawl extends AbstractVideoCrawl {
	@Autowired
	protected Duplicate duplicate;
	// 请求列表二级队列
	private List<String> secLists = new ArrayList<String>();

	public void crawl() {
		WebDriver driver = null;
		String index_url = "http://sports.qq.com/nbavideo/query/";
		try {
			// 加载adblock插件
			driver = getChromeDriver(ADBLOCK);
			driver.get(index_url);// 打开指定的网站

			closeCurWindow(driver);
			// 获取所有的视频列表
			List<WebElement> divs = driver.findElements(By.cssSelector("#container div.container"));// 点击按扭
			int size = divs.size(); // 列表视频大小
			while (true) {
				System.out.println("size:" + size);
				// 移动到页面最底部
				((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight)");
				// 给予充分的加载时间
				Thread.sleep(30 * 1000);
				divs = driver.findElements(By.cssSelector("#container div.container"));
				System.out.println("div size:" + divs.size());
				if (divs.size() == size)
					break;
				size = divs.size();
			}

			/**
			 * WebDriver自带了一个智能等待的方法。 dr.manage().timeouts().implicitlyWait(arg0, arg1）；
			 * Arg0：等待的时间长度，int 类型 ； Arg1：等待时间的单位 TimeUnit.SECONDS 一般用秒作为单位。
			 */
			// 请求列表队列
			List<String> urls = new ArrayList<String>();
			// 列表页图片队列
			List<String> imgs = new ArrayList<String>();
			for (int i = 0; i < divs.size(); i++) {
				WebElement webElement = divs.get(i);
				// 列表页链接地址
				List<WebElement> hrefs = webElement.findElements(By.tagName("a"));
				for (int j = 0; j < hrefs.size(); j++) {
					String href = hrefs.get(j).getAttribute("href");
					urls.add(href);
				}
				// 列表页图片获取
				List<WebElement> srcs = webElement.findElements(By.tagName("img"));
				for (int j = 0; j < srcs.size(); j++) {
					String src = srcs.get(j).getAttribute("src");
					imgs.add(src);
				}
			}

			// 一级队列
			System.out.println(urls.size());
			int i = 0;
			for (String url : urls) {
				try {
					// url 去重
					if (duplicate.isUrlDuplicate(url))
						continue;
					NewsInfoBean newsInfoBean = get(driver, url);
					List<String> titleImgs = new ArrayList<String>();
					String uploadImg = ossUtils.uploadImage(imgs.get(i++));
					titleImgs.add(uploadImg);
					// 添加列表图片
					newsInfoBean.setTitle_imgs(titleImgs);
					// 添加上级链接
					newsInfoBean.setIndex_url(index_url);
					process(newsInfoBean);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			// 二级队列
			System.out.println(secLists.size()); 
			for (String url : secLists) { 
				try {
					// url 去重
					if (duplicate.isUrlDuplicate(url))
						continue;
					NewsInfoBean newsInfoBean = get(driver, url);

					newsInfoBean.setIndex_url(index_url);
					process(newsInfoBean);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (driver != null) {
				/**
				 * dr.quit()和dr.close()都可以退出浏览器,简单的说一下两者的区别：第一个close，
				 * 如果打开了多个页面是关不干净的，它只关闭当前的一个页面。第二个quit，
				 * 是退出了所有Webdriver所有的窗口，退的非常干净，所以推荐使用quit最为一个case退出的方法。
				 */
				driver.quit();
			} // 退出浏览器}
		}
	}

	// kafka 操作
	@Autowired
	private Producer producer;
	private static final Logger LOGGER = LoggerFactory.getLogger(NbavideoCrawl.class);
	// mongo 操作工具
	@Autowired
	private MongoTemUtil mongoTemUtil;
	@Autowired
	OssUtils ossUtils;

	private void process(NewsInfoBean newsInfoBean) {
		setRequestParameters(newsInfoBean);
		boolean failed = isNUll(newsInfoBean);
		setDefaultPubDate(newsInfoBean);
		int disposeState = failed ? 2 : 1;
		newsInfoBean.setDispose_state(disposeState);
		setConstant(newsInfoBean);
		// 设置其他字段
		newsInfoBean.setCreate_date(DateUtils.getStrYYYYMMDDHHmmss(new Date()));
		// 数据类型咨询
		newsInfoBean.setData_type(0);
		LOGGER.info(newsInfoBean.toString());
		if (!failed) {
			mongoTemUtil.save(newsInfoBean);
			producer.send(newsInfoBean.getId(), new Gson().toJson(newsInfoBean));

			LOGGER.info("mongo,kafka 成功");
		}
	}

	// request 参数设置
	protected void setRequestParameters(NewsInfoBean newsInfoBean) {
		final String tag = "篮球 NBA 视频";
		newsInfoBean.setTag(tag);
	}

	private NewsInfoBean get(WebDriver driver, String url) {
		// infoBean 对象
		NewsInfoBean newsInfoBean = new NewsInfoBean();
		try {
			driver.get(url);
			System.out.println("Get:" + url);
			/**
			 * WebDriver自带了一个智能等待的方法。 dr.manage().timeouts().implicitlyWait(arg0, arg1）；
			 * Arg0：等待的时间长度，int 类型 ； Arg1：等待时间的单位 TimeUnit.SECONDS 一般用秒作为单位。
			 */
//			driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
			Thread.sleep(1000 * 40);
		} catch (Exception e) {
			e.printStackTrace();
		}

		String html = driver.getPageSource(), title = null, pubDate = null, videoUrl = null, keywords = "篮球 视频";

		// 获取标题
		try {
			title = driver.findElement(By.cssSelector("span._base_title")).getText();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (title == null) {
			try {
				title = driver.findElement(By.cssSelector("h1.video_title")).getText();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// 获取发布时间
		try {
			pubDate = driver.findElement(By.xpath("//*[@id=\"_video_tags_o\"]/span")).getText();
			pubDate = formate(pubDate);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 获取keywords
		try {
			List<WebElement> keywordsElement = driver.findElements(By.cssSelector("#_video_tags_o a"));
			for (WebElement webElement : keywordsElement) {
				keywords += " ";
				keywords += webElement.getText();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 获取redio
		try {
			int i = 3;
			while (videoUrl == null || (videoUrl.startsWith("blob")) && --i > 0) {
				videoUrl = driver.findElement(By.xpath("//*[@id=\"tenvideo_player\"]/txpdiv/txpdiv[3]/video[1]"))
						.getAttribute("src");
				System.out.println(videoUrl);
			}

		} catch (Exception e) {
			e.printStackTrace();

		}

		newsInfoBean.setUrl(url);
		newsInfoBean.setId(StringUtils.md5(url));
		newsInfoBean.setHtml(html);
		newsInfoBean.setTitle(title);
		newsInfoBean.setKey_word(keywords);
		newsInfoBean.setPub_date(pubDate);
		// 内容去重
		newsInfoBean.setContent(title);
		if (duplicate.isContentDuplicate(newsInfoBean)) {
			return new NewsInfoBean();
		}
		// 上传视频
		if (videoUrl.startsWith("http") || videoUrl.startsWith("https")) {
			videoUrl = ossUtils.uploadVideo(videoUrl);
			newsInfoBean.setVideo_url(videoUrl);
		} else {
			// 重新抓取
			secLists.add(url);
		}
		return newsInfoBean;
	}

	private String formate(String pubDate) {
		final int dayIndex = pubDate.indexOf("日");
		pubDate = pubDate.substring(0, dayIndex);
		pubDate = CrawlDateUtil.dateConvert3(pubDate);
		return pubDate + " 00:00";
	}
}
