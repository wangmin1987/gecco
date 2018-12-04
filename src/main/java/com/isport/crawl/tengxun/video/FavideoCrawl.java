package com.isport.crawl.tengxun.video;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.touch.TouchActions;
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
 * 英超足球视频
 * 
 * @author 八斗体育
 *
 */
@Service
public class FavideoCrawl extends AbstractVideoCrawl {
	@Autowired
	protected Duplicate duplicate;
	// 列表图片
	Map<String, String> titleImgs = new HashMap<String, String>();

	public void crawl() {
		WebDriver driver = null, mdriver = null;
		String index_url = "http://sports.qq.com/video/yc/";
		try {
			driver = getChromeDriver(ADBLOCK);
			driver.get(index_url);// 打开指定的网站

			// 关闭当前窗口
			Set<String> winHandels = driver.getWindowHandles();
			List<String> it = new ArrayList<String>(winHandels);
			driver.switchTo().window(it.get(1));
			driver.close();
			driver.switchTo().window(it.get(0));
			/**
			 * WebDriver自带了一个智能等待的方法。 dr.manage().timeouts().implicitlyWait(arg0, arg1）；
			 * Arg0：等待的时间长度，int 类型 ； Arg1：等待时间的单位 TimeUnit.SECONDS 一般用秒作为单位。
			 */
			// 请求列表队列
			List<String> urls =  getDetails(driver);

			mdriver = getMobileEmulationChromeDriver();
			mdriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
			// 设置打开的浏览器的屏幕大小,模拟手机屏幕.
			mdriver.manage().window().setSize(new Dimension(600, 900));
			for (String url : urls) {
				try {
					// url 去重
					if (duplicate.isUrlDuplicate(url))
						continue;
					NewsInfoBean newsInfoBean = get(mdriver, url);
					// 设置日期和关键字
					getOther(newsInfoBean, driver, url);
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

	protected List<String> getDetails(WebDriver driver) {
		List<String> urls = new ArrayList<String>();
		// 右上角视频列表
		List<WebElement> divs = driver.findElements(By.xpath("//*[@id=\"ls-special\"]/li[2]/div[2]/ul/li"));// 点击按扭
		System.out.println("divs1:" + divs.size());
		for (int i = 0; i < divs.size(); i++) {
			WebElement webElement = divs.get(i);
			WebElement href = webElement.findElement(By.tagName("a"));
			String url = href.getAttribute("href");
			urls.add(url);
			String src = webElement.findElement(By.tagName("img")).getAttribute("src");
			titleImgs.put(url, src);
		}
		System.out.println("urls1:" + urls.size());

		// 英超战队最新视频列表
		divs = driver.findElements(By.xpath("//*[@id=\"pl-teams\"]/div/div[2]/div[2]/ul/li"));
		System.out.println("divs2:" + divs.size());
		for (int i = 0; i < divs.size(); i++) {
			WebElement webElement = divs.get(i);
			WebElement href = webElement.findElement(By.tagName("a"));
			String url = href.getAttribute("href");
			urls.add(url);
			String src = webElement.findElement(By.tagName("img")).getAttribute("src");
			titleImgs.put(url, src);
		}
		System.out.println("urls2:" + urls.size());

		// 最新视频列表
		divs = driver.findElements(By.xpath("//*[@id=\"pl-latest\"]/div/div[2]/div/ul/li"));
		System.out.println("divs3:" + divs.size());
		for (int i = 0; i < divs.size(); i++) {
			WebElement webElement = divs.get(i);
			WebElement href = webElement.findElement(By.tagName("a"));
			String url = href.getAttribute("href");
			urls.add(url);
			String src = webElement.findElement(By.tagName("img")).getAttribute("src");
			titleImgs.put(url, src);
		}
		System.out.println("urls3:" + urls.size());

		// 足够动听列表
		divs = driver.findElements(By.xpath("//*[@id=\"pl-moving\"]/div/div[2]/div/div/ul/li"));
		System.out.println("divs4:" + divs.size());
		for (int i = 0; i < divs.size(); i++) {
			WebElement webElement = divs.get(i);
			WebElement href = webElement.findElement(By.tagName("a"));
			String url = href.getAttribute("href");
			urls.add(url);
			String src = webElement.findElement(By.tagName("img")).getAttribute("src");
			titleImgs.put(url, src);
		}
		System.out.println("urls4:" + urls.size());

		// 豪门早报
		divs = driver.findElements(By.xpath("//*[@id=\"pl-superstar\"]/div/div[2]/div/div/ul/li"));
		System.out.println("divs5:" + divs.size());
		for (int i = 0; i < divs.size(); i++) {
			WebElement webElement = divs.get(i);
			WebElement href = webElement.findElement(By.tagName("a"));
			String url = href.getAttribute("href");
			urls.add(url);
			String src = webElement.findElement(By.tagName("img")).getAttribute("src");
			titleImgs.put(url, src);
		}
		System.out.println("urls5:" + urls.size());

		// 英超群星会
		divs = driver.findElements(By.xpath("//*[@id=\"pl-stars\"]/div/div[2]/div[2]/ul/li"));
		System.out.println("divs6:" + divs.size());
		for (int i = 0; i < divs.size(); i++) {
			WebElement webElement = divs.get(i);
			WebElement href = webElement.findElement(By.tagName("a"));
			String url = href.getAttribute("href");
			urls.add(url);
			String src = webElement.findElement(By.tagName("img")).getAttribute("src");
			titleImgs.put(url, src);
		}
		System.out.println("urls6:" + urls.size());

		// 比赛视频库
		// more 点击
		while (true) {
			try {
				// 移动到页面最底部
				((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight)");
				// 给予充分的加载时间
				Thread.sleep(10 * 1000);
				WebElement more = driver.findElement(By.xpath("//*[@id=\"pl-gallery\"]/div/div[2]/div[3]/span/a"));
				more.click();
			} catch (Exception e) {
				e.printStackTrace();
				break;
			}
		}

		divs = driver.findElements(
				By.xpath("//*[@id=\"pl-gallery\"]/div/div[2]/div[2]/div[1]/div/ul/li[1]/div[2]/ul/li"));
		System.out.println("divs7:" + divs.size());
		for (int i = 0; i < divs.size(); i++) {
			WebElement webElement = divs.get(i);
			WebElement href = webElement.findElement(By.tagName("a"));
			String url = href.getAttribute("href");
			urls.add(url);
			String src = webElement.findElement(By.tagName("img")).getAttribute("src");
			titleImgs.put(url, src);
		}
		System.out.println("urls7:" + urls.size());
		return urls;
	}

	protected void getOther(NewsInfoBean newsInfoBean, WebDriver driver, String url) {
		try {
			driver.get(url);
			System.out.println("Get2:" + url);
		} catch (Exception e) {
			e.printStackTrace();
		}

		String pubDate = null, keywords = "足球 视频";

		// 获取发布时间
		try {
			pubDate = driver.findElement(By.xpath("//*[@id=\"_video_tags_o\"]/span")).getText();
			pubDate = formate(pubDate);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 获取keywords
		try {
			List<WebElement> keywordsElement = driver.findElements(By.xpath("//*[@id=\"_video_tags_o\"]/a"));
			for (WebElement webElement : keywordsElement) {
				keywords += " ";
				keywords += webElement.getText();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		newsInfoBean.setKey_word(keywords);
		newsInfoBean.setPub_date(pubDate);
	}

	protected String formate(String pubDate) {
		final int dayIndex = pubDate.indexOf("日");
		pubDate = pubDate.substring(0, dayIndex);
		pubDate = CrawlDateUtil.dateConvert3(pubDate);
		return pubDate + " 00:00";
	}

	// kafka 操作
	@Autowired
	private Producer producer;
	private static final Logger LOGGER = LoggerFactory.getLogger(FavideoCrawl.class);
	// mongo 操作工具
	@Autowired
	private MongoTemUtil mongoTemUtil;
	@Autowired
	OssUtils ossUtils;

	protected void process(NewsInfoBean newsInfoBean) {
		setRequestParameters(newsInfoBean);
		boolean failed = isNUll(newsInfoBean);
		// 设置默认发布时间
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
		final String tag = "足球 英超 视频";
		newsInfoBean.setTag(tag);
	}

	protected NewsInfoBean get(WebDriver driver, String url) {
		// infoBean 对象
		NewsInfoBean newsInfoBean = new NewsInfoBean();
		try {
			driver.get(url);
			System.out.println("Get:" + url);
//			Thread.sleep(1000 * 40);
			// 选择元素
			WebElement video = driver.findElement(By.cssSelector("button.txp_btn"));
			TouchActions touchActions = new TouchActions(driver);
			touchActions.singleTap(video).perform();
			/**
			 * WebDriver自带了一个智能等待的方法。 dr.manage().timeouts().implicitlyWait(arg0, arg1）；
			 * Arg0：等待的时间长度，int 类型 ； Arg1：等待时间的单位 TimeUnit.SECONDS 一般用秒作为单位。
			 */
//			 
			Thread.sleep(1000 * 75);
		} catch (Exception e) {
			e.printStackTrace();
		}

		String html = driver.getPageSource(), title = null, videoUrl = null;

		// 获取标题
		try {
			title = driver.findElement(By.cssSelector("div.video_tit")).getText();
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
		// 获取redio
		videoUrl = driver.findElement(By.cssSelector("txpdiv.txp_video_container video")).getAttribute("src");
		System.out.println(videoUrl);

		List<String> titleImg = new ArrayList<String>();
		String uploadImg = ossUtils.uploadImage(titleImgs.get(url));
		titleImg.add(uploadImg);
		newsInfoBean.setTitle_imgs(titleImg);
		newsInfoBean.setUrl(url);
		newsInfoBean.setId(StringUtils.md5(url));
		newsInfoBean.setHtml(html);
		newsInfoBean.setTitle(title);
		// 内容去重
		newsInfoBean.setContent(title);
		if (duplicate.isContentDuplicate(newsInfoBean)) {
			return new NewsInfoBean();
		}
		// 上传视频
		if (videoUrl.startsWith("http") || videoUrl.startsWith("https")) {
			videoUrl = ossUtils.uploadVideo(videoUrl);
			newsInfoBean.setVideo_url(videoUrl);
		}
		return newsInfoBean;
	}

}
