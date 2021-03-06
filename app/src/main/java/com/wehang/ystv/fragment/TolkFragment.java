package com.wehang.ystv.fragment;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.blankj.utilcode.util.LogUtils;
import com.google.gson.reflect.TypeToken;
import com.tencent.TIMAddFriendRequest;
import com.tencent.TIMConversation;
import com.tencent.TIMConversationType;
import com.tencent.TIMFriendResult;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMManager;
import com.tencent.TIMMessage;
import com.tencent.TIMMessageDraft;
import com.tencent.TIMMessageStatus;
import com.tencent.TIMTextElem;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;
import com.wehang.ystv.R;
import com.wehang.ystv.adapter.ChatAdapter;
import com.wehang.ystv.adapter.LiveAdapter;
import com.wehang.ystv.adapter.LiveVideoChartFragmentAdapter;
import com.wehang.ystv.bo.CustomMessage;
import com.wehang.ystv.bo.FileMessage;
import com.wehang.ystv.bo.GiftInfo;
import com.wehang.ystv.bo.GroupInfo;
import com.wehang.ystv.bo.ImageMessage;
import com.wehang.ystv.bo.Lives;
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
import com.wehang.ystv.interfaces.result.GetLvesResult;
import com.wehang.ystv.logic.ChatPresenter;
import com.wehang.ystv.logic.ChatRoomInfo;
import com.wehang.ystv.ui.ChatActivity;
import com.wehang.ystv.ui.ImagePreviewActivity;
import com.wehang.ystv.ui.LiveWatchNewActivity;
import com.wehang.ystv.ui.RechargeActivity;
import com.wehang.ystv.utils.FileUtil;
import com.wehang.ystv.utils.MediaUtil;
import com.wehang.ystv.utils.RecorderUtil;
import com.wehang.ystv.utils.ToastUtil;
import com.wehang.ystv.utils.UserLoginInfo;
import com.wehang.ystv.widget.MyChatInput;
import com.whcd.base.fragment.BaseFragment;
import com.whcd.base.interfaces.BaseData;
import com.whcd.base.interfaces.BaseResult;
import com.whcd.base.interfaces.HttpTool;
import com.whcd.base.widget.PageListView;
import com.whcd.base.widget.TopMenuBar;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static com.tencent.TIMConversationType.C2C;
import static com.tencent.TIMConversationType.Group;

/**
 * A simple {@link Fragment} subclass.
 */
public class TolkFragment extends BaseFragment implements ChatView {
  /*  public ListView conversationListView;
    public LiveVideoChartFragmentAdapter conversationAdapter;*/
    private LiveWatchNewActivity activity;
    public String name="TolkFragment";



    private static final String TAG = "ChatActivity";
    private TopMenuBar topMenuBar;
    private List<Message> messageList = new ArrayList<>();
    private ChatAdapter adapter;
    private ListView listView;
    public ChatPresenter presenter;
    private MyChatInput input;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final int IMAGE_STORE = 200;
    private static final int FILE_CODE = 300;
    private static final int IMAGE_PREVIEW = 400;
    private static final int ACCOUNT_RECHARGE = 500;
    private Uri fileUri;
    private VoiceSendingView voiceSendingView;
    private String identify;
    private String nickName;
    private String headPic;
    private RecorderUtil recorder = new RecorderUtil();
    private TIMConversationType type;
    private String titleStr;
    private Handler handler = new Handler();

/*    public static void navToChat(Context context, String identify, String nickName, String headPic, TIMConversationType type) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra("identify", identify);
        intent.putExtra("nickName", nickName);
        intent.putExtra("headPic", headPic);
        intent.putExtra("type", type);
        context.startActivity(intent);

    }*/
private String tag="addFriend";
private void addFriend(){
//??????????????????
    List<TIMAddFriendRequest> reqList = new ArrayList<TIMAddFriendRequest>();

//??????????????????
    TIMAddFriendRequest req = new TIMAddFriendRequest();
    req.setAddrSource("DemoApp");
    req.setAddWording("add me");
    req.setIdentifier(identify);
    req.setRemark(nickName);
    reqList.add(req);


    //??????????????????
    TIMFriendshipManager.getInstance().addFriend(reqList, new TIMValueCallBack<List<TIMFriendResult>>() {
        @Override
        public void onError(int code, String desc){
            //?????????code???????????????desc????????????????????????????????????
            //?????????code???????????????????????????
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity= (LiveWatchNewActivity) context;
        LogUtils.i("TolkRoomFragment",activity.mLiveRoomId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        identify = activity.mChatRoomId;
        nickName = "";
        headPic = "";
        UrlConstant.headPic = headPic;
        type = TIMConversationType.Group;
        return inflater.inflate(R.layout.layout1, container, false);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initview();
    }
    private void initview(){
        /*conversationListView = (ListView) getView().findViewById(com.wehang.ystv.R.id.list_view);
        conversationAdapter = new LiveVideoChartFragmentAdapter(getActivity());
        conversationListView.setAdapter(conversationAdapter);*/
        presenter = new ChatPresenter(this, identify, type);
        input = (MyChatInput) getView().findViewById(R.id.input_panel);
        input.setChatView(this);
        if (type == Group) {
            input.hideGift();
        }
        adapter = new ChatAdapter(getActivity(), R.layout.item_message, messageList,1,input);
        listView = (ListView) getView().findViewById(R.id.list);
        listView.setAdapter(adapter);
        listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        input.setInputMode(MyChatInput.InputMode.NONE);
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
                    //????????????????????????????????????
                    presenter.getMessage(messageList.size() > 0 ? messageList.get(0).getMessage() : null);

                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                firstItem = firstVisibleItem;
            }
        });
        registerForContextMenu(listView);
      /*  listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                LogUtils.i("onItemClick",i+""+adapter.getItem(i).getMessage().getSender()+":"+adapter.getItem(i).getMessage().getSenderProfile().getNickName());


                if (adapter.getItem(i).getMessage().getSender().equals(UserLoginInfo.getUserInfo().userId)){

            }else {
                UserInfo userInfo=new UserInfo();
                userInfo.userId=adapter.getItem(i).getMessage().getSender();
                userInfo.name=adapter.getItem(i).getMessage().getSenderProfile().getNickName();
                UserLoginInfo.wantToTolk=userInfo;
                input.setText("@" + userInfo.name + " :");
            }

        }
        });*/

        voiceSendingView = (VoiceSendingView)getView(). findViewById(R.id.voice_sending);
        presenter.start();
    }

    private void initTitlber(){
        List<String> list = new ArrayList<>();
        list.add(identify);
        switch (type) {
            case C2C:
                break;
            case Group:
               // topMenuBar.setTitle();
                GroupInfo.getInstance().getGroupName(activity.mChatRoomId);
                LogUtils.i("roomName",GroupInfo.getInstance().getGroupName(activity.mChatRoomId));
                break;

        }
    }
    @Override
    public void onPause() {
        super.onPause();
        //??????????????????????????????????????????????????????
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
    public void onDestroy() {
        super.onDestroy();
        clearAllMessage();
        presenter.stop();
        UrlConstant.headPic = "";

    }
    /**
     * ????????????
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
                            break;
                        case GIFT:

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
     * ????????????
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
     * ?????????????????????????????????
     */
    @Override
    public void clearAllMessage() {
        messageList.clear();
    }

    /**
     * ??????????????????
     *
     * @param message ???????????????
     */
    @Override
    public void onSendMessageSuccess(TIMMessage message) {
        showMessage(message);
    }

    /**
     * ??????????????????
     *
     * @param code ?????????
     * @param desc ????????????
     */
    @Override
    public void onSendMessageFail(int code, String desc, TIMMessage message) {
        long id = message.getMsgUniqueId();
        for (Message msg : messageList) {
            if (msg.getMessage().getMsgUniqueId() == id) {
                switch (code) {
                    case 80001:
                        //???????????????????????????
                        msg.setDesc(getString(R.string.chat_content_bad));
                        adapter.notifyDataSetChanged();
                        break;
                }
            }
        }

    }

    /**
     * ??????????????????
     */
    @Override
    public void sendImage() {
        Intent intent_album = new Intent("android.intent.action.GET_CONTENT");
        intent_album.setType("image/*");
        startActivityForResult(intent_album, IMAGE_STORE);
    }

    /**
     * ??????????????????
     */
    @Override
    public void sendPhoto() {
       /* Intent intent_photo = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent_photo.resolveActivity(getPackageManager()) != null) {
            File tempFile = FileUtil.getTempFile(FileUtil.FileType.IMG);
            if (tempFile != null) {
                fileUri = Uri.fromFile(tempFile);
            }
            intent_photo.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            startActivityForResult(intent_photo, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }*/
    }

    /**
     * ????????????
     */
    @Override
    public void sendGift(final GiftInfo giftInfo) {
    }


    /**
     * ??????
     */
    @Override
    public void recharge() {
    }

    /**
     * ??????????????????
     */
    @Override
    public void sendText() {
        if (TextUtils.isEmpty(input.getText())){
            ToastUtil.makeText(getActivity(),"??????????????????",Toast.LENGTH_SHORT).show();
            return;
        }
        if (UserLoginInfo.wantToTolk!=null){
            UserLoginInfo.wantToTolk=null;

       /*    String content=input.getText().toString().trim();
        String str="@" + UserLoginInfo.wantToTolk.name + " :";
            if (content.startsWith(str)){
//??????????????????
                String peer = UserLoginInfo.wantToTolk.userId;  //??????????????? "sample_user_1" ?????????
                TIMConversation conversation = TIMManager.getInstance().getConversation(
                        TIMConversationType.C2C,    //?????????????????????
                        peer);
                //??????????????????
                TIMMessage msg = new TIMMessage();

//??????????????????
                TIMTextElem elem = new TIMTextElem();
                String sendWhatStr=content.replace(str,"");
                if (TextUtils.isEmpty(sendWhatStr)) {
                    ToastUtil.makeText(getActivity(),"??????????????????", Toast.LENGTH_SHORT).show();
                    return;
                }
                elem.setText(sendWhatStr);

//???elem???????????????
                if(msg.addElement(elem) != 0) {
                    Log.d("@someOne", "addElement failed");
                    return;
                }

//????????????
                conversation.sendMessage(msg, new TIMValueCallBack<TIMMessage>() {//??????????????????
                    @Override
                    public void onError(int code, String desc) {//??????????????????
                        //?????????code???????????????desc????????????????????????????????????
                        //?????????code???????????????????????????
                        Log.d("@someOne", "send message failed. code: " + code + " errmsg: " + desc);
                    }

                    @Override
                    public void onSuccess(TIMMessage msg) {//??????????????????
                        Log.e("@someOne", "SendMsg ok");
                    }
                });
                UserLoginInfo.wantToTolk=null;
                input.setText("");
                return;
            }*/
        }


        Message message = new TextMessage(input.getText());
        presenter.sendMessage(message.getMessage());
       /* activity.mTCChatRoomMgr.sendTextMessage(input.getText()+"");*/

        android.os.Message msg = android.os.Message.obtain();
        SpannableStringBuilder stringBuilder= (SpannableStringBuilder) input.getText();
        ChatRoomInfo chatRoomInfo = new ChatRoomInfo(1, stringBuilder,activity. mineInfo.userId,activity. mineInfo.name, activity.mineInfo.iconUrl,activity. mineInfo.level,activity. mineInfo.isVip, "", "", "", "", "0");
        msg.what = 0;
        msg.obj = chatRoomInfo;
        activity. msgHandler.sendMessage(msg);
        input.setText("");


    }

    /**
     * ????????????
     */
    @Override
    public void sendFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, FILE_CODE);
    }


    /**
     * ????????????????????????
     */
    @Override
    public void startSendVoice() {
        voiceSendingView.setVisibility(View.VISIBLE);
        voiceSendingView.showRecording();
        recorder.startRecording();

    }

    /**
     * ????????????????????????
     */
    @Override
    public void endSendVoice() {
       /* voiceSendingView.release();
        voiceSendingView.setVisibility(View.GONE);
        recorder.stopRecording();
        if (recorder.getTimeInterval() < 1) {
            ToastUtil.makeText(this, getResources().getString(R.string.chat_audio_too_short), Toast.LENGTH_SHORT).show();
        } else {
            Message message = new VoiceMessage(recorder.getTimeInterval(), recorder.getFilePath());
            presenter.sendMessage(message.getMessage());
        }*/
    }

    /**
     * ?????????????????????
     *
     * @param fileName ?????????
     */
    @Override
    public void sendVideo(String fileName) {
        Message message = new VideoMessage(fileName);
        presenter.sendMessage(message.getMessage());
    }


    /**
     * ????????????????????????
     */
    @Override
    public void cancelSendVoice() {

    }

    /**
     * ????????????
     */
    @Override
    public void sending() {
        if (type == C2C) {
            Message message = new CustomMessage(CustomMessage.Type.TYPING);
            presenter.sendOnlineMessage(message.getMessage());
        }
    }

    /**
     * ????????????
     */
    @Override
    public void showDraft(TIMMessageDraft draft) {
        input.getText().append(TextMessage.getString(draft.getElems(), getActivity()));
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK && fileUri != null) {
                showImagePreview(fileUri.getPath());
            }
        } else if (requestCode == IMAGE_STORE) {
            if (resultCode == RESULT_OK && data != null) {
                showImagePreview(FileUtil.getFilePath(getActivity(), data.getData()));
            }

        } else if (requestCode == FILE_CODE) {
            if (resultCode == RESULT_OK) {
                sendFile(FileUtil.getFilePath(getActivity(), data.getData()));
            }
        } else if (requestCode == IMAGE_PREVIEW) {
            if (resultCode == RESULT_OK) {
                boolean isOri = data.getBooleanExtra("isOri", false);
                String path = data.getStringExtra("path");
                File file = new File(path);
                if (file.exists() && file.length() > 0) {
                    if (file.length() > 1024 * 1024 * 10) {
                        ToastUtil.makeText(getActivity(), getString(R.string.chat_file_too_large), Toast.LENGTH_SHORT).show();
                    } else {
                        Message message = new ImageMessage(path, isOri);
                        presenter.sendMessage(message.getMessage());
                    }
                } else {
                    ToastUtil.makeText(getActivity(), getString(R.string.chat_file_not_exist), Toast.LENGTH_SHORT).show();
                }
            }
        } else if (requestCode == ACCOUNT_RECHARGE) {
            input.showDiamond();
        }
    }


    private void showImagePreview(String path) {
        if (path == null) return;
        Intent intent = new Intent(getActivity(), ImagePreviewActivity.class);
        intent.putExtra("path", path);
        startActivityForResult(intent, IMAGE_PREVIEW);
    }

    private void sendFile(String path) {
        /*if (path == null) return;
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
        }*/

    }

}
