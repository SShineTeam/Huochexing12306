package com.sshine.huochexing.utils;

import java.util.Date;
import java.util.List;

import org.apache.http.cookie.Cookie;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class A6UserInfoSPUtil {
	private SharedPreferences sp;
	private Editor editor;

	@SuppressLint("CommitPrefEdits")
	public A6UserInfoSPUtil(Context context, String file) {
		sp = context.getSharedPreferences(file, Context.MODE_PRIVATE);
		editor = sp.edit();
	}
	
	public String getUserRealName(){
		return sp.getString("userRealName", null);
	}
	public void setUserRealName(String str1){
		editor.putString("userRealName", str1).commit();
	}

	public String getUserName() {
		return sp.getString("userName", null);
	}
	
	public String getPwd(){
		return sp.getString("pwd", null);
	}
	
	public boolean isLogin(){
		return sp.getBoolean("isLogin", false);
	}
	
	public void saveUserInfo(String strUserName, String strPwd){
		editor.putString("userName", strUserName);
		editor.putString("pwd", strPwd);
		editor.putBoolean("isLogin", true);
		editor.commit();
	}
	
	public void clearUserInfo(){
		editor.putString("userName", null)
			.putString("pwd", null)
			.putBoolean("isLogin", false)
			.commit();
	}
	
	public void setLogin(boolean b){
		editor.putBoolean("isLogin", b).commit();
	}
	
	public void saveCookies(List<Cookie> cookies){
		String strCookie = "";
        Date sessionTime = null;
        
        if (cookies != null && !cookies.isEmpty()) {
            for (int i = 0; i < cookies.size(); i++) {
            	Cookie cookie = cookies.get(i);
            	if (cookie.getName().equalsIgnoreCase("JSESSIONID")){
	            	strCookie += cookie.getName() + "="
		                     + cookie.getValue() + ";domain="
		                     +cookie.getDomain();
		            sessionTime = cookies.get(i).getExpiryDate();
            	}
            }
        }
        editor.putString("cookies", strCookie);
		editor.commit();
		editor.putString("cookiesExpiryDate", (sessionTime == null)?null:TimeUtil.getDTFormat().format(sessionTime));
		editor.commit();
	}
	
	public String getCookiesStr(){
		return sp.getString("cookies", null);
	}
	
	public String getCookiesExpirtyDate(){
		return sp.getString("cookiesExpiryDate", null);
	}

	public long getLastPowerOperateTimeMillis(){
		return sp.getLong("lastPowerOperateTimeMillis", 0);
	}
	public void clearPowerOperateTimeMillis(){
		editor.putLong("lastPowerOperateTimeMillis", 0);
		editor.commit();
	}
	public void updatePowerOperateTimeMillis(){
		editor.putLong("lastPowerOperateTimeMillis", System.currentTimeMillis());
		editor.commit();
	}
}
