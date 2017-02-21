package com.sshine.huochexing.ticketInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;

import com.actionbarsherlock.app.SherlockFragment;
import com.sshine.huochexing.R;
import com.sshine.huochexing.utils.MyDatabase;
import com.sshine.huochexing.utils.MyUtils;

public class BuyTicketInfoFragment extends SherlockFragment {
	
	public static BuyTicketInfoFragment newInstance(){
		return (new BuyTicketInfoFragment());
	}

	private List<Map<String, String>> mLstDatas = new ArrayList<Map<String,String>>();
	private SimpleAdapter mAdapter;
	private ListView lvInfos;
	private ScrollView sv1;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_buy_ticket_info, null);
		lvInfos = (ListView) v.findViewById(R.id.buyTicketInfo_lvInfos);
		MyDatabase myDB = new MyDatabase(this.getActivity());
		mAdapter= new SimpleAdapter(this.getActivity(), mLstDatas, R.layout.item_buy_ticket_info,
				new String[]{MyDatabase.KEY, MyDatabase.VALUE},
				new int[]{R.id.item_buy_ticket_info_tvQuestion, R.id.item_buy_ticket_info_tvAnswer}
				);
		lvInfos.setAdapter(mAdapter);
		myDB.closeDB();
		notifyAdapterDataChanged(myDB.getTicketInfos(0));
		MyUtils.setListViewHeightBasedOnChildren(lvInfos);  //设置ListView全部显示
		ViewGroup.LayoutParams params = lvInfos.getLayoutParams();
		
		params.height += 3000;   //方法不太准，人为校正高度
		lvInfos.setLayoutParams(params);
		sv1 = (ScrollView)v.findViewById(R.id.buyTicketInfo_sv1);
		sv1.smoothScrollTo(0, 20);
		return v;
	}
	public void notifyAdapterDataChanged(List<Map<String, String>> list) {
		mLstDatas.clear();
		if (list != null){
			for(Map<String, String> map:list){
				mLstDatas.add(map);
			}
		}
		if (mAdapter != null){
			mAdapter.notifyDataSetChanged();
		}
	}
}
