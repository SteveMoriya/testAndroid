package com.wehang.ystv.interfaces.result;

import com.wehang.ystv.bo.Classification;
import com.wehang.ystv.bo.Coursewares;
import com.wehang.ystv.bo.Lives;
import com.whcd.base.interfaces.BaseData;

import java.util.List;

/**
 * Created by lenovo on 2017/6/21.
 */

public class GetClassResult extends BaseData {
    private static final long serialVersionUID = 1L;
    public List<Classification> classification;
    public List<Coursewares>coursewares;


}
