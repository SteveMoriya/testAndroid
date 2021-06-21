package com.wehang.ystv.bo;

import com.whcd.base.interfaces.BaseData;

import java.util.List;

/**
 * 作者： GUOYOU WU
 * 日期： 2017/6/23
 * 邮箱：296824952@qq.com
 */

public class UserInfo extends BaseData {
	private static final long serialVersionUID = 1830871016704581271L;

	public String isRobot;
	public String phoneInfo;
	public String password;
	public String version;
	public String totalBean;


	public String userSig;
	public boolean isChosed;// 判断是否选中
	public String token;
	public String userId;
	public String chatRoomId;// 聊天室id
	public String iconUrl;
	public String name;
	public String userName;
	public String conferenceId;//连麦信息
	public int sex=0;
	public String phone;
	public int focusNum;
	public int fansNum;
	public String underwrite;
	public int liveNum;
	public int videoNum;
	public int collectionNum;
	public int balance;

	public int level;
	public int exp;// 经验值
	public int acceptStatus;// 状态 0-未认证 1-已认证 2-认证中
	public int status;// 状态 0-正常 1-停用
	public int drawType;// 提现类型 0-官方 1-自定义
	public int drawRate;
	public int robot;
	public int isVip=0;// 是否是vip 0-否 1-是
	public String vipTime;
	public int award;// 是否领取奖励 0-未 1-是
	public int cccoin;// 收入cccoin
	public String createTime;
	public String loginTime;// 上次登录时间
	public String godenBeen;// 收益任性值
	public int diamond;// 钻石余额
	public int sendDiamond;// 送出的钻石
	public String inviteUserId;// 邀请人ID
	public String inviteUserName;
	public int isFocus;// 是否关注 0-否 1-是
	public String dedicate;// 贡献的任性值
	public String openID;
	public String thirdType;
	public String thirdId;
	public String thirdName;
	public String bindPhone;
	public String address;
	public int totalcccoin;// 总收入任性值(统计，只增无减)
	public String user_id;
	public int isBlack;
	public UserNo1 NO1;
	public List<UserInfo> contributes;
	public int money;
	public boolean isOpen = false;
	public int send_diamond;// 送出的钻石，用户首页用到
	public int newsfeedNum;
	public String inviteNotice;
	public List<String> newsfeedPics;
	public int totalCccoin;
	public int isSilenced;//是否被禁言
//	public int isManager;//是否是管理员
	public int qualificStatus;
	public int isNewMsg;
	public String WX;
	public String QQ;
	public List<String>ranks;

    public int bean=0;//金豆收益
	public String code;
	public String province;
	public String city;
	public String district;
	public String hospital;
	public String department;
	public String title;

	public String authentication;//认证状态
	/**
	 * watchNum : 3213
	 * isCollect : 0
	 * pullRtmpUrlUrl : trmp://...
	 * pullHlsUrlUrl : ....
	 * videoUrl : ....
	 * coursewareId : ["....","....","...."]
	 */

	public int watchNum;
	public int isCollect;
	public String pullRtmpUrlUrl;
	public String pullHlsUrlUrl;
	public String videoUrl;
	public String coursewareId;
	public String districtId;

	//用户主页
	public List<Lives>tracks;
	public List<Lives> tag;


	@Override
	public String toString() {
		return "UserInfo{" +
				"password='" + password + '\'' +
				", userSig='" + userSig + '\'' +
				", token='" + token + '\'' +
				", userId='" + userId + '\'' +
				", name='" + name + '\'' +
				", userName='" + userName + '\'' +
				", sex=" + sex +
				", phone='" + phone + '\'' +
				", isVip=" + isVip +
				"province='" + province + '\'' +
				", city='" + city + '\'' +
				", district='" + district + '\'' +
				", balance='" + balance + '\'' +
				", hospital='" + hospital + '\'' +
				", department='" + department + '\'' +
				'}';
	}
}

