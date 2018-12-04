package com.isport;
public enum Constants {
	FAILURL_STATUS(500),
	FAILURL_DESC("失败"),
	SUCCESS_STATUS(200),
	SUCCESS_DESC("成功"),
	LOGIN_FAIL_STATUS(201),
	LOGIN_FAIL_DESC("用户未登陆"),
	BUSINESS_FAIL_STATUS(202),
	BUSINESS_FAIL_STATUS_DESC("业务异常"),
	//分页纪录数
    PAGE_SIZE(30),
	ISPORT_ES_INDEX("isport_es_index"),
	ISPORT_ES_TYPE("news"),
	
	NEWS_CONTENT_MIN_LENGTH(50),
	NEWS_SUMMARY_MAX_LENGTH(150),
	
	NEWS_SOURCE_NETEASE("网易"),
	NEWS_SOURCE_SINA("新浪"),
	NEWS_SOURCE_QQ("腾讯"),
	NEWS_SOURCE_DONGQIUDI("懂球帝"),
	NEWS_SOURCE_HUPU("虎扑"),
	NEWS_SOURCE_ZHIBO8("直播吧"),
	NEWS_SOURCE_SINA_WEIBO("新浪微博"),
	NEWS_SOURCE_FORMULA("野途网"),
	//新增
	NEWS_SOURCE_GOAL("GOAL"),
	NEWS_SOURCE_ONESOCCER("第一足球"),
	NEWS_SOURCE_IQIYI("爱奇艺"),
	NEWS_SOURCE_SOHU("搜狐"),
	NEWS_SOURCE_GUOAN("国安"),
	NEWS_SOURCE_RENHE("人和"),
	NEWS_SOURCE_BJH("百家号"),
	NEWS_SOURCE_YDZX("一点资讯"),
	NEWS_SOURCE_AYK("爱羽客"),
	NEWS_SOURCE_APK("爱乒客"),
	NEWS_SOURCE_TOP147("台球视界"),
	NEWS_SOURCE_XIGUAJI("西瓜集"), 
	NEWS_SOURCE_CCTV("央视网"), 
	NEWS_SOURCE_DIANJINGHU("电竞虎"), 
	//新增
	NEWS_ICON_CCTV("http://sports.cctv.com/favicon.ico"),
	NEWS_ICON_AYK("http://www.aiyuke.com/favicon.ico"),
	NEWS_ICON_YDZX("http://www.yidianzixun.com/favicon.ico"),
	NEWS_ICON_BJH("https://author.baidu.com/favicon.ico"),
	NEWS_ICON_RENHE("http://www.beijingrenhefc.com/images/logo.png"),
	NEWS_ICON_GUOAN(""),
	NEWS_ICON_IQIYI("http://www.ssports.com/favicon.ico"),
	NEWS_ICON_GOAL("https://www.goal.com/rebuild-beta-assets/favicons/favicon-32x32.png?v=3.80.0"),
	NEWS_ICON_ONESOCCER("http://www.1soccer.com/favicon.ico"),
	NEWS_ICON_SOHU("http://sports.sohu.com/favicon.ico"),
	NEWS_ICON_TOP147("https://static.59isport.com/news/top147.ico"),
	NEWS_ICON_XIGUAJI("https://static.59isport.com/news/xiguaji.ico"),
	NEWS_ICON_APK("https://static.59isport.com/news/xiguaji.ico"),
	NEWS_ICON_DIANJINGHU("http://www.dianjinghu.com/static/frontend/images/head-footer/hu.png"),
	
	NEWS_ICON_NETEASE("https://static.59isport.com/news/wy.ico"),
	NEWS_ICON_SINA("https://static.59isport.com/news/sina.ico"),
	NEWS_ICON_QQ("https://static.59isport.com/news/tx.ico"),
	NEWS_ICON_DQD("https://static.59isport.com/news/dqd.ico"),
	NEWS_ICON_HUPU("https://static.59isport.com/news/hp.ico"),
	NEWS_ICON_ZHIBO8("https://static.59isport.com/news/zhibo8.ico"),
	NEWS_ICON_FORMULA("http://static.wildto.com/storage/public/images/logo.png"),
	
	NEWS_BLOCK_ALL("综合"),
	NEWS_BLOCK_HOT("热门"),
	NEWS_BLOCK_FOOTBALL("足球"),
	NEWS_BLOCK_BASKETBALL("篮球"),
	NEWS_BLOCK_GAMES("竞技"),
	NEWS_TAG_ZHONGCHOU("中超"),
	NEWS_TAG_YINGCHOU("英超"),
	NEWS_TAG_XIJIA("西甲"),
	NEWS_TAG_DEJIA("德甲"),
	NEWS_TAG_YIJIA("意甲"),
	NEWS_TAG_WUZHOU("五洲"),
	NEWS_TAG_HOT("推荐"),
	
	NETEASE_NEWSSTYLE_ARTICLE("article"),
	NETEASE_NEWSSTYLE_PHOTOSET("photoset"),
	
	NEWS_DATA_TYPE_XINWEN(0),
	NEWS_DATA_TYPE_KUAIXUN(1),
	NEWS_DATA_TYPE_SHIPIN(2),
	;

	public String value;
	public int intValue;
	private Constants(){
	}
    private Constants(String value){
		this.value=value;
	}
    private Constants(int intValue){
		this.intValue=intValue;
	}
}