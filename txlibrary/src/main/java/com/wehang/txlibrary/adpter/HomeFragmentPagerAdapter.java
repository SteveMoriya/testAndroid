package com.wehang.txlibrary.adpter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.whcd.base.fragment.BaseFragment;

import java.util.List;

public class HomeFragmentPagerAdapter extends FragmentPagerAdapter {
	private List<Fragment> homeFragments;
	private BaseFragment currentFragment;
	private View mCurrentView;


	public HomeFragmentPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
		super(fm);
		this.homeFragments = fragments;
	}

	@Override
	public Fragment getItem(int arg0) {
		return homeFragments.get(arg0);
	}

	@Override
	public int getCount() {
		return homeFragments.size();
	}

	@Override
	public void setPrimaryItem(ViewGroup container, int position, Object object) {
		currentFragment = (BaseFragment) object;
		super.setPrimaryItem(container, position, object);
	}

	public BaseFragment getCurrentFragment() {
		return currentFragment;
	}

	@Override
	public int getItemPosition(Object object) {
		return super.getItemPosition(object);
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		super.destroyItem(container, position, object);
	}
}
