package com.sshine.huochexing.bean;

import com.google.gson.annotations.Expose;

public class TrainAvgLTInfo {
	@Expose
	private String trainNum;
	@Expose
	private int d_LateTime;
	@Expose
	private int a_LateTime;
	
	public int getD_LateTime() {
		return d_LateTime;
	}
	public void setD_LateTime(int d_LateTime) {
		this.d_LateTime = d_LateTime;
	}
	public int getA_LateTime() {
		return a_LateTime;
	}
	public void setA_LateTime(int a_LateTime) {
		this.a_LateTime = a_LateTime;
	}
	public String getTrainNum() {
		return trainNum;
	}
	public void setTrainNum(String trainNum) {
		this.trainNum = trainNum;
	}
}
