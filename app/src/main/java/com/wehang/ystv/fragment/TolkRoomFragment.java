package com.wehang.ystv.fragment;

/**
 * Created by lenovo on 2017/7/31.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.CheckableImageButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
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
import com.wehang.txlibrary.TCConstants;
import com.wehang.txlibrary.widget.myview.BottomPopupWindow2;
import com.wehang.txlibrary.widget.myview.BottomPopupwindow;
import com.wehang.txlibrary.widget.myview.LiveToolsPopupwindow;
import com.wehang.txlibrary.widget.myview.LookShardToolsPopupwindow;
import com.wehang.txlibrary.widget.myview.shareBean;
import com.wehang.ystv.Constant;
import com.wehang.ystv.R;
import com.wehang.ystv.VideoApplication;
import com.wehang.ystv.adapter.ConversationAdapter;
import com.wehang.ystv.adapter.ConversationDialogAdapter;
import com.wehang.ystv.adapter.ImgPagerAdapter;
import com.wehang.ystv.adapter.LiveQusetiondapter;
import com.wehang.ystv.adapter.LiveVideoChartFragmentAdapter;
import com.wehang.ystv.bo.Conversation;
import com.wehang.ystv.bo.Coursewares;
import com.wehang.ystv.bo.CustomMessage;
import com.wehang.ystv.bo.LineMicInfo;
import com.wehang.ystv.bo.Lives;
import com.wehang.ystv.bo.MessageFactory;
import com.wehang.ystv.bo.NomalConversation;
import com.wehang.ystv.bo.TextMessage;
import com.wehang.ystv.bo.UserInfo;
import com.wehang.ystv.bo.YsMessage;
import com.wehang.ystv.dbutils.DBHelper;
import com.wehang.ystv.dbutils.DownloadBean;
import com.wehang.ystv.dbutils.DownloadDao;
import com.wehang.ystv.dbutils.KejianBean;
import com.wehang.ystv.interfaces.ConversationView;
import com.wehang.ystv.interfaces.UrlConstant;
import com.wehang.ystv.interfaces.result.GetLvesResult;
import com.wehang.ystv.interfaces.result.GetSysMResult;
import com.wehang.ystv.logic.ChatRoomInfo;
import com.wehang.ystv.logic.ConversationPresenter;
import com.wehang.ystv.logic.FriendshipManagerPresenter;
import com.wehang.ystv.logic.GroupManagerPresenter;
import com.wehang.ystv.logic.UsersProfilePresenter;
import com.wehang.ystv.ui.AddQuseTionActivity;
import com.wehang.ystv.ui.GetAnswerActivity;
import com.wehang.ystv.ui.GetQuestionActivity;
import com.wehang.ystv.ui.LiveDetails;
import com.wehang.ystv.ui.LivePushActivity;
import com.wehang.ystv.ui.LiveWatchNewActivity;
import com.wehang.ystv.ui.LiveWatvhActivity;
import com.wehang.ystv.ui.SysTemMessageActivty;
import com.wehang.ystv.ui.UserHomeActivty;
import com.wehang.ystv.utils.CommonUtil;
import com.wehang.ystv.utils.GlideUtils;
import com.wehang.ystv.utils.ToastUtil;
import com.wehang.ystv.utils.UserLoginInfo;
import com.wehang.ystv.utils.Utils;
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
public class TolkRoomFragment extends BaseFragment implements ConversationView {
    private BottomPopupwindow sharePopupwindow;// ??????

    //?????????????????????
    public TextView nameImageVideo,onLineNumber;
    public ImageView avatarImageVideo;
    private RelativeLayout anchorRlyt;

    public  View view;
    private LiveWatchNewActivity activity;
    private LinearLayout.LayoutParams layoutParams;

    //Animation_top??????????????????
    private RelativeLayout Animation_top;
    //Animation_top??????????????????
    private RelativeLayout Animation_btm;
    //?????????
    public CheckableImageButton lock_img;
    //                ??????    ??????    ??????   ??????  ??????   ??????   ??????
    public ImageView kejian,mBtnLinkMic,duihua,wenti,showbig;

    public Button closeLineMicBtn;

    //?????????
    public EditText sendWhat;
    public TextView sendWhatTv;
    private TextView sendMessage;
    public ListView conversationListView;
    public LiveVideoChartFragmentAdapter conversationAdapter;

    public LinearLayout tiwenLiner;

    //??????
    public LiveQusetiondapter liveQusetiondapter;

    //??????
    private boolean isHavaClass=true;
    private boolean isShowKj=false;
    private List<String>coursewaresList=new ArrayList<>();
    private ImgPagerAdapter classAdapter;
    public TextView upPager,nextPager;
    private RelativeLayout kjDoRly;
    //?????????
    private DownloadDao searDao;
    private KejianBean kejianBean;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity= (LiveWatchNewActivity) context;
        LogUtils.i("TolkRoomFragment",activity.mLiveRoomId);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_item, null);
        return view;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initview();
        //??????????????????
        getLiveDetails();
    }
    @Override
    public void onClick(View v) {
        super.onClick(v);
        int i = v.getId();
        if (CommonUtil.isFirstClick()){
            return;
        }
        if (i== R.id.showback){
            if (activity.WhatBigImg.isShown()){
                activity.WhatBigImg.setVisibility(View.GONE);
                return;
            }
            if (activity.mIsBeingLinkMic){
                ToastUtil.makeText(getActivity(),"??????????????????",Toast.LENGTH_SHORT).show();
                return;
            }
            if (activity.instance.isPortrait()){
                activity.finish();
            }else {
                activity.instance.toggleScreen();
            }
        }
        if (i== R.id.lock_img){
            if (lock_img.isChecked()){
                lock_img.setChecked(false);
                lock_img.setImageResource(R.drawable.shangsuo);
                activity.instance.setIslock(false);
                return;
            }else {
                activity.instance.setIslock(true);
                lock_img.setChecked(true);
                lock_img.setImageResource(R.drawable.shangsu_suo);
                return;
            }
        }
        if (i== R.id.showbig){
            activity. instance.toggleScreen();
            return;
        }
        if (i==R.id.sendMessage){
            final String content = sendWhat.getText().toString().trim();
            if (TextUtils.isEmpty(content)) {
                ToastUtil.makeText(getActivity(),"??????????????????", Toast.LENGTH_SHORT).show();
                return;
            }
            if (content.length() > 30) {
                ToastUtil.makeText(getActivity(),"????????????????????????????????????", Toast.LENGTH_SHORT).show();
                return;
            }
            if (UserLoginInfo.wantToTolk!=null){
                UserLoginInfo.wantToTolk=null;
              /*  String str="@" + UserLoginInfo.wantToTolk.name + " :";
                if (content.startsWith(str)){
//??????????????????
                    String peer = UserLoginInfo.wantToTolk.userId;  //??????????????? "sample_user_1" ?????????
                    TIMConversation   conversation = TIMManager.getInstance().getConversation(
                            TIMConversationType.C2C,    //?????????????????????
                            peer);
                    //??????????????????
                    TIMMessage msg = new TIMMessage();

//??????????????????
                    TIMTextElem elem = new TIMTextElem();
                    String sendWhatStr=content.replace(str,"");
                    if (TextUtils.isEmpty(sendWhatStr)) {
                        ToastUtil.makeText(getActivity(),"??????????????????", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    elem.setText(sendWhatStr);

//???elem???????????????
                    if(msg.addElement(elem) != 0) {
                        Log.d("@someOne", "addElement failed");
                        return;
                    }

//????????????
                    conversation.sendMessage(msg, new TIMValueCallBack<TIMMessage>() {//??????????????????
                        @Override
                        public void onError(int code, String desc) {//??????????????????
                            //?????????code???????????????desc????????????????????????????????????
                            //?????????code???????????????????????????
                            Log.d("@someOne", "send message failed. code: " + code + " errmsg: " + desc);
                        }

                        @Override
                        public void onSuccess(TIMMessage msg) {//??????????????????
                            Log.e("@someOne", "SendMsg ok");
                        }
                    });
                    UserLoginInfo.wantToTolk=null;
                    sendWhat.setText("");
                    return;
                }*/
            }

            //??????2????????????
            //activity.mTCChatRoomMgr.sendTextMessage(content);
            com.wehang.ystv.bo.Message message = new TextMessage(content);
            activity.mTCChatRoomMgr.sendMessage(message.getMessage());


            Message msg = Message.obtain();
            SpannableStringBuilder stringBuilder= (SpannableStringBuilder) sendWhat.getText();
            ChatRoomInfo chatRoomInfo = new ChatRoomInfo(1, stringBuilder,activity. mineInfo.userId,activity. mineInfo.name, activity.mineInfo.iconUrl,activity. mineInfo.level,activity. mineInfo.isVip, "", "", "", "", "0");
            msg.what = 0;
            msg.obj = chatRoomInfo;
            activity. msgHandler.sendMessage(msg);

            sendWhat.setText("");
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
                    ToastUtil.makeText(getActivity(),"?????????????????????",Toast.LENGTH_SHORT).show();
                }
            }

        }
        if (i== R.id.whatch_lianmai){
            //case R.id.btnLineMic://????????????
            //case R.id.whatch_lianmai://????????????
            //?????????????????????????????????

            if (activity.mIsBeingLinkMic == false) {
                CommonUtil.showYesNoDialog(getActivity(), "???????????????????????????????????????????????????", "??????", "??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int arg1) {
                        if (arg1 == DialogInterface.BUTTON_POSITIVE) {
                            handUp();
                           activity. initLinkMic();
                        }
                    }
                }).show();
                //activity.startLinkMic();

            } else {
                CommonUtil.showYesNoDialog(getActivity(), "???????????????????????????", "??????", "??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int arg1) {
                        if (arg1 == DialogInterface.BUTTON_POSITIVE) {
                            activity.stopLinkMic();
                            activity. startPlay();
                            activity.stopLoading();
                            Utils.endLink(activity.sourceId,getActivity());
                            //??????????????????
                            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
                            lock_img.setChecked(false);
                            lock_img.setImageResource(R.drawable.shangsuo);
                            activity.instance.setIslock(false);
                            showbig.setEnabled(true);
                            showbig.setVisibility(View.VISIBLE);
                        }
                    }
                }).show();

            }
        }
        //??????
        if (i== R.id.whachLyt1){
            collect();
        }
        //??????
        if (i== R.id.whachLyt2){
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
        //??????
        if (i== R.id.whachLyt3){
            if (sharePopupwindow!=null){
                sharePopupwindow.showPopWindow();
            }else return;

        }

        if (i== R.id.whatch_wenti){
            conversationListView.setVisibility(View.VISIBLE);
            wenti.setImageResource(R.drawable.wenti_n);
            String ts=sendWhat.getHint().toString();
            String s=ts.replaceAll("?????????","??????");
            sendWhat.setHint(s);
            sendWhatTv.setHint(s);
            activity.tolkOrAsk=2;
            sendMessage.setText("??????");
            sendMessage.setEnabled(false);
            sendWhat.setVisibility(View.GONE);
            sendWhatTv.setVisibility(View.VISIBLE);
            conversationListView.setAdapter(liveQusetiondapter);
            if (liveQusetiondapter.getCount() > 0) {
                conversationListView.setSelection(liveQusetiondapter.getCount() - 1);
            }
        }
        //?????????
        if (i== R.id.whatch_duihua){
            //2???????????????
            if (activity.tolkOrAsk==1&&conversationListView.isShown()){
                conversationListView.setVisibility(View.GONE);
                return;
            }else {
                conversationListView.setVisibility(View.VISIBLE);
            }
            duihua.setImageResource(R.drawable.duihua_n);
            String ts=sendWhat.getHint().toString();
            String s=ts.replaceAll("??????","?????????");
            sendWhat.setHint(s);
            sendWhatTv.setHint(s);
            activity.tolkOrAsk=1;
            sendMessage.setText("??????");
            sendMessage.setEnabled(true);
            sendWhat.setVisibility(View.VISIBLE);
            sendWhatTv.setVisibility(View.GONE);
            conversationListView.setAdapter(conversationAdapter);
            if (conversationAdapter.getCount() > 0) {
                 conversationListView.setSelection(conversationAdapter.getCount() - 1);
            }

        }


        if (i==R.id.tiwenLiner){
            if (activity.tolkOrAsk==1){
                //???????????????
            }else if(activity.tolkOrAsk==2){
                if(activity.mIsBeingLinkMic){
                    ToastUtil.makeText(activity,"?????????????????????",Toast.LENGTH_SHORT).show();
                }
                //??????
                Lives lives=new Lives();
                lives.sourceId=activity.sourceId;
                Intent intent=new Intent(getActivity(),AddQuseTionActivity.class);
                Bundle bundle=new Bundle();
                bundle.putSerializable("data",lives);
                intent.putExtra("bundle",bundle);
                getActivity().startActivityForResult(intent,1);
            }
        }
        if (i==R.id.anchorRlyt){
            //??????????????????
            Intent intent=new Intent(getActivity(), UserHomeActivty.class);
            Bundle bundle=new Bundle();
            UserInfo userInfo=new UserInfo();
            userInfo.userId=activity.lives.userId;
            bundle.putSerializable("data",userInfo);
            intent.putExtra("bundle",bundle);
            getActivity().startActivity(intent);
        }



    }


    private void initview(){
        //??????????????????
        searDao=DownloadDao.getInstance(DBHelper.getInstance(getActivity()));

        avatarImageVideo=getView().findViewById(R.id.avatarImageVideo);
        nameImageVideo=getView().findViewById(R.id.nameImageVideo);

        onLineNumber=getView().findViewById(R.id.onLineNumber);
        anchorRlyt=getView().findViewById(R.id.anchorRlyt);
        anchorRlyt.setOnClickListener(this);

        lock_img= (CheckableImageButton)getView().findViewById(R.id.lock_img);
        //control_play.setVisibility(View.VISIBLE);
        lock_img.setOnClickListener(this);

        showbig= (ImageView) getView().findViewById(R.id.showbig);
        showbig.setOnClickListener(this);
        kejian= (ImageView) getView().findViewById(R.id.whatch_kj);
        upPager= (TextView)getView(). findViewById(R.id.upPager);
        nextPager= (TextView)getView(). findViewById(R.id.nextPager);
        upPager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


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

        mBtnLinkMic= (ImageView) getView().findViewById(R.id.whatch_lianmai);//??????

        wenti= (ImageView) getView().findViewById(R.id.whatch_wenti);
        tiwenLiner=getView().findViewById(R.id.tiwenLiner);
        tiwenLiner.setOnClickListener(this);
        duihua= (ImageView) getView().findViewById(R.id.whatch_duihua);
        kejian.setOnClickListener(this);
        mBtnLinkMic.setOnClickListener(this);

        wenti.setOnClickListener(this);
        duihua.setOnClickListener(this);
        activity.likeRly.setOnClickListener(this);
        activity. shareRly.setOnClickListener(this);
        activity. xiaoxiRly.setOnClickListener(this);


        getView(). findViewById(R.id.showback).setOnClickListener(this);

        sendWhat= (EditText) getView().findViewById(R.id.sendWhat);
        sendWhatTv=getView().findViewById(R.id.sendWhatTv);
        sendMessage= (TextView) getView().findViewById(R.id.sendMessage);
        sendMessage.setOnClickListener(this);

        conversationListView = (ListView) getView().findViewById(R.id.list_view);
        conversationAdapter = new LiveVideoChartFragmentAdapter(getActivity());
        conversationListView.setAdapter(conversationAdapter);
        conversationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                LogUtils.i("onItemClick",i+""+conversationAdapter.getItem(i).nickName);
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
        liveQusetiondapter=new LiveQusetiondapter(getActivity(),false,activity.sourceId,activity.WhatBigImg);

        tiwenLiner.setVisibility(View.GONE);

        conversationListView.setVisibility(View.GONE);
        if (!Utils.supportLinkMic()) {
           mBtnLinkMic.setVisibility(View.INVISIBLE);
        }else {
            mBtnLinkMic.setVisibility(View.VISIBLE);
        }
        //????????????????????????
        adapter = new ConversationDialogAdapter(getActivity(), R.layout.item_conversation, conversationList,activity.xiaoxi,list,2);
        presenter = new ConversationPresenter(this);
        presenter.getConversation();
    }
    private void initData() {
        getSysMessage();
    }
    //???3???item?????????
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
    private AlertDialog tolkdialog;
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
                //????????????
                //tolkdialog.dismiss();
                for (int i=0;i<conversationList.size();i++){
                    LogUtils.i("readAllMessage",4+"");
                    conversationList.get(i).readAllMessage();
                }
                activity.xiaoxi.setImageResource(R.drawable.icon_sixin_1);
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
                    //ToastUtil.makeText(getActivity(), "???????????????", Toast.LENGTH_SHORT).show();
                }else {
                    conversationList.get(position-2).navToDetail(getActivity());
                }


            }
        });


        //registerForContextMenu(conversationListView);
        adapter.notifyDataSetChanged();
        setXiaoxiImg(conversationList);

    }
    private void handUp(){

        Map<String, String> params = new HashMap<String, String>();
        params.put("token", UserLoginInfo.getUserInfo().token);
        LogUtils.i("token",UserLoginInfo.getUserInfo().token+"++++"+UserLoginInfo.getUserToken());
        params.put("sourceId", activity.sourceId);
        HttpTool.doPost(VideoApplication.applicationContext, UrlConstant.HANDUP, params, false, new TypeToken<BaseResult<LineMicInfo>>() {
        }.getType(), new HttpTool.OnResponseListener() {
            @Override
            public void onSuccess(BaseData data) {
                activity.startLinkMic();
            }

            @Override
            public void onError(int errorCode) {
                ToastUtil.makeText(VideoApplication.applicationContext, "????????????", Toast.LENGTH_SHORT).show();
            }
        });
    }






    //????????????
    private PageListView oTolistview;
    private List<Conversation> conversationList = new LinkedList<>();
    private  ConversationDialogAdapter adapter;
    private ConversationPresenter presenter;
    private FriendshipManagerPresenter friendshipManagerPresenter;
    private GroupManagerPresenter groupManagerPresenter;
    private List<String> userList;
    private List<String> groupList;
    /**
     * ??????????????????????????????
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
                    ///??????????????????????????????????????????
                    //????????????????????????UID??????????????????????????????
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


        //?????????????????????
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
     * ????????????????????????
     *
     * @param message ??????????????????
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
            TIMMessage currMsg = message;
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
     * ???????????????????????????
     */
    @Override
    public void updateFriendshipMessage() {
        LogUtils.i("qiuqiu",4);
        friendshipManagerPresenter.getFriendshipLastMessage();
    }

    /**
     * ????????????
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
     * ???????????????
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
     * ??????
     */
    @Override
    public void refresh() {
        LogUtils.i("qiuqiu",7);
        for (int i=0;i<conversationList.size();i++){
            LogUtils.i("qiuqiu",conversationList.get(i).getLastMessageSummary()+"");
        }
        Collections.sort(conversationList);
        if (setXiaoxiImg(conversationList)){
            activity.xiaoxi.setImageResource(R.drawable.icon_sixin_1);
        }else {
            activity.xiaoxi.setImageResource(R.drawable.icon_sixin_2);
        }
        adapter.notifyDataSetChanged();
        //????????????????????????
//            ((HomeActivity) getActivity()).setMsgUnread(getTotalUnreadNum() == 0);
    }
    private boolean setXiaoxiImg(List<Conversation> conversationList){
        //?????????????????????
        for (int i=0;i<conversationList.size();i++){
            long num = conversationList.get(i).getUnreadNum();
            if (num>0){
                return false;
            }
        }

        return true;
    }

    @Override
    public void onResume() {
        super.onResume();

        adapter.notifyDataSetChanged();
    }






    /*private Handler handler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            int what=message.what;
            switch (what){
                case 1:
                    closeAnimation(Animation_top,Animation_btm);
                    break;
                case 2:

                default:break;
                case 3:

            }
            return false;
        }
    });
    //????????????????????????????????????????????????????????????
    public void showcontrol_play(){
        openAnimation(Animation_top,Animation_btm);

        Message message=Message.obtain();
        message.what=1;
        handler.sendMessageDelayed(message,7000);
    }

    //??????????????????
    private void closeAnimation(final View Animation_top,final View Animation_btn){


        //??????AnimationUtils??????????????????loadAnimation()?????????XML????????????XML??????
        Animation animation= AnimationUtils.loadAnimation(this, R.anim.close);
        Animation animation1=AnimationUtils.loadAnimation(this, R.anim.close1);
        Animation_top.startAnimation(animation);
        Animation_btn.startAnimation(animation1);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation arg0) {}   //????????????????????????

            @Override
            public void onAnimationRepeat(Animation arg0) {}  //????????????????????????

            @Override
            public void onAnimationEnd(Animation arg0) {
                Animation_top.setVisibility(View.GONE);
            }
        });
        animation1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation arg0) {}   //????????????????????????

            @Override
            public void onAnimationRepeat(Animation arg0) {}  //????????????????????????

            @Override
            public void onAnimationEnd(Animation arg0) {
                Animation_btn.setVisibility(View.GONE);

            }
        });
    }

    //??????????????????
    private void openAnimation(View Animation_top,View Animation_btn){
        Animation_top.setVisibility(View.VISIBLE);
        Animation_btn.setVisibility(View.VISIBLE);
        //??????AnimationUtils??????????????????loadAnimation()?????????XML????????????XML??????
        Animation animation=AnimationUtils.loadAnimation(this, R.anim.open);
        Animation animation1=AnimationUtils.loadAnimation(this, R.anim.open1);
        Animation_top.startAnimation(animation);
        Animation_btn.startAnimation(animation1);
    }*/
    private String[] pic;
    private void getLiveDetails(){
        Map<String, String> params;
        params = new HashMap<String, String>();
        params.put("token",UserLoginInfo.getUserToken());
        params.put("sourceId", activity.lives.sourceId);
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
                activity.liveTitle.setText(activity.lives.sourceTitle);
                if (activity.lives.sourceType==1){
                    showbig.setVisibility(View.VISIBLE);
                    mBtnLinkMic.setVisibility(View.VISIBLE);
                }else {
                    showbig.setVisibility(View.GONE);
                    mBtnLinkMic.setVisibility(View.GONE);
                }
                //??????
                shareBean shareBean=new shareBean();
                shareBean.brife=activity.lives.summary;
                shareBean.sourceTitle=activity.lives.sourceTitle;
                shareBean.sourcePic= UrlConstant.IP+activity.lives.sourcePic;
                shareBean.Html="http://112.74.218.80/share/share.html?sourceId="+activity.lives.sourceId;
                sharePopupwindow = new BottomPopupwindow(activity.rootLayout,getActivity(),shareBean);


                if (!TextUtils.isEmpty(activity.lives.coursewareUrl)){
                     pic=activity.lives.coursewareUrl.split(",");
                    for (int i=0;i<pic.length;i++){
                        isHavaClass=true;
                        coursewaresList.add(pic[i]);

                    }
                    classAdapter=new ImgPagerAdapter(coursewaresList,getActivity());
                    activity.classViewpager.setAdapter(classAdapter);
                    activity.classViewpager.setOffscreenPageLimit(coursewaresList.size()>2?2:coursewaresList.size());
                    //??????????????????
                    searDao=DownloadDao.getInstance(DBHelper.getInstance(getActivity()));
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
                    kejian.setVisibility(View.GONE);
                    isHavaClass=false;
                }
            }

            @Override
            public void onError(int errorCode) {
                LogUtils.i("info","no");
            }
        });
    }
    public  void collect(){
        int type=activity.lives.isCollect==0?1:0;
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", UserLoginInfo.getUserToken());
        params.put("sourceId",activity.lives.sourceId);
        params.put("type",type+"");
        final CustomProgressDialog dialog=CustomProgressDialog.show(getActivity(),"?????????",true,null);
        HttpTool.doPost1(getActivity(), UrlConstant.COLLECT, params, true, new TypeToken<BaseResult<BaseData>>() {
        }.getType(), new HttpTool.OnResponseListener1() {


            @Override
            public void onSuccess(String string) {
                dialog.dismiss();
                if (activity.lives.isCollect==0){
                    activity.like.setImageResource(R.drawable.icon_shoucang_2);
                }else {
                    activity.like.setImageResource(R.drawable.icon_shoucang_1);

                }
                if (activity.lives.isCollect==0){
                    activity.lives.isCollect=1;
                }else {
                    activity.lives.isCollect=0;
                }
            }

            @Override
            public void onError(int errorCode) {
                ToastUtil.makeText(getActivity(),"?????????", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }
}