package com.sshine.huochexing.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.sshine.huochexing.R;
import com.sshine.huochexing.utils.MyApp;

public class FaceAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private List<Integer> faceList  = new ArrayList<Integer>();
	private Map<String, Integer> mFaceMap;
	
	public FaceAdapter(Context context, Map<String, Integer> faceMap) {
		inflater = LayoutInflater.from(context);
		//初始化表情id列表
		mFaceMap  = faceMap;
		for (Entry<String, Integer> entry : mFaceMap.entrySet()) {
			faceList.add(entry.getValue());
		}
		
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return faceList.size();
	}

	@Override
	public Object getItem(int position) {
		return faceList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if(convertView == null){
			viewHolder = new ViewHolder();
			convertView = inflater.inflate(R.layout.face, null, false);
			viewHolder.faceIV = (ImageView) convertView.findViewById(R.id.face_iv);
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.faceIV.setImageResource(faceList.get(position));
		return convertView;
	}
	
	public Map<String, Integer> getFaceMap() {
		return mFaceMap;
	}

	class ViewHolder{
		private ImageView faceIV;
	}

}
