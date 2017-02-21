package com.sshine.huochexing.bean;

import java.io.Serializable;

public class QueryLeftNewOptionInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int MODE_NORMAL = 0;  //普通订票模式
	public static final int MODE_RESIGN = 1;  //改签订票模式
	
	private String from_station_telecode;
	private String to_station_telecode;
	private String from_station_name;
	private String to_station_name;
	private boolean isFromExactMatch;
	private boolean isToExactMatch;
	private String departure_time;
	private String return_time;
	private int day_difference;
	private String tour_flag;
	private String ticket_type;
	private String from_time_range;
	private String to_time_range;
	private boolean[] selectedTrainTypeIndexes;
	//查询模式
	private int mode;
	
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
	public String getDeparture_time() {
		return departure_time;
	}
	public void setDeparture_time(String departure_time) {
		this.departure_time = departure_time;
	}
	public String getReturn_time() {
		return return_time;
	}
	public void setReturn_time(String return_time) {
		this.return_time = return_time;
	}
	public String getTicket_type() {
		return ticket_type;
	}
	public void setTicket_type(String ticket_type) {
		this.ticket_type = ticket_type;
	}
	public int getDay_difference() {
		return day_difference;
	}
	public void setDay_difference(int day_difference) {
		this.day_difference = day_difference;
	}
	public boolean isFromExactMatch() {
		return isFromExactMatch;
	}
	public void setFromExactMatch(boolean isFromExactMatch) {
		this.isFromExactMatch = isFromExactMatch;
	}
	public boolean isToExactMatch() {
		return isToExactMatch;
	}
	public void setToExactMatch(boolean isToExactMatch) {
		this.isToExactMatch = isToExactMatch;
	}
	public String getTo_station_name() {
		return to_station_name;
	}
	public void setTo_station_name(String to_station_name) {
		this.to_station_name = to_station_name;
	}
	public String getFrom_station_name() {
		return from_station_name;
	}
	public void setFrom_station_name(String from_station_name) {
		this.from_station_name = from_station_name;
	}
	public boolean[] getSelectedTrainTypeIndexes() {
		return selectedTrainTypeIndexes;
	}
	public void setSelectedTrainTypeIndexes(boolean[] selectedTrainTypeIndexes) {
		this.selectedTrainTypeIndexes = selectedTrainTypeIndexes;
	}
	public String getTo_time_range() {
		return to_time_range;
	}
	public void setTo_time_range(String to_time_range) {
		this.to_time_range = to_time_range;
	}
	public String getFrom_time_range() {
		return from_time_range;
	}
	public void setFrom_time_range(String from_time_range) {
		this.from_time_range = from_time_range;
	}
	public int getMode() {
		return mode;
	}
	public void setMode(int mode) {
		this.mode = mode;
	}
	public String getTour_flag() {
		return tour_flag;
	}
	public void setTour_flag(String tour_flag) {
		this.tour_flag = tour_flag;
	}
}
