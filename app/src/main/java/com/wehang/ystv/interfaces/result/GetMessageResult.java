package com.wehang.ystv.interfaces.result;

import com.wehang.ystv.bo.YsMessage;
import com.whcd.base.interfaces.BaseData;

import java.util.List;

/**
 * Created by lenovo on 2017/6/21.
 */

public class GetMessageResult extends BaseData {
    private static final long serialVersionUID = 1L;
    public YsMessage sysMessage;
    public YsMessage questionMessage;
    public YsMessage answerMessage;
    public List<YsMessage>reason;
}
