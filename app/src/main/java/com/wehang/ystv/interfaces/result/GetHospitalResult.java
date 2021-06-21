package com.wehang.ystv.interfaces.result;

import com.wehang.ystv.bo.Hospital;
import com.wehang.ystv.bo.Province;
import com.wehang.ystv.bo.QueryDepartment;
import com.wehang.ystv.bo.QueryTitle;
import com.whcd.base.interfaces.BaseData;

import java.util.List;

/**
 * Created by lenovo on 2017/6/21.
 */

public class GetHospitalResult extends BaseData {
    private static final long serialVersionUID = 1L;
    public List<Hospital> hospital;
    public List<QueryDepartment> department;
    public List<QueryTitle> title;
}
