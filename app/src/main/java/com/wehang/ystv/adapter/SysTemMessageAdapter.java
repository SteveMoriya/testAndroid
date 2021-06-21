package com.wehang.ystv.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.wehang.ystv.R;
import com.wehang.ystv.bo.SysMessage;
import com.wehang.ystv.bo.UserInfo;

import java.util.ArrayList;
import java.util.List;

public class SysTemMessageAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater inflater;
	private List<SysMessage> conversations;
	private List<UserInfo> userList = new ArrayList<UserInfo>();

	public SysTemMessageAdapter(Context context, List<SysMessage> eMConversations) {
		super();
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		this.conversations = eMConversations;
	}

	@Override
	public int getCount() {
		if (conversations.size()==0){return 0;}else return conversations.size();
	}

	@Override
	public SysMessage getItem(int position) {
		return conversations.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public List<SysMessage> getConversations() {
		return conversations;
	}

	public List<UserInfo> getUserList() {
		return userList;
	}

	public void setUserList(List<UserInfo> userList) {
		this.userList = userList;
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.item_system, null);
			/*holder.headImg = (ImageView) convertView.findViewById(R.id.headImg);
			holder.isVipImg = (ImageView) convertView.findViewById(R.id.isVipImg);
			holder.sexImg = (ImageView) convertView.findViewById(R.id.sexImg);
			holder.nickNameTv = (TextView) convertView.findViewById(R.id.nickNameTv);
			holder.lastMsgTv = (TextView) convertView.findViewById(R.id.lastMsgTv);
			holder.levelImg = (ImageView) convertView.findViewById(R.id.levelImg);
			holder.timeTv = (TextView) convertView.findViewById(R.id.timeTv);
			holder.unreadTv = (TextView) convertView.findViewById(R.id.unreadTv);*/
			holder.timeTv= (TextView) convertView.findViewById(R.id.time);
			holder.message= (TextView) convertView.findViewById(R.id.message);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		SysMessage data=conversations.get(position);
			holder.timeTv.setText(data.createTime+"");
			holder.message.setText(data.content+"");
		return convertView;
	}

	private class ViewHolder {
		private ImageView headImg, isVipImg, sexImg, levelImg;
		private TextView nickNameTv;
		private TextView lastMsgTv;
		private TextView unreadTv, timeTv,message;
	}
}
