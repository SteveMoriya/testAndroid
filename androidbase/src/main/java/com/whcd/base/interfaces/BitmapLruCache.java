package com.whcd.base.interfaces;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.whcd.base.component.volley.toolbox.ImageLoader.ImageCache;

/**
 * 
 * <p>
 * 图片缓存类(Lru算法)
 * </p>
 * 
 * @author 张家奇 2016年4月13日 下午8:07:44
 * @version V1.0
 * 
 * 
 * @modificationHistory=========================重大变更说明
 * @modify by user: 张家奇 2016年4月13日
 */
public class BitmapLruCache extends LruCache<String, Bitmap> implements ImageCache {
	private static final String TAG = BitmapLruCache.class.getSimpleName();
	private BitmapSoftRefCache softRefCache;// 图片缓存的软引用

	public BitmapLruCache(int maxSize) {
		super(maxSize);
		softRefCache = new BitmapSoftRefCache();// 初始化BitmapSoftRefCache
	}

	@Override
	protected int sizeOf(String key, Bitmap value) {
		return value.getRowBytes() * value.getHeight();
	}

	@Override
	protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
		if (evicted) {
			softRefCache.putBitmap(key, oldValue);// 将bitmap添加到软引用的缓存中
		}

	}

	/**
	 * 从缓存中获取图片
	 */
	@Override
	public Bitmap getBitmap(String url) {
		Bitmap bitmap = get(url);
		if (bitmap == null) {
			bitmap = softRefCache.getBitmap(url);// 从软引用缓存中获取
		}
		return bitmap;
	}

	/**
	 * 将图片放入到缓存中
	 */
	@Override
	public void putBitmap(String url, Bitmap bitmap) {
		put(url, bitmap);
	}
}