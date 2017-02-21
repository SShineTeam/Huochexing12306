package com.sshine.huochexing.bean;

import org.json.JSONObject;

public class SServiceInfo {
	private JSONObject jsonObj;
	private int resultCode;
	private String data;
	private String messages;
	
	public int getResultCode() {
		return resultCode;
	}
	public String getData() {
		return data;
	}
	public String getMessages() {
		return messages;
	}
	public void setResultCode(int resultCode) {
		this.resultCode = resultCode;
	}
	public void setData(String data) {
		this.data = data;
	}
	public void setMessages(String messages) {
		this.messages = messages;
	}
	public JSONObject getJsonObj() {
		return jsonObj;
	}
	public void setJsonObj(JSONObject jsonObj) {
		this.jsonObj = jsonObj;
	}
}
