package com.sshine.huochexing.ticketInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.actionbarsherlock.app.SherlockFragment;
import com.sshine.huochexing.R;
import com.sshine.huochexing.utils.MyDatabase;

public class OtherTicketInfoFragment extends SherlockFragment {

	public static OtherTicketInfoFragment newInstance(){
		return (new OtherTicketInfoFragment());
	}

	private SimpleAdapter mAdapter;
	private List<Map<String, String>> mLstDatas = new ArrayList<Map<String,String>>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_other_ticket_info, null);
		ListView lvInfos = (ListView) v.findViewById(R.id.otherTicketInfo_lvInfos);
		MyDatabase myDB = new MyDatabase(this.getActivity());
		mAdapter= new SimpleAdapter(this.getActivity(),mLstDatas, R.layout.item_buy_ticket_info,
				new String[]{MyDatabase.KEY, MyDatabase.VALUE},
				new int[]{R.id.item_buy_ticket_info_tvQuestion, R.id.item_buy_ticket_info_tvAnswer}
				);
		lvInfos.setAdapter(mAdapter);
		myDB.closeDB();
		notifyAdapterDataChanged(myDB.getTicketInfos(0));
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
