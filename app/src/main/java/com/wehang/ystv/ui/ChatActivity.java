package com.wehang.ystv.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.blankj.utilcode.util.LogUtils;
import com.google.gson.reflect.TypeToken;
import com.tencent.TIMAddFriendRequest;
import com.tencent.TIMConversationType;
import com.tencent.TIMFaceElem;
import com.tencent.TIMFriendResult;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMMessage;
import com.tencent.TIMMessageDraft;
import com.tencent.TIMMessageStatus;
import com.tencent.TIMTextElem;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;
import com.wehang.ystv.R;
import com.wehang.ystv.VideoApplication;
import com.wehang.ystv.adapter.ChatAdapter;
import com.wehang.ystv.bo.CustomMessage;
import com.wehang.ystv.bo.FileMessage;
import com.wehang.ystv.bo.GiftInfo;
import com.wehang.ystv.bo.GroupInfo;
import com.wehang.ystv.bo.ImageMessage;
import com.wehang.ystv.bo.Message;
import com.wehang.ystv.bo.MessageFactory;
import com.wehang.ystv.bo.TextMessage;
import com.wehang.ystv.bo.UserInfo;
import com.wehang.ystv.bo.VideoMessage;
import com.wehang.ystv.bo.VoiceMessage;
import com.wehang.ystv.component.imui.ChatInput;
import com.wehang.ystv.component.imui.VoiceSendingView;
import com.wehang.ystv.interfaces.ChatView;
import com.wehang.ystv.interfaces.UrlConstant;
import com.wehang.ystv.interfaces.result.GetGiftResult;
import com.wehang.ystv.logic.ChatPresenter;
import com.wehang.ystv.utils.EmojiUtils;
import com.wehang.ystv.utils.FileUtil;
import com.wehang.ystv.utils.IsHaveEmoji;
import com.wehang.ystv.utils.MediaUtil;
import com.wehang.ystv.utils.RecorderUtil;
import com.wehang.ystv.utils.ToastUtil;
import com.wehang.ystv.utils.UserLoginInfo;
import com.whcd.base.activity.AlbumActivity;
import com.whcd.base.activity.BaseActivity;
import com.whcd.base.activity.CropImageActivity;
import com.whcd.base.interfaces.BaseData;
import com.whcd.base.interfaces.BaseResult;
import com.whcd.base.interfaces.HttpTool;
import com.whcd.base.utils.ImageUtils;
import com.whcd.base.widget.TopMenuBar;


import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static com.tencent.TIMConversationType.C2C;
import static com.tencent.TIMConversationType.Group;
import static com.whcd.base.activity.AlbumActivity.TAKE_PHOTO_REQUEST_CODE;
import static com.whcd.base.utils.ImageUtils.takePhoto;

/**
 * @author 钟林宏 2017/1/19 11:46
 * @version V1.0
 * @ProjectName: ToLiveTencentIM
 * @Copyright: 2015 WHCD Co., Ltd. All Right Reserved.
 * @address:
 * @date: 2017/1/19 11:46
 * @Description: 本内容仅限于公司内部使用，禁止转发.
 * @instructions:聊天页面
 * @modificationHistory=========================重大变更说明
 * @modify by user: 钟林宏 2017/1/19
 */

public class ChatActivity extends BaseActivity implements ChatView {


    private static final String TAG = "ChatActivity";
    private TopMenuBar topMenuBar;
    private List<Message> messageList = new ArrayList<>();
    private ChatAdapter adapter;
    private ListView listView;
    private ChatPresenter presenter;
    private ChatInput input;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final int IMAGE_STORE = 200;
    private static final int FILE_CODE = 300;
    private static final int IMAGE_PREVIEW = 400;
    private static final int ACCOUNT_RECHARGE = 500;
    private Uri fileUri;
    private String capturePath;
    private VoiceSendingView voiceSendingView;
    private String identify;
    private String nickName;
    private String headPic;
    private RecorderUtil recorder = new RecorderUtil();
    private TIMConversationType type;
    private String titleStr;
    private Handler handler = new Handler();

    public static void navToChat(Context context, String identify, String nickName, String headPic, TIMConversationType type) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra("identify", identify);
        intent.putExtra("nickName", nickName);
        intent.putExtra("headPic", headPic);
        intent.putExtra("type", type);
        context.startActivity(intent);

    }
    private void addFriend(){
//创建请求列表
        List<TIMAddFriendRequest> reqList = new ArrayList<TIMAddFriendRequest>();

//添加好友请求
        TIMAddFriendRequest req = new TIMAddFriendRequest();
        req.setAddrSource("DemoApp");
        req.setAddWording("add me");
        req.setIdentifier(identify);
        req.setRemark(nickName);
        reqList.add(req);
        //申请添加好友
        TIMFriendshipManager.getInstance().addFriend(reqList, new TIMValueCallBack<List<TIMFriendResult>>() {
            @Override
            public void onError(int code, String desc){
                //错误码code和错误描述desc，可用于定位请求失败原因
                //错误码code列表请参见错误码表
                Log.e(tag, "addFriend failed: " + code + " desc");
            }

            @Override
            public void onSuccess(List<TIMFriendResult> result){
                Log.e(tag, "addFriend succ");
                for(TIMFriendResult res : result){
                    Log.e(tag, "identifier: " + res.getIdentifer() + " status: " + res.getStatus());
                }
            }
        });
    }
    private String tag="addFriend";
    @Override
    protected int getLayoutResId() {
        identify = getIntent().getStringExtra("identify");
        nickName = getIntent().getStringExtra("nickName");
        headPic = getIntent().getStringExtra("headPic");
        UrlConstant.headPic = headPic;
        type = (TIMConversationType) getIntent().getSerializableExtra("type");
        return R.layout.activity_chat;
    }

    @Override
    protected void initTitleBar() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        topMenuBar = (TopMenuBar) findViewById(R.id.topMenuBar);
        titleStr = nickName;
        topMenuBar.setTitle(nickName);
        Button leftButton = topMenuBar.getLeftButton();
        leftButton.setOnClickListener(this);
        topMenuBar.hideRightButton();
        List<String> list = new ArrayList<>();
        list.add(identify);
        switch (type) {
            case C2C:
                if (TextUtils.isEmpty(headPic)) {
                    TIMFriendshipManager.getInstance().getUsersProfile(list, new TIMValueCallBack<List<TIMUserProfile>>() {
                        @Override
                        public void onError(int i, String s) {
                            topMenuBar.setTitle(titleStr = identify);
                            LogUtils.i("getUsersProfile","e;"+i+";"+s);

                        }

                        @Override
                        public void onSuccess(List<TIMUserProfile> timUserProfiles) {
                            if (timUserProfiles != null && timUserProfiles.size() > 0) {
                                topMenuBar.setTitle(titleStr = timUserProfiles.get(0) == null ? identify : timUserProfiles.get(0).getNickName());
                                headPic = timUserProfiles.get(0) == null ? "" : timUserProfiles.get(0).getFaceUrl();
                                UrlConstant.headPic = headPic;
                                adapter.notifyDataSetChanged();
                            }
                            LogUtils.i("getUsersProfile","s;");
                        }
                    });
                }
                break;
            case Group:
                topMenuBar.setTitle(GroupInfo.getInstance().getGroupName(identify));
                break;

        }
    }

    @Override
    protected void initView() {
        presenter = new ChatPresenter(this, identify, type);
        input = (ChatInput) findViewById(R.id.input_panel);
        input.setChatView(this);
        if (type == Group) {
            input.hideGift();
        }
        adapter = new ChatAdapter(this, R.layout.item_message, messageList);
        listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(adapter);
        listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        input.setInputMode(ChatInput.InputMode.NONE);
                        break;
                }
                return false;
            }
        });
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {

            private int firstItem;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && firstItem == 0) {
                    //如果拉到顶端读取更多消息
                    presenter.getMessage(messageList.size() > 0 ? messageList.get(0).getMessage() : null);

                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                firstItem = firstVisibleItem;
            }
        });
        registerForContextMenu(listView);

        voiceSendingView = (VoiceSendingView) findViewById(R.id.voice_sending);
        presenter.start();
    }

    @Override
    protected void initData(Bundle bundle) {

        //loadGiftList();
        //addFriend();
    }

    /**
     * 获取礼物列表
     *
     * @author 包川 2016-7-10 下午10:app:06
     * @modificationHistory=========================方法变更说明
     * @modify by user: 包川 2016-7-10
     * @modify by reason: 原因
     */
    private void loadGiftList() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageNo", "1");
        params.put("pageSize", "100");
        HttpTool.doPost(VideoApplication.applicationContext, UrlConstant.GIFTLIST, params, false, new TypeToken<BaseResult<GetGiftResult>>() {
        }.getType(), new HttpTool.OnResponseListener() {
            @Override
            public void onSuccess(BaseData data) {
                GetGiftResult result = (GetGiftResult) data;
                if (result != null) {
                    input.initGift(result.gifts);
                }
            }

            @Override
            public void onError(int errorCode) {
                UserLoginInfo.loginOverdue(context, errorCode);
            }
        });
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.title_btn_left:
                back();
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //退出聊天界面时输入框有内容，保存草稿
        if (input.getText().length() > 0) {
            TextMessage message = new TextMessage(input.getText());
            presenter.saveDraft(message.getMessage());
        } else {
            presenter.saveDraft(null);
        }
//        RefreshEvent.getInstance().onRefresh();
        presenter.readMessages();
        MediaUtil.getInstance().stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.stop();
        UrlConstant.headPic = "";
    }


    /**
     * 显示消息
     *
     * @param message
     */
    @Override
    public void showMessage(TIMMessage message) {

        if (message == null) {
            adapter.notifyDataSetChanged();
        } else {
            Message mMessage = MessageFactory.getMessage(message);
            if (mMessage != null) {
                if (mMessage instanceof CustomMessage) {
                    CustomMessage.Type messageType = ((CustomMessage) mMessage).getType();
                    switch (messageType) {
                        case TYPING:
                            topMenuBar.setTitle(getString(R.string.chat_typing));
                            handler.removeCallbacks(resetTitle);
                            handler.postDelayed(resetTitle, 3000);
                            break;
                        case GIFT:
                            if (messageList.size() == 0) {
                                mMessage.setHasTime(null);
                            } else {
                                mMessage.setHasTime(messageList.get(messageList.size() - 1).getMessage());
                            }
                            messageList.add(mMessage);
                            adapter.notifyDataSetChanged();
                            listView.setSelection(adapter.getCount() - 1);
                            break;
                        default:
                            break;
                    }
                } else {
                    if (messageList.size() == 0) {
                        mMessage.setHasTime(null);
                    } else {
                        mMessage.setHasTime(messageList.get(messageList.size() - 1).getMessage());
                    }
                    messageList.add(mMessage);
                    adapter.notifyDataSetChanged();
                    listView.setSelection(adapter.getCount() - 1);
                }

            }
        }

    }

    /**
     * 显示消息
     *
     * @param messages
     */
    @Override
    public void showMessage(List<TIMMessage> messages) {
        int newMsgNum = 0;
        for (int i = 0; i < messages.size(); ++i) {
            Message mMessage = MessageFactory.getMessage(messages.get(i));
            if (mMessage == null || messages.get(i).status() == TIMMessageStatus.HasDeleted)
                continue;
            if (mMessage instanceof CustomMessage) {
                switch (((CustomMessage) mMessage).getType()) {
                    case TYPING:
                        continue;
                    case INVALID:
                        continue;
                    default:
                        break;
                }
            }
            ++newMsgNum;
            if (i != messages.size() - 1) {
                mMessage.setHasTime(messages.get(i + 1));
                messageList.add(0, mMessage);
            } else {
                mMessage.setHasTime(null);
                messageList.add(0, mMessage);
            }
        }
        adapter.notifyDataSetChanged();
        listView.setSelection(newMsgNum);
    }

    /**
     * 清除所有消息，等待刷新
     */
    @Override
    public void clearAllMessage() {
        messageList.clear();
    }

    /**
     * 发送消息成功
     *
     * @param message 返回的消息
     */
    @Override
    public void onSendMessageSuccess(TIMMessage message) {
        showMessage(message);
    }

    /**
     * 发送消息失败
     *
     * @param code 返回码
     * @param desc 返回描述
     */
    @Override
    public void onSendMessageFail(int code, String desc, TIMMessage message) {
        long id = message.getMsgUniqueId();
        for (Message msg : messageList) {
            if (msg.getMessage().getMsgUniqueId() == id) {
                switch (code) {
                    case 80001:
                        //发送内容包含敏感词
                        msg.setDesc(getString(R.string.chat_content_bad));
                        adapter.notifyDataSetChanged();
                        break;
                }
            }
        }

    }

    /**
     * 发送图片消息
     */
    @Override
    public void sendImage() {
       /* Intent intent_album = new Intent("android.intent.action.GET_CONTENT");
        intent_album.setType("image*//*");
        startActivityForResult(intent_album, IMAGE_STORE);*/
        if (ActivityCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) != 0) {
            ActivityCompat.requestPermissions(context, new String[]{READ_EXTERNAL_STORAGE}, 1);
        } else {
            Intent  intent = new Intent(context, AlbumActivity.class);
            intent.putExtra("type", 0);
            startActivityForResult(intent, AlbumActivity.TO_SELECT_PHOTO_REQUEST_CODE);
        }
    }

    /**
     * 发送照片消息
     */
    @Override
    public void sendPhoto() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != 0) {
            ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.CAMERA}, 0);
        } else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != 0) {
                ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
            } else {
                PackageManager packageManager = context.getPackageManager();
                if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                    capturePath= ImageUtils.takePhoto(this);
                }
            }
        }
    }

    /**
     * 赠送礼物
     */
    @Override
    public void sendGift(final GiftInfo giftInfo) {
        final UserInfo mineInfo = UserLoginInfo.getUserInfo();
        Map<String, String> params = new HashMap<String, String>();
        if (mineInfo != null) {
            params.put("token", mineInfo.token);
        }
        params.put("userId", identify);
        params.put("giftNo", String.valueOf(giftInfo.giftNo));
        HttpTool.doPost(context, UrlConstant.CONSUME, params, false, new TypeToken<BaseResult<BaseData>>() {
        }.getType(), new HttpTool.OnResponseListener() {
            @Override
            public void onSuccess(BaseData data) {
                if (type == C2C) {
                    Message message = new CustomMessage(giftInfo.giftPic);
                    presenter.sendMessage(message.getMessage());
                }
                mineInfo.diamond = mineInfo.diamond - giftInfo.diamond;
                UserLoginInfo.saveUserInfo(ChatActivity.this,mineInfo);
                input.showDiamond();
            }

            @Override
            public void onError(int errorCode) {
                ToastUtil.makeText(context, "礼物购买失败", Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * 充值
     */
    @Override
    public void recharge() {
        Intent intent = new Intent(context, RechargeActivity.class);
        startActivityForResult(intent, ACCOUNT_RECHARGE);
    }

    /**
     * 发送文本消息
     */
    @Override
    public void sendText() {

       LogUtils.i("infobiaoq", EmojiUtils.emoji2Unicode(input.getText()+""));
      // topMenuBar.setTitle(EmojiUtils.emoji2Unicode(input.getText()+""));
        Message message = new TextMessage(input.getText());

        presenter.sendMessage(message.getMessage());
        input.setText("");
    }

    /**
     * 发送文件
     */
    @Override
    public void sendFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, FILE_CODE);
    }


    /**
     * 开始发送语音消息
     */
    @Override
    public void startSendVoice() {
        voiceSendingView.setVisibility(View.VISIBLE);
        voiceSendingView.showRecording();
        recorder.startRecording();

    }

    /**
     * 结束发送语音消息
     */
    @Override
    public void endSendVoice() {
        voiceSendingView.release();
        voiceSendingView.setVisibility(View.GONE);
        recorder.stopRecording();
        if (recorder.getTimeInterval() < 1) {
            ToastUtil.makeText(this, getResources().getString(R.string.chat_audio_too_short), Toast.LENGTH_SHORT).show();
        } else {
            Message message = new VoiceMessage(recorder.getTimeInterval(), recorder.getFilePath());
            presenter.sendMessage(message.getMessage());
        }
    }

    /**
     * 发送小视频消息
     *
     * @param fileName 文件名
     */
    @Override
    public void sendVideo(String fileName) {
        Message message = new VideoMessage(fileName);
        presenter.sendMessage(message.getMessage());
    }


    /**
     * 结束发送语音消息
     */
    @Override
    public void cancelSendVoice() {
        voiceSendingView.release();
        voiceSendingView.setVisibility(View.GONE);
        recorder.stopRecording();
        ToastUtil.makeText(this, getResources().getString(R.string.chat_cancle), Toast.LENGTH_SHORT).show();
    }

    /**
     * 正在发送
     */
    @Override
    public void sending() {
        if (type == C2C) {
            Message message = new CustomMessage(CustomMessage.Type.TYPING);
            presenter.sendOnlineMessage(message.getMessage());
        }
    }

    /**
     * 显示草稿
     */
    @Override
    public void showDraft(TIMMessageDraft draft) {
        input.getText().append(TextMessage.getString(draft.getElems(), this));
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        Message message = messageList.get(info.position);
        menu.add(0, 1, Menu.NONE, getString(R.string.chat_del));
        if (message.isSendFail()) {
            menu.add(0, 2, Menu.NONE, getString(R.string.chat_resend));
        }
        if (message instanceof ImageMessage || message instanceof FileMessage) {
            menu.add(0, 3, Menu.NONE, getString(R.string.chat_save));
        }
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Message message = messageList.get(info.position);
        switch (item.getItemId()) {
            case 1:
                message.remove();
                messageList.remove(info.position);
                adapter.notifyDataSetChanged();
                break;
            case 2:
                messageList.remove(message);
                presenter.sendMessage(message.getMessage());
                break;
            case 3:
                message.save();
                break;
            default:
                break;
        }
        return super.onContextItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK && fileUri != null) {
                showImagePreview(fileUri.getPath());
            }
        } else if (requestCode == IMAGE_STORE) {
            if (resultCode == RESULT_OK && data != null) {
                showImagePreview(FileUtil.getFilePath(this, data.getData()));
            }

        } else if (requestCode == FILE_CODE) {
            if (resultCode == RESULT_OK) {
                sendFile(FileUtil.getFilePath(this, data.getData()));
            }
        } else if (requestCode == IMAGE_PREVIEW) {
            if (resultCode == RESULT_OK) {
                boolean isOri = data.getBooleanExtra("isOri", false);
                String path = data.getStringExtra("path");
                File file = new File(path);
                if (file.exists() && file.length() > 0) {
                    if (file.length() > 1024 * 1024 * 10) {
                        ToastUtil.makeText(this, getString(R.string.chat_file_too_large), Toast.LENGTH_SHORT).show();
                    } else {
                        Message message = new ImageMessage(path, isOri);
                        presenter.sendMessage(message.getMessage());
                    }
                } else {
                    ToastUtil.makeText(this, getString(R.string.chat_file_not_exist), Toast.LENGTH_SHORT).show();
                }
            }
        } else if (requestCode == ACCOUNT_RECHARGE) {
            input.showDiamond();
        }else if (requestCode==AlbumActivity.TO_SELECT_PHOTO_REQUEST_CODE){
            //选择图片回掉

            LogUtils.a("++++",1+data.getStringExtra("cropImagePath"));

            //sendFile(FileUtil.getFilePath(this, data.getData()));
            showImagePreview(data.getStringExtra("cropImagePath"));
        }else if (requestCode==AlbumActivity.TO_CROP_PHOTO_REQUEST_CODE){
            //裁剪完成回掉
           // data.getStringExtra("cropImagePath")
        }else if (requestCode==TAKE_PHOTO_REQUEST_CODE){
           /* //拍照回掉
            Intent intents = new Intent(context, CropImageActivity.class);
            intents.putExtra("path", capturePath);
            intents.putExtra("type", 0);
            intents.putExtra("cropWidth", 100);
            intents.putExtra("cropHeight", 100);
            startActivityForResult(intents, AlbumActivity.TO_CROP_PHOTO_REQUEST_CODE);*/
            showImagePreview(capturePath);
        }

    }


    private void showImagePreview(String path) {
        if (path == null) return;
        Intent intent = new Intent(this, ImagePreviewActivity.class);
        intent.putExtra("path", path);
        startActivityForResult(intent, IMAGE_PREVIEW);
    }

    private void sendFile(String path) {
        if (path == null) return;
        File file = new File(path);
        if (file.exists()) {
            if (file.length() > 1024 * 1024 * 10) {
                ToastUtil.makeText(this, getString(R.string.chat_file_too_large), Toast.LENGTH_SHORT).show();
            } else {
                Message message = new FileMessage(path);
                presenter.sendMessage(message.getMessage());
            }
        } else {
            ToastUtil.makeText(this, getString(R.string.chat_file_not_exist), Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * 将标题设置为对象名称
     */
    private Runnable resetTitle = new Runnable() {
        @Override
        public void run() {
            topMenuBar.setTitle(titleStr);
        }
    };


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 0:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != 0) {
                        ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                    } else {
                        capturePath= takePhoto(this);
                    }
                } else {
                    ToastUtil.makeText(context,"没有拍照权限", Toast.LENGTH_SHORT).show();
                }
                break;
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(context, AlbumActivity.class);
                    intent.putExtra("type", 0);
                    startActivityForResult(intent, AlbumActivity.TO_ALBUM_REQUEST_CODE);
                } else {
                    ToastUtil.makeText(context,"没有读取相册权限", Toast.LENGTH_SHORT).show();
                }
                break;
            case 2:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePhoto(this);
                } else {
                    ToastUtil.makeText(context,"没有保存文件权限", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


}
