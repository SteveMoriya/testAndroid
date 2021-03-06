package com.whcd.base.component.easeui.widget.chatrow;

import java.io.File;

import android.content.Context;
import android.text.Spannable;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMFileMessageBody;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMessage.ChatType;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.exceptions.HyphenateException;
import com.whcd.base.R;
import com.whcd.base.component.easeui.utils.EaseImageUtils;
import com.whcd.base.component.easeui.utils.EaseSmileUtils;

public class EaseChatRowText extends EaseChatRow {

	private TextView contentView;
	protected ImageView imageView;

	public EaseChatRowText(Context context, EMMessage message, int position, BaseAdapter adapter) {
		super(context, message, position, adapter);
	}

	@Override
	protected void onInflatView() {
		inflater.inflate(message.direct() == EMMessage.Direct.RECEIVE ? R.layout.ease_row_received_message : R.layout.ease_row_sent_message, this);
	}

	@Override
	protected void onFindViewById() {
		contentView = (TextView) findViewById(R.id.tv_chatcontent);
	}

	@Override
	public void onSetUpView() {
		Spannable span;
		try {
			EMTextMessageBody txtBody = (EMTextMessageBody) message.getBody();
			span = EaseSmileUtils.getSmiledText(context, txtBody.getMessage());
			// 设置内容
			contentView.setText(span, BufferType.SPANNABLE);
		} catch (Exception e) {
			EMImageMessageBody imgBody = (EMImageMessageBody) message.getBody();
		}

		handleTextMessage();
	}

	protected void handleTextMessage() {
		if (message.direct() == EMMessage.Direct.SEND) {
			setMessageSendCallback();
			switch (message.status()) {
			case CREATE:
				progressBar.setVisibility(View.GONE);
				statusView.setVisibility(View.VISIBLE);
				// 发送消息
				// sendMsgInBackground(message);
				break;
			case SUCCESS: // 发送成功
				progressBar.setVisibility(View.GONE);
				statusView.setVisibility(View.GONE);
				break;
			case FAIL: // 发送失败
				progressBar.setVisibility(View.GONE);
				statusView.setVisibility(View.VISIBLE);
				break;
			case INPROGRESS: // 发送中
				progressBar.setVisibility(View.VISIBLE);
				statusView.setVisibility(View.GONE);
				break;
			default:
				break;
			}
		} else {
			if (!message.isAcked() && message.getChatType() == ChatType.Chat) {
				try {
					EMClient.getInstance().chatManager().ackMessageRead(message.getFrom(), message.getMsgId());
				} catch (HyphenateException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	protected void onUpdateView() {
		adapter.notifyDataSetChanged();
	}

}
