package com.wehang.ystv.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.wehang.ystv.R;
import com.wehang.ystv.bo.Banner;
import com.wehang.ystv.interfaces.UrlConstant;

import java.util.List;

/**
 * Created by Administrator on 2016/11/7.
 */
//ViewPager适配器
public class SonPagerAdapter extends PagerAdapter {
    private  List<Banner> mTitleList;
    private Context context;


    public SonPagerAdapter(List<Banner> mTitleList, Context context) {
        this.mTitleList=mTitleList;
        this.context=context;
    }

    @Override
    public int getCount() {
       /* if (mTitleList.size()==0){return 0;}else {
            return mTitleList.size();
        }*/
            return Integer.MAX_VALUE / 2;//页卡数
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;//官方推荐写法
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view= LayoutInflater.from(context).inflate(R.layout.head_viewpager_item,null);
        ImageView imageView= (ImageView) view.findViewById(R.id.head_viewpger_img);
        if (mTitleList.size()>0){
            Glide.with(context).load(UrlConstant.IP+mTitleList.get(position%mTitleList.size()).bannerPic).placeholder(R.drawable.banner_mrjz).error(R.drawable.banner_mrjz).into(imageView);
            final Banner banner=mTitleList.get(position%mTitleList.size());
            if (banner.link!=null){
                if (!TextUtils.isEmpty(banner.link)){
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Uri uri = Uri.parse(banner.link);
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(uri);
                            context.startActivity(intent);
                        }
                    });
                }
            }

        }



        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);//删除页卡
    }
    private PagerAdapter copy = new PagerAdapter() {


        @Override
        public int getCount() {
            return mTitleList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return false;
        }
    };
    public PagerAdapter getCopyAdapter(){
        return copy;
    }
    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

}

