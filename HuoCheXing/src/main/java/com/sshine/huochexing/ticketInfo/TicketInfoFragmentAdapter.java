package com.sshine.huochexing.ticketInfo;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TicketInfoFragmentAdapter extends FragmentPagerAdapter {
	private String[] titleStr = {"购票指南","退票、改签"};
	public TicketInfoFragmentAdapter(FragmentManager fm) {
		super(fm);
	}

	//备用
	public TicketInfoFragmentAdapter(FragmentManager fm, Context context) {
		super(fm);
	}

	@Override
	public Fragment getItem(int position) {
		switch (position) {
		case 0:
			return BuyTicketInfoFragment.newInstance();
		case 1:
			return OtherTicketInfoFragment.newInstance();
		}
		return null;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return titleStr[position];
	}

	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}

	@Override
	public int getCount() {
		return titleStr.length;
	}
}
