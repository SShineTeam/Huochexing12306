package com.sshine.huochexing.more;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.sshine.huochexing.R;
import com.sshine.huochexing.utils.MyApp;
import com.sshine.huochexing.utils.SettingSPUtil;
import com.umeng.analytics.MobclickAgent;

public class ChatSetupAty extends SherlockActivity
	implements OnClickListener{
	private ImageView ivNotiOnBar,ivVibrate,ivRing, ivReceiveMsgAlways;
	private SettingSPUtil setSP = MyApp.getInstance().getSettingSPUtil();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.aty_chat_setup);
		initActionBar();
		initViews();
	}
	private void initViews() {
		ivRing = (ImageView)findViewById(R.id.ring);
		ivRing.setOnClickListener(this);
		ivVibrate = (ImageView)findViewById(R.id.vibrate);
		ivVibrate.setOnClickListener(this);
		ivNotiOnBar = (ImageView)findViewById(R.id.notiOnBar);
		ivNotiOnBar.setOnClickListener(this);
		ivReceiveMsgAlways = (ImageView)findViewById(R.id.receiveMsgAlways);
		ivReceiveMsgAlways.setOnClickListener(this);
		
		ivVibrate.setImageResource(setSP.isChatVibrate()==true ? R.drawable.chat_on:R.drawable.chat_off);
		ivRing.setImageResource(setSP.isChatRing()==true ? R.drawable.chat_on:R.drawable.chat_off);
		ivNotiOnBar.setImageResource(setSP.isChatNotiOnBar()==true ? R.drawable.chat_on:R.drawable.chat_off);
		ivReceiveMsgAlways.setImageResource(setSP.isChatReceiveMsgAlways()==true ? R.drawable.chat_on:R.drawable.chat_off);
	}
	private void initActionBar() {
		ActionBar actBar = getSupportActionBar();
		actBar.setDisplayShowTitleEnabled(false);
		actBar.setDisplayShowHomeEnabled(true);
		actBar.setDisplayHomeAsUpEnabled(true);
		actBar.setDisplayShowTitleEnabled(true);
		actBar.setTitle("聊天设置");
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
	public void showMsg(String strMsg){
		Toast.makeText(this, strMsg, Toast.LENGTH_SHORT).show();
	}
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.vibrate:
			boolean bVibrate = setSP.isChatVibrate()==true?false:true;
			if (bVibrate){
				ivVibrate.setImageResource(R.drawable.chat_on);
			}else{
				ivVibrate.setImageResource(R.drawable.chat_off);
			}
			setSP.setChatVibrate(bVibrate);
			break;
		case R.id.ring:
			boolean bRing = setSP.isChatRing()==true?false:true;
			if (bRing){
				ivRing.setImageResource(R.drawable.chat_on);
			}else{
				ivRing.setImageResource(R.drawable.chat_off);
			}
			setSP.setChatRing(bRing);
			break;
		case R.id.notiOnBar:
			boolean bNotiOnBar = setSP.isChatNotiOnBar()==true?false:true;
			if (bNotiOnBar){
				ivNotiOnBar.setImageResource(R.drawable.chat_on);
			}else{
				ivNotiOnBar.setImageResource(R.drawable.chat_off);
			}
			setSP.setChatNotiOnBar(bNotiOnBar);
			break;
		case R.id.receiveMsgAlways:
			boolean bReceiveMsgAlways = setSP.isChatReceiveMsgAlways()==true?false:true;
			if (bReceiveMsgAlways){
				ivReceiveMsgAlways.setImageResource(R.drawable.chat_on);
			}else{
				ivReceiveMsgAlways.setImageResource(R.drawable.chat_off);
			}
			setSP.setChatReceiveMsgAlways(bReceiveMsgAlways);
			break;
		}
	}
}
