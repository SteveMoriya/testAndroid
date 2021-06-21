package com.wehang.ystv.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.LogUtils;
import com.google.gson.reflect.TypeToken;
import com.wehang.ystv.Constant;
import com.wehang.ystv.R;
import com.wehang.ystv.adapter.SearchAdapter;
import com.wehang.ystv.bo.UserInfo;
import com.wehang.ystv.dbutils.DBHelper;
import com.wehang.ystv.dbutils.DownloadBean;
import com.wehang.ystv.dbutils.DownloadDao;
import com.wehang.ystv.fragment.SearchFragment;
import com.wehang.ystv.interfaces.UrlConstant;
import com.wehang.ystv.interfaces.result.GetSearchReslut;
import com.wehang.ystv.utils.CommonUtil;
import com.wehang.ystv.utils.ToastUtil;
import com.wehang.ystv.utils.UserLoginInfo;
import com.whcd.base.activity.BaseActivity;
import com.whcd.base.interfaces.BaseData;
import com.whcd.base.interfaces.BaseResult;
import com.whcd.base.interfaces.HttpTool;
import com.whcd.base.widget.PageListView;
import com.whcd.base.widget.PageListView.PageListViewListener;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 
 * <p>
 * {搜索界面}
 * </p>
 * 
 * @author 宋瑶 2016-7-5 上午9:45:03
 * @version V1.0
 * 
 * 
 * @modificationHistory=========================重大变更说明
 * @modify by user: 宋瑶 2016-7-5
 */
public class SearchActivity extends BaseActivity implements OnItemClickListener {
	private int currentPage = 1;

	private PageListView searchListView;
	private EditText searchET;
	private View deleteView;
	private TextView tipTv;
	private ImageView imageView;
	private SearchAdapter adapter;
	public String keyword;

	@Override
	protected int getLayoutResId() {
		return R.layout.activity_search;
	}

	@Override
	protected void initTitleBar() {
	}
	private SearchFragment fragment1;
	@Override
	protected void initView() {
		searDao=DownloadDao.getInstance(DBHelper.getInstance(context));
		/*DownloadBean downloadBean=new DownloadBean();
		DownloadBean downloadBean1=new DownloadBean();
		downloadBean.setMoviename("秋秋");
		downloadBean1.setMoviename("唐傲秋");
		searDao.add(downloadBean);
		searDao.add(downloadBean1);
		list.addAll( searDao.GetAllBean());
		LogUtils.i("seacDao",list.toString());
		searchListView.setVisibility(View.GONE);*/
		fragment1 = new SearchFragment();
		getSupportFragmentManager().beginTransaction().replace(R.id.main_container, fragment1).commit();

		token = UserLoginInfo.getUserToken();
		tipTv = (TextView) findViewById(R.id.tipTv);
		tipTv.setOnClickListener(this);
		findViewById(R.id.backKey).setOnClickListener(this);
		View view=View.inflate(context,R.layout.item_botom,null);
		view.findViewById(R.id.clearHistory).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				searDao.deleteAll(list);
				list.clear();
				adapter.notifyDataSetChanged();
			}
		});

		searchListView = (PageListView) findViewById(R.id.searchListView);
		searchListView.addFooterView(view);
		searchListView.setPullLoadEnable(false);
		searchListView.setPullRefreshEnable(false);
		adapter = new SearchAdapter(this, list,searDao);
		searchListView.setOnItemClickListener(this);
		searchListView.setAdapter(adapter);
		//searchListView.setPageListViewListener(listListener);
		searchET = (EditText) findViewById(R.id.searchET);
		deleteView = findViewById(R.id.deleteView);
		deleteView.setOnClickListener(this);
		searchET.setFocusable(true);
		searchET.setFocusableInTouchMode(true);
		searchET.requestFocus();
		timer = new Timer();
		inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		timer.schedule(new TimerTask() {
			public void run() {
				inputManager.showSoftInput(searchET, 0);
			}
		}, 500);

		searchET.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (TextUtils.isEmpty(s)) {
					tipTv.setText("取消");
					deleteView.setVisibility(View.INVISIBLE);
					searchListView.setVisibility(View.VISIBLE);
					return;
				}

				inputManager.showSoftInput(searchET, 0);
				tipTv.setText("搜索");
				deleteView.setVisibility(View.VISIBLE);
			}
		});
	}

/*	PageListViewListener listListener = new PageListViewListener() {
		@Override
		public void onRefresh() {
			searchListView.stopRefresh();
		}

		@Override
		public void onLoadMore() {
			currentPage += 1;
			String search = searchET.getText().toString().trim();
			initData(search);
		}
	};*/

	private String token;

	private InputMethodManager inputManager;

	private Timer timer;

	private void initData(String searchKey) {
		/*Map<String, String> params = new HashMap<String, String>();
		params.put("token", token);
		params.put("keyword", searchKey);
		params.put("page", currentPage + "");
		params.put("pageSize", 100 + "");
		HttpTool.doPost(this, UrlConstant.SEARCH, params, true, new TypeToken<BaseResult<GetSearchReslut>>() {
		}.getType(), new HttpTool.OnResponseListener() {

			@Override
			public void onSuccess(BaseData data) {
				searchListView.stopLoadMore();
				searchListView.stopRefresh();
				//adapter.setData(((GetSearchReslut) data).users);
				if (data != null && ((GetSearchReslut) data).users.size() <= 0) {
					inputManager.showSoftInput(searchET, 0);
					searchET.requestFocus();
					ToastUtil.makeText(context,"没有搜到相关结果", Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void onError(int errorCode) {
                UserLoginInfo.loginOverdue(context, errorCode);
				searchListView.stopLoadMore();
				searchListView.stopRefresh();
			}
		});*/
		DownloadBean downloadBean=new DownloadBean();
		Date date=new Date();
		downloadBean.setUrl(date.getTime()+"");
		downloadBean.setMoviename(searchET.getText().toString().trim());
		List<DownloadBean>panduanList=new ArrayList<>();
		panduanList.addAll(searDao.GetAllBean());
		for (int i=0;i<panduanList.size();i++){
			if (panduanList.get(i).getMoviename().equals(searchET.getText().toString().trim())){
				searDao.delete(panduanList.get(i));
				break;
			}
		}
		searDao.add(downloadBean);
		searchListView.setVisibility(View.GONE);
		list.clear();
		list.addAll( searDao.GetAllBean());
		LogUtils.i("seacDao",list.toString());
		adapter.notifyDataSetChanged();
		keyword=searchET.getText().toString().trim();
		fragment1.initTab();

	}
	private DownloadDao searDao;
	private List<DownloadBean>list=new ArrayList<>();
	@Override
	protected void initData(Bundle bundle) {
		list.addAll( searDao.GetAllBean());
		LogUtils.i("seacDao",list.toString());
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onClick(View v) {
		if (CommonUtil.isFirstClick())
			return;
		switch (v.getId()) {
		case R.id.deleteView:
			searchET.setText("");
			searchET.requestFocus();
			inputManager.showSoftInput(searchET, 0);
			break;

		case R.id.backKey:
			back();
			break;

		case R.id.tipTv:
			String searchStr = searchET.getText().toString();
			if (TextUtils.isEmpty(searchStr)) {
				back();
			} else if (searchStr.contains("%")) {
				ToastUtil.makeText(context,"不要输入含%的字符", Toast.LENGTH_SHORT).show();
			} else {
				initData(searchStr);
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		//
		searchET.setText(list.get(position-1).getMoviename());
		initData(list.get(position-1).getMoviename());
	}
}
