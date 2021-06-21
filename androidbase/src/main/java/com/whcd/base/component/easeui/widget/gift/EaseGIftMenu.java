package com.whcd.base.component.easeui.widget.gift;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.whcd.base.R;
import com.whcd.base.component.easeui.widget.emojicon.EaseEmojiconIndicatorView;
import com.whcd.base.component.easeui.widget.gift.EaseGiftPagerView.EaseGiftPagerViewListener;

public class EaseGIftMenu extends LinearLayout {
	protected EaseGiftMenuListener listener;
	private EaseEmojiconIndicatorView indicatorView;
	private EaseGiftPagerView pagerView;
	private TextView sendTv;
	private TextView leftDiamondTv;
	private List<List<EaseGiftEntity>> pageEaseGiftEntities;
	protected static long lastClickTime = 0;
	public EaseGIftMenu(Context context) {
		super(context);
		init(context);
	}

	@SuppressLint("NewApi")
	public EaseGIftMenu(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public EaseGIftMenu(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		LayoutInflater.from(context).inflate(R.layout.ease_widget_gift, this);
		pageEaseGiftEntities = new ArrayList<List<EaseGiftEntity>>();

		pagerView = (EaseGiftPagerView) findViewById(R.id.pager_view);
		indicatorView = (EaseEmojiconIndicatorView) findViewById(R.id.indicator_view);
		sendTv = (TextView) findViewById(R.id.sendTv);
		leftDiamondTv = (TextView) findViewById(R.id.leftDiamondTv);
		pagerView.setEaseGiftPagerViewListener(new GiftPagerViewListener());
		View rechargeTv = findViewById(R.id.rechargeTv);
		rechargeTv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (listener != null) {
					listener.onRechargeClick();
				}
			}
		});
	}

	public void init(List<EaseGiftEntity> easeGiftEntities) {
		List<EaseGiftEntity> list = null;
		for (int i = 0; i < easeGiftEntities.size(); i++) {
			if (i % 8 == 0) {
				if (list != null) {
					pageEaseGiftEntities.add(list);
				}
				list = new ArrayList<EaseGiftEntity>();
			}
			list.add(easeGiftEntities.get(i));
		}
		if (list != null && list.size() > 0) {
			pageEaseGiftEntities.add(list);
		}
		pagerView.initData(pageEaseGiftEntities);

		if (listener != null) {
			leftDiamondTv.setText(String.valueOf(listener.getLeftDiamound()));
		}
	}

	/**
	 * 设置回调监听
	 * 
	 * @param listener
	 */
	public void setEaseGiftMenuListener(EaseGiftMenuListener listener) {
		this.listener = listener;
	}

	public interface EaseGiftMenuListener {
		void onGiftSendClicked(EaseGiftEntity giftEntity, RefreshLeftDiamoundListener callback);

		void onRechargeClick();

		int getLeftDiamound();
	}

	public interface RefreshLeftDiamoundListener {
		void onRefresh();
	}

	private class GiftPagerViewListener implements EaseGiftPagerViewListener {
		@Override
		public void onGiftPagePostionChanged(int newPosition) {
			indicatorView.selectTo(newPosition);
		}

		@Override
		public void onGiftAllSize(int maxSize) {
			indicatorView.init(maxSize);
			indicatorView.selectTo(0);
		}

		@Override
		public void onGiftClick(final EaseGiftEntity easeGiftEntity) {
			if (easeGiftEntity == null) {
				sendTv.setClickable(false);
			} else {
				sendTv.setClickable(true);
				sendTv.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (System.currentTimeMillis() - lastClickTime < 1000) {
							return;
						}
						lastClickTime = System.currentTimeMillis();
						if (listener != null) {
							// if (easeGiftEntity.diamond <=
							// listener.getLeftDiamound()) {
							listener.onGiftSendClicked(easeGiftEntity, new RefreshLeftDiamoundListener() {
								@Override
								public void onRefresh() {
									leftDiamondTv.setText(String.valueOf(listener.getLeftDiamound()));
								}
							});
							// } else {
							// }
						}
					}
				});
			}
		}
	};
}
