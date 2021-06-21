package com.wehang.ystv.bo;

import com.whcd.base.interfaces.BaseData;

/**
 * Created by lenovo on 2017/9/6.
 */

public class LiveNeed extends BaseData{

    /**
     * pullHlsUrl : rtmp://4686.liveplay.myqcloud.com/live/4686_bef38466c5194537bc5a17b80cc7d25b?txSecret=4c849dc517694cf163f84da3cbde2dec&txTime=59B0B3F9&bizid=null&session_id=1318993245532367407
     * createTime : 2017-09-06 10:50:33
     * sourceType : 1
     * sourceTitle : 啊啊啊
     * isVip : 0
     * sourceStauts : 2
     * priceType : 0
     * pushUrl : rtmp://4686.livepush.myqcloud.com/live/4686_bef38466c5194537bc5a17b80cc7d25b?bizid=null&txSecret=4c849dc517694cf163f84da3cbde2dec&txTime=59B0B3F9
     * sourceId : bef38466c5194537bc5a17b80cc7d25b
     * sourcePic : 4e09c115ca9d4d1eb36806a41a9d9179.jpg
     * pullRtmpUrl : rtmp://4686.liveplay.myqcloud.com/live/4686_bef38466c5194537bc5a17b80cc7d25b?txSecret=4c849dc517694cf163f84da3cbde2dec&txTime=59B0B3F9&bizid=null&session_id=1318993245532367407
     * title : qwdq
     * chatRoomId : @TGS#3Y3KCW4EK
     * price : 1
     * sessionId : 1318993245532367407
     * classification : we
     * name : 更待何时
     * hospital : qfqf
     */

    public String pullHlsUrl;
    public String createTime;
    public int sourceType;
    public String sourceTitle;
    public int isVip;
    public int sourceStauts;
    public int priceType;
    public String pushUrl;
    public String sourceId;
    public String sourcePic;
    public String pullRtmpUrl;
    public String title;
    public String chatRoomId;
    public int price;
    public String sessionId;
    public String classification;
    public String name;
    public String hospital;
    public int isPhonePush;
    public String pullRtmpUrlUrl;

    @Override
    public String toString() {
        return "LiveNeed{" +
                "pullHlsUrl='" + pullHlsUrl + '\'' +
                ", createTime='" + createTime + '\'' +
                ", sourceType=" + sourceType +
                ", sourceTitle='" + sourceTitle + '\'' +
                ", isVip=" + isVip +
                ", sourceStauts=" + sourceStauts +
                ", priceType=" + priceType +
                ", pushUrl='" + pushUrl + '\'' +
                ", sourceId='" + sourceId + '\'' +
                ", sourcePic='" + sourcePic + '\'' +
                ", pullRtmpUrl='" + pullRtmpUrl + '\'' +
                ", title='" + title + '\'' +
                ", chatRoomId='" + chatRoomId + '\'' +
                ", price=" + price +
                ", sessionId='" + sessionId + '\'' +
                ", classification='" + classification + '\'' +
                ", name='" + name + '\'' +
                ", hospital='" + hospital + '\'' +
                '}';
    }
}
