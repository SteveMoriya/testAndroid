package com.wehang.ystv.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.blankj.utilcode.util.LogUtils;
import com.google.gson.reflect.TypeToken;
import com.pingplusplus.android.Pingpp;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.wehang.ystv.Constant;
import com.wehang.ystv.R;
import com.wehang.ystv.bo.AliPayResult;
import com.wehang.ystv.bo.Order;
import com.wehang.ystv.bo.UserInfo;
import com.wehang.ystv.bo.WXPay;
import com.wehang.ystv.interfaces.UrlConstant;
import com.wehang.ystv.utils.ToastUtil;
import com.wehang.ystv.utils.UserLoginInfo;
import com.wehang.ystv.widget.PayDialog;
import com.whcd.base.activity.BaseActivity;
import com.whcd.base.interfaces.BaseData;
import com.whcd.base.interfaces.BaseResult;
import com.whcd.base.interfaces.HttpTool;
import com.whcd.base.widget.CustomProgressDialog;
import com.whcd.base.widget.TopMenuBar;

import java.util.HashMap;
import java.util.Map;

public class RechargeActivity extends BaseActivity {
    private TopMenuBar topMenuBar;
    private LinearLayout l_pay1,l_pay2,l_pay3;

    private ImageView pay1,pay2,pay3;
    private CheckedTextView checkbox_1,checkbox_2,checkbox_3,checkbox_4;
    private EditText payET;
    @Override
    protected int getLayoutResId() {
        return R.layout.activity_recharge;
    }

    @Override
    protected void initTitleBar() {
        topMenuBar = (TopMenuBar) findViewById(R.id.topMenuBar);
        topMenuBar.setTitle("余额充值");
        topMenuBar.getLeftButton().setOnClickListener(this);
    }

    @Override
    protected void initView() {
        api = WXAPIFactory.createWXAPI(this, Constant.WEIXIN_APP_ID);

        l_pay2=findViewById(R.id.l_pay2);
        l_pay3=findViewById(R.id.l_pay3);


        l_pay2.setOnClickListener(this);
        l_pay3.setOnClickListener(this);


        pay2=findViewById(R.id.pay2);
        pay3=findViewById(R.id.pay3);
        checkbox_1=findViewById(R.id.checkbox_1);
        checkbox_2=findViewById(R.id.checkbox_2);
        checkbox_3=findViewById(R.id.checkbox_3);
        checkbox_4=findViewById(R.id.checkbox_4);
        checkbox_1.setOnClickListener(this);
        checkbox_2.setOnClickListener(this);
        checkbox_3.setOnClickListener(this);
        checkbox_4.setOnClickListener(this);
        //默认充值100
        //checkbox_1.setChecked(true);

        payET=findViewById(R.id.payET);
        payET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String s=charSequence.toString();
                if (s.toString().contains(" ")) {
                    String[] str = s.toString().split(" ");
                    String str1 = "";
                    for (int b = 0; b < str.length; b++) {
                        str1 += str[b];
                    }
                    payET.setText(str1);

                    payET.setSelection(i);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {
              /*  if (editable.toString().length()>0){
                    setCheckbox(0);
                    payMuch= Integer.parseInt(editable.toString().trim());
                }else {
                    setCheckbox(1);
                }*/
                if (editable.toString().length()>0){
                    payMuch= Integer.parseInt(editable.toString().trim());
                }else {
                    // setCheckbox(1);
                }
            }
        });
        findViewById(R.id.toPay).setOnClickListener(this);
        setCheckbox(0);
    }
    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.title_btn_left:
                if (Constant.HavePAY.equals("SUCCESS")){
                    Constant.HavePAY="";
                    setResult(RESULT_OK);
                    finish();
                }else {back();}
            case R.id.l_pay2:
                chargeType=1;
                setImgSrc(2);
                break;
            case R.id.l_pay3:
                chargeType=2;
                setImgSrc(3);
                break;
            case R.id.checkbox_1:
                setCheckbox(1);
                payET.setText("100");
                payET.setSelection(3);
                break;
            case R.id.checkbox_2:
                setCheckbox(2);
                payET.setText("200");
                payET.setSelection(3);
                break;
            case R.id.checkbox_3:
                setCheckbox(3);
                payET.setText("500");
                payET.setSelection(3);
                break;
            case R.id.checkbox_4:
                setCheckbox(4);
                payET.setText("1000");
                payET.setSelection(4);
                break;
            case R.id.toPay:
                if (TextUtils.isEmpty(payET.getText().toString().trim())){
                    ToastUtil.makeText(context,"请输入充值金额",Toast.LENGTH_SHORT).show();
                    return;
                }
                int a= Integer.parseInt(payET.getText().toString().trim());
                payMuch=a;
                if (payMuch==0){
                    ToastUtil.makeText(context,"请输入大于0的金额",Toast.LENGTH_SHORT).show();
                    return;
                }

                payAction(payMuch);
        }
    }
    private int payMuch=0;
    private void setCheckbox(int what){
        checkbox_1.setChecked(false);
        checkbox_2.setChecked(false);
        checkbox_3.setChecked(false);
        checkbox_4.setChecked(false);
        if (what==1){
            payMuch=100;
            checkbox_1.setChecked(true);
        }else if (what==2){
            payMuch=200;
            checkbox_2.setChecked(true);
        }else if (what==3){
            payMuch=400;
            checkbox_3.setChecked(true);
        }else if (what==4){
            payMuch=1000;
            checkbox_4.setChecked(true);
        }
    }
    private void setImgSrc(int what){

        pay2.setImageDrawable(ContextCompat.getDrawable(RechargeActivity.this,R.drawable.yuan_quan));
        pay3.setImageDrawable(ContextCompat.getDrawable(RechargeActivity.this,R.drawable.yuan_quan));
        if (what==2){
            pay2.setImageDrawable(ContextCompat.getDrawable(RechargeActivity.this,R.drawable.yuan_gou));
        }else if (what==3){
            pay3.setImageDrawable(ContextCompat.getDrawable(RechargeActivity.this,R.drawable.yuan_gou));
        }

    }

    @Override
    protected void initData(Bundle bundle) {

    }

    /**
     * 【 http://218.244.151.190/demo/charge 】是 ping++ 为了方便开发者体验 sdk 而提供的一个临时 url
     * 。 该 url 仅能调用【模拟支付控件】，开发者需要改为自己服务端的 url 。
     */
    private static String YOUR_URL = "http://218.244.151.190/demo/charge";
    public static final String URL = YOUR_URL;
    private int chargeType = 1;
    private boolean isOnPaying = false;
    private IWXAPI api;

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        isOnPaying = false;
        if (requestCode == Pingpp.REQUEST_CODE_PAYMENT) {
            if (resultCode == RESULT_OK) {
                String result = data.getExtras().getString("wechat_pay_result");
                if (result.equals("success")) {
                    ToastUtil.makeText(context,"充值成功", Toast.LENGTH_SHORT).show();
                    Constant.HavePAY="SUCCESS";
                } else if (result.equals("fail")) {
                    ToastUtil.makeText(context,"充值失败", Toast.LENGTH_SHORT).show();
                } else if (result.equals("cancel")) {
                    ToastUtil.makeText(context,"充值取消", Toast.LENGTH_SHORT).show();
                } else if (result.equals("invalid")) {
                    ToastUtil.makeText(context,"未安装支付插件", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    /**
     * 微信支付
     *
     * @param wxPay
     */
    private void wxPay(WXPay wxPay) {
        PayReq req = new PayReq();
        req.appId = Constant.WEIXIN_APP_ID;
        req.partnerId = wxPay.wxCharge.partnerid;
        req.prepayId = wxPay.wxCharge.prepayid;
        req.nonceStr = wxPay.wxCharge.noncestr;
        req.timeStamp = wxPay.wxCharge.timestamp;
        req.packageValue = "Sign=WXPay";
        req.sign = wxPay.wxCharge.sign;
//        req.partnerId = "1425772802";
//        req.prepayId = "wx20161221180513a04c9b73820193202216";
//        req.nonceStr = "fa148d80ddf41b9a8d4075f8bd35b9d4";
//        req.timeStamp = "1482314713";
//        req.packageValue = "Sign=WXPay";
//        req.sign = "E0CB6B354FADECE394B3A2E996032DA7";

        // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信

        api.sendReq(req);
    }

    private void aliPay(final String orderInfo) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                PayTask alipay = new PayTask(RechargeActivity.this);
//				Map<String, String> result = alipay.payV2("app_id=2016121404279642&biz_content=%7B%22total_amount%22%3A%2211%22%2C%22body%22%3A%22%E8%AE%A2%E5%8D%95%22%2C%22timeout_express%22%3A%2230m%22%2C%22subject%22%3A%22111%22%2C%22product_code%22%3A%22QUICK_MSECURITY_PAY%22%2C%22seller_id%22%3A%222088411208154941%22%2C%22out_trade_no%22%3A%22111%22%7D&charset=utf-8&format=json&method=alipay.trade.app.pay¬ify_url=http%3A%2F%2Fwww.wehang.net%2FSP%2Fappservice%2Fpay%2FnotifyUrl×tamp=2016-12-22+16%3A19%3A17&version=1.0&sign=jarMPlpPYiYIyHmrm715kDl7%2FNrlTvYh11obVYM8tGDnCHgoHM3QHZUHnvG6uVBMjcfAJI6CTBNIXcweOZEay3AcVuQVCgqqDPn8FD9w3zPa1HlbpzPZS4vhvdUM%2FrUkf8UNEBDCoTEmmysYBI4q2RJEmIp%2F83EoZhrnr6ytT9I%3D&sign_type=RSA", true);
                Map<String, String> result = alipay.payV2(orderInfo, true);

                Message msg = new Message();
                msg.what = 0;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        }).start();
    }


    public void payAction(int howmach) {
        String token = UserLoginInfo.getUserToken();
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", token);
        params.put("money",howmach*100+"");
        params.put("method", chargeType + "");
        dialog = CustomProgressDialog.show(context, "加载中...", true, null);
        HttpTool.doPost(context, UrlConstant.RECHARGE, params, true, new TypeToken<BaseResult<WXPay>>() {
        }.getType(), new HttpTool.OnResponseListener() {
            @Override
            public void onSuccess(BaseData data) {
                dialog.dismiss();
                WXPay wxPay = (WXPay) data;
                if (wxPay.wxCharge != null) {
                    LogUtils.i("WXPAY",wxPay.wxCharge.toString());
                    wxPay(wxPay);
                } else if (!TextUtils.isEmpty(wxPay.zfbCharge)) {
                    LogUtils.i("WXPAY",wxPay.zfbCharge.toString());
                    aliPay(wxPay.zfbCharge);
                }
            }

            @Override
            public void onError(int errorCode) {
                dialog.dismiss();
                UserLoginInfo.loginOverdue(context, errorCode);
            }
        });
    }
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0: {
                    @SuppressWarnings("unchecked")
                    AliPayResult payResult = new AliPayResult((Map<String, String>) msg.obj);
                    /**
                     对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    String memo=payResult.getMemo();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        LogUtils.i("caonim","!!");
                        //ToastUtil.makeText(context,"支付成功", Toast.LENGTH_SHORT).show();
                        PayDialog payDialog=new PayDialog(context,"支付成功");
                        payDialog.showDialog();
                        Constant.HavePAY="SUCCESS";
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        LogUtils.i("caonim","!!");
                        PayDialog payDialog=new PayDialog(context,memo);
                        payDialog.showDialog();
                    }
                    break;
                }
            }
        }

        ;
    };

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if (Constant.HavePAY.equals("SUCCESS")){
            Constant.HavePAY="";
            setResult(RESULT_OK);
            finish();
        }else {back();}
    }

}
