package com.wehang.ystv.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.google.gson.reflect.TypeToken;
import com.wehang.ystv.Constant;
import com.wehang.ystv.R;
import com.wehang.ystv.VideoApplication;
import com.wehang.ystv.bo.PhotoInfo;
import com.wehang.ystv.bo.UserInfo;
import com.wehang.ystv.interfaces.UrlConstant;
import com.wehang.ystv.ui.BuyHistoryActivity;
import com.wehang.ystv.ui.ConcernActivity;
import com.wehang.ystv.ui.MyQusetionActivity;
import com.wehang.ystv.ui.MyVideoActivity;
import com.wehang.ystv.ui.RealNameCertificationActivity;
import com.wehang.ystv.ui.SettingActivity;
import com.wehang.ystv.ui.UpgradeVipActivity;
import com.wehang.ystv.ui.UserHomeActivty;
import com.wehang.ystv.ui.UserProfileActivity;
import com.wehang.ystv.ui.WallteActivity;
import com.wehang.ystv.utils.CommonUtil;
import com.wehang.ystv.utils.UserLoginInfo;
import com.wehang.ystv.utils.Utils;
import com.whcd.base.activity.ImagePagerActivity;
import com.whcd.base.interfaces.BaseData;
import com.whcd.base.interfaces.BaseResult;
import com.whcd.base.interfaces.HttpTool;
import com.whcd.base.interfaces.VolleyTool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class PersonalCenterFragment extends Fragment implements View.OnClickListener{
    //4个模块
    private RelativeLayout liveRlyt,wallteRlyt,collectRlyt,videoRlyt;
    private TextView  liveNumTv,walletNumTV,colletNumTV,videoNumTV;
    //关注，粉丝
    private View fansLlyt, concernLlyt;

    private TextView concernNumTv, fansNumTv, tradesTv, numsTv, sendTv, nameTv , incomingTv, accountTv, userLevleTv,levelImgTV,vipTime;
    private ImageView headImg, sexImg, editInfoImg, isVipImg, levelImg;
    private TextView unreadLabel;
    private TextView identificationStatusTv;

    private UserInfo userInfo;

    public PersonalCenterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_personal_center, container, false);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
    }
    @Override
    public void onResume() {
        super.onResume();
        initData();
        //updateUnreadLabel();
    }
    private void initData() {
        if (UserLoginInfo.isLogin(getActivity())) {
            userInfo=UserLoginInfo.getUserInfo(getActivity());
            Map<String, String> params = new HashMap<String, String>();
            params.put("token", userInfo.token);
            HttpTool.doPost(getActivity(), UrlConstant.MYDATA, params, false, new TypeToken<BaseResult<UserInfo>>() {
            }.getType(), new HttpTool.OnResponseListener() {
                @Override
                public void onSuccess(BaseData data) {
                    UserInfo info = (UserInfo) data;
                    if (info != null) {
                        UserLoginInfo.saveUserInfo(getActivity(),info);
                        initUserMessage();
                    }
                }

                @Override
                public void onError(int errorCode) {
                    UserLoginInfo.loginOverdue(getActivity(), errorCode);
                }
            });
        }
    }

    //缺少
    private void initUserMessage(){
        if (UserLoginInfo.isLogin(getActivity())) {
            userInfo = UserLoginInfo.getUserInfo(getActivity());
            LogUtils.a("userUrl", UrlConstant.IP + userInfo.iconUrl);
            VolleyTool.getImage(UrlConstant.IP + userInfo.iconUrl, headImg, R.drawable.mrtx, R.drawable.mrtx);
            if (userInfo.isVip == 0) {
                isVipImg.setVisibility(View.INVISIBLE);
            } else {
                isVipImg.setVisibility(View.VISIBLE);
            }
            nameTv.setText(userInfo.name);

            if (userInfo.sex == Constant.GIRL) {
                sexImg.setImageResource(R.drawable.icon_home_g);
            } else {
                sexImg.setImageResource(R.drawable.icon_home_b);
            }
            numsTv.setText("ID：" + userInfo.userId);
            fansNumTv.setText(userInfo.fansNum + "");
            concernNumTv.setText(userInfo.focusNum + "");
            if (TextUtils.isEmpty(userInfo.underwrite)) {
                tradesTv.setText("ta好像忘记写签名了...");
            } else {
                tradesTv.setText(userInfo.underwrite);
            }
            liveNumTv.setText(userInfo.liveNum + "个");
            videoNumTV.setText(userInfo.videoNum+ "个");
            colletNumTV.setText(userInfo.collectionNum+ "个");
            walletNumTV.setText(userInfo.balance/100+".00");

            if (userInfo.isVip == 1) {
                vipTime.setText("有效期至" + userInfo.vipTime);
            } else {
                vipTime.setText("");
            }

            if (userInfo.authentication==null){
                identificationStatusTv.setText("未认证");
            } else if (userInfo.authentication.equals("0")){
                identificationStatusTv.setText("未认证");
            }else if (userInfo.authentication.equals("2")){
                identificationStatusTv.setText("认证中");
            } else if (userInfo.authentication.equals("1")) {
                identificationStatusTv.setText("已认证");
            } else if (userInfo.authentication.equals("3")) {
                identificationStatusTv.setText("已拒绝");
            }

        }
    }

    private void initViews(){
        //isOrNoyz= (TextView) getView().findViewById(R.id.my_isorrz);
        fansLlyt = getView().findViewById(R.id.fansLlyt);
        fansLlyt.setOnClickListener(this);
        concernLlyt = getView().findViewById(R.id.concernLlyt);
        concernLlyt.setOnClickListener(this);
        concernNumTv = (TextView) getView().findViewById(R.id.concernNumTv);
        fansNumTv = (TextView) getView().findViewById(R.id.fansNumTv);
        //个人简介
        tradesTv = (TextView) getView().findViewById(R.id.tradesTv);
        tradesTv.setOnClickListener(this);
        numsTv = (TextView) getView().findViewById(R.id.numsTv);
        //sendTv = (TextView) getView().findViewById(R.id.sendTv);
       // levelImg = (ImageView) getView().findViewById(R.id.levelImg);
        headImg = (ImageView) getView().findViewById(R.id.headImg);
        sexImg = (ImageView) getView().findViewById(R.id.sexImg);
        isVipImg = (ImageView) getView().findViewById(R.id.isVipImg);
        //userID
        nameTv = (TextView) getView().findViewById(R.id.nameTv);
        //点击头像
        getView().findViewById(R.id.left).setOnClickListener(this);


        liveRlyt = getView().findViewById(R.id.liveRlyt);
        liveNumTv=getView().findViewById(R.id.liveNumTv);
        liveRlyt.setOnClickListener(this);
        videoRlyt = getView().findViewById(R.id.videoRlyt);
        videoNumTV=getView().findViewById(R.id.videoTv);
        videoRlyt.setOnClickListener(this);
        wallteRlyt = getView().findViewById(R.id.wallteRlyt);
        walletNumTV=getView().findViewById(R.id.wallteTv);
        wallteRlyt.setOnClickListener(this);
        collectRlyt = getView().findViewById(R.id.collectRlyt);
        colletNumTV=getView().findViewById(R.id.collectTv);
        collectRlyt.setOnClickListener(this);

        editInfoImg = (ImageView) getView().findViewById(R.id.editInfoImg);
        editInfoImg.setOnClickListener(this);
        //升级会员
        getView().findViewById(R.id.upVip).setOnClickListener(this);
        vipTime=getView().findViewById(R.id.vipTime);
        //讲师验证
        getView().findViewById(R.id.identificationRlyt).setOnClickListener(this);
        identificationStatusTv = getView().findViewById(R.id.identificationStatusTv);
        //我的动态
        getView().findViewById(R.id.myDynamic).setOnClickListener(this);
        //我的提问
        getView().findViewById(R.id.myQuestion).setOnClickListener(this);
        //浏览记录
        getView().findViewById(R.id.lookHistoryTv).setOnClickListener(this);
        //购买记录
        getView().findViewById(R.id.buyHistory).setOnClickListener(this);
        //设置
        getView().findViewById(R.id.settingTv).setOnClickListener(this);



        getView().findViewById(R.id.left).setOnClickListener(this);







    }
    //个人简介是否展开
    private boolean isOpenTra=false;
    /**
     * 点击事件顺序上往下，左往右
     * @param v
     */
    @Override
    public void onClick(View v) {
        Intent intent;
        //LogUtils.i("userID",userInfo.userId+"");
        if (CommonUtil.isFirstClick()){
            return;
        }
        switch (v.getId()) {
            case R.id.tradesTv:
                if (!isOpenTra){
                    //打开
                    tradesTv.setSingleLine(false);
                }else {
                    //关闭
                    tradesTv.setSingleLine(true);
                }
                if (!isOpenTra){
                    //打开
                   isOpenTra=true;
                }else {
                    //关闭
                   isOpenTra=false;
                }
                break;

            //个人资料
            case R.id.editInfoImg:
                intent = new Intent(getActivity(), UserProfileActivity.class);
                startActivity(intent);
                break;
            //查看个人头像
            case R.id.left:
                 intent = new Intent(getActivity(), ImagePagerActivity.class);
                ArrayList<String> urls = new ArrayList<String>();
                //判断是网络图片还是本地 0默认网络
                int url_bd_wl=0;
                urls.add(UrlConstant.IP + userInfo.iconUrl);
                intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, urls);
                intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, 0);
                intent.putExtra("startPosition",0);
                intent.putExtra("type",url_bd_wl);
                startActivity(intent);
                break;
            //粉丝
            case R.id.fansLlyt:
                intent = new Intent(getActivity(), ConcernActivity.class);
                intent.putExtra(Constant.USERID, userInfo.userId);
                intent.putExtra("name",userInfo.name);
                startActivity(intent);
                break;
            //关注
            case R.id.concernLlyt:
                intent = new Intent(getActivity(), ConcernActivity.class);
                intent.putExtra("type", 1);
                intent.putExtra(Constant.USERID, userInfo.userId);
                intent.putExtra("name",userInfo.name);
                startActivity(intent);
                break;

            //我的直播
            case R.id.liveRlyt:
                intent = new Intent(getActivity(), MyVideoActivity.class);
                intent.putExtra("type", 1);
                startActivity(intent);
                break;
            //我的视频
            case R.id.videoRlyt:
                intent = new Intent(getActivity(), MyVideoActivity.class);
                intent.putExtra("type", 2);
                startActivity(intent);
                break;
            //我的收藏
            case R.id.collectRlyt:
                intent = new Intent(getActivity(), MyVideoActivity.class);
                intent.putExtra("type", 3);
                startActivity(intent);
                break;
            //我的钱包
            case R.id.wallteRlyt:
                intent = new Intent(getActivity(), WallteActivity.class);
                startActivity(intent);
                break;

            //升级会员
            case R.id.upVip:
                intent=new Intent(getActivity(), UpgradeVipActivity.class);
                startActivity(intent);
                break;
            //讲师认证界面
            case R.id.identificationRlyt:
                intent = new Intent(getActivity(), RealNameCertificationActivity.class);
                startActivity(intent);
                break;
            //我的动态-个人主页
            case R.id.myDynamic:
                //朋友圈
                /*getFriendCircle();*/
                intent = new Intent(getActivity(), UserHomeActivty.class);
                Bundle bundle=new Bundle();
                bundle.putSerializable("data",userInfo);
                intent.putExtra("bundle",bundle);
                startActivity(intent);
                //intent.putExtra(Constant.USERID, userInfo.userId);
                break;
            case R.id.myQuestion:
                intent = new Intent(getActivity(), MyQusetionActivity.class);
                //intent.putExtra(Constant.USERID, userInfo.userId);
                startActivity(intent);
                break;
            //浏览记录
            case R.id.lookHistoryTv:
                intent = new Intent(getActivity(), MyVideoActivity.class);
                intent.putExtra("type", 4);
                startActivity(intent);
                break;
            //购买记录
            case R.id.buyHistory:
                intent = new Intent(getActivity(), BuyHistoryActivity.class);
                //intent.putExtra(Constant.USERID, userInfo.userId);
                startActivity(intent);
                break;

            case R.id.settingTv:
                intent = new Intent(getActivity(), SettingActivity.class);
                startActivity(intent);
                break;
     /*       //收益
            case R.id.purseRlyt:
                intent = new Intent(getActivity(), MyIncomeActivity.class);
                startActivity(intent);
                break;

            //账户
            case R.id.diamoundRlyt:
                //intent = new Intent(getActivity(), RechargeActivity.class);
                intent = new Intent(getActivity(), MyzuanshiActivity.class);
                startActivity(intent);
                break;


            case R.id.settingTv:
                intent = new Intent(getActivity(), SettingActivity.class);
                startActivity(intent);
                break;
            //金豆贡献榜
            case R.id.godlenRankingList:
                intent = new Intent(getActivity(), GoldenRankingActivity.class);
                intent.putExtra(Constant.USERID, userInfo.userId);
                //intent.putExtra("userID", userInfo.userId);
                startActivity(intent);
                break;

            //跳转改变为直播购买记录
            case R.id.inviteFriendTv:
                intent = new Intent(getActivity(), BuyLiveActivity.class);
                startActivity(intent);
                break;

            //消息
            case R.id.chatImg:
                intent = new Intent(getActivity(), SysTemMessageActivty.class);
                getActivity().startActivity(intent);
                break;
            case R.id.vApplyRlyt:
                intent = new Intent(getActivity(), HuangjiaActivity.class);
                getActivity().startActivity(intent);
                break;
            case R.id.myMessageTV:
                startActivity(new Intent(getActivity(), MessageActivity.class));
                break;*/
           default:break;
        }
    }
}
