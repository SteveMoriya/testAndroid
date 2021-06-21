package com.whcd.base.component.easeui.widget.chatrow;

import android.content.Context;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.chat.EMMessage;
import com.whcd.base.R;
import com.whcd.base.component.easeui.EaseConstant;
import com.whcd.base.interfaces.VolleyTool;

/**
 * 大表情(动态表情)
 * 
 */
public class EaseChatRowBigExpression extends EaseChatRowText {

	private ImageView imageView;

	public EaseChatRowBigExpression(Context context, EMMessage message, int position, BaseAdapter adapter) {
		super(context, message, position, adapter);
	}

	@Override
	protected void onInflatView() {
		inflater.inflate(message.direct() == EMMessage.Direct.RECEIVE ? R.layout.ease_row_received_bigexpression : R.layout.ease_row_sent_bigexpression, this);
	}

	@Override
	protected void onFindViewById() {
		percentageView = (TextView) findViewById(R.id.percentage);
		imageView = (ImageView) findViewById(R.id.image);
	}

	@Override
	public void onSetUpView() {
		try {
			String url = message.getStringAttribute(EaseConstant.MESSAGE_ATTR_EXPRESSION_ADDR);
			VolleyTool.getImage(url, imageView, R.drawable.ease_default_expression, R.drawable.ease_default_expression);
		} catch (Exception e) {
			imageView.setImageResource(R.drawable.ease_default_expression);
		}

		handleTextMessage();
	}
}
