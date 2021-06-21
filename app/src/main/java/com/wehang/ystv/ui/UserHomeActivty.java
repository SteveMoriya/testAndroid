package com.wehang.ystv.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.google.gson.reflect.TypeToken;
import com.tencent.TIMConversationType;
import com.wehang.ystv.Constant;
import com.wehang.ystv.R;
import com.wehang.ystv.adapter.LiveAdapter;
import com.wehang.ystv.bo.Lives;
import com.wehang.ystv.bo.UserInfo;
import com.wehang.ystv.interfaces.UrlConstant;
import com.wehang.ystv.interfaces.result.GetLvesResult;
import com.wehang.ystv.utils.CommonUtil;
import com.wehang.ystv.utils.UserLoginInfo;
import com.whcd.base.activity.BaseActivity;
import com.whcd.base.activity.ImagePagerActivity;
import com.whcd.base.interfaces.BaseData;
import com.whcd.base.interfaces.BaseResult;
import com.whcd.base.interfaces.HttpTool;
import com.whcd.base.interfaces.VolleyTool;
import com.whcd.base.widget.PageListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserHomeActivty extends BaseActivity implements PageListView.PageListViewListener{

    private PageListView listView;
    //private NearLiveAdapter adapter;
    private LiveAdapter adapter;
    private List<Lives> nearLives = new ArrayList<>();
    private View empty;
    private int pageNo = 1;
    private int pageSize = 10;
    private String userId;



    // 关注，粉丝，直播，等级，钱包，账号
    private View fansLlyt, concernLlyt, liveRlyt, gradeRlyt, purseRlyt, diamoundRlyt, myDynamic;
    private TextView concernNumTv, fansNumTv, tradesTv, numsTv, sendTv, nameTv, liveNumTv, incomingTv, accountTv, userLevleTv,levelImgTV;
    private ImageView headImg, sexImg, editInfoImg, isVipImg, levelImg;
    private TextView unreadLabel;
    private TextView identificationStatusTv;
    //去关注
    private TextView gzTv;
    private RelativeLayout left;
    @Override
    protected int getLayoutResId() {
        return R.layout.activity_user_home_activty;
    }
    private ImageView imageView1,imageView2,imageView3;
    private UserInfo userInfo;
    private LinearLayout userHomeBt;
    @Override
    protected void initTitleBar() {

    }

    @Override
    protected void initView() {
        Bundle bundle=getIntent().getBundleExtra("bundle");
        userInfo = (UserInfo) bundle.getSerializable("data");
        empty= LayoutInflater.from(this).inflate(R.layout.empty, null);
        View head = LayoutInflater.from(this).inflate(R.layout.user_home_headitem, null);
        //粉丝关注
        fansLlyt = head.findViewById(R.id.fansLlyt);
        fansLlyt.setOnClickListener(this);
        concernLlyt =head.findViewById(R.id.concernLlyt);
        concernLlyt.setOnClickListener(this);
        concernNumTv = (TextView)head.findViewById(R.id.concernNumTv);
        fansNumTv = (TextView) head.findViewById(R.id.fansNumTv);

        //签名
        tradesTv = (TextView)head.findViewById(R.id.tradesTv);
        tradesTv.setOnClickListener(this);
        //ID
        numsTv = (TextView)head.findViewById(R.id.numsTv);

        headImg = (ImageView)head.findViewById(R.id.headImg);

        sexImg = (ImageView) head.findViewById(R.id.sexImg);
        isVipImg=head.findViewById(R.id.isVipImg);
        nameTv = (TextView)head.findViewById(R.id.nameTv);
        left=head.findViewById(R.id.left);
        left.setOnClickListener(this);


        findViewById(R.id.title_btn_left).setOnClickListener(this);

        //需要设置几个粉丝头像数量
        listView = (PageListView)findViewById(R.id.listView);
        listView.addHeaderView(head);
        listView.setPullRefreshEnable(true);
        listView.setPullLoadEnable(false);
        listView.setPageListViewListener(this);
        adapter = new LiveAdapter(this, nearLives,6);
        listView.setAdapter(adapter);


        findViewById(R.id.TIMchatTV).setOnClickListener(this);

        userHomeBt=findViewById(R.id.userHomeBt);
        if (userInfo.userId.equals(UserLoginInfo.getUserInfo().userId)){
            userHomeBt.setVisibility(View.GONE);
        }else {
            userHomeBt.setVisibility(View.VISIBLE);
        }
        gzTv=findViewById(R.id.gzTv);
        gzTv.setOnClickListener(this);
    }
    //缺少
    private void initUserMessage(){

        VolleyTool.getImage(UrlConstant.IP + userInfo.iconUrl, headImg, R.drawable.default_portrait, R.drawable.default_portrait);

        nameTv.setText(userInfo.name);

        if (userInfo.sex == Constant.GIRL) {
            sexImg.setImageResource(R.drawable.icon_home_g);
        } else {
            sexImg.setImageResource(R.drawable.icon_home_b);
        }
        numsTv.setText("ID：" + userInfo.userId);
        if (userInfo.isVip == 0) {
            isVipImg.setVisibility(View.INVISIBLE);
        } else {
            isVipImg.setVisibility(View.VISIBLE);
        }
        fansNumTv.setText(userInfo.fansNum + "");
        concernNumTv.setText(userInfo.focusNum + "");
        int resourceId = CommonUtil.getResourceId("l" + userInfo.level);
        //levelImg.setImageResource(resourceId);
        if (TextUtils.isEmpty(userInfo.underwrite)) {
            tradesTv.setText("ta好像忘记写签名了...");
        } else {
            tradesTv.setText(userInfo.underwrite);
        }
        if (userInfo.isFocus==0){
            gzTv.setText("+关注");
        }else {
            gzTv.setText("取消关注");
        }

    }
    private void initUser() {

        Map<String, String> params = new HashMap<String, String>();
        params.put("token", UserLoginInfo.getUserToken());
        params.put("userId", userInfo.userId);
        Log.i("need1","token:"+ UserLoginInfo.getUserToken()+"userId:"+userId+"url:"+ UrlConstant.USERHOME);
        HttpTool.doPost(this, UrlConstant.USERHOME, params, false, new TypeToken<BaseResult<UserInfo>>() {
        }.getType(), new HttpTool.OnResponseListener() {
            @Override
            public void onSuccess(BaseData data) {

                listView.stopRefresh();
                listView.stopLoadMore();
                userInfo = (UserInfo) data;
                //GetUserResult result = (GetUserResult) data;
                Log.i("need","success1");
                if (userInfo!=null){
                    Log.i("need","success2");
                    initUserMessage();

                   /* if (userInfo.tracks.size() < pageSize) {
                        listView.setPullLoadEnable(false);
                    } else {
                        listView.setPullLoadEnable(true);
                    }*/
                    if (pageNo == 1) {
                        nearLives.clear();
                    }
                    if (userInfo.tracks != null && userInfo.tracks.size() != 0) {
                        nearLives.addAll(userInfo.tracks);
                    }
                    LogUtils.i("adpter",nearLives.size()+nearLives.toString());
                    isShowEmpty(nearLives.size());
                    adapter.notifyDataSetChanged();


                }

            }

            @Override
            public void onError(int errorCode) {
                listView.stopRefresh();
                listView.stopLoadMore();
            }
        });
    }
    @Override
    protected void initData(Bundle bundle) {
       // initUser();
    }
    private boolean isHaveEmptyview=false;
    private void isShowEmpty(int size){
        if (size==0&&isHaveEmptyview==false){
            listView.addHeaderView(empty);
        }else if (size==0&&isHaveEmptyview==true){

        }else {
            listView.removeHeaderView(empty);
            isHaveEmptyview=false;
        }

        if (size==0&&isHaveEmptyview==false){
            isHaveEmptyview=true;
        }
    }
    private void initData(UserInfo userInfo) {
       /* Map<String, String> params = new HashMap<String, String>();
        params.put("token", UserLoginInfo.getUserToken(this));
        params.put("userId", userInfo.userId);
        params.put("page", pageNo + "");
        params.put("pageSize", pageSize + "");
        LogUtils.a("userlive", UserLoginInfo.getUserToken(this)+","+userInfo.userId+","+UrlConstant.USERLive);
        HttpTool.doPost(this, UrlConstant.USERLive, params, false, new TypeToken<BaseResult<GetLvesResult>>() {
        }.getType(), new HttpTool.OnResponseListener() {
            @Override
            public void onSuccess(BaseData data) {

                GetLvesResult result = (GetLvesResult) data;



            }

            @Override
            public void onError(int errorCode) {
                UserLoginInfo.loginOverdue(UserHomeActivty.this, errorCode);
                listView.stopRefresh();
                listView.stopLoadMore();
            }
        });*/
    }
    //个人简介是否展开
    private boolean isOpenTra=false;
    @Override
    public void onClick(View v) {
        Intent intent;
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
            case R.id.title_btn_left:
                back();
                break;
            //查看个人头像
            case R.id.left:
                intent = new Intent(context, ImagePagerActivity.class);
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
            /*case R.id.user_home_jdb_line:
                //跳转金豆排行榜
                intent = new Intent(this, GoldenRankingActivity.class);
                intent.putExtra(Constant.USERID, userInfo.userId);
                startActivity(intent);
                break;*/
            //粉丝
            case R.id.fansLlyt:
                if (!UserLoginInfo.getUserInfo().userId.equals(userInfo.userId)){
                    return;
                }
                intent = new Intent(this, ConcernActivity.class);
                intent.putExtra(Constant.USERID, userInfo.userId);
                intent.putExtra("name",userInfo.name);
                startActivity(intent);
                break;
            //关注
            case R.id.concernLlyt:
                if (!UserLoginInfo.getUserInfo().userId.equals(userInfo.userId)){
                    return;
                }
                intent = new Intent(this, ConcernActivity.class);
                intent.putExtra("type", 1);
                intent.putExtra(Constant.USERID, userInfo.userId);
                intent.putExtra("name",userInfo.name);
                startActivity(intent);
                break;
            case R.id.TIMchatTV:
              /*  userInfo=new UserInfo();
                userInfo.userId="600011";
                userInfo.userName="离开";
                userInfo.iconUrl="a677f196366b4495b0b0b1dab764f681.jpg";*/
                ChatActivity.navToChat(context,userInfo.userId,userInfo.name,userInfo.iconUrl, TIMConversationType.C2C);
                break;
            case R.id.gzTv:
                gzOrQuxiaogz();
        }
    }

    private void gzOrQuxiaogz() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", UserLoginInfo.getUserToken());
        params.put("userId", userInfo.userId);
        if (userInfo.isFocus == 0) {
            params.put("type", 0 + "");
        } else {
            params.put("type", 1 + "");
        }

        HttpTool.doPost(context, UrlConstant.FOCUS, params, true, new TypeToken<BaseResult<BaseData>>() {
        }.getType(), new HttpTool.OnResponseListener() {
            @Override
            public void onSuccess(BaseData data) {
                initUser();
            }

            @Override
            public void onError(int errorCode) {
                UserLoginInfo.loginOverdue(context, errorCode);
            }
        });
    }

    @Override
    public void onRefresh() {
        initUser();
    }

    @Override
    public void onLoadMore() {
       // initData(userInfo);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initUser();
    }
}


