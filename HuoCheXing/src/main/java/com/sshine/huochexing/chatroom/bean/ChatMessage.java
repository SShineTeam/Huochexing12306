package com.sshine.huochexing.chatroom.bean;

import java.io.Serializable;

import com.google.gson.annotations.Expose;

public class ChatMessage implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7235385520557062476L;
	@Expose
	private int uId;
	@Expose
	private String userId;
	@Expose
	private String channelId;
	@Expose
	private String trainId;
	@Expose
	private String nickName;
	@Expose
	private String headId;
	@Expose
	private long timeSamp;
	@Expose
	private String message;
	@Expose
	private int Tag;
	
	public ChatMessage(int uId, String userId, String channelId,
			String trainId, String nickName, String headId, long timeSamp,
			String message, int tag) {
		super();
		this.uId = uId;
		this.userId = userId;
		this.channelId = channelId;
		this.trainId = trainId;
		this.nickName = nickName;
		this.headId = headId;
		this.timeSamp = timeSamp;
		this.message = message;
		Tag = tag;
	}
	public int getuId() {
		return uId;
	}
	public void setuId(int uId) {
		this.uId = uId;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getChannelId() {
		return channelId;
	}
	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}
	public String getTrainId() {
		return trainId;
	}
	public void setTrainId(String trainId) {
		this.trainId = trainId;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public String getHeadId() {
		return headId;
	}
	public void setHeadId(String headId) {
		this.headId = headId;
	}
	public long getTimeSamp() {
		return timeSamp;
	}
	public void setTimeSamp(long timeSamp) {
		this.timeSamp = timeSamp;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public int getTag() {
		return Tag;
	}
	public void setTag(int tag) {
		Tag = tag;
	}
	@Override
	public String toString() {
		return "ChatMessage [uId=" + uId + ", userId=" + userId
				+ ", channelId=" + channelId + ", trainId=" + trainId
				+ ", nickName=" + nickName + ", headId=" + headId
				+ ", timeSamp=" + timeSamp + ", message=" + message + ", Tag="
				+ Tag + "]";
	}
	
	
}
