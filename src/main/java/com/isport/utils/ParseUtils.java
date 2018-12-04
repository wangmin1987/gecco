package com.isport.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ParseUtils {
	
	public static String getHtmlBody(String html) {
		try {
			Document doc = Jsoup.parse(html);
			Element ele = doc.select("div#artibody").first();
			if(ele!=null) {
				return ele.text();
			}
			return "";
		} catch (Exception e) {
			System.out.println(html);
			e.printStackTrace();
			return "";
		}
	}
	public static boolean checkVideo(String html) {
		Document doc = Jsoup.parse(html);
		Elements eles = doc.select("div#artibody video");
		return eles.size() > 0 ? false: true; 
	}


}
