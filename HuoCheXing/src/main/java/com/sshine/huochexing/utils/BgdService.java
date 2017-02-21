package com.sshine.huochexing.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Binder;
import android.os.IBinder;
import android.os.Vibrator;
import android.view.WindowManager;

import com.sshine.huochexing.bean.TravelBrief;
import com.sshine.huochexing.model.CustomDialog;
import com.sshine.huochexing.trainInfos.TrainInfoAty;
import com.sshine.huochexing.value.SF;


public class BgdService extends Service {
////	public static final long START_SERVICE_TIME = 6L * 3600 * 1000; // 开启服务的时间,暂弃用
//	public static final long START_GEOFENCE_TIME = 1L * 3600 * 1000; // 开启地理围栏服务的时间
////	public static final long DETECT_INTERVAL_TIME = 3L * 60 * 1000; // 检测间隔时间
////	public static final long TRAIN_EXPIRATION_TIME = 2L * 3600 * 1000; // 车次过期时间
//
//	public static final int GEOFENCE_RADIUS_TYPE = 1;
//	public static final long GEOFENCE_EXPIRATION = 24L * (3600 * 1000);// 24小时
//	
//	//2为经过出发站验证的定位信息发送，1为乘车时间区间内的发送。
//	public static final int LOC_MSG_WEIGHT_1 = 1;
//	public static final int LOC_MSG_WEIGHT_2 = 2;

	/*
	 * 查看车是否到站
	 * 请求json:{"requestType":"getTrainArriveStatus","trainNum":"K398","t_StartTime"
	 * :"2013-09-08","station":"吉林"}
	 * 返回json:{"resultCode":"1","isArrived":"1"}
	 * 
	 * 上传定位信息
	 * 请求json:{"requestType":"uploadLocMsg","weight":"2",isLogin"1","key":"353663698","trainNum"
	 * :"9876","t_StartTime":"2013-09-30","longitude":"98.352","latitude":"33.909" }
	 * 返回json:{"resultCode":"1","isContinue":"1"} //1继续上传，0不需要再上传
	 */
//	private String strUrl = "";
//
//	private LocationClient mLocClient;
//
//	private GeofenceClient mGeofenceClient;
//	private TravelBrief tb = null;
//	private MyLocationListenner myListener = new MyLocationListenner();
//	private HttpUtil httpUtil = new HttpUtil();
	private SettingSPUtil setSP = MyApp.getInstance()
			.getSettingSPUtil();
//	private UserInfoSPUtil userSP = MyApp.getInstance()
//			.getUserInfoSPUtil();
//	private int intWeight = LOC_MSG_WEIGHT_2;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		// 处理提醒
		handleReminder();
		stopSelf();
//
//		try{
//			MyDatabase myDB = new MyDatabase(this);
//			SQLiteDatabase db = myDB.getWritableDatabase();
//			int intUid = -1;
//			UserInfoSPUtil userSP = MyApp.getInstance().getUserInfoSPUtil();
//			if (userSP.isLogin()) {
//				intUid = userSP.getUId();
//			}
//			String sql = "select * from UserTrainB where U_id=" + intUid
//					+ " and UserStatus != 2 order by StartTime;";
//			Cursor c = db.rawQuery(sql, null);
//			c.moveToFirst();
//			if (c.getCount() >= 1) {
//				String strId = c.getString(c.getColumnIndex("_id"));
//				tb = new TravelBrief();
//				tb.setU_id(c.getInt(c.getColumnIndex("U_id")));
//				tb.setTravelName(c.getString(c.getColumnIndex("TravelName")));
//				tb.setTrainNum(c.getString(c.getColumnIndex("T_id")));
//				tb.setStartStation(c.getString(c.getColumnIndex("StartStation")));
//				tb.setEndStation(c.getString(c.getColumnIndex("EndStation")));
//				tb.setStartTime(c.getString(c.getColumnIndex("StartTime")));
//				tb.setEndTime(c.getString(c.getColumnIndex("EndTime")));
//				tb.setT_StartTime(c.getString(c.getColumnIndex("T_StartTime")));
//				tb.setR_Date(c.getString(c.getColumnIndex("R_Date")));
//				tb.setStartLongitude(c.getString(c.getColumnIndex("StartLongitude")));
//				tb.setStartLatitude(c.getString(c.getColumnIndex("StartLatitude")));
//				
//				c.close();
//				db.close();
//				myDB.closeDB();
//					
//				long nowTime = (new Date()).getTime();
//				if (nowTime + START_GEOFENCE_TIME < TimeUtil
//						.getDTMSeconds(tb.getStartTime())){
//					stopSelf();
//				}else if (nowTime <= TimeUtil.getDTMSeconds(tb.getStartTime())){
//					//只注册一次围栏
//					if (strId != setSP.getGeofenceId()){
//						handleGeofence(strId);
//					}
//				}else if (!setSP.isGeofenceTriggered()){
//					if (nowTime <= TimeUtil.getDTMSeconds(tb.getEndTime())){
//						//发送定位信息
//						if (setSP.isContinueSendLoc()){
//							intWeight = LOC_MSG_WEIGHT_1;
//							mLocClient = ((MyApp) getApplication()).mLocationClient;
//							mLocClient.registerLocationListener(myListener);
//							mLocClient.start();
//							mLocClient.requestLocation();  //显示请求定位
//						}else{
//							if (mLocClient != null){
//								mLocClient.stop();
//							}
//						}
//					}else {
//						//移除围栏
//						if (mLocClient != null){
//							mLocClient.stop();
//						}
//						if (mGeofenceClient != null){
//							mGeofenceClient.stop();
//						}
//						setSP.setGeofenceId("");
//						setSP.setGeofenceTriggered(false);
//						setSP.setContinueSendLoc(true);
//						
//						//销毁服务以节省电量
//						stopSelf();
//					}
//				}
//			}else{
//				c.close();
//				db.close();
//				myDB.closeDB();
//				stopSelf();
//			}
//		}catch(Exception e){
//			//先保证后台服务不会报错
//			e.printStackTrace();
//		}
	}

//	private void handleGeofence(String strId) {
//		mGeofenceClient = ((MyApp) getApplication()).mGeofenceClient;
//		BDGeofence fence = null;
//		try{
//			// 初始化GeofenceClient
//			// 注册地理围栏进入的监听器
//			mGeofenceClient
//					.registerGeofenceTriggerListener(new GeofenceTriggerListener());
//			fence = new BDGeofence.Builder()
//					.setGeofenceId(strId)
//					.setCircularRegion(Double.valueOf(tb.getStartLongitude()),
//							Double.valueOf(tb.getStartLatitude()),
//							GEOFENCE_RADIUS_TYPE)
//					.setExpirationDruation(GEOFENCE_EXPIRATION)
//					.setCoordType(BDGeofence.COORD_TYPE_GCJ).build();
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//		if (HttpUtil.isNetworkConnected(getApplicationContext())) {
//			// 无网络时添加围栏会报错
//			try {
//				mGeofenceClient.addBDGeofence(fence, new AddGeofenceListener());
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//	}
//	
//	/**
//	 * 实现添加围栏监听器
//	 * 
//	 */
//	public class AddGeofenceListener implements OnAddBDGeofencesResultListener {
//
//		@Override
//		public void onAddBDGeofencesResult(int statusCode, String geofenceId) {
//			try {
//				if (statusCode == BDLocationStatusCodes.SUCCESS) {
//					setSP.setGeofenceId(geofenceId);
//					if (mGeofenceClient != null) {
//						mGeofenceClient.start();// 在添加地理围栏成功后，开启地理围栏服务，对本次创建成功且已进入的地理围栏，可以实时的提醒
//					}
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//	}
//
//	/**
//	 * 实现进入围栏监听器
//	 * 
//	 */
//	public class GeofenceTriggerListener implements OnGeofenceTriggerListener {
//
//		@Override
//		public void onGeofenceTrigger(String geofenceId) {
//			L.i("已进入围栏");
//			try{
//			if ((!setSP.isGeofenceTriggered()) && tb != null) {
//				setSP.setGeofenceTriggered(true);  //只检测一次
//				//移除围栏
//				if (mGeofenceClient != null){
//					mGeofenceClient.stop();
//				}
//				setSP.setGeofenceId("");
//				setSP.setGeofenceTriggered(false);
//				setSP.setContinueSendLoc(true);
//				mLocClient = ((MyApp) getApplication()).mLocationClient;
//				mLocClient.registerLocationListener(myListener);
//				
//				JSONObject jObj = new JSONObject();
//				try {
//					jObj.put("requestType", "getTrainArriveStatus");
//					jObj.put("trainNum", tb.getTrainNum());
//					jObj.put("t_StartTime", tb.getT_StartTime());
//					jObj.put("station", tb.getStartStation());
//					
//					if (httpUtil.post(strUrl, jObj.toString())) {
//						JSONObject jsonObj = new JSONObject(
//								(String) httpUtil.getResponseStr());
//						int intResultCode = jsonObj
//								.getInt(HttpUtil.RESULT_CODE);
//						switch (intResultCode) {
//						case HttpUtil.MSG_RECEIVE_FAIL:
//							break;
//						case HttpUtil.MSG_RECEIVE_SUCCESS:
//							int isArrived = jsonObj.getInt("isArrived");
//							if (isArrived == 1){
//								if (setSP.isContinueSendLoc()){
//									//发送定位数据
//									intWeight = LOC_MSG_WEIGHT_1;
//									mLocClient.start();
//									mLocClient.requestLocation();  //显示请求定位
//								}else{
//									if (mLocClient != null){
//										mLocClient.stop();
//									}
//								}
//							}
//							break;
//						}
//					}
//				} catch (JSONException e) {
//					e.printStackTrace();
//				} catch (ClientProtocolException e) {
//					e.printStackTrace();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//			}catch(Exception e){
//				//
//			}
//		}
//	}


	class LocServiceBinder extends Binder {
		public BgdService getService() {
			return BgdService.this;
		}
	}

//	public class MyLocationListenner implements BDLocationListener {
//		@Override
//		public void onReceiveLocation(BDLocation location) {
//			L.i("已定位");
//			try{
//			if ((!HttpUtil.isNetworkConnected(getApplicationContext()))
//					&& location == null
//					|| (location.getLocType() != BDLocation.TypeGpsLocation && location
//							.getLocType() != BDLocation.TypeNetWorkLocation))
//				return;
//			JSONObject jObj = new JSONObject();
//			try {
//				jObj.put("requestType", "uploadLocMsg");
//				jObj.put("isLogin", userSP.isLogin());
//				jObj.put("weight", intWeight);
//				if (userSP.isLogin()) {
//					jObj.put("key", userSP.getUId());
//				} else {
//					jObj.put("key", MyUtils.getIMEI(getApplicationContext()));
//				}
//				jObj.put("trainNum", tb.getTrainNum());
//				jObj.put("t_StartTime", tb.getT_StartTime());
//				jObj.put("longitude", location.getLongitude());
//				jObj.put("latitude", location.getLatitude());
//				if (httpUtil.post(strUrl, jObj.toString())) {
//					JSONObject jsonObj = new JSONObject(
//							(String) httpUtil.getResponseStr());
//					int intResultCode = jsonObj.getInt(HttpUtil.RESULT_CODE);
//					switch (intResultCode) {
//					case HttpUtil.MSG_RECEIVE_FAIL:
//						break;
//					case HttpUtil.MSG_RECEIVE_SUCCESS:
//						L.i("发送定位信息");
//						int intIsContinue = jsonObj.getInt("isContinue");
//						switch (intIsContinue) {
//						case 0:
//							setSP.setContinueSendLoc(false);
//							break;
//						case 1:
//							setSP.setContinueSendLoc(true);
//							break;
//						}
//						break;
//					}
//				}
//			} catch (ClientProtocolException e) {
//				e.printStackTrace();
//			} catch (JSONException e) {
//				e.printStackTrace();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//			BgdService.this.stopSelf();
//			}catch(Exception e){
//				//
//			}
//		}
//
//		public void onReceivePoi(BDLocation poiLocation) {
//		}
//	}
//	
	private void handleReminder() {
		if (!setSP.isReminderSet()){
			return;
		}
		//轮询所有车次信息
		MyDatabase myDB = new MyDatabase(this);
		SQLiteDatabase db = myDB.getWritableDatabase();
		int intUid = -1;
		UserInfoSPUtil userSP = MyApp.getInstance().getUserInfoSPUtil();
		if (userSP.isLogin()) {
			intUid = userSP.getUId();
		}else{
			intUid = userSP.getUIdNotLogin();
		}
		String sql = "select * from UserTrainB where U_id=? order by StartTime;";
		Cursor c = db.rawQuery(sql, new String[]{String.valueOf(intUid)});
		while(c.moveToNext()) {
			TravelBrief tbReminder = new TravelBrief();
			final int intId = c.getInt(c.getColumnIndex("_id"));
			tbReminder.setTrainNum(c.getString(c.getColumnIndex("T_id")));
			tbReminder.setStartStation(c.getString(c.getColumnIndex("StartStation")));
			tbReminder.setEndStation(c.getString(c.getColumnIndex("EndStation")));
			tbReminder.setStartTime(c.getString(c.getColumnIndex("StartTime")));
			tbReminder.setEndTime(c.getString(c.getColumnIndex("EndTime")));
			tbReminder.setT_StartTime(c.getString(c.getColumnIndex("T_StartTime")));
			tbReminder.setReceivedReminder(c.getInt(c.getColumnIndex("ReceivedReminder")));
			tbReminder.setIsRepeatReminder(c.getInt(c.getColumnIndex("IsRepeatReminder")));
			
			//如果到站提醒未显示过或者要求重复提醒
			if (tbReminder.getReceivedReminder() == 0 || tbReminder.getIsRepeatReminder() == 1){
				String strReminderStation = "";
				String strReminderTime = "";
				Date now = new Date();
				long lStartTime = TimeUtil.getDTMSeconds(tbReminder.getStartTime());
				long lEndTime = TimeUtil.getDTMSeconds(tbReminder.getEndTime());
				if (setSP.isStartReminder() 
						&& (now.getTime() + setSP.getPreReminderTime() >= lStartTime)
						&& (now.getTime() < lStartTime)){
					strReminderStation = tbReminder.getStartStation();
					strReminderTime = tbReminder.getStartTime();
				}else if (setSP.isEndReminder()
						&& (now.getTime() + setSP.getPreReminderTime() >= lEndTime)
						&& (now.getTime() < lEndTime)){
					strReminderStation = tbReminder.getEndStation();
					strReminderTime = tbReminder.getEndTime();
				}
				//有符合条件的提醒
				if (!"".equals(strReminderStation)){
					// 弹出提醒
					SimpleDateFormat df1 = TimeUtil.getDTFormat();
					String strTime = TimeUtil.get_T_Str(TimeUtil.getFmt_DT_DT_DiffStr(strReminderTime, df1.format(now)));
					if (!(strTime == null || "".equals(strTime))){
						if (setSP.isVibrate()) {
							Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
							vibrator.vibrate(1000);
						}
						if (setSP.isRing()){
							MyUtils.ringNotifycation(BgdService.this);
						}
						String strMsg = tbReminder.getTrainNum() + "次列车将在"+ strTime
								+ "后到达" + strReminderStation + "站" + SF.SUCCESS;
						CustomDialog oldDlg = ((MyApp)getApplication()).mReminderDialog;
						if (oldDlg != null){
							oldDlg.dismiss();
						}
						CustomDialog dlg = new CustomDialog.Builder(getApplicationContext(),
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dlg, int which) {
									switch(which){
									case AlertDialog.BUTTON_POSITIVE:
										Intent intent = new Intent(getApplicationContext(), TrainInfoAty.class);
										intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
										startActivity(intent);
										break;
									case AlertDialog.BUTTON_NEGATIVE:
										if (intId == 0){
											//如果是提醒演示的话则跳过更新数据库.
										}else{
											MyDatabase myDB = new MyDatabase(getApplicationContext());
											if (((CustomDialog) dlg).isCheckBoxChecked()){
												myDB.updateUserTrainB(intId, "isRepeatReminder", "0");
											}else{
												myDB.updateUserTrainB(intId, "isRepeatReminder", "1");
											}
											myDB.closeDB();
										}
										break;
									}
								}
							}).setTitle("火车行提醒您")
							.setMessage(strMsg)
							.setCheckboxText("不再提醒")
							.setPositiveButton("打开火车行")
							.setNagativeButton("好的")
							.create();
						dlg.getWindow().setType((WindowManager.LayoutParams.TYPE_SYSTEM_ALERT));
						dlg.show();
						((MyApp)getApplication()).mReminderDialog = dlg;
						//设置已提醒过
					    myDB.updateUserTrainB(intId, "ReceivedReminder", "1");
					}
				}
			}
		}
		c.close();
		db.close();
		myDB.closeDB();
	}
}
