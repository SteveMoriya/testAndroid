package com.wehang.ystv.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.wehang.ystv.R;
import com.wehang.ystv.VideoApplication;
import com.wehang.ystv.bo.UserInfo;
import com.wehang.ystv.interfaces.UrlConstant;
import com.wehang.ystv.utils.CommonUtil;
import com.wehang.ystv.utils.UserLoginInfo;
import com.whcd.base.activity.BaseActivity;
import com.whcd.base.interfaces.BaseData;
import com.whcd.base.interfaces.BaseResult;
import com.whcd.base.interfaces.HttpTool;
import com.whcd.base.widget.TopMenuBar;

import java.util.HashMap;
import java.util.Map;

public class WallteActivity extends BaseActivity {
    private TopMenuBar topMenuBar;
    private TextView blanceTv;
    @Override
    protected int getLayoutResId() {
        return R.layout.activity_wallte;
    }

    @Override
    protected void initTitleBar() {
        topMenuBar = (TopMenuBar) findViewById(R.id.topMenuBar);
        topMenuBar.setTitle("我的钱包");
        topMenuBar.getLeftButton().setOnClickListener(this);
    }

    @Override
    protected void initView() {
        blanceTv=findViewById(R.id.blanceTv);
        findViewById(R.id.wallte_add).setOnClickListener(this);
        findViewById(R.id.wallte_watch).setOnClickListener(this);
    }

    @Override
    protected void initData(Bundle bundle) {
        blanceTv.setText(UserLoginInfo.getUserInfo().balance/100+"");
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (CommonUtil.isFirstClick()){
            return;
        }
        switch (v.getId()){

            case R.id.title_btn_left:
                back();
                break;
            case R.id.wallte_add:
                startActivityForResult(new Intent(WallteActivity.this,RechargeActivity.class),1);
                break;
            case R.id.wallte_watch:
                startActivity(new Intent(WallteActivity.this,TransactionRecordActivity.class));
                break;
            default:break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode==1){
            initData();
        }
    }
    private void initData() {
        if (UserLoginInfo.isLogin(this)) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("token", UserLoginInfo.getUserToken());
            HttpTool.doPost(context, UrlConstant.MYDATA, params, false, new TypeToken<BaseResult<UserInfo>>() {
            }.getType(), new HttpTool.OnResponseListener() {
                @Override
                public void onSuccess(BaseData data) {
                    UserInfo info = (UserInfo) data;
                    if (info != null) {
                        UserLoginInfo.saveUserInfo(VideoApplication.applicationContext.getApplicationContext(), info);
                        blanceTv.setText(UserLoginInfo.getUserInfo().balance/100+"");
                    }
                }

                @Override
                public void onError(int errorCode) {
                    UserLoginInfo.loginOverdue(context, errorCode);
                }
            });
        }
    }
}
