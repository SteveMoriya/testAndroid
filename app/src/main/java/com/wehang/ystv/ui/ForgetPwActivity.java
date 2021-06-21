package com.wehang.ystv.ui;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.whcd.base.widget.TopMenuBar;

import java.util.HashMap;
import java.util.Map;

public class ForgetPwActivity extends BaseActivity {
    //更改密码
    private TopMenuBar topMenuBar;
    private EditText phoneNm,codeNm,passwordNm;
    TextView codeTv;
    private String codeNum;
    private String phoneNum,passNum;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            return false;
        }
    });
    @Override
    protected int getLayoutResId() {
        return R.layout.activity_forget_pw;
    }

    @Override
    protected void initTitleBar() {
        topMenuBar = (TopMenuBar) findViewById(R.id.topMenuBar);
        topMenuBar.setTitle("更改密码");
        topMenuBar.getLeftButton().setOnClickListener(this);
    }

    @Override
    protected void initView() {
        findViewById(R.id.regist_confirmTv).setOnClickListener(this);
        phoneNm=findViewById(R.id.login_phone);
        codeNm=findViewById(R.id.regist_yzET);
        passwordNm=findViewById(R.id.login_password);
    }

    @Override
    protected void initData(Bundle bundle) {

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
        switch (v.getId()) {
            case R.id.regist_confirmTv:
                if (judgeEdit(0)) {
                    return;
                }
                phoneNum = phoneNm.getText().toString().trim();
                codeNum = codeNm.getText().toString().trim();
                passNum = passwordNm.getText().toString().trim();
                params = new HashMap<String, String>();
                params.put("token", UserLoginInfo.getUserToken());
                params.put("oldPwd", phoneNum);
                params.put("newPwd", passNum);
                dialog = CustomProgressDialog.show(context, "加载中...", true, null);
                HttpTool.doPost(this, UrlConstant.EDITPASSWORD, params, true, new TypeToken<BaseResult<BaseData>>() {
                }.getType(), new HttpTool.OnResponseListener() {
                    @Override
                    public void onSuccess(BaseData data) {
                        dialogDismiss();
                        LogUtils.i("info", "yes");
                        UserInfo info=UserLoginInfo.getUserInfo();
                        info.password=passNum;
                        UserLoginInfo.saveUserInfo(context,info);
                        finish();
                    }

                    @Override
                    public void onError(int errorCode) {
                        LogUtils.i("info", "no");
                        dialogDismiss();
                    }
                });

                break;
        }
    }
    private boolean judgeEdit(int i) {
        if (TextUtils.isEmpty(phoneNm.getText().toString().trim())) {
            ToastUtil.makeText(context, "请输入密码", Toast.LENGTH_SHORT).show();
            phoneNm.requestFocus();
            return true;
        }
        if (!Validator.isPassword(phoneNm.getText().toString().trim())) {
            phoneNm.requestFocus();
            ToastUtil.makeText(context, "密码格式错误", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (TextUtils.isEmpty(codeNm.getText().toString().trim())) {
            ToastUtil.makeText(context, "请输入6-16位新密码", Toast.LENGTH_SHORT).show();
            codeNm.requestFocus();
            return true;
        }
        if (!Validator.isPassword(codeNm.getText().toString().trim())){
            ToastUtil.makeText(context, "密码格式不对", Toast.LENGTH_SHORT).show();
            codeNm.requestFocus();
            return true;
        }
        if (TextUtils.isEmpty(passwordNm.getText().toString().trim())) {
            ToastUtil.makeText(context, "请再次输入新密码", Toast.LENGTH_SHORT).show();
            passwordNm.requestFocus();
            return true;
        }
        if (!passwordNm.getText().toString().trim().equals(codeNm.getText().toString().trim())){
            ToastUtil.makeText(context, "2次输入密码不同", Toast.LENGTH_SHORT).show();
            passwordNm.requestFocus();
            return true;
        }
        return false;
    }
}
