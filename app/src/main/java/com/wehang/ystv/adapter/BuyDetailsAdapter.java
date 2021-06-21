package com.wehang.ystv.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wehang.ystv.R;
import com.wehang.ystv.bo.Order;
import com.wehang.ystv.bo.VideoInfo;

import java.util.List;

public class BuyDetailsAdapter extends BaseAdapter {
	private Context context;
	private List<Order> videos;
	private LayoutInflater inflater;

	public BuyDetailsAdapter(Context context, List<Order> videos) {
		super();
		this.context = context;
		this.videos = videos;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return videos.size();
	}

	@Override
	public Order getItem(int position) {
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
			convertView = inflater.inflate(R.layout.item_buydetails, null);
			holder.howMach=convertView.findViewById(R.id.howMach);
			holder.sqsj=convertView.findViewById(R.id.sqsj);
			holder.clzt=convertView.findViewById(R.id.clzt);
			holder.tkly=convertView.findViewById(R.id.tkly);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		Order video = getItem(position);
		holder.howMach.setText(video.price+"");
		holder.sqsj.setText(video.orderTime);
		if (video.orderStatus==4){
			holder.clzt.setText("待处理");
		}else if (video.orderStatus==5){
			holder.clzt.setText("已通过");
		}else if (video.orderStatus==6){
			holder.clzt.setText("已拒绝");
		}
		holder.tkly.setText(video.content);
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
		private TextView howMach,sqsj,clzt,tkly;
	}

	public void setData(List<Order> videos) {
		this.videos.clear();
		this.videos.addAll(videos);
		notifyDataSetChanged();
	}
}
