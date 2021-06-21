package com.wehang.ystv.widget;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wehang.ystv.R;
import com.wehang.ystv.interfaces.UrlConstant;
import com.wehang.ystv.utils.GlideUtils;
import com.whcd.base.utils.DisplayUtils;
import com.whcd.base.widget.RoundImageView;


import java.util.Random;

/**
 * 
 * <p>
 * {弹幕View}
 * </p>
 * 
 * @author 常瑞 2016-6-11 下午12:01:13
 * @version V1.0
 * 
 * 
 * @modificationHistory=========================重大变更说明
 * @modify by user: 常瑞 2016-6-11
 */
public class BarrageView extends RelativeLayout {
	private Context context;
	private LayoutInflater inflate;
	private Animation translateAnimation;
	private long time;
	private int processIndex;

	public BarrageView(Context context) {
		super(context);
		this.context = context;
		inflate = LayoutInflater.from(context);
	}

	public BarrageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		inflate = LayoutInflater.from(context);
	}

	public BarrageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		inflate = LayoutInflater.from(context);
	}

	public void addBarrageView(String iconUrl, int isVip, String name, String content) {
		Random random = new Random();
		int number = random.nextInt(2);
		long processTime = System.currentTimeMillis();
		if (time - processTime < 700) {
			time = processTime;
			if (processIndex == number) {
				if (processIndex == 0) {
					processIndex = 1;
					number = 1;
				} else {
					processIndex = 0;
					number = 0;
				}
			}
		}

		int screenWeight = DisplayUtils.getScreenWidth((Activity) context);

		final View view = inflate.inflate(R.layout.layout_barrage, null);
		RoundImageView avatarImageVideo = (RoundImageView) view.findViewById(R.id.avatarImageVideo);
		ImageView isVipImg = (ImageView) view.findViewById(R.id.isVipImg);
		TextView userName = (TextView) view.findViewById(R.id.userName);
		TextView sayContent = (TextView) view.findViewById(R.id.sayContent);
		GlideUtils.loadHeadImage(context, UrlConstant.IP + iconUrl, avatarImageVideo);
		isVipImg.setVisibility(isVip == 1 ? View.VISIBLE : View.GONE);
		userName.setText(name);
		sayContent.setText(content);

		int w = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		int h = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		view.measure(w, h);
		float x = view.getMeasuredWidth();
		float y = view.getMeasuredHeight();
		float yPosition = x + screenWeight;
		if (number == 0) {
			translateAnimation = new TranslateAnimation(yPosition, -x, 0, 0);
		} else {
			translateAnimation = new TranslateAnimation(yPosition, -x, y + 20, y + 20);
		}

		translateAnimation.setRepeatCount(0);
		translateAnimation.setInterpolator(context, android.R.anim.linear_interpolator);
		translateAnimation.setDuration(5000);
		addView(view);
		view.startAnimation(translateAnimation);
		translateAnimation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				removeView(view);
			}
		});
	}
}
