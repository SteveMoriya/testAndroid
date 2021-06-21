package com.wehang.ystv.component.imui;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.wehang.ystv.R;
import com.wehang.ystv.bo.GiftInfo;
import com.wehang.ystv.bo.UserInfo;
import com.wehang.ystv.component.emojicon.CenterImageSpan;
import com.wehang.ystv.component.emojicon.DefaultEmojiconDatas;
import com.wehang.ystv.component.emojicon.Emojicon;
import com.wehang.ystv.component.emojicon.EmojiconGroupEntity;
import com.wehang.ystv.component.emojicon.EmojiconMenu;
import com.wehang.ystv.component.emojicon.EmojiconMenuBase;
import com.wehang.ystv.interfaces.ChatView;
import com.wehang.ystv.ui.ChatActivity;
import com.wehang.ystv.utils.UserLoginInfo;
import com.wehang.ystv.widget.VideoGiftsView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 聊天界面输入控件
 */
public class ChatInput extends RelativeLayout implements TextWatcher, View.OnClickListener {
    float y1 = 0;
    float y2 = 0;
    private Context mContext;
    private static final String TAG = "ChatInput";
    private ImageButton btnAdd, btnSend, btnVoice, btnKeyboard, btnEmotion;
    private LinearLayout btnGift;
    private EditText editText;
    private boolean isSendVisible, isHoldVoiceBtn, isEmoticonReady;
    private InputMode inputMode = InputMode.NONE;
    private ChatView chatView;
    private LinearLayout morePanel, textPanel;
    private TextView voicePanel;
    private RelativeLayout emoticonPanel;
    private RelativeLayout giftPanel;
    private EmojiconMenuBase emojiconMenu;
    private final int REQUEST_CODE_ASK_PERMISSIONS = 100;
    private List<EmojiconGroupEntity> emojiconGroupList;
    //礼物赠送
    private VideoGiftsView mVideoGiftsView;
    private LinearLayout chargeLlyt;
    private TextView leftDiamondTv, sendTv;
    private List<List<GiftInfo>> pageGiftList;
    private GiftInfo selectGiftInfo;

    public ChatInput(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.chat_input, this);
        initView();
    }

    private void initView() {
        textPanel = (LinearLayout) findViewById(R.id.text_panel);
        btnAdd = (ImageButton) findViewById(R.id.btn_add);
        btnAdd.setOnClickListener(this);
        btnSend = (ImageButton) findViewById(R.id.btn_send);
        btnSend.setOnClickListener(this);
        btnVoice = (ImageButton) findViewById(R.id.btn_voice);
        btnVoice.setOnClickListener(this);
        btnEmotion = (ImageButton) findViewById(R.id.btnEmoticon);
        btnEmotion.setOnClickListener(this);
        btnGift = (LinearLayout) findViewById(R.id.btn_gift);
        btnGift.setOnClickListener(this);
        morePanel = (LinearLayout) findViewById(R.id.morePanel);
        LinearLayout BtnImage = (LinearLayout) findViewById(R.id.btn_photo);
        BtnImage.setOnClickListener(this);
        LinearLayout BtnPhoto = (LinearLayout) findViewById(R.id.btn_image);
        BtnPhoto.setOnClickListener(this);
        LinearLayout btnVideo = (LinearLayout) findViewById(R.id.btn_video);
        btnVideo.setOnClickListener(this);
        LinearLayout btnFile = (LinearLayout) findViewById(R.id.btn_file);
        btnFile.setOnClickListener(this);
        setSendBtn();
        btnKeyboard = (ImageButton) findViewById(R.id.btn_keyboard);
        btnKeyboard.setOnClickListener(this);
        voicePanel = (TextView) findViewById(R.id.voice_panel);
        voicePanel.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //当手指按下的时候
                        y1 = event.getY();
                        isHoldVoiceBtn = true;
                        updateVoiceView();
                        break;
                    case MotionEvent.ACTION_UP:
                        //当手指离开的时候

                        y2 = event.getY();
                        if(y1 - y2 > 50) {
                            chatView.cancelSendVoice();
                            voicePanel.setText(getResources().getString(R.string.chat_press_talk));
                            voicePanel.setBackgroundResource(R.drawable.btn_voice_normal);
                        }else {
                            isHoldVoiceBtn = false;
                            updateVoiceView();
                        }

                        break;
                }
                return true;
            }
        });
        editText = (EditText) findViewById(R.id.input);
        editText.addTextChangedListener(this);
        editText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    updateView(InputMode.TEXT);
                }
            }
        });
        isSendVisible = editText.getText().length() != 0;
        emoticonPanel = (RelativeLayout) findViewById(R.id.emoticonPanel);
        giftPanel = (RelativeLayout) findViewById(R.id.giftPanel);
        mVideoGiftsView = (VideoGiftsView) findViewById(R.id.giftsPagerView);
        chargeLlyt = (LinearLayout) findViewById(R.id.chargeLlyt);
        chargeLlyt.setOnClickListener(this);
        leftDiamondTv = (TextView) findViewById(R.id.leftDiamondTv);
        leftDiamondTv.setText(String.valueOf(UserLoginInfo.getUserInfo().diamond));
        sendTv = (TextView) findViewById(R.id.sendTv);
        sendTv.setOnClickListener(this);

        mVideoGiftsView.setClickedItem(new VideoGiftsView.clickedItem() {
            @Override
            public void getGiftInfo(GiftInfo giftInfo) {
                selectGiftInfo = giftInfo;
            }
        });
        // 表情栏，没有自定义的用默认的
        if (emojiconMenu == null) {
            emojiconMenu = (EmojiconMenu) LayoutInflater.from(mContext).inflate(R.layout.im_layout_emojicon_menu, null);
            if (emojiconGroupList == null) {
                emojiconGroupList = new ArrayList<>();
                emojiconGroupList.add(new EmojiconGroupEntity(R.drawable.f001, Arrays.asList(DefaultEmojiconDatas.getData())));
            }
            ((EmojiconMenu) emojiconMenu).init(emojiconGroupList);
        }
        emoticonPanel.addView(emojiconMenu);

    }

    /**
     * 初始化礼物信息
     */
    public void initGift(List<GiftInfo> giftList) {
        this.pageGiftList = new ArrayList<>();
        List<GiftInfo> list = null;
        if (giftList != null && giftList.size() > 0) {
            for (int i = 0; i < giftList.size(); i++) {
                if (i % 8 == 0) {
                    if (list != null) {
                        pageGiftList.add(list);
                    }
                    list = new ArrayList<>();
                }
                list.add(giftList.get(i));
            }
            if (list != null && list.size() > 0) {
                pageGiftList.add(list);
            }
            mVideoGiftsView.initData(pageGiftList);
        }
    }

    /**
     * 显示剩余钻石数
     *
     */
    public void showDiamond() {
        UserInfo mineInfo = UserLoginInfo.getUserInfo();
        leftDiamondTv.setText(String.valueOf(mineInfo.diamond));
    }

    public void hideGift() {
        btnGift.setVisibility(GONE);
    }

    //    public TextView getDiamondTv() {
//        return leftDiamondTv;
//    }
//
    public VideoGiftsView getVideoGiftsView() {
        return mVideoGiftsView;
    }

    private void updateView(InputMode mode) {
        if (mode == inputMode) return;
        leavingCurrentState();
        switch (inputMode = mode) {
            case MORE:
                morePanel.setVisibility(VISIBLE);
                break;
            case TEXT:
                if (editText.requestFocus()) {
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
                }
                break;
            case VOICE:
                voicePanel.setVisibility(VISIBLE);
                textPanel.setVisibility(GONE);
                btnVoice.setVisibility(GONE);
                btnKeyboard.setVisibility(VISIBLE);
                break;
            case EMOTICON:
                if (!isEmoticonReady) {
                    prepareEmoticon();
                }
                emoticonPanel.setVisibility(VISIBLE);
                break;
            case GIFT:
                giftPanel.setVisibility(VISIBLE);
                break;
        }
    }

    private void leavingCurrentState() {
        switch (inputMode) {
            case TEXT:
                View view = ((Activity) getContext()).getCurrentFocus();
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                editText.clearFocus();
                break;
            case MORE:
                morePanel.setVisibility(GONE);
                break;
            case VOICE:
                voicePanel.setVisibility(GONE);
                textPanel.setVisibility(VISIBLE);
                btnVoice.setVisibility(VISIBLE);
                btnKeyboard.setVisibility(GONE);
                break;
            case EMOTICON:
                emoticonPanel.setVisibility(GONE);
            case GIFT:
                giftPanel.setVisibility(GONE);
                break;
        }
    }


    private void updateVoiceView() {
        if (isHoldVoiceBtn) {
            voicePanel.setText(getResources().getString(R.string.chat_release_send));
            voicePanel.setBackgroundResource(R.drawable.btn_voice_pressed);
            chatView.startSendVoice();
        } else {
            voicePanel.setText(getResources().getString(R.string.chat_press_talk));
            voicePanel.setBackgroundResource(R.drawable.btn_voice_normal);
            chatView.endSendVoice();
        }
    }


    /**
     * 关联聊天界面逻辑
     */
    public void setChatView(ChatView chatView) {
        this.chatView = chatView;
    }

    /**
     * This method is called to notify you that, within <code>s</code>,
     * the <code>count</code> characters beginning at <code>start</code>
     * are about to be replaced by new text with length <code>after</code>.
     * It is an error to attempt to make changes to <code>s</code> from
     * this callback.
     *
     * @param s
     * @param start
     * @param count
     * @param after
     */
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    /**
     * This method is called to notify you that, within <code>s</code>,
     * the <code>count</code> characters beginning at <code>start</code>
     * have just replaced old text that had length <code>before</code>.
     * It is an error to attempt to make changes to <code>s</code> from
     * this callback.
     *
     * @param s
     * @param start
     * @param before
     * @param count
     */
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        isSendVisible = s != null && s.length() > 0;
        setSendBtn();
        if (isSendVisible) {
            chatView.sending();
        }
    }

    /**
     * This method is called to notify you that, somewhere within
     * <code>s</code>, the text has been changed.
     * It is legitimate to make further changes to <code>s</code> from
     * this callback, but be careful not to get yourself into an infinite
     * loop, because any changes you make will cause this method to be
     * called again recursively.
     * (You are not told where the change took place because other
     * afterTextChanged() methods may already have made other changes
     * and invalidated the offsets.  But if you need to know here,
     * you can use {@link Spannable#setSpan} in {@link #onTextChanged}
     * to mark your place and then look up from here where the span
     * ended up.
     *
     * @param s
     */
    @Override
    public void afterTextChanged(Editable s) {

    }

    private void setSendBtn() {
        if (isSendVisible) {
            btnAdd.setVisibility(GONE);
            btnSend.setVisibility(VISIBLE);
        } else {
            btnAdd.setVisibility(VISIBLE);
            btnSend.setVisibility(GONE);
        }
    }

    private void prepareEmoticon() {
        if (emoticonPanel == null) {
            return;
        }
        // emojicon menu
        emojiconMenu.setEmojiconMenuListener(new EmojiconMenuBase.EaseEmojiconMenuListener() {
            @Override
            public void onExpressionClicked(Emojicon emojicon) {
                if (emojicon.getType() != Emojicon.Type.BIG_EXPRESSION) {
                    String content = String.valueOf(emojicon.getIdentityCode());
                    SpannableString str = new SpannableString(String.valueOf(emojicon.getIdentityCode()));
                    CenterImageSpan span = new CenterImageSpan(getContext(), drawableToBitmap(emojicon.getIcon()));
                    str.setSpan(span, 0, content.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    editText.append(str);
                } else {
                    if (!TextUtils.isEmpty(editText.getText())) {
                        KeyEvent event = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
                        editText.dispatchKeyEvent(event);
                    }
                }
            }

            @Override
            public void onDeleteImageClicked() {
                if (!TextUtils.isEmpty(editText.getText())) {
                    KeyEvent event = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
                    editText.dispatchKeyEvent(event);
                }
            }
        });
        isEmoticonReady = true;
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        Activity activity = (Activity) getContext();
        int id = v.getId();
        if (id == R.id.btn_send) {
            chatView.sendText();
        }
        if (id == R.id.btn_add) {
            updateView(inputMode == InputMode.MORE ? InputMode.TEXT : InputMode.MORE);
        }
        if (id == R.id.btn_photo) {
            if (activity != null && requestCamera(activity)) {
                chatView.sendPhoto();
            }
        }
        if (id == R.id.btn_image) {
            if (activity != null && requestStorage(activity)) {
                chatView.sendImage();
            }
        }
        if (id == R.id.btn_gift) {
            updateView(inputMode == InputMode.GIFT ? InputMode.TEXT : InputMode.GIFT);
        }
        if (id == R.id.btn_voice) {
            if (activity != null && requestAudio(activity)) {
                updateView(InputMode.VOICE);
            }
        }
        if (id == R.id.btn_keyboard) {
            updateView(InputMode.TEXT);
        }
        if (id == R.id.btn_video) {
            if (getContext() instanceof FragmentActivity) {
                FragmentActivity fragmentActivity = (FragmentActivity) getContext();
                if (requestVideo(fragmentActivity)) {
                    VideoInputDialog.show(fragmentActivity.getSupportFragmentManager());
                }
            }
        }
        if (id == R.id.btnEmoticon) {


            updateView(inputMode == InputMode.EMOTICON ? InputMode.TEXT : InputMode.EMOTICON);
            if (inputMode == InputMode.EMOTICON){
                btnEmotion.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_keyboard_input));
            }else {
                btnEmotion.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_face_input));
            }
        }
        if (id == R.id.btn_file) {
            chatView.sendFile();
        }
        if (id == R.id.sendTv) {
            if (selectGiftInfo != null) {
                chatView.sendGift(selectGiftInfo);
            }
        }
        if (id == R.id.chargeLlyt) {
            chatView.recharge();
        }
    }

    private Bitmap drawableToBitmap(int resId) {
        Drawable drawable = mContext.getResources().getDrawable(resId);
        BitmapDrawable bd = (BitmapDrawable) drawable;
        Bitmap bitMap = bd.getBitmap();
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(0.8f, 0.8f);
        // 得到新的图片
        Bitmap newBitMap = Bitmap.createBitmap(bitMap, 0, 0, bitMap.getWidth(), bitMap.getHeight(), matrix, true);
        return newBitMap;
    }


    /**
     * 获取输入框文字
     */
    public Editable getText() {
        return editText.getText();
    }

    /**
     * 设置输入框文字
     */
    public void setText(String text) {
        editText.setText(text);
    }


    /**
     * 设置输入模式
     */
    public void setInputMode(InputMode mode) {
        updateView(mode);
    }


    public enum InputMode {
        TEXT,
        VOICE,
        EMOTICON,
        GIFT,
        MORE,
        VIDEO,
        NONE,
    }

    private boolean requestVideo(Activity activity) {
        if (afterM()) {
            final List<String> permissionsList = new ArrayList<>();
            if ((activity.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED))
                permissionsList.add(Manifest.permission.CAMERA);
            if ((activity.checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED))
                permissionsList.add(Manifest.permission.RECORD_AUDIO);
            if (permissionsList.size() != 0) {
                activity.requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                        REQUEST_CODE_ASK_PERMISSIONS);
                return false;
            }
            int hasPermission = activity.checkSelfPermission(Manifest.permission.CAMERA);
            if (hasPermission != PackageManager.PERMISSION_GRANTED) {
                activity.requestPermissions(new String[]{Manifest.permission.CAMERA},
                        REQUEST_CODE_ASK_PERMISSIONS);
                return false;
            }
        }
        return true;
    }

    private boolean requestCamera(Activity activity) {
        if (afterM()) {
            int hasPermission = activity.checkSelfPermission(Manifest.permission.CAMERA);
            if (hasPermission != PackageManager.PERMISSION_GRANTED) {
                activity.requestPermissions(new String[]{Manifest.permission.CAMERA},
                        REQUEST_CODE_ASK_PERMISSIONS);
                return false;
            }
        }
        return true;
    }

    private boolean requestAudio(Activity activity) {
        if (afterM()) {
            int hasPermission = activity.checkSelfPermission(Manifest.permission.RECORD_AUDIO);
            if (hasPermission != PackageManager.PERMISSION_GRANTED) {
                activity.requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO},
                        REQUEST_CODE_ASK_PERMISSIONS);
                return false;
            }
        }
        return true;
    }

    private boolean requestStorage(Activity activity) {
        if (afterM()) {
            int hasPermission = activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (hasPermission != PackageManager.PERMISSION_GRANTED) {
                activity.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CODE_ASK_PERMISSIONS);
                return false;
            }
        }
        return true;
    }

    private boolean afterM() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

}
