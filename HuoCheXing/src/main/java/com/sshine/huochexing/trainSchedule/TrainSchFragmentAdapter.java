package com.sshine.huochexing.trainSchedule;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TrainSchFragmentAdapter extends FragmentPagerAdapter {
	private TrainSchAty mParentAty;
	private String[] strTitles = new String[]{"车票预订", "车次查询"};
	private List<Fragment> lstFragments = new ArrayList<Fragment>();
	
	public TrainSchFragmentAdapter(FragmentManager fm) {
		super(fm);
	}
	
	public TrainSchFragmentAdapter(FragmentManager fm, TrainSchAty aty1){
		this(fm);
		mParentAty = aty1;
	}

	@Override
	public Fragment getItem(int position) {
		Fragment fragment = null;
		switch(position){
		case 0:
			if (lstFragments.size() <= position || lstFragments.get(position) == null){
				lstFragments.add(ZhanZhanQueryFragment.getInstance(mParentAty.mPreQLNOInfo));
			}
			fragment = lstFragments.get(position);
			break;
		case 1:
			if (lstFragments.size() <= position || lstFragments.get(position) == null){
				lstFragments.add(new TrainNumQueryFragment());
			}
			fragment = lstFragments.get(position);
			break;
		default:
			break;
		}
		return fragment;
	}

	@Override
	public int getCount() {
		return strTitles.length;
	}

	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return strTitles[position];
	}

	public TrainSchAty getParentAty() {
		return mParentAty;
	}

	public void setParentAty(TrainSchAty parentAty) {
		mParentAty = parentAty;
	}
}
