package com.isport.bean;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "newsinfo")
public class NewsInfoBean {
	@Id
	private String id;
	private String source;// 来源
	private String source_icon; // 来源图标
	@Indexed
	private String title; // 标题
	private List<String> title_imgs = new ArrayList<String>(); // 标题图片
	private String index_url; // 入口页url(父级url)
	private String url; // 原始链接
	private String pub_date; // 发布时间 yyyy-MM-dd HH:ss
	private String author; // 原始作者
	private String author_img; // 原始作者头像
	private String author_id; // 系统用户ID（采集源配置中设置）
	private String content; // 内容
	private String summary; // 摘要
	@Indexed
	private String key_word; // 网站原始标签(eg.穆林 勇士 NBA)
	private String html; // 原始网站html
	private String parse_error; // 解析异常
	private String tag; // 分析标签（采集源配置可设置初始指定标签）
	private String remark;// 备注
	private String create_date;// 采集时间
	@Indexed
	private int dispose_state;// 处理状态 0-未处理 1-处理成功 2-处理失败
	private Integer data_type; // 数据类型 0:新闻资讯1:快讯 2:视频
	private String channel_id; // 频道ID（采集源配置中设置）
	private String video_url; // 视频url

	public String getVideo_url() {
		return video_url;
	}

	public void setVideo_url(String video_url) {
		this.video_url = video_url;
	}

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

	public List<String> getTitle_imgs() {
		return title_imgs;
	}

	public void setTitle_imgs(List<String> title_imgs) {
		this.title_imgs = title_imgs;
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

	public String getPub_date() {
		return pub_date;
	}

	public void setPub_date(String pub_date) {
		this.pub_date = pub_date;
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

	public String getAuthor_id() {
		return author_id;
	}

	public void setAuthor_id(String author_id) {
		this.author_id = author_id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
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

	public Integer getData_type() {
		return data_type;
	}

	public void setData_type(Integer data_type) {
		this.data_type = data_type;
	}

	public String getChannel_id() {
		return channel_id;
	}

	public void setChannel_id(String channel_id) {
		this.channel_id = channel_id;
	}

	@Override
	public String toString() {
		return "NewsInfoBean [id=" + id + ", source=" + source + ", source_icon=" + source_icon + ", title=" + title
				+ ", title_imgs=" + title_imgs + ", content=" + ", index_url=" + index_url + ", url=" + url
				+ ", pub_date=" + pub_date + ", author=" + author + ", author_img=" + author_img + ", key_word="
				+ key_word + ", remark=" + remark + ", create_date=" + create_date + ", dispose_state=" + dispose_state
				+ ", redio_url= " + video_url + "]";
	}

}
