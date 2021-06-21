package com.wehang.ystv.bo;

import com.whcd.base.interfaces.BaseData;

/**
 * Created by lenovo on 2017/6/21.
 */
//直播信息
public class Banner extends BaseData {
    /**
     * bannerId : asdasd
     * bannerName : 测试
     * bannerPic : asdasd
     * link : http://www.wehang.net
     * createTime : null
     */

    public String bannerId;
    public String bannerName;
    public String bannerPic;
    public String link;
    public String createTime;


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

    @Override
    public String toString() {
        return "Banner{" +
                "bannerId='" + bannerId + '\'' +
                ", bannerName='" + bannerName + '\'' +
                ", bannerPic='" + bannerPic + '\'' +
                ", link='" + link + '\'' +
                ", createTime='" + createTime + '\'' +
                '}';
    }
}
