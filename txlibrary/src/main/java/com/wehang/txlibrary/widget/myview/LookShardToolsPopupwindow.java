package com.wehang.txlibrary.widget.myview;

import android.app.Activity;
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
import android.widget.Toast;

import com.hyphenate.util.DensityUtil;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.wehang.txlibrary.R;
import com.whcd.base.utils.DisplayUtils;
import com.whcd.base.utils.LogUtils;


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
public class LookShardToolsPopupwindow extends PopupWindow implements OnClickListener {
	private LayoutInflater mInflater;;
	private View liveToolsPopupwindow;
	private PopupWindow popupWindow;
	private View v;
	private mShardViedo lookShardViedo;
	private Context context;
	private shareBean shareBean;


	public mShardViedo getLookShardViedo() {
		return lookShardViedo;
	}

	public void setLookShardViedo(mShardViedo lookShardViedo) {
		this.lookShardViedo = lookShardViedo;
	}

	public LookShardToolsPopupwindow(View v, Context context,shareBean shareBean) {
		this.v = v;
		this.context=context;
		this.shareBean=shareBean;
		mInflater = LayoutInflater.from(context);
		liveToolsPopupwindow = mInflater.inflate(R.layout.caonm, null);
		popupWindow = new PopupWindow(liveToolsPopupwindow, DisplayUtils.dip2px(context, 100), DisplayUtils.dip2px(context, 177), true);
		popupWindow.setOutsideTouchable(true);
		// 小键盘消失后popupWindow返回原位
		//popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		// 进入退出动画
		popupWindow.setAnimationStyle(R.style.in_out_pop_live);
		liveToolsPopupwindow.findViewById(R.id.weiBoTv).setOnClickListener(this);
		liveToolsPopupwindow.findViewById(R.id.weiXinTv).setOnClickListener(this);
		liveToolsPopupwindow.findViewById(R.id.circleOfFriendsTv).setOnClickListener(this);
		liveToolsPopupwindow.findViewById(R.id.qqTv).setOnClickListener(this);
		liveToolsPopupwindow.findViewById(R.id.qzoneTv).setOnClickListener(this);
	}

	public void showPopWindow() {
		int[] location = new int[2];
		v.getLocationOnScreen(location);
		int x=(location[0] + v.getWidth() / 2) - popupWindow.getWidth() / 2;
		int y=(location[1] + popupWindow.getHeight()/2- DisplayUtils.dip2px(context,50));
		Log.i("getlocation",location[0]+";"+location[1]+";"+x+";"+y);
		popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, x,y);
	}

	@Override
	public void onClick(View v) {
		int vId = 0;
		int i = v.getId();
		if (i == R.id.weiBoTv) {
			vId = R.id.weiBoTv;

		} else if (i == R.id.weiXinTv) {
			vId = R.id.weiXinTv;

		} else if (i == R.id.circleOfFriendsTv) {
			vId = R.id.circleOfFriendsTv;

		} else if (i == R.id.qqTv) {
			vId = R.id.qqTv;

		} else if (i == R.id.qzoneTv) {
			vId = R.id.qzoneTv;

		}
		dismiss();
		if (lookShardViedo != null && vId != 0) {
		shareLive(vId);
		}
	}
	private void shareLive(int vId) {
		UMImage image = new UMImage(context, shareBean.sourcePic);
		SHARE_MEDIA shardType = null;
		if (vId == R.id.weiBoTv) {
			shardType = SHARE_MEDIA.SINA;

		} else if (vId == R.id.weiXinTv) {
			shardType = SHARE_MEDIA.WEIXIN;

		} else if (vId == R.id.circleOfFriendsTv) {
			shardType = SHARE_MEDIA.WEIXIN_CIRCLE;

		} else if (vId == R.id.qqTv) {
			shardType = SHARE_MEDIA.QQ;

		} else if (vId == R.id.qzoneTv) {
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
		if(popupWindow!=null){
			popupWindow.dismiss();
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
}
