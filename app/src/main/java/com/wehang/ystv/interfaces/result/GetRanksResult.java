package com.wehang.ystv.interfaces.result;

import com.wehang.ystv.bo.RankUser;
import com.whcd.base.interfaces.BaseData;


import java.util.List;

public class GetRanksResult extends BaseData {
	private static final long serialVersionUID = 1L;
	public List<RankUser> ranks;
	public String cccoin;
	public int onlineNum;
	public int closed;
	public int totalCccoin;
}
