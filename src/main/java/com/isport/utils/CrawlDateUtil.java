package com.isport.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class CrawlDateUtil {
	// 时间转换 分钟前/小时前/月日
	public static long timeConvert(final String strDate) throws ParseException {
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
		String strhm = strDate.substring(dayIndex + 1, endIndex).trim();
		String strTime = strMonth + "-" + strDay + " " + strhm;
		SimpleDateFormat sdf2 = new SimpleDateFormat("MM-dd HH:mm");
		return sdf2.parse(strTime).getTime();
	}

	// 时间转换 分钟前/小时前/天前/月前
	public static long timeConvert2(final String strDate) throws ParseException {
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

		final int dayIndex = strDate.indexOf("天前");
		if (dayIndex > 0) {
			// 截取天数
			String strDay = strDate.substring(0, dayIndex);
			int day = Integer.valueOf(strDay);
			String time = DateUtils.getbeforeDay(day);
			return sdf.parse(time).getTime();
		}
		final int monthIndex = strDate.indexOf("个月前");
		if (monthIndex > 0) {
			// 截取月数
			String strMonth = strDate.substring(0, monthIndex);
			int month = Integer.valueOf(strMonth);
			String time = DateUtils.getbeforeMon(month, "yyyy-MM-dd HH:mm:ss");
			return sdf.parse(time).getTime();
		}
		return 0L;
	}
	
	//2018年03月24日 10:04 转换为 2018-03-24 10:04
	public static String dateConvert3(final String date) {
		String strDate = date;
		strDate = strDate.replace("年", "-");
		strDate = strDate.replace("月", "-");
		strDate = strDate.replace("日", "");
		return strDate;
	}
	
	//2018/10/4 18:58:13 转换为 2018-10-4 18:58:13
	public static String dateConvert4(final String date) {
		String strDate = date;
		strDate =strDate.replace("/", "-");
		return strDate;
	}
}
