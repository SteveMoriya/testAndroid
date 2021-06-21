package com.wehang.ystv.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.wehang.ystv.Constant;
import com.wehang.ystv.R;
import com.wehang.ystv.bo.QuestionMessages;
import com.wehang.ystv.bo.UserInfo;
import com.wehang.ystv.interfaces.UrlConstant;
import com.wehang.ystv.ui.AnswerActivity;
import com.wehang.ystv.ui.GetQuestionActivity;
import com.wehang.ystv.ui.LivePushActivity;
import com.wehang.ystv.ui.UserHomeActivty;

import java.util.ArrayList;
import java.util.List;

@SuppressLint({"HandlerLeak", "InflateParams"})
public class RoomQusetiondapter extends BaseAdapter {
    private Context context;

    private List<QuestionMessages> messageList=new ArrayList<>() ;
    private boolean isPush;
    private String sourceId;
    private int type=4;
    private ImageView WhatBigImg;


    public RoomQusetiondapter(Context context, boolean isPush, String sourceId,ImageView WhatBigImg) {
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
            convertView = inflater.inflate(R.layout.item_question, null);
            holder = new HolderView();
            holder.headImg = (ImageView) convertView.findViewById(R.id.user_tx_itemlive);
            holder.userNameTv = (TextView) convertView.findViewById(R.id.userName);
            holder.liveNameTV=convertView.findViewById(R.id.liveName);
            holder.askTimeTv= (TextView) convertView.findViewById(R.id.askTime);
            holder.ansTimeTV= (TextView) convertView.findViewById(R.id.ansTime);
            holder.contentTV=convertView.findViewById(R.id.contentTV);
            holder.img1=convertView.findViewById(R.id.tw_img1);
            holder.img2=convertView.findViewById(R.id.tw_img2);
            holder.img3=convertView.findViewById(R.id.tw_img3);
            holder.teacherNameTV=convertView.findViewById(R.id.teacherName);
            holder.teacherAnsTV=convertView.findViewById(R.id.teacherAnsawer);
            holder.delteTV=convertView.findViewById(R.id.delte);
            holder.bg=convertView.findViewById(R.id.tiwenBg);
            holder.isShowAnsLiner=convertView.findViewById(R.id.isShowAnsLiner);
            holder.showDeltLin=convertView.findViewById(R.id.showDeltLin);
            holder.toans=convertView.findViewById(R.id.toans);
            convertView.setTag(holder);
        } else {
            holder = (HolderView) convertView.getTag();
        }
        final QuestionMessages questionMessages=messageList.get(position);
        Glide.with(context).load(UrlConstant.IP+questionMessages.questionIconUrl).placeholder(R.drawable.default_portrait).error(R.drawable.default_portrait).into(holder.headImg);
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

        holder.userNameTv.setText(questionMessages.questionUserName);
        holder.contentTV.setText(questionMessages.questionContent);
        holder.teacherAnsTV.setText(questionMessages.answerContent);
        holder.teacherNameTV.setText(questionMessages.answerUserName+":");
        if (type==3){
            holder.delteTV.setVisibility(View.VISIBLE);
            holder.showDeltLin.setVisibility(View.VISIBLE);
            holder.bg.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_white_big_shape));
        }else {
            holder.delteTV.setVisibility(View.GONE);
            holder.showDeltLin.setVisibility(View.GONE);
            holder.bg.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_white_big_shape));
        }
        if (type==2){
            holder.liveNameTV.setVisibility(View.VISIBLE);
        }else {
            holder.liveNameTV.setVisibility(View.GONE);
        }
        if (type==4){
            holder.liveNameTV.setVisibility(View.GONE);
        }
        if(TextUtils.isEmpty(questionMessages.questionTime)){
            holder.ansTimeTV.setVisibility(View.GONE);
        }else {
            holder.ansTimeTV.setVisibility(View.VISIBLE);
            holder.askTimeTv.setVisibility(View.VISIBLE);
            holder.askTimeTv.setText(questionMessages.questionTime);
        }
        if (TextUtils.isEmpty(questionMessages.answerTime)){
            holder.ansTimeTV.setVisibility(View.GONE);
        }else {
            holder.ansTimeTV.setVisibility(View.VISIBLE);
            holder.ansTimeTV.setText(questionMessages.answerTime);
        }
        if (TextUtils.isEmpty(questionMessages.answerContent)){
            holder.isShowAnsLiner.setVisibility(View.GONE);
            if (type==2){
                holder.toans.setVisibility(View.VISIBLE);
            }else {
                holder.toans.setVisibility(View.GONE);
            }

        }else {
            holder.isShowAnsLiner.setVisibility(View.VISIBLE);
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
        private ImageView headImg, isVipImg, sexImg, levelImg,img1,img2,img3;
        private TextView userNameTv,liveNameTV;
        private TextView lastMsgTv;
        private TextView unreadTv, askTimeTv,ansTimeTV,contentTV,teacherNameTV,teacherAnsTV,delteTV;
        private RelativeLayout bg;
        private LinearLayout isShowAnsLiner,showDeltLin;
        private TextView toans;

    }
}