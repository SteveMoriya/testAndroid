package com.wehang.ystv.bo;

import com.whcd.base.interfaces.BaseData;

/**
 * Created by lenovo on 2017/8/9.
 */
//所有消息的父类
public class YsMessage extends BaseData {

    /**
     * content : 您的直播“直播标题”将在30分钟后开始，请您及时处理！
     * time : 2015-01-01 00:00:00
     */

    public String content;
    public String time;
    public boolean isChose=false;
    public int isDefriend;
    @Override
    public String toString() {
        return "YsMessage{" +
                "content='" + content + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
