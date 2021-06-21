package com.wehang.ystv.bo;

import com.whcd.base.interfaces.BaseData;

/**
 * Created by lenovo on 2017/9/5.
 */

public class Order extends BaseData{

    /**
     * orderId : xxxxx
     * orderMoney : 40
     */

    public String orderId;
    public int orderMoney;
    /**
     * orderNo : 123456
     * orderStatus : 1
     * orderTime : xxx
     * sourceId : xxxx
     * sourcePic : 封面图
     * sourceType : 1
     * sourceStauts : 1
     * classification : 分类
     * sourceTitle : 标题
     * name : 主播名称
     * isVip : 0
     * title : 职称
     * hospital : 医院
     * priceType : 0
     * price : 20
     * startTime : 开始时间
     * wacthNum : 0
     */

    public String orderNo;
    public int orderStatus;
    public String orderTime;
    public String sourceId;
    public String sourcePic;
    public int sourceType;
    public int sourceStauts;
    public String classification;
    public String sourceTitle;
    public String name;
    public int isVip;
    public String title;
    public String hospital;
    public int priceType;
    public int price;
    public String startTime;
    public int wacthNum;
    public String applyForRefundTime;
    public String content;
    public String iconUrl;

    @Override
    public String toString() {
        return "Order{" +
                "orderId='" + orderId + '\'' +
                ", orderMoney=" + orderMoney +
                '}';
    }
}
