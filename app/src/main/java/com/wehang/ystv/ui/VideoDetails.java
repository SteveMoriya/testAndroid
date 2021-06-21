package com.wehang.ystv.ui;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CheckableImageButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.google.gson.reflect.TypeToken;
import com.tencent.rtmp.ITXLivePlayListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePlayConfig;
import com.tencent.rtmp.TXLivePlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;
import com.umeng.socialize.UMShareAPI;
import com.wehang.txlibrary.utils.DensityUtil;
import com.wehang.txlibrary.utils.ScreenSwitchUtils;
import com.wehang.txlibrary.widget.myview.LookShardToolsPopupwindow;
import com.wehang.txlibrary.widget.myview.shareBean;
import com.wehang.ystv.Constant;
import com.wehang.ystv.R;
import com.wehang.ystv.adapter.QuestionAdapter;
import com.wehang.ystv.adapter.VideoAdapter;
import com.wehang.ystv.bo.Lives;
import com.wehang.ystv.bo.QuestionMessages;
import com.wehang.ystv.bo.UserInfo;
import com.wehang.ystv.bo.WXPay;
import com.wehang.ystv.interfaces.UrlConstant;
import com.wehang.ystv.interfaces.result.GetLvesResult;
import com.wehang.ystv.interfaces.result.GetSysMResult;
import com.wehang.ystv.myappview.MyGridView;
import com.wehang.ystv.myappview.MyListview;
import com.wehang.ystv.utils.CommonUtil;
import com.wehang.ystv.utils.GlideUtils;
import com.wehang.ystv.utils.NetworkUtils;
import com.wehang.ystv.utils.ToastUtil;
import com.wehang.ystv.utils.UserLoginInfo;
import com.wehang.ystv.utils.Utils;
import com.whcd.base.activity.BaseActivity;
import com.whcd.base.interfaces.BaseData;
import com.whcd.base.interfaces.BaseResult;
import com.whcd.base.interfaces.HttpTool;
import com.whcd.base.utils.LogUtils;
import com.whcd.base.widget.CustomProgressDialog;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.tencent.rtmp.TXLiveConstants.RENDER_MODE_ADJUST_RESOLUTION;
import static com.tencent.rtmp.TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN;

public class VideoDetails extends com.wehang.txlibrary.base.BaseActivity implements ITXLivePlayListener,View.OnClickListener {

    //收藏、
    private ImageView like_img;
    private TextView live_status,title_item_live,ngk,open_time,rqz,live_price,user_name_itemlive,user_ks_itemlive,user_zw_itemlive,user_yiyuanm_itemlive,live_brife,ljbmTV;

    private MyGridView gridView;
    private VideoAdapter gridAdapter;
    private ImageView detailsImg;

    private MyListview listview;
    private QuestionAdapter listAdapter;
    private List<QuestionMessages> list = new ArrayList<>();

    private TXLivePlayConfig mPlayConfig;
    private TXLivePlayer mLivePlayer;
    private TXCloudVideoView mPlayerView;
    private String flvUrl;


    private RelativeLayout backGroudRl;
    private TextView loadingTv;
    private ImageView mBtnPlay;
    private SeekBar mSeekBar;
    private TextView mTextDuration;
    private TextView mTextStart;

    private boolean mStartSeek = false;
    private boolean mVideoPlay = false;
    private boolean mVideoPause = false;
    private long mTrackingTouchTS = 0;
    private int addHeiht=0;
    private RelativeLayout contioner_r;
    private ImageView showbig;
    private CheckableImageButton lock_img;


    private LookShardToolsPopupwindow sharePopupwindow;// 分享
    private ImageView share,user_tx_itemlive;
    //控制屏幕的横竖屏
    private ScreenSwitchUtils instance;
    private ImageView user_home_head_img1,user_home_head_img2,user_home_head_img3,user_home_head_img4;

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
    private Lives lives;
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        Log.i("qiehuan","onConfigurationChanged:"+instance.isPortrait()+";"+screenWidth+";"+addHeiht);

        //android:configChanges="keyboardHidden|orientation|screenSize"设置此属性
        // // 切换成横屏，竖屏都不会去使用layout_prot和land,可以不重启生命周期
        //mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        //项目需求横竖屏显示不同的内容
        if (instance.isPortrait()){
            layoutParams.width=screenWidth;
            layoutParams.height= DensityUtil.dip2px(this,230);
            contioner_r.setLayoutParams(layoutParams);
            ljbmTV.setVisibility(View.VISIBLE);
            showbig.setImageResource(R.drawable.fdimg);
            lock_img.setVisibility(View.GONE);
        }else {
            layoutParams.width=addHeiht;
            layoutParams.height=screenWidth;
            contioner_r.setLayoutParams(layoutParams);
            showbig.setImageResource(R.drawable.suofang);
            lock_img.setVisibility(View.VISIBLE);
            ljbmTV.setVisibility(View.GONE);
            lock_img.setVisibility(View.GONE);

        }

        //横竖屏切换的时候如果popwindow isshow()
        if (sharePopupwindow.isShowing()){
            sharePopupwindow.dismiss();

        }


    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
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
        setContentView(R.layout.activity_video_details);
       // flvUrl = "http://182.138.26.43/Hannah.mp4";

        Bundle bundle=getIntent().getBundleExtra("bundle");
        lives= (Lives) bundle.getSerializable("data");



        initView();


        //获取相关视频
        getDitils();
    }

    private void initData() {
        lives=detailLive;
        Glide.with(this).load(UrlConstant.IP+lives.sourcePic).placeholder(R.drawable.live_mrjz).error(R.drawable.live_mrjz).into(detailsImg);
        Glide.with(this).load(UrlConstant.IP+lives.iconUrl).placeholder(R.drawable.default_portrait).error(R.drawable.default_portrait).into(user_tx_itemlive);

       if (lives.sourceType==3){
            live_status.setText("视频");
        }else if (lives.sourceType==4){
            live_status.setText("直播回放");
        }
        title_item_live.setText(lives.sourceTitle);
        ngk.setText(lives.classification);
        open_time.setText(lives.startTime);
        rqz.setText("人气值:"+lives.wacthNum+"");
        user_name_itemlive.setText(lives.name);
        user_zw_itemlive.setText(lives.title);
        user_ks_itemlive.setText(lives.departmentName+"|");
        user_yiyuanm_itemlive.setText(lives.hospital);
        live_brife.setText(lives.summary);
        live_price.setText("￥"+lives.price/100+".00");

        if (lives.isBuy==0){
            ljbmTV.setText("立即购买");
        }else {
            ljbmTV.setText("提问");
        }
        if (UserLoginInfo.getUserInfo().userId.equals(lives.userId)){
            ljbmTV.setVisibility(View.GONE);
        }
        if (lives.isCollect==0){
            like_img.setImageResource(R.drawable.like);
        }else {
            like_img.setImageResource(R.drawable.like_r);
        }
    }


    protected void initView() {

        findViewById(R.id.back_img).setOnClickListener(this);
        like_img= (ImageView) findViewById(R.id.like_img);

        like_img.setOnClickListener(this);
        //视频封面
        detailsImg= (ImageView) findViewById(R.id.detailsImg);
        detailsImg.setImageResource(R.drawable.banner_mrjz);

        showbig= (ImageView) findViewById(R.id.showbig);
        showbig.setOnClickListener(this);
        lock_img= (CheckableImageButton) findViewById(R.id.lock_img);
        //control_play.setVisibility(View.VISIBLE);
        lock_img.setOnClickListener(this);
        contioner_r= (RelativeLayout) findViewById(R.id.video_play);
        layoutParams=(RelativeLayout.LayoutParams)contioner_r.getLayoutParams();

        gridView= (MyGridView) findViewById(R.id.xg_vedio_gridview);
        gridAdapter=new VideoAdapter(this,videolist);
        gridView.setAdapter(gridAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent=new Intent(VideoDetails.this, VideoDetails.class);
                Bundle bundle=new Bundle();
                bundle.putSerializable("data",videolist.get(i));
                intent.putExtra("bundle",bundle);
                startActivity(intent);
            }
        });
        listview= (MyListview) findViewById(R.id.tw_vedio_listview);
        listAdapter=new QuestionAdapter(this,questionLists,1);
        listview.setAdapter(listAdapter);

        findViewById(R.id.havebm).setOnClickListener(this);
        ljbmTV= (TextView) findViewById(R.id.ljbmTV);ljbmTV.setOnClickListener(this);


        mPlayerView= (TXCloudVideoView)findViewById(R.id.video_view);
        mPlayerView.setVisibility(View.VISIBLE);

        backGroudRl = (RelativeLayout) findViewById(R.id.backgroud);
        backGroudRl.setOnClickListener(this);
        loadingTv = (TextView) findViewById(R.id.playloading);
        mSeekBar = (SeekBar) findViewById(R.id.seekbar);
        mBtnPlay = (ImageView) findViewById(R.id.btnPlay);
        mTextDuration = (TextView) findViewById(R.id.duration);
        mTextStart = (TextView) findViewById(R.id.play_start);

        mBtnPlay.setOnClickListener(this);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mLivePlayer != null) {
                    mLivePlayer.seek(seekBar.getProgress());
                }
                mTrackingTouchTS = System.currentTimeMillis();
                mStartSeek = false;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mStartSeek = true;
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mTextStart.setText(String.format("%02d:%02d", progress / 60, progress % 60));
            }
        });
        //开启自定义重力感应
        initShare();
        instance = ScreenSwitchUtils.init(this.getApplicationContext());



        live_status= (TextView) findViewById(R.id.live_status);
        title_item_live= (TextView) findViewById(R.id.title_item_live);
        ngk= (TextView) findViewById(R.id.ngk);
        open_time= (TextView) findViewById(R.id.open_time);
        rqz= (TextView) findViewById(R.id.rqz);
        live_price= (TextView) findViewById(R.id.live_price);
        user_tx_itemlive= (ImageView) findViewById(R.id.user_tx_itemlive);
        user_name_itemlive= (TextView) findViewById(R.id.user_name_itemlive);
        user_ks_itemlive= (TextView) findViewById(R.id.user_ks_itemlive);
        user_zw_itemlive= (TextView) findViewById(R.id.user_zw_itemlive);
        user_yiyuanm_itemlive= (TextView) findViewById(R.id.user_yiyuanm_itemlive);
        live_brife= (TextView) findViewById(R.id.live_brife);
        //报名头像
        user_home_head_img1= (ImageView) findViewById(R.id.user_home_head_img1);
        user_home_head_img2= (ImageView) findViewById(R.id.user_home_head_img2);
        user_home_head_img3= (ImageView) findViewById(R.id.user_home_head_img3);
        user_home_head_img4= (ImageView) findViewById(R.id.user_home_head_img4);
        user_home_head_img1.setVisibility(View.GONE);
        user_home_head_img2.setVisibility(View.GONE);
        user_home_head_img3.setVisibility(View.GONE);
        user_home_head_img4.setVisibility(View.GONE);

    //嘉宾栏
        findViewById(R.id.jbLly).setOnClickListener(this);
    }
    private List<UserInfo>userInfos=new ArrayList<>();
    /*private void getHaveBm(){
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", UserLoginInfo.getUserToken());
        params.put("sourceId",lives.sourceId);
        params.put("page","1");
        params.put("pageSize",4+"");
        HttpTool.doPost(this, UrlConstant.ENTEREDDUSERS, params, true, new TypeToken<BaseResult<GetLvesResult>>() {
        }.getType(), new HttpTool.OnResponseListener() {
            @Override
            public void onSuccess(BaseData data) {
                GetLvesResult result= (GetLvesResult) data;
                userInfos.clear();
                userInfos.addAll(result.users);
                com.blankj.utilcode.util.LogUtils.i("userInfos",userInfos.size());
                initHaveBm();
            }

            @Override
            public void onError(int errorCode) {
                com.blankj.utilcode.util.LogUtils.i("info","no");

            }
        });
    }
    private void initHaveBm(){
        user_home_head_img1.setVisibility(View.GONE);
        user_home_head_img2.setVisibility(View.GONE);
        user_home_head_img3.setVisibility(View.GONE);
        user_home_head_img4.setVisibility(View.GONE);
        if (userInfos.size()==1){
            user_home_head_img4.setVisibility(View.VISIBLE);
            GlideUtils.loadHeadImage(this, UrlConstant.IP + userInfos.get(0).iconUrl,user_home_head_img2);
        }else if (userInfos.size()==2){
            user_home_head_img3.setVisibility(View.VISIBLE);
            GlideUtils.loadHeadImage(this, UrlConstant.IP + userInfos.get(0).iconUrl,user_home_head_img3);
            user_home_head_img4.setVisibility(View.VISIBLE);
            GlideUtils.loadHeadImage(this, UrlConstant.IP + userInfos.get(1).iconUrl,user_home_head_img4);
        }
        else if (userInfos.size()==3){
            user_home_head_img2.setVisibility(View.VISIBLE);
            GlideUtils.loadHeadImage(this, UrlConstant.IP + userInfos.get(0).iconUrl,user_home_head_img2);
            user_home_head_img3.setVisibility(View.VISIBLE);
            GlideUtils.loadHeadImage(this, UrlConstant.IP + userInfos.get(1).iconUrl,user_home_head_img3);
            user_home_head_img4.setVisibility(View.VISIBLE);
            GlideUtils.loadHeadImage(this, UrlConstant.IP + userInfos.get(2).iconUrl,user_home_head_img4);
        }
        else if (userInfos.size()>=4){
            user_home_head_img1.setVisibility(View.VISIBLE);
            GlideUtils.loadHeadImage(this, UrlConstant.IP + userInfos.get(0).iconUrl,user_home_head_img1);
            user_home_head_img2.setVisibility(View.VISIBLE);
            GlideUtils.loadHeadImage(this, UrlConstant.IP + userInfos.get(1).iconUrl,user_home_head_img2);
            user_home_head_img3.setVisibility(View.VISIBLE);
            GlideUtils.loadHeadImage(this, UrlConstant.IP + userInfos.get(2).iconUrl,user_home_head_img3);
            user_home_head_img4.setVisibility(View.VISIBLE);
            GlideUtils.loadHeadImage(this, UrlConstant.IP + userInfos.get(3).iconUrl,user_home_head_img4);
        }
    }*/

    private Lives detailLive;
    private List<Lives>videolist=new ArrayList<>();
    private void getDitils(){
        Map<String, String> params;
        params = new HashMap<String, String>();
        params.put("token",UserLoginInfo.getUserToken());
        params.put("sourceId", lives.sourceId);
        dialog = CustomProgressDialog.show(this, "加载中...", true, null);
        HttpTool.doPost(this, UrlConstant.SOURCEDETAIL, params, true, new TypeToken<BaseResult<GetLvesResult>>() {
        }.getType(), new HttpTool.OnResponseListener() {
            @Override
            public void onSuccess(BaseData data) {
                dialog.dismiss();
                GetLvesResult result= ( GetLvesResult) data;
                detailLive=result.source;
                flvUrl=detailLive.videoUrl;
                com.blankj.utilcode.util.LogUtils.i("flvUrl",flvUrl);
                videolist.addAll(detailLive.correlation);
                com.blankj.utilcode.util.LogUtils.i("QUSERYCOURSEWARE",videolist.toString());
                if (videolist.size()>0){
                    gridAdapter.notifyDataSetChanged();
                }
                getQuestion();
                initData();
            }

            @Override
            public void onError(int errorCode) {
                dialog.dismiss();
                com.blankj.utilcode.util.LogUtils.i("info","no");
            }
        });
    }
    private List<QuestionMessages>questionLists=new ArrayList<>();
    private void getQuestion(){
        Map<String, String> params;
        params = new HashMap<String, String>();
        params.put("token", UserLoginInfo.getUserToken());
        params.put("sourceId",lives.sourceId);
        params.put("page","1");
        params.put("pageSize",1000+"");
        HttpTool.doPost(this, UrlConstant.SOURCEQUSETONS, params, true, new TypeToken<BaseResult<GetSysMResult>>() {
        }.getType(), new HttpTool.OnResponseListener() {
            @Override
            public void onSuccess(BaseData data) {
                questionLists.clear();
                GetSysMResult result= ( GetSysMResult) data;
                questionLists.addAll(result.questions);
                com.blankj.utilcode.util.LogUtils.i("QUSERYCOURSEWARE1",questionLists.size());
                if (questionLists.size()>0){
                    listAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(int errorCode) {
                com.blankj.utilcode.util.LogUtils.i("info","no");
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        //getHaveBm();
    }

    private void initShare(){
        //分享
        share = (ImageView) findViewById(R.id.share_img);
        shareBean shareBean=new shareBean();
        shareBean.sourceTitle=lives.sourceTitle;
        shareBean.brife=lives.summary;
        shareBean.sourcePic= UrlConstant.IP+lives.sourcePic;
        shareBean.Html="http://112.74.218.80/share/share.html?sourceId="+lives.sourceId;
        sharePopupwindow = new LookShardToolsPopupwindow(share, this,shareBean);

        sharePopupwindow.setLookShardViedo(new LookShardToolsPopupwindow.mShardViedo() {
            @Override
            public void shardViedo(int position) {
                Log.i("share", position + "");
            }
        });
        share.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==1){
            if (resultCode==RESULT_OK){
                lives.isBuy=1;
                ljbmTV.setText("提问");
            }

        }else if (requestCode==2&&resultCode==RESULT_OK){
            if (resultCode==RESULT_OK){
                getQuestion();
            }
        }
        else {
            UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
        }


    }
    private int mUrlPlayType;
    private boolean checkPlayUrl() {

        String mPlayUrl=flvUrl;
        Log.i("palycheck",flvUrl+"");

        boolean mIsLivePlay=true;//是否直播
        if (TextUtils.isEmpty(mPlayUrl) || (!mPlayUrl.startsWith("http://") && !mPlayUrl.startsWith("https://") && !mPlayUrl.startsWith("rtmp://"))) {
            if (TextUtils.isEmpty(mPlayUrl)){
                Toast.makeText(getApplicationContext(), "播放地址为空", Toast.LENGTH_SHORT).show();

            }else {
                Toast.makeText(getApplicationContext(), "播放地址不合法，目前仅支持rtmp,flv,hls,mp4播放方式!", Toast.LENGTH_SHORT).show();
            }
            dialoDismss();
            return false;
        }
        mIsLivePlay=false;
        if (mIsLivePlay) {
            if (mPlayUrl.startsWith("rtmp://")) {
                mUrlPlayType = TXLivePlayer.PLAY_TYPE_LIVE_RTMP;
            } else if ((mPlayUrl.startsWith("http://") || mPlayUrl.startsWith("https://"))&& mPlayUrl.contains(".flv")) {
                mUrlPlayType = TXLivePlayer.PLAY_TYPE_LIVE_FLV;
            } else {
                Log.i("palycheck","2");
                dialoDismss();
                Toast.makeText(getApplicationContext(), "播放地址不合法，直播目前仅支持rtmp,flv播放方式!", Toast.LENGTH_SHORT).show();

                return false;
            }
        } else {
            if (mPlayUrl.startsWith("http://") || mPlayUrl.startsWith("https://")) {
                if (mPlayUrl.contains(".flv")) {
                    mUrlPlayType = TXLivePlayer.PLAY_TYPE_VOD_FLV;
                } else if (mPlayUrl.contains(".m3u8")) {
                    mUrlPlayType = TXLivePlayer.PLAY_TYPE_VOD_HLS;
                } else if (mPlayUrl.toLowerCase().contains(".mp4")) {
                    mUrlPlayType = TXLivePlayer.PLAY_TYPE_VOD_MP4;
                } else {
                    Log.i("palycheck","3");
                    dialoDismss();
                    Toast.makeText(getApplicationContext(), "播放地址不合法，点播目前仅支持flv,hls,mp4播放方式!", Toast.LENGTH_SHORT).show();

                    return false;
                }
            } else {
                Log.i("palycheck","4");
                dialoDismss();
                Toast.makeText(getApplicationContext(), "播放地址不合法，点播目前仅支持flv,hls,mp4播放方式!", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    private void playLiveBack() {
        dialogplay=CustomProgressDialog.show(this,"加载中",true,null);
        mPlayConfig = new TXLivePlayConfig();
        mLivePlayer = new TXLivePlayer(this);
        mLivePlayer.setPlayerView(mPlayerView);
        mLivePlayer.setConfig(mPlayConfig);
        mLivePlayer.setPlayListener(this);
        mLivePlayer.setRenderMode(TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN);
        mLivePlayer.setRenderRotation(TXLiveConstants.RENDER_ROTATION_PORTRAIT);
       /* if (detailLive.sourceType==4&&detailLive.isPhonePush==1){
            mLivePlayer.setRenderRotation(TXLiveConstants.RENDER_ROTATION_LANDSCAPE);
        }else {

        }*/

        checkPlayUrl();
        int result = mLivePlayer.startPlay(flvUrl, mUrlPlayType);
        if (result == -2) {
            dialoDismss();
            ToastUtil.makeText(this, "回放地址错误，请联系客服", Toast.LENGTH_SHORT).show();
            dialoDismss();
        }
        if (result != 0) {
            mBtnPlay.setImageResource(R.drawable.play_start);
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        if (CommonUtil.isFirstClick()){
            return;
        }
        switch (v.getId()){
            case R.id.back_img:
                finish();
                break;
            //跳转到已经报名
            case R.id.havebm:
                intent= new Intent(this, ConcernActivity.class);
                intent.putExtra("type", 2);
                intent.putExtra(Constant.USERID, lives.sourceId);
                intent.putExtra("name","");
                startActivity(intent);
                break;
            case R.id.ljbmTV:

                if (lives.isBuy==0){
                    if (lives.price==0||UserLoginInfo.getUserInfo().isVip==1){
                        creatOrder();
                    }else {
                        intent=new Intent(this,PayActivity.class);
                        Bundle bundle=new Bundle();
                        bundle.putSerializable("data",lives);
                        intent.putExtra("bundle",bundle);
                        startActivityForResult(intent,1);
                    }

                }else if (lives.isBuy==1){
                    //提问
                    intent=new Intent(this,AddQuseTionActivity.class);
                    Bundle bundle=new Bundle();
                    bundle.putSerializable("data",lives);
                    intent.putExtra("bundle",bundle);
                    this.startActivityForResult(intent,2);

                }
                break;

            case R.id.btnPlay:
                boolean a=UserLoginInfo.getUserInfo().userId.equals(lives.userId);
                if (lives.isBuy==0&&a==false){
                    if (lives.price==0||UserLoginInfo.getUserInfo().isVip==1){
                        creatOrder();
                    }else {
                        intent=new Intent(this,PayActivity.class);
                        Bundle bundle=new Bundle();
                        bundle.putSerializable("data",lives);
                        intent.putExtra("bundle",bundle);
                        startActivityForResult(intent,1);
                    }

                }else {
                    if (mVideoPlay) {
                        if (mVideoPause) {
                            mLivePlayer.resume();
                            mBtnPlay.setImageResource(R.drawable.play_pause);
                        } else {
                            mLivePlayer.pause();
                            mBtnPlay.setImageResource(R.drawable.play_start);
                        }
                        mVideoPause = !mVideoPause;
                    } else {
                        if (!NetworkUtils.isWifi(VideoDetails.this)){
                            CommonUtil.showYesNoDialog(VideoDetails.this, "当前网络是非Wifi环境，是否观看？", "确定", "取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int arg1) {
                                    if (arg1 == DialogInterface.BUTTON_POSITIVE) {
                                        playLiveBack();
                                        mVideoPlay = true;
                                    }else {
                                        finish();
                                    }
                                }
                            }).show();
                        }else {
                            playLiveBack();
                            mVideoPlay = true;
                        }

                    }
                }

                break;
            case R.id.backgroud:
                boolean c=UserLoginInfo.getUserInfo().userId.equals(lives.userId);
                if (lives.isBuy==0&&c==false){
                    if (lives.price==0||UserLoginInfo.getUserInfo().isVip==1){
                        creatOrder();
                    }else {
                        intent=new Intent(this,PayActivity.class);
                        Bundle bundle=new Bundle();
                        bundle.putSerializable("data",lives);
                        intent.putExtra("bundle",bundle);
                        startActivityForResult(intent,1);
                    }

                }else {
                    if (mVideoPlay) {
                        if (mVideoPause) {
                            mLivePlayer.resume();
                            mBtnPlay.setImageResource(R.drawable.play_pause);
                        } else {
                            mLivePlayer.pause();
                            mBtnPlay.setImageResource(R.drawable.play_start);
                        }
                        mVideoPause = !mVideoPause;
                    } else {
                        if (!NetworkUtils.isWifi(VideoDetails.this)){
                            CommonUtil.showYesNoDialog(VideoDetails.this, "当前网络是非Wifi环境，是否观看？", "确定", "取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int arg1) {
                                    if (arg1 == DialogInterface.BUTTON_POSITIVE) {
                                        playLiveBack();
                                        mVideoPlay = true;
                                    }else {
                                        finish();
                                    }
                                }
                            }).show();
                        }else {
                            playLiveBack();
                            mVideoPlay = true;
                        }
                    }
                }

                break;
            case R.id.lock_img:
                if (lock_img.isChecked()){
                    lock_img.setChecked(false);
                    lock_img.setImageResource(R.drawable.shangsuo);
                    instance.setIslock(false);
                    return;
                }else {
                    instance.setIslock(true);
                    lock_img.setChecked(true);
                    lock_img.setImageResource(R.drawable.shangsu_suo);
                    return;
                }
            case R.id.showbig:
                if (instance.islock()){
                    lock_img.setChecked(false);
                    lock_img.setImageResource(R.drawable.shangsuo);
                    instance.setIslock(false);
                }
                instance.toggleScreen();
                break;
            case R.id.share_img:
                sharePopupwindow.showPopWindow();
                break;
            case R.id.like_img:
                collect();
                break;
            case R.id.jbLly:
                //点击头像跳转
                intent=new Intent(VideoDetails.this, UserHomeActivty.class);
                Bundle bundle=new Bundle();
                UserInfo userInfo=new UserInfo();
                userInfo.userId=lives.userId;
                bundle.putSerializable("data",userInfo);
                intent.putExtra("bundle",bundle);
                startActivity(intent);
                break;
        }
    }
    public  void collect(){
        int type=lives.isCollect==0?1:0;
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", UserLoginInfo.getUserToken());
        params.put("sourceId",lives.sourceId);
        params.put("type",type+"");
        final CustomProgressDialog dialog=CustomProgressDialog.show(this,"加载中",true,null);
        HttpTool.doPost1(this, UrlConstant.COLLECT, params, true, new TypeToken<BaseResult<BaseData>>() {
        }.getType(), new HttpTool.OnResponseListener1() {


            @Override
            public void onSuccess(String string) {
                dialog.dismiss();
                if (lives.isCollect==0){
                    like_img.setImageResource(R.drawable.like_r);
                }else {
                    like_img.setImageResource(R.drawable.like);

                }
                if (lives.isCollect==0){
                    lives.isCollect=1;
                }else {
                    lives.isCollect=0;
                }
            }

            @Override
            public void onError(int errorCode) {
                ToastUtil.makeText(VideoDetails.this,"请重试",Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }
    private boolean mPlaying = false;
    private void stopPlayRtmp() {
        mBtnPlay.setImageResource(R.drawable.play_start);
        if (mLivePlayer != null) {
            mLivePlayer.setPlayListener(null);
            mLivePlayer.stopPlay(true);
        }
    }

    @Override
    public void onPlayEvent(int event, Bundle param) {
        switch (event) {
            case TXLiveConstants.PLAY_EVT_PLAY_BEGIN:
                dialoDismss();
                loadingTv.setVisibility(View.GONE);
                break;
            case TXLiveConstants.PLAY_EVT_RCV_FIRST_I_FRAME:
                dialoDismss();
                loadingTv.setVisibility(View.GONE);
                backGroudRl.setVisibility(View.GONE);
                mBtnPlay.setImageResource(R.drawable.play_pause);
                break;
            case TXLiveConstants.PLAY_EVT_PLAY_LOADING:
                dialogplay.show();
                loadingTv.setVisibility(View.VISIBLE);
                break;
            case TXLiveConstants.PLAY_EVT_PLAY_PROGRESS:
                if (mStartSeek) {
                    return;
                }
                int progress = param.getInt(TXLiveConstants.EVT_PLAY_PROGRESS);
                int duration = param.getInt(TXLiveConstants.EVT_PLAY_DURATION);
                int hcjd=param.getInt(TXLiveConstants.NET_STATUS_PLAYABLE_DURATION);
                Log.i("2005",progress+";"+duration+";"+hcjd);
                long curTS = System.currentTimeMillis();

                // 避免滑动进度条松开的瞬间可能出现滑动条瞬间跳到上一个位置
                if (Math.abs(curTS - mTrackingTouchTS) < 500) {
                    return;
                }
                mTrackingTouchTS = curTS;
                if (mSeekBar != null) {
                    mSeekBar.setProgress(progress);
                    mSeekBar.setSecondaryProgress(hcjd);
                }
                if (mTextStart != null) {
                    mTextStart.setText(String.format("%02d:%02d", progress / 60, progress % 60));
                }
                if (mTextDuration != null) {
                    mTextDuration.setText(String.format("%02d:%02d", duration / 60, duration % 60));
                }
                if (mSeekBar != null) {
                    mSeekBar.setMax(duration);
                }
                break;
            case TXLiveConstants.PLAY_ERR_NET_DISCONNECT:
                dialoDismss();
                ToastUtil.makeText(this,"网络断连,且经多次重连抢救无效,可以放弃治疗,更多重试请自行重启播放",Toast.LENGTH_LONG).show();
            case TXLiveConstants.PLAY_EVT_PLAY_END:
                dialoDismss();
                stopPlayRtmp();
                mVideoPlay = false;
                mVideoPause = false;
                backGroudRl.setVisibility(View.VISIBLE);
                if (mTextStart != null) {
                    mTextStart.setText("00:00");
                }
                if (mSeekBar != null) {
                    mSeekBar.setProgress(0);
                    mSeekBar.setSecondaryProgress(0);
                }
                break;
        }
    }
    private CustomProgressDialog dialogplay;
    private void dialoDismss(){
        if (dialogplay!=null){
            if (dialogplay.isShowing()){
                dialogplay.dismiss();
            }
        }
    }
    @Override
    public void onNetStatus(Bundle bundle) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLivePlayer != null) {
            mLivePlayer.stopPlay(true);
        }
        if (mPlayerView != null) {
            mPlayerView.onDestroy();
        }
    }

    @Override
    public void onBackPressed() {
        if (instance.isPortrait()){
            finish();
        }else {
            instance.toggleScreen();
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

    protected CustomProgressDialog dialog;
    private void creatOrder(){
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", UserLoginInfo.getUserToken());
        params.put("sourceId", lives.sourceId);

        dialog = CustomProgressDialog.show(this, "加载中...", true, null);
        HttpTool.doPost(this, UrlConstant.BUTLIVE, params, true, new TypeToken<BaseResult<WXPay>>() {
        }.getType(), new HttpTool.OnResponseListener() {

            @Override
            public void onSuccess(BaseData data) {
                dialog.dismiss();
                lives.isBuy=1;
                ljbmTV.setText("提问");
            }

            @Override
            public void onError(int errorCode) {
                dialog.dismiss();
                UserLoginInfo.loginOverdue(VideoDetails.this, errorCode);
            }
        });
    }


}
