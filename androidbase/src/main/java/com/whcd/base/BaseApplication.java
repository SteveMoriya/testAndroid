package com.whcd.base;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;

import com.amap.api.location.AMapLocation;
import com.lzy.okgo.OkGo;
import com.whcd.base.interfaces.VolleyTool;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 自定义Application
 * 
 * @author Administrator
 */
public class BaseApplication extends Application {
	private static final String TAG = BaseApplication.class.getSimpleName();
	public static BaseApplication instance = null;
	private Map<String, String> params = new HashMap<String, String>();
	protected String appName = "com.wehang.ystv";
	public AMapLocation amapLocation = null;
	public static final String IP = "http://120.76.120.2";

	// "http://120.76.154.139/";

	@Override
	public void onCreate() {
		super.onCreate();
		initApplication();
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		System.gc();
	}

	/**
	 * App初始化
	 */
	private void initApplication() {
		instance = this;
		VolleyTool.init(getApplicationContext());
		OkGo.init(this);
		// 初始化异常处理器
//		CrashHandler crashHandler = CrashHandler.getInstance();
//		crashHandler.init(this);

		// 设置本地版本号
		try {
			PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			putAppParam("localVersion", packageInfo.versionCode);
			putAppParam("localVersionName", packageInfo.versionName);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		// 创建本地目录
		String projectPath = null;
		String sdCardState = Environment.getExternalStorageState();
		if (sdCardState.equals(Environment.MEDIA_MOUNTED)) {
			projectPath = Environment.getExternalStorageDirectory().getPath();
		} else {
			projectPath = getFilesDir().getPath();
		}
		projectPath += ("/" + appName);
		File file = new File(projectPath);
		if (!file.exists()) {
			file.mkdirs();
		}
		putAppParam("projectPath", projectPath);
	}

	public String getStringAppParam(String key) {
		return params.get(key);
	}

	public void putAppParam(String key, String value) {
		params.put(key, value);

	}

	public int getIntAppParam(String key) {
		return params.get(key) != null ? Integer.valueOf(params.get(key)) : -1;
	}

	public void putAppParam(String key, int value) {
		params.put(key, String.valueOf(value));
	}

	public boolean getBooleanAppParam(String key) {
		return params.get(key) != null ? Boolean.valueOf(params.get(key)) : false;
	}

	public void putAppParam(String key, boolean value) {
		params.put(key, String.valueOf(value));
	}
}
