package com.sshine.huochexing.trainSchedule;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.MenuItem;
import com.sshine.huochexing.R;
import com.sshine.huochexing.bean.QueryLeftNewOptionInfo;
import com.sshine.huochexing.model.FavoriteCharacterDialogFragment;
import com.sshine.huochexing.model.MultiChoiceFragment;
import com.sshine.huochexing.utils.A6Util;
import com.sshine.huochexing.utils.ImageUtil;
import com.sshine.huochexing.utils.MyApp;
import com.sshine.huochexing.utils.MyUtils;
import com.sshine.huochexing.utils.PersistentUtil;
import com.sshine.huochexing.utils.TimeUtil;
import com.sshine.huochexing.utils.TrainHelper;
import com.sshine.huochexing.value.SF;
import com.sshine.huochexing.value.StoreValue;
import com.sshine.huochexing.value.TT;

public class ZhanZhanQueryFragment extends SherlockFragment implements
		OnClickListener {
	public static final String EXTRA_MODE = "extraMode";
	private QueryLeftNewOptionInfo mQLNOInfo;
	private Button btnQueryType, btnFrom, btnTo, btnDepartureDate,
			btnReturnDate, btnTicketType, btnQuery;
	private TextView tvFromTip, tvToTip;
	private TableRow trReturnDate;
	private Button btnFromTimeRange, btnToTimeRange, btnTrainType;
	private ImageView ivSwap;
	private boolean[] mSelectedTrainTypes;
	private TrainHelper mTHelper = new TrainHelper();
	
	public static ZhanZhanQueryFragment getInstance(QueryLeftNewOptionInfo qlnoInfo){
		ZhanZhanQueryFragment f1 = new ZhanZhanQueryFragment();
		f1.mQLNOInfo = qlnoInfo;
		return f1;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_zhanzhan_query, container, false);
		// 查找控件并绑定单击事件
		btnQueryType = (Button) v.findViewById(R.id.zhanzhanQuery_btnQueryType);
		btnQueryType.setOnClickListener(this);
		tvFromTip = (TextView) v
				.findViewById(R.id.zhanzhanQuery_tvFromStationTip);
		tvFromTip.setOnClickListener(this);
		tvToTip = (TextView) v.findViewById(R.id.zhanzhanQuery_tvToStationTip);
		tvToTip.setOnClickListener(this);
		btnFrom = (Button) v.findViewById(R.id.zhanzhanQuery_btnStartStation);
		btnFrom.setOnClickListener(this);
		btnTo = (Button) v.findViewById(R.id.zhanzhanQuery_btnEndStation);
		btnTo.setOnClickListener(this);
		ivSwap = (ImageView) v.findViewById(R.id.zhanzhanQuery_ivSwap);
		ivSwap.setOnClickListener(this);
		btnDepartureDate = (Button) v
				.findViewById(R.id.zhanzhanQuery_btnDepartureDate);
		btnDepartureDate.setOnClickListener(this);
		trReturnDate = (TableRow) v
				.findViewById(R.id.zhanzhanQuery_trReturnDate);
		btnReturnDate = (Button) v
				.findViewById(R.id.zhanzhanQuery_btnReturnDate);
		btnReturnDate.setOnClickListener(this);
		btnTicketType = (Button) v
				.findViewById(R.id.zhanzhanQuery_btnTicketType);
		btnTicketType.setOnClickListener(this);
		btnFromTimeRange = (Button) v.findViewById(R.id.zhanzhanQuery_btnFromTimeRange);
		btnFromTimeRange.setOnClickListener(this);
		btnToTimeRange = (Button)v.findViewById(R.id.zhanzhanQuery_btnToTimeRange);
		btnToTimeRange.setOnClickListener(this);
		btnTrainType = (Button) v.findViewById(R.id.zhanzhanQuery_btnTrainType);
		btnTrainType.setOnClickListener(this);
		btnQuery = (Button) v.findViewById(R.id.zhanzhanQuery_btnQuery);
		btnQuery.setOnClickListener(this);

		if (mQLNOInfo == null){
			mQLNOInfo = (QueryLeftNewOptionInfo) PersistentUtil.readObject(MyApp.getInstance().getPathBaseRoot(StoreValue.QUERY_OPTION_INFO_FILE));
		}
		if (mQLNOInfo == null){
			//给予默认值
			mQLNOInfo = new QueryLeftNewOptionInfo();
		}
		btnQueryType.setEnabled(false);
		btnFrom.setText(mQLNOInfo.getFrom_station_name());
		btnFrom.setTag(mQLNOInfo.getFrom_station_telecode());
		btnTo.setText(mQLNOInfo.getTo_station_name());
		btnTo.setTag(mQLNOInfo.getTo_station_telecode());
		btnQueryType.setText(TT.QUERY_TYPE_KEYS[0]);
		btnQueryType.setTag(TT.QUERY_TYPE_VALUES[0]);
		btnTicketType.setText(TT.QUERY_TICKET_TYPE_KEYS[0]);
		btnTicketType.setTag(TT.QUERY_TICKET_TYPE_VALUES[0]);
		btnFromTimeRange.setText(TT.TIME_RANGE_KEYS[0]);
		btnFromTimeRange.setTag(TT.TIME_RANGE_VALUES[0]);
		btnToTimeRange.setText(TT.TIME_RANGE_KEYS[0]);
		btnToTimeRange.setTag(TT.TIME_RANGE_VALUES[0]);
		mSelectedTrainTypes = new boolean[mTHelper.getTrainTypes().size()];
		for(int i=0; i<mSelectedTrainTypes.length; i++){
			mSelectedTrainTypes[i] = true;
		}
		setTrainTypeTextAndTag();
		// 取得当前日期
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(System.currentTimeMillis());
		c.add(Calendar.DAY_OF_MONTH, 1);
		setDepartureDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

		// 默认为单程查询
		trReturnDate.setVisibility(View.GONE);

		MyUtils.setToogleTextViewStatus(tvFromTip, mQLNOInfo.isFromExactMatch(),
				"出发站:");
		MyUtils.setToogleTextViewStatus(tvToTip, mQLNOInfo.isToExactMatch(), "目的站:");
		this.setHasOptionsMenu(true);
		if (!A6Util.isCanBookingStuTicket(System.currentTimeMillis())){
			btnTicketType.setEnabled(false);
		}
		
		handleResignMode();
		return v;
	}
	/**
	 * 处理改签模式
	 */
	private void handleResignMode() {
		if (mQLNOInfo == null){
			return;
		}
		if (mQLNOInfo.getMode() == QueryLeftNewOptionInfo.MODE_RESIGN){
			btnQueryType.setText(TT.QUERY_TYPE_KEYS[0]);
			btnQueryType.setTag(TT.QUERY_TYPE_VALUES[0]);
			btnQueryType.setEnabled(false);
			btnFrom.setEnabled(false);
			btnTo.setEnabled(false);
		}
	}

	private boolean setTrainTypeTextAndTag() {
		String strText = "";
		int tempCount = 0;
		for(int i=0; i<mSelectedTrainTypes.length; i++){
			if (mSelectedTrainTypes[i]){
				tempCount++;
				strText += mTHelper.getTrainNames().valueAt(i) + ",";
			}
		}
		if (tempCount == 0){
			btnTrainType.setText("请至少选一项");
			btnTrainType.setTag(null);
			return false;
		}else if (tempCount == mSelectedTrainTypes.length){
			btnTrainType.setText("全部");
		}else{
			btnTrainType.setText(strText.substring(0, strText.length() - 1));
		}
		btnTrainType.setTag(mSelectedTrainTypes);
		return true;
	}

	private void setReturnDate(int year,int monthOfYear, int dayOfMonth) {
		Calendar c = Calendar.getInstance();
		c.set(year, monthOfYear, dayOfMonth);
		String strFormat = "yyyy年MM月dd日";
		SimpleDateFormat sdf = new SimpleDateFormat(strFormat, Locale.CHINA);
		btnReturnDate.setText(sdf.format(c.getTime()) + "  "
				+ TimeUtil.getWeek(c.getTime()));
		btnReturnDate.setTag(c.getTimeInMillis() + "");
	}

	private void setDepartureDate(int year,int monthOfYear, int dayOfMonth) {
		Calendar c = Calendar.getInstance();
		c.set(year, monthOfYear, dayOfMonth);
		String strFormat = "yyyy年MM月dd日";
		SimpleDateFormat sdf = new SimpleDateFormat(strFormat, Locale.CHINA);
		btnDepartureDate.setText(sdf.format(c.getTime()) + "  "
				+ TimeUtil.getWeek(c.getTime()));
		btnDepartureDate.setTag(c.getTimeInMillis() + "");
		setReturnDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.getSherlockActivity().finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {

		Intent intent1 = new Intent(this.getActivity(), SelectAty.class);
		intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 不加此标志StationAty有时执行两次finish()才会返回。
		intent1.putExtra(SelectAty.SEARCH_TYPE, SelectAty.SEARCH_STATION);
		switch (v.getId()) {
		case R.id.zhanzhanQuery_btnQueryType:
			FavoriteCharacterDialogFragment.show(this.getActivity(),
					TrainSchAty.REQUEST_QUERY_TYPE, "查询类别", TT.QUERY_TYPE_KEYS);
			break;
		case R.id.zhanzhanQuery_tvFromStationTip:
			boolean status = false;
			status = MyUtils.getToogleTextViewStatus(tvFromTip) == true ? false
					: true;
			mQLNOInfo.setFromExactMatch(status);
			MyUtils.setToogleTextViewStatus(tvFromTip, status, "出发站:");
			if (status) {
				showMsg("已开启出发站精确匹配");
			} else {
				showMsg("已关闭出发站精确匹配");
			}
			break;
		case R.id.zhanzhanQuery_tvToStationTip:
			boolean status1 = false;
			status1 = MyUtils.getToogleTextViewStatus(tvToTip) == true ? false
					: true;
			mQLNOInfo.setToExactMatch(status1);
			MyUtils.setToogleTextViewStatus(tvToTip, status1, "目的站:");
			if (status1) {
				showMsg("已开启目的站精确匹配");
			} else {
				showMsg("已关闭目的站精确匹配");
			}
			break;
		case R.id.zhanzhanQuery_btnStartStation:
			startActivityForResult(intent1, 1);
			break;
		case R.id.zhanzhanQuery_btnEndStation:
			startActivityForResult(intent1, 2);
			break;
		case R.id.zhanzhanQuery_ivSwap:
			// 旋转一圈
			ImageUtil.rotateImageOnce(getActivity(), ivSwap);
			CharSequence csTemp = "";
			Object obj;
			csTemp = btnFrom.getText();
			btnFrom.setText(btnTo.getText());
			btnTo.setText(csTemp);
			obj = btnFrom.getTag();
			btnFrom.setTag(btnTo.getTag());
			btnTo.setTag(obj);
			break;
		case R.id.zhanzhanQuery_btnDepartureDate:
			if (btnDepartureDate.getTag() instanceof String) {
				try{
					long milliseconds = Long.valueOf((String) btnDepartureDate.getTag());
					Calendar c = Calendar.getInstance(Locale.CHINA);
					c.setTimeInMillis(milliseconds);
					new DatePickerDialog(this.getActivity(), new OnDateSetListener() {

						@Override
						public void onDateSet(DatePicker view, int year,
								int monthOfYear, int dayOfMonth) {
							setDepartureDate(year, monthOfYear, dayOfMonth);
						}

					}, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			break;
		case R.id.zhanzhanQuery_btnReturnDate:
			if (btnReturnDate.getTag() instanceof String) {
				try{
					long returnMilliseconds = Long.valueOf((String) btnReturnDate.getTag());
					Calendar returnCalendar = Calendar.getInstance(Locale.CHINA);
					returnCalendar.setTimeInMillis(returnMilliseconds);
					new DatePickerDialog(this.getActivity(), new OnDateSetListener() {
	
						@Override
						public void onDateSet(DatePicker view, int year,
								int monthOfYear, int dayOfMonth) {
							try{
								Calendar returnCalendar1 = Calendar.getInstance(Locale.CHINA);
								returnCalendar1.set(year, monthOfYear, dayOfMonth);
								if (btnDepartureDate.getTag() instanceof String) {
									long milliseconds = Long.valueOf((String) btnDepartureDate.getTag());
									Calendar c = Calendar.getInstance(Locale.CHINA);
									c.setTimeInMillis(milliseconds);
									if (returnCalendar1.getTimeInMillis() < c.getTimeInMillis()){
										showMsg("返程日期不得小于出发日期哦"+SF.TIP);
									}else{
										setReturnDate(year, monthOfYear, dayOfMonth);
									}
								}
							}catch(Exception e){
								e.printStackTrace();
							}
						}
	
					}, returnCalendar.get(Calendar.YEAR), returnCalendar.get(Calendar.MONTH), returnCalendar.get(Calendar.DAY_OF_MONTH)).show();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			break;
		case R.id.zhanzhanQuery_btnTicketType:
			FavoriteCharacterDialogFragment.show(this.getActivity(),
					TrainSchAty.REQUEST_TICKET_TYPE, "车票类别", TT.QUERY_TICKET_TYPE_KEYS);
			break;
		case R.id.zhanzhanQuery_btnFromTimeRange:
			FavoriteCharacterDialogFragment.show(getActivity(),
					TrainSchAty.REQUEST_FROM_TIME_RANGE, "发车时间", TT.TIME_RANGE_KEYS);
			break;
		case R.id.zhanzhanQuery_btnToTimeRange:
			FavoriteCharacterDialogFragment.show(getActivity(),
					TrainSchAty.REQUEST_TO_TIME_RANGE, "到达时间", TT.TIME_RANGE_KEYS);
			break;
		case R.id.zhanzhanQuery_btnTrainType:
			MultiChoiceFragment.show(getActivity(),
					TrainSchAty.REQUEST_TRAIN_TYPE, "车次类型", mTHelper.getTrainTypeNames(), mSelectedTrainTypes);
			break;
		case R.id.zhanzhanQuery_btnQuery:
			doQuery();
			break;
		}
	}

	private void saveOptionInfo() {
		mQLNOInfo.setFrom_station_name(btnFrom.getText().toString());
		mQLNOInfo.setFrom_station_telecode(btnFrom.getTag()==null?null:btnFrom.getTag().toString());
		mQLNOInfo.setTo_station_name(btnTo.getText().toString());
		mQLNOInfo.setTo_station_telecode(btnTo.getTag()==null?null:btnTo.getTag().toString());
		PersistentUtil.writeObject(mQLNOInfo, MyApp.getInstance().getPathBaseRoot(StoreValue.QUERY_OPTION_INFO_FILE));
	}

	private void doQuery() {
		if (btnFrom.getText().toString().equals("")) {
			showMsg("请选择出发站点" + SF.TIP);
			return;
		}
		if (btnTo.getText().toString().equals("")) {
			showMsg("请选择目的站点" + SF.TIP);
			return;
		}
		if (btnDepartureDate.getTag() == null
				|| btnDepartureDate.getTag().toString().equals("")) {
			showMsg("请选择出发日" + SF.TIP);
			return;
		}
		if (trReturnDate.getVisibility() == View.VISIBLE
				&& (btnReturnDate.getTag() == null)) {
			showMsg("请选择返程日" + SF.TIP);
			return;
		}
		Date departureDate = null, returnDate = null;
		if (btnDepartureDate.getTag() instanceof String) {	
			try{
				long milliseconds = Long.valueOf((String) btnDepartureDate.getTag());
				Calendar c = Calendar.getInstance(Locale.CHINA);
				c.setTimeInMillis(milliseconds);
				departureDate = c.getTime();
				if (btnReturnDate.getTag() instanceof String) {
					long returnMilliseconds = Long.valueOf((String) btnReturnDate.getTag());
					Calendar returnCalendar = Calendar.getInstance(Locale.CHINA);
					returnCalendar.setTimeInMillis(returnMilliseconds);
					returnDate = returnCalendar.getTime();
					if (trReturnDate.getVisibility() == View.VISIBLE){
						if (returnCalendar.getTimeInMillis() < c.getTimeInMillis()){
							showMsg("返程日期不得小于出发日期哦"+SF.TIP);
							return;
						};
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		if (departureDate == null || returnDate == null){
			showMsg("日期有误");
			return;
		}
		setTrainTypeTextAndTag();
		if (btnTrainType.getTag() == null){
			showMsg("车次类型请至少选择一项" + SF.FAIL);
			return;
		}
		saveOptionInfo();

		QueryLeftNewOptionInfo qlndInfo = new QueryLeftNewOptionInfo();
		qlndInfo.setFrom_station_telecode(btnFrom.getTag().toString());
		qlndInfo.setTo_station_telecode(btnTo.getTag().toString());
		qlndInfo.setFrom_station_name(btnFrom.getText().toString());
		qlndInfo.setTo_station_name(btnTo.getText().toString());
		qlndInfo.setFromExactMatch(MyUtils.getToogleTextViewStatus(tvFromTip));
		qlndInfo.setToExactMatch(MyUtils.getToogleTextViewStatus(tvToTip));
		qlndInfo.setDeparture_time(TimeUtil.getDFormat().format(departureDate.getTime()));
		if (trReturnDate.getVisibility() == View.VISIBLE){
			qlndInfo.setReturn_time(TimeUtil.getDFormat().format(returnDate.getTime()));
			qlndInfo.setDay_difference(TimeUtil.getIntervalDays(departureDate, returnDate));
		}else{
			qlndInfo.setReturn_time(null);
			qlndInfo.setDay_difference(0);
		}
		if (qlndInfo.getMode() == QueryLeftNewOptionInfo.MODE_NORMAL){
			qlndInfo.setTour_flag(mQLNOInfo.getReturn_time()==null?TT.getTour_flags().get("dc"):TT.getTour_flags().get("wc"));
		}else{
			qlndInfo.setTour_flag(TT.getTour_flags().get("gc"));
		}
		qlndInfo.setTicket_type(btnTicketType.getTag().toString());
		qlndInfo.setFrom_time_range(btnFromTimeRange.getTag().toString());
		qlndInfo.setTo_time_range(btnToTimeRange.getTag().toString());
		qlndInfo.setSelectedTrainTypeIndexes((boolean[])btnTrainType.getTag());
		Intent intent = new Intent(this.getSherlockActivity(),
				TrainSchListAty.class);
		intent.putExtra(TrainSchListAty.EXTRA_OPTION_INFO, qlndInfo);
		this.getActivity().startActivity(intent);
	}

	private void showMsg(String strMsg) {
		Toast.makeText(this.getActivity(), strMsg, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data == null)
			return;
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case 1:
				btnFrom.setText(data.getExtras()
						.getString(SelectAty.RESULT_KEY));
				btnFrom.setTag(data.getExtras().getString(
						SelectAty.RESULT_VALUE));
				saveOptionInfo();
				break;
			case 2:
				btnTo.setText(data.getExtras().getString(SelectAty.RESULT_KEY));
				btnTo.setTag(data.getExtras().getString(SelectAty.RESULT_VALUE));
				saveOptionInfo();
				break;
			}
		}
	}
	
	public void onMultiChoiceSelectedFromActivity(View v, int requestCode, int which,
			boolean isChecked){
		switch(requestCode){
		case TrainSchAty.REQUEST_TRAIN_TYPE:
			if (mSelectedTrainTypes != null){
				mSelectedTrainTypes[which] = isChecked;
			}
			break;
		}
	}
	
	public void onMultiChoicePositiveButtonClickedFromActivity(int requestCode){
		try{
			switch(requestCode){
			case TrainSchAty.REQUEST_TRAIN_TYPE:
				if (!setTrainTypeTextAndTag()){
					showMsg("请至少选择一项" + SF.FAIL);
				}
				break;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public void onListItemSelectedFromActivity(int requestCode, String value,
			int number) {
		try{
			switch (requestCode) {
			case TrainSchAty.REQUEST_QUERY_TYPE:
				btnQueryType.setText(TT.QUERY_TYPE_KEYS[number]);
				btnQueryType.setTag(TT.QUERY_TYPE_VALUES[number]);
				if (number == 0) {
					trReturnDate.setVisibility(View.GONE);
				} else {
					trReturnDate.setVisibility(View.VISIBLE);
				}
				break;
			case TrainSchAty.REQUEST_TICKET_TYPE:
				btnTicketType.setText(TT.QUERY_TICKET_TYPE_KEYS[number]);
				btnTicketType.setTag(TT.QUERY_TICKET_TYPE_VALUES[number]);
				break;
			case TrainSchAty.REQUEST_FROM_TIME_RANGE:
				btnFromTimeRange.setText(TT.TIME_RANGE_KEYS[number]);
				btnFromTimeRange.setTag(TT.TIME_RANGE_VALUES[number]);
				break;
			case TrainSchAty.REQUEST_TO_TIME_RANGE:
				btnToTimeRange.setText(TT.TIME_RANGE_KEYS[number]);
				btnToTimeRange.setTag(TT.TIME_RANGE_VALUES[number]);
				break;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
