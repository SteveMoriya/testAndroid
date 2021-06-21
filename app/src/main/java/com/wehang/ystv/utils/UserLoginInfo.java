package com.wehang.ystv.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.blankj.utilcode.util.LogUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hyphenate.chat.EMClient;
import com.tencent.TIMCallBack;
import com.tencent.TIMFriendGenderType;
import com.tencent.TIMFriendshipManager;
import com.tencent.openqq.protocol.imsdk.im_common;
import com.wehang.ystv.bo.UserInfo;
import com.wehang.ystv.ui.LoginActivity;


import java.util.List;

public class UserLoginInfo {
    //注册成功后
    public static UserInfo userInfo;
    public static UserInfo wantToTolk;
    public static  String districtId=378+"";



    public static boolean isLogin(Context context) {
        return context.getSharedPreferences("userInfo", Context.MODE_PRIVATE).getBoolean("is_login", false);
    }

    public static boolean isPush(Context context) {
        return context.getSharedPreferences("userInfo", Context.MODE_PRIVATE).getBoolean("is_push", true);
    }

    public static void setPush(Context context, boolean isPush) {
        Editor editor = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE).edit();
        editor.putBoolean("is_push", isPush);
        editor.commit();
    }

    public static void saveUserInfo(Context context, UserInfo userInfo) {
        if (getUserInfo()!=null){
            if (!TextUtils.isEmpty(getUserInfo().password)){
                LogUtils.i("infoSave",getUserInfo().password+"");
                userInfo.password=getUserInfo().password;
            }
        }

        Editor editor = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE).edit();
        Gson gson = new Gson();
        editor.putString("userinfo", gson.toJson(userInfo));
        editor.putBoolean("is_login", true);
        editor.commit();
    }

    public static void saveFocusInfo(Context context, List<String> userID) {
        Editor editor = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE).edit();
        Gson gson = new Gson();
        String json = gson.toJson(userID);
        editor.putString("focus", json);
        editor.commit();
    }

    public static List<String> getFocusInfo(Context context) {
        String focuses = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE).getString("focus", "");
        if (focuses != null) {
            Gson gson = new Gson();
            List<String> focusList = gson.fromJson(focuses, new TypeToken<List<String>>() {
            }.getType());
            return focusList;
        }
        return null;
    }

    public static void exitLogin(Context context) {
        Editor editor = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE).edit();
        editor.putString("userinfo", "");
        editor.putBoolean("is_login", false);
        editor.commit();
    }
    public static void saveUserSig(String userSig) {
        SharedUtil.writeStringMethod("userInfo", "userSig", userSig);
    }

    public static String getUserSig() {
        return SharedUtil.readStringMethod("userInfo", "userSig", "");
    }
    public static UserInfo getUserInfo(Context context) {
        String userInfo = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE).getString("userinfo", "");
        if (userInfo != null) {
            Gson gson = new Gson();
            return gson.fromJson(userInfo, UserInfo.class);
        }
        return null;
    }
    public static String getUserToken() {
        String userInfoStr = SharedUtil.readStringMethod("userInfo", "userinfo", "");
        if (!TextUtils.isEmpty(userInfoStr)) {
            Gson gson = new Gson();
            UserInfo info = gson.fromJson(userInfoStr, UserInfo.class);
            return info.token;
        }
        return "";
    }

    public static UserInfo getUserInfo() {
        String userInfoStr = SharedUtil.readStringMethod("userInfo", "userinfo", "");
        if (!TextUtils.isEmpty(userInfoStr)) {
            Gson gson = new Gson();
            return gson.fromJson(userInfoStr, UserInfo.class);
        }
        return null;
    }
    public static String getUserToken(Context context) {
        String userInfo = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE).getString("userinfo", "");
        if (userInfo != null) {
            Gson gson = new Gson();
            UserInfo info = gson.fromJson(userInfo, UserInfo.class);
            return info.token;
        }
        return null;
    }

    public static boolean loginOverdue(final Activity context, int errorCode) {
        if (context != null && errorCode == -2) {
            CommonUtil.showYesDialog(context, "您的登录信息已过期，请重新登录", "确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                  //  EMClient.getInstance().logout(false);
                    exitLogin(context);
                    Intent intent = new Intent(context, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    context.startActivity(intent);
                    context.finish();
                }
            });
            return true;
        }else if (context!=null&&errorCode==1){
            ToastUtil.makeText(context,"请求数据失败", Toast.LENGTH_SHORT).show();
            //return true;
        }
        return false;
    }

    public static String tag="TIMtest";
    public static void setTIMmessage(UserInfo loginInfo){
        String name=loginInfo.name;
        String iconurl=loginInfo.iconUrl;
        Log.e(tag, name+";"+loginInfo);
        //设置新昵称为cat
        TIMFriendshipManager.getInstance().setNickName(loginInfo.name, new TIMCallBack(){
            @Override
            public void onError(int code, String desc){
                //错误码code和错误描述desc，可用于定位请求失败原因
                //错误码code列表请参见错误码表
                Log.e(tag, "setNickName failed: " + code + " desc"+desc);
            }

            @Override
            public void onSuccess(){
                Log.e(tag, "setNickName succ");
            }
        });
        //设置头像URL
        TIMFriendshipManager.getInstance().setFaceUrl(loginInfo.iconUrl, new TIMCallBack(){
            @Override
            public void onError(int code, String desc) {
                //错误码code和错误描述desc，可用于定位请求失败原因
                //错误码code列表请参见错误码表
                Log.e(tag, "setFaceUrl failed: " + code + " desc" + desc);
            }

            @Override
            public void onSuccess() {
                Log.e(tag, "setFaceUrl succ");
            }
        });
        TIMFriendGenderType type;
        if (loginInfo.sex==0){
            type=TIMFriendGenderType.Female;
        }else {
            type=TIMFriendGenderType.Male;
        }
        TIMFriendshipManager.getInstance().setGender(type, new TIMCallBack() {
            @Override
            public void onError(int code, String desc) {
                //错误码code和错误描述desc，可用于定位请求失败原因
                //错误码code列表请参见错误码表
                Log.e(tag, "setGender failed: " + code + " desc" + desc);
            }

            @Override
            public void onSuccess() {
                Log.e(tag, "setGender succ");
            }
        });
        TIMFriendshipManager.getInstance().setBirthday(loginInfo.isVip, new TIMCallBack() {
            @Override
            public void onError(int code, String desc) {
                //错误码code和错误描述desc，可用于定位请求失败原因
                //错误码code列表请参见错误码表
                Log.e(tag, "setSelfSignature failed: " + code + " desc" + desc);
            }

            @Override
            public void onSuccess() {
                Log.e(tag, "setSelfSignature succ");
            }
        });
    }
}
