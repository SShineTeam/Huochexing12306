package com.sshine.huochexing.bean;

public class TrainBrief {

	private String trainNum;   //车次
	private String a_Time;  //到达时间
	private String d_Time;  //发车时间
	private String r_Date;  //历时
	private String cfRunTime;  //距起始站的总耗时
	private String startLongitude;   //出发站经度
	private String startLatitude;   //出发站纬度
	
	public String getTrainNum() {
		return trainNum;
	}
	public void setTrainNum(String trainNum) {
		this.trainNum = trainNum;
	}
	public String getA_Time() {
		return a_Time;
	}
	public void setA_Time(String a_Time) {
		this.a_Time = a_Time;
	}
	public String getD_Time() {
		return d_Time;
	}
	public void setD_Time(String d_Time) {
		this.d_Time = d_Time;
	}
	public String getR_Date() {
		return r_Date;
	}
	public void setR_Date(String r_Date) {
		this.r_Date = r_Date;
	}
	public String getCFRunTime() {
		return cfRunTime;
	}
	public void setCFRunTime(String cfRunTime) {
		this.cfRunTime = cfRunTime;
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
	
}
