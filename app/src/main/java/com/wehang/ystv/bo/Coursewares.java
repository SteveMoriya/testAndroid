package com.wehang.ystv.bo;

import com.whcd.base.interfaces.BaseData;

/**
 * Created by lenovo on 2017/9/15.
 */

public class Coursewares extends BaseData{

    /**
     * coursewareId : 2
     * coursewareName : 2
     * coursewareUrl : 2
     */

    public String coursewareId;
    public String coursewareName;
    public String coursewareUrl;

    @Override
    public String toString() {
        return "Coursewares{" +
                "coursewareName='" + coursewareName + '\'' +
                '}';
    }
}
