package com.wehang.txlibrary.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by lenovo on 2017/7/31.
 */

public class MyViewPager extends ViewPager{
    public MyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyViewPager(Context context) {
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
    }

    private OnScrollChanged scrollChanged;

    public void setOnScrollChangedListener(OnScrollChanged scrollChanged) {
        this.scrollChanged = scrollChanged;
    }
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        /*getParent().requestDisallowInterceptTouchEvent(true);
        return super.onTouchEvent(ev);*/
       /* switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN://当手指按下的时候
                break;
            case MotionEvent.ACTION_UP:
                //当手指离开的时候
                break;
            case MotionEvent.ACTION_MOVE:
                int moveY = (int) ev.getY();
                if (moveY>50){
                    return false;
                }
                break;
        }*/
        return super.onTouchEvent(ev);
    }
}
