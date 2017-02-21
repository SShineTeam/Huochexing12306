package com.sshine.huochexing.more;

import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.sshine.huochexing.R;
import com.sshine.huochexing.listener.IFavoriteCharacterDialogListener;
import com.sshine.huochexing.model.FavoriteCharacterDialogFragment;
import com.sshine.huochexing.utils.L;
import com.sshine.huochexing.utils.MyApp;
import com.sshine.huochexing.utils.MyUtils;
import com.sshine.huochexing.utils.SettingSPUtil;
import com.sshine.huochexing.value.SF;
import com.umeng.analytics.MobclickAgent;

public class AntiTheftSetupAty extends SherlockFragmentActivity
	implements OnClickListener, IFavoriteCharacterDialogListener{
	private SettingSPUtil setSP = MyApp.getInstance().getSettingSPUtil();
	private ImageView ivShowAntiTheftStatus;
	private Button btnAlarmRingtone, btnRestSensitivity;
	private ImageView ivRing,ivVibrate;
	private ImageView ivEnhancedMode, ivBTClosed;
	private static final int REQUEST_SELECT_ALARM_RINGTONE = 1;
	
	//0:选strDelayTimes，1:选strRestSensitivitys.
	private int listType = 0;
	private String[] strDelayTimes = {"无延时", "2秒", "3秒", "4秒", "5秒", "6秒", "7秒", "8秒"};
	private long[] lDelayTimes = {0, 2*1000,3*1000,4*1000,5*1000,6*1000,7*1000, 8*1000};
	private String[] strRestSensitivitys = {"低", "较低", "中", "较高", "高"};
	private int[] intRestSensitivitys = {300, 200, 100, 50, 20};
	private Button btnDelay;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.aty_anti_theft_setup);
		initActionBar();
		initViews();
	}
	private void initViews() {
		
		ivShowAntiTheftStatus = (ImageView)findViewById(R.id.antiTheftStatus);
		ivShowAntiTheftStatus.setOnClickListener(this);
		ivRing = (ImageView)findViewById(R.id.ring);
		ivRing.setOnClickListener(this);
		ivVibrate = (ImageView)findViewById(R.id.vibrate);
		ivVibrate.setOnClickListener(this);
		btnDelay = (Button)findViewById(R.id.delay);
		btnDelay.setOnClickListener(this);
		btnAlarmRingtone = (Button)findViewById(R.id.ringtone);
		btnAlarmRingtone.setOnClickListener(this);
		ivEnhancedMode = (ImageView)findViewById(R.id.enhancedMode);
		ivEnhancedMode.setOnClickListener(this);
		ivBTClosed = (ImageView)findViewById(R.id.bt);
		ivBTClosed.setOnClickListener(this);
		btnRestSensitivity = (Button)findViewById(R.id.sensivity);
		btnRestSensitivity.setOnClickListener(this);
		
		MyUtils.setToogleImageStatus(ivShowAntiTheftStatus, setSP.isAntiTheftShowStatus());
		MyUtils.setToogleImageStatus(ivRing, setSP.isAntiTheftRing());
		MyUtils.setToogleImageStatus(ivVibrate, setSP.isAntiTheftVibrate());
		btnDelay.setText(setSP.getAntiTheftDelayTimeString());
		MyUtils.setToogleImageStatus(ivEnhancedMode, setSP.isAntiTheftOpenBTEnhancedMode());
		MyUtils.setToogleImageStatus(ivBTClosed, setSP.isAntiTheftBTClosedAlarm());
		btnRestSensitivity.setText(setSP.getAntiTheftRestSensitivityString());
	}
	
	private void initActionBar() {
		ActionBar actBar = getSupportActionBar();
		actBar.setDisplayShowTitleEnabled(false);
		actBar.setDisplayShowHomeEnabled(true);
		actBar.setDisplayHomeAsUpEnabled(true);
		actBar.setDisplayShowTitleEnabled(true);
		actBar.setTitle("安全防盗设置");
		actBar.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.actionbar_tab_bg));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			startActivity(new Intent(this, MoreAty.class));
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	@SuppressWarnings("deprecation")
	@Override
	public void onClick(View v) {
		boolean status = false;
		switch(v.getId()){
		case R.id.antiTheftStatus:
			status = MyUtils.getToogleImageStatus(ivShowAntiTheftStatus)==true?false:true;
			setSP.setAntiTheftShowStatus(status);
			MyUtils.setToogleImageStatus(ivShowAntiTheftStatus, status);
			break;
		case R.id.ring:
			status = MyUtils.getToogleImageStatus(ivRing)==true?false:true;
			setSP.setAntiTheftRing(status);
			MyUtils.setToogleImageStatus(ivRing, status);
			break;
		case R.id.vibrate:
			status = MyUtils.getToogleImageStatus(ivVibrate)==true?false:true;
			setSP.setAntiTheftVibrate(status);
			MyUtils.setToogleImageStatus(ivVibrate, status);
			break;
		case R.id.delay:
			listType = 0;
			FavoriteCharacterDialogFragment.show(this, "报警延时", strDelayTimes);
			break;
		case R.id.ringtone:
			Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
			intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "设置报警铃音");
			intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_RINGTONE);
			intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false);
			startActivityForResult(intent, REQUEST_SELECT_ALARM_RINGTONE);
			break;
		case R.id.enhancedMode:
			status = MyUtils.getToogleImageStatus(ivEnhancedMode)==true?false:true;
			setSP.setAntiTheftOpenBTEnhancedMode(status);
			MyUtils.setToogleImageStatus(ivEnhancedMode, status);
			break;
		case R.id.bt:
			status = MyUtils.getToogleImageStatus(ivBTClosed)==true?false:true;
			setSP.setAntiTheftBTClosedAlarm(status);
			MyUtils.setToogleImageStatus(ivBTClosed, status);
			break;
		case R.id.sensivity:
			listType = 1;
			FavoriteCharacterDialogFragment.show(this, "静置报警灵敏度", strRestSensitivitys);
			break;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data == null){
			return;
		}
		switch(requestCode){
		case REQUEST_SELECT_ALARM_RINGTONE:
			Uri pickedUri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
			L.i("pickedUri:" + pickedUri);
			if (pickedUri == null){
				showMsg("请选择一个报警铃音" + SF.TIP);
			}else{
				setSP.setAntiTheftRingtoneUriString(pickedUri.toString());
				showMsg("报警铃音设置已保存" + SF.TIP);
			}
			break;
		}
	}
	private void showMsg(String str1) {
		Toast.makeText(this, str1, Toast.LENGTH_LONG).show();
	}
	@Override
	public void onListItemSelected(int requestCode, String value, int number) {
		switch(listType){
		case 0:
			btnDelay.setText(value);
			setSP.setAntiTheftDelayTime(lDelayTimes[number]);
			setSP.setAntiTheftDelayTimeString(value);
			break;
		case 1:
			btnRestSensitivity.setText(value);
			setSP.setAntiTheftRestSensitivity(intRestSensitivitys[number]);
			setSP.setAntiTheftRestSensitivityString(value);
			break;
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
