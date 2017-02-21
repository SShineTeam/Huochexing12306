package com.sshine.huochexing.bean;

import java.io.Serializable;
import java.util.List;

public class MonitorInfo implements Serializable, Cloneable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private String from_station_telecode;
	private String to_station_telecode;
	private String from_station_name;
	private String to_station_name;
	private String start_time;
	private String purpose_codes;  //学生、成人
	private List<String> lstTrainNames;
	private List<String> lstTrainTimeRanges;
	private boolean[] selectedTrainsNames;
	private List<Integer> lstSeatTypes;
	private int monitorSpeed;
	private List<Integer> lstPNativeIndexes;
	private boolean isRing;
	private boolean isVibrate;
	private int nativeIndex;
	private boolean isRunning;
	private int status;
	private String startMonitorTime;
	private int retryCount;
	private long lastMonitorMillis;
	
	public String getFrom_station_telecode() {
		return from_station_telecode;
	}
	public void setFrom_station_telecode(String from_station_telecode) {
		this.from_station_telecode = from_station_telecode;
	}
	public String getTo_station_telecode() {
		return to_station_telecode;
	}
	public void setTo_station_telecode(String to_station_telecode) {
		this.to_station_telecode = to_station_telecode;
	}
	public String getFrom_station_name() {
		return from_station_name;
	}
	public void setFrom_station_name(String from_station_name) {
		this.from_station_name = from_station_name;
	}
	public String getTo_station_name() {
		return to_station_name;
	}
	public void setTo_station_name(String to_station_name) {
		this.to_station_name = to_station_name;
	}
	public String getStart_time() {
		return start_time;
	}
	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}
	public int getMonitorSpeed() {
		return monitorSpeed;
	}
	public void setMonitorSpeed(int monitorSpeed) {
		this.monitorSpeed = monitorSpeed;
	}
	public boolean isRing() {
		return isRing;
	}
	public void setRing(boolean isRing) {
		this.isRing = isRing;
	}
	public boolean isVibrate() {
		return isVibrate;
	}
	public void setVibrate(boolean isVibrate) {
		this.isVibrate = isVibrate;
	}
	public List<Integer> getLstSeatTypes() {
		return lstSeatTypes;
	}
	public void setLstSeatTypes(List<Integer> lstSeatTypes) {
		this.lstSeatTypes = lstSeatTypes;
	}
	public List<Integer> getLstPNativeIndexes() {
		return lstPNativeIndexes;
	}
	public void setLstPNativeIndexes(List<Integer> lstPNativeIndexes) {
		this.lstPNativeIndexes = lstPNativeIndexes;
	}
	public List<String> getLstTrainNames() {
		return lstTrainNames;
	}
	public void setLstTrainNames(List<String> lstTrainNames) {
		this.lstTrainNames = lstTrainNames;
	}
	public List<String> getLstTrainTimeRanges() {
		return lstTrainTimeRanges;
	}
	public void setLstTrainTimeRanges(List<String> lstTrainTimeRanges) {
		this.lstTrainTimeRanges = lstTrainTimeRanges;
	}
	public boolean[] getSelectedTrainsNames() {
		return selectedTrainsNames;
	}
	public void setSelectedTrainsNames(boolean[] selectedTrainsNames) {
		this.selectedTrainsNames = selectedTrainsNames;
	}
	public int getNativeIndex() {
		return nativeIndex;
	}
	public void setNativeIndex(int nativeIndex) {
		this.nativeIndex = nativeIndex;
	}
	public boolean isRunning() {
		return isRunning;
	}
	public void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}
	public int getRetryCount() {
		return retryCount;
	}
	public void setRetryCount(int retryCount) {
		this.retryCount = retryCount;
	}
	public long getLastMonitorMillis() {
		return lastMonitorMillis;
	}
	public void setLastMonitorMillis(long lastMonitorMillis) {
		this.lastMonitorMillis = lastMonitorMillis;
	}
	public String getStartMonitorTime() {
		return startMonitorTime;
	}
	public void setStartMonitorTime(String startMonitorTime) {
		this.startMonitorTime = startMonitorTime;
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getPurpose_codes() {
		return purpose_codes;
	}
	public void setPurpose_codes(String purpose_codes) {
		this.purpose_codes = purpose_codes;
	}
}
