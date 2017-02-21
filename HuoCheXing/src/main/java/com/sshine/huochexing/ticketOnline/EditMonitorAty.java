package com.sshine.huochexing.ticketOnline;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.view.MenuItem;
import com.sshine.huochexing.R;
import com.sshine.huochexing.base.BaseAty;
import com.sshine.huochexing.bean.BookingInfo;
import com.sshine.huochexing.bean.ConfirmPassengersInfo;
import com.sshine.huochexing.bean.MonitorInfo;
import com.sshine.huochexing.bean.PassengerInfo;
import com.sshine.huochexing.listener.IFavoriteCharacterDialogListener;
import com.sshine.huochexing.listener.IMultiChoiceDialogListener;
import com.sshine.huochexing.model.FavoriteCharacterDialogFragment;
import com.sshine.huochexing.model.MultiChoiceFragment;
import com.sshine.huochexing.utils.A6UserInfoSPUtil;
import com.sshine.huochexing.utils.A6Util;
import com.sshine.huochexing.utils.MyApp;
import com.sshine.huochexing.utils.MyUtils;
import com.sshine.huochexing.utils.PersistentUtil;
import com.sshine.huochexing.utils.SeatHelper;
import com.sshine.huochexing.utils.TimeUtil;
import com.sshine.huochexing.value.SF;
import com.sshine.huochexing.value.StoreValue;
import com.sshine.huochexing.value.TT;
import com.umeng.analytics.MobclickAgent;

import eu.inmite.android.lib.dialogs.ISimpleDialogListener;
import eu.inmite.android.lib.dialogs.SimpleDialogFragment;

public class EditMonitorAty extends BaseAty
	implements OnClickListener,ISimpleDialogListener, IFavoriteCharacterDialogListener,IMultiChoiceDialogListener{
	public static final String EXTRA_MONITOR_INFO = "info";
	public static final String EXTRA_OPERATE  = "extraOpearate";
	public static final int EXTRA_OPERATE_ADD  = 1;
	public static final int EXTRA_OPERATE_EDIT  = 2;
	private static final int REQUEST_MONITOR_TRAINS = 3;
	private static final int REQUEST_SEAT_TYPE = 4;
	private static final int REQUEST_MONITOR_SPEED = 5;
	private static final int REQUEST_P_INFO = 6;
	private static final int REQUEST_MIX_LITMIT_TIP = 7;
	private final int MAX_P_INFO_NUM = 10;
	
	private MonitorInfo mMInfo;
	private List<View> mLstViews = new ArrayList<View>();
	private Button btnMTrains,btnSeatType,btnSpeed,btnPInfos,btnOK;
	private ImageView ivRing,ivVibrate;
	private int mOperateType;
	private String[] mMTrainNames;
	private String[] mSeatTypeNames;
	private String[] mMSpeedKeys = {"高速","中速","低速(省流量)","超低速(省流量)"};
	private int[] mMSpeedValues = {3000,5000,10000, 30000};
	private String[] mPNames = null;
	private int[] mPNativeIndexes;
	private boolean[] mSelectedPNames;
	private boolean[] mSelectedTrainNames;
	private boolean[] mSelectedSeatTypes;
	private A6UserInfoSPUtil a6UserSP = MyApp.getInstance().getA6UserInfoSPUtil();
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler(){
		@SuppressWarnings("unchecked")
		public void handleMessage(android.os.Message msg) {
			switch(msg.what){
			case A6Util.MSG_GET_PASSENGERS_SUCCESS:
				initPNames((List<PassengerInfo>)msg.obj);
				break;
			}
		};
	};
	//1全选，0全不选
	private int mMTrainsSelectAllStatus = 1;
	private BookingInfo mBInfo = MyApp.getInstance().getCommonBInfo();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setDisableLoadingView(true);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.aty_edit_monitor);
		setTitle("监控信息");
		initViews();
	}
	private void initViews() {
		mOperateType = getIntent().getIntExtra(EXTRA_OPERATE, EXTRA_OPERATE_ADD);
		mMInfo = (MonitorInfo) getIntent().getSerializableExtra(EXTRA_MONITOR_INFO);
		if (mMInfo == null){
			return;
		}
		tv(R.id.from, mMInfo.getFrom_station_name());
		tv(R.id.to, mMInfo.getTo_station_name());
		tv(R.id.date, mMInfo.getStart_time());
		tv(R.id.ticketType, getTicketTypeText());
		btnMTrains = btn(R.id.trains);
		btnSeatType = btn(R.id.seatType);
		btnSpeed = btn(R.id.speed);
		btnPInfos = btn(R.id.pInfos);
		btnOK = btn(R.id.ok);
		ivRing = (ImageView)findViewById(R.id.ring);
		ivRing.setOnClickListener(this);
		ivVibrate = (ImageView)findViewById(R.id.vibrate);
		ivVibrate.setOnClickListener(this);
		
		setResult(RESULT_CANCELED);
		//初始化预选数组
		mMTrainNames = new String[mMInfo.getLstTrainNames().size()];
		for(int i=0; i<mMInfo.getLstTrainNames().size(); i++){
			mMTrainNames[i] = mMInfo.getLstTrainNames().get(i)
				+ "("+mMInfo.getLstTrainTimeRanges().get(i)+")";
		}
		if (mMInfo.getSelectedTrainsNames() == null){
			mSelectedTrainNames = new boolean[mMInfo.getLstTrainNames().size()];
		}else{
			mSelectedTrainNames = mMInfo.getSelectedTrainsNames();
		}
		SeatHelper sHelper = new SeatHelper();
		SparseArray<String> saSTypes = SeatHelper.getSeatTypes();
		mSeatTypeNames = sHelper.getSeatTypeNames();
		mSelectedSeatTypes = new boolean[saSTypes.size()];
		if (mMInfo.getLstSeatTypes() != null){
			for(int i=0; i<saSTypes.size(); i++){
				if (mMInfo.getLstSeatTypes().contains(saSTypes.keyAt(i))){
					mSelectedSeatTypes[i] = true;
				}
			}
		}
		setMontiorTrainsText();
		setSeatTypeText();
		setSpeedText();
		@SuppressWarnings("unchecked")
		List<PassengerInfo> lstPInfos = (List<PassengerInfo>) PersistentUtil.readObject(MyApp.getInstance().getPathBaseRoot(StoreValue.PASSENGER_INFOS_FILE));
		if (lstPInfos != null && (lstPInfos.size() != 0)){
			initPNames(lstPInfos);
		}else{
			if (!a6UserSP.isLogin()){
				startActivity(new Intent(this, A6LoginAty.class));
				this.finish();
			}else{
				if (!startHandle("请求数据...")){
					return;
				}
				new Thread(){
					public void run() {
						ConfirmPassengersInfo cpInfo = A6Util.getPassengerDTOs(MyApp.getInstance().getCommonBInfo());
						sendDismissDialog();
						if (cpInfo != null){
							if (cpInfo.getNormal_passengers() == null){
								sendToast("数据请求失败"+SF.FAIL);
							}else{
								for(int i=0; i<cpInfo.getNormal_passengers().size(); i++){
									cpInfo.getNormal_passengers().get(i).setNativeIndex(i+1);
								}
								PersistentUtil.writeObject(cpInfo.getNormal_passengers(), MyApp.getInstance().getPathBaseRoot(StoreValue.PASSENGER_INFOS_FILE));
								Message msg = mHandler.obtainMessage(A6Util.MSG_GET_PASSENGERS_SUCCESS);
								msg.obj = cpInfo.getNormal_passengers();
								mHandler.sendMessage(msg);
							}
						}else{
							sendToast("数据请求失败"+SF.FAIL);
						}
					};
				}.start();
			}
		}
		MyUtils.setToogleImageStatus(ivRing, mMInfo.isRing());
		MyUtils.setToogleImageStatus(ivVibrate, mMInfo.isVibrate());
		mLstViews.add(btnMTrains);
		mLstViews.add(btnPInfos);
		mLstViews.add(btnSeatType);
		mLstViews.add(btnSpeed);
		mLstViews.add(ivRing);
		mLstViews.add(ivVibrate);
		if (mMInfo.isRunning()){
			btnOK.setText("停止抢票");
			setViewEnableStatus(false);
		}else{
			btnOK.setText("开始抢票");
		}
	}
	private CharSequence getTicketTypeText() {
		int index = -1;
		for(int i=0; i<TT.QUERY_TICKET_TYPE_VALUES.length; i++){
			if (TT.QUERY_TICKET_TYPE_VALUES[i].equals(mMInfo.getPurpose_codes())){
				index = i;
				break;
			}
		}
		return TT.QUERY_TICKET_TYPE_KEYS[index];
	}
	public void initPNames(List<PassengerInfo> lstPInfos) {
		mPNames = new String[lstPInfos.size()];
		mPNativeIndexes = new int[lstPInfos.size()];
		mSelectedPNames = new boolean[lstPInfos.size()];
		for(int i=0; i<lstPInfos.size(); i++){
			PassengerInfo pInfo = lstPInfos.get(i);
			mPNames[i] = pInfo.getPassenger_name();
			mPNativeIndexes[i] = pInfo.getNativeIndex();
		}
		if (mMInfo.getLstPNativeIndexes() != null){
			for(int i=0; i<lstPInfos.size(); i++){
				PassengerInfo pInfo = lstPInfos.get(i);
				if (mMInfo.getLstPNativeIndexes().contains(pInfo.getNativeIndex())){
					mSelectedPNames[i] = true;
				}
			}
		}
		setPInfosText();
	}
	private void setPInfosText() {
		String strText = "";
		int tempCount = 0;
		for(int i=0; i<mSelectedPNames.length; i++){
			if (mSelectedPNames[i]){
				tempCount++;
				strText += mPNames[i] + ",";
			}
		}
		if (tempCount == 0){
			btnPInfos.setText("");
		}else if (tempCount > MAX_P_INFO_NUM){
			showDlg("为提高抢票成功率，最多可添加"+MAX_P_INFO_NUM+"个乘车人"+SF.TIP);
			btnPInfos.setText("");
		}else{
			btnPInfos.setText(strText.substring(0, strText.length() - 1));
		}
	}
	private void setSpeedText() {
		int index = -1;
		for(int i=0; i<mMSpeedValues.length; i++){
			if (mMInfo.getMonitorSpeed() == mMSpeedValues[i]){
				index = i;
				break;
			}
		}
		if (index != -1){
			btnSpeed.setText(mMSpeedKeys[index]);
		}
	}
	private void setMontiorTrainsText() {
		String strText = "";
		int tempCount = 0;
		for(int i=0; i<mSelectedTrainNames.length; i++){
			if (mSelectedTrainNames[i]){
				tempCount++;
				strText += mMInfo.getLstTrainNames().get(i) + ",";
			}
		}
		if (tempCount == 0){
			btnMTrains.setText("");
			mMTrainsSelectAllStatus = 1;
		}else if (tempCount == mSelectedTrainNames.length){
			btnMTrains.setText("全部");
			mMTrainsSelectAllStatus = 0;
		}else{
			btnMTrains.setText(strText.substring(0, strText.length() - 1));
			mMTrainsSelectAllStatus = 1;
		}
	}
	private void setSeatTypeText() {
		String strText = "";
		int tempCount = 0;
		for(int i=0; i<mSelectedSeatTypes.length; i++){
			if (mSelectedSeatTypes[i]){
				tempCount++;
				strText += SeatHelper.getSeatTypes().valueAt(i) + ",";
			}
		}
		if (tempCount == 0){
			btnSeatType.setText("");
		}else if (tempCount == mSelectedSeatTypes.length){
			btnSeatType.setText("全部");
		}else{
			btnSeatType.setText(strText.substring(0, strText.length() - 1));
		}
	}
	private Button btn(int id){
		Button btn1 = (Button) findViewById(id);
		btn1.setOnClickListener(this);
		return btn1;
	}
	private void tv(int id, CharSequence cs1){
		TextView tv1 = (TextView) findViewById(id);
		tv1.setText(cs1);
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
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.trains:
			showTrainNamesSelect();
			break;
		case R.id.seatType:
			MultiChoiceFragment.show(this, REQUEST_SEAT_TYPE, "座位选择", mSeatTypeNames, mSelectedSeatTypes);
			break;
		case R.id.speed:
			FavoriteCharacterDialogFragment.show(this, REQUEST_MONITOR_SPEED, "监控速率", mMSpeedKeys);
			break;
		case R.id.pInfos:
			if (mPNames != null){
				MultiChoiceFragment.show(this, REQUEST_P_INFO, "乘车人选择", "确定", "乘车人管理", mPNames, mSelectedPNames);
			}
			break;
		case R.id.ring:
			mMInfo.setRing(MyUtils.getToogleImageStatus(ivRing)?false:true);
			MyUtils.setToogleImageStatus(ivRing, mMInfo.isRing());
			break;
		case R.id.vibrate:
			mMInfo.setVibrate(MyUtils.getToogleImageStatus(ivVibrate)?false:true);
			MyUtils.setToogleImageStatus(ivVibrate, mMInfo.isVibrate());
			break;
		case R.id.ok:
			handleMonitor();
			break;
		}
	}
	public void showTrainNamesSelect() {
		String strNagative = mMTrainsSelectAllStatus==1?"全选":"全不选";
		MultiChoiceFragment.show(this, REQUEST_MONITOR_TRAINS, "监控车次选择", "确定", strNagative, mMTrainNames, mSelectedTrainNames);
	}
	private void handleMonitor() {
		if (mMInfo.isRunning()){
			mMInfo.setRunning(false);
			mMInfo.setStatus(BgdService2.STATUS_STOPED);
			mMInfo.setStartMonitorTime("");
		}else{
			if (A6Util.isNeedCheckUser(mBInfo)){
				//直接让登录
				startActivity(new Intent(this, A6LoginAty.class));
				return;
			}
			mMInfo.setRunning(true);
			mMInfo.setStatus(BgdService2.STATUS_RUNNING);
			mMInfo.setStartMonitorTime(TimeUtil.getTFormat().format(new Date()));
		}
		boolean hasMTrain = false;
		for(boolean b:mSelectedTrainNames){
			if (b){
				hasMTrain = true;
				break;
			}
		}
		mMInfo.setSelectedTrainsNames(mSelectedTrainNames);
		if (!hasMTrain){
			showMsg("请先添加监控车次"+SF.FAIL);
			return;
		}
		
		if (mMInfo.getLstSeatTypes() != null){
			mMInfo.getLstSeatTypes().clear();
		}else{
			mMInfo.setLstSeatTypes(new ArrayList<Integer>());
		}
		SparseArray<String> saSeatTypes = SeatHelper.getSeatTypes();
		if (mSelectedSeatTypes == null){
			return;
		}
		for(int i=0; i<mSelectedSeatTypes.length; i++){
			if (mSelectedSeatTypes[i]){
				mMInfo.getLstSeatTypes().add(saSeatTypes.keyAt(i));
			}
		}
		if (mMInfo.getLstSeatTypes().isEmpty()){
			showMsg("请先添加座位类别"+SF.FAIL);
			return;
		}
		
		if (mMInfo.getLstPNativeIndexes() != null){
			mMInfo.getLstPNativeIndexes().clear();
		}else{
			mMInfo.setLstPNativeIndexes(new ArrayList<Integer>());
		}
		if (mSelectedPNames == null){
			return;
		}
		for(int i=0; i<mSelectedPNames.length; i++){
			if (mSelectedPNames[i]){
				mMInfo.getLstPNativeIndexes().add(mPNativeIndexes[i]);
			}
		}
		if (mMInfo.getLstPNativeIndexes().isEmpty()){
			showMsg("请先添加乘客"+SF.FAIL);
			return;
		}else if (mMInfo.getLstPNativeIndexes().size() > MAX_P_INFO_NUM){
			showDlg("为提高抢票成功率，最多可添加"+MAX_P_INFO_NUM+"个乘车人"+SF.TIP);
			return;
		}
		
		String strPath = MyApp.getInstance().getPathBaseRoot(StoreValue.MONITOR_INFOS_FILE);
		@SuppressWarnings("unchecked")
		List<MonitorInfo> lstMInfos = (List<MonitorInfo>) PersistentUtil.readObject(strPath);
		if (lstMInfos == null){
			if (mOperateType == EXTRA_OPERATE_EDIT){
				showMsg("抢票信息保存失败"+SF.FAIL);
			}
			lstMInfos = new ArrayList<MonitorInfo>();
		}
		if (mOperateType == EXTRA_OPERATE_EDIT){
			for(int i=0; i<lstMInfos.size(); i++){
				if (lstMInfos.get(i).getNativeIndex() == mMInfo.getNativeIndex()){
					lstMInfos.set(i, mMInfo);
					break;
				}
			}
		}else{
			 if (lstMInfos.size() >= 8){
				 String strMsg = "为您移动设备的耗电量及消耗流量的考虑，抢票监控最多可添加8条，如您想"
					+"继续添加监控，请删除原有监控后再添加"+SF.TIP;
				SimpleDialogFragment.createBuilder(this, getSupportFragmentManager())
				 	.setCancelable(false)
				 	.setRequestCode(REQUEST_MIX_LITMIT_TIP)
				 	.setTitle("提示")
				 	.setMessage(strMsg)
				 	.setPositiveButtonText("确定")
				 	.show();
			 }
			int nativeIndex = lstMInfos.size()==0?1:(lstMInfos.get(lstMInfos.size()-1).getNativeIndex()+1);
			mMInfo.setNativeIndex(nativeIndex);
			lstMInfos.add(mMInfo);
		}
		if(!PersistentUtil.writeObject(lstMInfos, strPath)){
			showMsg("抢票信息保存失败"+SF.FAIL);
		}else{
			setResult(RESULT_OK);
			if (mOperateType == EXTRA_OPERATE_ADD){
				showMsg("抢票信息已保存" + SF.SUCCESS);
				startActivity(new Intent(this, MonitorMangAty.class));
			}
			this.finish();
		}
	}
	private void setViewEnableStatus(boolean b) {
		for(View v:mLstViews){
			v.setEnabled(b);
		}
	}
	@Override
	public void onMultiChoiceItemSelected(View v, int requestCode, int which,
			boolean isChecked) {
		switch(requestCode){
		case REQUEST_MONITOR_TRAINS:
			mSelectedTrainNames[which] = isChecked;
			break;
		case REQUEST_SEAT_TYPE:
			mSelectedSeatTypes[which] = isChecked;
			break;
		case REQUEST_P_INFO:
			mSelectedPNames[which] = isChecked;
			break;
		}
	}
	@Override
	public void onMultiChoicePositiveButtonClicked(int requestCode) {
		switch(requestCode){
		case REQUEST_MONITOR_TRAINS:
			setMontiorTrainsText();
			break;
		case REQUEST_SEAT_TYPE:
			setSeatTypeText();
			break;
		case REQUEST_P_INFO:
			setPInfosText();
			break;
		}
	}
	@Override
	public void onMultiChoiceNagativeButtonClicked(int requestCode) {
		switch(requestCode){
		case REQUEST_MONITOR_TRAINS:
			if (mMTrainsSelectAllStatus==1){
				for(int i=0;i<mSelectedTrainNames.length; i++){
					mSelectedTrainNames[i] = true;
				}
				mMTrainsSelectAllStatus = 0;
				showTrainNamesSelect();
			}else{
				for(int i=0;i<mSelectedTrainNames.length; i++){
					mSelectedTrainNames[i] = false;
				}
				mMTrainsSelectAllStatus = 1;
				showTrainNamesSelect();
			}
			break;
		case REQUEST_SEAT_TYPE:
			break;
		case REQUEST_P_INFO:
			startActivity(new Intent(this, PassengerMangAty.class));
			break;
		}
	}
	@Override
	public void onListItemSelected(int requestCode, String key, int number) {
		switch (requestCode) {
		case REQUEST_MONITOR_SPEED:
			btnSpeed.setText(key);
			mMInfo.setMonitorSpeed(mMSpeedValues[number]);
			break;
		}
	}
	@Override
	public void onPositiveButtonClicked(int requestCode) {
		switch(requestCode){
		case REQUEST_MIX_LITMIT_TIP:
			this.finish();
			break;
		}
	}
	@Override
	public void onNegativeButtonClicked(int requestCode) {
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
