package com.wehang.ystv.logic;

import com.blankj.utilcode.util.LogUtils;
import com.tencent.TIMConversation;
import com.tencent.TIMConversationType;
import com.tencent.TIMManager;
import com.tencent.TIMMessage;
import com.tencent.TIMMessageListener;
import com.wehang.ystv.utils.UserLoginInfo;


import java.util.List;

/**
 * @author 钟林宏 2017/1/25 10:57
 * @version V1.0
 * @ProjectName: ToLiveTencentIM
 * @Copyright: 2015 WHCD Co., Ltd. All Right Reserved.
 * @address:
 * @date: 2017/1/25 10:57
 * @Description: 本内容仅限于公司内部使用，禁止转发.
 * @instructions:{类文件说明}
 * @modificationHistory=========================重大变更说明
 * @modify by user: 钟林宏 2017/1/25
 */

public class HostChatInfo implements TIMMessageListener {
    private static String mUserId;
    private TIMConversation mHostConversation;
    private HostChatUnreadMessageListener mHostChatUnreadMessageListener;

    private static class HostChatInfoHolder {
        private static HostChatInfo instance = new HostChatInfo();
    }

    public void setUnreadMessageListener(HostChatUnreadMessageListener listener) {
        mHostChatUnreadMessageListener = listener;
        TIMManager.getInstance().addMessageListener(this);
    }

    public void setHostUserId(String userId) {
        mHostConversation = TIMManager.getInstance().getConversation(TIMConversationType.C2C, userId);
    }

    public static HostChatInfo getInstance() {
        mUserId = UserLoginInfo.getUserInfo().userId;
        return HostChatInfo.HostChatInfoHolder.instance;
    }

    @Override
    public boolean onNewMessages(List<TIMMessage> list) {
     /*   LogUtils.i("onNewMessages",list.size());
        if (mHostChatUnreadMessageListener != null && mHostConversation != null) {
            mHostChatUnreadMessageListener.onHostChatUnreadMessage(mHostConversation.getUnreadMessageNum());
        }
       */
        return false;
    }

    public void onDestroy() {
        mUserId = null;
        mHostConversation = null;
        mHostChatUnreadMessageListener = null;
    }

    public interface HostChatUnreadMessageListener {
        void onHostChatUnreadMessage(long unreadMessageNum);
    }
}
