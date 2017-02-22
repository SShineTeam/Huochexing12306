package com.sshine.huochexing.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.annotation.SuppressLint;

@SuppressLint("SimpleDateFormat")
public class TimeUtil {
	private static final SimpleDateFormat sdfDT = new SimpleDateFormat("yyyy-MM-dd HH:mm",Locale.CHINA);
	private static final SimpleDateFormat sdfD = new SimpleDateFormat("yyyy-MM-dd",Locale.CHINA);
	private static final SimpleDateFormat sdfT = new SimpleDateFormat("HH:mm",Locale.CHINA);
	
	public static SimpleDateFormat getDTFormat(){
		return sdfDT;
	}
	public static SimpleDateFormat getDFormat(){
		return sdfD;
	}
	public static SimpleDateFormat getTFormat(){
		return sdfT;
	}
	public static int getIntervalDays(Date fDate, Date oDate) {
	       if (null == fDate || null == oDate) {
	           return -1;
	       }
	       long intervalMilli = oDate.getTime() - fDate.getTime();
	       return (int) (intervalMilli / (24 * 60 * 60 * 1000));
	    }
	
	public static String getTime(long time) {
		SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd HH:mm",Locale.getDefault());
		return format.format(new Date(time));
	}

	public static String getHourAndMin(long time) {
		SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.getDefault());
		return format.format(new Date(time));
	}

	public static String getChatTime(long timesamp) {
		String result = "";
		SimpleDateFormat sdf = new SimpleDateFormat("dd");
		Date today = new Date(System.currentTimeMillis());
		Date otherDay = new Date(timesamp);
		int temp = Integer.parseInt(sdf.format(today))
				- Integer.parseInt(sdf.format(otherDay));

		switch (temp) {
		case 0:
			result = "今天 " + getHourAndMin(timesamp);
			break;
		case 1:
			result = "昨天 " + getHourAndMin(timesamp);
			break;
		case 2:
			result = "前天 " + getHourAndMin(timesamp);
			break;

		default:
			// result = temp + "天前 ";
			result = getTime(timesamp);
			break;
		}

		return result;
	}

	/**
	 * 输入HH::mm格式时间字符串的差，返回"HH:mm"格式字符串
	 * 
	 * @param str0
	 * @param str1
	 * @return str1-str2
	 */
	public static String getFmt_T_T_DiffStr(String str0, String str1) {
		String retValue = "";
		try {
			String[] strs1 = str0.split(":");
			int m1 = Integer.valueOf(strs1[0]) * 60 + Integer.valueOf(strs1[1]);
			String[] strs2 = str1.split(":");
			int m2 = Integer.valueOf(strs2[0]) * 60 + Integer.valueOf(strs2[1]);
			if (m1 < m2){
				m1 += 24*60;
			}
			int intHH = (m1 - m2) / 60;
			int intMM = m1 - m2 - intHH * 60;
			retValue = String.format("%02d", intHH) + ":" + String.format("%02d", intMM);
		} catch (Exception e) {
			retValue = "";
		}
		return retValue;
	}
	
	/**
	 * 输入秒数，返回"HH小时mm分"或"mm分"格式字符串
	 * @param s
	 * @return
	 */
	public static String get_T_Str(int s){
		String retValue = "";
		int intHH = s/3600;
		int intMM = (s-intHH*3600)/60;
		if (intHH == 0) {
			retValue = intMM + "分";
		} else if (intMM == 0) {
			retValue = intHH + "小时";
		} else {
			retValue = intHH + "小时" + intMM + "分";
		}
		return retValue;
	}
	
	/**
	 * 输入秒数，返回MM:ss
	 * @param s
	 * @return
	 */
	public static String getFmt_M_S_Str(long s){
		if (s == 0){
			return "00:00";
		}else{
			long intMM = s/60;
			long intSS = s-intMM*60;
			return String.format("%02d", intMM)+":"+String.format("%02d", intSS);
		}
	}

	/**
	 * 输入HH::MM格式时间字符串，返回"HH小时mm分"或"mm分"格式字符串
	 * 
	 * @param str1
	 * @return
	 */
	public static String get_T_Str(String str1) {
		String retValue = "";
		try {
			String[] strs1 = str1.split(":");
			int intHH = Integer.valueOf(strs1[0]);
			int intMM = Integer.valueOf(strs1[1]);
			if (intHH == 0) {
				retValue = intMM + "分";
			} else if (intMM == 0) {
				retValue = intHH + "小时";
			} else {
				retValue = intHH + "小时" + intMM + "分";
			}
		} catch (Exception e) {
			retValue = "";
		}
		return retValue;
	}
	
	/**
	 * 将毫秒数转换为格式化后的时间,查询晚点专用
	 * @param s
	 * @return "HH:mm"格式
	 */
	public static String getFmt_MSeconds_TStr(long mseconds){
		long seconds = mseconds/1000;
		long hh = seconds/3600;
		long mm = (seconds - hh*3600)/60;
		String str11 = "";
		if (hh < 10){
			str11 += 0;
		}
		str11 += hh + ":";
		if (mm < 10){
			str11 += 0;
		}
		str11 += mm;
		return str11;
	}
	
	/**
	 * 输入毫秒数返回"yyyy-MM-dd"格式字符串
	 * @param mseconds
	 * @return
	 */
	public static String getFmt_DT_Str(long mseconds){
		Calendar c = Calendar.getInstance(Locale.getDefault());
		c.setTimeInMillis(mseconds);
		return sdfDT.format(c.getTime());
	}

	/**
	 * 返回格式化的日期时间字串
	 * 
	 * @param strDateTime0
	 *            日期时间,"yyyy-MM-dd HH:mm"
	 * @param strTime1
	 *            时间,"HH:mm"
	 * @return strDateTime1-strTime2,返回"yyyy-MM-dd"格式字符串
	 */
	public static String getFmt_DT_T_DiffStr_GetD(String strDateTime0,
			String strTime1) {
		SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
		Date date1 = null;
		try {
			date1 = df1.parse(strDateTime0);
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		Calendar c = Calendar.getInstance(Locale.getDefault());
		c.setTime(date1);
		String[] strTimes = strTime1.split(":");
		c.add(Calendar.HOUR_OF_DAY, -Integer.valueOf(strTimes[0]));
		c.add(Calendar.MINUTE, -Integer.valueOf(strTimes[1]));
		SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd",
				Locale.getDefault());
		return df2.format(c.getTime());
	}

	/**
	 * 返回格式化的日期时间字串
	 * 
	 * @param strDateTime0
	 *            日期时间,"yyyy-MM-dd HH:mm"
	 * @param strTime1
	 *            时间,"HH:mm"
	 * @return strDateTime0+strTime1返回"yyyy-MM-dd HH:mm"格式字符串
	 */
	public static String getFmt_DT_T_SumStr(String strDateTime0, String strTime1) {
		SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm",
				Locale.getDefault());
		Date date1 = null;
		try {
			date1 = df1.parse(strDateTime0);
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		Calendar c = Calendar.getInstance(Locale.getDefault());
		c.setTime(date1);
		String[] strTimes2 = strTime1.split(":");
		c.add(Calendar.HOUR_OF_DAY, Integer.valueOf(strTimes2[0]));
		c.add(Calendar.MINUTE, Integer.valueOf(strTimes2[1]));
		SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm",
				Locale.getDefault());
		return df2.format(c.getTime());
	}

	public static String getFmt_DT_DT_DiffStr(String strDateTime0,
			String strDateTime1) {
		SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm",
				Locale.getDefault());
		Date date0 = null;
		Date date1 = null;
		try {
			date0 = df1.parse(strDateTime0);
			date1 = df1.parse(strDateTime1);
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		long l = date0.getTime() - date1.getTime();
		long lHH = l / (1000 * 3600);
		long lMM = (l - lHH * 1000 * 3600) / (1000 * 60);
		String hh = "", mm = "";
		if (lHH < 10) {
			hh = "0" + lHH;
		} else {
			hh = "" + lHH;
		}
		if (lMM < 10) {
			mm = "0" + lMM;
		} else {
			mm = "" + lMM;
		}
		return (hh + ":" + mm);
	}
	
	public static long getDTMSeconds(String strDT1){
		SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
		long lMSeconds = 0;
		try {
			Date date1 = df1.parse(strDT1);
			lMSeconds = date1.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return lMSeconds;
	}
	
	/**
	 * 输入"HH:mm"格式字符串，得到毫秒数
	 * @param str1
	 * @return
	 */
	public static long get_T_MSeconds(String str1){
		long lMillis = 0;
		try {
			String[] strs1 = str1.split(":");
			int intHH = Integer.valueOf(strs1[0]);
			int intMM = Integer.valueOf(strs1[1]);
			lMillis = intHH*3600*1000 + intMM*60*1000;
		}catch(Exception e){
			e.printStackTrace();
		}
		return lMillis;
	}
	
	/**
	 * 将"HH:mm"字符串附加上当前日期(日期中时间为零)
	 * @param str1 "HH:mm"
	 * @return Date对象
	 */
	public static Date getAddCurr_D(String str1){
		String strDate = sdfD.format(new Date()) + " " + str1;
		try {
			return sdfDT.parse(strDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * 与当前时间相比较，如果比当前时间晚则返回true
	 * @param str1 "HH:mm"格式字符串
	 */
	public static boolean isLaterThanNow(String str1){
		Calendar c = Calendar.getInstance(Locale.getDefault());
		c.setTime(new Date());
		double nowTime = Double.valueOf(c.get(Calendar.HOUR_OF_DAY) + "." + c.get(Calendar.MINUTE));
		String[] strs1 = str1.split(":");
		int intHH = Integer.valueOf(strs1[0]);
		int intMM = Integer.valueOf(strs1[1]);
		double dTime = Double.valueOf(intHH + "." + intMM);
		if (dTime > nowTime){
			return true;
		}else{
			return false;
		}
	}
	public static String getWeek(Date date){
		final String[] dayNames = { "周日", "周一", "周二", "周三", "周四", "周五",
			"周六" };
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		
		int dayOfWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
		if (dayOfWeek < 0) {
			dayOfWeek = 0;
		}
		return dayNames[dayOfWeek];
	}
	// 根据日期字串取得星期
	public static String getWeek(String strDate) {
		Date date = null;
		try {
			date = sdfD.parse(strDate);
			return getWeek(date);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}
}
