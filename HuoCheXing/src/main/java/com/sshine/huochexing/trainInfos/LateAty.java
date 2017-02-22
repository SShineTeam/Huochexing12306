package com.sshine.huochexing.trainInfos;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONObject;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sshine.huochexing.R;
import com.sshine.huochexing.bean.LateTime;
import com.sshine.huochexing.utils.HttpUtil;
import com.sshine.huochexing.utils.L;
import com.sshine.huochexing.utils.MyTask;
import com.sshine.huochexing.utils.TimeUtil;
import com.sshine.huochexing.value.SF;
import com.umeng.analytics.MobclickAgent;

import eu.inmite.android.lib.dialogs.ISimpleDialogCancelListener;
import eu.inmite.android.lib.dialogs.ISimpleDialogListener;
import eu.inmite.android.lib.dialogs.SimpleDialogFragment;
import eu.inmite.android.lib.dialogs.SimpleDialogFragment.SimpleDialogBuilder;

public class LateAty extends SherlockFragmentActivity implements ISimpleDialogListener,ISimpleDialogCancelListener {
	public static final String TRAIN_NUM = "trainNum";   //供外部引用。
	public static final String STATION = "station";   //供外部引用。
	private static final String Date = "date";
	private static final String LATE_TIME = "lateTime";
	
	/*
	 * 请求历史晚点详情
	 * 请求json:{"requestType":"getLateTimes","trainNum":"K219","station":"大连"}
	 * 返回json:{"resultCode":"1","lateTimes":[{"lateDate":"2013-08-12","lateTime":"3636"}]
	 */
	private String strUrl = "http://huochexing.duapp.com/server/train_schedule.php";
	private String strTrainNum, strStation;
	private ActionBar actBar;
	private ListView lvInfos;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.aty_late);
		initActionBar();
		
		initViews();
	}

	private void initViews() {
		strTrainNum = this.getIntent().getStringExtra(TRAIN_NUM);
		strStation = this.getIntent().getStringExtra(STATION);
		
		lvInfos = (ListView) findViewById(R.id.late_lv1);
		addListViewHeader();
		actBar.setTitle(strStation + "站历史晚点详情");
		refeshViews();
	}
	
	private void addListViewHeader() {
		TextView tv1 = new TextView(this);
		tv1.setLayoutParams(new ListView.LayoutParams(ListView.LayoutParams.MATCH_PARENT, ListView.LayoutParams.WRAP_CONTENT));
		tv1.setGravity(Gravity.CENTER);
		tv1.setText("此数据由12306官方晚点数据提供");
		tv1.setTextColor(Color.DKGRAY);
		tv1.setPadding(10, 0, 20, 10);
		lvInfos.addHeaderView(tv1);
	}

	private void refeshViews(){
		if (!HttpUtil.isNetworkConnected(this)){
			showMsg("单机了，无法获取历史晚点数据" + SF.NO_NETWORK);
		}else{
			new MyTask(LateAty.this, "请求数据..."){

				@Override
				protected Object myDoInBackground(Object... params)
						throws Exception {
					HttpUtil httpUtil = new HttpUtil();
					JSONObject jObj = new JSONObject();
					jObj.put("requestType", "getLateTimes");
					jObj.put("trainNum", strTrainNum);
					jObj.put("station", strStation);
					L.i("getLateTimes:" + jObj.toString());
					if (httpUtil.post(strUrl, jObj.toString())){
						return httpUtil.getResponseStr();
					}else{
						return null;
					}
				}

				@Override
				protected void myOnPostExecute(Object result) {
					if (result == null) {
						showMsg("访问服务器时出错,请稍候再试" + SF.TIP);
					} else {
						try {
							L.i("getLateTimes结果:" + (String)result);
							JSONObject jsonObj = new JSONObject((String) result);
							int intResultCode = jsonObj
									.getInt(HttpUtil.RESULT_CODE);
							switch (intResultCode) {
							case HttpUtil.MSG_RECEIVE_FAIL:
								showMsg("获取数据时出错" + SF.FAIL);
								break;
							case HttpUtil.MSG_RECEIVE_EMPTY:
								SimpleDialogBuilder dialog = SimpleDialogFragment
									.createBuilder(LateAty.this,getSupportFragmentManager())
										.setTitle("提示")
										.setMessage("未收集到" + strTrainNum + "次列车在" + strStation + "站的历史晚点数据.")
										.setPositiveButtonText("确定");
								dialog.show();
								break;
							case HttpUtil.MSG_RECEIVE_SUCCESS:
								Gson gson = new Gson();
								List<LateTime> lstLateTimes = gson.fromJson(jsonObj.getString("lateTimes"),
										new TypeToken<List<LateTime>>(){}.getType());
								List<Map<String, String>> lstLateTimes2 = new ArrayList<Map<String,String>>();
//								SimpleDateFormat df1 = new SimpleDateFormat("yyyy年MM年dd日",Locale.getDefault());
								for(LateTime lt1:lstLateTimes){
									Map<String,String> map = new HashMap<String, String>();
									map.put(Date, lt1.getLateDate());
									map.put(LATE_TIME,lt1.getLateTime());
									map.put(LATE_TIME, TimeUtil.get_T_Str(TimeUtil.getFmt_MSeconds_TStr(Long.valueOf(lt1.getLateTime())*1000)));
									lstLateTimes2.add(map);
								}
								SimpleAdapter adapter = new SimpleAdapter(LateAty.this, lstLateTimes2, R.layout.item_late, 
										new String[]{Date, LATE_TIME}, new int[]{R.id.item_late_tvTime, R.id.item_late_tvLateTime}
										);
								lvInfos.setAdapter(adapter);
								break;
							}
						}catch(Exception e){
							showMsg("获取数据时出错" + SF.FAIL);
							e.printStackTrace();
						}
					}
				}

				@Override
				protected void onException(Exception e) {
					if (e instanceof ConnectTimeoutException || e instanceof SocketTimeoutException){
						showMsg("请求超时" + SF.TIP);
					}else{
						showMsg("请求数据时出错" + SF.FAIL);
					}
				}
				
			}.execute(this);
		}
	}
	
	private void showMsg(String strMsg){
		Toast.makeText(this, strMsg, Toast.LENGTH_SHORT).show();
	}

	private void initActionBar() {
		actBar = getSupportActionBar();
		actBar.setDisplayShowTitleEnabled(false);
		actBar.setDisplayShowHomeEnabled(true);
		actBar.setDisplayHomeAsUpEnabled(true);
		actBar.setDisplayShowTitleEnabled(true);
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

	@Override
	public void onPositiveButtonClicked(int requestCode) {
		this.finish();
	}

	@Override
	public void onNegativeButtonClicked(int requestCode) {
	}

	@Override
	public void onCancelled(int requestCode) {
		finish();
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
