package com.wehang.ystv.bo;

import com.google.gson.annotations.SerializedName;
import com.whcd.base.interfaces.BaseData;

/**
 * @author 钟林宏 2016/12/21 15:10
 * @version V1.0
 * @ProjectName: WaywardLive
 * @Copyright: 2015 WHCD Co., Ltd. All Right Reserved.
 * @address:
 * @date: 2016/12/21 15:10
 * @Description: 本内容仅限于公司内部使用，禁止转发.
 * @instructions:{类文件说明}
 * @modificationHistory=========================重大变更说明
 * @modify by user: 钟林宏 2016/12/21
 */

public class WXPay extends BaseData {
    public WxCharge wxCharge;
    public String zfbCharge;
    public Order order;


    public class WxCharge {
        /**
         * appid : wxaaf68586b8916cc5
         * noncestr : e816c635cad85a60fabd6b97b03cbcc9
         * package : Sign=WXPay
         * partnerid : 1425772802
         * prepayid : wx20170704105050ec7a86d87c0670754815
         * sign : CF11F88AC5210E074757EFE45A9FCFAE
         * timestamp : 1499136650
         */

        public String appid;
        public String noncestr;
        @SerializedName("package")
        public String packageX;
        public String partnerid;
        public String prepayid;
        public String sign;
        public String timestamp;
      /*  public String partnerid;
        public String prepayid;
        public String noncestr;
        public String timestamp;
//        public String package;
        public String sign;


        *//**
         * appid : wxaaf68586b8916cc5
         * package : Sign=WXPay
         *//*

        public String appid;
        @SerializedName("package")
        public String packageX;*/

        @Override
        public String toString() {
            return "WxCharge{" +
                    "appid='" + appid + '\'' +
                    ", noncestr='" + noncestr + '\'' +
                    ", packageX='" + packageX + '\'' +
                    ", partnerid='" + partnerid + '\'' +
                    ", prepayid='" + prepayid + '\'' +
                    ", sign='" + sign + '\'' +
                    ", timestamp='" + timestamp + '\'' +
                    '}';
        }
    }
}
