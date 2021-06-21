package com.wehang.ystv.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.google.gson.reflect.TypeToken;
import com.hyphenate.EMCallBack;

import com.tencent.TIMManager;
import com.tencent.android.tpush.XGPushManager;
import com.wehang.ystv.Constant;
import com.wehang.ystv.R;
import com.wehang.ystv.VideoApplication;
import com.wehang.ystv.bo.VersionInfo;
import com.wehang.ystv.interfaces.UrlConstant;
import com.wehang.ystv.utils.ClearCacheUtil;
import com.wehang.ystv.utils.CommonUtil;
import com.wehang.ystv.utils.ToastUtil;
import com.wehang.ystv.utils.UserLoginInfo;
import com.whcd.base.activity.BaseActivity;
import com.whcd.base.interfaces.BaseData;
import com.whcd.base.interfaces.BaseResult;
import com.whcd.base.interfaces.HttpTool;
import com.whcd.base.service.AppUpgradeService;
import com.whcd.base.utils.DisplayUtils;
import com.whcd.base.widget.TopMenuBar;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;

import static com.wehang.ystv.ui.HomeActivity.instance;

/**
 * <p>
 * {设置界面}
 * </p>
 *
 * @author 宋瑶 2016-7-5 上午9:45:54
 * @version V1.0
 * @modificationHistory=========================重大变更说明
 * @modify by user: 宋瑶 2016-7-5
 */
public class SettingActivity extends BaseActivity {
    private TopMenuBar topMenuBar;
    private View safeView, clearCacheView, aboutUsView, commonproblemsViw;

    private String token;
    private TextView cleanCacheTv;
    private TextView tv_cache;
    private TextView set_vision_code;
    private ToggleButton pushTBtn;

    @Override
    protected void initTitleBar() {
        topMenuBar = (TopMenuBar) findViewById(R.id.topMenuBar);
        topMenuBar.setTitle("设置");
        Button leftButton = topMenuBar.getLeftButton();
        leftButton.setOnClickListener(this);
        topMenuBar.hideRightButton();
    }

    @Override
    public void onClick(View v) {
        if (CommonUtil.isFirstClick())
            return;
        Intent intent;
        switch (v.getId()) {
            case R.id.title_btn_left:
                finish();
                break;
            //意见反馈
            case R.id.safeView:
                intent = new Intent(this, FeedbackActivity.class);
                startActivity(intent);
                break;

            case R.id.backListLayout:

                intent = new Intent(this, AboutDetailActivity.class);
                intent.putExtra("type", 2);
                startActivity(intent);
                break;

           /* case R.id.PushLayout:
                intent = new Intent(this, PushManagerActivity.class);
                startActivity(intent);
                break;*/

            //检查更新
            case R.id.commonproblemsViw:

//                intent = new Intent(this, CommonProblemActivity.class);
//                startActivity(intent);
//                checkUpgrade(1);
                //检测版本更新
                updateVersion();
                break;

            case R.id.aboutUsView:
                intent = new Intent(this, AboutUsActivity.class);
                startActivity(intent);
                break;

            case R.id.clearCacheLayout:


                ClearCacheUtil.cleanCache(this);
                tv_cache.setText("0K");
                ToastUtil.makeText(context, "清除成功", Toast.LENGTH_SHORT).show();

                break;

            case R.id.exitBtn:
                CommonUtil.showYesNoDialog(this, "确认退出?", "确认", "取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        if (arg1 == DialogInterface.BUTTON_POSITIVE) {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("token", token);
                            HttpTool.doPost(SettingActivity.this, UrlConstant.LOGOUT, params, false, new TypeToken<BaseResult<BaseData>>() {
                            }.getType(), new HttpTool.OnResponseListener() {
                                @Override
                                public void onSuccess(BaseData baseData) {
                                    //使用腾讯IM
                                    TIMManager.getInstance().logout();
                                    UserLoginInfo.exitLogin(SettingActivity.this);
                                    Intent intent = new Intent(context, LoginActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();

                                }

                                @Override
                                public void onError(int errorCode) {
                                    UserLoginInfo.loginOverdue(context, errorCode);
                                }
                            });
                        }
                    }
                }).show();
                break;
            case R.id.xiugaiView:
                intent=new Intent(SettingActivity.this,ForgetPwActivity.class);
                startActivity(intent);
            default:
                break;
        }
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_setting;
    }

    @Override
    protected void initView() {

        findViewById(R.id.xiugaiView).setOnClickListener(this);

        tv_cache = (TextView)findViewById(R.id.tv_cache);
        set_vision_code = (TextView)findViewById(R.id.set_vision_code);
        pushTBtn = (ToggleButton) findViewById(R.id.pushTBtn);
        pushTBtn.setChecked(SPUtils.getInstance().getBoolean(Constant.JPUSHTBTN,true));
        pushTBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    JPushInterface.resumePush(SettingActivity.this);
                    SPUtils.getInstance().put(Constant.JPUSHTBTN,true);

                }else{
                    SPUtils.getInstance().put(Constant.JPUSHTBTN,false);
                    JPushInterface.stopPush(SettingActivity.this);
                }

            }
        });
//        set_vision_code.setText("版本号："+AppUtils.getAppVersionName());
        LogUtils.a("Constant.VERSIONNAME_UPGRADE ="+Constant.VERSIONNAME_UPGRADE+"---AppUtils.getAppVersionName() = "+AppUtils.getAppVersionName());

        set_vision_code.setText(Constant.VERSIONNAME_UPGRADE.equals(AppUtils.getAppVersionName())?"已是最新版本":"");
        tv_cache.setText(ClearCacheUtil.getTotalCache(SettingActivity.this));
        clearCacheView = findViewById(R.id.clearCacheLayout);
        clearCacheView.setOnClickListener(this);
        safeView = findViewById(R.id.safeView);
        safeView.setOnClickListener(this);
        aboutUsView = findViewById(R.id.aboutUsView);
        aboutUsView.setOnClickListener(this);
        commonproblemsViw = findViewById(R.id.commonproblemsViw);
        commonproblemsViw.setOnClickListener(this);
        findViewById(R.id.backListLayout).setOnClickListener(this);
        findViewById(R.id.PushLayout).setOnClickListener(this);
        cleanCacheTv = (TextView) findViewById(R.id.cleanCacheTv);
        try {
        } catch (Exception e) {
            e.printStackTrace();
        }
        findViewById(R.id.exitBtn).setOnClickListener(this);
    }

    @Override
    protected void initData(Bundle bundle) {
        if (UserLoginInfo.isLogin(this)) {
            token = UserLoginInfo.getUserToken();
        }
    }





    public void updateVersion() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("type", "Android");
        HttpTool.doPost(this, UrlConstant.QUERYVERSION, params, true, new TypeToken<BaseResult<VersionInfo>>() {
        }.getType(), new HttpTool.OnResponseListener() {

            @Override
            public void onSuccess(BaseData data) {
                VersionInfo version = (VersionInfo) data;
                Constant.VERSIONCODE_UPGRADE= version.versionCode;
                Constant.VERSIONNAME_UPGRADE= version.versionName;
                updateVersion(version, (VideoApplication) getApplication());
            }

            @Override
            public void onError(int errorCode) {
                UserLoginInfo.loginOverdue(context, errorCode);
            }
        });
    }
    /**
     * 提示更新
     */
    public void updateVersion(final VersionInfo data, final VideoApplication AmericaApplication) {
        if (data.versionCode > AmericaApplication.getIntAppParam("localVersion")) {
            final AlertDialog ad = new AlertDialog.Builder(this).create();
            ad.show();

            // 关键在下面的两行,使用window.setContentView,替换整个对话框窗口的布局
            Window window = ad.getWindow();
            window.setContentView(R.layout.view_dialog_yes_no);

            TextView titleView = (TextView) window.findViewById(R.id.titleTV);
            TextView detailsView = (TextView) window.findViewById(R.id.dialogTv);

            titleView.setText(data.versionName + "更新提示");
            detailsView.setText("请到各大应用市场上更新最新版本");

            WindowManager.LayoutParams params = window.getAttributes();
            params.width = (int) (DisplayUtils.getScreenWidth(instance) * 0.9);
            window.setAttributes(params);

            window.findViewById(R.id.noBtn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ad.isShowing()) {
                        ad.dismiss();
                    }
                }
            });
            window.findViewById(R.id.yesBtn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ad.isShowing()) {
                        ad.dismiss();
                    }
                    AmericaApplication.putAppParam("downloadUrl",
                            data.downloadUrl);
                    Intent serviceIntent = new Intent(instance,
                            AppUpgradeService.class);
                    instance.startService(serviceIntent);
                }
            });
        }else{
            ToastUtil.makeText(SettingActivity.this,"已是最新版本",Toast.LENGTH_SHORT).show();
        }
    }
}
