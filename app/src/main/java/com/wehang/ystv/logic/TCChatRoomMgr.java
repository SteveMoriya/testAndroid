package com.wehang.ystv.logic;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.blankj.utilcode.util.LogUtils;
import com.tencent.TIMCallBack;
import com.tencent.TIMConversation;
import com.tencent.TIMConversationType;
import com.tencent.TIMCustomElem;
import com.tencent.TIMElem;
import com.tencent.TIMElemType;
import com.tencent.TIMFaceElem;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMGroupManager;
import com.tencent.TIMGroupMemberInfo;
import com.tencent.TIMGroupSystemElem;
import com.tencent.TIMGroupSystemElemType;
import com.tencent.TIMManager;
import com.tencent.TIMMessage;
import com.tencent.TIMMessageListener;
import com.tencent.TIMTextElem;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;
import com.tencent.rtmp.TXLog;
import com.wehang.ystv.Constant;
import com.wehang.ystv.VideoApplication;
import com.wehang.ystv.bo.MessageFactory;
import com.wehang.ystv.bo.TextMessage;
import com.wehang.ystv.bo.UserInfo;
import com.wehang.ystv.component.emojicon.CenterImageSpan;
import com.wehang.ystv.event.MessageEvent;
import com.wehang.ystv.interfaces.UrlConstant;
import com.wehang.ystv.utils.CommonUtil;
import com.wehang.ystv.utils.GlideUtils;
import com.wehang.ystv.utils.UserLoginInfo;
import com.wehang.ystv.utils.Utils;


import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

import static com.tencent.TIMElemType.Text;
import static com.wehang.ystv.Constant.IMCMD_ENTER_LIVE;

/**
 * Created by teckjiang on 2016/8/4
 */
public class TCChatRoomMgr implements TIMMessageListener {

    public static final String TAG = TCChatRoomMgr.class.getSimpleName();
    private TIMConversation mGroupConversation;
    private TCChatRoomListener mTCChatRoomListener;
    private String mRoomId;
    private static String mUserId;

    private static TCFrequeControl mLikeFreque;
    private static TCFrequeControl mMsgFreque;

    private TCChatRoomMgr() {
        mLikeFreque = new TCFrequeControl();
        mLikeFreque.init(10, 1);

        mMsgFreque = new TCFrequeControl();
        mMsgFreque.init(20, 1);
    }

    private static class TCChatRoomMgrHolder {
        private static TCChatRoomMgr instance = new TCChatRoomMgr();
    }

    public static TCChatRoomMgr getInstance() {
        mUserId = UserLoginInfo.getUserInfo().userId;
        return TCChatRoomMgrHolder.instance;
    }

    public void sendPraiseMessage(String praiseNo) {
        sendMessage(Constant.IMCMD_PRAISE, praiseNo, "");
    }

    public void sendDanmuMessage(String msg) {
        sendMessage(Constant.IMCMD_DANMU, "0", msg);
    }

    public void sendOrdinaryGiftMessage(String giftCount, String giftName, String giftPic, String giftNo, String msg) {
        sendMessage(Constant.IMCMD_ORDINARY_GIFT, giftCount, giftName, giftPic, giftNo, "0", msg);
    }

    public void sendSpecialGiftMessage(String giftCount, String giftName, String giftPic, String giftNo, String msg) {
        sendMessage(Constant.IMCMD_SPECIAL_GIFT, giftCount, giftName, giftPic, giftNo, "0", msg);
    }

    public void sendRoomCollectInfoMessage(String msg) {
        sendMessage(Constant.IMCMD_ROOM_COLLECT_INFO, "0", msg);
    }
    public void sendJoinRoom(String msg){
        sendMessage(Constant.IMCMD_ENTER_LIVE, "0", msg);
    }
    public void sendTextMessage(String msg) {
        sendMessage(Constant.IMCMD_PAILN_TEXT, "0", msg);
    }

    public void sendQusetionMessage(){
        sendMessage(Constant.IMCMD_QUESTION, "0","");
    }
    public void sendQusetionMessage(String msg){
        sendMessage(Constant.IMCMD_QUESTION, "0",msg);
    }
    public void sendZhuBoJoinRoom(String msg){sendMessage(Constant.IMCMD_ZHUBOJOINROOM, "0",msg);}
    public void sendOnlne(String msg){
        sendMessage(Constant.IMCMD_ROOM_COLLECT_INFO, "0",msg);
    }
    public void sendLiveOver(String msg){
        sendMessage(Constant.IMCMD_LIVE_OVER, "0",msg);
    }
    /**
     * 发送消息
     *
     * @param cmd   控制符（代表不同的消息类型）具体查看Contant.IMCMD开头变量
     * @param param 参数
     */
    private void sendMessage(int cmd, String praiseNo, String param) {

        sendMessage(cmd, praiseNo, param, new TIMValueCallBack<TIMMessage>() {
            @Override
            public void onError(int i, String s) {
                TXLog.d("send cmd ", "error:" + s);
                if (null != mTCChatRoomListener)
                    mTCChatRoomListener.onSendMsgCallback(-1, null);
            }

            @Override
            public void onSuccess(TIMMessage timMessage) {
                if (null != mTCChatRoomListener)
                    mTCChatRoomListener.onSendMsgCallback(0, timMessage);
            }
        });
    }

    /**
     * @param cmd       控制符（代表不同的消息类型）具体查看Contant.IMCMD开头变量
     * @param giftCount 礼物数量
     * @param giftName  礼物名称
     * @param giftPic   礼物图片
     * @param giftNo    礼物编号
     * @param content   礼物内容
     */
    private void sendMessage(int cmd, String giftCount, String giftName, String giftPic, String giftNo, String praiseNo, String content) {

        sendMessage(cmd, giftCount, giftName, giftPic, giftNo, praiseNo, content, new TIMValueCallBack<TIMMessage>() {
            @Override
            public void onError(int i, String s) {
                TXLog.d("send cmd ", "error:" + s);
                if (null != mTCChatRoomListener)
                    mTCChatRoomListener.onSendMsgCallback(-1, null);
            }

            @Override
            public void onSuccess(TIMMessage timMessage) {
                if (null != mTCChatRoomListener)
                    mTCChatRoomListener.onSendMsgCallback(0, timMessage);
            }
        });
    }
    /**
     * 发送消息
     *同步上下2个聊天室的消息类型
     * @param message 发送的消息
     */
    public void sendMessage(final TIMMessage message) {
        this.mGroupConversation.sendMessage(message, new TIMValueCallBack<TIMMessage>() {
            @Override
            public void onError(int code, String desc) {//发送消息失败
                //错误码code和错误描述desc，可用于定位请求失败原因
                //错误码code含义请参见错误码表
                LogUtils.i("sendMessage",code+";"+desc);
                //view.onSendMessageFail(code, desc, message);
            }

            @Override
            public void onSuccess(TIMMessage msg) {
                //发送消息成功,消息状态已在sdk中修改，此时只需更新界面
                LogUtils.i("sendMessage","111");
                MessageEvent.getInstance().onNewMessage(null);

            }
        });
        //message对象为发送中状态
        MessageEvent.getInstance().onNewMessage(message);
    }
    /**
     * 观看者退出退出群组接口
     *
     * @param roomId 群组Id
     */
    public void quitGroup(final String roomId) {

        sendMessage(Constant.IMCMD_EXIT_LIVE, "0", "");

        TIMGroupManager.getInstance().quitGroup(roomId, new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
                Log.d(TAG, "quitGroup failed, code:" + i + ",msg:" + s);
//                    if(null != mTCChatRoomListener) {
//                        mTCChatRoomListener.onQuitGroupCallback(i, s);
//                    }
                mTCChatRoomListener = null;
                mGroupConversation = null;
                //TIMManager.getInstance().deleteConversation(TIMConversationType.Group, roomId);
            }

            @Override
            public void onSuccess() {
                Log.d(TAG, "quitGroup success, groupid:" + roomId);
                mTCChatRoomListener = null;
                mGroupConversation = null;
            }
        });
    }

    /**
     * 主播创建直播群组接口
     */
    public void createGroup() {
        //在特殊情况下未接收到kick out消息下会导致创建群组失败，在登录前做监测
        checkLoginState(new TCLoginMgr.TCLoginCallback() {
            @Override
            public void onSuccess() {
                TCLoginMgr.getInstance().removeTCLoginCallback();
                //用户登录，创建直播间
                TIMGroupManager.getInstance().createAVChatroomGroup("TVShow", new TIMValueCallBack<String>() {
                    @Override
                    public void onError(int code, String msg) {
                        Log.d(TAG, "create av group failed. code: " + code + " errmsg: " + msg);

                        if (null != mTCChatRoomListener)
                            mTCChatRoomListener.onJoinGroupCallback(true, code, msg);
                    }

                    @Override
                    public void onSuccess(String roomId) {
                        Log.d(TAG, "create av group succ, groupId:" + roomId);
                        mRoomId = roomId;
                        mGroupConversation = TIMManager.getInstance().getConversation(TIMConversationType.Group, roomId);
                        if (null != mTCChatRoomListener)
                            mTCChatRoomListener.onJoinGroupCallback(true, 0, roomId);
                    }
                });
            }

            @Override
            public void onFailure(int code, String msg) {
                TCLoginMgr.getInstance().removeTCLoginCallback();
                if (null != mTCChatRoomListener)
                    mTCChatRoomListener.onJoinGroupCallback(true, Constant.NO_LOGIN_CACHE, "no login cache, user has been kicked out");
            }
        });

    }

    /**
     * 登录态检测
     * 在进行createRoom/joinGroup操作时检测用户是否处于登录态，若不处于登录态执行login操作
     *
     * @param tcLoginCallback 登录回调
     */
    private void checkLoginState(TCLoginMgr.TCLoginCallback tcLoginCallback) {

        TCLoginMgr tcLoginMgr = TCLoginMgr.getInstance();
        if (TextUtils.isEmpty(TIMManager.getInstance().getLoginUser())) {
            tcLoginMgr.setTCLoginCallback(tcLoginCallback);
            tcLoginMgr.checkCacheAndLogin();
        } else {
            //已经处于登录态直接进行回调
            if (null != tcLoginCallback)
                tcLoginCallback.onSuccess();
        }
    }


    /**
     * 主播端退出前删除直播群组
     */
    public void deleteGroup() {

        sendMessage(Constant.IMCMD_EXIT_LIVE, "0", "");

        if (mRoomId == null)
            return;

        TIMManager.getInstance().deleteConversation(TIMConversationType.Group, mRoomId);
        TIMGroupManager.getInstance().deleteGroup(mRoomId, new TIMCallBack() {
            @Override
            public void onError(int code, String msg) {
                Log.d(TAG, "delete av group failed. code: " + code + " errmsg: " + msg);
//                if (null != mTCChatRoomListener)
//                    mTCChatRoomListener.onQuitGroupCallback(code, msg);
                mRoomId = null;
                mTCChatRoomListener = null;
            }

            @Override
            public void onSuccess() {
                Log.d(TAG, "delete av group succ. groupid: " + mRoomId);
//                if (null != mTCChatRoomListener)
//                    mTCChatRoomListener.onQuitGroupCallback(0, mRoomId);
                mRoomId = null;
                mTCChatRoomListener = null;
            }
        });
    }
    //加入群组后获取聊天室人数信息
    public void getNumber(String groupId){
        //获取群组成员信息
        TIMGroupManager.getInstance().getGroupMembers(
                groupId, //群组Id
                cb);
    }
    //创建回调
    TIMValueCallBack<List<TIMGroupMemberInfo>> cb = new TIMValueCallBack<List<TIMGroupMemberInfo>> () {
        @Override
        public void onError(int code, String desc) {
        }

        @Override
        public void onSuccess(List<TIMGroupMemberInfo> infoList) {//参数返回群组成员信息

            /*for(TIMGroupMemberInfo info : infoList) {
                Log.d(tag, "user: " + info.getUser() +
                        "join time: " + info.getJoinTime() +
                        "role: " + info.getRole());
            }*/
            LogUtils.i("qiuqiuMessage",infoList.size());
        }
    };
    /**
     * 加入群组
     *
     * @param isHost 是否为主播，true为主播，false为观众
     * @param roomId 群组ID
     */
    public void joinGroup(final boolean isHost, final String roomId) {
        //在特殊情况下未接收到kick out消息下会导致创建群组失败，在登录前做监测
        checkLoginState(new TCLoginMgr.TCLoginCallback() {
            @Override
            public void onSuccess() {
                if (!isHost) {
                    TCLoginMgr.getInstance().removeTCLoginCallback();
                    //用户登录，加入房间
                    mRoomId = roomId;
                    TIMGroupManager.getInstance().applyJoinGroup(roomId, "", new TIMCallBack() {
                        @Override
                        public void onError(int i, String s) {
                            if (i == 10013) {
                                Log.d(TAG, "joingroup success, groupid:" + roomId);
                                mGroupConversation = TIMManager.getInstance().getConversation(TIMConversationType.Group, roomId);

                                if (null != mTCChatRoomListener) {
                                    mTCChatRoomListener.onJoinGroupCallback(isHost, 0, roomId);
                                }

                            } else {
                                mRoomId = null;
                                Log.d(TAG, "joingroup failed, code:" + i + ",msg:" + s);
                                Log.d(TAG, "mPlayerListener not init");
                            }

                        }

                        @Override
                        public void onSuccess() {
                            Log.d(TAG, "joingroup success, groupid:" + roomId);
                            mGroupConversation = TIMManager.getInstance().getConversation(TIMConversationType.Group, roomId);

                            if (null != mTCChatRoomListener) {
                                mTCChatRoomListener.onJoinGroupCallback(isHost, 0, roomId);
                            } else {
                                Log.d(TAG, "mPlayerListener not init");
                            }
                        }
                    });
                } else {

                    TCLoginMgr.getInstance().removeTCLoginCallback();
                    //用户登录，加入房间
                    mRoomId = roomId;
                    TIMGroupManager.getInstance().applyJoinGroup(roomId, "", new TIMCallBack() {
                        @Override
                        public void onError(int i, String s) {
                            if (i == 10013) {
                                Log.d(TAG, "joingroup success, groupid:" + roomId);
                                mGroupConversation = TIMManager.getInstance().getConversation(TIMConversationType.Group, roomId);

                                if (null != mTCChatRoomListener) {
                                    mTCChatRoomListener.onJoinGroupCallback(true, 0, roomId);
                                }

                            } else {
                                mRoomId = null;
                                Log.d(TAG, "joingroup failed, code:" + i + ",msg:" + s);
                                Log.d(TAG, "mPlayerListener not init");
                            }

                        }

                        @Override
                        public void onSuccess() {
                            Log.d(TAG, "joingroup success, groupid:" + roomId);
                            mGroupConversation = TIMManager.getInstance().getConversation(TIMConversationType.Group, roomId);

                            if (null != mTCChatRoomListener) {
                                mTCChatRoomListener.onJoinGroupCallback(true, 0, roomId);
                            }
                        }
                    });

                }
            }


            @Override
            public void onFailure(int code, String msg) {
                Log.d(TAG, "relogin fail. code: " + code + " errmsg: " + msg);
                TCLoginMgr.getInstance().removeTCLoginCallback();
                if (null != mTCChatRoomListener)
                    mTCChatRoomListener.onJoinGroupCallback(isHost, Constant.NO_LOGIN_CACHE, "no login cache, user has been kicked out");

            }
        });

    }

    /**
     * IMSDK消息回调接口
     *
     * @param list 消息列表
     * @return 默认情况下所有消息监听器都将按添加顺序被回调一次
     * 除非用户在OnNewMessages回调中返回true
     * 此时将不再继续回调下一个消息监听器，此处默认返回false
     */
    @Override
    public boolean onNewMessages(List<TIMMessage> list) {
        LogUtils.i("onNewMessages",list.size());
        List<TIMMessage>listGroup=new ArrayList<>();
        for (int i=0;i<list.size();i++){
            if (list.get(i).getConversation().getType()==TIMConversationType.C2C){

            }else {
               listGroup.add(list.get(i));
            }
        }

        parseIMMessage(listGroup);
        return false;
    }


    /**
     * 注入消息回调监听类
     * 需要实现并注入TCChatRoomListener才能获取相应消息回调
     *
     * @param listener
     */
    public void setMessageListener(TCChatRoomListener listener) {
        mTCChatRoomListener = listener;
        TIMManager.getInstance().addMessageListener(this);
    }

    /**
     * 解析TIM消息列表
     *
     * @param list 消息列表
     */
    private void parseIMMessage(List<TIMMessage> list) {
        if (mTCChatRoomListener != null) {
            mTCChatRoomListener.onRefresh();
        }
        if (list.size() > 0) {
            if (mGroupConversation != null)
                if (mGroupConversation.getType()==TIMConversationType.Group){
                    mGroupConversation.setReadMessage(list.get(0));
                    Log.d(TAG, "parseIMMessage readMessage " + list.get(0).timestamp());
                }
        }
        for (int i = list.size() - 1; i >= 0; i--) {
            TIMMessage currMsg = list.get(i);

            boolean isTolk=false;
            for (int b = 0; b < currMsg.getElementCount(); b++){
                if (currMsg.getElement(b) == null)
                    continue;
                TIMElem elem = currMsg.getElement(b);
                TIMElemType type = elem.getType();
                if (type==TIMElemType.Face||type==TIMElemType.Text){
                    isTolk=true;
                    boolean hasText = false;
                    List<TIMElem> elems = new ArrayList<>();
                    for (int c = 0; c < currMsg.getElementCount(); ++c) {
                        elems.add(currMsg.getElement(c));
                        if (currMsg.getElement(c).getType() == Text) {
                            hasText = true;
                        }
                    }
                    final SpannableStringBuilder stringBuilder = getString(elems, VideoApplication.applicationContext);
                    if (!hasText) {
                        stringBuilder.insert(0, " ");
                    }
                    LogUtils.i("stringBuilder",stringBuilder+"");
                    String userId= currMsg.getSenderGroupMemberProfile().getUser();
                    // TIMManager.getInstance().initFriendshipSettings(11111111,null);
                    //待获取用户资料的用户列表
                    List<String> users = new ArrayList<String>();
                    users.add(userId);
                    //获取用户资料
                    TIMFriendshipManager.getInstance().getUsersProfile(users, new TIMValueCallBack<List<TIMUserProfile>>(){
                        @Override
                        public void onError(int code, String desc){
                            //错误码code和错误描述desc，可用于定位请求失败原因
                            //错误码code列表请参见错误码表
                            LogUtils.e("getUsersProfile", "getUsersProfile failed: " + code + " desc");
                        }

                        @Override
                        public void onSuccess(List<TIMUserProfile> result){
                            LogUtils.e("getUsersProfile", "getUsersProfile succ");
                            for(TIMUserProfile res : result){
                                LogUtils.e("getUsersProfile", "identifier: " + res.getIdentifier() + " nickName: " + res.getNickName()
                                        + " remark: " + res.getRemark());
                                String b = String.valueOf(res.getBirthday());
                                if (b.equals("")) {
                                    b = "0";
                                }
                                int intVip= Integer.parseInt(b);
                                String  userId = res.getIdentifier();
                                String   nickname = res.getNickName();
                                String  headPic = res.getFaceUrl();
                                int  userIsVip = intVip;
                                if (null != mTCChatRoomListener) {
                                    mTCChatRoomListener.onReceiveTextMsg(Constant.IMCMD_PAILN_TEXT, new ChatRoomInfo(Constant.IMCMD_PAILN_TEXT, stringBuilder, userId, nickname, headPic, 0, userIsVip, "", "", "", "", ""), "");
                                }
                            }
                        }
                    });

                    break;
                }
            }
            if (isTolk){
                continue;
            }

            for (int j = 0; j < currMsg.getElementCount(); j++) {
                if (currMsg.getElement(j) == null)
                    continue;
                TIMElem elem = currMsg.getElement(j);
                TIMElemType type = elem.getType();


                String sendId = currMsg.getSender();
                TIMUserProfile timUserProfile = currMsg.getSenderProfile();

                if (sendId.equals(mUserId)) {
                    TXLog.d(TAG, "recevie a self-msg type:" + type.name());
                    continue;
                }


                //系统消息
                if (type == TIMElemType.GroupSystem) {
                    if (TIMGroupSystemElemType.TIM_GROUP_SYSTEM_DELETE_GROUP_TYPE == ((TIMGroupSystemElem) elem).getSubtype()) {
                        //群被解散
                        if (null != mTCChatRoomListener)
                            mTCChatRoomListener.onGroupDelete();
                    }

                }


                //其他群消息过滤

                if (currMsg.getConversation() != null && currMsg.getConversation().getPeer() != null) {
                    //if (!CurLiveInfo.getChatRoomId().equals(currMsg.getConversation().getPeer())) {
                    //      continue;
                    //  }

                    // 最后处理文本消息
                    if (type == TIMElemType.Text) {
                          handleTextMsg(elem, timUserProfile);
                        }

                    //定制消息
                    if (type == TIMElemType.Custom) {
                        handleCustomTextMsg(elem, timUserProfile);
                    }
                }

            }
        }
    }

    /**
     * 退出房间
     */
    public void removeMsgListener() {
        mTCChatRoomListener = null;
        TIMManager.getInstance().removeMessageListener(this);
        TIMManager.getInstance().deleteConversation(TIMConversationType.Group, mRoomId);
    }

    /**
     * 发送消息
     *
     * @param msg              TIM消息
     * @param timValueCallBack 发送消息回调类
     */
    private void sendTIMMessage(TIMMessage msg, TIMValueCallBack<TIMMessage> timValueCallBack) {
        if (mGroupConversation != null)
            mGroupConversation.sendMessage(msg, timValueCallBack);
    }

    private void sendMessage(int cmd, String praiseNo, String content, TIMValueCallBack<TIMMessage> timValueCallBack) {

        JSONObject sendJson = new JSONObject();
        try {
            UserInfo userInfo = UserLoginInfo.getUserInfo();
            LogUtils.i("sendmessage",userInfo.name);
            sendJson.put("userAction", cmd);
            sendJson.put("userId", userInfo.userId);
            sendJson.put("nickName", userInfo.name);
            sendJson.put("level",userInfo.level);
            if (TextUtils.isEmpty(userInfo.iconUrl)) {
                sendJson.put("headPic", "");
            } else {
                sendJson.put("headPic", userInfo.iconUrl);
            }

            sendJson.put("isVip", userInfo.isVip);
            sendJson.put("praiseNo", praiseNo);
            sendJson.put("msg", content);
            if (mTCChatRoomListener != null) {
                mTCChatRoomListener.onReceiveMsg(cmd, new ChatRoomInfo(cmd, content, userInfo.userId, userInfo.name, userInfo.iconUrl, userInfo.level, userInfo.isVip, "", "", "", "", praiseNo), content);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }//sendJson.toString()



        String cmds = sendJson.toString();

        if (mGroupConversation != null)
            Log.i(TAG, "send cmd : " + cmd + "|" + cmds + "|" + mGroupConversation.toString());

        TIMMessage msg = new TIMMessage();

//        if (cmd == Constant.IMCMD_PAILN_TEXT) {

        TIMCustomElem elem = new TIMCustomElem();
        elem.setData(cmds.getBytes());

        if (msg.addElement(elem) != 0) {
            Log.d(TAG, "addElement failed");
            return;
        }

        sendTIMMessage(msg, timValueCallBack);

    }

    private void sendMessage(int cmd, final String giftCount, final String giftName, final String giftPic, final String giftNo, final String praiseNo, final String content, TIMValueCallBack<TIMMessage> timValueCallBack) {

        JSONObject sendJson = new JSONObject();
        try {
            UserInfo userInfo = UserLoginInfo.getUserInfo();
            sendJson.put("userAction", cmd);
            sendJson.put("userId", userInfo.userId);
            sendJson.put("nickName", userInfo.name);
            if (TextUtils.isEmpty(userInfo.iconUrl)) {
                sendJson.put("headPic", "");
            } else {
                sendJson.put("headPic", userInfo.iconUrl);
            }
            userInfo.level=1;
            sendJson.put("level", userInfo.level);
            sendJson.put("isVip", userInfo.isVip);
            sendJson.put("msg", content);
            sendJson.put("praiseNo", praiseNo);
            //以下为礼物相关参数
            sendJson.put("giftCount", giftCount);
            sendJson.put("giftName", giftName);
            sendJson.put("giftPic", giftPic);
            sendJson.put("giftNo", giftNo);
            if (mTCChatRoomListener != null) {
                mTCChatRoomListener.onReceiveMsg(cmd, new ChatRoomInfo(cmd, content, userInfo.userId, userInfo.name, userInfo.iconUrl, userInfo.level, userInfo.isVip, giftCount, giftName, giftPic, giftNo, praiseNo), content);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        String cmds = sendJson.toString();

        if (mGroupConversation != null)
            Log.i(TAG, "send cmd : " + cmd + "|" + cmds + "|" + mGroupConversation.toString());

        TIMMessage msg = new TIMMessage();

//        if (cmd == Constant.IMCMD_PAILN_TEXT) {

        TIMCustomElem elem = new TIMCustomElem();
        elem.setData(cmds.getBytes());

        if (msg.addElement(elem) != 0) {
            Log.d(TAG, "addElement failed");
            return;
        }

        sendTIMMessage(msg, timValueCallBack);

    }

    /**
     * 处理定制消息 赞 加入 退出 弹幕 并执行相关回调
     *
     * @param elem 消息体
     */
    private void handleCustomTextMsg(TIMElem elem, TIMUserProfile timUserProfile) {
        try {

            //String customText = new String(((TIMCustomElem) elem).getData(), "UTF-8");

            if (elem.getType() != TIMElemType.Custom)
                return;
            byte[] bytes = ((TIMCustomElem) elem).getData();
            String jsonString = new String(bytes);
            Log.i(TAG, "cumstom msg  " + jsonString);

            JSONTokener jsonParser = new JSONTokener(jsonString);
            JSONObject json = (JSONObject) jsonParser.nextValue();
            int action;
            String userId = "";
            String nickname = "";
            String headPic = "";
            String praiseNo = "";
            int userLevel = 1;
            int userIsVip = 0;
            action = (int) json.get("userAction");
            switch (action) {
                case Constant.IMCMD_PRAISE:
                    userId = (String) json.get("userId");
                    nickname = (String) json.get("nickName");
                    nickname = TextUtils.isEmpty(nickname) ? userId : nickname;
                    headPic = (String) json.get("headPic");
                    userIsVip = (int) json.opt("isVip");
                    if (null != mTCChatRoomListener) {
                        mTCChatRoomListener.onReceiveMsg(action, new ChatRoomInfo(action, "", userId, nickname, headPic, 0, 0, "", "", "", "", praiseNo), null);
                    }
                    break;
                case IMCMD_ENTER_LIVE:
                    userId = (String) json.get("userId");
                    nickname = (String) json.get("nickName");
                    nickname = TextUtils.isEmpty(nickname) ? userId : nickname;
                    headPic = (String) json.opt("headPic");
                    userIsVip = (int) json.get("isVip");
                    if (null != mTCChatRoomListener) {
                        String msg = (String) json.get("msg");
                        mTCChatRoomListener.onReceiveMsg(action, new ChatRoomInfo(action, msg, userId, nickname, headPic, 0, 0, "", "", "", "", praiseNo), null);
                    }
                    break;
                case Constant.IMCMD_EXIT_LIVE:

                    break;
                case Constant.IMCMD_PAILN_TEXT:
                case Constant.IMCMD_DANMU:
                case Constant.IMCMD_ROOM_COLLECT_INFO:
                    userId = (String) json.get("userId");
                    nickname = (String) json.get("nickName");
                    nickname = TextUtils.isEmpty(nickname) ? userId : nickname;
                    headPic = (String) json.get("headPic");
                    userIsVip = (int) json.opt("isVip");
                    if (null != mTCChatRoomListener) {
                        String msg = (String) json.get("msg");
                        mTCChatRoomListener.onReceiveMsg(action, new ChatRoomInfo(action, msg, userId, nickname, headPic, userLevel, userIsVip, "", "", "", "", praiseNo), msg);
                    }
                    break;
                case Constant.IMCMD_ORDINARY_GIFT:
                case Constant.IMCMD_SPECIAL_GIFT:
                    userId = (String) json.get("userId");
                    nickname = (String) json.get("nickName");
                    nickname = TextUtils.isEmpty(nickname) ? userId : nickname;
                    headPic = (String) json.get("headPic");
                    userIsVip = (int) json.get("isVip");
                    if (null != mTCChatRoomListener) {
                        String giftCount = (String) json.get("giftCount");
                        String giftName = (String) json.get("giftName");
                        String giftPic = (String) json.get("giftPic");
                        String giftNo = (String) json.get("giftNo");
                        String msg = (String) json.get("msg");
                        mTCChatRoomListener.onReceiveMsg(action, new ChatRoomInfo(action, msg, userId, nickname, headPic, userLevel, userIsVip, giftCount, giftName, giftPic, giftNo, praiseNo), msg);
                    }
                    break;
                case Constant.IMCMD_LIVE_OVER:
                    if (null!=mTCChatRoomListener){
                        mTCChatRoomListener.onReceiveMsg(action, new ChatRoomInfo(action, "", userId, nickname, headPic, userLevel, userIsVip, "", "", "", "", praiseNo), "");
                    }
                    break;

                case Constant.IMCMD_QUESTION:
                    userId = (String) json.get("userId");
                    nickname = (String) json.get("nickName");
                    nickname = TextUtils.isEmpty(nickname) ? userId : nickname;
                    headPic = (String) json.get("headPic");
                    userIsVip = (int) json.opt("isVip");
                    if (null != mTCChatRoomListener) {
                        String msg = (String) json.get("msg");
                        mTCChatRoomListener.onReceiveMsg(action, new ChatRoomInfo(action, msg, userId, nickname, headPic, userLevel, userIsVip, "", "", "", "", praiseNo), msg);
                    }
                    break;
                case Constant.IMCMD_ZHUBOJOINROOM:
                    if (null != mTCChatRoomListener) {
                        String msg = (String) json.get("msg");
                        mTCChatRoomListener.onReceiveMsg(action, null, msg);
                    }
                    break;

                case Constant.IMCMD_DELETE:
                    if (null != mTCChatRoomListener) {
                        mTCChatRoomListener.onReceiveMsg(action, null, "");
                    }
                    break;
                case Constant.IMCMD_COLOSE:
                    if (null != mTCChatRoomListener) {
                        mTCChatRoomListener.onReceiveMsg(action, null, "");
                    }
                default:
                    break;
            }

        }
//        catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
        catch (ClassCastException e) {
            String senderId = timUserProfile.getIdentifier();
            String nickname = timUserProfile.getNickName();
            String headPic = timUserProfile.getFaceUrl();
            nickname = TextUtils.isEmpty(nickname) ? senderId : nickname;
            Log.e("IOS数据", new String(((TIMCustomElem) elem).getData()));
            mTCChatRoomListener.onReceiveMsg(Constant.IMCMD_PAILN_TEXT,
                    new ChatRoomInfo(Constant.IMCMD_PAILN_TEXT, ((TIMCustomElem) elem).getData().toString(), senderId, nickname, headPic, 0, 0, "", "", "", "", "0"), ((TIMCustomElem) elem).getData().toString());
        } catch (JSONException e) {
            e.printStackTrace();
            // 异常处理代码
        }
    }
    /**
     * 处理定制消息 赞 加入 退出 弹幕 并执行相关回调
     *
     * @param elem 消息体
     */
    private void handleTextMsg(TIMElem elem, TIMUserProfile timUserProfile) {
        try {

            //String customText = new String(((TIMCustomElem) elem).getData(), "UTF-8");

            if (elem.getType() != TIMElemType.Text)
                return;
            byte[] bytes = ((TIMCustomElem) elem).getData();
            String jsonString = new String(bytes);
            Log.i(TAG, "cumstom msg  " + jsonString);

            JSONTokener jsonParser = new JSONTokener(jsonString);
            JSONObject json = (JSONObject) jsonParser.nextValue();
            int action;
            String userId = "";
            String nickname = "";
            String headPic = "";
            String praiseNo = "";
            int userLevel = 1;
            int userIsVip = 0;
            action = (int) json.get("userAction");
            switch (action) {
                case Constant.IMCMD_PRAISE:
                    userId = (String) json.get("userId");
                    nickname = (String) json.get("nickName");
                    nickname = TextUtils.isEmpty(nickname) ? userId : nickname;
                    headPic = (String) json.get("headPic");
                    userIsVip = (int) json.opt("isVip");
                    if (null != mTCChatRoomListener) {
                        mTCChatRoomListener.onReceiveMsg(action, new ChatRoomInfo(action, "", userId, nickname, headPic, 0, 0, "", "", "", "", praiseNo), null);
                    }
                    break;
                case IMCMD_ENTER_LIVE:
                    userId = (String) json.get("userId");
                    nickname = (String) json.get("nickName");
                    nickname = TextUtils.isEmpty(nickname) ? userId : nickname;
                    headPic = (String) json.opt("headPic");
                    userIsVip = (int) json.get("isVip");
                    if (null != mTCChatRoomListener) {
                        String msg = (String) json.get("msg");
                        mTCChatRoomListener.onReceiveMsg(action, new ChatRoomInfo(action, msg, userId, nickname, headPic, 0, 0, "", "", "", "", praiseNo), null);
                    }
                    break;
                case Constant.IMCMD_EXIT_LIVE:

                    break;
                case Constant.IMCMD_PAILN_TEXT:
                case Constant.IMCMD_DANMU:
                case Constant.IMCMD_ROOM_COLLECT_INFO:
                    userId = (String) json.get("userId");
                    nickname = (String) json.get("nickName");
                    nickname = TextUtils.isEmpty(nickname) ? userId : nickname;
                    headPic = (String) json.get("headPic");
                    userIsVip = (int) json.opt("isVip");
                    if (null != mTCChatRoomListener) {
                        String msg = (String) json.get("msg");
                        mTCChatRoomListener.onReceiveMsg(action, new ChatRoomInfo(action, msg, userId, nickname, headPic, userLevel, userIsVip, "", "", "", "", praiseNo), msg);
                    }
                    break;
                case Constant.IMCMD_ORDINARY_GIFT:
                case Constant.IMCMD_SPECIAL_GIFT:
                    userId = (String) json.get("userId");
                    nickname = (String) json.get("nickName");
                    nickname = TextUtils.isEmpty(nickname) ? userId : nickname;
                    headPic = (String) json.get("headPic");
                    userIsVip = (int) json.get("isVip");
                    if (null != mTCChatRoomListener) {
                        String giftCount = (String) json.get("giftCount");
                        String giftName = (String) json.get("giftName");
                        String giftPic = (String) json.get("giftPic");
                        String giftNo = (String) json.get("giftNo");
                        String msg = (String) json.get("msg");
                        mTCChatRoomListener.onReceiveMsg(action, new ChatRoomInfo(action, msg, userId, nickname, headPic, userLevel, userIsVip, giftCount, giftName, giftPic, giftNo, praiseNo), msg);
                    }
                    break;
                case Constant.IMCMD_LIVE_OVER:
                    mTCChatRoomListener.onReceiveMsg(action, new ChatRoomInfo(action, "", userId, nickname, headPic, userLevel, userIsVip, "", "", "", "", praiseNo), "");
                    break;

                case Constant.IMCMD_QUESTION:
                    userId = (String) json.get("userId");
                    nickname = (String) json.get("nickName");
                    nickname = TextUtils.isEmpty(nickname) ? userId : nickname;
                    headPic = (String) json.get("headPic");
                    userIsVip = (int) json.opt("isVip");
                    if (null != mTCChatRoomListener) {
                        String msg = (String) json.get("msg");
                        mTCChatRoomListener.onReceiveMsg(action, new ChatRoomInfo(action, msg, userId, nickname, headPic, userLevel, userIsVip, "", "", "", "", praiseNo), msg);
                    }
                    break;
                case Constant.IMCMD_ZHUBOJOINROOM:
                    if (null != mTCChatRoomListener) {
                        String msg = (String) json.get("msg");
                        mTCChatRoomListener.onReceiveMsg(action, null, msg);
                    }
                    break;
                default:
                    break;
            }

        }
//        catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
        catch (ClassCastException e) {
            String senderId = timUserProfile.getIdentifier();
            String nickname = timUserProfile.getNickName();
            String headPic = timUserProfile.getFaceUrl();
            nickname = TextUtils.isEmpty(nickname) ? senderId : nickname;
            Log.e("IOS数据", new String(((TIMCustomElem) elem).getData()));
            mTCChatRoomListener.onReceiveMsg(Constant.IMCMD_PAILN_TEXT,
                    new ChatRoomInfo(Constant.IMCMD_PAILN_TEXT, ((TIMCustomElem) elem).getData().toString(), senderId, nickname, headPic, 0, 0, "", "", "", "", "0"), ((TIMCustomElem) elem).getData().toString());
        } catch (JSONException e) {
            e.printStackTrace();
            // 异常处理代码
        }
    }
    /**
     * 消息循环监听类
     */
    public interface TCChatRoomListener {

        /**
         * 加入群组回调
         *
         * @param isHost 是否为主播，主播为true,false为观众
         * @param code   错误码，成功时返回0，失败时返回相应错误码
         * @param msg    返回信息，成功时返回群组Id，失败时返回相应错误信息
         */
        void onJoinGroupCallback(boolean isHost, int code, String msg);

        //void onGetGroupMembersList(int code, List<TIMUserProfile> result);

        /**
         * 发送消息结果回调
         *
         * @param code       错误码，成功时返回0，失败时返回相应错误码
         * @param timMessage 发送的TIM消息
         */
        void onSendMsgCallback(int code, TIMMessage timMessage);

        /**
         * 接受消息监听接口
         * 文本消息回调
         *
         * @param type         消息类型
         * @param chatRoomInfo 发送者信息
         * @param content      内容
         */
        void onReceiveMsg(int type, ChatRoomInfo chatRoomInfo, String content);

        /**
         *  @param type
         * @param chatRoomInfo
         * @param content
         */
        void onReceiveTextMsg(int type, ChatRoomInfo chatRoomInfo, String content);
        /**
         * 群组删除回调，在主播群组解散时被调用
         */
        void onGroupDelete();

        void onRefresh();
    }



    public static SpannableStringBuilder getString(List<TIMElem> elems, Context context) {
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder();
        for (int i = 0; i < elems.size(); ++i) {
            switch (elems.get(i).getType()) {
                case Face:
                    TIMFaceElem faceElem = (TIMFaceElem) elems.get(i);
                    int startIndex = stringBuilder.length();
                    String resName;
                    int index = faceElem.getIndex() + 1;
                    if (index < 10) {
                        resName = "f00" + index;
                    } else if (index < 100) {
                        resName = "f0" + index;
                    } else {
                        resName = "f" + index;
                    }
                    Bitmap bitmap = Utils.drawableToBitamp(CommonUtil.getResourceId(resName));
                    Matrix matrix = new Matrix();
                    int width = bitmap.getWidth();
                    int height = bitmap.getHeight();
                    matrix.postScale(1, 1);
                    Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                            width, height, matrix, true);
                    CenterImageSpan span = new CenterImageSpan(context, resizedBitmap);
                    stringBuilder.append(String.valueOf(faceElem.getIndex()));
                    stringBuilder.setSpan(span, startIndex, startIndex + getNumLength(faceElem.getIndex()), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    break;
                case Text:
                    TIMTextElem textElem = (TIMTextElem) elems.get(i);
                    stringBuilder.append(textElem.getText());
                    break;
            }

        }
        LogUtils.i("stringBuilder",stringBuilder+"");
        return stringBuilder;
    }
    private static int getNumLength(int n) {
        return String.valueOf(n).length();
    }
}
