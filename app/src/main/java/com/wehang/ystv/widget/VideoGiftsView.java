package com.wehang.ystv.widget;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;


import com.wehang.ystv.R;
import com.wehang.ystv.bo.GiftInfo;
import com.wehang.ystv.interfaces.UrlConstant;
import com.wehang.ystv.utils.GlideUtils;

import java.util.ArrayList;
import java.util.List;

public class VideoGiftsView extends ViewPager {
	private LayoutInflater inflater;
	private Context context;
	private List<List<GiftInfo>> pageGiftList;
	private List<View> views;
	private int onClickedIndex = -1;
	private int onResuleClickedIndex = -1;
	private clickedItem mClickedItem;
	private List<GridViewAdapter> adapters;

	public clickedItem getClickedItem() {
		return mClickedItem;
	}

	public void setClickedItem(clickedItem mClickedItem) {
		this.mClickedItem = mClickedItem;
	}

	public VideoGiftsView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}

	public VideoGiftsView(Context context) {
		super(context);
		this.context = context;
	}

	public void initData(List<List<GiftInfo>> pageGiftList) {
		inflater = LayoutInflater.from(context);
		this.pageGiftList = pageGiftList;
		views = new ArrayList<View>();
		adapters = new ArrayList<GridViewAdapter>();
		for (int i = 0; i < pageGiftList.size(); i++) {
			View view = inflater.inflate(R.layout.layout_gift_view, null);
			GridView giftGridView = (GridView) view.findViewById(R.id.giftGridView);
			GridViewAdapter adapter = new GridViewAdapter(pageGiftList.get(i));
			giftGridView.setAdapter(adapter);
			adapters.add(adapter);
			views.add(view);
		}
		setAdapter(new MPagerAdapter());
		setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				adapters.get(position).notifyDataSetChanged();
			}

			@Override
			public void onPageScrolled(int position, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int position) {

			}
		});
	}

	class MPagerAdapter extends PagerAdapter {
		@Override
		public int getCount() {
			return views.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public Object instantiateItem(ViewGroup view, int position) {
			view.addView(views.get(position));
			return views.get(position);
		}

		@Override
		public void destroyItem(ViewGroup view, int position, Object arg2) {
			view.removeView(views.get(position));

		}
	}

	public interface clickedItem {
		void getGiftInfo(GiftInfo giftInfo);
	}

	class GridViewAdapter extends BaseAdapter {
		private List<GiftInfo> lists;

		public GridViewAdapter(List<GiftInfo> lists) {
			this.lists = lists;
		}

		@Override
		public int getCount() {
			return lists.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.item_gift_menu, null);
				holder = new ViewHolder();
				holder.chargeTv = (TextView) convertView.findViewById(R.id.chargeTv);
				holder.nameTv = (TextView) convertView.findViewById(R.id.nameTv);
				holder.giftImg = (ImageView) convertView.findViewById(R.id.giftImg);
				holder.selectImg = (ImageView) convertView.findViewById(R.id.selectImg);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}


			if (onClickedIndex != -1 && onClickedIndex == lists.get(position).giftNo) {
				holder.selectImg.setImageResource(R.drawable.icon_cart_select);
			} else {
				holder.selectImg.setImageResource(R.drawable.icon_cart_unselect);
			}
			GlideUtils.loadBaseImage(context, UrlConstant.IP + lists.get(position).giftPic, holder.giftImg);
			holder.chargeTv.setText(lists.get(position).diamond + "");
			holder.nameTv.setText(lists.get(position).giftName);

			convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (mClickedItem != null) {
						onResuleClickedIndex = lists.get(position).giftNo;
						if (onResuleClickedIndex != onClickedIndex) {
							onClickedIndex = onResuleClickedIndex;
							mClickedItem.getGiftInfo(lists.get(position));
						} else {
							onClickedIndex = onResuleClickedIndex = -1;
							mClickedItem.getGiftInfo(null);
						}
						notifyDataSetChanged();
					}
				}
			});
			return convertView;
		}
	}

	class ViewHolder {
		ImageView selectImg;
		ImageView giftImg;
		TextView chargeTv;
		TextView nameTv;
	}
}
