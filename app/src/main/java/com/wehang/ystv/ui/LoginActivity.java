package com.wehang.ystv.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.blankj.utilcode.util.LogUtils;
import com.google.gson.reflect.TypeToken;
import com.tencent.TIMCallBack;
import com.tencent.TIMFriendGenderType;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMManager;
import com.wehang.ystv.R;
import com.wehang.ystv.bo.Lives;
import com.wehang.ystv.bo.UserInfo;
import com.wehang.ystv.interfaces.UrlConstant;
import com.wehang.ystv.logic.TCLoginMgr;
import com.wehang.ystv.utils.CommonUtil;
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

public class LoginActivity extends BaseActivity implements TCLoginMgr.TCLoginCallback{
    private UserInfo loginInfo;
    private EditText phoneET,passEt;
    @Override
    protected int getLayoutResId() {
        return R.layout.activity_logina;
    }

    @Override
    protected void initTitleBar() {

    }
    private boolean zdlogin=false;
    @Override
    protected void onResume() {
        super.onResume();
        //注册回来后自动登录去设置IM
        if (UserLoginInfo.userInfo!=null){
            zdlogin=true;
            phoneET.setText(UserLoginInfo.userInfo.phone);
            passEt.setText(UserLoginInfo.userInfo.password);
            loginOrBind();
            UserLoginInfo.userInfo=null;
        }


    }

    @Override
    protected void initView() {
        phoneET=findViewById(R.id.login_phone);
        passEt=findViewById(R.id.login_password);
        findViewById(R.id.forgetPasswordTV).setOnClickListener(this);
        findViewById(R.id.registTv).setOnClickListener(this);

        findViewById(R.id.login_confirmTv).setOnClickListener(this);
    }

    @Override
    protected void initData(Bundle bundle) {
        mTCLoginMgr = TCLoginMgr.getInstance();
        mTCLoginMgr.setTCLoginCallback(this);

        if (UserLoginInfo.getUserInfo()!=null){
            UserInfo info=UserLoginInfo.getUserInfo();
            phoneET.setText(info.phone);
            //passEt.setText(info.password);
            passEt.setText(info.password);
            loginOrBind();
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (CommonUtil.isFirstClick()){
            return;
        }
        switch (v.getId()){

            case R.id.forgetPasswordTV:
                startActivity(new Intent(LoginActivity.this,ForgetPassword.class));
                break;
            case R.id.registTv:
                startActivity(new Intent(LoginActivity.this,RegistActivity.class));
                break;

            case R.id.login_confirmTv:

                loginOrBind();

                //mTCLoginMgr.imLogin("600006", "eJxlz11PgzAYBeB7fgXptdHyNal3hA0lY3GgGOCmYW1x3SbUts4Z438XcYlNvH5OznnfT8u2bfCYPVy2hAxvvcb6QzBg39gAgos-FIJT3GrsSfoP2UlwyXDbaSYndIIgcCE0M5yyXvOOnxOzUeHMcEX3eBr5LfBHHltQaEb484SrRR2neSzEbrnLy3RAzcEvizK*rxmqMyWLTdi8OpDcLU4o9tAhydNttCIq6vfRnJekyzZr*L68rehV8SR9VfFjk2TreL6tkjJslTGp*Qs7fwQ9-9oJPGTokUnFh34KuOO5juv9vAWB9WV9A3edXDA_");
                break;
        }
    }
    private void loginOrBind() {
        if (TextUtils.isEmpty(phoneET.getText().toString().trim())) {
            ToastUtil.makeText(context, "请输入手机号码", Toast.LENGTH_SHORT).show();
            phoneET.requestFocus();
            return ;
        }
        if (!Validator.isMobile(phoneET.getText().toString().trim())) {
            phoneET.requestFocus();
            ToastUtil.makeText(context, "手机号格式错误", Toast.LENGTH_SHORT).show();
            return ;
        }

        if (!Validator.isPassword(passEt.getText().toString().trim())){
            ToastUtil.makeText(context, "您输入的手机号或密码不正确，请重新输入", Toast.LENGTH_SHORT).show();
            passEt.requestFocus();
            return ;
        }
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("phone", phoneET.getText().toString().trim());
        params.put("password", passEt.getText().toString().trim());
        String url = null;
        Type typeToken;
            url = UrlConstant.LOGIN;
            typeToken = new TypeToken<BaseResult<UserInfo>>() {
            }.getType();
        dialog = CustomProgressDialog.show(context, "加载中...", true, null);
        HttpTool.doPost(this, url, params, true, typeToken, new HttpTool.OnResponseListener() {
            @Override
            public void onSuccess(BaseData data) {
                //登录
                dialogDismiss();
                UserInfo info = (UserInfo) data;
                LogUtils.i("ysToken",info.token);
                LogUtils.i("info",info.toString());
                if (info != null) {
                    //有些不需要的属性自己设置
                    info.level=1;

                    //没部署
                    info.password=passEt.getText().toString().trim();
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
                dialogDismiss();
                ToastUtil.makeText(context,"服务器异常",Toast.LENGTH_SHORT).show();
            }
        });
    }
    private TCLoginMgr mTCLoginMgr;
    private void loginProcess(final UserInfo loginInfo) {
        dialog.show();
        mTCLoginMgr.imLogin(loginInfo.userId, loginInfo.userSig);
       // mTCLoginMgr.pwdLogin("600001", "1234");
    }
        //腾讯登录
    @Override
    public void onSuccess() {
        dialogDismiss();
        //如果是注册自动登录成功后根据项目需要，设置自己的头像，性别，和是否VIP；

        UserLoginInfo.setTIMmessage(loginInfo);


        if (loginInfo != null) {
           // UserLoginInfo.saveUserInfo(LoginActivity.this,loginInfo);
            UserLoginInfo.saveUserSig(loginInfo.userSig);
            qianda(loginInfo);
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else {
            ToastUtil.makeText(context, "数据错误", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onFailure(int code, String msg) {
        dialogDismiss();
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
            Set<String> tagSet = new LinkedHashSet<String>();
            if (userInfo.tag!=null){
                List<Lives>list=new ArrayList<>();
                list.addAll(userInfo.tag);
                if (list.size()>0){
                    for (int i=0;i<list.size();i++){
                        tagSet.add(list.get(i).sourceId);
                    }
                    JPushInterface.setTags(context, tagSet, new TagAliasCallback() {
                        @Override
                        public void gotResult(int i, String s, Set<String> set) {

                        }
                    });
                }
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
                UserLoginInfo.loginOverdue(context, errorCode);
            }
        });
    }
}
