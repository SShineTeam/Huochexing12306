package com.sshine.huochexing.trainSchedule;

import java.util.Comparator;

import com.sshine.huochexing.bean.QueryLeftNewDTOInfo;
import com.sshine.huochexing.bean.QueryLeftNewInfo;

public class TrainSchComparatorHelper{
	public class SpeedComparator implements Comparator<QueryLeftNewInfo>{
		private boolean mIsASC;
		public SpeedComparator(boolean isASC){
			this.mIsASC = isASC;
		}
		@Override
		public int compare(QueryLeftNewInfo lhs, QueryLeftNewInfo rhs) {
			QueryLeftNewDTOInfo q1 = lhs.getQueryLeftNewDTO();
			QueryLeftNewDTOInfo q2 = rhs.getQueryLeftNewDTO();
			Integer i1 = q1.getSpeed_index();
			Integer i2 = q2.getSpeed_index();
			if (mIsASC){
				return i1 - i2;
			}else{
				return i2-i1;
			}
		}
	}
	public class FromTimeComparator implements Comparator<QueryLeftNewInfo>{
		private boolean mIsASC;
		public FromTimeComparator(boolean isASC){
			this.mIsASC = isASC;
		}
		@Override
		public int compare(QueryLeftNewInfo lhs, QueryLeftNewInfo rhs) {
			QueryLeftNewDTOInfo q1 = lhs.getQueryLeftNewDTO();
			QueryLeftNewDTOInfo q2 = rhs.getQueryLeftNewDTO();
			if (mIsASC){
				return q1.getStart_time().compareTo(q2.getStart_time());
			}else{
				return q2.getStart_time().compareTo(q1.getStart_time());
			}
		}
	}
	public class ToTimeComparator implements Comparator<QueryLeftNewInfo>{
		private boolean mIsASC;
		public ToTimeComparator(boolean isASC){
			this.mIsASC = isASC;
		}
		@Override
		public int compare(QueryLeftNewInfo lhs, QueryLeftNewInfo rhs) {
			QueryLeftNewDTOInfo q1 = lhs.getQueryLeftNewDTO();
			QueryLeftNewDTOInfo q2 = rhs.getQueryLeftNewDTO();
			if (mIsASC){
				return q1.getArrive_time().compareTo(q2.getArrive_time());
			}else{
				return q2.getArrive_time().compareTo(q1.getArrive_time());
			}
		}
	}
	public class LISHIComparator implements Comparator<QueryLeftNewInfo>{
		private boolean mIsASC;
		public LISHIComparator(boolean isASC){
			this.mIsASC = isASC;
		}
		@Override
		public int compare(QueryLeftNewInfo lhs, QueryLeftNewInfo rhs) {
			QueryLeftNewDTOInfo q1 = lhs.getQueryLeftNewDTO();
			QueryLeftNewDTOInfo q2 = rhs.getQueryLeftNewDTO();
			if (mIsASC){
				return q1.getLishi().compareTo(q2.getLishi());
			}else{
				return q2.getLishi().compareTo(q1.getLishi());
			}
		}
	}
	public class D_late_timeComparator implements Comparator<QueryLeftNewInfo>{
		private boolean mIsASC;
		public D_late_timeComparator(boolean isASC){
			this.mIsASC = isASC;
		}
		@Override
		public int compare(QueryLeftNewInfo lhs, QueryLeftNewInfo rhs) {
			QueryLeftNewDTOInfo q1 = lhs.getQueryLeftNewDTO();
			QueryLeftNewDTOInfo q2 = rhs.getQueryLeftNewDTO();
			if (mIsASC){
				return (q1.getD_LateTime() - q2.getD_LateTime());
			}else{
				
				return (q2.getD_LateTime() - q1.getD_LateTime());
			}
		}
	}
	public class A_late_timeComparator implements Comparator<QueryLeftNewInfo>{
		private boolean mIsASC;
		public A_late_timeComparator(boolean isASC){
			this.mIsASC = isASC;
		}
		@Override
		public int compare(QueryLeftNewInfo lhs, QueryLeftNewInfo rhs) {
			QueryLeftNewDTOInfo q1 = lhs.getQueryLeftNewDTO();
			QueryLeftNewDTOInfo q2 = rhs.getQueryLeftNewDTO();
			if (mIsASC){
				return q1.getA_LateTime() - q2.getA_LateTime();
			}else{
				return q2.getA_LateTime()-q1.getA_LateTime();
			}
		}
	}
	public class WPComparator implements Comparator<QueryLeftNewInfo>{
		String strPlaceHolder = "--";
		@Override
		public int compare(QueryLeftNewInfo lhs, QueryLeftNewInfo rhs) {
			QueryLeftNewDTOInfo q1 = lhs.getQueryLeftNewDTO();
			QueryLeftNewDTOInfo q2 = rhs.getQueryLeftNewDTO();
			int lNum = getSeatNum(q1.getGr_num()) + getSeatNum(q1.getRw_num())
					+ getSeatNum(q1.getYw_num());
			int rNum = getSeatNum(q2.getGr_num()) + getSeatNum(q2.getRw_num())
					+ getSeatNum(q2.getYw_num());
			return rNum - lNum;
		}
		private int getSeatNum(String str1){
			if (str1.equals(strPlaceHolder)){
				return 0;
			}else if (str1.equals("有")){
				return 50;
			}else if (str1.equals("无")){
				return 0;
			}else{
				return Integer.valueOf(str1);
			}
		}
	}
	public class ZWComparator implements Comparator<QueryLeftNewInfo>{
		String strPlaceHolder = "--";
		@Override
		public int compare(QueryLeftNewInfo lhs, QueryLeftNewInfo rhs) {
			QueryLeftNewDTOInfo q1 = lhs.getQueryLeftNewDTO();
			QueryLeftNewDTOInfo q2 = rhs.getQueryLeftNewDTO();
			int lNum = getSeatNum(q1.getYz_num()) + getSeatNum(q1.getRz_num())
					+ getSeatNum(q1.getSwz_num()) + getSeatNum(q1.getZy_num())
					+ getSeatNum(q1.getZe_num()) + getSeatNum(q1.getTz_num());
			int rNum = getSeatNum(q2.getYz_num()) + getSeatNum(q2.getRz_num())
					+ getSeatNum(q2.getSwz_num()) + getSeatNum(q2.getZy_num())
					+ getSeatNum(q2.getZe_num()) + getSeatNum(q2.getTz_num());
			return rNum - lNum;
		}
		private int getSeatNum(String str1){
			if (str1.equals(strPlaceHolder)){
				return 0;
			}else if (str1.equals("有")){
				return 50;
			}else if (str1.equals("无")){
				return 0;
			}else{
				return Integer.valueOf(str1);
			}
		}
	}
	public class PreferentialPriceComparator implements Comparator<QueryLeftNewInfo>{
		@Override
		public int compare(QueryLeftNewInfo lhs, QueryLeftNewInfo rhs) {
			QueryLeftNewDTOInfo q1 = lhs.getQueryLeftNewDTO();
			QueryLeftNewDTOInfo q2 = rhs.getQueryLeftNewDTO();
			int prefPriceTokenValue = -100;
			int prefPriceTokenNum1 = q1.isHasPreferentialPrice()?prefPriceTokenValue:0;
			int prefPriceTokenNum2 = q2.isHasPreferentialPrice()?prefPriceTokenValue:0;
			return ((q1.getSpeed_index()+prefPriceTokenNum1) - (q2.getSpeed_index()+prefPriceTokenNum2));
		}
	}
	
	/**
	 * 可预订车次优先排序，排序方式：可预订ASC，有折扣车票ASC，车速ASC
	 * @author tp7309
	 * 2014-4-27
	 *
	 */
	public class CanWebBuyComparator implements Comparator<QueryLeftNewInfo>{
		@Override
		public int compare(QueryLeftNewInfo lhs, QueryLeftNewInfo rhs) {
			QueryLeftNewDTOInfo q1 = lhs.getQueryLeftNewDTO();
			QueryLeftNewDTOInfo q2 = rhs.getQueryLeftNewDTO();
			int canWebByTokenValue = -100;
			int canWebByTokenNum1 = q1.getCanWebBuy().equals("Y")?canWebByTokenValue:0;
			int canWebByTokenNum2 = q2.getCanWebBuy().equals("Y")?canWebByTokenValue:0;
			
			int prefPriceTokenValue = -100;
			int prefPriceTokenNum1 = q1.isHasPreferentialPrice()?prefPriceTokenValue:0;
			int prefPriceTokenNum2 = q2.isHasPreferentialPrice()?prefPriceTokenValue:0;
			return ((q1.getSpeed_index()+prefPriceTokenNum1+canWebByTokenNum1) - (q2.getSpeed_index()+prefPriceTokenNum2+canWebByTokenNum2));
		}
	}
	
	public D_late_timeComparator getD_late_timeComparator(boolean isASC) {
		return (new D_late_timeComparator(isASC));
	}
	public A_late_timeComparator getA_late_timeComparator(boolean isASC) {
		return (new A_late_timeComparator(isASC));
	}
	public WPComparator getWPComparator() {
		return (new WPComparator());
	}
	public ZWComparator getZWComparator() {
		return new ZWComparator();
	}
	public SpeedComparator getSpeedComparator(boolean isASC) {
		return (new SpeedComparator(isASC));
	}
	public FromTimeComparator getFromTimeComparator(boolean isASC) {
		return (new FromTimeComparator(isASC));
	}
	public ToTimeComparator getToTimeComparator(boolean isASC) {
		return (new ToTimeComparator(isASC));
	}
	public LISHIComparator getLISHIComparator(boolean isASC) {
		return (new LISHIComparator(isASC));
	}
	
	public PreferentialPriceComparator getPreferentialPriceComparator(){
		return (new PreferentialPriceComparator());
	}
	
	public CanWebBuyComparator getCanWebBuyComparator(){
		return (new CanWebBuyComparator());
	}
}
