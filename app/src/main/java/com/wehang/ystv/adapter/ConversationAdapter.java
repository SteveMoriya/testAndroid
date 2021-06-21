package com.wehang.ystv.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.wehang.ystv.R;
import com.wehang.ystv.bo.Conversation;
import com.wehang.ystv.bo.NomalConversation;
import com.wehang.ystv.bo.Province;
import com.wehang.ystv.bo.YsMessage;
import com.wehang.ystv.interfaces.UrlConstant;
import com.wehang.ystv.utils.GlideUtils;
import com.wehang.ystv.utils.TimeUtil;

import java.util.List;

/**
 * 会话界面adapter
 */
public class ConversationAdapter extends ArrayAdapter<Conversation> {

    private Context mContext;
    private int resourceId;
    private View view;
    private ViewHolder viewHolder;
    private List<Conversation> objects;
    private List<YsMessage>ysMessages;

    /**
     * Constructor
     *
     * @param context  The current context.
     * @param resource The resource ID for a layout file containing a TextView to use when
     *                 instantiating views.
     * @param objects  The objects to represent in the ListView.
     */
    public ConversationAdapter(Context context, int resource, List<Conversation> objects,List<YsMessage>ysMessages) {
        super(context, resource, objects);
        mContext=context;
        resourceId = resource;
        this.ysMessages=ysMessages;
        this.objects=objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView != null){
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }else{
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.tvName = (TextView) view.findViewById(R.id.name);
            viewHolder.avatar = (ImageView) view.findViewById(R.id.avatar);
            viewHolder.lastMessage = (TextView) view.findViewById(R.id.last_message);
            viewHolder.time = (TextView) view.findViewById(R.id.message_time);
            viewHolder.unread = (TextView) view.findViewById(R.id.unread_num);
            viewHolder.isVipImg=view.findViewById(R.id.isVipImg);
            viewHolder.sexImg=view.findViewById(R.id.sexImg);
            view.setTag(viewHolder);
        }
        if (position<3){
            viewHolder.sexImg.setVisibility(View.GONE);
            viewHolder.unread.setVisibility(View.INVISIBLE);
            if (position==0){
                viewHolder.avatar.setImageResource(R.drawable.icon_xtxx);
                viewHolder.tvName.setText("系统消息");
                viewHolder.isVipImg.setVisibility(View.GONE);
                if (ysMessages.size()>0){
                    viewHolder.lastMessage.setText(ysMessages.get(0).content);
                    viewHolder.time.setText(ysMessages.get(0).time);
                }
                return view;
            }else if (position==1){
                viewHolder.avatar.setImageResource(R.drawable.icon_tiwen);
                viewHolder.tvName.setText("收到的提问");
                viewHolder.isVipImg.setVisibility(View.GONE);

                if (ysMessages.size()>0){
                    viewHolder.lastMessage.setText(ysMessages.get(1).content);
                    viewHolder.time.setText(ysMessages.get(1).time);
                }
                return view;
            }else if (position==2){
                viewHolder.avatar.setImageResource(R.drawable.icon_huida);
                viewHolder.tvName.setText("收到的回答");
                viewHolder.isVipImg.setVisibility(View.GONE);

                if (ysMessages.size()>0){
                    viewHolder.lastMessage.setText(ysMessages.get(2).content);
                    viewHolder.time.setText(ysMessages.get(2).time);
                }
                return view;
            }
        }else {
            final Conversation data = getItem(position-3);
            viewHolder.tvName.setText(data.getName());
            Log.i("caonim",data.getName());
            GlideUtils.loadHeadImage(mContext, UrlConstant.IP+data.getAvatar(), viewHolder.avatar);
            viewHolder.lastMessage.setText(data.getLastMessageSummary());
            viewHolder.time.setText(TimeUtil.getTimeStr(data.getLastMessageTime()));
            long unRead = data.getUnreadNum();
            if (unRead <= 0){
                viewHolder.unread.setVisibility(View.INVISIBLE);
            }else{
                viewHolder.unread.setVisibility(View.VISIBLE);
                String unReadStr = String.valueOf(unRead);
                if (unRead < 10){
                    viewHolder.unread.setBackgroundResource(R.drawable.point1);
                }else{
                    viewHolder.unread.setBackgroundResource(R.drawable.point22);
                    if (unRead > 99){
                        unReadStr = getContext().getResources().getString(R.string.time_more);
                    }
                }
                viewHolder.unread.setText(unReadStr);
            }
            int sex=-1,isVip=-1;
            if (data instanceof NomalConversation){
                sex=((NomalConversation) data).sex;
                isVip=((NomalConversation) data).isvip;
            }
            if (sex==0){
                viewHolder.sexImg.setVisibility(View.VISIBLE);
                viewHolder.sexImg.setImageResource(R.drawable.icon_home_g);
            }else if (sex==1){
                viewHolder.sexImg.setVisibility(View.VISIBLE);
                viewHolder.sexImg.setImageResource(R.drawable.icon_home_b);
            }
            if (isVip==0){
                viewHolder.isVipImg.setVisibility(View.GONE);
            }else if (isVip==1){
                viewHolder.isVipImg.setVisibility(View.VISIBLE);
            }
        }


        return view;
    }
    @Override
    public int getCount() {
        return objects.size()+3;
    }

    public class ViewHolder{
        public TextView tvName;
        public ImageView avatar;
        public TextView lastMessage;
        public TextView time;
        public TextView unread;
        public ImageView isVipImg,sexImg;

    }
}
