package com.sshine.huochexing.bean;

public class TravelBrief {
	private int u_id;   //用户id
	private String trainNum;   //车次
	private String travelName;  //旅行代号
	private String startStation; //出发站
	private String endStation;  //目的站
	private String startTime;  //正点发车时间(包括日期)
	private String endTime;    //正点到站时间(包括日期)
	private String t_StartTime;   //车次发车时间
	private String r_Date;  //总耗时
	private String startLongitude;   //出发站经度
	private String startLatitude;   //出发站纬度
	private int receiveMsg;  // 是否接收消息
	private int receivedReminder;   //是否已接收过提醒
	private int isRepeatReminder;  //是否进行重复提醒
	
	public int getU_id() {
		return u_id;
	}
	public void setU_id(int u_id) {
		this.u_id = u_id;
	}
	public String getTrainNum() {
		return trainNum;
	}
	public void setTrainNum(String trainNum) {
		this.trainNum = trainNum;
	}
	public String getTravelName() {
		return travelName;
	}
	public void setTravelName(String travelName) {
		this.travelName = travelName;
	}
	public String getStartStation() {
		return startStation;
	}
	public void setStartStation(String startStation) {
		this.startStation = startStation;
	}
	public String getEndStation() {
		return endStation;
	}
	public void setEndStation(String endStation) {
		this.endStation = endStation;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getR_Date() {
		return r_Date;
	}
	public void setR_Date(String r_Date) {
		this.r_Date = r_Date;
	}
	public String getT_StartTime() {
		return t_StartTime;
	}
	public void setT_StartTime(String t_StartTime) {
		this.t_StartTime = t_StartTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getStartLongitude() {
		return startLongitude;
	}
	public void setStartLongitude(String startLongitude) {
		this.startLongitude = startLongitude;
	}
	public String getStartLatitude() {
		return startLatitude;
	}
	public void setStartLatitude(String startLatitude) {
		this.startLatitude = startLatitude;
	}
	public int getReceiveMsg() {
		return receiveMsg;
	}
	public void setReceiveMsg(int receiveMsg) {
		this.receiveMsg = receiveMsg;
	}
	public int getReceivedReminder() {
		return receivedReminder;
	}
	public void setReceivedReminder(int receivedReminder) {
		this.receivedReminder = receivedReminder;
	}
	public int getIsRepeatReminder() {
		return isRepeatReminder;
	}
	public void setIsRepeatReminder(int isRepeatReminder) {
		this.isRepeatReminder = isRepeatReminder;
	}
}
