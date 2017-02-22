package com.sshine.huochexing.bean;

import java.util.List;

import com.google.gson.annotations.Expose;

public class ConfirmPassengersInfo {
	@Expose
	private boolean isExist;
	@Expose
	private String exMsg;
	@Expose
	private List<PassengerInfo> normal_passengers;
	@Expose
	private String[] dj_passengers;
	
	public boolean isExist() {
		return isExist;
	}
	public void setExist(boolean isExist) {
		this.isExist = isExist;
	}
	public String getExMsg() {
		return exMsg;
	}
	public void setExMsg(String exMsg) {
		this.exMsg = exMsg;
	}
	public String[] getDj_passengers() {
		return dj_passengers;
	}
	public void setDj_passengers(String[] dj_passengers) {
		this.dj_passengers = dj_passengers;
	}
	public List<PassengerInfo> getNormal_passengers() {
		return normal_passengers;
	}
	public void setNormal_passengers(List<PassengerInfo> normal_passengers) {
		this.normal_passengers = normal_passengers;
	}
}
