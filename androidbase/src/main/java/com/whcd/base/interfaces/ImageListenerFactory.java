package com.whcd.base.interfaces;

import android.widget.ImageView;

import com.whcd.base.component.volley.VolleyError;
import com.whcd.base.component.volley.toolbox.ImageLoader.ImageContainer;
import com.whcd.base.component.volley.toolbox.ImageLoader.ImageListener;

/**
 * 
 * <p>
 * 图片加载状态监听
 * </p>
 * 
 * @author 张家奇 2016年4月13日 下午8:07:33
 * @version V1.0
 * 
 * 
 * @modificationHistory=========================重大变更说明
 * @modify by user: 张家奇 2016年4月13日
 */
public class ImageListenerFactory {
	private static final String TAG = ImageListenerFactory.class.getSimpleName();

	public static ImageListener getImageListener(final ImageView view, final int defaultImageResId, final int errorImageResId) {
		return new ImageListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				if (errorImageResId != 0) {
					view.setImageResource(errorImageResId);
				}
			}

			@Override
			public void onResponse(ImageContainer response, boolean isImmediate) {
				if (response.getBitmap() != null) {
					if (view.getTag().toString().equals(response.getRequestUrl())) {
						view.setImageBitmap(response.getBitmap());
					}
				} else if (defaultImageResId != 0) {
					view.setImageResource(defaultImageResId);
				}
			}
		};
	}
}