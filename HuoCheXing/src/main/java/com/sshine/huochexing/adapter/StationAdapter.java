package com.sshine.huochexing.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sshine.huochexing.R;
import com.sshine.huochexing.bean.StationInfo;
import com.sshine.huochexing.listener.OnItemViewClickListener;
public class StationAdapter extends BaseAdapter{
	private Context mContext;
	private List<StationInfo> mLstDatas;
	private LayoutInflater mInflater;
	
	private class ViewHolder{
		TextView tvStation;
		TextView tvArriveTime;
		TextView tvStartTime;
		TextView tvRemain;
	}
	public StationAdapter(Context context, List<StationInfo> data){
		mContext = context;
		mLstDatas = data;
		mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	@Override
	public int getCount() {
		return (mLstDatas== null?0:mLstDatas.size());
	}

	@Override
	public Object getItem(int position) {
		return (mLstDatas==null?null:mLstDatas.get(position));
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
			convertView = mInflater.inflate(R.layout.item_train_detail, null);
			holder = new ViewHolder();
			holder.tvStation = (TextView)convertView.findViewById(R.id.item_train_detail_station);
			holder.tvArriveTime = (TextView)convertView.findViewById(R.id.item_train_detail_drawIn);
			holder.tvStartTime = (TextView)convertView.findViewById(R.id.item_train_detail_drawOut);
			holder.tvRemain = (TextView)convertView.findViewById(R.id.item_train_detail_remain);
			convertView.setTag(holder);
		}
		final StationInfo sInfo = mLstDatas.get(pos);
		if (sInfo != null){
			holder.tvStation.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
			holder.tvStation.setText(sInfo.getStation_name());
			holder.tvStation.setOnClickListener(new OnClickListener() {
				
				@SuppressWarnings("unchecked")
				@Override
				public void onClick(View v) {
					if (mContext != null && (mContext instanceof OnItemViewClickListener)){
						((OnItemViewClickListener<StationInfo>)mContext).onItemClick(v, pos, sInfo);
					}
				}
			});
			holder.tvArriveTime.setText(sInfo.getArrive_time());
			holder.tvStartTime.setText(sInfo.getStart_time());
			holder.tvRemain.setText(sInfo.getRemain());
		}
		return convertView;
	}
}
