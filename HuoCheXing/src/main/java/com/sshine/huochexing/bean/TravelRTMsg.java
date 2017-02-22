package com.sshine.huochexing.bean;

public class TravelRTMsg {
	private int msgType; //信息类型
	private String trainStatus; //列车状态
	private String longitude;  //经度
	private String latitude;  //纬度
	private int stationSpace;  //距离出发站的站距
	private String lateTime;  //晚点时间
	private int userAddTrain;  //添加此车次的人数
	private int userOnTrain;   //当前在此车次上的人数
	
	public int getMsgType() {
		return msgType;
	}
	public void setMsgType(int msgType) {
		this.msgType = msgType;
	}
	public String getTrainStatus() {
		return trainStatus;
	}
	public void setTrainStatus(String trainStatus) {
		this.trainStatus = trainStatus;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public int getStationSpace() {
		return stationSpace;
	}
	public void setStationSpace(int stationSpace) {
		this.stationSpace = stationSpace;
	}
	public String getLateTime() {
		return lateTime;
	}
	public void setLateTime(String lateTime) {
		this.lateTime = lateTime;
	}
	public int getUserAddTrain() {
		return userAddTrain;
	}
	public void setUserAddTrain(int userAddTrain) {
		this.userAddTrain = userAddTrain;
	}
	public int getUserOnTrain() {
		return userOnTrain;
	}
	public void setUserOnTrain(int userOnTrain) {
		this.userOnTrain = userOnTrain;
	}
}
