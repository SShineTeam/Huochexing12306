package com.sshine.huochexing.ticketOnline;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.LayoutParams;
import com.actionbarsherlock.internal.widget.IcsAdapterView;
import com.actionbarsherlock.internal.widget.IcsAdapterView.OnItemSelectedListener;
import com.actionbarsherlock.internal.widget.IcsLinearLayout;
import com.actionbarsherlock.internal.widget.IcsSpinner;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sshine.huochexing.R;
import com.sshine.huochexing.adapter.A6OrderExpandableAdapter;
import com.sshine.huochexing.base.BaseAty;
import com.sshine.huochexing.bean.A6Info;
import com.sshine.huochexing.bean.BookingInfo;
import com.sshine.huochexing.bean.OrderDBInfo;
import com.sshine.huochexing.bean.StationTrainDTOInfo;
import com.sshine.huochexing.bean.TicketAffirmInfo;
import com.sshine.huochexing.bean.TicketInfo;
import com.sshine.huochexing.model.CustomDialog;
import com.sshine.huochexing.trainInfos.AddInfoAty;
import com.sshine.huochexing.utils.A6Util;
import com.sshine.huochexing.utils.MyApp;
import com.sshine.huochexing.utils.MyUtils;
import com.sshine.huochexing.utils.PayA6OrderHelper;
import com.sshine.huochexing.utils.TimeUtil;
import com.sshine.huochexing.value.SF;
import com.umeng.analytics.MobclickAgent;

import eu.inmite.android.lib.dialogs.ISimpleDialogListener;
import eu.inmite.android.lib.dialogs.SimpleDialogFragment;

public class A6OrderAty extends BaseAty implements OnClickListener,
		ISimpleDialogListener {
	private BookingInfo mBInfo = MyApp.getInstance().getCommonBInfo();
	private List<OrderDBInfo> mLstODBInfos = new ArrayList<OrderDBInfo>();
	private TextView tvEmptyView;
	private Button btnCancel;
	private Button btnPay;
	private String[] mLocations = { "未完成", "未出行", "历史订单", "其它订单" };

	public static final String EXTRA_PRE_LOAD_DATA_INDEX = "extraPreLoadDataIndex";
	private static final int REQUEST_CANCEL_ORDER = 1;
	private static final int REQUEST_PAY = 2;
	private static final int MSG_CANCEL_ORDER_FAIL = 1;
	private static final int MSG_CANCEL_ORDER_SUCCESS = 2;
	private static final int MSG_SHOW_REFUND_TICKET_TIP = 3;
	private static final int MSG_RETURN_TICKET_SUCCESS = 4;

	public static final int CONTEXT_GROUP_0 = 0;
	public static final int CONTEXT_GROUP_1 = 1;
	public static final int CONTEXT_ITEM_ADD_TRAIN_INFO = 1;
	public static final int CONTEXT_ITEM_REFUND_TICKET = 2;
	public static final int CONTEXT_ITEM_RESIGN = 3;

	private A6OrderExpandableAdapter mAdapter;
	private LinearLayout llytOperate;
	private int mNavigationIndex;
	private ExpandableListView lvOrders;

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case A6Util.MSG_QUERY_ORDER_SUCCESS:
				showContentView(true);
				if (mNavigationIndex == 0) {
					setPanelGone(false);
				} else {
					setPanelGone(true);
				}
				lvOrders.expandGroup(0);
				mAdapter.notifyDataSetChanged();
				tipLostTime();
				break;
			case A6Util.MSG_NO_COMPLETE_ORDER_EMPTY:
				switch (mNavigationIndex) {
				case 0:
					tvEmptyView.setText("您没有未完成订单");
					break;
				case 1:
					tvEmptyView.setText("您没有未出行订单");
					break;
				case 2:
					tvEmptyView.setText("您没有历史订单");
					break;
				}
				showContentView(true);
				setPanelGone(true);
				mAdapter.notifyDataSetChanged();
				break;
			case A6Util.MSG_NO_LOGIN:
				startActivity(new Intent(A6OrderAty.this, A6LoginAty.class));
				A6OrderAty.this.finish();
				break;
			case MSG_CANCEL_ORDER_FAIL:
				showMsg("取消订单失败" + SF.FAIL);
				btnCancel.setEnabled(true);
				break;
			case MSG_CANCEL_ORDER_SUCCESS:
				btnCancel.setEnabled(true);
				showMsg("已成功取消订单" + SF.SUCCESS);
				setPanelGone(true);
				mLstODBInfos.clear();
				mAdapter.notifyDataSetChanged();
				break;
			case MSG_SHOW_REFUND_TICKET_TIP:
				showRefundTicktTip((TicketAffirmInfo) msg.obj);
				break;
			case MSG_RETURN_TICKET_SUCCESS:
				requestData(true);
				break;
			}
		}
	};

	private void tipLostTime() {
		try {
			TicketInfo tInfo = mLstODBInfos.get(0).getTickets().get(0);
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS",
					Locale.CHINA);
			Date nowTime = new Date();
			Date loseTime = sdf1.parse(tInfo.getLose_time());
			long diffSS = loseTime.getTime() - nowTime.getTime();
			if (diffSS >= 0) {
				CountDownTimer cdTimer = new CountDownTimer(diffSS, 1000) {

					@Override
					public void onFinish() {
						btnPay.setText("订单信息已过期");
						btnPay.setEnabled(false);
					}

					@Override
					public void onTick(long millisUntilFinished) {
						String str1 = TimeUtil
								.getFmt_M_S_Str(millisUntilFinished / 1000);
						// tvPay.setText("立即支付(" + str1+")");
						btnPay.setText("剩余时间(" + str1 + ")");
					}
				};
				cdTimer.start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle("12306订单");
		setContentView(R.layout.aty_a6_order);
		initContainerViews();
		initViews();
		initListActionBar();
	}

	private void initListActionBar() {
		ActionBar actionBar = getSupportActionBar();
		Context context = getSupportActionBar().getThemedContext();
		ArrayAdapter<CharSequence> adpater = new ArrayAdapter<CharSequence>(
				context, R.layout.sherlock_spinner_dropdown_item, mLocations);
		adpater.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);
		IcsSpinner spinner = new IcsSpinner(this, null,
				R.attr.actionDropDownStyle);
		spinner.setAdapter(adpater);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(IcsAdapterView<?> parent, View view,
					int position, long id) {
				mNavigationIndex = position;
				doHeaderTask();
			}

			@Override
			public void onNothingSelected(IcsAdapterView<?> parent) {
			}
		});
		if (mNavigationIndex >= 0 && mNavigationIndex < 3){
			spinner.setSelection(mNavigationIndex);
		}

		IcsLinearLayout listNavLayout = (IcsLinearLayout) getLayoutInflater()
				.inflate(R.layout.abs__action_bar_tab_bar_view, null);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
		params.gravity = Gravity.CENTER;
		listNavLayout.addView(spinner, params);
		actionBar.setCustomView(listNavLayout, new ActionBar.LayoutParams(
				Gravity.RIGHT));
		actionBar.setDisplayShowCustomEnabled(true);
	}

	private void initViews() {
		mNavigationIndex = getIntent()
				.getIntExtra(EXTRA_PRE_LOAD_DATA_INDEX, 0);
		llytOperate = (LinearLayout) findViewById(R.id.operate);
		btnCancel = (Button) findViewById(R.id.cancel);
		btnCancel.setOnClickListener(this);
		btnPay = (Button) findViewById(R.id.pay);
		btnPay.setOnClickListener(this);
		tvEmptyView = (TextView) findViewById(R.id.emptyView);
		lvOrders = (ExpandableListView) findViewById(R.id.orders);

		lvOrders.setEmptyView(tvEmptyView);
		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		View footerView = inflater.inflate(R.layout.fv_a6_order, null);
		lvOrders.addFooterView(footerView);
		mAdapter = new A6OrderExpandableAdapter(this, mLstODBInfos);
		lvOrders.setAdapter(mAdapter);

		// btnPay.setEnabled(false);
		setPanelGone(true);
	}

	private void setPanelGone(boolean isGone) {
		int visibility = isGone ? View.GONE : View.VISIBLE;
		llytOperate.setVisibility(visibility);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.cancel:
			SimpleDialogFragment
					.createBuilder(this, getSupportFragmentManager())
					.setTitle("提示")
					.setMessage(
							getResources().getString(R.string.cancelA6OrderTip))
					.setPositiveButtonText("确定").setNegativeButtonText("取消")
					.setRequestCode(REQUEST_CANCEL_ORDER).show();
			break;
		case R.id.pay:
			MyUtils.startApp(this, "com.MobileTicket", "未找到12306官方客户端");
			// initPayEnvironment();
			break;
		}
	}

	// 初始化支付环境
	private void initPayEnvironment() {
		if (mLstODBInfos == null || mLstODBInfos.size() == 0) {
			showMsg("订单信息错误" + SF.FAIL);
			return;
		}
		startHandle("正在准备订单支付信息...", new Runnable() {
			@Override
			public void run() {
				OrderDBInfo orderDBInfo = mLstODBInfos.get(0);
				if (orderDBInfo == null) {
					return;
				}
				PayA6OrderHelper paoHelper = new PayA6OrderHelper(orderDBInfo
						.getSequence_no());
				boolean isOK = paoHelper.continuePayNoCompleteMyOrder(mBInfo);
				if (isOK) {
					if (paoHelper.payOrderInit(mBInfo)) {
						sendDismissDialog();
						Intent intent = new Intent(A6OrderAty.this,
								TicketOnlineAty.class);
						intent.putExtra(TicketOnlineAty.EXTRA_START_PAGE,
								paoHelper.getPayUrl());
						intent.putExtra(TicketOnlineAty.EXTRA_POST_PARAMS,
								paoHelper.getA6PayPostParams());
						startActivityForResult(intent, REQUEST_PAY);
					} else {
						sendToast("初始化订单支付信息时出错" + SF.FAIL);
					}
				} else {
					sendToast("此订单已不可支付!");
				}
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_PAY:
			// 刷新订单数据
			requestData(true);
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void requestData(boolean isShowLoading) {
		if (isShowLoading) {
			showLoadingView();
		}
		if (mLstODBInfos != null) {
			mLstODBInfos.clear();
		}
		new Thread(new Runnable() {
			public void run() {
				if (A6Util.isNeedCheckUser(mBInfo)) {
					boolean isLogin = A6Util.checkUser(mBInfo);
					if (!isLogin) {
						sendDismissDialog();
						Message msg = mHandler
								.obtainMessage(A6Util.MSG_NO_LOGIN);
						mHandler.sendMessage(msg);
						return;
					}
				}
				List<OrderDBInfo> lstODBInfos = null;
				switch (mNavigationIndex) {
				case 0:
					lstODBInfos = queryMyOrderNoComplete(mBInfo);
					if (lstODBInfos == null) {
						showContentView(false);
						sendToast("请求未完成订单数据时出错" + SF.FAIL);
						return;
					}
					break;
				case 1:
					lstODBInfos = queryMyOrder(mBInfo, mNavigationIndex);
					if (lstODBInfos == null) {
						showContentView(false);
						sendToast("请求未出行订单数据时出错" + SF.FAIL);
						return;
					}
				case 2:
					lstODBInfos = queryMyOrder(mBInfo, mNavigationIndex);
					if (lstODBInfos == null) {
						showContentView(false);
						sendToast("请求历史订单数据时出错" + SF.FAIL);
						return;
					}
					break;
				}
				mLstODBInfos.clear();
				for (OrderDBInfo oDBInfo : lstODBInfos) {
					mLstODBInfos.add(oDBInfo);
				}
				if (mLstODBInfos.size() == 0) {
					Message msg = mHandler
							.obtainMessage(A6Util.MSG_NO_COMPLETE_ORDER_EMPTY);
					mHandler.sendMessage(msg);
				} else {
					Message msg = mHandler
							.obtainMessage(A6Util.MSG_QUERY_ORDER_SUCCESS);
					mHandler.sendMessage(msg);
				}
			};
		}).start();
	}

	@Override
	public void onPositiveButtonClicked(int requestCode) {
		switch (requestCode) {
		case REQUEST_CANCEL_ORDER:
			btnCancel.setEnabled(false);
			startHandle("正在取消订单...", new Runnable() {
				public void run() {
					Boolean isOK = cancelOrder(mBInfo);
					sendDismissDialog();
					if (isOK) {
						Message msg = mHandler
								.obtainMessage(MSG_CANCEL_ORDER_SUCCESS);
						mHandler.sendMessage(msg);
					} else {
						Message msg = mHandler
								.obtainMessage(MSG_CANCEL_ORDER_FAIL);
						mHandler.sendMessage(msg);
					}
				};
			});
			break;
		}
	}

	@Override
	public boolean onContextItemSelected(android.view.MenuItem item) {
		switch (item.getGroupId()) {
		case CONTEXT_GROUP_0:
			handleGroup0(item);
			break;
		case CONTEXT_GROUP_1:
			break;
		}
		return super.onContextItemSelected(item);
	}

	public void handleGroup0(android.view.MenuItem item) {
		TicketInfo tInfo = mAdapter.getCurrTInfo();
		switch (item.getItemId()) {
		case CONTEXT_ITEM_ADD_TRAIN_INFO:
			if (tInfo != null) {
				StationTrainDTOInfo stDTOInfo = tInfo.getStationTrainDTO();
				Intent intent = new Intent(this, AddInfoAty.class);
				intent.putExtra(AddInfoAty.TRAIN_NUM,
						stDTOInfo.getStation_train_code());
				intent.putExtra(AddInfoAty.START_STATION,
						stDTOInfo.getFrom_station_name());
				intent.putExtra(AddInfoAty.END_STATION,
						stDTOInfo.getTo_station_name());
				intent.putExtra(AddInfoAty.EXTRA_START_TIME,
						tInfo.getStart_train_date_page());
				intent.putExtra(AddInfoAty.DATA_SOURCE,
						AddInfoAty.DATA_FROM_NEWWORK);
				startActivity(intent);
			}
			break;
		case CONTEXT_ITEM_REFUND_TICKET:
			tInfo.setWantReturn(true);
			refundTicket(tInfo);
			break;
		case CONTEXT_ITEM_RESIGN:
			tInfo.setWantResign(true);
			final OrderDBInfo oDBInfo = mAdapter.getCurrOrderDBInfo();
			startHandle(null, new Runnable() {
				@Override
				public void run() {
					boolean isOK = resignTicket(mBInfo, oDBInfo);
					sendDismissDialog();
					if (isOK) {
						// TODO 待处理
						showMsg("调试中...");
					} else {
						sendToast("获取改签信息失败" + SF.FAIL);
					}
				}
			});
			break;
		}
	}

	private void refundTicket(final TicketInfo tInfo) {
		if (tInfo.isWantReturn()) {
			tInfo.setWantReturn(false);
			startHandle(null, new Runnable() {
				@Override
				public void run() {
					TicketAffirmInfo taInfo = returnTicketAffirm(mBInfo, tInfo);
					sendDismissDialog();
					if (taInfo == null) {
						sendToast("初始化退票信息时出错");
					} else {
						Message msg = mHandler
								.obtainMessage(MSG_SHOW_REFUND_TICKET_TIP);
						msg.obj = taInfo;
						mHandler.sendMessage(msg);
					}
				}
			});
		}
	}

	private void showRefundTicktTip(TicketAffirmInfo taInfo) {
		if (taInfo == null) {
			return;
		}
		String strMsg = "退票信息:\r\n";
		strMsg += "车票票款：<b>" + taInfo.getTicket_price() + "</b>元，退票费：<b>"
				+ taInfo.getReturn_cost() + "</b>元，应退票款：<font color='#ff8c00'>"
				+ taInfo.getReturn_price()
				+ "</font>元，实际核收退票费及应退票款12306会按最终交易时间计算。";
		CustomDialog dlg = new CustomDialog.Builder(this,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case DialogInterface.BUTTON_POSITIVE:
							startHandle(null, new Runnable() {
								@Override
								public void run() {
									boolean isOK = returnTicket(mBInfo);
									if (isOK) {
										Message msg = mHandler
												.obtainMessage(MSG_RETURN_TICKET_SUCCESS);
										mHandler.sendMessage(msg);
										sendToast("退票成功" + SF.SUCCESS);
									} else {
										sendToast("退票失败" + SF.FAIL);
									}
								}
							});
							break;
						}
					}

				}).setTitle("确认要退票吗？").setMessage(Html.fromHtml(strMsg))
				.setPositiveButton("确认").setNagativeButton("取消").create();
		dlg.show();
	}

	@Override
	public void onNegativeButtonClicked(int requestCode) {
	}

	private boolean cancelOrder(BookingInfo bInfo) {
		String url = "https://kyfw.12306.cn/otn/queryOrder/cancelNoCompleteMyOrder";
		List<NameValuePair> lstParams = new ArrayList<NameValuePair>();
		lstParams.add(new BasicNameValuePair("sequence_no", mLstODBInfos.get(0)
				.getSequence_no()));
		lstParams.add(new BasicNameValuePair("cancel_flag", "cancel_order"));
		lstParams.add(new BasicNameValuePair("_json_att", ""));
		try {
			A6Info a6Json = A6Util
					.post(bInfo,
							A6Util.makeRefererColl("https://kyfw.12306.cn/otn/queryOrder/initNoComplete"),
							url, lstParams);
			JSONObject jsonObj = new JSONObject(a6Json.getData());
			if (jsonObj.getString("existError").equals("N")) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private List<OrderDBInfo> queryMyOrderNoComplete(BookingInfo bInfo) {
		String url = "https://kyfw.12306.cn/otn/queryOrder/queryMyOrderNoComplete";
		List<NameValuePair> lstParams = new ArrayList<NameValuePair>();
		lstParams.add(new BasicNameValuePair("_json_att", ""));
		try {
			A6Info a6Json = A6Util
					.post(bInfo,
							A6Util.makeRefererColl("https://kyfw.12306.cn/otn/queryOrder/initNoComplete"),
							url, lstParams);
			if ("".equals(a6Json.getData())) {
				return (new ArrayList<OrderDBInfo>());
			} else {
				int index1 = a6Json.getData().indexOf("[");
				int index2 = a6Json.getData().lastIndexOf("]") + 1;
				String strOrderDBList = a6Json.getData().substring(index1,
						index2);
				List<OrderDBInfo> lstODBInfos = A6Util.getGson().fromJson(
						strOrderDBList, new TypeToken<List<OrderDBInfo>>() {
						}.getType());
				return lstODBInfos;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private List<OrderDBInfo> queryMyOrder(BookingInfo bInfo, int index) {
		String url = "https://kyfw.12306.cn/otn/queryOrder/queryMyOrder";
		List<NameValuePair> lstParams = new ArrayList<NameValuePair>();
		Calendar c = Calendar.getInstance(Locale.CHINA);
		c.setTimeInMillis(System.currentTimeMillis());
		c.add(Calendar.YEAR, -3);
		String strQueryStartDate = TimeUtil.getDFormat().format(c.getTime());
		lstParams.add(new BasicNameValuePair("queryType", "1"));
		lstParams.add(new BasicNameValuePair("queryStartDate",
				strQueryStartDate));
		lstParams.add(new BasicNameValuePair("come_from_flag", "my_order"));
		lstParams.add(new BasicNameValuePair("pageSize", "8"));
		lstParams.add(new BasicNameValuePair("pageIndex", "0"));
		String query_where = "";
		String strQueryEndDate = "";
		c.add(Calendar.YEAR, 3);
		switch(index){
		case 1:
			strQueryEndDate = TimeUtil.getDFormat().format(c.getTime());
			query_where = "G";
			break;
		case 2:
			c.add(Calendar.DAY_OF_MONTH, -1);
			strQueryEndDate = TimeUtil.getDFormat().format(c.getTime());
			query_where = "H";
			break;
		}
		lstParams.add(new BasicNameValuePair("queryEndDate", strQueryEndDate));
		lstParams.add(new BasicNameValuePair("query_where", query_where));
		lstParams.add(new BasicNameValuePair("sequeue_train_name", ""));
		try {
			A6Info a6Json = A6Util
					.post(bInfo,
							A6Util.makeRefererColl("https://kyfw.12306.cn/otn/queryOrder/init"),
							url, lstParams);
			if ("".equals(a6Json.getData())) {
				return (new ArrayList<OrderDBInfo>());
			} else {
				int index1 = a6Json.getData().indexOf("[");
				int index2 = a6Json.getData().lastIndexOf("]") + 1;
				String strOrderDBList = a6Json.getData().substring(index1,
						index2);
				List<OrderDBInfo> lstODBInfos = (new Gson()).fromJson(
						strOrderDBList, new TypeToken<List<OrderDBInfo>>() {
						}.getType());
				return lstODBInfos;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private TicketAffirmInfo returnTicketAffirm(BookingInfo bInfo,
			TicketInfo tInfo) {
		String url = "https://kyfw.12306.cn/otn/queryOrder/returnTicketAffirm";
		List<NameValuePair> lstParams = new ArrayList<NameValuePair>();
		lstParams.add(new BasicNameValuePair("sequence_no", tInfo
				.getSequence_no()));
		lstParams.add(new BasicNameValuePair("batch_no", tInfo.getBatch_no()));
		lstParams.add(new BasicNameValuePair("coach_no", tInfo.getCoach_no()));
		lstParams.add(new BasicNameValuePair("seat_no", tInfo.getSeat_no()));
		lstParams.add(new BasicNameValuePair("start_train_date_page", tInfo
				.getStart_train_date_page()));
		lstParams.add(new BasicNameValuePair("train_code", tInfo
				.getStationTrainDTO().getStation_train_code()));
		lstParams.add(new BasicNameValuePair("coach_name", tInfo
				.getCoach_name()));
		lstParams
				.add(new BasicNameValuePair("seat_name", tInfo.getSeat_name()));
		lstParams.add(new BasicNameValuePair("seat_type_name", tInfo
				.getSeat_type_name()));
		lstParams.add(new BasicNameValuePair("train_date", tInfo
				.getTrain_date()));
		lstParams.add(new BasicNameValuePair("from_station_name", tInfo
				.getStationTrainDTO().getFrom_station_name()));
		lstParams.add(new BasicNameValuePair("to_station_name", tInfo
				.getStationTrainDTO().getTo_station_name()));
		lstParams.add(new BasicNameValuePair("start_time", tInfo
				.getStationTrainDTO().getStart_time()));
		lstParams.add(new BasicNameValuePair("passenger_name", tInfo
				.getPassengerDTO().getPassenger_name()));
		lstParams.add(new BasicNameValuePair("_json_att", ""));
		try {
			A6Info a6Json = A6Util
					.post(bInfo,
							A6Util.makeRefererColl("https://kyfw.12306.cn/otn/queryOrder/init"),
							url, lstParams);
			TicketAffirmInfo taInfo = A6Util.getGson().fromJson(
					a6Json.getData(), TicketAffirmInfo.class);
			return taInfo;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private boolean returnTicket(BookingInfo bInfo) {
		String url = "https://kyfw.12306.cn/otn/queryOrder/returnTicket";
		List<NameValuePair> lstParams = new ArrayList<NameValuePair>();
		lstParams.add(new BasicNameValuePair("_json_att", ""));
		try {
			String strHtml = bInfo
					.getHttpHelper()
					.post(A6Util
							.makeRefererColl("https://kyfw.12306.cn/otn/queryOrder/init"),
							url, lstParams);
			if (strHtml != null && strHtml.indexOf("退票成功") > 0) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/*
	 * 改签
	 */
	private boolean resignTicket(BookingInfo bInfo, OrderDBInfo oDBInfo) {
		if (mLstODBInfos == null) {
			return false;
		}
		String url = "https://kyfw.12306.cn/otn/queryOrder/resginTicket";
		List<NameValuePair> lstParams = new ArrayList<NameValuePair>();
		StringBuffer sbKey = new StringBuffer();
		String sequence_no = null;
		List<TicketInfo> lstTInfos = oDBInfo.getTickets();
		if (lstTInfos == null) {
			return false;
		}
		for (TicketInfo tInfo : lstTInfos) {
			if (tInfo.isWantResign()) {
				tInfo.setWantResign(false);
				if (sequence_no == null) {
					sequence_no = tInfo.getSequence_no();
				}
				sbKey.append(tInfo.getSequence_no()).append(",")
						.append(tInfo.getBatch_no()).append(",")
						.append(tInfo.getCoach_no()).append(",")
						.append(tInfo.getSeat_no()).append(",")
						.append(tInfo.getStart_train_date_page()).append("#");
			}
		}
		if (sbKey.length() == 0) {
			return false;
		}
		lstParams.add(new BasicNameValuePair("ticketkey", sbKey.toString()));
		lstParams.add(new BasicNameValuePair("sequenceNo", sequence_no));
		lstParams.add(new BasicNameValuePair("_json_att", ""));
		try {
			A6Info a6Json = A6Util
					.post(bInfo,
							A6Util.makeRefererColl("https://kyfw.12306.cn/otn/queryOrder/init"),
							url, lstParams);
			JSONObject jsonObj = new JSONObject(a6Json.getData());
			String existError = jsonObj.optString("existError", null);
			if (existError == null) {
				return false;
			} else if (existError.equals("N")) {
				return true;
			}
		} catch (Exception e) {
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
		switch (mNavigationIndex) {
		case 0:
		case 1:
		case 2:
			requestData(true);
			break;
		case 3:
			startActivity(new Intent(A6OrderAty.this, OrderAty.class));
			break;
		}
	}

	@Override
	public void doFooterTask() {
	}
}
