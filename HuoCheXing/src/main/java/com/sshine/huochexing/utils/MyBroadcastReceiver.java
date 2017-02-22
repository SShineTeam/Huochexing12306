package com.sshine.huochexing.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.SystemClock;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;


public class MyBroadcastReceiver extends BroadcastReceiver {

	private static final String ACTION_BGD_SERVICE = "com.sshine.huochexing.BgdService";
	private static final String CONNECTIVITY_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
			//开机启动激活服务
			openAlarm(context);
			delayRequestCheckBgdServiceStatus(context);
		}else if (intent.getAction().equals(ACTION_BGD_SERVICE)){
            Intent myIntent = new Intent(context, BgdService.class);
            context.startService(myIntent);
		}else if (intent.getAction().equals(CONNECTIVITY_ACTION)){
			if (HttpUtil.isNetworkConnected(context)){
				//绑定百度云推送
				if(!PushManager.isPushEnabled(context)){
					PushManager.startWork(context,
							PushConstants.LOGIN_TYPE_API_KEY,
							MyApp.API_KEY);
				}
			}
		}
	}
	private void openAlarm(Context context) {
		if (!MyApp.getInstance().isAlarmWakerSet){
			L.i("开启广播2");
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
}
