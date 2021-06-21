package com.wehang.ystv.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.LogUtils;
import com.bumptech.glide.Glide;
import com.google.gson.reflect.TypeToken;
import com.wehang.txlibrary.widget.myview.LookShardToolsPopupwindow;
import com.wehang.txlibrary.widget.myview.shareBean;
import com.wehang.ystv.Constant;
import com.wehang.ystv.R;
import com.wehang.ystv.adapter.ImgPagerAdapter;
import com.wehang.ystv.bo.Lives;
import com.wehang.ystv.bo.Order;
import com.wehang.ystv.bo.QuestionMessages;
import com.wehang.ystv.bo.UserInfo;
import com.wehang.ystv.bo.WXPay;
import com.wehang.ystv.interfaces.UrlConstant;
import com.wehang.ystv.interfaces.result.GetLvesResult;
import com.wehang.ystv.utils.CommonUtil;
import com.wehang.ystv.utils.GlideUtils;
import com.wehang.ystv.utils.ToastUtil;
import com.wehang.ystv.utils.UserLoginInfo;
import com.whcd.base.activity.BaseActivity;
import com.whcd.base.interfaces.BaseData;
import com.whcd.base.interfaces.BaseResult;
import com.whcd.base.interfaces.HttpTool;
import com.whcd.base.widget.CustomProgressDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

public class LiveDetails extends BaseActivity {
    private Lives lives;
    private List<QuestionMessages> list = new ArrayList<>();
    private LookShardToolsPopupwindow sharePopupwindow;// 分享

    private TextView live_status,title_item_live,ngk,open_time,rqz,live_price,user_name_itemlive,user_ks_itemlive,user_zw_itemlive,user_yiyuanm_itemlive,live_brife,ljbmTV;
    private ImageView like_img,share_img,live_img,user_tx_itemlive;

    private ImageView user_home_head_img1,user_home_head_img2,user_home_head_img3,user_home_head_img4;
    @Override
    protected int getLayoutResId() {
        return R.layout.activity_live_details;
    }

    @Override
    protected void initTitleBar() {

    }

    @Override
    protected void initView() {

        list.add(new QuestionMessages());
        list.add(new QuestionMessages());

        findViewById(R.id.havebm).setOnClickListener(this);
        ljbmTV= findViewById(R.id.ljbmTV);
        ljbmTV.setOnClickListener(this);
        findViewById(R.id.back_img).setOnClickListener(this);
        live_img=findViewById(R.id.live_img);
        //live_img.setOnClickListener(this);
        like_img=findViewById(R.id.like_img);
        share_img=findViewById(R.id.share_img);
        live_status=findViewById(R.id.live_status);
        title_item_live=findViewById(R.id.title_item_live);
        ngk=findViewById(R.id.ngk);
        open_time=findViewById(R.id.open_time);
        rqz=findViewById(R.id.rqz);
        live_price=findViewById(R.id.live_price);
        user_tx_itemlive=findViewById(R.id.user_tx_itemlive);
        user_name_itemlive=findViewById(R.id.user_name_itemlive);
        user_ks_itemlive=findViewById(R.id.user_ks_itemlive);
        user_zw_itemlive=findViewById(R.id.user_zw_itemlive);
        user_yiyuanm_itemlive=findViewById(R.id.user_yiyuanm_itemlive);
        live_brife=findViewById(R.id.live_brife);

        like_img.setOnClickListener(this);

        share_img.setOnClickListener(this);


        //报名头像
        user_home_head_img1=findViewById(R.id.user_home_head_img1);
        user_home_head_img2=findViewById(R.id.user_home_head_img2);
        user_home_head_img3=findViewById(R.id.user_home_head_img3);
        user_home_head_img4=findViewById(R.id.user_home_head_img4);
        user_home_head_img1.setVisibility(View.GONE);
        user_home_head_img2.setVisibility(View.GONE);
        user_home_head_img3.setVisibility(View.GONE);
        user_home_head_img4.setVisibility(View.GONE);

        //嘉宾栏
        findViewById(R.id.jbLly).setOnClickListener(this);
    }

    @Override
    protected void initData(Bundle bundle) {
        bundle=getIntent().getBundleExtra("bundle");
        lives = (Lives) bundle.getSerializable("data");

    }

    private List<UserInfo>userInfos=new ArrayList<>();
    private void getHaveBm(){
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
                LogUtils.i("userInfos",userInfos.size());
                initHaveBm();
            }

            @Override
            public void onError(int errorCode) {
                LogUtils.i("info","no");

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
            GlideUtils.loadHeadImage(context, UrlConstant.IP + userInfos.get(0).iconUrl,user_home_head_img4);
        }else if (userInfos.size()==2){
            user_home_head_img3.setVisibility(View.VISIBLE);
            GlideUtils.loadHeadImage(context, UrlConstant.IP + userInfos.get(0).iconUrl,user_home_head_img3);
            user_home_head_img4.setVisibility(View.VISIBLE);
            GlideUtils.loadHeadImage(context, UrlConstant.IP + userInfos.get(1).iconUrl,user_home_head_img4);
        }
        else if (userInfos.size()==3){
            user_home_head_img2.setVisibility(View.VISIBLE);
            GlideUtils.loadHeadImage(context, UrlConstant.IP + userInfos.get(0).iconUrl,user_home_head_img2);
            user_home_head_img3.setVisibility(View.VISIBLE);
            GlideUtils.loadHeadImage(context, UrlConstant.IP + userInfos.get(1).iconUrl,user_home_head_img3);
            user_home_head_img4.setVisibility(View.VISIBLE);
            GlideUtils.loadHeadImage(context, UrlConstant.IP + userInfos.get(2).iconUrl,user_home_head_img4);
        }
        else if (userInfos.size()>=4){
            user_home_head_img1.setVisibility(View.VISIBLE);
            GlideUtils.loadHeadImage(context, UrlConstant.IP + userInfos.get(0).iconUrl,user_home_head_img1);
            user_home_head_img2.setVisibility(View.VISIBLE);
            GlideUtils.loadHeadImage(context, UrlConstant.IP + userInfos.get(1).iconUrl,user_home_head_img2);
            user_home_head_img3.setVisibility(View.VISIBLE);
            GlideUtils.loadHeadImage(context, UrlConstant.IP + userInfos.get(2).iconUrl,user_home_head_img3);
            user_home_head_img4.setVisibility(View.VISIBLE);
            GlideUtils.loadHeadImage(context, UrlConstant.IP + userInfos.get(3).iconUrl,user_home_head_img4);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        getHaveBm();
        getLiveDetails();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        // 避免连续点击
        Intent intent;
        if (CommonUtil.isFirstClick())
        {
            return;
        }

        switch (v.getId()){
            case R.id.back_img:
                back();
                break;
            case R.id.like_img:
                collect();
                break;
            case R.id.share_img:
                sharePopupwindow.showPopWindow();

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
                        enterRoom(lives);
                    }else {
                        intent=new Intent(context,PayActivity.class);
                        Bundle bundle=new Bundle();
                        bundle.putSerializable("data",lives);
                        intent.putExtra("bundle",bundle);
                        context.startActivityForResult(intent,1);
                    }

                }else if (lives.isBuy==1){
                    //进入论坛
                    enterRoom(lives);

                }
                break;
         /*   //被屏蔽了
            case R.id.live_img:
                if (lives.isBuy==0){
                    if (lives.price==0){
                        creatOrder();
                        enterRoom(lives);
                    }
                    intent=new Intent(context,PayActivity.class);
                    Bundle bundle=new Bundle();
                    bundle.putSerializable("data",lives);
                    intent.putExtra("bundle",bundle);
                    context.startActivityForResult(intent,1);
                }else {
                    enterRoom(lives);
                }
                break;
                //如果报名成功
               //*/
            case R.id.jbLly:
                //点击头像跳转
                intent=new Intent(context, UserHomeActivty.class);
                Bundle bundle=new Bundle();
                UserInfo userInfo=new UserInfo();
                userInfo.userId=lives.userId;
                bundle.putSerializable("data",userInfo);
                intent.putExtra("bundle",bundle);
                context.startActivity(intent);
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
                ToastUtil.makeText(LiveDetails.this,"请重试", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode==1){
            if (resultCode==RESULT_OK)
            lives.isBuy=1;
            ljbmTV.setText("进入直播");
        }
    }
    private void enterRoom(final Lives lives) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", UserLoginInfo.getUserToken());
        params.put("sourceId", lives.sourceId);
        final CustomProgressDialog dialog = CustomProgressDialog.show(context, "加载中...", true, null);
        HttpTool.doPost(context, UrlConstant.ENTERROOM, params, true, new TypeToken<BaseResult<UserInfo>>() {
        }.getType(), new HttpTool.OnResponseListener() {
            @Override
            public void onSuccess(BaseData data) {
                dialog.dismiss();
                UserInfo userInfo = (UserInfo) data;
                if (userInfo != null) {
                    Intent intent=new Intent(context,LiveWatchNewActivity.class);
                    Bundle bundle=new Bundle();
                    bundle.putSerializable("data",userInfo);
                    bundle.putSerializable("live",lives);
                    intent.putExtra("bundle",bundle);
                    intent.putExtra("sourceId",lives.sourceId);
                    context.startActivity(intent);
                }
            }

            @Override
            public void onError(int errorCode) {
                dialog.dismiss();
                UserLoginInfo.loginOverdue(context, errorCode);
            }
        });
    }


    private void creatOrder(){
        setPushTag(lives);
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", UserLoginInfo.getUserToken());
        params.put("sourceId", lives.sourceId);
        dialog = CustomProgressDialog.show(context, "加载中...", true, null);
        HttpTool.doPost(context, UrlConstant.BUTLIVE, params, true, new TypeToken<BaseResult<WXPay>>() {
        }.getType(), new HttpTool.OnResponseListener() {

            @Override
            public void onSuccess(BaseData data) {
                dialog.dismiss();
                lives.isBuy=1;
                ljbmTV.setText("进入直播");
            }

            @Override
            public void onError(int errorCode) {
                dialog.dismiss();
                UserLoginInfo.loginOverdue(context, errorCode);
            }
        });
    }

    private void setPushTag(Lives lives){
        Set<String> tagSet = new LinkedHashSet<String>();
        tagSet.add(lives.sourceId);
        JPushInterface.setTags(context, tagSet, new TagAliasCallback() {
            @Override
            public void gotResult(int i, String s, Set<String> set) {

            }
        });
    }
    private void getLiveDetails(){
        Map<String, String> params;
        params = new HashMap<String, String>();
        params.put("token",UserLoginInfo.getUserToken());
        params.put("sourceId", lives.sourceId);
        final CustomProgressDialog dialog = CustomProgressDialog.show(context, "加载中...", true, null);
        HttpTool.doPost(context, UrlConstant.SOURCEDETAIL, params, true, new TypeToken<BaseResult<GetLvesResult>>() {
        }.getType(), new HttpTool.OnResponseListener() {
            @Override
            public void onSuccess(BaseData data) {
                dialog.dismiss();
                LogUtils.i("QUSERYCOURSEWARE",data.toString());
                GetLvesResult result= ( GetLvesResult) data;
                lives=result.source;
                Glide.with(context).load(UrlConstant.IP+lives.sourcePic).placeholder(R.drawable.live_mrjz).error(R.drawable.live_mrjz).into(live_img);
                Glide.with(context).load(UrlConstant.IP+lives.iconUrl).placeholder(R.drawable.default_portrait).error(R.drawable.default_portrait).into(user_tx_itemlive);
                if (lives.sourceType==1){
                    live_status.setText("直播中");
                }else if (lives.sourceType==2){
                    live_status.setText("即将开始");
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

                if (lives.isBuy==0&&lives.price!=0){
                    ljbmTV.setText("立即报名");
                }else {
                    ljbmTV.setText("进入直播");
                }
                //分享
                shareBean shareBean=new shareBean();
                shareBean.sourceTitle=lives.sourceTitle;
                shareBean.brife=lives.summary;
                shareBean.sourcePic= UrlConstant.IP+lives.sourcePic;
                shareBean.Html="http://112.74.218.80/share/share.html?sourceId="+lives.sourceId;
                sharePopupwindow = new LookShardToolsPopupwindow(share_img, context,shareBean);
                sharePopupwindow.setLookShardViedo(new LookShardToolsPopupwindow.mShardViedo() {
                    @Override
                    public void shardViedo(int position) {
                        Log.i("share", position + "");
                    }
                });
                if (lives.isCollect==1){
                    like_img.setImageResource(R.drawable.like_r);
                }else {
                    like_img.setImageResource(R.drawable.like);

                }
            }

            @Override
            public void onError(int errorCode) {
               dialog.dismiss();
            }
        });
    }
}
