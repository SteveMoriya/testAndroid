package com.wehang.ystv.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout.LayoutParams;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.wehang.ystv.R;
import com.wehang.ystv.bo.PhotoInfo;
import com.whcd.base.utils.DisplayUtils;


import java.util.List;

public class PhotoChooseAdapter extends BaseAdapter {
    private Activity context = null;
    private LayoutInflater inflater = null;
    private List<PhotoInfo> list = null;
    private GridView imageListView = null;
    private DeleteSelectPictureListener listener = null;
	private int status;

    public PhotoChooseAdapter(Activity context, List<PhotoInfo> list, GridView imageListView,int status) {
		this.context = context;
		this.inflater = LayoutInflater.from(this.context);
		this.list = list;
		this.status=status;
		this.imageListView = imageListView;
    }

    public void setList(List<PhotoInfo> list) {
	this.list = list;
    }

    @Override
    public int getCount() {
	return list != null ? list.size() : 0;
    }

    @Override
    public PhotoInfo getItem(int position) {
	return list.get(position);
    }

    @Override
    public long getItemId(int position) {
	return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
	ViewHolder viewHolder = null;
	if (convertView == null) {
	    viewHolder = new ViewHolder();
	    convertView = inflater.inflate(R.layout.item_seller_verify_photo, null);
	    viewHolder.imageView = (ImageView) convertView.findViewById(R.id.image_view);
	    viewHolder.deleteView = (ImageView) convertView.findViewById(R.id.delete_view);
	    viewHolder.borderLayout = (LinearLayout) convertView.findViewById(R.id.image_border_layout);
	    viewHolder.tipView = (TextView) convertView.findViewById(R.id.image_tip_tv);
	    convertView.setTag(viewHolder);
	} else {
	    viewHolder = (ViewHolder) convertView.getTag();
	}

	final PhotoInfo model = getItem(position);
	viewHolder.tipView.setText(model.name);

	int screenWidth = DisplayUtils.getScreenWidth((Activity) context) / 3;
	LayoutParams params = (LayoutParams) viewHolder.imageView.getLayoutParams();
	if (model.path != null) {
	    viewHolder.borderLayout.setVisibility(View.VISIBLE);
	    params.width = DisplayUtils.dip2px(context, 100f);
	    params.height = DisplayUtils.dip2px(context, 100f);
	    // params.height = screenWidth;
	    // params.width = screenWidth;
	    viewHolder.imageView.setLayoutParams(params);

	    viewHolder.imageView.setTag(model.path);
		LogUtils.i("imgIofo",model.path.indexOf("http") != -1 ? model.path : "file://" + model.path);
	    ImageLoader.getInstance().displayImage(model.path.indexOf("http") != -1 ? model.path : "file://" + model.path, viewHolder.imageView,
		    new ImageLoadingListener() {
			@Override
			public void onLoadingStarted(String arg0, View arg1) {

			}

			@Override
			public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
			    ImageView iconView = (ImageView) imageListView.findViewWithTag(model.path);
			    if (iconView != null) {
				iconView.setImageResource(R.drawable.default_cover);
			    }
			}

			@Override
			public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
			    ImageView iconView = (ImageView) imageListView.findViewWithTag(model.path);
			    if (iconView != null) {
				iconView.setImageBitmap(arg2);
			    }
			}

			@Override
			public void onLoadingCancelled(String arg0, View arg1) {

			}
		    });
	    if (listener != null) {
		if (position == 4) {
		    viewHolder.deleteView.setVisibility(View.GONE);
		} else {
		    viewHolder.deleteView.setVisibility(View.VISIBLE);
		}
	    } else {
		viewHolder.deleteView.setVisibility(View.GONE);
	    }
	} else {
	    viewHolder.borderLayout.setVisibility(View.GONE);

	    params.height = DisplayUtils.dip2px(context, 100f);
	    params.width = DisplayUtils.dip2px(context, 100f);
	    viewHolder.imageView.setLayoutParams(params);

	    viewHolder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.addphoto));
	    viewHolder.deleteView.setVisibility(View.GONE);
	}
	/*if (status==2){
		viewHolder.deleteView.setVisibility(View.VISIBLE);
	}*/
	viewHolder.deleteView.setOnClickListener(new View.OnClickListener() {
	    @Override
	    public void onClick(View v) {
		if (listener != null) {
		    listener.onDelete(model);
		}
	    }
	});

	return convertView;
    }

    final static class ViewHolder {
	LinearLayout borderLayout;
	ImageView imageView;
	ImageView deleteView;
	TextView tipView;
    }

    public interface DeleteSelectPictureListener {
	public void onDelete(PhotoInfo item);
    }

    public void setListener(DeleteSelectPictureListener listener) {
	this.listener = listener;
    }
}
