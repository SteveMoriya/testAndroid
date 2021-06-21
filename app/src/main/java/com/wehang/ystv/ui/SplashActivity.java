package com.wehang.ystv.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.blankj.utilcode.util.LogUtils;
import com.google.gson.reflect.TypeToken;
import com.wehang.ystv.R;
import com.wehang.ystv.VideoApplication;
import com.wehang.ystv.bo.LiveOpenStatus;
import com.wehang.ystv.bo.Lives;
import com.wehang.ystv.bo.UserInfo;
import com.wehang.ystv.interfaces.UrlConstant;
import com.wehang.ystv.logic.TCLoginMgr;
import com.wehang.ystv.utils.ToastUtil;
import com.wehang.ystv.utils.UserLoginInfo;
import com.wehang.ystv.utils.Utils;
import com.wehang.ystv.utils.Validator;
import com.whcd.base.activity.BaseActivity;
import com.whcd.base.interfaces.BaseData;
import com.whcd.base.interfaces.BaseResult;
import com.whcd.base.interfaces.HttpTool;
import com.whcd.base.service.LocationService;
import com.whcd.base.widget.CustomProgressDialog;


import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class SplashActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks, TCLoginMgr.TCLoginCallback{
    private static final String TAG ="SplashActivity " ;
    private ImageView splash_iv;
    /*private XGPushDao xgPushDao = null;
    private List<XGPushInfo> localPushList = new ArrayList<XGPushInfo>();*/
    private SharedPreferences preferences = null;
    private UserInfo userInfo;
    boolean isFirstOpen;
    private boolean isOpenLive = true;
    private Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        context=SplashActivity.this;
        mTCLoginMgr = TCLoginMgr.getInstance();
        mTCLoginMgr.setTCLoginCallback(this);
        initView();
    }



    protected void initView() {
        splash_iv = (ImageView) findViewById(R.id.splash_iv);
//        isOpenLive();
        /*xgPushDao = new XGPushDao(context);
        localPushList = xgPushDao.queryXGPushInfo();*/
        preferences = getSharedPreferences("isFirstOpen", MODE_PRIVATE);
        isFirstOpen = preferences.getBoolean("isFirst", true);
    }



    @Override
    protected void onStop() {
        super.onStop();
//		XGPushManager.onActivityStoped(this);
    }

    private void isOpenLive() {
        Map<String, String> map = new HashMap<>();
        HttpTool.doPost(context, UrlConstant.IS_OPEN_LIVE, map, false, new TypeToken<BaseResult<LiveOpenStatus>>() {
        }.getType(), new HttpTool.OnResponseListener() {
            @Override
            public void onSuccess(BaseData data) {
                LiveOpenStatus mLiveOpenStatus = (LiveOpenStatus) data;
                if (mLiveOpenStatus != null) {
                    if (mLiveOpenStatus.open == 1) {
                        splash_iv.setImageResource(R.drawable.splash_bg);
                        isOpenLive = true;
                    } else {
                        isOpenLive = false;
                    }
                }
            }

            @Override
            public void onError(int errorCode) {
                UserLoginInfo.loginOverdue(SplashActivity.this, errorCode);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

//		Intent intent = new Intent("com.whcd.base.service.LocationService");
//		intent.setPackage(getPackageName());
//		startService(intent);
        locationAndContactsTask();
    }
    /**
     * 权限6.0
     */

    private static final int RC_LOCATION_CONTACTS_PERM = 124;


    @AfterPermissionGranted(RC_LOCATION_CONTACTS_PERM)
    public void locationAndContactsTask() {
        String[] perms = { Manifest.permission.RECORD_AUDIO,Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA };
        //有权限的话
        if (EasyPermissions.hasPermissions(this, perms)) {
            // Have permissions, do the thing!
          jumpData();
        }
        //没有权限
        else {
            // Ask for both permissions
            EasyPermissions.requestPermissions(this, "这个应用程序需要访问您的权限。",
                    RC_LOCATION_CONTACTS_PERM, perms);
        }
    }
    //签到需要读取手机信号的权限，必须通过
    private void isHavePhonePer(){
        String[]perms={Manifest.permission.READ_PHONE_STATE};
        if (EasyPermissions.hasPermissions(this,perms)){
            jumpData();
        }else {
            EasyPermissions.requestPermissions(this,"这个应用程序需要以下权限。",RC_LOCATION_CONTACTS_PERM,perms);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // EasyPermissions handles the request result.
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        LogUtils.a(TAG, "onPermissionsGranted:" + requestCode + ":" + perms.size());
        //jumpData();
        isHavePhonePer();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        LogUtils.a(TAG, "onPermissionsDenied:" + requestCode + ":" + perms.size());
        //jumpData();
        isHavePhonePer();
        // (Optional) Check whether the user denied any permissions and checked "NEVER ASK AGAIN."
        // This will display a dialog directing them to enable the permission in app settings.
//        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
//            new AppSettingsDialog.Builder(this).build().show();
//        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            // Do something after user returned from app settings screen, like showing a Toast.
            Toast.makeText(this, "简体中文从应用程序设置返回到 mainactivity", Toast.LENGTH_SHORT)
                    .show();
        }
    }


    private void needtodo(){
        /*if (isOpenLive) {
            isFirstOpen=false;
            if (isFirstOpen) {

            } else {

              *//*  if (userInfo == null) {

                } else {
                    intent = new Intent(context, HomeActivity.class);
                    qianda(userInfo);
                }*//*

            }
        } else {

        }*/
        Intent intent;

        if (UserLoginInfo.getUserInfo()!=null){
            loginOrBind(UserLoginInfo.getUserInfo());
        }else {
            intent = new Intent(SplashActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        }

    }
    private Handler handler=new Handler(VideoApplication.applicationContext.getMainLooper()){
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            needtodo();
        };
    };
    private void jumpData() {

      handler.sendEmptyMessageDelayed(2,1500);
    }
    private UserInfo loginInfo;
    private void loginOrBind(UserInfo userInfo) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("phone", userInfo.phone);
        params.put("password", userInfo.password);
        String url = null;
        Type typeToken;
        url = UrlConstant.LOGIN;
        typeToken = new TypeToken<BaseResult<UserInfo>>() {
        }.getType();

        HttpTool.doPost(this, url, params, true, typeToken, new HttpTool.OnResponseListener() {
            @Override
            public void onSuccess(BaseData data) {
                //登录

                UserInfo info = (UserInfo) data;
                LogUtils.i("ysToken",info.token);
                LogUtils.i("info",info.toString());
                if (info != null) {
                    //有些不需要的属性自己设置
                    info.level=1;

                    UserLoginInfo.saveUserInfo(context,info);
                    loginInfo=info;
                    loginProcess(info);
                }

               /* } else {

                    UserInfo userInfo = UserLoginInfo.getUserInfo(LoginByPhoneActivity.this);
                    userInfo.phone = phoneNum;
                    UserLoginInfo.saveUserInfo(LoginByPhoneActivity.this, userInfo);
                    Intent intent = new Intent();
                    intent.putExtra("phone", phoneNum);
                    setResult(RESULT_OK, intent);
                    finish();
                }*/
            }

            @Override
            public void onError(int errorCode) {
                ToastUtil.makeText(SplashActivity.this,"服务器异常",Toast.LENGTH_SHORT).show();
                Intent  intent = new Intent(SplashActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            }
        });
    }
    private TCLoginMgr mTCLoginMgr;
    private void loginProcess(final UserInfo loginInfo) {

        mTCLoginMgr.imLogin(loginInfo.userId, loginInfo.userSig);
        // mTCLoginMgr.pwdLogin("600001", "1234");
    }

    //腾讯登录
    @Override
    public void onSuccess() {

        //如果是注册自动登录成功后根据项目需要，设置自己的头像，性别，和是否VIP；
        UserLoginInfo.setTIMmessage(loginInfo);


        if (loginInfo != null) {
            // UserLoginInfo.saveUserInfo(LoginActivity.this,loginInfo);
            UserLoginInfo.saveUserSig(loginInfo.userSig);
            qianda(loginInfo);
            Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
            //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else {
            ToastUtil.makeText(context, "数据错误", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onFailure(int code, String msg) {

        if (code == 6208) {
            ToastUtil.makeText(context, "其他终端登录帐号被踢，需重新登录", Toast.LENGTH_SHORT).show();
        } else {
            ToastUtil.makeText(context, "错误码：" + code+"msg:"+msg, Toast.LENGTH_SHORT).show();
        }
    }



    private String getInfo() {
        TelephonyManager mTm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        String imei = mTm.getDeviceId();
        String imsi = mTm.getSubscriberId();
        String mtype = android.os.Build.MODEL; // 手机型号
        String numer = mTm.getLine1Number(); // 手机号码，有的可得，有的不可得
        return "手机型号: " + android.os.Build.MODEL + ",\nSDK版本:"
                + android.os.Build.VERSION.SDK + ",\n系统版本:"
                + android.os.Build.VERSION.RELEASE;
    }
    private void qianda(UserInfo userinfo) {
        UserInfo userInfo = UserLoginInfo.getUserInfo(context);
        if (userInfo != null) {
            startService(new Intent(context, LocationService.class));
            JPushInterface.setAlias(context, userInfo.userId, new TagAliasCallback() {
                @Override
                public void gotResult(int i, String s, Set<String> set) {

                }
            });
        }
        Set<String> tagSet = new LinkedHashSet<String>();
        if (userInfo.tag!=null){
            List<Lives>list=new ArrayList<>();
            list.addAll(userInfo.tag);

            if (list.size()>0){
                for (int i=0;i<list.size();i++){
                    tagSet.add(list.get(i).sourceId);
                }
                LogUtils.i("setpush",tagSet.toString());
                JPushInterface.setTags(context, tagSet, new TagAliasCallback() {
                    @Override
                    public void gotResult(int i, String s, Set<String> set) {
                        LogUtils.i("setpush",set.toString());
                    }
                });
            }
        }
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", userinfo.token);
        params.put("phoneInfo", getInfo()+"");
        HttpTool.doPost(context, UrlConstant.LOCATION_INFO, params, true, new TypeToken<BaseResult<BaseData>>() {
        }.getType(), new HttpTool.OnResponseListener() {
            @Override
            public void onSuccess(BaseData data) {
                //ToastUtil.makeText(context,"签到成功", Toast.LENGTH_SHORT).show();
                LogUtils.i("qiandao","ssss");
            }
            @Override
            public void onError(int errorCode) {
                UserLoginInfo.loginOverdue(SplashActivity.this, errorCode);
            }
        });

    }

}
