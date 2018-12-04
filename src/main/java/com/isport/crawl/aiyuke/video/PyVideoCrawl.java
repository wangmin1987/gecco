package com.isport.crawl.aiyuke.video;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import com.isport.utils.DateUtils;
import com.isport.utils.MongoTemUtil;
import com.isport.utils.OssUtils;
import com.isport.utils.StringUtils;

/**
 * aiyueke 乒羽视频
 * 
 * @author 八斗体育
 *
 */
@Service
public class PyVideoCrawl extends AbstractPyVideo {
	@Autowired
	protected Duplicate duplicate;
	// 列表图片
	Map<String, String> titleImgs = new HashMap<String, String>();
	// kafka 操作
	@Autowired
	private Producer producer;
	private static final Logger LOGGER = LoggerFactory.getLogger(PyVideoCrawl.class);
	// mongo 操作工具
	@Autowired
	private MongoTemUtil mongoTemUtil;
	@Autowired
	OssUtils ossUtils;
	// 二级队列
	List<String> secUrls = new ArrayList<String>();

	public void crawl(String index_url) {
		WebDriver driver = null, mdriver = null;
//		String index_url = "http://www.aiyuke.com/video_list/1------------.html";

		try {
			// 加载adblock插件
			driver = getChromeDriver(ADBLOCK);
			driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);// 隐式等待
			driver.get(index_url);// 打开指定的网站
			Thread.sleep(1000 * 5);
			closeCurWindow(driver);

			mdriver = getMobileEmulationChromeDriver();
			mdriver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
			// 设置打开的浏览器的屏幕大小,模拟手机屏幕.
			mdriver.manage().window().setSize(new Dimension(600, 900));

			// 请求列表队列
			List<String> urls = getList(driver);

			// 一级队列
			for (String url : urls) {
				try {
					// url 去重
					if (duplicate.isUrlDuplicate(url))
						continue;
					NewsInfoBean newsInfoBean = get(driver, url);
					// 重新生成视频
					getOther(newsInfoBean, mdriver);
					newsInfoBean.setIndex_url(index_url);
					process(newsInfoBean);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			// 二级队列
			for (String url : secUrls) {
				try {
					// url 去重
					if (duplicate.isUrlDuplicate(url))
						continue;
					NewsInfoBean newsInfoBean = get(driver, url);
					// 重新生成视频
					getOther(newsInfoBean, mdriver);
					newsInfoBean.setIndex_url(index_url);
					process(newsInfoBean);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (

		Exception e) {
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
			if (mdriver != null) {
				mdriver.quit();
			}
		}
	}
	/**
	 * 获取详情页地址
	 * @param driver
	 * @return
	 */
	protected List<String> getList(WebDriver driver) {
		List<String> urls = new ArrayList<String>();
		// 教学视频列表
		WebElement nextElement = null;
		try {
			nextElement = driver.findElement(By.cssSelector("a.p_next"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		int x = 3;
		while (nextElement != null && x-- > 0) {
			try {
				// focus element
				((JavascriptExecutor) driver).executeScript("arguments[0].focus();", nextElement);
				nextElement.click();
				Thread.sleep(1000 * 5);
				List<WebElement> divs = driver
						.findElements(By.xpath("/html/body/div[4]/div/div[3]/div[2]/div[2]/div[1]/ul/li"));
				System.out.println(": divs:" + divs.size());
				for (int i = 0; i < divs.size(); i++) {
					WebElement webElement = divs.get(i);
					WebElement href = webElement.findElement(By.tagName("a"));
					String url = href.getAttribute("href");
					urls.add(url);
					String src = webElement.findElement(By.tagName("img")).getAttribute("src");
					titleImgs.put(url, src);
				}
				System.out.println("urls1:" + urls.size());
				nextElement = driver.findElement(By.cssSelector("a.p_next"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return urls;
	}

	private void getOther(NewsInfoBean newsInfoBean, WebDriver driver) {
		try {
			String videoUrl = newsInfoBean.getVideo_url();
			driver.get(videoUrl);
			System.out.println("Get2:" + videoUrl);
			Thread.sleep(1000 * 10);
			// 选择元素
			WebElement video = driver.findElement(By.cssSelector("div.x-video-button"));
			TouchActions touchActions = new TouchActions(driver);
			touchActions.singleTap(video).perform();

			Thread.sleep(1000 * 45);
			videoUrl = driver.findElement(By.cssSelector("div.x-video-player video")).getAttribute("src");
			System.out.println(videoUrl);
			// 上传视频
			if (videoUrl.startsWith("http") || videoUrl.startsWith("https")) {
				videoUrl = ossUtils.uploadVideo(videoUrl);
				newsInfoBean.setVideo_url(videoUrl);
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

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
			Thread.sleep(1000 * 15);
		} catch (Exception e) {
			e.printStackTrace();
		}

		String html = driver.getPageSource(), title = null, videoUrl = null, keywords = "乒羽 视频", pay = "";

		// 获取标题
		try {
			title = driver.findElement(By.xpath("/html/body/div[4]/div[1]/div[2]/div[1]/h1/span")).getText();
		} catch (Exception e) {
			System.err.println("no such element:#title");
		}
		if (title == null) {
			try {
				title = driver.findElement(By.cssSelector("h1.video_title")).getText();
			} catch (Exception e) {
				System.err.println("no such element:h1.video_titl");
			}
		}

		// 付费视频?
		try {
			pay = driver.findElement(By.xpath("/html/body/div[4]/div[1]/div[3]/div[1]/div[12]/div/div/div[2]/a[1]"))
					.getText();
			System.out.println(pay);
			if (pay.contains("付费")) {
				return new NewsInfoBean();
			}
		} catch (Exception e) {
			System.err.println("no such element:付费");
		}
		// 获取redio
		try {
			videoUrl = driver.findElement(By.xpath("//*[@id=\"iframeId\"]")).getAttribute("src");
			System.out.println(videoUrl);
		} catch (Exception e) {
			System.err.println("no such element://*[@id=\\\"iframeId\\\"]");
			if (StringUtils.isNUll(videoUrl)) {
				secUrls.add(url);
			}
		}
		List<String> titleImg = new ArrayList<String>();
//		titleImg.add(titleImgs.get(url));
		String uploadImg = ossUtils.uploadImage(titleImgs.get(url));
		titleImg.add(uploadImg);
		newsInfoBean.setTitle_imgs(titleImg);
		newsInfoBean.setUrl(url);
		newsInfoBean.setId(StringUtils.md5(url));
		newsInfoBean.setHtml(html);
		newsInfoBean.setTitle(title);
		newsInfoBean.setKey_word(keywords);
		newsInfoBean.setVideo_url(videoUrl);
		// 内容去重
		newsInfoBean.setContent(title);
		if (duplicate.isContentDuplicate(newsInfoBean)) {
			return new NewsInfoBean();
		}
		return newsInfoBean;
	}

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
		final String tag = "乒羽 视频";
		newsInfoBean.setTag(tag);
	}
}
