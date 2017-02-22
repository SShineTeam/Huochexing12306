package com.sshine.huochexing.antiTheft;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.sshine.huochexing.utils.L;
import com.sshine.huochexing.utils.MyApp;
import com.sshine.huochexing.utils.SettingSPUtil;

public class BTProtector extends BaseProtector {
	private BTBroadcastReceiver mReceiver;
	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothDevice mBTDevice;
	private SettingSPUtil setSP = MyApp.getInstance().getSettingSPUtil();
	
	public BTProtector(int id, AntiTheftService context) {
		super(id, context);
		setName("蓝牙行李防盗");
	}

	@Override
	public boolean start(Object obj) {
		String strAddress = obj.toString();
		if (strAddress == null || strAddress == ""){
			onProgress(AntiTheftService.MSG_NO_DEVICE_ADDRESS, null);
			return false;
		}
			try{
				mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
			}catch(Exception e){
				e.printStackTrace();
				onProgress(AntiTheftService.MSG_NO_BLUETOOTH_ADAPTER, null);
				return false;
			}
			try{
				mBTDevice = mBluetoothAdapter.getRemoteDevice(strAddress);
			}catch(Exception e){
				e.printStackTrace();
				onProgress(AntiTheftService.MSG_BIND_FAIL, null);
				return false;
			}
			mReceiver = new BTBroadcastReceiver();
			IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
			filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
			getmServiceContext().registerReceiver(mReceiver, filter);
			if (setSP.isAntiTheftOpenBTEnhancedMode()){
				if (mBluetoothAdapter.isDiscovering()){
					mBluetoothAdapter.cancelDiscovery();
				}
				Thread thread = new Thread(new Runnable(){
					@Override
					public void run() {
						while(isProtected())
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						L.i("mDevice Bonded:" + (mBTDevice.getBondState()==BluetoothDevice.BOND_BONDED ? true:false));
					}
				});
				thread.start();
				L.i("mThread start");
			}
		return true;
	}

	@Override
	public boolean stop() {
		mBluetoothAdapter.cancelDiscovery();
		getmServiceContext().unregisterReceiver(mReceiver);
		return true;
	}
	
	private class BTBroadcastReceiver extends BroadcastReceiver{
		
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)){
				if (isProtected()){
					BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
					if (device.getAddress().equals(mBTDevice.getAddress())){
						request(AntiTheftService.REQUEST_START_ALARM, null);
						if (!setSP.isAntiTheftOpenBTEnhancedMode()){
							request(AntiTheftService.REQUEST_STOP_PROTECT, null);
							request(AntiTheftService.REQUEST_SHOW_MSG, "已绑定蓝牙设备已断开连接，蓝牙防盗服务已自动关闭.");
							onProgress(AntiTheftService.MSG_REFESH_VIEWS, null);
						}
					}
				}
			}else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)){
				int currState = intent.getExtras().getInt(BluetoothAdapter.EXTRA_STATE);
				int previusState = intent.getExtras().getInt(BluetoothAdapter.EXTRA_PREVIOUS_STATE);
				if ((previusState == BluetoothAdapter.STATE_ON) && (currState == BluetoothAdapter.STATE_TURNING_OFF)){
					//蓝牙已被主动关闭
					if (setSP.isAntiTheftBTClosedAlarm()){
						request(AntiTheftService.REQUEST_START_ALARM, null);
					}
					request(AntiTheftService.REQUEST_STOP_PROTECT, null);
					request(AntiTheftService.REQUEST_SHOW_MSG,"蓝牙已被关闭，蓝牙防盗服务已自动关闭!");
					onProgress(AntiTheftService.MSG_REFESH_VIEWS, null);
				}
			}
		}
	}
}
