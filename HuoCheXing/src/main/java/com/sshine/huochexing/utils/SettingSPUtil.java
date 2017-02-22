package com.sshine.huochexing.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SettingSPUtil {
	private SharedPreferences sp;
	private Editor editor;

	@SuppressLint("CommitPrefEdits")
	public SettingSPUtil(Context context, String file) {
		sp = context.getSharedPreferences(file, Context.MODE_PRIVATE);
		editor = sp.edit();
	}

 	public void setTravelFirstShow(boolean b) {
 		editor.putBoolean("travelFirstShow", b);
 		editor.commit();
 	}

 	public boolean isTravelFirstShow() {
 		return sp.getBoolean("travelFirstShow", true);
 	}
	public void setFirstUse(boolean isFirstUse) {
		editor.putBoolean("isFirstUse", isFirstUse);
		editor.commit();
	}

	public boolean isFirstUse() {
		return sp.getBoolean("isFirstUse", true);
	}
	
	public void setDefaultAtyIndex(int index){
		editor.putInt("defaultAtyIndex", index).commit();
	}
	public int getDefaultAtyIndex(){
		return sp.getInt("defaultAtyIndex" , 0);
	}
	
	//到站提醒设置
	public void setPreReminderTime(long lTime) {
		editor.putLong("preReminderTime", lTime);
		editor.commit();
	}

	public long getPreReminderTime() {
		return sp.getLong("preReminderTime", 60 * 60 * 1000);
	}

	public void setPreReminderTimeString(String str1) {
		editor.putString("preReminderTimeString", str1);
		editor.commit();
	}

	public String getPreReminderTimeString() {
		return sp.getString("preReminderTimeString", "1个小时");
	}

	public void setStartReminder(boolean b) {
		editor.putBoolean("startReminder", b);
		editor.commit();
	}

	public boolean isStartReminder() {
		return sp.getBoolean("startReminder", true);
	}

	public void setEndReminder(boolean b) {
		editor.putBoolean("endReminder", b);
		editor.commit();
	}

	public boolean isEndReminder() {
		return sp.getBoolean("endReminder", true);
	}

	public void setVibrate(boolean b) {
		editor.putBoolean("isVibrate", b);
		editor.commit();
	}

	public boolean isVibrate() {
		return sp.getBoolean("isVibrate", true);
	}
	public void setRing(boolean b){
		editor.putBoolean("isRing", b);
		editor.commit();
	}
	public boolean isRing(){
		return sp.getBoolean("isRing", true);
	}

	//是否有提醒
	public void setReminderSet(boolean b){
		editor.putBoolean("isReminderSet", b);
		editor.commit();
	}
	public boolean isReminderSet(){
		return sp.getBoolean("isReminderSet", true);
	}
	
	//是否已设置Alarm
	public void setAlarmSet(boolean b){
		editor.putBoolean("isAlarmSet", b);
		editor.commit();
	}
	public boolean isAlarmSet(){
		return sp.getBoolean("isAlarmSet", false);
	}
	
	public void setGeofenceId(String id){
		editor.putString("geofenceId", id);
	}
	public String getGeofenceId(){
		return sp.getString("geofenceId", "");
	}
	public void setGeofenceTriggered(boolean b){
		editor.putBoolean("isGeofenceTriggered",b);
		editor.commit();
	}
	public boolean isGeofenceTriggered(){
		return sp.getBoolean("isGeofenceTriggered", false);
	}
	public void setContinueSendLoc(boolean b){
		editor.putBoolean("isContinuteSendLoc", b);
		editor.commit();
	}
	public boolean isContinueSendLoc(){
		return sp.getBoolean("isContinueSendLoc", true);
	}
	
	//安全防盗设置
	public boolean isAntiTheftShowStatus(){
		return sp.getBoolean("isAntiTheftShowStatus", true);
	}
	
	public void setAntiTheftShowStatus(boolean b){
		editor.putBoolean("isAntiTheftShowStatus", b);
		editor.commit();
	}
	public boolean isAntiTheftRing(){
		return sp.getBoolean("isAntiTheftRing", true);
	}
	public void setAntiTheftRing(boolean b){
		editor.putBoolean("isAntiTheftRing", b);
		editor.commit();
	}
	public boolean isAntiTheftVibrate(){
		return sp.getBoolean("isAntiTheftVibrate", true);
	}
	public void setAntiTheftVibrate(boolean b){
		editor.putBoolean("isAntiTheftVibrate", b);
		editor.commit();
	}
	public void setAntiTheftDelayTime(long lTime) {
		editor.putLong("antiTheftDelayTime", lTime);
		editor.commit();
	}

	public long getAntiTheftDelayTime() {
		return sp.getLong("antiTheftDelayTime", 3* 1000);
	}

	public void setAntiTheftDelayTimeString(String str1) {
		editor.putString("antiTheftDelayTimeString", str1);
		editor.commit();
	}

	public String getAntiTheftDelayTimeString() {
		return sp.getString("antiTheftDelayTimeString", "3秒");
	}
	public String getAntiTheftRingtoneUriString(){
		return sp.getString("antiTheftRingtoneUriString", null);
	}
	
	public void setAntiTheftRingtoneUriString(String str1){
		editor.putString("antiTheftRingtoneUriString", str1);
		editor.commit();
	}
	
	public boolean isAntiTheftOpenBTEnhancedMode(){
		return sp.getBoolean("isAntiTheftOpenBTEnhancedMode", true);
	}
	
	public void setAntiTheftOpenBTEnhancedMode(boolean b){
		editor.putBoolean("isAntiTheftOpenBTEnhancedMode", b);
		editor.commit();
	}
	
	public boolean isAntiTheftBTClosedAlarm(){
		return sp.getBoolean("isAntiTheftBTClosedAlarm", true);
	}
	
	public void setAntiTheftBTClosedAlarm(boolean b){
		editor.putBoolean("isAntiTheftBTClosedAlarm", b);
		editor.commit();
	}
	public void setAntiTheftRestSensitivity(int lTime) {
		editor.putInt("antiTheftRestSensitivity", lTime);
		editor.commit();
	}

	public int getAntiTheftRestSensitivity() {
		return sp.getInt("antiTheftRestSensitivity", 100);
	}

	public void setAntiTheftRestSensitivityString(String str1) {
		editor.putString("antiTheftRestSensitivityString", str1);
		editor.commit();
	}
	public String getAntiTheftRestSensitivityString() {
		return sp.getString("antiTheftRestSensitivityString", "中");
	}
	
	
	//LockPattern
	public boolean isLockPattternFirstUse(){
		return sp.getBoolean("isLockPattternFirstUse", true);
	}
	
	public void setLockPatternFirstUse(boolean b){
		editor.putBoolean("isLockPattternFirstUse", b);
		editor.commit();
	}
	
	//TrainSchAty
	public String getLastFromStationKey(){
		return sp.getString("lastFromStationKey", null);
	}
	public void setLastFromStationKey(String str1) {
		editor.putString("lastFromStationKey", str1);
		editor.commit();
	}
	public String getLastFromStationTagStr(){
		return sp.getString("lastFromStationTagStr", null);
	}
	public void setLastFromStationTagStr(String str1) {
		editor.putString("lastFromStationTagStr", str1);
		editor.commit();
	}
	public String getLastToStationKey(){
		return sp.getString("lastToStationKey", null);
	}
	public void setLastToStationKey(String str1) {
		editor.putString("lastToStationKey", str1);
		editor.commit();
	}
	public String getLastToStationTagStr(){
		return sp.getString("lastToStationTagStr", null);
	}
	public void setLastToStationTagStr(String str1) {
		editor.putString("lastToStationTagStr", str1);
		editor.commit();
	}
	public String getLastTrainNumKey(){
		return sp.getString("lastTrainNumKey", null);
	}
	public void setLastTrainNumKey(String str1){
		editor.putString("lastTrainNumKey", str1);
		editor.commit();
	}
	
	public boolean isAutoSubmit(){
		return sp.getBoolean("isAutoSubmit", true);
	}
	public void setAutoSubmit(boolean b){
		editor.putBoolean("isAutoSubmit", b);
		editor.commit();
	}
	
	//聊天设置
	public void setChatVibrate(boolean b) {
		editor.putBoolean("isChatVibrate", b);
		editor.commit();
	}

	public boolean isChatVibrate() {
		return sp.getBoolean("isChatVibrate", true);
	}
	public void setChatRing(boolean b){
		editor.putBoolean("isChatRing", b);
		editor.commit();
	}
	public boolean isChatRing(){
		return sp.getBoolean("isChatRing", true);
	}
	public void setChatNotiOnBar(boolean b){
		editor.putBoolean("isChatNotiOnBar", b);
		editor.commit();
	}
	public boolean isChatNotiOnBar(){
		return sp.getBoolean("isChatNotiOnBar", true);
	}
	public void setChatReceiveMsgAlways(boolean b){
		editor.putBoolean("isChatReceiveMsgAlways", b);
		editor.commit();
	}
	public boolean isChatReceiveMsgAlways(){
		return sp.getBoolean("isChatReceiveMsgAlways", true);
	}
	public void setReceivePublicChatroom(boolean b){
		editor.putBoolean("isReceivePublicChatroom", b);
		editor.commit();
	}
	public boolean isReceivePublicChatroom() {
		return sp.getBoolean("isReceivePublicChatroom", true);
	}
}
