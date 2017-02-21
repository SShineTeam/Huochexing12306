package com.sshine.huochexing.userInfo;

import java.util.Calendar;
import java.util.Locale;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.actionbarsherlock.view.MenuItem;
import com.sshine.huochexing.R;
import com.sshine.huochexing.base.BaseAty;
import com.sshine.huochexing.ticketOnline.A6LoginAty;
import com.sshine.huochexing.ticketOnline.A6OrderAty;
import com.sshine.huochexing.ticketOnline.PassengerMangAty;
import com.sshine.huochexing.ticketOnline.TicketOnlineAty;
import com.sshine.huochexing.utils.A6UserInfoSPUtil;
import com.sshine.huochexing.utils.L;
import com.sshine.huochexing.utils.MyApp;
import com.sshine.huochexing.utils.MyCookieDBManager;
import com.sshine.huochexing.value.SF;
import com.umeng.analytics.MobclickAgent;

public class A6UserInfoAty extends BaseAty implements View.OnClickListener  {
	private A6UserInfoSPUtil a6UserSP = MyApp.getInstance().getA6UserInfoSPUtil();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setDisableLoadingView(true);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.aty_a6_user_info);
		setTitle("12306个人信息");
		
		initViews();
	}

	private void initViews() {
		if (!a6UserSP.isLogin()){
			startActivity(new Intent(this, A6LoginAty.class));
			finish();
			return;
		}
		//不知为何会丢失点击事件
		findViewById(R.id.mang).setOnClickListener(this);
		findViewById(R.id.browser).setOnClickListener(this);
		findViewById(R.id.loginOut).setOnClickListener(this);
		findViewById(R.id.a6Order).setOnClickListener(this);
		findViewById(R.id.clear).setOnClickListener(this);
		setUserTipText();
	}
	private void setUserTipText() {
		Calendar c = Calendar.getInstance(Locale.CHINA);
		c.setTimeInMillis(System.currentTimeMillis());
		int intHH = c.get(Calendar.HOUR_OF_DAY);
		String strTip;
		if (intHH >= 0 && intHH < 4){
			strTip = "晚上好";
			showMsg("夜深了，早点休息哦" + SF.TIP);
		}else if (intHH < 6){
			strTip = "凌晨早";
		}else if (intHH < 8){
			strTip = "早上好";
		}else if (intHH < 12){
			strTip = "上午好";
		}else if (intHH < 20){
			strTip = "下午好";
		}else if (intHH < 24){
			strTip = "晚上好";
		}else{
			strTip = "您好";
		}
		TextView tvUserText = (TextView)findViewById(R.id.a6UserInfo_tvUserText);
		if (tvUserText != null){
			tvUserText.setText(Html.fromHtml(strTip + "，<font color='#ff8c00'><b>" + a6UserSP.getUserName() + "</b></font>"));
			L.i("userName:"+a6UserSP.getUserName());
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.mang:
			startActivity(new Intent(this, PassengerMangAty.class));
			break;
		case R.id.browser:
			Intent intent = new Intent(this, TicketOnlineAty.class);
//			String url = "https://kyfw.12306.cn/otn/modifyUser/initQueryUserInfo";
//			intent.putExtra(TicketOnlineAty.EXTRA_START_PAGE, url);
			startActivity(intent);
			break;
		case R.id.loginOut:
			a6UserSP.setLogin(false);
			a6UserSP.clearPowerOperateTimeMillis();
			MyCookieDBManager.getInstance().clear();
			a6UserSP.saveCookies(null);
			this.finish();
			break;
		case R.id.a6Order:
			startActivity(new Intent(this, A6OrderAty.class));
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

	@Override
	public void doHeaderTask() {
	}

	@Override
	public void doFooterTask() {
	}
}
