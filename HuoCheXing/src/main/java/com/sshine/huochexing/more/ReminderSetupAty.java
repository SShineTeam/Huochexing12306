package com.sshine.huochexing.more;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.sshine.huochexing.R;
import com.sshine.huochexing.listener.IFavoriteCharacterDialogListener;
import com.sshine.huochexing.model.CustomDialog;
import com.sshine.huochexing.model.FavoriteCharacterDialogFragment;
import com.sshine.huochexing.trainInfos.TrainInfoAty;
import com.sshine.huochexing.utils.L;
import com.sshine.huochexing.utils.MyApp;
import com.sshine.huochexing.utils.MyUtils;
import com.sshine.huochexing.utils.SettingSPUtil;
import com.sshine.huochexing.value.SF;
import com.umeng.analytics.MobclickAgent;

public class ReminderSetupAty extends SherlockFragmentActivity implements OnClickListener,
	IFavoriteCharacterDialogListener {
	//提醒设置
	private String[] strPreTimes = {"10分钟", "15分钟", "30分钟", "45分钟", "1个小时", "2个小时","3个小时"};
	private long[] lPreTimes = {10*60*1000,15*60*1000,30*60*1000,45*60*1000,1*3600*1000,2*3600*1000,3*3600*1000};
	private Button btnPreReminderTime;
	private ImageView ivVibrate,ivStart,ivEnd;
	private SettingSPUtil setSP = MyApp.getInstance().getSettingSPUtil();
	private ImageView ivRing;
	private TextView tvMIUIRepair;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.aty_reminder_setup);
		initActionBar();
		initViews();
	}

	private void initViews() {
		
		//提醒设置
		ivStart = (ImageView)findViewById(R.id.from);
		ivStart.setOnClickListener(this);
		ivEnd = (ImageView)findViewById(R.id.to);
		ivEnd.setOnClickListener(this);
		btnPreReminderTime = (Button)findViewById(R.id.time);
		btnPreReminderTime.setOnClickListener(this);
		ivRing = (ImageView)findViewById(R.id.ring);
		ivRing.setOnClickListener(this);
		ivVibrate = (ImageView)findViewById(R.id.vibrate);
		ivVibrate.setOnClickListener(this);
		tvMIUIRepair = (TextView)findViewById(R.id.repair);
		tvMIUIRepair.setOnClickListener(this);
		Button btnReminderDemo = (Button)findViewById(R.id.demo);
		btnReminderDemo.setOnClickListener(this);
		
		btnPreReminderTime.setText(setSP.getPreReminderTimeString());
		ivVibrate.setImageResource(setSP.isVibrate()==true ? R.drawable.chat_on:R.drawable.chat_off);
		ivRing.setImageResource(setSP.isRing()==true ? R.drawable.chat_on:R.drawable.chat_off);
		ivStart.setImageResource(setSP.isStartReminder()==true ? R.drawable.chat_on:R.drawable.chat_off);
		ivEnd.setImageResource(setSP.isEndReminder()==true ? R.drawable.chat_on:R.drawable.chat_off);
		
		if (!setSP.isReminderSet()){
			btnPreReminderTime.setEnabled(false);
			ivRing.setEnabled(false);
			ivVibrate.setEnabled(false);
			tvMIUIRepair.setEnabled(false);
		}else{
			btnPreReminderTime.setEnabled(true);
			ivRing.setEnabled(true);
			ivVibrate.setEnabled(true);
			tvMIUIRepair.setEnabled(true);
		}
	}
	
	private void initActionBar() {
		ActionBar actBar = getSupportActionBar();
		actBar.setDisplayShowTitleEnabled(false);
		actBar.setDisplayShowHomeEnabled(true);
		actBar.setDisplayHomeAsUpEnabled(true);
		actBar.setDisplayShowTitleEnabled(true);
		actBar.setTitle("到站提醒设置");
		actBar.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.actionbar_tab_bg));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			startActivity(new Intent(ReminderSetupAty.this, MoreAty.class));
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.time:
			FavoriteCharacterDialogFragment.show(this, "到站提前提醒时间", strPreTimes);
			break;
		case R.id.vibrate:
			boolean bVibrate = setSP.isVibrate()==true?false:true;
			if (bVibrate){
				ivVibrate.setImageResource(R.drawable.chat_on);
			}else{
				ivVibrate.setImageResource(R.drawable.chat_off);
			}
			setSP.setVibrate(bVibrate);
			break;
		case R.id.ring:
			boolean bRing = setSP.isRing()==true?false:true;
			if (bRing){
				ivRing.setImageResource(R.drawable.chat_on);
			}else{
				ivRing.setImageResource(R.drawable.chat_off);
			}
			setSP.setRing(bRing);
			break;
		case R.id.repair:
			L.i("packageName:" + getPackageName());
			MyUtils.showInstalledAppDetails(ReminderSetupAty.this, getPackageName());
			break;
		case R.id.from:
			boolean bStart = setSP.isStartReminder()==true?false:true;
			if (bStart){
				ivStart.setImageResource(R.drawable.chat_on);
			}else{
				ivStart.setImageResource(R.drawable.chat_off);
			}
			setSP.setStartReminder(bStart);
			break;
		case R.id.to:
			boolean bEnd = setSP.isEndReminder()==true?false:true;
			if (bEnd){
				ivEnd.setImageResource(R.drawable.chat_on);
			}else{
				ivEnd.setImageResource(R.drawable.chat_off);
			}
			setSP.setEndReminder(bEnd);
			break;
		case R.id.demo:
			showReminderDemo();
			break;
		}
		refeshNotifyView();
	}
	
	private void showReminderDemo(){
		if (setSP.isVibrate()) {
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vibrator.vibrate(1000);
		}
		if (setSP.isRing()){
			MyUtils.ringNotifycation(ReminderSetupAty.this);
		}
		// 弹出提醒
		String strMsg = "G105次列车即将在30分钟后到达苏州北站" + SF.TIP;
		CustomDialog dlg = new CustomDialog.Builder(this,
				new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dlg, int whitch) {
						switch(whitch){
						case AlertDialog.BUTTON_POSITIVE:
							Intent intent = new Intent(getApplicationContext(), TrainInfoAty.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							startActivity(intent);
							break;
						case AlertDialog.BUTTON_NEGATIVE:
							break;
						}
					}
				}).setTitle("火车行提醒您")
				.setMessage(strMsg)
				.setCheckboxText("不再提醒")
				.setPositiveButton("查看我的车次")
				.setNagativeButton("好的")
				.create();
			dlg.show();
	}
	
	private void refeshNotifyView(){
		if (setSP.isStartReminder() || setSP.isEndReminder()){
			setSP.setReminderSet(true);
		}else{
			setSP.setReminderSet(false);
		}
		if (!setSP.isReminderSet()){
			btnPreReminderTime.setEnabled(false);
			ivRing.setEnabled(false);
			ivVibrate.setEnabled(false);
		}else{
			btnPreReminderTime.setEnabled(true);
			ivRing.setEnabled(true);
			ivVibrate.setEnabled(true);
		}
	}
	
	public void showMsg(String strMsg){
		Toast.makeText(this, strMsg, Toast.LENGTH_SHORT).show();
	}
	@Override
	public void onListItemSelected(int requestCode, String value, int number) {
		btnPreReminderTime.setText(value);
		setSP.setPreReminderTime(lPreTimes[number]);
		setSP.setPreReminderTimeString(value);
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
