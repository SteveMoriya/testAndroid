package com.wehang.txlibrary.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wehang.txlibrary.R;

/**
 * Created by lenovo on 2017/8/18.
 */

public class Utils {
    public static AlertDialog showYesNoDialog(Context context, String text, String yes, String no, final DialogInterface.OnClickListener listener,boolean isShow) {
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
    public static AlertDialog showEerMesage(Context context, String text, String yes, String no, final DialogInterface.OnClickListener listener) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_dialog_mesage, null);
        TextView ss = (TextView) view.findViewById(R.id.dialogTv);
        ss.setText(text);
        final AlertDialog dialog = new AlertDialog.Builder(context).setCancelable(true).show();
        Window win = dialog.getWindow();
        win.setContentView(view);
        return dialog;
    }
}
