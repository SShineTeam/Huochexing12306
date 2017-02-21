package com.sshine.huochexing.trainInfos;

import java.io.File;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;
import com.sshine.huochexing.LoginAty;
import com.sshine.huochexing.MainActivity;
import com.sshine.huochexing.R;
import com.sshine.huochexing.base.BaseAty;
import com.sshine.huochexing.bean.Travel;
import com.sshine.huochexing.socialshare.ShareContentAty;
import com.sshine.huochexing.ticketOnline.A6OrderAty;
import com.sshine.huochexing.utils.HttpHelper;
import com.sshine.huochexing.utils.HttpUtil;
import com.sshine.huochexing.utils.MyApp;
import com.sshine.huochexing.utils.MyDatabase;
import com.sshine.huochexing.utils.MyTask;
import com.sshine.huochexing.utils.SettingSPUtil;
import com.sshine.huochexing.utils.ShareUtil;
import com.sshine.huochexing.utils.TimeUtil;
import com.sshine.huochexing.utils.TrainInfoUtil;
import com.sshine.huochexing.utils.UserInfoSPUtil;
import com.sshine.huochexing.value.SF;
import com.sshine.huochexing.value.ServiceValue;
import com.umeng.analytics.MobclickAgent;
import com.viewpagerindicator.PageIndicator;
import com.viewpagerindicator.TitlePageIndicator;

import eu.inmite.android.lib.dialogs.ISimpleDialogCancelListener;
import eu.inmite.android.lib.dialogs.ISimpleDialogListener;
import eu.inmite.android.lib.dialogs.SimpleDialogFragment;

public class TrainInfoAty extends BaseAty implements
		ISimpleDialogListener, ISimpleDialogCancelListener {
	private static final int MSG_RECEIVE_VERIFY_FAILED = -1;
	private TrainInfoFragmentAdapter mAdapter;
	private ViewPager mPager;
	private PageIndicator mIndicator;
	private int mItemPos = 0;

	private String strUrl = "http://huochexing.duapp.com/server/user_train.php";
	private String mNewUrl = ServiceValue.NODEJS_PATH + ServiceValue.TRAIN_INFO;
//	private String strUpdateUrl = "http://huochexing2.duapp.com/server/u_t";
	private List<Travel> mLstTravels = new ArrayList<Travel>();
	/*
	 * 第一次请求，更新车次列表 请求json(已登录):{"requestType":"getTravels","uid":"100"}
	 * 返回json:{
	 * "resultCode":"1","travels":[{"serverId":"1","travelName":"旅游","trainNum"
	 * :"K598","startStation":"广州","endStation":"大连","r_Date":"07:52",
	 * "startLongitude"
	 * :"","startLatitude":"","receiveMsg":"1","receivedReminder"
	 * :"0","isRepeatReminder":"1",
	 * "startTime":"2013-08-15 03:20","endTime":"2013-08-15 12:00"
	 * ,"t_StartTime":"2013-08-15","userStatus":"0"}]}
	 * 
	 * 请求实时信息:
	 * 请求json:{"requestType":"updateTravel","trainNum":"K598","startStation"
	 * :"广州","endStation":"上海","t_StartTime":"2013-08-15"}
	 * 返回json:{"resultCode":1
	 * ,"sourceType":0,"travel":{"msgType":2,"trainStatus":"发往郑州站"
	 * ,"longitude":"0"
	 * ,"latitude":"0","stationSpace":8,"lateTime":"00:00","userAddTrain"
	 * :"3","userOnTrain:"1"}}
	 * 
	 * 删除信息: 请求json:{"requestType":"deleteTravel","serverId":"123"}
	 * 返回json:{"resultCode":"1"}
	 */
	public static final String EXTRA_NEED_REFRESH ="extraNeedRefresh";
	private static final int REQUEST_REMINDER_ADD_TRAVEL = 0;
	private static final int REQUEST_DELETE_TRAVEL = 1;

	private UserInfoSPUtil userSP = MyApp.getInstance().getUserInfoSPUtil();
	private SettingSPUtil setSP = MyApp.getInstance().getSettingSPUtil();

	private static final int MESSAGE_TOAST = 1;
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_TOAST:
				showMsg((String) msg.obj);
				break;
			}
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.aty_train_info);
		
		initActionBar();
		initViews();
		initContainerViews();
	}
	
	@Override
	protected void onRestart() {
		if (getIntent().getBooleanExtra(EXTRA_NEED_REFRESH, false)){
			setContents();
		}
		super.onRestart();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem miShare = menu.add("分享");
		miShare.setIcon(R.drawable.header_share);
		miShare.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		miShare.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				// 取得当前车次
				if (mAdapter == null || mAdapter.getmContents().size() == 0){
					return false;
				}
				Travel currTravel = mAdapter.getmContents().get(mItemPos);
				// 取得分享内容
				String content = ShareUtil.getInstance().getShareConetnt(
						currTravel);
				//临时修改标题
				setTitle("火车行");
				// 取得页面截图
				String imgPath = ShareUtil.getInstance().getScreenShut(
						TrainInfoAty.this);
				//恢复标题
				setTitle("我的车次");
				if (TextUtils.isEmpty(imgPath)) {
					Toast.makeText(TrainInfoAty.this, "分享截图失败，请重试",
							Toast.LENGTH_LONG).show();
					return false;
				}
				Bundle bundle = new Bundle();
				bundle.putString(ShareContentAty.CONTENT, content);
				bundle.putString(ShareContentAty.IMAGE_DATA_PATH, imgPath);
				Intent intent = new Intent(TrainInfoAty.this,
						ShareContentAty.class);
				intent.putExtras(bundle);
				try{
					startActivity(intent);
				}catch(Exception e){
					e.printStackTrace();
					showMsg("分享时出错，请稍候再试");
				}
				return false;
			}
		});

		MenuItem miDel = menu.add("删除");
		miDel.setIcon(R.drawable.head_trash);
		miDel.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		miDel.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				if (mAdapter == null || mAdapter.getmContents().size() == 0){
					return false;
				}
				Travel t1 = mAdapter.getmContents().get(mItemPos);
				SimpleDialogFragment
						.createBuilder(TrainInfoAty.this,
								getSupportFragmentManager())
						.setTitle("删除确认")
						.setMessage(
								"确定要删除旅行代号为\"" + t1.getTravelName()
										+ "\"的车次信息吗?")
						.setPositiveButtonText("删除")
						.setNegativeButtonText("取消")
						.setRequestCode(REQUEST_DELETE_TRAVEL).show();
				return true;
			}
		});
		MenuItem miAdd = menu.add("添加车次");
		miAdd.setIcon(R.drawable.head_add);
		miAdd.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
		miAdd.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				Intent intent = new Intent(TrainInfoAty.this, AddInfoAty.class);
				startActivity(intent);
				return true;
			}
		});
		MenuItem miA6Order = menu.add("查看12306订单");
		miA6Order.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
		miA6Order.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				Intent intent = new Intent(TrainInfoAty.this, A6OrderAty.class);
				startActivity(intent);
				return true;
			}
		});
		return super.onCreateOptionsMenu(menu);
	}

	private void showMsg(String str1) {
		Toast.makeText(TrainInfoAty.this, str1, Toast.LENGTH_SHORT).show();
	}

	private void initViews() {
		if (!MyApp.getInstance().getUserInfoSPUtil().isLogin()){
			startActivity(new Intent(this, LoginAty.class));
			Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
			finish();
		}
		setContents();
	}

	private void setContents() {
		if (mLstTravels != null){
			mLstTravels.clear();
		}
		new MyTask(this, null) {

			@Override
			protected Object myDoInBackground(Object... params)
					throws Exception {
				MyDatabase myDB = new MyDatabase(TrainInfoAty.this);
				SQLiteDatabase  db = myDB.getWritableDB();
				if (setSP.isTravelFirstShow()) {
					int result = 1;
					if (HttpHelper.isNetworkConnected(TrainInfoAty.this)){
						//更新车次列表
						result = TrainInfoUtil.updateUserTrainList(db);
					}
					if(result == 1){
						MyApp.getInstance().getSettingSPUtil().setTravelFirstShow(false);
					}else{
						//其实就是 result == 0
						return result ;  
					}
				}
				setTravels(db);
				db.close();
				myDB.closeDB();

				// 更新第一趟列车与第二趟列车的实时信息
				if (HttpUtil.isNetworkConnected(TrainInfoAty.this)) {
					int intResult = updateRTMsg(0);
					if (intResult != 1) {
						return intResult;
					} else {
						intResult = updateRTMsg(1);
						return intResult;
					}
				} else {
					return 0;
				}
			}

			public void setTravels(SQLiteDatabase db) {
				int intUid = 0;
				if (userSP.isLogin()) {
					intUid = userSP.getUId();
				} else {
					intUid = userSP.getUIdNotLogin();
				}
				// 从数据库中取得车次列表
				Cursor c = db.rawQuery("select * from UserTrainB where U_id=? order by _id DESC",
						new String[] { String.valueOf(intUid) });
				while (c.moveToNext()) {
					Travel travel = new Travel();
					travel.setRequested(false);
					travel.setNativeId(c.getString(c.getColumnIndex("_id")));
					travel.setServerId(c.getString(c.getColumnIndex("ServerId")));
					travel.setUid(c.getInt(c.getColumnIndex("U_id")));
					travel.setTravelName(c.getString(c
							.getColumnIndex("TravelName")));
					travel.setTrainNum(c.getString(c.getColumnIndex("T_id")));
					travel.setStartStation(c.getString(c
							.getColumnIndex("StartStation")));
					travel.setEndStation(c.getString(c
							.getColumnIndex("EndStation")));
					travel.setR_Date(c.getString(c.getColumnIndex("R_Date")));
					travel.setStartLongitude(c.getString(c
							.getColumnIndex("StartLongitude")));
					travel.setStartLatitude(c.getString(c
							.getColumnIndex("StartLatitude")));
					travel.setReceiveMsg(c.getInt(c
							.getColumnIndex("ReceiveMsg")));
					travel.setReceivedReminder(c.getInt(c
							.getColumnIndex("ReceivedReminder")));
					travel.setIsRepeatReminder(c.getInt(c
							.getColumnIndex("IsRepeatReminder")));
					travel.setStartTime(c.getString(c
							.getColumnIndex("StartTime")));
					travel.setEndTime(c.getString(c.getColumnIndex("EndTime")));
					travel.setT_startTime(c.getString(c
							.getColumnIndex("T_StartTime")));
					travel.setUserStatus(c.getInt(c
							.getColumnIndex("UserStatus")));
					mLstTravels.add(travel);
				}
				c.close();
			}

			protected int updateRTMsg(int itemPos) {
				if (mLstTravels.size() == 0) {
					return 2;
				} else if (itemPos >= mLstTravels.size()) {
					return -1;
				} else {
					Travel t1 = mLstTravels.get(itemPos);
					String strJson1 = "{\"requestType\":\"updateTravel\",\"trainNum\":\""
							+ t1.getTrainNum()
							+ "\",\"startStation\":\""
							+ t1.getStartStation()
							+ "\",\"endStation\":\""
							+ t1.getEndStation()
							+ "\",\"t_StartTime\":\""
							+ t1.getT_startTime() + "\"}";
					HttpUtil httpUtil = new HttpUtil();
					try {
						if (httpUtil.post(mNewUrl, strJson1)) {
							JSONObject jsonObj = new JSONObject(
									(String) httpUtil.getResponseStr());
							int intResultCode = jsonObj
									.getInt(HttpUtil.RESULT_CODE);
							switch (intResultCode) {
							case HttpUtil.MSG_RECEIVE_FAIL:
								return 0;
							case HttpUtil.MSG_RECEIVE_EMPTY:
								return 2;
							case HttpUtil.MSG_RECEIVE_SUCCESS:
								t1.setSourceType(jsonObj.getInt("sourceType"));
								JSONObject subObj = jsonObj
										.getJSONObject("travel");
								t1.setMsgType(subObj.getInt("msgType"));
								t1.setTrainStatus(subObj
										.getString("trainStatus"));
								t1.setLongitude(subObj.getString("longitude"));
								t1.setLatitude(subObj.getString("latitude"));
								t1.setStationSpace(subObj
										.getInt("stationSpace"));
								long ltime = TimeUtil.get_T_MSeconds(subObj.getString("lateTime"));
								t1.setLateTime(ltime);
								t1.setUserAddTrain(subObj
										.getInt("userAddTrain"));
								t1.setUserOnTrain(subObj.getInt("userOnTrain"));
								t1.setRequested(true); // 标明已更新过
								break;
							}
						} else {
							return 0;
						}
					} catch (Exception e) {
						e.printStackTrace();
						onException(e);
					}
				}
				return 1; // 返回1表示处理成功.
			}

			@Override
			protected void myOnPostExecute(Object result) {
				setContentView(R.layout.aty_train_info);
				int intResult = Integer.valueOf(result.toString());
				switch (intResult) {
				case 0:
					showMsg("从服务器请求数据时出错" + SF.FAIL);
					break;
				case 1:
					break;
				case 2:
					SimpleDialogFragment
							.createBuilder(TrainInfoAty.this,
									getSupportFragmentManager()).setTitle("提示")
							.setMessage("您当前没有添加任何车次，是否立即添加?")
							.setPositiveButtonText("好的")
							.setNegativeButtonText("先等会儿")
							.setRequestCode(REQUEST_REMINDER_ADD_TRAVEL).show();
					break;
				}
				setViewPager();
			}

			@Override
			protected void onException(Exception e) {
				showContentView(false);
				if (!HttpUtil.isNetworkConnected(TrainInfoAty.this)) {
					sendToast("单机了，无法取得数据" + SF.NO_NETWORK);
					setViewPager();
				} else if ((e instanceof ConnectTimeoutException)
						|| (e instanceof SocketTimeoutException)) {
					sendToast("连接超时" + SF.FAIL);
				} else {
					sendToast("向服务器请求数据时出错" + SF.FAIL);
				}
			}
		}.execute(this);
	}

	private void sendToast(String strMsg) {
		Message msg = mHandler.obtainMessage(MESSAGE_TOAST, strMsg);
		mHandler.sendMessage(msg);
	}

	protected void setViewPager() {
		showContentView(true);
		// ViewPager设置
		mAdapter = new TrainInfoFragmentAdapter(getSupportFragmentManager(),
				mLstTravels);
		mPager = (ViewPager) findViewById(R.id.pager);
		mPager.setAdapter(mAdapter);
		TitlePageIndicator indicator = (TitlePageIndicator) findViewById(R.id.indicator);
		indicator.setViewPager(mPager);
		mIndicator = indicator;

		mIndicator
				.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						mItemPos = position;
					}

					@Override
					public void onPageScrolled(int position,
							float positionOffset, int positionOffsetPixels) {
					}

					@Override
					public void onPageScrollStateChanged(int state) {
					}
				});
		// 设置当前车次详情
		mPager.setCurrentItem(0);
	}

	private void initActionBar() {
		ActionBar actBar = getSupportActionBar();
		actBar.setDisplayShowTitleEnabled(false);
		// 自定义不显示logo
		actBar.setDisplayShowHomeEnabled(true);
		actBar.setDisplayHomeAsUpEnabled(true);
		actBar.setDisplayShowTitleEnabled(true);
		actBar.setTitle("我的车次");
		actBar.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.actionbar_tab_bg));
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

	@Override
	public void onPositiveButtonClicked(int requestCode) {
		switch (requestCode) {
		case REQUEST_REMINDER_ADD_TRAVEL:
			startActivity(new Intent(TrainInfoAty.this, AddInfoAty.class));
			finish();
			break;
		case REQUEST_DELETE_TRAVEL:
			final Travel t1 = mAdapter.getmContents().get(mItemPos);
			if (userSP.isLogin()
					&& (!HttpUtil.isNetworkConnected(TrainInfoAty.this))) {
				showMsg("单机了，无法删除车次信息" + SF.NO_NETWORK);
			}
			// 删除代码
			new MyTask(this, "正在删除...") {

				@Override
				protected Object myDoInBackground(Object... params)
						throws Exception {
					MyDatabase myDB = new MyDatabase(TrainInfoAty.this);
					SQLiteDatabase db = myDB.getWritableDatabase();
					if (userSP.isLogin()) {
						int uid = MyApp.getInstance().getUserInfoSPUtil()
								.getUId();
						String sessionCode = MyApp.getInstance()
								.getUserInfoSPUtil().getSessionCode();
						String strJson = "{\"requestType\":\"deleteTravel\",\"serverId\":\""
								+ t1.getServerId()
								+ "\",\"uid\":\""
								+ uid
								+ "\",\"sessionCode\":\"" + sessionCode + "\"}";
						HttpUtil httpUtil = new HttpUtil();
						if (httpUtil.post(strUrl, strJson)) {
							JSONObject jsonObj = new JSONObject(
									(String) httpUtil.getResponseStr());
							int intResultCode = jsonObj
									.getInt(HttpUtil.RESULT_CODE);
							switch (intResultCode) {
							case MSG_RECEIVE_VERIFY_FAILED:
								return MSG_RECEIVE_VERIFY_FAILED;
							case HttpUtil.MSG_RECEIVE_FAIL:
								return false;
							case HttpUtil.MSG_RECEIVE_SUCCESS:
								break;
							}
						} else {
							return false;
						}
					}
					db.delete("UserTrainB", "_id=?",
							new String[] { t1.getNativeId() });
					db.close();
					myDB.close();
					delOfflineTrainDetailFile(t1.getTrainNum());
					return true;
				}

				@Override
				protected void myOnPostExecute(Object result) {
					if (result instanceof Boolean) {
						Boolean isSucc = (Boolean) result;
						if (isSucc) {
							mAdapter.getmContents().remove(t1);
							mAdapter.notifyDataSetChanged();
							mIndicator.notifyDataSetChanged();
							showMsg("删除成功" + SF.SUCCESS);

							if (mLstTravels.size() == 0) {
								startActivity(new Intent(TrainInfoAty.this,
										MainActivity.class));
								TrainInfoAty.this.finish();
							}
						} else {
							showMsg("删除失败" + SF.FAIL);
						}
					} else {
						if (result != null) {
							int resultcode = (Integer) result;
							if (resultcode == MSG_RECEIVE_VERIFY_FAILED) {
								// 验证失败 需要重新登录
								showMsg("您的身份已过期,请重新登录" + SF.FAIL);
								MyApp.getInstance().getUserInfoSPUtil()
										.resetUserInfo();
								Intent loginIntent = new Intent(
										TrainInfoAty.this, LoginAty.class);
								startActivity(loginIntent);
								TrainInfoAty.this.finish();
							}
						}
					}
				}

				@Override
				protected void onException(Exception e) {
					showMsg("删除数据时发生错误,删除失败" + SF.FAIL);
				}

			}.execute(this);
			break;
		}
	}

	private void delOfflineTrainDetailFile(String trainNum) {
		File file = new File(MyApp.getInstance().getPathBaseRoot(getOfflineFilePath(trainNum)));
		if (file.exists()){
			file.delete();
		}
	}
	private String getOfflineFilePath(String strTrainNum){
		return MyApp.getInstance().getPathBaseRoot(Uri.encode("trainDetail_" + strTrainNum) + ".dat");
	}

	@Override
	public void onNegativeButtonClicked(int requestCode) {
		switch (requestCode) {
		case REQUEST_REMINDER_ADD_TRAVEL:
			this.finish();
			break;
		}
	}

	@Override
	public void onCancelled(int requestCode) {
		switch (requestCode) {
		case REQUEST_REMINDER_ADD_TRAVEL:
			this.finish();
			break;
		}
	}

	@Override
	public void onBackPressed() {
		startActivity(new Intent(this, MainActivity.class));
		this.finish();
		super.onBackPressed();
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
