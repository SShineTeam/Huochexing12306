package com.sshine.huochexing.bean;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.Expose;

/**
 * 智能查询上传服务器的信息实体类
 * @author tp7309
 * 2014-5-2
 *
 */
public class IntelligentQueryInfo implements Serializable {
	public static final int MODE_QUERY_HALF_WAY_TICKETS = 0; //查询半途票
	public static final int MODE_QUERY_TRAINSIT_TICKETS = 1; //查询中转票
	
	@Expose
	private int query_mode;  //查询模式
	@Expose
	private String departure_time;
	@Expose
	private String start_station_name;
	@Expose
	private String end_station_name;
	@Expose
	private int max_transfer_num;  //最大中转数，查询半途票为0
	@Expose
	private int min_remain_minute;  //在中转站的最小停留分钟数
	@Expose
	private int max_remain_minute;   //在中转站的最大停留分钟数
	@Expose
	private boolean allow_same_city_trainsfer;    //允许同城中转,默认为false
	
	private List<String> train_types;  //允许的车次类型
	private List<String> seat_types;   //允许的座位类型
	private boolean auto_valid_mode;    //自动验证模式，即自动查询12306相应票务进行对比
	
	public List<String> getTrain_types() {
		return train_types;
	}
	public List<String> getSeat_types() {
		return seat_types;
	}
	public boolean isAuto_valid_mode() {
		return auto_valid_mode;
	}
	public void setTrain_types(List<String> train_types) {
		this.train_types = train_types;
	}
	public void setSeat_types(List<String> seat_types) {
		this.seat_types = seat_types;
	}
	public void setAuto_valid_mode(boolean auto_valid_mode) {
		this.auto_valid_mode = auto_valid_mode;
	}
	public boolean isAllow_same_city_trainsfer() {
		return allow_same_city_trainsfer;
	}
	public void setAllow_same_city_trainsfer(boolean allow_same_city_trainsfer) {
		this.allow_same_city_trainsfer = allow_same_city_trainsfer;
	}
	public String getStart_station_name() {
		return start_station_name;
	}
	public String getEnd_station_name() {
		return end_station_name;
	}
	public int getMax_transfer_num() {
		return max_transfer_num;
	}
	public void setStart_station_name(String start_station_name) {
		this.start_station_name = start_station_name;
	}
	public void setEnd_station_name(String end_station_name) {
		this.end_station_name = end_station_name;
	}
	public void setMax_transfer_num(int max_transfer_num) {
		this.max_transfer_num = max_transfer_num;
	}
	public int getQuery_mode() {
		return query_mode;
	}
	public String getDeparture_time() {
		return departure_time;
	}
	public int getMin_remain_minute() {
		return min_remain_minute;
	}
	public int getMax_remain_minute() {
		return max_remain_minute;
	}
	public void setQuery_mode(int query_mode) {
		this.query_mode = query_mode;
	}
	public void setDeparture_time(String departure_time) {
		this.departure_time = departure_time;
	}
	public void setMin_remain_minute(int min_remain_minute) {
		this.min_remain_minute = min_remain_minute;
	}
	public void setMax_remain_minute(int max_remain_minute) {
		this.max_remain_minute = max_remain_minute;
	}
}
