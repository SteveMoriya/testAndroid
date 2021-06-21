package com.wehang.ystv.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.LogUtils;
import com.bumptech.glide.Glide;
import com.google.gson.reflect.TypeToken;
import com.wehang.ystv.R;
import com.wehang.ystv.adapter.BuyDetailsAdapter;
import com.wehang.ystv.bo.Lives;
import com.wehang.ystv.bo.Order;
import com.wehang.ystv.bo.WXPay;
import com.wehang.ystv.interfaces.UrlConstant;
import com.wehang.ystv.interfaces.result.GetBuyResult;
import com.wehang.ystv.utils.CommonUtil;
import com.wehang.ystv.utils.ToastUtil;
import com.wehang.ystv.utils.UserLoginInfo;
import com.whcd.base.activity.BaseActivity;
import com.whcd.base.interfaces.BaseData;
import com.whcd.base.interfaces.BaseResult;
import com.whcd.base.interfaces.HttpTool;
import com.whcd.base.widget.CustomProgressDialog;
import com.whcd.base.widget.PageListView;
import com.whcd.base.widget.TopMenuBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BuyDetails extends BaseActivity {
    private TopMenuBar topMenuBar;
    private PageListView listView;
    private BuyDetailsAdapter adapter;
    private int type=2;
    private Order order;
    private List<Order>list=new ArrayList<>();
    private TextView toPay,totalMonney;
    @Override
    protected int getLayoutResId() {
        return R.layout.activity_buy_details;
    }

    @Override
    protected void initTitleBar() {

        topMenuBar = (TopMenuBar) findViewById(R.id.topMenuBar);
        topMenuBar.setTitle("订单详情");

        topMenuBar.getLeftButton().setOnClickListener(this);
        //通过待付款和待使用
        if (TextUtils.isEmpty(order.orderStatus+"")){
            order.orderStatus=1;
        }
        if (order.orderStatus==1){
            topMenuBar.getRightButton().setText("取消订单");
            topMenuBar.getRightButton().setOnClickListener(this);
        }else if (order.orderStatus==2){
            topMenuBar.getRightButton().setOnClickListener(this);
            topMenuBar.getRightButton().setText("申请退款");
        }







    }
    private TextView orderId,orderId2,orderStatus,orderStatus2;
    @Override
    protected void initView() {
        Bundle bundle=getIntent().getBundleExtra("bundle");
        order= (Order) bundle.getSerializable("data");
        //订单所有数据想关都在头部，只有退款才有listview的Item
        View head = LayoutInflater.from(this).inflate(R.layout.buy_item, null);

        TextView title= (TextView)head.findViewById(R.id.title_item_live);
        TextView live_what= (TextView) head.findViewById(R.id.live_what);
        ImageView user_bg= (ImageView) head.findViewById(R.id.user_bg);
        ImageView user_touxiang= (ImageView) head.findViewById(R.id.user_tx_itemlive);
        TextView name= (TextView)head.findViewById(R.id.user_name_itemlive);
        TextView zw=head.findViewById(R.id.user_zw_itemlive);
        TextView ngk=head.findViewById(R.id.ngk);
        TextView openTime=head.findViewById(R.id.open_time);
        TextView rqz=head.findViewById(R.id.rqz);
        TextView live_todo=head.findViewById(R.id.live_todo);
        TextView live_into=head.findViewById(R.id.live_into);
        TextView price=head.findViewById(R.id.live_price);
        TextView yiyuanname=head.findViewById(R.id.user_yiyuanm_itemlive);
        ImageView isVipImg=head.findViewById(R.id.isVipImg);
        LinearLayout showDtials=head.findViewById(R.id.showDtials);

        //订单
        orderId=head.findViewById(R.id.orderId);
        orderId2=head.findViewById(R.id.orderId2);
        orderStatus=head.findViewById(R.id.orderStatus);
        orderStatus2=head.findViewById(R.id.orderStatus2);
        TextView orderTime=head.findViewById(R.id.orderTime);
        showDtials.setVisibility(View.VISIBLE);


        live_todo.setVisibility(View.GONE);
        if (order.orderStatus==1){
            live_todo.setVisibility(View.VISIBLE);
            live_todo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(context,PayActivity.class);
                    Lives needlives=new Lives();
                    needlives.price=order.price;
                    needlives.sourceId=order.sourceId;
                    Bundle bundle=new Bundle();
                    bundle.putSerializable("data",needlives);
                    intent.putExtra("bundle",bundle);
                    context.startActivityForResult(intent,1);
                }
            });
        }
        if (order.sourceType==1){

            //有直播，即将开始，审核中，未通过
            live_what.setText("直播");

        }
        if (order.sourceType==2){
            //有直播，即将开始，审核中，未通过
            live_what.setText("预告");
        }
        if (order.sourceType==3){
            //我的视频,判断是否通过审核
            live_what.setText("视频");
        }
        if (order.sourceType==4){
            live_what.setText("回放");
        }
        Glide.with(context).load(UrlConstant.IP+ UserLoginInfo.getUserInfo().iconUrl).placeholder(R.drawable.default_portrait).error(R.drawable.default_portrait).into(user_touxiang);
        Glide.with(context).load(UrlConstant.IP+order.sourcePic).placeholder(R.drawable.live_mrjz).error(R.drawable.live_mrjz).into(user_bg);
        if (order.isVip==0){
            isVipImg.setVisibility(View.GONE);
        }else {
            isVipImg.setVisibility(View.VISIBLE);
        }
        name.setText(order.name);
        yiyuanname.setText(order.hospital);
        zw.setText(order.title);
        ngk.setText(order.classification);
        title.setText(order.sourceTitle);
        openTime.setText(order.startTime+"");
        price.setText("￥"+order.price/100+".00");
        rqz.setText(order.wacthNum+"");

        orderId.setText(order.orderNo);
        orderId2.setText(order.orderNo);
        if (TextUtils.isEmpty(order.orderStatus+"")){
            order.orderStatus=1;
        }
        if (order.orderStatus==1){
            orderStatus.setText("待支付");
            orderStatus2.setText("待支付");
        }else if (order.orderStatus==2){
            orderStatus.setText("待使用");
           orderStatus2.setText("待使用");
        }else if (order.orderStatus==3){
            orderStatus.setText("已完成");
            orderStatus2.setText("已完成");
        }else if (order.orderStatus==4){
            orderStatus.setText("退款中");
            orderStatus2.setText("退款中");
        }else if (order.orderStatus==5) {
            orderStatus.setText("已退款");
            orderStatus2.setText("已退款");
        }

       orderTime.setText(order.orderTime);



        listView = findViewById(R.id.listView);
        listView.addHeaderView(head);
        listView.setPullRefreshEnable(false);
        listView.setPullLoadEnable(false);
        //listView.setPageListViewListener(this);



        adapter=new BuyDetailsAdapter(this,list);
        listView.setAdapter(adapter);
          if (order.orderStatus==4||order.orderStatus==5||order.orderStatus==6){
            list.clear();
            list.add(order);
            adapter.notifyDataSetChanged();
        }


        toPay=findViewById(R.id.toPay);
        totalMonney=findViewById(R.id.totalMonney);
        if (order.orderStatus==1){
            toPay.setVisibility(View.VISIBLE);
        }else {
            toPay.setVisibility(View.GONE);
        }
        toPay.setOnClickListener(this);
        totalMonney.setText("￥"+order.price/100+".00");
    }

    @Override
    protected void initData(Bundle bundle) {
        getOrdDitals();
    }
    @Override
    public void onClick(View v) {
        super.onClick(v);
        //title_text
        if (CommonUtil.isFirstClick()){
            return;
        }
        switch (v.getId()){
            case R.id.title_btn_left:finish();
                break;
            case R.id.title_btn_right:
                if (order.orderStatus==1){
                    CommonUtil.showYesNoDialog(context, "是否确认取消订单？", "确定", "取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int arg1) {
                            if (arg1 == DialogInterface.BUTTON_POSITIVE) {
                                quxiao();
                            }
                        }
                    }).show();

                }else if (order.orderStatus==2){
                    wantReMonney();
                }
                break;
            case R.id.toPay:
                Intent intent=new Intent(BuyDetails.this,PayActivity.class);
                Lives lives=new Lives();
                lives.price=order.price;
                lives.sourceId=order.sourceId;
                Bundle bundle=new Bundle();
                bundle.putSerializable("data",lives);
                intent.putExtra("bundle",bundle);
                context.startActivityForResult(intent,1);
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode==1&&resultCode==RESULT_OK){
            order.orderStatus=3;
            topMenuBar.getRightButton().setText("");
            topMenuBar.getRightButton().setOnClickListener(null);
            if (order.orderStatus==1){
                orderStatus.setText("待支付");
                orderStatus2.setText("待支付");
            }else if (order.orderStatus==2){
                orderStatus.setText("待使用");
                orderStatus2.setText("待使用");
            }else if (order.orderStatus==3){
                orderStatus.setText("已完成");
                orderStatus2.setText("已完成");
            }else if (order.orderStatus==4){
                orderStatus.setText("退款中");
                orderStatus2.setText("退款中");
            }else if (order.orderStatus==5){
                orderStatus.setText("已退款");
                orderStatus2.setText("已退款");
            }
            if (order.orderStatus==1){
                toPay.setVisibility(View.VISIBLE);
            }else {
                toPay.setVisibility(View.GONE);
            }
            finish();
        }else if (requestCode==2&&resultCode==RESULT_OK){
            //getOrdDitals();
            finish();
        }
    }

    //取消订单
    private void quxiao(){
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", UserLoginInfo.getUserToken(this));
        params.put("orderId", order.orderId);
        dialog = CustomProgressDialog.show(context, "加载中...", true, null);
        HttpTool.doPost(this, UrlConstant.CANCELORDER, params, false, new TypeToken<BaseResult<GetBuyResult>>() {
        }.getType(), new HttpTool.OnResponseListener() {
            @Override
            public void onSuccess(BaseData data) {
                dialog.dismiss();
                ToastUtil.makeText(context,"取消成功", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onError(int errorCode) {
                dialog.dismiss();
                UserLoginInfo.loginOverdue(context, errorCode);
            }
        });
    }
    //申请退款
    private void wantReMonney(){
        Intent intent=new Intent(BuyDetails.this,ApplyForRefundActivity.class);
        intent.putExtra("orderId",order.orderId);
        intent.putExtra("howmach",order.price/100);
        startActivityForResult(intent,2);
    }
    //订单详情
    private List<Order>listGet=new ArrayList<>();
    private void getOrdDitals(){
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", UserLoginInfo.getUserToken());
        params.put("orderId", order.orderId);
        dialog = CustomProgressDialog.show(context, "加载中...", true, null);
        HttpTool.doPost(context, UrlConstant.BUYRECORDDETAIL, params, true, new TypeToken<BaseResult<GetBuyResult>>() {
        }.getType(), new HttpTool.OnResponseListener() {
            @Override
            public void onSuccess(BaseData data) {
                dialog.dismiss();
                GetBuyResult result= (GetBuyResult) data;
                listGet=result.order;
                if (listGet!=null){
                    if (listGet.size()>0){
                        list.clear();
                        for (int i=0;i<listGet.size();i++){
                            if (listGet.get(i).orderStatus==4||listGet.get(i).orderStatus==5||listGet.get(i).orderStatus==6){
                                list.add(listGet.get(i));
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                }

            }

            @Override
            public void onError(int errorCode) {
                dialog.dismiss();
                ToastUtil.makeText(context,"服务器异常，请重试",Toast.LENGTH_SHORT).show();
            }
        });
    }
}

