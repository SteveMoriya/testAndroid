/**
 * @ProjectName:
 * @Copyright: 2015 WHCD Co., Ltd. All Right Reserved.
 * @address: 
 * @date: 2016-11-3 下午3:46:27
 * @Description: 本内容仅限于公司内部使用，禁止转发.
 */
package com.wehang.ystv.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

/**
 * <p>
 * Glide图像裁剪
 * </p>
 * 
 * @author 钟林宏 2016-11-3 下午3:46:27
 * @version V1.0
 * 
 * 
 * @modificationHistory=========================重大变更说明
 * @modify by user: 钟林宏 2016-11-3
 */

public class GlideCircleTransform extends BitmapTransformation {
	private boolean showBorder;

	public GlideCircleTransform(Context context, boolean showBorder) {
		super(context);
		this.showBorder = showBorder;
	}

	@Override
	protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
		return createCircleImage(toTransform, 0, showBorder);
	}

	@Override
	public String getId() {
		return getClass().getName();
	}

	/**
	 * 根据原图绘制圆形图片
	 * 
	 * @author 钟林宏 2016-11-3 下午3:51:21
	 * @param source
	 * @param min
	 * @return
	 * 
	 * @modificationHistory=========================方法变更说明
	 * @modify by user: 钟林宏 2016-11-3
	 * @modify by reason: 原因
	 */
	public Bitmap createCircleImage(Bitmap source, int min, boolean showBorder) {
		final Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setDither(true);
		if (0 == min) {
			min = source.getHeight() > source.getWidth() ? source.getWidth() : source.getHeight();
		}
		Bitmap target = Bitmap.createBitmap(min, min, Bitmap.Config.ARGB_4444);
		// 创建画布
		Canvas canvas = new Canvas(target);
		// 绘圆
		canvas.drawCircle(min / 2, min / 2, min / 2, paint);
		// 设置交叉模式
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));

		// 绘制图片
		canvas.drawBitmap(source, 0, 0, paint);
		if (showBorder) {
			setBitmapBorder(canvas, min / 2, min / 2, min / 2);
		}
		return target;
	}

	/**
	 * 给bitmap设置边框
	 * 
	 * @param canvas
	 */
	private void setBitmapBorder(Canvas canvas, int cx, int cy, int radius) {
		Paint paint = new Paint();
		// 设置边框颜色
		paint.setColor(Color.GRAY);
		paint.setStyle(Paint.Style.STROKE);
		paint.setAntiAlias(true);
		paint.setDither(true);
		// 设置边框宽度
		paint.setStrokeWidth(1.5f);
		canvas.drawCircle(cx, cy, radius - 1.4f, paint);
	}
}
