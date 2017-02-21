package com.sshine.huochexing.ticketOnline;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.LayoutParams;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.internal.widget.IcsAdapterView;
import com.actionbarsherlock.internal.widget.IcsAdapterView.OnItemSelectedListener;
import com.actionbarsherlock.internal.widget.IcsLinearLayout;
import com.actionbarsherlock.internal.widget.IcsSpinner;
import com.actionbarsherlock.view.MenuItem;
import com.sshine.huochexing.MainActivity;
import com.sshine.huochexing.R;
import com.umeng.analytics.MobclickAgent;

public class OrderAty extends SherlockActivity implements OnClickListener {
	private String[] mLocations = {"未完成", "未出行", "历史订单", "其它订单"};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.aty_order);
		
		initActionBar();
		initListActionBar();
		initViews();
	}
	private void initActionBar(){ 
		ActionBar actBar = getSupportActionBar();
		actBar.setDisplayShowTitleEnabled(false); 
		actBar.setDisplayShowHomeEnabled(true); 
		actBar.setDisplayHomeAsUpEnabled(true);
		actBar.setDisplayShowTitleEnabled(true);
		actBar.setTitle("其它订单");
		actBar.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.actionbar_tab_bg));
	}
	
	private void initListActionBar() {
		ActionBar actionBar = getSupportActionBar();
		Context context = getSupportActionBar().getThemedContext();
        ArrayAdapter<CharSequence> adpater = new ArrayAdapter<CharSequence>(context, R.layout.sherlock_spinner_dropdown_item, mLocations);
        adpater.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);
        IcsSpinner spinner = new IcsSpinner(this, null,
                R.attr.actionDropDownStyle);
        spinner.setAdapter(adpater);
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(IcsAdapterView<?> parent, View view,
                    int position, long id) {
            	switch(position){
            	case 0:
            	case 1:
            	case 2:
            		Intent intent = new Intent(OrderAty.this, A6OrderAty.class);
            		startActivity(intent);
            		finish();
            		break;
            	case 3:
            		break;
            	}
            }

            @Override
            public void onNothingSelected(IcsAdapterView<?> parent) {
            }
        });
        spinner.setSelection(3);
        IcsLinearLayout listNavLayout = (IcsLinearLayout) getLayoutInflater()
                .inflate(R.layout.abs__action_bar_tab_bar_view, null);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.CENTER;
        listNavLayout.addView(spinner, params);
        actionBar.setCustomView(listNavLayout, new ActionBar.LayoutParams(
                Gravity.RIGHT));
        actionBar.setDisplayShowCustomEnabled(true);
        
        
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			startActivity(new Intent(this, MainActivity.class));
			this.finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void initViews() {
		findViewById(R.id.quNa).setOnClickListener(this);
		findViewById(R.id.xieCheng).setOnClickListener(this);
		findViewById(R.id.tieYou).setOnClickListener(this);
		findViewById(R.id.tongCheng).setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		Intent intentOnline = new Intent(this, TicketOnlineAty.class);
		switch (v.getId()){
			case R.id.quNa:
				intentOnline.putExtra(TicketOnlineAty.EXTRA_START_PAGE, "http://touch.qunar.com/h5/user/usercenter");
				startActivity(intentOnline);
				break;
			case R.id.xieCheng:
				intentOnline.putExtra(TicketOnlineAty.EXTRA_START_PAGE, "http://m.ctrip.com/webapp/myctrip/");
				startActivity(intentOnline);
				break;
			case R.id.tieYou:
				intentOnline.putExtra(TicketOnlineAty.EXTRA_START_PAGE, "http://kefu.m.tieyou.com/");
				startActivity(intentOnline);
				break;
			case R.id.tongCheng:
				intentOnline.putExtra(TicketOnlineAty.EXTRA_START_PAGE, " http://touch.17u.cn/mytouch/index.html");
				startActivity(intentOnline);
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