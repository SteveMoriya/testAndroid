package com.wehang.ystv.ui;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.LogUtils;
import com.google.gson.reflect.TypeToken;
import com.wehang.ystv.R;
import com.wehang.ystv.adapter.RefoundAdapter;
import com.wehang.ystv.bo.Configure;
import com.wehang.ystv.bo.YsMessage;
import com.wehang.ystv.interfaces.UrlConstant;
import com.wehang.ystv.interfaces.result.GetMessageResult;
import com.wehang.ystv.interfaces.result.GetSysMResult;
import com.wehang.ystv.utils.CommonUtil;
import com.wehang.ystv.utils.ToastUtil;
import com.wehang.ystv.utils.UserLoginInfo;
import com.whcd.base.activity.BaseActivity;
import com.whcd.base.interfaces.BaseData;
import com.whcd.base.interfaces.BaseResult;
import com.whcd.base.interfaces.HttpTool;
import com.whcd.base.widget.CustomProgressDialog;
import com.whcd.base.widget.TopMenuBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApplyForRefundActivity extends BaseActivity {
    private TopMenuBar topMenuBar;
    private LinearLayout ly1,ly2,ly3,ly4,ly5,ly6;
    private ImageView lyImg1,lyImg2,lyImg3,lyImg4,lyImg5,lyImg6;
    private TextView lyTv1,lyTv2,lyTv3,lyTv4,lyTv5,lyTv6;

    private EditText signEt;
    private String sign;
    private TextView tipTv;
    private int count = 150;
    private String orderId;
    private String content;
    private ListView refundListView;
    private RefoundAdapter adapter;
    private EditText payET;
    private TextView howmach;
    @Override
    protected int getLayoutResId() {
        return R.layout.activity_apply_for_refund;
    }

    @Override
    protected void initTitleBar() {
        topMenuBar = (TopMenuBar) findViewById(R.id.topMenuBar);
        topMenuBar.setTitle("申请退款");
        topMenuBar.getLeftButton().setOnClickListener(this);
        topMenuBar.getRightButton().setOnClickListener(this);
        topMenuBar.getRightButton().setText("提交");
    }

    @Override
    protected void initView() {



        lyImg6=findViewById(R.id.lyImg6);
        lyTv6=findViewById(R.id.lyTv6);
        ly6=findViewById(R.id.ly6);
        ly6.setOnClickListener(this);

        refundListView=findViewById(R.id.refundListView);
        adapter=new RefoundAdapter(context,refoundList,content,lyImg6);
        refundListView.setAdapter(adapter);

        howmach=findViewById(R.id.howmach);
        payET=findViewById(R.id.payET);
        signEt = (EditText) findViewById(R.id.signEt);
        tipTv = (TextView) findViewById(R.id.tipTv);
        tipTv.setText((signEt.getText().toString().length()) + "/"+count);

        signEt.addTextChangedListener(new TextWatcher() {
            private CharSequence temp = "";// 监听前的文本
            int editStart;
            int editEnd;

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                temp = s;
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                setImgSrc(6);
            }

            @Override
            public void afterTextChanged(Editable s) {
                editStart = signEt.getSelectionStart();
                editEnd = signEt.getSelectionEnd();
                if (temp.length() > count) {
                    try {
                        if (s.length() <= 30) {
                            s.delete(editStart - 1, editEnd);
                            int tempSelection = editStart;
                            Log.e(TAG, "---------------------editStart:" + editStart);
                            signEt.setText(s);
                            signEt.setSelection(tempSelection);
                        }
                    } catch (Exception e) {
                        ToastUtil.makeText(context,"输入文字过长", Toast.LENGTH_SHORT).show();
                    }
                }
                tipTv.setText((signEt.getText().toString().length()) + "/"+count);
                content=s.toString().trim();
            }
        });
        //初始
//        content=lyTv1.getText().toString();

    }
    private int total=0;
    @Override
    protected void initData(Bundle bundle) {
        orderId=getIntent().getStringExtra("orderId");
        howmach.setText("￥"+getIntent().getIntExtra("howmach",0));
        total=getIntent().getIntExtra("howmach",0);
        getData();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (CommonUtil.isFirstClick()){
            return;
        }
        switch (v.getId()){
            case R.id.title_btn_left:
                finish();
            case R.id.title_btn_right:
                money=payET.getText().toString().trim();
                if (TextUtils.isEmpty(money.trim())){
                    ToastUtil.makeText(context,"输入金额不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }

                if (Integer.parseInt(money)>total){
                    ToastUtil.makeText(context,"输入金额不能大于总额",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (isChoseZdy()){
                    if (TextUtils.isEmpty(content.trim())){
                        ToastUtil.makeText(context,"自定义消息不能为空",Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                refund();
                break;

            case R.id.ly6:
                content=signEt.getText().toString().trim();
                setImgSrc(6);
                break;
        }
    }
    private boolean isChoseZdy(){
        for (int b=0;b<refoundList.size();b++){
            if (refoundList.get(b).isChose==true){
                content=refoundList.get(b).content;
                return false;
            }
        }
        return true;
    }
    private void setImgSrc(int what){
        curChose=6;
        for (int b=0;b<refoundList.size();b++){
            refoundList.get(b).isChose=false;
        }
        adapter.notifyDataSetChanged();
        lyImg6.setImageDrawable(ContextCompat.getDrawable(ApplyForRefundActivity.this,R.drawable.yuan_quan));
       if (what==6){
            lyImg6.setImageDrawable(ContextCompat.getDrawable(ApplyForRefundActivity.this,R.drawable.yuan_gou));
        }
    }
    public int curChose=2;
    private  String money;
    //提交退款
    private void refund(){
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", UserLoginInfo.getUserToken());
        params.put("orderId", orderId);
        params.put("content",content);
        if (TextUtils.isEmpty(money)){

        }else {
            params.put("money",money);
        }
        LogUtils.i("contentR",content);
        final CustomProgressDialog dialog = CustomProgressDialog.show(context, "加载中...", true, null);
        HttpTool.doPost(context, UrlConstant.REFUND, params, true, new TypeToken<BaseResult<BaseData>>() {
        }.getType(), new HttpTool.OnResponseListener() {
            @Override
            public void onSuccess(BaseData data) {
                dialog.dismiss();
                ToastUtil.makeText(context,"申请成功",Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onError(int errorCode) {
                dialog.dismiss();
                ToastUtil.makeText(context,"服务器异常，请重试",Toast.LENGTH_SHORT).show();
            }
        });
    }
    private List<YsMessage>refoundList=new ArrayList<>();
    private void getData(){
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", UserLoginInfo.getUserToken());
        dialog = CustomProgressDialog.show(context, "加载中...", true, null);
        HttpTool.doPost(context, UrlConstant.REFUNDREASON, params, false, new TypeToken<BaseResult<GetMessageResult>>() {
        }.getType(), new HttpTool.OnResponseListener() {
            @Override
            public void onSuccess(BaseData data) {
                dialog.dismiss();
                GetMessageResult result = (GetMessageResult) data;
                if (result.reason != null) {
                    refoundList.addAll(result.reason);
                    refoundList.get(0).isChose=true;
                    content=refoundList.get(0).content;
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(int errorCode) {
                dialog.dismiss();
            }
        });
    }
}
