package com.wehang.ystv.component.emojicon;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.style.ImageSpan;

/**
 * @author 钟林宏 2017/1/23 14:33
 * @version V1.0
 * @ProjectName: ToLiveTencentIM
 * @Copyright: 2015 WHCD Co., Ltd. All Right Reserved.
 * @address:
 * @date: 2017/1/23 14:33
 * @Description: 本内容仅限于公司内部使用，禁止转发.
 * @instructions:图片居中ImageSpan
 * @modificationHistory=========================重大变更说明
 * @modify by user: 钟林宏 2017/1/23
 */

public class CenterImageSpan extends ImageSpan {
    public CenterImageSpan(Context arg0, Bitmap arg1) {
        super(arg0, arg1);
    }

    public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        Drawable d = getDrawable();
        Rect rect = d.getBounds();
        if (fm != null) {
            Paint.FontMetricsInt fmPaint = paint.getFontMetricsInt();
            int fontHeight = fmPaint.bottom - fmPaint.top;
            int drHeight = rect.bottom - rect.top;

            int top = drHeight / 2 - fontHeight / 4;
            int bottom = drHeight / 2 + fontHeight / 4;

            fm.ascent = -bottom;
            fm.top = -bottom;
            fm.bottom = top;
            fm.descent = top;
        }
        return rect.right;
    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end,
                     float x, int top, int y, int bottom, Paint paint) {
        Drawable b = getDrawable();
        canvas.save();
        int transY = 0;
        transY = ((bottom - top) - b.getBounds().bottom) / 2 + top;
        canvas.translate(x, transY);
        b.draw(canvas);
        canvas.restore();
    }
}
