package com.wehang.ystv.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.wehang.ystv.R;
import com.wehang.ystv.utils.CommonUtil;
import com.whcd.base.activity.BaseActivity;
import com.whcd.base.widget.TopMenuBar;


/**
 * 关于我们详细
 * <p>
 * {类文件说明}
 * </p>
 * 
 * @author 包川 2016-8-15 下午6:36:43
 * @version V1.0
 * 
 * 
 * @modificationHistory=========================重大变更说明
 * @modify by user: 包川2016-8-15
 */
public class AboutDetailActivity extends BaseActivity {

	private TopMenuBar topMenuBar;
	private int type;// 0社区公约1隐私政策2服务条款
	private TextView contentTv;

	@Override
	protected int getLayoutResId() {
		return R.layout.activity_about_detail;
	}

	@Override
	protected void initTitleBar() {
		if (getIntent() != null && getIntent().hasExtra("type")) {
			type = getIntent().getIntExtra("type", 0);
		}
		topMenuBar = (TopMenuBar) findViewById(R.id.topMenuBar);
		if (type == 0) {

		}
		topMenuBar.setTitle(type == 0 ? "社区公约" : type == 1 ? "隐私政策" : type == 2 ? "服务条款" : type == 3 ? "联系我们" : "暂无");
		Button leftButton = topMenuBar.getLeftButton();
		leftButton.setOnClickListener(this);
		topMenuBar.hideRightButton();
	}

	@Override
	protected void initView() {
		contentTv = (TextView) findViewById(R.id.contentTv);
	}

	@Override
	protected void initData(Bundle bundle) {
		if (type == 0) {
			contentTv.setText(R.string.community_convention);
		} else if (type == 1) {
			contentTv.setText(R.string.privacy_policy);
		} else if (type == 2) {
			contentTv.setText(R.string.terms_of_service);
		} else if (type == 3) {
			contentTv.setText(R.string.contact_us);
            findViewById(R.id.contentTv1).setVisibility(View.VISIBLE);
		}
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
