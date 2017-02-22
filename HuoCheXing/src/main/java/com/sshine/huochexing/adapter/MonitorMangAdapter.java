package com.sshine.huochexing.adapter;

import java.util.List;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sshine.huochexing.R;
import com.sshine.huochexing.bean.MonitorInfo;
import com.sshine.huochexing.ticketOnline.BgdService2;
import com.sshine.huochexing.utils.ImageUtil;

public class MonitorMangAdapter extends BaseAdapter {
	private Context mContext;
	private List<MonitorInfo> mLstMInfos;
	private LayoutInflater mInflater;
	
	public MonitorMangAdapter(Context context, List<MonitorInfo> lstMInfos){
		mContext = context;
		mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mLstMInfos = lstMInfos;
	}
	
	private class ViewHolder{
		TextView tvStation;
		TextView tvStartTime;
		ImageView ivStatus;
		TextView tvStatusTip;
	}
	@Override
	public int getCount() {
		return mLstMInfos.size();
	}
	@Override
	public Object getItem(int position) {
		return mLstMInfos.get(position);
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
			convertView = mInflater.inflate(R.layout.item_monitor_mang, null);
			holder = new ViewHolder();
			holder.tvStation = (TextView)convertView.findViewById(R.id.tvStation);
			holder.tvStartTime = (TextView)convertView.findViewById(R.id.tvStartTime);
			holder.ivStatus = (ImageView)convertView.findViewById(R.id.ivStatus);
			holder.tvStatusTip = (TextView)convertView.findViewById(R.id.tvStatusTip);
			convertView.setTag(holder);
		}
		MonitorInfo mInfo = mLstMInfos.get(position);
		holder.tvStation.setText(mInfo.getFrom_station_name()+"  ——  "+mInfo.getTo_station_name());
		holder.tvStartTime.setText(mInfo.getStart_time());
		switch(mInfo.getStatus()){
		case BgdService2.STATUS_RUNNING:
			holder.ivStatus.setImageResource(R.drawable.ic_prog_start);
			ImageUtil.rotateImageForever(mContext, holder.ivStatus, R.anim.imge_rotate_forever1000);
			String strMsg = "已重新从"+mInfo.getStartMonitorTime()+"开始抢票，共已抢<font color='#ff8c00'>"
				+mInfo.getRetryCount()+"</font>次";
			holder.tvStatusTip.setText(Html.fromHtml(strMsg));
			break;
		case BgdService2.STATUS_STOPED:
			holder.ivStatus.clearAnimation();
			holder.ivStatus.setImageResource(R.drawable.ic_prog_stop);
			holder.tvStatusTip.setText("抢票监控已停止");
			break;
		case BgdService2.STATUS_PAUSED:
			holder.ivStatus.clearAnimation();
			holder.ivStatus.setImageResource(R.drawable.ic_prog_stop);
			holder.tvStatusTip.setText("已暂停抢票监控");
			break;
		case BgdService2.STATUS_EXPIRED:
			holder.ivStatus.clearAnimation();
			holder.ivStatus.setImageResource(R.drawable.ic_prog_stop);
			holder.tvStatusTip.setText("抢票信息已过期，请重新添加抢票监控");
			break;
		case BgdService2.STATUS_NO_NETWORK:
			holder.ivStatus.clearAnimation();
			holder.ivStatus.setImageResource(R.drawable.ic_prog_stop);
			holder.tvStatusTip.setText("无网络");
			break;
		case BgdService2.STATUS_CAN_NOT_BOOKING:
			holder.ivStatus.clearAnimation();
			holder.ivStatus.setImageResource(R.drawable.ic_prog_stop);
			holder.tvStatusTip.setText("23:00-07:00为12306系统维护时间，此段时间内不能抢票");
			break;
		case BgdService2.STATUS_WAIT_HANDLE_UNCOMPLETE_ORDERS:
			holder.ivStatus.clearAnimation();
			holder.ivStatus.setImageResource(R.drawable.ic_prog_stop);
			holder.tvStatusTip.setText("请先处理12306未完成订单");
			break;
		}
		return convertView;
	}

}
