package com.sshine.huochexing.bean;

import java.io.Serializable;

//列车简要信息实体类
	public class Train implements Serializable{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
//		public static final String TRAIN_NO = "train_no";
//		public static final String TRAIN_NUM = "trainNum";
//		public static final String START_STATION = "startStation";
//		public static final String END_STATION = "endStation";
//		public static final String A_TIME = "a_Time";
//		public static final String D_TIME = "d_Time";
//		public static final String R_DATE = "r_Date";
//		public static final String A_LATE_TIME = "a_lateTime";
//		public static final String D_LATE_TIME = "d_lateTime";
		
		private String train_no;
		private String trainNum;   //车次
		private String startStation; //出发站
		private String endStation;  //目的站
		private String a_Time;  //到达时间
		private String d_Time;  //发车时间
		private String r_Date;  //历时
		private String a_LateTime;  //到站晚点
		private String d_LateTime;  //发车晚点

		public Train(){
		}
		
		public String getTrainNum() {
			return trainNum;
		}
		public void setTrainNum(String trainNum) {
			this.trainNum = trainNum;
		}
		public String getStartStation() {
			return startStation;
		}
		public void setStartStation(String startStation) {
			this.startStation = startStation;
		}
		public String getEndStation() {
			return endStation;
		}
		public void setEndStation(String endStation) {
			this.endStation = endStation;
		}
		public String getR_Date() {
			return r_Date;
		}
		public void setR_Date(String r_Date) {
			this.r_Date = r_Date;
		}
		public String getD_Time() {
			return d_Time;
		}
		public void setD_Time(String d_Time) {
			this.d_Time = d_Time;
		}
		public String getA_Time() {
			return a_Time;
		}
		public String getA_LateTime() {
			return a_LateTime;
		}

		public void setA_LateTime(String a_LateTime) {
			this.a_LateTime = a_LateTime;
		}

		public String getD_LateTime() {
			return d_LateTime;
		}

		public void setD_LateTime(String d_LateTime) {
			this.d_LateTime = d_LateTime;
		}

		public void setA_Time(String a_Time) {
			this.a_Time = a_Time;
		}

		public String getTrain_no() {
			return train_no;
		}

		public void setTrain_no(String train_no) {
			this.train_no = train_no;
		}
	}
