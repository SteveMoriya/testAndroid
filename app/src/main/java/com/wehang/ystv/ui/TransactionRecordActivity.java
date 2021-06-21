package com.wehang.ystv.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.google.gson.reflect.TypeToken;
import com.wehang.ystv.R;
import com.wehang.ystv.adapter.LiveAdapter;
import com.wehang.ystv.adapter.TrsRecordAdapter;
import com.wehang.ystv.bo.Lives;
import com.wehang.ystv.bo.Transaction;
import com.wehang.ystv.bo.WXPay;
import com.wehang.ystv.interfaces.UrlConstant;
import com.wehang.ystv.interfaces.result.GetLvesResult;
import com.wehang.ystv.interfaces.result.GetTransactionResult;
import com.wehang.ystv.utils.CommonUtil;
import com.wehang.ystv.utils.UserLoginInfo;
import com.whcd.base.activity.BaseActivity;
import com.whcd.base.interfaces.BaseData;
import com.whcd.base.interfaces.BaseResult;
import com.whcd.base.interfaces.HttpTool;
import com.whcd.base.widget.CustomProgressDialog;
import com.whcd.base.widget.PageListView;
import com.whcd.base.widget.TopMenuBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransactionRecordActivity extends BaseActivity implements PageListView.PageListViewListener {
    private List<Transaction>list=new ArrayList<>();
    private TopMenuBar topMenuBar;
    private PageListView listView;
    //private VideoAdapter adapter;
    private TextView tipTv;
    private TrsRecordAdapter adapter;
    private int pageNo = 1;
    private int pageSize = 10;
    private View empty;
    @Override
    protected int getLayoutResId() {
        return R.layout.activity_transaction_record;
    }

    @Override
    protected void initTitleBar() {
        topMenuBar = (TopMenuBar) findViewById(R.id.topMenuBar);
        topMenuBar.setTitle("交易明细");
        topMenuBar.getLeftButton().setOnClickListener(this);
    }

    @Override
    protected void initView() {
        empty= LayoutInflater.from(this).inflate(R.layout.empty, null);
        listView = (PageListView) findViewById(R.id.listview);
        listView.setPullRefreshEnable(true);
        listView.setPullLoadEnable(false);
        listView.setPageListViewListener(this);
        adapter = new TrsRecordAdapter(this, list);
        listView.setAdapter(adapter);
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
        initData();
    }
    @Override
    public void onClick(View v) {
        if (CommonUtil.isFirstClick())
            return;
        switch (v.getId()) {
            case R.id.title_btn_left:
                back();
                break;

            default:
                break;
        }
    }
    private void initData(){
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", UserLoginInfo.getUserToken());
        params.put("page", pageNo+"");
        params.put("pageSize",pageSize+"");
        dialog = CustomProgressDialog.show(this, "加载中...", true, null);
        HttpTool.doPost(this, UrlConstant.TRANSACTIONRECORD, params, true, new TypeToken<BaseResult<GetTransactionResult>>() {
        }.getType(), new HttpTool.OnResponseListener() {

            @Override
            public void onSuccess(BaseData data) {
                dialog.dismiss();
                listView.stopRefresh();
                listView.stopLoadMore();
                GetTransactionResult result = (GetTransactionResult) data;
                if (result.transaction.size() < pageSize) {
                    listView.setPullLoadEnable(false);
                } else {
                    listView.setPullLoadEnable(true);
                }
                if (pageNo == 1) {
                    list.clear();
                }
                if (result.transaction != null && result.transaction.size() != 0) {
                    list.addAll(result.transaction);
                }
                LogUtils.i("adpter",list.size()+list.toString());
                isShowEmpty(list.size());
                //adapter.setData(nearLives);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(int errorCode) {
                dialog.dismiss();
                UserLoginInfo.loginOverdue(context, errorCode);
                listView.stopRefresh();
                listView.stopLoadMore();
            }
        });
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
}
