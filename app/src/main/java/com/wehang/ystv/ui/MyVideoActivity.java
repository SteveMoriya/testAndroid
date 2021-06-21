package com.wehang.ystv.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.LogUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.umeng.socialize.media.UMImage;
import com.wehang.txlibrary.ui.fragment.YsPushActivity;
import com.wehang.ystv.R;
import com.wehang.ystv.VideoApplication;
import com.wehang.ystv.adapter.LiveAdapter;
import com.wehang.ystv.bo.LineMicInfo;
import com.wehang.ystv.bo.LiveNeed;
import com.wehang.ystv.bo.Lives;
import com.wehang.ystv.bo.VideoInfo;
import com.wehang.ystv.interfaces.UrlConstant;
import com.wehang.ystv.interfaces.result.GetLvesResult;
import com.wehang.ystv.utils.CommonUtil;
import com.wehang.ystv.utils.ToastUtil;
import com.wehang.ystv.utils.UserLoginInfo;
import com.wehang.ystv.utils.Utils;
import com.whcd.base.activity.BaseActivity;
import com.whcd.base.interfaces.BaseData;
import com.whcd.base.interfaces.BaseResult;
import com.whcd.base.interfaces.HttpTool;
import com.whcd.base.widget.CustomProgressDialog;
import com.whcd.base.widget.PageListView;
import com.whcd.base.widget.PageListView.PageListViewListener;
import com.whcd.base.widget.TopMenuBar;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * {个人录制的视频}
 * </p>
 *
 * @author 宋瑶 2016-7-5 上午9:43:45
 * @version V1.0
 * @modificationHistory=========================重大变更说明
 * @modify by user: 宋瑶 2016-7-5
 */
public class MyVideoActivity extends BaseActivity implements PageListViewListener {
    protected static final int PAGE_SIZE = 20;
    private TopMenuBar topMenuBar;
    private PageListView listView;
    //private VideoAdapter adapter;
    private TextView tipTv;
    private LiveAdapter adapter;
    private List<Lives> nearLives = new ArrayList<>();
    private int pageNo = 1;
    private int pageSize = 10;
    private View empty;

    //1.我的直播，2，我的视频，3，我的收藏,4,我的浏览,5.主页
    private int type;
    @Override
    protected int getLayoutResId() {
        return R.layout.activity_mylive;
    }

    @Override
    protected void initTitleBar() {


        topMenuBar = (TopMenuBar) findViewById(R.id.topMenuBar);

        if (type==1){
            topMenuBar.setTitle("我的直播");
        }else if (type==2){
            topMenuBar.setTitle("我的视频");
        }else if (type==3){
            topMenuBar.setTitle("我的收藏");
        }else if (type==4){
            topMenuBar.setTitle("浏览记录");
            topMenuBar.getRightButton().setText("清空");
            topMenuBar.getRightButton().setOnClickListener(this);
        }
        topMenuBar.getLeftButton().setOnClickListener(this);

    }

    @SuppressLint("InflateParams")
    @Override
    protected void initView() {
        type=getIntent().getIntExtra("type",1);
        empty= LayoutInflater.from(this).inflate(R.layout.empty, null);
        listView = (PageListView) findViewById(R.id.videoListView);
        listView.setPullRefreshEnable(true);
        listView.setPullLoadEnable(false);
        listView.setPageListViewListener(this);
        adapter = new LiveAdapter(this, nearLives,type);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });
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

    @Override
    protected void initData(Bundle bundle) {





    }
    private void initData() {
        String url = null;
        switch (type){
            case 1:
                url=UrlConstant.USERLive;
                break;
            case 2:
                url=UrlConstant.USERVEDIO;
                break;
            case 3:
                url=UrlConstant.USERCOLLECTION;
                break;
            case 4:
                url=UrlConstant.USERWATCHHISTORY;
                break;
        }
        Map<String, String> params = new HashMap<String, String>();
        LogUtils.a("userlive", UserLoginInfo.getUserToken()+","+UserLoginInfo.getUserInfo(this).userId+","+UrlConstant.USERLive);
        params.put("token", UserLoginInfo.getUserToken());
        params.put("page", pageNo + "");
        params.put("pageSize", pageSize + "");
        HttpTool.doPost(this,url , params, false, new TypeToken<BaseResult<GetLvesResult>>() {
        }.getType(), new HttpTool.OnResponseListener() {
            @Override
            public void onSuccess(BaseData data) {
                listView.stopRefresh();
                listView.stopLoadMore();
                GetLvesResult result = (GetLvesResult) data;
                List<Lives>list=new ArrayList<Lives>();
                if (type==1){
                    list.addAll(result.lives);
                }else if (type==2){
                    list.addAll(result.videos);
                }
                else if (type==3){
                    list.addAll(result.collections);
                }else if (type==4){
                    list.addAll(result.views);
                }

                if (list.size() < pageSize) {
                    listView.setPullLoadEnable(false);
                } else {
                    listView.setPullLoadEnable(true);
                }
                if (pageNo == 1) {
                    nearLives.clear();
                }
                if (list != null && list.size() != 0) {
                    nearLives.addAll(list);
                }
                LogUtils.i("adpter",nearLives.size()+nearLives.toString());
                isShowEmpty(nearLives.size());
                //adapter.setData(nearLives);
                adapter.notifyDataSetChanged();
               /*);
                Message message=Message.obtain();
                message.what=102;
                handlerHolder.sendMessage(message);*/

            }

            @Override
            public void onError(int errorCode) {
                UserLoginInfo.loginOverdue(MyVideoActivity.this, errorCode);
                listView.stopRefresh();
                listView.stopLoadMore();
            }
        });
    }
    @Override
    public void onClick(View v) {
        if (CommonUtil.isFirstClick())
            return;
        switch (v.getId()) {
            case R.id.title_btn_left:
                back();
                break;
            case R.id.title_btn_right:
                clearWhatch();
            default:
                break;
        }
    }

    private void clearWhatch() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("token",UserLoginInfo.getUserToken());
        dialog = CustomProgressDialog.show(context, "加载中...", true, null);
        HttpTool.doPost(context, UrlConstant.DELETEVIEW, params, true, new TypeToken<BaseResult<BaseData>>() {
        }.getType(), new HttpTool.OnResponseListener() {
            @Override
            public void onSuccess(BaseData data) {
                dialog.dismiss();
                ToastUtil.makeText(context,"清除成功",Toast.LENGTH_SHORT).show();
                initData();
            }

            @Override
            public void onError(int errorCode) {
                dialog.dismiss();

            }
        });
    }

    private void deleteVideo(final VideoInfo video) {
      /*  CommonUtil.showYesNoDialog(context, "是否删除视频", "确认", "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                if (arg1 == DialogInterface.BUTTON_POSITIVE) {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("roomId", video.roomId);
                    HttpTool.doPost(context, UrlConstant.DELVIDEO, params, false, new TypeToken<BaseResult<BaseData>>() {
                    }.getType(), new HttpTool.OnResponseListener() {
                        @Override
                        public void onSuccess(BaseData data) {
                            initData(null);
                        }

                        @Override
                        public void onError(int errorCode) {
                            UserLoginInfo.loginOverdue(context, errorCode);
                        }
                    });
                }
            }
        }).show();*/
    }

    @Override
    public void onRefresh() {
        pageNo=1;
        initData();
    }

    @Override
    public void onLoadMore() {
        pageNo++;
        initData();
    }




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        UMImage umImage = new UMImage(context, R.drawable.ic_launcher);
        switch (requestCode) {
            case 0:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    adapter.startLive();
                } else {
                    ToastUtil.makeText(context, "没有获取使用相机和麦克风的权限", Toast.LENGTH_SHORT).show();
                }
                break;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (type==1){
            //如果是直播，进入前先判断是否有未结束的直播，有的话的直接进入未结束直播
            getSourceInfo();
        }
        initData();
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
                                    Utils.endLiveHome(liveNeed.sourceId,MyVideoActivity.this);
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
}
