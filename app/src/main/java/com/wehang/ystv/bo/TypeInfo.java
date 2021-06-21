package com.wehang.ystv.bo;

import com.whcd.base.interfaces.BaseData;

public class TypeInfo extends BaseData {
	private static final long serialVersionUID = -5797361401839862943L;
	public int msgType;// 0普通消息 1弹幕消息 2普通礼物 3特效礼物4聊天消息
	public String giftNo;
	public String giftCount;
	public String giftName;
	public String giftPic;

}
