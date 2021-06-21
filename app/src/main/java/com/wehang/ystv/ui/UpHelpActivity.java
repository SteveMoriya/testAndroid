package com.wehang.ystv.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.wehang.ystv.R;
import com.whcd.base.activity.BaseActivity;
import com.whcd.base.widget.TopMenuBar;

public class UpHelpActivity extends BaseActivity {
    private TopMenuBar topMenuBar;
    @Override
    protected int getLayoutResId() {
        return R.layout.activity_up_help;
    }

    @Override
    protected void initTitleBar() {
        topMenuBar = (TopMenuBar) findViewById(R.id.topMenuBar);
        topMenuBar.setTitle("上传帮助");
        topMenuBar.getLeftButton().setOnClickListener(this);
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData(Bundle bundle) {

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        back();
    }
}
