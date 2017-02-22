package com.sshine.huochexing.bean;

import java.io.Serializable;

//旅行车次类
public class Travel implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean isRequested;   //是否已向服务器端请求过数据
	private int msgType;            //信息类型，0为未发车，1为在车上，2为已到目的站，3为列车还没有从起始站发车
	private int sourceType;          //信息源类型，0为用时间区间推算而得，1为用用户信息反馈而得
	private String nativeId;        //本地数据库表中的相应记录的id
	private String serverId;   //服务器表中的相应记录的id
	private int Uid;           //用户id，现用于区分模拟数据
	private String travelName;  //旅行代号
	private String trainNum;   //车次
	private String startStation; //出发站
	private String endStation;  //目的站
	private String r_Date;  //总耗时
	private String startLongitude;   //出发站经度
	private String startLatitude;   //出发站纬度
	private int receiveMsg;  // 是否接收消息
	private int receivedReminder;   //是否已接收过提醒
	private int isRepeatReminder;  //是否进行重复提醒
	private String trainStatus; //列车状态
	private String longitude;  //经度
	private String latitude;  //纬度
	private int stationSpace;  //距离出发站的站距
	private long lateTime;  //晚点时间，毫秒
	private String startTime;  //正点发车时间(包括日期)
	private String endTime;    //正点到站时间(包括日期)
	private String t_StartTime;  //车次从起始站的发车时间
	private String predictTime;  //预计发车时间
	private String remainingTime;   //剩余时间
	private String userStatusRange;  //乘车状态可选项范围，用逗号分隔
	private int userStatus;  //乘车状态
	private int userAddTrain;  //添加此车次的人数
	private int userOnTrain;   //当前在此车次上的人数
	
	public static final String[] USER_STATUS = {"未上车","已上车","已下车"};
	
	public String getTravelName() {
		return travelName;
	}
	public void setTravelName(String travelName) {
		this.travelName = travelName;
	}
	public String getTrainNum() {
		return trainNum;
	}
	public void setTrainNum(String trainNum) {
		this.trainNum = trainNum;
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
	public String getR_Date() {
		return r_Date;
	}
	public void setR_Date(String r_Date) {
		this.r_Date = r_Date;
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
	public String getPredictTime() {
		return predictTime;
	}
	public void setPredictTime(String predictStartTime) {
		this.predictTime = predictStartTime;
	}
	public String getRemainingTime() {
		return remainingTime;
	}
	public void setRemainingTime(String remainingTime) {
		this.remainingTime = remainingTime;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getUserStatusArray() {
		return userStatusRange;
	}
	public void setUserStatusArray(String userStatusArray) {
		this.userStatusRange = userStatusArray;
	}
	public String getT_startTime() {
		return t_StartTime;
	}
	public void setT_startTime(String t_startTime) {
		this.t_StartTime = t_startTime;
	}
	public boolean isRequested() {
		return isRequested;
	}
	public void setRequested(boolean isRequested) {
		this.isRequested = isRequested;
	}
	public String getNativeId() {
		return nativeId;
	}
	public void setNativeId(String nativeId) {
		this.nativeId = nativeId;
	}
	public String getServerId() {
		return serverId;
	}
	public void setServerId(String serverId) {
		this.serverId = serverId;
	}
	public int getMsgType() {
		return msgType;
	}
	public void setMsgType(int msgType) {
		this.msgType = msgType;
	}
	public int getStationSpace() {
		return stationSpace;
	}
	public void setStationSpace(int stationSpace) {
		this.stationSpace = stationSpace;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public int getSourceType() {
		return sourceType;
	}
	public void setSourceType(int sourceType) {
		this.sourceType = sourceType;
	}
	public int getUserStatus() {
		return userStatus;
	}
	public void setUserStatus(int userStatus) {
		this.userStatus = userStatus;
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
	public String getUserStatusRange() {
		return userStatusRange;
	}
	public void setUserStatusRange(String userStatusRange) {
		this.userStatusRange = userStatusRange;
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
	public int getUid() {
		return Uid;
	}
	public void setUid(int uid) {
		Uid = uid;
	}
	public int getIsRepeatReminder() {
		return isRepeatReminder;
	}
	public void setIsRepeatReminder(int isRepeatReminder) {
		this.isRepeatReminder = isRepeatReminder;
	}
	public long getLateTime() {
		return lateTime;
	}
	public void setLateTime(long lateTime) {
		this.lateTime = lateTime;
	}
}
