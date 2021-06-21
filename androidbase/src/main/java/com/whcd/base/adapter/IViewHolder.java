package com.whcd.base.adapter;


import android.view.View;

public abstract class IViewHolder<T> {
	public View contentView;

	/**
	 * 
	 * initView(这里用一句话描述这个方法的作用)
	 * 
	 * @Title: initView 初始化布局
	 * @Description: TODO
	 * @param v
	 * @return void
	 */
	public final void createView(View v) {
		this.contentView = v;
		initView();
	};

	@SuppressWarnings("unchecked")
	public <V extends View> V findView(int resId) {
		return (V) contentView.findViewById(resId);
	}

	public abstract void initView();

	/**
	 * 
	 * buildData(这里用一句话描述这个方法的作用)
	 * 
	 * @Title: buildData 绑定数据
	 * @Description: TODO
	 * @param obj
	 * @return void
	 */
	public abstract void buildData(int position, T obj);
}
