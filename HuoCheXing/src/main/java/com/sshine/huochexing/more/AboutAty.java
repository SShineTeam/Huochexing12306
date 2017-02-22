package com.sshine.huochexing.more;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.sshine.huochexing.R;
import com.sshine.huochexing.utils.MyUtils;
import com.sshine.huochexing.value.SF;
import com.umeng.analytics.MobclickAgent;

public class AboutAty extends SherlockActivity implements OnClickListener{
	private Button btnEmail, btnQQ;
	private TextView tvSinaWeibo;
	private LinearLayout llytSinaWeibo, memberLayout;
	private ImageView teamLogo;
	private String strSinaWeibo = "SShine火车行", strQQ = "325634829";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.aty_about);
		initActionBar();
		initViews();
	}
	
	private void initViews() {
		TextView tvInfo1 = (TextView)findViewById(R.id.info);
		memberLayout = (LinearLayout)findViewById(R.id.about_memberLayout);
		teamLogo = (ImageView)findViewById(R.id.about_teamLogo);
		btnEmail = (Button)findViewById(R.id.email);
		btnEmail.setOnClickListener(this);
		btnQQ = (Button)findViewById(R.id.about_btnQQ);
		btnQQ.setOnClickListener(this);
		llytSinaWeibo = (LinearLayout)findViewById(R.id.about_llytSinaWeiBo);
		llytSinaWeibo.setOnClickListener(this);
		tvSinaWeibo = (TextView)findViewById(R.id.about_sinaWeiBo);
		
		String strInfo1 = "应用版本：V"+MyUtils.getVersionName(this);
		tvInfo1.setText(Html.fromHtml(strInfo1));
		String strEmail = "huochexing@126.com";
		btnEmail.setText(Html.fromHtml("<u>联系邮箱: "+strEmail+"</u>"));
		btnEmail.setTag(strEmail);
		btnQQ.setText(Html.fromHtml("<u>" + strQQ + "</u>"));
		tvSinaWeibo.setText(Html.fromHtml("<u>@"+strSinaWeibo+"</u>"));
		teamLogo.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				memberLayout.setVisibility(memberLayout.isShown() ? View.GONE : View.VISIBLE);
				return false;
			}
		});
	}

	private void initActionBar() {
		ActionBar actBar = getSupportActionBar();
		actBar.setDisplayShowTitleEnabled(false);
		actBar.setDisplayShowHomeEnabled(true);
		actBar.setDisplayHomeAsUpEnabled(true);
		actBar.setDisplayShowTitleEnabled(true);
		actBar.setTitle("关于");
		actBar.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.actionbar_tab_bg));
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
		case R.id.email:
			sendEmail();
			break;
		case R.id.about_llytSinaWeiBo:
			copyToClipboard(strSinaWeibo);
			MyUtils.startApp(this, "com.sina.weibo", null);
			break;
		case R.id.about_btnQQ:
			copyToClipboard(strQQ);
			MyUtils.startApp(this, "com.tencent.mobileqq", null);
			break;
		}
	}

	private void sendEmail() {
		try{
			Intent data=new Intent(Intent.ACTION_SENDTO);    
			data.setData(Uri.parse("mailto:"+btnEmail.getTag().toString()));    
			data.putExtra(Intent.EXTRA_SUBJECT, "帮助与反馈");    
			data.putExtra(Intent.EXTRA_TEXT, "(来自火车行客户端"+MyUtils.getVersionCode(this)+")");    
			startActivity(data);   
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("deprecation")
	private void copyToClipboard(CharSequence content){
		ClipboardManager cm = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
		cm.setText(content);
		showMsg("信息已复制到剪贴版"+SF.SUCCESS);
	}
	
	private void showMsg(CharSequence csMsg){
		Toast.makeText(this, csMsg, Toast.LENGTH_SHORT).show();
	}
}
