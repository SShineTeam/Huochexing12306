package com.sshine.huochexing.ticketOnline;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;
import com.sshine.huochexing.R;
import com.sshine.huochexing.adapter.PassengerMangAdapter;
import com.sshine.huochexing.base.BaseAty;
import com.sshine.huochexing.bean.A6Info;
import com.sshine.huochexing.bean.BookingInfo;
import com.sshine.huochexing.bean.PassengerInfo;
import com.sshine.huochexing.model.CustomDialog;
import com.sshine.huochexing.utils.A6UserInfoSPUtil;
import com.sshine.huochexing.utils.A6Util;
import com.sshine.huochexing.utils.MyApp;
import com.sshine.huochexing.utils.MyThread;
import com.sshine.huochexing.utils.PersistentUtil;
import com.sshine.huochexing.value.SF;
import com.sshine.huochexing.value.StoreValue;
import com.umeng.analytics.MobclickAgent;

import eu.inmite.android.lib.dialogs.ISimpleDialogListener;
import eu.inmite.android.lib.dialogs.ProgressDialogFragment;
import eu.inmite.android.lib.dialogs.SimpleDialogFragment;

public class PassengerMangAty extends BaseAty
	implements ISimpleDialogListener{
	private List<PassengerInfo> mLstPInfos;
	private PassengerMangAdapter mAdapter;
	private static final int REQUEST_ADD = EditPassengerAty.EXTRA_OPERATE_ADD;
	private static final int REQUEST_EDIT = EditPassengerAty.EXTRA_OPERATE_EDIT;
	private static final int REQUEST_EMPTY_TIP = 3;
	private static final int REQUEST_CANEL_SYNC_PASSENGERS = 4;
	private static final int REQUEST_DEL_P = 5;
	private static final int MSG_CONFIRM_P_INFO_CONFLICT = 28;
	private static final int MSG_CONFIRM_NO_P_INFO_AT_LOCAL = 29;
	private static final int MSG_SYNC_P_SUCCESS = 31;
	private static final int MSG_SYNC_P_TIP = 32;
	
	private A6UserInfoSPUtil a6UserSP = MyApp.getInstance().getA6UserInfoSPUtil();
	private BookingInfo mBInfo = MyApp.getInstance().getCommonBInfo();
	private MyThread mSyncPassengers;
	private PassengerInfo mTempLocalPInfo,mTempA6PInfo;
	List<PassengerInfo> mLstFinalPInfos = new ArrayList<PassengerInfo>();
	//0:提示，1,delPInfoFromA6,2:addA6,3:忽略
	private int mNoPInfoAtLocalType = 0;
	//0:提示，1:addP6，2：addLocal
	private int mPInfoConflictType = 0;

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_CONFIRM_P_INFO_CONFLICT:
				confirmConflict();
				break;
			case MSG_CONFIRM_NO_P_INFO_AT_LOCAL:
				confirmNoPInfoAtLocal();
				break;
			case MSG_SYNC_P_SUCCESS:
				String strMsg = "所有乘车人信息已同步完成，共从12306添加"
					+mAddA6Num+"个乘车人，删除"+mDelPInfoFromA6Num+"个乘车人"+SF.SUCCESS;
				showDlg(strMsg);
				mSyncPassengers = null;
				mLstFinalPInfos = null;
				mTempA6PInfo = null;
				mTempLocalPInfo = null;
				for(int i=0; i<mLstPInfos.size(); i++){
					mLstPInfos.get(i).setNativeIndex(i+1);
				}
				setUserSelfFlag();
				mSyncPDlg.dismissAllowingStateLoss();
				mAdapter.notifyDataSetChanged();
				break;
			case MSG_SYNC_P_TIP:
				mSyncPDlg.setMessage((CharSequence)msg.obj);
				break;
			}
		}
	};
	private ProgressDialogFragment mSyncPDlg;
	private int mDataIndex;
	private int mDelPInfoFromA6Num=0,mAddA6Num=0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setDisableLoadingView(true);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.aty_passenger_mang);
		setTitle("乘车人管理");
		initViews();
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

	private void confirmNoPInfoAtLocal() {
		CustomDialog dlg = new CustomDialog.Builder(this,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dlg, int which) {
						switch(which){
						case DialogInterface.BUTTON_POSITIVE:
							if (!delPInfoFromA6()){
								String strMsg = "删除乘车人&nbsp;<font color='#ff8c00'>" + mTempA6PInfo.getPassenger_name() + "--"
										+ mTempA6PInfo.getPassenger_id_no() + "</font>&nbsp;的信息失败" + SF.FAIL;
								showMsg(Html.fromHtml(strMsg));
							}
							break;
						case DialogInterface.BUTTON_NEUTRAL:
							addA6();
							break;
						case DialogInterface.BUTTON_NEGATIVE:
							break;
						}
						boolean isRepeat = ((CustomDialog)dlg).isCheckBoxChecked();
						if (isRepeat){
							switch(which){
							case DialogInterface.BUTTON_POSITIVE:
								mNoPInfoAtLocalType = 1;
								break;
							case DialogInterface.BUTTON_NEUTRAL:
								mNoPInfoAtLocalType = 2;
								break;
							case DialogInterface.BUTTON_NEGATIVE:
								mNoPInfoAtLocalType = 3;
								break;
							}
						}
						mSyncPassengers.setSupsend(false);
					}
				})
				.setTitle("冲突提示")
				.setCancelable(false)
				.setMessage(
						Html.fromHtml("检测到本地没有乘车人&nbsp;<font color='#ff8c00'>" + mTempA6PInfo.getPassenger_name() + "--"
								+ mTempA6PInfo.getPassenger_id_no() + "</font>&nbsp;的信息，请选择操作方式:"))
				.setCheckboxText("本次同步不再提示")
				.setPositiveButton("从12306删除").setNaturalButton("添加信息到本地")
				.setNagativeButton("忽略").create();
		dlg.show();
	}

	private boolean delPInfoFromA6() {
		new Thread(){
			@Override
			public void run() {
				if(deletePInfo(mTempA6PInfo)){
				}else{
					mDelPInfoFromA6Num--;
					if(mDelPInfoFromA6Num < 0){
						mDelPInfoFromA6Num=0;
					}
					String strMsg = "从12306删除<font color='#ff8c00'>"+mTempA6PInfo.getPassenger_name()
						+"</font>的信息失败"+SF.FAIL;
					sendToast(Html.fromHtml(strMsg));
				}
			}
		}.start();
		mDelPInfoFromA6Num++;
		return true;
	}
	public void addLocal() {
		mLstFinalPInfos.add(mTempLocalPInfo);
	}
	
	public void overrideLocal(){
		removeLocal();
		addA6();
	}
	
	public void removeLocal(){
		mLstFinalPInfos.remove(mTempLocalPInfo);
	}

	public void addA6() {
		mLstFinalPInfos.add(mTempA6PInfo);
		mAddA6Num++;
	}

	private void confirmConflict() {
		CustomDialog dlg = new CustomDialog.Builder(this,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dlg, int which) {
						switch(which){
						case DialogInterface.BUTTON_POSITIVE:
							overrideLocal();
							break;
						case DialogInterface.BUTTON_NEGATIVE:
							addLocal();
							break;
						}
						boolean isRepeat = ((CustomDialog)dlg).isCheckBoxChecked();
						if (isRepeat){
							switch(which){
							case DialogInterface.BUTTON_POSITIVE:
								mPInfoConflictType = 1;
								break;
							case DialogInterface.BUTTON_NEGATIVE:
								mPInfoConflictType = 2;
								break;
							}
						}
						mSyncPassengers.setSupsend(false);
					}
				})
				.setTitle("冲突提示")
				.setCancelable(false)
				.setMessage(
						Html.fromHtml("检测到乘车人&nbsp;<font color='#ff8c00'>" + mTempA6PInfo.getPassenger_name() + "--"
								+ mTempA6PInfo.getPassenger_id_no()
								+ "</font>&nbsp;在12306存储的信息与本地信息不一致，是否覆盖本地信息数据？"))
				.setCheckboxText("本次同步不再提示")
				.setPositiveButton("是").setNaturalButton("否")
				.create();
		dlg.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
//		MenuItem miAdd = menu.add("添加乘车人");
//		miAdd.setIcon(R.drawable.head_add);
//		miAdd.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
//		miAdd.setOnMenuItemClickListener(new OnMenuItemClickListener() {
//
//			@Override
//			public boolean onMenuItemClick(MenuItem item) {
//				Intent intent = new Intent(PassengerMangAty.this,
//						EditPassengerAty.class);
//				intent.putExtra(EditPassengerAty.EXTRA_OPERATE, EditPassengerAty.EXTRA_OPERATE_ADD);
//				startActivityForResult(intent, REQUEST_ADD);
//				return false;
//			}
//		});
		MenuItem miSync = menu.add("同步12306乘车人");
		miSync.setIcon(R.drawable.refesh);
		miSync.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		miSync.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				//初始化变量
				mNoPInfoAtLocalType = 0;
				mPInfoConflictType = 0;
				syncPassengers();
				return false;
			}
		});
		return super.onCreateOptionsMenu(menu);
	}

	private void syncPassengers() {
		if (!A6Util.isCanBooking()){
			showDlg("23:00-07:00为12306系统维护时间，此段时间内不能同步乘车人哦" + SF.TIP);
			return;
		}
		mSyncPDlg = (ProgressDialogFragment) ProgressDialogFragment.createBuilder(this, getSupportFragmentManager())
			.setTitle("同步12306乘车人")
			.setMessage("正在同步乘车人信息...")
			.setCancelable(false)
			.setRequestCode(REQUEST_CANEL_SYNC_PASSENGERS)
			.show();
		mDelPInfoFromA6Num=0;
		mAddA6Num=0;
		mSyncPassengers = new MyThread() {
			List<PassengerInfo> lstLocalPInfos = null;
			List<PassengerInfo> lstA6PInfos = null;

			@Override
			public void run() {
				try {
					lstLocalPInfos = mLstPInfos;
					lstA6PInfos = A6Util.getPassengerDTOs(mBInfo)
							.getNormal_passengers();
				} catch (Exception e) {
					e.printStackTrace();
					sendError();
					return;
				}
				if (lstA6PInfos == null) {
					sendError();
					return;
				}
				if (lstLocalPInfos == null) {
					lstLocalPInfos = new ArrayList<PassengerInfo>();
				}
				if (mLstFinalPInfos == null){
					mLstFinalPInfos = new ArrayList<PassengerInfo>();
				}
				for (int i = 0; i < lstLocalPInfos.size(); i++) {
					mLstFinalPInfos.add(lstLocalPInfos.get(i));
				}
				//数据状态，0:12306无此数据，1:数据冲突，2:本地无此数据，3:数据未更新.
				for (int i = 0; i < lstA6PInfos.size(); i++) {
					PassengerInfo a6PInfo = lstA6PInfos.get(i);
					PassengerInfo localPInfo = findPInfo(lstLocalPInfos, a6PInfo);
					if (localPInfo == null) {
						switchSyncWork(2, localPInfo, a6PInfo);
					} else {
						String strA6 = A6Util.getGson().toJson(a6PInfo);
						String strLocal = A6Util.getGson().toJson(localPInfo);
						if (!strA6.equals(strLocal)) {
							switchSyncWork(1, localPInfo, a6PInfo);
						}else{
							switchSyncWork(3, localPInfo, a6PInfo);
						}
					}
					String strMsg = "已成功同步&nbsp;<font color='#ff8c00'>"+a6PInfo.getPassenger_name()+"</font>&nbsp;的信息";
					Message msg = mHandler.obtainMessage(MSG_SYNC_P_TIP, Html.fromHtml(strMsg));
					mHandler.sendMessage(msg);
				}
				for (int i = 0; i < lstLocalPInfos.size(); i++) {
					PassengerInfo localPInfo = lstLocalPInfos.get(i);
					PassengerInfo a6PInfo = findPInfo(lstA6PInfos, localPInfo);
					if (a6PInfo == null) {
						switchSyncWork(0, localPInfo, a6PInfo);
					}
					String strMsg = "已成功同步&nbsp;<font color='#ff8c00'>"+localPInfo.getPassenger_name()+"</font>&nbsp;的信息";
					Message msg = mHandler.obtainMessage(MSG_SYNC_P_TIP, Html.fromHtml(strMsg));
					mHandler.sendMessage(msg);
				}
				PersistentUtil.writeObject(
						mLstFinalPInfos,
						MyApp.getInstance().getPathBaseRoot(
								StoreValue.PASSENGER_INFOS_FILE));
				mLstPInfos.clear();
				for(int i=0;i<mLstFinalPInfos.size(); i++){
					mLstPInfos.add(mLstFinalPInfos.get(i));
				}
				mLstFinalPInfos = null;
				Message msg = mHandler.obtainMessage(MSG_SYNC_P_SUCCESS);
				mHandler.sendMessage(msg);
				sendDismissDialog();
			}

			private void switchSyncWork(int status, PassengerInfo localPInfo, PassengerInfo a6PInfo){
				mTempLocalPInfo = localPInfo;
				mTempA6PInfo = a6PInfo;
				switch (status) {
				case 0:
					//12306没有对应数据的情况暂不处理
					break;
				case 1:
					switch(mPInfoConflictType){
					case 0:
						Message msg = mHandler.obtainMessage(MSG_CONFIRM_P_INFO_CONFLICT);
						mHandler.sendMessage(msg);
						this.setSupsend(true);
						break;
					case 1:
						if (!delPInfoFromA6()){
							String strMsg = "删除乘车人&nbsp;<font color='#ff8c00'>" + mTempA6PInfo.getPassenger_name() + "--"
									+ mTempA6PInfo.getPassenger_id_no() + "</font>&nbsp;的信息失败" + SF.FAIL;
							sendToast(Html.fromHtml(strMsg));
						}
						break;
					case 2:
						addA6();
						break;
					case 3:
						addLocal();
						break;
					}
					break;
				case 2:
					switch(mNoPInfoAtLocalType){
					case 0:
						Message msg1 = mHandler.obtainMessage(MSG_CONFIRM_NO_P_INFO_AT_LOCAL);
						mHandler.sendMessage(msg1);
						this.setSupsend(true);
						break;
					case 1:
						delPInfoFromA6();
						break;
					case 2:
						addA6();
						break;
					}
					break;
				case 3:
					break;
				}
			}
			private PassengerInfo findPInfo(List<PassengerInfo> lstPInfos, PassengerInfo pInfo1) {
				for (int i = 0; i < lstPInfos.size(); i++) {
					PassengerInfo pInfo = lstPInfos.get(i);
					if (pInfo.getPassenger_name().equals(pInfo1.getPassenger_name())
						&& pInfo.getPassenger_id_type_code().equals(pInfo1.getPassenger_id_type_code())
						&& pInfo.getPassenger_id_no().equals(pInfo1.getPassenger_id_no())){
						return pInfo;
					}
				}
				return null;
			}

			private void sendError() {
				sendToast("与12306服务器通信时出错，同步乘车人失败" + SF.FAIL);
				mSyncPDlg.dismissAllowingStateLoss();
			}

		};
		mSyncPassengers.start();
	}

	@SuppressWarnings("unchecked")
	private void initViews() {
		if (!a6UserSP.isLogin()) {
			startActivity(new Intent(this, A6LoginAty.class));
		}
		ListView lvPassengers = (ListView) findViewById(R.id.lv1);
		try {
			mLstPInfos = (List<PassengerInfo>) PersistentUtil.readObject(MyApp
					.getInstance().getPathBaseRoot(StoreValue.PASSENGER_INFOS_FILE));
			if (mLstPInfos == null){
				mLstPInfos = new ArrayList<PassengerInfo>();
			}else{
				setUserSelfFlag();
			}
			mAdapter = new PassengerMangAdapter(this, mLstPInfos, new PassengerMangAdapter.OnPMAClickListener() {
				
				@Override
				public void onNameClick(View v, int pos) {
					PassengerInfo pInfo = mLstPInfos.get(pos);
					pInfo.setCommon(pInfo.isCommon()?false:true);
					mAdapter.notifyDataSetChanged();
				}

				@Override
				public void onDelClick(View v, int pos) {
					mDataIndex = pos;
					PassengerInfo pInfo = mLstPInfos.get(mDataIndex);
					if (!pInfo.isUserSelf()){
						SimpleDialogFragment.createBuilder(PassengerMangAty.this, getSupportFragmentManager())
						.setTitle("提示")
						.setMessage("确定要删除此乘车人信息吗?")
						.setRequestCode(REQUEST_DEL_P)
						.setPositiveButtonText("是")
						.setNegativeButtonText("否")
						.show();
					}
				}
			});
			lvPassengers.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int position, long arg3) {
					Intent intent = new Intent(PassengerMangAty.this, EditPassengerAty.class);
					intent.putExtra(EditPassengerAty.EXTRA_PASSENGER_INFO, mLstPInfos.get(position));
					intent.putExtra(EditPassengerAty.EXTRA_OPERATE, EditPassengerAty.EXTRA_OPERATE_EDIT);
					startActivityForResult(intent, REQUEST_EDIT);
				}
			});
			lvPassengers.setAdapter(mAdapter);
			if (mLstPInfos == null || mLstPInfos.size() == 0) {
				SimpleDialogFragment
				.createBuilder(this, getSupportFragmentManager())
				.setCancelable(false)
				.setRequestCode(REQUEST_EMPTY_TIP).setTitle("提示")
				.setMessage("乘车人信息为空，是否立即从12306同步乘车人？")
				.setPositiveButtonText("是").setNegativeButtonText("否")
				.show();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void setUserSelfFlag() {
		for(int i=0;i<mLstPInfos.size(); i++){
			PassengerInfo pInfo = mLstPInfos.get(i);
			if (pInfo.getPassenger_name().equals(a6UserSP.getUserRealName())){
				pInfo.setUserSelf(true);
				break;
			}
		}
	}

	@Override
	protected void onStop() {
		savePInfos();
		super.onStop();
	}

	public void savePInfos() {
		String strPath = MyApp.getInstance().getPathBaseRoot(StoreValue.PASSENGER_INFOS_FILE);
		PersistentUtil.writeObject(mLstPInfos, strPath);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onPositiveButtonClicked(int requestCode) {
		switch (requestCode) {
		case REQUEST_EMPTY_TIP:
			mNoPInfoAtLocalType = 2;
			syncPassengers();
			break;
		case REQUEST_CANEL_SYNC_PASSENGERS:
			mSyncPassengers.stop();
			this.finish();
			break;
		case REQUEST_DEL_P:
			mLstPInfos.remove(mDataIndex);
			mAdapter.notifyDataSetChanged();
			break;
		}
	}
	
	@Override
	public void onNegativeButtonClicked(int requestCode) {
		switch (requestCode) {
		case REQUEST_EMPTY_TIP:
			this.finish();
			break;
		case REQUEST_CANEL_SYNC_PASSENGERS:
			mSyncPassengers.setSupsend(false);
			break;
		} 
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (resultCode == RESULT_OK){
			refreshData();
		}
		super.onActivityResult(requestCode, resultCode, intent);
	}
	public void refreshData() {
		@SuppressWarnings("unchecked")
		List<PassengerInfo> lstPInfos = (List<PassengerInfo>) PersistentUtil.readObject(MyApp.getInstance().getPathBaseRoot(StoreValue.PASSENGER_INFOS_FILE));
		if (lstPInfos != null){
			mLstPInfos.clear();
			for(PassengerInfo pInfo:lstPInfos){
				mLstPInfos.add(pInfo);
			}
			mAdapter.notifyDataSetChanged();
		}
	}
	
	private boolean deletePInfo(PassengerInfo pInfo){
		String url = "https://kyfw.12306.cn/otn/passengers/delete";
		List<NameValuePair> lstParams = new ArrayList<NameValuePair>();
		lstParams.add(new BasicNameValuePair("passenger_name", pInfo.getPassenger_name()));  
		lstParams.add(new BasicNameValuePair("passenger_id_type_code", pInfo.getPassenger_id_type_code())); 
		lstParams.add(new BasicNameValuePair("passenger_id_no", pInfo.getPassenger_id_no()));
		lstParams.add(new BasicNameValuePair("isUserSelf", "N"));
		try{
			A6Info a6Json = A6Util.post(mBInfo, A6Util.makeRefererColl("https://kyfw.12306.cn/otn/passengers/init"), url, lstParams);
			JSONObject jsonObj = new JSONObject(a6Json.getData());
			if (jsonObj.getBoolean("flag")){
				return true;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;
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
