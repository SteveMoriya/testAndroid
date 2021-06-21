package com.whcd.base;

import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.Gravity;

import com.whcd.base.utils.CommonUtils;
import com.whcd.base.utils.LogUtils;

/**
 * UncaughtException处理类,当程序发生Uncaught异常的时候,有该类来接管程序,并记录发送错误报告.
 * 
 * @author user
 */
public class CrashHandler implements UncaughtExceptionHandler {
	public static final String TAG = CrashHandler.class.getSimpleName();
	// 程序的Context对象
	private BaseApplication application;
	// CrashHandler实例
	private static CrashHandler INSTANCE = new CrashHandler();
	// 系统默认的UncaughtException处理类
	private UncaughtExceptionHandler mDefaultHandler;
	// 用来存储设备信息和异常信息
	private Map<String, String> infos = new HashMap<String, String>();
	// 用于格式化日期,作为日志文件名的一部分
	private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

	/**
	 * 保证只有一个CrashHandler实例
	 */
	private CrashHandler() {
	}

	/**
	 * 获取CrashHandler实例 ,单例模式
	 */
	public static CrashHandler getInstance() {
		return INSTANCE;
	}

	/**
	 * 初始化
	 * 
	 * @param application
	 */
	public void init(BaseApplication application) {
		this.application = application;
		// 获取系统默认的UncaughtException处理器
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		// 设置该CrashHandler为程序的默认处理器
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	/**
	 * 当UncaughtException发生时会转入该函数来处理
	 */
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		if (!handleException(ex) && mDefaultHandler != null) {
			// 如果用户没有处理则让系统默认的异常处理器来处理
			mDefaultHandler.uncaughtException(thread, ex);
		} else {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				LogUtils.error(TAG, "error : " + e.getMessage());
			}
			// 退出程序
			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(1);
		}
	}

	/**
	 * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
	 * 
	 * @param e
	 * @return true:如果处理了该异常信息;否则返回false.
	 */
	private boolean handleException(Throwable e) {
		if (e == null) {
			return false;
		}

		// 收集设备参数信息
		collectDeviceInfo(application);

		// 保存日志文件
//		final String fileName = saveCrashInfo2File(e);

		// 使用Toast来显示异常信息
		new Thread() {
			@Override
			public void run() {
				Looper.prepare();
//				BaseHttpTool.doUpload(application.getApplicationContext(), "http://120.76.154.139/LRVideo/appservice/basic/uploadFile", fileName, null, false,
//						new TypeToken<BaseResult<BaseData>>() {
//						}.getType(), null);
				CommonUtils.showTipMsg(application, Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 120, "很抱歉,程序出现异常,即将退出.");
				Looper.loop();
			}
		}.start();
		return true;
	}

	/**
	 * 收集设备参数信息
	 * 
	 * @param ctx
	 */
	public void collectDeviceInfo(Context ctx) {
		try {
			// app版本信息
			PackageManager pm = ctx.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
			if (pi != null) {
				String versionCode = pi.versionCode + "";
				infos.put("APP版本号", versionCode);
				String versionName = pi.versionName == null ? "null" : pi.versionName;
				infos.put("APP版本名称", versionName);
			}

			infos.put("系统定制商", android.os.Build.BRAND);
			infos.put("手机制造商", android.os.Build.PRODUCT);
			infos.put("硬件制造商", android.os.Build.MANUFACTURER);
			infos.put("硬件名称", android.os.Build.FINGERPRINT);
			infos.put("设备参数", android.os.Build.DEVICE);
			infos.put("显示屏参数", android.os.Build.DISPLAY);
			infos.put("手机型号", android.os.Build.MODEL);
			infos.put("SDK版本", android.os.Build.VERSION.SDK);
			infos.put("系统版本", android.os.Build.VERSION.RELEASE);

			// 屏幕信息
			DisplayMetrics metric = new DisplayMetrics();
			metric = application.getResources().getDisplayMetrics();
			infos.put("屏幕宽度", String.valueOf(metric.widthPixels));
			infos.put("屏幕高度", String.valueOf(metric.heightPixels));
			infos.put("屏幕密度", String.valueOf(metric.density));
			infos.put("屏幕密度DPI", String.valueOf(metric.densityDpi));
		} catch (NameNotFoundException e) {
			LogUtils.error(TAG, "an error occured when collect package info : " + e.getMessage());
		}
	}

	/**
	 * 保存错误信息到文件中
	 * 
	 * @param ex
	 * @return 返回文件名称, 便于将文件传送到服务器
	 */
	private String saveCrashInfo2File(Throwable ex) {
		long timestamp = System.currentTimeMillis();
		String time = formatter.format(new Date());
		StringBuffer sb = new StringBuffer();
		sb.append(time + "-" + timestamp + "\n");
		for (Map.Entry<String, String> entry : infos.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			sb.append(key + "=" + value + "\n");
		}

		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		ex.printStackTrace(printWriter);
		Throwable cause = ex.getCause();
		while (cause != null) {
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}
		printWriter.close();
		String result = writer.toString();
		sb.append(result);
		try {
			String fileName = "crash-" + time + "-" + timestamp + ".log";

			String path = application.getStringAppParam("projectPath");
			FileOutputStream fos = new FileOutputStream(path + fileName);
			fos.write(sb.toString().getBytes("UTF8"));
			fos.close();
			return path + fileName;
		} catch (Exception e) {
			LogUtils.error(TAG, "an error occured while writing file : " + e.getMessage());
		}
		return null;
	}
}
