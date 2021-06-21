package com.wehang.ystv.interfaces.result;

import com.wehang.ystv.bo.LiveNeed;
import com.wehang.ystv.bo.Lives;
import com.wehang.ystv.bo.QuestionMessages;
import com.wehang.ystv.bo.UserInfo;
import com.whcd.base.interfaces.BaseData;


import java.util.List;

/**
 * Created by lenovo on 2017/6/21.
 */

public class GetLvesResult extends BaseData {
    private static final long serialVersionUID = 1L;
    public List<Lives> lives;
    public List<Lives> videos;
    public List<Lives> collections;
    public List<Lives> views;
    public List<Lives>focusUsers;
    public Lives source;
    public List<Lives>sources;
    public List<UserInfo>users;
}
