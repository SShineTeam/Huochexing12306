package com.sshine.huochexing.chatroom;

import java.util.List;

import com.sshine.huochexing.chatroom.bean.ChatRoomInfo;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class ChatRoomFragmentAdapter extends FragmentPagerAdapter {
	protected List<ChatRoomInfo> cContents;

	public ChatRoomFragmentAdapter(FragmentManager fm, List<ChatRoomInfo> cContents1) {
		super(fm);
		cContents = cContents1;
	}
	
	@Override
    public Fragment getItem(int position) {
        return ChatRoomFragment.newInstance(cContents.get(position));
    }

    @Override
    public int getCount() {
        return cContents.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
      return this.cContents.get(position).getTrainNum();
    }
    
    
	public ChatRoomInfo getData(int position) {
		return cContents.get(position);
	}

	public void setcContents(List<ChatRoomInfo> cContents) {
		this.cContents = cContents;
	}
//
//    public void setCount(int count) {
//        if (count > 0 && count <= 10) {
//            mCount = count;
//            notifyDataSetChanged();
//        }
//    }
}
