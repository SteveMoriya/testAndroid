package com.whcd.base.interfaces;

import java.lang.ref.SoftReference;
import java.util.LinkedHashMap;

import android.graphics.Bitmap;

import com.whcd.base.component.volley.toolbox.ImageLoader.ImageCache;

/**
 * 
 * <p>
 * 图片缓存管理类(软引用)
 * </p>
 * 
 * @author 张家奇 2016年4月13日 下午8:04:18
 * @version V1.0
 * 
 * 
 * @modificationHistory=========================重大变更说明
 * @modify by user: 张家奇 2016年4月13日
 */
public class BitmapSoftRefCache implements ImageCache {
	private static final String TAG = BitmapSoftRefCache.class.getSimpleName();

	private LinkedHashMap<String, SoftReference<Bitmap>> map;// 创建一个集合保存Bitmap

	public BitmapSoftRefCache() {
		map = new LinkedHashMap<String, SoftReference<Bitmap>>();
	}

	/**
	 * 根据图片url从缓存中拿出bitmap
	 */
	@Override
	public Bitmap getBitmap(String url) {
		Bitmap bitmap = null;
		SoftReference<Bitmap> softRef = map.get(url);
		if (softRef != null) {
			bitmap = softRef.get();
			if (bitmap == null) {
				map.remove(url);// 从集合中移除
			}
		}
		return null;
	}

	/**
	 * 把图片放进缓存中
	 */
	@Override
	public void putBitmap(String url, Bitmap bitmap) {
		SoftReference<Bitmap> softRef = new SoftReference<Bitmap>(bitmap);
		map.put(url, softRef);
	}
}