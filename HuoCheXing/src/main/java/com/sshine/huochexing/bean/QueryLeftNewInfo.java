package com.sshine.huochexing.bean;

import java.io.Serializable;

import com.google.gson.annotations.Expose;

public class QueryLeftNewInfo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Expose
	private QueryLeftNewDTOInfo queryLeftNewDTO;
	@Expose
	private String secretStr;
	@Expose
	private String buttonTextInfo;
	
	public QueryLeftNewDTOInfo getQueryLeftNewDTO() {
		return queryLeftNewDTO;
	}
	public void setQueryLeftNewDTO(QueryLeftNewDTOInfo queryLeftNewDTO) {
		this.queryLeftNewDTO = queryLeftNewDTO;
	}
	public String getSecretStr() {
		return secretStr;
	}
	public void setSecretStr(String secretStr) {
		this.secretStr = secretStr;
	}
	public String getButtonTextInfo() {
		return buttonTextInfo;
	}
	public void setButtonTextInfo(String buttonTextInfo) {
		this.buttonTextInfo = buttonTextInfo;
	}
}
