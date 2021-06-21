package com.wehang.ystv.fragment;

/**
 * Created by lenovo on 2017/7/31.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.LinkAddress;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.CheckableImageButton;
import android.support.v4.view.ViewPager;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.LogUtils;

import com.google.gson.reflect.TypeToken;
import com.tencent.TIMConversation;
import com.tencent.TIMConversationType;
import com.tencent.TIMCustomElem;
import com.tencent.TIMElem;
import com.tencent.TIMElemType;
import com.tencent.TIMFriendGenderType;
import com.tencent.TIMGroupCacheInfo;
import com.tencent.TIMManager;
import com.tencent.TIMMessage;
import com.tencent.TIMTextElem;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;
import com.wehang.txlibrary.widget.myview.LiveToolsPopupwindow;
import com.wehang.txlibrary.widget.myview.LookShardToolsPopupwindow;
import com.wehang.txlibrary.widget.myview.shareBean;
import com.wehang.ystv.R;
import com.wehang.ystv.adapter.ConversationDialogAdapter;
import com.wehang.ystv.adapter.ImgPagerAdapter;
import com.wehang.ystv.adapter.LinkAdapter;
import com.wehang.ystv.adapter.LiveQusetiondapter;
import com.wehang.ystv.adapter.LiveVideoChartFragmentAdapter;
import com.wehang.ystv.bo.Conversation;
import com.wehang.ystv.bo.CustomMessage;
import com.wehang.ystv.bo.MessageFactory;
import com.wehang.ystv.bo.NomalConversation;
import com.wehang.ystv.bo.TextMessage;
import com.wehang.ystv.bo.UserInfo;
import com.wehang.ystv.bo.YsMessage;
import com.wehang.ystv.dbutils.DBHelper;
import com.wehang.ystv.dbutils.DownloadDao;
import com.wehang.ystv.dbutils.KejianBean;
import com.wehang.ystv.interfaces.ConversationView;
import com.wehang.ystv.interfaces.UrlConstant;
import com.wehang.ystv.interfaces.result.GetFocusesResult;
import com.wehang.ystv.interfaces.result.GetLvesResult;
import com.wehang.ystv.interfaces.result.GetSysMResult;
import com.wehang.ystv.logic.ChatRoomInfo;
import com.wehang.ystv.logic.ConversationPresenter;
import com.wehang.ystv.logic.FriendshipManagerPresenter;
import com.wehang.ystv.logic.GroupManagerPresenter;
import com.wehang.ystv.logic.UsersProfilePresenter;
import com.wehang.ystv.ui.LivePushActivity;
import com.wehang.ystv.ui.LiveWatchNewActivity;
import com.wehang.ystv.ui.SysTemMessageActivty;
import com.wehang.ystv.ui.UserHomeActivty;
import com.wehang.ystv.utils.CommonUtil;
import com.wehang.ystv.utils.GlideUtils;
import com.wehang.ystv.utils.ToastUtil;
import com.wehang.ystv.utils.UserLoginInfo;
import com.whcd.base.fragment.BaseFragment;
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
 * Created by Anonymous on 2016/1/27.
 */
public class zhuBoFragment extends BaseFragment implements View.OnClickListener,LiveToolsPopupwindow.LiveToolsOnItemClickLister, ConversationView {
    //主播和房间信息
    public TextView nameImageVideo,onLineNumber;
    public ImageView avatarImageVideo;
    private RelativeLayout anchorRlyt;


    public  View view;
    private LivePushActivity activity;
    private LinearLayout.LayoutParams layoutParams;

    //Animation_top点击顶部动画
    private RelativeLayout Animation_top;
    //Animation_top点击底部动画
    private RelativeLayout Animation_btm;
    //屏幕锁
    public CheckableImageButton lock_img;
    //                课件    连麦    分享   喜欢  对话   消息   问题
    public ImageView kejian,lianmai,share,like,duihua,xiaoxi,wenti,showbig;
    public LinearLayout tiwenLiner;

    private ImageView liveOperateImgBtn;
    public LiveToolsPopupwindow liveToolsPopupwindow;// 闪光、翻转、美颜弹窗

    private LookShardToolsPopupwindow sharePopupwindow;// 分享
    private ImageView wendang;

    //聊天室
    public EditText sendWhat;
    public TextView sendWhatTv;
    public TextView sendMessage;
    public ListView conversationListView;
    public LiveVideoChartFragmentAdapter conversationAdapter;

    //问题
    public LiveQusetiondapter liveQusetiondapter;

    //课件
    private boolean isHavaClass=true;
    private boolean isShowKj=false;
    private List<String>coursewaresList=new ArrayList<>();
    private ImgPagerAdapter classAdapter;
    public TextView upPager,nextPager;
    private RelativeLayout kjDoRly;

    //数据库
    private DownloadDao searDao;
    private KejianBean kejianBean;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity= (LivePushActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_item_zhubo, null);
        return view;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
       initview();
        getLiveDetails();
    }
    @Override
    public void onClick(View v) {
        super.onClick(v);
        int i = v.getId();
        if (CommonUtil.isFirstClick()){
            return;
        }

        if (i == R.id.img_bt_switch_camera) {
            liveToolsPopupwindow.showPopWindow();
        }
        if (i==R.id.whatch_kj){
            if (isShowKj){
                activity.kjRly.setVisibility(View.GONE);
                kjDoRly.setVisibility(View.GONE);
                kejian.setImageResource(R.drawable.wendang);
                isShowKj=false;
            }else {
                if (isHavaClass){
                    isShowKj=true;
                    kejian.setImageResource(R.drawable.gbwendang);
                    activity.kjRly.setVisibility(View.VISIBLE);
                    kjDoRly.setVisibility(View.VISIBLE);
                }else {
                    ToastUtil.makeText(getActivity(),"本课程没有课件",Toast.LENGTH_SHORT).show();
                }
            }

        }

        if (i == R.id.whatch_share) {
            if (i== R.id.whatch_share){
                if (sharePopupwindow!=null){
                    sharePopupwindow.showPopWindow();
                }else return;
            }
        }
        if (i== R.id.showback){
            AlertDialog exitLiveDialog = CommonUtil.showYesNoDialog(activity, "确定结束直播？", "确认", "取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    if (arg1 == DialogInterface.BUTTON_POSITIVE) {
                        com.wehang.ystv.utils.Utils.endLive(activity.liveNeed.sourceId,activity);
                        activity.mTCChatRoomMgr.sendLiveOver("主播结束");
                    }
                }
            });
            exitLiveDialog.show();
        }


        if (i==R.id.sendMessage){
            final String content = sendWhat.getText().toString().trim();
            if (TextUtils.isEmpty(content)) {
                ToastUtil.makeText(getActivity(),"内容不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
            if (content.length() > 30) {
                ToastUtil.makeText(getActivity(),"输入文本长度超过最大限制", Toast.LENGTH_SHORT).show();
                return;
            }
            if (UserLoginInfo.wantToTolk!=null){
                UserLoginInfo.wantToTolk=null;
                /*String str="@" + UserLoginInfo.wantToTolk.name + " :";
                if (content.startsWith(str)){
//获取单聊会话
                    String peer = UserLoginInfo.wantToTolk.userId;  //获取与用户 "sample_user_1" 的会话
                    TIMConversation   conversation = TIMManager.getInstance().getConversation(
                            TIMConversationType.C2C,    //会话类型：单聊
                            peer);
                    //构造一条消息
                    TIMMessage msg = new TIMMessage();

//添加文本内容
                    TIMTextElem elem = new TIMTextElem();
                    String sendWhatStr=content.replace(str,"");
                    if (TextUtils.isEmpty(sendWhatStr)) {
                        ToastUtil.makeText(getActivity(),"内容不能为空", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    elem.setText(sendWhatStr);

//将elem添加到消息
                    if(msg.addElement(elem) != 0) {
                        Log.d("@someOne", "addElement failed");
                        return;
                    }

//发送消息
                    conversation.sendMessage(msg, new TIMValueCallBack<TIMMessage>() {//发送消息回调
                        @Override
                        public void onError(int code, String desc) {//发送消息失败
                            //错误码code和错误描述desc，可用于定位请求失败原因
                            //错误码code含义请参见错误码表
                            Log.d("@someOne", "send message failed. code: " + code + " errmsg: " + desc);
                        }

                        @Override
                        public void onSuccess(TIMMessage msg) {//发送消息成功
                            Log.e("@someOne", "SendMsg ok");
                        }
                    });
                    UserLoginInfo.wantToTolk=null;
                    sendWhat.setText("");
                    return;
                }*/
            }
            //activity.mTCChatRoomMgr.sendTextMessage(content);
            //同步2个聊天室
           // activity.mTCChatRoomMgr.sendTextMessage(content);
            com.wehang.ystv.bo.Message message = new TextMessage(content);
            activity.mTCChatRoomMgr.sendMessage(message.getMessage());
            SpannableStringBuilder stringBuilder= (SpannableStringBuilder) sendWhat.getText();
            Message msg = Message.obtain();
            ChatRoomInfo chatRoomInfo = new ChatRoomInfo(1, stringBuilder,activity. mineInfo.userId,activity. mineInfo.name, activity.mineInfo.iconUrl,activity. mineInfo.level,activity. mineInfo.isVip, "", "", "", "", "0");
            msg.what = 0;
            msg.obj = chatRoomInfo;
            activity. msgHandler.sendMessage(msg);

            sendWhat.setText("");
        }
        if (i==R.id.whatch_kj){

        }
        if (i== R.id.whatch_lianmai){
            lianmai.setImageResource(R.drawable.lianmai);
            getLinkList();
        }
        if (i== R.id.whatch_like){

        }

        if (i== R.id.whatch_wenti){

            conversationListView.setVisibility(View.VISIBLE);
            wenti.setImageResource(R.drawable.wenti_n);
            tiwenLiner.setVisibility(View.GONE);
            activity.tolkOrAsk=2;
            sendMessage.setText("提问");
            sendWhat.setVisibility(View.GONE);
            sendWhatTv.setVisibility(View.VISIBLE);
            conversationListView.setAdapter(liveQusetiondapter);
            if (liveQusetiondapter.getCount() > 0) {
                conversationListView.setSelection(liveQusetiondapter.getCount() - 1);
            }
        }
        //聊天室
        if (i== R.id.whatch_duihua){
            //2次点击隐藏
            if (activity.tolkOrAsk==1&&conversationListView.isShown()){
                conversationListView.setVisibility(View.GONE);
                return;
            }else {
                conversationListView.setVisibility(View.VISIBLE);
            }
            duihua.setImageResource(R.drawable.duihua_n);
            activity.tolkOrAsk=1;
            sendMessage.setText("发送");
            sendWhat.setVisibility(View.VISIBLE);
            sendWhatTv.setVisibility(View.GONE);
            tiwenLiner.setVisibility(View.VISIBLE);
            conversationListView.setAdapter(conversationAdapter);
            if (conversationAdapter.getCount() > 0) {
                conversationListView.setSelection(conversationAdapter.getCount() - 1);
            }

        }
        //私信
        if (i== R.id.whatch_xiaoxi){
            if (tolkdialog!=null){
                if (tolkdialog.isShowing()){
                    return;
                }else {
                    showTolkDialog();
                }
            }else {
                showTolkDialog();
            }


        }
        if (i==R.id.anchorRlyt){
            //点击头像跳转不跳转自己的主页
            /*Intent intent=new Intent(getActivity(), UserHomeActivty.class);
            Bundle bundle=new Bundle();
            UserInfo userInfo=UserLoginInfo.getUserInfo(getActivity());
            bundle.putSerializable("data",userInfo);
            intent.putExtra("bundle",bundle);
            getActivity().startActivity(intent);*/
        }
        //停止
        if (i==R.id.push_stop){
            if (activity.mMapLinkMicMember.size()>0){
                ToastUtil.makeText(getActivity(),"请先结束连麦",Toast.LENGTH_SHORT).show();
                return;
            }
            if (activity.isPhonePush){
                AlertDialog exitLiveDialog = CommonUtil.showYesNoDialog(getActivity(), "确定结束直播？", "确认", "取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        if (arg1 == DialogInterface.BUTTON_POSITIVE) {
                            com.wehang.ystv.utils.Utils.endLive(activity.liveNeed.sourceId,getActivity());
                        }
                    }
                });
                exitLiveDialog.show();
            }else {
                activity.finish();
            }

        }

    }
    private void initview(){
      /*  lock_img= (CheckableImageButton)getView().findViewById(R.id.lock_img);
        //control_play.setVisibility(View.VISIBLE);
        lock_img.setOnClickListener(this);

        showbig= (ImageView) getView().findViewById(R.id.showbig);
        showbig.setOnClickListener(this);*/
        //初始化数据库
        searDao=DownloadDao.getInstance(DBHelper.getInstance(getActivity()));

        avatarImageVideo=getView().findViewById(R.id.avatarImageVideo);
        nameImageVideo=getView().findViewById(R.id.nameImageVideo);

        onLineNumber=getView().findViewById(R.id.onLineNumber);
        anchorRlyt=getView().findViewById(R.id.anchorRlyt);
        anchorRlyt.setOnClickListener(this);

        kejian= (ImageView) getView().findViewById(R.id.whatch_kj);
        upPager= (TextView)getView(). findViewById(R.id.upPager);
        nextPager= (TextView)getView(). findViewById(R.id.nextPager);
        upPager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogUtils.i("QUSERYCOURSEWARE","UP");
                activity.classViewpager.setCurrentItem(activity.classViewpager.getCurrentItem()-1);
                kejianBean.setPage(activity.classViewpager.getCurrentItem());
                if (kejianBean.getPage()==0){
                    upPager.setVisibility(View.GONE);
                }else if (kejianBean.getPage()==pic.length-1){
                    nextPager.setVisibility(View.GONE);
                }else {
                    upPager.setVisibility(View.VISIBLE);
                    nextPager.setVisibility(View.VISIBLE);
                }
                LogUtils.i("QUSERYCOURSEWARE","UP"+kejianBean.getPage());
                searDao.add(kejianBean);
            }
        });
        nextPager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogUtils.i("QUSERYCOURSEWARE","NEXT");
                activity.classViewpager.setCurrentItem( activity.classViewpager.getCurrentItem()+1);
                kejianBean.setPage(activity.classViewpager.getCurrentItem());
                if (kejianBean.getPage()==0){
                    upPager.setVisibility(View.GONE);
                }else if (kejianBean.getPage()==pic.length-1){
                    nextPager.setVisibility(View.GONE);
                } else {
                    upPager.setVisibility(View.VISIBLE);
                    nextPager.setVisibility(View.VISIBLE);
                }
                searDao.add(kejianBean);
                LogUtils.i("QUSERYCOURSEWARE","NEXT"+kejianBean.getPage());
            }
        });
        kjDoRly=getView().findViewById(R.id.kjDoRly);

        lianmai= (ImageView) getView().findViewById(R.id.whatch_lianmai);

        share= (ImageView) getView().findViewById(R.id.whatch_share);
        xiaoxi= (ImageView) getView().findViewById(R.id.whatch_xiaoxi);
        wenti= (ImageView) getView().findViewById(R.id.whatch_wenti);
        tiwenLiner=getView().findViewById(R.id.tiwenLiner);
        duihua= (ImageView) getView().findViewById(R.id.whatch_duihua);
        kejian.setOnClickListener(this);
        lianmai.setOnClickListener(this);

        share.setOnClickListener(this);
        xiaoxi.setOnClickListener(this);
        wenti.setOnClickListener(this);
        duihua.setOnClickListener(this);
        getView().findViewById(R.id.push_stop).setOnClickListener(this);

        liveOperateImgBtn = (ImageView) getView().findViewById(R.id.img_bt_switch_camera);
        liveOperateImgBtn.setOnClickListener(this);
        if (!activity.isPhonePush){
            liveOperateImgBtn.setVisibility(View.GONE);
        }

        liveToolsPopupwindow = new LiveToolsPopupwindow(liveOperateImgBtn, getActivity(), this);
        wendang = getView().findViewById(R.id.whatch_kj);
        wendang.setOnClickListener(this);


        share = (ImageView)getView(). findViewById(R.id.whatch_share);



        share.setOnClickListener(this);


        getView(). findViewById(R.id.showback).setOnClickListener(this);

        sendWhat= (EditText) getView().findViewById(R.id.sendWhat);
        sendMessage= (TextView) getView().findViewById(R.id.sendMessage);
        sendMessage.setOnClickListener(this);
        sendWhatTv=getView().findViewById(R.id.sendWhatTv);

        conversationListView = (ListView) getView().findViewById(R.id.list_view);
        conversationAdapter = new LiveVideoChartFragmentAdapter(getActivity());
        conversationListView.setAdapter(conversationAdapter);
        conversationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                LogUtils.i("onItemClick",i+""+conversationAdapter.getItem(i).nickName);
                if (conversationAdapter.getItem(i).userId.equals(UserLoginInfo.getUserInfo().userId)){

                }else {
                    UserInfo userInfo=new UserInfo();
                    userInfo.userId=conversationAdapter.getItem(i).userId;
                    userInfo.name=conversationAdapter.getItem(i).nickName;
                    UserLoginInfo.wantToTolk=userInfo;
                    String string="@" + conversationAdapter.getItem(i).nickName + " :";
                    sendWhat.setText(string);
                    sendWhat.setSelection(string.length());
                }

            }
        });

        liveQusetiondapter=new LiveQusetiondapter(getActivity(),true,activity.liveNeed.sourceId,activity.WhatBigImg);


        //初始话私聊对话框
        adapter = new ConversationDialogAdapter(getActivity(), R.layout.item_conversation, conversationList,xiaoxi,list,1);

        presenter = new ConversationPresenter(this);
        presenter.getConversation();
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
                adapter.notifyDataSetChanged();
                LogUtils.i("SysMessage",list.toString());
            }

            @Override
            public void onError(int errorCode) {
            }
        });

    }
    private AlertDialog tolkdialog,linkdialog;
    private void showTolkDialog(){
        getSysMessage();
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_talk, null);
        tolkdialog = new AlertDialog.Builder(getActivity()).setCancelable(true).show();
        Window win =tolkdialog.getWindow();
        win.setContentView(view);

        TextView allread = (TextView) view.findViewById(R.id.allread);

        allread.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //全部已读
                //tolkdialog.dismiss();
                for (int i=0;i<conversationList.size();i++){
                    LogUtils.i("readAllMessage",4+"");
                    conversationList.get(i).readAllMessage();
                }
                xiaoxi.setImageResource(R.drawable.xiaoxi_n);
                adapter.notifyDataSetChanged();
            }
        });
        LinearLayout cancel = (LinearLayout) view.findViewById(R.id.cancel);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tolkdialog.dismiss();
            }
        });



        oTolistview = view.findViewById(R.id.conversationListView);

        oTolistview.setAdapter(adapter);
        oTolistview.setPullRefreshEnable(false);
        oTolistview.setPullLoadEnable(false);
        oTolistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LogUtils.i("Click","ItemClick"+position);
                if (position == 1) {
                    startActivity(new Intent(getActivity(), SysTemMessageActivty.class));
                    //ToastUtil.makeText(getActivity(), "后台开发中", Toast.LENGTH_SHORT).show();
                }else {
                    conversationList.get(position-2).navToDetail(getActivity());
                }

            }
        });


        //registerForContextMenu(conversationListView);
        adapter.notifyDataSetChanged();
        setXiaoxiImg(conversationList);
    }


    //单人会话
    private PageListView oTolistview;
    private List<Conversation> conversationList = new LinkedList<>();
    private  ConversationDialogAdapter adapter;
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
    private List<TIMConversation>timConversationList=new ArrayList<>();
    @Override
    public void initView(List<TIMConversation> conversationList) {
        LogUtils.i("qiuqiu",1);
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



        LogUtils.i("liaotian",conversationList.size());


        //标记为已读需要
        timConversationList=conversationList;
    }


    public void updateNiceName() {
        LogUtils.i("qiuqiu",2);
        for (Conversation conversation : conversationList) {
            TIMUserProfile mTIMUserProfile = UsersProfilePresenter.getInstance().getUsersProfile(conversation.getIdentify());
            if (mTIMUserProfile != null) {
                if (conversation instanceof NomalConversation) {
                    ((NomalConversation) conversation).setNickName(mTIMUserProfile.getNickName());
                    ((NomalConversation) conversation).setAvatar(mTIMUserProfile.getFaceUrl());

                    TIMFriendGenderType type=mTIMUserProfile.getGender();
                    int a=3;
                    if (type==TIMFriendGenderType.Female){
                        a=0;
                    }else {
                        a=1;
                    }
                    ((NomalConversation) conversation).sex=a;

                    String b= String.valueOf(mTIMUserProfile.getBirthday());
                    if(b.equals("")){
                        b="0";
                    }
                    ((NomalConversation) conversation).isvip= Integer.parseInt(b);
                    String c=mTIMUserProfile.getNickName();
                    String d=mTIMUserProfile.getFaceUrl();
                    LogUtils.i("mTIMUserProfile",a+";"+b+";"+c+";"+d)
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

        LogUtils.i("qiuqiu",message.getConversation().getType());
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
        LogUtils.i("qiuqiu",4);
        friendshipManagerPresenter.getFriendshipLastMessage();
    }

    /**
     * 删除会话
     *
     * @param identify
     */
    @Override
    public void removeConversation(String identify) {
        LogUtils.i("qiuqiu",5);
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
        LogUtils.i("qiuqiu",6);
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
        LogUtils.i("qiuqiu",7);

        Collections.sort(conversationList);
        if (setXiaoxiImg(conversationList)){
            xiaoxi.setImageResource(R.drawable.xiaoxi_n);
        }else {
            xiaoxi.setImageResource(R.drawable.xiaoxi);
        }
        adapter.notifyDataSetChanged();
        //刷新首页未读消息
//            ((HomeActivity) getActivity()).setMsgUnread(getTotalUnreadNum() == 0);
    }
    private boolean setXiaoxiImg(List<Conversation> conversationList){
        //获取未读消息数
        for (int i=0;i<conversationList.size();i++){
            long num = conversationList.get(i).getUnreadNum();
            if (num>0){
                return false;
            }
        }

        return true;
    }
    private boolean needAsk=true;
    @Override
    public void onResume() {
        super.onResume();

        adapter.notifyDataSetChanged();
    }





    private boolean mFlashTurnOn = false;
    @Override
    public void liveToolsOnItemClick(int position, boolean isChecked, int frontOrBack) {
        if (position == 0) {
            Log.i("logpop", 1 + "；" + isChecked + ";" + frontOrBack);
            boolean c = true;
            //mFlashTurnOn为true表示打开，否则表示关闭
            if (isChecked) {
               activity. mTXLivePusher.turnOnFlashLight(false);

            } else {
                c =activity. mTXLivePusher.turnOnFlashLight(true);
            }
            if (!c) {
                Toast.makeText(getActivity().getApplicationContext(),
                        "打开闪光灯失败:绝大部分手机不支持前置闪光灯!", Toast.LENGTH_SHORT).show();
            }

        }
        if (position == 1) {
            Log.i("logpop", 2 + "");
            activity.mTXLivePusher.switchCamera();
            if (frontOrBack==0){
                activity.mTXPushConfig.setVideoEncoderXMirror(false);
                activity.mTXLivePusher.setMirror(false);
            }else {
                activity.mTXPushConfig.setVideoEncoderXMirror(true);
                activity.mTXLivePusher.setMirror(true);
            }
            //打开前置摄像头
            if (isChecked) {
                mFlashTurnOn = true;
            } else {
                mFlashTurnOn = false;
            }

        }
        if (position == 2) {
            Log.i("logpop", 3 + "");
            //美颜0——9
            if (isChecked) {
                activity.mTXLivePusher.setBeautyFilter(0,7,3,3);
            } else {
                activity. mTXLivePusher.setBeautyFilter(0, 0,0,0);
            }

        }
        if(liveToolsPopupwindow.popupWindow!=null){
            liveToolsPopupwindow.popupWindow.dismiss();
        }
    }

    private String[] pic;
    private void getLiveDetails(){
        Map<String, String> params;
        params = new HashMap<String, String>();
        params.put("token",UserLoginInfo.getUserToken());
        params.put("sourceId", activity.liveNeed.sourceId);
        HttpTool.doPost(getActivity(), UrlConstant.SOURCEDETAIL, params, true, new TypeToken<BaseResult<GetLvesResult>>() {
        }.getType(), new HttpTool.OnResponseListener() {
            @Override
            public void onSuccess(BaseData data) {
                LogUtils.i("QUSERYCOURSEWARE",data.toString());
                GetLvesResult result= ( GetLvesResult) data;
                activity.lives=result.source;
                GlideUtils.loadHeadImage(getActivity(), UrlConstant.IP + activity.lives.iconUrl, avatarImageVideo);
                nameImageVideo.setText(activity.lives.name);
                onLineNumber.setText(activity.lives.fansNum);
                //分享
                shareBean shareBean=new shareBean();
                shareBean.brife=activity.lives.summary;
                shareBean.sourcePic= UrlConstant.IP+activity.lives.sourcePic;
                shareBean.sourceTitle=activity.lives.sourceTitle;
                shareBean.Html="http://112.74.218.80/share/share.html?sourceId="+activity.lives.sourceId;
                sharePopupwindow = new LookShardToolsPopupwindow(share, getActivity(),shareBean);

                sharePopupwindow.setLookShardViedo(new LookShardToolsPopupwindow.mShardViedo() {
                    @Override
                    public void shardViedo(int position) {
                        Log.i("share", position + "");

                    }
                });
                if (!TextUtils.isEmpty(activity.lives.coursewareUrl)){
                     pic=activity.lives.coursewareUrl.split(",");
                    for (int i=0;i<pic.length;i++){
                        isHavaClass=true;
                        coursewaresList.add(pic[i]);

                    }
                    classAdapter=new ImgPagerAdapter(coursewaresList,getActivity());
                    activity.classViewpager.setAdapter(classAdapter);
                    activity.classViewpager.setOffscreenPageLimit(coursewaresList.size()>2?2:coursewaresList.size());


                    //初始化数据库
                    searDao= DownloadDao.getInstance(DBHelper.getInstance(getActivity()));
                    kejianBean=searDao.queryKejian(activity.lives.sourceId);
                    if (kejianBean==null){
                        kejianBean=new KejianBean();;
                        kejianBean.setUrl(activity.lives.sourceId);
                        kejianBean.setPage(0);
                    }
                    if (kejianBean.getPage()==0){
                        upPager.setVisibility(View.GONE);
                    }else if (kejianBean.getPage()==pic.length-1){
                        nextPager.setVisibility(View.GONE);
                    }else {
                        upPager.setVisibility(View.VISIBLE);
                        nextPager.setVisibility(View.VISIBLE);
                    }
                    activity.classViewpager.setCurrentItem(kejianBean.getPage());





                }else {
                    isHavaClass=false;
                    kejian.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(int errorCode) {
                LogUtils.i("info","no");
            }
        });
    }

    private PageListView linkListView;
    private List<UserInfo>linkList=new ArrayList<>();
    private LinkAdapter linkAdapter;
    private void getLinkList(){
        Map<String, String> params = new HashMap<String, String>();
        params.put("token",UserLoginInfo.getUserToken());
        params.put("sourceId", activity.liveNeed.sourceId);
        final CustomProgressDialog dialog = CustomProgressDialog.show(activity, "加载中...", true, null);
        HttpTool.doPost(activity, UrlConstant.CONNECTApplyList, params, true, new TypeToken<BaseResult<GetFocusesResult>>() {
        }.getType(), new HttpTool.OnResponseListener() {
            @Override
            public void onSuccess(BaseData data) {
                dialog.dismiss();
                GetFocusesResult result= (GetFocusesResult) data;
                linkList.clear();
                linkList.addAll(result.users);
                LogUtils.i("linkList",linkList.size());
                showLinkMicDialog();
            }

            @Override
            public void onError(int errorCode) {
                dialog.dismiss();
                ToastUtil.makeText(activity,"服务器异常，请重试",Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void showLinkMicDialog(){
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_link, null);
        linkdialog = new AlertDialog.Builder(getActivity()).setCancelable(true).show();
        Window win =linkdialog.getWindow();
        win.setContentView(view);

        LinearLayout cancel = (LinearLayout) view.findViewById(R.id.cancel);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linkdialog.dismiss();
            }
        });

        linkListView = view.findViewById(R.id.conversationListView);
        //初始化连麦对话框
        linkAdapter=new LinkAdapter(getActivity(),linkList,linkdialog);
        linkListView.setAdapter(linkAdapter);
        linkListView.setPullRefreshEnable(false);
        linkListView.setPullLoadEnable(false);
    }
}