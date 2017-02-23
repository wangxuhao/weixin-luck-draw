package com.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
	public static int YEAR = 0;
	public static int MONTH = 1;
	public static int DATE = 2;
	public static int HOUR = 3;
	public static int MINUTE = 4;
	public static int SECOND = 5;

	/**
	 * 将yyyy-MM-dd格式的日期字符串转换成Date类型
	 * 
	 * @param dateStr
	 * @return
	 */
	public static Date parseDate(String dateStr) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		try {
			return format.parse(dateStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 格式化日期类型为yyyyMMdd格式字符串
	 * 
	 * @param date
	 * @return
	 */
	public static String formatDate(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		return format.format(date);
	}

	/**
	 * 将日期转换指定格式的字符串
	 * 
	 * @param date
	 * @param dateFormat
	 * @return
	 */
	public static String formatDate(Date date, String dateFormat) {
		SimpleDateFormat format = new SimpleDateFormat(dateFormat);
		return format.format(date);
	}

	/**
	 * 获取某年某月的总天数
	 * 
	 * @param year
	 * @param month
	 * @return
	 */
	public static int getMaxDateOfMonth(int year, int month) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month - 1);
		cal.set(Calendar.DATE, 1);// 把日期设置为当月第一天
		cal.roll(Calendar.DATE, -1);// 日期回滚一天，也就是最后一天
		int MaxDate = cal.get(Calendar.DATE);
		return MaxDate;
	}

	/**
	 * 获取某年某月的总天数
	 * 
	 * @param date
	 *            日期格式yyyy-MM或yyyy-MM-dd或yyyy-MM-dd HH:mm:ss
	 * @return
	 */
	public static int getMaxDateOfMonth(String date) {
		int year = Integer.parseInt(date.substring(0, date.indexOf("-")));
		int month = 0;
		if (date.indexOf("-") == date.lastIndexOf("-")) {
			month = Integer.parseInt(date.substring(date.indexOf("-") + 1));
		} else {
			month = Integer.parseInt(date.substring(date.indexOf("-") + 1, date.lastIndexOf("-")));
		}

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month - 1);
		cal.set(Calendar.DATE, 1);// 把日期设置为当月第一天
		cal.roll(Calendar.DATE, -1);// 日期回滚一天，也就是最后一天
		int MaxDate = cal.get(Calendar.DATE);
		return MaxDate;
	}

	/**
	 * 获取日期某部分
	 * 
	 * @param file
	 * @param date
	 *            yyyy-MM-dd HH:mm:ss 或 yyyy-MM 或 yyyy-MM
	 * @return
	 */
	public static String getDatePart(int file, String date) {
		date = date.trim();
		// if(date.indexOf(" ") == -1 && date.indexOf(":") == -1){
		// date = date + " 00:00:00";
		// }
		String part = null;
		switch (file) {
		case 0:// 年
			part = date.substring(0, date.indexOf("-"));
			break;
		case 1:// 月
			if (date.indexOf("-") == date.lastIndexOf("-")) {
				part = date.substring(date.indexOf("-") + 1);
			} else {
				part = date.substring(date.indexOf("-") + 1, date.lastIndexOf("-"));
			}
			break;
		case 2:// 日
			if (date.indexOf(" ") != -1) {
				part = date.substring(date.lastIndexOf("-") + 1, date.indexOf(" "));
			} else {
				part = date.substring(date.lastIndexOf("-") + 1);
			}
			break;
		case 3:// 时
			part = date.substring(date.indexOf(" ") + 1, date.indexOf(":"));
			break;
		case 4:// 分
			part = date.substring(date.indexOf(":") + 1, date.lastIndexOf(":"));
			break;
		case 5:// 秒
			part = date.substring(date.lastIndexOf(":") + 1);
			break;
		}
		return part;
	}

	/**
	 * 获取上一个月份
	 * 
	 * @param month
	 *            当前月
	 * @return 上一个月
	 */
	public static int[] getLastMonth(int year, int month) {
		int[] time = new int[2];
		if (month == 1) {
			time[0] = year - 1;
			time[1] = 12;
		} else {
			time[0] = year;
			time[1] = month - 1;
		}
		return time;
	}

	/**
	 * 获取上个月
	 * 
	 * @param date
	 * @return yyyy-MM
	 */
	public static String getUpperMonth(String date) {
		int year = Integer.parseInt(DateUtil.getDatePart(DateUtil.YEAR, date));
		int month = Integer.parseInt(DateUtil.getDatePart(DateUtil.MONTH, date));
		int[] time = DateUtil.getLastMonth(year, month);
		if (time[1] < 10) {
			return time[0] + "-0" + time[1];
		} else {
			return time[0] + "-" + time[1];
		}
	}

	/**
	 * 获取上一个月
	 * 
	 * @param date
	 * @return yyyy-MM-dd
	 */
	public static String getLastMonth(String date) {
		int year = Integer.parseInt(DateUtil.getDatePart(DateUtil.YEAR, date));
		int month = Integer.parseInt(DateUtil.getDatePart(DateUtil.MONTH, date));
		String day = DateUtil.getDatePart(DateUtil.DATE, date);
		int[] time = DateUtil.getLastMonth(year, month);

		if (time[1] < 10) {
			return time[0] + "-0" + time[1] + "-" + day;
		} else {
			return time[0] + "-" + time[1] + "-" + day;
		}
	}

	/**
	 * 获取当前月信息
	 * 
	 * @param date
	 *            yyyy-MM-dd、yyyy-MM
	 * @return 字符串数组：0、月份的第一天yyyy-MM-01；1、月份的最后一天yyyy-MM-dd；2、月份yyyy-MM
	 */
	public static String[] getCurrentMonth(String date) {
		int year = Integer.parseInt(DateUtil.getDatePart(DateUtil.YEAR, date));
		int month = Integer.parseInt(DateUtil.getDatePart(DateUtil.MONTH, date));
		String monthStr = month < 10 ? "0" + month : month + "";
		String[] time = new String[3];
		time[0] = year + "-" + monthStr + "-01";
		int maxDate = DateUtil.getMaxDateOfMonth(year, month);
		String maxDateStr = maxDate < 10 ? "0" + maxDate : maxDate + "";
		time[1] = year + "-" + monthStr + "-" + maxDateStr;
		time[2] = year + "-" + monthStr;
		return time;
	}

	/**
	 * 获取上一月份信息
	 * 
	 * @param date
	 *            yyyy-MM-dd、yyyy-MM
	 * @return 字符串数组：0、月份的第一天yyyy-MM-01；1、月份的最后一天yyyy-MM-dd；2、月份yyyy-MM
	 */
	public static String[] getUpMonth(String date) {
		date = DateUtil.getUpperMonth(date);
		int year = Integer.parseInt(DateUtil.getDatePart(DateUtil.YEAR, date));
		int month = Integer.parseInt(DateUtil.getDatePart(DateUtil.MONTH, date));
		String monthStr = month < 10 ? "0" + month : month + "";
		String[] time = new String[3];
		time[0] = year + "-" + monthStr + "-01";
		int maxDate = DateUtil.getMaxDateOfMonth(year, month);
		String maxDateStr = maxDate < 10 ? "0" + maxDate : maxDate + "";
		time[1] = year + "-" + monthStr + "-" + maxDateStr;
		time[2] = year + "-" + monthStr;
		return time;
	}

	/**
	 * 毫秒数转为24制字符串
	 * @param timeMillis
	 * @return
	 */
	public static String parseTimeMillis2TimeStamp(long timeMillis) {
		Date date = new Date(timeMillis);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String timeStr = format.format(date);
		return timeStr;
	}

	public static String parseTimeMillis2Time(long timeMillis) {
		Date date = new Date(timeMillis);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String dateStr = format.format(date);
		return dateStr;
	}
}
