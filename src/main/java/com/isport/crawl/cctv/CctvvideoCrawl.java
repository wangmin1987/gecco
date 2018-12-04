package com.isport.crawl.cctv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
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
import com.isport.bean.NewsInfoBean;
import com.isport.crawl.aiyuke.video.PyVideoCrawl;
import com.isport.kafka.Producer;
import com.isport.utils.DateUtils;
import com.isport.utils.MongoTemUtil;
import com.isport.utils.OssUtils;
import com.isport.utils.StringUtils;

/**
 * cctv 排球视频
 * 
 * @author 八斗体育
 *
 */
@Service
public class CctvvideoCrawl extends AbstractCctvVideo {

	// 列表图片
	Map<String, String> titleImgs = new HashMap<String, String>();
	// kafka 操作
	@Autowired
	private Producer producer;
	private static final Logger LOGGER = LoggerFactory.getLogger(PyVideoCrawl.class);
	// 最大抓取视频ts 数
	private final int MAXTS = 10;
	// mongo 操作工具
	@Autowired
	private MongoTemUtil mongoTemUtil;
	@Autowired
	OssUtils ossUtils;

	public void crawl(String index_url) {
		// 浏览器驱动 ， 模拟移动端驱动
		WebDriver driver = null, mdriver = null;
		try {
			// 加载adblock插件
//			driver = getChromeDriver(ADBLOCK);
			driver = getCleanChromeDriver();
			driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);// 隐式等待
			driver.get(index_url);// 打开指定的网站
			Thread.sleep(1000 * 5);
//			closeCurWindow(driver);
//
			mdriver = getMobileEmulationChromeDriver();
			mdriver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
			// 设置打开的浏览器的屏幕大小,模拟手机屏幕.
			mdriver.manage().window().setSize(new Dimension(600, 900));

			// 请求列表队列
			List<String> urls = new ArrayList<String>();

			try {
				Thread.sleep(1000 * 5);
				List<WebElement> uls = driver.findElements(By.cssSelector("ul.il_w120_b1"));
				System.out.println(": uls:" + uls.size());
				for (int i = 0; i < uls.size(); i++) {
					WebElement webElement = uls.get(i);
					List<WebElement> lies = webElement.findElements(By.tagName("li"));
					System.out.println("lies:" + lies.size());
					for (WebElement li : lies) {
						WebElement href = li.findElement(By.cssSelector("div.image a"));
						String url = href.getAttribute("href");
						urls.add(url);
						String src = li.findElement(By.cssSelector("div.image img")).getAttribute("src");
						titleImgs.put(url, src);
					}
				}
				System.out.println("urls1:" + urls.size());
			} catch (Exception e) {
				e.printStackTrace();
			}
			// 一级队列
			for (String url : urls) {
				try {
					// url 去重
					if (duplicate.isUrlDuplicate(url))
						continue;
					NewsInfoBean newsInfoBean = get(mdriver, url);
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
			if (mdriver != null) {
				mdriver.quit();
			}
		}
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
		final String tag = "排球 视频";
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
			Thread.sleep(1000 * 15);

			// 选择元素
			WebElement video = driver.findElement(By.cssSelector("#playbtn_img"));
			TouchActions touchActions = new TouchActions(driver);
			touchActions.singleTap(video).perform();

			Thread.sleep(1000 * 15);
		} catch (Exception e) {
			e.printStackTrace();
		}

		String html = driver.getPageSource(), title = null, pubDate = null, videoUrl = null, keywords = "网球 视频";

		// 获取标题
		try {
			title = driver.findElement(By.xpath("//*[@id=\"scroller\"]/div[3]/h1")).getText();
		} catch (Exception e) {
//			e.printStackTrace();
			System.err.println("no such element:*[@id=\\\"scroller\\\"]/div[3]/h1");
		}
		if (title == null) {
			try {
				title = driver.findElement(By.cssSelector("#title")).getText();
			} catch (Exception e) {
//				e.printStackTrace();
				System.err.println("no such element:#title");
			}
		}

		// 获取发布时间
		try {
			pubDate = driver.findElement(By.cssSelector("#time")).getText();
			pubDate = pubDate.split("：")[1];
		} catch (Exception e) {
//			e.printStackTrace();
			System.err.println("no such element:#time");
		}
		if (pubDate == null) {
			try {
				pubDate = driver.findElement(By.xpath("//*[@id=\"info\"]/span[2]")).getText();
				String year = getSysYear();
				pubDate = year + "-" + pubDate;
			} catch (Exception e) {
//				e.printStackTrace();
				System.err.println("no such element:*[@id=\\\"info\\\"]/span[2]");
			}
		}
		if (pubDate == null) {
			try {
				pubDate = driver.findElement(By.cssSelector("#updatetime")).getText();
				pubDate = pubDate.split("：")[1];
			} catch (Exception e) {
//				e.printStackTrace();
				System.err.println("no such element:#updatetime");
			}
		}
		System.out.println(pubDate);
		// 获取redio
		try {
			videoUrl = driver.findElement(By.xpath("//*[@id=\"html5Player\"]")).getAttribute("src");
			System.out.println(videoUrl);
			String m3u8url = videoUrl;
			// 下载m3u8
			downloadNet(videoUrl, DOWNLOADFILE);
			int line = getFileLineNumber(new File(DOWNLOADFILE));
			System.out.println(line);
			// 使用最高清晰
			String m3u8addr = readLine(DOWNLOADFILE, line);
			System.out.println("read addr:" + m3u8addr);
			String host = getHost(videoUrl);
			// 合成ts文件地址
			videoUrl = "http://" + host + m3u8addr;
			System.out.println("m3u8 addr:" + videoUrl);
			downloadNet(videoUrl, DOWNLOADFILE);
			line = getFileLineNumber(new File(DOWNLOADFILE));
			System.out.println(line);
			String maxts = readLine(DOWNLOADFILE, line - 1);
			System.out.println(maxts);
			maxts = maxts.replace(".ts", "");
			int size = Integer.valueOf(maxts);
			if (size > MAXTS) {
				videoUrl = null;
			} else {
				for (int i = 0; i <= size; i++) {
					try {
						int index = videoUrl.lastIndexOf("/");
						videoUrl = videoUrl.substring(0, index);
						// 合成ts下载地址
						videoUrl = videoUrl + "/" + i + ".ts";
						String file = i + ".ts";
						downloadNet(videoUrl, file);
					} catch (Exception e) {
						System.err.println(i + ".ts failed");
					}
				}
				exec(COMBINEBAT);
				exec(TS2MP4BAT);
				File file = new File(MP4FILE);
				long fileSize = file.length();
				videoUrl = ossUtils.uploadVideo(new FileInputStream(file), m3u8url, "mp4", fileSize);
				exec(DELBAT);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		List<String> titleImg = new ArrayList<String>();
		String uploadImg = ossUtils.uploadImage(titleImgs.get(url));
		titleImg.add(uploadImg);
		newsInfoBean.setTitle_imgs(titleImg);
		newsInfoBean.setUrl(url);
		newsInfoBean.setId(StringUtils.md5(url));
		newsInfoBean.setHtml(html);
		newsInfoBean.setTitle(title);
		newsInfoBean.setPub_date(pubDate);
		newsInfoBean.setKey_word(keywords);
		newsInfoBean.setVideo_url(videoUrl);

		// 内容去重
		newsInfoBean.setContent(title);
		if (duplicate.isContentDuplicate(newsInfoBean)) {
			return new NewsInfoBean();
		}
		return newsInfoBean;
	}

	private String getSysYear() {
		Calendar date = Calendar.getInstance();
		String year = String.valueOf(date.get(Calendar.YEAR));
		return year;
	}

	public static void main(String[] args) {
		CctvvideoCrawl crawl = new CctvvideoCrawl();
//		String videoUrl = "http://asp.cntv.myalicdn.com/asp/hls/main/0303000a/3/default/37d8178fa2e847a5aa1d6e2a0c1e470a/main.m3u8?maxbr=2048&minbr=400";
//		String m3u8addr = crawl.readLine(DOWNLOADFILE, 9);
//		System.out.println("read addr:" + m3u8addr);
//		String host = crawl.getHost(videoUrl);
//		videoUrl = "http://" + host + m3u8addr;
//		System.out.println("m3u8 addr:" + videoUrl);
//		for (int i = 0; i <= 5; i++) {
//			int index = videoUrl.lastIndexOf("/");
//			videoUrl = videoUrl.substring(0, index);
//			videoUrl = videoUrl + "/" + i + ".ts";
//			String file = i + ".ts";
//			try {
//				crawl.downloadNet(videoUrl, file);
//			} catch (MalformedURLException e) {
//				e.printStackTrace();
//			}
//		}
//		try {
//			crawl.exec(COMBINEBAT);
//			crawl.exec(DELBAT);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		try {
//			int line = crawl.getFileLineNumber(new File(DOWNLOADFILE));
//			System.out.println(line);
//			String maxts = crawl.readLine(DOWNLOADFILE, line - 1);
//			System.out.println(maxts);
//			maxts = maxts.replace(".ts", "");
//			int size = Integer.valueOf(maxts);
//			System.out.println(size);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}

		try {
			crawl.exec(DELBAT);
//			File file = new File(MP4FILE);
//			System.out.println(file.length());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static final String DOWNLOADFILE = "temp.txt"; // 下载m3u8 临时文件
	private static final String COMBINEBAT = "combine.bat"; // 合并bat命令
	private static final String DELBAT = "delete.bat"; // 删除命令
	private static final String MP4FILE = "combine.mp4"; // 最终合成的mp4文件
	private static final String TS2MP4BAT = "ts2mp4.bat"; // ts转mp4的命令

	/**
	 * 获取文件行数
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	private int getFileLineNumber(File file) throws IOException {
		LineNumberReader lnr = new LineNumberReader(new FileReader(file));
		lnr.skip(Long.MAX_VALUE);
		int lineNo = lnr.getLineNumber();
		lnr.close();
		return lineNo;
	}

	/**
	 * 获取域名
	 * 
	 * @param link
	 * @return
	 */
	private String getHost(String link) {
		URL url;
		String host = "";
		try {
			url = new URL(link);
			host = url.getHost();
		} catch (MalformedURLException e) {
		}
		return host;
	}

	/**
	 * 执行exe
	 * 
	 * @param path
	 * @throws Exception
	 */
	private void exec(String path) throws Exception {
		Process p = Runtime.getRuntime().exec("cmd.exe /c  " + path + "");
		BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream(), "gbk"));
		String readLine = br.readLine();
		while ((readLine = br.readLine()) != null) {
			System.out.println(readLine);
		}
		if (br != null) {
			br.close();
		}
		p.destroy();
	}

	/**
	 * 读取文件第几行
	 * 
	 * @param file
	 * @param line
	 * @return
	 */
	private String readLine(String file, int line) {
		FileInputStream inputStream = null;
		BufferedReader bufferedReader = null;
		try {
			inputStream = new FileInputStream(file);
			bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

			String str = null;
			while (line-- > 0 && (str = bufferedReader.readLine()) != null) {
			}
			return str;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// close
			try {
				if (inputStream != null)
					inputStream.close();
				if (bufferedReader != null)
					bufferedReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 下载文件
	 * 
	 * @param addr
	 * @throws MalformedURLException
	 */
	public void downloadNet(String addr, String file) throws MalformedURLException {
		// 下载网络文件
		int bytesum = 0;
		int byteread = 0;
		URL url = new URL(addr);
		FileOutputStream fs = null;
		try {
			URLConnection conn = url.openConnection();
			InputStream inStream = conn.getInputStream();
			fs = new FileOutputStream(file);
			byte[] buffer = new byte[1204];
			while ((byteread = inStream.read(buffer)) != -1) {
				bytesum += byteread;
				fs.write(buffer, 0, byteread);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fs != null)
				try {
					fs.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}
}
