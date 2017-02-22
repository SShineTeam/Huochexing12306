package com.sshine.huochexing.utils;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.baidu.android.pushservice.PushManager;
import com.sshine.baidupush.client.PushMessageReceiver;

public class TrainInfoUtil {
	
	public static int updateUserTrainList(SQLiteDatabase db) throws JSONException, ClientProtocolException, IOException{
		String strUrl = "http://huochexing.duapp.com/server/user_train.php";
		String strJson = "{\"requestType\":\"getTravels\",\"uid\":\""
				+ MyApp.getInstance().getUserInfoSPUtil().getUId()
				+ "\"}";
		HttpUtil httpUtil = new HttpUtil();
		if (httpUtil.post(strUrl, strJson)) {
			JSONObject jsonObj = new JSONObject(
					(String) httpUtil.getResponseStr());
			int intResultCode = jsonObj
					.getInt(HttpUtil.RESULT_CODE);
			switch (intResultCode) {
			case HttpUtil.MSG_RECEIVE_FAIL:
				return 0;
			case HttpUtil.MSG_RECEIVE_SUCCESS:
				JSONArray jsonArray = jsonObj
						.getJSONArray("travels");
				db.delete(
						"UserTrainB",
						"U_id=?",
						new String[] { String.valueOf(MyApp
								.getInstance().getUserInfoSPUtil()
								.getUId()) });
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject subObj = jsonArray.getJSONObject(i);
					ContentValues cv = new ContentValues();
					cv.put("ServerId", subObj.getString("serverId"));
					cv.put("U_id", MyApp.getInstance()
							.getUserInfoSPUtil().getUId());
					cv.put("T_id", subObj.getString("trainNum"));
					cv.put("TravelName",
							subObj.getString("travelName"));
					cv.put("StartStation",
							subObj.getString("startStation"));
					cv.put("EndStation",
							subObj.getString("endStation"));
					cv.put("R_Date", subObj.getString("r_Date"));
					cv.put("startLongitude",
							subObj.getString("startLongitude"));
					cv.put("startLatitude",
							subObj.getString("startLatitude"));
					cv.put("receiveMsg",
							subObj.getInt("receiveMsg"));
					cv.put("receivedReminder",
							subObj.getInt("receivedReminder"));
					cv.put("isRepeatReminder",
							subObj.getInt("isRepeatReminder"));
					cv.put("StartTime",
							subObj.getString("startTime"));
					cv.put("EndTime", subObj.getString("endTime"));
					cv.put("T_StartTime",
							subObj.getString("t_StartTime"));
					cv.put("UserStatus",
							subObj.getString("userStatus"));
					db.insert("UserTrainB", null, cv);
				}
				// 更新车次后 设置是否接收聊天信息
				PushMessageReceiver
						.setListTagsAction(PushMessageReceiver.UPDATE_TAGS);
				PushManager.listTags(MyApp.getInstance());
				// 调用listTags()后
				// 会回调PushMessageReceiver中的onListTags方法
				break;
			}
		} else {
			return 0;
		}
		return 1;
	}
}
