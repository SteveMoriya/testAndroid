package com.wehang.ystv.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.LogUtils;
import com.google.gson.reflect.TypeToken;
import com.wehang.ystv.Constant;
import com.wehang.ystv.Myapplication;
import com.wehang.ystv.R;
import com.wehang.ystv.adapter.AddPhotoChooseAdapter;
import com.wehang.ystv.adapter.PhotoChooseAdapter;
import com.wehang.ystv.bo.ApplyInfo;
import com.wehang.ystv.bo.Lives;
import com.wehang.ystv.bo.PhotoInfo;
import com.wehang.ystv.interfaces.UrlConstant;
import com.wehang.ystv.interfaces.result.GetFileResult;
import com.wehang.ystv.utils.CommonUtil;
import com.wehang.ystv.utils.ToastUtil;
import com.wehang.ystv.utils.UserLoginInfo;
import com.wehang.ystv.widget.NotScrollGridView;
import com.whcd.base.activity.AlbumActivity;
import com.whcd.base.activity.BaseActivity;
import com.whcd.base.activity.ImagePagerActivity;
import com.whcd.base.bo.PictureItem;
import com.whcd.base.interfaces.BaseData;
import com.whcd.base.interfaces.BaseResult;
import com.whcd.base.interfaces.HttpTool;
import com.whcd.base.utils.ImageUtils;
import com.whcd.base.widget.BottomPopupWindow;
import com.whcd.base.widget.CustomProgressDialog;
import com.whcd.base.widget.TopMenuBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddQuseTionActivity extends BaseActivity implements AddPhotoChooseAdapter.DeleteSelectPictureListener{

	private TopMenuBar topMenuBar;
	private EditText signEt;
	private TextView tipTv;
	private int count = 150;
	private String userSign;
	private String sign;
	private Lives lives;

	// 三张图片
	private NotScrollGridView imageGridView;
	private AddPhotoChooseAdapter pictureAdapter = null;
	private List<PhotoInfo> photoList = new ArrayList<PhotoInfo>();
	private String capturePath;// 照相图片名称
	private ApplyInfo applyInfo = null;
	private int currentChooseIndex = 0;
	private String certificatesOne = null;
	private String Certificatestwo = null;
	private String certificatesThree = null;

	private BottomPopupWindow popupWindow;
	private LayoutInflater inflater = null;
	private LinearLayout rootLayout = null;

	@Override
	protected int getLayoutResId() {
		return R.layout.activity_addqusetion;
	}

	@Override
	protected void initTitleBar() {
		topMenuBar = (TopMenuBar) findViewById(R.id.topMenuBar);
		topMenuBar.setTitle("提问");
		topMenuBar.getLeftButton().setOnClickListener(this);
		topMenuBar.getRightButton().setText("提交");
		topMenuBar.getRightButton().setOnClickListener(this);
	}

	@Override
	protected void initView() {

		inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		rootLayout = (LinearLayout) findViewById(R.id.root_layout);
		signEt = (EditText) findViewById(R.id.signEt);
		tipTv = (TextView) findViewById(R.id.tipTv);
		tipTv.setText((signEt.getText().toString().length()) + "/"+count);



		imageGridView = (NotScrollGridView) findViewById(R.id.imageGridView);

		photoList.add(new PhotoInfo());
		photoList.add(new PhotoInfo());
		photoList.add(new PhotoInfo());
		pictureAdapter = new AddPhotoChooseAdapter(this, photoList, imageGridView);
		imageGridView.setAdapter(pictureAdapter);

		pictureAdapter.setListener(this);
		imageGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				PhotoInfo item = (PhotoInfo) imageGridView.getItemAtPosition(position);
				/*if (item.path == null) {

				} else {
					Intent intent = new Intent(context, ImagePagerActivity.class);
					ArrayList<String> urls = new ArrayList<String>();
					//判断是网络图片还是本地 0默认网络
					int url_bd_wl=0;
					if (item.path.indexOf("http") != -1) {
						url_bd_wl=0;
						for (int i=0;i<photoList.size();i++){
							urls.add(photoList.get(i).path);
						}
						//urls.add(item.path);
					} else {
						url_bd_wl=1;
						for (int i=0;i<photoList.size();i++){
							urls.add("file://" + photoList.get(i).path);

						}

					}
					intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, urls);
					intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
					intent.putExtra("startPosition",position);
					intent.putExtra("type",url_bd_wl);
					startActivity(intent);*/
				currentChooseIndex = position;
				showChoosePhotoPopupwindow();
			}
		});
	}

	@Override
	protected void initData(Bundle bundle) {

		bundle=getIntent().getBundleExtra("bundle");
		lives = (Lives) bundle.getSerializable("data");
		userSign = "";
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

	@Override
	public void onClick(View v) {
		if (CommonUtil.isFirstClick())
			return;
		switch (v.getId()) {
		case R.id.title_btn_left:
			back();
			break;

		case R.id.title_btn_right:
			dialogUpload = CustomProgressDialog.show(this, "正在上传...", false, null);
			applyLive(0);
			break;

		default:
			break;
		}
	}

	private void saveSign() {
		sign = signEt.getText().toString().trim();
		if (TextUtils.isEmpty(sign)) {
			ToastUtil.makeText(context,"请输入提问内容", Toast.LENGTH_SHORT).show();
			signEt.requestFocus();
			if (isValidContext(context) && dialogUpload.isShowing()) {
				dialogUpload.dismiss();
			}
			return;
		}

		String token = UserLoginInfo.getUserToken();
		Map<String, String> params = new HashMap<String, String>();
		params.put("token", token);
		params.put("sourceId", lives.sourceId);
		params.put("content",sign);
		String string="";
		if (!TextUtils.isEmpty(certificatesOne)){
			string=certificatesOne;
		}
		if (!TextUtils.isEmpty(Certificatestwo)){
			string=string+","+Certificatestwo;
		}
		if (!TextUtils.isEmpty(certificatesThree)){
			string=string+","+certificatesThree;
		}
		params.put("pics",string);
		HttpTool.doPost(this, UrlConstant.SUBMITQUSETION, params, true, new TypeToken<BaseResult<BaseData>>() {
		}.getType(), new HttpTool.OnResponseListener() {

			@Override
			public void onSuccess(BaseData data) {
				ToastUtil.makeText(context, "提问成功", Toast.LENGTH_SHORT).show();
				if (isValidContext(context) && dialogUpload.isShowing()) {
					dialogUpload.dismiss();
				}
				setResult(RESULT_OK);
				finish();
			}

			@Override
			public void onError(int errorCode) {
                UserLoginInfo.loginOverdue(context, errorCode);
				ToastUtil.makeText(context, "提问失败", Toast.LENGTH_SHORT).show();
				if (isValidContext(context) && dialogUpload.isShowing()) {
					dialogUpload.dismiss();
				}
			}
		});
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();

	}

	/**
	 * 选择拍照或者相册ppw
	 */
	public void showChoosePhotoPopupwindow() {
		View popView = inflater.inflate(R.layout.view_choose_photo, rootLayout, false);
		View contentView = popView.findViewById(R.id.pop_conent);
		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) contentView.getLayoutParams();
		params.gravity = Gravity.BOTTOM;
		contentView.setTag(1);
		contentView.setLayoutParams(params);
		popView.findViewById(R.id.take_photo).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isValidContext(context) && popupWindow.isShowing()) {
					popupWindow.dismiss();
				}
				capturePath = ImageUtils.takePhoto(context);

			}
		});
		popView.findViewById(R.id.album).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, AlbumActivity.class);
				intent.putExtra("type", 1);
				intent.putExtra("maxSelectNumber", 1);
				startActivityForResult(intent, AlbumActivity.TO_ALBUM_REQUEST_CODE);
				if (isValidContext(context) && popupWindow.isShowing()) {
					popupWindow.dismiss();
				}
			}
		});
		popView.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isValidContext(context) && popupWindow.isShowing()) {
					popupWindow.dismiss();
				}
			}
		});
		popupWindow = new BottomPopupWindow(this, popView);
		popupWindow.showAtLocation(rootLayout, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
	}

	@Override
	public void onDelete(PhotoInfo item) {
		/*for (PhotoInfo info : photoList) {
			LogUtils.i("photoList",photoList.toString()+";;"+item.name+";;"+item.path);
			if (info.path!=null||item.path!=null){
				if (info.path.equals(item.path)) {
					info.path = null;
					break;
				}
			}

		}*/
		String delPath=item.path;
		LogUtils.i("photoList",photoList.toString()+";;"+item.name+";;"+item.path);
		for (int i=0;i<photoList.size();i++){

			String path=photoList.get(i).path;
			if (path!=null){
				if (path.equals(delPath)){
					photoList.get(i).path=null;
					break;
				}
			}
		}
		pictureAdapter.notifyDataSetChanged();
	}
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == AlbumActivity.TO_ALBUM_REQUEST_CODE) {
				@SuppressWarnings("unchecked")
				ArrayList<PictureItem> selectImageList = (ArrayList<PictureItem>) data.getSerializableExtra("selectImageList");
				for (PictureItem pictureItem : selectImageList) {
					String picturePath = pictureItem.getImagePath();
					// 如果图片不在应用创建的目录下，则先移动图片
					String newPicturePath = picturePath;
					if (picturePath.indexOf(Constant.ROOT_PATH) < 0) {
						newPicturePath = Myapplication.instance.getStringAppParam("projectPath") + "/" + System.currentTimeMillis() + ".jpg";
						ImageUtils.copyFile(picturePath, newPicturePath);
					}
					ImageUtils.compressImage(newPicturePath);
					pictureItem.setImagePath(newPicturePath);
					photoList.get(currentChooseIndex).path = newPicturePath;
				}
				pictureAdapter.notifyDataSetChanged();
			} else if (requestCode == AlbumActivity.TAKE_PHOTO_REQUEST_CODE) {
				ImageUtils.compressImage(capturePath);
				photoList.get(currentChooseIndex).path = capturePath;
				pictureAdapter.notifyDataSetChanged();
			}
		}
	}
	private CustomProgressDialog dialogUpload = null;
	public void applyLive(final int index) {
		if (index < 3) {
			LogUtils.a("filepath",photoList.get(index).path);
			if (photoList.get(index).path!=null){
				HttpTool.doUpload(context, UrlConstant.UPLOADFILE, photoList.get(index).path, null, false, new TypeToken<BaseResult<GetFileResult>>() {
				}.getType(), new HttpTool.OnResponseListener() {
					@Override
					public void onSuccess(BaseData data) {
						GetFileResult fileInfo = (GetFileResult) data;
						LogUtils.a("filepath",photoList.get(index).path);
						if (index == 0) {
							certificatesOne = fileInfo.url;
							LogUtils.a("filepath",certificatesOne);
						} else if (index == 1) {
							Certificatestwo = fileInfo.url;
							LogUtils.a("filepath",Certificatestwo);
						} else if (index == 2) {
							certificatesThree = fileInfo.url;
							LogUtils.a("filepath",certificatesThree);
						}
						applyLive(index + 1);
					}

					@Override
					public void onError(int errorCode) {
						ToastUtil.makeText(context, "上传失败", Toast.LENGTH_SHORT).show();
						if (isValidContext(context) && dialogUpload.isShowing()) {
							dialogUpload.dismiss();
						}
					}
				});
			}else {
				applyLive(index + 1);
			}

		} else {
			saveSign();
		}
	}
}
