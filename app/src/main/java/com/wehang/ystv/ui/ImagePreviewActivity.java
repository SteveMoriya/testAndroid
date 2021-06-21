package com.wehang.ystv.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import com.wehang.ystv.R;
import com.whcd.base.activity.BaseActivity;
import com.whcd.base.widget.TopMenuBar;


import java.io.File;
import java.io.IOException;

/**
 * @author 钟林宏 2017/1/23 16:47
 * @version V1.0
 * @ProjectName: ToLiveTencentIM
 * @Copyright: 2015 WHCD Co., Ltd. All Right Reserved.
 * @address:
 * @date: 2017/1/23 16:47
 * @Description: 本内容仅限于公司内部使用，禁止转发.
 * @instructions:{类文件说明}
 * @modificationHistory=========================重大变更说明
 * @modify by user: 钟林宏 2017/1/23
 */

public class ImagePreviewActivity extends BaseActivity {
    private String path;
    private CheckBox isOri;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_image_preview;
    }

    @Override
    protected void initTitleBar() {
        TopMenuBar titleBar = (TopMenuBar) findViewById(R.id.topMenuBar);
        titleBar.setTitle("图片预览");
        Button leftButton = titleBar.getLeftButton();
        Drawable drawable = getResources().getDrawable(R.drawable.selector_icon_top_back_btn);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        leftButton.setCompoundDrawables(drawable, null, null, null);
        leftButton.setOnClickListener(this);

        Button rightButton = titleBar.getRightButton();
        rightButton.setText("发送");
        rightButton.setOnClickListener(this);
    }

    @Override
    protected void initView() {
        path = getIntent().getStringExtra("path");
        isOri = (CheckBox) findViewById(R.id.isOri);
    }

    @Override
    protected void initData(Bundle bundle) {
        showImage();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.title_btn_left:
                back();
                break;
            case R.id.title_btn_right:
                Intent intent = new Intent();
                intent.putExtra("path", path);
                intent.putExtra("isOri", isOri.isChecked());
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }

    private void showImage() {
        if (path.equals("")) return;
        File file = new File(path);
        if (file.exists() && file.length() > 0) {
            isOri.setText(getString(R.string.chat_image_preview_ori) + "(" + getFileSize(file.length()) + ")");
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, options);
            int reqWidth, reqHeight, width = options.outWidth, height = options.outHeight;
            if (width > height) {
                reqWidth = getWindowManager().getDefaultDisplay().getWidth();
                reqHeight = (reqWidth * height) / width;
            } else {
                reqHeight = getWindowManager().getDefaultDisplay().getHeight();
                reqWidth = (width * reqHeight) / height;
            }
            int inSampleSize = 1;
            if (height > reqHeight || width > reqWidth) {
                final int halfHeight = height / 2;
                final int halfWidth = width / 2;
                while ((halfHeight / inSampleSize) > reqHeight
                        && (halfWidth / inSampleSize) > reqWidth) {
                    inSampleSize *= 2;
                }
            }
            try {
                options.inSampleSize = inSampleSize;
                options.inJustDecodeBounds = false;
                float scaleX = (float) reqWidth / (float) (width / inSampleSize);
                float scaleY = (float) reqHeight / (float) (height / inSampleSize);
                Matrix mat = new Matrix();
                mat.postScale(scaleX, scaleY);
                Bitmap bitmap = BitmapFactory.decodeFile(path, options);
                ExifInterface ei = new ExifInterface(path);
                int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        mat.postRotate(90);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        mat.postRotate(180);
                        break;
                }
                ImageView imageView = (ImageView) findViewById(R.id.image);
                imageView.setImageBitmap(Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), mat, true));
            } catch (IOException e) {
                Toast.makeText(this, getString(R.string.chat_image_preview_load_err), Toast.LENGTH_SHORT).show();
            }
        } else {
            finish();
        }
    }

    private String getFileSize(long size) {
        StringBuilder strSize = new StringBuilder();
        if (size < 1024) {
            strSize.append(size).append("B");
        } else if (size < 1024 * 1024) {
            strSize.append(size / 1024).append("K");
        } else {
            strSize.append(size / 1024 / 1024).append("M");
        }
        return strSize.toString();
    }
}
