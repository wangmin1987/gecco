package com.isport.crawl.netease;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.isport.crawl.AbstractListPipeLine;
import com.isport.utils.DateUtils;

@Service
public class NtesList extends AbstractListPipeLine {
	private static final Logger LOGGER = LoggerFactory.getLogger(NtesList.class);

	@Override
	protected Object getList(JSONObject jo) {
		return jo.getJSONArray("newsList");
	}

	@Override
	protected long getNewsTime(Object obj) {
		JSONObject item = JSONObject.parseObject(obj.toString());
		String strPubDate = item.getString("pubDate");
//		LOGGER.info(strPubDate);
		try {
			return timeConvert(strPubDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	protected String getNewsDocUrl(String baseUrl, Object obj) {
		JSONObject item = JSONObject.parseObject(obj.toString());
		String docUrl = item.getString("docUrl");
		return docUrl;
	}

	@Override
	protected String getNextUrl(String url, String nextUrl, int page) {
		final int _index = url.indexOf("_");
		final int cronIndex = url.lastIndexOf(".");
		if (_index > 0) {
			String subUrl = url.substring(0, _index);
			if (page < 10) {
				subUrl = subUrl + "_" + "0" + page + ".html";
			} else {
				subUrl = subUrl + "_" + page + ".html";
			}
			return subUrl;
		} else {
			String subUrl = url.substring(0, cronIndex);
			subUrl = subUrl + "_" + "0" + page + ".html";
			return subUrl;
		}
	}

	// 时间转换
	private long timeConvert(final String strDate) throws ParseException {
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

}
