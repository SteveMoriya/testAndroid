package com.wehang.ystv.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.gson.reflect.TypeToken;
import com.wehang.ystv.Constant;
import com.wehang.ystv.R;
import com.wehang.ystv.interfaces.UrlConstant;
import com.wehang.ystv.utils.CommonUtil;
import com.wehang.ystv.utils.UserLoginInfo;
import com.whcd.base.activity.BaseActivity;
import com.whcd.base.interfaces.BaseData;
import com.whcd.base.interfaces.BaseResult;
import com.whcd.base.interfaces.HttpTool;
import com.whcd.base.widget.TopMenuBar;


import java.util.HashMap;
import java.util.Map;

public class UpDataSexActivity extends BaseActivity {

	private TopMenuBar topMenuBar;
	private View girlLayout, boyLayout;
	private ImageView boyImg, girlImg;
	private int sex = -1;
	private int userSex;


	//如果为0，表示为注册
	private int type=-1;

	@Override
	protected int getLayoutResId() {
		return R.layout.activity_updata_sex;
	}

	@Override
	protected void initTitleBar() {
		topMenuBar = (TopMenuBar) findViewById(R.id.topMenuBar);
		topMenuBar.setTitle("性别");
		topMenuBar.getLeftButton().setOnClickListener(this);
		topMenuBar.getRightButton().setText("保存");
		topMenuBar.getRightButton().setOnClickListener(this);
	}

	@Override
	protected void initView() {
		girlLayout = findViewById(R.id.girlLayout);
		girlLayout.setOnClickListener(this);
		boyLayout = findViewById(R.id.boyLayout);
		boyLayout.setOnClickListener(this);

		boyImg = (ImageView) findViewById(R.id.boyImg);
		girlImg = (ImageView) findViewById(R.id.girlImg);
	}

	private void changeSex(int curSex) {

		if (sex == curSex) {
			return;
		}
		if (curSex == Constant.GIRL) {
			girlImg.setImageResource(R.drawable.btn_sex_girl_on);
			boyImg.setImageResource(R.drawable.btn_sex_boy_off);
		} else {
			girlImg.setImageResource(R.drawable.btn_sex_girl_off);
			boyImg.setImageResource(R.drawable.btn_sex_boy_on);
		}
		sex = curSex;
	}

	@Override
	protected void initData(Bundle bundle) {
		userSex = getIntent().getIntExtra("sex", Constant.GIRL);
		if (userSex==-1){
			userSex=0;
		}
		type=getIntent().getIntExtra("type",-1);
		changeSex(userSex);
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

            if (type==0){
                Intent intent = new Intent();
                intent.putExtra("sex", sex);
                setResult(RESULT_OK, intent);
                finish();
            }else{
				saveSex();
			}
			break;

		case R.id.girlLayout:

			changeSex(Constant.GIRL);
			break;

		case R.id.boyLayout:
			changeSex(Constant.BOY);
			break;
		default:
			break;
		}
	}

	private void saveSex() {
		/*String token = UserLoginInfo.getUserToken();
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("token", token);
		params.put("sex", sex+"");
		HttpTool.doPost(this, UrlConstant.UPDATEMYDATA, params, true, new TypeToken<BaseResult<BaseData>>(){}.getType(), new HttpTool.OnResponseListener() {
			
			@Override
			public void onSuccess(BaseData data) {
				Intent intent = new Intent();
				intent.putExtra("sex", sex);
				setResult(RESULT_OK, intent);
				finish();
			}
			
			@Override
			public void onError(int errorCode) {
                UserLoginInfo.loginOverdue(context, errorCode);
			}
		});
*/
		Intent intent = new Intent();
		intent.putExtra("sex", sex);
		setResult(RESULT_OK, intent);
		finish();
	}

//	@Override
//	public void onBackPressed() {
//		Intent data = new Intent();
//		data.putExtra("sex", sex);
//		Log.e(TAG, "------------------" + sex);
//		setResult(RESULT_OK, data);
//		finish();
//	}
}
