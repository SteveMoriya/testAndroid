package com.wehang.txlibrary.widget.myview;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.LayoutInflaterFactory;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.wehang.txlibrary.R;
import com.whcd.base.utils.DisplayUtils;
import com.whcd.base.utils.LogUtils;

public class BottomPopupWindow2 extends PopupWindow implements View.OnClickListener {
	private View contentView = null;
	private LayoutInflater mInflater;;
	private BottomPopupwindow.mShardViedo lookShardViedo;
	private Context context;
	private shareBean shareBean;
	private View v;

	public BottomPopupWindow2() {
		super();
	}

	public BottomPopupWindow2(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public BottomPopupWindow2(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public BottomPopupWindow2(Context context,View v,shareBean shareBean) {
		super(context);
		this.v=v;
		this.shareBean=shareBean;
		this.context=context;
		View popView= LayoutInflater.from(context).inflate(com.wehang.txlibrary.R.layout.fxpopwindows, null);;
		this.contentView = popView.findViewById(R.id.pop_conent);
		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) contentView.getLayoutParams();
		params.gravity = Gravity.BOTTOM;
		contentView.setTag(1);
		contentView.setLayoutParams(params);
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
		this.setAnimationStyle(R.style.popwin_anim_style);
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

		contentView.findViewById(com.wehang.txlibrary.R.id.circleOfFriendsTv).setOnClickListener(this);
		contentView.findViewById(com.wehang.txlibrary.R.id.weiXinTv).setOnClickListener(this);

		contentView.findViewById(com.wehang.txlibrary.R.id.qqTv).setOnClickListener(this);
		contentView.findViewById(com.wehang.txlibrary.R.id.qzoneTv).setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		int vId = 0;
		int i = v.getId();
		if (i == com.wehang.txlibrary.R.id.weiBoTv) {
			vId = com.wehang.txlibrary.R.id.weiBoTv;

		} else if (i == com.wehang.txlibrary.R.id.weiXinTv) {
			vId = com.wehang.txlibrary.R.id.weiXinTv;

		} else if (i == com.wehang.txlibrary.R.id.circleOfFriendsTv) {
			vId = com.wehang.txlibrary.R.id.circleOfFriendsTv;

		} else if (i == com.wehang.txlibrary.R.id.qqTv) {
			vId = com.wehang.txlibrary.R.id.qqTv;

		} else if (i == com.wehang.txlibrary.R.id.qzoneTv) {
			vId = com.wehang.txlibrary.R.id.qzoneTv;

		}
		dismiss();
		if (lookShardViedo != null && vId != 0) {
			shareLive(vId);
		}
	}
	private void shareLive(int vId) {
		UMImage image = new UMImage(context, shareBean.sourcePic);
		SHARE_MEDIA shardType = null;
		if (vId == com.wehang.txlibrary.R.id.weiBoTv) {
			shardType = SHARE_MEDIA.SINA;

		} else if (vId == com.wehang.txlibrary.R.id.weiXinTv) {
			shardType = SHARE_MEDIA.WEIXIN;

		} else if (vId == com.wehang.txlibrary.R.id.circleOfFriendsTv) {
			shardType = SHARE_MEDIA.WEIXIN_CIRCLE;

		} else if (vId == com.wehang.txlibrary.R.id.qqTv) {
			shardType = SHARE_MEDIA.QQ;

		} else if (vId == com.wehang.txlibrary.R.id.qzoneTv) {
			shardType = SHARE_MEDIA.QZONE;

		}
		shareBean.toString();
		LogUtils.info("shareBean",shareBean.toString());
		new ShareAction((Activity) context).setPlatform(shardType)
				.withText(shareBean.brife)
				.withTargetUrl(shareBean.Html)
				.withTitle(shareBean.sourceTitle)
				.withMedia(image)
				.setCallback(umShareListener)
				.share();
		if(this!=null){
			this.dismiss();
		}
	}
	public interface mShardViedo {
		void shardViedo(int position);
	}
	private UMShareListener umShareListener = new UMShareListener() {
		@Override
		public void onResult(SHARE_MEDIA platform) {
			Log.d("plat","platform"+platform);

			Toast.makeText(context, " 分享成功啦", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onError(SHARE_MEDIA platform, Throwable t) {
			Toast.makeText(context, " 分享失败啦", Toast.LENGTH_SHORT).show();
			if(t!=null){
				Log.d("throw","throw:"+t.getMessage());
			}
		}

		@Override
		public void onCancel(SHARE_MEDIA platform) {
			//Toast.makeText(context, " 分享取消了", Toast.LENGTH_SHORT).show();
		}


	};
	/*public void showPopWindow() {
		if (this!=null){
			this.
		}
	}*/
}
