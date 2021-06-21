package com.wehang.ystv.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.blankj.utilcode.util.LogUtils;
import com.google.gson.reflect.TypeToken;
import com.wehang.ystv.R;
import com.wehang.ystv.adapter.QuestionAdapter;
import com.wehang.ystv.bo.QuestionMessages;
import com.wehang.ystv.interfaces.UrlConstant;
import com.wehang.ystv.interfaces.result.GetSysMResult;
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

public class MyQusetionActivity extends BaseActivity implements PageListView.PageListViewListener {

    private PageListView listView;
    private List<QuestionMessages> list = new ArrayList<>();
    private QuestionAdapter adapter;
    private int pageNo = 1;
    private int pageSize = 10;
    private View empty;
    @Override
    protected int getLayoutResId() {
        return R.layout.activity_my_qusetion;
    }
    @Override
    protected void initTitleBar() {
        TopMenuBar topMenuBar = (TopMenuBar) findViewById(R.id.topMenuBar);
        topMenuBar.setTitle("我的提问");
        Button leftButton = topMenuBar.getLeftButton();
        leftButton.setOnClickListener(this);
        topMenuBar.hideRightButton();
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
    protected void initView() {
        listView = (PageListView) findViewById(R.id.questionListView);
        empty= LayoutInflater.from(this).inflate(R.layout.empty, null);

        listView.setPullLoadEnable(false);
        listView.setPullRefreshEnable(true);
        listView.setPageListViewListener(this);
        adapter = new QuestionAdapter(context, list,3);
        listView.setAdapter(adapter);
    }


    private void initData() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", UserLoginInfo.getUserToken());
        params.put("page", pageNo + "");
        params.put("pageSize", pageSize + "");
        HttpTool.doPost(this, UrlConstant.QUSETIONLIST, params, false, new TypeToken<BaseResult<GetSysMResult>>() {
        }.getType(), new HttpTool.OnResponseListener() {
            @Override
            public void onSuccess(BaseData data) {
                listView.stopRefresh();
                listView.stopLoadMore();
                GetSysMResult result = (GetSysMResult) data;
                if (result.questions.size() < pageSize) {
                    listView.setPullLoadEnable(false);
                } else {
                    listView.setPullLoadEnable(true);
                }
                if (pageNo == 1) {
                    list.clear();
                }
                if (result.questions != null && result.questions.size() != 0) {
                    list.addAll(result.questions);
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

    @Override
    public void onClick(View v) {
        back();
    }

    @Override
    protected void initData(Bundle bundle) {
        initData();
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
