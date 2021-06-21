package com.whcd.base.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.whcd.base.R;
import com.whcd.base.component.photoview.PhotoViewAttacher;
import com.whcd.base.utils.CommonUtils;

/**
 * 
 * <p>
 * 单张图片预览
 * </p>
 * 
 * @author 张家奇 2016年4月13日 上午11:57:25
 * @version V1.0
 * 
 * 
 * @modificationHistory=========================重大变更说明
 * @modify by user: 张家奇 2016年4月13日
 */
public class ImageDetailFragment extends BaseFragment {
	private String imageUrl;
	private ImageView imageView;
	private ProgressBar progressBar;
	private PhotoViewAttacher attacher;
	private Bitmap bitmap;

	public static ImageDetailFragment newInstance(String imageUrl) {
		final ImageDetailFragment f = new ImageDetailFragment();
		final Bundle args = new Bundle();
		args.putString("url", imageUrl);
		f.setArguments(args);
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		imageUrl = getArguments() != null ? getArguments().getString("url") : null;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View v = inflater.inflate(R.layout.whcd_base_fragment_image_detail, container, false);
		imageView = (ImageView) v.findViewById(R.id.image);
		attacher = new PhotoViewAttacher(imageView);
		attacher.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
			@Override
			public void onViewTap(View view, float x, float y) {
				getActivity().finish();
			}
		});
		progressBar = (ProgressBar) v.findViewById(R.id.loading);
		return v;
	}

	public Bitmap getBitmap() {
		return this.bitmap;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		ImageLoader.getInstance().displayImage(imageUrl, imageView, new SimpleImageLoadingListener() {
			@Override
			public void onLoadingStarted(String imageUri, View view) {
				progressBar.setVisibility(View.VISIBLE);
			}

			@Override
			public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
				String message = null;
				switch (failReason.getType()) {
				case IO_ERROR:
					message = "下载错误";
					break;
				case DECODING_ERROR:
					message = "图片无法显示";
					CommonUtils.showTipMsg(getActivity(), Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 120, message);
					break;
				case NETWORK_DENIED:
					message = "网络有问题，无法下载";
					CommonUtils.showTipMsg(getActivity(), Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 120, message);
					break;
				case OUT_OF_MEMORY:
					message = "图片太大无法显示";
					CommonUtils.showTipMsg(getActivity(), Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 120, message);
					break;
				case UNKNOWN:
					message = "未知的错误";
					break;
				}

				progressBar.setVisibility(View.GONE);
				attacher.update();

			}

			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				bitmap = loadedImage;
				progressBar.setVisibility(View.GONE);
				attacher.update();
			}
		});
	}
}