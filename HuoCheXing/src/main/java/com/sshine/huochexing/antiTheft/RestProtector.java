package com.sshine.huochexing.antiTheft;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.CountDownTimer;

import com.sshine.huochexing.utils.L;
import com.sshine.huochexing.utils.MyApp;
import com.sshine.huochexing.utils.SettingSPUtil;
import com.sshine.huochexing.value.SF;

public class RestProtector extends BaseProtector {
	
	private SensorManager mSensorManger;
	private Sensor mSensor;
	private MySensorListener mSensorListener;
	private SettingSPUtil setSP = MyApp.getInstance().getSettingSPUtil();
	//摇晃速度临界值  
    private static int mSpeedThreshold = 200;
    //两次检测的时间间隔   
    private static final int UPTATE_INTERVAL_TIME = 200;  
   //上次检测时间   
    private long lastUpdateTime;
    //保存上一次记录  
    float lastX = 0;      
    float lastY = 0;      
    float lastZ = 0;
	private CountDownTimer mCDTimer;

	public RestProtector(int id, AntiTheftService context) {
		super(id, context);
		setName("静置状态被打断报警");
		setVibrateConflict(true);
	}

	@Override
	public boolean start(Object obj) {
		mSpeedThreshold = setSP.getAntiTheftRestSensitivity();
		try{
			mSensorManger = (SensorManager)getmServiceContext().getSystemService(Context.SENSOR_SERVICE);
			mSensor = mSensorManger.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
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
			request(AntiTheftService.REQUEST_SHOW_MSG, "开启\"" + getName() + "\"防护失败，可能您的设备不支持近距离传感器!");
			return false;
		}
	}

	@Override
	public boolean stop() {
		L.i("RestProtector stop");
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

		@SuppressWarnings("deprecation")
		@Override
		public void onSensorChanged(SensorEvent event) {
			if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER){
				return;
			}
             //现在检测时间   
              long currentUpdateTime = System.currentTimeMillis();   
              //两次检测的时间间隔   
              long timeInterval = currentUpdateTime - lastUpdateTime;     
              //判断是否达到了检测时间间隔   
              if(timeInterval < UPTATE_INTERVAL_TIME)    
               return;   
              //现在的时间变成last时间   
              lastUpdateTime = currentUpdateTime;   
            //获取加速度数值，以下三个值为重力分量在设备坐标的分量大小  
             float x = event.values[SensorManager.DATA_X];            
             float y = event.values[SensorManager.DATA_Y];            
             float z = event.values[SensorManager.DATA_Z];
              //获得x,y,z的变化值   
              float deltaX = x - lastX;   
              float deltaY = y - lastY;
              float deltaZ = z - lastZ;      
              //备份本次坐标  
              lastX = x;   
              lastY = y;   
              lastZ = z;     
              //计算移动速度  
              double speed = Math.sqrt(deltaX*deltaX + deltaY*deltaY + deltaZ*deltaZ)/timeInterval * 10000;  
              L.i("speed:" + speed);
              L.i("mSpeedThreshold:" + mSpeedThreshold);   
              if(speed >= mSpeedThreshold){
                 request(AntiTheftService.REQUEST_START_ALARM, null);
              }
		}
		
	}
}
