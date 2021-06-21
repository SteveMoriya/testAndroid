package com.whcd.base.service;

import java.io.File;
import java.util.Random;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.Gravity;
import android.widget.RemoteViews;

import com.whcd.base.BaseApplication;
import com.whcd.base.R;
import com.whcd.base.interfaces.UpgradeHttpTool;
import com.whcd.base.utils.CommonUtils;
import com.whcd.base.utils.LogUtils;

/**
 * 
 * <p>
 * APP更新服务
 * </p>
 * 
 * @author 张家奇 2016年4月12日 下午6:35:11
 * @version V1.0
 * 
 * 
 * @modificationHistory=========================重大变更说明
 * @modify by user: 张家奇 2016年4月12日
 */
public class AppUpgradeService extends Service {
	private final String TAG = AppUpgradeService.class.getSimpleName();

	private Context context = null;
	private NotificationManager notificationManager = null;
	private Notification notification = null;
	private int notifyId;
	private long total, download, update;
	private String apkPath = null;
	private static boolean downloadCancel = false;
	private String downloadUrl = null;// 下载地址

	public static boolean isDownloadCancel() {
		return downloadCancel;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		context = this;

		notificationManager = (NotificationManager) context.getSystemService(Activity.NOTIFICATION_SERVICE);
		notifyId = createNotifyId();

		PendingIntent pendingintent = PendingIntent.getActivity(context, 0, new Intent(), PendingIntent.FLAG_CANCEL_CURRENT);
		notification = new Notification(R.drawable.ic_launcher, "开始下载", System.currentTimeMillis());
		notification.contentIntent = pendingintent;

		// 指定个性化视图
		final RemoteViews contentView = new RemoteViews(context.getPackageName(), R.layout.whcd_base_view_upgrade_notify);
		notification.contentView = contentView;

		IntentFilter filter = new IntentFilter();
		filter.addAction(CommonUtils.CANCEL_UPGRADE_ACTION);
		context.registerReceiver(cancelUpgradeButtonOnClickReceiver, filter);

		Intent intent = new Intent(CommonUtils.CANCEL_UPGRADE_ACTION);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
		contentView.setOnClickPendingIntent(R.id.cancel_download_btn, pendingIntent);
		downloadCancel = false;

		// 指定内容意图
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, new Intent(), PendingIntent.FLAG_UPDATE_CURRENT);
		notification.contentIntent = contentIntent;

		// 初始化管理器
		notificationManager.notify(notifyId, notification);
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		Bundle bundle = intent.getExtras();
		apkPath = ((BaseApplication) getApplication()).getStringAppParam("projectPath") + "/" + System.currentTimeMillis() + ".apk";// 下载APK存储地址
		downloadUrl = bundle.getString("downloadUrl");// 下载地址，调用服务开启下载时传递过来的参数
		LogUtils.debug(TAG, "onStart=========>downloadUrl:" + downloadUrl);
		new DownloadTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	@Override
	public void onDestroy() {
		unregisterReceiver(cancelUpgradeButtonOnClickReceiver);
		super.onDestroy();
	}

	/**
	 * APK异步下载任务
	 * 
	 * @author zhangjiaqi
	 * 
	 */
	final class DownloadTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			new UpgradeHttpTool().upgrade(downloadUrl, apkPath, new UpgradeHttpTool.DownloadProgressListener() {
				@Override
				public void onProgress(long total, long download) {
					LogUtils.debug(TAG, "DownloadTask=========>onProgress:download:" + download + ", total:" + total);
					AppUpgradeService.this.total = total;
					AppUpgradeService.this.download = download;

					if (AppUpgradeService.this.download * 100 / AppUpgradeService.this.total - update * 100 / AppUpgradeService.this.total >= 5) {
						update = download;
						handler.sendEmptyMessage(0);
					} else if (AppUpgradeService.this.download >= AppUpgradeService.this.total) {
						handler.sendEmptyMessage(1);
					}
				}
			});
			return null;
		}
	}

	/**
	 * 随机生成notifyId
	 * 
	 * @return
	 */
	private int createNotifyId() {
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < 8; i++) {
			sb.append(random.nextInt(9));
		}
		return Integer.parseInt(sb.toString());
	}

	/**
	 * 安装下载完成的apk
	 * 
	 * @param apkPath
	 */
	public void installNewVersion(String apkPath) {
		Uri uri = Uri.fromFile(new File(apkPath));
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setDataAndType(uri, "application/vnd.android.package-archive");
		startActivity(intent);
	}

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (!isDownloadCancel()) {
				switch (msg.what) {
				case 0:
					notification.contentView.setTextViewText(R.id.content_view_text, context.getString(R.string.app_name) + "正在下载...  "
							+ (update * 100 / total) + "%");
					notification.contentView.setProgressBar(R.id.content_view_progress, 100, (int) (update * 100 / total), false);
					notificationManager.notify(notifyId, notification);
					break;
				case 1:
					CommonUtils.showTipMsg(context, Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 120, "下载完成");
					notificationManager.cancel(notifyId);
					installNewVersion(apkPath);
					stopSelf();
					break;
				default:
					break;
				}
			}
		}
	};

	private BroadcastReceiver cancelUpgradeButtonOnClickReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(CommonUtils.CANCEL_UPGRADE_ACTION)) {
				LogUtils.verbose(TAG, "取消更新");
				downloadCancel = true;
				notificationManager.cancel(notifyId);
				stopSelf();
			}
		}
	};
}
