package com.wehang.ystv.bo;

import com.whcd.base.interfaces.BaseData;

import java.util.List;

/**
 * Created by lenovo on 2017/6/21.
 */
//直播信息
public class Lives extends BaseData {

    /**
     * roomId : 2as2d31231a2sd
     * iconUrl : 头像地址
     * name : 昵称
     * title : 标题
     * price : 500
     * roomPic : ../..jpg
     * buyNum : 50
     * isBuy : 0
     * openTime : 2015-01-01 00:00:00
     */

    public String roomId;
    public String iconUrl;
    public String name;
    public String title;
    public int price;
    public String roomPic;
    public int buyNum;
    public int isBuy;
    public String openTime;
    public String videoUrl;
    public String chatRoomId;
    public String status;
    public String pullRtmlUrl;
    public String userId;
    public String createTime;
    public String beginTime;
    public String endTime;
    public int isRecommend;
    public int onlineNum;
    public int robotNum;
    public int dummyNum;
    public int gainBean;
    public String pushUrl;
    public String pullHlsUrl;
    public String pullRtmpUrl;
    public int isClosed;
    public String breakTime;
    public String pushTime;
    public int duration;
    public String connectId;
    public String connectUserId;
    public int isPhonePush;


    public List<Lives> correlation;

    public int getIsBuy() {
        return isBuy;
    }

    public void setIsBuy(int isBuy) {
        this.isBuy = isBuy;
    }
    /**
     * sourceType : 1
     * sourceTitle : 纷纷
     * isVip : 0
     * sourceStauts : 2
     * priceType : 0
     * sourceId : 38a0c2a2d7b3466987396a2a707113d2
     * sourcePic : 83236f8a3aa9464396bcde28a3f0857a.jpg
     * startTime : null
     * source_title : 纷纷
     * wacthNum : null
     * classification : qwe
     * userName : 更待何时
     * hospital : qfqf
     */

    //优生
    public int sourceType;
    public String sourceTitle;
    public int isVip;
    public int sourceStauts;
    public int priceType;
    public String sourceId;
    public String sourcePic;
    public String startTime;
    public String source_title;
    public String wacthNum;
    public String classification;
    public String userName;
    public String hospital;
    public String departmentName;
    public String summary;
    public int isCollect;
    public String coursewareUrl;
    public String coursewareName;
    public String coursewareId;
    public String fansNum;


    @Override
    public String toString() {
        return "Lives{" +
                "sourceType=" + sourceType +
                ", sourceTitle='" + sourceTitle + '\'' +
                ", isVip=" + isVip +
                ", sourceStauts=" + sourceStauts +
                ", priceType=" + priceType +
                ", sourceId='" + sourceId + '\'' +
                ", sourcePic='" + sourcePic + '\'' +
                ", startTime=" + startTime +
                ", source_title='" + source_title + '\'' +
                ", wacthNum=" + wacthNum +
                ", classification='" + classification + '\'' +
                ", userName='" + userName + '\'' +
                ", hospital='" + hospital + '\'' +
                ", departmentName='" + departmentName + '\'' +
                '}';
    }
}
