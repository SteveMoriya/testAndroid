package com.wehang.ystv.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.reflect.TypeToken;
import com.wehang.ystv.Constant;
import com.wehang.ystv.R;
import com.wehang.ystv.bo.UserInfo;
import com.wehang.ystv.bo.WXPay;
import com.wehang.ystv.interfaces.UrlConstant;
import com.wehang.ystv.ui.LivePushActivity;
import com.wehang.ystv.ui.UserHomeActivty;
import com.wehang.ystv.utils.CommonUtil;
import com.wehang.ystv.utils.ToastUtil;
import com.wehang.ystv.utils.UserLoginInfo;
import com.wehang.ystv.utils.Utils;
import com.whcd.base.interfaces.BaseData;
import com.whcd.base.interfaces.BaseResult;
import com.whcd.base.interfaces.HttpTool;
import com.whcd.base.widget.CustomProgressDialog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LinkAdapter extends BaseAdapter {
	private Context context;
	private List<UserInfo> videos;
	private LayoutInflater inflater;
	private Dialog dialog;

	public LinkAdapter(Context context, List<UserInfo> videos, Dialog dialog) {
		super();
		this.context = context;
		this.videos = videos;
		this.dialog=dialog;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return  videos != null ?  videos.size():0;
	}

	@Override
	public UserInfo getItem(int position) {
		return videos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.link_item, null);
			holder.user_tx_itemlive=convertView.findViewById(R.id.user_tx_itemlive);
			holder.isVipImg=convertView.findViewById(R.id.isVipImg);
			holder.user_name_itemlive=convertView.findViewById(R.id.user_name_itemlive);
			holder.user_yiyuanm_itemlive=convertView.findViewById(R.id.user_yiyuanm_itemlive);
			holder.linkDo=convertView.findViewById(R.id.linkDo);
			holder.sexImg=convertView.findViewById(R.id.sexImg);
			holder.hospitalName=convertView.findViewById(R.id.hospitalName);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final UserInfo userInfo=videos.get(position);
		Glide.with(context).load(UrlConstant.IP+userInfo.iconUrl).placeholder(R.drawable.default_portrait).error(R.drawable.default_portrait).into(holder.user_tx_itemlive);
		holder.user_tx_itemlive.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				//点击头像跳转
				Intent intent = new Intent(context, UserHomeActivty.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("data", userInfo);
				intent.putExtra("bundle", bundle);
				context.startActivity(intent);
			}
		});
		if (userInfo.isVip==0){
			holder.isVipImg.setVisibility(View.GONE);
		}else {
			holder.isVipImg.setVisibility(View.VISIBLE);
		}
		holder.user_name_itemlive.setText(userInfo.name);
		holder.user_yiyuanm_itemlive.setText(userInfo.createTime);

		if (userInfo.status==1){
			holder.linkDo.setText("断开");
			holder.linkDo.setBackground(ContextCompat.getDrawable(context,R.drawable.red));
			holder.linkDo.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					CommonUtil.showYesNoDialog(context, "是否确认断开连麦？", "确定", "取消", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialogInterface, int arg1) {
							if (arg1 == DialogInterface.BUTTON_POSITIVE) {
								LivePushActivity activity= (LivePushActivity) context;
								activity.outLink();
								dialog.dismiss();
								Utils.endLink(activity.liveNeed.sourceId,context);
							}
						}
					}).show();

				}
			});
		}else {
			holder.linkDo.setText("连麦");
			holder.linkDo.setBackground(ContextCompat.getDrawable(context,R.drawable.green));
			holder.linkDo.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					for (int i=0;i<videos.size();i++){
						if (videos.get(i).status==1){
							dialog.dismiss();
							ToastUtil.makeText(context,"请先断开连麦", Toast.LENGTH_SHORT).show();
							return;
						}
					}
					CommonUtil.showYesNoDialog(context, "是否确认连麦？", "确定", "取消", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialogInterface, int arg1) {
							if (arg1 == DialogInterface.BUTTON_POSITIVE) {
								LivePushActivity activity= (LivePushActivity) context;
								activity.linkACCEPT(userInfo.userId);
								dialog.dismiss();
							}

						}
					}).show();

				}
			});
		}
		int sex=userInfo.sex;
		if (sex==0){
			holder.sexImg.setVisibility(View.VISIBLE);
			holder.sexImg.setImageResource(R.drawable.icon_home_g);
		}else if (sex==1){
			holder.sexImg.setVisibility(View.VISIBLE);
			holder.sexImg.setImageResource(R.drawable.icon_home_b);
		}
		holder.hospitalName.setText(userInfo.hospital);
		return convertView;
	}

	private class ViewHolder {

		private ImageView user_tx_itemlive;
		private ImageView isVipImg,sexImg;
		private TextView user_name_itemlive,user_ks_itemlive,user_zw_itemlive,user_yiyuanm_itemlive,hospitalName;
		private TextView linkDo;
	}

	public void setData(List<UserInfo> videos) {
		this.videos.clear();
		this.videos.addAll(videos);
		notifyDataSetChanged();
	}
}
