package com.sshine.huochexing.adapter;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;

import com.sshine.huochexing.R;
import com.sshine.huochexing.bean.PassengerInfo;
import com.sshine.huochexing.bean.SeatInfo;
import com.sshine.huochexing.value.TT;

public class PLayoutAdapter extends BaseAdapter {
	private List<Map<String, Object>> mLstDatas;
	private LayoutInflater mInflater;
	private PLayoutItemClickListener mListener;
	
	private class ViewHolder{
		Button btnSeat;
		Button btnPassenger;
		ImageView ivDel;
	}
	public PLayoutAdapter(Context context, List<Map<String, Object>> data, PLayoutItemClickListener listener){
		mLstDatas = data;
		mListener = listener;
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
	public View getView(int position, View convertView, ViewGroup parent) {
		final int pos = position;
		ViewHolder holder;
		if (convertView != null){
			holder = (ViewHolder) convertView.getTag();
		}else{
			convertView = mInflater.inflate(R.layout.item_p_layout, null);
			holder = new ViewHolder();
			holder.btnSeat = (Button) convertView.findViewById(R.id.item_p_layout_btnSeat);
			holder.btnPassenger = (Button)convertView.findViewById(R.id.item_p_layout_tvPassenger);
			holder.ivDel = (ImageView)convertView.findViewById(R.id.item_p_layout_ivDel);
			convertView.setTag(holder);
		}
		SeatInfo sInfo = (SeatInfo) mLstDatas.get(position).get(TT.SEAT_INFO);
		if (sInfo != null){
			holder.btnSeat.setText(Html.fromHtml(
					sInfo.getName() + "(<font color='#ff8c00'>" + sInfo.getPrice() + "</font>)"));
			holder.btnSeat.setOnClickListener(new View.OnClickListener(){
	
				@Override
				public void onClick(View v) {
					if (mListener != null){
						mListener.onSeatButtonClick(v, pos);
					}
				}
			});
		}
		PassengerInfo pInfo = (PassengerInfo)mLstDatas.get(position).get(TT.PASSENGER_INFO);
		holder.btnPassenger.setText(Html.fromHtml("<u>"+
				pInfo.getPassenger_name() + "(<font color='#ff8c00'>" + pInfo.getPassenger_type_name() + "票</font></u>)"));
		holder.btnPassenger.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (mListener != null){
					mListener.onPassengerButtonClick(v, pos);
				}
			}
		});
		holder.ivDel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mListener != null){
					mListener.onDelButtonClick(v, pos);
				}
			}
		});
		return convertView;
	}
	public interface PLayoutItemClickListener{
		public void onPassengerButtonClick(View v, int pos);
		public void onSeatButtonClick(View v, int pos);
		public void onDelButtonClick(View v, int pos);
	}
}