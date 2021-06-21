package com.wehang.ystv.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wehang.ystv.R;
import com.wehang.ystv.bo.QuestionMessages;
import com.wehang.ystv.bo.UserInfo;

import java.util.ArrayList;
import java.util.List;

public class AnswerAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater inflater;
	private List<QuestionMessages> conversations;
	private List<UserInfo> userList = new ArrayList<UserInfo>();

	public AnswerAdapter(Context context, List<QuestionMessages> eMConversations) {
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
	public QuestionMessages getItem(int position) {
		return conversations.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public List<QuestionMessages> getConversations() {
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
			convertView = inflater.inflate(R.layout.item_answer, null);
			/*holder.headImg = (ImageView) convertView.findViewById(R.id.headImg);
			holder.isVipImg = (ImageView) convertView.findViewById(R.id.isVipImg);
			holder.sexImg = (ImageView) convertView.findViewById(R.id.sexImg);
			holder.nickNameTv = (TextView) convertView.findViewById(R.id.nickNameTv);
			holder.lastMsgTv = (TextView) convertView.findViewById(R.id.lastMsgTv);
			holder.levelImg = (ImageView) convertView.findViewById(R.id.levelImg);
			holder.timeTv = (TextView) convertView.findViewById(R.id.timeTv);
			holder.unreadTv = (TextView) convertView.findViewById(R.id.unreadTv);*/
			holder.content= (TextView) convertView.findViewById(R.id.content);
			holder.message= (TextView) convertView.findViewById(R.id.message);
			holder.askTimeTv= (TextView) convertView.findViewById(R.id.askTime);
			holder.ansTimeTV= (TextView) convertView.findViewById(R.id.ansTime);
			holder.teacherName=convertView.findViewById(R.id.teacherName);
			holder.teacherAnsawer=convertView.findViewById(R.id.teacherAnsawer);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		QuestionMessages data=conversations.get(position);
		holder.content.setText(data.questionContent);
		holder.askTimeTv.setText(data.questionTime);
		holder.teacherName.setText(data.name+":");
		holder.teacherAnsawer.setText(data.answerContent);
		holder.ansTimeTV.setText(data.answerTime);
		return convertView;

	}

	private class ViewHolder {
		private ImageView headImg, isVipImg, sexImg, levelImg;
		private TextView nickNameTv;
		private TextView lastMsgTv;
		private TextView unreadTv, timeTv,message,askTimeTv,ansTimeTV,content;
		private TextView teacherName,teacherAnsawer;
	}
}
