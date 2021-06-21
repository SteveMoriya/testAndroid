package com.wehang.ystv.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tencent.imcore.IFileTrans;
import com.wehang.ystv.R;
import com.wehang.ystv.bo.Lives;
import com.wehang.ystv.bo.VideoInfo;
import com.wehang.ystv.interfaces.UrlConstant;

import java.util.List;

public class VideoAdapter extends BaseAdapter {
	private Context context;
	private List<Lives> videos;
	private LayoutInflater inflater;

	public VideoAdapter(Context context, List<Lives> videos) {
		super();
		this.context = context;
		this.videos = videos;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		if (videos.size()<=2){
			return videos.size();
		}else return 2;

	}

	@Override
	public Lives getItem(int position) {
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
			convertView = inflater.inflate(R.layout.list_item_video, null);
			holder.titleTv = (TextView) convertView.findViewById(R.id.title);
			holder.timeTv = (TextView) convertView.findViewById(R.id.timeTv);
			holder.lookNumTv = (TextView) convertView.findViewById(R.id.lookNumTv);
			holder.vedioImg=convertView.findViewById(R.id.vedioImg);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		Lives video = getItem(position);
		holder.titleTv.setText(video.sourceTitle);
		Glide.with(context).load(UrlConstant.IP+video.sourcePic).placeholder(R.drawable.live_mrjz).error(R.drawable.live_mrjz).into(holder.vedioImg);
		return convertView;
	}

	private class ViewHolder {
		private TextView titleTv;
		private TextView timeTv;
		private TextView lookNumTv;
		private ImageView vedioImg;
	}

	public void setData(List<Lives> videos) {
		this.videos.clear();
		this.videos.addAll(videos);
		notifyDataSetChanged();
	}
}
