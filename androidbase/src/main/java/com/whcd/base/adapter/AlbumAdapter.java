package com.whcd.base.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.whcd.base.R;
import com.whcd.base.bo.PictureItem;
import com.whcd.base.utils.DisplayUtils;
import com.whcd.base.utils.ImageUtils;

public class AlbumAdapter extends BaseAdapter {
	private Activity context = null;
	private LayoutInflater inflater = null;
	private List<PictureItem> pictureItemList = null;

	private Bitmap defaultBmp = null;
	private ListView imageListView = null;
	/**
	 * 图片缓存技术的核心类，用于缓存所有下载好的图片，在程序内存达到设定值时会将最少最近使用的图片移除掉。
	 */
	private LruCache<String, Bitmap> mMemoryCache = null;

	public AlbumAdapter(Activity context, List<PictureItem> pictureItemList, ListView imageListView) {
		this.context = context;
		this.inflater = LayoutInflater.from(this.context);
		this.pictureItemList = pictureItemList;
		this.defaultBmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.default_cover);
		this.imageListView = imageListView;

		// 获取应用程序最大可用内存
		int maxMemory = (int) Runtime.getRuntime().maxMemory();
		int cacheSize = maxMemory / 8;
		// 设置图片缓存大小为程序最大可用内存的1/8
		this.mMemoryCache = new LruCache<String, Bitmap>(cacheSize);
	}

	@Override
	public int getCount() {
		return pictureItemList != null ? pictureItemList.size() : 0;
	}

	@Override
	public PictureItem getItem(int position) {
		return pictureItemList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return pictureItemList.get(position).getImageId();
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = inflater.inflate(R.layout.whcd_base_list_item_album, null);
			viewHolder.imageView = (ImageView) convertView.findViewById(R.id.image_view);
			viewHolder.folderView = (TextView) convertView.findViewById(R.id.folder_view);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		PictureItem item = (PictureItem) pictureItemList.get(position);
		viewHolder.folderView.setText(item.getFolderLabel() + "(" + item.count + ")");

		viewHolder.imageView.setTag(item.imagePath);
		setImageView(item.imagePath, viewHolder.imageView);
		return convertView;
	}

	/**
	 * 给ImageView设置图片。首先从LruCache中取出图片的缓存，设置到ImageView上。如果LruCache中没有该图片的缓存， 就给ImageView设置一张默认图片。
	 * 
	 * @param imageUrl
	 *            图片的URL地址，用于作为LruCache的键。
	 * @param imageView
	 *            用于显示图片的控件。
	 */
	private void setImageView(String imageUrl, final ImageView imageView) {
		Bitmap bitmap = getBitmapFromMemoryCache(imageUrl);
		if (bitmap != null) {
			imageView.setImageBitmap(bitmap);
		} else {
			new BitmapWorkerTask().execute(imageUrl);
		}
	}

	/**
	 * 将一张图片存储到LruCache中。
	 * 
	 * @param key
	 *            LruCache的键，这里传入图片的URL地址。
	 * @param bitmap
	 *            LruCache的键，这里传入从网络上下载的Bitmap对象。
	 */
	public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
		if (getBitmapFromMemoryCache(key) == null) {
			mMemoryCache.put(key, bitmap);
		}
	}

	/**
	 * 从LruCache中获取一张图片，如果不存在就返回null。
	 * 
	 * @param key
	 *            LruCache的键，这里传入图片的URL地址。
	 * @return 对应传入键的Bitmap对象，或者null。
	 */
	public Bitmap getBitmapFromMemoryCache(String key) {
		return mMemoryCache.get(key);
	}

	/**
	 * 异步下载图片的任务。
	 * 
	 * @author guolin
	 */
	class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
		/**
		 * 图片的URL地址
		 */
		private String imageUrl;

		@Override
		protected Bitmap doInBackground(String... params) {
			imageUrl = params[0];
			Bitmap bitmap = ImageUtils.createImageThumbnail(imageUrl, DisplayUtils.dip2px(context, 100), DisplayUtils.dip2px(context, 100));
			if (bitmap != null) {
				// 图片下载完成后缓存到LrcCache中
				addBitmapToMemoryCache(params[0], bitmap);
			}
			return bitmap;
		}

		@Override
		protected void onPostExecute(Bitmap bitmap) {
			super.onPostExecute(bitmap);
			// 根据Tag找到相应的ImageView控件，将下载好的图片显示出来。
			ImageView imageView = (ImageView) imageListView.findViewWithTag(imageUrl);
			if (imageView != null && bitmap != null) {
				imageView.setImageBitmap(bitmap);
			} else if (imageView != null) {
				imageView.setImageBitmap(defaultBmp);
			}
		}
	}

	final static class ViewHolder {
		ImageView imageView;
		TextView folderView;
	}
}
