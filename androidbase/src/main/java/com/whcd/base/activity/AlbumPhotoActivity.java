package com.whcd.base.activity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.whcd.base.R;
import com.whcd.base.adapter.AlbumPhotoAdapter;
import com.whcd.base.bo.PictureItem;
import com.whcd.base.widget.CustomProgressDialog;
import com.whcd.base.widget.TopMenuBar;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * <p>
 * 相册照片列表
 * </p>
 * 
 * @author 张家奇 2016年4月13日 上午11:11:23
 * @version V1.0
 * 
 * 
 * @modificationHistory=========================重大变更说明
 * @modify by user: 张家奇 2016年4月13日
 */
public class AlbumPhotoActivity extends BaseActivity {
	private TopMenuBar titleBar = null;

	private GridView imageGridView = null;
	private AlbumPhotoAdapter photoAdapter = null;
	private List<PictureItem> pictureItemList = new ArrayList<PictureItem>();

	private String albumName = null;
	private int type = 0;
	private int maxSelectNumber = 6;
	private ArrayList<PictureItem> selectImageList = new ArrayList<PictureItem>();
	private RelativeLayout bottomToolBar = null;
	private Button previewViewButton, sureButton = null;
	private int cropWidth = 100, cropHeight = 100;

	@Override
	public int getLayoutResId() {
		return R.layout.whcd_base_activity_album_photo;
	}

	@Override
	protected void initTitleBar() {
		titleBar = (TopMenuBar) findViewById(R.id.top_title_bar);

		Button leftButton = titleBar.getLeftButton();
		leftButton.setOnClickListener(this);

		Button rightButton = titleBar.getRightButton();
		rightButton.setText("取消");
		rightButton.setOnClickListener(this);
	}

	@Override
	public void initView() {
		imageGridView = (GridView) findViewById(R.id.photo_grid_view);
		imageGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (type == 0) {
					PictureItem item = (PictureItem) imageGridView.getItemAtPosition(position);
					if (imageIsDamage(item.imagePath)) {
						Toast.makeText(context, "该图片已经损坏，请选择其他图片", Toast.LENGTH_SHORT).show();
						return;
					}
					Intent intent = new Intent(context, CropImageActivity.class);
					intent.putExtra("path", item.imagePath);
					intent.putExtra("cropWidth", cropWidth);
					intent.putExtra("cropHeight", cropHeight);
					startActivityForResult(intent, AlbumActivity.TO_CROP_PHOTO_REQUEST_CODE);
				} else {
					PictureItem item = (PictureItem) imageGridView.getItemAtPosition(position);
					if (imageIsDamage(item.imagePath)) {
						Toast.makeText(context, "该图片已经损坏，请选择其他图片", Toast.LENGTH_SHORT).show();
						return;
					}
					if (selectImageList.size() >= maxSelectNumber && !item.isChecked()) {
						Toast.makeText(context, "最多只能选择1张图片", Toast.LENGTH_SHORT).show();
						return;
					}

					item.setChecked(!item.isChecked());
					view.findViewById(R.id.select_view).setVisibility(item.isChecked ? View.VISIBLE : View.GONE);
					updateSelectPictureList(item);
				}
			}
		});

		bottomToolBar = (RelativeLayout) findViewById(R.id.bottom_tool_bar);
		previewViewButton = (Button) findViewById(R.id.preview_button);
		previewViewButton.setOnClickListener(this);

		sureButton = (Button) findViewById(R.id.send_button);
		sureButton.setText("确定");
		sureButton.setOnClickListener(this);
	}

	@Override
	public void initData(Bundle bundle) {
		super.TAG = AlbumPhotoActivity.class.getSimpleName();

		Intent intent = getIntent();
		if (intent != null && intent.hasExtra("ablumName")) {
			albumName = intent.getStringExtra("ablumName");
		}
		if (bundle != null && bundle.containsKey("albumName")) {
			albumName = bundle.getString("albumName");
		}
		if (getIntent().hasExtra("maxSelectNumber")) {
			maxSelectNumber = getIntent().getIntExtra("maxSelectNumber", 6);
		}
		if (bundle != null && bundle.containsKey("maxSelectNumber")) {
			maxSelectNumber = bundle.getInt("maxSelectNumber");
		}
		if (getIntent() != null && getIntent().hasExtra("type")) {
			type = getIntent().getIntExtra("type", 0);
		}
		if (bundle != null && bundle.containsKey("type")) {
			type = bundle.getInt("type");
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

		titleBar.setTitle(albumName);
		bottomToolBar.setVisibility(type == 0 ? View.GONE : View.VISIBLE);
		new LoadAlbumPhotoTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == AlbumActivity.TO_CROP_PHOTO_REQUEST_CODE) {
				setResult(resultCode, data);
				finish();
			}
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putString("albumName", albumName);
		outState.putInt("maxSelectNumber", maxSelectNumber);
		outState.putInt("type", type);
		outState.putInt("cropWidth", cropWidth);
		outState.putInt("cropHeight", cropHeight);
		super.onSaveInstanceState(outState);
	}

	public void updateSelectPictureList(PictureItem item) {
		if (item.isChecked()) {
			selectImageList.add(item);
		} else {
			selectImageList.remove(item);
		}

		previewViewButton.setEnabled(selectImageList.size() > 0);
		sureButton.setEnabled(selectImageList.size() > 0);
		sureButton.setText(selectImageList.size() > 0 ? "确定(" + selectImageList.size() + ")" : "确定");
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);

		if (v.getId() == R.id.title_btn_left) {
			setResult(RESULT_CANCELED);
			back();
		} else if (v.getId() == R.id.title_btn_right) {
			setResult(AlbumActivity.CANCEL_SELECT_PHOTO_RESULT_CODE);
			back();
		} else if (v.getId() == R.id.send_button) {
			Intent intent = new Intent();
			intent.putExtra("selectImageList", selectImageList);
			setResult(RESULT_OK, intent);
			finish();
		} else if (v.getId() == R.id.preview_button) {
			Intent intent = new Intent(context, ImagePagerActivity.class);
			ArrayList<String> urls = new ArrayList<String>();
			for (int i = 0; i < selectImageList.size(); i++) {
				urls.add("file://" + selectImageList.get(i).imagePath);
			}
			intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, urls);
			intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, 0);
			intent.putExtra("type",1);
			startActivity(intent);
		}
	}

	private class LoadAlbumPhotoTask extends AsyncTask<Void, Void, Boolean> {
		private CustomProgressDialog dialog = null;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (isValidContext(context)) {
				dialog = CustomProgressDialog.show(context, "请稍后...", true, null);
			}
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			boolean isThereAnError = false;
			if (isCancelled()) {
				AlbumPhotoActivity.this.runOnUiThread(new Runnable() {
					public void run() {
						if (dialog != null && isValidContext(context) && dialog.isShowing()) {
							dialog.dismiss();
						}
					}
				});
			}

			try {
				if (albumName != null) {
					String columns[] = new String[] { Media._ID, Media.DATA };
					Cursor cursor = MediaStore.Images.Media.query(getContentResolver(), MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns,
							Media.BUCKET_DISPLAY_NAME + "==" + "'" + albumName + "'", Media.DATE_MODIFIED + " desc");
					while (cursor.moveToNext()) {
						PictureItem item = new PictureItem(cursor.getInt(cursor.getColumnIndex(Media._ID)), cursor.getString(cursor.getColumnIndex(Media.DATA)));
						pictureItemList.add(item);
					}
					photoAdapter = new AlbumPhotoAdapter(AlbumPhotoActivity.this, pictureItemList, imageGridView);

					cursor.close();
					cursor = null;
				} else {
					isThereAnError = true;
				}
			} catch (Exception e) {
				isThereAnError = true;
			}
			return isThereAnError;
		}

		@Override
		protected void onPostExecute(Boolean isThereAnError) {
			if (dialog != null && isValidContext(context) && dialog.isShowing()) {
				dialog.dismiss();
			}
			if (!isThereAnError) {
				imageGridView.setAdapter(photoAdapter);
			}
			super.onPostExecute(isThereAnError);
		}
	}

	private boolean imageIsDamage(String filePath) {
		BitmapFactory.Options options = null;
		if (options == null)
			options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;

		BitmapFactory.decodeFile(filePath, options); // filePath代表图片路径
		if (options.mCancel || options.outWidth == -1 || options.outHeight == -1) {
			// 表示图片已损毁
			return true;
		} else {
			return false;
		}
	}

}