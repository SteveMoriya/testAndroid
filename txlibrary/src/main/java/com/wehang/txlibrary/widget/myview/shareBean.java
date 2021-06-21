package com.wehang.txlibrary.widget.myview;

import com.whcd.base.interfaces.BaseData;

/**
 * Created by lenovo on 2017/8/30.
 */

public class shareBean extends BaseData{
    public String sourcePic;
    public String sourceTitle;
    public String Html;
    public String brife;

    @Override
    public String toString() {
        return "shareBean{" +
                "sourcePic='" + sourcePic + '\'' +
                ", sourceTitle='" + sourceTitle + '\'' +
                ", Html='" + Html + '\'' +
                ", brife='" + brife + '\'' +
                '}';
    }
}
