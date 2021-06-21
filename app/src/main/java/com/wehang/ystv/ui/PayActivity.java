package com.wehang.ystv.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import com.wehang.ystv.bo.Lives;
import com.wehang.ystv.bo.Order;
import com.wehang.ystv.bo.UserInfo;
import com.wehang.ystv.bo.WXPay;
import com.wehang.ystv.interfaces.UrlConstant;
import com.wehang.ystv.utils.CommonUtil;
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
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

public class PayActivity extends BaseActivity {
    private TopMenuBar topMenuBar;
    //本地支付，支付宝，微信
    private LinearLayout l_pay1,l_pay2,l_pay3;

    private ImageView pay1,pay2,pay3;
    private Lives lives;
    private TextView pay_howmuch,have_howmuch;
    @Override
    protected int getLayoutResId() {
        return R.layout.activity_pay;
    }

    @Override
    protected void initTitleBar() {
        topMenuBar = (TopMenuBar) findViewById(R.id.topMenuBar);
        topMenuBar.setTitle("选择支付方式");
        topMenuBar.getLeftButton().setOnClickListener(this);
    }

    @Override
    protected void initView() {

        api = WXAPIFactory.createWXAPI(this, Constant.WEIXIN_APP_ID);
        Bundle bundle=getIntent().getBundleExtra("bundle");
        lives = (Lives) bundle.getSerializable("data");
        l_pay1=findViewById(R.id.l_pay1);
        l_pay2=findViewById(R.id.l_pay2);
        l_pay3=findViewById(R.id.l_pay3);

        l_pay1.setOnClickListener(this);
        l_pay2.setOnClickListener(this);
        l_pay3.setOnClickListener(this);

        pay1=findViewById(R.id.pay1);
        pay2=findViewById(R.id.pay2);
        pay3=findViewById(R.id.pay3);
        pay_howmuch=findViewById(R.id.pay_howmuch);
        have_howmuch=findViewById(R.id.have_howmuch);
        findViewById(R.id.toPay).setOnClickListener(this);

    }

    @Override
    protected void initData(Bundle bundle) {
        pay_howmuch.setText(lives.price/100+"元");
        have_howmuch.setText(UserLoginInfo.getUserInfo().balance/100+"元");
    }



    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (CommonUtil.isFirstClick()){
            return;
        }
        switch (v.getId()){
            case R.id.title_btn_left:
                if (Constant.HavePAY.equals("SUCCESS")){
                    Constant.HavePAY="";
                    setResult(RESULT_OK);
                    finish();
                }else {back();}

            case R.id.l_pay1:

                setImgSrc(1);
                chargeType=0;
                break;
            case R.id.l_pay2:

                chargeType=1;
                setImgSrc(2);
                break;
            case R.id.l_pay3:

                setImgSrc(3);
                chargeType=2;
                break;
            case R.id.toPay:
                creatOrder();
        }
    }
    private void setImgSrc(int what){
        pay1.setImageDrawable(ContextCompat.getDrawable(PayActivity.this,R.drawable.yuan_quan));
        pay2.setImageDrawable(ContextCompat.getDrawable(PayActivity.this,R.drawable.yuan_quan));
        pay3.setImageDrawable(ContextCompat.getDrawable(PayActivity.this,R.drawable.yuan_quan));
        if (what==1){
            pay1.setImageDrawable(ContextCompat.getDrawable(PayActivity.this,R.drawable.yuan_gou));
        }else if (what==2){
            pay2.setImageDrawable(ContextCompat.getDrawable(PayActivity.this,R.drawable.yuan_gou));
        }else if (what==3){
            pay3.setImageDrawable(ContextCompat.getDrawable(PayActivity.this,R.drawable.yuan_gou));
        }

    }




    /**
     * 【 http://218.244.151.190/demo/charge 】是 ping++ 为了方便开发者体验 sdk 而提供的一个临时 url
     * 。 该 url 仅能调用【模拟支付控件】，开发者需要改为自己服务端的 url 。
     */
    private static String YOUR_URL = "http://218.244.151.190/demo/charge";
    public static final String URL = YOUR_URL;
    private int chargeType = 0;
    private boolean isOnPaying = false;
    private IWXAPI api;

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        isOnPaying = false;
        if (requestCode == Pingpp.REQUEST_CODE_PAYMENT) {
            if (resultCode == RESULT_OK) {
                String result = data.getExtras().getString("wechat_pay_result");
                if (result.equals("success")) {
                    ToastUtil.makeText(context,"充值成功", Toast.LENGTH_SHORT).show();
                    //initData(null);
                    //lives.isBuy=1;
                    Constant.HavePAY="SUCCESS";
                    Set<String> tagSet = new LinkedHashSet<String>();
                    tagSet.add(lives.sourceId);
                    JPushInterface.setTags(context, tagSet, new TagAliasCallback() {
                        @Override
                        public void gotResult(int i, String s, Set<String> set) {

                        }
                    });
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
                PayTask alipay = new PayTask(PayActivity.this);
//				Map<String, String> result = alipay.payV2("app_id=2016121404279642&biz_content=%7B%22total_amount%22%3A%2211%22%2C%22body%22%3A%22%E8%AE%A2%E5%8D%95%22%2C%22timeout_express%22%3A%2230m%22%2C%22subject%22%3A%22111%22%2C%22product_code%22%3A%22QUICK_MSECURITY_PAY%22%2C%22seller_id%22%3A%222088411208154941%22%2C%22out_trade_no%22%3A%22111%22%7D&charset=utf-8&format=json&method=alipay.trade.app.pay¬ify_url=http%3A%2F%2Fwww.wehang.net%2FSP%2Fappservice%2Fpay%2FnotifyUrl×tamp=2016-12-22+16%3A19%3A17&version=1.0&sign=jarMPlpPYiYIyHmrm715kDl7%2FNrlTvYh11obVYM8tGDnCHgoHM3QHZUHnvG6uVBMjcfAJI6CTBNIXcweOZEay3AcVuQVCgqqDPn8FD9w3zPa1HlbpzPZS4vhvdUM%2FrUkf8UNEBDCoTEmmysYBI4q2RJEmIp%2F83EoZhrnr6ytT9I%3D&sign_type=RSA", true);
                Map<String, String> result = alipay.payV2(orderInfo, true);

                Message msg = new Message();
                msg.what = 0;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        }).start();
    }
    private void creatOrder(){
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", UserLoginInfo.getUserToken());
        params.put("sourceId", lives.sourceId);
        dialog = CustomProgressDialog.show(context, "加载中...", true, null);
        HttpTool.doPost(context, UrlConstant.BUTLIVE, params, true, new TypeToken<BaseResult<WXPay>>() {
        }.getType(), new HttpTool.OnResponseListener() {

            @Override
            public void onSuccess(BaseData data) {
                dialog.dismiss();
                WXPay wxPay = (WXPay) data;

                Order order=wxPay.order;
                LogUtils.i("WXPAY",order.toString());
                payAction(order);
            }

            @Override
            public void onError(int errorCode) {
                dialog.dismiss();
                UserLoginInfo.loginOverdue(context, errorCode);
            }
        });
    }
    public void payAction(Order order) {
        String token = UserLoginInfo.getUserToken();
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", token);
        params.put("orderId",order.orderId);
        params.put("method", chargeType + "");
        dialog = CustomProgressDialog.show(context, "加载中...", true, null);
        HttpTool.doPost(context, UrlConstant.PAYORDER, params, true, new TypeToken<BaseResult<WXPay>>() {
        }.getType(), new HttpTool.OnResponseListener() {
            @Override
            public void onSuccess(BaseData data) {
                dialog.dismiss();
                WXPay wxPay = (WXPay) data;
                if (wxPay.wxCharge != null) {

                    LogUtils.i("WXPAY",wxPay.wxCharge.toString());
                    Constant.TAGWXPAY=lives.sourceId;
                    wxPay(wxPay);
                } else if (!TextUtils.isEmpty(wxPay.zfbCharge)) {
                    LogUtils.i("WXPAY",wxPay.zfbCharge.toString());
                    aliPay(wxPay.zfbCharge);
                } else {
                    PayDialog payDialog=new PayDialog(context,"支付成功");
                    payDialog.showDialog();
                    Constant.HavePAY="SUCCESS";
                    Set<String> tagSet = new LinkedHashSet<String>();
                    tagSet.add(lives.sourceId);
                    JPushInterface.setTags(context, tagSet, new TagAliasCallback() {
                        @Override
                        public void gotResult(int i, String s, Set<String> set) {

                        }
                    });
                    int totol=UserLoginInfo.getUserInfo().balance;
                    totol -=lives.price;
                    UserInfo userInfo=UserLoginInfo.getUserInfo();
                    userInfo.balance=totol;
                    UserLoginInfo.saveUserInfo(context,userInfo);
                   /* setResult(RESULT_OK);
                    finish();*/
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
                        PayDialog payDialog=new PayDialog(context,"支付成功");
                        payDialog.showDialog();
                        Constant.HavePAY="SUCCESS";
                        Set<String> tagSet = new LinkedHashSet<String>();
                        tagSet.add(lives.sourceId);
                        JPushInterface.setTags(context, tagSet, new TagAliasCallback() {
                            @Override
                            public void gotResult(int i, String s, Set<String> set) {

                            }
                        });
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
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
