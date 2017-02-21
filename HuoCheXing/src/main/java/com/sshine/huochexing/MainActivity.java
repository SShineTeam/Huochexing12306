package com.sshine.huochexing;

import java.util.TimeZone;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.baidu.android.pushservice.PushManager;
import com.sshine.huochexing.antiTheft.AntiTheftAty;
import com.sshine.huochexing.chatroom.ChatRoomAty;
import com.sshine.huochexing.more.MoreAty;
import com.sshine.huochexing.ticketInfo.TicketInfoAty;
import com.sshine.huochexing.ticketOnline.A6OrderAty;
import com.sshine.huochexing.ticketOnline.MonitorMangAty;
import com.sshine.huochexing.trainInfos.AddInfoAty;
import com.sshine.huochexing.trainInfos.TrainInfoAty;
import com.sshine.huochexing.trainSchedule.TrainSchAty;
import com.sshine.huochexing.userInfo.UserInfoAty;
import com.sshine.huochexing.utils.A6Util;
import com.sshine.huochexing.utils.L;
import com.sshine.huochexing.utils.MyApp;
import com.sshine.huochexing.utils.SettingSPUtil;
import com.sshine.huochexing.value.SF;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;

import eu.inmite.android.lib.dialogs.ISimpleDialogListener;
import eu.inmite.android.lib.dialogs.SimpleDialogFragment;

public class MainActivity extends SherlockFragmentActivity implements
		OnClickListener, ISimpleDialogListener{
	private ImageButton imgbtn_UserInfo;     //用户信息
	private ImageButton imgbtn_shezhi;       //设置
	private ImageButton btnUpdate;      //更新离线数据
	private ImageButton btnQuit;       //退出
	
	private ImageButton imgbtn_huochexing;   //火车行介绍
	private ImageButton imgbtn_fangdao;      //安全防盗 
	private ImageButton imgbtn_wodecheci;    //我的车次
	private ImageButton imgbtn_tianjia;      //添加车次
	private ImageButton imgbtn_zixun;        //最新资讯
	private ImageButton imgbtn_liaotian;     //车友聊天
	private ImageButton imgbtn_chaxun;       //列车查询
	
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch(msg.what){
			case A6Util.MSG_TOAST:
				showMsg((CharSequence)msg.obj);
				break;
			}
		}
	};
	private static final int REQUEST_SET_TIMEZONE = 0;
	long waitTime = 2000;  
	long touchTime = 0; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.aty_main);
		initActionBar();
		initViews();
	}

	private void initActionBar() { 
		com.actionbarsherlock.app.ActionBar actionBar = getSupportActionBar();
		actionBar.hide();
	}

	private void initViews() {
		imgbtn_huochexing = (ImageButton)findViewById(R.id.help);
		imgbtn_huochexing.setOnClickListener(this);
		imgbtn_UserInfo = (ImageButton) findViewById(R.id.userInfo);
		imgbtn_UserInfo.setOnClickListener(this);
		
		imgbtn_wodecheci = (ImageButton) findViewById(R.id.wodecheci);
		imgbtn_wodecheci.setOnClickListener(this);
		
		imgbtn_tianjia = (ImageButton) findViewById(R.id.add);
		imgbtn_tianjia.setOnClickListener(this);
		
		imgbtn_zixun = (ImageButton) findViewById(R.id.info);
		imgbtn_zixun.setOnClickListener(this);
		
		imgbtn_liaotian = (ImageButton) findViewById(R.id.chat);
		imgbtn_liaotian.setOnClickListener(this);
		
		imgbtn_chaxun = (ImageButton) findViewById(R.id.query);
		imgbtn_chaxun.setOnClickListener(this);
		
		imgbtn_shezhi = (ImageButton) findViewById(R.id.setup);
		imgbtn_shezhi.setOnClickListener(this);
		
		imgbtn_fangdao = (ImageButton) findViewById(R.id.antiTheft);
		imgbtn_fangdao.setOnClickListener(this);
		
		findViewById(R.id.order).setOnClickListener(this);
		btnUpdate = (ImageButton)findViewById(R.id.update);
		btnUpdate.setOnClickListener(this);
		btnQuit = (ImageButton)findViewById(R.id.quit);
		btnQuit.setOnClickListener(this);
		
		// 检测时区
		String strTest = ":" + TimeZone.getDefault().getRawOffset() + "," + TimeZone.getDefault().getRawOffset();
		if (TimeZone.getDefault().getRawOffset() != 28800000) {  
			SimpleDialogFragment
					.createBuilder(getApplicationContext(),
							getSupportFragmentManager()).setCancelable(true)
					.setTitle("时区错误提示" + strTest)
					.setMessage(R.string.setTimezoneStr)
					.setRequestCode(REQUEST_SET_TIMEZONE)
					.setPositiveButtonText("是(推荐)").setNegativeButtonText("否")
					.show();
		}
	}
	
	protected void onResume() {
		//设置头像
		try{
			int headIconId = Integer.parseInt(MyApp.getInstance().getUserInfoSPUtil().getHeadIcon());
			imgbtn_UserInfo.setBackgroundResource(headIconId);
		}catch(Exception e){
			e.printStackTrace();
		}
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.help:
			startActivity(new Intent(this, HuoCheXingAty.class));
			break;
		case R.id.userInfo:
			if(!MyApp.getInstance().getUserInfoSPUtil().isLogin()){
				startActivity(new Intent(MainActivity.this, LoginAty.class));
				return;
			}
			startActivity(new Intent(this, UserInfoAty.class));
			break;
		case R.id.wodecheci:
			startActivity(new Intent(this, TrainInfoAty.class));
			break;
		case R.id.add:
			startActivity(new Intent(this, AddInfoAty.class)); 
			break;
		case R.id.info:
			startActivity(new Intent(this, TicketInfoAty.class));
			break;
		case R.id.chat:
			startActivity(new Intent(this, ChatRoomAty.class));
			break;
		case R.id.query:
			startActivity(new Intent(this, TrainSchAty.class));
			break;
		case R.id.setup:
			startActivity(new Intent(this, MoreAty.class));
			break;
		case R.id.antiTheft:
			startActivity(new Intent(this, AntiTheftAty.class));
			break;
		case R.id.order:
			startActivity(new Intent(MainActivity.this, A6OrderAty.class));
			break;
		case R.id.update:
			checkUpdate();
			break;
		case R.id.quit:
			startActivity(new Intent(this, MonitorMangAty.class));
			break;
		}
	}

	public void checkUpdate() {
		UmengUpdateAgent.setUpdateAutoPopup(false);
		UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
			
			@Override
			 public void onUpdateReturned(int updateStatus,UpdateResponse updateInfo) {
		        switch (updateStatus) {
		        case UpdateStatus.Yes: // has update
		            UmengUpdateAgent.showUpdateDialog(MainActivity.this, updateInfo);
		            break;
		        case UpdateStatus.No: // has no update
		        	showMsg("当前已是最新版本"+SF.TIP);
		            break;
		        case UpdateStatus.Timeout: // time out
		        	showMsg("检测超时"+SF.FAIL);
		            break;
		        }
		        UmengUpdateAgent.setUpdateAutoPopup(true);
				UmengUpdateAgent.setUpdateListener(null);
		    }
		});
		showMsg("检测更新中，请稍候...");
		UmengUpdateAgent.update(this);
	}
	@Override
	public void onBackPressed() {
		long currentTime = System.currentTimeMillis();  
	    if((currentTime-touchTime)>=waitTime) {  
	        Toast.makeText(this, "再按一次退出火车行" + SF.TIP, Toast.LENGTH_SHORT).show();  
	        touchTime = currentTime;  
	    }else {  
	        quit();
	    }  
	}

	public void quit() {
		MobclickAgent.onKillProcess(this);
		MyApp myApp = ((MyApp)getApplication());
		L.i("isAntiTheftServiceStarted:" + myApp.isAntiTheftServiceStarted);
		L.i("isBgdService2Started:" + myApp.isBgdService2Started);
		if (myApp.isAntiTheftServiceStarted || myApp.isBgdService2Started){
			MainActivity.this.finish();
		}else{
			Intent startMain = new Intent(Intent.ACTION_MAIN);
			startMain.addCategory(Intent.CATEGORY_HOME);
			startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
			startActivity(startMain);
			System.exit(0);
		}
		//确认是否在退出后取消接收聊天信息
		SettingSPUtil setSP = MyApp.getInstance().getSettingSPUtil();
		if (!setSP.isChatReceiveMsgAlways()){
			PushManager.stopWork(getApplicationContext());
		}
	}
	@Override
	public void onPositiveButtonClicked(int requestCode) {
		switch(requestCode){
		case REQUEST_SET_TIMEZONE:
			startActivity(new Intent(Settings.ACTION_DATE_SETTINGS));
			break;
		}
	}

	private void showMsg(CharSequence cs1) {
		Toast.makeText(this, cs1, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onNegativeButtonClicked(int requestCode) {
		switch(requestCode){
		case REQUEST_SET_TIMEZONE:
			break;
		}
	}
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
}
