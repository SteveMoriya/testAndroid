package com.wehang.ystv.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.blankj.utilcode.util.LogUtils;
import com.google.gson.reflect.TypeToken;
import com.wehang.ystv.VideoApplication;
import com.wehang.ystv.bo.LiveNeed;
import com.wehang.ystv.bo.Lives;
import com.wehang.ystv.bo.UserInfo;
import com.wehang.ystv.interfaces.UrlConstant;
import com.wehang.ystv.ui.HomeActivity;
import com.wehang.ystv.ui.LivePushActivity;
import com.wehang.ystv.ui.LiveWatchNewActivity;
import com.wehang.ystv.utils.CommonUtil;
import com.wehang.ystv.utils.UserLoginInfo;
import com.wehang.ystv.utils.Utils;
import com.whcd.base.interfaces.BaseData;
import com.whcd.base.interfaces.BaseResult;
import com.whcd.base.interfaces.HttpTool;
import com.whcd.base.widget.CustomProgressDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;

/**
 * 自定义接收器
 * <p>
 * 如果不定义这个 Receiver，则： 1) 默认用户会打开主界面 2) 接收不到自定义消息
 */
public class JPushReceiver extends BroadcastReceiver {
    private static final String TAG = "JPush_JPushReceiver";
    private Context context;
    @Override
    public void onReceive(final Context context, Intent intent) {
        this.context=VideoApplication.applicationContext;
        Bundle bundle = intent.getExtras();
        Log.d(TAG, "[JPushReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));
        LogUtils.i("WhatIneed",IneedString+"");
        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            Log.d(TAG, "[JPushReceiver] 接收Registration Id : " + regId);
            // send the Registration Id to your server...

        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            Log.d(TAG, "[JPushReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
            processCustomMessage(context, bundle);

        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            Log.d(TAG, "[JPushReceiver] 接收到推送下来的通知");
            int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
            Log.d(TAG, "[JPushReceiver] 接收到推送下来的通知的ID: " + notifactionId);

        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            Log.d(TAG, "[JPushReceiver] 用户点击打开了通知");
            if (!TextUtils.isEmpty(IneedString)){
                final String[]s=IneedString.split(",");
                //别人的直播
                if (TextUtils.isEmpty(s[2])){
                    return;
                }
                if (s[0].equals("0")){
                  /*  CommonUtil.showYesNoDialog(context, "是否开始观看？", "确定", "取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == DialogInterface.BUTTON_POSITIVE) {

                            }else {

                            }

                        }
                    }).show();*/
                    //增加浏览记录
                    Lives lives=new Lives();
                    lives.sourceId=s[2];
                    lives.sourceType=1;
                    Utils.addWatchHistory(context, UserLoginInfo.getUserToken(),lives.sourceId);
                    enterRoom(lives);
                }else if (s[0].equals("1")){
                    /*CommonUtil.showYesNoDialog(context, "是否开始直播？", "确定", "取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == DialogInterface.BUTTON_POSITIVE) {

                            }else {

                            }

                        }
                    }).show();*/
                    getLiveNeed(s[2]);

                }
            }
            // 打开自定义的Activity
            // Intent i = new Intent(context, TestActivity.class);
            // i.putExtras(bundle);
            // // i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            // i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
            // Intent.FLAG_ACTIVITY_CLEAR_TOP);
            // context.startActivity(i);

        } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
            Log.d(TAG, "[JPushReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
            // 在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity，
            // 打开一个网页等..

        } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
            boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
            Log.w(TAG, "[JPushReceiver]" + intent.getAction() + " connected state change to " + connected);
        } else {
            Log.d(TAG, "[JPushReceiver] Unhandled intent - " + intent.getAction());
        }
    }
    private static String IneedString="";
    // 打印所有的 intent extra 数据
    private static String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
            } else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
            } else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
                if (TextUtils.isEmpty(bundle.getString(JPushInterface.EXTRA_EXTRA))) {
                    Log.i(TAG, "This message has no Extra data");
                    continue;
                }

                try {
                    JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
                    Iterator<String> it = json.keys();

                    while (it.hasNext()) {

                        String myKey = it.next().toString();
                        if (myKey.equals("ext")){
                            IneedString=json.optString(myKey);
                        }
                        sb.append("\nkey:" + key + ", value: [" + myKey + " - " + json.optString(myKey) + "]");
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Get message extra JSON error!");
                }

            } else {
                sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
            }
        }
        return sb.toString();
    }

    // send msg to MainActivity
    private void processCustomMessage(Context context, Bundle bundle) {
//		if (MainActivity.isForeground) {
//			String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
//			String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
//			Intent msgIntent = new Intent(MainActivity.MESSAGE_RECEIVED_ACTION);
//			msgIntent.putExtra(MainActivity.KEY_MESSAGE, message);
//			if (!ExampleUtil.isEmpty(extras)) {
//				try {
//					JSONObject extraJson = new JSONObject(extras);
//					if (extraJson.length() > 0) {
//						msgIntent.putExtra(MainActivity.KEY_EXTRAS, extras);
//					}
//				} catch (JSONException e) {
//
//				}
//
//			}
//			context.sendBroadcast(msgIntent);
//		}
    }

    public static boolean checkApkExist(Context context, Intent intent) {
        List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent, 0);
        if (list.size() > 0) {
            return true;
        }
        return false;
    }

    public static boolean checkApkExist(Context context, String packageName) {
        if (packageName == null || "".equals(packageName))
            return false;
        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(packageName,
                    PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (NameNotFoundException e) {
            return false;
        }
    }

    private void enterRoom(final Lives lives) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", UserLoginInfo.getUserToken());
        params.put("sourceId", lives.sourceId);
        HttpTool.doPost(context, UrlConstant.ENTERROOM, params, true, new TypeToken<BaseResult<UserInfo>>() {
        }.getType(), new HttpTool.OnResponseListener() {
            @Override
            public void onSuccess(BaseData data) {
                UserInfo userInfo = (UserInfo) data;
                if (userInfo != null) {
                    Intent intent=new Intent(context,LiveWatchNewActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Bundle bundle=new Bundle();
                    bundle.putSerializable("data",userInfo);
                    bundle.putSerializable("live",lives);
                    intent.putExtra("bundle",bundle);
                    intent.putExtra("sourceId",lives.sourceId);
                    context.startActivity(intent);
                }
            }

            @Override
            public void onError(int errorCode) {
            }
        });
    }
    //预告跳转直播需要
    private CustomProgressDialog dialog;
    public LiveNeed liveNeed;
    private void getLiveNeed(String sourceId){
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", UserLoginInfo.getUserInfo().token);
        params.put("sourceId", sourceId);
        HttpTool.doPost(context, UrlConstant.FORESHOWBEGINLIVE, params, true, new TypeToken<BaseResult<LiveNeed>>() {
        }.getType(), new HttpTool.OnResponseListener() {
            @Override
            public void onSuccess(BaseData data) {
                liveNeed= (LiveNeed) data;
                LogUtils.i("LiveNeed",liveNeed.toString());
                if (liveNeed!=null){
                    liveNeed.isPhonePush=1;
                    checkPermissions();
                }
            }
            @Override
            public void onError(int errorCode) {
            }
        });
    }
    public void checkPermissions() {
        Intent intent=new Intent(context,LivePushActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle bundle=new Bundle();
        bundle.putSerializable("data",liveNeed);
        intent.putExtra("bundle",bundle);
        context. startActivity(intent);
    }
}
