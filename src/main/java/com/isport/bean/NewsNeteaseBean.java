package com.isport.bean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

public class NewsNeteaseBean {

	// 新闻标题
	private String title;
	// 文章链接地址
	private String docurl;
	// 评论数
	private Integer tienum;
	// 所属板块
	private String label;
	// 关键字
	private List<Map<String, String>> keywords;
	// 发布时间
	private String time;
	// 新闻类型
	private String newstype;
	// 列表配图：分辨率较低
	private String imgurl;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDocurl() {
		return docurl;
	}

	public void setDocurl(String docurl) {
		this.docurl = docurl;
	}

	public Integer getTienum() {
		return tienum;
	}

	public void setTienum(Integer tienum) {
		this.tienum = tienum;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public List<Map<String, String>> getKeywords() {
		return keywords;
	}

	public void setKeywords(List<Map<String, String>> keywords) {
		this.keywords = keywords;
	}

	public String getTime() {
		SimpleDateFormat olddf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss"),
				newdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		try {
			return newdf.format(olddf.parse(time));
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getNewstype() {
		return newstype;
	}

	public void setNewstype(String newstype) {
		this.newstype = newstype;
	}

	public String getImgurl() {
		return imgurl;
	}

	public void setImgurl(String imgurl) {
		this.imgurl = imgurl;
	}

}
