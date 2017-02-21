package com.sshine.huochexing.antiTheft;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;
import com.sshine.huochexing.MainActivity;
import com.sshine.huochexing.R;
import com.sshine.huochexing.listener.OnProgressListener;
import com.sshine.huochexing.more.AntiTheftSetupAty;
import com.sshine.huochexing.more.LockPatternAty;
import com.sshine.huochexing.utils.MyUtils;
import com.sshine.huochexing.value.SF;
import com.umeng.analytics.MobclickAgent;

import eu.inmite.android.lib.dialogs.ISimpleDialogListener;
import eu.inmite.android.lib.dialogs.SimpleDialogFragment;

public class AntiTheftAty extends SherlockFragmentActivity implements
		OnClickListener, ISimpleDialogListener {
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_BIND_OBSERVED_DEVICE = 2;
    private static final int REQUEST_CLOSE_BT = 3;
    private static final int REQUEST_CHECK_LOCK_PATTERN = 4;
	private BluetoothAdapter mBluetoothAdapter;
	protected AntiTheftService mAntiTheftService;
	private TextView tvMsg;
	private ImageView ivBT, ivEarphone, ivCharge, ivPocket, ivRest;
	private TextView tvBT, tvEarphone, tvCharge, tvPocket, tvRest;
	private List<LinearLayout> lstLayout = new ArrayList<LinearLayout>();
	private LinearLayout llytBT;
	private static final String DEVICE_NAME = "deviceName";
	private static final String DEVICE_ADDRESS = "deviceAddress";
	private String strDeviceAddress;
	List<Map<String,String>> lstDevices = new ArrayList<Map<String,String>>();
	private ListView lvBTDevices;
	private SimpleAdapter adapter;
	private TextView tvBTDevicesTitle;
	private Intent mConnIntent;
	private Button btnStopAlarm;
	
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch(msg.what){
			case AntiTheftService.MSG_NO_DEVICE_ADDRESS:
				showMsg("蓝牙设备地址不正确");
				break;
			case AntiTheftService.MSG_NO_BLUETOOTH_ADAPTER:
				showMsg("蓝牙设备不可用");
				break;
			case AntiTheftService.MSG_BIND_FAIL:
				showMsg("绑定蓝牙设备失败");
				break;
			case AntiTheftService.MSG_MSG:
				tvMsg.setText(msg.obj.toString());
				break;
			case AntiTheftService.MSG_REFESH_VIEWS:
				break;
			case AntiTheftService.MSG_DELAY_ON_TICK:
				btnStopAlarm.setText("关闭警报(" + msg.obj.toString() + ")");
				break;
			case AntiTheftService.MSG_DELAY_FINISH:
				btnStopAlarm.setText("关闭警报");
				break;
			case AntiTheftService.MSG_START_SUCCESS:
				tvMsg.setText(msg.obj.toString());
				break;
			case AntiTheftService.MSG_START_FAIL:
				tvMsg.setText(msg.obj.toString());
				break;
			case AntiTheftService.MSG_STOP_SUCCESS:
				tvMsg.setText(msg.obj.toString());
				break;
			}
			refreshViews();
		};
	};
	
	private void refreshViews(){
		MyUtils.setToogleImageStatus(ivBT, mAntiTheftService.isProtected(AntiTheftService.PROTECT_BT));
		MyUtils.setToogleImageStatus(ivEarphone, mAntiTheftService.isProtected(AntiTheftService.PROTECT_EARPHONE));
		MyUtils.setToogleImageStatus(ivCharge, mAntiTheftService.isProtected(AntiTheftService.PROTECT_CHARGE));
		MyUtils.setToogleImageStatus(ivPocket, mAntiTheftService.isProtected(AntiTheftService.PROTECT_POCKET));
		MyUtils.setToogleImageStatus(ivRest, mAntiTheftService.isProtected(AntiTheftService.PROTECT_REST));
		
		btnStopAlarm.setEnabled(mAntiTheftService.isAlarmRunning());
		btnStopAlarm.setVisibility(mAntiTheftService.isAlarmRunning()?View.VISIBLE:View.GONE);
	}
	
	private ServiceConnection mServiceConnection = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			mAntiTheftService = null;
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mAntiTheftService = ((AntiTheftService.MyBinder)service).getService();
			mAntiTheftService.setOnProgressListener(new OnProgressListener(){
				@Override
				public void onProgress(int type, Object obj) {
					Message message = mHandler.obtainMessage(type, obj);
					mHandler.sendMessage(message);
				}
			});
			refreshViews();
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.aty_anti_theft);
		initActionBar();
		
		initViews();
	}
	
	private void initViews() {
		ivBT = (ImageView) findViewById(R.id.bt);
		ivBT.setOnClickListener(this);
		ivEarphone = (ImageView)findViewById(R.id.earphone);
		ivEarphone.setOnClickListener(this);
		ivCharge = (ImageView)findViewById(R.id.charge);
		ivCharge.setOnClickListener(this);
		ivPocket = (ImageView)findViewById(R.id.pocket);
		ivPocket.setOnClickListener(this);
		ivRest = (ImageView)findViewById(R.id.rest);
		ivRest.setOnClickListener(this);
		
		tvBT = (TextView)findViewById(R.id.bt1);
		tvBT.setOnClickListener(this);
		tvEarphone = (TextView)findViewById(R.id.earphone1);
		tvEarphone.setOnClickListener(this);
		tvCharge = (TextView)findViewById(R.id.charge1);
		tvCharge.setOnClickListener(this);
		tvPocket = (TextView)findViewById(R.id.pocket1);
		tvPocket.setOnClickListener(this);
		tvRest = (TextView)findViewById(R.id.rest1);
		tvRest.setOnClickListener(this);
		
		tvMsg = (TextView)findViewById(R.id.msg);
		btnStopAlarm = (Button)findViewById(R.id.stopAlarm);
		btnStopAlarm.setOnClickListener(this);
		tvBTDevicesTitle = (TextView)findViewById(R.id.antiTheft_tvBTDevicesTitle);
		lvBTDevices = (ListView)findViewById(R.id.antiTheft_lvBTDevices);
		llytBT = (LinearLayout)findViewById(R.id.antiTheft_llytBT);
		lstLayout.add(llytBT);
		
		//隐藏所有扩展选项
		toogleLayout(null);
		adapter = new SimpleAdapter(this, lstDevices, R.layout.item_anti_theft_connected_bluetooth_device,
				new String[]{DEVICE_NAME, DEVICE_ADDRESS},
				new int[]{R.id.item_anti_theft_tvDeviceName, R.id.item_anti_theft_tvDeviceAddress});
		lvBTDevices.setAdapter(adapter);
		MyUtils.setListViewHeightBasedOnChildren(lvBTDevices);
		lvBTDevices.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> av, View v,
					int arg2, long arg3) {
				mBluetoothAdapter.cancelDiscovery();
				TextView tvName = (TextView)v.findViewById(R.id.item_anti_theft_tvDeviceName);
				TextView tvAddress = (TextView)v.findViewById(R.id.item_anti_theft_tvDeviceAddress);
				//保存要绑定的设备地址
				lvBTDevices.setTag(tvAddress.getText().toString());
				SimpleDialogFragment
				.createBuilder(
						AntiTheftAty.this,
						getSupportFragmentManager())
				.setCancelable(false)
				.setRequestCode(REQUEST_BIND_OBSERVED_DEVICE)
				.setTitle("绑定提示")
				.setMessage("确定要绑定设备\"" + tvName.getText() + "\"以开启防盗防护吗？")
				.setPositiveButtonText("是")
				.setNegativeButtonText("否")
				.show();
			}
		});
		//启动服务
		mConnIntent = new Intent(AntiTheftAty.this, AntiTheftService.class);
		startService(mConnIntent);
		bindService(mConnIntent, mServiceConnection, BIND_AUTO_CREATE);
	}
	
	private void toogleLayout(LinearLayout llytShow){
		for(LinearLayout llyt1:lstLayout){
			llyt1.setVisibility(View.GONE);
		}
		if (llytShow != null){
			llytShow.setVisibility(View.VISIBLE);
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		//绑定设备列表
		if ((llytBT.getVisibility() == View.VISIBLE) && mBluetoothAdapter != null){
			//蓝牙断开报警模式
			Set<BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();
			if (devices != null){
				lstDevices.clear();
				for(BluetoothDevice device:devices){
					Map<String,String> map = new HashMap<String, String>();
					map.put(DEVICE_NAME, device.getName());
					map.put(DEVICE_ADDRESS, device.getAddress());
					lstDevices.add(map);
				}
				adapter.notifyDataSetChanged();
				MyUtils.setListViewHeightBasedOnChildren(lvBTDevices);
			}
		}
		MobclickAgent.onResume(this);
	}
	
	private void initActionBar() {
		ActionBar actBar = getSupportActionBar();
		actBar.setDisplayShowTitleEnabled(false);
		actBar.setDisplayShowHomeEnabled(true);
		actBar.setDisplayHomeAsUpEnabled(true);
		actBar.setDisplayShowTitleEnabled(true);
		actBar.setTitle("安全防盗");
		actBar.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.actionbar_tab_bg));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			startActivity(new Intent(this, MainActivity.class));
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	@Override
	public void onClick(View v) {
		String strDescription = "";
		Intent intentCheck = new Intent(AntiTheftAty.this, LockPatternAty.class);
		intentCheck.putExtra(LockPatternAty.EXTRA_OPERATE, LockPatternAty.EXTRA__OPERATE_CHECK);
		switch(v.getId()){
		case R.id.bt:
			if (!MyUtils.getToogleImageStatus(ivBT)){
				startBTAntiTheft();
			}else{
				intentCheck.putExtra(LockPatternAty.EXTRA_REQUEST_OPERATE_RESOURCE_ID, R.id.bt);
				startActivityForResult(intentCheck, REQUEST_CHECK_LOCK_PATTERN);
			}
			break;
		case R.id.earphone:
			if (!MyUtils.getToogleImageStatus(ivEarphone)){
				mAntiTheftService.startProtect(AntiTheftService.PROTECT_EARPHONE, null);
				MyUtils.setToogleImageStatus(ivEarphone, true);
			}else{
				intentCheck.putExtra(LockPatternAty.EXTRA_REQUEST_OPERATE_RESOURCE_ID, R.id.earphone);
				startActivityForResult(intentCheck, REQUEST_CHECK_LOCK_PATTERN);
			}
			break;
		case R.id.charge:
			if (!MyUtils.getToogleImageStatus(ivCharge)){
				mAntiTheftService.startProtect(AntiTheftService.PROTECT_CHARGE, null);
				MyUtils.setToogleImageStatus(ivCharge, true);
			}else{
				intentCheck.putExtra(LockPatternAty.EXTRA_REQUEST_OPERATE_RESOURCE_ID, R.id.charge);
				startActivityForResult(intentCheck, REQUEST_CHECK_LOCK_PATTERN);
			}
			break;
		case R.id.pocket:
			if (!MyUtils.getToogleImageStatus(ivPocket)){
				mAntiTheftService.startProtect(AntiTheftService.PROTECT_POCKET, null);
				MyUtils.setToogleImageStatus(ivPocket, true);
			}else{
				intentCheck.putExtra(LockPatternAty.EXTRA_REQUEST_OPERATE_RESOURCE_ID, R.id.pocket);
				startActivityForResult(intentCheck, REQUEST_CHECK_LOCK_PATTERN);
			}
			break;
		case R.id.rest:
			if (!MyUtils.getToogleImageStatus(ivRest)) {
				mAntiTheftService.startProtect(AntiTheftService.PROTECT_REST, null);
				MyUtils.setToogleImageStatus(ivRest, true);
			} else {
				intentCheck.putExtra(LockPatternAty.EXTRA_REQUEST_OPERATE_RESOURCE_ID, R.id.rest);
				startActivityForResult(intentCheck, REQUEST_CHECK_LOCK_PATTERN);
			}
			break;
		case R.id.bt1:
			strDescription = "在蓝牙行李防盗功能开启后您可以将绑定的蓝牙耳机(或其它可配对并连接的蓝牙设备)放入"
				+ "行李包中，当恶意人员将行李带出一定距离后，本应用会自动报警"
				+ "，保护您行李物品的安全.";
			alertProtectDescription(strDescription);
			break;
		case R.id.earphone1:
			strDescription = "耳机被拨出报警功能适用于您边听歌边休息的场合，您开启"
				+ "此功能后如果有恶意人员拨出您的耳机想偷走手机等设备时，本应用即会自动"
				+ "报警.";
			alertProtectDescription(strDescription);
			break;
		case R.id.charge1:
			strDescription = "充电状态被打断报警功能开启后，如果有人拔掉充电线想"
				+ "偷走您的手机等设备时，本应用会自动报警.";
			alertProtectDescription(strDescription);
			break;
		case R.id.pocket1:
			strDescription = "从口袋中被取出报警功能开启后，如果有人从您的口袋中"
					+ "取走您的手机等设备时，本应用会自动报警.";
			alertProtectDescription(strDescription);
			break;
		case R.id.rest1:
			strDescription = "您可以先将您的手机等设备放在一个固定不动的地方，如"
					+ "桌子上或行李箱中，再将静置状态被打断报警功能开启，此后如果"
					+ "有人移动您的手机等设备，本应用会自动报警.";
			alertProtectDescription(strDescription);
			break;
		case R.id.stopAlarm:
			intentCheck.putExtra(LockPatternAty.EXTRA_REQUEST_OPERATE_RESOURCE_ID, R.id.stopAlarm);
			startActivityForResult(intentCheck, REQUEST_CHECK_LOCK_PATTERN);
		}
	}
	
	//显示报警防护的说明
	private void alertProtectDescription(String strDescription){
		SimpleDialogFragment.createBuilder(this, getSupportFragmentManager())
			.setTitle("说明")
			.setMessage(strDescription)
			.setPositiveButtonText("关闭")
			.setCancelable(true)
			.show();
	}
	
	//关闭蓝牙防盗监控
	private void stopBTAntiTheft() {
		MyUtils.setToogleImageStatus(ivBT, false);
		mAntiTheftService.stopProtect(AntiTheftService.PROTECT_BT);
		confirmDisableBT();
	}

	private void confirmDisableBT() {
		try{
			mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		}catch(Exception e){
			e.printStackTrace();
		}
		if (mBluetoothAdapter.isEnabled()){
			try{
				SimpleDialogFragment.createBuilder(AntiTheftAty.this, getSupportFragmentManager())
				.setTitle("提示")
				.setMessage("是否关闭蓝牙?")
				.setCancelable(false)
				.setRequestCode(REQUEST_CLOSE_BT)
				.setPositiveButtonText("关闭")
				.setNegativeButtonText("取消")
				.show();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}

	//开启蓝牙防盗监控
	private void startBTAntiTheft() {
		//取得适配器
		if (mBluetoothAdapter == null){
			mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		}
		if (mBluetoothAdapter == null){
			showMsg("蓝牙不可用");
			tvMsg.setText("蓝牙不可用");
		}else{
			if (!mBluetoothAdapter.isEnabled()) {
				showMsg("正在请求打开蓝牙");
	            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
	            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
	        }else{
	        	handleBondBTDevice();
	        }
		}
	}
	
	private void handleBondBTDevice(){
		MyUtils.setToogleImageStatus(ivBT, true);
    	strDeviceAddress = "";
    	String strBondTip;
    	//默认显示蓝牙防盗扩展选项
		toogleLayout(llytBT);
		strBondTip = "请在此确认已连接您的蓝牙设备，之后再返回火车行界面" + SF.TIP;
		tvBTDevicesTitle.setText("请在此选择您要绑定的已连接设备:");
		tvMsg.setText("正在等待您确认要绑定的蓝牙设备...");
		Toast.makeText(getApplicationContext(), strBondTip, Toast.LENGTH_LONG).show();
    	startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
        case REQUEST_ENABLE_BT:
            if (resultCode == Activity.RESULT_OK) {
                showMsg("蓝牙已开启");
                handleBondBTDevice();
            } else {
            	showMsg("蓝牙不可用");
            	MyUtils.setToogleImageStatus(ivBT, false);
            }
            break;
        case REQUEST_CHECK_LOCK_PATTERN:
        	if (resultCode == Activity.RESULT_OK && (data != null)) {
        		int sourceId = data.getIntExtra(LockPatternAty.EXTRA_REQUEST_OPERATE_RESOURCE_ID, -1);
        		if (sourceId != -1){
        			switch (sourceId) {
					case R.id.bt:
						stopBTAntiTheft();
						break;
					case R.id.earphone:
						mAntiTheftService.stopProtect(AntiTheftService.PROTECT_EARPHONE);
						MyUtils.setToogleImageStatus(ivEarphone, false);
						break;
					case R.id.charge:
						mAntiTheftService.stopProtect(AntiTheftService.PROTECT_CHARGE);
						MyUtils.setToogleImageStatus(ivCharge, false);
						break;
					case R.id.pocket:
						mAntiTheftService.stopProtect(AntiTheftService.PROTECT_POCKET);
						MyUtils.setToogleImageStatus(ivPocket, false);
						break;
					case R.id.rest:
						mAntiTheftService.stopProtect(AntiTheftService.PROTECT_REST);
						MyUtils.setToogleImageStatus(ivRest, false);
						break;
					case R.id.stopAlarm:
						mAntiTheftService.stopAlarm();
						btnStopAlarm.setEnabled(false);
					}
        		}else{
        			showMsg("出错了" + SF.FAIL);
        		}
        	}
			break;
        }
    }
	
	private void showMsg(String str1){
		Toast.makeText(this, str1, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onPositiveButtonClicked(int requestCode) {
		switch(requestCode){
		case REQUEST_BIND_OBSERVED_DEVICE:
        	strDeviceAddress = lvBTDevices.getTag().toString();
        	tvMsg.setText("蓝牙设备已绑定，正在启动防盗服务...");
        	if (mAntiTheftService != null){
        		mAntiTheftService.startProtect(AntiTheftService.PROTECT_BT, strDeviceAddress);
        		llytBT.setVisibility(View.GONE);
        	}else{
        		tvMsg.setText("防盗服务启动失败!");
        	}
        	break;
		case REQUEST_CLOSE_BT:
			mBluetoothAdapter.disable();
		}
	}

	@Override
	public void onNegativeButtonClicked(int requestCode) {
	}
	
	@Override
	protected void onDestroy() {
		if (mAntiTheftService != null){
			try{
				unbindService(mServiceConnection);
				if ((!mAntiTheftService.hasProtect()) && (!mAntiTheftService.isAlarmRunning())){
					stopService(mConnIntent);
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		super.onDestroy();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem miDel = menu.add("提示信息");
    	miDel.setIcon(R.drawable.info);
    	miDel.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    	miDel.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				SimpleDialogFragment.createBuilder(AntiTheftAty.this, getSupportFragmentManager())
					.setTitle("提示")
					.setMessage("为了为您提供更准确更有效的防护，我们建议您将火车行添加到"
							+ "一键清理(进程清理)的白名单列表中，以免防盗服务被恶意人员或"
							+ "防护软件强制关闭哦" + SF.TIP)
					.setPositiveButtonText("关闭")
					.show();
				return true;
			} 
		});
    	
    	MenuItem miSetup = menu.add("设置");
    	miSetup.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
    	miSetup.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				Intent intent = new Intent(AntiTheftAty.this, AntiTheftSetupAty.class);
				AntiTheftAty.this.startActivity(intent);
				return true;
			}
		});
		return super.onCreateOptionsMenu(menu);
	}
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
}