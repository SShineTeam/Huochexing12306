package com.sshine.huochexing.bean;

import com.google.gson.annotations.Expose;

/**
 * 退票确认信息类
 * @author tp7309
 * 2014-4-7
 *
 */
public class TicketAffirmInfo {
	@Expose
	private StationTrainDTOInfo stationTrainDTO;
	@Expose
	private TicketAffirmPDTOInfo passengerDTO;
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
	private String seat_type_name;
	@Expose
	private String pay_limit_time;
	@Expose
	private String realize_time_char;
	@Expose
	private float ticket_price;
	@Expose
	private float return_price;
	@Expose
	private float return_cost;
	@Expose
	private String amount_char;
	@Expose
	private String start_train_date_page;
	@Expose
	private String str_ticket_price_page;
	@Expose
	private String come_go_traveller_ticket_page;
	@Expose
	private String rate;
	
	public StationTrainDTOInfo getStationTrainDTO() {
		return stationTrainDTO;
	}
	public void setStationTrainDTO(StationTrainDTOInfo stationTrainDTO) {
		this.stationTrainDTO = stationTrainDTO;
	}
	public TicketAffirmPDTOInfo getPassengerDTO() {
		return passengerDTO;
	}
	public void setPassengerDTO(TicketAffirmPDTOInfo passengerDTO) {
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
	public String getSeat_type_name() {
		return seat_type_name;
	}
	public void setSeat_type_name(String seat_type_name) {
		this.seat_type_name = seat_type_name;
	}
	public String getPay_limit_time() {
		return pay_limit_time;
	}
	public void setPay_limit_time(String pay_limit_time) {
		this.pay_limit_time = pay_limit_time;
	}
	public String getRealize_time_char() {
		return realize_time_char;
	}
	public void setRealize_time_char(String realize_time_char) {
		this.realize_time_char = realize_time_char;
	}
	public float getTicket_price() {
		return ticket_price;
	}
	public void setTicket_price(float ticket_price) {
		this.ticket_price = ticket_price;
	}
	public float getReturn_price() {
		return return_price;
	}
	public void setReturn_price(float return_price) {
		this.return_price = return_price;
	}
	public float getReturn_cost() {
		return return_cost;
	}
	public void setReturn_cost(float return_cost) {
		this.return_cost = return_cost;
	}
	public String getAmount_char() {
		return amount_char;
	}
	public void setAmount_char(String amount_char) {
		this.amount_char = amount_char;
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
	public String getRate() {
		return rate;
	}
	public void setRate(String rate) {
		this.rate = rate;
	}
}
