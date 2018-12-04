package com.isport.utils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;

public class DateUtils {

	public static final SimpleDateFormat DF_YMDHMS = new SimpleDateFormat("yyyyMMddHH:mm:ss");

	public static final SimpleDateFormat DF_YMD = new SimpleDateFormat("yyyyMMdd");
	public static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static final SimpleDateFormat SDF2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	public static final SimpleDateFormat GMT = new SimpleDateFormat("EEE MMM dd HH:mm:ss +0800 yyyy", Locale.ENGLISH);

	public static Date getSystemDateTime() {
		return new Date();
	}

	public static Date getCurrentDate() {
		try {
			return DateUtils.SDF.parse(DateUtils.SDF.format(new Date()));
		} catch (Exception e) {
		}
		return null;
	}

	public static Timestamp getCurrentTimestamp() {
		try {
			return new Timestamp(new Date().getTime());
		} catch (Exception e) {
		}
		return null;
	}

	public static String getCurrentTime() {
		try {
			return DateUtils.SDF2.format(new Date());
		} catch (Exception e) {
		}
		return null;
	}

	public static Date getDateFromStrYYYYMMDDhhmm(String strDate) {

		Date ret = null;
		try {
			String[] parsePatterns = { "yyyy-MM-dd HH:mm:ss" };
			ret = org.apache.commons.lang.time.DateUtils.parseDate(strDate, parsePatterns);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return ret;
	}

	public static Date getDateFromStrYYYYMMDDhhmmSSS(String strDate) {

		Date ret = null;
		try {
			String[] parsePatterns = { "yyyy-MM-dd HH:mm:ss:SSS" };
			ret = org.apache.commons.lang.time.DateUtils.parseDate(strDate, parsePatterns);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return ret;
	}

	/**
	 * yyyy-MM-dd to Date
	 * 
	 * @param strDate
	 * @return
	 */
	public static Date getDateFromStrYYYYMMDD(String strDate) throws Exception {

		Date ret = null;
		String[] parsePatterns = { "yyyy-MM-dd" };
		ret = org.apache.commons.lang.time.DateUtils.parseDate(strDate, parsePatterns);
		return ret;
	}

	public static Date getDateFromStrYYYYMMDD2(String strDate) {

		Date ret = null;
		try {
			String[] parsePatterns = { "yyyyMMdd" };
			ret = org.apache.commons.lang.time.DateUtils.parseDate(strDate, parsePatterns);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return ret;
	}

	public static String getStrYYYYMMDDhhmm(Date date) {
		return DateFormatUtils.format(date, "yyyy-MM-dd HH:mm:ss");
	}

	public static String getStrYYYYMMDDHHmmss(Date date) {
		if (date == null)
			return "";
		return DateFormatUtils.format(new Timestamp(date.getTime()), "yyyy-MM-dd HH:mm:ss");
	}

	public static String getStrYYYYMMDDHHmmT(long date) {

		return DateFormatUtils.format(new Timestamp(date * 1000), "yyyy-MM-dd HH:mm");
	}

	public static String getStrYYYYMMDDHHmmss(long date) {

		return DateFormatUtils.format(new Timestamp(date * 1000), "yyyy-MM-dd HH:mm:ss");
	}

	public static String getStrYYYYMMDDHHmmssSSS(Date date) {
		if (date == null)
			return "";
		return DateFormatUtils.format(new Timestamp(date.getTime()), "yyyy-MM-dd HH:mm:ss:SSS");
	}

	public static String getStrYYYYMMDD(Date date) {
		return DateFormatUtils.format(date, "yyyy-MM-dd");
	}

	public static String getStrYYYYMMDD(Timestamp timeStamp) {
		return DateFormatUtils.format(timeStamp, "yyyy-MM-dd");
	}

	public static String getStrYYYYMMDDHHmmss(Timestamp timeStamp) {
		return DateFormatUtils.format(timeStamp, "yyyy-MM-dd HH:mm:ss");
	}

	public static String getStryyyyMMdd(Date date) {
		return DF_YMD.format(date);
	}

	public static String getStrYYYYMMDDHHmmssByGMT(String date) {
		try {
			return getStrYYYYMMDDHHmmss(GMT.parse(date));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return "";
	}

	public static Date getBatchDate() {
		Date SysDate = getSystemDateTime();
		String StrSysDate = getStrYYYYMMDDhhmm(SysDate);
		Date FormatDate = getDateFromStrYYYYMMDDhhmm(StrSysDate);
		return FormatDate;
	}

	public static Date getDateFromStr(Date date) {
		String StrSysDate = getStrYYYYMMDDhhmm(date);
		Date FormatDate = getDateFromStrYYYYMMDDhhmm(StrSysDate);
		return FormatDate;
	}

	public static Date getDate(String year, String month, String day, String time) {
		try {

			if (month.length() <= 1)
				month = "0" + month;

			if (day.length() <= 1)
				day = "0" + day;

			return DF_YMDHMS.parse(year + month + day + time);
		} catch (ParseException e) {
			return null;
		}
	}

	public static Timestamp getTimestamp(String year, String month, String day, String time) {
		try {

			if (StringUtils.isBlank(year) || StringUtils.isBlank(month) || StringUtils.isBlank(day)
					|| StringUtils.isBlank(time))
				return null;

			// Month
			if (month.trim().equalsIgnoreCase("jan") || month.trim().equalsIgnoreCase("january")) {
				month = "1";
			} else if (month.trim().equalsIgnoreCase("feb") || month.trim().equalsIgnoreCase("february")) {
				month = "2";
			} else if (month.trim().equalsIgnoreCase("mar") || month.trim().equalsIgnoreCase("march")) {
				month = "3";
			} else if (month.trim().equalsIgnoreCase("apr") || month.trim().equalsIgnoreCase("april")) {
				month = "4";
			} else if (month.trim().equalsIgnoreCase("may") || month.trim().equalsIgnoreCase("may")) {
				month = "5";
			} else if (month.trim().equalsIgnoreCase("jun") || month.trim().equalsIgnoreCase("june")) {
				month = "6";
			} else if (month.trim().equalsIgnoreCase("jul") || month.trim().equalsIgnoreCase("july")) {
				month = "7";
			} else if (month.trim().equalsIgnoreCase("aug") || month.trim().equalsIgnoreCase("august")) {
				month = "8";
			} else if (month.trim().equalsIgnoreCase("sep") || month.trim().equalsIgnoreCase("september")) {
				month = "9";
			} else if (month.trim().equalsIgnoreCase("oct") || month.trim().equalsIgnoreCase("october")) {
				month = "10";
			} else if (month.trim().equalsIgnoreCase("nov") || month.trim().equalsIgnoreCase("november")) {
				month = "11";
			} else if (month.trim().equalsIgnoreCase("dec") || month.trim().equalsIgnoreCase("december")) {
				month = "12";
			}

			return new Timestamp(getDate(year, month, day, time).getTime());
		} catch (Exception e) {
			return null;
		}
	}

	public static Date getDate(String year, String month, String day) {
		try {

			if (month.length() <= 1)
				month = "0" + month;

			if (day.length() <= 1)
				day = "0" + day;

			return DF_YMD.parse(year + month + day);
		} catch (ParseException e) {
			return null;
		}
	}

	public static java.sql.Date getSqlDate(String year, String month, String day) {
		try {

			// Month
			if (month.trim().equalsIgnoreCase("jan") || month.trim().equalsIgnoreCase("january")) {
				month = "1";
			} else if (month.trim().equalsIgnoreCase("feb") || month.trim().equalsIgnoreCase("february")) {
				month = "2";
			} else if (month.trim().equalsIgnoreCase("mar") || month.trim().equalsIgnoreCase("march")) {
				month = "3";
			} else if (month.trim().equalsIgnoreCase("apr") || month.trim().equalsIgnoreCase("april")) {
				month = "4";
			} else if (month.trim().equalsIgnoreCase("may") || month.trim().equalsIgnoreCase("may")) {
				month = "5";
			} else if (month.trim().equalsIgnoreCase("jun") || month.trim().equalsIgnoreCase("june")) {
				month = "6";
			} else if (month.trim().equalsIgnoreCase("jul") || month.trim().equalsIgnoreCase("july")) {
				month = "7";
			} else if (month.trim().equalsIgnoreCase("aug") || month.trim().equalsIgnoreCase("august")) {
				month = "8";
			} else if (month.trim().equalsIgnoreCase("sep") || month.trim().equalsIgnoreCase("september")) {
				month = "9";
			} else if (month.trim().equalsIgnoreCase("oct") || month.trim().equalsIgnoreCase("october")) {
				month = "10";
			} else if (month.trim().equalsIgnoreCase("nov") || month.trim().equalsIgnoreCase("november")) {
				month = "11";
			} else if (month.trim().equalsIgnoreCase("dec") || month.trim().equalsIgnoreCase("december")) {
				month = "12";
			}

			if (month.length() <= 1)
				month = "0" + month;

			if (day.length() <= 1)
				day = "0" + day;

			return new java.sql.Date(DF_YMD.parse(year + month + day).getTime());
		} catch (ParseException e) {
			return null;
		}
	}

	public static Timestamp getTimestamp(String year, String month, String day) {
		try {

			if (StringUtils.isBlank(year) || StringUtils.isBlank(month) || StringUtils.isBlank(day))
				return null;

			return new Timestamp(getDate(year, month, day).getTime());
		} catch (Exception e) {
			return null;
		}
	}

	public static java.sql.Date getBeginWeek(java.sql.Date date) {
		try {

			if (DateFormatUtils.format(date, "E", Locale.US).equalsIgnoreCase("Mon")) {
				return date;
			}

			Calendar cd = Calendar.getInstance();
			java.sql.Date monday = null;
			cd.setTime(date);

			for (int i = -1; i > -7; i--) {
				cd.add(Calendar.DATE, i);

				monday = new java.sql.Date(cd.getTimeInMillis());

				if (DateFormatUtils.format(monday, "E", Locale.US).equalsIgnoreCase("Mon")) {
					break;
				}
			}

			return monday;
		} catch (Exception e) {
			return null;
		}
	}

	public static java.sql.Date getBeginMonth(java.sql.Date date) {
		try {

			String strDate = DateFormatUtils.format(date, "yyyyMMdd");

			if (strDate.substring(6).equals("01")) {
				return date;
			}

			return getSqlDate(strDate.substring(0, 4), strDate.substring(4, 6), "01");
		} catch (Exception e) {
			return null;
		}
	}

	public static Date ddMMMyyyyToyyyyMMddDate(String str) throws ParseException {

		Date date = new Date();

		SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);

		date = sdf.parse(str);

		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");

		return java.sql.Date.valueOf(sdf2.format(date));
	}

	public static Date addDay(Date date, int day) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, day);
		return cal.getTime();
	}

	public static Date datetimeToDate(Date datetime) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(datetime);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		return cal.getTime();
	}

	/**
	 * 将日期格式化，字符串输出
	 * 
	 * @param date   日期类型
	 * @param format 正则表达式
	 * @return
	 */
	public static String dateToString(Date date, String pattern) {
		String result = "";
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		if (date != null) {
			result = sdf.format(date);
		}
		return result;
	}

	// 字符串转换成时间
	public static Date stringToDate(String str, String pattern) {
		Date date = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(pattern);
			date = sdf.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * 获取当前日期的下一天
	 * 
	 * @param args
	 */
	public static String getTomorrowDate(String str, String pattern) {
		Date date = stringToDate(str, pattern);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		date = calendar.getTime();
		String torrowDay = dateToString(date, pattern);
		return torrowDay;
	}

	/**
	 * 根据出生日期计算年龄
	 * 
	 * @param birthDay
	 * @return 未来日期返回0
	 * @throws Exception
	 */
	public static int getAge(Date birthDay) {

		if (birthDay == null)
			return -1;

		Calendar cal = Calendar.getInstance();

		if (cal.before(birthDay)) {
			return 0;
		}

		int yearNow = cal.get(Calendar.YEAR);
		int monthNow = cal.get(Calendar.MONTH);
		int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);
		cal.setTime(birthDay);

		int yearBirth = cal.get(Calendar.YEAR);
		int monthBirth = cal.get(Calendar.MONTH);
		int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);

		int age = yearNow - yearBirth;

		if (monthNow <= monthBirth) {
			if (monthNow == monthBirth) {
				if (dayOfMonthNow < dayOfMonthBirth) {
					age--;
				}
			} else {
				age--;
			}
		}

		return age;
	}

	public static int getAge(Timestamp birthDay) {
		return getAge(new Date(birthDay.getTime()));
	}

	public static String format(String fmt, Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat(fmt);
		return sdf.format(date);
	}

	public static Date parse(String date, String pattern) {
		Date result;
		try {
			result = org.apache.commons.lang.time.DateUtils.parseDate(date, new String[] { pattern });
		} catch (Exception e) {
			result = new Date(0);
		}
		return result;
	}

	/**
	 * 根据出生日期计算年龄
	 * 
	 * @param strBirthDay 字符串型日期
	 * @param format      日期格式
	 * @return 未来日期返回0
	 * @throws           java.text.ParseException
	 * @throws Exception
	 */
	public static int getAge(String strBirthDay, String format) throws ParseException {

		DateFormat df = new SimpleDateFormat(format);
		Date birthDay = df.parse(strBirthDay);
		return getAge(birthDay);
	}

	public static String getbeforeN(int n) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -n); // 得到前一天
		Date date = calendar.getTime();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		return df.format(date);
	}

	public static String getbeforeNyyyyMMddHHmmss(int n) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -n); // 得到前一天
		Date date = calendar.getTime();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return df.format(date);
	}

	public static String getbeforeNyyyyMMdd(String instrdate, int n) {
		Date indate = stringToDate(instrdate, "yyyy-MM-dd");
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(indate);
		calendar.add(Calendar.DATE, -n); // 得到前一天
		Date date = calendar.getTime();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		return df.format(date);
	}

	public static String getbeforeN(String instrdate, int n) {
		Date indate = stringToDate(instrdate, "yyyy-MM-dd HH:mm:ss");
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(indate);
		calendar.add(Calendar.DATE, -n); // 得到前一天
		Date date = calendar.getTime();
		DateFormat df = new SimpleDateFormat("yyyyMMdd");
		return df.format(date);
	}

	public static String getbeforeNMinute(String instrdate, int n) {
		Date indate = stringToDate(instrdate, "yyyy-MM-dd HH:mm:ss");
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(indate);
		calendar.add(Calendar.MINUTE, -n); // 得到前一天
		Date date = calendar.getTime();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return df.format(date);
	}

	public static String getbeforeM(int n) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, -n); // 得到前一天
		Date date = calendar.getTime();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		return df.format(date);
	}

	public static String getbeforeMinute(int n) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MINUTE, -n); // 得到前一天
		Date date = calendar.getTime();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return df.format(date);
	}

	public static String getbeforeHour(int n) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.HOUR, -n); // 得到前一天
		Date date = calendar.getTime();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return df.format(date);
	}

	public static String getbeforeMon(int n) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, -n); // 得到前一月
		Date date = calendar.getTime();
		DateFormat df = new SimpleDateFormat("yyyy-MM");
		return df.format(date);
	}

	@SuppressWarnings("unused")
	public static long getDatePoor(Date endDate, Date nowDate) {

		long nd = 1000 * 24 * 60 * 60;
		long nh = 1000 * 60 * 60;
		long nm = 1000 * 60;
		// long ns = 1000;
		// 获得两个时间的毫秒时间差异
		long diff = endDate.getTime() - nowDate.getTime();
		// 计算差多少天
		long day = diff / nd;
		// 计算差多少小时
		long hour = diff / nh;
		// 计算差多少分钟
		long min = diff % nd % nh / nm;
		// 计算差多少秒//输出结果
		// long sec = diff % nd % nh % nm / ns;
		return hour;
	}

	public static void main(String[] args) {
		System.out.println(DateUtils.getbeforeHour(20));
		System.out.println(DateUtils.getbeforeMinute(3));
		System.out.println(DateUtils.getbeforeM(3));
		System.out.println(DateUtils.getbeforeDay(2));
	}
	//获取几天前的具体时间
	public static String getbeforeDay(int day) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -day); // 得到几天前
		Date date = calendar.getTime();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return df.format(date);
	} 
	//获取几个月前时间
	public static String getbeforeMon(int n,String pattern) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, -n); // 得到前一月
		Date date = calendar.getTime();
		DateFormat df = new SimpleDateFormat(pattern);
		return df.format(date);
	}
}
