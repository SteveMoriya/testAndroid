package com.wehang.ystv.bo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.tencent.TIMConversationType;
import com.tencent.TIMElem;
import com.tencent.TIMFaceElem;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMMessage;
import com.tencent.TIMMessageDraft;
import com.tencent.TIMTextElem;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;
import com.wehang.ystv.R;
import com.wehang.ystv.VideoApplication;
import com.wehang.ystv.adapter.ChatAdapter;
import com.wehang.ystv.component.emojicon.CenterImageSpan;
import com.wehang.ystv.interfaces.UrlConstant;
import com.wehang.ystv.utils.CommonUtil;
import com.wehang.ystv.utils.GlideUtils;
import com.wehang.ystv.utils.UserLoginInfo;
import com.wehang.ystv.utils.Utils;


import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.tencent.TIMElemType.Text;

/**
 * 文本消息数据
 */
public class TextMessage extends Message {

    public TextMessage(TIMMessage message) {
        this.message = message;
    }

    public TextMessage(String s) {
        message = new TIMMessage();
        TIMTextElem elem = new TIMTextElem();
        elem.setText(s);
        message.addElement(elem);
    }

    public TextMessage(TIMMessageDraft draft) {
        message = new TIMMessage();
        for (TIMElem elem : draft.getElems()) {
            message.addElement(elem);
        }
    }

    private List<ImageSpan> sortByIndex(final Editable editInput, ImageSpan[] array) {
        ArrayList<ImageSpan> sortList = new ArrayList<>();
        for (ImageSpan span : array) {
            sortList.add(span);
        }
        Collections.sort(sortList, new Comparator<ImageSpan>() {
            @Override
            public int compare(ImageSpan lhs, ImageSpan rhs) {
                return editInput.getSpanStart(lhs) - editInput.getSpanStart(rhs);
            }
        });

        return sortList;
    }

    public TextMessage(Editable s) {
        message = new TIMMessage();
        ImageSpan[] spans = s.getSpans(0, s.length(), ImageSpan.class);
        List<ImageSpan> listSpans = sortByIndex(s, spans);
        int currentIndex = 0;
        for (ImageSpan span : listSpans) {
            int startIndex = s.getSpanStart(span);
            int endIndex = s.getSpanEnd(span);
            if (currentIndex < startIndex) {
                TIMTextElem textElem = new TIMTextElem();
                textElem.setText(s.subSequence(currentIndex, startIndex).toString());
                message.addElement(textElem);
            }
            TIMFaceElem faceElem = new TIMFaceElem();
            int index = Integer.parseInt(s.subSequence(startIndex, endIndex).toString());
            faceElem.setIndex(index);
            faceElem.setData("[表情]".getBytes(Charset.forName("UTF-8")));
            message.addElement(faceElem);
            currentIndex = endIndex;
        }
        if (currentIndex < s.length()) {
            TIMTextElem textElem = new TIMTextElem();
            textElem.setText(s.subSequence(currentIndex, s.length()).toString());
            message.addElement(textElem);
        }

    }


    /**
     * 在聊天界面显示消息
     *
     * @param viewHolder 界面样式
     * @param context    显示消息的上下文
     */
    @Override
    public void showMessage(final ChatAdapter.ViewHolder viewHolder, final Context context) {
        clearView(viewHolder);
        try {
            boolean hasText = false;
            TextView tv = new TextView(VideoApplication.instance);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            tv.setTextColor(VideoApplication.instance.getResources().getColor(isSelf() ? R.color.white : R.color.black));
            List<TIMElem> elems = new ArrayList<>();
            for (int i = 0; i < message.getElementCount(); ++i) {
                elems.add(message.getElement(i));
                if (message.getElement(i).getType() == Text) {
                    hasText = true;
                }
            }
            SpannableStringBuilder stringBuilder = getString(elems, context);
            if (!hasText) {
                stringBuilder.insert(0, " ");
            }
            if (message.isSelf()) {
                GlideUtils.loadHeadImage(context, UrlConstant.IP + UserLoginInfo.getUserInfo().iconUrl, viewHolder.rightAvatar);
                LogUtils.i("chattxurl",UrlConstant.IP + UserLoginInfo.getUserInfo().iconUrl);
            } else {
                if (UrlConstant.headPic.contains("http://") || UrlConstant.headPic.contains("https://")) {
                    GlideUtils.loadHeadImage(context, UrlConstant.headPic, viewHolder.leftAvatar);
                } else {
                    if (message.getConversation().getType()== TIMConversationType.C2C){
                        GlideUtils.loadHeadImage(context, UrlConstant.IP + UrlConstant.headPic, viewHolder.leftAvatar);
                    }else {
                        String userId= message.getSenderGroupMemberProfile().getUser();
                        // TIMManager.getInstance().initFriendshipSettings(11111111,null);
                        //待获取用户资料的用户列表
                        List<String> users = new ArrayList<String>();
                        users.add(userId);

                        //获取用户资料
                        TIMFriendshipManager.getInstance().getUsersProfile(users, new TIMValueCallBack<List<TIMUserProfile>>(){
                            @Override
                            public void onError(int code, String desc){
                                //错误码code和错误描述desc，可用于定位请求失败原因
                                //错误码code列表请参见错误码表
                                LogUtils.e("getUsersProfile", "getUsersProfile failed: " + code + " desc");
                            }

                            @Override
                            public void onSuccess(List<TIMUserProfile> result){
                                LogUtils.e("getUsersProfile", "getUsersProfile succ");
                                for(TIMUserProfile res : result){
                                    LogUtils.e("getUsersProfile", "identifier: " + res.getIdentifier() + " nickName: " + res.getNickName()
                                            + " remark: " + res.getRemark());
                                    String iconUrl=res.getFaceUrl();
                                    GlideUtils.loadHeadImage(context, UrlConstant.IP + iconUrl, viewHolder.leftAvatar);
                                    viewHolder.sender.setText(res.getNickName());
                                    String b = String.valueOf(res.getBirthday());
                                    if (b.equals("")) {
                                        b = "0";
                                    }
                                    int intVip= Integer.parseInt(b);
                                    if (intVip == 0) {
                                        viewHolder. isVipImg.setVisibility(View.INVISIBLE);
                                    } else {
                                        viewHolder. isVipImg.setVisibility(View.VISIBLE);
                                    }
                                }
                            }
                        });

                    }

                }
            }
            LogUtils.i("stringBuilder",stringBuilder);
            tv.setText(stringBuilder);
            getBubbleView(viewHolder).addView(tv);
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
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < message.getElementCount(); ++i) {
            switch (message.getElement(i).getType()) {
                case Face:
                    TIMFaceElem faceElem = (TIMFaceElem) message.getElement(i);
                    byte[] data = faceElem.getData();
                    if (data != null) {
                        result.append(new String(data, Charset.forName("UTF-8")));
                    }
                    break;
                case Text:
                    TIMTextElem textElem = (TIMTextElem) message.getElement(i);
                    result.append(textElem.getText());
                    break;
            }

        }
        return result.toString();
    }

    /**
     * 保存消息或消息文件
     */
    @Override
    public void save() {

    }

    private static int getNumLength(int n) {
        return String.valueOf(n).length();
    }


    public static SpannableStringBuilder getString(List<TIMElem> elems, Context context) {
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder();
        for (int i = 0; i < elems.size(); ++i) {
            switch (elems.get(i).getType()) {
                case Face:
                    TIMFaceElem faceElem = (TIMFaceElem) elems.get(i);
                    int startIndex = stringBuilder.length();
                    String resName;
                    int index = faceElem.getIndex() + 1;
                    if (index < 10) {
                        resName = "f00" + index;
                    } else if (index < 100) {
                        resName = "f0" + index;
                    } else {
                        resName = "f" + index;
                    }
                    Bitmap bitmap = Utils.drawableToBitamp(CommonUtil.getResourceId(resName));
                    Matrix matrix = new Matrix();
                    int width = bitmap.getWidth();
                    int height = bitmap.getHeight();
                    matrix.postScale(1, 1);
                    Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                            width, height, matrix, true);
                    CenterImageSpan span = new CenterImageSpan(context, resizedBitmap);
                    stringBuilder.append(String.valueOf(faceElem.getIndex()));
                    stringBuilder.setSpan(span, startIndex, startIndex + getNumLength(faceElem.getIndex()), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    break;
                case Text:
                    TIMTextElem textElem = (TIMTextElem) elems.get(i);
                    stringBuilder.append(textElem.getText());
                    break;
            }

        }
        return stringBuilder;
    }


}
