package com.wehang.ystv.bo;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.tencent.TIMCustomElem;
import com.tencent.TIMMessage;
import com.wehang.ystv.VideoApplication;
import com.wehang.ystv.adapter.ChatAdapter;
import com.wehang.ystv.interfaces.UrlConstant;
import com.wehang.ystv.utils.GlideUtils;
import com.wehang.ystv.utils.UserLoginInfo;
import com.whcd.base.utils.DisplayUtils;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * 自定义消息
 */
public class CustomMessage extends Message {


    private String TAG = getClass().getSimpleName();

    private final int TYPE_TYPING = 14;
    private final int TYPE_GIFT = 15;

    private Type type;
    private String desc;
    private String data;

    public CustomMessage(TIMMessage message) {
        this.message = message;
        TIMCustomElem elem = (TIMCustomElem) message.getElement(0);
        parse(elem.getData());

    }

    public CustomMessage(Type type) {
        message = new TIMMessage();
        String data = "";
        JSONObject dataJson = new JSONObject();
        try {
            switch (type) {
                case TYPING:
                    dataJson.put("userAction", TYPE_TYPING);
                    dataJson.put("actionParam", "EIMAMSG_InputStatus_Ing");
                    data = dataJson.toString();
            }
        } catch (JSONException e) {
            Log.e(TAG, "generate json error");
        }
        TIMCustomElem elem = new TIMCustomElem();
        elem.setData(data.getBytes());
        message.addElement(elem);
    }

    public CustomMessage(String giftPic) {
        message = new TIMMessage();
        String data = "";
        JSONObject dataJson = new JSONObject();
        try {
            dataJson.put("userAction", TYPE_GIFT);
            dataJson.put("giftPic", giftPic);
            data = dataJson.toString();
        } catch (JSONException e) {
            Log.e(TAG, "generate json error");
        }
        TIMCustomElem elem = new TIMCustomElem();
        elem.setData(data.getBytes());
        message.addElement(elem);
    }


    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    private void parse(byte[] data) {
        type = Type.INVALID;
        try {
            String str = new String(data, "UTF-8");
            JSONObject jsonObj = new JSONObject(str);
            int action = jsonObj.getInt("userAction");
            switch (action) {
                case TYPE_TYPING:
                    type = Type.TYPING;
                    this.data = jsonObj.getString("actionParam");
                    if (this.data.equals("EIMAMSG_InputStatus_End")) {
                        type = Type.INVALID;
                    }
                    break;
                case TYPE_GIFT:
                    type = Type.GIFT;
                    this.data = jsonObj.getString("giftPic");
                    break;
            }

        } catch (IOException | JSONException e) {
            Log.e(TAG, "parse json error");

        }
    }

    /**
     * 显示消息
     *
     * @param viewHolder 界面样式
     * @param context    显示消息的上下文
     */
    @Override
    public void showMessage(ChatAdapter.ViewHolder viewHolder, Context context) {
        if (type == Type.GIFT) {
            clearView(viewHolder);
            ImageView imageView = new ImageView(VideoApplication.instance);
            imageView.setLayoutParams(new RelativeLayout.LayoutParams(
                    DisplayUtils.dip2px(VideoApplication.instance, 100), DisplayUtils.dip2px(VideoApplication.instance, 100)));
            if (message.isSelf()) {
                GlideUtils.loadHeadImage(context, UrlConstant.IP + UserLoginInfo.getUserInfo().iconUrl, viewHolder.rightAvatar);
            } else {
                if (UrlConstant.headPic.contains("http://") || UrlConstant.headPic.contains("https://")) {
                    GlideUtils.loadHeadImage(context, UrlConstant.headPic, viewHolder.leftAvatar);
                } else {
                    GlideUtils.loadHeadImage(context, UrlConstant.IP + UrlConstant.headPic, viewHolder.leftAvatar);
                }
            }
            if (!TextUtils.isEmpty(data)) {
                GlideUtils.loadBaseImage(context, "http://112.74.218.80" + data, imageView);
                getBubbleView(viewHolder).addView(imageView);
                showStatus(viewHolder);
            }
        }
    }

    /**
     * 获取消息摘要
     */
    @Override
    public String getSummary() {
        return null;
    }

    /**
     * 保存消息或消息文件
     */
    @Override
    public void save() {

    }

    public enum Type {
        TYPING,
        INVALID,
        GIFT
    }
}
