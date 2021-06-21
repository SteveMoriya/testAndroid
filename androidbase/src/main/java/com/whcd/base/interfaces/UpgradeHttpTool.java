package com.whcd.base.interfaces;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import com.whcd.base.service.AppUpgradeService;

/**
 * 升级HTTP请求工具类
 * 
 * @author Administrator
 * 
 */
public class UpgradeHttpTool {
	public interface DownloadProgressListener {
		public void onProgress(long total, long download);
	}

	/**
	 * 版本升级接口
	 * 
	 * @param url
	 *            下载地址
	 * @param savePath
	 *            本地路径
	 * @param listener
	 *            下载监听
	 * @return
	 */
	public boolean upgrade(String url, String savePath, DownloadProgressListener listener) {
		boolean isSuccess = false;
		try {
			// ************************HTTPS代码 开始**************************//
			// HttpParams myParams = new BasicHttpParams();
			// HttpConnectionParams.setConnectionTimeout(myParams, 30000);
			// HttpConnectionParams.setSoTimeout(myParams, 30000);
			// HttpClient client = initHttpClient(myParams);
			// ************************HTTPS代码 结束**************************//

			// ************************HTTP代码 开始**************************//
			DefaultHttpClient client = new DefaultHttpClient();
			HttpParams myParams = client.getParams();
			HttpConnectionParams.setConnectionTimeout(myParams, 30000);
			HttpConnectionParams.setSoTimeout(myParams, 30000);
			// ************************HTTP代码 结束**************************//

			HttpGet get = new HttpGet(url);

			HttpResponse response = client.execute(get);
			long total = response.getEntity().getContentLength();
			InputStream in = response.getEntity().getContent();
			FileOutputStream out = new FileOutputStream(new File(savePath));

			if (response != null && in != null) {
				byte[] b = new byte[1024];
				int len = 0;
				long download = 0;
				while ((len = in.read(b)) != -1 && !AppUpgradeService.isDownloadCancel()) {
					out.write(b, 0, len);
					download += len;
					if (listener != null) {
						listener.onProgress(total, download);
					}
				}
				if (listener != null) {
					listener.onProgress(total, total);
				}
				isSuccess = true;
			}

			in.close();
			out.close();
			client.getConnectionManager().shutdown();
		} catch (Exception e) {
			e.printStackTrace();
			isSuccess = false;
		}
		return isSuccess;
	}
}
