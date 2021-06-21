package com.wehang.ystv.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.wehang.ystv.R;
import com.wehang.ystv.bo.UserInfo;
import com.wehang.ystv.bo.VideoInfo;
import com.wehang.ystv.interfaces.UrlConstant;

import java.util.List;

public class ChengyuanAdapter extends BaseAdapter {
	private Context context;
	private List<UserInfo> videos;
	private LayoutInflater inflater;

	public ChengyuanAdapter(Context context, List<UserInfo> videos) {
		super();
		this.context = context;
		this.videos = videos;
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
			convertView = inflater.inflate(R.layout.chengyuan_item, null);
			holder.user_tx_itemlive=convertView.findViewById(R.id.user_tx_itemlive);
			holder.isVipImg=convertView.findViewById(R.id.isVipImg);
			holder.user_name_itemlive=convertView.findViewById(R.id.user_name_itemlive);
			holder.user_ks_itemlive=convertView.findViewById(R.id.user_ks_itemlive);
			holder.user_yiyuanm_itemlive=convertView.findViewById(R.id.user_yiyuanm_itemlive);
			holder.user_zw_itemlive=convertView.findViewById(R.id.user_zw_itemlive);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		UserInfo userInfo=videos.get(position);
		Glide.with(context).load(UrlConstant.IP+userInfo.iconUrl).placeholder(R.drawable.default_portrait).error(R.drawable.default_portrait).into(holder.user_tx_itemlive);
		if (userInfo.isVip==0){
			holder.isVipImg.setVisibility(View.GONE);
		}else {
			holder.isVipImg.setVisibility(View.VISIBLE);
		}
		holder.user_name_itemlive.setText(userInfo.name);
		holder.user_yiyuanm_itemlive.setText(userInfo.hospital);
		holder.user_ks_itemlive.setText(userInfo.department+" |");
		holder.user_zw_itemlive.setText(userInfo.title);
/*		VideoInfo video = getItem(position);
		holder.titleTv.setText(video.title);
		holder.timeTv.setText(video.endTime);
		holder.lookNumTv.setText(String.valueOf(video.watchNum));*/
		return convertView;
	}

	private class ViewHolder {
		private TextView titleTv;
		private TextView timeTv;
		private TextView lookNumTv;
		private ImageView user_tx_itemlive;
		private ImageView isVipImg;
		private TextView user_name_itemlive,user_ks_itemlive,user_zw_itemlive,user_yiyuanm_itemlive;

	}

	public void setData(List<UserInfo> videos) {
		this.videos.clear();
		this.videos.addAll(videos);
		notifyDataSetChanged();
	}
}
