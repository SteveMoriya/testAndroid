package com.whcd.base.component.easeui.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

public abstract class EaseBaseFragment extends Fragment{
    protected InputMethodManager inputMethodManager;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        
        initView();
        setUpView();
    }
    
    
    protected void hideSoftKeyboard() {
        if (getActivity().getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getActivity().getCurrentFocus() != null)
                inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
    
    /**
     * 初始化控件
     */
    protected abstract void initView();
    
    /**
     * 设置属性，监听等
     */
    protected abstract void setUpView();
    
    
}
