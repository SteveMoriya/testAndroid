package com.wehang.ystv.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.LogUtils;
import com.google.gson.reflect.TypeToken;
import com.wehang.ystv.R;
import com.wehang.ystv.bo.UserInfo;
import com.wehang.ystv.interfaces.UrlConstant;
import com.wehang.ystv.utils.CommonUtil;
import com.wehang.ystv.utils.ToastUtil;
import com.wehang.ystv.utils.UserLoginInfo;
import com.whcd.base.activity.BaseActivity;
import com.whcd.base.interfaces.BaseData;
import com.whcd.base.interfaces.BaseResult;
import com.whcd.base.interfaces.HttpTool;
import com.whcd.base.widget.CustomProgressDialog;
import com.whcd.base.widget.TopMenuBar;


import java.util.HashMap;
import java.util.Map;



/**
 * 
 * <p>
 * {个人安全等级}
 * </p>
 * 
 * @author 宋瑶 2016-7-5 上午9:44:52
 * @version V1.0
 * 
 * 
 * @modificationHistory=========================重大变更说明
 * @modify by user: 宋瑶 2016-7-5
 */
//意见反馈
public class FeedbackActivity extends BaseActivity {
	private static final int BINT_PHOEN_NUM = 0x110;
	private static final int IDENTIFY_USER = 0x111;
	private TextView safeTv, bindPhoneTv, tipTv, identifyTv,commitBt;
	private TopMenuBar topMenuBar;
	private UserInfo userInfo;
	private EditText et_comment;

	@Override
	protected int getLayoutResId() {
		return R.layout.activity_safe;
	}

	@Override
	protected void initTitleBar() {
		topMenuBar = (TopMenuBar) findViewById(R.id.topMenuBar);
		topMenuBar.setTitle("意见反馈");
		Button leftButton = topMenuBar.getLeftButton();
		leftButton.setOnClickListener(this);
		topMenuBar.hideRightButton();
	}

	@Override
	protected void initView() {
		/*safeTv = (TextView) findViewById(R.id.safeTv);
		tipTv = (TextView) findViewById(R.id.tipTv);
		bindPhoneTv = (TextView) findViewById(R.id.bindPhoneTv);
		identifyTv = (TextView) findViewById(R.id.identifyTv);*/
		commitBt= (TextView) findViewById(R.id.comitBtn);
		et_comment= (EditText) findViewById(R.id.et_comment);
		commitBt.setOnClickListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		initData(null);
	}

	@Override
	protected void initData(Bundle bundle) {
		userInfo = UserLoginInfo.getUserInfo(this);

		/*String level;
		if (TextUtils.isEmpty(userInfo.phone) && userInfo.acceptStatus != 1) {
			level = "低";
		} else if (!TextUtils.isEmpty(userInfo.phone) && userInfo.acceptStatus == 1) {
			level = "高";
		} else {
			level = "中";
		}
		String str = "安全等级：" + level;
		SpannableStringBuilder style = new SpannableStringBuilder(str);
		style.setSpan(new ForegroundColorSpan(Color.parseColor("#78d98d")), str.length() - 1, str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		safeTv.setText(style);

		if (TextUtils.isEmpty(userInfo.phone) || userInfo.acceptStatus != 1) {
			tipTv.setVisibility(View.VISIBLE);
		} else {
			tipTv.setVisibility(View.GONE);
		}

		if (TextUtils.isEmpty(userInfo.phone)) {
			bindPhoneTv.setText("未绑定");
			findViewById(R.id.bindPhoneView).setOnClickListener(this);
		} else {
			bindPhoneTv.setTextColor(Color.parseColor("#5f85c9"));
			bindPhoneTv.setCompoundDrawables(null, null, null, null);
			bindPhoneTv.setText(userInfo.phone);
			findViewById(R.id.bindPhoneView).setOnClickListener(null);
		}

		identifyTv.setText(userInfo.acceptStatus == 0 ? "未认证" : (userInfo.acceptStatus == 1 ? "已认证" : "认证中"));
		if (userInfo.acceptStatus != 0) {
			identifyTv.setCompoundDrawables(null, null, null, null);
			identifyTv.setTextColor(Color.parseColor("#5f85c9"));
			findViewById(R.id.identifyView).setOnClickListener(null);
		} else {
			identifyTv.setTextColor(getResources().getColor(R.color.addrgray));
			findViewById(R.id.identifyView).setOnClickListener(this);
		}*/
	}

	@Override
	public void onClick(View v) {
		if (CommonUtil.isFirstClick())
			return;
		switch (v.getId()) {
			case R.id.title_btn_left:
				back();
				break;

		/*case R.id.bindPhoneView:
			Intent intent = new Intent(this, LoginByPhoneActivity.class);
			intent.putExtra("type", 1);
			startActivityForResult(intent, BINT_PHOEN_NUM);
			break;

		case R.id.identifyView:
			intent = new Intent(this, CertificationNameActivity.class);
			startActivityForResult(intent, IDENTIFY_USER);
			break;*/
			case R.id.comitBtn:
				commitFk();
				break;

			default:
				break;
		}
	}
	//提交意见
	private void commitFk(){
		if (!TextUtils.isEmpty(et_comment.getText().toString().trim())){
			Map<String, String> params;
			params = new HashMap<String, String>();
			params.put("token", UserLoginInfo.getUserToken());
			params.put("content", et_comment.getText().toString().trim());
			dialog = CustomProgressDialog.show(context, "加载中...", true, null);

			HttpTool.doPost(this, UrlConstant.SUBMITFEEDBACK, params, true, new TypeToken<BaseResult<BaseData>>() {
			}.getType(), new HttpTool.OnResponseListener() {
				@Override
				public void onSuccess(BaseData data) {
					LogUtils.a(data);
					dialogDismiss();

					ToastUtil.makeText(FeedbackActivity.this,"提交成功！", Toast.LENGTH_SHORT).show();
					back();
				}

				@Override
				public void onError(int errorCode) {
					dialogDismiss();
					UserLoginInfo.loginOverdue(context, errorCode);
				}
			});

		}else{
			ToastUtil.makeText(this,"请给点意见吧!", Toast.LENGTH_SHORT).show();
		}
	}
}
