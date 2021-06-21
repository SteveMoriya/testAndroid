package com.whcd.base.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.whcd.base.R;
import com.whcd.base.fragment.ImageDetailFragment;
import com.whcd.base.utils.LogUtils;
import com.whcd.base.widget.CustomProgressDialog;
import com.whcd.base.widget.HackyViewPager;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 
 * <p>
 * 图片查看器
 * </p>
 * 
 * @author 张家奇 2016年4月13日 上午11:24:46
 * @version V1.0
 * 
 * 
 * @modificationHistory=========================重大变更说明
 * @modify by user: 张家奇 2016年4月13日
 */
public class ImagePagerActivity extends BaseActivity {
	private static final String STATE_POSITION = "STATE_POSITION";
	public static final String EXTRA_IMAGE_INDEX = "image_index";
	public static final String EXTRA_IMAGE_URLS = "image_urls";

	private int startPosition=0;
	private HackyViewPager viewPager;
	public int pagerPosition;
	private TextView indicatorTv;
	public TextView saveTv;
	public ImagePagerAdapter adapter;
	public ArrayList<String> urls;
	private int type=0;
	@Override
	protected int getLayoutResId() {
		return R.layout.whcd_base_activity_image_pager;
	}

	@Override
	protected void initTitleBar() {
	}

	@Override
	protected void initView() {
		viewPager = (HackyViewPager) findViewById(R.id.pager);
		indicatorTv = (TextView) findViewById(R.id.indicator);
		saveTv = (TextView) findViewById(R.id.saveTv);
		type=getIntent().getIntExtra("type",0);
		Log.i("caonima",type+"");

	}

	@Override
	protected void initData(Bundle bundle) {
		pagerPosition = getIntent().getIntExtra(EXTRA_IMAGE_INDEX, 0);
		urls = getIntent().getStringArrayListExtra(EXTRA_IMAGE_URLS);
		startPosition=getIntent().getIntExtra("startPosition",0);


		adapter = new ImagePagerAdapter(getSupportFragmentManager(), urls);
		viewPager.setAdapter(adapter);
		viewPager.setCurrentItem(startPosition);
		CharSequence indicatorText = getString(R.string.viewpager_indicator,startPosition+1, viewPager.getAdapter().getCount());
		indicatorTv.setText(indicatorText);
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageScrollStateChanged(int arg0) {
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageSelected(int arg0) {
				pagerPosition = arg0;
				CharSequence indicatorText = getString(R.string.viewpager_indicator, arg0 + 1, viewPager.getAdapter().getCount());
				indicatorTv.setText(indicatorText);
			}
		});

		if (bundle != null) {
			pagerPosition = bundle.getInt(STATE_POSITION);
		}
		viewPager.setCurrentItem(pagerPosition);
		saveTv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				savePic(urls.get(viewPager.getCurrentItem()));
			}
		});
	}
	////文件下载时，需要指定下载的文件目录
	public static String imgpath = Environment.getExternalStorageDirectory() + "/YsImgDown";
	private void savePic(String url){
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			String str = imgpath;
			File path1 = new File(str);
			if (!path1.exists()) {
				//若不存在，创建目录，可以在应用启动的时候创建
				path1.mkdirs();
			}
			String imgname = url.substring(url.lastIndexOf('/') + 1);
			//imgname = imgname.substring(0, imgname.indexOf("?"));


			if (type==0){
				final CustomProgressDialog dialog=CustomProgressDialog.show(context, "保存图片...", true, null);
				dialog.show();
				OkGo.get(url)//
						.tag(this)//
						.execute(new FileCallback(imgpath,imgname) {  //文件下载时，需要指定下载的文件目录和文件名
							@Override
							public void onSuccess(File file, Call call, Response response) {
								// file 即为文件数据，文件保存在指定目录
								dialog.dismiss();
								Toast.makeText(ImagePagerActivity.this,"成功保存到："+imgpath,Toast.LENGTH_LONG).show();
							}

							@Override
							public void downloadProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
								//这里回调下载进度(该回调在主线程,可以直接更新ui)
							}

							@Override
							public void onError(Call call, Response response, Exception e) {
								super.onError(call, response, e);
								dialog.dismiss();
								Toast.makeText(ImagePagerActivity.this,"保存失败",Toast.LENGTH_LONG).show();
							}
						});
			}else {
				File file=new File(url);
				copyFile(url,imgpath);

			}
		}else {
			Toast.makeText(context, "没有SD卡", Toast.LENGTH_LONG).show();

		}


	}

	public void copyFile(String oldPath, String newPath) {
		final CustomProgressDialog dialog=CustomProgressDialog.show(context, "保存图片...", true, null);
		dialog.show();
		try {
			oldPath=oldPath.substring(7);
			String imgname = oldPath.substring(oldPath.lastIndexOf('/') + 1);
			int bytesum = 0;
			int byteread = 0;
			File oldfile = new File(oldPath);
			if (oldfile.exists()) { //文件存在时
				InputStream inStream = new FileInputStream(oldPath); //读入原文件
				OutputStream fs = new FileOutputStream(newPath+imgname);
				byte[] buffer = new byte[1024];
				int length;
				while ( (byteread = inStream.read(buffer)) != -1) {
					bytesum += byteread; //字节数 文件大小
					fs.write(buffer, 0, byteread);
				}
				dialog.dismiss();
				Toast.makeText(ImagePagerActivity.this,"成功保存到："+imgpath,Toast.LENGTH_LONG).show();
				inStream.close();
				fs.close();
			}else {
				dialog.dismiss();
				Toast.makeText(ImagePagerActivity.this,"文件不存在"+imgpath,Toast.LENGTH_LONG).show();
			}
		}
		catch (Exception e) {
			dialog.dismiss();
			Toast.makeText(ImagePagerActivity.this,"复制单个文件操作出错",Toast.LENGTH_LONG).show();
			e.printStackTrace();

		}

	}
	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt(STATE_POSITION, viewPager.getCurrentItem());
	}

	public class ImagePagerAdapter extends FragmentStatePagerAdapter {
		public ArrayList<String> fileList;
		public ImageDetailFragment imageDetailFragment;
		public Map<Integer, ImageDetailFragment> list = new HashMap<Integer, ImageDetailFragment>();

		public ImagePagerAdapter(FragmentManager fm, ArrayList<String> fileList) {
			super(fm);
			this.fileList = fileList;
		}

		@Override
		public int getCount() {
			return fileList == null ? 0 : fileList.size();
		}

		@Override
		public ImageDetailFragment getItem(int position) {
			String url = fileList.get(position);
			imageDetailFragment = ImageDetailFragment.newInstance(url);
			list.put(position, imageDetailFragment);
			return imageDetailFragment;
		}
	}
}
