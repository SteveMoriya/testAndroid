package com.wehang.ystv.bo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.tencent.TIMCallBack;
import com.tencent.TIMMessage;
import com.tencent.TIMSnapshot;
import com.tencent.TIMVideo;
import com.tencent.TIMVideoElem;
import com.wehang.ystv.R;
import com.wehang.ystv.VideoApplication;
import com.wehang.ystv.adapter.ChatAdapter;
import com.wehang.ystv.interfaces.UrlConstant;
import com.wehang.ystv.utils.FileUtil;
import com.wehang.ystv.utils.GlideUtils;
import com.wehang.ystv.utils.MediaUtil;
import com.wehang.ystv.utils.UserLoginInfo;


import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 小视频消息数据
 */
public class VideoMessage extends Message {

    private static final String TAG = "VideoMessage";


    public VideoMessage(TIMMessage message) {
        this.message = message;
    }

    public VideoMessage(String fileName) {
        message = new TIMMessage();
        TIMVideoElem elem = new TIMVideoElem();
        elem.setVideoPath(FileUtil.getCacheFilePath(fileName));
        Bitmap thumb = ThumbnailUtils.createVideoThumbnail(FileUtil.getCacheFilePath(fileName), MediaStore.Images.Thumbnails.MINI_KIND);
        elem.setSnapshotPath(FileUtil.createFile(thumb, new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date())));
        TIMSnapshot snapshot = new TIMSnapshot();
        snapshot.setType("PNG");
        snapshot.setHeight(thumb.getHeight());
        snapshot.setWidth(thumb.getWidth());
        TIMVideo video = new TIMVideo();
        video.setType("MP4");
        video.setDuaration(MediaUtil.getInstance().getDuration(FileUtil.getCacheFilePath(fileName)));
        elem.setSnapshot(snapshot);
        elem.setVideo(video);
        message.addElement(elem);
    }

    /**
     * 显示消息
     *
     * @param viewHolder 界面样式
     * @param context    显示消息的上下文
     */
    @Override
    public void showMessage(final ChatAdapter.ViewHolder viewHolder, final Context context) {
        clearView(viewHolder);
        try {
            if (message.isSelf()) {
                GlideUtils.loadHeadImage(context, UrlConstant.IP + UserLoginInfo.getUserInfo().iconUrl, viewHolder.rightAvatar);
            } else {
                if (UrlConstant.headPic.contains("http://") || UrlConstant.headPic.contains("https://")) {
                    GlideUtils.loadHeadImage(context, UrlConstant.headPic, viewHolder.leftAvatar);
                } else {
                    GlideUtils.loadHeadImage(context, UrlConstant.IP + UrlConstant.headPic, viewHolder.leftAvatar);
                }
            }
            final TIMVideoElem e = (TIMVideoElem) message.getElement(0);
            switch (message.status()) {
                case Sending:
                    showSnapshot(viewHolder, BitmapFactory.decodeFile(e.getSnapshotPath(), new BitmapFactory.Options()));
                    break;
                case SendSucc:

                    final TIMSnapshot snapshot = e.getSnapshotInfo();
                    if (FileUtil.isCacheFileExist(snapshot.getUuid())) {
                        showSnapshot(viewHolder, BitmapFactory.decodeFile(FileUtil.getCacheFilePath(snapshot.getUuid()), new BitmapFactory.Options()));
                    } else {
                        snapshot.getImage(FileUtil.getCacheFilePath(snapshot.getUuid()), new TIMCallBack() {
                            @Override
                            public void onError(int i, String s) {
                                Log.e(TAG, "get snapshot failed. code: " + i + " errmsg: " + s);
                            }

                            @Override
                            public void onSuccess() {
                                showSnapshot(viewHolder, BitmapFactory.decodeFile(FileUtil.getCacheFilePath(snapshot.getUuid()), new BitmapFactory.Options()));
                            }
                        });
                    }
                    final String fileName = e.getVideoInfo().getUuid();
                    if (!FileUtil.isCacheFileExist(fileName)) {
                        e.getVideoInfo().getVideo(FileUtil.getCacheFilePath(fileName), new TIMCallBack() {
                            @Override
                            public void onError(int i, String s) {
                                Log.e(TAG, "get video failed. code: " + i + " errmsg: " + s);
                            }

                            @Override
                            public void onSuccess() {
                                setVideoEvent(viewHolder, fileName, context);
                            }
                        });
                    } else {
                        setVideoEvent(viewHolder, fileName, context);
                    }
                    break;
            }

            showStatus(viewHolder);
        } catch (Exception e) {
            e.printStackTrace();
            clearView(viewHolder);
        }
    }

    /**
     * 获取消息摘要
     */
    @Override
    public String getSummary() {
        return VideoApplication.instance.getString(R.string.summary_video);
    }

    /**
     * 保存消息或消息文件
     */
    @Override
    public void save() {

    }


    /**
     * 显示缩略图
     */
    private void showSnapshot(final ChatAdapter.ViewHolder viewHolder, final Bitmap bitmap) {
        if (bitmap == null) return;
        ImageView imageView = new ImageView(VideoApplication.instance);
        imageView.setImageBitmap(bitmap);
        getBubbleView(viewHolder).addView(imageView);
    }

    private void showVideo(String path, Context context) {
        if (context == null) return;
//        Intent intent = new Intent(context, VideoActivity.class);
//        intent.putExtra("path", path);
//        context.startActivity(intent);
    }

    private void setVideoEvent(final ChatAdapter.ViewHolder viewHolder, final String fileName, final Context context) {
        getBubbleView(viewHolder).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showVideo(FileUtil.getCacheFilePath(fileName), context);
            }
        });
    }
}
