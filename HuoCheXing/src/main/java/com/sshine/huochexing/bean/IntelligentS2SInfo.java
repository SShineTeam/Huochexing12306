package com.sshine.huochexing.bean;

import java.util.List;

import com.google.gson.annotations.Expose;

/**
 * 两站可到达车次信息
 * @author tp7309
 * 2014-5-3
 *
 */
public class IntelligentS2SInfo {
	@Expose
	private String from_station_name;
	@Expose
	private String to_station_name;
	@Expose
	private String from_station_telecode;
	@Expose
	private String to_station_telecode;
	@Expose
	private List<IntelligentTrainInfo> lstMatchTrains;  //所有符合条件的车次列表
	private List<QueryLeftNewInfo> lstQLNInfos;
	
	public String getFrom_station_name() {
		return from_station_name;
	}
	public String getTo_station_name() {
		return to_station_name;
	}
	public String getFrom_station_telecode() {
		return from_station_telecode;
	}
	public String getTo_station_telecode() {
		return to_station_telecode;
	}
	public List<QueryLeftNewInfo> getLstQLNInfos() {
		return lstQLNInfos;
	}
	public void setFrom_station_name(String from_station_name) {
		this.from_station_name = from_station_name;
	}
	public void setTo_station_name(String to_station_name) {
		this.to_station_name = to_station_name;
	}
	public void setFrom_station_telecode(String from_station_telecode) {
		this.from_station_telecode = from_station_telecode;
	}
	public void setTo_station_telecode(String to_station_telecode) {
		this.to_station_telecode = to_station_telecode;
	}
	public void setLstQLNInfos(List<QueryLeftNewInfo> lstQLNInfos) {
		this.lstQLNInfos = lstQLNInfos;
	}
}
