package com.whcd.base.utils;



import android.util.Log;

/**
 * 日志工具类
 *
 * @author Administrator
 */
public class LogUtils {
	private static boolean isEnable = true;
	private static String TAG = LogUtils.class.getSimpleName();

	public static void verbose(String tag, String msg) {
		if (isEnable) {
			Log.v(tag, msg);
		}
	}

	public static void verbose(String msg) {
		if (isEnable) {
			Log.v(TAG, msg);
		}
	}

	public static void debug(String tag, String msg) {
		if (isEnable) {
			Log.d(tag, msg);
		}
	}

	public static void debug(String msg) {
		if (isEnable) {
			Log.d(TAG, msg);
		}
	}

	public static void info(String tag, String msg) {
		if (isEnable) {
			Log.i(tag, msg);
		}
	}

	public static void info(String msg) {
		if (isEnable) {
			Log.i(TAG, msg);
		}
	}

	public static void error(String tag, String msg, Exception e) {
		if (isEnable) {
			Log.e(tag, msg, e);
		}
	}

	public static void error(String tag, String msg) {
		if (isEnable) {
			Log.e(tag, msg);
		}
	}

	public static void error(String msg) {
		if (isEnable) {
			Log.e(TAG, msg);
		}
	}
}
