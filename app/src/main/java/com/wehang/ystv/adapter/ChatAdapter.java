package com.wehang.ystv.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.blankj.utilcode.util.LogUtils;
import com.tencent.TIMConversationType;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;
import com.tencent.imcore.FriendshipManager;
import com.wehang.ystv.R;
import com.wehang.ystv.VideoApplication;
import com.wehang.ystv.bo.Message;
import com.wehang.ystv.bo.UserInfo;
import com.wehang.ystv.interfaces.UrlConstant;
import com.wehang.ystv.logic.UsersProfilePresenter;
import com.wehang.ystv.ui.AddLiveActivity;
import com.wehang.ystv.ui.UserHomeActivty;
import com.wehang.ystv.utils.GlideUtils;
import com.wehang.ystv.utils.UserLoginInfo;
import com.wehang.ystv.widget.MyChatInput;
import com.whcd.base.utils.DisplayUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 聊天界面adapter
 */
public class ChatAdapter extends ArrayAdapter<Message> {

    private final String TAG = "ChatAdapter";

    private int resourceId;
    private View view;
    private ViewHolder viewHolder;
    private int type=-1;
    private MyChatInput input;


    /**
     * Constructor
     *
     * @param context  The current context.
     * @param resource The resource ID for a layout file containing a TextView to use when
     *                 instantiating views.
     * @param objects  The objects to represent in the ListView.
     */
    public ChatAdapter(Context context, int resource, List<Message> objects) {
        super(context, resource, objects);
        resourceId = resource;
    }
    public ChatAdapter(Context context, int resource, List<Message> objects,int type,MyChatInput input) {
        super(context, resource, objects);
        resourceId = resource;
        this.type=type;
        this.input=input;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView != null) {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        } else {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.leftAvatar = (ImageView) view.findViewById(R.id.leftAvatar);
            viewHolder.rightAvatar = (ImageView) view.findViewById(R.id.rightAvatar);
            viewHolder.leftMessage = (RelativeLayout) view.findViewById(R.id.leftMessage);
            viewHolder.rightMessage = (RelativeLayout) view.findViewById(R.id.rightMessage);
            viewHolder.leftPanel = (RelativeLayout) view.findViewById(R.id.leftPanel);
            viewHolder.rightPanel = (RelativeLayout) view.findViewById(R.id.rightPanel);
            viewHolder.sending = (ProgressBar) view.findViewById(R.id.sending);
            viewHolder.error = (ImageView) view.findViewById(R.id.sendError);
            viewHolder.sender = (TextView) view.findViewById(R.id.sender);
            viewHolder.rightDesc = (TextView) view.findViewById(R.id.rightDesc);
            viewHolder.systemMessage = (TextView) view.findViewById(R.id.systemMessage);
            viewHolder.toTolkSomeOne=view.findViewById(R.id.toTolkSomeOne);
            viewHolder.isVipImg=view.findViewById(R.id.isVipImg);
            viewHolder.isVipImgMy=view.findViewById(R.id.isVipImgMy);
            view.setTag(viewHolder);
        }

        if (position < getCount()) {
            final Message data = getItem(position);
            viewHolder.rightAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(getContext(), UserHomeActivty.class);
                    Bundle bundle=new Bundle();
                    UserInfo userInfo= UserLoginInfo.getUserInfo();
                    bundle.putSerializable("data",userInfo);
                    intent.putExtra("bundle",bundle);
                    getContext().startActivity(intent);
                }
            });


            viewHolder. isVipImgMy.setVisibility(View.GONE);
            viewHolder.isVipImg.setVisibility(View.GONE);
            if (data.getMessage().isSelf()){
                if (UserLoginInfo.getUserInfo().isVip == 0) {
                    viewHolder. isVipImgMy.setVisibility(View.INVISIBLE);
                } else {
                    viewHolder. isVipImgMy.setVisibility(View.VISIBLE);
                }
            }else{
                if (data.getMessage().getConversation().getType()==TIMConversationType.C2C){
                    TIMUserProfile mTIMUserProfile = UsersProfilePresenter.getInstance().getUsersProfile(data.getMessage().getSender());
                    if (mTIMUserProfile==null){
                        String b;
                        if (TextUtils.isEmpty(mTIMUserProfile.getBirthday()+"")){
                            b="0";
                        }else {
                            b= String.valueOf(mTIMUserProfile.getBirthday());
                        }

                        int intVip= Integer.parseInt(b);
                        if (intVip == 0) {
                            viewHolder.isVipImg.setVisibility(View.INVISIBLE);
                        } else {
                            viewHolder.isVipImg.setVisibility(View.VISIBLE);
                        }
                    }else {
                        String userId= data.getMessage().getSender();
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
                                    String b = String.valueOf(res.getBirthday());
                                    if (b.equals("")) {
                                        b = "0";
                                    }
                                    int intVip= Integer.parseInt(b);
                                    if (intVip == 0) {
                                        viewHolder.isVipImg.setVisibility(View.INVISIBLE);
                                    } else {
                                        viewHolder.isVipImg.setVisibility(View.VISIBLE);
                                    }
                                }
                            }
                        });
                    }

                }

                if (type!=-1){
                    viewHolder.toTolkSomeOne.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            LogUtils.i("onItemClick","!!!");
                            UserInfo userInfo=new UserInfo();
                            userInfo.userId=data.getMessage().getSender();
                            userInfo.name=data.getMessage().getSenderProfile().getNickName();
                            UserLoginInfo.wantToTolk=userInfo;
                            String string="@" + userInfo.name + " :";
                            input.setText(string);
                            input.editText.setSelection(string.length());
                        }
                    });
                }
                if (data.getMessage().getConversation().getType()==TIMConversationType.Group){
                    viewHolder.leftAvatar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent=new Intent(getContext(), UserHomeActivty.class);
                            Bundle bundle=new Bundle();
                            UserInfo userInfo= new UserInfo();
                            userInfo.userId=data.getMessage().getSender();;
                            bundle.putSerializable("data",userInfo);
                            intent.putExtra("bundle",bundle);
                            getContext().startActivity(intent);
                        }
                    });
                }else {
                    final String id= data.getMessage().getSender();
                    viewHolder.leftAvatar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent=new Intent(getContext(), UserHomeActivty.class);
                            Bundle bundle=new Bundle();
                            UserInfo userInfo= new UserInfo();
                            userInfo.userId=id;
                            bundle.putSerializable("data",userInfo);
                            intent.putExtra("bundle",bundle);
                            getContext().startActivity(intent);
                        }
                    });
                }
            }

            data.showMessage(viewHolder, getContext());

            viewHolder.toTolkSomeOne.setHeight(viewHolder.leftMessage.getHeight());
            viewHolder.toTolkSomeOne.setWidth(viewHolder.leftMessage.getWidth());
        }
        return view;
    }


    public class ViewHolder {
        public ImageView leftAvatar;
        public ImageView rightAvatar;
        public RelativeLayout leftMessage;
        public RelativeLayout rightMessage;
        public RelativeLayout leftPanel;
        public RelativeLayout rightPanel;
        public ProgressBar sending;
        public ImageView error;
        public TextView sender;
        public TextView systemMessage;
        public TextView rightDesc;
        public String imgPath;
        public TextView toTolkSomeOne;
        public ImageView isVipImg,isVipImgMy;
    }

}
