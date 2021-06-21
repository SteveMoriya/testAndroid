package com.wehang.ystv.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wehang.ystv.R;
import com.wehang.ystv.bo.VideoInfo;

import java.util.List;

public class QusetionAdapter extends BaseAdapter {
	private Context context;
	private List<VideoInfo> videos;
	private LayoutInflater inflater;

	public QusetionAdapter(Context context, List<VideoInfo> videos) {
		super();
		this.context = context;
		this.videos = videos;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return 2;
	}

	@Override
	public VideoInfo getItem(int position) {
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
			convertView = inflater.inflate(R.layout.list_item_qusetion, null);
			holder.titleTv = (TextView) convertView.findViewById(R.id.title);
			holder.timeTv = (TextView) convertView.findViewById(R.id.timeTv);
			holder.lookNumTv = (TextView) convertView.findViewById(R.id.lookNumTv);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

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
	}

	public void setData(List<VideoInfo> videos) {
		this.videos.clear();
		this.videos.addAll(videos);
		notifyDataSetChanged();
	}
}
