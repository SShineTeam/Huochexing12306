package com.sshine.huochexing.bean;

import com.google.gson.annotations.Expose;

public class TicketInfo {
	@Expose
	private StationTrainDTOInfo stationTrainDTO;
	@Expose
	private PassengerDTOInfo passengerDTO;
	@Expose
	private String ticket_no;
	@Expose
	private String sequence_no;
	@Expose
	private String batch_no;
	@Expose
	private String train_date;
	@Expose
	private String coach_no;
	@Expose
	private String coach_name;
	@Expose
	private String seat_no;
	@Expose
	private String seat_name;
	@Expose
	private String seat_flag;
	@Expose
	private String seat_type_code;
	@Expose
	private String seat_type_name;
	@Expose
	private String ticket_type_code;
	@Expose
	private String ticket_type_name;
	@Expose
	private String reserve_time;
	@Expose
	private String limit_time;
	@Expose
	private String lose_time;
	@Expose
	private String pay_limit_time;
	@Expose
	private float ticket_price;
	@Expose
	private String print_eticket_flag;
	@Expose
	private String resign_flag;
	@Expose
	private String return_flag;
	@Expose
	private String confirm_flag;
	@Expose
	private String pay_mode_code;
	@Expose
	private String ticket_status_code;
	@Expose
	private String ticket_status_name;
	@Expose
	private String cancel_flag;
	@Expose
	private String amount_char;
	@Expose
	private String trade_mode;
	@Expose
	private String start_train_date_page;
	@Expose
	private String str_ticket_price_page;
	@Expose
	private String come_go_traveller_ticket_page;
	
	//是否要退票
	private boolean isWantReturn;
	//是否要改签
	private boolean isWantResign;
	
	public StationTrainDTOInfo getStationTrainDTO() {
		return stationTrainDTO;
	}
	public void setStationTrainDTO(StationTrainDTOInfo stationTrainDTO) {
		this.stationTrainDTO = stationTrainDTO;
	}
	public PassengerDTOInfo getPassengerDTO() {
		return passengerDTO;
	}
	public void setPassengerDTO(PassengerDTOInfo passengerDTO) {
		this.passengerDTO = passengerDTO;
	}
	public String getTicket_no() {
		return ticket_no;
	}
	public void setTicket_no(String ticket_no) {
		this.ticket_no = ticket_no;
	}
	public String getSequence_no() {
		return sequence_no;
	}
	public void setSequence_no(String sequence_no) {
		this.sequence_no = sequence_no;
	}
	public String getBatch_no() {
		return batch_no;
	}
	public void setBatch_no(String batch_no) {
		this.batch_no = batch_no;
	}
	public String getTrain_date() {
		return train_date;
	}
	public void setTrain_date(String train_date) {
		this.train_date = train_date;
	}
	public String getCoach_no() {
		return coach_no;
	}
	public void setCoach_no(String coach_no) {
		this.coach_no = coach_no;
	}
	public String getCoach_name() {
		return coach_name;
	}
	public void setCoach_name(String coach_name) {
		this.coach_name = coach_name;
	}
	public String getSeat_no() {
		return seat_no;
	}
	public void setSeat_no(String seat_no) {
		this.seat_no = seat_no;
	}
	public String getSeat_name() {
		return seat_name;
	}
	public void setSeat_name(String seat_name) {
		this.seat_name = seat_name;
	}
	public String getSeat_flag() {
		return seat_flag;
	}
	public void setSeat_flag(String seat_flag) {
		this.seat_flag = seat_flag;
	}
	public String getSeat_type_code() {
		return seat_type_code;
	}
	public void setSeat_type_code(String seat_type_code) {
		this.seat_type_code = seat_type_code;
	}
	public String getSeat_type_name() {
		return seat_type_name;
	}
	public void setSeat_type_name(String seat_type_name) {
		this.seat_type_name = seat_type_name;
	}
	public String getTicket_type_code() {
		return ticket_type_code;
	}
	public void setTicket_type_code(String ticket_type_code) {
		this.ticket_type_code = ticket_type_code;
	}
	public String getTicket_type_name() {
		return ticket_type_name;
	}
	public void setTicket_type_name(String ticket_type_name) {
		this.ticket_type_name = ticket_type_name;
	}
	public String getReserve_time() {
		return reserve_time;
	}
	public void setReserve_time(String reserve_time) {
		this.reserve_time = reserve_time;
	}
	public String getLimit_time() {
		return limit_time;
	}
	public void setLimit_time(String limit_time) {
		this.limit_time = limit_time;
	}
	public String getLose_time() {
		return lose_time;
	}
	public void setLose_time(String lose_time) {
		this.lose_time = lose_time;
	}
	public String getPay_limit_time() {
		return pay_limit_time;
	}
	public void setPay_limit_time(String pay_limit_time) {
		this.pay_limit_time = pay_limit_time;
	}
	public float getTicket_price() {
		return ticket_price;
	}
	public void setTicket_price(float ticket_price) {
		this.ticket_price = ticket_price;
	}
	public String getPrint_eticket_flag() {
		return print_eticket_flag;
	}
	public void setPrint_eticket_flag(String print_eticket_flag) {
		this.print_eticket_flag = print_eticket_flag;
	}
	public String getResign_flag() {
		return resign_flag;
	}
	public void setResign_flag(String resign_flag) {
		this.resign_flag = resign_flag;
	}
	public String getReturn_flag() {
		return return_flag;
	}
	public void setReturn_flag(String return_flag) {
		this.return_flag = return_flag;
	}
	public String getConfirm_flag() {
		return confirm_flag;
	}
	public void setConfirm_flag(String confirm_flag) {
		this.confirm_flag = confirm_flag;
	}
	public String getPay_mode_code() {
		return pay_mode_code;
	}
	public void setPay_mode_code(String pay_mode_code) {
		this.pay_mode_code = pay_mode_code;
	}
	public String getTicket_status_code() {
		return ticket_status_code;
	}
	public void setTicket_status_code(String ticket_status_code) {
		this.ticket_status_code = ticket_status_code;
	}
	public String getTicket_status_name() {
		return ticket_status_name;
	}
	public void setTicket_status_name(String ticket_status_name) {
		this.ticket_status_name = ticket_status_name;
	}
	public String getCancel_flag() {
		return cancel_flag;
	}
	public void setCancel_flag(String cancel_flag) {
		this.cancel_flag = cancel_flag;
	}
	public String getAmount_char() {
		return amount_char;
	}
	public void setAmount_char(String amount_char) {
		this.amount_char = amount_char;
	}
	public String getTrade_mode() {
		return trade_mode;
	}
	public void setTrade_mode(String trade_mode) {
		this.trade_mode = trade_mode;
	}
	public String getStart_train_date_page() {
		return start_train_date_page;
	}
	public void setStart_train_date_page(String start_train_date_page) {
		this.start_train_date_page = start_train_date_page;
	}
	public String getStr_ticket_price_page() {
		return str_ticket_price_page;
	}
	public void setStr_ticket_price_page(String str_ticket_price_page) {
		this.str_ticket_price_page = str_ticket_price_page;
	}
	public String getCome_go_traveller_ticket_page() {
		return come_go_traveller_ticket_page;
	}
	public void setCome_go_traveller_ticket_page(
			String come_go_traveller_ticket_page) {
		this.come_go_traveller_ticket_page = come_go_traveller_ticket_page;
	}
	public boolean isWantReturn() {
		return isWantReturn;
	}
	public void setWantReturn(boolean isWantReturn) {
		this.isWantReturn = isWantReturn;
	}
	public boolean isWantResign() {
		return isWantResign;
	}
	public void setWantResign(boolean isWantResign) {
		this.isWantResign = isWantResign;
	}
}
