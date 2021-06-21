package com.wehang.ystv.event;


import android.util.Log;

import com.blankj.utilcode.util.LogUtils;
import com.tencent.TIMConversation;
import com.tencent.TIMCustomElem;
import com.tencent.TIMElem;
import com.tencent.TIMElemType;
import com.tencent.TIMManager;
import com.tencent.TIMMessage;
import com.tencent.TIMMessageListener;
import com.wehang.ystv.Constant;
import com.wehang.ystv.bo.Conversation;
import com.wehang.ystv.utils.UserLoginInfo;
import com.wehang.ystv.utils.Utils;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;

/**
 * 消息通知事件，上层界面可以订阅此事件
 */
public class MessageEvent extends Observable implements TIMMessageListener {


    private volatile static MessageEvent instance;

    private MessageEvent(){
        //注册消息监听器
        TIMManager.getInstance().addMessageListener(this);
    }

    public static MessageEvent getInstance(){
        if (instance == null) {
            synchronized (MessageEvent.class) {
                if (instance == null) {
                    instance = new MessageEvent();
                }
            }
        }
        return instance;
    }

    /**
     * 主动通知新消息
     */
    @Override
    public boolean onNewMessages(List<TIMMessage> list) {
        LogUtils.i("onNewMessagesQ","1;"+list.size());
        /*Utils.isC2C(list.get(0));*/
        for (TIMMessage item:list){
            setChanged();
            notifyObservers(item);
        }
        return false;
    }

    /**
     * 主动通知新消息
     */
    public void onNewMessage(TIMMessage message){
       /* LogUtils.i("onNewMessagesQ","3;");
        if (Utils.isC2C(message)){

        }*/
        /*Utils.isC2C(message);*/
        LogUtils.i("onNewMessagesQ","4");
        setChanged();
        notifyObservers(message);

    }
/*    *//**
     * 主动通知新消息
     *//*
    public void onNewMessageC2C(TIMMessage message){
        LogUtils.i("onNewMessagesQ","3;");
        if (Utils.isC2C(message)){
            LogUtils.i("onNewMessagesQ","4");
            setChanged();
            notifyObservers(message);
        }

    }*/

    /**
     * 清理消息监听
     */
    public void clear(){
        instance = null;
    }
}
