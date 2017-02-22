package com.sshine.huochexing.trainSchedule;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Html;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.sshine.huochexing.R;
import com.sshine.huochexing.adapter.TrainSchListAdapter;
import com.sshine.huochexing.base.BaseAty;
import com.sshine.huochexing.bean.A6Info;
import com.sshine.huochexing.bean.BookingInfo;
import com.sshine.huochexing.bean.MonitorInfo;
import com.sshine.huochexing.bean.QueryLeftNewDTOInfo;
import com.sshine.huochexing.bean.QueryLeftNewInfo;
import com.sshine.huochexing.bean.QueryLeftNewOptionInfo;
import com.sshine.huochexing.bean.SeatInfo;
import com.sshine.huochexing.bean.Train;
import com.sshine.huochexing.bean.TrainAvgLTInfo;
import com.sshine.huochexing.listener.IFavoriteCharacterDialogListener;
import com.sshine.huochexing.model.CustomDialog;
import com.sshine.huochexing.model.FavoriteCharacterDialogFragment;
import com.sshine.huochexing.ticketOnline.A6LoginAty;
import com.sshine.huochexing.ticketOnline.A6OrderAty;
import com.sshine.huochexing.ticketOnline.ConfirmPassengerAty;
import com.sshine.huochexing.ticketOnline.EditMonitorAty;
import com.sshine.huochexing.trainInfos.AddInfoAty;
import com.sshine.huochexing.trainInfos.TrainDetailAty;
import com.sshine.huochexing.utils.A6Util;
import com.sshine.huochexing.utils.HttpUtil;
import com.sshine.huochexing.utils.L;
import com.sshine.huochexing.utils.MyApp;
import com.sshine.huochexing.utils.MyTask;
import com.sshine.huochexing.utils.SeatHelper;
import com.sshine.huochexing.utils.TimeUtil;
import com.sshine.huochexing.utils.TrainHelper;
import com.sshine.huochexing.utils.VoidAsyncTask;
import com.sshine.huochexing.value.SF;
import com.sshine.huochexing.value.TT;
import com.umeng.analytics.MobclickAgent;

import eu.inmite.android.lib.dialogs.ISimpleDialogListener;

public class TrainSchListAty extends BaseAty implements
		View.OnClickListener, ISimpleDialogListener,
		IFavoriteCharacterDialogListener, OnTouchListener {
	/**
	 * 获取平均晚点
	 * 请求json:{"requestType":"getStationAvgLateTimes",data:[{"index":"1",
	 * "trainNum":"","fromStation":"", "toStation":""}]}
	 * 返回json:{"resultCode":"1",data:[{"index":"1","d_LateTime":"",
	 * "a_LateTime":""}]}
	 * 
	 * 请求json:{"requestType":"getLeftNewInfos","from_time":"","from_station":"","to_station":"","purpose_codes":""}
	 * 返回由12306决定
	 */
	private String strUrl = "http://huochexing.duapp.com/server/train_schedule.php";

	public static final String EXTRA_OPTION_INFO = "extraOptionInfo";
	private static final int REQUEST_SHOW_EMPTY = 1;
	private static final int REQUEST_SELECT_MORE = 2;
	private static final int REQUEST_LOGIN = 3;
	
	private String[] mMoreSortText = { "出发平均晚点", "到站平均晚点", "有卧铺优先", "有座位优先", "有优惠车票优先", "可预订车次优先"};
	private String[] mMoreSortText1 = { "出发平均晚点", "到站平均晚点", "有卧铺优先", "有座位优先" ,"有优惠车票优先", "可预订车次优先"};   //可能加标识
	private String mASCText = "(升序)";
	private String mDESCText = "(降序)";
	
	private ImageView ivPrev, ivNext;
	private Button btnCurrDate;
	private ListView lvTrains = null;
	private LinearLayout llytOperatePanel;
	protected int intCurrDataPos;
	private QueryLeftNewOptionInfo mQLNOInfo = null;
	private Gson mGson = new GsonBuilder()
			.excludeFieldsWithoutExposeAnnotation().create();
	private List<QueryLeftNewInfo> mLstInfos;
	private TrainSchListAdapter mAdapter;
	private Button btnSpeed, btnFromTime, btnToTime, btnTotalTime, btnMore;
	private TrainSchComparatorHelper mTSCHelper = new TrainSchComparatorHelper();
	// key为id
	private SparseBooleanArray mSBASortStatus = new SparseBooleanArray();
	// key为index
	private SparseBooleanArray mSBAMoreSortStatus = new SparseBooleanArray();
	private Calendar c;

	private GestureDetector mGestureDetector;
	private BookingInfo mBInfo = MyApp.getInstance().getCommonBInfo();
	private RelativeLayout mContainer;
	private View mFooterLoadingView;
	
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler(){
		@SuppressWarnings("unchecked")
		public void handleMessage(android.os.Message msg) {
			switch(msg.what){
			case A6Util.MSG_WAIT_HANDLE_ORDERS:
				CustomDialog dlg = new CustomDialog.Builder(TrainSchListAty.this,
					new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							switch(which){
							case AlertDialog.BUTTON_POSITIVE:
								startActivity(new Intent(TrainSchListAty.this, A6OrderAty.class));
								break;
							case AlertDialog.BUTTON_NEGATIVE:
								break;
							}
						}
					}).setTitle("提示")
					.setCancelable(false)
					.setMessage("您有未处理的订单，12306规定必须处理完订单才能购票，是否立即查看未处理的订单?")
					.setPositiveButton("是")
					.setNagativeButton("否")
					.create();
				dlg.show();
				break;
			case A6Util.MSG_QUERY_TICKETS_SUCCESS:
				//加载完首次数据再添加滑动监听器
				mGestureDetector = new GestureDetector(TrainSchListAty.this, onGestureListener);
				lvTrains.setOnTouchListener(TrainSchListAty.this);
				setOperatePanelEnable(true);
				showContentView();
				A6Info<List<QueryLeftNewInfo>> a6Info = null;
				if (msg.obj != null){
					a6Info = (A6Info<List<QueryLeftNewInfo>>)msg.obj;
				}
				if (mLstInfos == null){
					mLstInfos = new ArrayList<QueryLeftNewInfo>();
				}else{
					mLstInfos.clear();
				}
				if (a6Info == null || !a6Info.isStatus()){
					showMsg("请求数据失败");
					if (mAdapter != null){
						mAdapter.notifyDataSetChanged();
					}
					break;
				}
				//操作太多，延迟取消对话框
				if (a6Info != null && a6Info.getData() != null){
					for(QueryLeftNewInfo qlnInfo:a6Info.getDataObject()){
						mLstInfos.add(qlnInfo);
					}
				}
				if (mLstInfos == null || mLstInfos.size() == 0) {
					showEmptyTip();
				} else {
					filterTrainInfos();
					bindListData();
					//默认以出发时间排序
					doSort(R.id.from, false);
					setLateTimeInfos();
					if (mLstInfos.size() == 0){
						showEmptyTip();
					}
				}
				break;
			case A6Util.MSG_SHOW_TICKET_PRICES:
				showDlg((CharSequence)msg.obj);
				break;
			}
		}

		public void showEmptyTip() {
			showMsg("没有符合条件的车次哦"+SF.FAIL);
			if (mAdapter != null){
				mAdapter.notifyDataSetChanged();
			}
		}
	};

	private boolean mShouldTipSort = true;
	
	protected void onCreate(android.os.Bundle savedInstanceState) {
		setDisableLoadingView(true);
		super.onCreate(savedInstanceState);
		setTitle("车票预订");
		setContentView(R.layout.aty_trainsch_list);
		initViews();
	};
	
	private void startConfirmAty(){
		QueryLeftNewInfo qlnInfo1 = mLstInfos.get(intCurrDataPos);
		QueryLeftNewDTOInfo qlndInfo = qlnInfo1.getQueryLeftNewDTO();
		String secretStr = A6Util.decode(qlnInfo1.getSecretStr());
		mBInfo.setSecretStr(secretStr);
		mBInfo.setTrain_date(mQLNOInfo.getDeparture_time());
		mBInfo.setBack_train_date(mQLNOInfo.getDeparture_time());
		mBInfo.setTour_flag(mQLNOInfo.getTour_flag());
		mBInfo.setPurpose_codes(mQLNOInfo.getTicket_type());
		mBInfo.setQuery_from_station_name(qlndInfo.getFrom_station_name());
		mBInfo.setQuery_to_station_name(qlndInfo.getTo_station_name());
		
		Intent intentConfirm = new Intent(TrainSchListAty.this, ConfirmPassengerAty.class);
		intentConfirm.putExtra(ConfirmPassengerAty.EXTRA_TRAIN_INFO, qlndInfo);
		Map<String, String> map1 = TT.getTour_flags();
		intentConfirm.putExtra(ConfirmPassengerAty.EXTRA_TOUR_FLAG, (mQLNOInfo.getReturn_time() == null?map1.get("dc"):map1.get("wc")));
		startActivity(intentConfirm);
	}

	public void initViews() {
		mQLNOInfo = (QueryLeftNewOptionInfo) this.getIntent()
				.getSerializableExtra(EXTRA_OPTION_INFO);
		if (mQLNOInfo == null){
			showMsg("信息有误，请重试");
			return;
		}
		
		mFooterLoadingView = LayoutInflater.from(this).inflate(R.layout.footer_loading_view, null);
		mFooterLoadingView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		mContainer = (RelativeLayout)findViewById(R.id.container);
		
		ivPrev = (ImageView) findViewById(R.id.back);
		ivPrev.setOnClickListener(this);
		ivNext = (ImageView) findViewById(R.id.forward);
		ivNext.setOnClickListener(this);
		btnCurrDate = (Button) findViewById(R.id.date);
		btnCurrDate.setOnClickListener(this);
		lvTrains = (ListView) findViewById(R.id.lv1);
		llytOperatePanel = (LinearLayout)findViewById(R.id.llyt3);
		btnSpeed = (Button) findViewById(R.id.speed);
		btnSpeed.setOnClickListener(this);
		btnFromTime = (Button) findViewById(R.id.from);
		btnFromTime.setOnClickListener(this);
		btnToTime = (Button) findViewById(R.id.to);
		btnToTime.setOnClickListener(this);
		btnTotalTime = (Button) findViewById(R.id.totalTime);
		btnTotalTime.setOnClickListener(this);
		btnMore = (Button) findViewById(R.id.more);
		btnMore.setOnClickListener(this);

		String strDate = mQLNOInfo.getDeparture_time();
		btnCurrDate.setText(strDate + " " + TimeUtil.getWeek(strDate));
		lvTrains.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				intCurrDataPos = position;
				//取得对应对象
				QueryLeftNewDTOInfo qlndInfo = ((QueryLeftNewInfo)mLstInfos.get(position)).getQueryLeftNewDTO();
				if (!A6Util.isCanBooking()){
					showDlg("23:00-07:00为12306系统维护时间，此段时间内不能购票哦" + SF.TIP);
					return;
				}
				if (!qlndInfo.getCanWebBuy().equals("Y")){
					showMsg("此趟车次不能预订" + SF.TIP);
					return;
				}
				checkUser();
			}
		});
		// 右键菜单
		lvTrains.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {

			@Override
			public void onCreateContextMenu(ContextMenu menu, View v,
					ContextMenuInfo menuInfo) {
				intCurrDataPos = ((AdapterContextMenuInfo) menuInfo).position;
				menu.setHeaderTitle("快捷操作");
				menu.add(0, 0, 0, "添加车次");
				menu.add(0, 1, 1, "查看车次详情");
			}
		});
		mSBASortStatus.put(R.id.speed, true);
		mSBASortStatus.put(R.id.from, false);
		mSBASortStatus.put(R.id.to, false);
		mSBASortStatus.put(R.id.totalTime, false);
		mSBAMoreSortStatus.put(0, false);
		mSBAMoreSortStatus.put(1, false);
		mSBAMoreSortStatus.put(2, true);
		mSBAMoreSortStatus.put(3, true);

		c = Calendar.getInstance();
		try {
			c.setTime(TimeUtil.getDFormat().parse(mQLNOInfo.getDeparture_time()));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		loadData();
	}

	private void checkUser(){
		if (!A6Util.isNeedCheckUser(mBInfo)){
			startConfirmAty();
			return;
		}
		startHandle("检测登录状态...", new Runnable(){
			@Override
			public void run() {
				boolean isLogin = A6Util.checkUser(mBInfo);
				sendDismissDialog();
				if (isLogin){
					startConfirmAty();
				}else{
					Intent intent1 = new Intent(TrainSchListAty.this, A6LoginAty.class);
					startActivityForResult(intent1, REQUEST_LOGIN);
					sendToast("登录信息已过期，请登录");
				}
			}
		});
	}

	private void loadData() {
		if (mLstInfos != null){
			mLstInfos.clear();
		}else{
			mLstInfos = new ArrayList<QueryLeftNewInfo>();
		}
		if (mAdapter != null){
			mAdapter.notifyDataSetChanged();
		}
		mContainer.removeView(mFooterLoadingView);
		mContainer.addView(mFooterLoadingView);
		setOperatePanelEnable(false);
		new VoidAsyncTask() {
			
			@Override
			protected Object doInBackground(Object... params) {
				HttpUtil httpUtil = new HttpUtil();
				JSONObject jsonObj = new JSONObject();
				try {
					jsonObj.put("requestType", "getLeftNewInfos");
					jsonObj.put("from_time", mQLNOInfo.getDeparture_time());
					jsonObj.put("from_station", mQLNOInfo.getFrom_station_telecode());
					jsonObj.put("to_station", mQLNOInfo.getTo_station_telecode());
					jsonObj.put("purpose_codes", mQLNOInfo.getTicket_type());
					httpUtil.post(strUrl, jsonObj.toString()); //上传信息到服务器
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}
		}.start();
		mBInfo.setTrain_date(mQLNOInfo.getDeparture_time());
		mBInfo.setFrom_station(mQLNOInfo.getFrom_station_telecode());
		mBInfo.setTo_station(mQLNOInfo.getTo_station_telecode());
		mBInfo.setPurpose_codes(mQLNOInfo.getTicket_type());
		boolean b = startHandle(new Runnable(){
			String currTrain_date = mBInfo.getTrain_date();
			@Override
			public void run() {
				A6Info<List<QueryLeftNewInfo>> a6Info = A6Util.queryTickets(mBInfo);
					if (!currTrain_date.equals(mBInfo.getTrain_date())){
						return;
					}
					
					Message msg = mHandler.obtainMessage();
					msg.what = A6Util.MSG_QUERY_TICKETS_SUCCESS;
					msg.obj = a6Info;
					mHandler.sendMessage(msg);
			};
		});
		if(!b){
			showContentView();
		}
	}

	/**
	 * 设置是否禁用底层操作面板
	 * @param isEnable
	 */
	private void setOperatePanelEnable(boolean isEnable) {
		if (llytOperatePanel != null){
			for(int i=0; i<llytOperatePanel.getChildCount();i++){
				View v = llytOperatePanel.getChildAt(i);
				v.setEnabled(isEnable);
			}
		}
	}
	// 过滤车次
	protected void filterTrainInfos() {
		boolean[] selectedIndexes = (boolean[]) mQLNOInfo
				.getSelectedTrainTypeIndexes();
		int selectedCount = 0;
		for (int i = 0; i < selectedIndexes.length; i++) {
			if (selectedIndexes[i]) {
				selectedCount++;
			}
		}
		TrainHelper tHelper = new TrainHelper();
		boolean isFilterTrainType = selectedCount != tHelper.getTrainTypes().size();
		for (int i = mLstInfos.size() - 1; i >= 0; i--) {
			QueryLeftNewDTOInfo qldInfo = mLstInfos.get(i).getQueryLeftNewDTO();
			if (mQLNOInfo.isFromExactMatch()
					&& (!qldInfo.getFrom_station_name().equals(
							mQLNOInfo.getFrom_station_name()))) {
				mLstInfos.remove(i);
				continue;
			} else if (mQLNOInfo.isToExactMatch()
					&& (!qldInfo.getTo_station_name().equals(
							mQLNOInfo.getTo_station_name()))) {
				mLstInfos.remove(i);
				continue;
			} else if (mQLNOInfo.getReturn_time() != null
					&& qldInfo.getDay_difference() != mQLNOInfo
							.getDay_difference()) {
				mLstInfos.remove(i);
				continue;
			}
			if (!mQLNOInfo.getFrom_time_range().equals(
					TT.TIME_RANGE_VALUES[0])) {
				String[] strTimeRanges = mQLNOInfo.getFrom_time_range().split(
						",");
				if (qldInfo.getStart_time().compareTo(strTimeRanges[0]) < 0
						|| qldInfo.getStart_time().compareTo(strTimeRanges[1]) > 0) {
					mLstInfos.remove(i);
					continue;
				}
			}
			if (!mQLNOInfo.getTo_time_range().equals(
					TT.TIME_RANGE_VALUES[0])) {
				String[] strTimeRanges = mQLNOInfo.getTo_time_range().split(",");
				if (qldInfo.getArrive_time().compareTo(strTimeRanges[0]) < 0
						|| qldInfo.getArrive_time().compareTo(strTimeRanges[1]) > 0) {
					mLstInfos.remove(i);
					continue;
				}
			}
			
			if (isFilterTrainType) {
				boolean isMatchCondition = false, hasMatchTrain = false;
				if (qldInfo.getSpeed_index() != -1) {
					hasMatchTrain = true;
				}
				SparseArray<String> saTrainTypes = tHelper.getTrainTypes();
				for (int j = 0; j < selectedIndexes.length - 1; j++) {
					if (selectedIndexes[j]) {
						boolean isMatchTrain = qldInfo.getStation_train_code()
								.startsWith(saTrainTypes.valueAt(j));
						if (isMatchTrain) {
							// 有符合的车次后就不再寻找
							isMatchCondition = true;
							break;
						}
					}
				}
				// 不是其它
				if (!isMatchCondition) {
					boolean isQTSelected = selectedIndexes[selectedIndexes.length - 1];
					if (!(selectedCount == 1 && !hasMatchTrain && isQTSelected)) {
						mLstInfos.remove(i);
					}
				}
			}
		}
	}

	// 异步设置晚点信息
	protected void setLateTimeInfos() {
		if (mLstInfos == null || mLstInfos.size() == 0){
			return;
		}
		new MyTask(TrainSchListAty.this) {
			String currTrain_date = mBInfo.getTrain_date();
			@Override
			protected void onException(Exception e) {
				if (!currTrain_date.equals(mBInfo.getTrain_date())){
					return;
				}
				showMsg("获取晚点信息失败" + SF.FAIL);
				mShouldTipSort = false;
			}

			@Override
			protected void myOnPostExecute(Object result) {
				if (!currTrain_date.equals(mBInfo.getTrain_date())){
					return;
				}
				if (result == null) {
					showMsg("12306比较忙" + SF.FAIL);
				} else {
					List<TrainAvgLTInfo> lstALTInfo = mGson.fromJson(
							(String) result,
							new TypeToken<List<TrainAvgLTInfo>>() {
							}.getType());
					if (lstALTInfo == null){
						showMsg("请求数据时出错"+SF.FAIL);
						return;
					}
					for (int i = 0; i < mLstInfos.size(); i++) {
						if (lstALTInfo.size() <= i){
							break;
						}
						TrainAvgLTInfo taltInfo = lstALTInfo.get(i);
						QueryLeftNewDTOInfo qlndInfo = findQLNDInfo(taltInfo.getTrainNum());
						if (qlndInfo != null){
							qlndInfo.setD_LateTime(taltInfo.getD_LateTime());
							qlndInfo.setA_LateTime(taltInfo.getA_LateTime());
						}
					}
					mAdapter.notifyDataSetChanged();
				}
				mShouldTipSort = false;
			}

			@Override
			protected Object myDoInBackground(Object... params)
					throws Exception {
				HttpUtil httpUtil = new HttpUtil();
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("requestType", "getStationAvgLateTimes");
				JSONArray jsonArray = new JSONArray();
				for (int i = 0; i < mLstInfos.size(); i++) {
					QueryLeftNewDTOInfo qldInfo = mLstInfos.get(i)
							.getQueryLeftNewDTO();
					JSONObject jo1 = new JSONObject();
					jo1.put("trainNum", qldInfo.getStation_train_code());
					jo1.put("fromStation", qldInfo.getFrom_station_name());
					jo1.put("toStation", qldInfo.getTo_station_name());
					jsonArray.put(jo1);
				}
				jsonObj.put("data", jsonArray);
				if (httpUtil.post(strUrl, jsonObj.toString())) {
					return httpUtil.getResponseStr();
				} else {
					return null;
				}
			}
		}.execute(TrainSchListAty.this);

	}

	private QueryLeftNewDTOInfo findQLNDInfo(String trainNum) {
		for(QueryLeftNewInfo qlnInfo:mLstInfos){
			QueryLeftNewDTOInfo qlndInfo = qlnInfo.getQueryLeftNewDTO();
			if (qlndInfo.getStation_train_code().equals(trainNum)){
				return qlndInfo;
			}
		}
		return null;
	}
	@Override
	public boolean onContextItemSelected(android.view.MenuItem item) {
		QueryLeftNewDTOInfo qlndInfo = mLstInfos.get(intCurrDataPos).getQueryLeftNewDTO();
		switch (item.getItemId()) {
		case 0:
			 Intent intent = new Intent(TrainSchListAty.this, AddInfoAty.class);
			 intent.putExtra(AddInfoAty.TRAIN_NUM, qlndInfo.getStation_train_code());
			 intent.putExtra(AddInfoAty.START_STATION, qlndInfo.getFrom_station_name());
			 intent.putExtra(AddInfoAty.END_STATION, qlndInfo.getTo_station_name());
			 intent.putExtra(AddInfoAty.DATA_SOURCE, AddInfoAty.DATA_FROM_NEWWORK);
			 startActivity(intent);
			break;
		case 1:
			mBInfo.setTrain_no(qlndInfo.getTrain_no());
			mBInfo.setFrom_station_no(qlndInfo.getFrom_station_no());
			mBInfo.setTo_station_no(qlndInfo.getTo_station_no());
			mBInfo.setSeat_types(qlndInfo.getSeat_types());
			Intent intent1 = new Intent(TrainSchListAty.this,
					TrainDetailAty.class);
			Train train = new Train();
			train.setTrain_no(qlndInfo.getTrain_no());
			train.setTrainNum(qlndInfo.getStation_train_code());
			train.setStartStation(qlndInfo.getFrom_station_name());
			train.setEndStation(qlndInfo.getTo_station_name());
			train.setD_Time(qlndInfo.getStart_time());
			train.setA_Time(qlndInfo.getArrive_time());
			train.setD_LateTime(String.valueOf(qlndInfo.getD_LateTime()));
			train.setA_LateTime(String.valueOf(qlndInfo.getA_LateTime()));
			train.setR_Date(qlndInfo.getLishi());
			intent1.putExtra(TrainDetailAty.TRAIN, train);
			startActivity(intent1);
			break;
		}
		return super.onContextItemSelected(item);
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

	public void showMsg(final CharSequence csMsg) {
		Thread action = new Thread() {
			public void run() {
				Looper.prepare();
				Toast.makeText(TrainSchListAty.this, csMsg, Toast.LENGTH_SHORT)
						.show();
				Looper.loop();
			}
		};
		action.start();
	}


	private void bindListData() {
		TrainHelper tHelper = new TrainHelper();
		for(QueryLeftNewInfo qlnInfo:mLstInfos){
			QueryLeftNewDTOInfo qlndInfo = qlnInfo.getQueryLeftNewDTO();
			//不显示，只为取值
			SeatHelper.setPreferentialPriceFlag(qlndInfo);
			qlndInfo.setSpeed_index(tHelper.getTrainSpeedIndex(qlndInfo
					.getStation_train_code()));
		}
		if (mAdapter == null){
			mAdapter = new TrainSchListAdapter(this, mLstInfos);
			lvTrains.setAdapter(mAdapter);
		}else{
			mAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onPositiveButtonClicked(int requestCode) {
		switch(requestCode){
		case REQUEST_SHOW_EMPTY:
			finish();
			break;
		case REQUEST_SELECT_MORE:
			break;
		}
	}

	@Override
	public void onNegativeButtonClicked(int requestCode) {
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			reloadData(-1);
			break;
		case R.id.date:
			try{
				new DatePickerDialog(this, new OnDateSetListener() {
	
					@Override
					public void onDateSet(DatePicker view, int year,
							int monthOfYear, int dayOfMonth) {
						if (c == null){
							return;
						}
						String tempDate = year + "-" + (monthOfYear + 1) + "-"
								+ dayOfMonth;
						try {
							Date date = TimeUtil.getDFormat().parse(tempDate);
							c.setTime(date);
							String currDate = TimeUtil.getDFormat().format(new Date());
							btnCurrDate.setText(TimeUtil.getDFormat().format(date) + TimeUtil.getWeek(date));
							mQLNOInfo.setDeparture_time(TimeUtil.getDFormat().format(date));
							if (tempDate.compareTo(currDate) < 0) {
								showMsg("时间太早了" + SF.FAIL);
								if (mLstInfos != null){
									mLstInfos.clear();
								}
								if (mAdapter != null){
									mAdapter.notifyDataSetChanged();
								}
							} else {
								loadData();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}, c.get(Calendar.YEAR), c.get(Calendar.MONTH),
						c.get(Calendar.DAY_OF_MONTH)).show();
			}catch(Exception e){
				e.printStackTrace();
			}
			break;
		case R.id.forward:
			reloadData(1);
			break;
		case R.id.more:
			FavoriteCharacterDialogFragment.show(this, REQUEST_SELECT_MORE,
					"更多排序选项", mMoreSortText1);
			break;
		default:
			doSort(v.getId(), false);
			break;
		}
	}

	private void reloadData(int addDay) {
		c.add(Calendar.DAY_OF_MONTH, addDay);
		String tempDate = TimeUtil.getDFormat().format(c.getTime());
		String currDate = TimeUtil.getDFormat().format(new Date());
		mQLNOInfo.setDeparture_time(tempDate);
		btnCurrDate.setText(tempDate + " " + TimeUtil.getWeek(c.getTime()));
		if (tempDate.compareTo(currDate) < 0 || mLstInfos == null) {
			showMsg("时间太早了" + SF.FAIL);
			if (mLstInfos != null){
				mLstInfos.clear();
			}
			if (mAdapter != null){
				mAdapter.notifyDataSetChanged();
			}
		} else {
			loadData();
		}
	}

	private void doSort(final int id, final boolean isMoreSort) {
		if (mLstInfos == null || mLstInfos.size() == 0){
			return;
		}
		final Drawable dUp = getResources().getDrawable(R.drawable.arrow_up);
		final Drawable dDown = getResources()
				.getDrawable(R.drawable.arrow_down);
		final Button btn1;
		final Comparator<QueryLeftNewInfo> comparator;
		final boolean isASC;
		if (isMoreSort) {
			isASC = mSBAMoreSortStatus.get(id);
			btn1 = null;
			switch (id) {
			case 0:
				comparator= mTSCHelper.getD_late_timeComparator(!isASC);
				break;
			case 1:
				comparator = mTSCHelper.getA_late_timeComparator(!isASC);
				break;
			case 2:
				comparator = mTSCHelper.getWPComparator();
				break;
			case 3:
				comparator = mTSCHelper.getZWComparator();
				break;
			case 4:
				comparator = mTSCHelper.getPreferentialPriceComparator();
				break;
			case 5:
				comparator = mTSCHelper.getCanWebBuyComparator();
				break;
			default:
				return;
			}
		} else {
			isASC = mSBASortStatus.get(id);
			switch (id) {
			case R.id.speed:
				btn1 = btnSpeed;
				comparator = mTSCHelper.getSpeedComparator(!isASC);
				break;
			case R.id.from:
				btn1 = btnFromTime;
				comparator = mTSCHelper.getFromTimeComparator(!isASC);
				break;
			case R.id.to:
				btn1 = btnToTime;
				comparator = mTSCHelper.getToTimeComparator(!isASC);
				break;
			case R.id.totalTime:
				btn1 = btnTotalTime;
				comparator = mTSCHelper.getLISHIComparator(!isASC);
				break;
			default:
				return;
			}
		}
		String strSortTip = null;
		//未请求完平均晚点前点排序需显示进度框
		if (mShouldTipSort){
			strSortTip = "排序中...";
		}else{
			strSortTip = null;
		}
		new MyTask(this, strSortTip, false) {

			@Override
			protected void onException(Exception e) {
				e.printStackTrace();
				showMsg("排序时出错了" + SF.FAIL);
			}

			@Override
			protected void myOnPostExecute(Object result) {
				if (result == null) {
					showMsg("排序时出错了1" + SF.FAIL);
				} else {
					int indexOfKey = -1;
					if (isMoreSort) {
						if (id == 0 || id == 1) {
							resetMoreSortText();
							if (isASC) {
								mSBAMoreSortStatus.put(id, false);
								mMoreSortText1[id] = mMoreSortText[id] + mDESCText;
								showMsg(Html
										.fromHtml("已按&nbsp;<font color='#ff8c00'>"
												+ mMoreSortText[id]
												+ "</font>&nbsp;<b>降序</b>&nbsp;排列"));
							} else {
								mSBAMoreSortStatus.put(id, true);
								mMoreSortText1[id] = mMoreSortText[id] + mASCText;
								showMsg(Html
										.fromHtml("已按&nbsp;<font color='#ff8c00'>"
												+ mMoreSortText[id]
												+ "</font>&nbsp;<b>升序</b>&nbsp;排列"));
							}
						} else {
							showMsg(Html
									.fromHtml("已按&nbsp;<font color='#ff8c00'>"
											+ mMoreSortText[id]
											+ "</font>&nbsp;排列"));
						}
					} else {
						if (isASC) {
							mSBASortStatus.put(id, false);
							// / 这一步必须要做,否则不会显示.
							dDown.setBounds(0, 0, dUp.getMinimumWidth(),
									dUp.getMinimumHeight());
							btn1.setCompoundDrawables(null, null, dDown, null);
						} else {
							mSBASortStatus.put(id, true);
							// / 这一步必须要做,否则不会显示.
							dUp.setBounds(0, 0, dDown.getMinimumWidth(),
									dUp.getMinimumHeight());
							btn1.setCompoundDrawables(null, null, dUp, null);
						}
						// 清除其它按钮的排序图标
						indexOfKey = mSBASortStatus.indexOfKey(id);
					}
					for (int i = 0; i < mSBASortStatus.size(); i++) {
						if (i != indexOfKey) {
							Button btn2 = (Button) findViewById(mSBASortStatus
									.keyAt(i));
							btn2.setCompoundDrawables(null, null, null, null);
						}
					}
					mAdapter.notifyDataSetChanged();
				}
			}

			@Override
			protected Object myDoInBackground(Object... params)
					throws Exception {
				Collections.sort(mLstInfos, comparator);
				return "";
			}
		}.execute(this);
	}

	/**
	 * 将升序、降序字段去除
	 */
	protected void resetMoreSortText() {
		for(int i=0; i<mMoreSortText1.length; i++){
			mMoreSortText1[i] = mMoreSortText1[i].replace(mASCText, "");
			mMoreSortText1[i] = mMoreSortText1[i].replace(mDESCText, "");
		}
	}

	@Override
	public void onListItemSelected(int requestCode, String key, int number) {
		switch (requestCode) {
		case REQUEST_SELECT_MORE:
			doSort(number, true);
			break;
		}
	}
	@Override  
    public boolean onTouch(View v, MotionEvent event) {  
         return mGestureDetector.onTouchEvent(event);   
    }
    
    private GestureDetector.OnGestureListener onGestureListener = new GestureDetector.SimpleOnGestureListener() {
    	private static final int FLING_MIN_DISTANCE = 400;  
        private static final int FLING_MIN_VELOCITY = 200;
    	@Override  
	    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,  
	            float velocityY) {
    		if (e1 == null){
    			return false;
    		}
    		float diffX = e1.getX() - e2.getX();
    		float diffY = e1.getY()-e2.getY();
	         if (diffX > FLING_MIN_DISTANCE   
	                    && Math.abs(diffY) <= FLING_MIN_VELOCITY) {   
	        	 L.i("Fling left");
	        	 reloadData(1);
	         } else if ((-diffX) > FLING_MIN_DISTANCE   
	                    && Math.abs(diffY) <= FLING_MIN_VELOCITY) {
	            	L.i("Fling right");
	            	reloadData(-1);  
	         }   
	         return false;   
	    }
	};
	
	public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
		MenuItem miAdd = menu.add("添加");
		miAdd.setIcon(R.drawable.head_add);
		miAdd.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		miAdd.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				if (mLstInfos == null || mLstInfos.size() == 0){
					return false;
				}
				Intent intent = new Intent(TrainSchListAty.this, EditMonitorAty.class);
				intent.putExtra(EditMonitorAty.EXTRA_OPERATE, EditMonitorAty.EXTRA_OPERATE_ADD);
				MonitorInfo mInfo = new MonitorInfo();
				mInfo.setFrom_station_name(mQLNOInfo.getFrom_station_name());
				mInfo.setFrom_station_telecode(mQLNOInfo.getFrom_station_telecode());
				mInfo.setTo_station_name(mQLNOInfo.getTo_station_name());
				mInfo.setTo_station_telecode(mQLNOInfo.getTo_station_telecode());
				mInfo.setStart_time(mQLNOInfo.getDeparture_time());
				mInfo.setPurpose_codes(mQLNOInfo.getTicket_type());
				List<String> lstTrainNames = new ArrayList<String>();
				List<String> lstTrainTimeRanges = new ArrayList<String>();
				for(QueryLeftNewInfo qlnInfo:mLstInfos){
					QueryLeftNewDTOInfo qlndInfo = qlnInfo.getQueryLeftNewDTO();
					lstTrainNames.add(qlndInfo.getStation_train_code());
					lstTrainTimeRanges.add(qlndInfo.getStart_time()+"--"+qlndInfo.getArrive_time());
				}
				mInfo.setLstTrainNames(lstTrainNames);
				mInfo.setLstTrainTimeRanges(lstTrainTimeRanges);
				mInfo.setMonitorSpeed(5000);
				mInfo.setRing(true);
				mInfo.setVibrate(true);
				intent.putExtra(EditMonitorAty.EXTRA_MONITOR_INFO, mInfo);
				startActivity(intent);
				return true;
			}
		});
		MenuItem miDel = menu.add("票价");
    	miDel.setIcon(R.drawable.yuan48);
    	miDel.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    	miDel.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				if (mLstInfos == null || mLstInfos.size() == 0){
					return true;
				}
				startHandle("正在取回票价...", new Runnable(){
					public void run() {
						String strResultText = "";
						List<Map<String, String>> lstTicketTrains = getTicketTrains();
						for(int i=0; i<lstTicketTrains.size(); i++){
							Map<String, String> map = lstTicketTrains.get(i);
							mBInfo.setTrain_no(map.get(TT.TRAIN_NO));
							mBInfo.setFrom_station_no(map.get(TT.FROM_STATION_NO));
							mBInfo.setTo_station_no(map.get(TT.TO_STATION_NO));
							mBInfo.setSeat_types(map.get(TT.SEAT_TYPES));
							List<SeatInfo> lstSeatInfos = A6Util.queryTicketPrice(mBInfo);
							if (lstSeatInfos == null || lstSeatInfos.size() == 0){
								//不报错
								sendDismissDialog();
								return;
							}
							String strPrices = "";
							for(int j=0; j<lstSeatInfos.size(); j++){
								SeatInfo sInfo = lstSeatInfos.get(j);
								strPrices += sInfo.getName() + ":<font color='#ff8c00'>"
									+ sInfo.getPrice() + "</font>&nbsp;&nbsp;&nbsp;&nbsp;";
								if ((j+1)%2 == 0){ 
									strPrices += "<br/>";
								}
							}
							strResultText += "<b>" + map.get(TT.TRAIN_CLASS_NAME)
								+ ":</b><br/>" + strPrices + "<br/><br/>";
						}
						sendDismissDialog();
						Message msg = mHandler.obtainMessage(A6Util.MSG_SHOW_TICKET_PRICES);
						msg.obj = Html.fromHtml(strResultText);
						mHandler.sendMessage(msg);
					};
				});
				return true;
			}
		});
    	
//    	MenuItem miBuyMidWayTicket = menu.add("查询半途票");
//    	miBuyMidWayTicket.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
//    	miBuyMidWayTicket.setOnMenuItemClickListener(new OnMenuItemClickListener() {
//
//			@Override
//			public boolean onMenuItemClick(MenuItem item) {
//				return false;
//			}
//    		
//    	});
    	return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	protected void onActivityResult (int requestCode, int resultCode, Intent data) {
		switch(requestCode){
		case REQUEST_LOGIN:
			if (resultCode == RESULT_OK){
				try{
					startConfirmAty();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			break;
		}
	}

	private List<Map<String, String>> getTicketTrains() {
		List<Map<String, String>> lstTicketTrains = new ArrayList<Map<String, String>>();
		List<Integer> lstTemp = new ArrayList<Integer>();
		TrainHelper tHelper = new TrainHelper();
		SparseIntArray saTrainSpeeds = tHelper.getTrainSpeeds();
		for (int i = 0; i < mLstInfos.size(); i++) {
			QueryLeftNewDTOInfo qldInfo = mLstInfos.get(i)
					.getQueryLeftNewDTO();
			if (!lstTemp.contains(qldInfo.getSpeed_index()) || qldInfo.isHasPreferentialPrice()){
				if (!qldInfo.isHasPreferentialPrice()){
					lstTemp.add(qldInfo.getSpeed_index());
				}
				Map<String, String> map = new HashMap<String, String>();
				map.put(TT.TRAIN_NO, qldInfo.getTrain_no());
				String strName = "";
				for(int j=0; j<saTrainSpeeds.size(); j++){
					if (saTrainSpeeds.valueAt(j) == qldInfo.getSpeed_index()){
						strName = tHelper.getTrainNames().get(saTrainSpeeds.keyAt(j));
						break;
					}
				}
				map.put(TT.TRAIN_CLASS_NAME, qldInfo.isHasPreferentialPrice()?
						("<font color='#ff8c00'>[折]</font>"+qldInfo.getStation_train_code()):strName);
				map.put(TT.FROM_STATION_NO, qldInfo.getFrom_station_no());
				map.put(TT.TO_STATION_NO, qldInfo.getTo_station_no());
				map.put(TT.SEAT_TYPES, qldInfo.getSeat_types());
				lstTicketTrains.add(map);
			}
		}
		return lstTicketTrains;
	};
	public void showContentView() {
		mHandler.post(new Runnable(){
			@Override
			public void run() {
				mContainer.removeView(mFooterLoadingView);
			}
		});
	}
	public void onResume() {
		super.onResume();
		if (mAdapter != null){
			mAdapter.notifyDataSetChanged();
		}
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
