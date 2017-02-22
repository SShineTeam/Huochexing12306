package com.sshine.huochexing.bean;

import java.util.List;

import com.google.gson.annotations.Expose;

/**
 * 乘车路径
 * @author tp7309
 * 2014-5-3
 *
 */
public class IntelligentPathIno {
	@Expose
	private int transfer_num;
	@Expose
	private List<IntelligentS2SInfo> lstS2SInfos;
	
	public int getTransfer_num() {
		return transfer_num;
	}
	public List<IntelligentS2SInfo> getLstS2SInfos() {
		return lstS2SInfos;
	}
	public void setTransfer_num(int transfer_num) {
		this.transfer_num = transfer_num;
	}
	public void setLstS2SInfos(List<IntelligentS2SInfo> lstS2SInfos) {
		this.lstS2SInfos = lstS2SInfos;
	}
}
