package com.sshine.huochexing.ticketOnline;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Vibrator;
import android.util.SparseIntArray;
import android.widget.Toast;

import com.sshine.huochexing.R;
import com.sshine.huochexing.bean.A6Info;
import com.sshine.huochexing.bean.BookingInfo;
import com.sshine.huochexing.bean.MonitorInfo;
import com.sshine.huochexing.bean.QueryLeftNewDTOInfo;
import com.sshine.huochexing.bean.QueryLeftNewInfo;
import com.sshine.huochexing.bean.TargetInfo;
import com.sshine.huochexing.listener.OnProgressListener;
import com.sshine.huochexing.utils.A6Util;
import com.sshine.huochexing.utils.HttpHelper;
import com.sshine.huochexing.utils.HttpUtil;
import com.sshine.huochexing.utils.L;
import com.sshine.huochexing.utils.MyApp;
import com.sshine.huochexing.utils.PersistentUtil;
import com.sshine.huochexing.utils.SeatHelper;
import com.sshine.huochexing.utils.TimeUtil;
import com.sshine.huochexing.value.SF;
import com.sshine.huochexing.value.StoreValue;
import com.sshine.huochexing.value.TT;

public class BgdService2 extends Service {
	public static final int MSG_REFRESH_CURR_MONIOTR_INFO_STATUS = 1;
	public static final int MSG_REFRESH_ALL_STATUS =2;
	private static final int MSG_START_CONFIRM_ATY = 3;
	private static final int MSG_RELOGIN = 4;
	
	public static final int STATUS_STOPED = 0;
	public static final int STATUS_PAUSED = 6;
	public static final int STATUS_RUNNING = 1;
	public static final int STATUS_EXPIRED = 2;
	public static final int STATUS_NO_NETWORK = 3;
	public static final int STATUS_CAN_NOT_BOOKING = 4;
	public static final int STATUS_WAIT_HANDLE_UNCOMPLETE_ORDERS = 5;
	private MyBinder myBinder = new MyBinder();
	private Vibrator mVibrator;
	private OnProgressListener mOnProgressListener;
	private List<MonitorInfo> mLstMInfos;
	private BookingInfo mBgdBInfo = new BookingInfo();
	private MonitorInfo mCurrMInfo;
	private boolean mIsHandlingMonitors = false;
	private List<MonitorInfo> mPausedMInfos = new ArrayList<MonitorInfo>();
	private boolean mHasPausedMInfos = false;
	
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch(msg.what){
			case MSG_START_CONFIRM_ATY:
				startConfirmAty((TargetInfo)msg.obj);
				break;
			case MSG_RELOGIN:
				showMsg("登录信息已过期，请重新登录");
				break;
			}
		};
	};

	@Override
	public void onCreate() {
		super.onCreate();
		L.i("BgdService2 onCreate");
		mVibrator = (Vibrator) getApplicationContext().getSystemService(
				VIBRATOR_SERVICE);
		//防止干扰前台12306查询功能
		HttpHelper httpHelper = new HttpHelper(A6Util.getHttpClient(false));
		mBgdBInfo.setHttpHelper(httpHelper);
		MyApp myApp = ((MyApp) getApplication());
		myApp.isBgdService2Started = true;
	}

	protected void notifyLstMInfosChanged(List<MonitorInfo> lstMInfos) {
		mLstMInfos = lstMInfos;
		saveMInfos2Local();
		handleMonitors();
	}

	private synchronized void setCurrMInfo() {
		if (mLstMInfos == null || mLstMInfos.size() == 0) {
			mCurrMInfo = null;
			return;
		}
		try {
			//没有当前监控则找第一个
			if (mCurrMInfo == null){
				for (MonitorInfo mInfo1 : mLstMInfos) {
					if (mInfo1 != null && mInfo1.isRunning()){
						mCurrMInfo = (MonitorInfo) mInfo1.clone();
						return;
					}
				}
			}
			
			//先往后找
			for (MonitorInfo mInfo1 : mLstMInfos) {
				if (mInfo1 != null && mInfo1.isRunning()){
					if(mInfo1.getNativeIndex() > mCurrMInfo.getNativeIndex()){
						mCurrMInfo = (MonitorInfo) mInfo1.clone();
						return;
					}
				}
			}
			//再找前面的
			for (MonitorInfo mInfo1 : mLstMInfos) {
				if (mInfo1 != null && mInfo1.isRunning()){
					if (mInfo1.getNativeIndex() <= mCurrMInfo.getNativeIndex()){
						mCurrMInfo = (MonitorInfo) mInfo1.clone();
						return;
					}
				}
			}
			mCurrMInfo = null;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public void handleMonitors() {
		L.i("handleMonitors");
		if (!hasMonitor() || (mIsHandlingMonitors)) {
			return;
		}
		mIsHandlingMonitors = true;
		new Thread() {
			public void run() {
				L.i("handleMonitors2");
				setCurrMInfo();
				while (mCurrMInfo != null) {
					if ((System.currentTimeMillis() - mCurrMInfo
							.getLastMonitorMillis()) < mCurrMInfo
							.getMonitorSpeed()) {
						setCurrMInfo();
						continue;
					}
					if (checkExpired()){
						sendRefreshCurrMInfoStatus();
						setCurrMInfo();
						continue;
					}
					if (checkNoNetwork()){
						sendRefreshCurrMInfoStatus();
						break;
					}
					keepA6Online();
					
					mCurrMInfo.setLastMonitorMillis(System.currentTimeMillis());
					mCurrMInfo.setRetryCount(mCurrMInfo.getRetryCount() + 1);
					sendRefreshCurrMInfoStatus();
					TargetInfo tInfo = getTargetInfo();
					if (tInfo == null) {
						setCurrMInfo();
						continue;
					}
					mBgdBInfo.setHttpHelper(MyApp.getInstance().getCommonBInfo().getHttpHelper());
					if (!preparedOrder(tInfo)){
						setCurrMInfo();
						continue;
					}
					pauseMonitors();
					sendRefreshAllStatus();
					doAlarm();
					if (tInfo != null && tInfo.getQlnInfo() != null && mCurrMInfo != null){
						Message msg = mHandler.obtainMessage(MSG_START_CONFIRM_ATY);
						msg.obj = tInfo;
						mHandler.sendMessage(msg);
						mIsHandlingMonitors = false;
						break;
					}
				}
				mIsHandlingMonitors = false;
			}
		}.start();
	}
	public void sendRefreshCurrMInfoStatus() {
		if (mOnProgressListener != null) {
			mOnProgressListener.onProgress(MSG_REFRESH_CURR_MONIOTR_INFO_STATUS,
					mCurrMInfo);
		}
	};
	public void sendRefreshAllStatus(){
		if (mOnProgressListener != null) {
			mOnProgressListener.onProgress(MSG_REFRESH_ALL_STATUS,
					null);
		}
	}
	private boolean preparedOrder(TargetInfo tInfo) {
		if (!A6Util.isCanBooking()){
			setAllMonitorsStatus(STATUS_CAN_NOT_BOOKING);
			sendRefreshCurrMInfoStatus();
			return false;
		}
		if (!A6Util.checkUser(mBgdBInfo)){
			return false;
		}
		QueryLeftNewDTOInfo qlndInfo = tInfo.getQlnInfo().getQueryLeftNewDTO();
		String secretStr = A6Util.decode(tInfo.getQlnInfo().getSecretStr());
		mBgdBInfo.setSecretStr(secretStr);
		mBgdBInfo.setTrain_date(mCurrMInfo.getStart_time());
		mBgdBInfo.setBack_train_date(mCurrMInfo.getStart_time());
		mBgdBInfo.setTour_flag(TT.getTour_flags().get("dc"));
		mBgdBInfo.setPurpose_codes(mCurrMInfo.getPurpose_codes());
		mBgdBInfo.setQuery_from_station_name(qlndInfo.getFrom_station_name());
		mBgdBInfo.setQuery_to_station_name(qlndInfo.getTo_station_name());
		int result = A6Util.submitOrderRequest(mBgdBInfo);
		switch(result){
		case -1:
			mHandler.sendEmptyMessage(MSG_RELOGIN);
			return false;
		case 0:
			return false;
		case 2:
			setAllMonitorsStatus(STATUS_WAIT_HANDLE_UNCOMPLETE_ORDERS);
			sendRefreshCurrMInfoStatus();
			return false;
		case 3:
			mBgdBInfo.setQuery_detect_key(null);
			mBgdBInfo.setQuery_detect_value(null);
			mCurrMInfo.setStatus(STATUS_EXPIRED);
			return false;
		default:
			break;
		}
		return true;
	}

	private void keepA6Online() {
		if (A6Util.isNeedCheckUser(MyApp.getInstance().getCommonBInfo())){
			A6Util.checkUser(MyApp.getInstance().getCommonBInfo());
		}
	}

	private TargetInfo getTargetInfo(){
		mBgdBInfo.setTrain_date(mCurrMInfo.getStart_time());
		mBgdBInfo.setFrom_station(mCurrMInfo
				.getFrom_station_telecode());
		mBgdBInfo.setTo_station(mCurrMInfo.getTo_station_telecode());
		mBgdBInfo.setPurpose_codes(mCurrMInfo.getPurpose_codes());
		A6Info<List<QueryLeftNewInfo>> a6Info = A6Util.queryTickets(mBgdBInfo);
		if (a6Info == null || a6Info.getData() == null){
			return null;
		}
		for (QueryLeftNewInfo qlnInfo : a6Info.getDataObject()) {
			QueryLeftNewDTOInfo qlndInfo = qlnInfo
					.getQueryLeftNewDTO();
			int trainIndex = mCurrMInfo.getLstTrainNames().indexOf(qlndInfo.getStation_train_code());
			if (trainIndex != -1 && mCurrMInfo.getSelectedTrainsNames()[trainIndex]){
				SeatHelper sHelper = new SeatHelper(qlndInfo);
				SparseIntArray saNums = sHelper
						.getSeatReferenceNums();
				int num = mCurrMInfo.getLstPNativeIndexes().size();
				for (int i = 0; i < mCurrMInfo.getLstSeatTypes()
						.size(); i++) {
					Integer seatType = mCurrMInfo.getLstSeatTypes()
							.get(i);
					Integer seatNum = saNums.get(seatType);
					if (seatNum != null && (seatNum >= num)) {
						TargetInfo tInfo = new TargetInfo();
						tInfo.setQlnInfo(qlnInfo);
						tInfo.setSeatHelper(sHelper);
						tInfo.setSeatType(seatType);
						tInfo.setNum(num);
						return tInfo;
					}
				}
			}
		}
		return null;
	}

	private boolean checkNoNetwork() {
		if (HttpUtil.isNetworkConnected(getApplicationContext())){
			return false;
		}else{
			setAllMonitorsStatus(STATUS_NO_NETWORK);
			return true;
		}
	}

	private boolean checkExpired() {
		String date1 = TimeUtil.getDFormat().format(new Date());
		if (date1.compareTo(mCurrMInfo.getStart_time()) <= 0){
			return false;
		}else{
			mCurrMInfo.setStatus(STATUS_EXPIRED);
			mCurrMInfo.setRunning(false);
			return true;
		}
	}
	
	private void setAllMonitorsStatus(int status){
		boolean runningStatus = (status==STATUS_RUNNING)?true:false;
		if (mCurrMInfo != null){
			mCurrMInfo.setRunning(runningStatus);
			mCurrMInfo.setStatus(status);
		}
		for (MonitorInfo mInfo : mLstMInfos) {
			if (mInfo.isRunning()) {
				mInfo.setRunning(runningStatus);
				mInfo.setStatus(status);
			}
		}
		saveMInfos2Local();
	}

	/**
	 *  暂停所有抢票任务
	 */
	public synchronized void pauseMonitors() {
		if (mLstMInfos == null){
			return;
		}
		if (mCurrMInfo != null){
			mCurrMInfo.setRunning(false);
			mCurrMInfo.setStatus(STATUS_PAUSED);
		}
		for (MonitorInfo mInfo : mLstMInfos) {
			if (mInfo != null && mInfo.isRunning()) {
				mInfo.setRunning(false);
				mInfo.setStatus(STATUS_PAUSED);
				mPausedMInfos.add(mInfo);
			}
		}
		mHasPausedMInfos = true;
		saveMInfos2Local();
	}
	
	/**
	 * 是否已有已暂停的监控信息
	 * @return
	 */
	public boolean hasPausedMInfos(){
		return mHasPausedMInfos;
	}
	
	/*
	 * 恢复被暂停的监控任务
	 */
	public synchronized void resumeMonitors(){
		if (mPausedMInfos == null){
			mHasPausedMInfos = false;
		}
		for(MonitorInfo mInfo1:mLstMInfos){
			for (MonitorInfo mInfo : mPausedMInfos) {
				if (mInfo != null && mInfo.getNativeIndex() == mInfo1.getNativeIndex()) {
					mInfo1.setRunning(true);
					mInfo1.setStatus(STATUS_RUNNING);
					break;
				}
			}
		}
		mPausedMInfos.clear();
		mHasPausedMInfos = false;
		saveMInfos2Local();
		handleMonitors();
	}
	
	private void startConfirmAty(TargetInfo tInfo) {
		if (tInfo != null && tInfo.getQlnInfo() != null && mCurrMInfo != null){
			showMsg("抢票监控已检测到可用票"+SF.SUCCESS);
			MyApp.getInstance().setBgdBInfo(mBgdBInfo);
			Intent intent = new Intent(getApplicationContext(), ConfirmPassengerAty.class);
			intent.putExtra(ConfirmPassengerAty.EXTRA_TRAIN_INFO, tInfo.getQlnInfo().getQueryLeftNewDTO());
			intent.putExtra(ConfirmPassengerAty.EXTRA_TOUR_FLAG, TT.getTour_flags().get("dc"));
			intent.putExtra(ConfirmPassengerAty.EXTRA_MODE, ConfirmPassengerAty.EXTRA_MODE_MONITOR);
			intent.putIntegerArrayListExtra(ConfirmPassengerAty.EXTRA_P_NATIVE_INDEXS, (ArrayList<Integer>)mCurrMInfo.getLstPNativeIndexes());
			intent.putExtra(ConfirmPassengerAty.EXTRA_DEFAULT_SEAT_TYPE, tInfo.getSeatType());
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		}
	}
	
	private void showMsg(CharSequence cs1) {
		Toast.makeText(getApplicationContext(), cs1, Toast.LENGTH_SHORT).show();
	}

	private void doAlarm() {
		if (mCurrMInfo.isVibrate()){
			mVibrator.vibrate(1000);
		}
		if (mCurrMInfo.isRing()){
			SoundPool soundPool = new SoundPool(2, AudioManager.STREAM_SYSTEM, 5);
			soundPool.load(this, R.raw.ticket_alarm, 1);
			soundPool.play(1, 1, 1, 0, 0, 1);
		}
	}

	public boolean hasMonitor() {
		if (mLstMInfos == null) {
			return false;
		}
		for (MonitorInfo mInfo : mLstMInfos) {
			if (mInfo.isRunning()) {
				return true;
			}
		}
		return false;
	}

	public void setOnProgressListener(OnProgressListener listener) {
		this.mOnProgressListener = listener;
	}
	
	@Override
	public void onDestroy() {
		//尽量保存状态
		saveMInfos2Local();
		MyApp myApp = ((MyApp) getApplication());
		myApp.isBgdService2Started = false;
		super.onDestroy();
	}

	/*
	 * 保存监控信息到本地
	 */
	private void saveMInfos2Local() {
		String strMFilesPath = MyApp.getInstance().getPathBaseRoot(StoreValue.MONITOR_INFOS_FILE);
		PersistentUtil.writeObject(mLstMInfos, strMFilesPath);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return myBinder;
	}

	public class MyBinder extends Binder {
		BgdService2 getService() {
			return BgdService2.this;
		}
	}
}
