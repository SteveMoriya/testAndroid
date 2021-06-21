package com.wehang.ystv.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.LogUtils;
import com.google.gson.reflect.TypeToken;
import com.tencent.TIMManager;
import com.wehang.ystv.R;
import com.wehang.ystv.VideoApplication;
import com.wehang.ystv.bo.LiveNeed;
import com.wehang.ystv.bo.UserInfo;
import com.wehang.ystv.fragment.DynamicFragment;
import com.wehang.ystv.fragment.HomeFragment;
import com.wehang.ystv.fragment.LiveFragment;
import com.wehang.ystv.fragment.MessageFragment;
import com.wehang.ystv.fragment.PersonalCenterFragment;
import com.wehang.ystv.interfaces.UrlConstant;
import com.wehang.ystv.utils.CommonUtil;
import com.wehang.ystv.utils.ToastUtil;
import com.wehang.ystv.utils.UserLoginInfo;
import com.wehang.ystv.utils.Utils;
import com.whcd.base.activity.BaseActivity;
import com.whcd.base.interfaces.BaseData;
import com.whcd.base.interfaces.BaseResult;
import com.whcd.base.interfaces.HttpTool;
import com.whcd.base.widget.BottomPopupWindow;
import com.whcd.base.widget.CustomProgressDialog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class HomeActivity extends BaseActivity {
    public static HomeActivity instance = null;
    public static String TAG="HomeActivity";
    private List<Fragment> fragments;
    private FragmentManager fragmentManager;
    private HomeFragment homeFragment;
    private DynamicFragment dynamicFragment;
    private LiveFragment liveFragment;
    private MessageFragment messageFragment;
    private PersonalCenterFragment personalCenterFragment;

    private RadioButton homeLayout, myLayout,messageLayout,dynamicLayout;
    private ImageView liveImg;


    private FrameLayout rootLayout;
    @Override
    protected int getLayoutResId() {
        return R.layout.activity_home;
    }

    @Override
    protected void initTitleBar() {

    }

    @Override
    protected void initView() {

        rootLayout= findViewById(R.id.home_root_layout);
        homeLayout =  findViewById(R.id.homeLayout);
        myLayout =  findViewById(R.id.myLayout);
        messageLayout=findViewById(R.id.MessageLayout);
        dynamicLayout=findViewById(R.id.DymamicLayout);
        liveImg=findViewById(R.id.liveImg);
        homeLayout.setOnClickListener(this);
        myLayout.setOnClickListener(this);
        messageLayout.setOnClickListener(this);
        dynamicLayout.setOnClickListener(this);
        liveImg.setOnClickListener(this);

        initFragmet();

    }
    private void initFragmet(){

        fragments = new ArrayList<Fragment>();
        fragmentManager = getSupportFragmentManager();
        homeFragment=new HomeFragment();
        dynamicFragment=new DynamicFragment();
        messageFragment=new MessageFragment();
        personalCenterFragment=new PersonalCenterFragment();

        fragments.add(homeFragment);
        fragments.add(dynamicFragment);
        fragments.add(messageFragment);
        fragments.add(personalCenterFragment);
        switchFragment(0);
    }
    private UserInfo userInfo;
    @Override
    protected void initData(Bundle bundle) {
        if (UserLoginInfo.isLogin(context)) {
            userInfo=UserLoginInfo.getUserInfo(context);
            Map<String, String> params = new HashMap<String, String>();
            params.put("token", userInfo.token);
            HttpTool.doPost(context, UrlConstant.MYDATA, params, false, new TypeToken<BaseResult<UserInfo>>() {
            }.getType(), new HttpTool.OnResponseListener() {
                @Override
                public void onSuccess(BaseData data) {
                    UserInfo info = (UserInfo) data;
                    if (info != null) {
                        UserLoginInfo.saveUserInfo(VideoApplication.applicationContext.getApplicationContext(), info);
                    }
                }

                @Override
                public void onError(int errorCode) {
                }
            });
        }
    }

    private void switchFragment(int index) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Fragment fragment = fragmentManager.findFragmentByTag(fragments.get(index).getClass().getName());
        if (fragment == null) {
            transaction.add(R.id.main_container, fragments.get(index), fragments.get(index).getClass().getName());
        } else {
            transaction.show(fragment);
        }
        if (currentSelectIndex != -1) {
            transaction.hide(fragments.get(currentSelectIndex));
        }
        currentSelectIndex = index;
        transaction.commitAllowingStateLoss();
    }
    private static final int EXIT_APP = 0x110;
    private static final int FINISH_APP=0x111;
    private int currentSelectIndex = -1;
    private boolean isExit = false;
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case EXIT_APP:
                    isExit = false;
                    break;
                case FINISH_APP:
                    //使用腾讯IM
                    TIMManager.getInstance().logout();
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("token", UserLoginInfo.getUserToken());
                    HttpTool.doPost(context, UrlConstant.LOGOUT, params, false, new TypeToken<BaseResult<BaseData>>() {
                    }.getType(), new HttpTool.OnResponseListener() {
                        @Override
                        public void onSuccess(BaseData baseData) {
                            HomeActivity.this.finish();
                        }
                        @Override
                        public void onError(int errorCode) {
                            UserLoginInfo.loginOverdue(context, errorCode);
                        }
                    });
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    private BottomPopupWindow popupWindow;
    /**
     * 选择直播或者视频
     */
    public void showChoosePhotoPopupwindow() {
        View popView = LayoutInflater.from(this).inflate(R.layout.iitem_chose_liveorship, rootLayout, false);
        View contentView = popView.findViewById(R.id.pop_conent);
        TextView tv= (TextView) popView.findViewById(R.id.item_chose_prTV);

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) contentView.getLayoutParams();
        params.gravity = Gravity.BOTTOM;
        contentView.setTag(1);
        contentView.setLayoutParams(params);
        popView.findViewById(R.id.item_chose_live).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isValidContext(context) && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
                startActivity(new Intent(context, AddLiveActivity.class));

            }
        });
        popView.findViewById(R.id.item_chose_pre).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (isValidContext(context) && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
                Intent intent;
                //进入上传视频

                intent=new Intent(context,AddVedioActivity.class);
                startActivity(intent);
            }
        });
        popView.findViewById(R.id.item_chose_qx).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isValidContext(context) && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
            }
        });
        popupWindow = new BottomPopupWindow(this, popView);
        popupWindow.showAtLocation(rootLayout, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.homeLayout:
                if (currentSelectIndex != 0) {
                    switchFragment(0);
                    Log.i(TAG,"homeLayout");
                }
                break;
            case R.id.liveImg:
               if (UserLoginInfo.getUserInfo().authentication==null){
                   ToastUtil.makeText(context,"请先讲师认证",Toast.LENGTH_SHORT).show();
                   startActivity(new Intent(context, RealNameCertificationActivity.class));
               }else if (!UserLoginInfo.getUserInfo().authentication.equals("1")){
                    ToastUtil.makeText(context,"请先讲师认证",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(context, RealNameCertificationActivity.class));
                }else {
                    showChoosePhotoPopupwindow();
                }
                Log.i(TAG,"live");

                break;

            case R.id.DymamicLayout:
                if (currentSelectIndex != 1) {
                    switchFragment(1);
                    Log.i(TAG,"DymamicLayout");
                }
                break;

            case R.id.MessageLayout:
                if (currentSelectIndex != 2) {
                    switchFragment(2);
                    Log.i(TAG,"MessageLayout");
                }
                break;
            case R.id.myLayout:
                if (currentSelectIndex != 3) {
                    switchFragment(3);
                    Log.i(TAG,"myLayout");
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (alertDialog!=null){
            if (alertDialog.isShowing()){
                Utils.endLiveHome(liveNeed.sourceId,HomeActivity.this);
            }
        }

        if (isExit) {

            handler.sendEmptyMessage(FINISH_APP);

        } else {
            isExit = true;
            ToastUtil.makeText(context,"再点击一次将退出该程序", Toast.LENGTH_SHORT).show();
            handler.sendEmptyMessageDelayed(EXIT_APP, 2000);
        }
    }


    private AlertDialog alertDialog;
    private  LiveNeed liveNeed;
    private boolean isHave=false;
    public void getSourceInfo(){
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", UserLoginInfo.getUserToken());
        dialog = CustomProgressDialog.show(context, "加载中...", true, null);
        HttpTool.doPost(VideoApplication.applicationContext, UrlConstant.GETSOURCEINFO, params, false, new TypeToken<BaseResult<LiveNeed>>() {
        }.getType(), new HttpTool.OnResponseListener() {
            @Override
            public void onSuccess(BaseData data) {
                dialog.dismiss();
                 liveNeed= (LiveNeed) data;

                if (liveNeed!=null){
                    LogUtils.i("LiveNeed",liveNeed.toString());
                    isHave=true;
                    if (liveNeed.sourceId!=null){
                        String str1=null;
                        String str2=null;
                        if (liveNeed.isPhonePush==1){
                            str1="还处于离线状态，是否继续直播？";
                            str2="继续";

                        }else {
                            str1="在PC端直播中，是否进入直播？";
                            str2="进入";
                        }
                        alertDialog =  CommonUtil.mustChoseDialog(context, "您的直播"+liveNeed.sourceTitle+str1, str2, "结束", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == DialogInterface.BUTTON_POSITIVE) {
                                    Intent intent=new Intent(context,LivePushActivity.class);
                                    Bundle bundle=new Bundle();
                                    bundle.putSerializable("data",liveNeed);
                                    intent.putExtra("bundle",bundle);
                                    context. startActivity(intent);
                                }else {
                                    Utils.endLiveHome(liveNeed.sourceId,HomeActivity.this);
                                }

                            }
                        });
                        alertDialog.show();

                    }else {
                        return;
                    }

                }
            }

            @Override
            public void onError(int errorCode) {
                dialogDismiss();
                UserLoginInfo.loginOverdue(context, errorCode);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (alertDialog!=null){
            if(alertDialog.isShowing()){
                alertDialog.dismiss();
            }
        }

        getSourceInfo();
    }
}
