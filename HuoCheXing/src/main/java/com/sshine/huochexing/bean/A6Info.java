package com.sshine.huochexing.bean;

public class A6Info<T> {
	public static final String FLAG_DATA_SUCCESS = "Y";
	public static final String FLAG_DATA_FAIL = "N";
	
	private String rawString;
	private String validateMessagesShowId;
	private boolean status;
	private int httpstatus;
	private String data;
	private String messages;
	private String validateMessages;
	
	private T dataObject;
	
	
	public String getValidateMessagesShowId() {
		return validateMessagesShowId;
	}
	public void setValidateMessagesShowId(String validateMessagesShowId) {
		this.validateMessagesShowId = validateMessagesShowId;
	}
	public boolean isStatus() {
		return status;
	}
	public void setStatus(boolean status) {
		this.status = status;
	}
	public int getHttpstatus() {
		return httpstatus;
	}
	public void setHttpstatus(int httpstatus) {
		this.httpstatus = httpstatus;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public String getMessages() {
		return messages;
	}
	public void setMessages(String messages) {
		this.messages = messages;
	}
	public String getValidateMessages() {
		return validateMessages;
	}
	public void setValidateMessages(String validateMessages) {
		this.validateMessages = validateMessages;
	}
	
	public String getRawString() {
		return rawString;
	}
	public void setRawString(String rawString) {
		this.rawString = rawString;
	}
	@Override
	public String toString() {
		return this.rawString;
	}
	public T getDataObject() {
		return dataObject;
	}
	public void setDataObject(T dataObject) {
		this.dataObject = dataObject;
	}
}
