package com.sshine.huochexing.bean;

import java.io.Serializable;

import com.google.gson.annotations.Expose;

public class PassengerInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Expose
	private int code;
	@Expose
	private String passenger_name;
	@Expose
	private String sex_code;
	@Expose
	private String sex_name;
	@Expose
	private String born_date;
	@Expose
	private String country_code;
	@Expose
	private String passenger_id_type_code;
	@Expose
	private String passenger_id_type_name;
	@Expose
	private String passenger_id_no;
	@Expose
	private String passenger_type;
	@Expose
	private String passenger_flag;
	@Expose
	private String passenger_type_name;
	@Expose
	private String mobile_no;
	@Expose
	private String phone_no;
	@Expose
	private String email;
	@Expose
	private String address;
	@Expose
	private String postalcode;
	@Expose
	private String first_letter;
	@Expose
	private int recordCount;
	@Expose
	private int total_times;
	//是否是常用乘车人
	private boolean isCommon;
	private boolean isUserSelf;
	//本地储存索引，修改时用
	private int nativeIndex;
	
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getPassenger_name() {
		return passenger_name;
	}
	public void setPassenger_name(String passenger_name) {
		this.passenger_name = passenger_name;
	}
	public String getSex_code() {
		return sex_code;
	}
	public void setSex_code(String sex_code) {
		this.sex_code = sex_code;
	}
	public String getSex_name() {
		return sex_name;
	}
	public void setSex_name(String sex_name) {
		this.sex_name = sex_name;
	}
	public String getBorn_date() {
		return born_date;
	}
	public void setBorn_date(String born_date) {
		this.born_date = born_date;
	}
	public String getCountry_code() {
		return country_code;
	}
	public void setCountry_code(String country_code) {
		this.country_code = country_code;
	}
	public String getPassenger_id_type_code() {
		return passenger_id_type_code;
	}
	public void setPassenger_id_type_code(String passenger_id_type_code) {
		this.passenger_id_type_code = passenger_id_type_code;
	}
	public String getPassenger_id_type_name() {
		return passenger_id_type_name;
	}
	public void setPassenger_id_type_name(String passenger_id_type_name) {
		this.passenger_id_type_name = passenger_id_type_name;
	}
	public String getPassenger_id_no() {
		return passenger_id_no;
	}
	public void setPassenger_id_no(String passenger_id_no) {
		this.passenger_id_no = passenger_id_no;
	}
	public String getPassenger_type() {
		return passenger_type;
	}
	public void setPassenger_type(String passenger_type) {
		this.passenger_type = passenger_type;
	}
	public String getPassenger_flag() {
		return passenger_flag;
	}
	public void setPassenger_flag(String passenger_flag) {
		this.passenger_flag = passenger_flag;
	}
	public String getPassenger_type_name() {
		return passenger_type_name;
	}
	public void setPassenger_type_name(String passenger_type_name) {
		this.passenger_type_name = passenger_type_name;
	}
	public String getMobile_no() {
		return mobile_no;
	}
	public void setMobile_no(String mobile_no) {
		this.mobile_no = mobile_no;
	}
	public String getPhone_no() {
		return phone_no;
	}
	public void setPhone_no(String phone_no) {
		this.phone_no = phone_no;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getPostalcode() {
		return postalcode;
	}
	public void setPostalcode(String postalcode) {
		this.postalcode = postalcode;
	}
	public String getFirst_letter() {
		return first_letter;
	}
	public void setFirst_letter(String first_letter) {
		this.first_letter = first_letter;
	}
	public int getRecordCount() {
		return recordCount;
	}
	public void setRecordCount(int recordCount) {
		this.recordCount = recordCount;
	}
	public boolean isCommon() {
		return isCommon;
	}
	public void setCommon(boolean isCommon) {
		this.isCommon = isCommon;
	}
	public boolean isUserSelf() {
		return isUserSelf;
	}
	public void setUserSelf(boolean isUserSelf) {
		this.isUserSelf = isUserSelf;
	}
	public int getNativeIndex() {
		return nativeIndex;
	}
	public void setNativeIndex(int nativeIndex) {
		this.nativeIndex = nativeIndex;
	}
	public int getTotal_times() {
		return total_times;
	}
	public void setTotal_times(int total_times) {
		this.total_times = total_times;
	}
}
