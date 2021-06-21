package com.wehang.ystv.interfaces.result;

import com.wehang.ystv.bo.QuestionMessages;
import com.wehang.ystv.bo.SysMessage;
import com.whcd.base.interfaces.BaseData;


import java.util.List;

/**
 * Created by lenovo on 2017/6/21.
 */

public class GetSysMResult extends BaseData {
    private static final long serialVersionUID = 1L;
    public List<SysMessage> messages;
    public List<QuestionMessages>questions;
    public List<SysMessage>sysMessages;
    public List<QuestionMessages>questionMessages;

    public List<QuestionMessages>answerMessages;
    /*public SysMessage sysMessage;
    public SysMessage questionMessage;
    public SysMessage answerMessage;*/
}
