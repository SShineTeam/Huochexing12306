package com.sshine.huochexing.adapter;

import java.util.List;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.sshine.huochexing.R;
import com.sshine.huochexing.bean.PassengerInfo;

public class PassengerMangAdapter extends BaseAdapter {
	private List<PassengerInfo> mLstPInfos;
	private LayoutInflater mInflater;
	private OnPMAClickListener mListener;
	
	private class ViewHolder{
		Button btnName;
		TextView tvTypeName;
		TextView tvStatus;
		ImageView ivDel;
	}
	
	public PassengerMangAdapter(Context context, List<PassengerInfo> lstPInfos, PassengerMangAdapter.OnPMAClickListener listener){
		mLstPInfos = lstPInfos;
		mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mListener = listener;
	}
	@Override
	public int getCount() {
		return (mLstPInfos== null?0:mLstPInfos.size());
	}

	@Override
	public Object getItem(int position) {
		return (mLstPInfos==null?null:mLstPInfos.get(position));
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
			convertView = mInflater.inflate(R.layout.item_passenger_mang, null);
			holder = new ViewHolder();
			holder.btnName = (Button)convertView.findViewById(R.id.item_passenger_mang_btnName);
			holder.tvStatus = (TextView)convertView.findViewById(R.id.item_passenger_mang_tvStatus);
			holder.tvTypeName = (TextView)convertView.findViewById(R.id.item_passenger_mang_tvTypeName);
			holder.ivDel = (ImageView)convertView.findViewById(R.id.item_passenger_mang_ivDel);
			convertView.setTag(holder);
		}
		PassengerInfo pInfo = mLstPInfos.get(pos);
		if (pInfo != null){
			String strMsg;
			if (pInfo.isCommon()){
				strMsg = "<u>"+pInfo.getPassenger_name()
						+ "(<font color='#ff8c00'>常用</font>)</u>";
			}else{
				strMsg = "<u>"+pInfo.getPassenger_name()+"</u>";
			}	
			holder.btnName.setText(Html.fromHtml(strMsg));
			holder.btnName.setOnClickListener(new View.OnClickListener() {
						
				@Override
				public void onClick(View v) {
					if (mListener != null){
						mListener.onNameClick(v, pos);
					}
				}
			});
			holder.tvTypeName.setText(pInfo.getPassenger_type_name());
			String strStatus = "<font color='#0077FF'>已通过</font>";
			if (pInfo.getTotal_times() != 99){
				strStatus = "<b>待核验</b>";
			}
			holder.tvStatus.setText(Html.fromHtml(strStatus));
			if (pInfo.isUserSelf()){
				holder.ivDel.setImageBitmap(null);
			}else{
				holder.ivDel.setImageResource(R.drawable.trash);
			}
			holder.ivDel.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if (mListener != null){
						mListener.onDelClick(v, pos);
					}
				}
			});
		}
		return convertView;
	}
	
	public interface OnPMAClickListener{
		void onNameClick(View v, int pos);
		void onDelClick(View v, int pos);
	}
}
