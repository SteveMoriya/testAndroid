package com.wehang.ystv.logic;

import android.os.Build;
import android.util.Log;

import com.blankj.utilcode.util.LogUtils;
import com.tencent.TIMConversation;
import com.tencent.TIMConversationType;
import com.tencent.TIMCustomElem;
import com.tencent.TIMElem;
import com.tencent.TIMElemType;
import com.tencent.TIMManager;
import com.tencent.TIMMessage;
import com.tencent.TIMMessageListener;
import com.tencent.TIMValueCallBack;
import com.wehang.ystv.Constant;
import com.wehang.ystv.bo.UserInfo;
import com.wehang.ystv.utils.UserLoginInfo;


import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.List;

public class TCLinkMicMgr implements TIMMessageListener {
    public static final String TAG = TCLinkMicMgr.class.getSimpleName();
    private String mChatRoomId;

    public void setmChatRoomId(String mChatRoomId) {
        this.mChatRoomId = mChatRoomId;
    }

    private TCLinkMicListener mLinkMicListener;

    private TCLinkMicMgr() {
        TIMManager.getInstance().addMessageListener(this);
    }

    private static class TCLinkMicMgrHolder {
        private static TCLinkMicMgr instance = new TCLinkMicMgr();
    }

    public static TCLinkMicMgr getInstance() {
        return TCLinkMicMgrHolder.instance;
    }

    public static boolean supportLinkMic() {
        return Build.VERSION.SDK_INT >= 18;
    }

    public void setLinkMicListener(TCLinkMicListener listener) {
        mLinkMicListener = listener;
    }

    public void sendLinkMicRequest(String strToUserId) {
        sendMessage(strToUserId, Constant.LINKMIC_CMD_REQUEST, "");
    }

    public void sendLinkMicResponse(String strToUserId, int responseType, String param) {
        try {
            int cmd = -1;
            JSONObject json = new JSONObject();
            switch (responseType) {
                case Constant.LINKMIC_RESPONSE_TYPE_ACCEPT:
                    cmd = Constant.LINKMIC_CMD_ACCEPT;
                    json.put("sessionID", param);
                    break;

                case Constant.LINKMIC_RESPONSE_TYPE_REJECT:
                    cmd = Constant.LINKMIC_CMD_REJECT;
                    json.put("reason", param);
                    break;

                default:
                    break;
            }

            if (cmd != -1) {
                sendMessage(strToUserId, cmd, json.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMemberJoinNotify(String strToUserId, String strJoinerID, String playUrl) {
        try {
            JSONObject json = new JSONObject();
            json.put("joinerID", strJoinerID);
            json.put("playUrl", playUrl);
            sendMessage(strToUserId, Constant.LINKMIC_CMD_MEMBER_JOIN_NOTIFY, json.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMemberExitNotify(String strToUserId, String strExiterID) {
        try {
            JSONObject json = new JSONObject();
            json.put("exiterID", strExiterID);
            sendMessage(strToUserId, Constant.LINKMIC_CMD_MEMBER_EXIT_NOTIFY, json.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void kickOutLinkMicMember(String strToUserId) {
        sendMessage(strToUserId, Constant.LINKMIC_CMD_KICK_MEMBER, "");
    }

    private void sendMessage(final String strUserId, final int cmd, String param) {
        sendMessage(strUserId, cmd, param, new TIMValueCallBack<TIMMessage>() {
            @Override
            public void onError(int i, String s) {
                Log.d(TAG, "sendMessage failed, cmd = " + cmd + " toUserId = " + strUserId);
            }

            @Override
            public void onSuccess(TIMMessage timMessage) {
                Log.d(TAG, "sendMessage success, cmd = " + cmd + " toUserId = " + strUserId);
            }
        });
    }

    private void sendMessage(String strUserId, int cmd, String param, TIMValueCallBack<TIMMessage> timValueCallBack) {
        JSONObject sendJson = new JSONObject();
        try {
            UserInfo userInfo = UserLoginInfo.getUserInfo();
            sendJson.put("userAction", cmd);
            sendJson.put("userId", userInfo.userId);
            sendJson.put("nickName", userInfo.name);
            sendJson.put("toWho",strUserId);
            sendJson.put("param", param);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        TIMCustomElem elem = new TIMCustomElem();
        elem.setData(sendJson.toString().getBytes());

        TIMMessage msg = new TIMMessage();
        msg.addElement(elem);

        sendTIMMessage(strUserId, msg, timValueCallBack);
    }

    /**
     * ????????????
     *
     * @param msg              TIM??????
     * @param timValueCallBack ?????????????????????
     */
    private void sendTIMMessage(String strUserId, TIMMessage msg, TIMValueCallBack<TIMMessage> timValueCallBack) {


        TIMConversation conversion = TIMManager.getInstance().getConversation(TIMConversationType.Group, mChatRoomId);
        if (conversion != null) {
            conversion.sendMessage(msg, timValueCallBack);
        } else {
            Log.e(TAG, "TIMManager GetConversation failed");
        }
    }

    @Override
    public boolean onNewMessages(List<TIMMessage> list) {
        LogUtils.i("onNewMessages",list.size());
        if (mLinkMicListener == null) {
            return false;
        }
        for (int i = list.size() - 1; i >= 0; i--) {
            TIMMessage currMsg = list.get(i);
            for (int j = 0; j < currMsg.getElementCount(); j++) {
                if (currMsg.getElement(j) == null) {
                    continue;
                }

                TIMElem elem = currMsg.getElement(j);
                TIMElemType type = elem.getType();
                String sendId = currMsg.getSender();

                if (sendId.equals(UserLoginInfo.getUserInfo().userId)) {
                    Log.d(TAG, "recevie a self-msg type:" + type.name());
                    continue;
                }

                if (type != TIMElemType.Custom) {
                    continue;
                }

                try {
                    byte[] bytes = ((TIMCustomElem) elem).getData();
                    String jsonString = new String(bytes);
                    Log.i(TAG, "receive linkmic message: " + jsonString);

                    JSONTokener jsonParser = new JSONTokener(jsonString);
                    JSONObject json = (JSONObject) jsonParser.nextValue();
                    int action = (int) json.get("userAction");
                    String userId = (String) json.get("userId");
                    String nickname = (String) json.get("nickName");
                    String param = (String) json.opt("param");
                    String toWho=json.getString("toWho");
                    if (!toWho.equals(UserLoginInfo.getUserInfo().userId)){
                        return false;
                    }

                    if (Constant.LINKMIC_CMD_REQUEST == action) {
                        if (supportLinkMic()) {
                            mLinkMicListener.onReceiveLinkMicRequest(userId, nickname);
                        } else {
                            sendLinkMicResponse(userId, Constant.LINKMIC_RESPONSE_TYPE_REJECT, "????????????????????????");
                        }
                    } else if (Constant.LINKMIC_CMD_ACCEPT == action) {
                        String strSessionID = "";
                        if (param != null && param.length() > 0) {
                            JSONTokener tokener = new JSONTokener(param);
                            JSONObject obj = (JSONObject) tokener.nextValue();
                            strSessionID = obj.getString("sessionID");
                        }
                        if (strSessionID != null && strSessionID.length() > 0) {
                            mLinkMicListener.onReceiveLinkMicResponse(userId, Constant.LINKMIC_RESPONSE_TYPE_ACCEPT, strSessionID);
                        } else {
                            Log.e(TAG, "recvc linkmic accept response, invalid sessionID");
                        }
                    } else if (Constant.LINKMIC_CMD_REJECT == action) {
                      /*  String reason = "";
                        if (param != null && param.length() > 0) {
                            JSONTokener tokener = new JSONTokener(param);
                            JSONObject obj = (JSONObject) tokener.nextValue();
                            reason = obj.getString("reason");
                        }*/
                        mLinkMicListener.onReceiveLinkMicResponse(userId, Constant.LINKMIC_RESPONSE_TYPE_REJECT, "??????????????????");
                    } else if (Constant.LINKMIC_CMD_MEMBER_JOIN_NOTIFY == action) {
                        String strJoinerID = "";
                        String strPlayUrl = "";
                        if (param != null && param.length() > 0) {
                            JSONTokener tokener = new JSONTokener(param);
                            JSONObject obj = (JSONObject) tokener.nextValue();
                            strJoinerID = obj.getString("joinerID");
                            strPlayUrl = obj.getString("playUrl");
                        }
                        if (strJoinerID.length() > 0 && strPlayUrl.length() > 0) {
                            mLinkMicListener.onReceiveMemberJoinNotify(strJoinerID, strPlayUrl);
                        }
                    } else if (Constant.LINKMIC_CMD_MEMBER_EXIT_NOTIFY == action) {
                        String strExiterID = "";
                        if (param != null && param.length() > 0) {
                            JSONTokener tokener = new JSONTokener(param);
                            JSONObject obj = (JSONObject) tokener.nextValue();
                            strExiterID = obj.getString("exiterID");
                        }
                        if (strExiterID.length() > 0) {
                            mLinkMicListener.onReceiveMemberExitNotify(strExiterID);
                        }
                    } else if (Constant.LINKMIC_CMD_KICK_MEMBER == action) {
                        mLinkMicListener.onReceiveKickedOutNotify();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    public String getStreamIDByStreamUrl(String strStreamUrl) {
        if (strStreamUrl == null || strStreamUrl.length() == 0) {
            return null;
        }

        strStreamUrl = strStreamUrl.toLowerCase();

        //?????????????????????rtmp://8888.livepush.myqcloud.com/live/8888_test_12345_test?txSecret=aaaa&txTime=bbbb
        //?????????????????????rtmp://8888.liveplay.myqcloud.com/live/8888_test_12345_test
        //            http://8888.liveplay.myqcloud.com/live/8888_test_12345_test.flv
        //            http://8888.liveplay.myqcloud.com/live/8888_test_12345_test.m3u8

        String strLive = "/live/";
        int index = strStreamUrl.indexOf(strLive);
        if (index == -1) {
            return null;
        }

        String strSubString = strStreamUrl.substring(index + strLive.length());
        String[] strArrays = strSubString.split("[?.]");
        if (strArrays.length > 0) {
            return strArrays[0];
        }

        return null;
    }

    public interface TCLinkMicListener {

        /**
         * ??????????????????
         *
         * @param strUserId
         * @param strNickName
         */
        void onReceiveLinkMicRequest(final String strUserId, final String strNickName);

        /**
         * ??????????????????
         *
         * @param strUserId
         * @param responseType
         * @param strParams
         */
        void onReceiveLinkMicResponse(final String strUserId, final int responseType, final String strParams);

        /**
         * ????????????????????????????????????
         */
        void onReceiveMemberJoinNotify(final String strUserId, final String strPlayUrl);

        /**
         * ?????????????????????????????????
         */
        void onReceiveMemberExitNotify(final String strUserId);

        /**
         * ?????????????????????
         */
        void onReceiveKickedOutNotify();
    }
}
