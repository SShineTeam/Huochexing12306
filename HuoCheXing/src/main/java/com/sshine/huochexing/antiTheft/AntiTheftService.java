package com.sshine.huochexing.antiTheft;

import java.io.IOException;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.SparseArray;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.sshine.huochexing.R;
import com.sshine.huochexing.listener.OnProgressListener;
import com.sshine.huochexing.utils.L;
import com.sshine.huochexing.utils.MyApp;
import com.sshine.huochexing.utils.SettingSPUtil;

public class AntiTheftService extends Service {

	public static final int PROTECT_BT = 0;
	public static final int PROTECT_EARPHONE = 1;
	public static final int PROTECT_CHARGE = 2;
	public static final int PROTECT_POCKET = 3;
	public static final int PROTECT_REST = 4;

	public static final int MSG_NO_BLUETOOTH_ADAPTER = 0;
	public static final int MSG_MSG = 1;
	public static final int MSG_DELAY_ON_TICK = 2;
	public static final int MSG_DELAY_FINISH = 3;
	public static final int MSG_NO_DEVICE_ADDRESS = 4;
	public static final int MSG_BIND_FAIL = 5;
	public static final int MSG_REFESH_VIEWS = 6;
	public static final int MSG_START_SUCCESS = 7;
	public static final int MSG_START_FAIL = 8;
	public static final int MSG_STOP_SUCCESS = 9;

	public static final int REQUEST_START_ALARM = 1;
	public static final int REQUEST_STOP_PROTECT = 2;
	public static final int REQUEST_SHOW_MSG = 3;

	private SparseArray<BaseProtector> protectors = new SparseArray<BaseProtector>();
	private MyBinder myBinder = new MyBinder();
	boolean isServiceStart = false;
	OnProgressListener mOnProgressListener;
	static final int NOTIFY_ID = 0;
	private Notification noti;
	private NotificationManager notiMang;
	private RemoteViews contentView;
	private SettingSPUtil setSP = MyApp.getInstance()
			.getSettingSPUtil();
	private MediaPlayer mPlayer = new MediaPlayer();
	private Vibrator mVibrator = null;
	private int mMaxVolume;
	private AudioManager mAM;
	private int mCurrVolume;
	private class VolumeThread extends Thread{

		@Override
		public void run() {
			while(mIsVolumeThreadStarted){
				setMaxVolume();
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			this.interrupt();
		}
	}
	private VolumeThread mVolumeThread = null;
	private boolean mIsAlarmRunning = false;
	private boolean mIsVolumeThreadStarted = false;

	@Override
	public IBinder onBind(Intent intent) {
		return myBinder;
	}

	public void request(BaseProtector protector, int requestType, String msg) {
		switch (requestType) {
		case REQUEST_START_ALARM:
			startAlarm(protector.getId());
			break;
		case REQUEST_STOP_PROTECT:
			stopProtect(protector.getId());
			break;
		case REQUEST_SHOW_MSG:
			showMsg(msg);
			break;
		}
	}

	public void startProtect(int protectType, Object obj) {
		BaseProtector protector = null;
		switch (protectType) {
		case PROTECT_BT:
			protector = new BTProtector(PROTECT_BT, this);
			break;
		case PROTECT_EARPHONE:
			protector = new EarphoneProtector(PROTECT_EARPHONE, this);
			break;
		case PROTECT_CHARGE:
			protector = new ChargeProtector(PROTECT_CHARGE, this);
			break;
		case PROTECT_POCKET:
			protector = new PocketProtector(PROTECT_POCKET, this);
			break;
		case PROTECT_REST:
			protector = new RestProtector(PROTECT_REST, this);
			break;
		default:
			if (mOnProgressListener != null) {
				showMsg("开启防护失败，未知的防护类型");
				mOnProgressListener.onProgress(MSG_START_FAIL, null);
			}
			return;
		}
		if (protector.start(obj)) {
			protector.setProtected(true);
			protector.setAlarmTriggered(false);
			protectors.put(protectType, protector);
			MyApp myApp = ((MyApp) getApplication());
			myApp.isAntiTheftServiceStarted = true;
			if (protector.isVibrateConflict()){
				showMsg("此模式与震动报警冲突，震动已被自动关闭");
			}
			if (setSP.isAntiTheftShowStatus()) {
				setNotification();
			}
			if (mOnProgressListener != null) {
				String strMsg = protector.getName() + "防护已开启";
				if (!protector.isNeedDelay()) {
					showMsg(strMsg);
				}
				mOnProgressListener.onProgress(MSG_START_SUCCESS, strMsg + ".");
			}
		} else {
			if (mOnProgressListener != null) {
				String strMsg = "开启" + protector.getName() + "防护失败";
				showMsg(strMsg);
				mOnProgressListener.onProgress(MSG_START_FAIL, strMsg + ".");
			}
		}
	}

	public void stopProtect(int protectType) {
		BaseProtector protector = protectors.get(protectType);
		if (protector != null) {
			if (protector.isProtected()) {
				protector.stop();
			}
			protectors.remove(protectType);
			if (!hasProtect()) {
				clearNotification();
			} else {
				setNotification();
			}
			if (mOnProgressListener != null) {
				mOnProgressListener.onProgress(MSG_STOP_SUCCESS,
						protector.getName() + "防护已关闭.");
			}
		} else if (mOnProgressListener != null) {
			mOnProgressListener.onProgress(MSG_STOP_SUCCESS, "防护已关闭.");
		}
	}

	private void clearNotification() {
		NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		nm.cancel(NOTIFY_ID);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mVibrator = (Vibrator) getApplicationContext().getSystemService(
				VIBRATOR_SERVICE);
		mAM = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		mMaxVolume = mAM.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
	}

	public void setOnProgressListener(OnProgressListener listener) {
		this.mOnProgressListener = listener;
	}

	public boolean isProtected(int protectType) {
		if (protectors.get(protectType) != null) {
			return protectors.get(protectType).isProtected();
		} else {
			return false;
		}
	}

	// 检测是否还有防盗防护正在运行
	public boolean hasProtect() {
		boolean flag = false;
		for (int i = 0; i < protectors.size(); i++) {
			if (protectors.valueAt(i).isProtected()) {
				flag = true;
				break;
			}
		}
		return flag;
	}

	public boolean hasAlarmTriggered() {
		boolean flag = false;
		for (int i = 0; i < protectors.size(); i++) {
			if (protectors.valueAt(i).isAlarmTriggered()) {
				flag = true;
				break;
			}
		}
		return flag;
	}

	/**
	 * 取得已开启防护的名称字符串，用逗号连接.
	 * 
	 * @return
	 */
	private String getOpenedProtectsString() {
		if (protectors.size() == 0) {
			return "";
		} else {
			String strTemp = "";
			for (int i = 0; i < protectors.size(); i++) {
				if (protectors.valueAt(i).isProtected()) {
					strTemp += protectors.valueAt(i).getName() + ",";
				}
			}
			return strTemp.substring(0, strTemp.length() - 1);
		}
	}

	// 创建通知
	@SuppressWarnings("deprecation")
	private void setNotification() {
		if (notiMang == null) {
			notiMang = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			noti = new Notification(R.drawable.ic_launcher, "防盗监控服务已开启",
					System.currentTimeMillis());
			// 放置在正在运行栏目中
			noti.flags = Notification.FLAG_ONGOING_EVENT;
			// 指定个性化视图
			contentView = new RemoteViews(getPackageName(),
					R.layout.noti_anti_theft);
			contentView
					.setTextViewText(R.id.notiAntiTheft_tvTitle, "防盗监控服务已开启");
			contentView.setTextViewText(R.id.notiAntiTheft_tvMsg, "已开启防护:"
					+ getOpenedProtectsString());
			noti.contentView = contentView;
			Intent notiIntent = new Intent(this, AntiTheftAty.class);
			PendingIntent contentIntent = PendingIntent.getActivity(
					AntiTheftService.this, 0, notiIntent,
					PendingIntent.FLAG_UPDATE_CURRENT);
			noti.contentIntent = contentIntent;
		} else {
			contentView.setTextViewText(R.id.notiAntiTheft_tvMsg, "已开启防护:"
					+ getOpenedProtectsString());
		}
		notiMang.notify(NOTIFY_ID, noti);
	}

	public class MyBinder extends Binder {
		AntiTheftService getService() {
			return AntiTheftService.this;
		}
	}

	@Override
	public void onDestroy() {
		// 关闭全部防护
		for (int i = 0; i < protectors.size(); i++) {
			if (protectors.valueAt(i).isProtected()) {
				stopProtect(protectors.get(i).getId());
			}
		}
		MyApp myApp = ((MyApp) getApplication());
		myApp.isAntiTheftServiceStarted = false;
		// 清除通知
		clearNotification();
//		releaseWakeLock();
		super.onDestroy();
	}

	/**
	 * 取得整秒数
	 * 
	 * @param ms
	 *            如毫秒数
	 * @return 如1995返回2，4980返回5
	 */
	private int getSenond(long ms) {
		return (int) Math.floor((ms + 300) / 1000);
	}

	// 停止报警
	public void stopAlarm() {
		mVibrator.cancel();
		if (mPlayer != null) {
			mPlayer.stop();
		}
		stopVolumeThread();
		restoreVolume();
		for (int i = 0; i < protectors.size(); i++) {
			protectors.valueAt(i).setAlarmTriggered(false);
		}
		mIsAlarmRunning = false;
	}

	/**
	 * 开始报警.
	 */
	private void startAlarm(final int protectType) {
		final BaseProtector protector = protectors.get(protectType);
		if (protector == null) {
			return;
		}
		if (hasAlarmTriggered()) {
			return;
		}
		protector.setAlarmTriggered(true);
		mIsAlarmRunning = true;
		if (setSP.getAntiTheftDelayTime() != 0) {
			CountDownTimer cdTimer = new CountDownTimer(
					setSP.getAntiTheftDelayTime(), 1000) {

				@Override
				public void onTick(long millisUntilFinished) {
					if (mOnProgressListener != null) {
						L.i("millisUntilFinished:" + millisUntilFinished);
						mOnProgressListener
								.onProgress(
										MSG_DELAY_ON_TICK,
										String.valueOf(getSenond(millisUntilFinished) - 1));
					}
				}

				@Override
				public void onFinish() {
					runAlarm(protectType);
				}
			};
			cdTimer.start();
		} else {
			runAlarm(protectType);
		}
	}

	// 具体执行报警的方法
	private void runAlarm(int protectType) {
		if (mOnProgressListener != null) {
			mOnProgressListener.onProgress(MSG_DELAY_FINISH, null);
		}
		// 再取一次确认，解决报警已被触发但在延迟报警期间防护被停止还会报警的问题.
		BaseProtector p0 = protectors.get(protectType);
		if (p0 == null || (!p0.isProtected()) || (!p0.isAlarmTriggered())) {
			return;
		}
		if (setSP.isAntiTheftVibrate() && (!p0.isVibrateConflict())) {
			mVibrator.vibrate(new long[] { 200, 3000 }, 0);
		}
		if (setSP.isAntiTheftRing()) {
			Uri uri;
			if (setSP.getAntiTheftRingtoneUriString() == null) {
				uri = RingtoneManager
						.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
				// RingtoneManager.getActualDefaultRingtoneUri(getApplicationContext(),
				// RingtoneManager.TYPE_RINGTONE);
			} else {
				uri = Uri.parse(setSP.getAntiTheftRingtoneUriString());
			}
			try {
				mPlayer.reset();
				mPlayer.setDataSource(getApplicationContext(), uri);
				mPlayer.setLooping(true);
				mPlayer.prepare();
				mPlayer.start();
				startVolumeThread();
				if (mOnProgressListener != null) {
					mOnProgressListener.onProgress(MSG_REFESH_VIEWS, null);
				}
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void startVolumeThread(){
		stopVolumeThread();
		mVolumeThread = new VolumeThread();
		mIsVolumeThreadStarted = true;
		mVolumeThread.start();
	}
	
	private void stopVolumeThread() {
		mIsVolumeThreadStarted = false;
	}

	private void setMaxVolume(){
		mCurrVolume = mAM.getStreamVolume(AudioManager.STREAM_MUSIC);
		mAM.setStreamVolume(AudioManager.STREAM_MUSIC, mMaxVolume, AudioManager.FLAG_VIBRATE);
	}
	private void restoreVolume(){
		mAM.setStreamVolume(AudioManager.STREAM_MUSIC, mCurrVolume, AudioManager.FLAG_VIBRATE);
	}
	
	private void showMsg(String str1) {
		Toast.makeText(getApplicationContext(), str1, Toast.LENGTH_LONG).show();
	}

	/**
	 * 是否正在报警
	 * @return
	 */
	public boolean isAlarmRunning() {
		return mIsAlarmRunning;
	}
}
