package com.sshine.huochexing.utils;

import android.annotation.SuppressLint;
import android.text.Html;
import android.util.SparseArray;
import android.util.SparseIntArray;

import com.sshine.huochexing.bean.QueryLeftNewDTOInfo;
import com.sshine.huochexing.bean.SeatInfo;
import com.sshine.huochexing.bean.StationTrainDTOInfo;
import com.sshine.huochexing.bean.TicketInfo;


public class SeatHelper {
	public static final int ZY = 0;
	public static final int ZE = 1;
	public static final int SWZ = 2;
	public static final int TZ = 3;
	public static final int YZ = 4;
	public static final int RZ = 5;
	public static final int YW = 6;
	public static final int RW = 7;
	public static final int GR = 8;
	public static final int WZ = 9;
	private static SparseArray<String> seatTypes;
	private static SparseArray<String> seatPriceTypes;
	private static SparseArray<String> seatPrefPriceTypes;
	private SparseArray<String> seatNums;
	private SparseIntArray seatReferenceNums;
	private QueryLeftNewDTOInfo qlndInfo;
	private static final String SEAT_NUM_PLACE_HOLDER = "--";
	private static final String SEAT_NUM_SOME = "有";
	private static final String SEAT_NUM_NEITHER_ONE = "无";
	private String mLoc30 = "三人座靠窗座";
	private String mLoc31 = "三人座中座";
	private String mLoc32 = "三人座外座";
	private String mLoc20 = "两人座靠窗座";
	private String mLoc21 = "两人座外座";
	private String mLoc1 = "一人座靠窗座";
	
	public SeatHelper(){
		getSeatTypes();
		getSeatPriceTypes();
		getSeatPrePriceTypes();
		seatNums = new SparseArray<String>();
	}

	public SparseArray<String> getSeatPriceTypes() {
		if (seatPriceTypes == null){
			seatPriceTypes = new SparseArray<String>();
			seatPriceTypes.put(SWZ, "A9");
			seatPriceTypes.put(TZ, "P");
			seatPriceTypes.put(ZY, "M");
			seatPriceTypes.put(ZE, "O");
			seatPriceTypes.put(GR, "A6");
			seatPriceTypes.put(RW, "A4");
			seatPriceTypes.put(RZ, "UKNOWN");
			seatPriceTypes.put(YW, "A3");
			seatPriceTypes.put(YZ, "A1");
			seatPriceTypes.put(WZ, "WZ");
		}
		return seatPriceTypes;
	}

	public static SparseArray<String> getSeatPrePriceTypes() {
		if (seatPrefPriceTypes == null){
			seatPrefPriceTypes = new SparseArray<String>();
			seatPrefPriceTypes.put(SWZ, "91");
			seatPrefPriceTypes.put(TZ, "P1");
			seatPrefPriceTypes.put(ZY, "M1");
			seatPrefPriceTypes.put(ZE, "O1");
			seatPrefPriceTypes.put(GR, "61");
			seatPrefPriceTypes.put(RW, "41");
			seatPrefPriceTypes.put(RZ, "21");
			seatPrefPriceTypes.put(YW, "31");
			seatPrefPriceTypes.put(YZ, "11");
			seatPrefPriceTypes.put(WZ, "W1");
		}
		return seatPrefPriceTypes;
	}

	public static SparseArray<String> getSeatTypes() {
		if (seatTypes == null){
			seatTypes = new SparseArray<String>();
			seatTypes.put(SWZ, "商务座");
			seatTypes.put(TZ, "特等座");
			seatTypes.put(ZY, "一等座");
			seatTypes.put(ZE, "二等座");
			seatTypes.put(GR, "高级软卧");
			seatTypes.put(RW, "软卧");
			seatTypes.put(RZ, "软座");
			seatTypes.put(YW, "硬卧");
			seatTypes.put(YZ, "硬座");
			seatTypes.put(WZ, "无座");
		}
		return seatTypes;
	}
	
	public SeatHelper(QueryLeftNewDTOInfo qlndInfo){
		this();
		this.qlndInfo = qlndInfo;
		setSeatNum(SWZ, qlndInfo.getSwz_num());
		setSeatNum(TZ, qlndInfo.getTz_num());
		setSeatNum(ZY, qlndInfo.getZy_num());
		setSeatNum(ZE, qlndInfo.getZe_num());
		setSeatNum(GR, qlndInfo.getSwz_num());
		setSeatNum(RW, qlndInfo.getRw_num());
		setSeatNum(RZ, qlndInfo.getRz_num());
		setSeatNum(YW, qlndInfo.getYw_num());
		setSeatNum(WZ, qlndInfo.getWz_num());
		setSeatNum(YZ, qlndInfo.getYz_num());
	}
	public void setSeatNum(int seatType, String strNum){
		seatNums.put(seatType, strNum);
	}
	
	public String getSeatNum(int seatType){
		return seatNums.get(seatType);
	}
	/**
	 * 检测是否有优惠车票价标志位
	 */
	public static void setPreferentialPriceFlag(QueryLeftNewDTOInfo qlndInfo){
		getSeatTypes();
		for(int i=0; i<seatTypes.size(); i++){
			if (isPrefPrice(qlndInfo.getYp_ex(), seatTypes.keyAt(i))){
				qlndInfo.setHasPreferentialPrice(true);
				return;
			}
		}
		qlndInfo.setHasPreferentialPrice(false);
	}
		
	public CharSequence getSeatText(){
		String strRetTempValue = "";
		int tempCount = 0;
		for(int i=0; i<seatTypes.size(); i++){
			String seatNumStr = seatNums.valueAt(i);
			if (!(SEAT_NUM_PLACE_HOLDER.equals(seatNumStr) || "".equals(seatNumStr))){
				tempCount++;
				if (tempCount%4==0){
					strRetTempValue += "<br />";
				}
				//添加打折车票图标
				if (isPrefPrice(qlndInfo.getYp_ex(), seatTypes.keyAt(i))){
					qlndInfo.setHasPreferentialPrice(true);
					strRetTempValue += "<font color='#ff8c00'>[折]</font>"+seatTypes.valueAt(i);
				}else{
					qlndInfo.setHasPreferentialPrice(false);
					strRetTempValue += seatTypes.valueAt(i);
				}
				if (seatNumStr.equals(SEAT_NUM_SOME)){
					strRetTempValue += ":<font color='#4cb848'>有</font>&nbsp;&nbsp;&nbsp;&nbsp;";
				}else if (seatNumStr.equals(SEAT_NUM_NEITHER_ONE)){
					strRetTempValue += ":无&nbsp;&nbsp;&nbsp;&nbsp;";
				}else{
					strRetTempValue += ":<font color='#e7453d'>" + seatNumStr + "</font>&nbsp;&nbsp;&nbsp;&nbsp;";
				}
			}
		}
		return Html.fromHtml(strRetTempValue);
	}
	
	//检测是否是打折车票
	public static boolean isPrefPrice(String yp_index, int seatType) {
		getSeatPrePriceTypes();
		int d= yp_index != null?yp_index.indexOf(seatPrefPriceTypes.get(seatType)):-1;
		boolean c = false;
		if (d>-1 && (d%2)==0){
			c = true;
		}
		return c;
	}

	private int getIndexOfValue(SparseArray<String> sa1, String strValue){
		for(int i=0; i<sa1.size(); i++){
			if (strValue.equals(sa1.valueAt(i))){
				return i;
			}
		}
		return -1;
	}
	
	public SeatInfo getInfoBySeatPriceType(String strType){
		SeatInfo sInfo = new SeatInfo();
		int indexOfValue = getIndexOfValue(seatPriceTypes, strType);
		if (indexOfValue == -1){
			return null;
		}else{
			int type = seatPriceTypes.keyAt(indexOfValue);
			sInfo.setName(seatTypes.get(type));
			sInfo.setType(type);
			sInfo.setPriceType(strType);
		}
		return sInfo;
	}
	
	public double getStuPrice(int type, double selfPrice, double yzPrice){
		switch(type){
		case YZ:
			return yzPrice/2;
		case YW:
			return (selfPrice-yzPrice/2);
		case ZE:
			return (selfPrice*0.75);
		default:
			return selfPrice;
		}
	}
	
	public String getSeatType(int type){
		return seatTypes.get(type);
	}
	public String getSeatPriceType(int type){
		return seatPriceTypes.get(type);
	}
	public String[] getSeatTypeNames(){
		String[] stNames = new String[seatTypes.size()];
		for(int i=0; i<seatTypes.size(); i++){
			stNames[i] = seatTypes.valueAt(i);
		}
		return stNames;
	}
	
	public int getSeatReferenceNum(String strNumValue){
		int num = 0;
		if (strNumValue.equals(SEAT_NUM_SOME)){
			num = 50;
		}else if (strNumValue.equals(SEAT_NUM_NEITHER_ONE)){
			num = 0;
		}else if (strNumValue.equals(SEAT_NUM_PLACE_HOLDER)){
			num = 0;
		}else{
			try{
				num = Integer.valueOf(strNumValue);
			}catch(Exception e){
				e.printStackTrace();
				num = 0;
			}
		}
		return num;
	}

	@SuppressLint("UseSparseArrays")
	public SparseIntArray getSeatReferenceNums() {
		if (seatReferenceNums == null){
			seatReferenceNums = new SparseIntArray();
			for(int i=0; i<seatNums.size(); i++){
				seatReferenceNums.append(seatNums.keyAt(i), getSeatReferenceNum(seatNums.valueAt(i)));
			}
		}
		return seatReferenceNums;
	}
	
	public String getReferenceLocation(TrainHelper tHelper, TicketInfo tInfo){
		String strRetValue = null;
		try{
			StationTrainDTOInfo stdInfo = tInfo.getStationTrainDTO();
			int trainType = tHelper.getTrainType(stdInfo.getStation_train_code());
			switch(trainType){
			case TrainHelper.K:
				strRetValue = getRLoc118(tInfo);
				break;
			case TrainHelper.T:
				strRetValue = getRLoc118(tInfo);
				break;
			case TrainHelper.D:
				strRetValue = getRLocABCDF(tInfo);
				break;
			case TrainHelper.Z:
				strRetValue = getRLocABCDF(tInfo);
				break;
			case TrainHelper.G:
				strRetValue = getRLocABCDF(tInfo);
				break;
			case TrainHelper.L:
				break;
			case TrainHelper.QT:
				strRetValue = getRLoc118(tInfo);
				break;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return strRetValue;
	}

	public String getRLocABCDF(TicketInfo tInfo) {
		int seatType = -1;
		for(int i=0; i<seatTypes.size(); i++){
			if (seatTypes.valueAt(i).equals(tInfo.getSeat_type_name())){
				seatType = seatTypes.keyAt(i);
			}
		}
		String strSymbol = tInfo.getSeat_no().substring(tInfo.getSeat_no().length()-1);
		switch(seatType){
		case SWZ:
			if (strSymbol.equals("A")){
				return mLoc20;
			}else if (strSymbol.equals("C")){
				return mLoc21;
			}if (strSymbol.equals("F")){
				return mLoc1;
			}
			break;
		case ZY:
			if (strSymbol.equals("A")){
				return mLoc20;
			}else if (strSymbol.equals("C")){
				return mLoc21;
			}if (strSymbol.equals("D")){
				return mLoc21;
			}if (strSymbol.equals("F")){
				return mLoc20;
			}
			break;
		case ZE:
			if (strSymbol.equals("A")){
				return mLoc30;
			}else if (strSymbol.equals("B")){
				return mLoc31;
			}else if (strSymbol.equals("C")){
				return mLoc32;
			}if (strSymbol.equals("D")){
				return mLoc21;
			}if (strSymbol.equals("F")){
				return mLoc20;
			}
			break;
		}
		return null;
	}

	public String getRLoc118(TicketInfo tInfo) {
		if (tInfo.getSeat_type_name().equals(getSeatType(YZ))){
			int coach_no = Integer.valueOf(tInfo.getCoach_no());
			int seat_no = Integer.valueOf(tInfo.getSeat_no());
			//按118座算
//			if (coach_no != 8){
				if (seat_no == 1 || seat_no == 4 || seat_no == 115 || seat_no == 118){
					return mLoc20;
				}else if (seat_no == 2 || seat_no == 3 || seat_no == 116 || seat_no == 117){
					return mLoc21;
				}else if (tInfo.getSeat_no().endsWith("0") || tInfo.getSeat_no().endsWith("4")
						|| tInfo.getSeat_no().endsWith("5") || tInfo.getSeat_no().endsWith("9")){
					return mLoc30;
				}else if (tInfo.getSeat_no().endsWith("1") || tInfo.getSeat_no().endsWith("6")){
					return mLoc31;
				}else if (tInfo.getSeat_no().endsWith("2") || tInfo.getSeat_no().endsWith("7")){
					return mLoc32;
				}else if (tInfo.getSeat_no().endsWith("3") || tInfo.getSeat_no().endsWith("8")){
					return mLoc21;
				}
//			}else{
//				if (seat_no == 1 || seat_no == 113 || seat_no==116){
//					return mLoc20;
//				}else if (seat_no == 2 || seat_no == 114 || seat_no==115){
//					return mLoc21;
//				}else if (tInfo.getSeat_no().endsWith("3") || tInfo.getSeat_no().endsWith("5")
//						|| tInfo.getSeat_no().endsWith("8") || tInfo.getSeat_no().endsWith("0")){
//					return mLoc30;
//				}else if (tInfo.getSeat_no().endsWith("4") || tInfo.getSeat_no().endsWith("9")){
//					return mLoc31;
//				}else if (tInfo.getSeat_no().endsWith("0") || tInfo.getSeat_no().endsWith("5")){
//					return mLoc32;
//				}else if (tInfo.getSeat_no().endsWith("1") || tInfo.getSeat_no().endsWith("6")){
//					return mLoc21;
//				}else if (tInfo.getSeat_no().endsWith("2") || tInfo.getSeat_no().endsWith("7")){
//					return mLoc20;
//				}
//			}
		}
		return null;
	}
}
