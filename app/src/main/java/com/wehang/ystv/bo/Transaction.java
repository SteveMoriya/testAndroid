package com.wehang.ystv.bo;

import com.whcd.base.interfaces.BaseData;

/**
 * Created by lenovo on 2017/8/11.
 */

public class Transaction extends BaseData {

    /**
     * type : 0
     * sourceName : 资源名称
     * money : 50
     * channel : 1
     * createTime : 2015-01-01 00: 00
     */

    public int type;
    public String sourceName;
    public int money;
    public int channel;
    public String createTime;
    public String title;
}
