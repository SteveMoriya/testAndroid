package com.wehang.ystv.widget;

/**
 * Created by lenovo on 2017/9/27.
 */

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Toast;

import com.wehang.ystv.VideoApplication;

public class IndexViewPager extends ViewPager {

    private boolean isCanScroll = false;

    public IndexViewPager(Context context) {
        super(context);
    }

    public IndexViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setScanScroll(boolean isCanScroll) {
        this.isCanScroll = isCanScroll;
    }

    @Override
    public void scrollTo(int x, int y) {
        super.scrollTo(x, y);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
/*        // TODO Auto-generated method stub
        //手指按下的点为(x1, y1)手指离开屏幕的点为(x2, y2)
        float x1 = 0;
        float x2 = 0;
        float y1 = 0;
        float y2 = 0;
        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            //当手指按下的时候
            x1 = event.getX();
            y1 = event.getY();
        }
        if(event.getAction() == MotionEvent.ACTION_UP) {
            //当手指离开的时候
            x2 = event.getX();
            y2 = event.getY();
            if(y1 - y2 > 50) {
                isCanScroll=true;
                Toast.makeText(VideoApplication.applicationContext, "向上滑", Toast.LENGTH_SHORT).show();
            } else if(y2 - y1 > 50) {
                isCanScroll=true;
                Toast.makeText(VideoApplication.applicationContext, "向下滑", Toast.LENGTH_SHORT).show();
            } else if(x1 - x2 > 50) {
                isCanScroll=false;
                Toast.makeText(VideoApplication.applicationContext, "向左滑", Toast.LENGTH_SHORT).show();
            } else if(x2 - x1 > 50) {
                isCanScroll=false;
                Toast.makeText(VideoApplication.applicationContext, "向右滑", Toast.LENGTH_SHORT).show();
            }
        }
        if (isCanScroll) {
            return super.onTouchEvent(event);
        } else {

        }*/
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN://当手指按下的时候
                break;
            case MotionEvent.ACTION_UP:
                //当手指离开的时候
                break;
            case MotionEvent.ACTION_MOVE:
                int moveY = (int) ev.getY();
                if (moveY>50){
                    return super.onTouchEvent(ev);
                }
                break;
        }
        return false;
    }

    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        // TODO Auto-generated method stub
        super.setCurrentItem(item, smoothScroll);
    }

    @Override
    public void setCurrentItem(int item) {
        // TODO Auto-generated method stub
        super.setCurrentItem(item);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub

        /*if (isCanScroll) {
            return super.onInterceptTouchEvent(event);
        } else {

        }*/
        return false;
    }
}
