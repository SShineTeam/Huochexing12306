package com.sshine.huochexing.trainSchedule;

import java.util.Calendar;
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
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.MenuItem;
import com.sshine.huochexing.R;
import com.sshine.huochexing.bean.Train;
import com.sshine.huochexing.trainInfos.TrainDetailAty;
import com.sshine.huochexing.utils.MyApp;
import com.sshine.huochexing.utils.SettingSPUtil;
import com.sshine.huochexing.utils.TimeUtil;

public class TrainNumQueryFragment extends SherlockFragment implements
		OnClickListener {
	private int intYear, intMonth, intDay;
	private Button btnTrainNum, btnDate, btnQuery;
	private Calendar c;
	private static final int REQUEST_GET_TRAIN_NUM = 1;
	private SettingSPUtil setSP = MyApp.getInstance().getSettingSPUtil();
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_train_num_query, null);

		// 查找控件并绑定单击事件
		if (btnTrainNum == null) {
			btnTrainNum = (Button) v
					.findViewById(R.id.fragment_train_num_query_btnTrainNum);
			btnTrainNum.setOnClickListener(this);
		}
		if (btnDate == null) {
			btnDate = (Button) v
					.findViewById(R.id.fragment_train_num_query_btnDate);
			btnDate.setOnClickListener(this);
		}
		if (btnQuery == null) {
			btnQuery = (Button) v
					.findViewById(R.id.fragment_train_num_query_btnQuery);
			btnQuery.setOnClickListener(this);
		}
		
		btnTrainNum.setText(setSP.getLastTrainNumKey());
		btnTrainNum.setTag(setSP.getLastTrainNumKey());
		// 取得当前日期
		c = Calendar.getInstance();
		c.setTimeInMillis(System.currentTimeMillis());
		c.add(Calendar.DAY_OF_MONTH, 1);
		intYear = c.get(Calendar.YEAR);
		intMonth = c.get(Calendar.MONTH);
		intDay = c.get(Calendar.DAY_OF_MONTH);
		return v;
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
		switch (v.getId()) {
		case R.id.fragment_train_num_query_btnTrainNum:
			Intent intent1 = new Intent(this.getActivity(), SelectAty.class);
			intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 不加此标志StationAty有时执行两次finish()才会返回。
			intent1.putExtra(SelectAty.SEARCH_TYPE, SelectAty.SEARCH_TRAIN_NUM);
			startActivityForResult(intent1, REQUEST_GET_TRAIN_NUM);
			break;
		case R.id.fragment_train_num_query_btnDate:
			new DatePickerDialog(this.getSherlockActivity(),
					new OnDateSetListener() {

						@Override
						public void onDateSet(DatePicker view, int year,
								int monthOfYear, int dayOfMonth) {
							String tempDate = year + "-" + (monthOfYear + 1)
									+ "-" + dayOfMonth;
							btnDate.setText(year + "年" + (monthOfYear + 1)
									+ "月" + dayOfMonth + "日" + "  "
									+ TimeUtil.getWeek(tempDate));
							btnDate.setTag(tempDate);
						}

					}, intYear, intMonth, intDay).show();
			break;
		case R.id.fragment_train_num_query_btnQuery:
			if (btnTrainNum.getText().toString().equals("")) {
				Toast.makeText(this.getSherlockActivity(), "请选择车次.",
						Toast.LENGTH_SHORT).show();
				return;
			}
			
			//晢时取消输入时间
			String tempDate = c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH)+1) + "-" + c.get(Calendar.DAY_OF_MONTH);
			btnDate.setText(tempDate + "  " + TimeUtil.getWeek(tempDate));
			btnDate.setTag(tempDate);
//			if (btnDate.getTag() == null
//					|| btnDate.getTag().toString().equals("")) {
//				Toast.makeText(this.getSherlockActivity(), "请选择日期.",
//						Toast.LENGTH_SHORT).show();
//				return;
//			}
			Intent intent = new Intent(this.getActivity(), TrainDetailAty.class);
			Train train = new Train();
			train.setTrainNum(btnTrainNum.getText().toString());
			intent.putExtra(TrainDetailAty.TRAIN, train);
			getActivity().startActivity(intent);
			break;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data == null)
			return;
		switch(requestCode){
		case REQUEST_GET_TRAIN_NUM:
			if (resultCode == Activity.RESULT_OK) {
				btnTrainNum.setText(data.getExtras().getString(SelectAty.RESULT_KEY).toUpperCase(Locale.getDefault()));
			}
			break;
		}
	}
}
