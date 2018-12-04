package com.isport.crawl.dianjinghu;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.touch.TouchActions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.isport.Constants;
import com.isport.bean.NewsInfoBean;
import com.isport.crawl.aipingke.APstractPyVideo;
import com.isport.kafka.Producer;
import com.isport.utils.DateUtils;
import com.isport.utils.MongoTemUtil;
import com.isport.utils.OssUtils;
import com.isport.utils.RedisUtils;
import com.isport.utils.SimilarityUtils;
import com.isport.utils.StringUtils;

/**
 * 乒乓视频 抓取
 * 
 * @author 八斗体育
 *
 */
@Service
public class LOLvideoCrawl extends APstractPyVideo{
	// 文本去重工具
	@Autowired
	private SimilarityUtils similarityUtils;
	// 请求列表二级队列
	private List<String> secLists = new ArrayList<String>();
	// 列表图片
	Map<String, String> titleImgs = new HashMap<String, String>();
	// kafka 操作
	@Autowired
	private Producer producer;
	private static final Logger LOGGER = LoggerFactory.getLogger(LOLvideoCrawl.class);
	// mongo 操作工具
	@Autowired
	private MongoTemUtil mongoTemUtil;
	@Autowired
	OssUtils ossUtils;
	@Autowired
	RedisUtils redisUtils;
	WebDriver driver = null;
	WebDriver mdriver = null;

	private void setChrom(String index_url) {
		// 加载adblock插件
		System.setProperty("webdriver.chrome.driver", "C:\\Google\\Chrome\\chromedriver.exe");// chromedriver服务地址
		// 新建一个WebDriver 的对象，但是new 的是FirefoxDriver的驱动
		driver = getChromeDriver("C:\\Google\\Chrome\\adblockpluschrome-3.4.1.2199.crx");
		driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);// 隐式等待
		closeCurWindow(driver);
		mdriver = getMobileEmulationChromeDriver();
		mdriver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
		// 设置打开的浏览器的屏幕大小,模拟手机屏幕.
		mdriver.manage().window().setSize(new Dimension(600, 900));
		}
	public void crawl(String index_url) {
		if (StringUtils.isNUll(index_url)) {
			index_url = "http://lol.dianjinghu.com/video/all/1.html";
		}
		if(driver==null||mdriver==null) {
			
			setChrom(index_url);
		}
		try {
			driver.get(index_url);// 打开指定的网站
			String pageElement = driver.findElement(By.cssSelector("a.pages_size.pages_right")).getAttribute("href");
			String lastPage =null;
			if(pageElement!=null) {
				
				lastPage = getPage(pageElement);
			}
		
			// 当前页码
			String nowPage = getPage(index_url);
			// 获取所有的视频列表
 			List<WebElement> divs = driver.findElements(By.cssSelector("div.list-left.article-left > ul > li"));// 点击按扭
			/**
			 * WebDriver自带了一个智能等待的方法。 dr.manage().timeouts().implicitlyWait(arg0, arg1）；
			 * Arg0：等待的时间长度，int 类型 ； Arg1：等待时间的单位 TimeUnit.SECONDS 一般用秒作为单位。
			 */
			// 请求列表队列
			List<String> urls = new ArrayList<String>();
			for (int i = 0; i < divs.size(); i++) {
				WebElement webElement = divs.get(i);
				List<WebElement> hrefs = webElement.findElements(By.cssSelector("a"));
				for (int j = 0; j < hrefs.size(); j++) {
					String src = webElement.findElement(By.tagName("img")).getAttribute("src");
					String href = hrefs.get(j).getAttribute("href");
					if (StringUtils.isNUll(href) || !redisUtils.setIfAbsent(href, 1L, 30L, TimeUnit.DAYS)) {
						continue;
					}
					urls.add(href);
					titleImgs.put(href,src);
				}
			}
			// 一级队列
			System.out.println(urls.size());
			for (String url : urls) {
				try {
					NewsInfoBean newsInfoBean = get(driver, url);
					int other = getOther(newsInfoBean, mdriver);
					if (newsInfoBean.getVideo_url() == null) {
						continue;
					}
					if(other==0) {
						continue;
					}
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
					NewsInfoBean newsInfoBean = get(driver, url);
					newsInfoBean.setIndex_url(index_url);
					process(newsInfoBean);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			// 进行下一页读取
			if(lastPage!=null) {
				if (Integer.valueOf(nowPage) < Integer.valueOf(lastPage)) {
					getNextUrl(index_url, Integer.valueOf(nowPage));
				} else {
					System.out.println("采集到尾页了");
					return;
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

	// 采集存储
	private void process(NewsInfoBean newsInfoBean) {
		setRequestParameters(newsInfoBean);
		boolean failed = isNUll(newsInfoBean);
		int disposeState = failed ? 2 : 1;
		newsInfoBean.setDispose_state(disposeState);
		// 设置其他字段
		newsInfoBean.setCreate_date(DateUtils.getStrYYYYMMDDHHmmss(new Date()));
		// 数据类型咨询
		newsInfoBean.setData_type(0);
		// 正文相似性去重
		String id = StringUtils.md5(newsInfoBean.getUrl());
		String mainBody = newsInfoBean.getTitle();
//		String similar = similarityUtils.distinguish(id, mainBody);
//		if (!similar.equals("true")) {
//			LOGGER.info("查重失败: " + similar);
//			return;
//		}
		LOGGER.info(newsInfoBean.toString());
		if (!failed) {
			mongoTemUtil.save(newsInfoBean);
			producer.send(newsInfoBean.getId(), new Gson().toJson(newsInfoBean));
			LOGGER.info("mongo,kafka 成功");
			System.out.println("插入成功");
		}
	}


	// request 参数设置
	protected void setRequestParameters(NewsInfoBean newsInfoBean) {
		final String tag = "电竞  LOL 视频", authorId = "5300b5da347f41d5810a79c4326f3080",
				channelId = "26150cf365a7465687ab084e502e4e6e";
		newsInfoBean.setTag(tag);
		newsInfoBean.setAuthor_id(authorId);
		newsInfoBean.setChannel_id(channelId);
		newsInfoBean.setSource(Constants.NEWS_SOURCE_DIANJINGHU.value);
		newsInfoBean.setSource_icon(Constants.NEWS_ICON_DIANJINGHU.value);
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
			// driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
			Thread.sleep(1000 * 10);
		} catch (Exception e) {
			e.printStackTrace();
		}

		String html = driver.getPageSource(), title = null, pubDate = null, videoUrl = null, keywords = "电竞 视频";
		// 获取标题
		try {
			title = driver.findElement(By.cssSelector("#news_detail > h3")).getText();
			title=title.substring(0, title.indexOf("2"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 获取发布时间
		try {
			pubDate = driver.findElement(
					By.cssSelector("#news_detail > h3 > p"))
					.getText();
			pubDate = formate(pubDate);
		} catch (Exception e) {
			e.printStackTrace();
		}

//		// 获取keywords
//		try {
//			List<WebElement> keywordsElement = driver.findElements(By
//					.cssSelector("#ligo_vabout_cont > div.deleft > div > div.span8 > div > ul > li.attr.clearfix> a"));
//			for (WebElement webElement : keywordsElement) {
//				keywords += " ";
//				keywords += webElement.getText();
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		// 获取vedio
		try {
			int i = 3;
			while (videoUrl == null || (videoUrl.startsWith("blob")) && --i > 0) {
				videoUrl = driver.findElement(By.cssSelector("body > div.pages-nav > div > div.container.L1200 > div.video > div.video-left.left > iframe")).getAttribute("src");
				if(StringUtils.isNUll(videoUrl)||videoUrl.contains(".swf")) {
					return null;
				}
				System.out.println(videoUrl);
			}

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		List<String> titleImg = new ArrayList<String>();
		newsInfoBean.setUrl(url);
		newsInfoBean.setId(StringUtils.md5(url));
		newsInfoBean.setHtml(html);
		newsInfoBean.setTitle(title);
		newsInfoBean.setPub_date(pubDate);
		newsInfoBean.setVideo_url(videoUrl);
		String uploadImg = ossUtils.uploadImage(titleImgs.get(url));
		titleImg.add(uploadImg);
		newsInfoBean.setTitle_imgs(titleImg);
		return newsInfoBean;
	}

	private String formate(String pubDate) {
		 pubDate = pubDate.replaceAll("日", "").replaceAll("月","-").replaceAll("年","-");
		return pubDate.substring(0,pubDate.indexOf("来"));
	}

	private String getPage(String url) {
		String nowPage = url.substring(url.lastIndexOf("/")+1, url.lastIndexOf("."));
		return nowPage;
	}

	private void getNextUrl(String url, int page) {
		page++;
		String nextUrl = url.substring(0,url.lastIndexOf("/")+1) + page + ".html";
		crawl(nextUrl);
	}

	private int getOther(NewsInfoBean newsInfoBean, WebDriver driver) {
		try {
			String videoUrl = newsInfoBean.getVideo_url();
			if (videoUrl.equals("http://v.qq.com/txp/iframe/player.html?vid=f08017cw0iy")) {
				return 0;
			}
			driver.get(videoUrl);
			System.out.println("Get2:" + videoUrl);
			Thread.sleep(1000 * 10);
			// 选择元素
			try {
			WebElement video = driver.findElement(By.cssSelector("txpdiv.txp_overlay_play > button.txp_btn.txp_btn_play_lg.txp_show"));
			TouchActions touchActions = new TouchActions(driver);
			touchActions.singleTap(video).perform();
			}catch (Exception e) {
				try {
					WebElement video1 = driver.findElement(By.cssSelector("txpdiv.txp_overlay_play > button.txp_btn.txp_btn_play_lg.txp_show > svg"));
					TouchActions touchActions1 = new TouchActions(driver);
					touchActions1.singleTap(video1).perform();
				}catch (Exception e1) {
					try {
						WebElement video2 = driver.findElement(By.cssSelector("#mod_tenvideo_video_player_0 > div > div.tvp_overlay_play > span"));
						TouchActions touchActions2 = new TouchActions(driver);
						touchActions2.singleTap(video2).perform();
					}catch (Exception e2) {
						try {
							WebElement video2 = driver.findElement(By.cssSelector("#txp_svg_play_lg > circle"));
							TouchActions touchActions2 = new TouchActions(driver);
							touchActions2.singleTap(video2).perform();
						}catch (Exception e3) {
							
						}
					}
				}
				
			}
			Thread.sleep(1000 * 75);
			try {
			videoUrl = driver.findElement(By.cssSelector("txpdiv.txp_video_container > video")).getAttribute("src");
			}catch (Exception e) {
				try {
					
					videoUrl = driver.findElement(By.cssSelector("#tenvideo_video_player_0")).getAttribute("src");
				}catch (Exception e1) {
					System.out.println("解析vediourl失败");
					return 0;
				}
			}
			if(StringUtils.isNUll(videoUrl)) {
				System.out.println("解析vediourl失败");
				return 0;
			}
			System.out.println("上传前的videourl:"+videoUrl);
			// 上传视频
 			if (videoUrl.startsWith("http") || videoUrl.startsWith("https")) {
				videoUrl = ossUtils.uploadVideo(videoUrl);
				newsInfoBean.setVideo_url(videoUrl);
				System.out.println("上传后的videourl:"+videoUrl);
				return 1;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
		return 1;
	}

}
