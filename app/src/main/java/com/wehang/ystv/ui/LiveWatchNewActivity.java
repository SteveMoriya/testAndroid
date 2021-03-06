package com.wehang.ystv.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.LogUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tencent.TIMElemType;
import com.tencent.TIMMessage;
import com.tencent.rtmp.ITXLivePlayListener;
import com.tencent.rtmp.ITXLivePushListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePlayConfig;
import com.tencent.rtmp.TXLivePlayer;
import com.tencent.rtmp.TXLivePushConfig;
import com.tencent.rtmp.TXLivePusher;
import com.tencent.rtmp.TXLog;
import com.tencent.rtmp.ui.TXCloudVideoView;
import com.wehang.txlibrary.TCConstants;
import com.wehang.txlibrary.adpter.HomeFragmentPagerAdapter;
import com.wehang.txlibrary.ui.fragment.Tab1;
import com.wehang.txlibrary.utils.DensityUtil;
import com.wehang.txlibrary.utils.DisplayUtils;
import com.wehang.txlibrary.utils.ScreenSwitchUtils;
import com.wehang.txlibrary.widget.MyTabLayout;
import com.wehang.txlibrary.widget.MyViewPager;
import com.wehang.ystv.Constant;
import com.wehang.ystv.R;
import com.wehang.ystv.VideoApplication;
import com.wehang.ystv.bo.LineMicInfo;
import com.wehang.ystv.bo.Lives;
import com.wehang.ystv.bo.QuestionMessages;
import com.wehang.ystv.bo.TextMessage;
import com.wehang.ystv.bo.UserInfo;
import com.wehang.ystv.fragment.NoWahtFragment;
import com.wehang.ystv.fragment.PreFragment;
import com.wehang.ystv.fragment.QuestinFragment;
import com.wehang.ystv.fragment.TolkFragment;
import com.wehang.ystv.fragment.TolkRoomFragment;
import com.wehang.ystv.interfaces.UrlConstant;
import com.wehang.ystv.interfaces.result.GetClassResult;
import com.wehang.ystv.interfaces.result.GetGiftResult;
import com.wehang.ystv.interfaces.result.GetLvesResult;
import com.wehang.ystv.interfaces.result.GetRanksResult;
import com.wehang.ystv.logic.ChatRoomInfo;
import com.wehang.ystv.logic.HostChatInfo;
import com.wehang.ystv.logic.TCChatRoomMgr;
import com.wehang.ystv.logic.TCLinkMicMgr;
import com.wehang.ystv.utils.CommonUtil;
import com.wehang.ystv.utils.NetworkUtils;
import com.wehang.ystv.utils.ToastUtil;
import com.wehang.ystv.utils.UserLoginInfo;
import com.wehang.ystv.utils.Utils;
import com.wehang.ystv.widget.IndexViewPager;
import com.whcd.base.interfaces.BaseData;
import com.whcd.base.interfaces.BaseResult;
import com.whcd.base.interfaces.HttpTool;
import com.whcd.base.widget.CustomProgressDialog;
import com.whcd.base.widget.RoundImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import static com.tencent.rtmp.TXLiveConstants.ENCODE_VIDEO_AUTO;
import static com.tencent.rtmp.TXLiveConstants.ENCODE_VIDEO_SOFTWARE;
import static com.tencent.rtmp.TXLiveConstants.RENDER_MODE_ADJUST_RESOLUTION;
import static com.wehang.ystv.Constant.ACTIVITY_RESULT;
import static com.wehang.ystv.Constant.ERROR_MSG_NET_DISCONNECTED;
import static com.wehang.ystv.Constant.IMCMD_COLOSE;
import static com.wehang.ystv.Constant.IMCMD_DELETE;
import static com.wehang.ystv.Constant.IMCMD_LIVE_OVER;
import static com.wehang.ystv.Constant.IMCMD_QUESTION;
import static com.wehang.ystv.Constant.IMCMD_ROOM_COLLECT_INFO;
import static com.wehang.ystv.Constant.IMCMD_ZHUBOJOINROOM;
import static com.wehang.ystv.Constant.NO_LOGIN_CACHE;

public class LiveWatchNewActivity  extends com.wehang.txlibrary.base.BaseActivity implements View.OnClickListener, MyViewPager.OnScrollChanged ,ITXLivePlayListener,
        TCChatRoomMgr.TCChatRoomListener,HostChatInfo.HostChatUnreadMessageListener,
        ITXLivePushListener,TCLinkMicMgr.TCLinkMicListener {
    public UserInfo mineInfo;
    private UserInfo liveUserInfo;
    private static final String TAG ="LiveWatchNewActivity" ;
    private Context context;
    private TXLivePlayer mTXLivePlayer;
    private TXCloudVideoView mTXCloudVideoView;
    private TXLivePlayConfig mTXPlayConfig = new TXLivePlayConfig();
    private String mPlayUrl;
    private boolean mPlaying = false;
    //?????????
    public TCChatRoomMgr mTCChatRoomMgr;
    public String mChatRoomId;
    private Timer barragetimer = new Timer();// ??????????????????
    private int barrageIndex;// ????????????????????????????????????
    private int barrageProcessIndex;// ???????????????????????????????????????????????????
    private int barrageTotalIndex;// ??????????????????????????????????????????????????????




    private RelativeLayout anchorCoverIv;
    public RelativeLayout leaveRlyt;
    public TextView leaveTv;
//    private HostChatInfo mHostChatInfo;
    private long mUnreadMessageNum = 0;
    private TextView unreadLabelTv;

    private RoundImageView liveHeadRiv;
    private ImageView liveVipIv;
    private String mHostUserId;// ??????ID
    public String mLiveRoomId;
    private String mIconUrl;// ????????????
    private int isVip;// ????????????
    //???????????????
    private RelativeLayout wksRly;
    private TextView kcName,kcTime;
    
    private int screenHeight;

    //??????????????????ViewPAGER
    private MyViewPager operationViewPager;
    private HomeFragmentPagerAdapter operationAdapter;
    private List<Fragment> operationFragment = new ArrayList<Fragment>();

    //?????????????????????

    private RelativeLayout contioner_r;
    
    private TolkRoomFragment tolkRoomFragment;
    private TolkFragment tab1;
    private QuestinFragment tab2;

    private View decorView;

    //?????????????????????viewpager???tablayout
    private MyTabLayout tabLayout;
    private MyViewPager viewPager;
    private LayoutInflater mInflater;

    private List<String> mTitle = new ArrayList<String>();
    private List<Fragment> mFragment = new ArrayList<Fragment>();



    //????????????
    private String mLinkMicPlayUrl;
    public String sourceId;//??????ID
    public Handler mHandler = new Handler();
    public boolean mWaitingLinkMicResponse = false;
    public boolean mIsBeingLinkMic = false;
    public String mSessionID;
   // public ImageButton mBtnSwitchCamera;
    public TXCloudVideoView mSmallVideoView;
    public ImageView mLinkMicLoading;
    public FrameLayout mLinkMicLoadingBg;
    public TXLivePusher mTXLivePusherLink;
    public TXLivePushConfig mTXLivePushConfigLink;
    public TXLivePlayer mTXLivePlayerLink;
    public TXLivePlayConfig mTXLivePlayConfigLink;
    public TCLinkMicMgr mTCLinkMicMgr;
    public String mLinkMicNotifyUrl;
    
    //????????????????????????
    public ScreenSwitchUtils instance;
    private int addHeiht=0;

    //????????????????????????????????????????????? 1,?????????2????????? ?????????1
    public int  tolkOrAsk=1;
    //??????TAB????????????
    private CheckedTextView tab_online;
    private CheckedTextView tab_twTv;

    public Lives lives;
    //????????????
    public IndexViewPager classViewpager;
    public RelativeLayout kjRly;
    //????????????
    public ImageView WhatBigImg;

    //????????????3???????????????ACTIVITY???
    public ImageView xiaoxi,share,like;
    public LinearLayout likeRly,xiaoxiRly,shareRly;
    //??????????????????
    public TextView onlineWhatchNumeber;
    public TextView liveTitle;



    public boolean isPhonePush=true;
    public FrameLayout rootLayout;
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        //??????????????????view
        View decorView = getWindow().getDecorView();
        addHeiht=decorView.getHeight();
        int[]Heiht=getAccurateScreenDpi(this);
        Log.i("qiehuan","onWindowFocusChanged:"+Heiht[0]+"<"+Heiht[1]);

        addHeiht=Heiht[0]>Heiht[1]?Heiht[0]:Heiht[1];
    }
    /**
     * ???????????????????????????
     */
    public int[] getAccurateScreenDpi(Activity activity)
    {
        int[] screenWH = new int[2];
        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        try {
            Class<?> c = Class.forName("android.view.Display");
            Method method = c.getMethod("getRealMetrics",DisplayMetrics.class);
            method.invoke(display, dm);
            screenWH[0] = dm.widthPixels;
            screenWH[1] = dm.heightPixels;
        }catch(Exception e){
            e.printStackTrace();
        }
        return screenWH;
    }
    private RelativeLayout.LayoutParams layoutParams;
    private int screenWidth;
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        Log.i("qiehuan","onConfigurationChanged:"+instance.fanxiang+instance.isPortrait()+";"+screenWidth+";"+addHeiht);

        //android:configChanges="keyboardHidden|orientation|screenSize"???????????????
        // // ??????????????????????????????????????????layout_prot???land,???????????????????????????
        //mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        //??????????????????????????????????????????
        if (instance.isPortrait()){

            layoutParams.width=screenWidth;
            layoutParams.height= DensityUtil.dip2px(this,230);
            contioner_r.setLayoutParams(layoutParams);
            tolkRoomFragment.showbig.setImageResource(R.drawable.fdimg);
            //????????????????????????
            tolkRoomFragment.lock_img.setVisibility(View.GONE);
            tolkRoomFragment.wenti.setVisibility(View.GONE);
            tolkRoomFragment.duihua.setVisibility(View.GONE);
            //pushRotation = TXLiveConstants.VIDEO_ANGLE_HOME_DOWN;
            //mTXLivePushConfigLink.setHomeOrientation(pushRotation);

            tolkRoomFragment.tiwenLiner.setVisibility(View.GONE);
            tolkRoomFragment.conversationListView.setVisibility(View.GONE);
        }else {
            layoutParams.width=addHeiht;
            layoutParams.height=screenWidth;
            contioner_r.setLayoutParams(layoutParams);
            tolkRoomFragment.showbig.setImageResource(R.drawable.suofang);
            //????????????????????????
            //tolkRoomFragment.lock_img.setVisibility(View.VISIBLE);
            tolkRoomFragment.lock_img.setVisibility(View.GONE);
            tolkRoomFragment.wenti.setVisibility(View.VISIBLE);
            tolkRoomFragment.duihua.setVisibility(View.VISIBLE);

            tolkRoomFragment.tiwenLiner.setVisibility(View.VISIBLE);
            tolkRoomFragment.conversationListView.setVisibility(View.VISIBLE);
        }

        //0-l  1r 2 down
       /* //??????????????????????????????popwindow isshow()
        if (sharePopupwindow.isShowing()){
            sharePopupwindow.dismiss();

        }*/


    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ???????????????
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // ??????????????????
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        screenWidth = display.getWidth();
        //????????????????????????????????????
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
      //  getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        setContentView(R.layout.activity_live_watch_new);
        Bundle bundle=getIntent().getBundleExtra("bundle");
        liveUserInfo = (UserInfo) bundle.getSerializable("data");
        lives= (Lives) bundle.getSerializable("live");
        mHostUserId = liveUserInfo.userId;

        sourceId=getIntent().getStringExtra("sourceId");

        context=this;
        mineInfo = UserLoginInfo.getUserInfo();


        //??????????????????
        if (lives.isPhonePush==0){
            isPhonePush=false;
        }else {
            isPhonePush=true;
            //??????????????????????????????????????????
        }
        initView();

        //???????????????????????????
        instance = ScreenSwitchUtils.init(this.getApplicationContext());



        Log.i("qiehuan",""+instance.isPortrait()+"");

        initData();
        playLive();

        TelephonyManager tm = (TelephonyManager) this.getApplicationContext().getSystemService(Service.TELEPHONY_SERVICE);
        tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);

        NeeDtodo();
        LiveWatchNewActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
    PhoneStateListener listener = new PhoneStateListener(){
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch(state){
                //??????????????????
                case TelephonyManager.CALL_STATE_RINGING:
                    if (mTXLivePlayer != null) mTXLivePlayer.setMute(true);
                    break;
                //????????????
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    if (mTXLivePlayer != null) mTXLivePlayer.setMute(true);
                    break;
                //????????????
                case TelephonyManager.CALL_STATE_IDLE:
                    if (mTXLivePlayer != null) mTXLivePlayer.setMute(false);
                    break;
            }
        }
    };
    protected void initData() {
    /*    if (!TextUtils.isEmpty(mIconUrl)) {
            GlideUtils.loadHeadImage(context, UrlConstant.IP + mIconUrl, liveHeadRiv);
        }
        if (!TextUtils.isEmpty(mIconUrl)) {
            Glide.with(context).load(UrlConstant.IP + mIconUrl).asBitmap().into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {
                    FastBlurUtil.createScaleImage(anchorCoverIv, bitmap);
                }
            });
        }*/
        //????????????????????????????????????????????????????????????????????????/?????????????????????????????????????????????????????????????????????
        mTCChatRoomMgr = TCChatRoomMgr.getInstance();

        //???????????????????????????????????????????????????
        mTCChatRoomMgr.setMessageListener(this);
       /* mHostChatInfo = HostChatInfo.getInstance();
        mHostChatInfo.setUnreadMessageListener(this);
        mHostChatInfo.setHostUserId(mHostUserId);*/
        //checkLiveStatus();

    }
    private HomeFragmentPagerAdapter pagerAdapter;

    private void initView() {

        //????????????
        rootLayout= (FrameLayout) findViewById(R.id.myshare);
        //mPlayerView???step1??????????????????view
        mTXCloudVideoView= (TXCloudVideoView)findViewById(R.id.video_view);
       // mTXCloudVideoView.setRenderMode(RENDER_MODE_ADJUST_RESOLUTION);
//??????player??????
        mTXLivePlayer = new TXLivePlayer(this);

        mPlayUrl = liveUserInfo.pullRtmpUrlUrl;
        mChatRoomId=liveUserInfo.chatRoomId;
        mLinkMicPlayUrl=liveUserInfo.pullRtmpUrlUrl;


        leaveRlyt = (RelativeLayout) findViewById(R.id.leaveRlyt);
        leaveTv = (TextView) findViewById(R.id.leaveTv);
        anchorCoverIv = (RelativeLayout) findViewById(R.id.anchor_cover);


        //?????????
        wksRly= (RelativeLayout) findViewById(R.id.wksRly);
        kcName= (TextView) findViewById(R.id.kcName);
        kcTime= (TextView) findViewById(R.id.kcTime);

        //??????
        WhatBigImg= (ImageView) findViewById(R.id.WhatBigImg);
        WhatBigImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WhatBigImg.setVisibility(View.GONE);
            }
        });




        operationViewPager= (MyViewPager) findViewById(R.id.operationViewPager);
        tolkRoomFragment=new TolkRoomFragment();
        operationFragment.add(tolkRoomFragment);
        operationFragment.add(new NoWahtFragment());
        operationAdapter=new HomeFragmentPagerAdapter(this.getSupportFragmentManager(),operationFragment);
        operationViewPager.setAdapter(operationAdapter);


//??????
        like= (ImageView)findViewById(R.id.whatch_like);
        if (lives.isCollect==1){
            like.setImageResource(R.drawable.icon_shoucang_2);
        }else {
            like.setImageResource(R.drawable.icon_shoucang_1);

        }
        share= (ImageView) findViewById(R.id.whatch_share);

        xiaoxi= (ImageView)findViewById(R.id.whatch_xiaoxi);
        likeRly= (LinearLayout) findViewById(R.id.whachLyt1);
        xiaoxiRly= (LinearLayout) findViewById(R.id.whachLyt2);
        shareRly= (LinearLayout) findViewById(R.id.whachLyt3);

        liveTitle= (TextView) findViewById(R.id.liveTitle);
        onlineWhatchNumeber= (TextView) findViewById(R.id.onlineWhatchNumeber);

        contioner_r= (RelativeLayout) findViewById(R.id.contioner_r);
        layoutParams=(RelativeLayout.LayoutParams)contioner_r.getLayoutParams();


        classViewpager= (IndexViewPager) findViewById(R.id.classViewPager);
        kjRly= (RelativeLayout) findViewById(R.id.kjRly);


        tab_online= (CheckedTextView) findViewById(R.id.tab_online);
        tab_twTv= (CheckedTextView) findViewById(R.id.tab_twTv);
        mTitle.add("tab1");
        mTitle.add("tab2");


        mFragment.add(new TolkFragment());
        mFragment.add(new QuestinFragment());

        tab1= (TolkFragment) mFragment.get(0);
        tab2= (QuestinFragment) mFragment.get(1);

       // LogUtils.i("qiuqiuname",tab1.name+";"+tab2.name);

       /* tabLayout= (MyTabLayout) findViewById(R.id.tablayout);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);*/
        viewPager= (MyViewPager) findViewById(R.id.watvhViewpager);

        pagerAdapter = new HomeFragmentPagerAdapter(this.getSupportFragmentManager(),mFragment);
        viewPager.setAdapter(pagerAdapter);
        tab_online.setTextColor(ContextCompat.getColor(context,R.color.main_color));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position==0){
                    tab_online.setTextColor(ContextCompat.getColor(context,R.color.main_color));
                    tab_twTv.setTextColor(ContextCompat.getColor(context,R.color.main_text_color));
                }else if (position==1){
                    tab_twTv.setTextColor(ContextCompat.getColor(context,R.color.main_color));
                    tab_online.setTextColor(ContextCompat.getColor(context,R.color.main_text_color));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewPager.setOnScrollChangedListener(this);
        viewPager.setOffscreenPageLimit(2);

        int[] mCheckedIds = {R.id.ltRoom, R.id.twRoom};
        mCheckedTvs = new ArrayList<LinearLayout>();
        for (int checkedTv : mCheckedIds) {
            LinearLayout checked = (LinearLayout)findViewById(checkedTv);
            mCheckedTvs.add(checked);
            checked.setOnClickListener(this);
        }
        initIndicator();

        //??????
        mSmallVideoView = (TXCloudVideoView) findViewById(R.id.small_video_view);



        mLinkMicLoading = (ImageView) findViewById(R.id.linkmic_loading);
        mLinkMicLoadingBg = (FrameLayout) findViewById(R.id.linkmic_loading_bg);


        initLinkMic();
    }


    //??????tablayout??????????????????
    private View mIndictorView;
    private int peoplesNum=0;
    private int mTabWidth,mLineWidth;
    private int startWidth;
    private List<LinearLayout> mCheckedTvs;
    private RelativeLayout.LayoutParams params;
    private void initIndicator() {
        mIndictorView = findViewById(R.id.indictorView);
        //?????????tablyout?????????
        mTabWidth = screenWidth/2;
        mLineWidth=screenWidth / 4;
        LogUtils.i("info",mTabWidth+";"+mLineWidth+";"+screenWidth);
        //mLineWidth=0;
        startWidth=(mTabWidth-mLineWidth)/2;
        params = (RelativeLayout.LayoutParams) mIndictorView.getLayoutParams();
        params.width = mLineWidth;
        params.leftMargin = startWidth;
        mIndictorView.setLayoutParams(params);
    }
    @Override
    public void onScrollChanged(int l, int t, int oldl, int oldt) {
        params.leftMargin = l * mTabWidth / viewPager.getWidth() + startWidth ;
        mIndictorView.setLayoutParams(params);
    }
















    private void NeeDtodo(){
        if (lives.sourceType==1){
            instance.setIslock(false);
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

        }else {
            wksRly.setVisibility(View.VISIBLE);
            kcName.setText(lives.sourceTitle);
            kcTime.setText("??????????????????"+Utils.getHowTime(lives.startTime));
            instance.setIslock(true);
            findViewById(R.id.jzTs).setVisibility(View.GONE);
        }
    }
    //?????????????????????

    @Override
    protected void onResume() {
        super.onResume();
        //mTXCloudVideoView.onResume();

    /*    if (lives.sourceType==1){
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

        }else {
            wksRly.setVisibility(View.VISIBLE);
            kcName.setText(lives.sourceTitle);
            kcTime.setText("??????????????????"+Utils.getHowTime(lives.startTime));
            tolkRoomFragment.showbig.setVisibility(View.GONE);
            instance.setIslock(true);
        }*/
      /*  if (mSmallVideoView != null) {
            mSmallVideoView.onResume();
        }
        if (mIsBeingLinkMic) {
            mTXLivePusherLink.resumePusher();
            startLinkPlay();
        }*/
       // onHostChatUnreadMessage(0);
    }
    @Override
    protected void onPause() {
        super.onPause();

      /*  mTXCloudVideoView.onPause();
        stopPlay(false);
        if (mSmallVideoView != null) {
            mSmallVideoView.onPause();
        }*/
    }



    @Override
    public void onClick(View view) {
        int i = view.getId();

        for (int b = 0; b < mCheckedTvs.size(); b++) {
            if (view == mCheckedTvs.get(b)) {
                viewPager.setCurrentItem(b, false);
               /* if (i == 3) {
                    isSearch = false;
                    searchImg.setImageResource(R.drawable.ic_photo);
                } else {
                    isSearch = true;
                    searchImg.setImageResource(R.drawable.btn_search_press);
                }*/
                return;
            }
        }



    }
    @Override
    protected void onStart() {
        super.onStart();
        instance.start(this);

        Log.i("NEED","onstart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        instance.stop();
    }




    /**
     * ???????????????
     */
    public void quitRoom() {
        //??????????????????
        mTCChatRoomMgr.sendOnlne((onlineNum-1)+"");
        mTCChatRoomMgr.quitGroup(mChatRoomId);
        mTCChatRoomMgr.removeMsgListener();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
       /* mLivePlayer.stopPlay(true); // true??????????????????????????????
        mPlayerView.onDestroy();*/
        mTXCloudVideoView.onDestroy();
        stopPlay(true);
        quitRoom();
        //????????????FLB??????????????????????????????????????????????????????
        if (mSmallVideoView != null) {
            mSmallVideoView.onDestroy();
        }
        stopLinkMic();

        if (mTCLinkMicMgr != null) {
            mTCLinkMicMgr.setLinkMicListener(null);
            mTCLinkMicMgr = null;
        }
       // mHostChatInfo.onDestroy();
        Utils.outRoom(lives.sourceId,LiveWatchNewActivity.this);
    }



    public List<ChatRoomInfo> botommessages=new ArrayList<>();

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
                /*    if (tab1.conversationAdapter != null) {
                        tab1.conversationAdapter.appendToList(message);
                        if (tab1.conversationAdapter.getCount() > 0) {
                            tab1. conversationListView.setSelection(tab1.conversationAdapter.getCount() - 1);
                        }
                    }*/
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
                 /*   if (tab1.conversationAdapter != null) {
                        tab1.conversationAdapter.resetMessageList(messages);
                        if (tab1.conversationAdapter.getCount() > 0) {
                            tab1. conversationListView.setSelection(tab1.conversationAdapter.getCount() - 1);
                        }
                    }*/
                    if (tab2.liveQusetiondapter != null) {
                        tab2.liveQusetiondapter.resetMessageList(list);
                        if (tab2.liveQusetiondapter.getCount() > 0) {
                            tab2. conversationListView.setSelection(tab2.liveQusetiondapter.getCount() - 1);
                        }
                    }
                    break;
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
                    if (tab2.liveQusetiondapter != null) {
                        tab2.liveQusetiondapter.resetMessageList(list);
                        if (tab2.liveQusetiondapter.getCount() > 0) {
                            tab2. conversationListView.setSelection(tab2.liveQusetiondapter.getCount() - 1);
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
                    if (tab2.liveQusetiondapter != null) {
                        tab2.liveQusetiondapter.resetMessageList(list);
                        if (tab2.liveQusetiondapter.getCount() > 0) {
                            tab2. conversationListView.setSelection(tab2.liveQusetiondapter.getCount() - 1);
                        }
                    }
                    break;
            }
            return false;
        }
    });



    //????????????
    @Override
    public void onPlayEvent(int event, Bundle bundle) {
        LogUtils.i("onPlayEvent",event+"+++"+bundle.toString());
        switch (event) {
            // ????????????????????????
            case TXLiveConstants.PLAY_EVT_RCV_FIRST_I_FRAME:
                //mTXLivePlayer.setRenderMode(TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN);
                leaveRlyt.setVisibility(View.GONE);
                leaveTv.setText("?????????????????????????????????????????????~");
                anchorCoverIv.setVisibility(View.GONE);
                wksRly.setVisibility(View.GONE);
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
                /*showErrorAndQuit(ERROR_MSG_NET_DISCONNECTED);*/
                ToastUtil.makeText(context,"????????????,??????????????????????????????,??????????????????,????????????????????????",Toast.LENGTH_SHORT).show();
                finish();
                break;
            // ????????????
            case TXLiveConstants.PLAY_EVT_PLAY_END:
                if (anchorCoverIv.getVisibility() == View.VISIBLE) {
                    anchorCoverIv.setVisibility(View.GONE);
                }
                leaveRlyt.setVisibility(View.VISIBLE);
                leaveTv.setText("?????????????????????????????????????????????~");
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
        leaveTv.setText("?????????????????????????????????????????????~");
        stopPlay(true);
        Intent rstData = new Intent();
        rstData.putExtra(ACTIVITY_RESULT, errorMsg);
        setResult(100, rstData);
    }



    @Override
    public void onNetStatus(Bundle bundle) {
        Log.i("playmessage",bundle.toString());
    }


    /**
     * ????????????
     */
    private static final long BARRAGE_INTERVAL_TIME = 2000;// ?????????????????????????????????
    private static final long ENTITY_INTERVAL_TIME = 13000;// ????????????????????????????????????
    private void playLive() {
//        liveVipIv.setVisibility(isVip == 1 ? View.VISIBLE : View.GONE);
        joinRoom();
    }
    /**
     * ????????????????????????
     */
    public void joinRoom() {
        mTCChatRoomMgr.joinGroup(false, mChatRoomId);
    }

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

        mTXLivePlayer.setRenderMode(TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN);

        mTXLivePlayer.setPlayListener(this);
        mTXLivePlayer.setConfig(mTXPlayConfig);

        int result;
        result = mTXLivePlayer.startPlay(mPlayUrl, mUrlPlayType);

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
        if (TextUtils.isEmpty(mPlayUrl) || (!mPlayUrl.startsWith("http://") && !mPlayUrl.startsWith("https://") && !mPlayUrl.startsWith("rtmp://"))) {
            ToastUtil.makeText(getApplicationContext(), "???????????????????????????????????????rtmp,flv,hls,mp4????????????!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (mPlayUrl.startsWith("rtmp://")) {
            mUrlPlayType = TXLivePlayer.PLAY_TYPE_LIVE_RTMP;
        } else if ((mPlayUrl.startsWith("http://") || mPlayUrl.startsWith("https://")) && mPlayUrl.contains(".flv")) {
            mUrlPlayType = TXLivePlayer.PLAY_TYPE_LIVE_FLV;
        } else {
            ToastUtil.makeText(getApplicationContext(), "?????????????????????????????????????????????rtmp,flv????????????!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public void onJoinGroupCallback(boolean isHost, int code, String msg) {
        if (code == 0) {
            Log.d(TAG, "onJoin group success" + msg);
            List<ChatRoomInfo> messages = new ArrayList<>();
            ChatRoomInfo chatRoomInfo = new ChatRoomInfo(100, "?????????????????????????????????TV?????????????????????????????????????????????????????????????????????", mineInfo.userId, mineInfo.name, mineInfo.iconUrl, mineInfo.level, mineInfo.isVip, "", "", "", "", "0");
            ChatRoomInfo chatRoomInfo1 = new ChatRoomInfo(2, "???????????????", mineInfo.userId, mineInfo.name, mineInfo.iconUrl, mineInfo.level, mineInfo.isVip, "", "", "", "", "0");
            messages.add(0, chatRoomInfo);
            messages.add(1,chatRoomInfo1);
            Message message = Message.obtain();
            message.what = 1;
            message.obj = messages;
            msgHandler.sendMessage(message);

            //??????????????????????????????????????????
            //sendMessage(IMCMD_ENTER_LIVE, "0", "???????????????");
            com.wehang.ystv.bo.Message timMessage = new TextMessage("???????????????");
            mTCChatRoomMgr.sendMessage(timMessage.getMessage());
            //??????????????????
            com.wehang.ystv.utils.Utils.getOnLine(context,lives.sourceId,mTCChatRoomMgr);
            //????????????
            com.wehang.ystv.utils.Utils.sourceQuestions(context,UserLoginInfo.getUserToken(),lives.sourceId,msgHandler,list);
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
        LogUtils.i("errCode",errCode+";"+timMessage.getElement(0).getType());
        //???????????????????????????
        if (errCode == 0) {
            TIMElemType elemType = timMessage.getElement(0).getType();
            if (elemType == TIMElemType.Text) {
                Log.d(TAG, "onSendTextMsgsuccess:" + errCode);
            } else if (elemType == TIMElemType.Custom) {
                //custom????????????????????????,????????????????????????
                Log.d(TAG, "onSendCustomMsgsuccess:" + errCode);
            }
        } else {
            Log.d(TAG, "onSendMsgfail:" + errCode);
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
    public void onReceiveMsg(int type, final ChatRoomInfo chatRoomInfo, String content) {
        LogUtils.i("onReceiveMsg","type:"+type+"String:"+content);
        switch (type) {
            //????????????
            case TCConstants.IMCMD_ENTER_LIVE:
                break;
            //????????????
            case TCConstants.IMCMD_PAILN_TEXT:
                Message msg = Message.obtain();
                msg.what = 0;
                msg.obj = chatRoomInfo;
                msgHandler.sendMessage(msg);
                break;
            //????????????
            case TCConstants.IMCMD_EXIT_LIVE:
                break;

            //????????????
            case IMCMD_ROOM_COLLECT_INFO:
              /*  runOnUiThread(new Runnable() {
                    public void run() {
                        try {
                            String s = chatRoomInfo.msg;
                            GetRanksResult result = new Gson().fromJson(s, new TypeToken<GetRanksResult>() {
                            }.getType());
                            //????????????????????????????????????
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });*/
               // tolkRoomFragment.onLineNumber.setText(content);
                //if (context.)
                if (!com.wehang.ystv.utils.Utils.isNumeric(content)){
                    return;
                }
                String string="??????????????? (?????????"+content+")";
                if (tolkOrAsk==2){
                    string="???????????? (?????????"+content+")";
                }
                tolkRoomFragment.sendWhat.setHint(string);
                tolkRoomFragment.sendWhatTv.setHint(string);
                onlineWhatchNumeber.setText(content+"???????????????");
                onlineNum= Integer.parseInt(content);
                break;
            case IMCMD_DELETE://??????????????????????????????
            case IMCMD_COLOSE:
            case IMCMD_LIVE_OVER:
                //onGroupDelete();
                if (anchorCoverIv.getVisibility() == View.VISIBLE) {
                    anchorCoverIv.setVisibility(View.GONE);
                }
                leaveRlyt.setVisibility(View.VISIBLE);
                leaveTv.setText("????????????");
                stopPlay(true);
                break;
            case IMCMD_QUESTION:
                //??????????????????
                if (content.equals("")){
                    //?????????????????????
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
                        Message message = Message.obtain();
                        message.what = 3;
                        msgHandler.sendMessage(message);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                break;

            case IMCMD_ZHUBOJOINROOM:
                //???????????????
                if (content.equals("0")){
                    isPhonePush=false;
                }else {
                    isPhonePush=true;
                }

                stopPlay(true);
                tolkRoomFragment.showbig.setVisibility(View.VISIBLE);
                tolkRoomFragment.mBtnLinkMic.setVisibility(View.VISIBLE);
                instance.setIslock(false);
                wksRly.setVisibility(View.GONE);
                anchorCoverIv.setVisibility(View.VISIBLE);
                findViewById(R.id.jzTs).setVisibility(View.GONE);
                //????????????
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
                break;
        }
    }



    public List<QuestionMessages>list=new ArrayList<>();
    @Override
    public void onGroupDelete() {
        if (anchorCoverIv.getVisibility() == View.VISIBLE) {
            anchorCoverIv.setVisibility(View.GONE);
        }
        leaveRlyt.setVisibility(View.VISIBLE);
        leaveTv.setText("????????????");
        mTXCloudVideoView.onPause();
        stopPlay(true);
    }

    @Override
    public void onRefresh() {

    }
    //??????
    @Override
    public void onHostChatUnreadMessage(long unreadMessageNum) {
       /* mUnreadMessageNum += unreadMessageNum;
        if (mUnreadMessageNum <= 0) {
            //unreadLabelTv.setVisibility(View.INVISIBLE);
        } else if (mUnreadMessageNum > 0 && mUnreadMessageNum < 100) {
           *//* unreadLabelTv.setVisibility(View.VISIBLE);
            unreadLabelTv.setText(String.valueOf(mUnreadMessageNum));*//*
        } else if (unreadMessageNum >= 100) {
           *//* unreadLabelTv.setVisibility(View.VISIBLE);
            unreadLabelTv.setText("99+");*//*
        }*/
    }

    @Override
    public void onBackPressed() {
        if (WhatBigImg.isShown()){
            WhatBigImg.setVisibility(View.GONE);
            return;
        }
        if (mIsBeingLinkMic){
            ToastUtil.makeText(context,"??????????????????",Toast.LENGTH_SHORT).show();
            return;
        }
        if (instance.isPortrait()){
            finish();
        }else {
            instance.toggleScreen();
        }

    }



    private int pushRotation;
    public void initLinkMic() {
        if (mTXLivePusherLink!=null){
            return;
        }
        mTXLivePusherLink = new TXLivePusher(this);
        mTXLivePushConfigLink = new TXLivePushConfig();
        //????????????

        mTXLivePushConfigLink.setVideoEncoderXMirror(true);
        mTXLivePusherLink.setMirror(true);
        //??????????????????????????????
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pause_publish, options);

        pushRotation = TXLiveConstants.VIDEO_ANGLE_HOME_RIGHT;
        mTXLivePushConfigLink.setHomeOrientation(pushRotation);
        mTXLivePushConfigLink.setPauseImg(bitmap);
        mTXLivePushConfigLink.setPauseFlag(TXLiveConstants.PAUSE_FLAG_PAUSE_VIDEO | TXLiveConstants.PAUSE_FLAG_PAUSE_AUDIO);
        mTXLivePushConfigLink.enableAEC(true);
        mTXLivePushConfigLink.setANS(true);
        mTXLivePushConfigLink.setTouchFocus(false);
        mTXLivePushConfigLink.setHardwareAcceleration(ENCODE_VIDEO_AUTO);
        mTXLivePushConfigLink.setAudioSampleRate(48000);
        mTXLivePushConfigLink.setVideoResolution(TXLiveConstants.VIDEO_RESOLUTION_TYPE_320_480);
        mTXLivePushConfigLink.setMaxVideoBitrate(300);
        mTXLivePushConfigLink.setMinVideoBitrate(100);
        mTXLivePushConfigLink.setAutoAdjustBitrate(true);
        mTXLivePushConfigLink.setAutoAdjustStrategy(TXLiveConstants.AUTO_ADJUST_BITRATE_STRATEGY_2);


       // mTXLivePushConfigLink.setBeautyFilter(4, 3);
        mTXLivePushConfigLink.setBeautyFilter(1,7,3);

        //??????????????????
        mTXLivePusherLink.setRenderRotation(TXLiveConstants.RENDER_ROTATION_PORTRAIT);

        mTXLivePusherLink.setVideoQuality(TXLiveConstants.VIDEO_QUALITY_LINKMIC_MAIN_PUBLISHER,false,true);

        mTXLivePusherLink.setPushListener(this);
        mTXLivePusherLink.setConfig(mTXLivePushConfigLink);



        mTXLivePlayerLink = new TXLivePlayer(this);
        mTXLivePlayConfigLink = new TXLivePlayConfig();
        mTXLivePlayConfigLink.setAutoAdjustCacheTime(true);
        mTXLivePlayConfigLink.setMinAutoAdjustCacheTime(0.2f);
        mTXLivePlayConfigLink.setMaxAutoAdjustCacheTime(0.2f);
        mTXLivePlayConfigLink.enableAEC(true);



        //mTXLivePlayerLink.C(TXLiveConstants.RENDER_ROTATION_PORTRAIT);
        mTXLivePlayerLink.setRenderMode(TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN);

        mTXLivePlayerLink.setConfig(mTXLivePlayConfigLink);
        mTXLivePlayerLink.setPlayListener(new TXLivePlayListener());
        mTXLivePlayerLink.enableHardwareDecode(true);


        mTCLinkMicMgr = TCLinkMicMgr.getInstance();
        mTCLinkMicMgr.setmChatRoomId(mChatRoomId);
        mTCLinkMicMgr.setLinkMicListener(this);

        mTXCloudVideoView.disableLog(true);
        mSmallVideoView.disableLog(true);
    }
    private void startLinkPlay() {
        if (mTXLivePlayerLink != null) {
            startLoading();
            mTXLivePlayerLink.setPlayerView(mSmallVideoView);
            mTXLivePlayerLink.startPlay(mLinkMicPlayUrl, TXLivePlayer.PLAY_TYPE_LIVE_RTMP_ACC);
        }
    }
    //????????????
    public void startLinkMic() {
        if (mIsBeingLinkMic || mWaitingLinkMicResponse) {
            return;
        }
        //??????????????????group???????????????
        mTCLinkMicMgr.sendLinkMicRequest(mHostUserId);

        mWaitingLinkMicResponse = true;

        tolkRoomFragment.mBtnLinkMic.setEnabled(false);
        tolkRoomFragment.mBtnLinkMic.setImageResource(R.drawable.lianmai);

        mHandler.removeCallbacks(mRunnableLinkMicTimeOut);
        mHandler.postDelayed(mRunnableLinkMicTimeOut, 10000);   //10?????????
    }
    private Runnable mRunnableLinkMicTimeOut = new Runnable() {
        @Override
        public void run() {
           /* //????????????
            if (mWaitingLinkMicResponse) {
                mWaitingLinkMicResponse = false;
                tolkRoomFragment.mBtnLinkMic.setEnabled(true);
                tolkRoomFragment.mBtnLinkMic.setImageResource(R.drawable.jushouj);
                ToastUtil.makeText(getApplicationContext(), "?????????????????????????????????????????????", Toast.LENGTH_SHORT).show();
            }*/
        }
    };

    private void startLoading() {
        mLinkMicLoadingBg.setVisibility(View.VISIBLE);
        mLinkMicLoading.setVisibility(View.VISIBLE);
        mLinkMicLoading.setImageResource(R.drawable.linkmic_loading);
        AnimationDrawable ad = (AnimationDrawable) mLinkMicLoading.getDrawable();
        if (ad != null) {
            ad.start();
        }
    }

    public void stopLoading() {
        mLinkMicLoadingBg.setVisibility(View.GONE);
        mLinkMicLoading.setVisibility(View.GONE);
        AnimationDrawable ad = (AnimationDrawable) mLinkMicLoading.getDrawable();
        if (ad != null) {
            ad.stop();
        }
    }

    public synchronized void stopLinkMic() {
        if (mIsBeingLinkMic) {
            mIsBeingLinkMic = false;
            mTCLinkMicMgr.sendMemberExitNotify(mHostUserId, mineInfo.userId);
        }

        if (mWaitingLinkMicResponse) {
            mWaitingLinkMicResponse = false;
            mHandler.removeCallbacks(mRunnableLinkMicTimeOut);
        }

        if (tolkRoomFragment.mBtnLinkMic != null) {
            tolkRoomFragment. mBtnLinkMic.setEnabled(true);
            tolkRoomFragment.mBtnLinkMic.setImageResource(R.drawable.jushouj);
        }

        /*if (mBtnSwitchCamera != null) {
            mBtnSwitchCamera.setVisibility(View.INVISIBLE);
        }*/

        stopLinkPlay();
        stopLinkPush();
    }
    private void stopLinkPlay() {
        if (mTXLivePlayerLink != null) {
            stopLoading();
            mTXLivePlayerLink.stopPlay(true);
        }
    }
    public TXLivePushConfig mTXPushConfig = new TXLivePushConfig();
    private void startLinkPush(String pusherUrl) {

        //????????????
        if (mTXLivePusherLink != null) {
            mTXCloudVideoView.setVisibility(View.VISIBLE);
            mTXLivePusherLink.startCameraPreview(mTXCloudVideoView);
            mTXLivePusherLink.startPusher(pusherUrl);
        }

        //tolkRoomFragment.  mBtnSwitchCamera.setVisibility(View.VISIBLE);
        startLoading();
    }
    private void stopLinkPush() {
        if (mTXLivePusherLink != null) {
            mTXLivePusherLink.stopCameraPreview(true);
            mTXLivePusherLink.stopPusher();
        }
    }
    @Override
    public void onPushEvent(int event, Bundle bundle) {
        LogUtils.i("onPushEvent",event+"+++"+bundle.toString());
        if (event == TXLiveConstants.PUSH_EVT_PUSH_BEGIN && mIsBeingLinkMic) {                     //????????????????????????
            //????????????????????????????????????
            mTCLinkMicMgr.sendMemberJoinNotify(mHostUserId, mineInfo.userId, mLinkMicNotifyUrl);

            startLinkPlay();
        } else if (event == TXLiveConstants.PUSH_ERR_NET_DISCONNECT) {            //????????????????????????
            handleLinkMicFailed("???????????????????????????");
        } else if (event == TXLiveConstants.PUSH_ERR_OPEN_CAMERA_FAIL) {//????????????????????????
            handleLinkMicFailed("???????????????????????????????????????");
        } else if (event == TXLiveConstants.PUSH_ERR_OPEN_MIC_FAIL) { //????????????????????????
            if (mIsBeingLinkMic) {
                handleLinkMicFailed("???????????????????????????????????????");
            }
        }
    }

    @Override
    public void onReceiveLinkMicRequest(String strUserId, String strNickName) {

    }
    private AlertDialog jieshoudialog;
    @Override
    public void onReceiveLinkMicResponse(String strUserId, final int responseType, final String strParams) {
        LogUtils.i("mWaitingLinkMicResponse",mWaitingLinkMicResponse);
        if (mWaitingLinkMicResponse == false) {
            return;
        }

        mWaitingLinkMicResponse = false;
        tolkRoomFragment.mBtnLinkMic.setEnabled(true);

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (Constant.LINKMIC_RESPONSE_TYPE_ACCEPT == responseType) {
                    mSessionID = strParams;
                    /*if (mSessionID == null || mSessionID.length() == 0) {
                        return;
                    }*/
                    mIsBeingLinkMic = true;
                    tolkRoomFragment. mBtnLinkMic.setImageResource(R.drawable.lianmai);
                   // ToastUtil.makeText(getApplicationContext(), "????????????????????????????????????????????????", Toast.LENGTH_SHORT).show();
                  jieshoudialog=CommonUtil.showYesNoDialog(context, "?????????????????????????????????????????????????????????", "??????", "??????", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == DialogInterface.BUTTON_POSITIVE) {
                                //??????????????????
                                initLinkMic();
                                instance. isPortrait = false;
                                // ????????????????????????
                                LiveWatchNewActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                                LiveWatchNewActivity.this.instance.setIslock(true);
                                tolkRoomFragment.lock_img.setChecked(true);
                                tolkRoomFragment.lock_img.setImageResource(R.drawable.shangsu_suo);
                                tolkRoomFragment.showbig.setEnabled(false);
                                tolkRoomFragment.showbig.setVisibility(View.GONE);
                                //
                                getLineMicPushUrl();
                            }else {
                                mIsBeingLinkMic = false;
                                tolkRoomFragment.mBtnLinkMic.setImageResource(R.drawable.jushouj);
                                Utils.endLink(sourceId,context);
                                mTCLinkMicMgr.sendLinkMicResponse(lives.userId, Constant.LINKMIC_RESPONSE_TYPE_REJECT, "??????????????????");
                            }
                        }
                    });
                    jieshoudialog.show();

                } else if (Constant.LINKMIC_RESPONSE_TYPE_REJECT == responseType) {
                    String reason = strParams;
                    if (reason != null && reason.length() > 0) {
                        ToastUtil.makeText(getApplicationContext(), reason, Toast.LENGTH_SHORT).show();
                    }
                    mIsBeingLinkMic = false;
                    tolkRoomFragment.mBtnLinkMic.setImageResource(R.drawable.jushouj);
                }
            }
        });
    }
    /**
     * ??????????????????
     */
    private void getLineMicPushUrl() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("userId", mineInfo.userId);
        params.put("sourceId", sourceId);
        LogUtils.i("getLineMicPushUrl",mineInfo.userId+",,"+sourceId);
        HttpTool.doPost(VideoApplication.applicationContext, UrlConstant.GET_LINE_MIC_PUSH_URL, params, false, new TypeToken<BaseResult<LineMicInfo>>() {
        }.getType(), new HttpTool.OnResponseListener() {
            @Override
            public void onSuccess(BaseData data) {
                LineMicInfo lineMicInfo = (LineMicInfo) data;
                mLinkMicNotifyUrl = lineMicInfo.connectReduceUrl;
                LogUtils.i("getLineMicPushUrl",mineInfo.userId+",,"+sourceId);
                if (lineMicInfo != null && !TextUtils.isEmpty(lineMicInfo.connectPushUrl)) {
                    //?????????CDN??????
                    stopPlay(true);
                    startLinkPush(lineMicInfo.connectPushUrl);
                }

            }

            @Override
            public void onError(int errorCode) {
                ToastUtil.makeText(VideoApplication.applicationContext, "??????????????????????????????", Toast.LENGTH_SHORT).show();
                mTCLinkMicMgr.sendMemberExitNotify(mHostUserId, mineInfo.userId);
                mIsBeingLinkMic = false;
                mWaitingLinkMicResponse = false;
                tolkRoomFragment. mBtnLinkMic.setImageResource(R.drawable.jushouj);
            }
        });
    }
    @Override
    public void onReceiveMemberJoinNotify(String strUserId, String strPlayUrl) {

    }

    @Override
    public void onReceiveMemberExitNotify(String strUserId) {

    }

    @Override
    public void onReceiveKickedOutNotify() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                stopLoading();
                if (jieshoudialog.isShowing()){
                    jieshoudialog.dismiss();
                }
                ToastUtil.makeText(getApplicationContext(), "?????????????????????", Toast.LENGTH_SHORT).show();
                //??????????????????
                LiveWatchNewActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
                tolkRoomFragment. lock_img.setChecked(false);
                tolkRoomFragment. lock_img.setImageResource(R.drawable.shangsuo);
                LiveWatchNewActivity.this.instance.setIslock(false);
                tolkRoomFragment.showbig.setEnabled(true);
                tolkRoomFragment.showbig.setVisibility(View.VISIBLE);
                //

                //????????????
                stopLinkMic();
                //?????????CDN????????????
                startPlay();
            }
        });
    }

    private class TXLivePlayListener implements ITXLivePlayListener {

        @Override
        public void onPlayEvent(int event, Bundle param) {
            LogUtils.i("onPlayEvent",event+"+++"+param.toString());
            if (event == TXLiveConstants.PLAY_EVT_PLAY_BEGIN||event ==TXLiveConstants.PLAY_EVT_PLAY_PROGRESS) {
                //??????????????????????????????????????????loading
                stopLoading();
            } else if (event == TXLiveConstants.PLAY_ERR_NET_DISCONNECT || event == TXLiveConstants.PLAY_EVT_PLAY_END) {
                handleLinkMicFailed("???????????????????????????????????????");
            }
            if (event==TXLiveConstants.PLAY_WARNING_READ_WRITE_FAIL){
                handleLinkMicFailed("?????????????????????????????????");
            }
        }

        @Override
        public void onNetStatus(Bundle bundle) {

        }
    }
    private void handleLinkMicFailed(String message) {
        ToastUtil.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        com.wehang.ystv.utils.Utils.endLink(lives.sourceId,context);
        //????????????
        stopLinkMic();

        //?????????CDN????????????
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (!mIsBeingLinkMic) { //??????????????????????????????
                    startPlay();
                }
            }
        });

        //??????loading
        stopLoading();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==1&&resultCode==RESULT_OK){
            //???????????????????????????????????????
            com.wehang.ystv.utils.Utils.sourceQuestions(context,UserLoginInfo.getUserToken(),lives.sourceId,mTCChatRoomMgr);
            //mTCChatRoomMgr.sendQusetionMessage();
        }
    }


}

