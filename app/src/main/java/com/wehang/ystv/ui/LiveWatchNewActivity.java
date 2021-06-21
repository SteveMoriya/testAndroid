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
    //聊天室
    public TCChatRoomMgr mTCChatRoomMgr;
    public String mChatRoomId;
    private Timer barragetimer = new Timer();// 定时刷新下标
    private int barrageIndex;// 连续弹幕时记录弹幕的顺序
    private int barrageProcessIndex;// 连续弹幕时记录该连续状态下过程下标
    private int barrageTotalIndex;// 连续弹幕时记录该连续状态下的总得个数




    private RelativeLayout anchorCoverIv;
    public RelativeLayout leaveRlyt;
    public TextView leaveTv;
//    private HostChatInfo mHostChatInfo;
    private long mUnreadMessageNum = 0;
    private TextView unreadLabelTv;

    private RoundImageView liveHeadRiv;
    private ImageView liveVipIv;
    private String mHostUserId;// 主播ID
    public String mLiveRoomId;
    private String mIconUrl;// 主播头像
    private int isVip;// 主播头像
    //为开播处理
    private RelativeLayout wksRly;
    private TextView kcName,kcTime;
    
    private int screenHeight;

    //操作层应该是ViewPAGER
    private MyViewPager operationViewPager;
    private HomeFragmentPagerAdapter operationAdapter;
    private List<Fragment> operationFragment = new ArrayList<Fragment>();

    //播放大小的容器

    private RelativeLayout contioner_r;
    
    private TolkRoomFragment tolkRoomFragment;
    private TolkFragment tab1;
    private QuestinFragment tab2;

    private View decorView;

    //聊天室和提问的viewpager和tablayout
    private MyTabLayout tabLayout;
    private MyViewPager viewPager;
    private LayoutInflater mInflater;

    private List<String> mTitle = new ArrayList<String>();
    private List<Fragment> mFragment = new ArrayList<Fragment>();



    //连麦相关
    private String mLinkMicPlayUrl;
    public String sourceId;//直播ID
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
    
    //控制屏幕的横竖屏
    public ScreenSwitchUtils instance;
    private int addHeiht=0;

    //聊天室显示的是聊天信息还是提问 1,聊天，2，问题 默认是1
    public int  tolkOrAsk=1;
    //聊天TAB显示人数
    private CheckedTextView tab_online;
    private CheckedTextView tab_twTv;

    public Lives lives;
    //课件相关
    public IndexViewPager classViewpager;
    public RelativeLayout kjRly;
    //查看大图
    public ImageView WhatBigImg;

    //把碎片的3个图片放到ACTIVITY里
    public ImageView xiaoxi,share,like;
    public LinearLayout likeRly,xiaoxiRly,shareRly;
    //在线观看人数
    public TextView onlineWhatchNumeber;
    public TextView liveTitle;



    public boolean isPhonePush=true;
    public FrameLayout rootLayout;
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        //底部虚拟按键view
        View decorView = getWindow().getDecorView();
        addHeiht=decorView.getHeight();
        int[]Heiht=getAccurateScreenDpi(this);
        Log.i("qiehuan","onWindowFocusChanged:"+Heiht[0]+"<"+Heiht[1]);

        addHeiht=Heiht[0]>Heiht[1]?Heiht[0]:Heiht[1];
    }
    /**
     * 获取精确的屏幕大小
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

        //android:configChanges="keyboardHidden|orientation|screenSize"设置此属性
        // // 切换成横屏，竖屏都不会去使用layout_prot和land,可以不重启生命周期
        //mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        //项目需求横竖屏显示不同的内容
        if (instance.isPortrait()){

            layoutParams.width=screenWidth;
            layoutParams.height= DensityUtil.dip2px(this,230);
            contioner_r.setLayoutParams(layoutParams);
            tolkRoomFragment.showbig.setImageResource(R.drawable.fdimg);
            //去掉锁屏都不显示
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
            //去掉锁屏都不显示
            //tolkRoomFragment.lock_img.setVisibility(View.VISIBLE);
            tolkRoomFragment.lock_img.setVisibility(View.GONE);
            tolkRoomFragment.wenti.setVisibility(View.VISIBLE);
            tolkRoomFragment.duihua.setVisibility(View.VISIBLE);

            tolkRoomFragment.tiwenLiner.setVisibility(View.VISIBLE);
            tolkRoomFragment.conversationListView.setVisibility(View.VISIBLE);
        }

        //0-l  1r 2 down
       /* //横竖屏切换的时候如果popwindow isshow()
        if (sharePopupwindow.isShowing()){
            sharePopupwindow.dismiss();

        }*/


    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 先隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        screenWidth = display.getWidth();
        //让布局向上移来显示软键盘
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


        //是否手机推流
        if (lives.isPhonePush==0){
            isPhonePush=false;
        }else {
            isPhonePush=true;
            //一切准备工作就绪，进入倒计时
        }
        initView();

        //开启自定义重力感应
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
                //电话等待接听
                case TelephonyManager.CALL_STATE_RINGING:
                    if (mTXLivePlayer != null) mTXLivePlayer.setMute(true);
                    break;
                //电话接听
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    if (mTXLivePlayer != null) mTXLivePlayer.setMute(true);
                    break;
                //电话挂机
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
        //初始化消息回调，当前存在：获取文本消息、用户加入/退出消息、群组解散消息、点赞消息、弹幕消息回调
        mTCChatRoomMgr = TCChatRoomMgr.getInstance();

        //仅当直播时才进行执行加入直播间逻辑
        mTCChatRoomMgr.setMessageListener(this);
       /* mHostChatInfo = HostChatInfo.getInstance();
        mHostChatInfo.setUnreadMessageListener(this);
        mHostChatInfo.setHostUserId(mHostUserId);*/
        //checkLiveStatus();

    }
    private HomeFragmentPagerAdapter pagerAdapter;

    private void initView() {

        //分享背景
        rootLayout= (FrameLayout) findViewById(R.id.myshare);
        //mPlayerView即step1中添加的界面view
        mTXCloudVideoView= (TXCloudVideoView)findViewById(R.id.video_view);
       // mTXCloudVideoView.setRenderMode(RENDER_MODE_ADJUST_RESOLUTION);
//创建player对象
        mTXLivePlayer = new TXLivePlayer(this);

        mPlayUrl = liveUserInfo.pullRtmpUrlUrl;
        mChatRoomId=liveUserInfo.chatRoomId;
        mLinkMicPlayUrl=liveUserInfo.pullRtmpUrlUrl;


        leaveRlyt = (RelativeLayout) findViewById(R.id.leaveRlyt);
        leaveTv = (TextView) findViewById(R.id.leaveTv);
        anchorCoverIv = (RelativeLayout) findViewById(R.id.anchor_cover);


        //未开播
        wksRly= (RelativeLayout) findViewById(R.id.wksRly);
        kcName= (TextView) findViewById(R.id.kcName);
        kcTime= (TextView) findViewById(R.id.kcTime);

        //大图
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


//新增
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

        //连麦
        mSmallVideoView = (TXCloudVideoView) findViewById(R.id.small_video_view);



        mLinkMicLoading = (ImageView) findViewById(R.id.linkmic_loading);
        mLinkMicLoadingBg = (FrameLayout) findViewById(R.id.linkmic_loading_bg);


        initLinkMic();
    }


    //实现tablayout选中字体变大
    private View mIndictorView;
    private int peoplesNum=0;
    private int mTabWidth,mLineWidth;
    private int startWidth;
    private List<LinearLayout> mCheckedTvs;
    private RelativeLayout.LayoutParams params;
    private void initIndicator() {
        mIndictorView = findViewById(R.id.indictorView);
        //每一个tablyout的宽度
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
                CommonUtil.showYesNoDialog(context, "当前网络是非Wifi环境，是否观看？", "确定", "取消", new DialogInterface.OnClickListener() {
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
            kcTime.setText("距离开播还有"+Utils.getHowTime(lives.startTime));
            instance.setIslock(true);
            findViewById(R.id.jzTs).setVisibility(View.GONE);
        }
    }
    //获取焦点时显示

    @Override
    protected void onResume() {
        super.onResume();
        //mTXCloudVideoView.onResume();

    /*    if (lives.sourceType==1){
            if (!NetworkUtils.isWifi(context)){
                CommonUtil.showYesNoDialog(context, "当前网络是非Wifi环境，是否观看？", "确定", "取消", new DialogInterface.OnClickListener() {
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
            kcTime.setText("距离开播还有"+Utils.getHowTime(lives.startTime));
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
     * 退出聊天室
     */
    public void quitRoom() {
        //获取在线人数
        mTCChatRoomMgr.sendOnlne((onlineNum-1)+"");
        mTCChatRoomMgr.quitGroup(mChatRoomId);
        mTCChatRoomMgr.removeMsgListener();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
       /* mLivePlayer.stopPlay(true); // true代表清除最后一帧画面
        mPlayerView.onDestroy();*/
        mTXCloudVideoView.onDestroy();
        stopPlay(true);
        quitRoom();
        //可以参照FLB的处理方法，监听到发出信息后才退出去
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
                // 刷新消息
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
                // 初次加载
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
                    LogUtils.i("caonim","进入拉去");
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



    //监听播放
    @Override
    public void onPlayEvent(int event, Bundle bundle) {
        LogUtils.i("onPlayEvent",event+"+++"+bundle.toString());
        switch (event) {
            // 获取到第一帧图片
            case TXLiveConstants.PLAY_EVT_RCV_FIRST_I_FRAME:
                //mTXLivePlayer.setRenderMode(TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN);
                leaveRlyt.setVisibility(View.GONE);
                leaveTv.setText("请稍等，主播暂时离开，马上回来~");
                anchorCoverIv.setVisibility(View.GONE);
                wksRly.setVisibility(View.GONE);
                break;
            // 视频播放开始，如果有转菊花什么的这个时候该停了
            case TXLiveConstants.PLAY_EVT_PLAY_BEGIN:
                break;
            // 视频播放进度，会通知当前进度和总体进度
            case TXLiveConstants.PLAY_EVT_PLAY_PROGRESS:

                break;
            // 网络断连, 已启动自动重连 (重连超过三次就直接抛送 PLAY_ERR_NET_DISCONNECT 了)
            case TXLiveConstants.PLAY_WARNING_RECONNECT:
                break;
            // 视频播放loading，如果能够恢复，之后会有BEGIN事件
            case TXLiveConstants.PLAY_EVT_PLAY_LOADING:
                break;
            // 网络断连,且经多次重连抢救无效,可以放弃治疗,更多重试请自行重启播放
            case TXLiveConstants.PLAY_ERR_NET_DISCONNECT:
                /*showErrorAndQuit(ERROR_MSG_NET_DISCONNECTED);*/
                ToastUtil.makeText(context,"网络断连,且经多次重连抢救无效,可以放弃治疗,请连接网络后重连",Toast.LENGTH_SHORT).show();
                finish();
                break;
            // 直播结束
            case TXLiveConstants.PLAY_EVT_PLAY_END:
                if (anchorCoverIv.getVisibility() == View.VISIBLE) {
                    anchorCoverIv.setVisibility(View.GONE);
                }
                leaveRlyt.setVisibility(View.VISIBLE);
                leaveTv.setText("请稍等，主播暂时离开，马上回来~");
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
        leaveTv.setText("请稍等，主播暂时离开，马上回来~");
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
     * 直播准备
     */
    private static final long BARRAGE_INTERVAL_TIME = 2000;// 弹幕动画播放的间隔时间
    private static final long ENTITY_INTERVAL_TIME = 13000;// 定时释放动画下标间隔时间
    private void playLive() {
//        liveVipIv.setVisibility(isVip == 1 ? View.VISIBLE : View.GONE);
        joinRoom();
    }
    /**
     * 观众加入房间操作
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



/*//自动模式
        mPlayConfig.setAutoAdjustCacheTime(true);
        mPlayConfig.setMinAutoAdjustCacheTime(1);
        mPlayConfig.setMaxAutoAdjustCacheTime(5);*/

//极速模式
        mTXPlayConfig.setAutoAdjustCacheTime(true);
        mTXPlayConfig.setMinAutoAdjustCacheTime(1);
        mTXPlayConfig.setMaxAutoAdjustCacheTime(1);

/*//流畅模式
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
            ToastUtil.makeText(getApplicationContext(), "播放地址不合法，目前仅支持rtmp,flv,hls,mp4播放方式!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (mPlayUrl.startsWith("rtmp://")) {
            mUrlPlayType = TXLivePlayer.PLAY_TYPE_LIVE_RTMP;
        } else if ((mPlayUrl.startsWith("http://") || mPlayUrl.startsWith("https://")) && mPlayUrl.contains(".flv")) {
            mUrlPlayType = TXLivePlayer.PLAY_TYPE_LIVE_FLV;
        } else {
            ToastUtil.makeText(getApplicationContext(), "播放地址不合法，直播目前仅支持rtmp,flv播放方式!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public void onJoinGroupCallback(boolean isHost, int code, String msg) {
        if (code == 0) {
            Log.d(TAG, "onJoin group success" + msg);
            List<ChatRoomInfo> messages = new ArrayList<>();
            ChatRoomInfo chatRoomInfo = new ChatRoomInfo(100, "系统消息：欢迎来到优生TV，请不要在直播间散播不良信息，发现后直接封号。", mineInfo.userId, mineInfo.name, mineInfo.iconUrl, mineInfo.level, mineInfo.isVip, "", "", "", "", "0");
            ChatRoomInfo chatRoomInfo1 = new ChatRoomInfo(2, "加入直播间", mineInfo.userId, mineInfo.name, mineInfo.iconUrl, mineInfo.level, mineInfo.isVip, "", "", "", "", "0");
            messages.add(0, chatRoomInfo);
            messages.add(1,chatRoomInfo1);
            Message message = Message.obtain();
            message.what = 1;
            message.obj = messages;
            msgHandler.sendMessage(message);

            //加入群组成功后，发送加入消息
            //sendMessage(IMCMD_ENTER_LIVE, "0", "加入直播间");
            com.wehang.ystv.bo.Message timMessage = new TextMessage("加入直播间");
            mTCChatRoomMgr.sendMessage(timMessage.getMessage());
            //获取在线人数
            com.wehang.ystv.utils.Utils.getOnLine(context,lives.sourceId,mTCChatRoomMgr);
            //获取提问
            com.wehang.ystv.utils.Utils.sourceQuestions(context,UserLoginInfo.getUserToken(),lives.sourceId,msgHandler,list);
        } else if (NO_LOGIN_CACHE == code) {
            ToastUtil.makeText(context, "聊天未登录" + msg, Toast.LENGTH_SHORT).show();
            finish();
            TXLog.d(TAG, "onJoin group failed" + msg);
        } else {
            ToastUtil.makeText(context, "加入聊天室失败" + msg, Toast.LENGTH_SHORT).show();
            finish();
            TXLog.d(TAG, "onJoin group failed" + msg);
        }
    }

    @Override
    public void onSendMsgCallback(int errCode, TIMMessage timMessage) {
        LogUtils.i("errCode",errCode+";"+timMessage.getElement(0).getType());
        //消息发送成功后回显
        if (errCode == 0) {
            TIMElemType elemType = timMessage.getElement(0).getType();
            if (elemType == TIMElemType.Text) {
                Log.d(TAG, "onSendTextMsgsuccess:" + errCode);
            } else if (elemType == TIMElemType.Custom) {
                //custom消息存在消息回调,此处显示成功失败
                Log.d(TAG, "onSendCustomMsgsuccess:" + errCode);
            }
        } else {
            Log.d(TAG, "onSendMsgfail:" + errCode);
        }
    }
    @Override
    public void onReceiveTextMsg(int type, ChatRoomInfo chatRoomInfo, String content) {
        switch (type){

            //文本消息
            case TCConstants.IMCMD_PAILN_TEXT:
                Message msg = Message.obtain();
                msg.what = 0;
                msg.obj = chatRoomInfo;
                msgHandler.sendMessage(msg);
                break;
        }
    }
    //在线人数
    private int onlineNum=0;
    @Override
    public void onReceiveMsg(int type, final ChatRoomInfo chatRoomInfo, String content) {
        LogUtils.i("onReceiveMsg","type:"+type+"String:"+content);
        switch (type) {
            //进入房间
            case TCConstants.IMCMD_ENTER_LIVE:
                break;
            //文本消息
            case TCConstants.IMCMD_PAILN_TEXT:
                Message msg = Message.obtain();
                msg.what = 0;
                msg.obj = chatRoomInfo;
                msgHandler.sendMessage(msg);
                break;
            //退出房间
            case TCConstants.IMCMD_EXIT_LIVE:
                break;

            //房间信息
            case IMCMD_ROOM_COLLECT_INFO:
              /*  runOnUiThread(new Runnable() {
                    public void run() {
                        try {
                            String s = chatRoomInfo.msg;
                            GetRanksResult result = new Gson().fromJson(s, new TypeToken<GetRanksResult>() {
                            }.getType());
                            //发送文本消息，谁进入房间
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
                String string="我来说两句 (在线："+content+")";
                if (tolkOrAsk==2){
                    string="我来提问 (在线："+content+")";
                }
                tolkRoomFragment.sendWhat.setHint(string);
                tolkRoomFragment.sendWhatTv.setHint(string);
                onlineWhatchNumeber.setText(content+"人在线观看");
                onlineNum= Integer.parseInt(content);
                break;
            case IMCMD_DELETE://后台关闭直播或者删除
            case IMCMD_COLOSE:
            case IMCMD_LIVE_OVER:
                //onGroupDelete();
                if (anchorCoverIv.getVisibility() == View.VISIBLE) {
                    anchorCoverIv.setVisibility(View.GONE);
                }
                leaveRlyt.setVisibility(View.VISIBLE);
                leaveTv.setText("直播结束");
                stopPlay(true);
                break;
            case IMCMD_QUESTION:
                //有人发送提问
                if (content.equals("")){
                    //观众段不做处理
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
                //先停止播放
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
                //重新播放
                if (!NetworkUtils.isWifi(context)){
                    CommonUtil.showYesNoDialog(context, "当前网络是非Wifi环境，是否观看？", "确定", "取消", new DialogInterface.OnClickListener() {
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
        leaveTv.setText("主播离开");
        mTXCloudVideoView.onPause();
        stopPlay(true);
    }

    @Override
    public void onRefresh() {

    }
    //私信
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
            ToastUtil.makeText(context,"请先结束连麦",Toast.LENGTH_SHORT).show();
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
        //设置镜像

        mTXLivePushConfigLink.setVideoEncoderXMirror(true);
        mTXLivePusherLink.setMirror(true);
        //连麦者切后台推流图片
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

        //设置横屏推流
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
    //连麦相关
    public void startLinkMic() {
        if (mIsBeingLinkMic || mWaitingLinkMicResponse) {
            return;
        }
        //请求发送的是group类型的消息
        mTCLinkMicMgr.sendLinkMicRequest(mHostUserId);

        mWaitingLinkMicResponse = true;

        tolkRoomFragment.mBtnLinkMic.setEnabled(false);
        tolkRoomFragment.mBtnLinkMic.setImageResource(R.drawable.lianmai);

        mHandler.removeCallbacks(mRunnableLinkMicTimeOut);
        mHandler.postDelayed(mRunnableLinkMicTimeOut, 10000);   //10秒超时
    }
    private Runnable mRunnableLinkMicTimeOut = new Runnable() {
        @Override
        public void run() {
           /* //不做超时
            if (mWaitingLinkMicResponse) {
                mWaitingLinkMicResponse = false;
                tolkRoomFragment.mBtnLinkMic.setEnabled(true);
                tolkRoomFragment.mBtnLinkMic.setImageResource(R.drawable.jushouj);
                ToastUtil.makeText(getApplicationContext(), "连麦请求超时，主播没有做出回应", Toast.LENGTH_SHORT).show();
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

        //启动推流
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
        if (event == TXLiveConstants.PUSH_EVT_PUSH_BEGIN && mIsBeingLinkMic) {                     //开始推流事件通知
            //向主播发送加入连麦的通知
            mTCLinkMicMgr.sendMemberJoinNotify(mHostUserId, mineInfo.userId, mLinkMicNotifyUrl);

            startLinkPlay();
        } else if (event == TXLiveConstants.PUSH_ERR_NET_DISCONNECT) {            //推流失败事件通知
            handleLinkMicFailed("推流失败，结束连麦");
        } else if (event == TXLiveConstants.PUSH_ERR_OPEN_CAMERA_FAIL) {//未获得摄像头权限
            handleLinkMicFailed("未获得摄像头权限，结束连麦");
        } else if (event == TXLiveConstants.PUSH_ERR_OPEN_MIC_FAIL) { //未获得麦克风权限
            if (mIsBeingLinkMic) {
                handleLinkMicFailed("未获得麦克风权限，结束连麦");
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
                   // ToastUtil.makeText(getApplicationContext(), "主播接受了您的连麦请求，开始连麦", Toast.LENGTH_SHORT).show();
                  jieshoudialog=CommonUtil.showYesNoDialog(context, "主播接受了您的连麦请求，是否开始连麦？", "接受", "拒绝", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == DialogInterface.BUTTON_POSITIVE) {
                                //必须换成横屏
                                initLinkMic();
                                instance. isPortrait = false;
                                // 切换成横屏，锁定
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
                                mTCLinkMicMgr.sendLinkMicResponse(lives.userId, Constant.LINKMIC_RESPONSE_TYPE_REJECT, "观众拒绝连麦");
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
     * 获取推流地址
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
                    //结束从CDN拉流
                    stopPlay(true);
                    startLinkPush(lineMicInfo.connectPushUrl);
                }

            }

            @Override
            public void onError(int errorCode) {
                ToastUtil.makeText(VideoApplication.applicationContext, "获取连麦推流地址失败", Toast.LENGTH_SHORT).show();
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
                ToastUtil.makeText(getApplicationContext(), "主播已断开连麦", Toast.LENGTH_SHORT).show();
                //可以旋转屏幕
                LiveWatchNewActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
                tolkRoomFragment. lock_img.setChecked(false);
                tolkRoomFragment. lock_img.setImageResource(R.drawable.shangsuo);
                LiveWatchNewActivity.this.instance.setIslock(false);
                tolkRoomFragment.showbig.setEnabled(true);
                tolkRoomFragment.showbig.setVisibility(View.VISIBLE);
                //

                //结束连麦
                stopLinkMic();
                //重新从CDN拉流播放
                startPlay();
            }
        });
    }

    private class TXLivePlayListener implements ITXLivePlayListener {

        @Override
        public void onPlayEvent(int event, Bundle param) {
            LogUtils.i("onPlayEvent",event+"+++"+param.toString());
            if (event == TXLiveConstants.PLAY_EVT_PLAY_BEGIN||event ==TXLiveConstants.PLAY_EVT_PLAY_PROGRESS) {
                //开始拉流，或者拉流失败，结束loading
                stopLoading();
            } else if (event == TXLiveConstants.PLAY_ERR_NET_DISCONNECT || event == TXLiveConstants.PLAY_EVT_PLAY_END) {
                handleLinkMicFailed("主播的流拉取失败，结束连麦");
            }
            if (event==TXLiveConstants.PLAY_WARNING_READ_WRITE_FAIL){
                handleLinkMicFailed("网络状态不好，结束连麦");
            }
        }

        @Override
        public void onNetStatus(Bundle bundle) {

        }
    }
    private void handleLinkMicFailed(String message) {
        ToastUtil.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        com.wehang.ystv.utils.Utils.endLink(lives.sourceId,context);
        //结束连麦
        stopLinkMic();

        //重新从CDN拉流播放
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (!mIsBeingLinkMic) { //退到后台不用启动拉流
                    startPlay();
                }
            }
        });

        //结束loading
        stopLoading();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==1&&resultCode==RESULT_OK){
            //提问成功的话，发送提问消息
            com.wehang.ystv.utils.Utils.sourceQuestions(context,UserLoginInfo.getUserToken(),lives.sourceId,mTCChatRoomMgr);
            //mTCChatRoomMgr.sendQusetionMessage();
        }
    }


}

