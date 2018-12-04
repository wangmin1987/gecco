package com.isport.crawl.aipingke;

public class test {

	public static void main(String[] args) {
		String url = "http://lol.dianjinghu.com/video/all/18.html";
		String nowPage = url.substring(0,url.lastIndexOf("/")+1);
//		String pubDate= "发布时间:15484";
//		final int dayIndex = pubDate.indexOf(":");
//		pubDate = pubDate.substring(dayIndex+1, pubDate.length()).trim();
		System.out.println(nowPage);
	}
}
