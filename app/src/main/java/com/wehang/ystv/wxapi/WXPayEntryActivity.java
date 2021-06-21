package com.wehang.ystv.wxapi;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;

import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.wehang.ystv.Constant;
import com.wehang.ystv.R;
import com.wehang.ystv.bo.WXPay;
import com.whcd.base.widget.TopMenuBar;

import java.util.LinkedHashSet;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;


public class WXPayEntryActivity extends FragmentActivity implements IWXAPIEventHandler, View.OnClickListener {

    private static final String TAG = "WXPayEntryActivity";
    private IWXAPI api;
    private TextView payResultTv;
    private TopMenuBar topMenuBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wechat_pay_result);
        topMenuBar = (TopMenuBar) findViewById(R.id.topMenuBar);
        topMenuBar.setTitle("支付结果");
        payResultTv = (TextView) findViewById(R.id.payResultTv);
        topMenuBar.getLeftButton().setOnClickListener(this);
        topMenuBar.hideRightButton();
        api = WXAPIFactory.createWXAPI(this, Constant.WEIXIN_APP_ID);
        api.handleIntent(getIntent(), this);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
    }

    @Override
    public void onResp(BaseResp resp) {
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            switch (resp.errCode) {
                case 0:
                    payResultTv.setText("支付成功");
                    Constant.HavePAY="SUCCESS";
                    Set<String> tagSet = new LinkedHashSet<String>();
                    tagSet.add(Constant.TAGWXPAY);
                    JPushInterface.setTags(WXPayEntryActivity.this, tagSet, new TagAliasCallback() {
                        @Override
                        public void gotResult(int i, String s, Set<String> set) {

                        }
                    });
                    break;
                case -1:
                    payResultTv.setText("支付失败");
                    break;
                case -2:
                    payResultTv.setText("取消支付");
                    break;
            }
        }
        handler.sendMessageDelayed(Message.obtain(),2000);
    }
    private Handler handler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            finish();
            return false;
        }
    });
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_btn_left:
                finish();
                break;
        }
    }
}