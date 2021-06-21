package com.wehang.ystv.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.bumptech.glide.Glide;
import com.google.gson.reflect.TypeToken;
import com.wehang.ystv.Constant;
import com.wehang.ystv.R;
import com.wehang.ystv.bo.Order;
import com.wehang.ystv.bo.QuestionMessages;
import com.wehang.ystv.bo.SysMessage;
import com.wehang.ystv.bo.UserInfo;
import com.wehang.ystv.bo.WXPay;
import com.wehang.ystv.interfaces.UrlConstant;
import com.wehang.ystv.ui.AnswerActivity;
import com.wehang.ystv.ui.GetQuestionActivity;
import com.wehang.ystv.ui.LivePushActivity;
import com.wehang.ystv.ui.MyQusetionActivity;
import com.wehang.ystv.ui.UserHomeActivty;
import com.wehang.ystv.utils.CommonUtil;
import com.wehang.ystv.utils.UserLoginInfo;
import com.whcd.base.activity.ImagePagerActivity;
import com.whcd.base.interfaces.BaseData;
import com.whcd.base.interfaces.BaseResult;
import com.whcd.base.interfaces.HttpTool;
import com.whcd.base.widget.CustomProgressDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuestionAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater inflater;
	private List<QuestionMessages> conversations;
	//private List<UserInfo> userList = new ArrayList<UserInfo>();
	/**
	 * 1.直播页，2消息页，3我的提问 4.聊天室的提问
	 */
	private int type;
	public QuestionAdapter(Context context, List<QuestionMessages> eMConversations,int type) {
		super();
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		this.conversations = eMConversations;
		this.type=type;
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

	/*public List<UserInfo> getUserList() {
		return userList;
	}

	public void setUserList(List<UserInfo> userList) {
		this.userList = userList;
	}*/


	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.item_question, null);
			/*);
			holder.isVipImg = (ImageView) convertView.findViewById(R.id.isVipImg);
			holder.sexImg = (ImageView) convertView.findViewById(R.id.sexImg);

			holder.lastMsgTv = (TextView) convertView.findViewById(R.id.lastMsgTv);
			holder.levelImg = (ImageView) convertView.findViewById(R.id.levelImg);
			holder.timeTv = (TextView) convertView.findViewById(R.id.timeTv);
			holder.unreadTv = (TextView) convertView.findViewById(R.id.unreadTv);*/
			holder.headImg = (ImageView) convertView.findViewById(R.id.user_tx_itemlive);
			holder.userNameTv = (TextView) convertView.findViewById(R.id.userName);
			holder.liveNameTV=convertView.findViewById(R.id.liveName);
			holder.askTimeTv= (TextView) convertView.findViewById(R.id.askTime);
			holder.ansTimeTV= (TextView) convertView.findViewById(R.id.ansTime);
			holder.contentTV=convertView.findViewById(R.id.contentTV);
			holder.img1=convertView.findViewById(R.id.tw_img1);
			holder.img2=convertView.findViewById(R.id.tw_img2);
			holder.img3=convertView.findViewById(R.id.tw_img3);
			holder.teacherNameTV=convertView.findViewById(R.id.teacherName);
			holder.teacherAnsTV=convertView.findViewById(R.id.teacherAnsawer);
			holder.delteTV=convertView.findViewById(R.id.delte);
			holder.bg=convertView.findViewById(R.id.tiwenBg);
			holder.isShowAnsLiner=convertView.findViewById(R.id.isShowAnsLiner);
			holder.showDeltLin=convertView.findViewById(R.id.showDeltLin);
			holder.toans=convertView.findViewById(R.id.toans);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (type==3){
			holder.delteTV.setVisibility(View.VISIBLE);
			holder.showDeltLin.setVisibility(View.VISIBLE);
			holder.bg.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_white_big_shape));
		}else {
			holder.delteTV.setVisibility(View.GONE);
			holder.showDeltLin.setVisibility(View.GONE);
			holder.bg.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_white_big_shape));
		}
		if (type==2){
			holder.liveNameTV.setVisibility(View.VISIBLE);
		}else {
			holder.liveNameTV.setVisibility(View.GONE);
		}
		if (type==4){
			holder.liveNameTV.setVisibility(View.GONE);
		}
		final QuestionMessages questionMessages=conversations.get(position);
		if (type==3){
			questionMessages.iconUrl=UserLoginInfo.getUserInfo().iconUrl;
			questionMessages.questionUserName=UserLoginInfo.getUserInfo().name;
			questionMessages.questionUserId=UserLoginInfo.getUserInfo().userId;
		}
		Glide.with(context).load(UrlConstant.IP+questionMessages.iconUrl).placeholder(R.drawable.default_portrait).error(R.drawable.default_portrait).into(holder.headImg);
		holder.headImg.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				//点击头像跳转
				Intent intent=new Intent(context, UserHomeActivty.class);
				Bundle bundle=new Bundle();
				UserInfo userInfo=new UserInfo();
				userInfo.userId=questionMessages.questionUserId;
				bundle.putSerializable("data",userInfo);
				intent.putExtra("bundle",bundle);
				context.startActivity(intent);
			}
		});
		final String[] pic;
		if (TextUtils.isEmpty(questionMessages.pics)){
			pic= new String[0];
			holder.img1.setVisibility(View.GONE);
			holder.img3.setVisibility(View.GONE);
			holder.img2.setVisibility(View.GONE);
		}else {
			pic=questionMessages.pics.split(",");
		}

		if (pic.length>0){
			holder.img1.setVisibility(View.GONE);
			holder.img3.setVisibility(View.GONE);
			holder.img2.setVisibility(View.GONE);
			if (pic.length>0){
				holder.img1.setVisibility(View.VISIBLE);
				Glide.with(context).load(UrlConstant.IP+pic[0]).placeholder(R.drawable.default_cover).error(R.drawable.default_cover).into(holder.img1);
			}
			if (pic.length>1){
				holder.img2.setVisibility(View.VISIBLE);
				Glide.with(context).load(UrlConstant.IP+pic[1]).placeholder(R.drawable.default_cover).error(R.drawable.default_cover).into(holder.img2);
			}
			if (pic.length>2){
				holder.img3.setVisibility(View.VISIBLE);
				Glide.with(context).load(UrlConstant.IP+pic[2]).placeholder(R.drawable.default_cover).error(R.drawable.default_cover).into(holder.img3);
			}

		}
		holder.img1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				toWAHTCH(pic,0);
			}
		});
		holder.img2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				toWAHTCH(pic,1);
			}
		});
		holder.img3.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				toWAHTCH(pic,2);
			}
		});

		holder.userNameTv.setText(questionMessages.questionUserName);
		holder.contentTV.setText(questionMessages.questionContent);
		holder.teacherAnsTV.setText(questionMessages.answerContent);
		holder.teacherNameTV.setText(questionMessages.answerUserName+":");
		holder.liveNameTV.setText(questionMessages.title+"");

		if(TextUtils.isEmpty(questionMessages.questionTime)){
			holder.ansTimeTV.setVisibility(View.GONE);
		}else {
			holder.ansTimeTV.setVisibility(View.VISIBLE);
			holder.askTimeTv.setVisibility(View.VISIBLE);
			holder.askTimeTv.setText(questionMessages.questionTime);
		}
		if (TextUtils.isEmpty(questionMessages.answerTime)){
			holder.ansTimeTV.setVisibility(View.GONE);
		}else {
			holder.ansTimeTV.setVisibility(View.VISIBLE);
			holder.ansTimeTV.setText(questionMessages.answerTime);
		}
		if (TextUtils.isEmpty(questionMessages.answerContent)){
			holder.isShowAnsLiner.setVisibility(View.GONE);
			if (type==2){
				holder.toans.setVisibility(View.VISIBLE);
			}else {
				holder.toans.setVisibility(View.GONE);
			}

		}else {
			holder.isShowAnsLiner.setVisibility(View.VISIBLE);
			holder.toans.setVisibility(View.GONE);
		}
		holder.toans.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Constant.questinId=questionMessages.questionId;
				Intent intent=new Intent(context, AnswerActivity.class);
				Bundle bundle=new Bundle();
				bundle.putSerializable("data",questionMessages);
				intent.putExtra("bundle",bundle);
				Activity activity=(GetQuestionActivity) context;
				activity.startActivityForResult(intent,110);
			}
		});
		holder.delteTV.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				delet(conversations.get(position));
			}
		});

		return convertView;
	}

	private class ViewHolder {
		private ImageView headImg, isVipImg, sexImg, levelImg,img1,img2,img3;
		private TextView userNameTv,liveNameTV;
		private TextView lastMsgTv;
		private TextView unreadTv, askTimeTv,ansTimeTV,contentTV,teacherNameTV,teacherAnsTV,delteTV;
		private RelativeLayout bg;
		private LinearLayout isShowAnsLiner,showDeltLin;
		private TextView toans;
	}

	private void delet(final QuestionMessages questionMessages){
		CommonUtil.showYesNoDialog(context, "是否确认删除？", "确定", "取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int arg1) {
				if (arg1 == DialogInterface.BUTTON_POSITIVE) {
					Map<String, String> params = new HashMap<String, String>();
					params.put("token", UserLoginInfo.getUserToken());
					params.put("questionId", questionMessages.questionId);
					final CustomProgressDialog dialog = CustomProgressDialog.show(context, "加载中...", true, null);
					HttpTool.doPost(context, UrlConstant.DELETEQUESTION, params, true, new TypeToken<BaseResult<WXPay>>() {
					}.getType(), new HttpTool.OnResponseListener() {

						@Override
						public void onSuccess(BaseData data) {
							dialog.dismiss();
							conversations.remove(questionMessages);
							notifyDataSetChanged();
						}

						@Override
						public void onError(int errorCode) {
							dialog.dismiss();
						}
					});
				}
			}
		}).show();

	}
	private void toWAHTCH(String[]pic,int which){
		Intent	intent = new Intent(context, ImagePagerActivity.class);
		ArrayList<String> urls = new ArrayList<String>();
		//判断是网络图片还是本地 0默认网络
		int url_bd_wl=0;
		for (int i=0;i<pic.length;i++){
			if (!pic[i].equals("null")){
				urls.add(UrlConstant.IP + pic[i]);
			}
		}

		intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, urls);
		intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, which);
		intent.putExtra("startPosition",which);
		intent.putExtra("type",url_bd_wl);
		context.startActivity(intent);
	}
}
