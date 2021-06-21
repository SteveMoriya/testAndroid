package com.whcd.base.component.volley.toolbox;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.whcd.base.component.volley.AuthFailureError;
import com.whcd.base.component.volley.NetworkResponse;
import com.whcd.base.component.volley.ParseError;
import com.whcd.base.component.volley.Request;
import com.whcd.base.component.volley.Response;
import com.whcd.base.component.volley.Response.ErrorListener;
import com.whcd.base.component.volley.Response.Listener;

/**
 * Volley adapter for JSON requests that will be parsed into Java objects by Gson.
 */
public class GsonObjectRequest<T> extends Request<T> {
	protected final Gson gson = new Gson();
	protected final Class<T> clazz;
	protected final Map<String, String> headers;
	protected final Listener<T> listener;

	/**
	 * Make a GET request and return a parsed object from JSON.
	 * 
	 * @param url
	 *            URL of the request to make
	 * @param clazz
	 *            Relevant class object, for Gson's reflection
	 * @param headers
	 *            Map of request headers
	 */
	public GsonObjectRequest(int method, String url, Class<T> clazz, Map<String, String> headers, Listener<T> listener, ErrorListener errorListener) {
		super(method, url, errorListener);
		this.clazz = clazz;
		this.headers = headers;
		this.listener = listener;
	}

	public GsonObjectRequest(String url, Class<T> clazz, Map<String, String> headers, Listener<T> listener, ErrorListener errorListener) {
		this(Method.GET, url, clazz, headers, listener, errorListener);
	}

	@Override
	public Map<String, String> getHeaders() throws AuthFailureError {
		return headers != null ? headers : super.getHeaders();
	}

	@Override
	protected void deliverResponse(T response) {
		listener.onResponse(response);
	}

	// 将请求获取的json转成 用gson 转成了对应的对象
	@Override
	protected Response<T> parseNetworkResponse(NetworkResponse response) {
		try {
			String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
			return Response.success(gson.fromJson(json, clazz), HttpHeaderParser.parseCacheHeaders(response));
		} catch (UnsupportedEncodingException e) {
			return Response.error(new ParseError(e));
		} catch (JsonSyntaxException e) {
			return Response.error(new ParseError(e));
		}
	}
}