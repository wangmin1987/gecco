package com.isport.crawl.hupu;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.isport.crawl.AbstractListPipeLine;
import com.isport.utils.DateUtils;
import com.isport.utils.StringUtils;

@Service
public class HupuCSLList extends AbstractListPipeLine {

	@Autowired
	HupuSoccerListPipeline hupuSoccerListPipeline;

	@Override
	protected Object getList(JSONObject jo) {
		return jo.getJSONArray("newsList");
	}

	@Override
	protected long getNewsTime(Object obj) throws Exception {
		JSONObject item = JSONObject.parseObject(obj.toString());
		String strPubDate = item.getString("pubDate");
		if(StringUtils.isNUll(strPubDate)) {
			throw new Exception("新闻目录");
		} 
		return timeConvert(strPubDate);
	}

	@Override
	protected String getNewsDocUrl(String baseUrl, Object obj) {
		JSONObject item = JSONObject.parseObject(obj.toString());
		String docUrl = item.getString("docUrl");
		return docUrl;
	}

	@Override
	protected String getNextUrl(String url, String nextUrl, int page) {
		final int slash = url.lastIndexOf("/");
		return url.substring(0, slash) + "/" + page;
	}

	// 时间转换
	private long timeConvert(final String strDate) throws ParseException {
		// 下标结束地址
		int endIndex = strDate.length();
		// 分钟前的下标
		final int minuteIndex = strDate.indexOf("分钟前");
		// 日期格式化
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// 截取并转换时间
		if (minuteIndex > 0) {
			// 截取分钟值
			String strMinute = strDate.substring(0, minuteIndex);
			int minute = Integer.valueOf(strMinute);
			String time = DateUtils.getbeforeMinute(minute);
			return sdf.parse(time).getTime();
		}
		final int hourIndex = strDate.indexOf("小时前");
		if (hourIndex > 0) {
			// 截取小时
			String strHour = strDate.substring(0, hourIndex);
			int hour = Integer.valueOf(strHour);
			String time = DateUtils.getbeforeHour(hour);
			return sdf.parse(time).getTime();
		}
		// 截取月/日 值
		final int monthIndex = strDate.indexOf("月");
		String strMonth = strDate.substring(0, monthIndex);
		final int dayIndex = strDate.indexOf("日");
		String strDay = strDate.substring(monthIndex + 1, dayIndex);
		String strTime = strMonth + "-" + strDay;
		SimpleDateFormat sdf2 = new SimpleDateFormat("MM-dd");
		return sdf2.parse(strTime).getTime();
	}
}
