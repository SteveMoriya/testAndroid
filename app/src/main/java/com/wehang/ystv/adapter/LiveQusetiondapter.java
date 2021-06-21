package com.wehang.ystv.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.bumptech.glide.Glide;
import com.wehang.ystv.Constant;
import com.wehang.ystv.R;
import com.wehang.ystv.bo.QuestionMessages;
import com.wehang.ystv.bo.TypeInfo;
import com.wehang.ystv.bo.UserInfo;
import com.wehang.ystv.interfaces.UrlConstant;
import com.wehang.ystv.logic.ChatRoomInfo;
import com.wehang.ystv.ui.AnswerActivity;
import com.wehang.ystv.ui.LivePushActivity;
import com.wehang.ystv.ui.UserHomeActivty;
import com.wehang.ystv.utils.CommonUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.wehang.ystv.utils.UserLoginInfo.userInfo;

@SuppressLint({"HandlerLeak", "InflateParams"})
public class LiveQusetiondapter extends BaseAdapter {
    private Context context;

    private List<QuestionMessages> messageList=new ArrayList<>() ;
    private boolean isPush;
    private String sourceId;
    private ImageView WhatBigImg;


    public LiveQusetiondapter(Context context,boolean isPush,String sourceId,ImageView WhatBigImg) {
        this.context = context;

        this.isPush=isPush;
        this.sourceId=sourceId;
        this.WhatBigImg=WhatBigImg;
    }

    public QuestionMessages getItem(int position) {
        if (messageList != null && position < messageList.size()) {
            return messageList.get(position);
        }
        return null;
    }

    public long getItemId(int position) {
        return position;
    }

    public int getCount() {
        return messageList.size();
    }

    @SuppressWarnings("deprecation")
    @SuppressLint({"NewApi", "ResourceAsColor"})
    public View getView(final int position, View convertView, ViewGroup parent) {
        HolderView holder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.list_item_live_video_chart1, null);
            holder = new HolderView();
            holder.messageTv = (TextView) convertView.findViewById(R.id.messageTv);
            holder.nameTv = (TextView) convertView.findViewById(R.id.nameTv);
            holder.levelImg = (ImageView) convertView.findViewById(R.id.levelImg);
            holder.line1 =  convertView.findViewById(R.id.line1);
            holder.user_tx_itemlive=convertView.findViewById(R.id.user_tx_itemlive);
            holder.isVipImg=convertView.findViewById(R.id.isVipImg);

            //回答
            holder.img1=convertView.findViewById(R.id.question_img1);
            holder.img2=convertView.findViewById(R.id.question_img2);
            holder.img3=convertView.findViewById(R.id.question_img3);
            holder.pic_line=convertView.findViewById(R.id.pic_Line);
            holder.ans=convertView.findViewById(R.id.ansContent);
            holder.toans=convertView.findViewById(R.id.toans);
            convertView.setTag(holder);
        } else {
            holder = (HolderView) convertView.getTag();
        }
        final QuestionMessages questionMessages=messageList.get(position);
        holder.nameTv.setText(questionMessages.questionUserName + ":");
        Glide.with(context).load(UrlConstant.IP+questionMessages.questionIconUrl).placeholder(R.drawable.default_portrait).error(R.drawable.default_portrait).into(holder.user_tx_itemlive);
        holder.line1.setVisibility(View.GONE);
        holder.messageTv.setText(questionMessages.questionContent);
        holder.messageTv.setTextColor(context.getResources().getColor((R.color.white)));

            if ( questionMessages.isVip==1){
                holder.isVipImg.setVisibility(View.VISIBLE);
            }else {
                holder.isVipImg.setVisibility(View.GONE);
            }
        holder.user_tx_itemlive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserInfo userInfo=new UserInfo();
                userInfo.userId=questionMessages.questionUserId;
                Intent intent=new Intent(context, UserHomeActivty.class);
                Bundle bundle=new Bundle();
                bundle.putSerializable("data",userInfo);
                intent.putExtra("bundle",bundle);
                context.startActivity(intent);
            }
        });
        final String[] pic;
        if (TextUtils.isEmpty(questionMessages.pics)){
            pic= new String[0];
            holder.img1.setVisibility(View.GONE);
            holder.img3.setVisibility(View.GONE);
            holder.img2.setVisibility(View.GONE);
        }else {
            pic=questionMessages.pics.split(",");
        }
        if (pic.length>0){
            holder.img1.setVisibility(View.GONE);
            holder.img3.setVisibility(View.GONE);
            holder.img2.setVisibility(View.GONE);
            if (pic.length>0){
                holder.img1.setVisibility(View.VISIBLE);
                Glide.with(context).load(UrlConstant.IP+pic[0]).placeholder(R.drawable.default_cover).error(R.drawable.default_cover).into(holder.img1);
            }
            if (pic.length>1){
                holder.img2.setVisibility(View.VISIBLE);
                Glide.with(context).load(UrlConstant.IP+pic[1]).placeholder(R.drawable.default_cover).error(R.drawable.default_cover).into(holder.img2);
            }
            if (pic.length>2){
                holder.img3.setVisibility(View.VISIBLE);
                Glide.with(context).load(UrlConstant.IP+pic[2]).placeholder(R.drawable.default_cover).error(R.drawable.default_cover).into(holder.img3);
            }
            holder.img1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    WhatBigImg.setVisibility(View.VISIBLE);
                    Glide.with(context).load(UrlConstant.IP+pic[0]).placeholder(R.drawable.live_mrjz).error(R.drawable.live_mrjz).into(WhatBigImg);
                }
            });
            holder.img2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    WhatBigImg.setVisibility(View.VISIBLE);
                    Glide.with(context).load(UrlConstant.IP+pic[1]).placeholder(R.drawable.live_mrjz).error(R.drawable.live_mrjz).into(WhatBigImg);
                }
            });
            holder.img3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    WhatBigImg.setVisibility(View.VISIBLE);
                    Glide.with(context).load(UrlConstant.IP+pic[2]).placeholder(R.drawable.live_mrjz).error(R.drawable.live_mrjz).into(WhatBigImg);
                }
            });
        }

        holder.pic_line.setVisibility(View.GONE);
        if (TextUtils.isEmpty(questionMessages.answerContent)){
            holder.ans.setVisibility(View.GONE);
            if (isPush){
                holder.toans.setVisibility(View.VISIBLE);
                holder.toans.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Constant.questinId=questionMessages.questionId;
                        Intent intent=new Intent(context, AnswerActivity.class);
                        Bundle bundle=new Bundle();
                        //需要自己设置，后台没有这个字段
                        questionMessages.sourceId=sourceId;
                        bundle.putSerializable("data",questionMessages);
                        intent.putExtra("bundle",bundle);
                        Activity activity=(LivePushActivity) context;
                        activity.startActivityForResult(intent,110);
                    }
                });
            }else {
                holder.toans.setVisibility(View.GONE);
            }
        }else {
            holder.pic_line.setVisibility(View.VISIBLE);
            holder.ans.setVisibility(View.VISIBLE);
            holder.ans.setText(questionMessages.answerContent);
            holder.toans.setVisibility(View.GONE);
        }
        return convertView;
    }

    /**
     * 初始化消息列表
     *
     * @param msgs
     */

    public void resetMessageList(List<QuestionMessages> msgs) {
        messageList.clear();
        if (msgs != null) {
            messageList.addAll(msgs);
        }
        notifyDataSetChanged();
    }

    /**
     * 刷新页面, 选择最后一条
     */
    public void appendToList(QuestionMessages message) {
        if (messageList.size() >= 100) {
            messageList.remove(0);
        }
        messageList.add(message);
        notifyDataSetChanged();
    }

    class HolderView {
        TextView messageTv, nameTv;
        ImageView levelImg,user_tx_itemlive,isVipImg;
        RelativeLayout line1;

        View pic_line;
        ImageView img1,img2,img3;
        TextView ans,toans;

    }
}