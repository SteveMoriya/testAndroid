package com.wehang.ystv.ui;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CheckableImageButton;
import android.support.v4.app.Fragment;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.tencent.TIMElemType;
import com.tencent.TIMMessage;
import com.tencent.imcore.IFileTrans;
import com.tencent.rtmp.ITXLivePlayListener;
import com.tencent.rtmp.ITXLivePushListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePlayConfig;
import com.tencent.rtmp.TXLivePlayer;
import com.tencent.rtmp.TXLivePushConfig;
import com.tencent.rtmp.TXLivePusher;
import com.tencent.rtmp.TXLog;
import com.tencent.rtmp.ui.TXCloudVideoView;
import com.umeng.socialize.UMShareAPI;

import com.wehang.txlibrary.TCConstants;
import com.wehang.txlibrary.adpter.HomeFragmentPagerAdapter;
import com.wehang.txlibrary.base.BaseActivity;
import com.wehang.txlibrary.utils.Utils;
import com.wehang.txlibrary.widget.MyViewPager;

import com.wehang.ystv.Constant;
import com.wehang.ystv.R;
import com.wehang.ystv.adapter.LiveQusetiondapter;
import com.wehang.ystv.bo.LiveNeed;
import com.wehang.ystv.bo.Lives;
import com.wehang.ystv.bo.QuestionMessages;
import com.wehang.ystv.bo.TextMessage;
import com.wehang.ystv.bo.UserInfo;
import com.wehang.ystv.bo.WXPay;
import com.wehang.ystv.fragment.NoWahtFragment;
import com.wehang.ystv.fragment.TolkRoomFragment;
import com.wehang.ystv.fragment.zhuBoFragment;
import com.wehang.ystv.interfaces.UrlConstant;
import com.wehang.ystv.interfaces.result.GetLvesResult;
import com.wehang.ystv.logic.ChatRoomInfo;
import com.wehang.ystv.logic.TCChatRoomMgr;
import com.wehang.ystv.logic.TCLinkMicMgr;
import com.wehang.ystv.utils.CommonUtil;
import com.wehang.ystv.utils.NetworkUtils;
import com.wehang.ystv.utils.ToastUtil;
import com.wehang.ystv.utils.UserLoginInfo;
import com.wehang.ystv.widget.IndexViewPager;
import com.whcd.base.interfaces.BaseData;
import com.whcd.base.interfaces.BaseResult;
import com.whcd.base.interfaces.HttpTool;
import com.whcd.base.utils.CommonUtils;
import com.whcd.base.widget.CustomProgressDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.Vector;

import static com.tencent.rtmp.TXLiveConstants.ENCODE_VIDEO_AUTO;
import static com.tencent.rtmp.TXLiveConstants.ENCODE_VIDEO_SOFTWARE;
import static com.wehang.ystv.Constant.ACTIVITY_RESULT;
import static com.wehang.ystv.Constant.ERROR_MSG_NET_DISCONNECTED;
import static com.wehang.ystv.Constant.IMCMD_COLOSE;
import static com.wehang.ystv.Constant.IMCMD_DANMU;
import static com.wehang.ystv.Constant.IMCMD_DELETE;
import static com.wehang.ystv.Constant.IMCMD_QUESTION;
import static com.wehang.ystv.Constant.NO_LOGIN_CACHE;
import static com.wehang.ystv.utils.UserLoginInfo.userInfo;


public class LivePushActivity extends BaseActivity implements  ITXLivePushListener,ITXLivePlayListener,
        TCChatRoomMgr.TCChatRoomListener ,TCLinkMicMgr.TCLinkMicListener{
    private static final String TAG = "LivePushActivity";
    private Context context;
    public UserInfo mineInfo;
    public TXLivePushConfig mTXPushConfig = new TXLivePushConfig();
    public TXCloudVideoView mTXCloudVideoView;
    public TXLivePusher mTXLivePusher;
    private boolean mPasuing = false;


    private int pushRotation;
    private String mPushUrl;

    protected TextView countDownTv;// ?????????
    private View decorView;




    private FrameLayout mengcen;

    public LiveNeed liveNeed;
    public Lives lives;


    //?????????
    public TCChatRoomMgr mTCChatRoomMgr;
    private String mChatRoomId;

    //??????????????????ViewPAGER
    private MyViewPager operationViewPager;
    private HomeFragmentPagerAdapter operationAdapter;
    private List<Fragment> operationFragment = new ArrayList<Fragment>();
    private zhuBoFragment tolkRoomFragment;



    // ??????
    private Button closeLineMicBtn;//????????????
    private static final int MAX_LINKMIC_MEMBER_SUPPORT = 1;
    public String mSessionID;//?????????????????????????????????
    private boolean mHasPendingRequest = false;//mHasPendingRequest ????????????????????????
    private Vector<TCPlayItem> mVecPlayItems = new Vector<>();
    private TCLinkMicTimeoutRunnable mLinkMicTimeOutRunnable = new TCLinkMicTimeoutRunnable();
    public Map<String, String> mMapLinkMicMember = new HashMap<>();

    public TCLinkMicMgr mTCLinkMicMgr;

    //????????????????????????????????????????????? 1,?????????2????????? ?????????1
    public int  tolkOrAsk=1;


    //????????????
    public IndexViewPager classViewpager;
    public RelativeLayout kjRly;

    //????????????
    public ImageView WhatBigImg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        decorView = getWindow().getDecorView();
        //????????????????????????????????????
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_live_push);
        // ???????????????
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        Bundle bundle=getIntent().getBundleExtra("bundle");
        liveNeed = (LiveNeed) bundle.getSerializable("data");
        LogUtils.i("LiveNeed",liveNeed.toString());
        //mPushUrl = "rtmp://8036.livepush.myqcloud.com/live/8036_a3a3eea8d5?bizid=8036&txSecret=c10253d0770af35690a5cd976553e688&txTime=59970EFF";
        mPushUrl=liveNeed.pushUrl;
        mChatRoomId=liveNeed.chatRoomId;
        mSessionID=liveNeed.sessionId;
        context=this;
        mineInfo = UserLoginInfo.getUserInfo();


        initview();
        //??????????????????
        if (liveNeed.isPhonePush==0){
            mengcen.setVisibility(View.GONE);
            isPhonePush=false;
            Constant.isPhonePush=0;
            zhuBoPlayUrl=liveNeed.pullRtmpUrlUrl;
        }else {
            isPhonePush=true;
            Constant.isPhonePush=1;
            //??????????????????????????????????????????
            showBeforPush(times);
        }
        initData();





        TelephonyManager tm = (TelephonyManager) this.getApplicationContext().getSystemService(Service.TELEPHONY_SERVICE);
        tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);




    }
    final PhoneStateListener listener = new PhoneStateListener(){
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch(state){
                //??????????????????
                case TelephonyManager.CALL_STATE_RINGING:
                    if (mTXLivePusher != null) mTXLivePusher.pausePusher();
                    if (mTXLivePlayer != null) mTXLivePlayer.setMute(true);
                    break;
                //????????????
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    if (mTXLivePusher != null) mTXLivePusher.pausePusher();
                    if (mTXLivePlayer != null) mTXLivePlayer.setMute(true);
                    break;
                //????????????
                case TelephonyManager.CALL_STATE_IDLE:
                    if (mTXLivePusher != null) mTXLivePusher.resumePusher();
                    if (mTXLivePlayer != null) mTXLivePlayer.setMute(false);
                    break;

            }
        }
    };


    public static final int COUNTDOWN_END_INDEX = 1;
    public static final int COUNTDOWN_DELAY = 800;

    //??????????????????
    private int times = 3;
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            int what = message.what;
            if (what == 1) {
                if (times >= 1) {
                    showBeforPush(times);
                }

            }
            return false;
        }
    });

    //?????????????????????
    private void showBeforPush(final int count) {
        countDownTv.setVisibility(View.VISIBLE);
        countDownTv.setText(String.format("%d", count));
        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 0f, 1.0f, 0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(COUNTDOWN_DELAY);
        scaleAnimation.setFillAfter(false);
        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                times--;
                handler.sendEmptyMessageDelayed(1, 200);
                countDownTv.setVisibility(View.GONE);
                Log.i("tsks", "befor" + times);
                if (times == 0) {
                    mengcen.setVisibility(View.GONE);
                    if (!NetworkUtils.isWifi(context)){
                        CommonUtil.showYesNoDialog(context, "??????????????????Wifi??????????????????????????????", "??????", "??????", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int arg1) {
                                if (arg1 == DialogInterface.BUTTON_POSITIVE) {
                                    startPublish();
                                }else {
                                    finish();
                                }
                            }
                        }).show();
                    }else {
                        startPublish();
                    }

                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        countDownTv.startAnimation(scaleAnimation);
    }

    private void initview() {
        countDownTv = (TextView) findViewById(R.id.countdown_txtv);
        mTXCloudVideoView = (TXCloudVideoView) findViewById(R.id.video_view);

        mengcen = (FrameLayout) findViewById(R.id.push_mc);
        mengcen.setVisibility(View.VISIBLE);

        //??????
        WhatBigImg= (ImageView) findViewById(R.id.WhatBigImg);
        WhatBigImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WhatBigImg.setVisibility(View.GONE);
            }
        });

        operationViewPager= (MyViewPager) findViewById(R.id.operationViewPager);
        tolkRoomFragment=new zhuBoFragment();
        operationFragment.add(tolkRoomFragment);
        operationFragment.add(new NoWahtFragment());
        operationAdapter=new HomeFragmentPagerAdapter(this.getSupportFragmentManager(),operationFragment);
        operationViewPager.setAdapter(operationAdapter);

        //????????????
        closeLineMicBtn = (Button) findViewById(R.id.btn_kick_out1);
        //??????
        classViewpager= (IndexViewPager) findViewById(R.id.classViewPager);
        kjRly= (RelativeLayout) findViewById(R.id.kjRly);


        //??????????????????????????????
        leaveRlyt = (RelativeLayout) findViewById(R.id.leaveRlyt);
        leaveTv = (TextView) findViewById(R.id.leaveTv);
        anchorCoverIv = (RelativeLayout) findViewById(R.id.anchor_cover);

    }
    protected void initData() {
        mTCChatRoomMgr = TCChatRoomMgr.getInstance();
        mTCChatRoomMgr.setMessageListener(this);


        //?????????????????????

        //???????????????
        mTCChatRoomMgr.joinGroup(true, mChatRoomId);

        //??????
        mTCLinkMicMgr = TCLinkMicMgr.getInstance();
        //????????????????????????????????????
        mTCLinkMicMgr.setmChatRoomId(mChatRoomId);
        mTCLinkMicMgr.setLinkMicListener(this);

        if (TCLinkMicMgr.supportLinkMic() == false) {
            return;
        }

        TCPlayItem playItem1 = new TCPlayItem();
        playItem1.mVideoView = (TXCloudVideoView) findViewById(R.id.play_video_view1);
        playItem1.mVideoView.disableLog(true);
        playItem1.mLinkMicLoading = (ImageView) findViewById(R.id.linkmic_loading);
        playItem1.mLinkMicLoadingBg = (FrameLayout) findViewById(R.id.linkmic_loading_bg);
        playItem1.mBtnKickout = (Button) findViewById(R.id.btn_kick_out1);
        playItem1.mTXLivePlayer = new TXLivePlayer(this);
        playItem1.mTXLivePlayer.setPlayListener(new TXLivePlayListener(playItem1));
        playItem1.mTXLivePlayer.setPlayerView(playItem1.mVideoView);
        playItem1.mTXLivePlayer.enableHardwareDecode(true);

        playItem1.mTXLivePlayConfig = new TXLivePlayConfig();
        playItem1.mTXLivePlayConfig.enableAEC(true);
       /* //?????????????????????
        pushRotation = TXLiveConstants.VIDEO_ANGLE_HOME_RIGHT;
        mTXPushConfig.setHomeOrientation(pushRotation);*/

        playItem1.mTXLivePlayConfig.setAutoAdjustCacheTime(true);
        playItem1.mTXLivePlayConfig.setMinAutoAdjustCacheTime(0.2f);
        playItem1.mTXLivePlayConfig.setMaxAutoAdjustCacheTime(0.2f);
        playItem1.mTXLivePlayer.setConfig(playItem1.mTXLivePlayConfig);
      /*  if (isPhonePush){
            playItem1.mTXLivePlayer.setRenderRotation(TXLiveConstants.RENDER_ROTATION_LANDSCAPE);
        }else {

        }*/
        playItem1.mTXLivePlayer.setRenderRotation(TXLiveConstants.RENDER_ROTATION_PORTRAIT);
        playItem1.mTXLivePlayer.setRenderMode(TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN);

        mVecPlayItems.add(playItem1);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==110&&resultCode==RESULT_OK){
            com.wehang.ystv.utils.Utils.sourceQuestions(context,UserLoginInfo.getUserToken(),lives.sourceId,mTCChatRoomMgr);
            /*for (int i=0;i<tolkRoomFragment.liveQusetiondapter.getCount();i++){
                if (tolkRoomFragment.liveQusetiondapter.getItem(i).questionId.equals(Constant.questinId)){
                    tolkRoomFragment.liveQusetiondapter.getItem(i).answerContent=data.getStringExtra("answer");
                    tolkRoomFragment.liveQusetiondapter.notifyDataSetChanged();
                    break;
                }
            };*/

        }

        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);

    }
    private void startPublish() {
        mTXPushConfig.enableNearestIP(true);//????????????????????????????????????????????????????????? YES????????????????????????????????????????????? DNS ???????????????????????????
        mTXPushConfig.enableAEC(true);//??????????????????
        mTXPushConfig.setANS(true);
        mTXPushConfig.setAutoAdjustBitrate(true);//???????????? Qos ????????????????????????SDK ????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
        mTXPushConfig.setAutoAdjustStrategy(TXLiveConstants.AUTO_ADJUST_BITRATE_STRATEGY_2);
       // mTXPushConfig.setTouchFocus(false);
        //???????????????????????????????????????????????????
        mTXPushConfig.setVideoEncoderXMirror(true);
        pushRotation = TXLiveConstants.VIDEO_ANGLE_HOME_RIGHT;
        mTXPushConfig.setHomeOrientation(pushRotation);
        if (mTXLivePusher == null) {
            mTXLivePusher = new TXLivePusher(this);
            //???????????????????????????????????????????????????
            mTXLivePusher.setMirror(true);

            mTXLivePusher.setPushListener(this);
            mTXLivePusher.setZoom(1);
            if (Build.VERSION.SDK_INT < 18) {
                Log.d(TAG, "????????????API?????????????????????18???,?????????????????????");
                mTXPushConfig.setVideoBitrate(700);
                mTXPushConfig.setVideoResolution(TXLiveConstants.VIDEO_RESOLUTION_TYPE_360_640);
                mTXPushConfig.setHardwareAcceleration(ENCODE_VIDEO_SOFTWARE);
            } else {
                mTXPushConfig.setVideoResolution(TXLiveConstants.VIDEO_RESOLUTION_TYPE_540_960);
                mTXPushConfig.setVideoBitrate(1000);
                mTXPushConfig.setHardwareAcceleration(ENCODE_VIDEO_AUTO);
            }
            //?????????????????????
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pause_publish, options);
            mTXPushConfig.setPauseImg(bitmap);
            mTXPushConfig.setPauseFlag(TXLiveConstants.PAUSE_FLAG_PAUSE_VIDEO | TXLiveConstants.PAUSE_FLAG_PAUSE_AUDIO);
            //??????????????????
            mTXLivePusher.setRenderRotation(TXLiveConstants.RENDER_ROTATION_PORTRAIT);


            mTXLivePusher.setConfig(mTXPushConfig);
        }
        //???????????????????????????
        /**
         * quality??? SDK ?????????????????????????????????????????????????????????????????????????????????????????????????????? STANDARD???HIGH???SUPER ????????????????????????MAIN_PUBLISHER ??? SUB_PUBLISHER ??????????????????????????????????????????VIDEOCHAT ?????????????????????
         * adjustBitrate??????????????? Qos ????????????????????????SDK ????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????//?????????????????????????????????????????????
         * adjustResolution?????????????????????????????????????????? SDK ????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
         */
        mTXLivePusher.setVideoQuality(TXLiveConstants.VIDEO_QUALITY_HIGH_DEFINITION, false, true);

        mTXCloudVideoView.setVisibility(View.VISIBLE);
        if (!mTXLivePusher.setBeautyFilter(0,7, 3,7)) {
            Toast.makeText(getApplicationContext(), "?????????????????????????????????????????????", Toast.LENGTH_SHORT).show();
        }
        mTXLivePusher.startCameraPreview(mTXCloudVideoView);
        mTXLivePusher.startPusher(mPushUrl);
    }
    private void startLinkPublish(){
        //stopPublish();
        if (isPhonePush){
            mTXLivePusher.setVideoQuality(TXLiveConstants.VIDEO_QUALITY_LINKMIC_SUB_PUBLISHER,false,true);
            mTXLivePusher.startPusher(mPushUrl);
        }
    }
    private void jiesuLinkPublisH(){
        if (isPhonePush){
            mTXLivePusher.setVideoQuality(TXLiveConstants.VIDEO_QUALITY_HIGH_DEFINITION, false, true);
            mTXLivePusher.startPusher(mPushUrl);
        }
    }
    private void stopPublish() {
        mengcen.setVisibility(View.VISIBLE);
        if (mTXLivePusher != null) {
            mTXLivePusher.stopCameraPreview(false);
            mTXLivePusher.setPushListener(null);
            mTXLivePusher.stopPusher();
        }
        for (TCPlayItem item : mVecPlayItems) {
            if (!TextUtils.isEmpty(item.mPlayUrl)) {
                item.mTXLivePlayer.stopPlay(false);
            }
        }
    }



    @Override
    public void onPushEvent(int event, Bundle bundle) {
        Log.i("pushevent",event+"");
        if (mTXCloudVideoView != null) {
            mTXCloudVideoView.setLogText(null, bundle, event);
        }
        if (event < 0) {
            if (event == TXLiveConstants.PUSH_ERR_NET_DISCONNECT) {//???????????????????????????????????????????????????????????????????????????????????????????????????
                //showComfirmDialog(TCConstants.ERROR_MSG_NET_DISCONNECTED, true);
                //????????????????????????????????????????????????
                stopPublish();
               // quitRoom();
                final AlertDialog dialog=   Utils.showEerMesage(this, TCConstants.ERROR_MSG_NET_DISCONNECTED, "??????", "??????",null);
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        LivePushActivity.this.finish();
                    }
                });

            } else if (event == TXLiveConstants.PUSH_ERR_OPEN_CAMERA_FAIL) {//????????????????????????????????????????????????????????????
                Toast.makeText(getApplicationContext(), bundle.getString(TCConstants.ERROR_MSG_OPEN_CAMERA_FAIL), Toast.LENGTH_SHORT).show();
                finish();
            } else if (event == TXLiveConstants.PUSH_ERR_OPEN_MIC_FAIL || event == TXLiveConstants.PUSH_ERR_MIC_RECORD_FAIL) { //????????????????????????????????????????????????????????????
                Toast.makeText(getApplicationContext(), bundle.getString(TXLiveConstants.EVT_DESCRIPTION), Toast.LENGTH_SHORT).show();
            } else {
                //???????????????Toast?????????????????????
                Toast.makeText(getApplicationContext(), bundle.getString(TXLiveConstants.EVT_DESCRIPTION), Toast.LENGTH_SHORT).show();

                mTXCloudVideoView.onPause();
                //TIM NEED : TCPusherMgr.getInstance().changeLiveStatus(mUserId, TCPusherMgr.TCLiveStatus_Offline);
                finish();
            }
        }

        if (event == TXLiveConstants.PUSH_WARNING_HW_ACCELERATION_FAIL) {
            Log.d(TAG, "????????????????????????????????????");
            mTXPushConfig.setVideoResolution(TXLiveConstants.VIDEO_RESOLUTION_TYPE_360_640);
            mTXPushConfig.setVideoBitrate(700);
            mTXPushConfig.setHardwareAcceleration(ENCODE_VIDEO_SOFTWARE);
            mTXLivePusher.setConfig(mTXPushConfig);
        }

        if (event == TXLiveConstants.PUSH_EVT_PUSH_BEGIN) {
            //TIM NEED :  TCPusherMgr.getInstance().changeLiveStatus(mUserId, TCPusherMgr.TCLiveStatus_Online);
        }
    }

    @Override
    public void onNetStatus(Bundle bundle) {

    }


    /**
     * ??????????????????
     *
     * @param msg     ????????????
     * @param isError true?????????????????????????????? false???????????????????????????????????????
     *
     */
    /**
     * ????????????
     * ?????????????????????IMSDK??????????????????
     */
    public void quitRoom() {
//??????????????????
        mTCChatRoomMgr.sendOnlne((onlineNum-1)+"");
        //????????????
        //mTCChatRoomMgr.deleteGroup();
       // mTCChatRoomMgr.quitGroup(mChatRoomId);
        mTCChatRoomMgr.removeMsgListener();
      /*  if (isPhonePush){
            mTCChatRoomMgr.deleteGroup();
        }else {

        }*/
        mTCChatRoomMgr.quitGroup(mChatRoomId);
    }
    public void showComfirmDialog(String msg, Boolean isError) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.ConfirmDialogStyle);
        builder.setCancelable(true);
        builder.setTitle(msg);

        if (!isError) {
            builder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    stopPublish();
                    //quitRoom();
                    finish();
                    /*quitRoom();
                    stopRecordAnimation();
                    showDetailDialog();*/
                }
            });
            builder.setNegativeButton("??????", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        } else {
            //????????????????????????????????????????????????
            stopPublish();
            //quitRoom();
            builder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finish();
                }
            });
        }
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.setCanceledOnTouchOutside(false);
    }


    @Override
    protected void onResume() {
        super.onResume();
        mTXCloudVideoView.onResume();

        if (mPasuing) {
            mPasuing = false;

            if (mTXLivePusher != null) {
                mTXLivePusher.resumePusher();
//                mTXLivePusher.startCameraPreview(mTXCloudVideoView);
                mTXLivePusher.resumeBGM();
            }
        }
        for (int i = 0; i < mVecPlayItems.size(); ++i) {
            TXCloudVideoView videoView = mVecPlayItems.get(i).mVideoView;
            if (videoView != null) {
                videoView.onResume();
            }
        }

        for (TCPlayItem item : mVecPlayItems) {
            if (!TextUtils.isEmpty(item.mPlayUrl) && !TextUtils.isEmpty(item.mUserID)) {
                startLoading(item);
                LogUtils.i("infoCaonima",item.mPlayUrl);
                item.mTXLivePlayer.startPlay(item.mPlayUrl, TXLivePlayer.PLAY_TYPE_LIVE_RTMP_ACC);
            }
        }

        if (!isPhonePush){
            if (!NetworkUtils.isWifi(context)){
                CommonUtil.showYesNoDialog(context, "??????????????????Wifi????????????????????????", "??????", "??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int arg1) {
                        if (arg1 == DialogInterface.BUTTON_POSITIVE) {
                            startPlay();
                        }else {
                            finish();
                        }
                    }
                }).show();
            }else {
                startPlay();
            }
        }

    }
    @Override
    protected void onPause() {
        super.onPause();
       /* mTXCloudVideoView.onPause();
        if (mTXLivePusher != null) {
            mTXLivePusher.pauseBGM();
        }
        if (mTXLivePlayer!=null){
            stopPlay(false);
        }
        for (int i = 0; i < mVecPlayItems.size(); ++i) {
            TXCloudVideoView videoView = mVecPlayItems.get(i).mVideoView;
            if (videoView != null) {
                videoView.onPause();
            }
        }*/
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPasuing = true;
        
       /*
        if (mTXLivePusher != null) {
//            mTXLivePusher.stopCameraPreview(false);
            mTXLivePusher.pausePusher();
        }
        for (TCPlayItem item : mVecPlayItems) {
            if (!TextUtils.isEmpty(item.mPlayUrl)) {
                stopLoading(item, false);
                item.mTXLivePlayer.stopPlay(true);
            }
        }*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mTXCloudVideoView.onDestroy();

        /*mTCChatRoomMgr.quitGroup(mChatRoomId);
        mTCChatRoomMgr.removeMsgListener();*/
        quitRoom();
        for (int i = 0; i < mVecPlayItems.size(); ++i) {
            TXCloudVideoView videoView = mVecPlayItems.get(i).mVideoView;
            if (videoView != null) {
                videoView.onDestroy();
            }
        }

        mVecPlayItems.clear();

        if (mTCLinkMicMgr != null) {
            mTCLinkMicMgr.setLinkMicListener(null);
            mTCLinkMicMgr = null;
        }

        if (isPhonePush){
            stopPublish();
        }else {
            stopPlay(true);
        }
        //????????????
        //com.wehang.ystv.utils.Utils.outRoom(lives.sourceId, this);
    }

  public   Handler msgHandler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                // ????????????
                case 0:
                    if (tolkOrAsk==2){
                        tolkRoomFragment.duihua.setImageResource(R.drawable.duihua);
                    }
                    ChatRoomInfo message = (ChatRoomInfo) msg.obj;

                    if (tolkRoomFragment.conversationAdapter != null) {
                        tolkRoomFragment.conversationAdapter.appendToList(message);
                        if (tolkRoomFragment.conversationAdapter.getCount() > 0) {
                            tolkRoomFragment. conversationListView.setSelection(tolkRoomFragment.conversationAdapter.getCount() - 1);
                        }
                    }
                    break;
                // ????????????
                case 1:
                    List<ChatRoomInfo> messages = (List<ChatRoomInfo>) msg.obj;
                    if (tolkRoomFragment.conversationAdapter != null) {
                        tolkRoomFragment.conversationAdapter.resetMessageList(messages);
                        if (tolkRoomFragment.conversationAdapter.getCount() > 0) {
                            tolkRoomFragment. conversationListView.setSelection(tolkRoomFragment.conversationAdapter.getCount() - 1);
                        }
                    }
                    break;
                //??????

                case 3:
                    LogUtils.i("caonim");
                    if (tolkOrAsk==1){
                        tolkRoomFragment.wenti.setImageResource(R.drawable.wenti);
                    }
                    if (tolkRoomFragment.liveQusetiondapter != null) {
                        tolkRoomFragment.liveQusetiondapter.resetMessageList(list);
                        if (tolkRoomFragment.liveQusetiondapter.getCount() > 0) {
                            tolkRoomFragment. conversationListView.setSelection(tolkRoomFragment.liveQusetiondapter.getCount() - 1);
                        }
                    }
                    break;
                case 4:

                    LogUtils.i("caonim","????????????");
                    if (tolkRoomFragment.liveQusetiondapter != null) {
                        tolkRoomFragment.liveQusetiondapter.resetMessageList(list);
                        if (tolkRoomFragment.liveQusetiondapter.getCount() > 0) {
                            tolkRoomFragment. conversationListView.setSelection(tolkRoomFragment.liveQusetiondapter.getCount() - 1);
                        }
                    }
                    break;
            }
            return false;
        }
    });

    @Override
    public void onJoinGroupCallback(boolean isHost, int code, String msg) {
        if (code == 0) {
            Log.d(TAG, "onJoin group success" + msg);
            //????????????
            List<ChatRoomInfo> messages = new ArrayList<>();
            ChatRoomInfo chatRoomInfo = new ChatRoomInfo(100, "?????????????????????????????????TV?????????????????????????????????????????????????????????????????????", mineInfo.userId, mineInfo.name, mineInfo.iconUrl, mineInfo.level, mineInfo.isVip, "", "", "", "", "0");
            ChatRoomInfo chatRoomInfo1 = new ChatRoomInfo(2, "?????????????????????", mineInfo.userId, mineInfo.name, mineInfo.iconUrl, mineInfo.level, mineInfo.isVip, "", "", "", "", "0");
            messages.add(0, chatRoomInfo);
            messages.add(1,chatRoomInfo1);
            Message message = Message.obtain();
            message.what = 1;
            message.obj = messages;
            msgHandler.sendMessage(message);

            //?????????????????????????????????
            mTCChatRoomMgr.sendZhuBoJoinRoom(Constant.isPhonePush+"");

            //??????????????????
            com.wehang.ystv.bo.Message timMessage = new TextMessage("?????????????????????");
            mTCChatRoomMgr.sendMessage(timMessage.getMessage());
            //??????????????????
            com.wehang.ystv.utils.Utils.getOnLine(context,liveNeed.sourceId,mTCChatRoomMgr);
            //????????????
            com.wehang.ystv.utils.Utils.sourceQuestions(context,UserLoginInfo.getUserToken(),liveNeed.sourceId,msgHandler,list);
        } else if (NO_LOGIN_CACHE == code) {
            ToastUtil.makeText(context, "???????????????" + msg, Toast.LENGTH_SHORT).show();
            finish();
            TXLog.d(TAG, "onJoin group failed" + msg);
        } else {
            ToastUtil.makeText(context, "?????????????????????" + msg, Toast.LENGTH_SHORT).show();
            finish();
            TXLog.d(TAG, "onJoin group failed" + msg);
        }
    }

    @Override
    public void onSendMsgCallback(int errCode, TIMMessage timMessage) {
        if (timMessage != null)
            if (errCode == 0) {
                TIMElemType elemType = timMessage.getElement(0).getType();
                if (elemType == TIMElemType.Text) {
                    //????????????????????????
                    Log.d(TAG, "onSendTextMsgsuccess:" + errCode);
                } else if (elemType == TIMElemType.Custom) {
                    Log.d(TAG, "onSendCustomMsgsuccess:" + errCode);
                }

            } else {
                Log.d(TAG, "onSendMsgfail:" + errCode + " msg:" + timMessage.getMsgId());
            }
    }
    @Override
    public void onReceiveTextMsg(int type, ChatRoomInfo chatRoomInfo, String content) {
        switch (type){
            //????????????
            case TCConstants.IMCMD_PAILN_TEXT:
                Message msg = Message.obtain();
                msg.what = 0;
                msg.obj = chatRoomInfo;
                msgHandler.sendMessage(msg);
                break;
        }
    }
    //????????????
    private int onlineNum=0;
    @Override
    public void onReceiveMsg(int type, ChatRoomInfo chatRoomInfo, String content) {
        LogUtils.i("onReceiveMsg","type:"+type+"String:"+content);
        switch (type) {
            //????????????
            case Constant.IMCMD_ENTER_LIVE:

            // ????????????
            case Constant.IMCMD_PAILN_TEXT:
                Message msg = Message.obtain();
                msg.what = 0;
                msg.obj = chatRoomInfo;
                msgHandler.sendMessage(msg);
                break;
            //????????????
            case Constant.IMCMD_EXIT_LIVE:

                break;
            //??????
            case Constant.IMCMD_PRAISE:
                //starView.addLayout(Integer.valueOf(chatRoomInfo.praiseNo));
                break;

            //??????
            case Constant.IMCMD_DANMU:
               /* Message msg1 = Message.obtain();
                msg1.what = 0;
                msg1.obj = chatRoomInfo;
                msgHandler.sendMessage(msg1);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        barrageTotalIndex++;
                        barrageView.addBarrageView(chatRoomInfo.headPic, chatRoomInfo.userIsVip, chatRoomInfo.nickName, chatRoomInfo.msg);
                    }
                }, barrageIndex * BARRAGE_INTERVAL_TIME);
                barrageIndex++;*/
                break;
         /*   //????????????
            case Constant.IMCMD_ORDINARY_GIFT:
                Message msg2 = Message.obtain();
                msg2.what = 0;
                msg2.obj = chatRoomInfo;
                msgHandler.sendMessage(msg2);
                showNormGift(chatRoomInfo);
                break;
            //????????????
            case Constant.IMCMD_SPECIAL_GIFT:
                Message msg3 = Message.obtain();
                msg3.what = 0;
                msg3.obj = chatRoomInfo;
                msgHandler.sendMessage(msg3);
                showAnimationGift(chatRoomInfo.nickName + content, Integer.valueOf(chatRoomInfo.giftNo));
                break;*/
            case Constant.IMCMD_ROOM_COLLECT_INFO:
                //tolkRoomFragment.onLineNumber.setText(content);tolkRoomFragment.sendWhat.setHint("??????????????? (?????????"+content+")");
                if (!com.wehang.ystv.utils.Utils.isNumeric(content)){
                    return;
                }
                tolkRoomFragment.sendWhat.setHint("??????????????? (?????????"+content+")");
                tolkRoomFragment.sendWhatTv.setHint("??????????????? (?????????"+content+")");
                onlineNum= Integer.parseInt(content);
                break;
            //?????????????????????????????????????????????????????????????????????????????????
            case Constant.IMCMD_LIVE_OVER:
              /*//showComfirmDialog(Constant.ERROR_MSG_LIVE_OVER, true);
                ToastUtil.makeText(context,"?????????????????????????????????",Toast.LENGTH_SHORT).show();
                finish();*/
              //????????????????????????
                break;
            case IMCMD_QUESTION:
                //??????????????????
                if (content.equals("")){
                   // com.wehang.ystv.utils.Utils.sourceQuestions(context,UserLoginInfo.getUserToken(),liveNeed.sourceId,mTCChatRoomMgr);
                }else {
                    LogUtils.i("IMCMD_QUESTION",content);
                    try {
                        JSONObject jsonObject=new JSONObject(content);
                        JSONObject jsonObject2=jsonObject.getJSONObject("data");

                        JSONArray array=jsonObject2.getJSONArray("questions");
                        list.clear();
                        for (int i=0;i<array.length();i++){
                            JSONObject jsonObject1=array.getJSONObject(i);
                            QuestionMessages questionMessages = new Gson().fromJson(jsonObject1.toString(), QuestionMessages.class);
                            LogUtils.i("IMCMD_QUESTION",questionMessages.toString());
                            list.add(questionMessages);
                        }
                        msgHandler.sendEmptyMessage(3);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                break;
            case IMCMD_DELETE:
                ToastUtil.makeText(context,"?????????????????????????????????",Toast.LENGTH_SHORT).show();
                finish();
                break;
            case IMCMD_COLOSE:
                //showComfirmDialog(Constant.ERROR_MSG_LIVE_OVER, true);
                ToastUtil.makeText(context,"?????????????????????????????????",Toast.LENGTH_SHORT).show();
                finish();
            default:
                break;
        }
    }
    private List<QuestionMessages>list=new ArrayList<>();

    @Override
    public void onGroupDelete() {
        if (isPhonePush){
            showComfirmDialog(Constant.ERROR_MSG_LIVE_OVER, true);
        }else {

        }

    }

    @Override
    public void onRefresh() {
        /*mUnreadMessageNum += getTotalUnreadNum();
        if (mUnreadMessageNum <= 0) {
            unreadLabTv.setVisibility(View.INVISIBLE);
        } else if (mUnreadMessageNum > 0 && mUnreadMessageNum < 100) {
            unreadLabTv.setVisibility(View.VISIBLE);
            unreadLabTv.setText(String.valueOf(mUnreadMessageNum));
        } else if (mUnreadMessageNum >= 100) {
            unreadLabTv.setVisibility(View.VISIBLE);
            unreadLabTv.setText("99+");
        }*/
    }



    //????????????start
    @Override
    public void onReceiveLinkMicRequest(final String strUserId, String strNickName) {

        if (mMapLinkMicMember.size() >= MAX_LINKMIC_MEMBER_SUPPORT || mHasPendingRequest == true) {
            mTCLinkMicMgr.sendLinkMicResponse(strUserId, Constant.LINKMIC_RESPONSE_TYPE_REJECT, "???????????????????????????????????????");
            return;
        }
        tolkRoomFragment.lianmai.setImageResource(R.drawable.erji);

    }
    //????????????
    public void linkACCEPT(String strUserId){
       /* if (TextUtils.isEmpty(mSessionID)){
            ToastUtil.makeText(context,"mSessionID"+"??????,????????????",Toast.LENGTH_SHORT).show();
            return;
        }*/
       startLinkPublish();
        mTCLinkMicMgr.sendLinkMicResponse(strUserId, Constant.LINKMIC_RESPONSE_TYPE_ACCEPT, "mSessionID");

        for (TCPlayItem item : mVecPlayItems) {
            if (item.mUserID == null || item.mUserID.length() == 0) {
                item.mUserID = strUserId;
                item.mPending = true;
                startLoading(item);

                //??????????????????
                mLinkMicTimeOutRunnable.setUserID(strUserId);
                handler.removeCallbacks(mLinkMicTimeOutRunnable);
                handler.postDelayed(mLinkMicTimeOutRunnable, 30000);

                //????????????????????????
                mMapLinkMicMember.put(strUserId, "");

                break;
            }
        }
        mHasPendingRequest = false;
    }
    //????????????
    public void outLink(){
        for (int i = 0; i < mVecPlayItems.size(); ++i) {
            TCPlayItem item = mVecPlayItems.get(i);
            item.mTXLivePlayer.stopPlay(true);
            stopLoading(item, false);
            mTCLinkMicMgr.kickOutLinkMicMember(item.mUserID);
            mMapLinkMicMember.remove(item.mUserID);
            item.empty();
        }
        jiesuLinkPublisH();
    }
    @Override
    public void onReceiveLinkMicResponse(String strUserId, int responseType, String strParams) {


        //??????????????????
        TCPlayItem item = getPlayItemByUserID(strUserId);
        if (item != null && item.mPending == true) {
            if (item.mPlayUrl != null) {
                item.mTXLivePlayer.stopPlay(true);
            }
            jiesuLinkPublisH();
           // mTCLinkMicMgr.kickOutLinkMicMember(strUserId);
            stopLoading(item, false);
            item.empty();
            item.mBtnKickout.setVisibility(View.GONE);

            mMapLinkMicMember.remove(strUserId);
            com.wehang.ystv.utils.Utils.endLink(liveNeed.sourceId,context);
            ToastUtil.makeText(getApplicationContext(), "??????????????????", Toast.LENGTH_SHORT).show();
        }
    }
    private String xiaoZhuboId;
    @Override
    public void onReceiveMemberJoinNotify(final String strUserID, final String strPlayUrl) {
        if (TextUtils.isEmpty(strUserID) || TextUtils.isEmpty(strPlayUrl)) {
            return;
        }

        handler.post(new Runnable() {
            @Override
            public void run() {
                if (!mMapLinkMicMember.containsKey(strUserID)) {
                    return;
                }

                TCPlayItem item = getPlayItemByUserID(strUserID);
                if (item == null) {
                    return;
                }

                mMapLinkMicMember.put(strUserID, strPlayUrl);
                xiaoZhuboId=strUserID;
                if (item.mPlayUrl == null || item.mPlayUrl.length() == 0) {
                    TCPlayItem playItem = getPlayItemByUserID(strUserID);
                    if (playItem != null) {
                        playItem.mPlayUrl = strPlayUrl;
                        LogUtils.i("mPlayUrl",playItem.mPlayUrl);
                        playItem.mTXLivePlayer.startPlay(playItem.mPlayUrl, TXLivePlayer.PLAY_TYPE_LIVE_RTMP_ACC);
                    }
                }
            }
        });
        handler.removeCallbacks(mLinkMicTimeOutRunnable);
    }


    @Override
    public void onReceiveMemberExitNotify(final String strUserID) {

        handler.post(new Runnable() {
            @Override
            public void run() {
                TCPlayItem item = getPlayItemByUserID(strUserID);
                if (item != null) {
                    if (item.mPending == true) {
                        handler.removeCallbacks(mLinkMicTimeOutRunnable);
                    }

                    if (item.mPlayUrl != null && item.mPlayUrl.length() != 0) {
                        item.mTXLivePlayer.stopPlay(true);
                    }

                    stopLoading(item, false);
                    item.empty();
                    item.mBtnKickout.setVisibility(View.GONE);
                }

                mMapLinkMicMember.remove(strUserID);
            }
        });
    }


    @Override
    public void onReceiveKickedOutNotify() {

    }
    //????????????end



    private class TCPlayItem {
        public boolean mPending = false;
        public String mUserID = "";
        public String mPlayUrl = "";
        public TXCloudVideoView mVideoView;
        public ImageView mLinkMicLoading;
        public FrameLayout mLinkMicLoadingBg;
        public Button mBtnKickout;

        public TXLivePlayer mTXLivePlayer;
        public TXLivePlayConfig mTXLivePlayConfig = new TXLivePlayConfig();

        public void empty() {
            mPending = false;
            mUserID = "";
            mPlayUrl = "";
        }
    }
    private int linkTimes=0;
    //????????????????????????
    private class TXLivePlayListener implements ITXLivePlayListener {
        private TCPlayItem item;

        public TXLivePlayListener(TCPlayItem item) {
            this.item = item;
        }

        public void onPlayEvent(final int event, final Bundle param) {
            LogUtils.i("onPushEvent",event+"+++"+param.toString());
            if (event==TXLiveConstants.PLAY_WARNING_READ_WRITE_FAIL){
                handleLinkMicFailed(item, "?????????????????????????????????");
            }
            if (event == TXLiveConstants.PLAY_ERR_NET_DISCONNECT || event == TXLiveConstants.PLAY_EVT_PLAY_END) {
                if (item.mPending == true) {
                    handleLinkMicFailed(item, "???????????????????????????");
                } else {
                    handleLinkMicFailed(item, "???????????????????????????????????????");
                }

            } else if (event == TXLiveConstants.PLAY_EVT_PLAY_BEGIN||event==TXLiveConstants.PLAY_EVT_PLAY_PROGRESS) {
                if (item.mPending == true) {
                    item.mPending = false;
                }
                stopLoading(item, true);
                linkTimes=0;
                com.wehang.ystv.utils.Utils.linkSuccss(context,lives.sourceId,xiaoZhuboId);
            }else if (event==TXLiveConstants.PLAY_ERR_GET_RTMP_ACC_URL_FAIL){
                linkTimes++;
                if (linkTimes>=10){
                    handleLinkMicFailed(item, "???????????????????????????");
                    linkTimes=0;
                    return;
                }
                for (TCPlayItem item : mVecPlayItems) {
                    if (!TextUtils.isEmpty(item.mPlayUrl) && !TextUtils.isEmpty(item.mUserID)) {
                        startLoading(item);
                        item.mTXLivePlayer.startPlay(item.mPlayUrl, TXLivePlayer.PLAY_TYPE_LIVE_RTMP_ACC);
                    }
                }

            }
        }

        public void onNetStatus(final Bundle status) {

        }
    }
    private void handleLinkMicFailed(TCPlayItem item, String message) {
        if (item == null) {
            return;
        }

        if (item.mPending == true) {
            handler.removeCallbacks(mLinkMicTimeOutRunnable);
        }
        if (item.mPlayUrl != null) {
            item.mTXLivePlayer.stopPlay(true);
        }
        jiesuLinkPublisH();
        mTCLinkMicMgr.kickOutLinkMicMember(item.mUserID);
        mMapLinkMicMember.remove(item.mUserID);
        stopLoading(item, false);
        item.empty();
        item.mBtnKickout.setVisibility(View.GONE);
        com.wehang.ystv.utils.Utils.endLink(liveNeed.sourceId,context);
        ToastUtil.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
    class TCLinkMicTimeoutRunnable implements Runnable {
        private String strUserID = "";

        public void setUserID(String userID) {
            strUserID = userID;
        }

        @Override
        public void run() {
            TCPlayItem item = getPlayItemByUserID(strUserID);
            if (item != null && item.mPending == true) {
                if (item.mPlayUrl != null) {
                    item.mTXLivePlayer.stopPlay(true);
                }
                jiesuLinkPublisH();
                mTCLinkMicMgr.kickOutLinkMicMember(strUserID);
                stopLoading(item, false);
                item.empty();
                item.mBtnKickout.setVisibility(View.GONE);

                mMapLinkMicMember.remove(strUserID);
                com.wehang.ystv.utils.Utils.endLink(liveNeed.sourceId,context);
                ToastUtil.makeText(getApplicationContext(), "??????????????????", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private TCPlayItem getPlayItemByUserID(String strUserID) {
        if (strUserID == null || strUserID.length() == 0) {
            return null;
        }

        for (TCPlayItem item : mVecPlayItems) {
            if (strUserID.equalsIgnoreCase(item.mUserID)) {
                return item;
            }
        }

        return null;
    }

    private void startLoading(TCPlayItem item) {
        item.mBtnKickout.setVisibility(View.GONE);
        item.mLinkMicLoadingBg.setVisibility(View.VISIBLE);
        item.mLinkMicLoading.setVisibility(View.VISIBLE);
        item.mLinkMicLoading.setImageResource(R.drawable.linkmic_loading);
        AnimationDrawable ad = (AnimationDrawable) item.mLinkMicLoading.getDrawable();
        ad.start();
    }
    private void stopLoading(TCPlayItem item, boolean showKickout) {
        //????????????
        //item.mBtnKickout.setVisibility(showKickout ? View.VISIBLE : View.GONE);
        item.mBtnKickout.setVisibility(View.GONE);
        item.mLinkMicLoadingBg.setVisibility(View.GONE);
        item.mLinkMicLoading.setVisibility(View.GONE);
        AnimationDrawable ad = (AnimationDrawable) item.mLinkMicLoading.getDrawable();
        if (ad != null) {
            ad.stop();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        //???????????????????????????????????????
        if (com.wehang.ystv.utils.Utils.isInRangeOfView(closeLineMicBtn, event)) {
            for (int i = 0; i < mVecPlayItems.size(); ++i) {
                TCPlayItem item = mVecPlayItems.get(i);
                item.mTXLivePlayer.stopPlay(true);
                stopLoading(item, false);
                jiesuLinkPublisH();
                mTCLinkMicMgr.kickOutLinkMicMember(item.mUserID);
                mMapLinkMicMember.remove(item.mUserID);
                item.empty();
            }
            com.wehang.ystv.utils.Utils.endLink(liveNeed.sourceId,LivePushActivity.this);
            return false;
        }

        return super.dispatchTouchEvent(event);
    }



    @Override
    public void onBackPressed() {
        if (WhatBigImg.isShown()){
            WhatBigImg.setVisibility(View.GONE);
            return;
        }
        if (mMapLinkMicMember.size()>0){
            ToastUtil.makeText(context,"??????????????????",Toast.LENGTH_SHORT).show();
            return;
        }
      /*  if (isPhonePush){

        }else {
            finish();
        };*/
        AlertDialog exitLiveDialog = CommonUtil.showYesNoDialog(this, "?????????????????????", "??????", "??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                if (arg1 == DialogInterface.BUTTON_POSITIVE) {
                    com.wehang.ystv.utils.Utils.endLive(liveNeed.sourceId,LivePushActivity.this);
                    //mTCChatRoomMgr.sendLiveOver("????????????");//????????????
                }
            }
        });
        exitLiveDialog.show();

    }




    //????????????????????????????????????????????????

    private RelativeLayout anchorCoverIv;
    public RelativeLayout leaveRlyt;
    public TextView leaveTv;
    public boolean isPhonePush=true;
    private TXLivePlayer mTXLivePlayer;
    private TXLivePlayConfig mTXPlayConfig = new TXLivePlayConfig();
    //???????????????????????????????????????
    private String zhuBoPlayUrl;
    private boolean mPlaying = false;
   /* private void dialoDismss(){
        if (dialog!=null){
            if (dialog.isShowing()){
                dialog.dismiss();
            }
        }
    }*/
    private CustomProgressDialog dialog;
    public void startPlay() {

        mTXCloudVideoView.setVisibility(View.VISIBLE);


        if (!checkPlayUrl()) {
            return;
        }

        if (mTXLivePlayer == null) {
            mTXLivePlayer = new TXLivePlayer(this);
        }



/*//????????????
        mPlayConfig.setAutoAdjustCacheTime(true);
        mPlayConfig.setMinAutoAdjustCacheTime(1);
        mPlayConfig.setMaxAutoAdjustCacheTime(5);*/

//????????????
        mTXPlayConfig.setAutoAdjustCacheTime(true);
        mTXPlayConfig.setMinAutoAdjustCacheTime(1);
        mTXPlayConfig.setMaxAutoAdjustCacheTime(1);

/*//????????????
        mPlayConfig.setAutoAdjustCacheTime(false);
        mPlayConfig.setCacheTime(5);*/

        mTXPlayConfig.enableAEC(true);
        anchorCoverIv.setVisibility(View.VISIBLE);

        mTXLivePlayer.setPlayerView(mTXCloudVideoView);

       /* //?????????????????????
        pushRotation = TXLiveConstants.VIDEO_ANGLE_HOME_RIGHT;
        mTXPushConfig.setHomeOrientation(pushRotation);*/
       /* if (isPhonePush){
            mTXLivePlayer.setRenderRotation(TXLiveConstants.RENDER_ROTATION_LANDSCAPE);
        }else {
            mTXLivePlayer.setRenderRotation(TXLiveConstants.RENDER_ROTATION_PORTRAIT);
        }*/
       /* //?????????????????????
        pushRotation = TXLiveConstants.VIDEO_ANGLE_HOME_RIGHT;
        mTXPushConfig.setHomeOrientation(pushRotation);*/
        mTXLivePlayer.setRenderRotation(TXLiveConstants.RENDER_ROTATION_PORTRAIT);
        mTXLivePlayer.setRenderMode(TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN);

        mTXLivePlayer.setPlayListener(this);
        mTXLivePlayer.setConfig(mTXPlayConfig);

        int result;
        result = mTXLivePlayer.startPlay(zhuBoPlayUrl, mUrlPlayType);

        if (0 != result) {
            Intent rstData = new Intent();
            if (-1 == result) {
                Log.d(TAG, TCConstants.ERROR_MSG_NOT_QCLOUD_LINK);
                ToastUtil.makeText(getApplicationContext(), TCConstants.ERROR_MSG_NOT_QCLOUD_LINK, Toast.LENGTH_SHORT).show();
                rstData.putExtra(TCConstants.ACTIVITY_RESULT, TCConstants.ERROR_MSG_NOT_QCLOUD_LINK);
            } else {
                Log.d(TAG, TCConstants.ERROR_RTMP_PLAY_FAILED);
                ToastUtil.makeText(getApplicationContext(), TCConstants.ERROR_RTMP_PLAY_FAILED, Toast.LENGTH_SHORT).show();
                rstData.putExtra(TCConstants.ACTIVITY_RESULT, TCConstants.ERROR_MSG_NOT_QCLOUD_LINK);
            }
            mTXCloudVideoView.onPause();
            stopPlay(true);
            setResult(100, rstData);
            finish();
        } else {
            mPlaying = true;
        }
    }
    protected void stopPlay(boolean clearLastFrame) {
        if (mTXLivePlayer != null) {
            mTXLivePlayer.setPlayListener(null);
            mTXLivePlayer.stopPlay(clearLastFrame);
            mPlaying = false;
        }
    }
    private int mUrlPlayType = TXLivePlayer.PLAY_TYPE_LIVE_RTMP;
    private boolean checkPlayUrl() {
        if (TextUtils.isEmpty(zhuBoPlayUrl) || (!zhuBoPlayUrl.startsWith("http://") && !zhuBoPlayUrl.startsWith("https://") && !zhuBoPlayUrl.startsWith("rtmp://"))) {
            ToastUtil.makeText(getApplicationContext(), "???????????????????????????????????????rtmp,flv,hls,mp4????????????!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (zhuBoPlayUrl.startsWith("rtmp://")) {
            mUrlPlayType = TXLivePlayer.PLAY_TYPE_LIVE_RTMP;
        } else if ((zhuBoPlayUrl.startsWith("http://") || zhuBoPlayUrl.startsWith("https://")) && zhuBoPlayUrl.contains(".flv")) {
            mUrlPlayType = TXLivePlayer.PLAY_TYPE_LIVE_FLV;
        } else {
            ToastUtil.makeText(getApplicationContext(), "?????????????????????????????????????????????rtmp,flv????????????!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    //????????????
    @Override
    public void onPlayEvent(int event, Bundle bundle) {
        switch (event) {
            // ????????????????????????
            case TXLiveConstants.PLAY_EVT_RCV_FIRST_I_FRAME:
                //mTXLivePlayer.setRenderMode(TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN);
                anchorCoverIv.setVisibility(View.GONE);
                break;
            // ?????????????????????????????????????????????????????????????????????
            case TXLiveConstants.PLAY_EVT_PLAY_BEGIN:
                break;
            // ?????????????????????????????????????????????????????????
            case TXLiveConstants.PLAY_EVT_PLAY_PROGRESS:

                break;
            // ????????????, ????????????????????? (????????????????????????????????? PLAY_ERR_NET_DISCONNECT ???)
            case TXLiveConstants.PLAY_WARNING_RECONNECT:

                break;
            // ????????????loading????????????????????????????????????BEGIN??????
            case TXLiveConstants.PLAY_EVT_PLAY_LOADING:
                break;
            // ????????????,??????????????????????????????,??????????????????,?????????????????????????????????
            case TXLiveConstants.PLAY_ERR_NET_DISCONNECT:

                showErrorAndQuit(ERROR_MSG_NET_DISCONNECTED);
                break;
            // ????????????
            case TXLiveConstants.PLAY_EVT_PLAY_END:

                if (anchorCoverIv.getVisibility() == View.VISIBLE) {
                    anchorCoverIv.setVisibility(View.GONE);
                }
                leaveRlyt.setVisibility(View.VISIBLE);
                leaveTv.setText("????????????");
                stopPlay(false);
                break;
        }
    }

    private void showErrorAndQuit(String errorMsg) {
        mTXCloudVideoView.onPause();
        if (anchorCoverIv.getVisibility() == View.VISIBLE) {
            anchorCoverIv.setVisibility(View.GONE);
        }
        leaveRlyt.setVisibility(View.VISIBLE);
        leaveTv.setText("????????????");
        stopPlay(true);
        Intent rstData = new Intent();
        rstData.putExtra(ACTIVITY_RESULT, errorMsg);
        setResult(100, rstData);
    }

}
