package com.sshine.huochexing.value;

/**
 * 服务器相关地址
 * @author tp7309
 * 2014-4-27
 *
 */
public class ServiceValue {
	public static final String A6_DOMIN = "kyfw.12306.cn";
	public static final String BASE_PATH = "http://huochexing.duapp.com/server/";
	public static final String NODEJS_PATH = "http://huochexing2.duapp.com/server/";
	public static final String P_TRAIN_SCHEDULE = "train_schedule.php";
	
	public static final String TRAIN_INFO = "u_t";
	public static final String TRAIN_SCH = "t_sch";
	
	public static String getUserPath(){
		return NODEJS_PATH+"user";
	}
	
	
}
