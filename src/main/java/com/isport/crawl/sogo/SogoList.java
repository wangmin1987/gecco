package com.isport.crawl.sogo;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
 
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.gargoylesoftware.htmlunit.util.UrlUtils;
import com.isport.crawl.AbstractListPipeLine;

@Service
public class SogoList extends AbstractListPipeLine {
	// redis 主机
	@Value("${spring.redis.host}")
	private String redis_host;
	// redis 端口
	@Value("${spring.redis.port}")
	private String redis_port;

	@Override
	protected Object getList(JSONObject jo) throws Exception {
		return jo.getJSONArray("newsList");
	}

	@Override
	protected long getNewsTime(Object obj) throws Exception {
		return 0;
	}

	@Override
	protected String getNewsDocUrl(String baseUrl, Object obj) {
		JSONObject item = JSONObject.parseObject(obj.toString());
		String docUrl = item.getString("docUrl");
		try {
			downloadNet(docUrl,getParamByUrl(docUrl, "name"));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return ILLEGAL_URL;
	}

	@Override
	protected String getNextUrl(String url, String nextUrl, int page) {
		return UrlUtils.resolveUrl(url, nextUrl);
	}

	private void downloadNet(String file, String name) throws MalformedURLException {
		String path ="sogo/";
		// 下载网络文件
		int byteread = 0;
		FileOutputStream fs = null;
		URL url = new URL(file);
		try {
			URLConnection conn = url.openConnection();
			InputStream inStream = conn.getInputStream();
			fs = new FileOutputStream(path+name);
			byte[] buffer = new byte[1204];
			while ((byteread = inStream.read(buffer)) != -1) {
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

	/**
	 * 获取指定url中的某个参数
	 * 
	 * @param url
	 * @param name
	 * @return
	 */
	public static String getParamByUrl(String url, String name) {
		url += "&";
		String pattern = "(\\?|&){1}#{0,1}" + name + "=[a-zA-Z0-9%]*(&{1})";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(url);
		if (m.find()) { 
			return m.group(0).split("=")[1].replace("&", "");
		} else {
			return null;
		}
	}

	public static void main(String[] args) throws Exception {
		String url = "http://download.pinyin.sogou.com/dict/download_cell.php?id=427&name=%E7%AF%AE%E7%90%83%E6%9C%AF%E8%AF%AD%E8%AF%8D%E5%BA%93";
		System.out.println(getParamByUrl(url, "name"));
	}
}
