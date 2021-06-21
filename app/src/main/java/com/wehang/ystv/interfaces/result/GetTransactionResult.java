package com.wehang.ystv.interfaces.result;

import com.wehang.ystv.bo.Lives;
import com.wehang.ystv.bo.Transaction;
import com.wehang.ystv.bo.UserInfo;
import com.whcd.base.interfaces.BaseData;

import java.util.List;

/**
 * Created by lenovo on 2017/6/21.
 */

public class GetTransactionResult extends BaseData {
    private static final long serialVersionUID = 1L;
    public List<Transaction> transaction;

}
