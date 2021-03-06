/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.whcd.base.component.easeui.utils;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.hyphenate.chat.EMConversation.EMConversationType;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.util.EMLog;
import com.hyphenate.util.HanziToPinyin;
import com.hyphenate.util.HanziToPinyin.Token;
import com.whcd.base.R;
import com.whcd.base.component.easeui.EaseConstant;
import com.whcd.base.component.easeui.domain.EaseUser;

public class EaseCommonUtils {
	private static final String TAG = "CommonUtils";
	private static final String STATUS_BAR_HEIGHT_RES_NAME = "status_bar_height";

	/**
	 * 检测网络是否可用
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetWorkConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable() && mNetworkInfo.isConnected();
			}
		}

		return false;
	}

	/**
	 * 检测Sdcard是否存在
	 * 
	 * @return
	 */
	public static boolean isExitsSdcard() {
		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
			return true;
		else
			return false;
	}

	public static EMMessage createExpressionMessage(String toChatUsername, String expressioName, String identityCode) {
		EMMessage message = EMMessage.createTxtSendMessage("[" + expressioName + "]", toChatUsername);
		if (identityCode != null) {
			message.setAttribute(EaseConstant.MESSAGE_ATTR_EXPRESSION_ID, identityCode);
		}
		message.setAttribute(EaseConstant.MESSAGE_ATTR_IS_BIG_EXPRESSION, true);
		return message;
	}

	/**
	 * 根据消息内容和消息类型获取消息内容提示
	 * 
	 * @param message
	 * @param context
	 * @return
	 */
	public static String getMessageDigest(EMMessage message, Context context) {
		String digest = "";
		switch (message.getType()) {
		case LOCATION: // 位置消息
			if (message.direct() == EMMessage.Direct.RECEIVE) {
				// 从sdk中提到了ui中，使用更简单不犯错的获取string方法
				// digest = EasyUtils.getAppResourceString(context,
				// "location_recv");
				digest = getString(context, R.string.location_recv);
				digest = String.format(digest, message.getFrom());
				return digest;
			} else {
				// digest = EasyUtils.getAppResourceString(context,
				// "location_prefix");
				digest = getString(context, R.string.location_prefix);
			}
			break;
		case IMAGE: // 图片消息
			digest = getString(context, R.string.picture);
			break;
		case VOICE:// 语音消息
			digest = getString(context, R.string.voice_prefix);
			break;
		case VIDEO: // 视频消息
			digest = getString(context, R.string.video);
			break;
		case TXT: // 文本消息
			EMTextMessageBody txtBody = (EMTextMessageBody) message.getBody();
			/*
			 * if(((DemoHXSDKHelper)HXSDKHelper.getInstance()).isRobotMenuMessage
			 * (message)){ digest =
			 * ((DemoHXSDKHelper)HXSDKHelper.getInstance()).
			 * getRobotMenuMessageDigest(message); }else
			 */if (message.getBooleanAttribute(EaseConstant.MESSAGE_ATTR_IS_VOICE_CALL, false)) {
				digest = getString(context, R.string.voice_call) + txtBody.getMessage();
			} else if (message.getBooleanAttribute(EaseConstant.MESSAGE_ATTR_IS_VIDEO_CALL, false)) {
				digest = getString(context, R.string.video_call) + txtBody.getMessage();
			} else if (message.getBooleanAttribute(EaseConstant.MESSAGE_ATTR_IS_BIG_EXPRESSION, false)) {
				if (!TextUtils.isEmpty(txtBody.getMessage())) {
					digest = txtBody.getMessage();
				} else {
					digest = getString(context, R.string.dynamic_expression);
				}
			} else {
				digest = txtBody.getMessage();
			}
			break;
		case FILE: // 普通文件消息
			digest = getString(context, R.string.file);
			break;
		default:
			EMLog.e(TAG, "error, unknow type");
			return "";
		}

		return digest;
	}

	static String getString(Context context, int resId) {
		return context.getResources().getString(resId);
	}

	/**
	 * 获取栈顶的activity
	 * 
	 * @param context
	 * @return
	 */
	public static String getTopActivity(Context context) {
		ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);

		if (runningTaskInfos != null)
			return runningTaskInfos.get(0).topActivity.getClassName();
		else
			return "";
	}

	/**
	 * 设置user昵称(没有昵称取username)的首字母属性，方便通讯中对联系人按header分类显示，以及通过右侧ABCD...
	 * 字母栏快速定位联系人
	 * 
	 * @param username
	 * @param user
	 */
	public static void setUserInitialLetter(EaseUser user) {
		final String DefaultLetter = "#";
		String letter = DefaultLetter;

		final class GetInitialLetter {
			String getLetter(String name) {
				if (TextUtils.isEmpty(name)) {
					return DefaultLetter;
				}
				char char0 = name.toLowerCase().charAt(0);
				if (Character.isDigit(char0)) {
					return DefaultLetter;
				}
				ArrayList<Token> l = HanziToPinyin.getInstance().get(name.substring(0, 1));
				if (l != null && l.size() > 0 && l.get(0).target.length() > 0) {
					Token token = l.get(0);
					String letter = token.target.substring(0, 1);
					char c = letter.toLowerCase().charAt(0);
					if (c < 'a' || c > 'z') {
						return DefaultLetter;
					}
					return letter;
				}
				return DefaultLetter;
			}
		}

		if (!TextUtils.isEmpty(user.getNick())) {
			letter = new GetInitialLetter().getLetter(user.getNick());
			user.setInitialLetter(letter);
			return;
		}
		if (letter == DefaultLetter && !TextUtils.isEmpty(user.getUsername())) {
			letter = new GetInitialLetter().getLetter(user.getUsername());
		}
		user.setInitialLetter(letter);
	}

	/**
	 * 将应用的会话类型转化为SDK的会话类型
	 * 
	 * @param chatType
	 * @return
	 */
	public static EMConversationType getConversationType(int chatType) {
		if (chatType == EaseConstant.CHATTYPE_SINGLE) {
			return EMConversationType.Chat;
		} else if (chatType == EaseConstant.CHATTYPE_GROUP) {
			return EMConversationType.GroupChat;
		} else {
			return EMConversationType.ChatRoom;
		}
	}

	public static int getStatusBarHeight(Context context) {
		return getInternalDimensionSize(context.getResources(), STATUS_BAR_HEIGHT_RES_NAME);
	}

	private static int getInternalDimensionSize(Resources res, String key) {
		int result = 0;
		int resourceId = res.getIdentifier(key, "dimen", "android");
		if (resourceId > 0) {
			result = res.getDimensionPixelSize(resourceId);
		}
		return result;
	}

	public static int getKeyboardHeight(Activity paramActivity) {

		int height = getScreenHeight(paramActivity) - getStatusBarHeight(paramActivity) - getAppHeight(paramActivity);
		if (height == 0) {
			height = SharedPreferencesUtils.getIntShareData("KeyboardHeight", getScreenHeight(paramActivity) / 3);// 787为默认软键盘高度
																													// 基本差不离
		} else {
			SharedPreferencesUtils.putIntShareData("KeyboardHeight", height);
		}
		return height;
	}

	/** 屏幕分辨率高 **/
	public static int getScreenHeight(Activity paramActivity) {
		Display display = paramActivity.getWindowManager().getDefaultDisplay();
		DisplayMetrics metrics = new DisplayMetrics();
		display.getMetrics(metrics);
		return metrics.heightPixels;
	}

	/** 可见屏幕高度 **/
	private static int getAppHeight(Activity paramActivity) {
		Rect localRect = new Rect();
		paramActivity.getWindow().getDecorView().getWindowVisibleDisplayFrame(localRect);
		return localRect.height();
	}

	/** 键盘是否在显示 **/
	public static boolean isKeyBoardShow(Activity paramActivity) {
		int height = getScreenHeight(paramActivity) - getStatusBarHeight(paramActivity) - getAppHeight(paramActivity);
		return height != 0;
	}

	/** 显示键盘 **/
	public static void showKeyBoard(final View paramEditText, final Context context) {
		paramEditText.requestFocus();
		paramEditText.post(new Runnable() {
			@Override
			public void run() {
				((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(paramEditText, 0);
			}
		});
	}
}
