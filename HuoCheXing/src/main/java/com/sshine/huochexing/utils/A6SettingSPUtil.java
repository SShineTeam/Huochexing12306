package com.sshine.huochexing.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class A6SettingSPUtil {
	private SharedPreferences sp;
	private Editor editor;

	@SuppressLint("CommitPrefEdits")
	public A6SettingSPUtil(Context context, String file) {
		sp = context.getSharedPreferences(file, Context.MODE_PRIVATE);
		editor = sp.edit();
	}
	public void setDefaultRegisterUserName(String str1){
		editor.putString("defaultRegisterUserName", str1).commit();
	}
	public String getDefaultRegisterUserName(){
		return sp.getString("defaultRegisterUserName", null);
	}
	public void setDefaultRegisterPwd(String str1){
		editor.putString("defaultRegisterPwd", str1).commit();
	}
	public String getDefaultRegisterPwd(){
		return sp.getString("defaultRegisterPwd", null);
	}
	public void setDefaultRegisterEmail(String str1){
		editor.putString("defaultRegisterEmail", str1).commit();
	}
	public String getDefaultRegisterEmail(){
		return sp.getString("defaultRegisterEmail", null);
	}
}
