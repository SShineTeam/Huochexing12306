package com.sshine.huochexing.adapter;

import java.util.List;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sshine.huochexing.R;
import com.sshine.huochexing.bean.PassengerDTOInfo;
import com.sshine.huochexing.bean.StationTrainDTOInfo;
import com.sshine.huochexing.bean.TicketInfo;
import com.sshine.huochexing.utils.SeatHelper;
import com.sshine.huochexing.utils.TrainHelper;

public class A6OrderItemAdapter extends BaseAdapter {
	private List<TicketInfo> mLstDatas;
	private LayoutInflater mInflater;
	private TrainHelper mTHelper = new TrainHelper();
	private SeatHelper mSHelper = new SeatHelper();
	
	public A6OrderItemAdapter(Context context, List<TicketInfo> lstDatas){
		mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mLstDatas = lstDatas;
	}
	
	private class ViewHolder{
		TextView tvTrainNum;
		TextView tvStation;
		TextView tvTStartTime;
		TextView tvPassenger;
		TextView tvTicketTypeName;
		TextView tvOrderStatus;
		TextView tvSeat;
		TextView tvSeatRLoc;
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
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView != null){
			holder = (ViewHolder) convertView.getTag();
		}else{
			convertView = mInflater.inflate(R.layout.item_item_a6_order, null);
			holder = new ViewHolder();
			holder.tvTrainNum = (TextView)convertView.findViewById(R.id.item_item_a6_order_tvTrainNum);
			holder.tvStation = (TextView)convertView.findViewById(R.id.item_item_a6_order_tvStation);
			holder.tvTStartTime = (TextView)convertView.findViewById(R.id.item_item_a6_order_tvTStartTime);
			holder.tvPassenger = (TextView)convertView.findViewById(R.id.item_item_a6_order_tvPassenger);
			holder.tvTicketTypeName = (TextView)convertView.findViewById(R.id.item_item_a6_order_tvTicketTypeName);
			holder.tvOrderStatus = (TextView)convertView.findViewById(R.id.item_item_a6_order_tvOrderStatus);
			holder.tvSeat = (TextView)convertView.findViewById(R.id.item_item_a6_order_tvSeat);
			holder.tvSeatRLoc = (TextView)convertView.findViewById(R.id.item_item_a6_order_tvSeatRLoc);
			convertView.setTag(holder);
		}
		TicketInfo tInfo = mLstDatas.get(position);
		if (tInfo != null){
			StationTrainDTOInfo stdInfo = tInfo.getStationTrainDTO();
			PassengerDTOInfo pdInfo = tInfo.getPassengerDTO();
			holder.tvTrainNum.setText(stdInfo.getStation_train_code());
			holder.tvStation.setText(stdInfo.getFrom_station_name()+" -- "+stdInfo.getTo_station_name());
			holder.tvTStartTime.setText(tInfo.getStart_train_date_page());
			holder.tvPassenger.setText(pdInfo.getPassenger_name());
			holder.tvTicketTypeName.setText(tInfo.getTicket_type_name());
			String strOrderStatus = tInfo.getTicket_status_name()+"(<font color='#ff8c00'>￥"
				+tInfo.getStr_ticket_price_page()+"</font>)";
			holder.tvOrderStatus.setText(Html.fromHtml(strOrderStatus));
			if (tInfo.getSeat_name().equals("无座")){
				holder.tvSeat.setText(tInfo.getCoach_name()+"车厢"+tInfo.getSeat_name());
				holder.tvSeatRLoc.setText("");
			}else{
				holder.tvSeat.setText(tInfo.getCoach_name()+"车厢"+tInfo.getSeat_name()+" "+tInfo.getSeat_type_name());
				holder.tvSeatRLoc.setText(mSHelper.getReferenceLocation(mTHelper, tInfo));
			}
			if (tInfo.getTicket_status_name().equals("已改签")){
				setViewsDisable(holder);
			}
		}
		return convertView;
	}
	private void setViewsDisable(ViewHolder holder) {
		holder.tvTrainNum.setEnabled(false);
		holder.tvStation.setEnabled(false);
		holder.tvTStartTime.setEnabled(false);
		holder.tvPassenger.setEnabled(false);
		holder.tvTicketTypeName.setEnabled(false);
		holder.tvOrderStatus.setEnabled(false);
		holder.tvSeat.setEnabled(false);
		holder.tvSeatRLoc.setEnabled(false);
	}
}
