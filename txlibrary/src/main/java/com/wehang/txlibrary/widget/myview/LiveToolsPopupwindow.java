package com.wehang.txlibrary.widget.myview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.util.DensityUtil;
import com.wehang.txlibrary.R;
import com.whcd.base.utils.DisplayUtils;

/**
 * <p>
 * 视频相关设置
 * </p>
 *
 * @author 钟林宏 2016-9-28 下午3:11:39
 * @version V1.0
 * @modificationHistory=========================重大变更说明
 * @modify by user: 钟林宏 2016-9-28
 */
public class LiveToolsPopupwindow extends PopupWindow {
    private LayoutInflater mInflater;

    private View liveToolsPopupwindow;
    public PopupWindow popupWindow;
    private View v;

    private CheckBox cbCameraLight;// 闪光灯
    private TextView cbCameraTurnover;// 翻转摄像头
    private CheckBox cbCameraMagictool;// 美颜
    private int currentCamera = 1;// 当前摄像头 1前置 0后置
    private Context context;
    @SuppressLint("InflateParams")
    public LiveToolsPopupwindow(View v, final Context context, final LiveToolsOnItemClickLister toolsOnItemClickLister) {
        this.v = v;
        mInflater = LayoutInflater.from(context);
        this.context=context;
        liveToolsPopupwindow = mInflater.inflate(R.layout.popupwindow_ido, null);
        //popupWindow=liveToolsPopupwindow.findViewById(R.id.qiupop);
        popupWindow = new PopupWindow(liveToolsPopupwindow, DisplayUtils.dip2px(context, 100), DisplayUtils.dip2px(context, 115), true);
        popupWindow.setOutsideTouchable(true);

        // 小键盘消失后popupWindow返回原位
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // 进入退出动画
        popupWindow.setAnimationStyle(R.style.in_out_pop_live);

        cbCameraLight = (CheckBox) liveToolsPopupwindow.findViewById(R.id.cbCameraLight);
        //cbCameraLight.setEnabled(false);
        cbCameraLight.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (currentCamera==1){
                    cbCameraLight.setChecked(true);
                    cbCameraLight.setText("开闪光");
                    Toast.makeText(context,"只支持后置摄像头",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (toolsOnItemClickLister != null) {
                    toolsOnItemClickLister.liveToolsOnItemClick(0, cbCameraLight.isChecked(), 0);
                }
                cbCameraLight.setText(cbCameraLight.isChecked() ? "开闪光" : "关闪光");// checked状态下点击打开，not
            }
        });

        cbCameraTurnover = (TextView) liveToolsPopupwindow.findViewById(R.id.cbCameraTurnover);
        cbCameraTurnover.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //翻转
                if (currentCamera == 1) {
                    if (toolsOnItemClickLister != null) {
                        toolsOnItemClickLister.liveToolsOnItemClick(1, cbCameraLight.isChecked(), 0);//true
                    }
                    currentCamera = 0;
                    //cbCameraLight.setEnabled(true);
                } else if (currentCamera == 0) {
                    cbCameraLight.setChecked(true);
                    cbCameraLight.setText("开闪光");
                    if (toolsOnItemClickLister != null) {
                        toolsOnItemClickLister.liveToolsOnItemClick(1, cbCameraLight.isChecked(), 1);
                    }
                    currentCamera = 1;
                    //cbCameraLight.setEnabled(false);
                }
            }
        });

        cbCameraMagictool = (CheckBox) liveToolsPopupwindow.findViewById(R.id.cbCameraMagictool);
        cbCameraMagictool.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {// 打开美颜
                    cbCameraMagictool.setText("关美颜");
                } else {// 关闭美颜
                    cbCameraMagictool.setText("开美颜");
                }
                if (toolsOnItemClickLister != null) {
                    toolsOnItemClickLister.liveToolsOnItemClick(2, isChecked, 0);
                }
            }
        });
    }

    public void showPopWindow() {
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        //popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, (location[0] + v.getWidth() / 2) - popupWindow.getWidth() / 2, location[1] - popupWindow.getHeight());

        int x = (location[0] + v.getWidth() / 2) - popupWindow.getWidth() / 2;
        int y = (location[1] + popupWindow.getHeight() / 2-DensityUtil.dip2px(context,20));
        Log.i("getlocation", location[0] + ";" + location[1] + ";" + x + ";" + y);
        popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, x, y);
    }

    public interface LiveToolsOnItemClickLister {
        /**
         * 直播特效美化
         *
         * @param position    点击item的下标
         * @param isChecked   是否选中
         * @param frontOrBack 参数禁针对position为1时有效
         * @author 钟林宏 2016-9-28 下午3:09:38
         * @modificationHistory=========================方法变更说明
         * @modify by user: 钟林宏 2016-9-28
         * @modify by reason: 原因
         */
        public void liveToolsOnItemClick(int position, boolean isChecked, int frontOrBack);
    }
}