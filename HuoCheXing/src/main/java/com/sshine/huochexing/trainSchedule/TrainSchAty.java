package com.sshine.huochexing.trainSchedule;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.sshine.huochexing.MainActivity;
import com.sshine.huochexing.R;
import com.sshine.huochexing.bean.QueryLeftNewOptionInfo;
import com.sshine.huochexing.listener.IFavoriteCharacterDialogListener;
import com.sshine.huochexing.listener.IMultiChoiceDialogListener;
import com.umeng.analytics.MobclickAgent;
import com.viewpagerindicator.TabPageIndicator;

public class TrainSchAty extends SherlockFragmentActivity
	implements IFavoriteCharacterDialogListener, IMultiChoiceDialogListener{
	protected QueryLeftNewOptionInfo mPreQLNOInfo;
	private TrainSchFragmentAdapter mAdapter;
	private ActionBar actBar;
	protected static final int REQUEST_QUERY_TYPE = 0;
	protected static final int REQUEST_TICKET_TYPE = 1;
	protected static final int REQUEST_FROM_TIME_RANGE = 2;
	protected static final int REQUEST_TO_TIME_RANGE = 3;
	protected static final int REQUEST_TRAIN_TYPE = 4;
	protected static final int REQUEST_SEAT_TYPE = 5;	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_trainsch);
        initActionBar();
        
        initViews();
	}

	private void initViews() {
		int mode = getIntent().getIntExtra(ZhanZhanQueryFragment.EXTRA_MODE, QueryLeftNewOptionInfo.MODE_NORMAL);
		if (mode == QueryLeftNewOptionInfo.MODE_RESIGN){
			mPreQLNOInfo = new QueryLeftNewOptionInfo();
		}
		
		mAdapter = new TrainSchFragmentAdapter(getSupportFragmentManager(), this);
		ViewPager pager = (ViewPager)findViewById(R.id.pager);
		pager.setAdapter(mAdapter);
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
		actBar.setTitle("车次查询");
		actBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_tab_bg));
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case android.R.id.home:
			startActivity(new Intent(this, MainActivity.class));
			this.finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onListItemSelected(int requestCode, String value, int number) {
		if (mAdapter == null || mAdapter.getCount() < 1){
			return;
		}
		ZhanZhanQueryFragment zzFragment = (ZhanZhanQueryFragment)mAdapter.getItem(0);
		if (zzFragment != null){
			zzFragment.onListItemSelectedFromActivity(requestCode, value, number);
		}
	}

	@Override
	public void onMultiChoiceItemSelected(View v, int requestCode, int whitch,
			boolean isChecked) {
		if (mAdapter == null || mAdapter.getCount() < 1){
			return;
		}
		ZhanZhanQueryFragment zzFragment = (ZhanZhanQueryFragment)mAdapter.getItem(0);
		if (zzFragment != null){
			zzFragment.onMultiChoiceSelectedFromActivity(v, requestCode, whitch, isChecked);
		}
	}

	@Override
	public void onMultiChoicePositiveButtonClicked(int requestCode) {
		if (mAdapter == null || mAdapter.getCount() < 1){
			return;
		}
		ZhanZhanQueryFragment zzFragment = (ZhanZhanQueryFragment)mAdapter.getItem(0);
		if (zzFragment != null){
			zzFragment.onMultiChoicePositiveButtonClickedFromActivity(requestCode);
		}
	}

	@Override
	public void onMultiChoiceNagativeButtonClicked(int requestCode) {
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