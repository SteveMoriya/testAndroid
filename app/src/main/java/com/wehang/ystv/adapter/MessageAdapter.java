package com.wehang.ystv.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wehang.ystv.R;
import com.wehang.ystv.bo.VideoInfo;
import com.wehang.ystv.bo.YsMessage;

import java.util.List;

public class MessageAdapter extends BaseAdapter {
	private Context context;
	private List<YsMessage> videos;
	private LayoutInflater inflater;

	public MessageAdapter(Context context, List<YsMessage> videos) {
		super();
		this.context = context;
		this.videos = videos;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return 3;
	}

	@Override
	public YsMessage getItem(int position) {
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
			convertView = inflater.inflate(R.layout.message_item, null);
			holder.timeTv=convertView.findViewById(R.id.message_time);
			holder.contentTv=convertView.findViewById(R.id.message_what);
			holder.titleTv=convertView.findViewById(R.id.message_title);
			holder.imageView=convertView.findViewById(R.id.message_img);
			holder.isVip=convertView.findViewById(R.id.isVipImg);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

/*		VideoInfo video = getItem(position);
		holder.titleTv.setText(video.title);
		holder.timeTv.setText(video.endTime);
		holder.lookNumTv.setText(String.valueOf(video.watchNum));*/
		if (position==0){
			holder.imageView.setImageResource(R.drawable.icon_xtxx);
			holder.titleTv.setText("系统消息");
			holder.isVip.setVisibility(View.GONE);
		}else if (position==1){
			holder.imageView.setImageResource(R.drawable.icon_tiwen);
			holder.titleTv.setText("收到的提问");
			holder.isVip.setVisibility(View.GONE);
		}else if (position==2){
			holder.imageView.setImageResource(R.drawable.icon_huida);
			holder.titleTv.setText("收到的回答");
			holder.isVip.setVisibility(View.GONE);
		}
		return convertView;
	}

	private class ViewHolder {
		private TextView contentTv;
		private TextView timeTv;
		private TextView titleTv;
		private ImageView imageView,isVip;
	}

	public void setData(List<YsMessage> videos) {
		this.videos.clear();
		this.videos.addAll(videos);
		notifyDataSetChanged();
	}
}
