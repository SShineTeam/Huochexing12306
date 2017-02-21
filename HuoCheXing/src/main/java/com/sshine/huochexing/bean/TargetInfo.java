package com.sshine.huochexing.bean;

import com.sshine.huochexing.utils.SeatHelper;

public class TargetInfo{
	private QueryLeftNewInfo qlnInfo;
	private SeatHelper sHelper;
	private Integer seatType;
	private int num;
	
	public QueryLeftNewInfo getQlnInfo() {
		return qlnInfo;
	}
	public void setQlnInfo(QueryLeftNewInfo qlnInfo) {
		this.qlnInfo = qlnInfo;
	}
	public SeatHelper getSeatHelper() {
		return sHelper;
	}
	public void setSeatHelper(SeatHelper sHelper) {
		this.sHelper = sHelper;
	}
	public Integer getSeatType() {
		return seatType;
	}
	public void setSeatType(Integer seatType) {
		this.seatType = seatType;
	}
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
}
