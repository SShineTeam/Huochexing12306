package com.sshine.huochexing.trainInfos;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONObject;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sshine.huochexing.R;
import com.sshine.huochexing.bean.A6Info;
import com.sshine.huochexing.bean.BookingInfo;
import com.sshine.huochexing.bean.Train;
import com.sshine.huochexing.bean.Travel;
import com.sshine.huochexing.model.PullToRefreshView;
import com.sshine.huochexing.model.PullToRefreshView.OnFooterRefreshListener;
import com.sshine.huochexing.model.PullToRefreshView.OnHeaderRefreshListener;
import com.sshine.huochexing.utils.A6Util;
import com.sshine.huochexing.utils.HttpUtil;
import com.sshine.huochexing.utils.L;
import com.sshine.huochexing.utils.MyApp;
import com.sshine.huochexing.utils.MyDatabase;
import com.sshine.huochexing.utils.MyTask;
import com.sshine.huochexing.utils.RCodeDialog;
import com.sshine.huochexing.utils.RCodeDialog.RCodeDialogListener;
import com.sshine.huochexing.utils.RegexUtils;
import com.sshine.huochexing.utils.TimeUtil;
import com.sshine.huochexing.utils.VoidAsyncTask;
import com.sshine.huochexing.value.SF;
import com.sshine.huochexing.value.ServiceValue;

public class TrainInfoFragment extends Fragment
	implements OnClickListener,OnHeaderRefreshListener, OnFooterRefreshListener {
	public Travel mContent; // 内容数据
	private TextView tvTime;
	private TextView tvTrainStatus, tvStationSpaceTip, tvStationSpace,
			tvLateTimeTip, tvTimeTip, tvPredictTimeTip,
			tvPredictTime, tvRemainingTimeTip, tvRemainingTime;
	private TextView tvUserAddTrain, tvUserOnTrain;
	private TableLayout tlytArea2;
	private LinearLayout llytArea3;
	private Button btnWeather, btnLocation;
	private TextView tvLateTime;
	private BookingInfo mBInfo = MyApp.getInstance().getCommonBInfo();

	/*
	 * 请求实时信息:
	 * 请求json:{"requestType":"updateTravel","trainNum":"K598","startStation"
	 * :"广州","endStation":"上海","t_StartTime":"2013-08-15"}
	 * 返回json:{"resultCode":1,"sourceType":0,"travel":{"msgType":2,"trainStatus":"发往郑州站"
	 * ,"longitude":"0","latitude":"0","stationSpace":8,"lateTime":"00:00","userAddTrain":"3","userOnTrain:"1"}}
	 */
	private String strUrl = "http://huochexing.duapp.com/server/user_train.php";
	private String mNewUrl = ServiceValue.NODEJS_PATH + ServiceValue.TRAIN_INFO;
//	private String strUpdateUrl = "http://huochexing2.duapp.com/server/u_t";
	
	private LinearLayout mContainer;
	private PullToRefreshView mPullToRefreshView;
	private View mFooterLoadingView;
	private RCodeDialog mLTimeDlg;

	public static TrainInfoFragment newInstance(Travel cContent1) {
		TrainInfoFragment fragment = new TrainInfoFragment();

		fragment.mContent = cContent1;
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_train_info, null);
		mContainer = (LinearLayout)v.findViewById(R.id.trainInfo_container);
		mFooterLoadingView = inflater.inflate(R.layout.footer_loading_view, null);
		mFooterLoadingView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		mContainer.addView(mFooterLoadingView);
		if (mContent == null){
			return v;
		}
		
		// 查找控件并绑定事件.
		Button btnTrainNum = (Button) v
				.findViewById(R.id.trainInfo_btnTrainNum);
		btnTrainNum.setOnClickListener(this);
		TextView tvStartStation = (TextView) v
				.findViewById(R.id.trainInfo_tvStartStation);
		TextView tvEndStation = (TextView) v
				.findViewById(R.id.trainInfo_tvEndStation);
		TextView tvR_Date = (TextView) v.findViewById(R.id.trainInfo_tvR_Date);
		tvTrainStatus = (TextView) v.findViewById(R.id.trainInfo_tvTrainStatus);
		btnLocation = (Button) v.findViewById(R.id.trainInfo_btnLocation);
		btnLocation.setOnClickListener(this);
		tvStationSpaceTip = (TextView) v
				.findViewById(R.id.trainInfo_tvStationSpaceTip);
		tvStationSpace = (TextView) v
				.findViewById(R.id.trainInfo_tvStationSpace);
		tvLateTimeTip = (TextView) v.findViewById(R.id.trainInfo_tvLateTimeTip);
		tvLateTime = (TextView) v.findViewById(R.id.trainInfo_tvLateTime);
		tvLateTime.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		tvLateTime.setOnClickListener(this);
		tvTimeTip = (TextView) v.findViewById(R.id.trainInfo_tvTimeTip);
		tvTime = (TextView) v.findViewById(R.id.trainInfo_tvTime);
		tvPredictTimeTip = (TextView) v
				.findViewById(R.id.trainInfo_tvPredictTimeTip);
		tvPredictTime = (TextView) v.findViewById(R.id.trainInfo_tvPredictTime);
		tvRemainingTimeTip = (TextView) v
				.findViewById(R.id.trainInfo_tvRemainingTimeTip);
		tvRemainingTime = (TextView) v
				.findViewById(R.id.trainInfo_tvRemainingTime);
		tlytArea2 = (TableLayout) v.findViewById(R.id.trainInfo_tlytArea2);
		llytArea3 = (LinearLayout) v.findViewById(R.id.trainInfo_llytArea3);
		tvUserAddTrain = (TextView) v
				.findViewById(R.id.trainInfo_tvUserAddTrain);
		tvUserOnTrain = (TextView) v.findViewById(R.id.trainInfo_tvUserOnTrain);
		
		//天气
		btnWeather = (Button) v.findViewById(R.id.trainInfo_btnWeather);
		btnWeather.setOnClickListener(this);
		
		//设置上拉下拉刷新
		mPullToRefreshView = (PullToRefreshView)v.findViewById(R.id.trainInfo_pull_refresh_view);
		mPullToRefreshView.setOnHeaderRefreshListener(this);
		mPullToRefreshView.setOnFooterRefreshListener(this);
		mFooterLoadingView = v.findViewById(R.id.footer_loading_containter);

		this.setHasOptionsMenu(true);
		//暂时默认隐藏此功能
		llytArea3.setVisibility(View.GONE);
		// 加载数据
		if (mContent.getTrainNum() != null) {
			btnTrainNum.setText(mContent.getTrainNum());
			tvStartStation.setText(mContent.getStartStation());
			tvEndStation.setText(mContent.getEndStation());
			tvR_Date.setText(TimeUtil.get_T_Str(mContent.getR_Date()));
			tvTimeTip.setText("正点发车时间:");
			tvTime.setText(mContent.getStartTime());

		} else {
			btnTrainNum.setEnabled(false);
		}

		if (mContent.isRequested()) {
			updateRTMsgViews();
			showContentView();
		} else {
			getRTMsgAndThenRefeshViews(1);
		}
		return v;
	}
	
	/**
	 * 请求最新信息并更新界面
	 * @param type 请求类型，1为有Dialog界面，0为无Dialog界面.
	 */
	private void getRTMsgAndThenRefeshViews(final int type) {
		String strMsg = null;
		if (type == 1){
			//不显示提示窗口
//			strMsg = "正在更新列车信息...";
		}
		new MyTask(TrainInfoFragment.this.getActivity(), strMsg) {
			@Override
			protected Object myDoInBackground(Object... params)
					throws Exception {
				String strJson1 = "{\"requestType\":\"updateTravel\",\"trainNum\":\""
						+ mContent.getTrainNum()
						+ "\",\"startStation\":\""
						+ mContent.getStartStation()
						+ "\",\"endStation\":\""
						+ mContent.getEndStation()
						+ "\",\"t_StartTime\":\""
						+ mContent.getT_startTime() + "\"}";
				HttpUtil httpUtil = new HttpUtil();
				if (httpUtil.post(mNewUrl, strJson1)) {
					JSONObject jsonObj = new JSONObject(
							(String) httpUtil.getResponseStr());
					int intResultCode = jsonObj
							.getInt(HttpUtil.RESULT_CODE);
					switch (intResultCode) {
					case HttpUtil.MSG_RECEIVE_FAIL:
						return null;
					case HttpUtil.MSG_RECEIVE_SUCCESS:
						mContent.setSourceType(jsonObj
								.getInt("sourceType"));
						JSONObject subObj = jsonObj
								.getJSONObject("travel");
						mContent.setMsgType(subObj.getInt("msgType"));
						mContent.setTrainStatus(subObj
								.getString("trainStatus"));
						mContent.setLongitude(subObj
								.getString("longitude"));
						mContent.setLatitude(subObj
								.getString("latitude"));
						mContent.setStationSpace(subObj
								.getInt("stationSpace"));
						mContent.setLateTime(TimeUtil.get_T_MSeconds(subObj
								.getString("lateTime")));
						mContent.setUserAddTrain(subObj.getInt("userAddTrain"));
						mContent.setUserOnTrain(subObj.getInt("userOnTrain"));
						mContent.setRequested(true);
						break;
					}
				} else {
					return null;
				}
				return "";
			}

			@Override
			protected void myOnPostExecute(Object result) {
				if (result != null) {
					updateRTMsgViews();
					//如果无Dialog显示则是上拉下拉刷新.
					if (type == 0){
						showMsg("数据已更新");
					}
				} else {
					showMsg("网络不稳定,无法更新车次信息" + SF.FAIL);
				}
				showContentView();
			}

			@Override
			protected void onException(Exception e) {
				showContentView();
				showMsg("更新车次信息时出错" + SF.FAIL);
			}
		}.execute(this);
	}

	private void updateRTMsgViews() {
		switch (mContent.getSourceType()) {
		case 0:
			// 从时间区间推算而知
			tvTrainStatus.setText(mContent.getTrainStatus()); // 晢时两种数据来源类型处理方式相同
			break;
		case 1:
			// 由用户数据推算而知
			tvTrainStatus.setText(mContent.getTrainStatus()); // 晢时两种数据来源类型处理方式相同
			break;
		}
		// 显示实时数据
		SimpleDateFormat df1 = TimeUtil.getDTFormat();
//		String strLateTime = "<u>"+TimeUtil.get_T_Str(mContent.getLateTime())+"</u>";
		String strLateTime = TimeUtil.get_T_Str((int)(mContent.getLateTime()/1000));
		switch (mContent.getMsgType()) {
		case 0:
			if (TextUtils.isEmpty(strLateTime)){
				break;
			}
			tvStationSpaceTip.setText("距出发站还有:");
			tvTimeTip.setText("正点发车时间:");
			tvLateTimeTip.setText("预计发车晚点:");
			tvPredictTimeTip.setText("预计发车时间:");
			tvRemainingTimeTip.setText("距离发车还有:");

			tvStationSpace.setText(mContent.getStationSpace() + "个站点");
			tvLateTime.setText(Html.fromHtml(strLateTime));
			tvTime.setText(mContent.getStartTime());
			try {
				long predictTime=0;
				predictTime = TimeUtil.getDTFormat().parse(mContent.getStartTime()).getTime();
				predictTime += mContent.getLateTime();
				mContent.setPredictTime(TimeUtil.getFmt_DT_Str(predictTime));
				tvPredictTime.setText(mContent.getPredictTime());
			} catch (ParseException e) {
				e.printStackTrace();
			}
			mContent.setRemainingTime(TimeUtil.getFmt_DT_DT_DiffStr(mContent.getPredictTime(), df1.format(new Date())));
			tvRemainingTime.setText(TimeUtil.get_T_Str(mContent
					.getRemainingTime()));

			mContent.setUserStatus(0);
			btnLocation.setEnabled(true);
			tlytArea2.setVisibility(View.VISIBLE);
//			llytArea3.setVisibility(View.VISIBLE);
			break;
		case 1:
			if (TextUtils.isEmpty(strLateTime)){
				break;
			}
			tvStationSpaceTip.setText("距目的站还有:");
			tvLateTimeTip.setText("预计到站晚点:");
			tvTimeTip.setText("正点到站时间:");
			tvPredictTimeTip.setText("预计到站时间:");
			tvRemainingTimeTip.setText("距离到站还有:");

			tvStationSpace.setText(mContent.getStationSpace() + "个站点");
			tvLateTime.setText(Html.fromHtml(strLateTime));
			tvTime.setText(mContent.getEndTime());
			long predictTime1;
			try {
				predictTime1 = TimeUtil.getDTFormat().parse(mContent.getEndTime()).getTime();
				predictTime1 += mContent.getLateTime();
				mContent.setPredictTime(TimeUtil.getFmt_DT_Str(predictTime1));
				tvPredictTime.setText(mContent.getPredictTime());
			} catch (ParseException e) {
				e.printStackTrace();
			}
			mContent.setRemainingTime(TimeUtil.getFmt_DT_DT_DiffStr(mContent.getPredictTime(), df1.format(new Date())));
			tvRemainingTime.setText(TimeUtil.get_T_Str(mContent.getRemainingTime()));

			mContent.setUserStatus(1);
			btnLocation.setEnabled(true);
			tlytArea2.setVisibility(View.VISIBLE);
//			llytArea3.setVisibility(View.VISIBLE);
			break;
		case 2:
			tvTimeTip.setText("正点到站时间:");
			tvTime.setText(mContent.getEndTime());
			tvTrainStatus.setText("列车已到目的站");
			
			mContent.setUserStatus(2);
			tlytArea2.setVisibility(View.GONE);
			llytArea3.setVisibility(View.GONE);
			break;
		case 3:
			tvTrainStatus.setText("列车尚未运行");
			tlytArea2.setVisibility(View.GONE);
			llytArea3.setVisibility(View.GONE);
			break;
		}
		
		//1.0版本暂不显示信息统计
//		// 显示用户数量
//		tvUserAddTrain.setText(String.valueOf(mContent.getUserAddTrain()));
//		tvUserOnTrain.setText(String.valueOf(mContent.getUserOnTrain()));
		llytArea3.setVisibility(View.GONE);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.trainInfo_btnTrainNum:
			Intent intent = new Intent(this.getActivity(), TrainDetailAty.class);
			Train train = new Train();
			train.setTrainNum(mContent.getTrainNum());
			intent.putExtra(TrainDetailAty.TRAIN, train);
			getActivity().startActivity(intent);
			break;
		case R.id.trainInfo_btnLocation:
			showLocation();
			break;
		case R.id.trainInfo_btnWeather:
			//天气
			Intent weatherIntent = new Intent(this.getActivity(),WeatherInfoAty.class);
			weatherIntent.putExtra(WeatherInfoAty.EXTRA_CITY, mContent.getEndStation());
			getActivity().startActivity(weatherIntent);
			break;
		case R.id.trainInfo_tvLateTime:
			showRCDialog();
			break;
		}
	}

	private void showRCDialog() {
		if (mLTimeDlg != null){
			mLTimeDlg.dismiss();
		}
		mLTimeDlg = new RCodeDialog(getActivity(), "12306晚点", null, new RCodeDialogListener(){
			boolean isSucc = false;
			@Override
			public Bitmap onRCodeRequest(DialogInterface dlg) {
				mBInfo.setCxlx(0);
				mBInfo.setTo_station(mContent.getEndStation());
				mBInfo.setStationTrainCode(mContent.getTrainNum());
				return A6Util.getLTimeRandCode(mBInfo);
			}

			@Override
			public void onClick(DialogInterface dlg, int which) {
				if (mLTimeDlg == null){
					return;
				}
				switch(which){
				case DialogInterface.BUTTON_POSITIVE:
					mBInfo.setRand_code(mLTimeDlg.getRCodeText().toString());
					new VoidAsyncTask() {
						long arriveTime = 0;
						@Override
						protected Object doInBackground(Object... params) {
							if (TextUtils.isEmpty(mBInfo.getToStationTelecode())){
								MyDatabase myDB = new MyDatabase(getActivity());
								mBInfo.setToStationTelecode(myDB.getStationTeleCode(mBInfo.getTo_station()));
								myDB.closeDB();
							}
							String strStationTrainCode = A6Util.getA6StationTrainCode(mBInfo);
							L.i("station_train_code:"+strStationTrainCode);
							if (strStationTrainCode != null){
								mBInfo.setStationTrainCode(strStationTrainCode);
							}
							A6Info a6Json = A6Util.getLTime(mBInfo);
							try {
								JSONObject jsonObj = new JSONObject(a6Json.getData());
								String strMsg = jsonObj.optString("message");
								if (!TextUtils.isEmpty(strMsg)){
									return strMsg;
								}
								String data = jsonObj.getString("data");
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
									showLongMsg(result.toString());
									//设置值
										try{
											//提取正点时间
											String strTime = TimeUtil.getTFormat().format(TimeUtil.getDTFormat().parse(mContent.getEndTime()));
											long intTime = TimeUtil.get_T_MSeconds(strTime);
											long diffTime = arriveTime-intTime;
											//10:00-12:00，预计到达时间为00:00-02:00，如果在此时间段内则
											//认为是隔天到达
											if (diffTime <0 && (arriveTime>=0 && arriveTime <= 2*3600*1000)
													&& ((intTime >=10*3600*1000 && intTime <=24*3600*1000) || (intTime==0))){
												diffTime += 24*3600*1000;
											}
											//超时两个小时视为无效
											if (Math.abs(diffTime) > 2*3600*1000){
												diffTime = 0;
											}
											mContent.setLateTime(diffTime);
											updateRTMsgViews();
										}catch(Exception e){
											showMsg("数据有误");
											e.printStackTrace();
										}
								}else{
									showMsg(result.toString());
								}
							}
						};
					}.start();
					break;
				case DialogInterface.BUTTON_NEGATIVE:
					break;
				}
				mLTimeDlg = null;
			}
			
		});
		mLTimeDlg.show();
	}

	@Override
	public void onDestroy() {
		if (mLTimeDlg != null){
			mLTimeDlg.dismiss();
		}
		super.onDestroy();
	}
	public void showLocation() {
//		if(mContent.getMsgType()==2||mContent.getMsgType()==3){
//			//定位为不可用状态
//			Toast.makeText(getActivity(), "目前列车未运行"+SF.FAIL, Toast.LENGTH_SHORT).show();
//			return;
//		}
//		if(!HttpUtil.isNetworkConnected(getActivity())){
//			Toast.makeText(getActivity(), "网络不可用，请检查您的网络", Toast.LENGTH_SHORT).show();
//			return;
//		}
//		Intent intent1 = new Intent(this.getActivity(),
//				TrainLocationAty.class);
//		Bundle bundle1 = new Bundle();
//		// 传递车次
//		bundle1.putString(TrainLocationAty.TRAIN_NUM,
//				mContent.getTrainNum());
//		// 传递列车坐标到TrainLocationAty.
//		L.i("longitude:" + mContent.getLongitude() + ",latitude:"
//				+ mContent.getLatitude());
//		try {
//			double dLongitude = Double.valueOf(mContent.getLongitude());
//			double dLatitude = Double.valueOf(mContent.getLatitude());
//			if (dLongitude == 0 || dLatitude == 0) {
//				SimpleDialogFragment
//						.createBuilder(
//								TrainInfoFragment.this.getActivity(),
//								TrainInfoFragment.this.getActivity()
//										.getSupportFragmentManager())
//						.setTitle("提示")
//						.setMessage(
//								"暂没有"
//										+ mContent.getTrainNum()
//										+ "次列车的实时定位数据，"
//										+ "我们正在加紧收集用户反馈数据与12306官方数据，请多给系统几天时间吧^_^")
//						.setPositiveButtonText("确定").show();
//			} else {
//				 bundle1.putInt(TrainLocationAty.LONGITUDE,
//				 (int)(dLongitude*1E6));
//				 bundle1.putInt(TrainLocationAty.LATITUDE,
//				 (int)(dLatitude*1E6));
//				intent1.putExtras(bundle1);
//				getActivity().startActivity(intent1);
//			}
//		} catch (Exception e) {
//			showMsg("定位数据异常" + SF.FAIL);
//			e.printStackTrace();
//		}
	}

	private void showMsg( final String string) {
		if (getActivity() != null){
			Toast.makeText(getActivity(), string, Toast.LENGTH_SHORT).show();
		}
	}
	private void showLongMsg( final String string) {
		if (getActivity() != null){
			Toast.makeText(getActivity(), string, Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onFooterRefresh(PullToRefreshView view) {
		mPullToRefreshView.postDelayed(new Runnable() {

			@Override
			public void run() {
				getRTMsgAndThenRefeshViews(0);
			}
		}, 200);
	}

	@Override
	public void onHeaderRefresh(PullToRefreshView view) {
		mPullToRefreshView.postDelayed(new Runnable() {

			@Override
			public void run() {
				getRTMsgAndThenRefeshViews(0);
			}
		}, 200);
	}

	public void showContentView() {
		mPullToRefreshView.onHeaderRefreshComplete();
		mPullToRefreshView.onFooterRefreshComplete();
		mPullToRefreshView.setVisibility(View.VISIBLE);
		mContainer.removeView(mFooterLoadingView);
		mFooterLoadingView = null;
	}
}
