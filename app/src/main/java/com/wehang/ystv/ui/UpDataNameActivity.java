package com.wehang.ystv.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.wehang.ystv.R;
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

public class UpDataNameActivity extends BaseActivity {

	private TopMenuBar topMenuBar;
	private EditText nameEt;
	private View deleteView;
	private String name;

	@Override
	protected int getLayoutResId() {
		return R.layout.activity_updata_name;
	}

	@Override
	protected void initTitleBar() {
		topMenuBar = (TopMenuBar) findViewById(R.id.topMenuBar);
		topMenuBar.setTitle("昵称");
		topMenuBar.getLeftButton().setOnClickListener(this);
		topMenuBar.getRightButton().setText("保存");
		topMenuBar.getRightButton().setOnClickListener(this);
	}


	@Override
	protected void initView() {
		String userName = getIntent().getStringExtra("name");
		deleteView = findViewById(R.id.deleteView);
		deleteView.setOnClickListener(this);
		nameEt = (EditText) findViewById(R.id.nameEt);
		nameEt.setText(userName);
		nameEt.setSelection(userName.length());
		nameEt.addTextChangedListener(new TextWatcher() {
			//设置输入空格无效
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (s.toString().contains(" ")) {
					String[] str = s.toString().split(" ");
					String str1 = "";
					for (int i = 0; i < str.length; i++) {
						str1 += str[i];
					}
					nameEt.setText(str1);

					nameEt.setSelection(start);
				}

				}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				if (TextUtils.isEmpty(s)) {
					deleteView.setVisibility(View.INVISIBLE);
					return;
				}
				deleteView.setVisibility(View.VISIBLE);
			}
		});
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
			back();
			break;
			
		case R.id.title_btn_right:
			changeName();
			break;
			
		case R.id.deleteView:
			nameEt.setText("");
			break;
		default:
			break;
		}
	}

	private void changeName() {
		name = nameEt.getText().toString().trim();
		if (TextUtils.isEmpty(name)) {
			ToastUtil.makeText(context,"请输入昵称", Toast.LENGTH_SHORT).show();
			return;
		}
		Intent intent = new Intent();
		intent.putExtra("name", name);
		setResult(RESULT_OK, intent);
		finish();
		/*String token = UserLoginInfo.getUserToken();
		Map<String, String> params = new HashMap<String, String>();
		params.put("token", token);
		params.put("name", name);
		HttpTool.doPost(this, UrlConstant.UPDATEMYDATA, params, true, new TypeToken<BaseResult<BaseData>>(){}.getType(), new HttpTool.OnResponseListener() {
			
			@Override
			public void onSuccess(BaseData data) {


			}
			
			@Override
			public void onError(int errorCode) {
                UserLoginInfo.loginOverdue(context, errorCode);
			}
		});*/
	}
}
