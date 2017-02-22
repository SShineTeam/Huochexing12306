package com.sshine.huochexing.base;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.sshine.huochexing.R;
import com.sshine.huochexing.model.CustomDialog;
import com.sshine.huochexing.model.PullToRefreshView;
import com.sshine.huochexing.model.PullToRefreshView.OnFooterRefreshListener;
import com.sshine.huochexing.model.PullToRefreshView.OnHeaderRefreshListener;
import com.sshine.huochexing.utils.A6Util;
import com.sshine.huochexing.utils.HttpUtil;
import com.sshine.huochexing.utils.L;
import com.sshine.huochexing.value.SF;

import eu.inmite.android.lib.dialogs.ProgressDialogFragment;

public abstract class BaseAty extends SherlockFragmentActivity
	implements OnHeaderRefreshListener, OnFooterRefreshListener{

	protected DialogFragment mDlg;
	private ViewGroup mContainer, mContentView;
	private View mFooterLoadingView;
	
	private static final int REFRESH_TYPE_LOADING = 0;
	private static final int REFRESH_TYPE_PULL_DOWN = 1;
	private static final int REFRESH_TYPE_PULL_UP = 2;
	//刷新模式
	private int mRefreshType = REFRESH_TYPE_LOADING;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		L.d("onCreate:" + getClass().getSimpleName());
		initActionBar();
	}

	private void initActionBar() {
		ActionBar actBar = getSupportActionBar();
		actBar.setDisplayShowTitleEnabled(false);
		actBar.setDisplayShowHomeEnabled(true);
		actBar.setDisplayHomeAsUpEnabled(true);
		actBar.setDisplayShowTitleEnabled(true);
		actBar.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.actionbar_tab_bg));
	}
	/**
	 * 只检测有无网络
	 * @return
	 */
	protected boolean startHandle(Runnable task) {
		if (!HttpUtil.isNetworkConnected(this)){
			Message msg = mHandler.obtainMessage(A6Util.MSG_TOAST);
			msg.obj = "网络不可用，请检测网络状态" + SF.NO_NETWORK;
			mHandler.sendMessage(msg);
			showContentView(false);
			return false;
		}else{
			new Thread(task).start();
			return true;
		}
	}
	@Override
	public void setTitle(CharSequence title) {
		getSupportActionBar().setTitle(title);
	}
	protected void showMsg(CharSequence strMsg) {
		Toast.makeText(this, strMsg, Toast.LENGTH_SHORT).show();
	}
	
	/**
	 * 只用于显示ProgressDialog
	 * @param strMsg
	 * @return 无网络时返回false
	 */
	protected boolean startHandle(String strMsg) {
		return startHandle(strMsg, null);
	}
	
	protected boolean checkNetwork() {
		if (!HttpUtil.isNetworkConnected(this)){
			Message msg = mHandler.obtainMessage(A6Util.MSG_TOAST);
			msg.obj = "网络不可用，请检测网络状态" + SF.NO_NETWORK;
			mHandler.sendMessage(msg);
			return false;
		}else{
			return true;
		}
	}
	protected boolean startHandle(String strMsg, Runnable task){
		if (strMsg == null){
			strMsg = getResources().getString(R.string.requestData);
		}
		if (checkNetwork()){
			Message msg = mHandler.obtainMessage(A6Util.MSG_START_PROGRESS);
			msg.obj = strMsg;
			mHandler.sendMessage(msg);
			showContentView(false);
			if (task != null){
				new Thread(task).start();
			}
			return true;
		}
		return false;
	}
	
	protected void showDlg(CharSequence csMsg) {
		//报错原因不明，暂时catch.
		try{
			CustomDialog dlg = new CustomDialog.Builder(this, null)
				.setTitle("提示")
				.setMessage(csMsg)
				.setPositiveButton("确定")
				.create();
			dlg.show();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	protected void sendToast(CharSequence csMsg){
		Message msg = mHandler.obtainMessage(A6Util.MSG_TOAST, csMsg);
		mHandler.sendMessage(msg);
		sendDismissDialog();
	}
	
	protected void sendDismissDialog() {
		mHandler.sendMessage(mHandler.obtainMessage(A6Util.MSG_DISMISS_DIALOG));
	}
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch(msg.what){
			case A6Util.MSG_TOAST:
				showMsg((CharSequence)msg.obj);
				break;
			case A6Util.MSG_DISMISS_DIALOG:
				if (mDlg != null){
					mDlg.dismissAllowingStateLoss();
				}
			case A6Util.MSG_START_PROGRESS:
				String strMsg = (String)msg.obj;
				if (strMsg != null){
					try{
						if (mDlg != null){
							mDlg.dismissAllowingStateLoss();
						}
						mDlg = ProgressDialogFragment.createBuilder(BaseAty.this, getSupportFragmentManager())
								.setMessage(strMsg)
								.show();
					}catch(Exception e){
						e.printStackTrace();
						showMsg(strMsg);
					}
				}
				break;
			}
		}
	};
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private boolean mDisableLoadingView;
	protected PullToRefreshView mPullToRefreshView;
	
	/**
	 * 初始化界面时调用
	 */
	public void initContainerViews(){
		initContainerViews(R.id.container, R.id.contentView, R.id.pull_to_refresh_view);
	}
	
	/**
	 * 初始化界面时调用
	 * @param containerId
	 * @param contentViewId
	 */
	public void initContainerViews(int containerId, int contentViewId, int pullToRefreshViewId){
		if (!mDisableLoadingView){
			mFooterLoadingView = LayoutInflater.from(this).inflate(R.layout.footer_loading_view, null);
			mFooterLoadingView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
			mContainer = (ViewGroup)findViewById(containerId);
			mContentView = (ViewGroup)findViewById(contentViewId);
			mPullToRefreshView = (PullToRefreshView)findViewById(pullToRefreshViewId);
			if (mPullToRefreshView != null){
				mPullToRefreshView.setOnHeaderRefreshListener(this);
				mPullToRefreshView.setOnFooterRefreshListener(this);
			}
			showLoadingView();
		}
	}
	
	/**
	 * 显示界面，需要在数据请求完成后调用，不使用BaseAty的loadingView的无需调用。
	 */
	public void showContentView(final boolean isSuccess) {
			mHandler.post(new Runnable(){
				@Override
				public void run() {
					if (mPullToRefreshView != null){
						mPullToRefreshView.onHeaderRefreshComplete();
						mPullToRefreshView.onFooterRefreshComplete();
						if (isSuccess){
							switch(mRefreshType){
							case REFRESH_TYPE_PULL_DOWN:
								showMsg("数据已更新");
								break;
							}
						}
					}
					if (!mDisableLoadingView){
						mContainer.removeView(mFooterLoadingView);
						mContentView.setVisibility(View.VISIBLE);
					}
				}
			});
	}
	
	/**
	 * 显示loading
	 */
	public void showLoadingView(){
		if (!mDisableLoadingView){
			mRefreshType = REFRESH_TYPE_LOADING;
			mContentView.setVisibility(View.GONE);
			mContainer.removeView(mFooterLoadingView);
			mContainer.addView(mFooterLoadingView);
		}
	}
	
	/**
	 * 下拉刷新任务，更新完后需调用showContentView()方法通知已更新数据。
	 */
	public abstract void doHeaderTask();
	
	/**
	 * 上拉加载任务，更新完后需调用showContentView()方法通知已更新数据。
	 */
	public abstract void doFooterTask();
	@Override
	public void onFooterRefresh(PullToRefreshView view) {
		mContainer.removeView(mFooterLoadingView);
		mContainer.addView(mFooterLoadingView);
		mRefreshType = REFRESH_TYPE_PULL_UP;
		doFooterTask();
	}

	@Override
	public void onHeaderRefresh(PullToRefreshView view) {
		mContainer.removeView(mFooterLoadingView);
		mContainer.addView(mFooterLoadingView);
		mRefreshType = REFRESH_TYPE_PULL_DOWN;
		doHeaderTask();
	}

	/**
	 * 是否已禁用加载界面
	 * @return
	 */
	public boolean isDisableLoadingView() {
		return mDisableLoadingView;
	}

	/**
	 * 设置是否禁用加载界面，需要在super.OnCreate()之前调用，默认为false
	 * @param disableLoadingView
	 */
	public void setDisableLoadingView(boolean disableLoadingView) {
		mDisableLoadingView = disableLoadingView;
	}
}
