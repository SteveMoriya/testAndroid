package com.wehang.ystv.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.wehang.ystv.R;


import java.lang.reflect.Field;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;



public class CommonUtil {
    protected static long lastClickTime = 0;
    protected final static int TIME_INTERVAL = 500;

    public static int getInt(String str, int def) {
        try {
            if (str == null) {
                return def;
            }
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return def;
    }

    public static float getFloat(String str) {
        try {
            if (TextUtils.isEmpty(str)) {
                return 0;
            }
            float resultStr = (Float.parseFloat(str)) / 100;
            return resultStr;
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * @return
     * @author 常瑞 2016-5-27 上午10:49:12
     * 的宽高
     * @modificationHistory=========================方法变更说明
     * @modify by user: 常瑞 2016-5-27
     * @modify by reason: 原因
     */
    public static int[] getLocation(View v) {
        int[] loc = new int[4];
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        loc[0] = location[0];
        loc[1] = location[1];
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        v.measure(w, h);

        loc[2] = v.getMeasuredWidth();
        loc[3] = v.getMeasuredHeight();

        // base = computeWH();
        System.out.println(loc[0] + ":::::::::::" + loc[1] + "::::::::::::" + loc[2] + ":::::::::::" + loc[3]);
        return loc;
    }
    public static boolean isFirstClick() {
        // 避免连续点击
        if (System.currentTimeMillis() - lastClickTime < TIME_INTERVAL) {
            return true;
        }
        lastClickTime = System.currentTimeMillis();
        return false;
    }
    public static AlertDialog showYesNoDialog(Context context, String text, String yes, String no, final OnClickListener listener) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_dialog_yes_no, null);
        TextView ss = (TextView) view.findViewById(R.id.dialogTv);
        ss.setText(text);
        final AlertDialog dialog = new AlertDialog.Builder(context).setCancelable(true).show();
        Window win = dialog.getWindow();
        win.setContentView(view);

        Button btnYes = (Button) view.findViewById(R.id.yesBtn);
        btnYes.setText(yes);
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(dialog, DialogInterface.BUTTON_POSITIVE);
                dialog.dismiss();
            }
        });
        Button btnNo = (Button) view.findViewById(R.id.noBtn);
        btnNo.setText(no);
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(dialog, DialogInterface.BUTTON_NEGATIVE);
                dialog.dismiss();
            }
        });
        return dialog;
    }
    public static AlertDialog mustChoseDialog(Context context, String text, String yes, String no, final OnClickListener listener) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_dialog_yes_no, null);
        TextView ss = (TextView) view.findViewById(R.id.dialogTv);
        ss.setText(text);
        final AlertDialog dialog = new AlertDialog.Builder(context).setCancelable(true).show();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        Window win = dialog.getWindow();
        win.setContentView(view);

        Button btnYes = (Button) view.findViewById(R.id.yesBtn);
        btnYes.setText(yes);
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(dialog, DialogInterface.BUTTON_POSITIVE);
                dialog.dismiss();
            }
        });
        Button btnNo = (Button) view.findViewById(R.id.noBtn);
        btnNo.setText(no);
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(dialog, DialogInterface.BUTTON_NEGATIVE);
                dialog.dismiss();
            }
        });
        return dialog;
    }
    public static AlertDialog QshowYesNoDialog(Context context, String text, String yes, String no, final OnClickListener listener) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_dialog_yes_no, null);
        TextView ss = (TextView) view.findViewById(R.id.dialogTv);
        ss.setText(text);
        final AlertDialog dialog = new AlertDialog.Builder(context).setCancelable(true).show();
        Window win = dialog.getWindow();
        win.setContentView(view);

        Button btnYes = (Button) view.findViewById(R.id.yesBtn);
        btnYes.setTextColor(ContextCompat.getColor(context,R.color.btn_blue_normal));
        btnYes.setText(yes);
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(dialog, DialogInterface.BUTTON_POSITIVE);
                dialog.dismiss();
            }
        });
        Button btnNo = (Button) view.findViewById(R.id.noBtn);
        btnNo.setTextColor(ContextCompat.getColor(context,R.color.btn_blue_normal));
        btnNo.setText(no);
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(dialog, DialogInterface.BUTTON_NEGATIVE);
                dialog.dismiss();
            }
        });
        return dialog;
    }

    public static AlertDialog showTalkMessage(Context context, String text, String yes, String no, final OnClickListener listener) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_talk, null);
        TextView ss = (TextView) view.findViewById(R.id.dialogTv);
        ss.setText(text);
        final AlertDialog dialog = new AlertDialog.Builder(context).setCancelable(true).show();
        Window win = dialog.getWindow();
        win.setContentView(view);

        Button btnYes = (Button) view.findViewById(R.id.yesBtn);
        btnYes.setTextColor(ContextCompat.getColor(context,R.color.btn_blue_normal));
        btnYes.setText(yes);
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(dialog, DialogInterface.BUTTON_POSITIVE);
                dialog.dismiss();
            }
        });
        Button btnNo = (Button) view.findViewById(R.id.noBtn);
        btnNo.setTextColor(ContextCompat.getColor(context,R.color.btn_blue_normal));
        btnNo.setText(no);
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(dialog, DialogInterface.BUTTON_NEGATIVE);
                dialog.dismiss();
            }
        });
        return dialog;
    }

    public static AlertDialog showYesNoDialogAddTitle(Context context, String text, String yes, String no, final OnClickListener listener) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_dialog_yes_no, null);
        TextView ss = (TextView) view.findViewById(R.id.dialogTv);
        ss.setText(text);
        final AlertDialog dialog = new AlertDialog.Builder(context).setCancelable(true).show();
        Window win = dialog.getWindow();
        win.setContentView(view);

        Button btnYes = (Button) view.findViewById(R.id.yesBtn);
        btnYes.setText(yes);
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(dialog, DialogInterface.BUTTON_POSITIVE);
                dialog.dismiss();
            }
        });
        Button btnNo = (Button) view.findViewById(R.id.noBtn);
        btnNo.setText(no);
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(dialog, DialogInterface.BUTTON_NEGATIVE);
                dialog.dismiss();
            }
        });
        return dialog;
    }

    public static AlertDialog showYesDialog(Context context, String text, String yes, final OnClickListener listener) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_dialog_yes, null);
        TextView ss = (TextView) view.findViewById(R.id.dialogTv);
        ss.setText(text);
        final AlertDialog dialog = new AlertDialog.Builder(context).setCancelable(false).show();
        dialog.setCanceledOnTouchOutside(false);
        Window win = dialog.getWindow();
        win.setContentView(view);

        Button btnYes = (Button) view.findViewById(R.id.yesBtn);
        btnYes.setText(yes);
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(dialog, DialogInterface.BUTTON_POSITIVE);
                dialog.dismiss();
            }
        });

        return dialog;
    }

    public static AlertDialog showSetUpDialog(Context context, String text, String yes, final OnClickListener listener) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_dialog_set_up, null);
        TextView ss = (TextView) view.findViewById(R.id.dialogTv);
        ss.setText(text);
        final AlertDialog dialog = new AlertDialog.Builder(context).setCancelable(true).show();
        Window win = dialog.getWindow();
        win.setContentView(view);

        Button btnYes = (Button) view.findViewById(R.id.yesBtn);
        btnYes.setText(yes);
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(dialog, DialogInterface.BUTTON_POSITIVE);
                dialog.dismiss();
            }
        });
        return dialog;
    }

    /**
     * 获取未读消息数
     *
     * @return
     */

    public static int getUnreadMsgCountTotal() {
        int unreadMsgCountTotal = 0;
        int chatroomUnreadMsgCount = 0;
        unreadMsgCountTotal = EMClient.getInstance().chatManager().getUnreadMsgsCount();
        // for (EMConversation conversation :
        // EMClient.getInstance().chatManager().getAllConversations().values())
        // {
        // if (conversation.getType() == EMConversationType.ChatRoom)
        // chatroomUnreadMsgCount = chatroomUnreadMsgCount +
        // conversation.getUnreadMsgCount();
        // }
        return unreadMsgCountTotal - chatroomUnreadMsgCount;
    }

    public static int getResourceId(String name) {
        try {
            Field field = R.drawable.class.getField(name);
            return Integer.parseInt(field.get(null).toString());
        } catch (Exception e) {
        }
        return 0;
    }

    public static void showSoftKeyboard(Activity aty, EditText et) {
        InputMethodManager imm = (InputMethodManager) aty.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(et, InputMethodManager.RESULT_SHOWN);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    public static void closeKeyboard(Activity aty, EditText et) {
        InputMethodManager imm = (InputMethodManager) aty.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
    }

    public static int getStatusBarHeight(Context context) {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return statusBarHeight;
    }



    /*public static void setPushStatus(Context context, String userId, boolean status) {
        XGPushDao xgPushDao = new XGPushDao(context);

        if (status) {
            xgPushDao.insertXGPushInfo(new XGPushInfo(userId));
        } else {
            xgPushDao.deleteXGPushInfo(userId);
        }

        Set<String> tagSet = new LinkedHashSet<>();
        List<XGPushInfo> localPushList = xgPushDao.queryXGPushInfo();
        if (localPushList != null && localPushList.size() != 0) {
            for (XGPushInfo info : localPushList) {
                tagSet.add(info.keyWords);
            }
        }
        JPushInterface.setTags(context, tagSet, new TagAliasCallback() {
            @Override
            public void gotResult(int i, String s, Set<String> set) {
            }
        });
    }*/
}
