package com.wehang.ystv.logic;

import android.text.SpannableStringBuilder;

/**
 * 聊天室消息
 */
public class ChatRoomInfo {
    //消息
    public String msg;
    //用户Id
    public String userId;
    //昵称
    public String nickName;
    //头像
    public String headPic;
    //等级
    public int userLevel;
    //是否为Vip
    public int userIsVip;
    //消息类型
    public int userAction;
    //礼物数量
    public String giftCount;
    //礼物名称
    public String giftName;
    //礼物图片
    public String giftPic;
    //礼物编号
    public String giftNo;
    //点赞图标
    public String praiseNo;

    public SpannableStringBuilder stringBuilder;

    public ChatRoomInfo(int userAction, String msg, String userId, String nickName, String headPic, int userLevel, int userIsVip, String giftCount, String giftName, String giftPic, String giftNo, String praiseNo) {
        this.msg = msg;
        this.userId = userId;
        this.nickName = nickName;
        this.headPic = headPic;
        this.userLevel = userLevel;
        this.userIsVip = userIsVip;
        this.userAction = userAction;
        this.giftCount = giftCount;
        this.giftName = giftName;
        this.giftPic = giftPic;
        this.giftNo = giftNo;
        this.praiseNo = praiseNo;
    }
    public ChatRoomInfo(int userAction, SpannableStringBuilder stringBuilder, String userId, String nickName, String headPic, int userLevel, int userIsVip, String giftCount, String giftName, String giftPic, String giftNo, String praiseNo) {
        this.stringBuilder =stringBuilder;
        this.userId = userId;
        this.nickName = nickName;
        this.headPic = headPic;
        this.userLevel = userLevel;
        this.userIsVip = userIsVip;
        this.userAction = userAction;
        this.giftCount = giftCount;
        this.giftName = giftName;
        this.giftPic = giftPic;
        this.giftNo = giftNo;
        this.praiseNo = praiseNo;
    }
}
