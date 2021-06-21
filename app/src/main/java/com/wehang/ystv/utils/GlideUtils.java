/**
 * @ProjectName:
 * @Copyright: 2015 WHCD Co., Ltd. All Right Reserved.
 * @address: 
 * @date: 2016-8-20 下午2:47:47
 * @Description: 本内容仅限于公司内部使用，禁止转发.
 */
package com.wehang.ystv.utils;

import android.content.Context;
import android.os.Looper;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.wehang.ystv.R;


/**
 * <p>
 * Glide图片加载类
 * </p>
 * 
 * @author 钟林宏 2016-8-20 下午2:47:47
 * @version V1.0
 * 
 * 
 * @modificationHistory=========================重大变更说明
 * @modify by user: 钟林宏 2016-8-20
 */
public class GlideUtils {
	/***
	 * 加载普通图片
	 * 
	 * @author 钟林宏 2016-8-20 下午3:13:01
	 * @param context
	 * @param url
	 * @param imgView
	 * 
	 * @modificationHistory=========================方法变更说明
	 * @modify by user: 钟林宏 2016-8-20
	 * @modify by reason: 原因
	 */

	public static void loadBaseImage(Context context, String url, ImageView imgView) {
		if (isOnMainThread()) {
			Glide.with(context).load(url).diskCacheStrategy(DiskCacheStrategy.ALL)
					.placeholder(R.drawable.app144).error(R.drawable.app144).into(imgView);
		}
	}

	/***
	 * 加载圆形图片
	 * 
	 * @author 钟林宏 2016-8-20 下午3:13:18
	 * @param context
	 * @param url
	 * @param imgView
	 * 
	 * @modificationHistory=========================方法变更说明
	 * @modify by user: 钟林宏 2016-8-20
	 * @modify by reason: 原因
	 */
	public static void loadHeadImage(Context context, String url, final ImageView imgView) {
		if (isOnMainThread()) {
			Glide.with(context).load(url).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.mrtx).error(R.drawable.mrtx)
					.transform(new GlideCircleTransform(context, false)).into(imgView);
		}
	}

	public static void loadHeadBorderImage(Context context, String url, final ImageView imgView) {
		if (isOnMainThread()) {
			Glide.with(context).load(url).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.mrtx).error(R.drawable.mrtx)
					.transform(new GlideCircleTransform(context, true)).into(imgView);
		}
	}
	/**
	 * 判断当前是否在主线程
	 *
	 * @return
	 * @author 钟林宏 2016-10-25 下午10:00:38
	 * @modificationHistory=========================方法变更说明
	 * @modify by user: 钟林宏 2016-10-25
	 * @modify by reason: 原因
	 */
	private static boolean isOnMainThread() {
		return Looper.myLooper() == Looper.getMainLooper();
	}
}
