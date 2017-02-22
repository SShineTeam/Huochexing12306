package com.sshine.huochexing.utils;

import android.util.Log;

import com.sshine.huochexing.BuildConfig;

/*
 * @author way
 */

public class L {
	private static String TAG = "Log";
	private static boolean isDebug = BuildConfig.DEBUG;
	
	public static void v(String msg){
		if (isDebug){
			Log.v(TAG, msg);
		}
	}
	public static void d(String tag, String msg){
		if (isDebug){
			Log.d(tag, msg);
		}
	}
	public static void d(String msg){
		if (isDebug){
			Log.d(TAG, msg);
		}
	}
	public static void i(String msg){
		if (isDebug){
			Log.i(TAG, msg);
		}
	}
	public static void w(String msg){
		if (isDebug){
			Log.w(TAG, msg);
		}
	}
	public static void e(String msg){
		if (isDebug){
			Log.e(TAG, msg);
		}
	}
}
