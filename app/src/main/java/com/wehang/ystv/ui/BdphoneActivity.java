package com.wehang.ystv.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

import butterknife.Bind;
import butterknife.ButterKnife;

public class BdphoneActivity extends BaseActivity {
    @Bind(R.id.bd_phoneEt)
    EditText mRegistPhoneEt;
    @Bind(R.id.bd_yzET)
    EditText mRegistYzET;
    @Bind(R.id.bd_yzmTv)
    TextView codeTv;
    @Bind(R.id.bd_confirmTv)
    TextView done;
    private String codeNum;
    private String phoneNum;
    private TopMenuBar topMenuBar;
    private int type;// 0--登录 1--绑定手机号
    private int leftTime = 0;// 倒计时剩余时间(秒)
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            return false;
        }
    });

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_bdphone;
    }

    @Override
    protected void initTitleBar() {
        topMenuBar = (TopMenuBar) findViewById(R.id.topMenuBar);
        topMenuBar.setTitle("手机绑定");
        topMenuBar.getLeftButton().setOnClickListener(this);
    }

    @Override
    protected void initView() {
        ButterKnife.bind(this);
        findViewById(R.id.topMenuBar).setOnClickListener(this);
        findViewById(R.id.bd_confirmTv).setOnClickListener(this);
        findViewById(R.id.bd_yzmTv).setOnClickListener(this);
        codeTv.setOnClickListener(this);
        done.setOnClickListener(this);
    }

    @Override
    protected void initData(Bundle bundle) {
        userInfo = UserLoginInfo.getUserInfo(this);
        if (userInfo == null) {
            return;
        }

    }
    @Override
    public void onClick(View v) {
        // 避免连续点击
        if (CommonUtil.isFirstClick())
            return;
        Map<String, String> params;
        switch (v.getId()) {
            case R.id.title_btn_left:
                back();
                break;
            case R.id.bd_yzmTv:
                phoneNum = mRegistPhoneEt.getText().toString().trim();
                if (judgeEdit(0)) return;
                codeTv.setEnabled(false);
                leftTime = 60;
                codeTv.setText(String.valueOf(leftTime) + "秒");
                handler.postDelayed(updateTimeRunnable, 1000);
                params = new HashMap<String, String>();
                params.put("phone", phoneNum);
                params.put("type", "0");
                dialog = CustomProgressDialog.show(context, "加载中...", true, null);
                HttpTool.doPost(this, UrlConstant.VERIFY, params, true, new TypeToken<BaseResult<BaseData>>() {
                }.getType(), new HttpTool.OnResponseListener() {
                    @Override
                    public void onSuccess(BaseData data) {
                        dialogDismiss();
                    }

                    @Override
                    public void onError(int errorCode) {
                        dialogDismiss();
                        UserLoginInfo.loginOverdue(context, errorCode);
                    }
                });
                break;

            case R.id.bd_confirmTv:
                if (!judgeEdit(1)) {
                    yzYzm();
                }
               /* Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                BdphoneActivity.this.finish();*/
                break;
            default:
                break;
        }
    }
    private UserInfo userInfo;
    private void yzYzm(){
        phoneNum=mRegistPhoneEt.getText().toString().trim();
        codeNum=mRegistYzET.getText().toString().trim();
        Map<String, String> params;
        params = new HashMap<String, String>();
        params.put("phone", phoneNum);
        params.put("code", codeNum);
        dialog = CustomProgressDialog.show(context, "加载中...", true, null);
        HttpTool.doPost(this, UrlConstant.YZVERIFY, params, true, new TypeToken<BaseResult<BaseData>>() {
        }.getType(), new HttpTool.OnResponseListener() {
            @Override
            public void onSuccess(BaseData data) {
                dialogDismiss();
                bdsji();
            }

            @Override
            public void onError(int errorCode) {
                dialogDismiss();

            }
        });
    }
    private void bdsji() {
        int type=0;
        if (userInfo.phone==null){

        }else {
            type=1;
        }
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", userInfo.token);
        params.put("phone",phoneNum);
        params.put("code",codeNum );
        params.put("type",type+"" );
        HttpTool.doPost(this, UrlConstant.BINDPHONE, params, true, new TypeToken<BaseResult<BaseData>>() {
        }.getType(), new HttpTool.OnResponseListener() {
            @Override
            public void onSuccess(BaseData data) {
                LogUtils.d("change","");
                userInfo.phone=phoneNum;
                UserLoginInfo.saveUserInfo(BdphoneActivity.this, userInfo);
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                BdphoneActivity.this.finish();
            }

            @Override
            public void onError(int errorCode) {
                UserLoginInfo.loginOverdue(context, errorCode);
            }
        });

    }

    private boolean judgeEdit(int i) {
        if (TextUtils.isEmpty(mRegistPhoneEt.getText().toString().trim())) {
            ToastUtil.makeText(context, "请输入手机号码", Toast.LENGTH_SHORT).show();
            mRegistPhoneEt.requestFocus();
            return true;
        }
        if (!Validator.isMobile(mRegistPhoneEt.getText().toString().trim())) {
            ToastUtil.makeText(context, "请输入正确的手机号码", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (i ==1){
            if (TextUtils.isEmpty(mRegistYzET.getText().toString().trim())){
                ToastUtil.makeText(context, "请输入验证码", Toast.LENGTH_SHORT).show();
                return true;
            }}
        return false;
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
