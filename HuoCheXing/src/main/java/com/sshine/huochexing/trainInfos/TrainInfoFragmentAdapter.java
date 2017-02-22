package com.sshine.huochexing.trainInfos;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.sshine.huochexing.bean.Travel;

public class TrainInfoFragmentAdapter extends FragmentPagerAdapter {
	private List<Travel> mContents;
	private int mChildCount;

	public TrainInfoFragmentAdapter(FragmentManager fm, List<Travel> cContents1) {
		super(fm);
		setmContents(cContents1);
	}
	
	//重写是为了调用adapter.notifyDataSetChanged()时 更新pager

	@Override
    public Fragment getItem(int position) {
        return TrainInfoFragment.newInstance(getmContents().get(position));
    }

    @Override
    public int getCount() {
        return getmContents().size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
      return this.getmContents().get(position).getTravelName();
    }
//
//    public void setCount(int count) {
//        if (count > 0 && count <= 10) {
//            mCount = count;
//            notifyDataSetChanged();
//        }
//    }

	public List<Travel> getmContents() {
		return mContents;
	}

	public void setmContents(List<Travel> mContents) {
		this.mContents = mContents;
	}
}
