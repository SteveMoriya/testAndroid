package com.wehang.ystv.interfaces;

import java.math.RoundingMode;

public class UrlConstant {
    //192.168.3.183
//    public static final String UPLOADIP = "http://www.wehang.net/";
    public static final String UPLOADIP = "http://112.74.218.80/";
    public static final String SERVICE = "http://112.74.74.29:8080/";
    // public static final String SERVICE = "http://120.76.120.2/";
    public static final String TESTIP = "http://192.168.3.120:8080/";
    public static final String IP = SERVICE + "yslive/resources/image/";
    public static final String KJIP=SERVICE+"yslive/resources/sourcewareImages/";
    private static final String BASIC_URL = SERVICE + "yslive/appservice/basic/";
    private static final String INFO_URL = SERVICE + "yslive/appservice/info/";
    private static final String DIRCTSEED_URL = SERVICE + "yslive/appservice/live/";
    private static final String GET_URL = SERVICE + "yslive/appservice/index/";
    private static final String ROOM_URL = SERVICE + "yslive/appservice/room/";
    private static final String REDPACKETS = SERVICE + "yslive/appservice/award/";
    private static final String DYNAMIC = SERVICE + "yslive/appservice/newsfeed/";
    //是否显示直播页面
    public static final String IS_OPEN_LIVE = SERVICE + "yslive/appservice/basic/fetchIosClock";

    /**
     * 开始直播--七牛
     */
    public static final String START_LIVE_QINIU = ROOM_URL + "beginLive";
    /**
     * 预告开始直播
     */
    public static final String FORESHOWBEGINLIVE = ROOM_URL + "foreshowBeginLive";
    /**
     * 取消预播
     */
    public static final String START_LIVE_QX = ROOM_URL + "clearForeshow";
    /**
     * 禁言/取消
     */
    public static final String SLIENCED = ROOM_URL + "silenced";
    /**
     * 设置管理员/取消管理员
     */
    public static final String MANAGER = ROOM_URL + "resetManager";
    /**
     * 禁言列表
     */
    public static final String SLIENCED_LIST = ROOM_URL + "silencedList";
    /**
     * 管理员列表
     */
    public static final String MANAGER_LIST = ROOM_URL + "managerList";
    /**
     * 获取验证码
     **/
    public static final String VERIFY = BASIC_URL + "verify";
    /**
     * 验证验证码和手机号
     **/
    public static final String YZVERIFY = BASIC_URL + "verifyCode";
    /**
     * 定位信息上报
     **/
    public static final String LOCATION_INFO = BASIC_URL + "startup";

    /**
     * 手机登录
     **/
    public static final String LOGIN = BASIC_URL + "login";
    /**
     * 手机注册
     **/
    public static final String REGISTER = BASIC_URL + "register";
    /**
     * 找回密码
     **/
    public static final String RECOVERPWD = BASIC_URL + "recoverPwd";
    /**
     * 修改密码
     */
    public static final String EDITPASSWORD=INFO_URL+"editPassword";
    /**
     * 退出登录
     **/
    public static final String LOGOUT = BASIC_URL + "logout";
    /**
     * 第三方登录
     **/
    public static final String THIRDLOGIN = BASIC_URL + "thirdLogin";

    /**
     * 第三方绑定
     */
    public static final String THIRDBD = INFO_URL + "bindThird";
    /**
     * 第三方解绑
     */
    public static final String THIRDBDJC = INFO_URL + "removeThird";
    /**
     * 提交意见反馈
     **/
    public static final String SUBMITFEEDBACK = BASIC_URL + "submitFeedback";

    /**
     * 上传文件
     **/
    //public static final String UPLOADFILE = BASIC_URL + "uploadImg";
    public static final String UPLOADFILE = BASIC_URL + "uploadImg";
    /**
     * 心跳
     **/
    public static final String HEARTBEAT = BASIC_URL + "heartbeat";
    /**
     * 分享成功接口
     **/
    public static final String SHARESUCCESS = BASIC_URL + "shareSuccess";
    /**
     * 地区获取接口
     **/
    public static final String QUERYADDRESSMETA = BASIC_URL + "queryAddressMeta";
    /**
     * 版本更新接口
     **/
    public static final String QUERYVERSION = BASIC_URL + "queryVersion";

    /**
     * 获取个人信息
     **/
    public static final String MYDATA = INFO_URL + "myData";

    /**
     * 获取消息列表
     */
    public static final String SYSMESSAGE = INFO_URL + "messageList";
    /**
     * 绑定手机号码
     **/
    public static final String BINDPHONE = INFO_URL + "updatePhone";
    /**
     * 更新个人资料
     **/
    public static final String UPDATEMYDATA = INFO_URL + "updateMyData";
    /**
     * 提现记录
     **/
    public static final String DRAWRECORD = INFO_URL + "drawRecord";
    /**
     * 任性值贡献排行
     **/
    public static final String DEDICATERANK = INFO_URL + "beanRank";

    /**
     * 获取等级信息
     **/
    public static final String LEVELINFO = INFO_URL + "levelInfo";
    /**
     * 获取邀请奖励
     **/
    public static final String INVITEAWARD = INFO_URL + "inviteAward";
    /**
     * 获取录播列表
     **/
    public static final String PLAYBACK = INFO_URL + "playback";
    /**
     * 来认证
     **/
    public static final String AUTHENTICATION = INFO_URL + "submitApply";
    /**
     * 获取粉丝列表
     **/
    public static final String FANSLIST = INFO_URL + "fansList";
    /**
     * 获取关注人列表
     **/
    public static final String FOCUSLIST = INFO_URL + "focusList";
    /**
     * 根据任性ID数组获取用户信息
     **/
    public static final String USER_LIST = INFO_URL + "queryUserList";
    /**
     * 3.6获取V友列表
     **/
    public static final String VFRIENDLIST = INFO_URL + "VFriendList";
    /**
     * 获取收益信息
     **/
    public static final String ACCOUNTINFO = INFO_URL + "accountInfo";
    /**
     * 金豆兑换砖石 的兑换数据
     **/
    public static final String cccoinTODIADATA = INFO_URL + "exchangeList";
    /**
     * 映票兑换砖石
     **/
    public static final String cccoinTODIA = INFO_URL + "cccoinToDia";
    //金豆兑换钻石
    public static final String beanTODIA = INFO_URL + "exchange";
    /**
     * 人民币兑换砖石 的兑换数据
     **/
    public static final String RMBToDiaData = ROOM_URL + "chargeRatioList";
    /**
     * 人民币兑换砖石
     **/
    public static final String CURRENCYTODIA = ROOM_URL + "charge";
    /**
     * 关注或取消关注
     **/
    public static final String FOCUS = INFO_URL + "focus";
    /**
     * 提现接口
     **/
    public static final String DRAW = INFO_URL + "draw";
    /**
     * 黑名单列表接口
     **/
    public static final String DEFIRENDLIST = INFO_URL + "defirendList";
    /**
     * 实名认证接口
     **/
    public static final String VIPAPPLY = INFO_URL + "vipApply";
    /**
     * 获取认证信息
     **/
    public static final String AUTHINFO = INFO_URL + "authInfo";
    /**
     * 判断微信绑定情况
     **/
    public static final String JUDGEDWXBIND = INFO_URL + "judgedWxbind";
    //diamondRecord钻石收支记录
    public static final String ZSJL = INFO_URL + "diamondRecord";
    /**
     * 绑定微信号
     **/
    public static final String BINDWX = INFO_URL + "bindwx";
    /**
     * 公众号提现
     **/
    public static final String WXDRAW = INFO_URL + "wxDraw";

    /**
     * 搜索
     **/
    public static final String SEARCH = GET_URL + "search";
    /**
     * 最新直播列表
     **/
    public static final String NEWLIVE = DIRCTSEED_URL + "tencentNewLive";
    /**
     * 最热直播列表
     **/
    public static final String HEATLIVES = DIRCTSEED_URL + "tencentHeatLives";
    /**
     * 关注直播列表
     **/
    public static final String FOCUSLIVE = DIRCTSEED_URL + "tencentFocusLive";
    /**
     * 附近直播
     */
    public static final String NEAR_LIVE = DIRCTSEED_URL + "nearbyLives";
    //正在直播列表
    public static final String YSLIVE = GET_URL + "liveList";
    //视频
    public static final String YSVEDIO = GET_URL + "videoList";
    //关注的人的动态
    public static final String FOCUSUSERLIST=GET_URL+"focusUserList";

    //预告列表
    public static final String PRELIVE = GET_URL + "foreshowList";
    //历史记录列表
    public static final String HISLIVE = GET_URL + "historyList";

    //banner图
    public static final String BANNER = GET_URL + "bannerList";

    /**
     * 频道列表
     **/
    public static final String CHANNELLIST = DIRCTSEED_URL + "channelList";
    /**
     * 直播频道列表
     **/
    public static final String CHANNELLIVELIST = DIRCTSEED_URL + "tencentChannelLiveList";

    /**
     * 拉黑、解除拉黑
     **/
    public static final String DEFIREND = ROOM_URL + "defirend";
    /**
     * 查看别人信息
     **/
    public static final String OTHERDATA = ROOM_URL + "otherData";
    /**
     * 查看别人主页
     **/
    public static final String OTHERHOME = ROOM_URL + "otherHome";
    /**
     * 消费
     **/
    public static final String CONSUME = ROOM_URL + "consume";
    /**
     * 获取直播间信息
     **/
    public static final String FRESHEN = ROOM_URL + "roomInfo";


    /**
     * 检查直播状态
     */
    public static final String CHECKLIVE = ROOM_URL + "checkLive";
    /**
     * 举报
     **/
    public static final String REPORT = ROOM_URL + "report";
    /**
     * 进入直播间
     **/
    public static final String ENTERROOM = ROOM_URL + "enterRoom";

    /**
     *
     */
    public static final String WATCHHIS = ROOM_URL + "enterRoom";
    /** 创建直播室 **/
    // public static final String BEGINLIVE = ROOM_URL + "beginLive";
    /**
     * 结束直播
     **/
    public static final String ENDLIVE = ROOM_URL + "endLive";
    /**
     * 离开直播室
     **/
    public static final String OUTROOM = ROOM_URL + "outRoom";
    /**
     * 删除视频
     **/
    public static final String DELVIDEO = ROOM_URL + "delVideo";
    /**
     * 礼物列表
     **/
    public static final String GIFTLIST = ROOM_URL + "giftList";
    // 推送直播消息
    public static final String PUSHLIVEMSG = ROOM_URL + "pushLiveMsg";
    //
    public static final String CONNECTMICROPHONE = ROOM_URL + "connectMicrophone";

    // 红包明细
    public static final String REDPACKETRECORD = REDPACKETS + "redpacketRecord";
    // 已发红包
    public static final String REDPACKAGELIST = REDPACKETS + "redpacketList";
    // 奖品信息
    public static final String AWARDINFO = REDPACKETS + "awardInfo";
    // 随机奖品和红包
    public static final String RANDOMAWARD = REDPACKETS + "randomAward";
    // 红包详情
    public static final String REDPACKETINFO = REDPACKETS + "redpacketInfo";
    // 发红包
    public static final String PUTREDPACKET = REDPACKETS + "putRedpacket";
    // 地址列表
    public static final String ADDRESSLIST = REDPACKETS + "addressList";
    // 申请奖品
    public static final String AWARDAPPLY = REDPACKETS + "awardApply";
    // 保存地址
    public static final String SAVEADDRESS = REDPACKETS + "saveAddress";
    // 保存地址
    public static final String REMOVEADDRESS = REDPACKETS + "removeAddress";
    // 领红包
    public static final String RECEIVEREDPACKET = REDPACKETS + "receiveRedpacket";

    // 动态列表
    public static final String NEWSFEEDLIST = DYNAMIC + "newsfeedList";
    // 发布动态
    public static final String PUBLISHNEWSFEED = DYNAMIC + "publishNewsfeed";
    // 评论列表
    public static final String COMMENTLIST = DYNAMIC + "commentList";
    // 评论列表
    public static final String NEWSFEEDCOMMENT = DYNAMIC + "newsfeedComment";
    // 删除动态
    public static final String DELETENEWSFEED = DYNAMIC + "deleteNewsfeed";
    // 删除动态
    public static final String NEWSFEEDPRAISE = DYNAMIC + "newsfeedPraise";
    // 删除评论
    public static final String DELETECOMMENT = DYNAMIC + "deleteComment";


    public static final String GZLIVE = GET_URL + "focusLiveList";

    //用户直播列表
    public static final String USERLive = INFO_URL + "liveList";
    //用户视频

    public static final String USERVEDIO = INFO_URL + "videoList";

    //用户收藏collectionList
    public static final String USERCOLLECTION = INFO_URL + "collectionList";
    //收藏和取消收藏

    public static final String COLLECT = ROOM_URL+ "collect";


    //浏览记录
    public static final String USERWATCHHISTORY = INFO_URL + "viewRecord";
    /**
     * 查看用户主页
     **/
    public static final String USERHOME = INFO_URL + "userInfo";

    /**
     * 购买直播
     */
    public static final String BUTLIVE = GET_URL + "buyLive";
    /**
     * 直播购买记录
     */
    public static final String BUTLIVEHIS = INFO_URL + "buyRecord";
    /**
     * 观看录播
     */
    public static final String WATCHHISVD = ROOM_URL + "watchVideo";


    //区域接口
    public static final String QUERYADDR = BASIC_URL + "queryAddr";

    //1.14	医院列表接口
    public static final String QUERYHOSPITAL = BASIC_URL + "queryHospital";

    //1.15	科室列表接口
    public static final String QUERYDEPARtTMENT = BASIC_URL + "queryDepartment";
    //queryTitle1.16	职称列表接口
    public static final String QUERYTITLE = BASIC_URL + "queryTitle";
    //分类接口
    public static final String FENLEI = BASIC_URL + "queryClassification";
    //课程接口
    public static final String  QUSERYCOURSEWARE=ROOM_URL+"queryCourseware";
    //课程详情
    public static final String  SOURCEDETAIL=ROOM_URL+"sourceDetail";
    //开启直播
    public static final String CREATELIVE = ROOM_URL + "createLive";

    //编辑直播
    public static final String EDITLIVE = INFO_URL + "editLive";
    //编辑视频
    public static final String EDITVIDEO = INFO_URL + "editVideo";
    //删除直播
    public static final String DELETEVIDEO = INFO_URL + "deleteVideo";
    //删除视频
    public static final String DELETELIVE = INFO_URL + "deleteLive";

    //充值借口
    public static final String RECHARGE = INFO_URL + "recharge";
    //支付
    //充值借口
    public static final String PAYORDER = INFO_URL + "payOrder";

    public static String headPic = "";


    /**
     * 连麦推流地址获取
     */
    public static final String GET_LINE_MIC_PUSH_URL = ROOM_URL + "connectMicrophone";


    //举手
    public static final String HANDUP = ROOM_URL + "handUp";


    //获取是否有未结束直播
    public static final String GETSOURCEINFO = ROOM_URL + "getSourceInfo";

    //增加浏览记录
    public static final String ADDVIEWRECORD=INFO_URL+"addViewRecord";

    //获取问题列表
    public static final String  SOURCEQUSETONS=ROOM_URL+"sourceQuestions";
    //获取问题列表
    public static final String  SUBMITQUSETION=ROOM_URL+"submitQuestion";


    //获取已经报名的人员
    public static final String  ENTEREDDUSERS=ROOM_URL+"enteredUsers";

    //获取提问列表
    public static final String  QUSETIONLIST=INFO_URL+"questionList";

    //取消订单
    public static final String CANCELORDER=INFO_URL+"cancelOrder";

    /**
     * 交易记录
     */
    public static final String TRANSACTIONRECORD=INFO_URL+"transactionRecord";
    /**
     * 升级会员
     */
    public static final String UPVIP=INFO_URL+"upVip";
    /**
     * 删除问题
     */
    public static final String DELETEQUESTION=INFO_URL+"deleteQuestion";
    /**
     * 获取消息菜单的前3个信息
     */
    public static final String MESSAGEINFO=INFO_URL+"messageInfo";
    /**
     * 申请退款
     */
    public static final String REFUND=INFO_URL+"refund";
    /**
     * 订单详情
     */
    public static final String BUYRECORDDETAIL=INFO_URL+"buyRecordDetail";
    /**
     * 清空记录
     */
    public static final String DELETEVIEW=INFO_URL+"deleteView";

    /**
     * 回答提问
     */
    public static final String ANSWERQUESTION=ROOM_URL+"answerQuestion";
    /**
     * 获取连麦列表
     */
    public static final String CONNECTApplyList=ROOM_URL+"connectApplyList";
    /**
     * 结束连麦
     */
    public static final String CLOSUREMICROPHONE=ROOM_URL+"closureMicrophone";
    /**
     * 会员价格
     */
    public static final String CONFIGURE=BASIC_URL+"configure";
    /**
     * 退款理由
     */
    public static final String REFUNDREASON=BASIC_URL+"refundReason";

    /**
     * 后台混流
     */
    public static final String SERVERMIXEDFLOW=ROOM_URL+"serverMixedFlow";
    /**
     * 获取是否拉黑
     */
    public static final String ISDEFRIEND=INFO_URL+"yslive/appservice/info/isDefriend";
    /**
     * 拉黑操作
     */
    public static final String DEFRIEND=INFO_URL+"yslive/appservice/info/defriend";
}
