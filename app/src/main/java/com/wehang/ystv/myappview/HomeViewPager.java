package com.wehang.ystv.myappview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.wehang.ystv.adapter.SonPagerAdapter;


@SuppressLint("NewApi")
public class HomeViewPager extends ViewPager {

	public HomeViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public HomeViewPager(Context context) {
		super(context);
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);

		if (scrollChanged != null) {
			scrollChanged.onScrollChanged(l, t, oldl, oldt);
		}
	}

	public static interface OnScrollChanged {
		void onScrollChanged(int l, int t, int oldl, int oldt);

		void onChange();
	}

	private OnScrollChanged scrollChanged;

	public void setOnScrollChangedListener(OnScrollChanged scrollChanged) {
		this.scrollChanged = scrollChanged;
	}
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		getParent().requestDisallowInterceptTouchEvent(true);
		return super.onTouchEvent(ev);
	}
	@Override
	public PagerAdapter getAdapter() {
		return((SonPagerAdapter)super.getAdapter()).getCopyAdapter();
	}
}
