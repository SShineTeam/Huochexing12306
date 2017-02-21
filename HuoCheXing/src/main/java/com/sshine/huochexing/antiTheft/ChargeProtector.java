package com.sshine.huochexing.antiTheft;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class ChargeProtector extends BaseProtector {
	private ChargeBroadcastReceiver mReceiver;
	public ChargeProtector(int id, AntiTheftService context) {
		super(id, context);
		setName("充电状态被打断报警");
	}

	@Override
	public boolean start(Object obj) {
		mReceiver = new ChargeBroadcastReceiver();
		IntentFilter filter = new IntentFilter(Intent.ACTION_POWER_DISCONNECTED);
		getmServiceContext().registerReceiver(mReceiver, filter);
		return true;
	}

	@Override
	public boolean stop() {
		getmServiceContext().unregisterReceiver(mReceiver);
		return true;
	}
	private class ChargeBroadcastReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (Intent.ACTION_POWER_DISCONNECTED.equals(action)){
				request(AntiTheftService.REQUEST_START_ALARM, null);
			}
		}
	}
}
