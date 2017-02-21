package com.sshine.huochexing.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.sshine.huochexing.R;

public class UserInfoSPUtil {
	private  SharedPreferences sp ;
	private Editor editor;
	 
	@SuppressLint("CommitPrefEdits")
	public UserInfoSPUtil(Context context, String file) {
		sp = context.getSharedPreferences(file, Context.MODE_PRIVATE);
		editor = sp.edit();
	}
	/**
	 * 注销用户
	 */
	public void resetUserInfo(){
		editor.remove("uid");
		editor.remove("sex");
		editor.remove("email");
		editor.remove("nickname");
		editor.remove("headIcon");
		editor.remove("point");
		editor.apply();
		this.setLogin(false);
		this.setAutoLogin(false);
	}
	public void setSessionCode(String sessionCode){
		editor.putString("userSessionCode", sessionCode);
		editor.apply();
	}
	public String getSessionCode(){
		return sp.getString("userSessionCode", "");
	}
	public void setUId(int uId){
		editor.putInt("uid", uId);
		editor.apply();
	}
	public int getUId(){
		return sp.getInt("uid", -1);
	}
	public void setUsername(String username){
		editor.putString("username", username);
		editor.apply();
	}
	public String getUsername(){
		return sp.getString("username", "");
	}
	public String getPwd(){
		return sp.getString("pwd", "");
	}
	public void setPwd(String encryptedPwd){
		editor.putString("pwd", encryptedPwd).commit();
	}
	public void setSex(String str1){
		editor.putString("sex", str1);
		editor.apply();
	}
	public String getSex(){
		return sp.getString("sex", "女");
	}
	
	public void setEmail(String email){
		editor.putString("email", email);
		editor.apply();
	}
	public String getEmail(){
		return sp.getString("email", "");
	}
	public void setNickName(String nickName) {
		editor.putString("nickname", nickName);
		editor.apply();
	}
	public void setAutoRegister(boolean b){
		editor.putBoolean("isAutoRegister", b).commit();
	}
	public boolean isAutoRegister(){
		return sp.getBoolean("isAutoRegister", false);
	}
	public String getNickName(){
		return sp.getString("nickname", "火车行用户");
	}
	public void setHeadIcon(String resId) {
		editor.putString("headIcon", resId);
		editor.apply();
	}
	public String getHeadIcon(){
		return sp.getString("headIcon", String.valueOf(R.drawable.head001));
	}
	public void setPoint(int point){
		editor.putInt("point", point);
		editor.apply();
	}
	public int getPoint(){
		return sp.getInt("point", 0);
	}
	public void setBaiduAppId (String appId){
		editor.putString("baiduappid", appId);
		editor.apply();
	}
	public String getBaiduAppId (){
		return sp.getString("baiduappid", "");
	}
	
	public void setBaiduUserId(String userId){
		editor.putString("baiduuserid", userId);
		editor.apply();
	}
	
	public String getBaiduUserId() {
		return sp.getString("baiduuserid", "");
	}
	public void setBaiduChannelId(String ChannelId){
		editor.putString("baiduchannelid", ChannelId);
		editor.apply();
	}
	
	public String getBaiduChannelId() {
		return sp.getString("baiduchannelid", "");
	}
	
	public void setLogin(boolean login){
		editor.putBoolean("login", login);
		editor.apply();
	}
	public boolean isLogin(){
		return sp.getBoolean("login", false);
	}
	
	public boolean isThirdPartyLogin(){
		return sp.getBoolean("isThirdPartyLogin", false);
	}
	public void setThirdPartyLogin(boolean b){
		editor.putBoolean("isThirdPartyLogin", b).commit();
	}
	
	public void setAutoLogin(boolean autoLogin){
		editor.putBoolean("autoLogin", autoLogin);
		editor.apply();
	}
	
	public boolean isAutoLogin(){
		return sp.getBoolean("autoLogin", false);
	}
	
	//未登录用户的方法
	public int getUIdNotLogin(){
		return -1;
	}

	public void setUsernameNotLogin(String username){
		editor.putString("usernameNotLogin", username);
		editor.apply();
	}
	public String getUsernameNotLogin(){
		return sp.getString("usernameNotLogin", "");
	}
	public void setSexNotLogin(String str1){
		editor.putString("sexNotLogin", str1);
		editor.apply();
	}
	public String getSexNotLogin(){
		return sp.getString("sexNotLogin", "女");
	}
	public void setEmailNotLogin(String email){
		editor.putString("emailNotLogin", email);
		editor.apply();
	}
	public String getEmailNotLogin(){
		return sp.getString("emailNotLogin", "");
	}
	public void setRegistTimeNotLogin(String registTime){
		editor.putLong("registtimeNotLogin", Long.parseLong(registTime));
		editor.apply();
	}
	public Long getRegistTimeNotLogin(){
		return sp.getLong("registtimeNotLogin", 0);
	}

	public void setHeadIconNotLogin(String resId) {
		editor.putString("headIconNotLogin", resId);
		editor.apply();
	}
	public String getHeadIconNotLogin(){
		return sp.getString("headIconNotLogin", String.valueOf(R.drawable.head001));
	}

	public void setNickNameNotLogin(String nickName) {
		editor.putString("nicknameNotLogin", nickName);
		editor.apply();
	}
	public String getNickNameNotLogin(){
		return sp.getString("nicknameNotLogin", "火车行用户");
	}
}
