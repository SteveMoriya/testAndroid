package com.wehang.ystv.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.blankj.utilcode.util.LogUtils;
import com.bumptech.glide.Glide;
import com.wehang.ystv.R;
import com.wehang.ystv.bo.TypeInfo;
import com.wehang.ystv.bo.UserInfo;
import com.wehang.ystv.interfaces.UrlConstant;
import com.wehang.ystv.logic.ChatRoomInfo;
import com.wehang.ystv.ui.UserHomeActivty;
import com.wehang.ystv.utils.CommonUtil;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressLint({"HandlerLeak", "InflateParams"})
public class LiveVideoChartFragmentAdapter extends BaseAdapter {
    private Context context;
    private TypeInfo typeInfo;

    private List<ChatRoomInfo> messageList = new ArrayList<ChatRoomInfo>();
    @SuppressLint("UseSparseArrays")
    private Map<Integer, Integer> levelIds = new HashMap<Integer, Integer>();

    public LiveVideoChartFragmentAdapter(Context context) {
        this.context = context;
        for (int i = 1; i < 400; i++) {
            levelIds.put(i, CommonUtil.getResourceId("l" + i));
        }
    }

    public ChatRoomInfo getItem(int position) {
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
            convertView = inflater.inflate(R.layout.list_item_live_video_chart, null);
            holder = new HolderView();
            holder.messageTv = (TextView) convertView.findViewById(R.id.messageTv);
            holder.nameTv = (TextView) convertView.findViewById(R.id.nameTv);
            holder.levelImg = (ImageView) convertView.findViewById(R.id.levelImg);
            holder.line1 =  convertView.findViewById(R.id.line1);
            holder.user_tx_itemlive=convertView.findViewById(R.id.user_tx_itemlive);
            holder.isVipImg=convertView.findViewById(R.id.isVipImg);
            convertView.setTag(holder);
        } else {
            holder = (HolderView) convertView.getTag();
        }

        ChatRoomInfo message = getItem(position);
        holder.messageTv.setVisibility(View.VISIBLE);
        final UserInfo  userInfo = new UserInfo();
        userInfo.name = message.nickName;
        userInfo.level = Integer.valueOf(message.userLevel);
        userInfo.iconUrl = message.headPic;
        userInfo.userId = message.userId;
        LogUtils.i("userInfo.userId",userInfo.userId+";"+message.userId);
        userInfo.isVip = Integer.valueOf(message.userIsVip);
        typeInfo = new TypeInfo();
        typeInfo.giftCount = message.giftCount;
        typeInfo.giftName = message.giftName;
        typeInfo.giftNo = message.giftNo;
        typeInfo.giftPic = message.giftPic;
        typeInfo.msgType = message.userAction;

        if (userInfo != null) {
            if (levelIds != null) {
                /*Drawable levelDrawable = context.getResources().getDrawable(levelIds.get(userInfo.level));
                holder.levelImg.setImageDrawable(levelDrawable);*/
            }
            holder.nameTv.setText(userInfo.name + "：");
            Glide.with(context).load(UrlConstant.IP+userInfo.iconUrl).placeholder(R.drawable.default_portrait).error(R.drawable.default_portrait).into(holder.user_tx_itemlive);
            if (typeInfo.msgType == 100) {
                holder.nameTv.setVisibility(View.GONE);
                holder.line1.setVisibility(View.GONE);
                holder.messageTv.setText(message.msg);
                holder.messageTv.setTextColor(context.getResources().getColor((R.color.text_msg_pink)));
            }else if (typeInfo.msgType==2){
                holder.nameTv.setVisibility(View.VISIBLE);
                holder.messageTv.setTextColor(context.getResources().getColor((R.color.colorLabelYellow)));
                holder.line1.setVisibility(View.GONE);
                holder.messageTv.setText("" + message.msg);
            }else if (typeInfo.msgType==1){
                holder.line1.setVisibility(View.GONE);
                holder.nameTv.setVisibility(View.VISIBLE);
                holder.messageTv.setText(message.stringBuilder);
                String string= String.valueOf(message.stringBuilder);
                if (string.equals("主播加入直播间")||string.equals("加入直播间")){
                    holder.messageTv.setTextColor(context.getResources().getColor((R.color.colorLabelYellow)));
                }else {
                    holder.messageTv.setTextColor(context.getResources().getColor((R.color.white)));
                }

            }
            else {
                holder.line1.setVisibility(View.GONE);
                holder.nameTv.setVisibility(View.VISIBLE);
                holder.messageTv.setText("" + message.msg);
                holder.messageTv.setTextColor(context.getResources().getColor((R.color.white)));

            }
            if (userInfo.isVip==1){
                holder.isVipImg.setVisibility(View.VISIBLE);
            }else {
                holder.isVipImg.setVisibility(View.GONE);
            }
        holder.user_tx_itemlive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, UserHomeActivty.class);
                Bundle bundle=new Bundle();
                bundle.putSerializable("data",userInfo);
                intent.putExtra("bundle",bundle);
                context.startActivity(intent);
            }
        });
        }
        return convertView;
    }

    /**
     * 初始化消息列表
     *
     * @param msgs
     */

    public void resetMessageList(List<ChatRoomInfo> msgs) {
        messageList.clear();
        if (msgs != null) {
            messageList.addAll(msgs);
        }
        notifyDataSetChanged();
    }

    /**
     * 刷新页面, 选择最后一条
     */
    public void appendToList(ChatRoomInfo message) {
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

    }
}