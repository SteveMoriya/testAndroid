package com.wehang.ystv.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.wehang.ystv.R;
import com.wehang.ystv.utils.CommonUtil;
import com.whcd.base.activity.BaseActivity;
import com.whcd.base.widget.TopMenuBar;


/**
 * 
 * <p>
 * {关于我们}
 * </p>
 * 
 * @author 宋瑶 2016-7-5 上午9:36:26
 * @version V1.0
 * 
 * 
 * @modificationHistory=========================重大变更说明
 * @modify by user: 宋瑶 2016-7-5
 */
public class AboutUsDetailActivity extends BaseActivity {

	private TopMenuBar topMenuBar;

	@Override
	protected int getLayoutResId() {
		return R.layout.activity_about_us_detail;
	}

	@Override
	protected void initTitleBar() {
		topMenuBar = (TopMenuBar) findViewById(R.id.topMenuBar);
		topMenuBar.setTitle("关于我们");
		Button leftButton = topMenuBar.getLeftButton();
		leftButton.setOnClickListener(this);
		topMenuBar.hideRightButton();
	}

	@Override
	protected void initView() {

	}

	@Override
	protected void initData(Bundle bundle) {

	}

	@Override
	public void onClick(View v) {
		if (CommonUtil.isFirstClick())
			return;
		switch (v.getId()) {
		case R.id.title_btn_left:
			finish();
			break;

		default:
			break;
		}
	}

}
