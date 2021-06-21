package com.wehang.ystv.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.wehang.ystv.R;
import com.wehang.ystv.adapter.HomeFragmentPagerAdapter;
import com.wehang.ystv.myappview.HomeViewPager;
import com.wehang.ystv.ui.SearchActivity;
import com.whcd.base.utils.DisplayUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment implements HomeViewPager.OnScrollChanged ,View.OnClickListener{
    private HomeViewPager homeViewPager;
    private View mIndictorView;


    private RelativeLayout.LayoutParams params;
    private List<Fragment> homeFragmentList;
    private List<LinearLayout> mCheckedTvs;
    private int mTabWidth,mLineWidth;
    private int startWidth;
    private ImageView searchImg;
    private HomeFragmentPagerAdapter pagerAdapter;
    public SearchFragment() {
        // Required empty public constructor
    }

    private void initFragment() {
        homeFragmentList = new ArrayList<Fragment>();
        homeFragmentList.add(new searchGetFragment(1));
        homeFragmentList.add(new searchGetFragment(2));
        homeFragmentList.add(new searchGetFragment(3));
        tab1= (searchGetFragment) homeFragmentList.get(0);
        tab2= (searchGetFragment) homeFragmentList.get(1);
        tab3= (searchGetFragment) homeFragmentList.get(2);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
    }
    private void initViews(){
        initFragment();
        homeViewPager = getView().findViewById(R.id.homeViewPager);
        searchImg = getView().findViewById(R.id.searchImg);
        searchImg.setVisibility(View.GONE);
        pagerAdapter = new HomeFragmentPagerAdapter(getActivity().getSupportFragmentManager(), homeFragmentList);
        homeViewPager.setAdapter(pagerAdapter);
        homeViewPager.setOffscreenPageLimit(5);
        homeViewPager.setOnScrollChangedListener(this);


        homeViewPager.setCurrentItem(0);
        int[] mCheckedIds = {R.id.nearCT,R.id.newCT, R.id.hotCT };
        mCheckedTvs = new ArrayList<LinearLayout>();
        for (int checkedTv : mCheckedIds) {
            LinearLayout checked = (LinearLayout) getView().findViewById(checkedTv);
            mCheckedTvs.add(checked);
            checked.setOnClickListener(this);
        }
        searchImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                getActivity().startActivity(intent);
            }
        });
        initIndicator();
    }

    @Override
    public void onScrollChanged(int l, int t, int oldl, int oldt) {
        params.leftMargin = l * mTabWidth / homeViewPager.getWidth() + startWidth ;
        mIndictorView.setLayoutParams(params);
    }
    private searchGetFragment tab1,tab2,tab3;
    @Override
    public void onResume() {
        super.onResume();

    }
    public void initTab(){
        tab1.initData();
        tab2.initData();
        tab3.initData();
    }
    @Override
    public void onChange() {
        homeViewPager.setCurrentItem(0);
    }
    private void initIndicator() {
        mIndictorView = getView().findViewById(R.id.indictorView);
        mTabWidth = DisplayUtils.dip2px(getActivity(), 240) / mCheckedTvs.size();
        mLineWidth=DisplayUtils.dip2px(getActivity(), 300) / mCheckedTvs.size();
        //mLineWidth=0;
        params = (RelativeLayout.LayoutParams) mIndictorView.getLayoutParams();
        params.width = mTabWidth/2;
        startWidth = mTabWidth/2/2;
        params.leftMargin = startWidth;
        mIndictorView.setLayoutParams(params);
    }
    @Override
    public void onClick(View v) {
        for (int i = 0; i < mCheckedTvs.size(); i++) {
            if (v == mCheckedTvs.get(i)) {
                homeViewPager.setCurrentItem(i, false);
               /* if (i == 3) {
                    isSearch = false;
                    searchImg.setImageResource(R.drawable.ic_photo);
                } else {
                    isSearch = true;
                    searchImg.setImageResource(R.drawable.btn_search_press);
                }*/
            }
        }
    }

}
