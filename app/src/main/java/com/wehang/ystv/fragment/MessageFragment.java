package com.wehang.ystv.fragment;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.Utils;
import com.google.gson.reflect.TypeToken;
import com.tencent.TIMConversation;
import com.tencent.TIMConversationType;
import com.tencent.TIMCustomElem;
import com.tencent.TIMElem;
import com.tencent.TIMElemType;
import com.tencent.TIMFriendFutureItem;
import com.tencent.TIMFriendGenderType;
import com.tencent.TIMGroupCacheInfo;
import com.tencent.TIMGroupPendencyItem;
import com.tencent.TIMManager;
import com.tencent.TIMMessage;
import com.tencent.TIMUserProfile;
import com.wehang.ystv.Constant;
import com.wehang.ystv.R;
import com.wehang.ystv.adapter.ConversationAdapter;
import com.wehang.ystv.adapter.LiveAdapter;
import com.wehang.ystv.adapter.MessageAdapter;
import com.wehang.ystv.bo.Conversation;
import com.wehang.ystv.bo.CustomMessage;
import com.wehang.ystv.bo.Lives;
import com.wehang.ystv.bo.MessageFactory;
import com.wehang.ystv.bo.NomalConversation;
import com.wehang.ystv.bo.QuestionMessages;
import com.wehang.ystv.bo.SysMessage;
import com.wehang.ystv.bo.YsMessage;
import com.wehang.ystv.interfaces.ConversationView;
import com.wehang.ystv.interfaces.FriendInfoView;
import com.wehang.ystv.interfaces.UrlConstant;
import com.wehang.ystv.interfaces.result.GetMessageResult;
import com.wehang.ystv.interfaces.result.GetSysMResult;
import com.wehang.ystv.logic.ConversationPresenter;
import com.wehang.ystv.logic.FriendshipManagerPresenter;
import com.wehang.ystv.logic.GroupManagerPresenter;
import com.wehang.ystv.logic.UsersProfilePresenter;
import com.wehang.ystv.ui.GetAnswerActivity;
import com.wehang.ystv.ui.GetQuestionActivity;
import com.wehang.ystv.ui.LiveDetails;
import com.wehang.ystv.ui.SysTemMessageActivty;
import com.wehang.ystv.utils.ToastUtil;
import com.wehang.ystv.utils.UserLoginInfo;
import com.whcd.base.interfaces.BaseData;
import com.whcd.base.interfaces.BaseResult;
import com.whcd.base.interfaces.HttpTool;
import com.whcd.base.widget.CustomProgressDialog;
import com.whcd.base.widget.PageListView;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class

MessageFragment extends Fragment implements ConversationView, PageListView.PageListViewListener {
    /*
        private PageListView listView;
        //private NearLiveAdapter adapter;
        private MessageAdapter madapter;*/
    private List<YsMessage> nearLives = new ArrayList<>();
    private int pageNo = 1;
    private int pageSize = 10;
    private View empty;

    public MessageFragment() {
        // Required empty public constructor
    }

    private FriendshipManagerPresenter managerPresenter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_message, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
    }

    @Override
    public void onRefresh() {
        initData();
    }

    @Override
    public void onLoadMore() {

    }

    private void initViews() {
        getView().findViewById(R.id.isReadTV).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i < conversationList.size(); i++) {
                    LogUtils.i("readAllMessage", 3 + "");
                    conversationList.get(i).readAllMessage();
                }
                adapter.notifyDataSetChanged();
            }
        });

        conversationListView = getView().findViewById(R.id.conversationListView);

        adapter = new ConversationAdapter(getActivity(), R.layout.item_conversation, conversationList,list);
        conversationListView.setAdapter(adapter);
        conversationListView.setPullRefreshEnable(true);
        conversationListView.setPullLoadEnable(false);
        conversationListView.setPageListViewListener(this);
        conversationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LogUtils.i("Click", "ItemClick" + position);
                // 注意这里的i是1开始的不是0；传值的穿i-1
                if (position == 1) {
                    startActivity(new Intent(getActivity(), SysTemMessageActivty.class));
                    //ToastUtil.makeText(getActivity(), "后台开发中", Toast.LENGTH_SHORT).show();
                } else if (position == 2) {
                    startActivity(new Intent(getActivity(), GetQuestionActivity.class));
                } else if (position == 3) {
                    startActivity(new Intent(getActivity(), GetAnswerActivity.class));
                } else {
                   /* //本页面需要刷新未读，重新调用方法
                    *//*timConversationList.get(position-4).setReadMessage();
                    initView(timConversationList);*//*
                    conversationList.get(position-4).readAllMessage();
                    adapter.notifyDataSetChanged();
*/
                    conversationList.get(position - 4).navToDetail(getActivity());


                }

            }
        });

        presenter = new ConversationPresenter(this);
        presenter.getConversation();
        registerForContextMenu(conversationListView);
        adapter.notifyDataSetChanged();

    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
        adapter.notifyDataSetChanged();
    }

    private void initData() {
        getSysMessage();
    }

    private PageListView conversationListView;
    private List<Conversation> conversationList = new LinkedList<>();
    private ConversationAdapter adapter;
    private ConversationPresenter presenter;
    private FriendshipManagerPresenter friendshipManagerPresenter;
    private GroupManagerPresenter groupManagerPresenter;
    private List<String> userList;
    private List<String> groupList;
    /**
     * 初始化界面或刷新界面
     *
     * @param conversationList
     */
    private List<TIMConversation> timConversationList = new ArrayList<>();

    @Override
    public void initView(List<TIMConversation> conversationList) {
        this.conversationList.clear();
        groupList = new ArrayList<>();
        userList = new ArrayList<>();
        for (TIMConversation item : conversationList) {
            switch (item.getType()) {
                case C2C:
                    userList.add(item.getPeer());
                    ///这里所有对话的信息都需要设置
                    //先通过所有对话的UID去获取用户信息再设置
                    this.conversationList.add(new NomalConversation(item));
                    break;
                case Group:
//                    this.conversationList.add(new NomalConversation(item));
//                    groupList.add(item.getPeer());
                    break;
            }
        }

        UsersProfilePresenter.getInstance().getUsersProfiles(userList, new UsersProfilePresenter.UsersProfileCallBack() {
            @Override
            public void onError() {

            }

            @Override
            public void onSuccess() {
                updateNiceName();
            }
        });


        LogUtils.i("liaotian", conversationList.size());


        //标记为已读需要
        timConversationList = conversationList;
    }


    public void updateNiceName() {

        for (Conversation conversation : conversationList) {
            TIMUserProfile mTIMUserProfile = UsersProfilePresenter.getInstance().getUsersProfile(conversation.getIdentify());
            LogUtils.i("mTIMUserProfile", conversation.getIdentify() + ";");

            if (mTIMUserProfile != null) {
                if (conversation instanceof NomalConversation) {
                    ((NomalConversation) conversation).setNickName(mTIMUserProfile.getNickName());
                    ((NomalConversation) conversation).setAvatar(mTIMUserProfile.getFaceUrl());

                    TIMFriendGenderType type = mTIMUserProfile.getGender();
                    int a = 3;
                    if (type == TIMFriendGenderType.Female) {
                        a = 0;
                    } else {
                        a = 1;
                    }
                    ((NomalConversation) conversation).sex = a;

                    String b = String.valueOf(mTIMUserProfile.getBirthday());
                    if (b.equals("")) {
                        b = "0";
                    }
                    ((NomalConversation) conversation).isvip = Integer.parseInt(b);
                    String c = mTIMUserProfile.getNickName();
                    String d = mTIMUserProfile.getFaceUrl();
                    LogUtils.i("mTIMUserProfile", a + ";" + b + ";" + c + ";" + d)
                    ;
                }
            }
        }
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * 更新最新消息显示
     *
     * @param message 最后一条消息
     */
    @Override
    public void updateMessage(TIMMessage message) {

        if (message == null) {
            adapter.notifyDataSetChanged();
            return;
        }
        if (message.getConversation().getType() == TIMConversationType.System) {
            //groupManagerPresenter.getGroupManageLastMessage();
            return;
        }
        if (MessageFactory.getMessage(message) instanceof CustomMessage) {
            return;
        }
        if (message.getConversation().getType()==TIMConversationType.C2C){
            NomalConversation conversation = new NomalConversation(message.getConversation());
            Iterator<Conversation> iterator = conversationList.iterator();
            while (iterator.hasNext()) {
                Conversation c = iterator.next();
                if (conversation.equals(c)) {
                    conversation = (NomalConversation) c;
                    iterator.remove();
                    break;
                }
            }
            conversation.setLastMessage(MessageFactory.getMessage(message));
            conversationList.add(conversation);
            Collections.sort(conversationList);
            refresh();
        }

    }

    /**
     * 更新好友关系链消息
     */
    @Override
    public void updateFriendshipMessage() {
        friendshipManagerPresenter.getFriendshipLastMessage();
    }

    /**
     * 删除会话
     *
     * @param identify
     */
    @Override
    public void removeConversation(String identify) {
        Iterator<Conversation> iterator = conversationList.iterator();
        while (iterator.hasNext()) {
            Conversation conversation = iterator.next();
            if (conversation.getIdentify() != null && conversation.getIdentify().equals(identify)) {
                iterator.remove();
                adapter.notifyDataSetChanged();
                return;
            }
        }
    }

    /**
     * 更新群信息
     *
     * @param info
     */
    @Override
    public void updateGroupInfo(TIMGroupCacheInfo info) {
        for (Conversation conversation : conversationList) {
            if (conversation.getIdentify() != null && conversation.getIdentify().equals(info.getGroupInfo().getGroupId())) {
                adapter.notifyDataSetChanged();
                return;
            }
        }
    }

    /**
     * 刷新
     */
    @Override
    public void refresh() {
        Collections.sort(conversationList);
        adapter.notifyDataSetChanged();
        //刷新首页未读消息
//            ((HomeActivity) getActivity()).setMsgUnread(getTotalUnreadNum() == 0);
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        LogUtils.i("Click", "111");
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        if (info.position > 3) {
            Conversation conversation = conversationList.get(info.position - 4);
            if (conversation instanceof NomalConversation) {
                menu.add(0, 1, Menu.NONE, getString(R.string.conversation_del));
            }
        }

    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        LogUtils.i("Click", "222");
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        NomalConversation conversation = (NomalConversation) conversationList.get(info.position - 4);
        switch (item.getItemId()) {
            case 1:
                if (conversation != null) {
                    if (presenter.delConversation(conversation.getType(), conversation.getIdentify())) {
                        conversationList.remove(conversation);
                        adapter.notifyDataSetChanged();
                    }
                }
                break;
            default:
                break;
        }
        return super.onContextItemSelected(item);
    }

    private long getTotalUnreadNum() {
        long num = 0;
        for (Conversation conversation : conversationList) {
            num += conversation.getUnreadNum();
        }
        return num;
    }
    //前3个item的数据
    private List<YsMessage>list=new ArrayList<>();
    private void getSysMessage(){
        Map<String, String> params = new HashMap<String, String>();
        params.put("token",UserLoginInfo.getUserToken());
        HttpTool.doPost(getActivity(), UrlConstant.MESSAGEINFO, params, true, new TypeToken<BaseResult<GetSysMResult>>() {
        }.getType(), new HttpTool.OnResponseListener() {
            @Override
            public void onSuccess(BaseData data) {
                conversationListView.stopRefresh();
                GetSysMResult result= (GetSysMResult) data;
                list.clear();
                if (result.sysMessages.size()>0){
                    YsMessage sysMessage=new YsMessage();
                    sysMessage.content=result.sysMessages.get(0).content;
                    if (result.sysMessages.get(0).createTime!=null){
                        sysMessage.time= com.wehang.ystv.utils.Utils.getData(result.sysMessages.get(0).createTime);
                    }
                    list.add(sysMessage);
                }else {
                    list.add(new YsMessage());
                }
                if (result.questionMessages.size()>0){
                    YsMessage sysMessage=new YsMessage();
                    sysMessage.content=result.questionMessages.get(0).questionContent;
                    if (result.questionMessages.get(0).questionTime!=null){
                        sysMessage.time= com.wehang.ystv.utils.Utils.getData(result.questionMessages.get(0).questionTime);
                    }
                    list.add(sysMessage);
                }else {
                    list.add(new YsMessage());
                }
                if (result.answerMessages.size()>0){
                    YsMessage sysMessage=new YsMessage();
                    sysMessage.content=result.answerMessages.get(0).content;
                    if (result.answerMessages.get(0).answerTime!=null){
                        sysMessage.time= com.wehang.ystv.utils.Utils.getData(result.answerMessages.get(0).answerTime);
                    }
                    list.add(sysMessage);
                }else {
                    list.add(new YsMessage());
                }
                adapter.notifyDataSetChanged();
                LogUtils.i("SysMessage",list.toString());
            }

            @Override
            public void onError(int errorCode) {
                conversationListView.stopRefresh();
            }
        });

    }


}
