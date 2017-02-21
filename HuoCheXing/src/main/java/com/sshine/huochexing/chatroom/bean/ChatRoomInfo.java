package com.sshine.huochexing.chatroom.bean;

import java.io.Serializable;

public class ChatRoomInfo implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1737248409740558635L;
	private String travelName;
	private String trainNum;
	private String pushTag;
	private boolean receiveMsg = false;
	
	
	
	
	public ChatRoomInfo(){
		
	}
	
	public ChatRoomInfo(String travelName, String trainNum ,String pushTag , boolean receiveMsg) {
		this.travelName = travelName;
		this.trainNum = trainNum;
		this.receiveMsg = receiveMsg;
		this.pushTag = pushTag;
	}
	
	public boolean isReceiveMsg() {
		return receiveMsg;
	}
	
	public void setReceiveMsg(boolean receiveMsg) {
		this.receiveMsg = receiveMsg;
	}
	
	public String getTravelName() {
		return travelName;
	}

	public void setTravelName(String travelName) {
		this.travelName = travelName;
	}

	public String getTrainNum() {
		return trainNum;
	}
	public void setTrainNum(String trainNum) {
		this.trainNum = trainNum;
	}

	public String getPushTag() {
		return pushTag;
	}

	public void setPushTag(String pushTag) {
		this.pushTag = pushTag;
	}
	
}
