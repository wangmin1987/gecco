package com.isport.crawl.yidian;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.geccocrawler.gecco.request.HttpRequest;
import com.isport.crawl.AbstractListPipeLine;
import com.isport.utils.StringUtils;

@Service
public class YidianList extends AbstractListPipeLine {

	@Override
	protected Object getList(JSONObject jo) throws Exception {
		return jo.getObject("newsList", List.class);
	}

	@Override
	protected long getNewsTime(Object obj) throws Exception {
		// 转换对象为JSONObject
		JSONObject item = JSONObject.parseObject(obj.toString());
		String strPubDate = item.getString("date");
		if (StringUtils.isNUll(strPubDate)) {
			throw new Exception("非新闻内容");
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			// 日期字符串转换为时间戳
			return sdf.parse(strPubDate).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	protected String getNewsDocUrl(String baseUrl, Object obj) {
		// 转换对象为JSONObject
		JSONObject item = JSONObject.parseObject(obj.toString());
		String docId = item.getString("docid");
		return "http://www.yidianzixun.com/article/" + docId;
	}

	@Override
	protected String getNextUrl(String url, String nextUrl, int page) {
		String e = getParamByUrl(url, "channel_id");
		String i = getParamByUrl(url, "cstart");
		String t = getParamByUrl(url, "cend");
		i = t;
		int sum = Integer.valueOf(t) + Integer.valueOf(10);
		t = String.valueOf(sum);
		String _spt = getToken(e, i, t);
		String sub1 = "", sub2 = "";
		int index = _spt.lastIndexOf("~");
		sub1 = _spt.substring(0, index+1); 
		sub2 = _spt.substring(index+1, _spt.length()); 
		try {
			_spt = sub1 + URLEncoder.encode(sub2, "utf-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		// http://www.yidianzixun.com/home/q/news_list_for_channel?channel_id=t147&infinite=true&refresh=1&__from__=pc&multi=5&appid=web_yidian&cstart=7&cend=17&_spt=yz~eaod~%3B%3E%3D%3D%3B%3D
		int sptIndex = url.indexOf("cstart=");
		url = url.substring(0, sptIndex);
		url = url + "cstart=" + i + "&cend=" + t + "&_spt=" + _spt;
		return url;
	}

	@Override
	protected void setRequestParameter(HttpRequest subRequest, Object obj) {
		// 转换对象为JSONObject
		JSONObject item = JSONObject.parseObject(obj.toString());
		String strPubDate = item.getString("date");
		subRequest.addParameter("date", strPubDate);
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

	public static void main(String[] args) throws UnsupportedEncodingException {
		System.out.println(getToken("u10947", "0", "10"));
		String _spt = getToken("u10947", "0", "10");
		String sub1 = "", sub2 = "";
		int index = _spt.lastIndexOf("~");
		sub1 = _spt.substring(0, index+1);
		System.out.println("sub1:" + sub1);
		sub2 = _spt.substring(index+1, _spt.length());
		System.out.println("sub2:" + sub2);
		try {
			_spt = sub1 + URLEncoder.encode(sub2, "utf-8");
			System.out.println(_spt);
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
	}

	public static String getToken(String e, String i, String t) {
		String o = "sptoken", a = "";
		o = o + e;
		o = o + i;
		o = o + t;
		for (int c = 0; c < o.length(); c++) {
			int r = 10 ^ o.charAt(c);
			String b = Character.toString((char) r);
			a = a + b;
		}
		return a;
	}

}
