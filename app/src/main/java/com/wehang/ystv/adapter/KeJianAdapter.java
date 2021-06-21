package com.wehang.ystv.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wehang.ystv.R;
import com.wehang.ystv.bo.Coursewares;
import com.wehang.ystv.bo.VideoInfo;

import java.util.List;

public class KeJianAdapter extends BaseAdapter {
	private Context context;
	private List<Coursewares> videos;
	private LayoutInflater inflater;

	public KeJianAdapter(Context context, List<Coursewares> videos) {
		super();
		this.context = context;
		this.videos = videos;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		 return videos != null ? videos.size():0;
	}

	@Override
	public Coursewares getItem(int position) {
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
			convertView = inflater.inflate(R.layout.kejian_item, null);
			holder.titleTv=convertView.findViewById(R.id.kejian_name);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

/*		VideoInfo video = getItem(position);
		holder.titleTv.setText(video.title);
		holder.timeTv.setText(video.endTime);
		holder.lookNumTv.setText(String.valueOf(video.watchNum));*/
		holder.titleTv.setText(videos.get(position).coursewareName);
		return convertView;
	}

	private class ViewHolder {
		private TextView titleTv;
		private TextView timeTv;
		private TextView lookNumTv;
	}

	public void setData(List<Coursewares> videos) {
		this.videos.clear();
		this.videos.addAll(videos);
		notifyDataSetChanged();
	}
}
