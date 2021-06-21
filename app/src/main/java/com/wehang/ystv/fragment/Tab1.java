package com.wehang.ystv.fragment;

/**
 * Created by lenovo on 2017/7/31.
 */

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.wehang.txlibrary.R;
import com.wehang.ystv.adapter.LiveVideoChartFragmentAdapter;
import com.whcd.base.fragment.BaseFragment;

/**
 * Created by Anonymous on 2016/1/27.
 */
public class Tab1 extends BaseFragment {
  /*  public ListView conversationListView;
    public LiveVideoChartFragmentAdapter conversationAdapter;*/
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.layout1, null);

        return view;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initview();
    }
    private void initview(){
        /*conversationListView = (ListView) getView().findViewById(com.wehang.ystv.R.id.list_view);
        conversationAdapter = new LiveVideoChartFragmentAdapter(getActivity());
        conversationListView.setAdapter(conversationAdapter);*/
    }
}