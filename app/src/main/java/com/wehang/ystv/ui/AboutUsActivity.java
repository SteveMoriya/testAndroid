package com.wehang.ystv.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.blankj.utilcode.util.AppUtils;
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
public class AboutUsActivity extends BaseActivity {

	private TopMenuBar topMenuBar;
	private TextView tv_vision_code;

	@Override
	protected int getLayoutResId() {
		return R.layout.activity_about_us;
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
		/*findViewById(R.id.communityConventionTv).setOnClickListener(this);
		findViewById(R.id.privacyPolicyTv).setOnClickListener(this);

		findViewById(R.id.contactTv).setOnClickListener(this);*/
		findViewById(R.id.termsOfServiceTv).setOnClickListener(this);
		tv_vision_code = (TextView) findViewById(R.id.tv_vision_code);
		tv_vision_code.setText("V "+AppUtils.getAppVersionName());
	}

	@Override
	protected void initData(Bundle bundle) {

	}

	@Override
	public void onClick(View v) {
		if (CommonUtil.isFirstClick())
			return;
		Intent intent = new Intent(context, AboutDetailActivity.class);

		switch (v.getId()) {
		case R.id.title_btn_left:
			finish();
			break;
		/*case R.id.communityConventionTv:
			intent.putExtra("type", 0);
			startActivity(intent);
			break;
		case R.id.privacyPolicyTv:
			intent.putExtra("type", 1);
			startActivity(intent);
			break;

		case R.id.contactTv:
			intent.putExtra("type", 3);
			startActivity(intent);
			break;*/
			case R.id.termsOfServiceTv:
				intent.putExtra("type", 2);
				startActivity(intent);
				break;
		default:
			break;
		}
	}

}
