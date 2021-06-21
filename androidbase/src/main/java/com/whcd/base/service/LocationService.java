package com.whcd.base.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationListener;
import com.google.gson.Gson;
import com.tencent.mm.sdk.modelmsg.ShowMessageFromWX;
import com.whcd.base.BaseApplication;
import com.whcd.base.utils.CommonUtils;
import com.whcd.base.utils.LogUtils;

/**
 * 
 * <p>
 * 高德定位服务
 * </p>
 * 
 * @author 张家奇 2016年4月12日 下午5:37:50
 * @version V1.0
 * 
 * 
 * @modificationHistory=========================新增
 * @modify by user: 张家奇 2016年4月12日
 */
public class LocationService extends Service implements AMapLocationListener {
	private final String TAG = LocationService.class.getSimpleName();
	private Context context = null;

	// 声明AMapLocationClient类对象
	public AMapLocationClient mLocationClient = null;
	// 声明mLocationOption对象
	public AMapLocationClientOption mLocationOption = null;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		context = this;
		startLocation();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
	}

	/**
	 * 开启定位
	 */
	private void startLocation() {
		// 初始化定位
		mLocationClient = new AMapLocationClient(getApplicationContext());
		// 设置定位回调监听
		mLocationClient.setLocationListener(this);
		// 初始化定位参数
		mLocationOption = new AMapLocationClientOption();
		// 设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
		mLocationOption.setLocationMode(AMapLocationMode.Hight_Accuracy);
		// 设置是否返回地址信息（默认返回地址信息）
		mLocationOption.setNeedAddress(true);
		// 设置是否只定位一次,默认为false
		mLocationOption.setOnceLocation(true);
		// 设置是否强制刷新WIFI，默认为强制刷新
		mLocationOption.setWifiActiveScan(true);
		// 设置是否允许模拟位置,默认为false，不允许模拟位置
		mLocationOption.setMockEnable(false);
		// 设置定位间隔,单位毫秒,默认为2000ms
		mLocationOption.setInterval(2000);
		// 给定位客户端对象设置定位参数
		mLocationClient.setLocationOption(mLocationOption);
		// 启动定位
		mLocationClient.startLocation();
	}

	@Override
	public void onLocationChanged(AMapLocation amapLocation) {
		if (amapLocation != null) {
			if (amapLocation.getErrorCode() == 0) {
				BaseApplication.instance.amapLocation = amapLocation;
				Message msg = handler.obtainMessage();
				msg.what = 1;
				handler.sendMessage(msg);
				return;
			}
			Message msg = handler.obtainMessage();
			msg.what = 0;
			handler.sendMessage(msg);
		}
	}

	@Override
	public void onDestroy() {
		if (mLocationClient != null) {
			mLocationClient.onDestroy();// 销毁定位客户端。
		}
		super.onDestroy();
	}

	private Handler handler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			Intent intent = new Intent();
			intent.putExtra("success", msg.what == 1 ? true : false);
			intent.setAction(CommonUtils.FINISH_LOCATION_ACTION);
			sendBroadcast(intent);
			LogUtils.verbose(TAG, msg.what == 1 ? "定位成功" : "定位失败");
			stopSelf();
			return false;
		}
	});
}