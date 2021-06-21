package com.whcd.base.component.easeui.utils;

import android.content.Context;
import android.graphics.Bitmap.Config;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.whcd.base.R;
import com.whcd.base.component.easeui.controller.EaseUI;
import com.whcd.base.component.easeui.controller.EaseUI.EaseUserProfileProvider;
import com.whcd.base.component.easeui.domain.EaseUser;

public class EaseUserUtils {

	static EaseUserProfileProvider userProvider;
	static DisplayImageOptions portraitoptions;
	static {
		userProvider = EaseUI.getInstance().getUserProfileProvider();
		portraitoptions = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.default_portrait).showImageOnFail(R.drawable.default_portrait)
				.cacheInMemory(true).cacheOnDisk(true).bitmapConfig(Config.RGB_565).build();
	}

	/**
	 * 根据username获取相应user
	 * 
	 * @param username
	 * @return
	 */
	public static EaseUser getUserInfo(String username) {
		if (userProvider != null)
			return userProvider.getUser(username);

		return null;
	}

	/**
	 * 设置用户头像
	 * 
	 * @param username
	 */
	public static void setUserAvatar(Context context, String username, ImageView imageView) {
		EaseUser user = getUserInfo(username);
		if (user != null && user.getAvatar() != null) {
			try {
				int avatarResId = Integer.parseInt(user.getAvatar());
				Glide.with(context).load(avatarResId).into(imageView);
			} catch (Exception e) {
				// 正常的string路径
				Glide.with(context).load(user.getAvatar()).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.ease_default_avatar).into(imageView);
			}
		} else {
			Glide.with(context).load(R.drawable.ease_default_avatar).into(imageView);
		}
	}

	public static void setUserhead(Context context, String iconUrl, final ImageView imageView) {
		if (iconUrl != null) {
			ImageLoader.getInstance().displayImage(iconUrl, imageView, portraitoptions);
		} else {
			imageView.setImageResource(R.drawable.default_portrait);
		}
	}

	/**
	 * 设置用户昵称
	 */
	public static void setUserNick(String username, TextView textView) {
		if (textView != null) {
			EaseUser user = getUserInfo(username);
			if (user != null && user.getNick() != null) {
				textView.setText(user.getNick());
			} else {
				textView.setText(username);
			}
		}
	}
}
