package com.whcd.base.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class BaseAdapter<T> extends android.widget.BaseAdapter {
	private Context context;
	protected LayoutInflater layoutInflater;
	protected List<T> listData;
	protected int resId;

	public BaseAdapter(Context context, List<T> listData, int resId) {
		super();
		this.context = context;
		this.layoutInflater = LayoutInflater.from(context);
		this.listData = listData;
		this.resId = resId;
	}

	public BaseAdapter(Context context, int resId) {
		super();
		this.context = context;
		this.layoutInflater = LayoutInflater.from(context);
		this.listData = new ArrayList<T>();
		this.resId = resId;
	}

	public List<T> getList() {
		return listData;
	}

	public void appendList(List<T> list) {
		if (list != null) {
			this.listData.addAll(list);
		}
		notifyDataSetChanged();
	}

	public void setList(List<T> list) {
		this.listData.clear();
		if (list != null) {
			this.listData = list;
		}
		notifyDataSetChanged();
	}

	public void addItem(T bean) {
		this.listData.add(bean);
		notifyDataSetChanged();
	}

	public void addFristItem(T bean) {
		this.listData.add(0, bean);
		notifyDataSetChanged();
	}

	public void delItem(T bean) {
		this.listData.remove(bean);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return listData.size();
	}

	@Override
	public T getItem(int position) {
		return listData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	protected int getItemViewId(int position) {
		return resId;
	}

	@SuppressWarnings("unchecked")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		IViewHolder<T> viewHolder = null;
		if (convertView == null) {
			convertView = layoutInflater.inflate(getItemViewId(position), null);
			viewHolder = getViewHolder(position);
			viewHolder.createView(convertView);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (IViewHolder<T>) convertView.getTag();
		}
		viewHolder.buildData(position, getItem(position));
		return convertView;
	}

	protected abstract IViewHolder<T> getViewHolder(int position);

	public Context getContext() {
		return context;
	}

}
