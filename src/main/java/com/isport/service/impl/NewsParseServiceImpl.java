package com.isport.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.isport.Constants;
import com.isport.service.NewsParseService;
import com.isport.utils.OssUtils;
import com.isport.utils.StringUtils;

@Service
public class NewsParseServiceImpl implements NewsParseService {

	@Autowired
	OssUtils ossUtils;

	/**
	 * 资讯图片上传
	 */
	@Override
	public boolean uploadImg(Element element) {
		try {
			Elements imgs = element.select("img");
			for (Element ele : imgs) {
				String href = ele.attr("src");
				if (StringUtils.isNUll(href)) {
					href = ele.attr("data-src");
				}
				if (StringUtils.isNUll(href)) {
					href = ele.attr("srcset");
				}
				if (!StringUtils.isNUll(href)) {
					if (href.indexOf("http") == -1) {
						href = "http:" + href;
					}
					String filePath = ossUtils.uploadImage(href);
					if (filePath == null) {
						return false;
					}
					ele.attr("src", filePath);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * 获取资讯配图：图片压缩并上传OSS（最多3张）
	 */
	@Override
	public List<String> getImage(Element ele) {
		List<String> pics = new ArrayList<String>();
		try {
			Elements imgs = ele.select("img");
			for (int i = 0; i < imgs.size() && pics.size() < 3; i++) {
				String src = imgs.get(i).attr("src");
				if (!src.equals(" ")) {
					pics.add(src);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pics;
	}

	/**
	 * 获取资讯第一张配图
	 */
	@Override
	public String getFirstImage(Element element) {
		try {
			Elements imgs = element.select("img");
			for (Element img : imgs) {
				if (img.attr("src") != "") {
					return img.attr("src");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean checkVideoImg(String content) {
		try {
			Element ele = Jsoup.parse(content);
			return this.checkVideoImg(ele);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean checkVideoImg(Element ele) {
		try {
			Elements eles = ele.select("video");
			if (eles != null && eles.size() > 0) {
				Elements imgEles = ele.select("img");
				if (imgEles == null || imgEles.size() == 0) {
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public String getSummary(String content) {
		String text = Jsoup.clean(content, Whitelist.none());
		return text.length() <= Constants.NEWS_SUMMARY_MAX_LENGTH.intValue ? text
				: text.substring(0, Constants.NEWS_SUMMARY_MAX_LENGTH.intValue);
	}

	@Override
	public void filter(String content) throws Exception {
		// 正文长度过滤
		if (StringUtils.isNUll(content) || content.length() < Constants.NEWS_CONTENT_MIN_LENGTH.intValue) {
			throw new Exception("新闻内容异常，正文长度不足50字符");
		}
		// 视频新闻过滤
		if (this.checkVideoImg(content)) {
			throw new Exception("视频新闻，无图片");
		}
	}

}
