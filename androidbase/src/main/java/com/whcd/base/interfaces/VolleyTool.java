package com.whcd.base.interfaces;

import android.app.ActivityManager;
import android.content.Context;
import android.widget.ImageView;

import com.whcd.base.component.volley.Request;
import com.whcd.base.component.volley.RequestQueue;
import com.whcd.base.component.volley.toolbox.ImageLoader;
import com.whcd.base.component.volley.toolbox.Volley;
import com.whcd.base.utils.LogUtils;

/**
 * 
 * <p>
 * Volley管理类
 * </p>
 * 
 * @author 张家奇 2016年4月13日 下午8:07:14
 * @version V1.0
 * 
 * 
 * @modificationHistory=========================重大变更说明
 * @modify by user: 张家奇 2016年4月13日
 */
public class VolleyTool {
	private static final String TAG = VolleyTool.class.getSimpleName();

	private static VolleyTool instance;
	// 请求队列
	private static RequestQueue mRequestQueue;
	// 创建ImageLoader
	private static ImageLoader mImageLoader;
	// 默认分配最大空间的几分之几
	private final static int RATE = 8;

	public VolleyTool(Context context) {
		// 初始化请求队列(默认创建5个线程)
		mRequestQueue = Volley.newRequestQueue(context);
		// 获取ActivityManager管理者
		ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		int maxSize = manager.getMemoryClass() / RATE;
		// 初始化ImageLoader对象
		mImageLoader = new ImageLoader(mRequestQueue, new BitmapLruCache(1024 * 1024 * maxSize));
		LogUtils.info(TAG, "VolleyTool初始化完成");
	}

	/**
	 * Volley的初始化操作，使用volley前必须调用此方法
	 */
	public static void init(Context context) {
		if (instance == null) {
			instance = new VolleyTool(context);
		}
	}

	/**
	 * 获取消息队列
	 */
	public static RequestQueue getRequestQueue() {
		throwIfNotInit();
		return mRequestQueue;
	}

	/**
	 * 获取ImageLoader
	 */
	public static ImageLoader getImageLoader() {
		throwIfNotInit();
		return mImageLoader;
	}

	/**
	 * 加入请求队列
	 */
	public static void addRequest(Request<?> request) {
		getRequestQueue().add(request);
	}

	/**
	 * 加载网络图片
	 */
	public static void getImage(String requestUrl, ImageView imageView) {
		getImage(requestUrl, imageView, 0, 0);
	}

	/**
	 * 加载网络图片
	 * 
	 */
	public static void getImage(String requestUrl, ImageView imageView, int defaultImageResId, int errorImageResId) {
		getImage(requestUrl, imageView, defaultImageResId, errorImageResId, 0, 0);
	}

	/**
	 * 加载网络图片
	 * 
	 */
	public static void getImage(String requestUrl, ImageView imageView, int defaultImageResId, int errorImageResId, int maxWidth, int maxHeight) {
		imageView.setTag(requestUrl);
		try {
			getImageLoader().get(requestUrl, ImageListenerFactory.getImageListener(imageView, defaultImageResId, errorImageResId), maxWidth, maxHeight);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 检查是否完成初始化
	 */
	private static void throwIfNotInit() {
		if (instance == null) {
			throw new IllegalStateException("VolleyTool尚未初始化，在使用前应该执行init()");
		}
	}
}