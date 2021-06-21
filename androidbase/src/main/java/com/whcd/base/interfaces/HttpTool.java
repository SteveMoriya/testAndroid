package com.whcd.base.interfaces;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;

import com.google.gson.Gson;
import com.whcd.base.component.volley.AuthFailureError;
import com.whcd.base.component.volley.Request.Method;
import com.whcd.base.component.volley.Response;
import com.whcd.base.component.volley.VolleyError;
import com.whcd.base.component.volley.toolbox.MultipartRequest;
import com.whcd.base.component.volley.toolbox.StringRequest;
import com.whcd.base.utils.CommonUtils;
import com.whcd.base.utils.LogUtils;
import com.whcd.base.widget.CustomProgressDialog;

import java.io.File;
import java.lang.reflect.Type;
import java.util.Map;

import static com.umeng.socialize.Config.dialog;

/**
 * <p>
 * Http Get、Post、文件上传请求封装类
 * </p>
 *
 * @author 张家奇 2016年4月16日 下午5:27:21
 * @version V1.0
 * @modificationHistory=========================重大变更说明
 * @modify by user: 张家奇 2016年4月16日
 */
public class HttpTool {
    private static final String TAG = "HttpTool";
    public static final int NETWORK_ERROR = -100;
    public static final int OTHER_ERROR = -101;

    /**
     * http post请求
     *
     * @param context
     * @param url                  接口地址
     * @param params               参数Map
     * @param isShowProgressDialog 是否显示进度框
     * @param listener             接口回调
     * @author 张家奇 2016年4月16日 下午5:28:05
     * @modificationHistory=========================方法变更说明
     * @modify by user: 张家奇 2016年4月16日
     * @modify by reason: 原因
     */
    public static void doPost(final Context context, final String url, final Map<String, String> params, final boolean isShowProgressDialog, final Type type,
                              final OnResponseListener listener) {
        if (!CommonUtils.isNetworkAvailable(context)) {
            CommonUtils.showTipMsg(context, Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 120, "加载失败，请检查网络或稍后重试");
            if (listener != null) {
                listener.onError(NETWORK_ERROR);
            }
            return;
        }


        final StringRequest request = new StringRequest(Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                LogUtils.debug(TAG, url + "返回：" + response);

                BaseResult<BaseData> result = null;
                if (response != null) {
                    try {
                        result = new Gson().fromJson(response, type);
                        Log.i("response",response);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                boolean success = checkResponse(context, result);
                if (listener != null && success) {
                    listener.onSuccess(result.data);
                } else if (listener != null) {
                    String error=result.status+"";
                    if (!TextUtils.isEmpty(error)){
                        listener.onError(result.status);
                    }else {
                        listener.onError(1);
                    }

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }

                LogUtils.error(TAG, url + "返回：" + error.getMessage());
                if (listener != null) {
                    listener.onError(OTHER_ERROR);
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };

        request.setTag(context.getClass().getSimpleName());
        VolleyTool.addRequest(request);
    }


    public static void doPost1(final Context context, final String url, final Map<String, String> params, final boolean isShowProgressDialog, final Type type,
                              final OnResponseListener1 listener) {
        if (!CommonUtils.isNetworkAvailable(context)) {
            CommonUtils.showTipMsg(context, Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 120, "加载失败，请检查网络或稍后重试");
            if (listener != null) {
                listener.onError(NETWORK_ERROR);
            }
            return;
        }


        StringRequest request = new StringRequest(Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                LogUtils.debug(TAG, url + "返回：" + response);
                BaseResult<BaseData> result = null;
                if (response != null) {
                    try {
                        result = new Gson().fromJson(response, type);
                        Log.i("response",response);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                boolean success = checkResponse(context, result);
                if (listener != null && success) {
                    listener.onSuccess(response);
                } else if (listener != null) {
                    listener.onError(result.status);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }

                LogUtils.error(TAG, url + "返回：" + error.getMessage());
                if (listener != null) {
                    listener.onError(OTHER_ERROR);
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };

        request.setTag(context.getClass().getSimpleName());
        VolleyTool.addRequest(request);
    }

    /**
     * http get请求
     *
     * @param context
     * @param url                  接口地址?key=value&key=value&
     * @param isShowProgressDialog 是否显示进度框
     * @param listener             接口回调
     * @author 张家奇 2016年4月16日 下午5:28:11
     * @modificationHistory=========================方法变更说明
     * @modify by user: 张家奇 2016年4月16日
     * @modify by reason: 原因
     */
    public static void doGet(final Context context, final String url, final boolean isShowProgressDialog, final Type type, final OnResponseListener listener) {
        if (!CommonUtils.isNetworkAvailable(context)) {
            CommonUtils.showTipMsg(context, Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 120, "加载失败，请检查网络或稍后重试");

            if (listener != null) {
                listener.onError(NETWORK_ERROR);
            }
            return;
        }


        StringRequest request = new StringRequest(Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                LogUtils.debug(TAG, url + "返回：" + response);

                BaseResult<BaseData> result = null;
                if (response != null) {
                    try {
                        result = new Gson().fromJson(response, type);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                boolean success = checkResponse(context, result);
                if (listener != null && success) {
                    listener.onSuccess(result.data);
                } else if (listener != null) {
                    listener.onError(result.status);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }

                LogUtils.error(TAG, url + "返回：" + error.getMessage());
                if (listener != null) {
                    listener.onError(OTHER_ERROR);
                }
            }
        });

        request.setTag(context.getClass().getSimpleName());
        VolleyTool.addRequest(request);
    }

    /**
     * 文件上传
     *
     * @param context
     * @param url
     * @param path
     * @param params
     * @param isShowProgressDialog
     * @param listener
     * @author 张家奇 2016年4月16日 下午5:48:38
     * @modificationHistory=========================方法变更说明
     * @modify by user: 张家奇 2016年4月16日
     * @modify by reason: 原因
     */
    public static void doUpload(final Context context, final String url, final String path, final Map<String, String> params,
                                final boolean isShowProgressDialog, final Type type, final OnResponseListener listener) {
        if (!CommonUtils.isNetworkAvailable(context)) {
            CommonUtils.showTipMsg(context, Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 120, "加载失败，请检查网络或稍后重试");

            if (listener != null) {
                listener.onError(NETWORK_ERROR);
            }
            return;
        }

        final CustomProgressDialog dialog;
        if (isShowProgressDialog) {
            dialog = CustomProgressDialog.show(context, "加载中...", true, null);
        } else {
            dialog = null;
        }

        MultipartRequest request = new MultipartRequest(url, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }

                LogUtils.error(TAG, url + "返回：" + error.getMessage());
                if (listener != null) {
                    listener.onError(OTHER_ERROR);
                }
            }
        }, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }

                LogUtils.debug(TAG, url + "返回：" + response);

                BaseResult<BaseData> result = null;
                if (response != null) {
                    try {
                        result = new Gson().fromJson(response, type);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                boolean success = checkResponse(context, result);
                if (listener != null && success) {
                    listener.onSuccess(result.data);
                } else if (listener != null) {
                    listener.onError(result.status);
                }
            }
        }, "file", new File(path), params);

        request.setTag(context.getClass().getSimpleName());
        VolleyTool.addRequest(request);
    }


    public static void doUploadNoDilog(final Context context, final String url, final String path, final Map<String, String> params,
                                final boolean isShowProgressDialog, final Type type, final OnResponseListener listener) {
        if (!CommonUtils.isNetworkAvailable(context)) {
            CommonUtils.showTipMsg(context, Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 120, "加载失败，请检查网络或稍后重试");

            if (listener != null) {
                listener.onError(NETWORK_ERROR);
            }
            return;
        }

        MultipartRequest request = new MultipartRequest(url, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }

                LogUtils.error(TAG, url + "返回：" + error.getMessage());
                if (listener != null) {
                    listener.onError(OTHER_ERROR);
                }
            }
        }, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }

                LogUtils.debug(TAG, url + "返回：" + response);

                BaseResult<BaseData> result = null;
                if (response != null) {
                    try {
                        result = new Gson().fromJson(response, type);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                boolean success = checkResponse(context, result);
                if (listener != null && success) {
                    listener.onSuccess(result.data);
                } else if (listener != null) {
                    listener.onError(result.status);
                }
            }
        }, "file", new File(path), params);

        request.setTag(context.getClass().getSimpleName());
        VolleyTool.addRequest(request);
    }

    /**
     * 检查请求结果
     *
     * @param context
     * @param result
     * @return
     * @author 张家奇 2016年4月17日 下午4:56:04
     * @modificationHistory=========================方法变更说明
     * @modify by user: 张家奇 2016年4月17日
     * @modify by reason: 原因
     */
    public static boolean checkResponse(Context context, BaseResult<BaseData> result) {
        if (result != null) {
            if (result.status == 0) {
                return true;
            } else if (result.status == -2) {
//                CommonUtils.showTipMsg(context, Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 120, "您的登录信息已过期,请重新登录");
                return false;
            } else {
                CommonUtils.showTipMsg(context, Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 120, result.msg);
                return false;
            }
        } else {
            CommonUtils.showTipMsg(context, Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 120, "加载失败，请检查网络或稍后重试");
            return false;
        }
    }

    public interface OnResponseListener {
        public void onSuccess(BaseData data);

        public void onError(int errorCode);
    }
    public interface OnResponseListener1 {
        public void onSuccess(String string);

        public void onError(int errorCode);
    }

}
