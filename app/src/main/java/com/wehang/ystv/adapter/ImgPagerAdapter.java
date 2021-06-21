package com.wehang.ystv.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.squareup.picasso.Picasso;
import com.wehang.ystv.R;
import com.wehang.ystv.bo.Banner;
import com.wehang.ystv.bo.Coursewares;
import com.wehang.ystv.interfaces.UrlConstant;


import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by Administrator on 2016/11/7.
 */
//ViewPager适配器
public class ImgPagerAdapter extends PagerAdapter {
    private List<String> mTitleList;
    private Context context;

    public ImgPagerAdapter(List<String> mTitleList, Context context) {
        this.mTitleList=mTitleList;
        this.context=context;
    }

    @Override
    public int getCount() {
        //return if (//list.size()==0){return 0;}else
            //return Integer.MAX_VALUE / 2;//页卡数
        return mTitleList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;//官方推荐写法
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view= LayoutInflater.from(context).inflate(R.layout.head_viewpager_item,null);
        final LinearLayout itemLyt=view.findViewById(R.id.itemLyt);
        final ImageView imageView= (ImageView) view.findViewById(R.id.head_viewpger_img);
        if (mTitleList.size()>0) {
            Glide.with(context).load(UrlConstant.KJIP+mTitleList.get(position)).placeholder(R.drawable.live_mrjz).error(R.drawable.live_mrjz).into(imageView);
            LogUtils.i("kjIMGpath",UrlConstant.KJIP+mTitleList.get(position));
            //加载原图显示
           /* final ViewGroup.LayoutParams lp = imageView.getLayoutParams();
            Glide.with(context)
                    .load(UrlConstant.KJIP+mTitleList.get(position)).asBitmap().placeholder(R.drawable.live_mrjz).error(R.drawable.live_mrjz)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap bitmap,
                                                    GlideAnimation<? super Bitmap> glideAnimation) {
                            int width = bitmap.getWidth();
                            int height = bitmap.getHeight();
                            //40点参数是因为viewHolder里面有距左距右各20DP的原因
                            lp.width = itemLyt.getWidth();
                            float tempHeight = height * ((float) lp.width / width);
                            lp.height = (int) tempHeight;
                            imageView.setLayoutParams(lp);
                            imageView.setImageBitmap(bitmap);
                        }
                    });*/
        }
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);//删除页卡
    }
}

