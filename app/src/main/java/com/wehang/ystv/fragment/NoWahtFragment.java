package com.wehang.ystv.fragment;

/**
 * Created by lenovo on 2017/7/31.
 */

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wehang.ystv.R;
import com.whcd.base.fragment.BaseFragment;

/**
 * Created by Anonymous on 2016/1/27.
 */
public class NoWahtFragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_nowaht, null);

        return view;
    }
}