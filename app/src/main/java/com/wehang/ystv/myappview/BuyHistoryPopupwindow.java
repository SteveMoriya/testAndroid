package com.wehang.ystv.myappview;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.hyphenate.util.DensityUtil;

import com.wehang.ystv.R;
import com.whcd.base.utils.DisplayUtils;


/**
 * 
 * <p>
 * {点击直播或观看直播的消息按钮时弹出的popupwindow}
 * </p>
 * 
 * @author 常瑞 2016-6-4 下午1:52:50
 * @version V1.0
 * 
 * 
 * @modificationHistory=========================重大变更说明
 * @modify by user: 常瑞 2016-6-4
 */
public class BuyHistoryPopupwindow extends PopupWindow implements OnClickListener {
	private LayoutInflater mInflater;;
	private View liveToolsPopupwindow;
	public PopupWindow popupWindow;
	private View v;
	private mShardViedo lookShardViedo;
	private Context context;

	public mShardViedo getLookShardViedo() {
		return lookShardViedo;
	}

	public void setLookShardViedo(mShardViedo lookShardViedo) {
		this.lookShardViedo = lookShardViedo;
	}

	public BuyHistoryPopupwindow(View v, Context context) {
		this.v = v;
		this.context=context;
		mInflater = LayoutInflater.from(context);
		liveToolsPopupwindow = mInflater.inflate(R.layout.hisbuy_pop, null);
		popupWindow = new PopupWindow(liveToolsPopupwindow, DisplayUtils.dip2px(context, 100), DisplayUtils.dip2px(context, 177), true);
		popupWindow.setOutsideTouchable(true);
		// 小键盘消失后popupWindow返回原位
		popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		// 进入退出动画
		popupWindow.setAnimationStyle(R.style.in_out_pop_live);
		liveToolsPopupwindow.findViewById(R.id.all).setOnClickListener(this);
		liveToolsPopupwindow.findViewById(R.id.waitpay).setOnClickListener(this);
		liveToolsPopupwindow.findViewById(R.id.waiuse).setOnClickListener(this);
		liveToolsPopupwindow.findViewById(R.id.havedone).setOnClickListener(this);
		liveToolsPopupwindow.findViewById(R.id.refund).setOnClickListener(this);
	}

	public void showPopWindow() {
		int[] location = new int[2];
		v.getLocationOnScreen(location);
		int x=(location[0] + v.getWidth() / 2) - popupWindow.getWidth() / 2;
		int y=(location[1] + popupWindow.getHeight()/2- DensityUtil.dip2px(context,50));
		Log.i("getlocation",location[0]+";"+location[1]+";"+x+";"+y);
		popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, x,y);
	}

	@Override
	public void onClick(View v) {
		int vId = 0;
		int i = v.getId();
		if (i == R.id.all) {
			vId = 1;

		} else if (i == R.id.waitpay) {
			vId = 2;

		} else if (i == R.id.waiuse) {
			vId = 3;

		} else if (i == R.id.havedone) {
			vId = 4;

		} else if (i == R.id.refund) {
			vId = 5;

		}
		dismiss();
		if (lookShardViedo != null && vId != 0) {
			lookShardViedo.shardViedo(vId);
		}
	}

	public interface mShardViedo {
		void shardViedo(int position);
	}
}
