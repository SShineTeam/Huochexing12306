package com.sshine.huochexing.bean;

import com.google.gson.annotations.Expose;

/**
 * 智能查询时从服务器拿回来的车次详情实体类
 * @author tp7309
 * 2014-5-11
 *
 */
public class IntelligentTrainInfo {
	@Expose
	private String train_no;
	@Expose
	private String from_station_telecode;
	@Expose
	private String from_station_name;
	@Expose
	private String to_station_telecode;
	@Expose
	private String to_station_name;
	@Expose
	private String start_time;
	@Expose
	private String arrive_time;
	@Expose
	private int day_difference;
	@Expose
	private String train_class_name;
	@Expose
	private String lishi;
	@Expose
	private String lishiValue;
	@Expose
	private int a_LateTime;
	@Expose
	private int d_LateTime;
	@Expose
	private boolean flag_start;
	@Expose
	private boolean flag_end;
	@Expose
	private int speed_index;
	public String getTrain_no() {
		return train_no;
	}
	public String getFrom_station_telecode() {
		return from_station_telecode;
	}
	public String getFrom_station_name() {
		return from_station_name;
	}
	public String getTo_station_telecode() {
		return to_station_telecode;
	}
	public String getTo_station_name() {
		return to_station_name;
	}
	public String getStart_time() {
		return start_time;
	}
	public String getArrive_time() {
		return arrive_time;
	}
	public int getDay_difference() {
		return day_difference;
	}
	public String getTrain_class_name() {
		return train_class_name;
	}
	public String getLishi() {
		return lishi;
	}
	public String getLishiValue() {
		return lishiValue;
	}
	public int getA_LateTime() {
		return a_LateTime;
	}
	public int getD_LateTime() {
		return d_LateTime;
	}
	public boolean isFlag_start() {
		return flag_start;
	}
	public boolean isFlag_end() {
		return flag_end;
	}
	public int getSpeed_index() {
		return speed_index;
	}
	public void setTrain_no(String train_no) {
		this.train_no = train_no;
	}
	public void setFrom_station_telecode(String from_station_telecode) {
		this.from_station_telecode = from_station_telecode;
	}
	public void setFrom_station_name(String from_station_name) {
		this.from_station_name = from_station_name;
	}
	public void setTo_station_telecode(String to_station_telecode) {
		this.to_station_telecode = to_station_telecode;
	}
	public void setTo_station_name(String to_station_name) {
		this.to_station_name = to_station_name;
	}
	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}
	public void setArrive_time(String arrive_time) {
		this.arrive_time = arrive_time;
	}
	public void setDay_difference(int day_difference) {
		this.day_difference = day_difference;
	}
	public void setTrain_class_name(String train_class_name) {
		this.train_class_name = train_class_name;
	}
	public void setLishi(String lishi) {
		this.lishi = lishi;
	}
	public void setLishiValue(String lishiValue) {
		this.lishiValue = lishiValue;
	}
	public void setA_LateTime(int a_LateTime) {
		this.a_LateTime = a_LateTime;
	}
	public void setD_LateTime(int d_LateTime) {
		this.d_LateTime = d_LateTime;
	}
	public void setFlag_start(boolean flag_start) {
		this.flag_start = flag_start;
	}
	public void setFlag_end(boolean flag_end) {
		this.flag_end = flag_end;
	}
	public void setSpeed_index(int speed_index) {
		this.speed_index = speed_index;
	}
}
