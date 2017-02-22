package com.sshine.huochexing.ticketOnline;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.sshine.huochexing.MainActivity;
import com.sshine.huochexing.R;
import com.sshine.huochexing.adapter.MonitorMangAdapter;
import com.sshine.huochexing.bean.MonitorInfo;
import com.sshine.huochexing.listener.OnProgressListener;
import com.sshine.huochexing.utils.L;
import com.sshine.huochexing.utils.MyApp;
import com.sshine.huochexing.utils.PersistentUtil;
import com.sshine.huochexing.value.StoreValue;
import com.umeng.analytics.MobclickAgent;

public class MonitorMangAty extends SherlockFragmentActivity{
	private static final int REQUEST_EDIT = 1;
	private BgdService2 mBgdSerivce2;
	private List<MonitorInfo> mLstMInfos;
	
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch(msg.what){
			case BgdService2.MSG_REFRESH_CURR_MONIOTR_INFO_STATUS:
				refreshStatusFromService((MonitorInfo)msg.obj);
				break;
			case BgdService2.MSG_REFRESH_ALL_STATUS:
				refreshData(false);
				break;
			}
		};
	};
	private ServiceConnection mServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mBgdSerivce2 = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mBgdSerivce2 = ((BgdService2.MyBinder) service).getService();
			mBgdSerivce2.setOnProgressListener(new OnProgressListener() {
				@Override
				public void onProgress(int type, Object obj) {
					Message message = mHandler.obtainMessage(type, obj);
					mHandler.sendMessage(message);
				}
			});
			if (mBgdSerivce2 != null){
				mBgdSerivce2.notifyLstMInfosChanged(mLstMInfos);
			}
		}
	};
	private Intent mConnIntent;
	private MonitorMangAdapter mAdpater;
	private ListView lvMang;
	protected int intCurrDataPos;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.aty_monitor_mang);
		
		initActionBar();
		initViews();
	}

	@SuppressWarnings("unchecked")
	private void initViews() {
		lvMang = (ListView)findViewById(R.id.lv1);
		TextView tvEmptyView = (TextView)findViewById(R.id.emptyView);
		
		lvMang.setEmptyView(tvEmptyView);
		mLstMInfos = (List<MonitorInfo>) PersistentUtil.readObject(MyApp.getInstance().getPathBaseRoot(StoreValue.MONITOR_INFOS_FILE));
		if (mLstMInfos == null){
			mLstMInfos = new ArrayList<MonitorInfo>();
		}
		
		mAdpater = new MonitorMangAdapter(this, mLstMInfos);
		lvMang.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(MonitorMangAty.this, EditMonitorAty.class);
				intent.putExtra(EditMonitorAty.EXTRA_MONITOR_INFO, mLstMInfos.get(position));
				intent.putExtra(EditMonitorAty.EXTRA_OPERATE, EditMonitorAty.EXTRA_OPERATE_EDIT);
				startActivityForResult(intent,REQUEST_EDIT);
			}
		});
		setListViewContextMenuWithDel();
		lvMang.setAdapter(mAdpater);
		// 启动服务
		mConnIntent = new Intent(MonitorMangAty.this, BgdService2.class);
		startService(mConnIntent);
		bindService(mConnIntent, mServiceConnection, BIND_AUTO_CREATE);
	}

	private void setListViewContextMenuWithDel() {
		lvMang.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
			
			@Override
			public void onCreateContextMenu(ContextMenu menu, View v,
					ContextMenuInfo menuInfo) {
				intCurrDataPos = ((AdapterContextMenuInfo) menuInfo).position;
				menu.add(0, 0, 0, "删除");
			}
		});
	}
	private void setListViewContextMenuWithDel_Resume() {
		lvMang.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
			
			@Override
			public void onCreateContextMenu(ContextMenu menu, View v,
					ContextMenuInfo menuInfo) {
				intCurrDataPos = ((AdapterContextMenuInfo) menuInfo).position;
				menu.add(0, 0, 0, "删除");
				menu.add(0, 1, 1, "恢复所有已暂停车次");
			}
		});
	}
	@Override
	public boolean onContextItemSelected(android.view.MenuItem item) {
		MonitorInfo mInfo = mLstMInfos.get(intCurrDataPos);
		switch(item.getItemId()){
		case 0:
			mLstMInfos.remove(mInfo);
			mAdpater.notifyDataSetChanged();
			mBgdSerivce2.notifyLstMInfosChanged(mLstMInfos);
			break;
		case 1:
			if (mBgdSerivce2 != null){
				mBgdSerivce2.resumeMonitors();
				refreshData(false);
			}
			hideResumePanel();
			break;
		}
		return super.onContextItemSelected(item);
	}
	
	/**
	 * 只刷新当前监控的状态
	 * @param mInfo
	 */
	private void refreshStatusFromService(MonitorInfo mInfo){
		if (mBgdSerivce2 != null){
			if (mInfo != null){
				for(int i=0; i<mLstMInfos.size(); i++){
					if (mLstMInfos.get(i).getNativeIndex() == mInfo.getNativeIndex()){
						MonitorInfo mInfo1 = mLstMInfos.get(i);
						mInfo1.setRetryCount(mInfo.getRetryCount());
						mInfo1.setStartMonitorTime(mInfo.getStartMonitorTime());
						mInfo1.setStatus(mInfo.getStatus());
						break;
					}
				}
			}
			mAdpater.notifyDataSetChanged();
		}
	}
	/**
	 * 刷新整个列表
	 * @param isNotifyService 是否通知Service刷新列表
	 */
	@SuppressWarnings("unchecked")
	private void refreshData(boolean isNotifyService) {
		List<MonitorInfo> lstMInfos = (List<MonitorInfo>) PersistentUtil.readObject(MyApp.getInstance().getPathBaseRoot(StoreValue.MONITOR_INFOS_FILE));
		if (lstMInfos != null){
			mLstMInfos.clear();
			for(MonitorInfo mInfo:lstMInfos){
				mLstMInfos.add(mInfo);
			}
			mAdpater.notifyDataSetChanged();
			if (isNotifyService && mBgdSerivce2 != null){
				mBgdSerivce2.notifyLstMInfosChanged(mLstMInfos);
			}
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK){
			refreshData(true);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void initActionBar() {
		ActionBar actBar = getSupportActionBar();
		actBar.setDisplayShowTitleEnabled(false);
		actBar.setDisplayShowHomeEnabled(true);
		actBar.setDisplayHomeAsUpEnabled(true);
		actBar.setDisplayShowTitleEnabled(true);
		actBar.setTitle("抢票监控");
		actBar.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.actionbar_tab_bg));
	}
	
	@Override
	protected void onDestroy() {
		if (mBgdSerivce2 != null){
			try{
				unbindService(mServiceConnection);
				if ((!mBgdSerivce2.hasMonitor())){
					stopService(mConnIntent);
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		L.i("MonitorMangAty onDestory");
		super.onDestroy();
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
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		if (mBgdSerivce2 != null && mBgdSerivce2.hasPausedMInfos()){
			showResumePanel();
		}else{
			hideResumePanel();
		}
	}
	private void hideResumePanel() {
		setListViewContextMenuWithDel();
	}

	private void showResumePanel() {
		setListViewContextMenuWithDel_Resume();
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
}
