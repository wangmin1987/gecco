package com.isport.utils;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.aliyun.oss.OSSClient;

@Service
public class OssUtils {

	@Autowired
	OSSClient ossClient;

	/**
	 * OSS存储空间
	 */
	@Value("${oss.bucket}")
	private String bucket;

	/**
	 * 访问OSS的CDN地址
	 */
	@Value("${oss.cdnurl}")
	private String cdnurl;

	/**
	 * 当前业务的OSS存储路径
	 */
	private String path = "news/";
	private String videoPath = "video/";

	public String uploadLogo(String imageUrl, String filename) {
		try {
			filename = path + filename;
			InputStream inputStream = new URL(imageUrl).openStream();
			ossClient.putObject(bucket, filename, inputStream);
			return cdnurl + filename;
		} catch (Exception e) {
			System.out.println("UploadImage Exception:" + e.getMessage() + "url=" + imageUrl);
			return null;
		}
	}

	/**
	 * 上传图片
	 * @param imageUrl
	 * @param headers
	 * @return
	 */
	public synchronized String uploadImage(String imageUrl, Map<String, String> headers) {
		try {
			InputStream inputStream = getImage(imageUrl, headers);
			ByteArrayOutputStream baos = cloneInputStream(inputStream);
			InputStream stream1 = new ByteArrayInputStream(baos.toByteArray());
			InputStream stream2 = new ByteArrayInputStream(baos.toByteArray());
			InputStream stream3 = new ByteArrayInputStream(baos.toByteArray());
			// 获取图片格式
			String imageType = FileUtils.getImageType(stream1);
			String suffix = StringUtils.isNUll(imageType) ? "" : "." + imageType;
			// 获取源文件的大小
			BufferedImage srcImage = ImageIO.read(stream2);
			int height = srcImage.getHeight();
			int width = srcImage.getWidth();
			// 生成保存文件名
			Date date = new Date();
			SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd/");// 设置日期格式
			String name = path + df.format(date) + StringUtils.md5(imageUrl) + "_" + width + "x" + height + suffix;
			// 流式上传
			ossClient.putObject(bucket, name, stream3);
			return cdnurl + name;
		} catch (Exception e) {
			System.out.println("UploadImage Exception:" + e.getMessage() + "url=" + imageUrl);
			return null;
		}

	}

	/**
	 * 获取图片流
	 * @param imageUrl
	 * @param headers
	 * @return
	 * @throws Exception
	 */
	public InputStream getImage(String imageUrl, Map<String, String> headers) throws Exception {
		// new一个URL对象
		URL url = new URL(imageUrl);
		// 打开链接
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		// 设置请求方式为"GET"
		conn.setRequestMethod("GET");
		// 超时响应时间为5秒
		conn.setConnectTimeout(5 * 1000);
		// 设置header便利headers
		Iterator<String> keyIterator = headers.keySet().iterator();
		while (keyIterator.hasNext()) {
			String key = keyIterator.next();
			String value = headers.get(key);
			conn.addRequestProperty(key, value);
		}
		// 通过输入流获取图片数据
		return conn.getInputStream();
	}

	public String uploadImage(String imageUrl) {
		try {
			InputStream inputStream = new URL(imageUrl).openStream();
			ByteArrayOutputStream baos = cloneInputStream(inputStream);
			InputStream stream1 = new ByteArrayInputStream(baos.toByteArray());
			InputStream stream2 = new ByteArrayInputStream(baos.toByteArray());
			InputStream stream3 = new ByteArrayInputStream(baos.toByteArray());
			// 获取图片格式
			String imageType = FileUtils.getImageType(stream1);
			String suffix = StringUtils.isNUll(imageType) ? "" : "." + imageType;
			// 获取源文件的大小
			BufferedImage srcImage = ImageIO.read(stream2);
			int height = srcImage.getHeight();
			int width = srcImage.getWidth();
			// 生成保存文件名
			Date date = new Date();
			SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd/");// 设置日期格式
			String name = path + df.format(date) + StringUtils.md5(imageUrl) + "_" + width + "x" + height + suffix;
			// 流式上传
			ossClient.putObject(bucket, name, stream3);
			return cdnurl + name;
		} catch (Exception e) {
			System.out.println("UploadImage Exception:" + e.getMessage() + "url=" + imageUrl);
			return null;
		}
	}

	public String uploadVideo(String videoUrl) {
		try {
			// 下载视频流
			HttpClient httpclient = HttpClients.createDefault();
			HttpGet httpGet = new HttpGet(videoUrl);
			HttpResponse response = httpclient.execute(httpGet);
			HttpEntity resEntity = response.getEntity();
			InputStream inputStream = resEntity.getContent();

			// 生成保存文件名
			long filesize = resEntity.getContentLength();
			String contentType = resEntity.getContentType().getValue();
			String suffix = StringUtils.isNUll(contentType) ? FileUtils.parseSuffixByUrl(videoUrl)
					: contentType.substring(contentType.lastIndexOf("/") + 1);
			if (suffix == null) {
				suffix = "mp4";
			}
			Date date = new Date();
			SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd/");// 设置日期格式
			String filename = videoPath + df.format(date) + StringUtils.md5(videoUrl) + "_" + filesize + "." + suffix;

			ossClient.putObject(bucket, filename, inputStream);
			return cdnurl + filename;
		} catch (Exception e) {
			System.out.println("UploadVideo Exception:" + e.getMessage() + "url=" + videoUrl);
			return null;
		}
	}
	
	public String uploadVideo(InputStream inputStream,String videoUrl,String suffix,long filesize) {
		try { 
			Date date = new Date();
			SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd/");// 设置日期格式
			String filename = videoPath + df.format(date) + StringUtils.md5(videoUrl)+ "_" + filesize + "." + suffix;
			ossClient.putObject(bucket, filename, inputStream);
			return cdnurl + filename;
		} catch (Exception e) {
			System.out.println("UploadVideo Exception:" + e.getMessage() + "url=" + videoUrl);
			return null;
		}
	}

	/**
	 * 关闭OSSClient
	 */
	public void shutdown() {
		try {
			ossClient.shutdown();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	private ByteArrayOutputStream cloneInputStream(InputStream input) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int len;
			while ((len = input.read(buffer)) > -1) {
				baos.write(buffer, 0, len);
			}
			baos.flush();
			return baos;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

}
