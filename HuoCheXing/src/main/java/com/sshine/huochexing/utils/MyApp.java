package com.sshine.huochexing.utils;

import java.io.File;

import android.os.Environment;
import android.os.Vibrator;

import com.baidu.frontia.FrontiaApplication;
import com.sshine.baidupush.server.BaiduPush;
import com.sshine.baidupush.server.BaiduPushTagsHelper;
import com.sshine.huochexing.bean.BookingInfo;
import com.sshine.huochexing.model.CustomDialog;

public class MyApp extends FrontiaApplication {
	public static final String USER_INFO_SP_FILE_NAME = "user_info_sp";
	public static final String A6_USER_INFO_SP_FILE_NAME = "a6_user_info_sp";
	public static final String SETTING_SP_FILE_NAME = "setting_sp";
	public static final String A6_SETTING_SP_FILE_NAME = "a6_setting_sp";

	//百度地图授权key
	public final static String API_KEY = "tOO9BsBVfnpETXjcG5SGoxHt";
	public final static String SECRIT_KEY = "0U3eYBC2KRR1g7kkiVe1kijdY5dCBDG1";
	public static final String MAP_KEY = "1af909338e09a9aafeea8904017d370b";
	// 迅飞语音appid
	public static final String XunFei_KEY = "52790675";
//	//微信
	public static final String WEIXIN_KEY = "wxe60cb91e6965c6c3";
//	//QQ授权key
//	public static final String QQ_APPID ="100551176";
//	public static final String QQ_APP_KEY = "78d4853f33d7260b8016e0009340ad03";

	private int newMsgCount = 0; // 通知栏新消息的数目

	private static MyApp mInstance;
	public boolean m_bKeyRight = true;
	// public BMapManager mBMapManager = null;
	private A6UserInfoSPUtil a6UserInfoSPUtil;
	private UserInfoSPUtil userInfoSPUtil;
	private SettingSPUtil settingSPUtil;
	private A6SettingSPUtil a6SettingSPUtil;
	private HttpHelper a6HttpHelper;
	private HttpHelper myHttpHelper;

	private MessageDB messageDB;
	private BaiduPush baiduPush;
	private BaiduPushTagsHelper tagsHelper;
	CustomDialog mReminderDialog = null;
	private BookingInfo mCommonBInfo = null;
	private BookingInfo mBgdBInfo = null;
	
	public String mDefaultClearTextPwd = null;

	// //百度定位
	// public LocationClient mLocationClient = null;
	// public GeofenceClient mGeofenceClient;

	public Vibrator mVibrator01;

	// 标明防盗服务是否已启动
	public boolean isAntiTheftServiceStarted = false;
	public boolean isBgdService2Started = false;
	public boolean isAlarmWakerSet = false;
	public boolean isAlarmWakerEffective = false;

	@Override
	public void onCreate() {
		super.onCreate();
		initData();
	}

	private void initData() {
		mInstance = this;
		// //百度地图
		// initEngineManager(this);
		// //百度定位
		// mLocationClient = new LocationClient(this);
		// mLocationClient.setAK(MyApp.MAP_KEY);
		// setLocationOption();
		// mGeofenceClient = new GeofenceClient(this);
		L.i("isAntiTheftServiceStarted:" + isAntiTheftServiceStarted);
		// 用户信息sharePreferences
		userInfoSPUtil = new UserInfoSPUtil(this, USER_INFO_SP_FILE_NAME);
	}
	public String getPathBaseRoot(String str1) {
		return getApplicationContext()
				.getFilesDir().getParentFile().getPath() + File.separator+str1;
	}
	/**
	 * 取得存储文件对象，优先存储在SD卡，如果SD卡不存在则存储在手机内部存储
	 * @param strName
	 * @return
	 */
	public File getStoreFile(String strName){
		File file1 = new File(getApplicationContext()
				.getFilesDir().getParentFile().getPath() + File.separator+strName);
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
		if (sdCardExist) {
			File sdDir = null;
			sdDir = Environment.getExternalStorageDirectory();// 获取根目录
			File file = new File(sdDir.getPath()+File.separator+strName);
			if (file.exists()){
				return file;
			}else{
				return file1;
			}
		}else{
			return file1;
		}
	}

	/**
	 * 取得设置tag的工具对象
	 * 
	 * @return 设置tag的工具对象
	 */
	public synchronized BaiduPushTagsHelper getTagsHelper() {
		if (tagsHelper == null) {
			tagsHelper = new BaiduPushTagsHelper();
		}
		return tagsHelper;
	}

	/**
	 * 取得Frontia推送服务
	 */
	/*
	 * public synchronized FrontiaPush getFrontiaPush() { if(frontiaPush ==
	 * null){ frontiaPush = Frontia.getPush(); } return frontiaPush; }
	 */

	/**
	 * 取得OfficicalUserInfoSPUtil对象
	 * 
	 * @return
	 */
	public synchronized A6UserInfoSPUtil getA6UserInfoSPUtil() {
		if (a6UserInfoSPUtil == null) {
			a6UserInfoSPUtil = new A6UserInfoSPUtil(this,
					A6_USER_INFO_SP_FILE_NAME);
		}
		return a6UserInfoSPUtil;
	}

	/**
	 * 取得UserInfoSPUtil对象
	 * 
	 * @return UserInfoSPUtil对象
	 */
	public synchronized UserInfoSPUtil getUserInfoSPUtil() {
		if (userInfoSPUtil == null) {
			userInfoSPUtil = new UserInfoSPUtil(this, USER_INFO_SP_FILE_NAME);
		}
		return userInfoSPUtil;
	}

	/**
	 * 取得SettingSPUtil对象
	 * 
	 * @return
	 */
	public synchronized SettingSPUtil getSettingSPUtil() {
		if (settingSPUtil == null) {
			settingSPUtil = new SettingSPUtil(this, SETTING_SP_FILE_NAME);
		}
		return settingSPUtil;
	}

	public synchronized A6SettingSPUtil getA6SettingSPUtil() {
		if (a6SettingSPUtil == null) {
			a6SettingSPUtil = new A6SettingSPUtil(this, A6_SETTING_SP_FILE_NAME);
		}
		return a6SettingSPUtil;
	}

	public synchronized MessageDB getMessageDB() {
		if (messageDB == null) {
			return new MessageDB(this);
		}
		return messageDB;
	}

	/**
	 * 取得BaiduPush对象
	 * 
	 * @return BaiduPush对象
	 */
	public synchronized BaiduPush getBaiduPush() {
		if (baiduPush == null) {
			return new BaiduPush(BaiduPush.HTTP_METHOD_POST, SECRIT_KEY,
					API_KEY);
		}
		return baiduPush;
	}

	public synchronized BookingInfo getCommonBInfo() {
		if (mCommonBInfo == null) {
			mCommonBInfo = new BookingInfo();
			mCommonBInfo.setHttpHelper(new HttpHelper(A6Util.getHttpClient()));
		}
		return mCommonBInfo;
	}

	public BookingInfo getBgdBInfo() {
		return mBgdBInfo;
	}

	public void setBgdBInfo(BookingInfo bInfo) {
		mBgdBInfo = bInfo;
	}

	public synchronized HttpHelper getMyHttpHelper() {
		if (myHttpHelper == null) {
			myHttpHelper = new HttpHelper(MyHtttpClient.getHttpClient());
		}
		return myHttpHelper;
	}

	// public void initEngineManager(Context context) {
	// if (mBMapManager == null) {
	// mBMapManager = new BMapManager(context);
	// }
	//
	// if (!mBMapManager.init(MAP_KEY,new MyGeneralListener())) {
	// Toast.makeText(getApplicationContext(),
	// "BMapManager  初始化错误!", Toast.LENGTH_LONG).show();
	// }
	// }
	// // 设置相关参数
	// private void setLocationOption() {
	// LocationClientOption option = new LocationClientOption();
	// option.setOpenGps(false); // 打开gps
	// option.setCoorType("bd0911"); // 设置坐标类型
	// option.setServiceName("com.baidu.location.service_v2.9");
	// option.setPoiExtraInfo(false);
	// // option.setAddrType("all");
	// option.setScanSpan(100);
	// option.setPriority(LocationClientOption.GpsFirst);
	// // option.setPoiNumber(10);
	// option.disableCache(true);
	// mLocationClient.setLocOption(option);
	// }
	public synchronized static MyApp getInstance() {
		return mInstance;
	}

	// // 常用事件监听，用来处理通常的网络错误，授权验证错误等
	// public static class MyGeneralListener implements MKGeneralListener {
	//
	// @Override
	// public void onGetNetworkState(int iError) {
	// if (iError == MKEvent.ERROR_NETWORK_CONNECT) {
	// // Toast.makeText(MyApplication.getInstance().getApplicationContext(),
	// "您的网络出错啦！",
	// // Toast.LENGTH_LONG).show();
	// }
	// else if (iError == MKEvent.ERROR_NETWORK_DATA) {
	// Toast.makeText(MyApp.getInstance().getApplicationContext(), "输入正确的检索条件！",
	// Toast.LENGTH_LONG).show();
	// }
	// }
	//
	// @Override
	// public void onGetPermissionState(int iError) {
	// if (iError == MKEvent.ERROR_PERMISSION_DENIED) {
	// //授权Key错误：
	// Toast.makeText(MyApp.getInstance().getApplicationContext(),
	// "请在 MyApplication.java文件输入正确的授权Key！", Toast.LENGTH_LONG).show();
	// MyApp.getInstance().m_bKeyRight = false;
	// }
	// }
	// }

	@Override
	public void onTerminate() {
		super.onTerminate();
	}

	public int getNewMsgCount() {
		return newMsgCount;
	}

	public void setNewMsgCount(int newMsgCount) {
		this.newMsgCount = newMsgCount;
	}
}
