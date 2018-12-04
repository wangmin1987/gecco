package com.isport.bean;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "information")
public class InformationBean {
	private String id;
	private String source;// 来源
	private String source_icon; // 来源图标
	private String title; // 标题
	private List<String> title_img; // 标题图片
	private String index_url; // 入口页url(父级url)
	private String url; // 原始链接
	private String pubDate; // 发布时间 yyyy-MM-dd HH:ss
	private String author; // 原始作者
	private String author_img; // 原始作者头像
	private String user_id;// 系统用户id
	private String content;// 内容
	private String key_word;// 网站原始标签(eg.穆林 勇士 NBA)
	private String html; // 原始网站html
	private String parse_error;// 解析异常
	private String tag; // 分析标签
	private String remark;// 备注
	private String create_date;// 采集时间
	private int dispose_state;// 处理状态
	private int data_type;// 数据类型 0:新闻资讯1:快讯 2:视频

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getSource_icon() {
		return source_icon;
	}

	public void setSource_icon(String source_icon) {
		this.source_icon = source_icon;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<String> getTitle_img() {
		return title_img;
	}

	public void setTitle_img(List<String> title_img) {
		this.title_img = title_img;
	}

	public String getIndex_url() {
		return index_url;
	}

	public void setIndex_url(String index_url) {
		this.index_url = index_url;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getPubDate() {
		return pubDate;
	}

	public void setPubDate(String pubDate) {
		this.pubDate = pubDate;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getAuthor_img() {
		return author_img;
	}

	public void setAuthor_img(String author_img) {
		this.author_img = author_img;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getKey_word() {
		return key_word;
	}

	public void setKey_word(String key_word) {
		this.key_word = key_word;
	}

	public String getHtml() {
		return html;
	}

	public void setHtml(String html) {
		this.html = html;
	}

	public String getParse_error() {
		return parse_error;
	}

	public void setParse_error(String parse_error) {
		this.parse_error = parse_error;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getCreate_date() {
		return create_date;
	}

	public void setCreate_date(String create_date) {
		this.create_date = create_date;
	}

	public int getDispose_state() {
		return dispose_state;
	}

	public void setDispose_state(int dispose_state) {
		this.dispose_state = dispose_state;
	}

	public int getData_type() {
		return data_type;
	}

	public void setData_type(int data_type) {
		this.data_type = data_type;
	}

}
