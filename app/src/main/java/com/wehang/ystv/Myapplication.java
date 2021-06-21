package com.wehang.ystv;

import com.whcd.base.BaseApplication;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by lenovo on 2017/8/8.
 */

public class Myapplication extends BaseApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        JPushInterface.setDebugMode(false);
        JPushInterface.init(this);
    }
}
