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
import android.widget.CheckedTextView;
import android.widget.EditText;
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
import com.wehang.ystv.VideoApplication;
import com.wehang.ystv.bo.AliPayResult;
import com.wehang.ystv.bo.Configure;
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
import java.util.Map;

public class UpgradeVipActivity extends BaseActivity {
    private TopMenuBar topMenuBar;
    //本地支付，支付宝，微信
    private LinearLayout l_pay1,l_pay2,l_pay3;

    private ImageView pay1,pay2,pay3;
    private CheckedTextView checkbox_1,checkbox_2,checkbox_3,checkbox_4;
    private EditText payET;
    private TextView pay_howmuch,have_howmuch;
    private TextView hm_tv1,hm_tv2,hm_tv3,hm_tv4;
    @Override
    protected int getLayoutResId() {
        return R.layout.activity_upgrade_vip;
    }

    @Override
    protected void initTitleBar() {
        topMenuBar = (TopMenuBar) findViewById(R.id.topMenuBar);
        topMenuBar.setTitle("升级会员");
        topMenuBar.getLeftButton().setOnClickListener(this);
    }

    @Override
    protected void initView() {
        api = WXAPIFactory.createWXAPI(this, Constant.WEIXIN_APP_ID);

        hm_tv1=findViewById(R.id.hm_tv1);
        hm_tv2=findViewById(R.id.hm_tv2);
        hm_tv3=findViewById(R.id.hm_tv3);
        hm_tv4=findViewById(R.id.hm_tv4);

        l_pay1=findViewById(R.id.l_pay1);
        l_pay2=findViewById(R.id.l_pay2);
        l_pay3=findViewById(R.id.l_pay3);

        l_pay1.setOnClickListener(this);
        l_pay2.setOnClickListener(this);
        l_pay3.setOnClickListener(this);

        pay1=findViewById(R.id.pay1);
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
        checkbox_1.setChecked(true);

        payET=findViewById(R.id.payET);
        payET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String s=charSequence.toString();
                if (s.length()==2){
                    int c= Integer.parseInt(s);
                    if (c>12||c<1){
                        ToastUtil.makeText(context,"请输入1-12的数字",Toast.LENGTH_SHORT).show();
                        String s1=s.substring(0,1);
                        payET.setText(s1);

                        payET.setSelection(1);
                        return;
                    }
                }

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
                if (editable.toString().length()>0){
                    payMuch= Integer.parseInt(editable.toString().trim());
                }else {
                   // setCheckbox(1);
                }
            }
        });
        have_howmuch=findViewById(R.id.have_howmuch);
        findViewById(R.id.toPay).setOnClickListener(this);
        setCheckbox(0);
    }
    private int payMuch=0;
    private void setCheckbox(int what){
        checkbox_1.setChecked(false);
        checkbox_2.setChecked(false);
        checkbox_3.setChecked(false);
        checkbox_4.setChecked(false);
        if (what==1){
            payMuch=1;
            checkbox_1.setChecked(true);
        }else if (what==2){
            payMuch=3;
            checkbox_2.setChecked(true);
        }else if (what==3){
            payMuch=6;
            checkbox_3.setChecked(true);
        }else if (what==4){
            payMuch=12;
            checkbox_4.setChecked(true);
        }
    }
    private void setImgSrc(int what){
        pay1.setImageDrawable(ContextCompat.getDrawable(UpgradeVipActivity.this,R.drawable.yuan_quan));
        pay2.setImageDrawable(ContextCompat.getDrawable(UpgradeVipActivity.this,R.drawable.yuan_quan));
        pay3.setImageDrawable(ContextCompat.getDrawable(UpgradeVipActivity.this,R.drawable.yuan_quan));
        if (what==1){
            pay1.setImageDrawable(ContextCompat.getDrawable(UpgradeVipActivity.this,R.drawable.yuan_gou));
        }else if (what==2){
            pay2.setImageDrawable(ContextCompat.getDrawable(UpgradeVipActivity.this,R.drawable.yuan_gou));
        }else if (what==3){
            pay3.setImageDrawable(ContextCompat.getDrawable(UpgradeVipActivity.this,R.drawable.yuan_gou));
        }

    }
    private int curChose=1;
    @Override
    protected void initData(Bundle bundle) {
        getPrice();
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
                back();
                break;
            case R.id.l_pay1:
                chargeType=0;
                setImgSrc(1);
                break;
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
                payET.setText("1");
                payET.setSelection(1);
                break;
            case R.id.checkbox_2:
                setCheckbox(2);
                payET.setText("3");
                payET.setSelection(1);
                break;
            case R.id.checkbox_3:
                setCheckbox(3);
                payET.setText("6");
                payET.setSelection(1);
                break;
            case R.id.checkbox_4:
                setCheckbox(4);
                payET.setText("12");
                payET.setSelection(2);
                break;
            case R.id.toPay:
                payAction(payMuch);
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
                PayTask alipay = new PayTask(UpgradeVipActivity.this);
//				Map<String, String> result = alipay.payV2("app_id=2016121404279642&biz_content=%7B%22total_amount%22%3A%2211%22%2C%22body%22%3A%22%E8%AE%A2%E5%8D%95%22%2C%22timeout_express%22%3A%2230m%22%2C%22subject%22%3A%22111%22%2C%22product_code%22%3A%22QUICK_MSECURITY_PAY%22%2C%22seller_id%22%3A%222088411208154941%22%2C%22out_trade_no%22%3A%22111%22%7D&charset=utf-8&format=json&method=alipay.trade.app.pay¬ify_url=http%3A%2F%2Fwww.wehang.net%2FSP%2Fappservice%2Fpay%2FnotifyUrl×tamp=2016-12-22+16%3A19%3A17&version=1.0&sign=jarMPlpPYiYIyHmrm715kDl7%2FNrlTvYh11obVYM8tGDnCHgoHM3QHZUHnvG6uVBMjcfAJI6CTBNIXcweOZEay3AcVuQVCgqqDPn8FD9w3zPa1HlbpzPZS4vhvdUM%2FrUkf8UNEBDCoTEmmysYBI4q2RJEmIp%2F83EoZhrnr6ytT9I%3D&sign_type=RSA", true);
                Map<String, String> result = alipay.payV2(orderInfo, true);

                Message msg = new Message();
                msg.what = 0;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        }).start();
    }
    public void payAction(int payMuch) {
        String token = UserLoginInfo.getUserToken();
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", token);
        params.put("month",payMuch+"");
        params.put("method", chargeType + "");
        dialog = CustomProgressDialog.show(context, "加载中...", true, null);
        HttpTool.doPost(context, UrlConstant.UPVIP, params, true, new TypeToken<BaseResult<WXPay>>() {
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
                } else {
                    PayDialog payDialog=new PayDialog(context,"支付成功");
                    payDialog.showDialog();
                    initData();
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
                        have_howmuch.setText(UserLoginInfo.getUserInfo().balance/100+"元");
                    }
                }

                @Override
                public void onError(int errorCode) {
                    UserLoginInfo.loginOverdue(context, errorCode);
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
    private int oneMonthPrice=0;
    private void getPrice(){
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", UserLoginInfo.getUserToken());
        dialog = CustomProgressDialog.show(context, "加载中...", true, null);
        HttpTool.doPost(context, UrlConstant.CONFIGURE, params, false, new TypeToken<BaseResult<Configure>>() {
        }.getType(), new HttpTool.OnResponseListener() {
            @Override
            public void onSuccess(BaseData data) {
                dialog.dismiss();
                Configure configure = (Configure) data;
                if (configure != null) {
                    oneMonthPrice=configure.vipPrice/100;
                    hm_tv1.setText("       "+oneMonthPrice);
                    hm_tv2.setText("       "+3*oneMonthPrice);
                    hm_tv3.setText("       "+6*oneMonthPrice);
                    hm_tv4.setText("       "+12*oneMonthPrice);
                }
            }

            @Override
            public void onError(int errorCode) {
                dialog.dismiss();
                UserLoginInfo.loginOverdue(context, errorCode);
            }
        });
    }
}
