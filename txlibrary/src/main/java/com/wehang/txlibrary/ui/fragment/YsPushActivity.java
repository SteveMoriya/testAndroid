package com.wehang.txlibrary.ui.fragment;

import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CheckableImageButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.rtmp.ITXLivePushListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePlayer;
import com.tencent.rtmp.TXLivePushConfig;
import com.tencent.rtmp.TXLivePusher;
import com.tencent.rtmp.ui.TXCloudVideoView;
import com.umeng.socialize.UMShareAPI;
import com.wehang.txlibrary.R;
import com.wehang.txlibrary.TCConstants;
import com.wehang.txlibrary.base.BaseActivity;
import com.wehang.txlibrary.utils.Utils;
import com.wehang.txlibrary.widget.myview.LiveToolsPopupwindow;
import com.wehang.txlibrary.widget.myview.LookShardToolsPopupwindow;
import com.wehang.txlibrary.widget.myview.shareBean;

import static com.tencent.rtmp.TXLiveConstants.ENCODE_VIDEO_AUTO;
import static com.tencent.rtmp.TXLiveConstants.ENCODE_VIDEO_SOFTWARE;


public class YsPushActivity extends BaseActivity implements View.OnClickListener,LiveToolsPopupwindow.LiveToolsOnItemClickLister, ITXLivePushListener {
    private static final String TAG = "YsPushActivity";
    private TXLivePushConfig mTXPushConfig = new TXLivePushConfig();
    private TXCloudVideoView mTXCloudVideoView;
    private TXLivePusher mTXLivePusher;
    private boolean mPasuing = false;


    private int pushRotation;
    private String mPushUrl;

    protected TextView countDownTv;// 倒计时
    private View decorView;

    private ImageView liveOperateImgBtn;
    public LiveToolsPopupwindow liveToolsPopupwindow;// 闪光、翻转、美颜弹窗

    private LookShardToolsPopupwindow sharePopupwindow;// 分享
    private ImageView share;

    private FrameLayout mengcen;
    private CheckableImageButton wendang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        decorView = getWindow().getDecorView();
        setContentView(R.layout.activity_ys_push);
        // 切换成横屏
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        mPushUrl = "rtmp://8036.livepush.myqcloud.com/live/8036_a3a3eea8d5?bizid=8036&txSecret=c10253d0770af35690a5cd976553e688&txTime=59970EFF";
        initview();
        //一切准备工作就绪，进入倒计时
        showBeforPush(times);



        TelephonyManager tm = (TelephonyManager) this.getApplicationContext().getSystemService(Service.TELEPHONY_SERVICE);
        tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
    }
    final PhoneStateListener listener = new PhoneStateListener(){
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch(state){
                //电话等待接听
                case TelephonyManager.CALL_STATE_RINGING:
                    if (mTXLivePusher != null) mTXLivePusher.pausePusher();
                    break;
                //电话接听
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    if (mTXLivePusher != null) mTXLivePusher.pausePusher();
                    break;
                //电话挂机
                case TelephonyManager.CALL_STATE_IDLE:
                    if (mTXLivePusher != null) mTXLivePusher.resumePusher();
                    break;
            }
        }
    };


    public static final int COUNTDOWN_END_INDEX = 1;
    public static final int COUNTDOWN_DELAY = 800;

    //动画执行次数
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

    //倒计时显示动画
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
                    startPublish();
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
        liveOperateImgBtn = (ImageView) findViewById(R.id.img_bt_switch_camera);
        liveOperateImgBtn.setOnClickListener(this);

        liveToolsPopupwindow = new LiveToolsPopupwindow(liveOperateImgBtn, this, this);
        mengcen = (FrameLayout) findViewById(R.id.push_mc);
        mengcen.setVisibility(View.VISIBLE);


        wendang = (CheckableImageButton) findViewById(R.id.whatch_kj);
        wendang.setOnClickListener(this);


        share = (ImageView) findViewById(R.id.whatch_share);

        //分享
        shareBean shareBean=new shareBean();
        shareBean.brife="分享内容";
        String IP = "http://orzpce18z.bkt.clouddn.com/";
        shareBean.sourcePic=IP+"1ea806ebf88e41a9bed641ef8df4d35e.jpg";
        shareBean.Html="https://hao.360.cn/?src=lm&ls=n0123fca393";
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
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);

    }
    private void startPublish() {
        mTXPushConfig.enableNearestIP(true);//为腾讯云地址，请务必在推流前将其设置为 YES，否则推流质量可能会因为运营商 DNS 不准确而受到影响。
        mTXPushConfig.enableAEC(true);//实现音频通话
        mTXPushConfig.setAutoAdjustBitrate(true);//是否开启 Qos 流量控制，开启后SDK 会根据主播上行网络的好坏自动调整视频码率。相应的代价就是，主播如果网络不好，画面会很模糊且有很多马赛克。
        mTXPushConfig.setAutoAdjustStrategy(TXLiveConstants.AUTO_ADJUST_BITRATE_STRATEGY_2);


        pushRotation = TXLiveConstants.VIDEO_ANGLE_HOME_RIGHT;
        mTXPushConfig.setHomeOrientation(pushRotation);
        if (mTXLivePusher == null) {
            mTXLivePusher = new TXLivePusher(this);
            mTXLivePusher.setPushListener(this);
            mTXPushConfig.setTouchFocus(false);
            mTXPushConfig.setAutoAdjustBitrate(false);
            if (Build.VERSION.SDK_INT < 18) {
                Log.d(TAG, "当前手机API级别过低（最低18）,不支持硬件编码");
                mTXPushConfig.setVideoBitrate(700);
                mTXPushConfig.setVideoResolution(TXLiveConstants.VIDEO_RESOLUTION_TYPE_360_640);
                mTXPushConfig.setHardwareAcceleration(ENCODE_VIDEO_SOFTWARE);
            } else {
                mTXPushConfig.setVideoResolution(TXLiveConstants.VIDEO_RESOLUTION_TYPE_540_960);
                mTXPushConfig.setVideoBitrate(1000);
                mTXPushConfig.setHardwareAcceleration(ENCODE_VIDEO_AUTO);
            }
            //切后台推流图片
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pause_publish, options);
            mTXPushConfig.setPauseImg(bitmap);
            mTXPushConfig.setPauseFlag(TXLiveConstants.PAUSE_FLAG_PAUSE_VIDEO | TXLiveConstants.PAUSE_FLAG_PAUSE_AUDIO);
            //设置横屏推流
            mTXLivePusher.setRenderRotation(0);
            mTXLivePusher.setConfig(mTXPushConfig);
        }
        //设置视频质量：高清
        /*mTXLivePusher.setVideoQuality(TXLiveConstants.VIDEO_QUALITY_HIGH_DEFINITION, false, false);
        mTXCloudVideoView.enableHardwareDecode(true);*/
        mTXCloudVideoView.setVisibility(View.VISIBLE);
       /* if (!mTXLivePusher.setBeautyFilter(7, 3)) {
            Toast.makeText(getApplicationContext(), "当前机型的性能无法支持美颜功能", Toast.LENGTH_SHORT).show();
        }*/
        mTXLivePusher.startCameraPreview(mTXCloudVideoView);
        mTXLivePusher.startPusher(mPushUrl);
    }


    private void stopPublish() {
        mengcen.setVisibility(View.VISIBLE);
        if (mTXLivePusher != null) {
            mTXLivePusher.stopCameraPreview(false);
            mTXLivePusher.setPushListener(null);
            mTXLivePusher.stopPusher();
        }
        /*for (TCPlayItem item : mVecPlayItems) {
            if (!TextUtils.isEmpty(item.mPlayUrl)) {
                item.mTXLivePlayer.stopPlay(false);
            }
        }*/
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.img_bt_switch_camera) {
            liveToolsPopupwindow.showPopWindow();
        }
        if (i == R.id.whatch_kj) {

            if (wendang.isChecked()) {
                wendang.setChecked(false);
                wendang.setImageResource(R.drawable.wendang);
            } else {
                wendang.setChecked(true);
                wendang.setImageResource(R.drawable.gbwendang);
            }
        }

        if (i == R.id.whatch_share) {
            sharePopupwindow.showPopWindow();
        }
    }

    private boolean mFlashTurnOn = false;

    @Override
    public void liveToolsOnItemClick(int position, boolean isChecked, int frontOrBack) {
        if (position == 0) {
            Log.i("logpop", 1 + "；" + isChecked + ";" + frontOrBack);
            boolean c = true;
            //mFlashTurnOn为true表示打开，否则表示关闭
            if (isChecked) {
                mTXLivePusher.turnOnFlashLight(false);

            } else {
                c = mTXLivePusher.turnOnFlashLight(true);
            }
            if (!c) {
                Toast.makeText(this.getApplicationContext(),
                        "打开闪光灯失败:绝大部分手机不支持前置闪光灯!", Toast.LENGTH_SHORT).show();
            }

        }
        if (position == 1) {
            Log.i("logpop", 2 + "");
            mTXLivePusher.switchCamera();
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
           /* if (isChecked) {
                mTXLivePusher.setBeautyFilter(7, 3);
            } else {
                mTXLivePusher.setBeautyFilter(0, 0);
            }*/

        }
    }

    @Override
    public void onPushEvent(int event, Bundle bundle) {
        Log.i("pushevent",event+"");
        if (mTXCloudVideoView != null) {
            mTXCloudVideoView.setLogText(null, bundle, event);
        }
        if (event < 0) {
            if (event == TXLiveConstants.PUSH_ERR_NET_DISCONNECT) {//网络断开，弹对话框强提醒，推流过程中直播中断需要显示直播信息后退出
               // showComfirmDialog(TCConstants.ERROR_MSG_NET_DISCONNECTED, true);
                final AlertDialog dialog=   Utils.showEerMesage(this, TCConstants.ERROR_MSG_NET_DISCONNECTED, "确定", "取消",null);
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        finish();
                    }
                });

            } else if (event == TXLiveConstants.PUSH_ERR_OPEN_CAMERA_FAIL) {//未获得摄像头权限，弹对话框强提醒，并退出
                Toast.makeText(getApplicationContext(), bundle.getString(TCConstants.ERROR_MSG_OPEN_CAMERA_FAIL), Toast.LENGTH_SHORT).show();
                finish();
            } else if (event == TXLiveConstants.PUSH_ERR_OPEN_MIC_FAIL || event == TXLiveConstants.PUSH_ERR_MIC_RECORD_FAIL) { //未获得麦克风权限，弹对话框强提醒，并退出
                Toast.makeText(getApplicationContext(), bundle.getString(TXLiveConstants.EVT_DESCRIPTION), Toast.LENGTH_SHORT).show();
            } else {
                //其他错误弹Toast弱提醒，并退出
                Toast.makeText(getApplicationContext(), bundle.getString(TXLiveConstants.EVT_DESCRIPTION), Toast.LENGTH_SHORT).show();

                mTXCloudVideoView.onPause();
                //TIM NEED : TCPusherMgr.getInstance().changeLiveStatus(mUserId, TCPusherMgr.TCLiveStatus_Offline);
                finish();
            }
        }

        if (event == TXLiveConstants.PUSH_WARNING_HW_ACCELERATION_FAIL) {
            Log.d(TAG, "当前机型不支持视频硬编码");
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
     * 显示确认消息
     *
     * @param msg     消息内容
     * @param isError true错误消息（必须退出） false提示消息（可选择是否退出）
     *
     */

    public void showComfirmDialog(String msg, Boolean isError) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.ConfirmDialogStyle);
        builder.setCancelable(true);
        builder.setTitle(msg);

        if (!isError) {
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    stopPublish();
                    /*quitRoom();
                    stopRecordAnimation();
                    showDetailDialog();*/
                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        } else {
            //当情况为错误的时候，直接停止推流
            stopPublish();
          /*  quitRoom();
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    stopRecordAnimation();
                    showDetailDialog();
                }
            });*/
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
        /*for (int i = 0; i < mVecPlayItems.size(); ++i) {
            TXCloudVideoView videoView = mVecPlayItems.get(i).mVideoView;
            if (videoView != null) {
                videoView.onResume();
            }
        }

        for (TCPlayItem item : mVecPlayItems) {
            if (!TextUtils.isEmpty(item.mPlayUrl) && !TextUtils.isEmpty(item.mUserID)) {
                startLoading(item);
                item.mTXLivePlayer.startPlay(item.mPlayUrl, TXLivePlayer.PLAY_TYPE_LIVE_RTMP_ACC);
            }
        }*/
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPasuing = true;
        if (mTXLivePusher != null) {
//            mTXLivePusher.stopCameraPreview(false);
            mTXLivePusher.pausePusher();
        }
        /*for (TCPlayItem item : mVecPlayItems) {
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

        stopPublish();
        /*mTCChatRoomMgr.removeMsgListener();
        for (int i = 0; i < mVecPlayItems.size(); ++i) {
            TXCloudVideoView videoView = mVecPlayItems.get(i).mVideoView;
            if (videoView != null) {
                videoView.onDestroy();
            }
        }*/
    }
}
