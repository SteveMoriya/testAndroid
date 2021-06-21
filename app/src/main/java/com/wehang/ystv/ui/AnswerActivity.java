package com.wehang.ystv.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.LogUtils;
import com.google.gson.reflect.TypeToken;
import com.wehang.ystv.R;
import com.wehang.ystv.bo.QuestionMessages;
import com.wehang.ystv.interfaces.UrlConstant;
import com.wehang.ystv.utils.CommonUtil;
import com.wehang.ystv.utils.ToastUtil;
import com.wehang.ystv.utils.UserLoginInfo;
import com.whcd.base.activity.BaseActivity;
import com.whcd.base.interfaces.BaseData;
import com.whcd.base.interfaces.BaseResult;
import com.whcd.base.interfaces.HttpTool;
import com.whcd.base.widget.TopMenuBar;

import java.util.HashMap;
import java.util.Map;

public class AnswerActivity extends BaseActivity {

	private TopMenuBar topMenuBar;
	private EditText signEt;
	private TextView tipTv;
	private int count = 150;
	private String userSign;
	private String sign;

	@Override
	protected int getLayoutResId() {
		return R.layout.activity_answer;
	}

	@Override
	protected void initTitleBar() {
		topMenuBar = (TopMenuBar) findViewById(R.id.topMenuBar);
		topMenuBar.setTitle("回答");
		topMenuBar.getLeftButton().setOnClickListener(this);
		topMenuBar.getRightButton().setText("提交");
		topMenuBar.getRightButton().setOnClickListener(this);

	}

	@Override
	protected void initView() {
		signEt = (EditText) findViewById(R.id.signEt);
		tipTv = (TextView) findViewById(R.id.tipTv);
		tipTv.setText((signEt.getText().toString().length()) + "/"+count);
	}

	@Override
	protected void initData(Bundle bundle) {
		bundle=getIntent().getBundleExtra("bundle");
		questionMessages= (QuestionMessages) bundle.getSerializable("data");
		int length;
		if (TextUtils.isEmpty(userSign)) {
			length = 0;
		} else {

			length = userSign.length();
		}
		//tipTv.setText((150 - length) + "");
		signEt.setText(userSign);
		signEt.addTextChangedListener(new TextWatcher() {
			private CharSequence temp = userSign;// 监听前的文本
			int editStart;
			int editEnd;

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				temp = s;
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				editStart = signEt.getSelectionStart();
				editEnd = signEt.getSelectionEnd();
				if (temp.length() > count) {
					try {
						if (s.length() <= 150) {
							s.delete(editStart - 1, editEnd);
							int tempSelection = editStart;
							Log.e(TAG, "---------------------editStart:" + editStart);
							signEt.setText(s);
							signEt.setSelection(tempSelection);
						}
					} catch (Exception e) {
						ToastUtil.makeText(context,"输入文字过长", Toast.LENGTH_SHORT).show();
					}
				}
				tipTv.setText((signEt.getText().toString().length()) + "/"+count);
			}
		});
	}
	private QuestionMessages questionMessages;
	@Override
	public void onClick(View v) {
		if (CommonUtil.isFirstClick())
			return;
		switch (v.getId()) {
		case R.id.title_btn_left:
			back();
			break;

		case R.id.title_btn_right:
			String str=signEt.getText().toString().trim();
			if (TextUtils.isEmpty(str)){
				ToastUtil.makeText(context,"回答不能为空",Toast.LENGTH_SHORT).show();
				return;
			}
			saveSign(questionMessages);
			break;
		default:
			break;
		}
	}
	//回答
	private void saveSign(QuestionMessages questionMessages) {

		final String token = UserLoginInfo.getUserToken();
		Map<String, String> params = new HashMap<String, String>();
		params.put("token", token);
		LogUtils.i("sourceId",questionMessages.sourceId+"");
		params.put("sourceId", questionMessages.sourceId+"");
		params.put("content",signEt.getText().toString().trim());
		params.put("questionId",questionMessages.questionId);
		HttpTool.doPost(this, UrlConstant.ANSWERQUESTION, params, true, new TypeToken<BaseResult<BaseData>>() {
		}.getType(), new HttpTool.OnResponseListener() {

			@Override
			public void onSuccess(BaseData data) {
				Intent intent=new Intent();
				//intent.putExtra("answer",signEt.getText().toString().trim());
                ToastUtil.makeText(context,"回答成功",Toast.LENGTH_SHORT).show();
				setResult(RESULT_OK,intent);
				finish();
			}

			@Override
			public void onError(int errorCode) {
                UserLoginInfo.loginOverdue(context, errorCode);
			}
		});
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}
}
