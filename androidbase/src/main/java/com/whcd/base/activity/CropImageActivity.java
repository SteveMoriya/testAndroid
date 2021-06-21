package com.whcd.base.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.whcd.base.BaseApplication;
import com.whcd.base.R;
import com.whcd.base.utils.CommonUtils;
import com.whcd.base.utils.DisplayUtils;
import com.whcd.base.utils.ImageUtils;
import com.whcd.base.widget.CropImageView;
import com.whcd.base.widget.TopMenuBar;

/**
 * 
 * <p>
 * 头像移动和缩放
 * </p>
 * 
 * @author 张家奇 2016年4月13日 上午11:57:10
 * @version V1.0
 * 
 * 
 * @modificationHistory=========================重大变更说明
 * @modify by user: 张家奇 2016年4月13日
 */
public class CropImageActivity extends BaseActivity {
	// 顶部工具栏
	private TopMenuBar topMenuBar = null;

	// 待裁剪头像
	private CropImageView cropImageView = null;

	private String path = null;

	private int type;

	private int cropWidth = 100, cropHeight = 100;

	private Bitmap bitmap;

	@Override
	protected int getLayoutResId() {
		return R.layout.whcd_base_activity_crop_image;
	}

	@Override
	protected void initTitleBar() {
		topMenuBar = (TopMenuBar) findViewById(R.id.top_title_bar);
		topMenuBar.setTitle("移动和缩放");

		Button titleLeftButton = topMenuBar.getLeftButton();
		titleLeftButton.setOnClickListener(this);

		topMenuBar.getRightButton().setText("保存");
		topMenuBar.getRightButton().setOnClickListener(this);
	}

	@Override
	protected void initView() {
		cropImageView = (CropImageView) findViewById(R.id.crop_img_view);
	}

	@Override
	protected void initData(Bundle bundle) {
		super.TAG = CropImageActivity.class.getSimpleName();

		String path = getIntent().getStringExtra("path");
		int type = type = getIntent().getIntExtra("type", 0);
		if (bundle != null && bundle.containsKey("path")) {
			path = bundle.getString("path");
		}
		if (bundle != null && bundle.containsKey("type")) {
			type = getIntent().getIntExtra("type", 0);
		}
		if (getIntent().hasExtra("cropWidth")) {
			cropWidth = getIntent().getIntExtra("cropWidth", 100);
		}
		if (bundle != null && bundle.containsKey("cropWidth")) {
			cropWidth = bundle.getInt("cropWidth");
		}

		if (getIntent().hasExtra("cropHeight")) {
			cropHeight = getIntent().getIntExtra("cropHeight", 100);
		}
		if (bundle != null && bundle.containsKey("cropHeight")) {
			cropHeight = bundle.getInt("cropHeight");
		}
		if (type == 1) {
			if (TextUtils.isEmpty(path)) {
				return;
			}
			bitmap = CommonUtils.getCompressBitmapForScale(path, 100, 100);
			if (bitmap != null) {
				String newPath = getExternalCacheDir().getAbsolutePath() + File.separator + "photos" + File.separator + new File(path).getName();// 不重要的文件都放在这里
				File newFile = new File(newPath);
				if (!newFile.getParentFile().exists()) {
					newFile.getParentFile().mkdirs();
				}
				FileOutputStream fos = null;
				try {
					fos = new FileOutputStream(newFile);
					bitmap.compress(CompressFormat.PNG, 100, fos);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} finally {
					if (fos != null) {
						try {
							fos.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		} else {
			bitmap = ImageUtils.decodeUriAsBitmap(path);
		}
		cropImageView.setDrawable(new BitmapDrawable(bitmap), DisplayUtils.dip2px(context, cropWidth), DisplayUtils.dip2px(context, cropHeight));
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putString("path", path);
		outState.putInt("type", type);
		outState.putInt("cropWidth", cropWidth);
		outState.putInt("cropHeight", cropHeight);
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);

		if (v.getId() == R.id.title_btn_left) {
			back();
		} else if (v.getId() == R.id.title_btn_right) {
			String cropImagePath = ((BaseApplication) getApplication()).getStringAppParam("projectPath") + "/" + System.currentTimeMillis() + ".jpg";
			ImageUtils.writeImage(cropImageView.getCropImage(), cropImagePath, 100);
			Intent intent = new Intent();
			intent.putExtra("cropImagePath", cropImagePath);
			setResult(RESULT_OK, intent);
			finish();
		}
	}
}