package com.wehang.txlibrary.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.View;

import com.whcd.base.utils.CommonUtils;

/**
 * 
 * {fragment公共基类}
 * 
 * @author 刘树宝 2016年4月6日 下午4:57:21
 * @version V1.0
 * 
 * 
 * @modificationHistory=========================创建
 * @modify by user: 刘树宝 2016年4月6日
 */
public abstract class BaseFragment extends Fragment implements View.OnClickListener {
	protected long lastClickTime = 0;
	protected final int SUCCESS = 0;
	protected final int TIME_INTERVAL = 500;
	protected boolean isVisible;
	/** 标志位，标志已经初始化完成 */
	protected boolean isPrepared;
	/** 是否已被加载过一次，第二次就不再去请求数据了 */
	protected boolean isLoaded;
	/**
	 * 显示提示信息
	 * 
	 * @param msg
	 */
	public void showTipMsg(String msg) {
		CommonUtils.showTipMsg(getActivity(), Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 120, msg);
	}

	/**
	 * 判断上下文是否有效
	 * 
	 * @param context
	 * @return
	 */
	protected boolean isValidContext(Context context) {
		Activity a = (Activity) context;
		if (a.isFinishing()) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (getUserVisibleHint()) {
			isVisible = true;
			onVisible();
		} else {
			isVisible = false;
			onInvisible();
		}
	}

	/**
	 * 可见
	 */
	protected void onVisible() {
		lazyLoad();
	}

	/**
	 * 不可见
	 */
	protected void onInvisible() {

	}

	/**
	 * 延迟加载 子类必须重写此方法
	 */
	protected void lazyLoad() {
		if (!isPrepared || !isVisible || isLoaded) {
			return;
		}
	}

	@Override
	public void onClick(View v) {
		// 避免连续点击
		if (System.currentTimeMillis() - lastClickTime < TIME_INTERVAL)
			return;

		lastClickTime = System.currentTimeMillis();

	}

	public boolean isLoaded() {
		return isLoaded;
	}
}
