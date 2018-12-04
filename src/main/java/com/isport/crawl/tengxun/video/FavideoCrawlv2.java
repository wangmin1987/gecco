package com.isport.crawl.tengxun.video;

import java.util.ArrayList;
import java.util.List;  

import org.openqa.selenium.By; 
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement; 
import org.springframework.stereotype.Service;

import com.isport.bean.NewsInfoBean;
import com.isport.utils.StringUtils;

@Service
public class FavideoCrawlv2 extends FavideoCrawl {

	@Override
	public void crawl() {
		WebDriver driver = null;
		String index_url = "http://sports.qq.com/video/yc/";
		try {
			driver = getChromeDriver(ADBLOCK);
			driver.get(index_url);// 打开指定的网站
			closeCurWindow(driver);
			/**
			 * WebDriver自带了一个智能等待的方法。 dr.manage().timeouts().implicitlyWait(arg0, arg1）；
			 * Arg0：等待的时间长度，int 类型 ； Arg1：等待时间的单位 TimeUnit.SECONDS 一般用秒作为单位。
			 */
			// 请求列表队列
			List<String> urls = getDetails(driver);

			for (String url : urls) {
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

	@Override
	protected NewsInfoBean get(WebDriver driver, String url) {

		// infoBean 对象
		NewsInfoBean newsInfoBean = new NewsInfoBean();
		try {
			driver.get(url);
			System.out.println("Get:" + url);
			Thread.sleep(1000 * 45);
		} catch (Exception e) {
			e.printStackTrace();
		}

		String html = driver.getPageSource(), title = null, videoUrl = null, pubDate = null, keywords = "足球 视频";
		;

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
		}
		return newsInfoBean;
	}
}
