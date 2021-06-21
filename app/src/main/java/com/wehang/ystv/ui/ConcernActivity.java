package com.wehang.ystv.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.google.gson.reflect.TypeToken;
import com.wehang.ystv.Constant;
import com.wehang.ystv.R;
import com.wehang.ystv.adapter.ChengyuanAdapter;
import com.wehang.ystv.bo.UserInfo;
import com.wehang.ystv.interfaces.UrlConstant;
import com.wehang.ystv.interfaces.result.GetFansesResult;
import com.wehang.ystv.interfaces.result.GetFocusesResult;
import com.wehang.ystv.utils.CommonUtil;
import com.wehang.ystv.utils.UserLoginInfo;
import com.whcd.base.activity.BaseActivity;
import com.whcd.base.interfaces.BaseData;
import com.whcd.base.interfaces.BaseResult;
import com.whcd.base.interfaces.HttpTool;
import com.whcd.base.widget.PageListView;
import com.whcd.base.widget.TopMenuBar;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConcernActivity extends BaseActivity implements AdapterView.OnItemClickListener, PageListView.PageListViewListener {
    private TopMenuBar topMenuBar;
    private int type;// 0--粉丝,1--关注
    private PageListView friendListView;
    //private SearchAdapter adapter;
    private ChengyuanAdapter adapter;
    private TextView tv;
    private int page = 1;
    private int pageSize = 10;
    private TextView total;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_concern;
    }

    @Override
    protected void initTitleBar() {
        type = getIntent().getIntExtra("type", 0);
        userID=getIntent().getStringExtra(Constant.USERID);
        String name=getIntent().getStringExtra("name");
        topMenuBar = (TopMenuBar) findViewById(R.id.topMenuBar);
        if (type == 1) {
            topMenuBar.setTitle(name+"的关注");
        }else if (type==2){
            topMenuBar.setTitle("报名人员列表");
        } else {
            topMenuBar.setTitle(name+"的粉丝");
        }

        topMenuBar.getLeftButton().setOnClickListener(this);

        /*token = UserLoginInfo.getUserToken();
        userID = getIntent().getStringExtra(Constant.USERID);*/
		/*Button rightButton = topMenuBar.getRightButton();
		Drawable drawable = getResources().getDrawable(R.drawable.sel_btn_person);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
		rightButton.setCompoundDrawables(null, null, drawable, null);
		rightButton.setOnClickListener(this);*/
    }

    private List<UserInfo> list = new ArrayList<>();

    @Override
    protected void initView() {
        friendListView = (PageListView) findViewById(R.id.friendListView);
        tv = (TextView) findViewById(R.id.tvem);

        View emptyView = findViewById(R.id.emptyView);
        friendListView.setEmptyView(emptyView);
        friendListView.setPullLoadEnable(false);
        friendListView.setPullRefreshEnable(false);
        friendListView.setOnItemClickListener(this);
        friendListView.setPageListViewListener(this);
        adapter = new ChengyuanAdapter(ConcernActivity.this, list);
        friendListView.setAdapter(adapter);
        total=findViewById(R.id.total);
    }

    @Override
    protected void onResume() {
        // TODO 自动生成方法
        super.onResume();

    }

    private String token, userID;

    protected void initData() {
        String url;
        Type typeToken;

        LogUtils.i("userId", userID + "");
        if (type == 1) {
            url = UrlConstant.FOCUSLIST;
            typeToken = new TypeToken<BaseResult<GetFocusesResult>>() {
            }.getType();
        }else if (type==2){
            url = UrlConstant.ENTEREDDUSERS;
            typeToken = new TypeToken<BaseResult<GetFansesResult>>() {
            }.getType();
        } else {
            url = UrlConstant.FANSLIST;
            typeToken = new TypeToken<BaseResult<GetFansesResult>>() {
            }.getType();
        }
        Map<String, String> params = new HashMap<String, String>();
        if (type==2){
            params.put("token", UserLoginInfo.getUserToken());
            params.put("sourceId", userID);
            params.put("page", page + "");
            params.put("pageSize", pageSize + "");
        }else {
            params.put("token", UserLoginInfo.getUserToken());
            params.put("userId", userID);
            params.put("page", page + "");
            params.put("pageSize", pageSize + "");
        }

        LogUtils.i("qiuqiutest", "" + url + "" + "token:" + token + "userID:" + userID);
        HttpTool.doPost(this, url, params, true, typeToken, new HttpTool.OnResponseListener() {

            @Override
            public void onSuccess(BaseData data) {
                friendListView.stopRefresh();

                if (type == 1) {
                    GetFocusesResult result = (GetFocusesResult) data;
                    list.addAll(result.focuses);
                    if (result.focuses.size() >= 10) {
                        friendListView.setPullLoadEnable(true);
                    } else {
                        friendListView.setPullLoadEnable(false);
                    }
                    total.setText("("+result.total+"人"+")");

                }else if (type==2){
                    GetFansesResult result = (GetFansesResult) data;
                    list.addAll(result.users);
                    if (result.users.size() >= 10) {
                        friendListView.setPullLoadEnable(true);
                    } else {
                        friendListView.setPullLoadEnable(false);
                    }
                    total.setText("("+result.total+"人"+")");
                }
                else {
                    GetFansesResult result = (GetFansesResult) data;
                    list.addAll(result.fanses);
                    if (result.fanses.size() >= 10) {
                        friendListView.setPullLoadEnable(true);
                    } else {
                        friendListView.setPullLoadEnable(false);
                    }
                    total.setText("("+result.total+"人"+")");
                }

                LogUtils.a("qiuqiutest", list.size() + list.toString());
                adapter.notifyDataSetChanged();
                //注意最后一个人物图标没有
            }

            @Override
            public void onError(int errorCode) {
                friendListView.stopRefresh();
                UserLoginInfo.loginOverdue(context, errorCode);
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (CommonUtil.isFirstClick())
            return;
        Intent intent;
        switch (v.getId()) {
            case R.id.title_btn_left:
                back();
                break;

            case R.id.title_btn_right:

                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, UserHomeActivty.class);
        UserInfo userInfo = (UserInfo) parent.getAdapter().getItem(position);
        Bundle bundle=new Bundle();
        bundle.putSerializable("data",userInfo);
        intent.putExtra("bundle",bundle);
        //intent.putExtra(Constant.USERID, userInfo.userId);
        startActivity(intent);
    }

    @Override
    protected void initData(Bundle bundle) {


        // TODO 自动生成方法
        if (type == 1) {
            LogUtils.i("qiuqiutest", type);
            tv.setText("还没有关注的人哦");
        } else {
            LogUtils.i("qiuqiutest", type);
            tv.setText("还没有人关注哦");
        }
        initData();
    }

    @Override
    public void onRefresh() {
        LogUtils.i("qiuqiutest", "r");
        page = 1;
        list.clear();
        initData();
    }

    @Override
    public void onLoadMore() {
        LogUtils.i("qiuqiutest", "l" + "");
        page++;
        initData();
    }
}