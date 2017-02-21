package com.sshine.huochexing.trainInfos;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

import com.actionbarsherlock.view.MenuItem;
import com.sshine.huochexing.R;
import com.sshine.huochexing.base.BaseAty;
import com.sshine.huochexing.value.SF;

public class StationDetailAty extends BaseAty {
	public static final String EXTRA_STATION_NAME = "extraStationName";
	private static final int MSG_GET_IMAGES_SUCCESS = 1;
	private String mStationName;
	private ViewPager viewPager; // android-support-v4中的滑动组件
	private List<ImageView> mPics; // 滑动的图片集合

	private String[] titles; // 图片标题
	private Map<String, Bitmap> mMapBitmaps;
	private List<View> mDots; // 图片标题正文的那些点

	private TextView tvTitle;
	private int currentItem = 0; // 当前图片的索引号
	private ScheduledExecutorService scheduledExecutorService;
	// 切换当前显示的图片
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch(msg.what){
			case MSG_GET_IMAGES_SUCCESS:
				loadViews();
				break;
			default:
				viewPager.setCurrentItem(currentItem);// 切换当前显示的图片
				break;
			}
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setDisableLoadingView(true);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.aty_add_info);
		
		initViews();
	}
	public void loadViews() {
		titles = new String[mMapBitmaps.size()];
		mPics = new ArrayList<ImageView>();
		mDots = new ArrayList<View>();
		int i=0;
		for(String key:mMapBitmaps.keySet()){
			titles[i++] = key;
			ImageView iv1 = new ImageView(StationDetailAty.this);
			iv1.setImageBitmap(mMapBitmaps.get(key));
			iv1.setScaleType(ScaleType.CENTER_CROP);
			iv1.setOnClickListener(new MyOnClickListener(i));
			mPics.add(iv1);
			
			ImageView iv2 = new ImageView(StationDetailAty.this);
			iv2.setBackgroundResource(R.drawable.dot_normal);
			mDots.add(iv2);
		}
		tvTitle.setText(titles[0]);
	}
	private void initViews() {
		mStationName = getIntent().getStringExtra(EXTRA_STATION_NAME);
		if (mStationName == null){
			showMsg("调用出错"+SF.FAIL);
			return;
		}
		setTitle(mStationName + "站详情");
		tvTitle = (TextView)findViewById(R.id.msg);
		
		tvTitle.setText("加载中...");
		viewPager = (ViewPager) findViewById(R.id.vp1);
		viewPager.setAdapter(new MyAdapter());// 设置填充ViewPager页面的适配器
		// 设置一个监听器，当ViewPager中的页面改变时调用
		viewPager.setOnPageChangeListener(new MyPageChangeListener());
		new Thread(){
			public void run() {
				mMapBitmaps = getShowBitmaps();
				if (mMapBitmaps != null && mMapBitmaps.size() != 0){
					sendToast("获取车站图片失败"+SF.FAIL);
				}else{
					Message msg = mHandler.obtainMessage(MSG_GET_IMAGES_SUCCESS);
					mHandler.sendMessage(msg);
				}
			};
		}.start();
	}
	private Map<String, Bitmap> getShowBitmaps() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	/**
	 * 当ViewPager中页面的状态发生改变时调用
	 * 
	 * @author Administrator
	 * 
	 */
	private class MyPageChangeListener implements OnPageChangeListener {
		private int oldPosition = 0;

		/**
		 * This method will be invoked when a new page becomes selected.
		 * position: Position index of the new selected page.
		 */
		public void onPageSelected(int position) {
			currentItem = position;
			tvTitle.setText(titles[position]);
			mDots.get(oldPosition).setBackgroundResource(R.drawable.dot_normal);
			mDots.get(position).setBackgroundResource(R.drawable.dot_focused);
			oldPosition = position;
		}

		public void onPageScrollStateChanged(int arg0) {

		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}
	}

	/**
	 * 填充ViewPager页面的适配器
	 * 
	 * @author Administrator
	 * 
	 */
	private class MyAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return mPics.size();
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			((ViewPager) arg0).addView(mPics.get(arg1));
			return mPics.get(arg1);
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView((View) arg2);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {

		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {

		}

		@Override
		public void finishUpdate(View arg0) {

		}
	}
	private class MyOnClickListener implements OnClickListener {

		private int currItem = -1;
		public MyOnClickListener(int currItem){
			this.currItem = currItem;
		}
		public void onClick(View v) {
			Log.i("title:", titles[currItem]);
		}
	}
	@Override
	public void doHeaderTask() {
	}
	@Override
	public void doFooterTask() {
	}
}
