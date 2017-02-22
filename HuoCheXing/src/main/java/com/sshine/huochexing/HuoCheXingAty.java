package com.sshine.huochexing;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.umeng.analytics.MobclickAgent;

public class HuoCheXingAty extends SherlockFragmentActivity implements OnClickListener
{
	private Button btnUserGuid ;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.aty_hcx); 
		initActionBar();
		initViews();
	}

	public void initViews() {
		btnUserGuid = (Button) this.findViewById(R.id.guide);
		btnUserGuid.setOnClickListener(this);
	} 

	private void initActionBar(){ 
		ActionBar actBar = getSupportActionBar();
		actBar.setDisplayShowTitleEnabled(false); 
		actBar.setDisplayShowHomeEnabled(true); 
		actBar.setDisplayHomeAsUpEnabled(true);
		actBar.setDisplayShowTitleEnabled(true);
		actBar.setTitle("火车行简介");
		actBar.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.actionbar_tab_bg));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.guide:
			break;
		default:
			break;
		}
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
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
}
