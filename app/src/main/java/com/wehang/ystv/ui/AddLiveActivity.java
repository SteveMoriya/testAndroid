package com.wehang.ystv.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.LogUtils;
import com.google.gson.reflect.TypeToken;
import com.wehang.txlibrary.ui.fragment.YsPushActivity;
import com.wehang.ystv.R;
import com.wehang.ystv.bo.Classification;
import com.wehang.ystv.bo.Coursewares;
import com.wehang.ystv.bo.Lives;
import com.wehang.ystv.bo.UserInfo;
import com.wehang.ystv.interfaces.UrlConstant;
import com.wehang.ystv.interfaces.result.GetClassResult;
import com.wehang.ystv.interfaces.result.GetFileResult;
import com.wehang.ystv.myappview.PickerView;
import com.wehang.ystv.myappview.XCRoundRectImageView;
import com.wehang.ystv.utils.CommonUtil;
import com.wehang.ystv.utils.ToastUtil;
import com.wehang.ystv.utils.UserLoginInfo;
import com.whcd.base.activity.AlbumActivity;
import com.whcd.base.activity.BaseActivity;
import com.whcd.base.activity.CropImageActivity;
import com.whcd.base.interfaces.BaseData;
import com.whcd.base.interfaces.BaseResult;
import com.whcd.base.interfaces.HttpTool;
import com.whcd.base.interfaces.VolleyTool;
import com.whcd.base.utils.DisplayUtils;
import com.whcd.base.utils.ImageUtils;
import com.whcd.base.widget.BottomPopupWindow;
import com.whcd.base.widget.CustomProgressDialog;
import com.whcd.base.widget.TopMenuBar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static com.whcd.base.activity.AlbumActivity.TAKE_PHOTO_REQUEST_CODE;
import static com.whcd.base.utils.ImageUtils.takePhoto;

public class AddLiveActivity extends BaseActivity implements View.OnTouchListener {

    private int price=0;
    private String title,belong,time,kejianName,brife;

    private EditText titleET,priceET,kcbrifeET;
    private TextView belongTV,timeTV,kejianTV,rmb_img;

    private RelativeLayout belong_r,time_r,kejianChose_r;

    private LinearLayout rootLayout;
    private ImageView btnClose;
    private TextView addliveTV;


    private BottomPopupWindow popupWindow,toupopWindow,belongWindow;

    private ImageView fmImg;
    private static final int UPDATAHEAD = 0x111;
    private View kejian_line;
    private LinearLayout.LayoutParams params;
    private TopMenuBar topMenuBar;
    @Override
    protected int getLayoutResId() {
        return R.layout.activity_add_live;
    }

    @Override
    protected void initTitleBar() {
        topMenuBar = (TopMenuBar) findViewById(R.id.topMenuBar);
        topMenuBar.setTitle("我的直播");
        topMenuBar.getLeftButton().setOnClickListener(this);

        Button rightButton = topMenuBar.getRightButton();
		Drawable drawable = ContextCompat.getDrawable(context,R.drawable.delete_b);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
		rightButton.setCompoundDrawables(null, null, drawable, null);
		rightButton.setOnClickListener(this);
    }


    @Override
    protected void initView() {

        rootLayout= (LinearLayout) findViewById(R.id.zb_line);
        kejian_line=findViewById(R.id.kejian_line);
        params = (LinearLayout.LayoutParams) kejian_line.getLayoutParams();
        addliveTV= findViewById(R.id.addliveTV);
        addliveTV.setOnClickListener(this);

        titleET=findViewById(R.id.titleEdt);
        priceET=findViewById(R.id.priceEdt);

        belongTV=findViewById(R.id.belong_what);
        timeTV=findViewById(R.id.begin_time);
        kejianTV=findViewById(R.id.kejian_chose);
        kcbrifeET=findViewById(R.id.kecheng_brife);

        belong_r=findViewById(R.id.belong_r);
        time_r=findViewById(R.id.time_r);
        kejianChose_r=findViewById(R.id.kejianChose_r);


        belong_r.setOnClickListener(this);
        time_r.setOnClickListener(this);
        kejianChose_r.setOnClickListener(this);


        fmImg= findViewById(R.id.zb_fmImg);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            fmImg.setClipToOutline(true);
        }
        fmImg.setOnClickListener(this);
        btnClose= findViewById(R.id.btnClose);
        btnClose.setOnClickListener(this);
        rmb_img=findViewById(R.id.rmb_img);
        priceET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!editable.toString().equals("")){
                    rmb_img.setVisibility(View.VISIBLE);
                }else {
                    rmb_img.setVisibility(View.GONE);
                }
                LogUtils.i("price",editable.toString());
            }
        });

        kcbrifeET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int width=kcbrifeET.getWidth();
                int minwidth=DisplayUtils.dip2px(AddLiveActivity.this, 158);
                if (width<minwidth){
                    width=minwidth;
                }
                int heigt= DisplayUtils.dip2px(AddLiveActivity.this, 1)/2;
                params.width=width;
                params.height=heigt;
                kejian_line.setLayoutParams(params);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                int width=kcbrifeET.getWidth();
                int minwidth=DisplayUtils.dip2px(AddLiveActivity.this, 158);
                if (width<minwidth){
                    width=minwidth;
                }
                int heigt= DisplayUtils.dip2px(AddLiveActivity.this, 1)/2;
                params.width=width;
                params.height=heigt;
                kejian_line.setLayoutParams(params);
            }
        });
    }
    private void dismissPop() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
        if (toupopWindow!=null&&toupopWindow.isShowing()){
            toupopWindow.dismiss();
        }
    }
    private List<Classification>classificationList=new ArrayList<>();
    private void getFenlei(){
        //获取分类信息和课件信息
        Map<String, String> params;
        params = new HashMap<String, String>();
        HttpTool.doPost(this, UrlConstant.FENLEI, params, true, new TypeToken<BaseResult<GetClassResult>>() {
        }.getType(), new HttpTool.OnResponseListener() {
            @Override
            public void onSuccess(BaseData data) {
                GetClassResult result= (GetClassResult) data;
                if (result.classification!=null){
                    classificationList.clear();
                    classificationList.addAll(result.classification);
                }
            }

            @Override
            public void onError(int errorCode) {
                LogUtils.i("info","no");
                UserLoginInfo.loginOverdue(context, errorCode);
            }
        });
    }
    //创建
    private void addLive(){
        Map<String, String> params;
        params = new HashMap<String, String>();
        params.put("token",UserLoginInfo.getUserToken());
        params.put("sourcePic",roomPic);
        params.put("sourceTitle",title);
        params.put("startTime",OPTIME);
        params.put("courseware",coursewareId);
        params.put("price",(price*100)+"");
        params.put("summary",brife);
        params.put("classification",belong);
        params.put("payType",(price>0?1:0)+"");
        dialog=CustomProgressDialog.show(this, "加载中", false, null);
        HttpTool.doPost(this, UrlConstant.CREATELIVE, params, true, new TypeToken<BaseResult<GetClassResult>>() {
        }.getType(), new HttpTool.OnResponseListener() {
            @Override
            public void onSuccess(BaseData data) {
                dialog.dismiss();
                ToastUtil.makeText(context,"创建直播完成",Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onError(int errorCode) {
                LogUtils.i("info","no");
                UserLoginInfo.loginOverdue(context, errorCode);
            }
        });
    }
    //编辑预告
    private void editLive(){
        Map<String, String> params;
        params = new HashMap<String, String>();
        params.put("token",UserLoginInfo.getUserToken());
        params.put("sourceId",lives.sourceId);
        params.put("sourcePic",roomPic);
        params.put("sourceTitle",title);
        params.put("startTime",OPTIME);
        params.put("courseware",coursewareId);
        params.put("price",(price*100)+"");
        params.put("summary",brife);
        params.put("classification",belong);
       // params.put("payType",(price>0?0:1)+"");
        dialog=CustomProgressDialog.show(this, "加载中", false, null);
        HttpTool.doPost(this, UrlConstant.EDITLIVE, params, true, new TypeToken<BaseResult<GetClassResult>>() {
        }.getType(), new HttpTool.OnResponseListener() {
            @Override
            public void onSuccess(BaseData data) {
                dialog.dismiss();
                ToastUtil.makeText(context,"编辑完成",Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onError(int errorCode) {
                LogUtils.i("info","no");
                dialog.dismiss();
                UserLoginInfo.loginOverdue(context, errorCode);

            }
        });
    }
    //编辑视频
    private void editVideo(){
        Map<String, String> params;
        params = new HashMap<String, String>();
        params.put("token",UserLoginInfo.getUserToken());
        params.put("sourceId",lives.sourceId);
        params.put("sourcePic",roomPic);
        params.put("sourceTitle",title);
        params.put("price",(price*100)+"");
        params.put("summary",brife);
        params.put("classification",belong);
        // params.put("payType",(price>0?0:1)+"");
        dialog=CustomProgressDialog.show(this, "加载中", false, null);
        HttpTool.doPost(this, UrlConstant.EDITVIDEO, params, true, new TypeToken<BaseResult<GetClassResult>>() {
        }.getType(), new HttpTool.OnResponseListener() {
            @Override
            public void onSuccess(BaseData data) {
                dialog.dismiss();
                ToastUtil.makeText(context,"编辑完成",Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onError(int errorCode) {
                LogUtils.i("info","no");
                dialog.dismiss();
                UserLoginInfo.loginOverdue(context, errorCode);

            }
        });
    }
    //删除
    private void deleteLive(){
        Map<String, String> params;
        params = new HashMap<String, String>();
        params.put("token",UserLoginInfo.getUserToken());
        params.put("sourceId",lives.sourceId);
        dialog=CustomProgressDialog.show(this, "加载中", false, null);
        String url=null;
        url=lives.sourceType==2?UrlConstant.DELETELIVE:UrlConstant.DELETEVIDEO;
        HttpTool.doPost(this, url, params, true, new TypeToken<BaseResult<GetClassResult>>() {
        }.getType(), new HttpTool.OnResponseListener() {
            @Override
            public void onSuccess(BaseData data) {
                dialog.dismiss();
                ToastUtil.makeText(context,"删除完成",Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onError(int errorCode) {
                LogUtils.i("info","no");
                UserLoginInfo.loginOverdue(context, errorCode);
            }
        });
    }
    private Lives lives;
    @Override
    protected void initData(Bundle bundle) {
        if (getIntent().getBundleExtra("bundle")!=null){
            bundle=getIntent().getBundleExtra("bundle");
            lives = (Lives) bundle.getSerializable("data");
        }


        if (lives==null){
            kejianName="";
            btnClose.setVisibility(View.VISIBLE);
            topMenuBar.setVisibility(View.GONE);
            getFenlei();
            addliveTV.setText("创 建");
        }else {

            if (lives.sourceType==2){
                //编辑预告
                timeTV.setText(lives.startTime+"");
                OPTIME=lives.startTime;
                if (lives.coursewareName!=null){
                    kejianTV.setText(lives.coursewareName);//课件名
                }
            }else if(lives.sourceType==3){
                //编辑视频
                topMenuBar.setTitle("我的视频");
                time_r.setVisibility(View.GONE);
                findViewById(R.id.time_Line).setVisibility(View.GONE);
                kejianChose_r.setVisibility(View.GONE);
                findViewById(R.id.kj_line).setVisibility(View.GONE);
            }
            VolleyTool.getImage( UrlConstant.IP+lives.sourcePic, fmImg, R.drawable.live_mrjz, R.drawable.live_mrjz);
            findViewById(R.id.addTs).setVisibility(View.GONE);
            titleET.setText(lives.sourceTitle);
            belongTV.setText(lives.classification);
            priceET.setText(lives.price/100+"");
           /// kejianTV.setText(lives.);
            kcbrifeET.setText(lives.summary+"");
            btnClose.setVisibility(View.GONE);
            topMenuBar.setVisibility(View.VISIBLE);
            addliveTV.setText("保 存");


            roomPic=lives.sourcePic;
            title=lives.sourceTitle;
            belong=lives.classification;
            price=lives.price/100;
            brife=lives.summary;

        }


    }
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {
            case R.id.titleEdt:
                // 解决scrollView中嵌套EditText导致不能上下滑动的问题
                v.getParent().requestDisallowInterceptTouchEvent(true);
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_UP:
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }
                break;
            case R.id.priceEdt:
                v.getParent().requestDisallowInterceptTouchEvent(true);
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_UP:
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }
            case R.id.kecheng_brife:
                v.getParent().requestDisallowInterceptTouchEvent(true);
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_UP:
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }
        }
        return false;
    }
    private String coursewareId="";
    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (CommonUtil.isFirstClick())
            return;
        Intent intent;
        switch (v.getId()){
            case R.id.title_btn_left:
                back();
                break;

            case R.id.title_btn_right:
                    deleteLive();
                break;
            case R.id.addliveTV:
                if (beforAdd()){
                   // startActivity(new Intent(this, YsPushActivity.class));
                    if (lives==null){
                        addLive();
                    }else {
                        if (lives.sourceType==2){
                            editLive();
                        }else if (lives.sourceType==3){
                            editVideo();
                        }

                    }

                }
                Log.i("addlive_need","title:"+title+","+"belong:"+belong+","+"time:"+time+","+"kejianName:"+kejianName+","+"price:"+price+","+"brife:"+brife);
                break;

            case R.id.belong_r:
                showBelongPopWindow();
                break;
            case R.id.time_r:
                showChoosetimePopupwindow();
                break;
            case R.id.kejianChose_r:
                startActivityForResult(new Intent(AddLiveActivity.this,KeJianActivity.class),115);
                break;

            case R.id.btnClose:
                finish();
                break;
            case R.id.zb_fmImg:
                showPopWindow();
                break;

            case R.id.take_photo:
                dismissPop();
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != 0) {
                    ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.CAMERA}, 0);
                } else {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != 0) {
                        ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                    } else {
                        PackageManager packageManager = context.getPackageManager();
                        if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                            capturePath= ImageUtils.takePhoto(this);
                        }
                    }
                }
                break;

            case R.id.album:
                dismissPop();
                if (ActivityCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) != 0) {
                    ActivityCompat.requestPermissions(context, new String[]{READ_EXTERNAL_STORAGE}, 1);
                } else {
                    intent = new Intent(context, AlbumActivity.class);
                    intent.putExtra("type", 0);
                    startActivityForResult(intent, AlbumActivity.TO_SELECT_PHOTO_REQUEST_CODE);
                }
                break;
            case R.id.cancel:
                dismissPop();
                break;
        }
    }


    /**
     * 选择拍照或者相册ppw
     */
    private void showPopWindow() {

        if (toupopWindow == null) {
            View popView = LayoutInflater.from(this).inflate(R.layout.view_headimg_pop, rootLayout, false);
            View contentView = popView.findViewById(R.id.pop_conent);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) contentView.getLayoutParams();
            params.gravity = Gravity.BOTTOM;
            contentView.setTag(1);
            contentView.setLayoutParams(params);
            popView.findViewById(R.id.take_photo).setOnClickListener(this);
            popView.findViewById(R.id.album).setOnClickListener(this);
            popView.findViewById(R.id.cancel).setOnClickListener(this);
            toupopWindow = new BottomPopupWindow(this, popView);
        }
        toupopWindow.showAtLocation(rootLayout, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    /**
     * 选择时间
     */

    private   String YSTR;
    private   String MSTR ;
    private String DSTR ;
    private  String MINSTR ;
    private  String HSTR ;
    private  String OPTIME=null;
    private  int numDays=30;
    private  void getDays(int s,List<String>day){
        switch (s) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                numDays = 31;
                break;
            case 4:
            case 6:
            case 9:
            case 11:
                numDays = 30;
                break;
            case 2:
                int year= Integer.parseInt(YSTR);
                if (((year % 4 == 0) && !(year % 100 == 0))
                        || (year % 400 == 0))
                    numDays = 29;
                else
                    numDays = 28;
                break;
        }
        for (int i = 1; i <=31; i++)
        {
            day.add(i+"");
        }
    }
    public void showChoosetimePopupwindow() {
        long curren = System.currentTimeMillis();
        curren += 40 * 60 * 1000;
        Date da = new Date(curren);

        SimpleDateFormat year_f=new SimpleDateFormat("yyyy");
        SimpleDateFormat month_f=new SimpleDateFormat("MM");
        SimpleDateFormat day_f=new SimpleDateFormat("dd");
        SimpleDateFormat hour_f=new SimpleDateFormat("HH");
        SimpleDateFormat min_f=new SimpleDateFormat("mm");

        YSTR=year_f.format(da);
        MSTR=month_f.format(da);
        DSTR=day_f.format(da);
        HSTR=hour_f.format(da);
        MINSTR=min_f.format(da);
        LogUtils.i("!!!!!!1"+YSTR+"_"+MSTR+"_"+DSTR+"_"+HSTR+"_"+MINSTR);
        if (popupWindow==null){
            View popView = LayoutInflater.from(this).inflate(R.layout.itemchoseitem, rootLayout, false);
            View contentView = popView.findViewById(R.id.pop_conent);
            final PickerView minute_pv,month_pv,day_pv,hour_pv;

            final List<String> hour = new ArrayList<String>();
            List<String> seconds = new ArrayList<String>();
            List<String> month = new ArrayList<String>();
            final List<String> day = new ArrayList<String>();

            for (int i =1; i <= 12; i++)
            {
                month.add(i+"");
            }
            for (int i = 0; i < 60; i++)
            {
                seconds.add(i+"");
            }

            getDays(Integer.parseInt(MSTR),day);
            for (int i = 0; i < 24; i++)
            {
                hour.add(i+"");
            }
            month_pv= (PickerView) popView.findViewById(R.id.month_pv);
            day_pv= (PickerView) popView.findViewById(R.id.second_pv);
            minute_pv= (PickerView) popView.findViewById(R.id.minute_pv);
            hour_pv= (PickerView) popView.findViewById(R.id.hour_pv);
            //

            minute_pv.setData(seconds);
            minute_pv.setSelected(Integer.parseInt(MINSTR));

            hour_pv.setData(hour);
            hour_pv.setSelected( Integer.parseInt(HSTR));

            //月和日是1开始不是0，所以的减一
            month_pv.setData(month);
            month_pv.setSelected( Integer.parseInt(MSTR)-1);

            day_pv.setData(day);
            day_pv.setSelected(Integer.parseInt(DSTR)-1);

            month_pv.setOnSelectListener(new PickerView.onSelectListener()
            {

                public int numDays;

                @Override
                public void onSelect(String text) {
                    int a= Integer.parseInt(text);
                    if (a<10){
                        MSTR="0"+text;
                    }else{
                        MSTR=text;
                    }


                    int s = Integer.parseInt(text);
                    switch (s) {
                        case 1:
                        case 3:
                        case 5:
                        case 7:
                        case 8:
                        case 10:
                        case 12:
                            numDays = 31;
                            break;
                        case 4:
                        case 6:
                        case 9:
                        case 11:
                            numDays = 30;
                            break;
                        case 2:
                            int year= Integer.parseInt(YSTR);
                            if (((year % 4 == 0) && !(year % 100 == 0))
                                    || (year % 400 == 0))
                                numDays = 29;
                            else
                                numDays = 28;
                            break;
                    }
                    day.clear();
                    for (int i=1;i<=numDays;i++){
                        day.add(i+"");
                    }
                    LogUtils.i("qiuqiu",numDays+"");
                    day_pv.setData(day);
                }
            });
            day_pv.setOnSelectListener(new PickerView.onSelectListener() {
                @Override
                public void onSelect(String text) {
                    int a= Integer.parseInt(text);
                    if (a<10){
                        DSTR="0"+text;
                    }else
                        DSTR=text;
                }
            });
            hour_pv.setOnSelectListener(new PickerView.onSelectListener() {
                @Override
                public void onSelect(String text) {
                    int a= Integer.parseInt(text);
                    if (a<10){
                        HSTR="0"+text;
                    }else
                        HSTR=text;
                }
            });
            minute_pv.setOnSelectListener(new PickerView.onSelectListener() {
                @Override
                public void onSelect(String text) {
                    int a= Integer.parseInt(text);
                    if (a<10){
                        MINSTR="0"+text;
                    }else
                        MINSTR=text;
                }
            });

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) contentView.getLayoutParams();
            params.gravity = Gravity.BOTTOM;
            contentView.setTag(1);
            contentView.setLayoutParams(params);
            popView.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (isValidContext(context) && popupWindow.isShowing()) {
                        popupWindow.dismiss();
                    }

                }
            });
            popView.findViewById(R.id.sure).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (isValidContext(context) && popupWindow.isShowing()) {
                        popupWindow.dismiss();
                    }
                    String str=YSTR+"-"+MSTR+"-"+DSTR+" "+HSTR+":"+MINSTR+":"+"00";
                    OPTIME=str;
                    LogUtils.i("!!!!!",str);
                    timeTV.setText(OPTIME+"");
                }
            });
            popupWindow = new BottomPopupWindow(this, popView);
        }
        popupWindow.showAtLocation(rootLayout, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    /**
     * 选择所属分类
     */
    private void showBelongPopWindow() {
        List<String>belongList=new ArrayList<>();
        if (classificationList.size()==0){
            getFenlei();
            return;
        }
        for (int i=0;i<classificationList.size();i++){
            belongList.add(classificationList.get(i).classificationName);
        }
        if (belongWindow == null) {
            View popView = LayoutInflater.from(this).inflate(R.layout.view_belong_pop, rootLayout, false);
            View contentView = popView.findViewById(R.id.pop_conent);
            final PickerView belong_pv;
            belong_pv= (PickerView) popView.findViewById(R.id.belong_pv);

            belong_pv.setData(belongList);
            if (belongList.size()>3){
                belong_pv.setSelected(2);
                belong=belongList.get(2);
            }else {
                belong_pv.setSelected(0);
                belong=belongList.get(0);
            }
            belong_pv.setOnSelectListener(new PickerView.onSelectListener() {
                @Override
                public void onSelect(String text) {
                    belong=text;
                }
            });
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) contentView.getLayoutParams();
            params.gravity = Gravity.BOTTOM;
            contentView.setTag(1);
            contentView.setLayoutParams(params);

            popView.findViewById(R.id.b_cancel).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (isValidContext(context) && belongWindow.isShowing()) {
                        belongWindow.dismiss();
                    }
                }
            });
            popView.findViewById(R.id.b_sure).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (isValidContext(context) && belongWindow.isShowing()) {
                        belongWindow.dismiss();
                    }
                    belongTV.setText(belong);

                }
            });
            belongWindow = new BottomPopupWindow(this, popView);
        }
        belongWindow.showAtLocation(rootLayout, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    private String capturePath;
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 0:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != 0) {
                        ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                    } else {
                        capturePath= takePhoto(this);
                    }
                } else {
                    ToastUtil.makeText(context,"没有拍照权限", Toast.LENGTH_SHORT).show();
                }
                break;
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(context, AlbumActivity.class);
                    intent.putExtra("type", 0);
                    startActivityForResult(intent, AlbumActivity.TO_ALBUM_REQUEST_CODE);
                } else {
                    ToastUtil.makeText(context,"没有读取相册权限", Toast.LENGTH_SHORT).show();
                }
                break;
            case 2:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePhoto(this);
                } else {
                    ToastUtil.makeText(context,"没有保存文件权限", Toast.LENGTH_SHORT).show();
                }
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
       /* if (requestCode == DIALOG_LIVE_PAGE_REQUESTCODE) {
            isStartLive = true;
        } else {
            UMShareAPI.get(StartLiveActivity.this).onActivityResult(requestCode, resultCode, data);
        }*/
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                //选择图片回掉
                case AlbumActivity.TO_SELECT_PHOTO_REQUEST_CODE:
                    //jiazaibediImg(data.getStringExtra("cropImagePath"));
                    capturePath = data.getStringExtra("cropImagePath");
                    updatPic(capturePath);

                    break;
                //拍照回掉
                case AlbumActivity.TO_CROP_PHOTO_REQUEST_CODE:
                    //jiazaibediImg(data.getStringExtra("cropImagePath"));
                    capturePath = data.getStringExtra("cropImagePath");
                    updatPic(capturePath);
                    break;
                case TAKE_PHOTO_REQUEST_CODE:
                    Intent intents = new Intent(context, CropImageActivity.class);
                    intents.putExtra("path", capturePath);
                    intents.putExtra("type", 0);
                    intents.putExtra("cropWidth", 100);
                    intents.putExtra("cropHeight", 100);
                    startActivityForResult(intents, AlbumActivity.TO_CROP_PHOTO_REQUEST_CODE);
                    break;


                case UPDATAHEAD:
                    capturePath=data.getStringExtra("cropImagePath");
                    break;

                case 115:
                    Bundle bundle=data.getBundleExtra("bundle");
                    Coursewares coursewares= (Coursewares) bundle.getSerializable("data");
                    coursewareId= coursewares.coursewareId;
                    kejianTV.setText(coursewares.coursewareName);
                    break;
                default:
                    capturePath=data.getStringExtra("cropImagePath");
                    break;
            }
        }
    }
    private String roomPic;
    private void updatPic(String capturePath){
        dialog=CustomProgressDialog.show(this, "加载中", false, null);
        HttpTool.doUpload(this, UrlConstant.UPLOADFILE, capturePath, null, true, new TypeToken<BaseResult<GetFileResult>>() {
        }.getType(), new HttpTool.OnResponseListener() {
            @Override
            public void onSuccess(BaseData data) {
                dialog.dismiss();
                findViewById(R.id.addTs).setVisibility(View.GONE);
                GetFileResult fileResult = (GetFileResult) data;
                //capturePath = fileResult.picUrl;
                roomPic = fileResult.url;
                LogUtils.a(roomPic);
                VolleyTool.getImage( UrlConstant.IP+roomPic, fmImg, R.drawable.live_mrjz, R.drawable.live_mrjz);
            }

            @Override
            public void onError(int errorCode) {
                dialog.dismiss();
                ToastUtil.makeText(context,"上传失败，请重试", Toast.LENGTH_SHORT).show();
            }
        });
        //ToastUtil.makeText(context,capturePath, Toast.LENGTH_SHORT).show();
    }
    private boolean beforAdd(){
        title=titleET.getText().toString().trim();
        if (TextUtils.isEmpty(roomPic)){
            ToastUtil.makeText(context, "请设置直播封面", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(title)){
            ToastUtil.makeText(context, "请输入直播标题", Toast.LENGTH_SHORT).show();
            titleET.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(belong)){
            ToastUtil.makeText(context, "请选择分类", Toast.LENGTH_SHORT).show();
            showBelongPopWindow();
            return false;
        }
        if (lives!=null){
            if (lives.sourceType==3){

            }else {
                if (TextUtils.isEmpty(OPTIME)){
                    ToastUtil.makeText(context, "请选择时间", Toast.LENGTH_SHORT).show();
                    showChoosetimePopupwindow();
                    return false;
                }else {
                    SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    try {
                        Date date=sdf.parse(OPTIME);
                        Calendar calendar = Calendar.getInstance();
                        calendar.add(Calendar.MINUTE,30);
                        Date now = calendar.getTime();
                        //date.compareTo(now);
                        Calendar calendar1=Calendar.getInstance();
                        calendar1.add(Calendar.DAY_OF_YEAR,30);
                        Date futher=calendar1.getTime();

                        boolean a= date.after(now);
                        boolean b=date.after(futher);
                        LogUtils.i("data_aa",a+"");
                        if (a==false){
                            ToastUtil.makeText(context,"请选择晚于当前半小时后的时间", Toast.LENGTH_SHORT).show();
                            showChoosetimePopupwindow();
                            return false;
                        }
                        if (b){
                            ToastUtil.makeText(context,"请选择不超过当前30天后的时间", Toast.LENGTH_SHORT).show();
                            showChoosetimePopupwindow();
                            return false;
                        }
                        //date.before(now);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        }else {
            if (TextUtils.isEmpty(OPTIME)){
                ToastUtil.makeText(context, "请选择时间", Toast.LENGTH_SHORT).show();
                showChoosetimePopupwindow();
                return false;
            }else {
                SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    Date date=sdf.parse(OPTIME);
                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.MINUTE,30);
                    Date now = calendar.getTime();
                    //date.compareTo(now);
                    Calendar calendar1=Calendar.getInstance();
                    calendar1.add(Calendar.DAY_OF_YEAR,30);
                    Date futher=calendar1.getTime();

                    boolean a= date.after(now);
                    boolean b=date.after(futher);
                    LogUtils.i("data_aa",a+"");
                    if (a==false){
                        ToastUtil.makeText(context,"请选择晚于当前半小时后的时间", Toast.LENGTH_SHORT).show();
                        showChoosetimePopupwindow();
                        return false;
                    }
                    if (b){
                        ToastUtil.makeText(context,"请选择不超过当前30天后的时间", Toast.LENGTH_SHORT).show();
                        showChoosetimePopupwindow();
                        return false;
                    }
                    //date.before(now);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }


        if (TextUtils.isEmpty(priceET.getText().toString().trim())){
            ToastUtil.makeText(context, "请输入价格", Toast.LENGTH_SHORT).show();
            return false;
        }else {
            price= Integer.parseInt(priceET.getText().toString().trim());
        }
        brife=kcbrifeET.getText().toString().trim();
        if (TextUtils.isEmpty(brife)){
            ToastUtil.makeText(context, "请输入课程简介", Toast.LENGTH_SHORT).show();
            kcbrifeET.requestFocus();
            return false;
        }
        return true;
    }


}
