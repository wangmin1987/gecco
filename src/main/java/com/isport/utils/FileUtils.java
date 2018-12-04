package com.isport.utils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileUtils {

	/**
	 * byte数组转换成16进制字符串
	 * @param src
	 * @return
	 */
	private static String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString().toUpperCase();
	}

	/**
	 * 根据文件流判断图片类型
	 * @param is
	 * @return jpg/png/gif/tif/bmp
	 */
	public static String getImageType(InputStream is) {
		//读取文件的前几个字节来判断图片格式
		byte[] b = new byte[4];
		try {
			is.read(b, 0, b.length);
			String type = bytesToHexString(b);
			if (type.contains("FFD8FF")) {
				return "jpg";
			} else if (type.contains("89504E47")) {
				return "png";
			} else if (type.contains("47494638")) {
				return "gif";
			} else if (type.contains("49492A00")) {
				return "tif";
			} else if (type.contains("424D")) {
				return "bmp";
			} else {
				return "";
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	/**
	 * 根据文件流判断视频类型
	 * @param is
	 * @return avi/rm/mpg/mov/asf/mp4
	 */
	public static String getVideoType(InputStream is) {
		//读取文件的前几个字节来判断图片格式
		byte[] b = new byte[4];
		try {
			is.read(b, 0, b.length);
			String type = bytesToHexString(b);
			if (type.contains("41564920")) {
				return "avi";
			} else if (type.contains("2E524D46")) {
				return "rm";
			} else if (type.contains("000001BA") || type.contains("000001B3")) {
				return "mpg";
			} else if (type.contains("6D6F6F76")) {
				return "mov";
			} else if (type.contains("3026B2758E66CF11")) {
				return "asf";
			} else {
				return "mp4";
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	/**
	 * 获取指定URL文件的后缀名：通过截取链接的后缀名
	 */
	public static String parseSuffixByUrl(String strUrl) {
		try {
			String[] spUrl = strUrl.toString().split("/");
			String endUrl = spUrl[spUrl.length - 1];

			Pattern pattern = Pattern.compile("\\S*[?]\\S*");
			Matcher matcher = pattern.matcher(strUrl);
			if (matcher.find()) {
				endUrl = endUrl.split("\\?")[0];
			}
			String[] spPoint = endUrl.split("\\.");
			String suffix = spPoint[spPoint.length - 1].toLowerCase();
			if ("avi,rm,mpg,mov,asf,mp4".indexOf(suffix) == -1) {
				suffix = null;
			}
			return suffix;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取指定URL文件的后缀名：通过Content-Type获取
	 */
	public static String parseSuffixByStream(String strUrl) {
		BufferedInputStream bis = null;
		HttpURLConnection urlConnection = null;
		URL url = null;
		try {
			url = new URL(strUrl);
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.connect();
			bis = new BufferedInputStream(urlConnection.getInputStream());
			String contentType = HttpURLConnection.guessContentTypeFromStream(bis);
			return contentType.substring(contentType.lastIndexOf("/") + 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
