package com.whcd.base.utils;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;

import com.whcd.base.BaseApplication;
import com.whcd.base.activity.AlbumActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImageUtils {
	private static final String TAG = ImageUtils.class.getSimpleName();

	/**
	 * 拍照
	 * 
	 * @author 张家奇 2016年4月12日 下午7:02:17
	 * @param context
	 * @return
	 * 
	 * @modificationHistory=========================新增
	 * @modify by user: 张家奇 2016年4月12日
	 * @modify by reason: 原因
	 */
	public static String takePhoto(Activity context) {
		String capturePath = ((BaseApplication) context.getApplication()).getStringAppParam("projectPath") + "/"
				+ System.currentTimeMillis() + ".jpg";
		LogUtils.debug(TAG, "capturePath===========>" + capturePath);
		File mTmpFile = null;
		int currentApiVersion = android.os.Build.VERSION.SDK_INT;
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		if (intent.resolveActivity(context.getPackageManager()) != null) {
			try {
				mTmpFile = new File(capturePath);
				if (!mTmpFile.exists()) {
					if (!mTmpFile.getParentFile().exists()) {
						mTmpFile.getParentFile().mkdirs();
					}
					mTmpFile.createNewFile();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (mTmpFile != null && mTmpFile.exists()) {
				if (currentApiVersion < 24) {
					intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mTmpFile));
					intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
					context.startActivityForResult(intent, AlbumActivity.TAKE_PHOTO_REQUEST_CODE);
				} else {
					ContentValues contentValues = new ContentValues(1);
					contentValues.put(MediaStore.Images.Media.DATA, mTmpFile.getAbsolutePath());
					Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
					intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
					context.startActivityForResult(intent, AlbumActivity.TAKE_PHOTO_REQUEST_CODE);
				}
			} else {
				Toast.makeText(context, "图片信息存储错误", Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(context, "该手机没有相机模块", Toast.LENGTH_SHORT).show();
		}
		return capturePath;
	}

	public static void writeImage(Bitmap bitmap, String destPath, int quality) {
		try {
			FileUtils.deleteFile(destPath);
			if (FileUtils.createFile(destPath)) {
				FileOutputStream out = new FileOutputStream(destPath);
				if (bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)) {
					out.flush();
					out.close();
					out = null;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 从路径中读取图片
	 * 
	 * @param path
	 * @return
	 */
	public static Bitmap decodeUriAsBitmap(String path) {
		Bitmap bitmap = null;
		BitmapFactory.Options opts = new BitmapFactory.Options();
		File file = new File(path);
		if (file != null && file.exists()) {
			if (file.length() < 20480) { // 0-20k
				opts.inSampleSize = 1;
			} else if (file.length() < 51200) { // 20-50k
				opts.inSampleSize = 1;
			} else if (file.length() < 307200) { // 50-300k
				opts.inSampleSize = 1;
			} else if (file.length() < 819200) { // 300-800k
				opts.inSampleSize = 2;
			} else if (file.length() < 1048576) { // 800-1024k
				opts.inSampleSize = 3;
			} else {
				opts.inSampleSize = 4;
			}
			bitmap = BitmapFactory.decodeFile(path, opts);

			int angle = getExifOrientation(path);
			if (angle != 0) { // 如果照片出现了 旋转 那么 就更改旋转度数
				Matrix matrix = new Matrix();
				matrix.postRotate(angle);
				bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
			}
		}
		return bitmap;
	}

	/**
	 * 得到 图片旋转 的角度
	 * 
	 * @param filePath
	 * @return
	 */
	public static int getExifOrientation(String filePath) {
		int degree = 0;
		ExifInterface exif = null;
		try {
			exif = new ExifInterface(filePath);
		} catch (IOException ex) {
			LogUtils.error(TAG, "cannot read exif", ex);
		}

		if (exif != null) {
			int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
			if (orientation != -1) {
				switch (orientation) {
				case ExifInterface.ORIENTATION_ROTATE_90:
					degree = 90;
					break;
				case ExifInterface.ORIENTATION_ROTATE_180:
					degree = 180;
					break;
				case ExifInterface.ORIENTATION_ROTATE_270:
					degree = 270;
					break;
				}
			}
		}
		return degree;
	}

	/**
	 * 根据指定的图像路径和大小来获取缩略图 此方法有两点好处： 1. 使用较小的内存空间，第一次获取的bitmap实际上为null，只是为了读取宽度和高度， 第二次读取的bitmap是根据比例压缩过的图像，第三次读取的bitmap是所要的缩略图。 2.
	 * 缩略图对于原图像来讲没有拉伸，这里使用了2.2版本的新工具ThumbnailUtils，使 用这个工具生成的图像不会被拉伸。
	 * 
	 * @param imagePath
	 *            图像的路径
	 * @param width
	 *            指定输出图像的宽度
	 * @param height
	 *            指定输出图像的高度
	 * @return 生成的缩略图
	 */
	public static Bitmap createImageThumbnail(String imagePath, int width, int height) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);

		options.inJustDecodeBounds = false; // 设为 false
		// 计算缩放比
		int h = options.outHeight;
		int w = options.outWidth;
		int beWidth = w / width;
		int beHeight = h / height;
		int be = 1;
		if (beWidth < beHeight) {
			be = beWidth;
		} else {
			be = beHeight;
		}
		if (be <= 0) {
			be = 1;
		}

		options.inSampleSize = be;
		// 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false
		bitmap = BitmapFactory.decodeFile(imagePath, options);

		int angle = getExifOrientation(imagePath);
		if (angle != 0) { // 如果照片出现了 旋转 那么 就更改旋转度数
			Matrix matrix = new Matrix();
			matrix.postRotate(angle);
			bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		}
		// 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象
		bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		return bitmap;
	}

	/**
	 * 根据图片名称获取图片的资源id
	 * 
	 * @param imageName
	 * @return
	 */
	public static int getResource(Context context, String imageName) {
		int resId = context.getResources().getIdentifier(imageName, "drawable", context.getPackageName());
		return resId;
	}

	/**
	 * 复制单个文件
	 * 
	 * @param oldPath
	 * @param newPath
	 */
	public static void copyFile(String oldPath, String newPath) {
		try {
			int read = 0;
			File oldfile = new File(oldPath);
			if (oldfile.exists()) {
				InputStream is = new FileInputStream(oldPath);
				FileOutputStream fos = new FileOutputStream(newPath);
				byte[] buffer = new byte[1024];
				while ((read = is.read(buffer)) != -1) {
					fos.write(buffer, 0, read);
				}
				fos.close();
				is.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 图片尺寸压缩
	 * 
	 * @param srcPath
	 * @return
	 */
	private static Bitmap compressSize(String srcPath) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();

		// 开始读入图片，此时把options.inJustDecodeBounds设回true了
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bitmap为空

		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;

		// 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
		float hh = 800f;// 这里设置高度为800f
		float ww = 480f;// 这里设置宽度为480f

		// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = 1;// be=1表示不缩放
		if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
			be = (int) (w / ww);
		} else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
			be = (int) (h / hh);
		}

		if (be <= 0)
			be = 1;

		newOpts.inSampleSize = be;// 设置缩放比例
		// 重新读入图片，注意此时已经把options.inJustDecodeBounds设回false了
		bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
		return bitmap;// 压缩好比例大小后再进行质量压缩
	}

	/**
	 * 图片压缩，先进行尺寸压缩，再压缩质量
	 * 
	 * @param srcPath
	 */
	public static void compressImage(String srcPath) {
		// 尺寸压缩
		Bitmap image = compressSize(srcPath);

		// 如果照片出现了 旋转 那么 就更改旋转度数
		int angle = getExifOrientation(srcPath);
		if (angle != 0) {
			Matrix matrix = new Matrix();
			matrix.postRotate(angle);
			image = Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), matrix, true);
		}

		// 质量压缩
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		while (baos.toByteArray().length / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
			baos.reset();// 重置baos即清空baos
			options -= 10;// 每次都减少10
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
		}

		try {
			File srcFile = new File(srcPath);
			FileOutputStream fos = new FileOutputStream(srcFile);
			fos.write(baos.toByteArray());
			fos.flush();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
