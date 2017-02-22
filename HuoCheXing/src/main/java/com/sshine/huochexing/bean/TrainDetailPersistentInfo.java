package com.sshine.huochexing.bean;

import java.io.Serializable;
import java.util.List;

public class TrainDetailPersistentInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Train train;
	private List<StationInfo> lstSInfos;
	
	public Train getTrain() {
		return train;
	}
	public void setTrain(Train train) {
		this.train = train;
	}
	public List<StationInfo> getLstSInfos() {
		return lstSInfos;
	}
	public void setLstSInfos(List<StationInfo> lstSInfos) {
		this.lstSInfos = lstSInfos;
	}
}
