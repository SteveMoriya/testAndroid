package com.wehang.ystv.interfaces.result;

import com.wehang.ystv.bo.UserInfo;
import com.whcd.base.interfaces.BaseData;


import java.util.List;

public class GetFocusesResult extends BaseData {

	private static final long serialVersionUID = 1L;
	public List<UserInfo> focuses;
	public List<UserInfo>users;
	public int total;
}
