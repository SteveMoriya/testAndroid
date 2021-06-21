package com.whcd.base.component.easeui.widget;

import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.hyphenate.chat.EMMessage;

public class EaseChatMessageList {

	public interface MessageListItemClickListener {
		void onResendClick(EMMessage message);

		/**
		 * 控件有对气泡做点击事件默认实现，如果需要自己实现，return true。
		 * 当然也可以在相应的chatrow的onBubbleClick()方法里实现点击事件
		 * 
		 * @param message
		 * @return
		 */
		boolean onBubbleClick(EMMessage message, View bubbleLayout, ImageView imageIv, ImageView readStatusIv, BaseAdapter adapter);

		void onBubbleLongClick(EMMessage message);

		void onUserAvatarClick(String username);
	}

}
