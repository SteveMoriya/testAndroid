package com.wehang.ystv.adapter;

import android.content.Context;
import android.support.design.widget.CheckableImageButton;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wehang.ystv.R;
import com.wehang.ystv.bo.Coursewares;
import com.wehang.ystv.bo.YsMessage;
import com.wehang.ystv.ui.ApplyForRefundActivity;

import java.util.List;

public class RefoundAdapter extends BaseAdapter {
	private Context context;
	private List<YsMessage> videos;
	private LayoutInflater inflater;
    private String content;
	private ImageView lyim6;
	public RefoundAdapter(Context context, List<YsMessage> videos, String content, ImageView lyimg6) {
		super();
		this.context = context;
		this.videos = videos;
        this.content=content;
		this.lyim6=lyimg6;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		 return videos != null ? videos.size():0;
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.refound_item, null);
			holder.lyTv=convertView.findViewById(R.id.lyTv);
			holder.lyImg=convertView.findViewById(R.id.lyImg);
			holder.ly=convertView.findViewById(R.id.ly);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

/*		VideoInfo video = getItem(position);
		holder.titleTv.setText(video.title);
		holder.timeTv.setText(video.endTime);
		holder.lookNumTv.setText(String.valueOf(video.watchNum));*/
		holder.lyTv.setText(videos.get(position).content);
		final YsMessage ysMessage=getItem(position);
		if (ysMessage.isChose){
			holder.lyImg.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.yuan_gou));
		}else {
			holder.lyImg.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.yuan_quan));
		}
		holder.ly.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				for (int b=0;b<videos.size();b++){
					if (b==position){
						videos.get(b).isChose=true;
					}else {
						videos.get(b).isChose=false;
					}
				}
				lyim6.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.yuan_quan));
				content=ysMessage.content;

				notifyDataSetChanged();
			}
		});
		return convertView;
	}

	private class ViewHolder {
		private TextView lyTv;
		private ImageView lyImg;
		private LinearLayout ly;
	}

	public void setData(List<YsMessage> videos) {
		this.videos.clear();
		this.videos.addAll(videos);
		notifyDataSetChanged();
	}
}
