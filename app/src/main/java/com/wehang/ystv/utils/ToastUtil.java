package com.wehang.ystv.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;


import com.wehang.ystv.R;

import java.lang.reflect.Field;


/**
 * @author 钟林宏 2017/1/21 13:11
 * @version V1.0
 * @ProjectName: Test
 * @Copyright: 2015 WHCD Co., Ltd. All Right Reserved.
 * @address:
 * @date: 2017/1/21 13:11
 * @Description: 本内容仅限于公司内部使用，禁止转发.
 * @instructions:顶部弹出Toast
 * @modificationHistory=========================重大变更说明
 * @modify by user: 钟林宏 2017/1/21
 */

public class ToastUtil {
    private static Toast toast;

    private ToastUtil() {
    }

    /**
     * 调用有动画的Toast
     */
    public static Toast makeText(Context context, CharSequence text, int duration) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.base_view_toast, null);

        TextView textTv = (TextView) layout.findViewById(R.id.text);
        textTv.setText(text);
        if (toast == null) {
            toast = new Toast(context);
            toast.setGravity(Gravity.FILL_HORIZONTAL | Gravity.TOP, 0, 0);
            toast.setDuration(duration);
        }
        toast.setView(layout);
        try {
            Object mTN;
            mTN = getField(toast, "mTN");
            if (mTN != null) {
                Object mParams = getField(mTN, "mParams");
                if (mParams != null
                        && mParams instanceof WindowManager.LayoutParams) {
                    WindowManager.LayoutParams params = (WindowManager.LayoutParams) mParams;
                    params.windowAnimations = R.style.AnimationToast;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return toast;
    }

    /**
     * 反射字段
     *
     * @param object    要反射的对象
     * @param fieldName 要反射的字段名称
     */
    private static Object getField(Object object, String fieldName)
            throws NoSuchFieldException, IllegalAccessException {
        Field field = object.getClass().getDeclaredField(fieldName);
        if (field != null) {
            field.setAccessible(true);
            return field.get(object);
        }
        return null;
    }
}
