package com.sshine.huochexing.bean;

import com.google.gson.annotations.Expose;

/**
 * 退票时用到的实体类
 * @author tp7309
 * 2014-4-7
 *
 */
public class TicketAffirmPDTOInfo {
	@Expose
	private String passenger_name;
	@Expose
	private String total_times;
	
	public String getPassenger_name() {
		return passenger_name;
	}
	public void setPassenger_name(String passenger_name) {
		this.passenger_name = passenger_name;
	}
	public String getTotal_times() {
		return total_times;
	}
	public void setTotal_times(String total_times) {
		this.total_times = total_times;
	}
}
