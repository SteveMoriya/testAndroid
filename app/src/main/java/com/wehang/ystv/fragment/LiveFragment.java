package com.wehang.ystv.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.blankj.utilcode.util.LogUtils;
import com.google.gson.reflect.TypeToken;
import com.wehang.ystv.R;
import com.wehang.ystv.adapter.LiveAdapter;
import com.wehang.ystv.bo.Lives;
import com.wehang.ystv.bo.UserInfo;
import com.wehang.ystv.interfaces.UrlConstant;
import com.wehang.ystv.interfaces.result.GetLvesResult;
import com.wehang.ystv.ui.HomeActivity;
import com.wehang.ystv.ui.LiveDetails;
import com.wehang.ystv.ui.LiveWatchNewActivity;
import com.wehang.ystv.ui.PayActivity;
import com.wehang.ystv.utils.UserLoginInfo;
import com.wehang.ystv.utils.Utils;
import com.whcd.base.fragment.BaseFragment;
import com.whcd.base.interfaces.BaseData;
import com.whcd.base.interfaces.BaseResult;
import com.whcd.base.interfaces.HttpTool;
import com.whcd.base.widget.CustomProgressDialog;
import com.whcd.base.widget.PageListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class LiveFragment extends BaseFragment implements PageListView.PageListViewListener{

    private View view = null;
    private PageListView listView;
    //private NearLiveAdapter adapter;
    private LiveAdapter adapter;
    private List<Lives> nearLives = new ArrayList<>();
    private int pageNo = 1;
    private int pageSize = 10;
    private View empty;

    public LiveFragment() {
        // Required empty public constructor
    }
    private HomeActivity activity;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity= (HomeActivity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_live, container, false);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
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

    private void initViews() {
        empty= LayoutInflater.from(getActivity()).inflate(R.layout.empty, null);
        listView = getView().findViewById(R.id.livelistView);
        listView.setPullRefreshEnable(true);
        listView.setPullLoadEnable(false);
        listView.setPageListViewListener(this);
        adapter = new LiveAdapter(getActivity(), nearLives,5);
        listView.setAdapter(adapter);
      /*  listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });*/
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    @Override
    public void onRefresh() {
        activity.getSourceInfo();
        pageNo=1;
        initData();
    }

    @Override
    public void onLoadMore() {
        pageNo++;
        initData();
    }

    private void initData() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", UserLoginInfo.getUserToken());
        params.put("page", pageNo + "");
        params.put("pageSize", pageSize + "");
        HttpTool.doPost(getActivity(), UrlConstant.YSLIVE, params, false, new TypeToken<BaseResult<GetLvesResult>>() {
        }.getType(), new HttpTool.OnResponseListener() {
            @Override
            public void onSuccess(BaseData data) {
                listView.stopRefresh();
                listView.stopLoadMore();
                GetLvesResult result = (GetLvesResult) data;
                if (result.lives.size() < pageSize) {
                    listView.setPullLoadEnable(false);
                } else {
                    listView.setPullLoadEnable(true);
                }
                if (pageNo == 1) {
                    nearLives.clear();
                }
                if (result.lives != null && result.lives.size() != 0) {
                    nearLives.addAll(result.lives);
                }
                LogUtils.i("adpter",nearLives.size()+nearLives.toString());
                isShowEmpty(nearLives.size());
                //adapter.setData(nearLives);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(int errorCode) {
                UserLoginInfo.loginOverdue(getActivity(), errorCode);
                listView.stopRefresh();
                listView.stopLoadMore();
            }
        });
    }

    private void enterRoom(final String sourceId) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", UserLoginInfo.getUserToken());
        params.put("sourceId", sourceId);
        final CustomProgressDialog dialog = CustomProgressDialog.show(getActivity(), "加载中...", true, null);
        HttpTool.doPost(getActivity(), UrlConstant.ENTERROOM, params, true, new TypeToken<BaseResult<UserInfo>>() {
        }.getType(), new HttpTool.OnResponseListener() {
            @Override
            public void onSuccess(BaseData data) {
                dialog.dismiss();
                UserInfo userInfo = (UserInfo) data;
                if (userInfo != null) {
                    Intent intent=new Intent(getActivity(),LiveWatchNewActivity.class);
                    Bundle bundle=new Bundle();
                    bundle.putSerializable("data",userInfo);
                    intent.putExtra("bundle",bundle);
                    intent.putExtra("sourceId",sourceId);
                    getActivity().startActivity(intent);
                }
            }

            @Override
            public void onError(int errorCode) {
                dialog.dismiss();
                UserLoginInfo.loginOverdue(getActivity(), errorCode);
            }
        });
    }
}
