package com.wehang.ystv.adapter;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.wehang.ystv.R;
import com.wehang.ystv.bo.UserInfo;
import com.wehang.ystv.dbutils.DownloadBean;
import com.wehang.ystv.dbutils.DownloadDao;
import com.wehang.ystv.interfaces.UrlConstant;
import com.wehang.ystv.utils.CommonUtil;
import com.wehang.ystv.utils.ToastUtil;
import com.wehang.ystv.utils.UserLoginInfo;
import com.whcd.base.interfaces.BaseData;
import com.whcd.base.interfaces.BaseResult;
import com.whcd.base.interfaces.HttpTool;
import com.whcd.base.interfaces.VolleyTool;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchAdapter extends BaseAdapter {
    private Activity context;
    private List<DownloadBean> userInfos;
    private LayoutInflater inflater;
    private UserInfo info;
    private DownloadDao dao;

    public SearchAdapter(Activity context, List<DownloadBean> userInfos,DownloadDao dao) {
        super();
        this.context = context;
        this.userInfos = userInfos;
        this.dao=dao;
        inflater = LayoutInflater.from(context);
        info = UserLoginInfo.getUserInfo(context);
    }

    @Override
    public int getCount() {
        return userInfos.size();
    }

    @Override
    public DownloadBean getItem(int position) {
        return userInfos.get(position);
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
            convertView = inflater.inflate(R.layout.list_item_search, null);
            holder.searchTv=convertView.findViewById(R.id.searchTV);
            holder.searchDelete=convertView.findViewById(R.id.searchDelete);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final DownloadBean item = getItem(position);

        holder.searchDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dao.delete(item);
                userInfos.remove(position);
                notifyDataSetChanged();
            }
        });
        holder.searchTv.setText(item.getMoviename());




        return convertView;
    }

    private class ViewHolder {
       /* private ImageView headImg, isVipImg, sexImg, levelImg;
        private TextView nickNameTv;
        private TextView lastMsgTv;
        private ImageView isFocusImg;*/
        private LinearLayout searchDelete;
        private TextView searchTv;
    }

    public void setData(List<DownloadBean> userInfos) {
        this.userInfos.clear();
        this.userInfos.addAll(userInfos);
        notifyDataSetChanged();
    }

    public void addData(List<DownloadBean> userInfos) {
        this.userInfos.addAll(userInfos);
        notifyDataSetChanged();
    }
}
