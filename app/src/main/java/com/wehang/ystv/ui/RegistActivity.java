package com.wehang.ystv.ui;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.LogUtils;
import com.google.gson.reflect.TypeToken;
import com.wehang.ystv.R;
import com.wehang.ystv.bo.UserInfo;
import com.wehang.ystv.interfaces.UrlConstant;
import com.wehang.ystv.utils.CommonUtil;
import com.wehang.ystv.utils.ToastUtil;
import com.wehang.ystv.utils.UserLoginInfo;
import com.wehang.ystv.utils.Validator;
import com.whcd.base.activity.BaseActivity;
import com.whcd.base.interfaces.BaseData;
import com.whcd.base.interfaces.BaseResult;
import com.whcd.base.interfaces.HttpTool;
import com.whcd.base.widget.CustomProgressDialog;

import java.util.HashMap;
import java.util.Map;

public class RegistActivity extends BaseActivity {
    private EditText phoneNm,codeNm,passwordNm,passwordNmAg;
    TextView codeTv;
    private String codeNum;
    private String phoneNum,passNum,passNumAg;
    private int leftTime = 0;// 倒计时剩余时间(秒)

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            return false;
        }
    });


    @Override
    protected int getLayoutResId() {
        return R.layout.activity_regist;
    }

    @Override
    protected void initTitleBar() {

    }

    @Override
    protected void initView() {
        codeTv=findViewById(R.id.regist_yzmTv);
        codeTv.setOnClickListener(this);
        findViewById(R.id.regist_confirmTv).setOnClickListener(this);
        phoneNm=findViewById(R.id.login_phone);
        codeNm=findViewById(R.id.regist_yzET);
        passwordNm=findViewById(R.id.login_password);
        passwordNmAg=findViewById(R.id.login_password_ag);
        findViewById(R.id.detalis).setOnClickListener(this);

    }

    @Override
    protected void initData(Bundle bundle) {

    }
    private boolean judgeEdit(int i) {
        if (TextUtils.isEmpty(phoneNm.getText().toString().trim())) {
            ToastUtil.makeText(context, "请输入手机号码", Toast.LENGTH_SHORT).show();
            phoneNm.requestFocus();
            return true;
        }
        if (!Validator.isMobile(phoneNm.getText().toString().trim())) {
            phoneNm.requestFocus();
            ToastUtil.makeText(context, "手机号格式错误", Toast.LENGTH_SHORT).show();
            return true;
        }

        if (TextUtils.isEmpty(codeNm.getText().toString().trim())){
                ToastUtil.makeText(context, "请输入验证码", Toast.LENGTH_SHORT).show();
            codeNm.requestFocus();
                return true;
        }
        if (TextUtils.isEmpty(passwordNm.getText().toString().trim())){
            ToastUtil.makeText(context, "请输入密码", Toast.LENGTH_SHORT).show();
            passwordNm.requestFocus();
            return true;
        }
        if (!Validator.isPassword(passwordNm.getText().toString().trim())){
            ToastUtil.makeText(context, "密码格式不对", Toast.LENGTH_SHORT).show();
            passwordNm.requestFocus();
            return true;
        }
        if (TextUtils.isEmpty(passwordNmAg.getText().toString().trim())){
            ToastUtil.makeText(context, "请再次输入密码", Toast.LENGTH_SHORT).show();
            passwordNmAg.requestFocus();
            return true;
        }
        if (!passwordNm.getText().toString().trim().equals(passwordNmAg.getText().toString().trim())){
            ToastUtil.makeText(context, "2次输入密码不同", Toast.LENGTH_SHORT).show();
            passwordNmAg.requestFocus();
            return true;
        }
        return false;
    }
    @Override
    public void onClick(View v) {
        super.onClick(v);
        // 避免连续点击
        if (CommonUtil.isFirstClick())
        {
            return;
        }
        Map<String, String> params;
        switch (v.getId()){
            case R.id.regist_confirmTv:
                if (judgeEdit(0)){
                    return;
                }
                phoneNum = phoneNm.getText().toString().trim();
                codeNum=codeNm.getText().toString().trim();
                passNum=passwordNm.getText().toString().trim();
                passNumAg=passwordNmAg.getText().toString().trim();
                params = new HashMap<String, String>();
                params.put("phone", phoneNum);
                params.put("code", codeNum);
                dialog = CustomProgressDialog.show(context, "加载中...", true, null);
                HttpTool.doPost(this, UrlConstant.YZVERIFY, params, true, new TypeToken<BaseResult<BaseData>>() {
                }.getType(), new HttpTool.OnResponseListener() {
                    @Override
                    public void onSuccess(BaseData data) {
                        dialogDismiss();
                        LogUtils.i("info","yes");
                        Intent intent=new Intent(new Intent(RegistActivity.this,PerfectDataActivity.class));
                        Bundle bundle=new Bundle();
                        UserInfo userInfo=new UserInfo();
                        userInfo.phone=phoneNum;
                        userInfo.code=codeNum;
                        userInfo.password=passNum;
                        bundle.putSerializable("userInfo",userInfo);
                        intent.putExtra("bundle",bundle);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onError(int errorCode) {
                        LogUtils.i("info","no");
                        dialogDismiss();
                    }
                });

                break;
            case R.id.regist_yzmTv:
                LogUtils.i("info","no");
                if (TextUtils.isEmpty(phoneNm.getText().toString().trim())) {
                    ToastUtil.makeText(context, "请输入手机号码", Toast.LENGTH_SHORT).show();
                    phoneNm.requestFocus();
                    return;
                }
                if (!Validator.isMobile(phoneNm.getText().toString().trim())) {
                    phoneNm.requestFocus();
                    ToastUtil.makeText(context, "手机号格式错误", Toast.LENGTH_SHORT).show();
                    return;
                }
                phoneNum = phoneNm.getText().toString().trim();
                codeTv.setEnabled(false);
                leftTime = 60;
                codeTv.setText(String.valueOf(leftTime) + "秒");
                handler.postDelayed(updateTimeRunnable, 1000);
                params = new HashMap<String, String>();
                params.put("phone", phoneNum);
                params.put("type", 0+"");
               // dialog = CustomProgressDialog.show(context, "加载中...", true, null);
                HttpTool.doPost(this, UrlConstant.VERIFY, params, true, new TypeToken<BaseResult<BaseData>>() {
                }.getType(), new HttpTool.OnResponseListener() {
                    @Override
                    public void onSuccess(BaseData data) {
                       // dialogDismiss();
                        LogUtils.i("info","yes");
                        ToastUtil.makeText(context,getResources().getString(R.string.seng_code),Toast.LENGTH_SHORT).show();
                        //leftTime=0;
                    }

                    @Override
                    public void onError(int errorCode) {
                        //LogUtils.i("info","no");
                        //codeTv.setText("获取验证码");
                        //leftTime=0;
                        leftTime=0;
                        dialogDismiss();
                    }
                });
                break;
            case R.id.detalis:
                startActivity(new Intent(context,AboutDetailActivity.class));
        }
    }

    /**
     * 验证码获取倒计时线程
     */
    private Runnable updateTimeRunnable = new Runnable() {
        @Override
        public void run() {
            leftTime--;
            if (leftTime > 0) {
                codeTv.setText(String.valueOf(leftTime) + "秒");
                handler.postDelayed(this, 1000);
            } else {
                codeTv.setEnabled(true);
                codeTv.setText("获取验证码");
            }
        }
    };
}
