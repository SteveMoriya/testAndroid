package com.whcd.base.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.whcd.base.R;
import com.whcd.base.utils.CommonUtils;
import com.whcd.base.utils.DisplayUtils;
import com.whcd.base.utils.LogUtils;
import com.whcd.base.widget.CustomProgressDialog;


/**
 * <p>
 * {activity公共基类}
 * </p>
 *
 * @author 刘树宝 2016年4月6日 下午4:52:24
 * @version V1.0
 * @modificationHistory=========================创建
 * @modify by user: 刘树宝 2016年4月6日
 */
public abstract class BaseActivity extends FragmentActivity implements OnClickListener {
    protected String TAG = BaseActivity.class.getSimpleName();
    protected Activity context = this;
    protected long lastClickTime = 0;
    protected final int SUCCESS = 0;
    protected final int TIME_INTERVAL = 2000;
    private CustomReceiver customReceiver = null;
    protected CustomProgressDialog dialog;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(getLayoutResId());

        initView();
        customReceiver = new CustomReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(CommonUtils.CLOSE_ACTION);
        context.registerReceiver(customReceiver, filter);

        initStateBar();
        initTitleBar();
        initData(bundle);

    }

    @Override
    protected void onStop() {
        super.onStop();
        // VolleyTool.getRequestQueue().cancelAll(context.getClass().getName());
    }

    /**
     * 设置ViewID
     */
    protected abstract int getLayoutResId();

    /**
     * 设置标题栏
     *
     * @author 张家奇 2016年4月12日 下午7:10:06
     * @modificationHistory=========================方法变更说明
     * @modify by user: 张家奇 2016年4月12日
     * @modify by reason: 原因
     */
    protected abstract void initTitleBar();

    /**
     * 初始化控件
     */
    protected abstract void initView();

    /**
     * 初始化数据
     *
     * @param bundle 数据恢复bundle
     * @author 张家奇 2016年4月13日 上午10:57:57
     * @modificationHistory=========================方法变更说明
     * @modify by user: 张家奇 2016年4月13日
     * @modify by reason: 原因
     */
    protected abstract void initData(Bundle bundle);

    @Override
    public void onClick(View v) {
        // 避免连续点击
        if (System.currentTimeMillis() - lastClickTime < TIME_INTERVAL)
            return;

        lastClickTime = System.currentTimeMillis();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
    }

    protected void broadcastActivity(Intent intent) {
        if (CommonUtils.CLOSE_ACTION.equals(intent.getAction())) {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        if (customReceiver != null) {
            unregisterReceiver(customReceiver);
        }
        dialogDismiss();
        super.onDestroy();
    }


    protected void dialogDismiss() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    /**
     * 返回上一页方法
     */
    protected void back() {
        finish();
    }

    /**
     * @param context 当前activity是否取消掉
     * @return
     * @author 刘树宝 2016年4月6日 下午4:42:15
     * @modificationHistory=========================创建
     * @modify by user: 刘树宝 2016年4月6日
     */
    protected boolean isValidContext(Context context) {
        Activity a = (Activity) context;
        if (a.isFinishing()) {
            LogUtils.debug(TAG, "Activity is invalid. isFinishing-->" + a.isFinishing());
            return false;
        } else {
            return true;
        }
    }

    /**
     * 显示提示信息
     *
     * @param msg
     */
    public void showTipMsg(String msg) {
        CommonUtils.showTipMsg(context, Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0, msg);
    }

    public void showTipMsg(int resId) {
        CommonUtils.showTipMsg(context, Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0, getString(resId));
    }

    /**
     * 判断是否需要隐藏键盘
     *
     * @param v
     * @param event
     * @return
     */
    public boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            v.getLocationInWindow(leftTop);
            int left = 0;
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = DisplayUtils.getScreenWidth(context);
            if (event.getX() > left && event.getX() < right && event.getY() > top && event.getY() < bottom) {
                return false;
            } else {
                return true;
            }
        }
        return false;

    }

    /**
     * 点击非获取焦点EditText隐藏键盘
     *
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {
                try {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                } catch (Exception e) {
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }

    /**
     * <p>
     * {广播接收者}
     * </p>
     *
     * @author 刘树宝 2016年4月6日 下午4:41:19
     * @version V1.0
     * @modificationHistory=========================创建
     * @modify by user: 刘树宝 2016年4月6日
     */
    private class CustomReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            broadcastActivity(intent);
        }
    }

    protected boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
       // StatusBarCompat.translucentStatusBar(this, true);
        //initBar();
    }


    /**
     * 初始化沉浸式
     */
    private void initStateBar() {
        setColorId();
        if (isNeedLoadStatusBar()) {
            loadStateBar();
        }
    }

    protected int mColorId= R.color.main_color;//状态栏的默认背景色
    private SystemBarTintManager tintManager;
    private void loadStateBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }
        tintManager = new SystemBarTintManager(this);
        // 激活状态栏设置
        tintManager.setStatusBarTintEnabled(true);
        // 激活导航栏设置
        tintManager.setNavigationBarTintEnabled(true);
        // 设置一个状态栏颜色
        tintManager.setStatusBarTintResource(getColorId());
    }
    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    /**
     * 如果子类使用非默认的StatusBar,就重写此方法,传入布局的id
     */
    protected void setColorId() {
        //this.mColorId=R.color.XXX;子类重写方式
    }

    protected int getColorId() {
        return mColorId;
    }

    /**
     * 子类是否需要实现沉浸式,默认需要
     *
     * @return
     */
    protected boolean isNeedLoadStatusBar() {
        return true;
    }


}
