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
import com.wehang.ystv.adapter.ChengyuanAdapter;
import com.wehang.ystv.adapter.KeJianAdapter;
import com.wehang.ystv.adapter.LiveAdapter;
import com.wehang.ystv.bo.Coursewares;
import com.wehang.ystv.interfaces.UrlConstant;
import com.wehang.ystv.interfaces.result.GetClassResult;
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

public class KeJianActivity extends BaseActivity implements PageListView.PageListViewListener{
    private TopMenuBar topMenuBar;
    private PageListView listView;
    private KeJianAdapter adapter;
    private View empty;
    private List<Coursewares>list=new ArrayList<>();
    private int pageNo = 1;
    private int pageSize = 10;
    @Override
    protected int getLayoutResId() {
        return R.layout.activity_ke_jian;
    }

    @Override
    protected void initTitleBar() {
        topMenuBar = (TopMenuBar) findViewById(R.id.topMenuBar);
        topMenuBar.setTitle("课件选择");
        topMenuBar.getLeftButton().setOnClickListener(this);
        topMenuBar.getRightButton().setText("帮助");
        topMenuBar.getRightButton().setOnClickListener(this);
    }

    @Override
    protected void initView() {
        listView=findViewById(R.id.kejianlistview);
        adapter=new KeJianAdapter(this,list);
        listView.setPullRefreshEnable(true);
        listView.setPullLoadEnable(false);
        listView.setPageListViewListener(this);

        listView.setAdapter(adapter);
        empty= LayoutInflater.from(KeJianActivity.this).inflate(R.layout.kejian_empty, null);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent=new Intent(KeJianActivity.this,AddLiveActivity.class);
                Bundle bundle=new Bundle();
                bundle.putSerializable("data",list.get(i-1));
                intent.putExtra("bundle",bundle);
                setResult(RESULT_OK,intent);
                finish();
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
        getKejian();
    }
    private void getKejian(){
        //获取分类信息和课件信息
        Map<String, String> params;
        params = new HashMap<String, String>();
        params.put("token",UserLoginInfo.getUserToken());
        params.put("page", pageNo + "");
        params.put("pageSize", pageSize + "");
        HttpTool.doPost(this, UrlConstant.QUSERYCOURSEWARE, params, true, new TypeToken<BaseResult<GetClassResult>>() {
        }.getType(), new HttpTool.OnResponseListener() {
            @Override
            public void onSuccess(BaseData data) {
                LogUtils.i("QUSERYCOURSEWARE",data.toString());
                listView.stopRefresh();
                listView.stopLoadMore();
                GetClassResult result= (GetClassResult) data;
                LogUtils.i("adpter",result.coursewares.size()+result.coursewares.toString());
                if (pageNo == 1) {
                    list.clear();
                }
                if (result.coursewares!=null&&result.coursewares.size()!=0){
                    list.addAll(result.coursewares);
                }
                if (result.coursewares.size() < pageSize) {
                    listView.setPullLoadEnable(false);
                } else {
                    listView.setPullLoadEnable(true);
                }


                isShowEmpty(list.size());
                LogUtils.i("adpter",list.size()+list.toString());
                //adapter.setData(nearLives);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(int errorCode) {
                listView.stopRefresh();
                listView.stopLoadMore();
                LogUtils.i("info","no");
                UserLoginInfo.loginOverdue(context, errorCode);
            }
        });
    }

    @Override
    public void onRefresh() {
        pageNo=1;
        initData(null);
    }

    @Override
    public void onLoadMore() {
        pageNo++;
        initData(null);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.title_btn_right:
                startActivity(new Intent(context,UpHelpActivity.class));
                break;
            case R.id.title_btn_left:
                back();
                break;
        }



    }
}
