package com.wehang.ystv.adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.wehang.ystv.R;
import com.wehang.ystv.bo.Lives;
import com.wehang.ystv.bo.Order;
import com.wehang.ystv.interfaces.UrlConstant;
import com.wehang.ystv.ui.BuyDetails;
import com.wehang.ystv.ui.PayActivity;
import com.wehang.ystv.utils.UserLoginInfo;

import java.util.List;


public class LiveBuyAdapter extends BaseAdapter {
    private Activity context;
    private LayoutInflater inflater;
    private List<Order> nearLives;
    protected long lastClickTime = 0;
    protected final int TIME_INTERVAL = 500;
    int type=0;

    public LiveBuyAdapter(Activity context, List<Order> nearLives, int type) {
        super();
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.nearLives = nearLives;
        this.type=type;
    }

    @Override
    public int getCount() {
        return nearLives != null ? nearLives.size():0;
    }

    @Override
    public Order getItem(int position) {
        return nearLives.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LiveBuyAdapter.ViewHolder holder = null;
        if (convertView == null) {
            holder = new LiveBuyAdapter.ViewHolder();
            convertView = inflater.inflate(R.layout.buy_item, null);
           /*
            holder.buyCount= (TextView) convertView.findViewById(R.id.user_gmsl_itemlive);*/
            //holder.zsCount= (TextView) convertView.findViewById(R.id.user_zsc_itemlive);
            holder.title= (TextView) convertView.findViewById(R.id.title_item_live);
            holder.live_what= (TextView) convertView.findViewById(R.id.live_what);
            holder.user_bg= (ImageView) convertView.findViewById(R.id.user_bg);
            holder.user_touxiang= (ImageView) convertView.findViewById(R.id.user_tx_itemlive);
            holder.name= (TextView) convertView.findViewById(R.id.user_name_itemlive);
            holder.zw=convertView.findViewById(R.id.user_zw_itemlive);
            holder.ngk=convertView.findViewById(R.id.ngk);
            holder.openTime=convertView.findViewById(R.id.open_time);
            holder.rqz=convertView.findViewById(R.id.rqz);
            holder.live_todo=convertView.findViewById(R.id.live_todo);
            holder.live_into=convertView.findViewById(R.id.live_into);
            holder.price=convertView.findViewById(R.id.live_price);
            holder.yiyuanname=convertView.findViewById(R.id.user_yiyuanm_itemlive);
            holder.isVipImg=convertView.findViewById(R.id.isVipImg);


            //订单
            holder.orderId=convertView.findViewById(R.id.orderId);
            holder.orderId2=convertView.findViewById(R.id.orderId2);
            holder.orderStatus=convertView.findViewById(R.id.orderStatus);
            holder.orderStatus2=convertView.findViewById(R.id.orderStatus2);
            holder.orderTime=convertView.findViewById(R.id.orderTime);
            convertView.setTag(holder);
        } else {
            holder = (LiveBuyAdapter.ViewHolder) convertView.getTag();
        }
        /*    if (type==1){

                //有直播，即将开始，审核中，未通过
                holder.live_what.setText("直播");
                //我的直播,判断是否通过审核
                holder.live_todo.setText("进入直播");
                holder.live_todo.setBackground(ContextCompat.getDrawable(context,R.drawable.red));
                *//*holder.live_todo.setText("编辑");
                holder.live_todo.setBackground(ContextCompat.getDrawable(context,R.drawable.blue));*//*
            }
            if (type==2){
                //我的视频,判断是否通过审核
                holder.live_what.setVisibility(View.GONE);
                //进入论坛
                holder.live_todo.setText("进入论坛");
                holder.live_todo.setBackground(ContextCompat.getDrawable(context,R.drawable.blue));
                //立即购买
                holder.live_todo.setText("立即购买");
                holder.live_todo.setBackground(ContextCompat.getDrawable(context,R.drawable.red));
                //点击进入
                holder.live_todo.setText("点击进入");
                holder.live_todo.setBackground(ContextCompat.getDrawable(context,R.drawable.green));

            }*/
        final Order lives=nearLives.get(position);
        holder.live_todo.setVisibility(View.GONE);
        if (lives.orderStatus==1){
            holder.live_todo.setVisibility(View.VISIBLE);
            holder.live_todo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(context,PayActivity.class);
                    Lives needlives=new Lives();
                    needlives.price=lives.price;
                    needlives.sourceId=lives.sourceId;
                    Bundle bundle=new Bundle();
                    bundle.putSerializable("data",needlives);
                    intent.putExtra("bundle",bundle);
                    context.startActivityForResult(intent,1);
                }
            });
        }
        if (lives.sourceType==1){

            //有直播，即将开始，审核中，未通过
            holder.live_what.setText("直播");
            //我的直播,判断是否通过审核
            /*holder.live_todo.setText("进入直播");
            holder.live_todo.setBackground(ContextCompat.getDrawable(context,R.drawable.red));*/
                /*holder.live_todo.setText("编辑");
                holder.live_todo.setBackground(ContextCompat.getDrawable(context,R.drawable.blue));*/
        }
        if (lives.sourceType==2){

            //有直播，即将开始，审核中，未通过
            holder.live_what.setText("预告");
            //我的直播,判断是否通过审核
           /* holder.live_todo.setText("进入直播");
            holder.live_todo.setBackground(ContextCompat.getDrawable(context,R.drawable.red));*/
                /*holder.live_todo.setText("编辑");
                holder.live_todo.setBackground(ContextCompat.getDrawable(context,R.drawable.blue));*/
        }
        if (lives.sourceType==3){
            //我的视频,判断是否通过审核
            holder.live_what.setVisibility(View.VISIBLE);
            holder.live_what.setText("视频");
          /*  //进入论坛
            holder.live_todo.setText("进入论坛");
            holder.live_todo.setBackground(ContextCompat.getDrawable(context,R.drawable.blue));
            //立即购买
            holder.live_todo.setText("立即购买");
            holder.live_todo.setBackground(ContextCompat.getDrawable(context,R.drawable.red));
            //点击进入
            holder.live_todo.setText("点击进入");
            holder.live_todo.setBackground(ContextCompat.getDrawable(context,R.drawable.green));*/

        }
        if (lives.sourceType==4){
            holder.live_what.setVisibility(View.VISIBLE);
            //有直播，即将开始，审核中，未通过
            holder.live_what.setText("回放");
            //我的直播,判断是否通过审核
           /* holder.live_todo.setText("进入直播");
            holder.live_todo.setBackground(ContextCompat.getDrawable(context,R.drawable.red));
                *//*holder.live_todo.setText("编辑");
                holder.live_todo.setBackground(ContextCompat.getDrawable(context,R.drawable.blue));*/
        }
        Glide.with(context).load(UrlConstant.IP+lives.iconUrl).placeholder(R.drawable.default_portrait).error(R.drawable.default_portrait).into(holder.user_touxiang);
        Glide.with(context).load(UrlConstant.IP+lives.sourcePic).placeholder(R.drawable.live_mrjz).error(R.drawable.live_mrjz).into(holder.user_bg);
        if (lives.isVip==0){
            holder.isVipImg.setVisibility(View.GONE);
        }else {
            holder.isVipImg.setVisibility(View.VISIBLE);
        }
        holder.name.setText(lives.name);
        holder.yiyuanname.setText(lives.hospital);
        holder.zw.setText("("+lives.title+")");
        holder.ngk.setText(lives.classification);
        holder.title.setText(lives.sourceTitle);
        holder.openTime.setText(lives.startTime+"");
        holder.price.setText("￥"+lives.price/100+".00");
        holder.rqz.setText(lives.wacthNum+"");

        holder.orderId.setText(lives.orderNo);
        holder.orderId2.setText(lives.orderNo);
        if (TextUtils.isEmpty(lives.orderStatus+"")){
            lives.orderStatus=1;
        }
        if (lives.orderStatus==1){
            holder.orderStatus.setText("待支付");
            holder.orderStatus2.setText("待支付");
        }else if (lives.orderStatus==2){
            holder.orderStatus.setText("待使用");
            holder.orderStatus2.setText("待使用");
        }else if (lives.orderStatus==3){
            holder.orderStatus.setText("已完成");
            holder.orderStatus2.setText("已完成");
        }else if (lives.orderStatus==4){
            holder.orderStatus.setText("退款中");
            holder.orderStatus2.setText("退款中");
        }else if (lives.orderStatus==5){
            holder. orderStatus.setText("已退款");
            holder.orderStatus2.setText("已退款");
        }

        holder.orderTime.setText(lives.orderTime);
        return convertView;
    }

    private class ViewHolder {
        ImageView user_touxiang,user_bg,isVipImg;

        TextView name, zw, yiyuanname,ngk,buyCount,title,live_what,openTime,rqz,price,live_todo,live_into;

        //订单相关
        TextView orderId,orderId2,orderStatus,orderStatus2,orderTime;
    }

    public void setData(List<Order> nearLives) {
        this.nearLives.clear();
        this.nearLives.addAll(nearLives);
        notifyDataSetChanged();
    }

}