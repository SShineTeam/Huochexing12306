package com.sshine.huochexing.userInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.sshine.huochexing.R;
import com.umeng.analytics.MobclickAgent;

public class EditImageAty extends SherlockActivity {
	public static final String RESULT = "iamgeResult";
	public static final String C_RESULT_CODE = "customResultCode";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.aty_editimage);
		initActionBar();
		
		initViews();
	}

	private void initViews() {
		GridView gvIcons = (GridView) findViewById(R.id.icons);
		SimpleAdapter adapter = new SimpleAdapter(this, getIcons(),
				R.layout.item_editimage, new String[] { "item", "resId" },
				new int[] { R.id.item_editimage_ivIcon,
						R.id.item_editimaeg_tvResId });
		gvIcons.setAdapter(adapter);
		gvIcons.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				TextView tv1 = (TextView) arg1
						.findViewById(R.id.item_editimaeg_tvResId);
				int resId = Integer.valueOf(tv1.getText().toString());
				Intent intent = new Intent();
				intent.putExtra(RESULT, resId);
				EditImageAty.this.setResult(EditImageAty.this.getIntent()
						.getIntExtra(C_RESULT_CODE, 0), intent);
				EditImageAty.this.finish();
			}
		});
	}

	private List<Map<String, Object>> getIcons() {
		int[] resIds = { R.drawable.head000, R.drawable.head001,
				R.drawable.head002, R.drawable.head003, R.drawable.head004,
				R.drawable.head005, R.drawable.head006, R.drawable.head007,
				R.drawable.head008, R.drawable.head009, R.drawable.head010,
				R.drawable.head011, R.drawable.head012 };
		List<Map<String, Object>> lst = new ArrayList<Map<String, Object>>();
		Map<String, Object> map;
		for (int resId : resIds) {
			map = new HashMap<String, Object>();
			map.put("item", resId);
			map.put("resId", resId);
			lst.add(map);
		}
		return lst;
	}

	private void initActionBar() {
		ActionBar actBar = getSupportActionBar();
		actBar.setDisplayShowTitleEnabled(false);
		// 自定义不显示logo
		actBar.setDisplayShowHomeEnabled(true);
		actBar.setDisplayHomeAsUpEnabled(true);
		actBar.setDisplayShowTitleEnabled(true);
		actBar.setTitle("选择头像");
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
}
