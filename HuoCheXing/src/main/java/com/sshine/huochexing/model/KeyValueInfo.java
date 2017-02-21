package com.sshine.huochexing.model;
/**
 * 键值对
 * @author tp7309 2014-7-2
 */
public class KeyValueInfo {
	private String key;
	private String value;
	
	public KeyValueInfo(String key, String value){
		this.key = key;
		this.value = value;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
}
