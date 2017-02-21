package com.sshine.huochexing.bean;

/**
 * 12306订单支付信息类
 * @author wangruifeng
 * 2014-4-5
 */
public class A6OrderPayInfo {
	private String sequence_no;
	private String interfaceName;
	private String interfaceVersion;
	private String tranData;
	private String merSignMsg;
	private String appId;
	private String transType;
	
	public String getSequence_no() {
		return sequence_no;
	}
	public void setSequence_no(String sequence_no) {
		this.sequence_no = sequence_no;
	}
	public String getInterfaceName() {
		return interfaceName;
	}
	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}
	public String getInterfaceVersion() {
		return interfaceVersion;
	}
	public void setInterfaceVersion(String interfaceVersion) {
		this.interfaceVersion = interfaceVersion;
	}
	public String getTranData() {
		return tranData;
	}
	public void setTranData(String tranData) {
		this.tranData = tranData;
	}
	public String getMerSignMsg() {
		return merSignMsg;
	}
	public void setMerSignMsg(String merSignMsg) {
		this.merSignMsg = merSignMsg;
	}
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	public String getTransType() {
		return transType;
	}
	public void setTransType(String transType) {
		this.transType = transType;
	}
}
