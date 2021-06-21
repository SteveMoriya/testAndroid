package com.wehang.txlibrary.base;

import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.wehang.txlibrary.statubar.StatusBarCompat;


/**
 * Created by lenovo on 2017/6/14.
 */
public  class BaseActivity extends AppCompatActivity {
    /**
     * 网络观察者
     */
   // protected NetChangeObserver mNetChangeObserver = null;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

  /*      // 网络改变的一个回掉类
        mNetChangeObserver = new NetChangeObserver() {
            @Override
            public void onNetConnected(NetUtils.NetType type) {
                onNetworkConnected(type);
            }

            @Override
            public void onNetDisConnect() {
                onNetworkDisConnected();
            }
        };

        //开启广播去监听 网络 改变事件
        NetStateReceiver.registerObserver(mNetChangeObserver);*/

    }

   /* *
     * 网络连接状态
     *
     * @param type 网络状态*//**//**//**//**//*


    protected abstract onNetworkConnected(NetUtils.NetType type); *//*{
        //Toast.makeText(this,"网络连接正常",Toast.LENGTH_SHORT).show();
        ToastAlone.showToast(MyAppcation.getContext(), "网络连接正常", 4000);
    }*//*

    *//* * 网络断开的时候调用
     *//**//**//**//**//*

    protected  void onNetworkDisConnected(){
        ToastAlone.showToast(MyAppcation.getContext(),"网络连接断开",4000);
    };*/
    /**
     * 网络连接状态
     *
     * @param type 网络状态
     */
/*    protected abstract void onNetworkConnected(NetUtils.NetType type);

    *//**
     * 网络断开的时候调用
     */
   // protected abstract void onNetworkDisConnected();
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        StatusBarCompat.translucentStatusBar(this, true);

       // hideBottomUIMenu();
    }
    /**
     * 隐藏虚拟按键，并且全屏
     */
    protected void hideBottomUIMenu() {
        /*//隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }*/
    }
}


