package com.wehang.ystv.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.wehang.ystv.R;
import com.wehang.ystv.adapter.LiveQusetiondapter;
import com.wehang.ystv.adapter.LiveVideoChartFragmentAdapter;
import com.wehang.ystv.adapter.RoomQusetiondapter;
import com.wehang.ystv.bo.Lives;
import com.wehang.ystv.ui.AddQuseTionActivity;
import com.wehang.ystv.ui.LiveWatchNewActivity;
import com.whcd.base.fragment.BaseFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class QuestinFragment extends BaseFragment {
    public ListView conversationListView;
    public RoomQusetiondapter liveQusetiondapter;
    private LiveWatchNewActivity activity;
    public String name="QuestinFragment";
    public TextView gzTw;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity= (LiveWatchNewActivity) context;
        LogUtils.i("TolkRoomFragment",activity.mLiveRoomId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.layout2, container, false);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initview();
    }
    private void initview(){
        conversationListView = (ListView) getView().findViewById(R.id.list_view);
        liveQusetiondapter = new RoomQusetiondapter(getActivity(),false,activity.sourceId,activity.WhatBigImg);
        conversationListView.setAdapter(liveQusetiondapter);
        gzTw=getView().findViewById(R.id.gzTw);
        gzTw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //提问
                Lives lives=new Lives();
                lives.sourceId=activity.sourceId;
                Intent intent=new Intent(getActivity(),AddQuseTionActivity.class);
                Bundle bundle=new Bundle();
                bundle.putSerializable("data",lives);
                intent.putExtra("bundle",bundle);
                getActivity().startActivityForResult(intent,1);
            }
        });
    }


}
