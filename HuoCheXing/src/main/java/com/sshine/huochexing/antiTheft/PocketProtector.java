package com.sshine.huochexing.antiTheft;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.CountDownTimer;

import com.sshine.huochexing.utils.L;
import com.sshine.huochexing.value.SF;

public class PocketProtector extends BaseProtector {
	private SensorManager mSensorManger;
	private Sensor mSensor;
	private MySensorListener mSensorListener;
	private CountDownTimer mCDTimer;
	
	public PocketProtector(int id, AntiTheftService context) {
		super(id, context);
		setName("从口袋中被取出报警");
	}

	@Override
	public boolean start(Object obj) {
		try{
			mSensorManger = (SensorManager)getmServiceContext().getSystemService(Context.SENSOR_SERVICE);
			mSensor = mSensorManger.getDefaultSensor(Sensor.TYPE_PROXIMITY);
			mSensorListener = new MySensorListener();
			request(AntiTheftService.REQUEST_SHOW_MSG, getName() + "防护已开启，将在10秒后正式生效" + SF.TIP);
			mCDTimer = new CountDownTimer(10000, 1000) {
				
				@Override
				public void onTick(long millisUntilFinished) {
				}
				
				@Override
				public void onFinish() {
					mSensorManger.registerListener(mSensorListener, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
				}
			};
			setNeedDelay(true);
			mCDTimer.start();
			return true;
		}catch(Exception e){
			request(AntiTheftService.REQUEST_SHOW_MSG, "开启\"" + getName() + "\"防护失败，可能您的设备不支持距离传感器!");
			return false;
		}
	}

	@Override
	public boolean stop() {
		if (mSensorManger != null){
			if (mSensor != null && mSensorListener != null){
				mSensorManger.unregisterListener(mSensorListener, mSensor);
			}
		}
		if (mCDTimer != null){
			mCDTimer.cancel();
		}
		return true;
	}
	
	private class MySensorListener implements SensorEventListener{

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}

		@Override
		public void onSensorChanged(SensorEvent event) {
			if (event.sensor.getType() == Sensor.TYPE_PROXIMITY){
				float currDistance = event.values[0];
				L.i("currDistance:" + currDistance);
				L.i("maximumRange:" + event.sensor.getMaximumRange());
				if (currDistance >= event.sensor.getMaximumRange()){
					request(AntiTheftService.REQUEST_START_ALARM, null);
				}
			}
		}
		
	}
}
