package com.wehang.txlibrary;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePushConfig;
import com.tencent.rtmp.TXLivePusher;
import com.tencent.rtmp.ui.TXCloudVideoView;
import com.wehang.txlibrary.widget.TCAudioControl;

import java.io.UnsupportedEncodingException;



public class LivePushActivity extends AppCompatActivity implements BeautyDialogFragment.OnBeautyParamsChangeListener,View.OnClickListener,TCInputTextMsgDialog.OnTextSendListener{
    private TXLivePushConfig mLivePushConfig;
    private TXCloudVideoView mCaptureView;
    private TXLivePusher mTXLivePusher;
    private BeautyDialogFragment.BeautyParams   mBeautyParams = new BeautyDialogFragment.BeautyParams();
    private BeautyDialogFragment mBeautyDialogFragment;
    //闪光灯
    private Button mFlashView;

    private boolean mFlashOn = false;

    private TCInputTextMsgDialog mInputTextMsgDialog;


    private TCAudioControl mAudioCtrl;
    protected Button mBtnAudioCtrl;
    private LinearLayout mAudioPluginLayout;

    private String rtmpUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_spl);



        mTXLivePusher = new TXLivePusher(this);
        mLivePushConfig = new TXLivePushConfig();
        //设置横屏推流
        mTXLivePusher.setRenderRotation(0);
        pushRotation = TXLiveConstants.VIDEO_ANGLE_HOME_RIGHT;
        mLivePushConfig.setHomeOrientation(pushRotation);
        mTXLivePusher.setConfig(mLivePushConfig);

        rtmpUrl = "rtmp://8036.livepush.myqcloud.com/live/8036_22222222222?bizid=8036&txSecret=965443018321dfaad78553c9f3b08507&txTime=5981F6FF";


        mTXLivePusher.startPusher(rtmpUrl);
        mCaptureView = (TXCloudVideoView)findViewById(R.id.video_view);
        mTXLivePusher.startCameraPreview(mCaptureView);
        mCaptureView.setVisibility(View.VISIBLE);

        mBeautyDialogFragment = new BeautyDialogFragment();
        mBeautyDialogFragment.setBeautyParamsListner(mBeautyParams,this);
        initView();
    }

    private void initView() {
        mFlashView = (Button) findViewById(R.id.flash_btn);

        mInputTextMsgDialog = new TCInputTextMsgDialog(this, R.style.InputDialog);
        mInputTextMsgDialog.setmOnTextSendListener(this);


        mAudioCtrl = (TCAudioControl) findViewById(R.id.layoutAudioControlContainer);
        mAudioPluginLayout = (LinearLayout) findViewById(R.id.audio_plugin);
        //mAudioCtrl.setPluginLayout(mAudioPluginLayout);
        if (mAudioCtrl==null){
            Log.i("null++","1");
        }
        if (mAudioPluginLayout==null){
            Log.i("null++","2");
        }
        if (mCaptureView != null) {
            mCaptureView.disableLog(!mShowLog);
        }
        mCaptureView.enableHardwareDecode(true);


    }

    @Override
    protected void onResume() {
        super.onResume();
        mCaptureView.onResume();
    }
    private int pushRotation;
    @Override
    public void onBeautyParamsChange(BeautyDialogFragment.BeautyParams params, int key) {
        switch (key) {
            case BeautyDialogFragment.BEAUTYPARAM_BEAUTY:
            case BeautyDialogFragment.BEAUTYPARAM_WHITE:
                if (mTXLivePusher != null) {
                  //  mTXLivePusher.setBeautyFilter(params.mBeautyProgress, params.mWhiteProgress);
                }
                break;
            case BeautyDialogFragment.BEAUTYPARAM_FACE_LIFT:
                if (mTXLivePusher != null) {
                    mTXLivePusher.setFaceSlimLevel(params.mFaceLiftProgress);
                }
                break;
            case BeautyDialogFragment.BEAUTYPARAM_BIG_EYE:
                if (mTXLivePusher != null) {
                    mTXLivePusher.setEyeScaleLevel(params.mBigEyeProgress);
                }
                break;
            case BeautyDialogFragment.BEAUTYPARAM_FILTER:
                if (mTXLivePusher != null) {
                    mTXLivePusher.setFilter(TCUtils.getFilterBitmap(getResources(), params.mFilterIdx));
                }
                break;
            case BeautyDialogFragment.BEAUTYPARAM_MOTION_TMPL:
                if (mTXLivePusher != null) {
                    mTXLivePusher.setMotionTmpl(params.mMotionTmplPath);
                }
                break;
            case BeautyDialogFragment.BEAUTYPARAM_GREEN:
                if (mTXLivePusher != null) {
                    mTXLivePusher.setGreenScreenFile(TCUtils.getGreenFileName(params.mGreenIdx));
                }
                break;
            default:
                break;
        }
    }
    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.switch_cam) {
            mTXLivePusher.switchCamera();

        } else if (i == R.id.flash_btn) {
            if (!mTXLivePusher.turnOnFlashLight(!mFlashOn)) {
                Toast.makeText(getApplicationContext(), "前置摄像头才能打开闪光灯", Toast.LENGTH_SHORT).show();
                return;
            }
            mFlashOn = !mFlashOn;
            mFlashView.setBackgroundDrawable(mFlashOn ?
                    getResources().getDrawable(R.drawable.icon_flash_pressed) :
                    getResources().getDrawable(R.drawable.icon_flash));


        } else if (i == R.id.beauty_btn) {
            if (mBeautyDialogFragment.isAdded())
                mBeautyDialogFragment.dismiss();
            else
                mBeautyDialogFragment.show(getFragmentManager(), "");

        } else if (i == R.id.btn_close) {
            showComfirmDialog(TCConstants.TIPS_MSG_STOP_PUSH, false);
//                for(int i = 0; i< 100; i++)
//                    mHeartLayout.addFavor();

        } else if (i == R.id.btn_message_input) {
            showInputMsgDialog();

        } else if (i == R.id.btn_audio_ctrl) {
            if (null != mAudioCtrl) {
                mAudioCtrl.setVisibility(mAudioCtrl.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            }

        } else if (i == R.id.btn_audio_effect) {
            mAudioCtrl.setVisibility(mAudioCtrl.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);

        } else if (i == R.id.btn_audio_close) {
            mAudioCtrl.stopBGM();
            mAudioPluginLayout.setVisibility(View.GONE);
            mAudioCtrl.setVisibility(View.GONE);

        } else if (i == R.id.btn_log) {
            showLog();

        } else {//mLayoutFaceBeauty.setVisibility(View.GONE);

        }
    }

    //log相关
    protected boolean mShowLog = false;
    protected void showLog() {
        mShowLog = !mShowLog;
        if (mCaptureView != null) {
            mCaptureView.disableLog(!mShowLog);
        }
        Button liveLog = (Button) findViewById(R.id.btn_log);
        if (mShowLog) {
            if (liveLog != null) liveLog.setBackgroundResource(R.drawable.icon_log_on);

        } else {
            if (liveLog != null) liveLog.setBackgroundResource(R.drawable.icon_log_off);
        }
    }

    /**
     * 发消息弹出框
     */
    private void showInputMsgDialog() {
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = mInputTextMsgDialog.getWindow().getAttributes();

        lp.width = (int) (display.getWidth()); //设置宽度
        mInputTextMsgDialog.getWindow().setAttributes(lp);
        mInputTextMsgDialog.setCancelable(true);
        mInputTextMsgDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        mInputTextMsgDialog.show();
    }


    public void showDetailDialog() {
        //确认则显示观看detail
       /* stopRecordAnimation();
        DetailDialogFragment dialogFragment = new DetailDialogFragment();
        Bundle args = new Bundle();
        args.putString("time", TCUtils.formattedTime(mSecond));
        args.putString("heartCount", String.format(Locale.CHINA, "%d", lHeartCount));
        args.putString("totalMemberCount", String.format(Locale.CHINA, "%d", lTotalMemberCount));
        dialogFragment.setArguments(args);
        dialogFragment.setCancelable(false);
        if (dialogFragment.isAdded())
            dialogFragment.dismiss();
        else
            dialogFragment.show(getFragmentManager(), "");*/

    }

    /**
     * 显示确认消息
     *
     * @param msg     消息内容
     * @param isError true错误消息（必须退出） false提示消息（可选择是否退出）
     */
    public void showComfirmDialog(String msg, Boolean isError) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.ConfirmDialogStyle);
        builder.setCancelable(true);
        builder.setTitle(msg);

        if (!isError) {
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    stopPublish();
                    quitRoom();
                    //stopRecordAnimation();
                    showDetailDialog();
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
            quitRoom();
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                   // stopRecordAnimation();
                    showDetailDialog();
                }
            });
        }
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.setCanceledOnTouchOutside(false);
    }

    /**
     * 退出房间
     * 包含后台退出与IMSDK房间退出操作
     */
    public void quitRoom() {

        /*mTCChatRoomMgr.deleteGroup();
        mTCPusherMgr.changeLiveStatus(mUserId, TCPusherMgr.TCLiveStatus_Offline);*/
    }

    protected void stopPublish() {
        if (mTXLivePusher != null) {
            mTXLivePusher.stopCameraPreview(false);
            mTXLivePusher.setPushListener(null);
            mTXLivePusher.stopPusher();
        }
        /*if (mAudioCtrl != null) {
            mAudioCtrl.unInit();
            mAudioCtrl = null;
        }*/
    }

    @Override
    public void onTextSend(String msg, boolean tanmuOpen) {
        if (msg.length() == 0)
            return;
        try {
            byte[] byte_num = msg.getBytes("utf8");
            if (byte_num.length > 160) {
                Toast.makeText(this, "请输入内容", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return;
        }

      /*  TCChatEntity entity = new TCChatEntity();
        entity.setSenderName("我:");
        entity.setContext(msg);
        entity.setType(TCConstants.TEXT_TYPE);
        notifyMsg(entity);

        if (danmuOpen) {
            if (mDanmuMgr != null) {
                mDanmuMgr.addDanmu(TCUserInfoMgr.getInstance().getHeadPic(), TCUserInfoMgr.getInstance().getNickname(), msg);
            }
            mTCChatRoomMgr.sendDanmuMessage(msg);
        } else {
            mTCChatRoomMgr.sendTextMessage(msg);
        }*/
    }
    //结束推流，注意做好清理工作
    public void stopRtmpPublish() {
        mTXLivePusher.stopCameraPreview(true); //停止摄像头预览
        mTXLivePusher.stopPusher();            //停止推流
        mTXLivePusher.setPushListener(null);   //解绑 listener
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopPublish();
        stopRtmpPublish();
    }
}
