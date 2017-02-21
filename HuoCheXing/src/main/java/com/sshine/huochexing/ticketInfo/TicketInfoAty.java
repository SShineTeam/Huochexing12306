package com.sshine.huochexing.ticketInfo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.sshine.huochexing.MainActivity;
import com.sshine.huochexing.R;
import com.umeng.analytics.MobclickAgent;
import com.viewpagerindicator.TabPageIndicator;

public class TicketInfoAty extends SherlockFragmentActivity {
	
	private ActionBar actBar;
	private FragmentPagerAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.aty_ticket_info);
		initActionBar();
		initViews();
	}
	
	private void initViews() {
		
		adapter = new TicketInfoFragmentAdapter(getSupportFragmentManager(), this);
		ViewPager pager = (ViewPager)findViewById(R.id.pager);
		pager.setAdapter(adapter);
		TabPageIndicator indicator = (TabPageIndicator)findViewById(R.id.indicator);
		indicator.setViewPager(pager);
	}
	private void initActionBar() {
		actBar = getSupportActionBar();
		actBar.setDisplayShowTitleEnabled(false);
		//自定义不显示logo
		actBar.setDisplayShowHomeEnabled(true);
		actBar.setDisplayHomeAsUpEnabled(true);
		actBar.setDisplayShowTitleEnabled(true);
		actBar.setTitle("火车票资讯");
		actBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_tab_bg));
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case android.R.id.home:
			startActivity(new Intent(this, MainActivity.class));
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
