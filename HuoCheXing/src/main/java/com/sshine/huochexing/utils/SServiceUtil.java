package com.sshine.huochexing.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sshine.huochexing.bean.SServiceInfo;
import com.sshine.huochexing.bean.StationInfo;
import com.sshine.huochexing.value.ServiceValue;
import com.sshine.huochexing.value.TT;

public class SServiceUtil {
	private static AESCrypt mAES = null;
	private static String mVersionName = MyUtils.getVersionName(MyApp.getInstance());
	private static int mVersionCode = MyUtils.getVersionCode(MyApp.getInstance());

	public static SServiceInfo post(String url, JSONObject jsonObj) throws Exception{
		if (TextUtils.isEmpty(url) || jsonObj == null){
			return null;
		}
		String jsonMessage = jsonObj.toString();
		if (mAES == null) {
			mAES = new AESCrypt();
		}
		List<NameValuePair> lstParams = new ArrayList<NameValuePair>();
		jsonObj.put("version_code", mVersionCode);
		jsonObj.put("version_name", mVersionName);
		lstParams.add(new BasicNameValuePair("message", jsonObj.toString()));
		jsonMessage = mAES.encrypt(jsonMessage);
		String responseStr = MyApp.getInstance().getMyHttpHelper()
				.post(null, url, lstParams);
		responseStr = mAES.decrypt(responseStr);
		JSONObject jsonObj1 = new JSONObject(responseStr);
		SServiceInfo serverJson = new SServiceInfo();
		serverJson.setJsonObj(jsonObj1);
		serverJson.setResultCode(jsonObj1.getInt(TT.RESULT_CODE));
		serverJson.setData(jsonObj1.optString("data"));
		serverJson.setMessages(jsonObj1.optString("messages"));
		return serverJson;
	}
	
	/**
	 * 获取车站列表
	 * @param station_train_code
	 * @param train_no
	 * @param from_station_telecode
	 * @param to_station_telecode
	 * @return
	 */
	public static List<StationInfo> getStations(String station_train_code, String train_no, String from_station_telecode, String to_station_telecode){
		try{
			JSONObject jsonObj = new JSONObject();
			jsonObj.put(TT.REQUEST_TYPE, "getStations");
			jsonObj.put(TT.STATION_TRAIN_CODE, station_train_code);
			jsonObj.put(TT.TRAIN_NO, train_no);
			jsonObj.put(TT.FROM_STATION_TELECODE, from_station_telecode);
			jsonObj.put(TT.TO_STATION_TELECODE, to_station_telecode);
			String url = ServiceValue.BASE_PATH+ServiceValue.P_TRAIN_SCHEDULE;
			SServiceInfo serverJson = post(url, jsonObj);
			if (serverJson == null){
				return null;
			}
			List<StationInfo> lstStations = (new Gson()).fromJson(serverJson.getJsonObj().getString("stations"),
					new TypeToken<List<StationInfo>>(){}.getType());
			return lstStations;
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	public static String getLTime(String trainNum, String station){
		try{
			JSONObject jsonObj = new JSONObject();
			jsonObj.put(TT.REQUEST_TYPE, "lateTime");
			jsonObj.put("train_num", trainNum);
			jsonObj.put("station", station);
			String url = ServiceValue.NODEJS_PATH + ServiceValue.TRAIN_SCH;
			HttpUtil httpUtil = new HttpUtil();
			if (httpUtil.post(url, jsonObj.toString())){
				JSONObject jObj = new JSONObject(httpUtil.getResponseStr());
				return jObj.optString("message");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
}
