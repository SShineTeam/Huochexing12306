package com.sshine.huochexing.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sshine.huochexing.R;
import com.sshine.huochexing.bean.QueryLeftNewDTOInfo;
import com.sshine.huochexing.bean.QueryLeftNewInfo;
import com.sshine.huochexing.utils.A6Util;
import com.sshine.huochexing.utils.SeatHelper;
import com.sshine.huochexing.utils.TimeUtil;
import com.sshine.huochexing.utils.TrainHelper;

public class TrainSchListAdapter extends BaseAdapter {
	private List<QueryLeftNewInfo> mLstDatas;
	private LayoutInflater mInflater;
	private TrainHelper mTHelper = new TrainHelper();
	private boolean mIsCanBooking = true;
	
	private class ViewHolder{
		TextView tvTrainNum;
		TextView tvTotalTime;
		ImageView ivStartStationTip;
		ImageView ivEndStationTip;
		TextView tvStartStation;
		TextView tvEndStation;
		TextView tvD_Time;
		TextView tvA_Time;
		TextView tvD_LateTime;
		TextView tvA_LateTime;
		TextView tvSeatInfo;
		TextView tvCanWebBuy;
	}

	public TrainSchListAdapter(Context context, List<QueryLeftNewInfo> data){
		mLstDatas = data;
		mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	@Override
	public int getCount() {
		return mLstDatas.size();
	}

	@Override
	public Object getItem(int position) {
		return mLstDatas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public void notifyDataSetChanged() {
		mIsCanBooking = A6Util.isCanBooking();
		super.notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final int pos = position;
		if (mLstDatas == null || (mLstDatas.size() <= pos) || mLstDatas.get(pos) == null){
			return null;
		}
		ViewHolder holder;
		if (convertView != null){
			holder = (ViewHolder) convertView.getTag();
		}else{
			convertView = mInflater.inflate(R.layout.item_trainsch_list, null);
			holder = new ViewHolder();
			holder.tvTrainNum = (TextView) convertView.findViewById(R.id.item_trainsch_list_tvTrainNum);
			holder.tvTotalTime = (TextView) convertView.findViewById(R.id.item_trainsch_list_tvTotalTime);
			holder.ivStartStationTip = (ImageView) convertView.findViewById(R.id.item_trainsch_list_ivStartStationType);
			holder.ivEndStationTip = (ImageView) convertView.findViewById(R.id.item_trainsch_list_ivEndStationType);
			holder.tvStartStation = (TextView) convertView.findViewById(R.id.item_trainsch_list_tvStartStation);
			holder.tvEndStation = (TextView) convertView.findViewById(R.id.item_trainsch_list_tvEndStation);
			holder.tvD_Time = (TextView) convertView.findViewById(R.id.item_trainsch_list_tvD_Time);
			holder.tvA_Time = (TextView) convertView.findViewById(R.id.item_trainsch_list_tvA_Time);
			holder.tvD_LateTime = (TextView) convertView.findViewById(R.id.item_trainsch_list_tvD_LateTime);
			holder.tvA_LateTime = (TextView) convertView.findViewById(R.id.item_trainsch_list_tvA_LateTime);
			holder.tvSeatInfo = (TextView) convertView.findViewById(R.id.item_trainsch_list_tvSeatInfo);
			holder.tvCanWebBuy = (TextView) convertView.findViewById(R.id.item_trainsch_list_tvCanWebBuy);
			convertView.setTag(holder);
		}
		QueryLeftNewDTOInfo qlndInfo = mLstDatas.get(pos).getQueryLeftNewDTO();
		if (qlndInfo != null){
			holder.tvTrainNum.setText(qlndInfo.getStation_train_code());
			holder.tvTotalTime.setText(qlndInfo.getLishi());
			boolean isStart = qlndInfo.getFrom_station_telecode().equals(
					qlndInfo.getStart_station_telecode());
			qlndInfo.setFlag_start(isStart);
			holder.ivStartStationTip.setImageResource(isStart ? R.drawable.station_start
					: R.drawable.station_pass);
			boolean isEnd = qlndInfo.getTo_station_telecode().equals(
					qlndInfo.getEnd_station_telecode());
			qlndInfo.setFlag_end(isEnd);
			holder.ivEndStationTip.setImageResource(isEnd ? R.drawable.station_end_point
					: R.drawable.station_pass);
			holder.tvStartStation.setText(qlndInfo.getFrom_station_name());
			holder.tvEndStation.setText(qlndInfo.getTo_station_name());
			holder.tvD_Time.setText(qlndInfo.getStart_time());
			holder.tvA_Time.setText(getFomartArriveTimeString(qlndInfo.getArrive_time(),
					qlndInfo.getDay_difference()));
			holder.tvD_LateTime.setText(qlndInfo.getD_LateTime()==0?"0":TimeUtil.get_T_Str(qlndInfo.getD_LateTime()));
			holder.tvA_LateTime.setText(qlndInfo.getA_LateTime()==0?"0":TimeUtil.get_T_Str(qlndInfo.getA_LateTime()));
			String strWebBuy = "";
			if (!mIsCanBooking){
				strWebBuy = "12306维护时间";
			}else if (qlndInfo.getCanWebBuy().equals("Y")){
				strWebBuy = "可预订";
			}else if (!mLstDatas.get(pos).getButtonTextInfo().equals("预订")){
				strWebBuy = mLstDatas.get(pos).getButtonTextInfo();
			}
			holder.tvCanWebBuy.setText(strWebBuy);
			SeatHelper seatHelper = new SeatHelper(qlndInfo);
			holder.tvSeatInfo.setText(seatHelper.getSeatText());
		}
		
		return convertView;
	}
	private String getFomartArriveTimeString(String strArriveTime,
			int day_difference) {
		String strRetValue = "";
		switch (day_difference) {
		case 0:
			strRetValue = "当日 " + strArriveTime;
			break;
		case 1:
			strRetValue = "次日 " + strArriveTime;
			break;
		default:
			strRetValue = day_difference + "日后 " + strArriveTime;
		}
		return strRetValue;
	}
}
