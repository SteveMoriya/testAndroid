package com.whcd.base.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.whcd.base.R;

/**
 * 
 * <p>
 * {手机常用工具}
 * </p>
 * 
 * @author 刘树宝 2016年4月6日 下午4:29:42
 * @version V1.0
 * 
 * 
 * @modificationHistory=========================创建
 * @modify by user: 刘树宝2016年4月6日
 */
public class CommonUtils {
	private static final String TAG = CommonUtils.class.getSimpleName();
	public static final String CLOSE_ACTION = "com.whcd.base.CLOSE_ACTION";
	public static final String FINISH_LOCATION_ACTION = "com.whcd.base.FINISH_LOCATION_ACTION";
	public static final String CANCEL_UPGRADE_ACTION = "com.whcd.base.CANCEL_UPGRADE_ACTION";

	public static Toast toast;

	/**
	 * 显示提示信息
	 * 
	 * @param context
	 *            上下文
	 * @param gravity
	 *            权重
	 * @param msg
	 *            提示内容
	 */
	public static void showTipMsg(Context context, int gravity, int x, int y, String msg) {
		if (context != null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View layout = inflater.inflate(R.layout.whcd_base_view_toast_layout, null);

			TextView text = (TextView) layout.findViewById(R.id.text);
			text.setText(msg);
			if (toast == null) {
				toast = new Toast(context);
				toast.setGravity(gravity, x, y);
				toast.setDuration(Toast.LENGTH_SHORT);
			}
			toast.setView(layout);
			toast.show();
		}
	}

	/**
	 * 显示提示信息
	 * 
	 * @param context
	 *            上下文
	 * @param gravity
	 *            权重
	 * @param msg
	 *            提示内容
	 */
	public static void showTipMsg(Context context, String msg) {
		if (context != null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View layout = inflater.inflate(R.layout.whcd_base_view_toast_layout, null);

			TextView text = (TextView) layout.findViewById(R.id.text);
			text.setText(msg);
			if (toast == null) {
				toast = new Toast(context);
			}
			toast.setDuration(Toast.LENGTH_SHORT);

			toast.setView(layout);
			toast.show();
		}
	}

	/**
	 * 检测Sdcard是否存在
	 * 
	 * @return true为存在，false为没有
	 */
	public static boolean isExitsSdcard() {
		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
			return true;
		else
			return false;
	}

	/**
	 * 当前网络是否可用
	 * 
	 * @return
	 */
	public static boolean isNetworkAvailable(Context context) {
		boolean isAvailable = false;
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connectivityManager.getActiveNetworkInfo();
		if (info != null && info.isAvailable()) {
			isAvailable = true;
			LogUtils.verbose(TAG, "当前使用网络名称：" + info.getTypeName());
		} else {
			LogUtils.verbose(TAG, "当前没有可用网络");
		}
		return isAvailable;
	}

	/**
	 * MD5加密
	 * 
	 * @param str
	 *            待加密字符串
	 * @return
	 */
	@SuppressLint("DefaultLocale")
	public static String md5(String str) {
		MessageDigest messageDigest = null;
		try {
			messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.reset();
			messageDigest.update(str.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		byte[] bytes = messageDigest.digest();
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < bytes.length; i++) {
			if (Integer.toHexString(0xFF & bytes[i]).length() == 1) {
				sb.append("0").append(Integer.toHexString(0xFF & bytes[i]));
			} else {
				sb.append(Integer.toHexString(0xFF & bytes[i]));
			}
		}
		// 32位加密，从第0位到31位
		return sb.substring(0, 32).toString().toLowerCase();
	}

	/**
	 * 计算listview高度
	 * 
	 * @param listView
	 */
	public static void setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}

		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);
	}

	public static Bitmap getCompressBitmapForScale(String path,int maxWidth,int maxHeight){
		Options options = new Options();
		options.inJustDecodeBounds = true;//只获取其宽高，不占内存
		Bitmap bitmap = BitmapFactory.decodeFile(path, options);

		int outHeight = options.outHeight;
		int outWidth = options.outWidth;
		int sampleSize = 1;
		if (outWidth*1.0f/maxWidth>outHeight*1.0f/maxHeight) {
			sampleSize=outWidth/maxWidth;
		}else {
			sampleSize=outHeight/maxHeight;
		}
		if(sampleSize < 1){
			sampleSize = 1;
		}
		options.inJustDecodeBounds = false;
		options.inSampleSize = sampleSize;
		bitmap=BitmapFactory.decodeFile(path, options);
		if(bitmap == null){
			return null;
		}
		return bitmap;
	}
}
