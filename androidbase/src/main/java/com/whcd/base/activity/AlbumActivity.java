package com.whcd.base.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.whcd.base.R;
import com.whcd.base.adapter.AlbumAdapter;
import com.whcd.base.bo.PictureItem;
import com.whcd.base.widget.CustomProgressDialog;
import com.whcd.base.widget.TopMenuBar;

/**
 * 
 * <p>
 * 相册列表
 * </p>
 * 
 * @author 张家奇 2016年4月13日 上午11:00:16
 * @version V1.0
 * 
 * 
 * @modificationHistory=========================重大变更说明
 * @modify by userTO_CROP_PHOTO_REQUEST_CODE 张家奇 2016年4月13日
 */
public class AlbumActivity extends BaseActivity {
	public static final int TO_ALBUM_REQUEST_CODE = 10001;
	public static final int TO_SELECT_PHOTO_REQUEST_CODE = 10002;
	public static final int TO_CROP_PHOTO_REQUEST_CODE = 10003;
	public static final int TAKE_PHOTO_REQUEST_CODE = 10004;
	public static final int CANCEL_SELECT_PHOTO_RESULT_CODE = 20001;

	private int type = 0;
	private int maxSelectNumber = 6;
	private ListView albumListView = null;
	private AlbumAdapter albumListAdapter = null;
	private List<PictureItem> pictureItemList = new ArrayList<PictureItem>();
	private int cropWidth = 100, cropHeight = 100;

	@Override
	public int getLayoutResId() {
		return R.layout.whcd_base_activity_album;
	}

	@Override
	protected void initTitleBar() {
		TopMenuBar titleBar = (TopMenuBar) findViewById(R.id.top_title_bar);
		titleBar.setTitle("选择相册");

		Button titleLeftButton = titleBar.getLeftButton();
		titleLeftButton.setOnClickListener(this);

		titleBar.hideRightButton();
	}

	@Override
	public void initView() {
		albumListView = (ListView) findViewById(R.id.album_list_view);
		albumListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				PictureItem item = (PictureItem) albumListView.getItemAtPosition(position);
				Intent intent = new Intent(context, AlbumPhotoActivity.class);
				intent.putExtra("ablumName", item.getFolderLabel());
				intent.putExtra("maxSelectNumber", maxSelectNumber);
				intent.putExtra("type", type);
				intent.putExtra("cropWidth", cropWidth);
				intent.putExtra("cropHeight", cropHeight);
				startActivityForResult(intent, TO_SELECT_PHOTO_REQUEST_CODE);
			}
		});
	}

	@Override
	public void initData(Bundle savedInstanceState) {
		super.TAG = AlbumActivity.class.getSimpleName();

		if (getIntent().hasExtra("maxSelectNumber")) {
			maxSelectNumber = getIntent().getIntExtra("maxSelectNumber", 6);
		}
		if (savedInstanceState != null && savedInstanceState.containsKey("maxSelectNumber")) {
			maxSelectNumber = savedInstanceState.getInt("maxSelectNumber");
		}
		if (getIntent() != null && getIntent().hasExtra("type")) {
			type = getIntent().getIntExtra("type", 0);
		}
		if (savedInstanceState != null && savedInstanceState.containsKey("type")) {
			type = savedInstanceState.getInt("type");
		}
		if (getIntent().hasExtra("cropWidth")) {
			cropWidth = getIntent().getIntExtra("cropWidth", 100);
		}
		if (savedInstanceState != null && savedInstanceState.containsKey("cropWidth")) {
			cropWidth = savedInstanceState.getInt("cropWidth");
		}
		if (getIntent().hasExtra("cropHeight")) {
			cropHeight = getIntent().getIntExtra("cropHeight", 100);
		}
		if (savedInstanceState != null && savedInstanceState.containsKey("cropHeight")) {
			cropHeight = savedInstanceState.getInt("cropHeight");
		}

		new LoadAlbumListTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putInt("maxSelectNumber", maxSelectNumber);
		outState.putInt("type", type);
		outState.putInt("cropWidth", cropWidth);
		outState.putInt("cropHeight", cropHeight);
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == TO_SELECT_PHOTO_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				Log.i("++++","jiequ");
				setResult(RESULT_OK, data);
				back();
			} else if (resultCode == CANCEL_SELECT_PHOTO_RESULT_CODE) {
				back();
			}
		}
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);

		if (v.getId() == R.id.title_btn_left) {
			setResult(RESULT_CANCELED);
			back();
		}
	}

	private class LoadAlbumListTask extends AsyncTask<Void, Void, Boolean> {
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
				AlbumActivity.this.runOnUiThread(new Runnable() {
					public void run() {
						if (dialog != null && isValidContext(context) && dialog.isShowing()) {
							dialog.dismiss();
						}
					}
				});
			}

			try {
				String projection[] = new String[] { Media._ID, Media.DATA, Media.BUCKET_DISPLAY_NAME };
				Cursor cursor = MediaStore.Images.Media.query(getContentResolver(), MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null,
						Media.BUCKET_DISPLAY_NAME + "," + Media.DATE_MODIFIED + " desc");

				PictureItem item = null;
				while (cursor.moveToNext()) {
					String albumName = cursor.getString(cursor.getColumnIndex(Media.BUCKET_DISPLAY_NAME));
					if (!TextUtils.isEmpty(albumName) && !albumName.equals("butler")) {
						if (item == null) {
							item = new PictureItem();
							item.folderLabel = albumName;
							item.imageId = cursor.getInt(cursor.getColumnIndex(Media._ID));
							item.imagePath = cursor.getString(cursor.getColumnIndex(Media.DATA));
							item.count = 1;
						} else if (item.folderLabel.equals(albumName)) {
							item.count += 1;
						} else {
							pictureItemList.add(item);
							item = new PictureItem();
							item.folderLabel = albumName;
							item.imageId = cursor.getInt(cursor.getColumnIndex(Media._ID));
							item.imagePath = cursor.getString(cursor.getColumnIndex(Media.DATA));
							item.count = 1;
						}
					}
				}
				if (item != null) {
					pictureItemList.add(item);
				}

				albumListAdapter = new AlbumAdapter(AlbumActivity.this, pictureItemList, albumListView);
				cursor.close();
				cursor = null;
			} catch (Exception e) {
				e.printStackTrace();
				isThereAnError = true;
			}
			return isThereAnError;
		}

		@Override
		protected void onPostExecute(Boolean isThereAnError) {
			super.onPostExecute(isThereAnError);
			if (dialog != null && isValidContext(context) && dialog.isShowing()) {
				dialog.dismiss();
			}
			if (!isThereAnError) {
				albumListView.setAdapter(albumListAdapter);
			}
		}
	}
}