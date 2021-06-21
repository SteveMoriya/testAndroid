package com.whcd.base.widget;


import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Scroller;

public class SlideableView extends FrameLayout {
	protected Scroller mScroller;

	public SlideableView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mScroller = new Scroller(context);
	}

	public SlideableView(Context context) {
		super(context);
		mScroller = new Scroller(context);
	}

	public void smoothScrollTo(int desX, int desY) {
		int duration = 500;
		int offsetX = desX - getScrollX();
		int offsetY = desY - getScrollY();

		mScroller.startScroll(getScrollX(), getScrollY(), offsetX, offsetY,
				duration);
		// 只能在主线程中使用，刷新页面
		invalidate();
		// postInvalidate();
	}

	@Override
	public void computeScroll() {
		if (!mScroller.isFinished()) {
			if (mScroller.computeScrollOffset()) {
				int oldX = getScrollX();
				int oldY = getScrollY();
				int x = mScroller.getCurrX();
				int y = mScroller.getCurrY();
				if (oldX != x || oldY != y) {
					scrollTo(x, y);
				}
				invalidate();
			} else {
				clearChildrenCache();
			}
		} else {
			clearChildrenCache();
		}
	}

	private void clearChildrenCache() {
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			final View layout = (View) getChildAt(i);
			layout.setDrawingCacheEnabled(false);
		}
	}
}
