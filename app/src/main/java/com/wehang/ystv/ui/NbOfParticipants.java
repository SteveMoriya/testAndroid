package com.wehang.ystv.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.wehang.ystv.R;
import com.wehang.ystv.adapter.ChengyuanAdapter;
import com.whcd.base.activity.BaseActivity;
import com.whcd.base.widget.PageListView;
import com.whcd.base.widget.TopMenuBar;

//报名人员页面
public class NbOfParticipants extends BaseActivity {

    private TopMenuBar topMenuBar;
    private PageListView listView;
    private ChengyuanAdapter adapter;
    @Override
    protected int getLayoutResId() {
        return R.layout.activity_nb_of_participants;
    }

    @Override
    protected void initTitleBar() {
        topMenuBar = (TopMenuBar) findViewById(R.id.topMenuBar);
        topMenuBar.setTitle("报名人员列表");
        topMenuBar.getLeftButton().setOnClickListener(this);
    }

    @Override
    protected void initView() {
        listView=findViewById(R.id.chengyuanlistview);
        adapter=new ChengyuanAdapter(this,null);
        listView.setAdapter(adapter);
    }

    @Override
    protected void initData(Bundle bundle) {

    }
}
