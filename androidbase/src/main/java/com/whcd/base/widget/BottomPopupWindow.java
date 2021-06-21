package com.whcd.base.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;

import com.whcd.base.R;

public class BottomPopupWindow extends PopupWindow {
	private View contentView = null;

	public BottomPopupWindow() {
		super();
	}

	public BottomPopupWindow(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public BottomPopupWindow(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public BottomPopupWindow(Context context, View view) {
		super(context);
		this.contentView = view;

		// 设置SelectAddressPopupWindow的View
		this.setContentView(contentView);

		// 设置SelectAddressPopupWindow弹出窗体的宽
		this.setWidth(LayoutParams.FILL_PARENT);

		// 设置SelectAddressPopupWindow弹出窗体的高
		this.setHeight(LayoutParams.FILL_PARENT);

		// 设置SelectAddressPopupWindow弹出窗体可点击
		this.setFocusable(true);

		// 实例化一个ColorDrawable颜色为半透明
		ColorDrawable dw = new ColorDrawable(0xb0000000);

		// 设置SelectAddressPopupWindow弹出窗体的背景
		this.setBackgroundDrawable(dw);
		contentView.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				int y = (int) event.getY();
				View view = contentView.findViewById(R.id.pop_conent);
				Object obj = view.getTag();
				if (obj == null) {
					int height = view.getBottom();
					if (event.getAction() == MotionEvent.ACTION_UP && y > height) {
						dismiss();
					}
				} else {
					int height = view.getTop();
					if (event.getAction() == MotionEvent.ACTION_UP && y < height) {
						dismiss();
					}
				}
				return true;
			}
		});
	}
}
