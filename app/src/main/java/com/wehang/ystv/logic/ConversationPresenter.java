package com.wehang.ystv.logic;

import android.util.Log;

import com.blankj.utilcode.util.LogUtils;
import com.tencent.TIMConversation;
import com.tencent.TIMConversationType;
import com.tencent.TIMCustomElem;
import com.tencent.TIMElem;
import com.tencent.TIMElemType;
import com.tencent.TIMGroupCacheInfo;
import com.tencent.TIMManager;
import com.tencent.TIMMessage;
import com.tencent.TIMValueCallBack;
import com.wehang.ystv.event.FriendshipEvent;
import com.wehang.ystv.event.GroupEvent;
import com.wehang.ystv.event.MessageEvent;
import com.wehang.ystv.event.RefreshEvent;
import com.wehang.ystv.interfaces.ConversationView;
import com.wehang.ystv.utils.Utils;


import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * 会话界面逻辑
 */
public class ConversationPresenter implements Observer {

    private static final String TAG = "ConversationPresenter";
    private ConversationView view;

    public ConversationPresenter(ConversationView view) {
        //注册消息监听
        MessageEvent.getInstance().addObserver(this);
        //注册刷新监听
        RefreshEvent.getInstance().addObserver(this);
        //注册好友关系链监听
        FriendshipEvent.getInstance().addObserver(this);
        //注册群关系监听
        GroupEvent.getInstance().addObserver(this);
        this.view = view;
    }


    @Override
    public void update(Observable observable, Object data) {
        if (observable instanceof MessageEvent) {
            TIMMessage msg = (TIMMessage) data;
            if (view != null) {
                view.updateMessage(msg);
            }
            if (view != null) {
               /* if (msg.getConversation().getType().equals(TIMConversationType.C2C)){
                    Log.i("update",)
                    if (Utils.isC2C(msg)){

                    }
                }*/
                view.refresh();
            }
        } else if (observable instanceof FriendshipEvent) {
            FriendshipEvent.NotifyCmd cmd = (FriendshipEvent.NotifyCmd) data;
            switch (cmd.type) {
                case ADD_REQ:
                case READ_MSG:
                case ADD:
                    if (view != null) {
                        view.updateFriendshipMessage();
                    }
                    break;
            }
        } else if (observable instanceof GroupEvent) {
            GroupEvent.NotifyCmd cmd = (GroupEvent.NotifyCmd) data;
            switch (cmd.type) {
                case UPDATE:
                case ADD:
                    if (view != null) {
                        view.updateGroupInfo((TIMGroupCacheInfo) cmd.data);
                    }
                    break;
                case DEL:
                    if (view != null) {
                        view.removeConversation((String) cmd.data);
                    }
                    break;

            }
        } else if (observable instanceof RefreshEvent) {

        }
    }


    public List<TIMConversation> getConversation() {
        List<TIMConversation> list = TIMManager.getInstance().getConversionList();
        List<TIMConversation> result = new ArrayList<>();
        for (TIMConversation conversation : list) {
            if (conversation.getType() == TIMConversationType.System) continue;
            if (conversation.getType()!=TIMConversationType.C2C)continue;
            result.add(conversation);
            conversation.getMessage(1, null, new TIMValueCallBack<List<TIMMessage>>() {
                @Override
                public void onError(int i, String s) {
                    Log.e(TAG, "get message error" + s);
                }

                @Override
                public void onSuccess(List<TIMMessage> timMessages) {
                    if (timMessages.size() > 0) {
                        if (view != null) {
                            view.updateMessage(timMessages.get(0));
                        }
                    }

                }
            });

        }
        if (view != null) {
            view.initView(result);
        }
        return result;
    }

    /**
     * 删除会话
     *
     * @param type 会话类型
     * @param id   会话对象id
     */
    public boolean delConversation(TIMConversationType type, String id) {
        return TIMManager.getInstance().deleteConversationAndLocalMsgs(type, id);
    }


}
