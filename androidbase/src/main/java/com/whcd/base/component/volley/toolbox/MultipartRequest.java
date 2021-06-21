package com.whcd.base.component.volley.toolbox;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

import com.whcd.base.component.volley.AuthFailureError;
import com.whcd.base.component.volley.NetworkResponse;
import com.whcd.base.component.volley.Request;
import com.whcd.base.component.volley.Response;
import com.whcd.base.component.volley.VolleyLog;

public class MultipartRequest extends Request<String> {
	private MultipartEntity entity = new MultipartEntity();
	private final Response.Listener<String> mListener;

	private List<File> mFiles;
	private String mFileName;
	private Map<String, String> mParams;

	/**
	 * 单个文件
	 * 
	 * @param url
	 * @param errorListener
	 * @param listener
	 * @param fileName
	 * @param file
	 * @param params
	 */
	public MultipartRequest(String url, Response.ErrorListener errorListener, Response.Listener<String> listener, String fileName, File file,
			Map<String, String> params) {
		super(Method.POST, url, errorListener);

		mFiles = new ArrayList<File>();
		if (file != null) {
			mFiles.add(file);
		}
		mFileName = fileName;
		mListener = listener;
		mParams = params;
		buildMultipartEntity();
	}

	/**
	 * 多个文件，对应一个key
	 * 
	 * @param url
	 * @param errorListener
	 * @param listener
	 * @param fileName
	 * @param files
	 * @param params
	 */
	public MultipartRequest(String url, Response.ErrorListener errorListener, Response.Listener<String> listener, String fileName, List<File> files,
			Map<String, String> params) {
		super(Method.POST, url, errorListener);
		mFileName = fileName;
		mListener = listener;
		mFiles = files;
		mParams = params;
		buildMultipartEntity();
	}

	private void buildMultipartEntity() {
		if (mFiles != null && mFiles.size() > 0) {
			for (File file : mFiles) {
				entity.addPart(mFileName, new FileBody(file));
			}
		}

		try {
			if (mParams != null && mParams.size() > 0) {
				for (Map.Entry<String, String> entry : mParams.entrySet()) {
					entity.addPart(entry.getKey(), new StringBody(entry.getValue(), Charset.forName("UTF-8")));
				}
			}
		} catch (UnsupportedEncodingException e) {
			VolleyLog.e("UnsupportedEncodingException");
		}
	}

	@Override
	public String getBodyContentType() {
		return entity.getContentType().getValue();
	}

	@Override
	public byte[] getBody() throws AuthFailureError {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			entity.writeTo(bos);
		} catch (IOException e) {
			VolleyLog.e("IOException writing to ByteArrayOutputStream");
		}
		return bos.toByteArray();
	}

	@Override
	protected Response<String> parseNetworkResponse(NetworkResponse response) {
		if (VolleyLog.DEBUG) {
			if (response.headers != null) {
				for (Map.Entry<String, String> entry : response.headers.entrySet()) {
					VolleyLog.d(entry.getKey() + "=" + entry.getValue());
				}
			}
		}

		String parsed;
		try {
			parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
		} catch (UnsupportedEncodingException e) {
			parsed = new String(response.data);
		}
		return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.android.volley.Request#getHeaders()
	 */
	@Override
	public Map<String, String> getHeaders() throws AuthFailureError {
		VolleyLog.d("getHeaders");
		Map<String, String> headers = super.getHeaders();

		if (headers == null || headers.equals(Collections.emptyMap())) {
			headers = new HashMap<String, String>();
		}

		return headers;
	}

	@Override
	protected void deliverResponse(String response) {
		mListener.onResponse(response);
	}
}