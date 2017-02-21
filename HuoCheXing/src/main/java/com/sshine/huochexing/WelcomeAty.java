package com.sshine.huochexing;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.sshine.huochexing.antiTheft.AntiTheftAty;
import com.sshine.huochexing.bean.MonitorInfo;
import com.sshine.huochexing.chatroom.ChatRoomAty;
import com.sshine.huochexing.ticketInfo.TicketInfoAty;
import com.sshine.huochexing.ticketOnline.BgdService2;
import com.sshine.huochexing.ticketOnline.MonitorMangAty;
import com.sshine.huochexing.ticketOnline.OrderAty;
import com.sshine.huochexing.trainInfos.TrainInfoAty;
import com.sshine.huochexing.trainSchedule.TrainSchAty;
import com.sshine.huochexing.utils.A6UserInfoSPUtil;
import com.sshine.huochexing.utils.A6Util;
import com.sshine.huochexing.utils.FileUtil;
import com.sshine.huochexing.utils.HttpUtil;
import com.sshine.huochexing.utils.MyApp;
import com.sshine.huochexing.utils.MyDatabase;
import com.sshine.huochexing.utils.MyUtils;
import com.sshine.huochexing.utils.PersistentUtil;
import com.sshine.huochexing.utils.SettingSPUtil;
import com.sshine.huochexing.utils.TimeTickReceiver;
import com.sshine.huochexing.value.StoreValue;
import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.FeedbackAgent;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UpdateStatus;

public class WelcomeAty extends Activity implements Runnable {
	private LinearLayout llyt1;
	private static final String ACTION_BGD_SERVICE = "com.sshine.huochexing.BgdService";
	private SettingSPUtil setSP = MyApp.getInstance().getSettingSPUtil();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 全屏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.aty_welcome);
		initViews();
//		if (!checkValidity()){
//			android.os.Process.killProcess(android.os.Process.myPid());
//			return;
//		}
		setA6LoginStatus();
		operateUmeng();
		// 定时发送广播激活服务
		openAlarm(this);
		delayRequestCheckBgdServiceStatus(this);
		new Thread(this).start();
		
	}

	private void initViews() {
		llyt1= (LinearLayout)findViewById(R.id.welcome_llyt1);
		MyUtils.setBackgroundResource(this, llyt1, R.drawable.welcome);
	}
	@Override
	protected void onDestroy() {
		MyUtils.releaseBackgroundResource(this, llyt1);
		super.onDestroy();
	}

	public void operateUmeng() {
		UmengUpdateAgent.setUpdateUIStyle(UpdateStatus.STYLE_NOTIFICATION);
		UmengUpdateAgent.setDeltaUpdate(true);
		UmengUpdateAgent.setUpdateOnlyWifi(false);
		UmengUpdateAgent.update(this);
		//检测开发者反馈回复
		FeedbackAgent agent = new FeedbackAgent(this);
		agent.sync();
	}

	@SuppressLint("WorldReadableFiles")
	@Override
	public void run() {
		//确定已复制数据库
		MyDatabase myDB = new MyDatabase(this);
		myDB.closeDB();
		
//		setSP.setTravelFirstShow(true);
		try {
			if (HttpUtil.isNetworkConnected(this)) {
				// 绑定百度云推送
				PushManager
						.startWork(this.getApplicationContext(),
								PushConstants.LOGIN_TYPE_API_KEY,
								MyApp.API_KEY);
			}
			//移动历史离线文件到SD卡
			updateHisOfflineFile();
			
			//初始化抢票监控状态
			String strMFilesPath = MyApp.getInstance().getPathBaseRoot(StoreValue.MONITOR_INFOS_FILE);
			@SuppressWarnings("unchecked")
			List<MonitorInfo> lstMInfos = (List<MonitorInfo>) PersistentUtil.readObject(strMFilesPath);
			if (lstMInfos != null && lstMInfos.size() != 0){
				for (MonitorInfo mInfo : lstMInfos) {
					mInfo.setRunning(false);
					mInfo.setStatus(BgdService2.STATUS_STOPED);
				}
				PersistentUtil.writeObject(lstMInfos, strMFilesPath);
			}
			
			Thread.sleep(1000);
			//检查是否是第一次登录
			if (setSP.isFirstUse()){
				startActivity(new Intent(WelcomeAty.this,
						LoginAty.class));
			}else{
				launtchDefaultAty();
			}
			finish();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void launtchDefaultAty() {
		switch(setSP.getDefaultAtyIndex()){
		case 0:
			startActivity(new Intent(WelcomeAty.this, MainActivity.class));
			break;
		case 1:
			startActivity(new Intent(WelcomeAty.this, TrainSchAty.class));
			break;
		case 2:
			startActivity(new Intent(WelcomeAty.this, TrainInfoAty.class));
			break;
		case 3:
			startActivity(new Intent(WelcomeAty.this, ChatRoomAty.class));
			break;
		case 4:
			startActivity(new Intent(WelcomeAty.this, AntiTheftAty.class));
			break;
		case 5:
			startActivity(new Intent(WelcomeAty.this, MonitorMangAty.class));
			break;
		case 6:
			startActivity(new Intent(WelcomeAty.this, OrderAty.class));
			break;
		case 7:
			startActivity(new Intent(WelcomeAty.this, TicketInfoAty.class));
			break;
		default:
			startActivity(new Intent(WelcomeAty.this, MainActivity.class));
			break;
		}
	}

	private boolean checkValidity() {
		if (0!=(getApplicationInfo().flags&=ApplicationInfo.FLAG_DEBUGGABLE)) {
			return false;
		}
		return true;
	}
	public static String getProp(Context context, String property) {
    	try {
    		ClassLoader cl = context.getClassLoader();
    		@SuppressWarnings("rawtypes")
			Class SystemProperties = cl.loadClass("android.os.SystemProperties");
    		@SuppressWarnings("unchecked")
			Method method = SystemProperties.getMethod("get", String.class);
    		Object[] params = new Object[1];
    		params[0] = new String(property);
    		return (String)method.invoke(SystemProperties, params);
    	} catch (Exception e) {
    		return null;
    	}
    }

	private void openAlarm(Context context) {
		if (!MyApp.getInstance().isAlarmWakerSet){
			AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		    Intent intent1 = new Intent();
		    intent1.setAction(ACTION_BGD_SERVICE);
		    int requestCode = 0;
		    PendingIntent pendIntent = PendingIntent.getBroadcast(context, requestCode, intent1,PendingIntent.FLAG_UPDATE_CURRENT);
		    int triggerAtTime = (int) (SystemClock.elapsedRealtime() + 1* 1000);
		    int interval = 30 * 1000;
		    alarmMgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, interval, pendIntent);
		    MyApp.getInstance().isAlarmWakerSet = true;
		}
	}
	private void delayRequestCheckBgdServiceStatus(Context context) {
		final Context appContext = context.getApplicationContext();
		new Thread(){
			public void run() {
				try {
					sleep(60000);
					//AlarmManager失效的备用手段
			    	if (!MyApp.getInstance().isAlarmWakerEffective){
			    		IntentFilter filter = new IntentFilter(Intent.ACTION_TIME_TICK);
			            TimeTickReceiver receiver = new TimeTickReceiver();
			            appContext.registerReceiver(receiver, filter);
			    	}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			};
		}.start();
	}
	
	private void setA6LoginStatus(){
		new Thread(){
			public void run() {
				A6UserInfoSPUtil a6UserSP = MyApp.getInstance().getA6UserInfoSPUtil();
				a6UserSP.setLogin(A6Util.checkUser(MyApp.getInstance().getCommonBInfo()));
			};
		}.start();
	}
	//移动历史离线数据到SD卡
	private void updateHisOfflineFile() {
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
		if (sdCardExist) {
			File deviceRootFile = new File(getApplicationContext()
					.getFilesDir().getParentFile().getPath());
			//将历史车次详情文件移动到SD卡目录下
			File[] files = deviceRootFile.listFiles(new myFileFilter());
			if (files != null){
				try {
					for (File file : files) {
						file.renameTo(new File(FileUtil.getSDTrainDetailDir()
								+ File.separator + file.getName()));
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private class myFileFilter implements FileFilter {

		@Override
		public boolean accept(File pathname) {
			if (pathname.isFile()) {
				String decodeName = Uri.decode(pathname.getName());
				if (decodeName.startsWith("trainDetail_")) {
					return true;
				} else {
					return false;
				}
			}
			{
				return false;
			}
		}
	}
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
}
