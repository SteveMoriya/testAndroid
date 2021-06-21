package com.wehang.ystv;


import com.wehang.ystv.interfaces.UrlConstant;

public class Constant {

  /*  public static final int GIRL = 0;
    public static final int BOY = 1;
    public static final int PAGE_SIZE = 10;*/

    public static final boolean LOG = true;//log开关
    public static final String TAG = "=====";
    public static final boolean DEBUG = true; //是否为debug模式
    public static int TRANSLATE_TAG = 0;

    //图片返回的key前面需要拼接
    public static final String MATCHING = "http://orzpce18z.bkt.clouddn.com/";
  /*  public static final String WEIXIN_APP_ID = "wxfb2dfbd043f0ccb1";
    public static final String ROOT_PATH = "/america/";






    // 环信
    public static final String MESSAGE_ATTR_IS_VOICE_CALL = "is_voice_call";
    public static final String MESSAGE_ATTR_IS_VIDEO_CALL = "is_video_call";

    public static final String MESSAGE_ATTR_IS_BIG_EXPRESSION = "em_is_big_expression";
    public static final String MESSAGE_ATTR_EXPRESSION_ID = "em_expression_id";
    public static final int CHATTYPE_SINGLE = 1;
    public static final int CHATTYPE_GROUP = 2;
    public static final int CHATTYPE_CHATROOM = 3;
    public static final String EXTRA_CHAT_TYPE = "chatType";
    public static final String EXTRA_USER_ID = "userId";
    public static final String NEW_FRIENDS_USERNAME = "item_new_friends";
    public static final String GROUP_USERNAME = "item_groups";
    public static final String CHAT_ROOM = "item_chatroom";
    public static final String ACCOUNT_REMOVED = "account_removed";
    public static final String ACCOUNT_CONFLICT = "conflict";
    public static final String CHAT_ROBOT = "item_robots";
    public static final String MESSAGE_ATTR_ROBOT_MSGTYPE = "msgtype";
    public static final String ACTION_GROUP_CHANAGED = "action_group_changed";
    public static final String ACTION_CONTACT_CHANAGED = "action_contact_changed";



    /**
     * 是否返回首页
     */
    public static  boolean back_home = false;

    // 暂用环信聊天室ID
    // public final static String TO_ChAT_USERNAME = "114714059982504528";//

    // roomID
    public final static String PUSH = "publish.ccvzb.cn";
    public final static String PULL = "rtmp://hls.ccvzb.cn/ccvzb/";

/*    // 提现方式
    public final static int BANK = 0;
    public final static int ALIPAY = 1;

    public final static String ADDRESS_DETAIL = "address_detail";
    public static String ADDRESS_MEAT_INFO = "address_meta_info";*/

    //极光推送  sp
    public static  String JPUSHTBTN = "JPUSHTBTN";
    public static  String JPUSH_MSG_BTN = "JPUSH_MSG_BTN";





    public static final int GIRL = 0;
    public static final int BOY = 1;
    public static final int PAGE_SIZE = 10;
    public static final int IMSDK_APPID = 1400015598;
    public static final int IMSDK_ACCOUNT_TYPE = 7798;

    public static final String WEIXIN_APP_ID = "wx4ff4159ac940a0de";
    //主播退出广播字段
    public static final String EXIT_APP = "EXIT_APP";
    /**
     * IM 互动消息类型
     */
    public static final int IMCMD_PAILN_TEXT = 1;   // 文本消息
    public static final int IMCMD_ENTER_LIVE = 2;   // 用户加入直播
    public static final int IMCMD_EXIT_LIVE = 3;   // 用户退出直播
    public static final int IMCMD_PRAISE = 4;   // 点赞消息
    public static final int IMCMD_DANMU = 5;   // 弹幕消息
    public static final int IMCMD_ORDINARY_GIFT = 6;//普通礼物
    public static final int IMCMD_SPECIAL_GIFT = 7;//特效礼物
    public static final int IMCMD_ROOM_COLLECT_INFO = 8;//房间信息汇总（观众、金豆等等)
    public static final int IMCMD_LIVE_OVER = 9;//主播直播结束（超过时间为收到主播的推流，或者主播直播违规）
    public static final int IMCMD_QUESTION=10;//提问
    public static final int IMCMD_ZHUBOJOINROOM=11;//主播加入房间
    public static final int IMCMD_DELETE=15;//后台删除直播
    public static final int IMCMD_COLOSE=16;//后台关闭直播

    //ERROR CODE TYPE
    public static final int ERROR_GROUP_NOT_EXIT = 10010;
    public static final int ERROR_QALSDK_NOT_INIT = 6013;
    public static final int ERROR_JOIN_GROUP_ERROR = 10015;
    public static final int SERVER_NOT_RESPONSE_CREATE_ROOM = 1002;
    public static final int NO_LOGIN_CACHE = 1265;
    /**
     * 用户可见的错误提示语
     */
    public static final String ERROR_MSG_NET_DISCONNECTED = "网络异常，请检查网络";

    //直播结束
    public static final String ERROR_MSG_LIVE_OVER = "服务器长时间未收到直播流，或者直播非法，已结束直播";
    //直播端错误信息
    public static final String ERROR_MSG_CREATE_GROUP_FAILED = "创建直播房间失败,Error:";
    public static final String ERROR_MSG_GET_PUSH_URL_FAILED = "拉取直播推流地址失败,Error:";
    public static final String ERROR_MSG_OPEN_CAMERA_FAIL = "无法打开摄像头，需要摄像头权限";
    public static final String ERROR_MSG_OPEN_MIC_FAIL = "无法打开麦克风，需要麦克风权限";
    public static final String ERROR_MSG_RECORD_PERMISSION_FAIL = "无法进行录屏,需要录屏权限";
    public static final String ERROR_MSG_NO_LOGIN_CACHE = "您的帐号已在其它地方登陆";

    //播放端错误信息
    public static final String ERROR_MSG_GROUP_NOT_EXIT = "直播已结束，加入失败";
    public static final String ERROR_MSG_JOIN_GROUP_FAILED = "加入房间失败，Error:";
    public static final String ERROR_MSG_LIVE_STOPPED = "直播已结束";
    public static final String ERROR_MSG_NOT_QCLOUD_LINK = "非腾讯云链接，若要放开限制请联系腾讯云商务团队";
    public static final String ERROR_RTMP_PLAY_FAILED = "视频流播放失败，Error:";

    //连麦开关
    public static final boolean TX_ENABLE_LINK_MIC = true; //开启连麦标志位

    //连麦消息类型
    public static final int LINKMIC_CMD_REQUEST = 10001;
    public static final int LINKMIC_CMD_ACCEPT = 10002;
    public static final int LINKMIC_CMD_REJECT = 10003;
    public static final int LINKMIC_CMD_MEMBER_JOIN_NOTIFY = 10004;
    public static final int LINKMIC_CMD_MEMBER_EXIT_NOTIFY = 10005;
    public static final int LINKMIC_CMD_KICK_MEMBER = 10006;

    //连麦响应类型
    public static final int LINKMIC_RESPONSE_TYPE_ACCEPT = 1;    //主播接受连麦
    public static final int LINKMIC_RESPONSE_TYPE_REJECT = 2;    //主播拒绝连麦

    public static final String ACTIVITY_RESULT = "activity_result";

    public static final String ROOT_PATH = "/america/";
    // 环信
    public static final String MESSAGE_ATTR_IS_VOICE_CALL = "is_voice_call";
    public static final String MESSAGE_ATTR_IS_VIDEO_CALL = "is_video_call";

    public static final String MESSAGE_ATTR_IS_BIG_EXPRESSION = "em_is_big_expression";
    public static final String MESSAGE_ATTR_EXPRESSION_ID = "em_expression_id";
    public static final int CHATTYPE_SINGLE = 1;
    public static final int CHATTYPE_GROUP = 2;
    public static final int CHATTYPE_CHATROOM = 3;
    public static final String EXTRA_CHAT_TYPE = "chatType";
    public static final String EXTRA_USER_ID = "userId";
    public static final String NEW_FRIENDS_USERNAME = "item_new_friends";
    public static final String GROUP_USERNAME = "item_groups";
    public static final String CHAT_ROOM = "item_chatroom";
    public static final String ACCOUNT_REMOVED = "account_removed";
    public static final String ACCOUNT_CONFLICT = "conflict";
    public static final String CHAT_ROBOT = "item_robots";
    public static final String MESSAGE_ATTR_ROBOT_MSGTYPE = "msgtype";
    public static final String ACTION_GROUP_CHANAGED = "action_group_changed";
    public static final String ACTION_CONTACT_CHANAGED = "action_contact_changed";

    // 多处需要传递的userid参数
    public static final String USERID = "userId";
    public static final String LIVE_VIDEO_SHARD_PATH = UrlConstant.SERVICE + "live/resources/share.html";
    public static final String LOOK_LIVE_VIDEO_SHARD_PATH = UrlConstant.SERVICE + "live/resources/share.html";
    public static final String SHARE_TEXT = "来来吧，直播就来!";

    public static final String SEND_GIFT = "em_is_gift_id";

    public static final String START_COUNT_DOWN = "com.whcd.tolive.START_COUNT_DOWN";
    public static final String COUNT_DOWN = "com.whcd.tolive.COUNT_DOWN";


    // 提现方式
    public final static int BANK = 0;
    public final static int ALIPAY = 1;

    public final static String ADDRESS_DETAIL = "address_detail";
    public static String ADDRESS_MEAT_INFO = "address_meta_info";


    //更新接口返回的versionCode
    public static  int VERSIONCODE_UPGRADE = 0;
    public static  String VERSIONNAME_UPGRADE = "";



    //更新某一条问题的回答
    public static String questinId="";

    //微信支付
    public static String HavePAY="";
    //微信支付成功 极光注册
    public static String TAGWXPAY="";


    //正在直播的是不是手机推流
    public static int isPhonePush=-1;

}
