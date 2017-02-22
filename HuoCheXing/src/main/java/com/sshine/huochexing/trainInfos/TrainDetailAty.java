package com.sshine.huochexing.trainInfos;

import java.io.File;
import java.util.List;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sshine.huochexing.R;
import com.sshine.huochexing.adapter.StationAdapter;
import com.sshine.huochexing.base.BaseAty;
import com.sshine.huochexing.bean.BookingInfo;
import com.sshine.huochexing.bean.SeatInfo;
import com.sshine.huochexing.bean.StationInfo;
import com.sshine.huochexing.bean.Train;
import com.sshine.huochexing.bean.TrainDetailPersistentInfo;
import com.sshine.huochexing.listener.OnItemViewClickListener;
import com.sshine.huochexing.utils.A6Util;
import com.sshine.huochexing.utils.HttpUtil;
import com.sshine.huochexing.utils.MyApp;
import com.sshine.huochexing.utils.MyDatabase;
import com.sshine.huochexing.utils.MyTask;
import com.sshine.huochexing.utils.MyUtils;
import com.sshine.huochexing.utils.PersistentUtil;
import com.sshine.huochexing.utils.RegexUtils;
import com.sshine.huochexing.utils.SServiceUtil;
import com.sshine.huochexing.utils.TimeUtil;
import com.sshine.huochexing.utils.VoidAsyncTask;
import com.sshine.huochexing.value.SF;
import com.sshine.huochexing.value.TT;
import com.umeng.analytics.MobclickAgent;

public class TrainDetailAty extends BaseAty implements OnClickListener, OnItemViewClickListener<StationInfo> {
	
	/**
	 * 取得车次详细信息
	 * 请求json:{"requestType":"getTrainForTrainNum","trainNum":"K598"}
	 * 返回json:{"resultCode":"1","train":{"a_LateTime":"15分钟","a_Time":"04:27","d_LateTime":"8分钟","d_Time":"16:32","endStation":"北京","r_Date":"11小时55分","startStation":"包头","trainNum":"1712/1713"}}
	 * s
	* 取得途经车站信息
	* 请求json:{"requestType":"getStations","station_train_code":"D178","train_no":""}
	* 返回json:{"resultCode":"1","stations":[{"station_name":"吉林","start_time":"00:27","arrive_time":"21:36","remain":"00:03"]}
	*/
	private String strUrl = "http://huochexing.duapp.com/server/train_schedule.php";
	public static final String TRAIN = "train";
	private static final int SHOW_TRAIN_VIEW = 1;
	private Train mTrain;
	private ListView lvStations;
	private StationAdapter mAdapter;
	private TextView tvFrom, tvTo, tvD_Time, tvA_Time, tvInfo2;
	private TextView tvD_LateTime,tvA_LateTime,tvTPrices;
	private ScrollView sv1;
	private Button btnD_LateTime, btnA_LateTime;
	private BookingInfo mBInfo = MyApp.getInstance().getCommonBInfo();
	
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch(msg.what){
			case A6Util.MSG_QUERY_TICKET_PRICE_SUCCESS:
				String strMsg = (String)msg.obj;
				setTPricesView(strMsg);
				break;
			case SHOW_TRAIN_VIEW:
				setTrainView(mTrain);
				break;
			}
		}
	};

	protected int intDataSource = AddInfoAty.DATA_FROM_OFFINE_DB;

	private TrainDetailPersistentInfo mTDPInfo;
	//只提醒一次
	protected boolean mTiped = false;
//	private RCodeDialog mLTimeDlg;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.aty_train_detail);
		initContainerViews(R.id.container, R.id.pull_to_refresh_view, R.id.pull_to_refresh_view);
		initViews();
	}
	private void initViews() {
		//存储传到此处的数据.
		mTrain = (Train)getIntent().getSerializableExtra(TRAIN);
		
		sv1 = (ScrollView)findViewById(R.id.sv1);
		tvFrom = (TextView)findViewById(R.id.from);
		tvTo = (TextView)findViewById(R.id.to);
		tvD_Time = (TextView)findViewById(R.id.d_time);
		tvA_Time = (TextView)findViewById(R.id.a_time);
		tvInfo2 = (TextView)findViewById(R.id.info);
		tvD_LateTime = (TextView)findViewById(R.id.d_lateTime1);
		tvA_LateTime = (TextView)findViewById(R.id.a_lateTime1);
		btnD_LateTime = (Button)findViewById(R.id.d_lateTime);
		btnD_LateTime.setOnClickListener(this);
		btnA_LateTime = (Button)findViewById(R.id.a_lateTime);
		btnA_LateTime.setOnClickListener(this);
		tvTPrices = (TextView)findViewById(R.id.prices);
		lvStations = (ListView) findViewById(R.id.lv1);
		
		setTitle(mTrain.getTrainNum());
		mTDPInfo = (TrainDetailPersistentInfo) PersistentUtil.readObject(
				getOfflineFilePath(mTrain.getTrainNum()));
		if (mTDPInfo != null){
			mTrain = mTDPInfo.getTrain();
			setTrainView(mTrain);
			tvTPrices.setVisibility(View.GONE);
			setStationsView(mTDPInfo.getLstSInfos());
			showContentView(true);
		}else{
			mTDPInfo = new TrainDetailPersistentInfo();
			doHeaderTask();
		}
	}
	public void initTrainAndStationsOnline() {
		new MyTask(this, null){
			private List<StationInfo> lstStations;
			
			public void shMsg(String strMsg){
				sendToast(strMsg);
			}
			@Override
			protected Object myDoInBackground(Object... params)
					throws Exception {
				final Context cxt1 = (Context)params[0];
				
				HttpUtil httpUtil = new HttpUtil();
				Gson gson = new Gson();
				if (HttpUtil.isNetworkConnected(cxt1)){
					//查询在线数据
					//如果是车次查询刚从根据车次获取详细信息
					if (mTrain.getStartStation() == null){
						JSONObject jsonObj1 = new JSONObject();
						jsonObj1.put("requestType", "getTrainForTrainNum");
						jsonObj1.put("trainNum", mTrain.getTrainNum());
						if (httpUtil.post(strUrl, jsonObj1.toString())){
							JSONObject jsonObj = new JSONObject(httpUtil.getResponseStr());
							int intResultCode = jsonObj
									.getInt(HttpUtil.RESULT_CODE);
							switch (intResultCode) {
							case HttpUtil.MSG_RECEIVE_FAIL:
								shMsg("获取数据时出错" + SF.FAIL);
								break;
							case HttpUtil.MSG_RECEIVE_EMPTY:
								shMsg("没有匹配的车次" + SF.TIP);
								break;
							case HttpUtil.MSG_RECEIVE_SUCCESS:
								//对train进行重新赋值
								mTrain = gson.fromJson(jsonObj.getString("train"), Train.class);
								break;
							}
						}else{
							shMsg("网络不稳定，无法获取车次详细信息" + SF.FAIL);
						}
						sendDismissDialog();
					}
					Message msg = mHandler.obtainMessage(SHOW_TRAIN_VIEW);
					mHandler.sendMessage(msg);
					//获取途经站点
					if (mTrain != null){
						JSONObject jsonObj1 = new JSONObject();
						jsonObj1.put(TT.REQUEST_TYPE, "getStations");
						jsonObj1.put(TT.STATION_TRAIN_CODE, mTrain.getTrainNum());
						jsonObj1.put(TT.TRAIN_NO, mTrain.getTrain_no());
						MyDatabase myDB = new MyDatabase(TrainDetailAty.this);
						jsonObj1.put(TT.FROM_STATION_TELECODE, myDB.getStationTeleCode(mTrain.getStartStation()));
						jsonObj1.put(TT.TO_STATION_TELECODE, myDB.getStationTeleCode(mTrain.getEndStation()));
						myDB.closeDB();
						if (httpUtil.post(strUrl, jsonObj1.toString())){
							JSONObject jsonObj = new JSONObject(httpUtil.getResponseStr());
							int intResultCode = jsonObj
									.getInt(HttpUtil.RESULT_CODE);
							switch (intResultCode) {
							case HttpUtil.MSG_RECEIVE_FAIL:
								shMsg("获取数据时出错" + SF.FAIL);
								break;
							case HttpUtil.MSG_RECEIVE_EMPTY:
								//已提醒过一次，所以对没有匹配的车次不再提醒. 
								break;
							case HttpUtil.MSG_RECEIVE_SUCCESS:
								//对lstStations进行重新赋值
								lstStations = gson.fromJson(jsonObj.getString("stations"),
										new TypeToken<List<StationInfo>>(){}.getType());
								intDataSource = AddInfoAty.DATA_FROM_NEWWORK;
								break;
							}
						}else{
							shMsg("网络不稳定，无法获取车次途径站点信息" + SF.TIP);
						}
					}
				}else{
					shMsg("单机了,无法取得数据" + SF.NO_NETWORK);
				}
				return null;
			}

			@Override
			protected void myOnPostExecute(Object result) {
				if (mTrain == null){
					return;
				}
				showContentView(false);
				if (mTrain.getStartStation() == null){
					TrainDetailAty.this.finish();
				}else{
					showContentView(true);
					setStationsView(lstStations);
					mTDPInfo.setTrain(mTrain);
					mTDPInfo.setLstSInfos(lstStations);
					boolean isOK = PersistentUtil.writeObject(mTDPInfo, getOfflineFilePath(mTrain.getTrainNum()));
					if ((!mTiped) && isOK){
						mTiped = true;
						String strMsg = "数据已离线保存，下次查看不耗流量哦"+SF.SUCCESS;
						Toast.makeText(TrainDetailAty.this, strMsg, Toast.LENGTH_LONG).show();
					}
				}
			}
			
			@Override
			protected void onException(Exception e) {
				shMsg("获取车次详情时出错" + SF.FAIL);
				showContentView(false);
			}
			
		}.execute(this);
	}
	private void setStationsView(List<StationInfo> lstStations) {
		if (lstStations == null){
			return;
		}
		mAdapter = new StationAdapter(this, lstStations);
		lvStations.setAdapter(mAdapter);
		MyUtils.setListViewHeightBasedOnChildren(lvStations);
		sv1.smoothScrollTo(0, 20);
	}
	public void setTrainView(Train train) {
		tvFrom.setText(train.getStartStation());
		tvTo.setText(train.getEndStation());
		tvD_Time.setText("(" + train.getD_Time() + ")");
		tvA_Time.setText("(" + train.getA_Time() + ")");
		tvInfo2.setText("历时:" + TimeUtil.get_T_Str(train.getR_Date()));
		tvD_LateTime.setText("发车平均晚点(" + train.getStartStation() + "站):");
		tvA_LateTime.setText("到站平均晚点(" + train.getEndStation() + "站):");
		if (train.getD_LateTime() != null){
			btnD_LateTime.setText(TimeUtil.get_T_Str(Integer.valueOf(train.getD_LateTime())));
			btnA_LateTime.setText(TimeUtil.get_T_Str(Integer.valueOf(train.getA_LateTime())));
		}
	}
	public void setTPricesView(String strMsg) {
		tvTPrices.setText(Html.fromHtml(strMsg));
	};
	
	private void initTPricesOnline() {
		//如果是车次查询则跳过显示票价
		if (mTrain.getStartStation() == null){
			tvTPrices.setVisibility(View.GONE);
			return;
		}
		startHandle(new Runnable(){
			public void run() {
				List<SeatInfo> lstSInfos = A6Util.queryTicketPrice(mBInfo);
				sendDismissDialog();
				if (lstSInfos == null || lstSInfos.size() == 0){
					if (!A6Util.isCanBooking()){
						initTrainAndStationsOnline();
					}
				}else{
					String strPrices = "";
					for(int j=0; j<lstSInfos.size(); j++){
						SeatInfo sInfo = lstSInfos.get(j);
						strPrices += sInfo.getName() + ":<font color='#ff8c00'>"
							+ sInfo.getPrice() + "</font>&nbsp;&nbsp;&nbsp;&nbsp;";
						if ((j+1)%2 == 0){ 
							strPrices += "<br/>";
						}
					}
					Message msg = mHandler.obtainMessage(A6Util.MSG_QUERY_TICKET_PRICE_SUCCESS);
					msg.obj = strPrices;
					mHandler.sendMessage(msg);
				}
			};
		});
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem miAdd = menu.add("添加");
		miAdd.setIcon(R.drawable.head_add);
		miAdd.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		miAdd.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				Intent intent = new Intent(TrainDetailAty.this, AddInfoAty.class);
				intent.putExtra(AddInfoAty.TRAIN_NUM, mTrain.getTrainNum());
				intent.putExtra(AddInfoAty.START_STATION, mTrain.getStartStation());
				intent.putExtra(AddInfoAty.END_STATION, mTrain.getEndStation());
				intent.putExtra(AddInfoAty.DATA_SOURCE, intDataSource);
				startActivity(intent);
				return true;
			}
		});
		MenuItem miCache = menu.add("清除离线缓存");
		miCache.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
		miCache.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				try{
					File file = new File(getOfflineFilePath(mTrain.getTrainNum()));
					file.delete();
					showMsg("离线缓存已清除"+SF.SUCCESS);
					TrainDetailAty.this.finish();
				}catch(Exception e){
					showMsg("离线缓存清除失败"+SF.FAIL);
					e.printStackTrace();
				}
				return true;
			}
		});
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.d_lateTime:
			Intent intent1 = new Intent(this, LateAty.class);
			intent1.putExtra(LateAty.TRAIN_NUM, mTrain.getTrainNum());
			intent1.putExtra(LateAty.STATION, mTrain.getStartStation());
			startActivity(intent1);
			break;
		case R.id.a_lateTime:
			Intent intent2 = new Intent(this, LateAty.class);
			intent2.putExtra(LateAty.TRAIN_NUM, mTrain.getTrainNum());
			intent2.putExtra(LateAty.STATION, mTrain.getEndStation());
			startActivity(intent2);
			break;
		}
	}
	private String getOfflineFilePath(String strTrainNum){
		return MyApp.getInstance().getStoreFile(Uri.encode("trainDetail_" + strTrainNum) + ".dat").getPath();
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
		initTPricesOnline();
		initTrainAndStationsOnline();
	}
	@Override
	public void doFooterTask() {
	}
	
	@Override
	protected void onDestroy() {
//		if (mLTimeDlg != null){
//			mLTimeDlg.dismiss();
//		}
		super.onDestroy();
	}
	@Override
	public void onItemClick(View v, int pos, final StationInfo entity,
			Object... params) {
		if (entity == null){
			return;
		}
		switch(v.getId()){
		case R.id.item_train_detail_station:
			new VoidAsyncTask() {
				boolean isSucc = false;
				long arriveTime = 0;
				@Override
				protected Object doInBackground(Object... arg0) {
					String data = SServiceUtil.getLTime(mTrain.getTrainNum(), entity.getStation_name());
					try {
						if (!TextUtils.isEmpty(data)){
							isSucc = true;
							if (data.indexOf("正点") > 0){
								arriveTime = 0;
								return data;
							}
							//提取时间
							String regex = "(\\d{2}[:]\\d{2})";
							String strTime = RegexUtils.getMatcher(regex, data);
							if (strTime != null){
								arriveTime = TimeUtil.get_T_MSeconds(strTime);
							}
							return data;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					return null;
				}
				protected void onPostExecute(Object result) {
					if (result == null){
						showMsg("请求数据失败");
					}else if (result.toString().length() > 0){
						if (isSucc){
							showDlg(result.toString());
						}else{
							showMsg(result.toString());
						}
					}
				};
			}.start();
//			showRCDialog(entity);
			break;
		}
	}
	public void showRCDialog(final StationInfo entity) {
//		if (mLTimeDlg != null){
//			mLTimeDlg.dismiss();
//		}
//		mLTimeDlg = new RCodeDialog(this, "12306晚点", null, new RCodeDialogListener(){
//			boolean isSucc = false;
//			@Override
//			public Bitmap onRCodeRequest(DialogInterface dlg) {
//				mBInfo.setCxlx(0);
//				mBInfo.setTo_station(entity.getStation_name());
//				mBInfo.setStationTrainCode(mTrain.getTrainNum());
//				return A6Util.getLTimeRandCode(mBInfo);
//			}
//
//			@Override
//			public void onClick(DialogInterface dlg, int which) {
//				if (mLTimeDlg == null){
//					return;
//				}
//				switch(which){
//				case DialogInterface.BUTTON_POSITIVE:
//					mBInfo.setRand_code(mLTimeDlg.getRCodeText().toString());
//					new VoidAsyncTask() {
//						long arriveTime = 0;
//						@Override
//						protected Object doInBackground(Object... params) {
//							if (TextUtils.isEmpty(mBInfo.getToStationTelecode())){
//								MyDatabase myDB = new MyDatabase(TrainDetailAty.this);
//								mBInfo.setToStationTelecode(myDB.getStationTeleCode(mBInfo.getTo_station()));
//								myDB.closeDB();
//							}
//							String strStationTrainCode = A6Util.getA6StationTrainCode(mBInfo);
//							L.i("station_train_code:"+strStationTrainCode);
//							if (strStationTrainCode != null){
//								mBInfo.setStationTrainCode(strStationTrainCode);
//							}
//							A6ResponseJsonMsg a6Json = A6Util.getLTime(mBInfo);
//							try {
//								JSONObject jsonObj = new JSONObject(a6Json.getData());
//								String strMsg = jsonObj.optString("message");
//								if (!TextUtils.isEmpty(strMsg)){
//									return strMsg;
//								}
//								String data = jsonObj.getString("data");
//								if (!TextUtils.isEmpty(data)){
//									isSucc = true;
//									if (data.indexOf("正点") > 0){
//										arriveTime = 0;
//										return data;
//									}
//									//提取时间
//									String regex = "(\\d{2}[:]\\d{2})";
//									String strTime = RegexUtils.getMatcher(regex, data);
//									if (strTime != null){
//										arriveTime = TimeUtil.get_T_MSeconds(strTime);
//									}
//									return data;
//								}
//							} catch (Exception e) {
//								e.printStackTrace();
//							}
//							return null;
//						}
//						protected void onPostExecute(Object result) {
//							if (result == null){
//								showMsg("请求数据失败");
//							}else if (result.toString().length() > 0){
//								if (isSucc){
//									showDlg(result.toString());
//								}else{
//									showMsg(result.toString());
//								}
//							}
//						};
//					}.executeTask();
//					break;
//				case DialogInterface.BUTTON_NEGATIVE:
//					break;
//				}
//				mLTimeDlg = null;
//			}
//			
//		});
//		mLTimeDlg.show();
	}
	private void showMsg( final String string) {
		if (this != null){
			Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
		}
	}
}
