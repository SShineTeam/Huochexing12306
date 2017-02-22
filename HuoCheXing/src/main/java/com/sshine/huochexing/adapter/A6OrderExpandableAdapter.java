package com.sshine.huochexing.adapter;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.text.Html;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.sshine.huochexing.R;
import com.sshine.huochexing.bean.OrderDBInfo;
import com.sshine.huochexing.bean.TicketInfo;
import com.sshine.huochexing.model.SubListView;
import com.sshine.huochexing.ticketOnline.A6OrderAty;
import com.sshine.huochexing.utils.TimeUtil;

public class A6OrderExpandableAdapter extends BaseExpandableListAdapter {
	private Context mContext;
	private List<OrderDBInfo> mLstDatas;
	private LayoutInflater mInflater;
	private int mCurrChildItemPos = -1;
	private int mCurrGroupPos = -1;
	
	public A6OrderExpandableAdapter(Context context, List<OrderDBInfo> lstDatas){
		mContext = context;
		mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mLstDatas = lstDatas;
	}
	private class GroupViewHolder{
		TextView tvSequenceNo;
		TextView tvTotalPrice;
		TextView tvOrderDate;
		TextView tvStation;
	}
	private class ChildViewHolder{
		SubListView slv1;
	}
	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return mLstDatas.get(groupPosition).getTickets().get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
			ViewGroup parent) {
		if (mLstDatas == null || mLstDatas.size() == 0){
			return null;
		}
		final int groupPos = groupPosition;
		ChildViewHolder holder;
		if (convertView != null){
			holder = (ChildViewHolder) convertView.getTag();
		}else{
			convertView = mInflater.inflate(R.layout.item_a6_order, null);
			holder = new ChildViewHolder();
			holder.slv1 = (SubListView)convertView.findViewById(R.id.item_a6_order_slv1);
			convertView.setTag(holder);
		}
		holder.slv1.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
			
			@Override
			public void onCreateContextMenu(ContextMenu menu, View v,
					ContextMenuInfo menuInfo) {
				mCurrGroupPos = groupPos;
				mCurrChildItemPos = ((AdapterContextMenuInfo) menuInfo).position;
				menu.clear();
				menu.add(A6OrderAty.CONTEXT_GROUP_0, A6OrderAty.CONTEXT_ITEM_ADD_TRAIN_INFO, 0, "添加车次");
				TicketInfo tInfo = mLstDatas.get(groupPos).getTickets().get(mCurrChildItemPos);
				try {
					Date trainDate = TimeUtil.getDTFormat().parse(tInfo.getStart_train_date_page());
					if (trainDate.getTime() >= (new Date()).getTime()){
						if (tInfo.getReturn_flag().equals("Y")){
							//退票
							menu.add(A6OrderAty.CONTEXT_GROUP_0, A6OrderAty.CONTEXT_ITEM_REFUND_TICKET, 1, "退票");
						}
//						if (tInfo.getResign_flag().equals("Y")){
//							//改签
//							menu.add(A6OrderAty.CONTEXT_GROUP_0, A6OrderAty.CONTEXT_ITEM_RESIGN, 2, "改签");
//						}
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		});
		A6OrderItemAdapter adapter = new A6OrderItemAdapter(mContext, mLstDatas.get(groupPosition).getTickets());
		holder.slv1.setAdapter(adapter);
		return convertView;
	}
	
	public TicketInfo getCurrTInfo(){
		if (mCurrGroupPos == -1 || mCurrChildItemPos == -1){
			return null;
		}else{
			return mLstDatas.get(mCurrGroupPos).getTickets().get(mCurrChildItemPos);
		}
	}
	
	public OrderDBInfo getCurrOrderDBInfo(){
		if (mCurrGroupPos == -1 || mCurrChildItemPos == -1){
			return null;
		}else{
			return mLstDatas.get(mCurrGroupPos);
		}
	}

	@Override
	public int getChildrenCount (int groupPosition) {
		return 1;
	}

	@Override
	public Object getGroup(int groupPosition) {
		return mLstDatas.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return mLstDatas.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		GroupViewHolder holder;
		if (convertView != null){
			holder = (GroupViewHolder) convertView.getTag();
		}else{
			convertView = mInflater.inflate(R.layout.group_a6_order, null);
			holder = new GroupViewHolder();
			holder.tvSequenceNo = (TextView)convertView.findViewById(R.id.group_a6_order_tvSequenceNo);
			holder.tvOrderDate = (TextView)convertView.findViewById(R.id.group_a6_order_tvOrderDate);
			holder.tvTotalPrice = (TextView)convertView.findViewById(R.id.group_a6_order_tvTotalPrice);
			holder.tvStation = (TextView)convertView.findViewById(R.id.group_a6_order_tvStation);
			convertView.setTag(holder);
		}
		if (groupPosition >= mLstDatas.size()){
			return convertView;
		}
		OrderDBInfo oDBInfo = mLstDatas.get(groupPosition);
		if (oDBInfo.getTickets() != null){
			holder.tvSequenceNo.setText(oDBInfo.getSequence_no());
			String strTotalPrice = "共:<font color='#ff8c00'>￥"+oDBInfo.getTicket_total_price_page()+"</font>";
			holder.tvTotalPrice.setText(Html.fromHtml(strTotalPrice));
			holder.tvOrderDate.setText(oDBInfo.getOrder_date()+"下单");
			String strStation = "<b>"+oDBInfo.getFrom_station_name_page()[0]+" -- "+oDBInfo.getTo_station_name_page()[0]+"</b>";
			holder.tvStation.setText(Html.fromHtml(strStation));
		}
		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
}
