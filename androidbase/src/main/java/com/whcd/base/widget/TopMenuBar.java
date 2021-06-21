package com.whcd.base.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.whcd.base.R;

public class TopMenuBar extends RelativeLayout {
	private Context context;
	private Button leftButton;
	private TextView titleTextView;
	private Button rightButton;
	private LayoutInflater inflater;
	private LinearLayout relativeLayout;

	public TopMenuBar(Context context) {
		super(context);
		this.context = context;
		init();
	}

	public TopMenuBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		init();
	}

	@SuppressLint("InflateParams")
	public void init() {
		removeAllViews();
		inflater = LayoutInflater.from(context);
		relativeLayout = (LinearLayout) inflater.inflate(R.layout.whcd_base_view_title_bar, null);
		addView(relativeLayout);
		leftButton = (Button) relativeLayout.findViewById(R.id.title_btn_left);
		titleTextView = (TextView) relativeLayout.findViewById(R.id.title_text);
		rightButton = (Button) relativeLayout.findViewById(R.id.title_btn_right);
	}

	public Button getLeftButton() {
		return leftButton;
	}

	public TopMenuBar setLeftButton(Button leftButton) {
		this.leftButton = leftButton;
		return this;
	}

	public TopMenuBar hideLeftButton() {
		leftButton.setVisibility(View.INVISIBLE);
		return this;
	}

	public TopMenuBar setTitle(String title) {
		this.titleTextView.setText(title);
		return this;
	}
	public TopMenuBar setTitleRitdrwable() {
		Drawable xia, img_off;
		Resources res = getResources();
		img_off = res.getDrawable(R.drawable.xiangxias);
//调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
		img_off.setBounds(0, 0, img_off.getMinimumWidth(), img_off.getMinimumHeight());
		this.titleTextView.setCompoundDrawables(null, null, img_off, null); //设置左图标
		this.titleTextView.setCompoundDrawablePadding(5);
		return this;
	}
	public TextView getTitle() {
		return titleTextView;
	}

	public Button getRightButton() {
		return rightButton;
	}

	public TopMenuBar setRightButton(Button rightButton) {
		this.rightButton = rightButton;
		return this;
	}

	public TopMenuBar hideRightButton() {
		rightButton.setVisibility(View.INVISIBLE);
		return this;
	}

	/**
	 * 添加右侧按钮，在removeRightButton后，重新添加右侧按钮使用
	 * 
	 * @return
	 */
	public Button showRightButton() {
		rightButton.setVisibility(View.VISIBLE);
		return rightButton;
	}

	public void setRightButtonDrawable(int resId) {
		if (rightButton.getVisibility() == View.VISIBLE) {
			rightButton.setBackgroundResource(resId);
		}
	}

	public Button showLeftButton() {
		leftButton.setVisibility(View.VISIBLE);
		return leftButton;
	}

}