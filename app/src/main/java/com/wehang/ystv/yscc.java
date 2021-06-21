package com.wehang.ystv;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.blankj.utilcode.util.LogUtils;
import com.tencent.TIMManager;
import com.tencent.TIMMessage;
import com.tencent.TIMMessageListener;
import com.wehang.txlibrary.LivePushActivity;
import com.wehang.ystv.ui.LiveWatvhActivity;
import com.wehang.txlibrary.ui.fragment.YsPushActivity;

import java.util.List;

public class yscc extends AppCompatActivity implements View.OnClickListener,TIMMessageListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yscc);
        findViewById(R.id.push111).setOnClickListener(this);
        findViewById(R.id.push222).setOnClickListener(this);
        findViewById(R.id.watch).setOnClickListener(this);
        TIMManager.getInstance().addMessageListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.push111:startActivity( new Intent(yscc.this,YsPushActivity.class));
                break;
            case R.id.push222:startActivity( new Intent(yscc.this,LivePushActivity.class));
                break;
            case R.id.watch:startActivity( new Intent(yscc.this, LiveWatvhActivity.class));
                break;
            default:break;
        }
    }

    @Override
    public boolean onNewMessages(List<TIMMessage> list) {
        LogUtils.i("onNewMessages",list.size());
        return false;
    }
}
