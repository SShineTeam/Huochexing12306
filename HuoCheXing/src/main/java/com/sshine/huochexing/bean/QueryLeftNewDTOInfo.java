package com.sshine.huochexing.bean;

import java.io.Serializable;

import com.google.gson.annotations.Expose;

public class QueryLeftNewDTOInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Expose
	private String train_no;
	@Expose
	private String station_train_code;
	@Expose
	private String start_station_telecode;
	@Expose
	private String start_station_name;
	@Expose
	private String end_station_telecode;
	@Expose
	private String end_station_name;
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
	private String canWebBuy;
	@Expose
	private String lishiValue;
	@Expose
	private String yp_info;
	@Expose
	private String control_train_day;
	@Expose
	private String start_train_date;
	@Expose
	private String seat_feature;
	@Expose
	private String yp_ex;
	@Expose
	private String train_seat_feature;
	@Expose
	private String seat_types;
	@Expose
	private String location_code;
	@Expose
	private String from_station_no;
	@Expose
	private String to_station_no;
	@Expose
	private String control_day;
	@Expose
	private String sale_time;
	@Expose
	private String is_support_card;
	@Expose
	private String gg_num;
	@Expose
	private String gr_num;
	@Expose
	private String qt_num;
	@Expose
	private String rw_num;
	@Expose
	private String rz_num;
	@Expose
	private String tz_num;
	@Expose
	private String wz_num;
	@Expose
	private String yb_num;
	@Expose
	private String yw_num;
	@Expose
	private String yz_num;
	@Expose
	private String ze_num;
	@Expose
	private String zy_num;
	@Expose
	private String swz_num;
	private int a_LateTime;
	private int d_LateTime;
	private boolean flag_start;
	private boolean flag_end;
	private String seat_string;
	private int speed_index;
	private boolean hasPreferentialPrice;  //是否有优惠车票价
	
	public String getTrain_no() {
		return train_no;
	}
	public void setTrain_no(String train_no) {
		this.train_no = train_no;
	}
	public String getStation_train_code() {
		return station_train_code;
	}
	public void setStation_train_code(String station_train_code) {
		this.station_train_code = station_train_code;
	}
	public String getStart_station_telecode() {
		return start_station_telecode;
	}
	public void setStart_station_telecode(String start_station_telecode) {
		this.start_station_telecode = start_station_telecode;
	}
	public String getStart_station_name() {
		return start_station_name;
	}
	public void setStart_station_name(String start_station_name) {
		this.start_station_name = start_station_name;
	}
	public String getEnd_station_telecode() {
		return end_station_telecode;
	}
	public void setEnd_station_telecode(String end_station_telecode) {
		this.end_station_telecode = end_station_telecode;
	}
	public String getEnd_station_name() {
		return end_station_name;
	}
	public void setEnd_station_name(String end_station_name) {
		this.end_station_name = end_station_name;
	}
	public String getFrom_station_telecode() {
		return from_station_telecode;
	}
	public void setFrom_station_telecode(String from_station_telecode) {
		this.from_station_telecode = from_station_telecode;
	}
	public String getFrom_station_name() {
		return from_station_name;
	}
	public void setFrom_station_name(String from_station_name) {
		this.from_station_name = from_station_name;
	}
	public String getTo_station_telecode() {
		return to_station_telecode;
	}
	public void setTo_station_telecode(String to_station_telecode) {
		this.to_station_telecode = to_station_telecode;
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
	public String getArrive_time() {
		return arrive_time;
	}
	public void setArrive_time(String arrive_time) {
		this.arrive_time = arrive_time;
	}
	public String getTrain_class_name() {
		return train_class_name;
	}
	public void setTrain_class_name(String train_class_name) {
		this.train_class_name = train_class_name;
	}
	public String getLishi() {
		return lishi;
	}
	public void setLishi(String lishi) {
		this.lishi = lishi;
	}
	public String getCanWebBuy() {
		return canWebBuy;
	}
	public void setCanWebBuy(String canWebBuy) {
		this.canWebBuy = canWebBuy;
	}
	public String getLishiValue() {
		return lishiValue;
	}
	public void setLishiValue(String lishiValue) {
		this.lishiValue = lishiValue;
	}
	public String getYp_info() {
		return yp_info;
	}
	public void setYp_info(String yp_info) {
		this.yp_info = yp_info;
	}
	public String getControl_train_day() {
		return control_train_day;
	}
	public void setControl_train_day(String control_train_day) {
		this.control_train_day = control_train_day;
	}
	public String getStart_train_date() {
		return start_train_date;
	}
	public void setStart_train_date(String start_train_date) {
		this.start_train_date = start_train_date;
	}
	public String getSeat_feature() {
		return seat_feature;
	}
	public void setSeat_feature(String seat_feature) {
		this.seat_feature = seat_feature;
	}
	public String getYp_ex() {
		return yp_ex;
	}
	public void setYp_ex(String yp_ex) {
		this.yp_ex = yp_ex;
	}
	public String getTrain_seat_feature() {
		return train_seat_feature;
	}
	public void setTrain_seat_feature(String train_seat_feature) {
		this.train_seat_feature = train_seat_feature;
	}
	public String getSeat_types() {
		return seat_types;
	}
	public void setSeat_types(String seat_types) {
		this.seat_types = seat_types;
	}
	public String getLocation_code() {
		return location_code;
	}
	public void setLocation_code(String location_code) {
		this.location_code = location_code;
	}
	public String getFrom_station_no() {
		return from_station_no;
	}
	public void setFrom_station_no(String from_station_no) {
		this.from_station_no = from_station_no;
	}
	public String getTo_station_no() {
		return to_station_no;
	}
	public void setTo_station_no(String to_station_no) {
		this.to_station_no = to_station_no;
	}
	public String getControl_day() {
		return control_day;
	}
	public void setControl_day(String control_day) {
		this.control_day = control_day;
	}
	public String getSale_time() {
		return sale_time;
	}
	public void setSale_time(String sale_time) {
		this.sale_time = sale_time;
	}
	public String getIs_support_card() {
		return is_support_card;
	}
	public void setIs_support_card(String is_support_card) {
		this.is_support_card = is_support_card;
	}
	public String getGg_num() {
		return gg_num;
	}
	public void setGg_num(String gg_num) {
		this.gg_num = gg_num;
	}
	public String getGr_num() {
		return gr_num;
	}
	public void setGr_num(String gr_num) {
		this.gr_num = gr_num;
	}
	public String getQt_num() {
		return qt_num;
	}
	public void setQt_num(String qt_num) {
		this.qt_num = qt_num;
	}
	public String getRw_num() {
		return rw_num;
	}
	public void setRw_num(String rw_num) {
		this.rw_num = rw_num;
	}
	public String getRz_num() {
		return rz_num;
	}
	public void setRz_num(String rz_num) {
		this.rz_num = rz_num;
	}
	public String getTz_num() {
		return tz_num;
	}
	public void setTz_num(String tz_num) {
		this.tz_num = tz_num;
	}
	public String getWz_num() {
		return wz_num;
	}
	public void setWz_num(String wz_num) {
		this.wz_num = wz_num;
	}
	public String getYb_num() {
		return yb_num;
	}
	public void setYb_num(String yb_num) {
		this.yb_num = yb_num;
	}
	public String getYw_num() {
		return yw_num;
	}
	public void setYw_num(String yw_num) {
		this.yw_num = yw_num;
	}
	public String getYz_num() {
		return yz_num;
	}
	public void setYz_num(String yz_num) {
		this.yz_num = yz_num;
	}
	public String getZe_num() {
		return ze_num;
	}
	public void setZe_num(String ze_num) {
		this.ze_num = ze_num;
	}
	public String getZy_num() {
		return zy_num;
	}
	public void setZy_num(String zy_num) {
		this.zy_num = zy_num;
	}
	public String getSwz_num() {
		return swz_num;
	}
	public void setSwz_num(String swz_num) {
		this.swz_num = swz_num;
	}
	public boolean isFlag_start() {
		return flag_start;
	}
	public void setFlag_start(boolean flag_start) {
		this.flag_start = flag_start;
	}
	public boolean isFlag_end() {
		return flag_end;
	}
	public void setFlag_end(boolean flag_end) {
		this.flag_end = flag_end;
	}
	public String getSeat_string() {
		return seat_string;
	}
	public void setSeat_string(String seat_string) {
		this.seat_string = seat_string;
	}
	public int getA_LateTime() {
		return a_LateTime;
	}
	public void setA_LateTime(int a_LateTime) {
		this.a_LateTime = a_LateTime;
	}
	public int getD_LateTime() {
		return d_LateTime;
	}
	public void setD_LateTime(int d_LateTime) {
		this.d_LateTime = d_LateTime;
	}
	public int getDay_difference() {
		return day_difference;
	}
	public void setDay_difference(int day_difference) {
		this.day_difference = day_difference;
	}
	public int getSpeed_index() {
		return speed_index;
	}
	public void setSpeed_index(int speed_index) {
		this.speed_index = speed_index;
	}
	public boolean isHasPreferentialPrice() {
		return hasPreferentialPrice;
	}
	public void setHasPreferentialPrice(boolean hasPreferentialPrice) {
		this.hasPreferentialPrice = hasPreferentialPrice;
	}
}
