package com.whcd.base.component.easeui.widget.gift;

import java.util.ArrayList;
import java.util.List;

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

import com.whcd.base.BaseApplication;
import com.whcd.base.R;
import com.whcd.base.interfaces.VolleyTool;

public class EaseGiftPagerView extends ViewPager {
	private LayoutInflater inflater;
	private List<View> views;
	private int onClickedIndex = -1;
	private clickedItem mClickedItem;
	private List<GridViewAdapter> adapters;
	private Context context;

	public EaseGiftPagerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}

	public EaseGiftPagerView(Context context) {
		super(context);
		this.context = context;
	}

	public clickedItem getmClickedItem() {
		return mClickedItem;
	}

	public void setmClickedItem(clickedItem mClickedItem) {
		this.mClickedItem = mClickedItem;
	}

	public void initData(List<List<EaseGiftEntity>> giftViews) {
		inflater = LayoutInflater.from(context);
		views = new ArrayList<View>();
		adapters = new ArrayList<EaseGiftPagerView.GridViewAdapter>();
		for (int i = 0; i < giftViews.size(); i++) {
			View view = inflater.inflate(R.layout.layout_gift_view, null);
			GridView giftGridView = (GridView) view.findViewById(R.id.giftGridView);
			GridViewAdapter adapter = new GridViewAdapter(giftViews.get(i));
			giftGridView.setAdapter(adapter);
			adapters.add(adapter);
			views.add(view);
		}
		if (listener != null) {
			listener.onGiftAllSize(giftViews.size());
		}
		setAdapter(new MPagerAdapter());
		setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				adapters.get(position).setCheckedId(-1);
				adapters.get(position).notifyDataSetChanged();
				if (listener != null) {
					listener.onGiftPagePostionChanged(position);
				}
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
		void getGiftInfo(EaseGiftEntity giftInfo);
	}

	class GridViewAdapter extends BaseAdapter {
		private List<EaseGiftEntity> lists;
		private int checkedId = -1;

		public int getCheckedId() {
			return checkedId;
		}

		public void setCheckedId(int checkedId) {
			this.checkedId = checkedId;
		}

		public GridViewAdapter(List<EaseGiftEntity> lists) {
			this.lists = lists;
		}

		@Override
		public int getCount() {
			return lists.size();
		}

		@Override
		public EaseGiftEntity getItem(int position) {
			return lists.get(position);
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
				convertView = inflater.inflate(R.layout.grid_item_gift_menu, null);
				holder.chargeTv = (TextView) convertView.findViewById(R.id.chargeTv);
				holder.expTv = (TextView) convertView.findViewById(R.id.expTv);
				holder.giftImg = (ImageView) convertView.findViewById(R.id.giftImg);
				holder.selectImg = (ImageView) convertView.findViewById(R.id.selectImg);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			VolleyTool.getImage(BaseApplication.IP + lists.get(position).giftPic, holder.giftImg);
			if (onClickedIndex != -1 && onClickedIndex == lists.get(position).giftNo) {
				holder.selectImg.setImageResource(R.drawable.icon_cart_select);
			} else {
				holder.selectImg.setImageResource(R.drawable.icon_cart_unselect);
			}
			holder.chargeTv.setText(lists.get(position).diamond + "");
			holder.expTv.setText(lists.get(position).giftName);

			convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (listener != null) {
						if (checkedId == lists.get(position).giftNo) {
							checkedId = onClickedIndex = -1;
							listener.onGiftClick(null);
						} else {
							checkedId = onClickedIndex = lists.get(position).giftNo;
							listener.onGiftClick(lists.get(position));
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
		TextView expTv;
	}

	private EaseGiftPagerViewListener listener;

	public void setEaseGiftPagerViewListener(EaseGiftPagerViewListener listener) {
		this.listener = listener;
	}

	public interface EaseGiftPagerViewListener {
		void onGiftAllSize(int maxSize);

		void onGiftPagePostionChanged(int newPosition);

		void onGiftClick(EaseGiftEntity easeGiftEntity);
	}

}
