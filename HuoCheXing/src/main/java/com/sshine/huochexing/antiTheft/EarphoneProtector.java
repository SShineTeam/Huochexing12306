package com.sshine.huochexing.antiTheft;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;

public class EarphoneProtector extends BaseProtector {
	private EarphoneBroadcastReceiver mReceiver;
	public EarphoneProtector(int id, AntiTheftService context) {
		super(id, context);
		setName("耳机被拨出报警");
	}

	@Override
	public boolean start(Object obj) {
		mReceiver = new EarphoneBroadcastReceiver();
		IntentFilter filter = new IntentFilter(
				AudioManager.ACTION_AUDIO_BECOMING_NOISY);
		getmServiceContext().registerReceiver(mReceiver, filter);
		return true;
	}

	@Override
	public boolean stop() {
		getmServiceContext().unregisterReceiver(mReceiver);
		return true;
	}

	private class EarphoneBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(action)) {
				request(AntiTheftService.REQUEST_START_ALARM, null);
			}
		}
	}
}
