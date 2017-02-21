package com.sshine.huochexing.trainInfos;

import java.net.SocketTimeoutException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.actionbarsherlock.view.MenuItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sshine.huochexing.LoginAty;
import com.sshine.huochexing.MainActivity;
import com.sshine.huochexing.R;
import com.sshine.huochexing.base.BaseAty;
import com.sshine.huochexing.bean.TrainBrief;
import com.sshine.huochexing.bean.TravelBrief;
import com.sshine.huochexing.listener.IFavoriteCharacterDialogListener;
import com.sshine.huochexing.model.FavoriteCharacterDialogFragment;
import com.sshine.huochexing.trainSchedule.SelectAty;
import com.sshine.huochexing.utils.HttpUtil;
import com.sshine.huochexing.utils.L;
import com.sshine.huochexing.utils.MyApp;
import com.sshine.huochexing.utils.MyDatabase;
import com.sshine.huochexing.utils.MyTask;
import com.sshine.huochexing.utils.RegexUtils;
import com.sshine.huochexing.utils.SettingSPUtil;
import com.sshine.huochexing.utils.TimeUtil;
import com.sshine.huochexing.utils.UserInfoSPUtil;
import com.sshine.huochexing.value.SF;
import com.umeng.analytics.MobclickAgent;

import eu.inmite.android.lib.dialogs.ISimpleDialogListener;
import eu.inmite.android.lib.dialogs.SimpleDialogFragment;
import eu.inmite.android.lib.dialogs.SimpleDialogFragment.SimpleDialogBuilder;

public class AddInfoAty extends BaseAty implements
		OnClickListener, IFavoriteCharacterDialogListener,ISimpleDialogListener {
	public static final String TRAIN_NUM = "trainNum";
	public static final String START_STATION = "startStation";
	public static final String END_STATION = "endStation";
	public static final String DATA_SOURCE = "dataSource"; //数据来源
	public static final String EXTRA_START_TIME = "extraStartTime";
	
	private static final int MSG_RECEIVE_VERIFY_FAILED = -1;
	public static final int DATA_FROM_NEWWORK = 0;
	public static final int DATA_FROM_OFFINE_DB = 1;
	public static final int DATA_FROM_MANUAL_REQUEST = 2;
	
	private static final String TAG = "AddInfoAty";
	private static final int REQUET_SET_TRAVEL_BRIEF_OF_DATA_FROM_OFFLINE_DB = 1;
	private static final int REQUEST_ADD_TRAIN_SUCCESS = 2;
	private static final int REQUEST_LOGIN = 3;
	private static final int REQUEST_FROM_STATION = 4;
	private static final int REQUEST_TO_STATION = 5;
	
	private Button btnTrainNum, btnDate, btnOK;
	private Button btnFrom, btnTo;
	private int intYear, intMonth, intDay;
	private EditText etTravelName, etTime;
	private List<TrainBrief> lstTrainNums;
	private int lstTrainNumsIndex = 0;
	private SettingSPUtil setSP = MyApp.getInstance().getSettingSPUtil();

	/**
	 * 请求车次列表
	 * 请求Json:{"requestType":"getTrainNums","startStation":"北京","endStation":"上海"}
	 * 返回Json:{"resultCode":"1","trainNums":[{"trainNum":"1461","a_Time":"10:15","d_Time":"10:20","r_Date":"17:20","cfRunTime":"00:39","startLongitude":"33.45","startLatitude":"12.789"}]}
	 * 
	 * 上传车次信息到服务器
	 * 请求json:{"requestType":"saveTravel","endStation":"北京","r_Date":"22:12","startStation":"上海",
	 * "startTime":"2013-09-07 12:28","endTime":"2013-09-07 22:31","t_StartTime":"2013-09-06",
	 * "trainNum":"1462","travelName":"1462","u_id":36,"startLongitude":"33.45","startLatitude":"12.789",
	 * "receiveMsg":"0","receivedReminder":"0","isRepeatReminder":"0"};
	 * 返回json:{"resultCode":"1","serverId":"1"}
	 */
	private String strUrl = "http://huochexing.duapp.com/server/add_train.php";  //取得车次时请求的url
	private int intDataSource = DATA_FROM_MANUAL_REQUEST;
	private String strTrainNum;
	UserInfoSPUtil userSP = MyApp.getInstance().getUserInfoSPUtil();
	private boolean isUserDemo = false;
	private EditText etDate;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setDisableLoadingView(true);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.aty_add_info);
		setTitle("添加车次");
		
		initViews();
	}

	private void initViews() {
		btnFrom = (Button) findViewById(R.id.from);
		btnFrom.setOnClickListener(this);
		btnTo = (Button) findViewById(R.id.to);
		btnTo.setOnClickListener(this);
		btnTrainNum = (Button) findViewById(R.id.trainNum); // 车次
		btnTrainNum.setOnClickListener(this);
		etDate = (EditText)findViewById(R.id.date);
		btnDate = (Button) findViewById(R.id.btnDate); // 选择乘车日期
		btnDate.setOnClickListener(this);
		btnOK = (Button) findViewById(R.id.ok);
		btnOK.setOnClickListener(this);
		etTravelName = (EditText) findViewById(R.id.travelName);
		etTime = (EditText) findViewById(R.id.time);
		
		btnFrom.setText(setSP.getLastFromStationKey());
		btnTo.setText(setSP.getLastToStationKey());
		// 取得当前日期
		Calendar c = Calendar.getInstance(Locale.getDefault());
		c.setTimeInMillis(System.currentTimeMillis());
		intYear = c.get(Calendar.YEAR);
		intMonth = c.get(Calendar.MONTH);
		intDay = c.get(Calendar.DAY_OF_MONTH);
		String strDate = TimeUtil.getDFormat().format(c.getTime());
		etDate.setText(strDate + "  " + TimeUtil.getWeek(c.getTime()));
		etDate.setTag(strDate);
		
		if (this.getIntent().getStringExtra(TRAIN_NUM) != null){
			strTrainNum = this.getIntent().getStringExtra(TRAIN_NUM);
			btnFrom.setText(this.getIntent().getStringExtra(START_STATION));
			btnTo.setText(this.getIntent().getStringExtra(END_STATION));
			intDataSource = this.getIntent().getIntExtra(DATA_SOURCE, DATA_FROM_OFFINE_DB);
			
			if (intDataSource == DATA_FROM_OFFINE_DB){
				SimpleDialogFragment
				.createBuilder(
						AddInfoAty.this,
						getSupportFragmentManager())
				.setCancelable(false)
				.setRequestCode(REQUET_SET_TRAVEL_BRIEF_OF_DATA_FROM_OFFLINE_DB)
				.setTitle("提示")
				.setMessage("检测到您采用离线数据快速添加数据，为保证数据准确性请您手动进行车次选择"+SF.TIP)
				.setPositiveButtonText("确定")
				.show();
			}
			String strStartTime = getIntent().getStringExtra(EXTRA_START_TIME);
			if (!TextUtils.isEmpty(strStartTime)){
				try {
					Date date1 = TimeUtil.getDFormat().parse(strStartTime);
					strStartTime = TimeUtil.getDFormat().format(date1);
					etDate.setText(strStartTime + " " + TimeUtil.getWeek(strStartTime));
					etDate.setTag(strStartTime);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			getTrains();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			startActivity(new Intent(AddInfoAty.this, MainActivity.class));
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		Intent intent1 = new Intent(AddInfoAty.this, SelectAty.class);
		intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 不加此标志StationAty有时执行两次finish()才会返回。
		intent1.putExtra(SelectAty.SEARCH_TYPE, SelectAty.SEARCH_STATION);
		switch (v.getId()) {
		case R.id.from:
			startActivityForResult(intent1, REQUEST_FROM_STATION);
			break;
		case R.id.to:
			startActivityForResult(intent1, REQUEST_TO_STATION);
			break;
		case R.id.trainNum:
			if (btnFrom.getText().length() == 0){
				showMsg("请先选择出发地" + SF.TIP);
			}else if (btnTo.getText().length() == 0){
				showMsg("请先选择目的地" + SF.TIP);
			}else if (!HttpUtil.isNetworkConnected(AddInfoAty.this)){
				showMsg("无网络，无法取得车次列表" + SF.NO_NETWORK);
			}else{
				intDataSource = DATA_FROM_MANUAL_REQUEST;
				getTrains();
			}
			break;
		case R.id.btnDate:
			new DatePickerDialog(this, new OnDateSetListener() {

				@Override
				public void onDateSet(DatePicker view, int year,
						int monthOfYear, int dayOfMonth) {
					String tempDate = year + "-" + (monthOfYear + 1) + "-"
							+ dayOfMonth;
					try {
						Date date = TimeUtil.getDFormat().parse(tempDate);
						String preDate = intYear + "-" + (intMonth + 1) + "-"
								+ (intDay-1);
						if (tempDate.compareTo(preDate)<0) {
							showMsg("最早可设置前一天以内的日期" + SF.FAIL);
						} else {
							tempDate = TimeUtil.getDFormat().format(date);
							etDate.setText(tempDate + "  "
									+ TimeUtil.getWeek(tempDate));
							etDate.setTag(tempDate);
						}
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
			}, intYear, intMonth, intDay).show();
			break;
		case R.id.ok:
			addTravel();
			break;
		}
	}

	private void addTravel() {
		if ((btnFrom.getText() == null || btnFrom.getText().length() == 0)
				|| (btnTo.getText() == null || btnTo.getText().length() == 0)
				|| (btnTrainNum.getText() == null || btnTrainNum.getText()
						.length() == 0)
				|| (etTravelName.getText() == null || etTravelName
						.getText().length() == 0)
				|| (etTime.getText() == null || etTime.getText().length() == 0)) {
			showMsg("所有信息都不可为空" + SF.TIP);
		} else if (!Pattern.matches(RegexUtils.regexTravelName, etTravelName.getText()
				.toString())) {
			// 检测旅行代号是否合法
			showMsg("旅行代号必须是20个字符以内的字母、数字、汉字或下划线" + SF.TIP);
		} else if (etDate.getTag() == null
				|| etDate.getTag().toString().length() == 0) {
			showMsg("请选择乘车日期");
		} else {
			// 添加车次代码
			//强制登录
			if (!userSP.isLogin()){
				showMsg("请先登录");
				Intent intent = new Intent(AddInfoAty.this, LoginAty.class);
				startActivityForResult(intent, REQUEST_LOGIN);
				return;
			}
			doAddTravelWork();
		}
	}

	public void doAddTravelWork() {
		final TravelBrief tb = new TravelBrief();
		tb.setU_id(userSP.getUId());
		TrainBrief trainBrief = lstTrainNums.get(lstTrainNumsIndex);
		tb.setTrainNum(trainBrief.getTrainNum());
		tb.setTravelName(etTravelName.getText().toString());
		tb.setStartStation(btnFrom.getText().toString());
		tb.setEndStation(btnTo.getText().toString());
		tb.setStartTime(etDate.getTag().toString() + " "
				+ etTime.getText().toString());
		tb.setEndTime(TimeUtil.getFmt_DT_T_SumStr(tb.getStartTime(), trainBrief.getR_Date()));
		tb.setR_Date(trainBrief.getR_Date());
		tb.setT_StartTime(TimeUtil.getFmt_DT_T_DiffStr_GetD(tb.getStartTime(), trainBrief.getCFRunTime()));
		tb.setStartLongitude(trainBrief.getStartLongitude());
		tb.setStartLatitude(trainBrief.getStartLatitude());
		tb.setReceiveMsg(1);
		tb.setReceivedReminder(0);
		tb.setIsRepeatReminder(1);
		
		//检测是否重复添加
		MyDatabase myDB = new MyDatabase(AddInfoAty.this);
		SQLiteDatabase db = myDB.getWritableDatabase();
		String sql = "select T_id from UserTrainB where U_id=? and T_id=? and T_StartTime=?;";
		Cursor c = db.rawQuery(sql, new String[]{String.valueOf(tb.getU_id()), tb.getTrainNum(), tb.getT_StartTime()});
		if (c.getCount() > 0){
			showMsg("您已添加过此车次" + SF.TIP);
		}else{
			if (userSP.isLogin() && (!isUserDemo)) {
				// 如果已经登录则上传车次信息到服务器
				Gson gson = new Gson();
				String sessionCode = MyApp.getInstance().getUserInfoSPUtil().getSessionCode();
				final String strJson = "{\"requestType\":\"saveTravel\",\"sessionCode\":\"" + sessionCode + "\"," + gson.toJson(tb).substring(1);
				L.i("OK:" + strJson);
				new MyTask(this, "提交数据中...") {
					
					@Override
					protected Object myDoInBackground(Object... params)
							throws Exception {
						
						HttpUtil httpHelper = new HttpUtil();

						if (httpHelper.post(strUrl, strJson)) {
							return httpHelper.getResponseStr();
						} else {
							return null;
						}
					}

					@Override
					protected void myOnPostExecute(Object result) {
						if (result == null) {
							showMsg("访问服务器出错,请稍候再试" + SF.NO_NETWORK);
						} else {
							try {
								JSONObject jsonObj = new JSONObject(
										(String) result);
								L.i("确认:" + jsonObj.toString());
								int intResultCode = jsonObj
										.getInt(HttpUtil.RESULT_CODE);
								switch (intResultCode) {
								
								case MSG_RECEIVE_VERIFY_FAILED:
									//验证失败 需要重新登录
									showMsg("您的身份已过期,请重新登录" + SF.FAIL);
									MyApp.getInstance().getUserInfoSPUtil().resetUserInfo();
									Intent loginIntent = new Intent(AddInfoAty.this, LoginAty.class);
									startActivity(loginIntent);
									AddInfoAty.this.finish();
									break;
								case HttpUtil.MSG_RECEIVE_FAIL:
									showMsg("获取数据时出错" + SF.FAIL);
									break;
								case HttpUtil.MSG_RECEIVE_SUCCESS:
									// 写入本地数据库
									MyDatabase myDB = new MyDatabase(
											AddInfoAty.this);
									SQLiteDatabase db = myDB.getWritableDatabase();
									String sql = "select T_id from UserTrainB where U_id=? and T_id=? and T_StartTime=?;";
									Cursor c = db.rawQuery(sql, new String[]{String.valueOf(tb.getU_id()), tb.getTrainNum(), tb.getT_StartTime()});
									if (c.getCount() > 0){
										showMsg("您已添加过此车次" + SF.TIP);
									}else{
										ContentValues cv = new ContentValues();
										cv.put("serverId", jsonObj.getString("serverId"));
										cv.put("U_id", tb.getU_id());
										cv.put("T_id", tb.getTrainNum());
										cv.put("TravelName", tb.getTravelName());
										cv.put("StartStation",
												tb.getStartStation());
										cv.put("EndStation", tb.getEndStation());
										cv.put("StartTime", tb.getStartTime());
										cv.put("EndTime", tb.getEndTime());
										cv.put("R_Date", tb.getR_Date());
										cv.put("T_StartTime", tb.getT_StartTime());
										cv.put("ReceiveMsg", "1");
										cv.put("StartLongitude", tb.getStartLongitude());
										cv.put("StartLatitude", tb.getStartLatitude());
										cv.put("ReceiveMsg", tb.getReceiveMsg());
										cv.put("ReceivedReminder", tb.getReceivedReminder());
										cv.put("IsRepeatReminder", tb.getIsRepeatReminder());
										db.insert("UserTrainB", null, cv);
										myDB.close();
										db.close();
										SimpleDialogBuilder dialog = SimpleDialogFragment
												.createBuilder(
														AddInfoAty.this,
														getSupportFragmentManager())
												.setRequestCode(REQUEST_ADD_TRAIN_SUCCESS)
												.setTitle("提示")
												.setMessage("已成功添加车次信息!")
												.setPositiveButtonText("查看我的车次")
												.setNegativeButtonText("继续添加");
										dialog.show();
									}
									c.close();
									db.close();
									myDB.closeDB();
									break;
								}
							} catch (Exception e) {
								e.printStackTrace();
								showMsg("保存数据时出错" + SF.FAIL);
							}
						}
					}

					@Override
					protected void onException(Exception e) {
						if (e instanceof ConnectTimeoutException || e instanceof SocketTimeoutException){
							showMsg("提交数据超时" + SF.FAIL);
						}else{
							showMsg("提交数据时出错" + SF.TIP);
						}
					}
				}.execute(this);
			}else{
				new MyTask(this, "保存车次信息中..."){

					@Override
					protected Object myDoInBackground(Object... params)
							throws Exception {
						// 写入本地数据库
						MyDatabase myDB = new MyDatabase(
								AddInfoAty.this);
						SQLiteDatabase db = myDB
								.getWritableDatabase();
						String sql = "select T_id from UserTrainB where U_id=? and T_id=? and T_StartTime=?;";
						Cursor c = db.rawQuery(sql, new String[]{String.valueOf(tb.getU_id()), tb.getTrainNum(), tb.getT_StartTime()});
						if (c.getCount() > 0){
							db.close();
							myDB.closeDB();
							return 3;
						}else{
							ContentValues cv = new ContentValues();
							cv.put("U_id", tb.getU_id());
							cv.put("T_id", tb.getTrainNum());
							cv.put("TravelName", tb.getTravelName());
							cv.put("StartStation",
									tb.getStartStation());
							cv.put("EndStation", tb.getEndStation());
							cv.put("StartTime", tb.getStartTime());
							cv.put("EndTime", tb.getEndTime());
							cv.put("R_Date", tb.getR_Date());
							cv.put("T_StartTime", tb.getT_StartTime());
							cv.put("ReceiveMsg", "1");
							cv.put("StartLongitude", tb.getStartLongitude());
							cv.put("StartLatitude", tb.getStartLatitude());
							cv.put("ReceiveMsg", tb.getReceiveMsg());
							cv.put("ReceivedReminder", tb.getReceivedReminder());
							cv.put("IsRepeatReminder", tb.getIsRepeatReminder());
							db.insert("UserTrainB", null, cv);
							
							db.close();
							myDB.closeDB();
							return 1;
						}
					}

					@Override
					protected void myOnPostExecute(Object result) {
						int intResult = (Integer)result;
						switch(intResult){
						case 3:
							showMsg("您已添加过此车次" + SF.TIP);
							break;
						case 1:
							SimpleDialogBuilder dialog = SimpleDialogFragment
							.createBuilder(
									AddInfoAty.this,
									getSupportFragmentManager())
							.setRequestCode(REQUEST_ADD_TRAIN_SUCCESS)
							.setTitle("提示")
							.setMessage("已成功添加车次信息!")
							.setPositiveButtonText("查看我的车次")
							.setNegativeButtonText("继续添加");
					dialog.show();
							break;
						}
					}

					@Override
					protected void onException(Exception e) {
						showMsg("保存车次信息时出错" + SF.FAIL);
					}
				}.execute(this);
			}
		}
	}

	private void getTrains() {
		// 异步获取车次列表
		new MyTask(this, "请求数据...") {

			@Override
			protected void onException(Exception e) {
				if (e instanceof ConnectTimeoutException || e instanceof SocketTimeoutException){
					showMsg("访问超时" + SF.TIP);
				}else{
					showMsg("访问服务器时出错,请稍候再试" + SF.FAIL);
				}
			}

			@SuppressWarnings("deprecation")
			@Override
			protected void myOnPostExecute(Object result) {
				if (result == null) {
					showMsg("访问服务器时出错,请稍候再试" + SF.FAIL);
				} else {
					try {
						JSONObject jsonObj = new JSONObject((String) result);
						int intResultCode = jsonObj
								.getInt(HttpUtil.RESULT_CODE);
						switch (intResultCode) {
						case HttpUtil.MSG_RECEIVE_FAIL:
							showMsg("获取数据时出错" + SF.FAIL);
							break;
						case HttpUtil.MSG_RECEIVE_EMPTY:
							showMsg("没有匹配的车次" + SF.TIP);
							break;
						case HttpUtil.MSG_RECEIVE_SUCCESS:
							Gson gson = new Gson();
							lstTrainNums = gson.fromJson(jsonObj.getString("trainNums"),
									new TypeToken<List<TrainBrief>>(){}.getType());
							switch(intDataSource){
							case DATA_FROM_NEWWORK:
								for(int i = 0; i < lstTrainNums.size(); i++){
									if (lstTrainNums.get(i).getTrainNum().equals(strTrainNum)){
										handleTrainBrief(i);
										break;
									}
								}
								break;
							case DATA_FROM_OFFINE_DB:
								break;
							case DATA_FROM_MANUAL_REQUEST:
								String[] strTrainNums1 = new String[lstTrainNums.size()];
								for(int i = 0; i < lstTrainNums.size(); i++){
									strTrainNums1[i] = lstTrainNums.get(i).getTrainNum();
								}
								try{
									FavoriteCharacterDialogFragment.show(
											AddInfoAty.this, "选择车次", strTrainNums1);
								}catch(Exception e){
								}
								break;
							}
							break;
						}
					} catch (JSONException e) {
						showMsg("获取数据时出错" + SF.FAIL);
						e.printStackTrace();
					}

				}
			}

			@Override
			protected Object myDoInBackground(Object... params)
					throws Exception {

				String strMsg = "{\"requestType\":\"getTrainNums\",\"startStation\":\""
						+ btnFrom.getText().toString()
						+ "\",\"endStation\":\""
						+ btnTo.getText().toString() + "\"}";
				Log.i(TAG, "trainNumRequest:" + strMsg);
				
				HttpUtil httpHelper = new HttpUtil();
				if (httpHelper.post(strUrl, strMsg)) {
					Log.i(TAG, "trainNumResponse:" + httpHelper.getResponseStr());
					return httpHelper.getResponseStr();
				} else {
					return null;
				}
			}
		}.execute(this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode){
		case REQUEST_LOGIN:
			if (resultCode == RESULT_OK){
				doAddTravelWork();
			}
			break;
		case REQUEST_FROM_STATION:
			if (resultCode == RESULT_OK){
				String fromStation = data.getExtras().getString(SelectAty.RESULT_KEY);
				if (!fromStation.equals(btnFrom.getText().toString())){
					btnFrom.setText(fromStation);
					btnTrainNum.setText("");
					etTravelName.setText("");
				}
			}
			break;
		case REQUEST_TO_STATION:
			if (resultCode == RESULT_OK){
				String toStation = data.getExtras().getString(SelectAty.RESULT_KEY);
				if (!toStation.equals(btnTo.getText().toString())){
					btnTo.setText(toStation);
					btnTrainNum.setText("");
					etTravelName.setText("");
				}
			}
			break;
		}
	}

	@Override
	public void onListItemSelected(int requestCode, String value, int number) {
		handleTrainBrief(number);
	}
	private void handleTrainBrief(int number){
		TrainBrief tb = lstTrainNums.get(number);
		btnTrainNum.setText(tb.getTrainNum());
		etTravelName.setText(tb.getTrainNum());
		etTime.setText(tb.getD_Time());
		lstTrainNumsIndex = number;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onPositiveButtonClicked(int requestCode) {
		switch(requestCode){
		case REQUET_SET_TRAVEL_BRIEF_OF_DATA_FROM_OFFLINE_DB:
			getTrains();
			if (lstTrainNums != null){
				String[] strTrainNums = new String[lstTrainNums.size()];
				for(int i = 0; i < lstTrainNums.size(); i++){
					strTrainNums[i] = lstTrainNums.get(i).getTrainNum();
				}
				FavoriteCharacterDialogFragment.show(
						AddInfoAty.this, "选择车次", strTrainNums);
			}
			break;
		case REQUEST_ADD_TRAIN_SUCCESS:
			Intent intent = new Intent(AddInfoAty.this, TrainInfoAty.class);
			intent.putExtra(TrainInfoAty.EXTRA_NEED_REFRESH, true);
			startActivity(intent);
			this.finish();
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
