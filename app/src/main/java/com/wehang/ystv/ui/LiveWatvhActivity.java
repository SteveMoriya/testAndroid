package com.wehang.ystv.ui;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.design.widget.CheckableImageButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.LogUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tencent.TIMConversation;
import com.tencent.TIMConversationType;
import com.tencent.TIMCustomElem;
import com.tencent.TIMElemType;
import com.tencent.TIMManager;
import com.tencent.TIMMessage;
import com.tencent.TIMMessageListener;
import com.tencent.TIMTextElem;
import com.tencent.TIMValueCallBack;
import com.tencent.rtmp.ITXLivePlayListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePlayConfig;
import com.tencent.rtmp.TXLivePlayer;
import com.tencent.rtmp.TXLog;
import com.tencent.rtmp.ui.TXCloudVideoView;


import com.umeng.socialize.media.Constant;
import com.wehang.txlibrary.TCConstants;
import com.wehang.txlibrary.adpter.HomeFragmentPagerAdapter;
import com.wehang.txlibrary.adpter.MyAdapter;
import com.wehang.txlibrary.base.BaseActivity;
import com.wehang.txlibrary.ui.fragment.Tab1;
import com.wehang.txlibrary.utils.DensityUtil;
import com.wehang.txlibrary.utils.DisplayUtils;
import com.wehang.txlibrary.utils.ScreenSwitchUtils;
import com.wehang.txlibrary.widget.MyTabLayout;
import com.wehang.txlibrary.widget.MyViewPager;
import com.wehang.ystv.R;
import com.wehang.ystv.adapter.LiveVideoChartFragmentAdapter;
import com.wehang.ystv.bo.Conversation;
import com.wehang.ystv.bo.UserInfo;
import com.wehang.ystv.fragment.NoWahtFragment;
import com.wehang.ystv.fragment.TolkRoomFragment;
import com.wehang.ystv.interfaces.UrlConstant;
import com.wehang.ystv.interfaces.result.GetGiftResult;
import com.wehang.ystv.interfaces.result.GetRanksResult;
import com.wehang.ystv.logic.ChatRoomInfo;
import com.wehang.ystv.logic.HostChatInfo;
import com.wehang.ystv.logic.TCChatRoomMgr;
import com.wehang.ystv.utils.ToastUtil;
import com.wehang.ystv.utils.UserLoginInfo;
import com.wehang.ystv.widget.BarrageView;
import com.whcd.base.interfaces.BaseData;
import com.whcd.base.interfaces.BaseResult;
import com.whcd.base.interfaces.HttpTool;
import com.whcd.base.widget.RoundImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static com.tencent.rtmp.TXLiveConstants.RENDER_MODE_ADJUST_RESOLUTION;
import static com.wehang.ystv.Constant.ACTIVITY_RESULT;
import static com.wehang.ystv.Constant.ERROR_MSG_NET_DISCONNECTED;
import static com.wehang.ystv.Constant.IMCMD_ENTER_LIVE;
import static com.wehang.ystv.Constant.IMCMD_LIVE_OVER;
import static com.wehang.ystv.Constant.IMCMD_ROOM_COLLECT_INFO;
import static com.wehang.ystv.Constant.NO_LOGIN_CACHE;

public class LiveWatvhActivity extends AppCompatActivity implements View.OnClickListener, MyViewPager.OnScrollChanged ,ITXLivePlayListener,TCChatRoomMgr.TCChatRoomListener,HostChatInfo.HostChatUnreadMessageListener {
    private UserInfo mineInfo;
    private static final String TAG ="LiveWatvhActivity" ;
    private Context context;
    private  TXLivePlayer mTXLivePlayer;
    private TXCloudVideoView mTXCloudVideoView;
    private TXLivePlayConfig mTXPlayConfig = new TXLivePlayConfig();
    private String mPlayUrl;
    private boolean mPlaying = false;
    //聊天室
    public TCChatRoomMgr mTCChatRoomMgr;
    private Timer barragetimer = new Timer();// 定时刷新下标
    private int barrageIndex;// 连续弹幕时记录弹幕的顺序
    private int barrageProcessIndex;// 连续弹幕时记录该连续状态下过程下标
    private int barrageTotalIndex;// 连续弹幕时记录该连续状态下的总得个数


    private String mChatRoomId;

    private ImageView anchorCoverIv;
    public RelativeLayout leaveRlyt;
    public TextView leaveTv;
    private HostChatInfo mHostChatInfo;
    private long mUnreadMessageNum = 0;
    private TextView unreadLabelTv;

    private RoundImageView liveHeadRiv;
    private ImageView liveVipIv;
    private String mHostUserId;// 主播ID
    public String mLiveRoomId;
    private String mIconUrl;// 主播头像
    private int isVip;// 主播头像
    private int screenWidth;
    private int screenHeight;

    //操作层应该是ViewPAGER
    private MyViewPager operationViewPager;
    private HomeFragmentPagerAdapter operationAdapter;
    private List<Fragment>operationFragment = new ArrayList<Fragment>();

    //播放大小的容器

    private RelativeLayout contioner_r;
    private LinearLayout.LayoutParams layoutParams;
    private TolkRoomFragment tolkRoomFragment;

    private View decorView;

    //聊天室和提问的viewpager和tablayout
    private MyTabLayout tabLayout;
    private MyViewPager viewPager;
    private LayoutInflater mInflater;

    private List<String> mTitle = new ArrayList<String>();
    private List<Fragment> mFragment = new ArrayList<Fragment>();

    //控制屏幕的横竖屏
    public ScreenSwitchUtils instance;

    private int addHeiht=0;



    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);


        //底部虚拟按键view
        View decorView = getWindow().getDecorView();
        addHeiht=decorView.getHeight();

        int bottomHeiht=  getBottomKeyboardHeight(this);
        int[]Heiht=getAccurateScreenDpi(this);
        Log.i("qiehuan","onWindowFocusChanged:"+bottomHeiht+":"+Heiht[0]+"<"+Heiht[1]);
        addHeiht=Heiht[0]>Heiht[1]?Heiht[0]:Heiht[1];
    }
    /**
     * 获取底部虚拟键盘的高度
     */
    public int getBottomKeyboardHeight(Activity activity){
        int screenHeight =  getAccurateScreenDpi(activity)[1];
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int heightDifference = screenHeight - dm.heightPixels;
        return heightDifference;
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
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        Log.i("qiehuan","onConfigurationChanged:"+instance.isPortrait()+"");

        //android:configChanges="keyboardHidden|orientation|screenSize"设置此属性
        // // 切换成横屏，竖屏都不会去使用layout_prot和land,可以不重启生命周期
        //mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        //项目需求横竖屏显示不同的内容
        if (instance.isPortrait()){

            layoutParams.width=screenWidth;
            layoutParams.height=DensityUtil.dip2px(this,230);
            contioner_r.setLayoutParams(layoutParams);

            tolkRoomFragment.showbig.setImageResource(R.drawable.fdimg);
            tolkRoomFragment.lock_img.setVisibility(View.GONE);
            tolkRoomFragment.wenti.setVisibility(View.GONE);
            tolkRoomFragment.duihua.setVisibility(View.GONE);
        }else {
            layoutParams.width=addHeiht;
            layoutParams.height=screenWidth;
            contioner_r.setLayoutParams(layoutParams);
            tolkRoomFragment.showbig.setImageResource(R.drawable.suofang);
            tolkRoomFragment.lock_img.setVisibility(View.VISIBLE);
            tolkRoomFragment.wenti.setVisibility(View.VISIBLE);
            tolkRoomFragment.duihua.setVisibility(View.VISIBLE);
        }



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
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        screenWidth = display.getWidth();
        screenHeight = display.getHeight();
        decorView = getWindow().getDecorView();
        //这里使用的是垂直布局
        setContentView(R.layout.activity_live_watvh);

        context=this;
        mineInfo = UserLoginInfo.getUserInfo();
        initView();

        //开启自定义重力感应
        instance = ScreenSwitchUtils.init(this.getApplicationContext());



        Log.i("qiehuan",""+instance.isPortrait()+"");

        initData();
        playLive();
    }


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
        mHostChatInfo = HostChatInfo.getInstance();
        mHostChatInfo.setUnreadMessageListener(this);
        mHostChatInfo.setHostUserId(mHostUserId);
        //checkLiveStatus();

    }
    private HomeFragmentPagerAdapter pagerAdapter;

    private void initView() {
        //mPlayerView即step1中添加的界面view
        mTXCloudVideoView= (TXCloudVideoView)findViewById(R.id.video_view);
        mTXCloudVideoView.setRenderMode(RENDER_MODE_ADJUST_RESOLUTION);
//创建player对象
        mTXLivePlayer = new TXLivePlayer(this);

        mPlayUrl = "http://8036.liveplay.myqcloud.com/live/8036_4284fee55d.flv";
        mChatRoomId="@TGS#3TBKOM4EW";

        leaveRlyt = (RelativeLayout) findViewById(R.id.leaveRlyt);
        leaveTv = (TextView) findViewById(R.id.leaveTv);
        anchorCoverIv = (ImageView) findViewById(R.id.anchor_cover);





        mTXCloudVideoView.setVisibility(View.VISIBLE);


        operationViewPager= (MyViewPager) findViewById(R.id.operationViewPager);
        tolkRoomFragment=new TolkRoomFragment();
        operationFragment.add(tolkRoomFragment);
        operationFragment.add(new NoWahtFragment());
        operationAdapter=new HomeFragmentPagerAdapter(this.getSupportFragmentManager(),operationFragment);
        operationViewPager.setAdapter(operationAdapter);



        contioner_r= (RelativeLayout) findViewById(R.id.contioner_r);
        layoutParams=(LinearLayout.LayoutParams)contioner_r.getLayoutParams();





        mTitle.add("tab1");
        mTitle.add("tab2");

        mFragment.add(new Tab1());
        mFragment.add(new Tab1());
       /* tabLayout= (MyTabLayout) findViewById(R.id.tablayout);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);*/
        viewPager= (MyViewPager) findViewById(R.id.watvhViewpager);

        pagerAdapter = new HomeFragmentPagerAdapter(this.getSupportFragmentManager(),mFragment);
        viewPager.setAdapter(pagerAdapter);
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
        mTabWidth = DisplayUtils.dip2px(this, screenWidth) /4;
        mLineWidth=DisplayUtils.dip2px(this, screenWidth) / 8;
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


















    //获取焦点时显示
    @Override
    protected void onResume() {
        super.onResume();
        mTXCloudVideoView.onResume();
        //mTXLivePlayer.resume();
        startPlay();
        /*if (mSmallVideoView != null) {
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

        mTXCloudVideoView.onPause();
        stopPlay(false);
        /*if (mSmallVideoView != null) {
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
       /* if (mSmallVideoView != null) {
            mSmallVideoView.onDestroy();
        }
        stopLinkMic();

        if (mTCLinkMicMgr != null) {
            mTCLinkMicMgr.setLinkMicListener(null);
            mTCLinkMicMgr = null;
        }*/
        mHostChatInfo.onDestroy();
    }




    Handler msgHandler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                // 刷新消息
                case 0:
                    ChatRoomInfo message = (ChatRoomInfo) msg.obj;
                    if (tolkRoomFragment.conversationAdapter != null) {
                        tolkRoomFragment.conversationAdapter.appendToList(message);
                        if (tolkRoomFragment.conversationAdapter.getCount() > 0) {
                            tolkRoomFragment. conversationListView.setSelection(tolkRoomFragment.conversationAdapter.getCount() - 1);
                        }
                    }
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
                    break;
            }
            return false;
        }
    });



    //监听播放
    @Override
    public void onPlayEvent(int event, Bundle bundle) {
        switch (event) {
            // 获取到第一帧图片
            case TXLiveConstants.PLAY_EVT_RCV_FIRST_I_FRAME:
                mTXLivePlayer.setRenderMode(TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN);
                anchorCoverIv.setVisibility(View.GONE);
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
                showErrorAndQuit(ERROR_MSG_NET_DISCONNECTED);
                break;
            // 直播结束
            case TXLiveConstants.PLAY_EVT_PLAY_END:
                if (anchorCoverIv.getVisibility() == View.VISIBLE) {
                    anchorCoverIv.setVisibility(View.GONE);
                }
                leaveRlyt.setVisibility(View.VISIBLE);
                leaveTv.setText("主播离开");
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
        leaveTv.setText("主播离开");
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


    protected void startPlay() {
        if (!checkPlayUrl()) {
            return;
        }

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


        anchorCoverIv.setVisibility(View.VISIBLE);
        mTXLivePlayer.setPlayerView(mTXCloudVideoView);
        mTXLivePlayer.setRenderRotation(TXLiveConstants.RENDER_ROTATION_PORTRAIT);
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
           // mPlaying = false;
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
            ChatRoomInfo chatRoomInfo = new ChatRoomInfo(100, "系统消息：欢迎来到来吧，请不要在直播间散播不良信息，发现后直接封号。", mineInfo.userId, mineInfo.name, mineInfo.iconUrl, mineInfo.level, mineInfo.isVip, "", "", "", "", "0");
            messages.add(0, chatRoomInfo);
            Message message = Message.obtain();
            message.what = 1;
            message.obj = messages;
            msgHandler.sendMessage(message);
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
    public void onReceiveMsg(int type, final ChatRoomInfo chatRoomInfo, String content) {
        LogUtils.i("onReceiveMsg","type:"+type+"String:"+content);
        switch (type) {
            //进入房间
            case TCConstants.IMCMD_ENTER_LIVE:
                break;
            //退出房间
            case TCConstants.IMCMD_EXIT_LIVE:
                break;
            //文本消息
            case TCConstants.IMCMD_PAILN_TEXT:
                Message msg = Message.obtain();
                msg.what = 0;
                msg.obj = chatRoomInfo;
                msgHandler.sendMessage(msg);
                break;
            //房间信息
            case IMCMD_ROOM_COLLECT_INFO:
                runOnUiThread(new Runnable() {
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
                });
                break;
            case IMCMD_LIVE_OVER:
                onGroupDelete();
                break;
        }
    }

    @Override
    public void onReceiveTextMsg(int type, ChatRoomInfo chatRoomInfo, String content) {

    }

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
        mUnreadMessageNum += unreadMessageNum;
        if (mUnreadMessageNum <= 0) {
            //unreadLabelTv.setVisibility(View.INVISIBLE);
        } else if (mUnreadMessageNum > 0 && mUnreadMessageNum < 100) {
           /* unreadLabelTv.setVisibility(View.VISIBLE);
            unreadLabelTv.setText(String.valueOf(mUnreadMessageNum));*/
        } else if (unreadMessageNum >= 100) {
           /* unreadLabelTv.setVisibility(View.VISIBLE);
            unreadLabelTv.setText("99+");*/
        }
    }

    @Override
    public void onBackPressed() {
        if (instance.isPortrait()){
            UserInfo mineInfo = UserLoginInfo.getUserInfo();
            if (mineInfo != null) {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", mineInfo.token);
                params.put("roomId", getIntent().getStringExtra("roomId"));
                HttpTool.doPost(context, UrlConstant.OUTROOM, params, false, new TypeToken<BaseResult<GetGiftResult>>() {
                }.getType(), new HttpTool.OnResponseListener() {
                    @Override
                    public void onSuccess(BaseData data) {

                    }

                    @Override
                    public void onError(int errorCode) {
                        UserLoginInfo.loginOverdue(LiveWatvhActivity.this, errorCode);
                    }
                });
            }
            finish();
        }else {
            instance.toggleScreen();
        }

    }




}
