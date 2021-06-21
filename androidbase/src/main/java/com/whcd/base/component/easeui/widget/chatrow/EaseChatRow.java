package com.whcd.base.component.easeui.widget.chatrow;

import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMessage.Direct;
import com.hyphenate.util.DateUtils;
import com.whcd.base.R;
import com.whcd.base.component.easeui.utils.EaseUserUtils;
import com.whcd.base.component.easeui.widget.EaseChatMessageList;
import com.whcd.base.component.easeui.widget.EaseChatMessageList.MessageListItemClickListener;

public abstract class EaseChatRow extends LinearLayout {
	protected static final String TAG = EaseChatRow.class.getSimpleName();

	protected LayoutInflater inflater;
	protected Context context;
	protected BaseAdapter adapter;
	protected EMMessage message;
	protected int position;

	protected TextView timeStampView;
	protected ImageView userAvatarView;
	protected ImageView vipView;
	protected View bubbleLayout;
	protected TextView usernickView;

	protected TextView percentageView;
	protected ProgressBar progressBar;
	protected ImageView statusView;
	protected Activity activity;

	// protected TextView ackedView;
	protected TextView deliveredView;

	protected EMCallBack messageSendCallback;
	protected EMCallBack messageReceiveCallback;

	protected MessageListItemClickListener itemClickListener;

	protected String ip;
	protected String mineIconUrl;
	protected int mineVip;

	public EaseChatRow(Context context, EMMessage message, int position, BaseAdapter adapter) {
		super(context);
		this.context = context;
		this.activity = (Activity) context;
		this.message = message;
		this.position = position;
		this.adapter = adapter;
		inflater = LayoutInflater.from(context);

		initView();
	}

	private void initView() {
		onInflatView();
		timeStampView = (TextView) findViewById(R.id.timestamp);
		userAvatarView = (ImageView) findViewById(R.id.headImg);
		vipView = (ImageView) findViewById(R.id.isVipImg);
		bubbleLayout = findViewById(R.id.bubble);
		usernickView = (TextView) findViewById(R.id.tv_userid);

		progressBar = (ProgressBar) findViewById(R.id.progress_bar);
		statusView = (ImageView) findViewById(R.id.msg_status);
		deliveredView = (TextView) findViewById(R.id.tv_delivered);
		onFindViewById();
	}

	/**
	 * 根据当前message和position设置控件属性等
	 * 
	 * @param message
	 * @param position
	 */
	public void setUpView(EMMessage message, int position, EaseChatMessageList.MessageListItemClickListener itemClickListener, String ip, String iconUrl,
			int vip) {
		this.message = message;
		this.position = position;
		this.itemClickListener = itemClickListener;
		this.ip = ip;
		this.mineIconUrl = iconUrl;
		this.mineVip = vip;

		setUpBaseView();
		onSetUpView();
		setClickListener();
	}

	private void setUpBaseView() {
		// 设置用户昵称头像，bubble背景等
		TextView timestamp = (TextView) findViewById(R.id.timestamp);
		if (timestamp != null) {
			if (position == 0) {
				timestamp.setText(DateUtils.getTimestampString(new Date(message.getMsgTime())));
				timestamp.setVisibility(View.VISIBLE);
			} else {
				// 两条消息时间离得如果稍长，显示时间
				EMMessage prevMessage = (EMMessage) adapter.getItem(position - 1);
				if (prevMessage != null && DateUtils.isCloseEnough(message.getMsgTime(), prevMessage.getMsgTime())) {
					timestamp.setVisibility(View.GONE);
				} else {
					timestamp.setText(DateUtils.getTimestampString(new Date(message.getMsgTime())));
					timestamp.setVisibility(View.VISIBLE);
				}
			}
		}

		// 设置头像和nick
		String iconUrl = null;
		try {
			if (message.direct() == Direct.SEND) {
				iconUrl = mineIconUrl;
			} else {
				iconUrl = message.getStringAttribute("iconUrl");
			}
		} catch (Exception e) {
		}
		EaseUserUtils.setUserhead(context, iconUrl != null ? ip + iconUrl : null, userAvatarView);// 发送方不显示nick

		if (deliveredView != null) {
			if (message.isDelivered()) {
				deliveredView.setVisibility(View.VISIBLE);
			} else {
				deliveredView.setVisibility(View.INVISIBLE);
			}
		}
	}

	/**
	 * 设置消息发送callback
	 */
	protected void setMessageSendCallback() {
		if (messageSendCallback == null) {
			messageSendCallback = new EMCallBack() {
				@Override
				public void onSuccess() {
					updateView();
				}

				@Override
				public void onProgress(final int progress, String status) {
					activity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							if (percentageView != null)
								percentageView.setText(progress + "%");
						}
					});
				}

				@Override
				public void onError(int code, String error) {
					updateView();
				}
			};
		}
		message.setMessageStatusCallback(messageSendCallback);
	}

	/**
	 * 设置消息接收callback
	 */
	protected void setMessageReceiveCallback() {
		if (messageReceiveCallback == null) {
			messageReceiveCallback = new EMCallBack() {

				@Override
				public void onSuccess() {
					updateView();
				}

				@Override
				public void onProgress(final int progress, String status) {
					activity.runOnUiThread(new Runnable() {
						public void run() {
							if (percentageView != null) {
								percentageView.setText(progress + "%");
							}
						}
					});
				}

				@Override
				public void onError(int code, String error) {
					updateView();
				}
			};
		}
		message.setMessageStatusCallback(messageReceiveCallback);
	}

	protected void setClickListener() {
		if (bubbleLayout != null) {
			bubbleLayout.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (itemClickListener != null) {
						itemClickListener.onBubbleClick(message, bubbleLayout, null, null, null);
					}
				}
			});

			bubbleLayout.setOnLongClickListener(new OnLongClickListener() {

				@Override
				public boolean onLongClick(View v) {
					if (itemClickListener != null) {
						itemClickListener.onBubbleLongClick(message);
					}
					return true;
				}
			});
		}

		if (statusView != null) {
			statusView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (itemClickListener != null) {
						itemClickListener.onResendClick(message);
					}
				}
			});
		}

		userAvatarView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (itemClickListener != null) {
					if (message.direct() == Direct.SEND) {
						itemClickListener.onUserAvatarClick(EMClient.getInstance().getCurrentUser());
					} else {
						itemClickListener.onUserAvatarClick(message.getFrom());
					}
				}
			}
		});
	}

	protected void updateView() {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				if (message.status() == EMMessage.Status.FAIL) {
					if (message.getError() == EMError.EM_NO_ERROR) {
						Toast.makeText(activity, "你被拉入黑名单，不可私信", 0).show();
					} else {
						Toast.makeText(activity, "消息发送失败", 0).show();
					}
				}

				onUpdateView();
			}
		});

	}

	/**
	 * 填充layout
	 */
	protected abstract void onInflatView();

	/**
	 * 查找chatrow里的控件
	 */
	protected abstract void onFindViewById();

	/**
	 * 消息状态改变，刷新listview
	 */
	protected abstract void onUpdateView();

	/**
	 * 设置更新控件属性
	 */
	protected abstract void onSetUpView();

}
