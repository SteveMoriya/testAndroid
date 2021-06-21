package com.wehang.ystv.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
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
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.LogUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.callback.StringCallback;
import com.squareup.picasso.Picasso;
import com.wehang.ystv.Constant;
import com.wehang.ystv.R;
import com.wehang.ystv.bo.Hospital;
import com.wehang.ystv.bo.Province;
import com.wehang.ystv.bo.QueryDepartment;
import com.wehang.ystv.bo.QueryTitle;
import com.wehang.ystv.bo.UserInfo;
import com.wehang.ystv.interfaces.UrlConstant;
import com.wehang.ystv.interfaces.result.GetFileResult;
import com.wehang.ystv.interfaces.result.GetHospitalResult;
import com.wehang.ystv.interfaces.result.GetLvesResult;
import com.wehang.ystv.interfaces.result.GetProvinceResult;
import com.wehang.ystv.myappview.PickerView;
import com.wehang.ystv.myappview.WaterWaveView;
import com.wehang.ystv.myappview.WaterWaveView2;
import com.wehang.ystv.utils.CommonUtil;
import com.wehang.ystv.utils.ToastUtil;
import com.wehang.ystv.utils.UserLoginInfo;
import com.whcd.base.activity.AlbumActivity;
import com.whcd.base.activity.BaseActivity;
import com.whcd.base.activity.CropImageActivity;
import com.whcd.base.activity.ImagePagerActivity;
import com.whcd.base.component.photoview.Compat;
import com.whcd.base.interfaces.BaseData;
import com.whcd.base.interfaces.BaseResult;
import com.whcd.base.interfaces.HttpTool;
import com.whcd.base.interfaces.VolleyTool;
import com.whcd.base.utils.ImageUtils;
import com.whcd.base.widget.BottomPopupWindow;
import com.whcd.base.widget.CustomProgressDialog;
import com.whcd.base.widget.TopMenuBar;

import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import okhttp3.Call;
import okhttp3.Response;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static com.whcd.base.activity.AlbumActivity.TAKE_PHOTO_REQUEST_CODE;
import static com.whcd.base.utils.ImageUtils.takePhoto;

public class PerfectDataActivity extends BaseActivity {
    private TopMenuBar topMenuBar;
    private EditText nameET;
    private TextView sexTV;
    private int sex=-1;

    private static final int REQUEST_SEX = 0x113;
    private static final int ZDY_YY = 0x124;
    private static final int ZDY_KS = 0x125;
    private static final int ZDY_ZC = 0x126;

    private static final int FLAG_ONE = 0X0001;
    private static final int FLAG_TWO = 0X0002;
    private int max_progress = 100;
    private int progress;
    private int needProgress=0;
    private LinearLayout rootLayout;
    private ImageView headImg;
    private BottomPopupWindow popupWindow;//选择头像
    private void showPopWindow() {
        if (popupWindow == null) {
            View popView = LayoutInflater.from(this).inflate(R.layout.view_headimg_pop, rootLayout, false);
            View contentView = popView.findViewById(R.id.pop_conent);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) contentView.getLayoutParams();
            params.gravity = Gravity.BOTTOM;
            contentView.setTag(1);
            contentView.setLayoutParams(params);
            popView.findViewById(R.id.take_photo).setOnClickListener(this);
            popView.findViewById(R.id.album).setOnClickListener(this);
            popView.findViewById(R.id.cancel).setOnClickListener(this);
            popupWindow = new BottomPopupWindow(this, popView);
        }
        popupWindow.showAtLocation(rootLayout, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    private TextView perfect_Dqname,perfect_Yiname,perfect_Ksname,perfect_Zcname;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            progress++;
            LogUtils.i("msgwhat",msg.what+"");
            switch (msg.what) {

                case FLAG_ONE:
                    if (progress <= max_progress){
                        waveView.setProgressSync(progress);
                        waveView2.setProgressSync(progress);
                        if (progress==max_progress){
                            waveView.setVisibility(View.GONE);
                            waveView2.setVisibility(View.GONE);
                        }
                        sendEmptyMessageDelayed(FLAG_ONE, 100);
                    }else {
                    }
                case 99:

                    int progress=msg.arg1;
                    handler.removeMessages(100);
                    Message message=Message.obtain();
                    message.arg1=progress;
                    message.what=100;
                    handler.sendMessageDelayed(message,20);
                    break;
                case 100:
                    LogUtils.i("needprogress",needProgress);
                    int progress1=msg.arg1;
                    if (needProgress<progress1){
                        needProgress++;
                        waveView.setProgressSync(needProgress);
                        waveView2.setProgressSync(needProgress);
                        handler.removeMessages(100);
                        Message message1=Message.obtain();
                        message1.arg1=progress1;
                        message1.what=100;
                        handler.sendMessageDelayed(message1,20);
                    }
                    if (needProgress==100){
                        needProgress=0;
                        handler.removeMessages(100);
                        waveView.setVisibility(View.GONE);
                        waveView2.setVisibility(View.GONE);
                    }

            }
        }
    };

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_perfect_data;
    }
    private BottomPopupWindow placePopWindow,hospitalPopWindow,departmentPopWindow,zhichengPopWindow;


    /**
     * 选择地区
     */
    private String sf,sq,dq;
    private List<Province> provinceList=new ArrayList<>();
    private void getProvinceList(){
        provinceList.clear();
        Map<String, String> params = new HashMap<String, String>();

        HttpTool.doPost(PerfectDataActivity.this, UrlConstant.QUERYADDR, params, false, new TypeToken<BaseResult<GetProvinceResult>>() {
        }.getType(), new HttpTool.OnResponseListener() {
            @Override
            public void onSuccess(BaseData data) {

                GetProvinceResult result = (GetProvinceResult) data;
                provinceList.addAll(result.province);
                LogUtils.i("province",provinceList.get(0).toString());
            }

            @Override
            public void onError(int errorCode) {
                UserLoginInfo.loginOverdue(PerfectDataActivity.this, errorCode);

            }
        });
    }
    private List<String> sfList=new ArrayList<>();
    private List<String> sqList=new ArrayList<>();
    private List<String> dqList=new ArrayList<>();
    private void showPlacePopWindow() {
        if (placePopWindow == null) {
            View popView = LayoutInflater.from(this).inflate(R.layout.itemchose_place_item, rootLayout, false);
            View contentView = popView.findViewById(R.id.pop_conent);
            final PickerView sf_pv,sq_pv,dq_pv;
            sf_pv= (PickerView) popView.findViewById(R.id. sf_pv);
            sq_pv=popView.findViewById(R.id.sq_pv);
            dq_pv=popView.findViewById(R.id.dq_pv);
            if (sfList.size()==0){
                getsfList( sf_pv, sq_pv,dq_pv);
            }
            sf_pv.setOnSelectListener(new PickerView.onSelectListener() {
                @Override
                public void onSelect(String text) {
                    sf=text;
                    for (int i=0;i<provinceList.size();i++){
                        if (sf.equals(provinceList.get(i).name)){
                            curChoseProvince=provinceList.get(i);
                            getsqList(curChoseProvince,sq_pv,dq_pv);
                            break;
                        }
                    }

                }
            });
            sq_pv.setOnSelectListener(new PickerView.onSelectListener() {
                @Override
                public void onSelect(String text) {
                    sq=text;
                    for (int i=0;i<curChoseProvince.children.size();i++){
                        LogUtils.i("dqlist",sq+curChoseCity.name);
                        if (sq.equals(curChoseProvince.children.get(i).name)){
                            curChoseCity=curChoseProvince.children.get(i);
                            LogUtils.i("dqlist",curChoseCity.name);
                            getdqList(curChoseCity,dq_pv);
                            break;
                        }
                    }
                }
            });
            dq_pv.setOnSelectListener(new PickerView.onSelectListener() {
                @Override
                public void onSelect(String text) {
                    dq=text;
                    for (int i=0;i<curChoseCity.children.size();i++){
                        LogUtils.i("dqlist",sq+curChoseCity.name);
                        if (dq.equals(curChoseCity.children.get(i).name)){
                            curChoseDistrict=curChoseCity.children.get(i);
                            break;
                        }
                    }
                }
            });
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) contentView.getLayoutParams();
            params.gravity = Gravity.BOTTOM;
            contentView.setTag(1);
            contentView.setLayoutParams(params);

            popView.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (isValidContext(context) && placePopWindow.isShowing()) {
                        placePopWindow.dismiss();
                    }

                }
            });
            popView.findViewById(R.id.sure).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (isValidContext(context) && placePopWindow.isShowing()) {
                        placePopWindow.dismiss();
                    }
                    perfect_Dqname.setText(sf+sq+dq);
                    getHospitalList();

                }
            });
            placePopWindow = new BottomPopupWindow(this, popView);
        }
        placePopWindow.showAtLocation(rootLayout, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }
    private Province curChoseProvince;
    private Province.ChildrenBeanX curChoseCity;
    private Province.ChildrenBeanX.ChildrenBean curChoseDistrict;
    private void getsfList(PickerView sf_pv,PickerView sq_pv,PickerView dq_pv){

        for (int i=0;i<provinceList.size();i++){
            sfList.add(provinceList.get(i).name);
        }
        sf_pv.setData(sfList);
        if (sfList.size()>2){
            sf_pv.setSelected(2);
            curChoseProvince=provinceList.get(2);
            sf=sfList.get(2);
        }else {
            sf_pv.setSelected(0);
            curChoseProvince=provinceList.get(0);
            sf=sfList.get(0);
        }

        getsqList(curChoseProvince,sq_pv,dq_pv);
    }
    /**
     * 通过省份获取市区
      */
    private void getsqList(Province curChoseProvince,PickerView sq_Pv,PickerView dq_Pv){
        sqList.clear();

        for (int i=0;i<curChoseProvince.children.size();i++){
            sqList.add(curChoseProvince.children.get(i).name);
        }
        sq_Pv.setData(sqList);
        if (sqList.size()>2){
            sq_Pv.setSelected(2);
            curChoseCity=curChoseProvince.children.get(2);
            sq=sqList.get(2);
        }else {
            sq_Pv.setSelected(0);
            curChoseCity=curChoseProvince.children.get(0);
            sq=sqList.get(0);
        }
        getdqList(curChoseCity,dq_Pv);
    }
    /**
     * 通过市区获取地区
     */
    private void getdqList(Province.ChildrenBeanX curChoseCity, PickerView pickerView){
        dqList.clear();
        LogUtils.i("dqlist",curChoseCity.name);
        for (int i=0;i<curChoseCity.children.size();i++){
            dqList.add(curChoseCity.children.get(i).name);
        }
        LogUtils.i("dqlist",dqList.toString());
        pickerView.setData(dqList);
        if (dqList.size()>2){
            pickerView.setSelected(2);
            curChoseDistrict=curChoseCity.children.get(2);
            dq=dqList.get(2);
        }else {
            pickerView.setSelected(0);
            curChoseDistrict=curChoseCity.children.get(0);
            dq=dqList.get(0);
        }

    }
    private UserInfo userInfo;
    @Override
    protected void initTitleBar() {
        topMenuBar = (TopMenuBar) findViewById(R.id.topMenuBar);
        topMenuBar.setTitle("完善资料");
        topMenuBar.getLeftButton().setOnClickListener(this);
        Bundle bundle=getIntent().getBundleExtra("bundle");
        userInfo = (UserInfo) bundle.getSerializable("userInfo");
    }
    //双波浪线，偏移量不一样
    private WaterWaveView waveView;
    private WaterWaveView2 waveView2;
    @Override
    protected void initView() {
        waveView= (WaterWaveView) findViewById(R.id.wpv);
        waveView.setMax(100);
        //#71F4C1
        waveView.setWaveColor("#5061f25e");
        waveView2=(WaterWaveView2) findViewById(R.id.wpv2);
        //#287CF9
        waveView2.setMax(100);
        waveView2.setWaveColor("#50287CF9");
        rootLayout=findViewById(R.id.rootLayout);

        perfect_Dqname=findViewById(R.id.perfect_Dqname);
        perfect_Yiname=findViewById(R.id.perfect_Yiname);
        perfect_Ksname=findViewById(R.id.perfect_Ksname);
        perfect_Zcname=findViewById(R.id.perfect_Zcname);
        headImg=findViewById(R.id.headImg);
        headImg.setOnClickListener(this);
        findViewById(R.id.perfect_Dqyt).setOnClickListener(this);
        findViewById(R.id.perfect_Yiyt).setOnClickListener(this);
        findViewById(R.id.perfect_Ksyt).setOnClickListener(this);
        findViewById(R.id.perfect_Zcyt).setOnClickListener(this);


        //自定义
        findViewById(R.id.zdyYy).setOnClickListener(this);
        findViewById(R.id.zdyKs).setOnClickListener(this);
        findViewById(R.id.zdyZc).setOnClickListener(this);

        findViewById(R.id.regist_confirmTv).setOnClickListener(this);


        nameET=findViewById(R.id.nameTv);
        nameET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int b, int i1, int i2) {
                if (s.toString().contains(" ")) {
                    String[] str = s.toString().split(" ");
                    String str1 = "";
                    for (int i = 0; i < str.length; i++) {
                        str1 += str[i];
                    }
                    nameET.setText(str1);

                    nameET.setSelection(b);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        sexTV=findViewById(R.id.sexImg);

        findViewById(R.id.sexLayout).setOnClickListener(this);
    }
    /**
     * 选择所属医院
     */
    private String hospitalName;
    private List<Hospital>hospitalList=new ArrayList<>();
    private void getHospitalList(){
        hospitalList.clear();
        Map<String, String> params = new HashMap<String, String>();
        params.put("addrId",curChoseDistrict.id+"");
        HttpTool.doPost(PerfectDataActivity.this, UrlConstant.QUERYHOSPITAL, params, false, new TypeToken<BaseResult<GetHospitalResult>>() {
        }.getType(), new HttpTool.OnResponseListener() {
            @Override
            public void onSuccess(BaseData data) {

                GetHospitalResult result = (GetHospitalResult) data;
                if (result.hospital!=null){
                    if (result.hospital.size()>0){
                        hospitalList.addAll(result.hospital);
                    }
                }
                if (hospitalList.size()==0){
                    ToastUtil.makeText(context,"暂无医院信息",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(int errorCode) {
                UserLoginInfo.loginOverdue(PerfectDataActivity.this, errorCode);

            }
        });
    }
    private void showHospitalPopWindow() {
        List<String>hospitalList2=new ArrayList<>();
        for (int i=0;i<hospitalList.size();i++){
            hospitalList2.add(hospitalList.get(i).hospital_name);
        }
        if (hospitalPopWindow == null) {
            View popView = LayoutInflater.from(this).inflate(R.layout.view_belong_pop, rootLayout, false);
            View contentView = popView.findViewById(R.id.pop_conent);
            TextView textView=popView.findViewById(R.id.title_pop);
            textView.setText("选择医院");
            final PickerView belong_pv;
            belong_pv= (PickerView) popView.findViewById(R.id.belong_pv);
            if (hospitalList2.size()>2){
                belong_pv.setData(hospitalList2);
                belong_pv.setSelected(2);
                hospitalName=hospitalList2.get(2);
            }else {
                belong_pv.setData(hospitalList2);
                belong_pv.setSelected(0);
                hospitalName=hospitalList2.get(0);
            }

            belong_pv.setOnSelectListener(new PickerView.onSelectListener() {
                @Override
                public void onSelect(String text) {
                    hospitalName=text;
                }
            });
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) contentView.getLayoutParams();
            params.gravity = Gravity.BOTTOM;
            contentView.setTag(1);
            contentView.setLayoutParams(params);

            popView.findViewById(R.id.b_cancel).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (isValidContext(context) && hospitalPopWindow.isShowing()) {
                        hospitalPopWindow.dismiss();
                    }

                }
            });
            popView.findViewById(R.id.b_sure).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (isValidContext(context) && hospitalPopWindow.isShowing()) {
                        hospitalPopWindow.dismiss();
                    }
                    perfect_Yiname.setText(hospitalName);

                }
            });
            hospitalPopWindow = new BottomPopupWindow(this, popView);
        }
        hospitalPopWindow.showAtLocation(rootLayout, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }
    /**
     * 选择所属职位
     */
    private String zhiweiName;
    private List<QueryTitle>titleList=new ArrayList<>();
    private void getTitiltList(){
        hospitalList.clear();
        Map<String, String> params = new HashMap<String, String>();
        HttpTool.doPost(PerfectDataActivity.this, UrlConstant.QUERYTITLE, params, false, new TypeToken<BaseResult<GetHospitalResult>>() {
        }.getType(), new HttpTool.OnResponseListener() {
            @Override
            public void onSuccess(BaseData data) {

                GetHospitalResult result = (GetHospitalResult) data;
                if (result.title!=null){
                    if (result.title.size()>0){
                        titleList.addAll(result.title);
                    }
                }
                if (titleList.size()==0){
                    ToastUtil.makeText(context,"暂无职称信息",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(int errorCode) {
                UserLoginInfo.loginOverdue(PerfectDataActivity.this, errorCode);

            }
        });
    }
    private void showzhichengPopWindow() {
        List<String>hospitalList=new ArrayList<>();
        for (int i=0;i<titleList.size();i++){
            hospitalList.add(titleList.get(i).titleName);
        }
        if (zhichengPopWindow == null) {
            View popView = LayoutInflater.from(this).inflate(R.layout.view_belong_pop, rootLayout, false);
            View contentView = popView.findViewById(R.id.pop_conent);
            TextView textView=popView.findViewById(R.id.title_pop);
            textView.setText("选择职称");
            final PickerView belong_pv;
            belong_pv= (PickerView) popView.findViewById(R.id.belong_pv);
            belong_pv.setData(hospitalList);
            if (titleList.size()>2){
                belong_pv.setSelected(2);
                zhiweiName=hospitalList.get(2);
            }else {
                belong_pv.setSelected(0);
                zhiweiName=hospitalList.get(0);
            }

            belong_pv.setOnSelectListener(new PickerView.onSelectListener() {
                @Override
                public void onSelect(String text) {
                    zhiweiName=text;
                }
            });
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) contentView.getLayoutParams();
            params.gravity = Gravity.BOTTOM;
            contentView.setTag(1);
            contentView.setLayoutParams(params);

            popView.findViewById(R.id.b_cancel).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (isValidContext(context) && zhichengPopWindow.isShowing()) {
                        zhichengPopWindow.dismiss();
                    }

                }
            });
            popView.findViewById(R.id.b_sure).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (isValidContext(context) && zhichengPopWindow.isShowing()) {
                        zhichengPopWindow.dismiss();
                    }
                    perfect_Zcname.setText(zhiweiName);

                }
            });
            zhichengPopWindow = new BottomPopupWindow(this, popView);
        }
        zhichengPopWindow.showAtLocation(rootLayout, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }
    /**
     * 选择所属科室
     */
    private String departmentName;
    private List<QueryDepartment>departmentList=new ArrayList<>();
    private void getDepartmentList(){
        hospitalList.clear();
        Map<String, String> params = new HashMap<String, String>();
        HttpTool.doPost(PerfectDataActivity.this, UrlConstant.QUERYDEPARtTMENT, params, false, new TypeToken<BaseResult<GetHospitalResult>>() {
        }.getType(), new HttpTool.OnResponseListener() {
            @Override
            public void onSuccess(BaseData data) {

                GetHospitalResult result = (GetHospitalResult) data;
                if (result.department!=null){
                    if (result.department.size()>0){
                        departmentList.addAll(result.department);
                    }
                }
                if (departmentList.size()==0){
                    ToastUtil.makeText(context,"暂无科室信息",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(int errorCode) {
                UserLoginInfo.loginOverdue(PerfectDataActivity.this, errorCode);

            }
        });
    }
    private void showDepartmentPopWindow() {
        List<String>hospitalList=new ArrayList<>();
        for (int i=0;i<departmentList.size();i++){
            hospitalList.add(departmentList.get(i).departmentName);
        }
        if (departmentPopWindow == null) {
            View popView = LayoutInflater.from(this).inflate(R.layout.view_belong_pop, rootLayout, false);
            View contentView = popView.findViewById(R.id.pop_conent);
            TextView textView=popView.findViewById(R.id.title_pop);
            textView.setText("选择科室");
            final PickerView belong_pv;
            belong_pv= (PickerView) popView.findViewById(R.id.belong_pv);
            belong_pv.setData(hospitalList);
            if (departmentList.size()>2){
                belong_pv.setSelected(2);
                departmentName=hospitalList.get(2);
            }else {
                belong_pv.setSelected(0);
                departmentName=hospitalList.get(0);
            }
            belong_pv.setOnSelectListener(new PickerView.onSelectListener() {
                @Override
                public void onSelect(String text) {
                    departmentName=text;
                }
            });
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) contentView.getLayoutParams();
            params.gravity = Gravity.BOTTOM;
            contentView.setTag(1);
            contentView.setLayoutParams(params);

            popView.findViewById(R.id.b_cancel).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (isValidContext(context) && departmentPopWindow.isShowing()) {
                        departmentPopWindow.dismiss();
                    }

                }
            });
            popView.findViewById(R.id.b_sure).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (isValidContext(context) && departmentPopWindow.isShowing()) {
                        departmentPopWindow.dismiss();
                    }
                    perfect_Ksname.setText(departmentName);

                }
            });
            departmentPopWindow = new BottomPopupWindow(this, popView);
        }
        departmentPopWindow.showAtLocation(rootLayout, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }
    @Override
    protected void initData(Bundle bundle) {
        getProvinceList();
        getDepartmentList();
        getTitiltList();
        userInfo.iconUrl="default.jpg";
        //一开始默认加载头像
        VolleyTool.getImage(UrlConstant.IP + userInfo.iconUrl, headImg, R.drawable.default_portrait, R.drawable.default_portrait);
    }
    private void dismissPop() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
    }
    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (CommonUtil.isFirstClick())
        {
            return;
        }
        Map<String, String> params;
        Intent intent;
        switch (v.getId()){
            case R.id.title_btn_left:
                back();
                break;
            case R.id.headImg:
                showPopWindow();
                break;
            case R.id.perfect_Dqyt:
                if (provinceList.size()==0){
                    getProvinceList();
                    ToastUtil.makeText(context,"正在获取地区信息，请稍后再试",Toast.LENGTH_LONG).show();
                    return;
                }
                showPlacePopWindow();
                break;
            case R.id.perfect_Yiyt:
                if (TextUtils.isEmpty(dq)){
                    ToastUtil.makeText(context,"请先选择地区",Toast.LENGTH_LONG).show();
                    return;
                }else {
                    if (hospitalList.size()==0){
                        getHospitalList();
                        ToastUtil.makeText(context,"正在获取医院信息，请稍后再试",Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                showHospitalPopWindow();
                break;
            case R.id.perfect_Ksyt:
                if (departmentList.size()==0){
                    getDepartmentList();
                    ToastUtil.makeText(context,"正在获取科室信息，请稍后再试",Toast.LENGTH_LONG).show();
                    return;
                }
                showDepartmentPopWindow();
                break;
            case R.id.perfect_Zcyt:
                if (titleList.size()==0){
                    getTitiltList();
                    ToastUtil.makeText(context,"正在获取职称信息，请稍后再试",Toast.LENGTH_LONG).show();
                    return;
                }
                showzhichengPopWindow();

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
            case R.id.regist_confirmTv:
                judgeEdit();
                params = new HashMap<String, String>();
                params.put("iconUrl",userInfo.iconUrl);
                params.put("phone", userInfo.phone);
                params.put("password", userInfo.password);
                params.put("name", userInfo.name);
                params.put("sex", userInfo.sex+"");
                params.put("province", userInfo.province);
                params.put("city", userInfo.city);
                params.put("district", userInfo.district);
                params.put("hospital", userInfo.hospital);
                params.put("department",userInfo.department);
                params.put("title", userInfo.title);

                dialog = CustomProgressDialog.show(context, "加载中...", true, null);
                HttpTool.doPost(this, UrlConstant.REGISTER, params, true, new TypeToken<BaseResult<UserInfo>>() {
                }.getType(), new HttpTool.OnResponseListener() {
                    @Override
                    public void onSuccess(BaseData data) {
                        dialogDismiss();
                        LogUtils.i("info","yes");
                        userInfo = (UserInfo) data;
                        UserLoginInfo.userInfo=userInfo;
                        UserLoginInfo.districtId=curChoseDistrict.id+"";
                        finish();
                    }

                    @Override
                    public void onError(int errorCode) {
                        LogUtils.i("info","no");
                        //codeTv.setText("获取验证码");

                        dialogDismiss();
                    }
                });
                break;
            case R.id.sexLayout:
                intent = new Intent(context, UpDataSexActivity.class);
                //intent.putExtra("sex", userInfo.sex);

                intent.putExtra("sex", sex);
                intent.putExtra("type",0);
                startActivityForResult(intent, REQUEST_SEX);
                break;
            case R.id.zdyYy:
                intent = new Intent(context, ZdyActivity.class);
                intent.putExtra("name","医院");
                startActivityForResult(intent, ZDY_YY);
                break;
            case R.id.zdyKs:
                intent = new Intent(context, ZdyActivity.class);
                intent.putExtra("name","科室");
                startActivityForResult(intent, ZDY_KS);
                break;
            case R.id.zdyZc:
                intent = new Intent(context, ZdyActivity.class);
                intent.putExtra("name","职称");
                startActivityForResult(intent, ZDY_ZC);
                break;
        }
    }
    private String iconUrlKey="default.jpg";
    private void judgeEdit(){
        if (iconUrlKey.equals("")){
            ToastUtil.makeText(context, "请上传头像", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(nameET.getText().toString().trim())){
            ToastUtil.makeText(context, "请输入名字", Toast.LENGTH_SHORT).show();
            nameET.requestFocus();
            return;
        }
        if (sex==-1){
            ToastUtil.makeText(context, "请选择性别", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(perfect_Dqname.getText().toString().trim())){
            ToastUtil.makeText(context, "请选择地区", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(perfect_Yiname.getText().toString().trim())){
            ToastUtil.makeText(context, "请选择医院", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(perfect_Ksname.getText().toString().trim())){
            ToastUtil.makeText(context, "请选择科室", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(perfect_Zcname.getText().toString().trim())){
            ToastUtil.makeText(context, "请选择职位", Toast.LENGTH_SHORT).show();
            return;
        }
        userInfo.name=nameET.getText().toString().trim();
        userInfo.sex=sex;
        userInfo.province=sf;
        userInfo.city=sq;
        userInfo.district=dq;
        userInfo.hospital=perfect_Yiname.getText().toString().trim();
        userInfo.department=perfect_Ksname.getText().toString().trim();
        userInfo.title=perfect_Zcname.getText().toString().trim();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtils.a("C","get");
        if (resultCode == RESULT_OK) {
            LogUtils.a("++++", "ok" + requestCode);
            switch (requestCode) {

                //选择图片回掉
                case AlbumActivity.TO_SELECT_PHOTO_REQUEST_CODE:
                    LogUtils.a("++++", 1 + data.getStringExtra("cropImagePath"));
                     updatePortrait(data.getStringExtra("cropImagePath"));
                    break;
                //拍照回掉
                case AlbumActivity.TO_CROP_PHOTO_REQUEST_CODE:
                    LogUtils.a("++++", 5 + data.getStringExtra("cropImagePath"));
                     updatePortrait(data.getStringExtra("cropImagePath"));
                    break;
                case TAKE_PHOTO_REQUEST_CODE:
                    LogUtils.a("++++", 2 + "");
                    Intent intents = new Intent(context, CropImageActivity.class);
                    intents.putExtra("path", capturePath);
                    intents.putExtra("type", 0);
                    intents.putExtra("cropWidth", 100);
                    intents.putExtra("cropHeight", 100);
                    startActivityForResult(intents, AlbumActivity.TO_CROP_PHOTO_REQUEST_CODE);
                    break;
                case REQUEST_SEX:
                    sex = data.getIntExtra("sex", Constant.GIRL);
                    userInfo.sex = sex;
                    UserLoginInfo.saveUserInfo(this, userInfo);
                    LogUtils.i("sex",sex+"");
                    String sexStr = sex ==0 ? "女" : "男";
                    sexTV.setText(sexStr);
                    break;
                case ZDY_YY:
                    hospitalName = data.getStringExtra("name");
                    perfect_Yiname.setText(hospitalName);
                    break;
                case ZDY_KS:
                    departmentName = data.getStringExtra("name");
                    perfect_Ksname.setText(departmentName);
                    break;
                case ZDY_ZC:
                    zhiweiName = data.getStringExtra("name");
                    perfect_Zcname.setText(zhiweiName);
                    break;
            }
        }
    }
 /*   private void updatePortrait(String capturePath){
        waveView.setVisibility(View.VISIBLE);
        waveView2.setVisibility(View.VISIBLE);
        Picasso.with(context).load(new File(capturePath)).into(headImg);
        Message message=Message.obtain();
        message.what=FLAG_ONE;
        handler.sendMessage(message);}*/

    private void updatePortrait(final String file) {
        waveView.setVisibility(View.VISIBLE);
        waveView2.setVisibility(View.VISIBLE);
        LogUtils.d("sccg",""+file);
        Picasso.with(context).load(new File(file)).placeholder(R.drawable.mrtx).error(R.drawable.mrtx).into(headImg);
        File file1=new File(file);
        Map<String, String> params = new HashMap<String, String>();
        OkGo.post(UrlConstant.UPLOADFILE)//
                .tag(this)//
                .params("file",file1)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        //上传成功
                        Toast.makeText(getApplicationContext(), "上传成功", Toast.LENGTH_SHORT).show();
                        LogUtils.i("progress",s+";"+response);
                        if (response != null) {
                            try {
                                JSONObject jsonObject=new JSONObject(s);
                                JSONObject jsonObject1=jsonObject.getJSONObject("data");
                                String key=jsonObject1.getString("url");
                                userInfo.iconUrl=key;
                                iconUrlKey=key;
                                LogUtils.i("iconUrlkey",key+"");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    }

                    @Override
                    public void upProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
                        //这里回调上传进度(该回调在主线程,可以直接更新ui)
                        LogUtils.i("needprogress",progress*100);
                        Message message=Message.obtain();
                        message.arg1=(int) progress*100;
                        message.what=99;
                        handler.sendMessage(message);
                    }
                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        Toast.makeText(context,"上传失败,请重试",Toast.LENGTH_LONG).show();
                    }
                });
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
}
