package com.wehang.ystv.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wehang.ystv.R;
import com.wehang.ystv.bo.Transaction;
import com.wehang.ystv.bo.UserInfo;

import java.util.List;

public class TrsRecordAdapter extends BaseAdapter {
	private Context context;
	private List<Transaction> list;
	private LayoutInflater inflater;

	public TrsRecordAdapter(Context context, List<Transaction> list) {
		super();
		this.context = context;
		this.list = list;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Transaction getItem(int position) {
		return list.get(position);
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
			convertView = inflater.inflate(R.layout.trans_item, null);
			holder.zyName=convertView.findViewById(R.id.zyName);
			holder.transType=convertView.findViewById(R.id.transType);
			holder.useType=convertView.findViewById(R.id.useType);
			holder.noshow=convertView.findViewById(R.id.trans_noshow);
			holder.howMach=convertView.findViewById(R.id.howMach);
			holder.time=convertView.findViewById(R.id.time);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		Transaction transaction=list.get(position);
/*		VideoInfo video = getItem(position);
		holder.titleTv.setText(video.title);
		holder.timeTv.setText(video.endTime);
		holder.lookNumTv.setText(String.valueOf(video.watchNum));*/
		/*if (position==1){
			holder.noshow.setVisibility(View.GONE);
		}*/
		holder.noshow.setVisibility(View.GONE);
		holder.zyName.setText(transaction.title);
		holder.time.setText(transaction.createTime);
		if (transaction.type==0){
			holder.noshow.setVisibility(View.VISIBLE);
			holder.transType.setText("购买");
			holder.howMach.setText("-"+transaction.money/100);
		}else if (transaction.type==1){
			holder.transType.setText("退款");
			holder.noshow.setVisibility(View.VISIBLE);
			holder.howMach.setText("+"+transaction.money/100);
		}else if (transaction.type==2){
			holder.transType.setText("充值");
			holder.howMach.setText("-"+transaction.money/100);
		}else if (transaction.type==3){
			holder.transType.setText("升级会员");
			holder.howMach.setText("-"+transaction.money/100);
		}
		if (transaction.channel==0){
			holder.useType.setText("余额");
		}else if (transaction.channel==1){
			holder.useType.setText("支付宝");
		}else if (transaction.channel==2){
			holder.useType.setText("微信");
		}

		return convertView;
	}

	private class ViewHolder {
		private TextView zyName;
		private TextView transType;
		private TextView useType;
		private LinearLayout noshow;
		private TextView howMach;
		private TextView time;
	}

	public void setData(List<Transaction> list) {
		this.list.clear();
		this.list.addAll(list);
		notifyDataSetChanged();
	}
}
