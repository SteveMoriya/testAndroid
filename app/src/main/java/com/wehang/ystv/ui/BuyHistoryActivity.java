package com.wehang.ystv.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;

import com.blankj.utilcode.util.LogUtils;
import com.google.gson.reflect.TypeToken;
import com.wehang.ystv.R;
import com.wehang.ystv.adapter.LiveAdapter;
import com.wehang.ystv.adapter.LiveBuyAdapter;
import com.wehang.ystv.bo.Lives;
import com.wehang.ystv.bo.Order;
import com.wehang.ystv.interfaces.UrlConstant;
import com.wehang.ystv.interfaces.result.GetBuyResult;
import com.wehang.ystv.interfaces.result.GetSysMResult;
import com.wehang.ystv.myappview.BuyHistoryPopupwindow;
import com.wehang.ystv.utils.CommonUtil;
import com.wehang.ystv.utils.UserLoginInfo;
import com.whcd.base.activity.BaseActivity;
import com.whcd.base.interfaces.BaseData;
import com.whcd.base.interfaces.BaseResult;
import com.whcd.base.interfaces.HttpTool;
import com.whcd.base.widget.PageListView;
import com.whcd.base.widget.TopMenuBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BuyHistoryActivity extends BaseActivity implements PageListView.PageListViewListener{
    private TopMenuBar topMenuBar;
    private BuyHistoryPopupwindow popupwindow;

    private LiveBuyAdapter adapter;
    private List<Order>list=new ArrayList<>();
    private View view = null;
    private PageListView listView;
    //private NearLiveAdapter adapter;
    private int pageNo = 1;
    private int pageSize = 10;
    private View empty;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_buy_history;
    }

    @Override
    protected void initTitleBar() {
        topMenuBar = (TopMenuBar) findViewById(R.id.topMenuBar);
        topMenuBar.setTitle("全部");
        topMenuBar.setTitleRitdrwable();
        topMenuBar.getLeftButton().setOnClickListener(this);
        topMenuBar.getTitle().setOnClickListener(this);
        popupwindow=new BuyHistoryPopupwindow(topMenuBar.getTitle(),BuyHistoryActivity.this);
        popupwindow.setLookShardViedo(new BuyHistoryPopupwindow.mShardViedo() {
            @Override
            public void shardViedo(int position) {
                switch (position){
                    case 1:topMenuBar.setTitle("全部");
                        type=0;
                        break;
                    case 2:topMenuBar.setTitle("待付款");
                        type=1;
                        break;
                    case 3:topMenuBar.setTitle("待使用");
                        type=2;
                        break;
                    case 4:topMenuBar.setTitle("已完成");
                        type=3;
                        break;
                    case 5:topMenuBar.setTitle("退款");
                        type=4;
                        break;
                }
                list.clear();
                pageNo=1;
                initData();
                popupwindow.popupWindow.dismiss();

            }
        });

    }

    @Override
    protected void initView() {
        adapter = new LiveBuyAdapter(this,list,1);
        empty= LayoutInflater.from(this).inflate(R.layout.empty, null);
        listView = findViewById(R.id.livelistView);
        listView.setPullRefreshEnable(true);
        listView.setPullLoadEnable(false);
        listView.setPageListViewListener(this);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                // 注意这里的i是1开始的不是0；传值的穿i-1
                Intent intent=new Intent(BuyHistoryActivity.this, BuyDetails.class);
                Bundle bundle=new Bundle();
                bundle.putSerializable("data",list.get(i-1));
                intent.putExtra("bundle",bundle);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void initData(Bundle bundle) {

    }
    private int type=0;
    private void initData(){
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", UserLoginInfo.getUserToken(this));
        params.put("type",type+"");
        params.put("page", pageNo + "");
        params.put("pageSize", pageSize + "");

        HttpTool.doPost(this, UrlConstant.BUTLIVEHIS, params, false, new TypeToken<BaseResult<GetBuyResult>>() {
        }.getType(), new HttpTool.OnResponseListener() {
            @Override
            public void onSuccess(BaseData data) {
                listView.stopRefresh();
                listView.stopLoadMore();
                GetBuyResult result = (GetBuyResult) data;
                if (result.orders.size() < pageSize) {
                    listView.setPullLoadEnable(false);
                } else {
                    listView.setPullLoadEnable(true);
                }
                if (pageNo == 1) {
                    list.clear();
                }
                if (result.orders != null && result.orders.size() != 0) {
                    list.addAll(result.orders);
                }
                LogUtils.i("data",list.size());
                isShowEmpty(list.size());
                adapter.notifyDataSetChanged();

            }


            @Override
            public void onError(int errorCode) {
                UserLoginInfo.loginOverdue(context, errorCode);
                listView.stopRefresh();
                listView.stopLoadMore();
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
    public void onClick(View v) {
        super.onClick(v);
        //title_text
        if (CommonUtil.isFirstClick()){
            return;
        }
        switch (v.getId()){
            case R.id.title_btn_left:finish();
                break;
            case R.id.title_text:
                popupwindow.showPopWindow();
                break;
        }
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
    protected void onResume() {
        super.onResume();
        initData();
    }
}
