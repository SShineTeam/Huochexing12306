package com.sshine.huochexing.utils;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class TimeTickReceiver extends BroadcastReceiver {
	int receiveCount = 1;
	@Override
	public void onReceive (Context context, Intent intent) {
		if(intent.getAction().equals(Intent.ACTION_TIME_TICK)) {
            if ((receiveCount%3) == 0){
	            Intent intent1 = new Intent(context, BgdService.class);
	            context.startService(intent1);
	            receiveCount++;
	            if (receiveCount == Integer.MAX_VALUE){
	            	receiveCount = 0;
	            }
            }
        }
	}

}
