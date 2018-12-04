package com.isport.crawl.selenium;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

/**
 * selenium驱动程序抽象类
 * 
 * @author 八斗体育
 *
 */
public abstract class AbstractDriver {
	// chromedriver
	private static final String DRIVER = "D:\\chrome\\chromedriver.exe";
	// adblock
	protected static final String ADBLOCK = "D:\\chrome\\adblockpluschrome-3.4.1.2199.crx";
	// 页面加载超时
	private final int pageLoadTimeOut = 180;
	private final int implicitlyWait = 10;

	protected WebDriver getCleanChromeDriver() {
		// 加载adblock插件
		System.setProperty("webdriver.chrome.driver", DRIVER);// chromedriver服务地址

		WebDriver driver = new ChromeDriver(); // 新建一个WebDriver 的对象，但是new 的是FirefoxDriver的驱动
		driver.manage().timeouts().implicitlyWait(implicitlyWait, TimeUnit.SECONDS);// 隐式等待
		driver.manage().timeouts().pageLoadTimeout(pageLoadTimeOut, TimeUnit.SECONDS);
		driver.manage().timeouts().setScriptTimeout(pageLoadTimeOut, TimeUnit.SECONDS);
		return driver;
	}

	protected void closeCurWindow(WebDriver driver) {
		// 关闭当前窗口
		Set<String> winHandels = driver.getWindowHandles();
		List<String> it = new ArrayList<String>(winHandels);
		driver.switchTo().window(it.get(1));
		driver.close();
		driver.switchTo().window(it.get(0));
	}

	// 获取谷歌浏览器驱动对象
	protected WebDriver getChromeDriver(String file) {
		// 加载adblock插件
		System.setProperty("webdriver.chrome.driver", DRIVER);// chromedriver服务地址
		ChromeOptions options = new ChromeOptions();
		options.addExtensions(new File(file));
		options.addArguments("start-maximized");
		WebDriver driver = new ChromeDriver(options); // 新建一个WebDriver 的对象，但是new 的是FirefoxDriver的驱动
		driver.manage().timeouts().implicitlyWait(implicitlyWait, TimeUnit.SECONDS);// 隐式等待
		driver.manage().timeouts().pageLoadTimeout(pageLoadTimeOut, TimeUnit.SECONDS);
		driver.manage().timeouts().setScriptTimeout(pageLoadTimeOut, TimeUnit.SECONDS);
		return driver;
	}

	// 获取手机端模拟谷歌浏览器驱动对象
	protected WebDriver getMobileEmulationChromeDriver() {
		// 开启浏览器模拟模式，否则action操作无效
		Map<String, Object> deviceMetrics = new HashMap<>();
		deviceMetrics.put("width", 360);
		deviceMetrics.put("height", 640);
		deviceMetrics.put("pixelRatio", 3.0);
		Map<String, Object> mobileEmulation = new HashMap<>();
		mobileEmulation.put("deviceMetrics", deviceMetrics);
		mobileEmulation.put("userAgent",
				"Mozilla/5.0 (Linux; Android 4.2.1; en-us; Nexus 5 Build/JOP40D) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.166 Mobile Safari/535.19");
		ChromeOptions chromeOptions = new ChromeOptions();
		chromeOptions.setExperimentalOption("mobileEmulation", mobileEmulation);
		WebDriver driver = new ChromeDriver(chromeOptions);
		driver.manage().timeouts().implicitlyWait(implicitlyWait, TimeUnit.SECONDS);// 隐式等待
		driver.manage().timeouts().pageLoadTimeout(pageLoadTimeOut, TimeUnit.SECONDS);
		driver.manage().timeouts().setScriptTimeout(pageLoadTimeOut, TimeUnit.SECONDS);
		return driver;
	}

}
