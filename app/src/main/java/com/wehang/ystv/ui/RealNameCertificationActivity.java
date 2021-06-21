package com.wehang.ystv.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.LogUtils;
import com.google.gson.reflect.TypeToken;
import com.wehang.ystv.Constant;
import com.wehang.ystv.Myapplication;
import com.wehang.ystv.R;
import com.wehang.ystv.adapter.PhotoChooseAdapter;
import com.wehang.ystv.bo.ApplyInfo;
import com.wehang.ystv.bo.Hospital;
import com.wehang.ystv.bo.PhotoInfo;
import com.wehang.ystv.bo.QueryDepartment;
import com.wehang.ystv.bo.QueryTitle;
import com.wehang.ystv.bo.UserInfo;
import com.wehang.ystv.interfaces.UrlConstant;
import com.wehang.ystv.interfaces.result.GetFileResult;
import com.wehang.ystv.interfaces.result.GetHospitalResult;
import com.wehang.ystv.interfaces.result.GetRealNameResult;
import com.wehang.ystv.myappview.PickerView;
import com.wehang.ystv.utils.CommonUtil;
import com.wehang.ystv.utils.ToastUtil;
import com.wehang.ystv.utils.UserLoginInfo;
import com.wehang.ystv.utils.Validator;
import com.wehang.ystv.widget.NotScrollGridView;
import com.whcd.base.activity.AlbumActivity;
import com.whcd.base.activity.BaseActivity;
import com.whcd.base.activity.ImagePagerActivity;
import com.whcd.base.bo.PictureItem;
import com.whcd.base.interfaces.BaseData;
import com.whcd.base.interfaces.BaseResult;
import com.whcd.base.interfaces.HttpTool;
import com.whcd.base.utils.ImageUtils;
import com.whcd.base.widget.BottomPopupWindow;
import com.whcd.base.widget.CustomProgressDialog;
import com.whcd.base.widget.TopMenuBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * {实名认证}
 * </p>
 *
 * @author 包川 2016-5-12 下午5:41:17
 * @version V1.0
 * @modificationHistory=========================创建
 * @modify by user: 包川 2016-5-12
 */
public class RealNameCertificationActivity extends BaseActivity implements PhotoChooseAdapter.DeleteSelectPictureListener {
    private LayoutInflater inflater = null;
    private LinearLayout rootLayout = null;
    private static final int CHECK_TYPE_ING = 2;// 审核中
    private static final int CHECK_TYPE_SUCCESS = 1; // 审核成功
    private static final int CHECK_TYPE_FAIL = 3; // 审核失败
    private static final int CHECK_TYPE_NO = 0;// 未提交
    private static final int REQUEST_SEX = 0x113;
    private static final int ZDY_YY = 0x124;
    private static final int ZDY_KS = 0x125;
    private static final int ZDY_ZC = 0x126;
    private TextView typeTv, instructionTv, sexTv, certificateTypeTv;
    private EditText nameEdt, cardIDEdt;
    private TextView hospitalEdt,keshiEdt,zhichengEdt;
    // 三张图片
    private NotScrollGridView imageGridView;
    private PhotoChooseAdapter pictureAdapter = null;
    private List<PhotoInfo> photoList = new ArrayList<PhotoInfo>();
    private String capturePath;// 照相图片名称
    private ApplyInfo applyInfo = null;
    private int currentChooseIndex = 0;
    private String certificatesOne = null;
    private String Certificatestwo = null;
    private String certificatesThree = null;
    private BottomPopupWindow popupWindow, typePPw;
    private UserInfo userInfo;
    private String name, sex, phone, cardId;
    private Button titleRightButton;
    private TopMenuBar topMenuBar;
    private CustomProgressDialog dialogUpload = null;

    //使用spanner
    private ArrayAdapter<String> adapter,adapterpd;

    private static final String[] list={"男","女"};
    private static final String[] listpd={"美女","性感","游戏大神"};
    private Spinner spinner,spinnerpd;

    //提示布局和状态布局
    private LinearLayout ts,sqzt;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_want_live;
    }

    @Override
    protected void initTitleBar() {
        topMenuBar = (TopMenuBar) findViewById(R.id.topTitleBar);
        topMenuBar.setTitle("讲师认证");

        Button titleLeftButton = topMenuBar.getLeftButton();
        titleRightButton = topMenuBar.getRightButton();
        titleRightButton.setText("提交");
        titleRightButton.setOnClickListener(this);
        titleLeftButton.setOnClickListener(this);
    }

    @Override
    protected void initView() {
        userInfo = UserLoginInfo.getUserInfo(context);
        getCheckStatus();
        instructionTv = (TextView) findViewById(R.id.instructionTv);
        typeTv = (TextView) findViewById(R.id.typeTv);
        nameEdt = (EditText) findViewById(R.id.nameEdt);

        /*certificateTypeTv = (TextView) findViewById(R.id.certificateTypeTv);
        certificateTypeTv.setOnClickListener(this);*/
        cardIDEdt = (EditText) findViewById(R.id.cardIDEdt);
        cardIDEdt.setOnClickListener(this);
        imageGridView = (NotScrollGridView) findViewById(R.id.imageGridView);

        photoList.add(new PhotoInfo());
        photoList.add(new PhotoInfo());
        photoList.add(new PhotoInfo());
        pictureAdapter = new PhotoChooseAdapter(this, photoList, imageGridView,0);
        imageGridView.setAdapter(pictureAdapter);
        sqzt = (LinearLayout) findViewById(R.id.sqztia);
        nameEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().contains(" ")) {
                    String[] str = s.toString().split(" ");
                    String str1 = "";
                    for (int i = 0; i < str.length; i++) {
                        str1 += str[i];
                    }
                    nameEdt.setText(str1);

                    nameEdt.setSelection(start);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        hospitalEdt=findViewById(R.id.hospitalEdt);
       // hospitalEdt.setOnClickListener(this);
        keshiEdt=findViewById(R.id.keshiEdt);
       // keshiEdt.setOnClickListener(this);
        zhichengEdt=findViewById(R.id.zhichengEdt);
      //  zhichengEdt.setOnClickListener(this);

       /* //自定义
        findViewById(R.id.zdyYy).setOnClickListener(this);
        findViewById(R.id.zdyKs).setOnClickListener(this);
        findViewById(R.id.zdyZc).setOnClickListener(this);*/

    }

    @Override
    protected void initData(Bundle bundle) {
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rootLayout = (LinearLayout) findViewById(R.id.root_layout);
        hospitalEdt.setText(UserLoginInfo.getUserInfo().hospital);
        keshiEdt.setText(UserLoginInfo.getUserInfo().department);
        zhichengEdt.setText(UserLoginInfo.getUserInfo().title);
        /*getHospitalList();
        getDepartmentList();
        getTitiltList();*/
    }

    private void initContent() {
        /*SetStatus(applyInfo == null ? 0 : applyInfo.status+1);*/
       /* if (applyInfo==null){
            SetStatus(0);
        }else if (applyInfo.status==0){
            SetStatus(1);
        }else if (applyInfo.status==1){
            SetStatus(2);
        }else if (applyInfo.status==2){
            SetStatus(3);
        }*/
        SetStatus(applyInfo == null ? 0 : applyInfo.status);
        if (applyInfo != null) {
            if (applyInfo.name != null) {
                loadInfo(applyInfo);
            }
        }
        imageGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PhotoInfo item = (PhotoInfo) imageGridView.getItemAtPosition(position);
                if (item.path == null) {
                    currentChooseIndex = position;
                    showChoosePhotoPopupwindow();
                } else {
                    Intent intent = new Intent(context, ImagePagerActivity.class);
                    ArrayList<String> urls = new ArrayList<String>();
                    //判断是网络图片还是本地 0默认网络
                    int url_bd_wl=0;
                    if (item.path.indexOf("http") != -1) {
                        url_bd_wl=0;
                        for (int i=0;i<photoList.size();i++){
                            urls.add(photoList.get(i).path);
                        }
                        //urls.add(item.path);
                    } else {
                        url_bd_wl=1;
                        for (int i=0;i<photoList.size();i++){
                            urls.add("file://" + photoList.get(i).path);

                        }

                    }
                    intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, urls);
                    intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
                    intent.putExtra("startPosition",position);
                    intent.putExtra("type",url_bd_wl);
                    startActivity(intent);
                }
            }
        });
        loadPicText(applyInfo, applyInfo == null ? 1 : applyInfo.certificateType);
    }

    public void loadPicText(ApplyInfo applyInfo, int type) {
        type=1;
        photoList.clear();
        if (applyInfo == null) {
            photoList.add(new PhotoInfo(type == 1 ? "身份证正面图片" : "上传护照正面图片", null));
            photoList.add(new PhotoInfo(type == 1 ? "身份证反面图片" : "上传护照首页图片", null));
            photoList.add(new PhotoInfo(type == 1 ? "医生执照" : "上传手持护照图片", null));
        } else {
            if (!TextUtils.isEmpty(applyInfo.IDFront)) {
                photoList.add(new PhotoInfo(type == 1 ? "身份证正面图片" : "护照正面图片", UrlConstant.IP + applyInfo.IDFront));
            }
            if (!TextUtils.isEmpty(applyInfo.IDCon)) {
                photoList.add(new PhotoInfo(type == 1 ? "身份证反面图片" : "护照首页图片", UrlConstant.IP + applyInfo.IDCon));
            }
            if (!TextUtils.isEmpty(applyInfo.IDSeize)) {
                photoList.add(new PhotoInfo(type == 1 ? "医生执照" : "手持护照图片", UrlConstant.IP + applyInfo.IDSeize));
            }

        }
        if (applyInfo==null){
            pictureAdapter = new PhotoChooseAdapter(this, photoList, imageGridView,-1);
        }else {
            pictureAdapter = new PhotoChooseAdapter(this, photoList, imageGridView,applyInfo.status);
        }
        if (applyInfo == null) {
            pictureAdapter.setListener(this);
        }
        imageGridView.setAdapter(pictureAdapter);

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == AlbumActivity.TO_ALBUM_REQUEST_CODE) {
                @SuppressWarnings("unchecked")
                ArrayList<PictureItem> selectImageList = (ArrayList<PictureItem>) data.getSerializableExtra("selectImageList");
                for (PictureItem pictureItem : selectImageList) {
                    String picturePath = pictureItem.getImagePath();
                    // 如果图片不在应用创建的目录下，则先移动图片
                    String newPicturePath = picturePath;
                    if (picturePath.indexOf(Constant.ROOT_PATH) < 0) {
                        newPicturePath = Myapplication.instance.getStringAppParam("projectPath") + "/" + System.currentTimeMillis() + ".jpg";
                        ImageUtils.copyFile(picturePath, newPicturePath);
                    }
                    ImageUtils.compressImage(newPicturePath);
                    pictureItem.setImagePath(newPicturePath);
                    photoList.get(currentChooseIndex).path = newPicturePath;
                }
                pictureAdapter.notifyDataSetChanged();
            } else if (requestCode == AlbumActivity.TAKE_PHOTO_REQUEST_CODE) {
                ImageUtils.compressImage(capturePath);
                photoList.get(currentChooseIndex).path = capturePath;
                pictureAdapter.notifyDataSetChanged();
            } else if (requestCode == REQUEST_SEX) {
                int sex = data.getIntExtra("sex", Constant.GIRL);
                sexTv.setText(sex == 0 ? "女" : "男");
            } else if(requestCode==ZDY_YY){
                hospitalName = data.getStringExtra("name");
                hospitalEdt.setText(hospitalName);
            } else if (requestCode==ZDY_KS){
                departmentName = data.getStringExtra("name");
                keshiEdt.setText(departmentName);
            } else if (requestCode==ZDY_ZC){
                zhiweiName = data.getStringExtra("name");
                zhichengEdt.setText(zhiweiName);
            }

        }
    }

    @Override
    public void onClick(View v) {
        if (CommonUtil.isFirstClick())
            return;
        Intent intent;
        switch (v.getId()) {
            case R.id.title_btn_left:
                back();
                break;
            case R.id.title_btn_right:
                if (!titleRightButton.getText().toString().trim().equals("重新申请")) {
                    submitApply();
                } else {
                    applyInfo = null;
                   // certificateTypeTv.setText("身份证");
                    initContent();
                    getIsEnabled(true);
                }
                break;
            /*case R.id.certificateTypeTv:
                showPopWindow();
                break;*/
            case R.id.take_photo:
                dismissPop();
                //certificateTypeTv.setText("身份证");
                loadPicText(applyInfo, 1);
                cardIDEdt.setEnabled(true);
                break;
            case R.id.album:
                dismissPop();
               // certificateTypeTv.setText("护照");
                loadPicText(applyInfo, 0);
                cardIDEdt.setEnabled(true);
                break;
            case R.id.cancel:
                dismissPop();
                break;
          /*  case R.id.cardIDEdt:
                if (TextUtils.isEmpty(certificateTypeTv.toString().trim())) {
                    ToastUtil.makeText(context, "请选择证件类型", Toast.LENGTH_SHORT).show();
                    cardIDEdt.setEnabled(false);
                }
                break;*/
            case R.id.hospitalEdt:
                if (hospitalList.size()==0){
                    getHospitalList();
                    ToastUtil.makeText(context,"正在获取医院信息，请稍后再试",Toast.LENGTH_LONG).show();
                    return;
                }
                showHospitalPopWindow();
                break;
            case R.id.keshiEdt:
                if (departmentList.size()==0){
                    getDepartmentList();
                    ToastUtil.makeText(context,"正在获取科室信息，请稍后再试",Toast.LENGTH_LONG).show();
                    return;
                }
                showDepartmentPopWindow();
                break;
            case R.id.zhichengEdt:
                if (titleList.size()==0){
                    getTitiltList();
                    ToastUtil.makeText(context,"正在获取职称信息，请稍后再试",Toast.LENGTH_LONG).show();
                    return;
                }
                showzhichengPopWindow();

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
            default:
                break;
        }
    }
    private BottomPopupWindow hospitalPopWindow,departmentPopWindow,zhichengPopWindow;
    /**
     * 选择所属医院
     */
    private String hospitalName;
    private String hospitalId;
    private List<Hospital>hospitalList=new ArrayList<>();
    private void getHospitalList(){
        hospitalList.clear();
        Map<String, String> params = new HashMap<String, String>();
        params.put("addrId",UserLoginInfo.getUserInfo().districtId);
        HttpTool.doPost(context, UrlConstant.QUERYHOSPITAL, params, false, new TypeToken<BaseResult<GetHospitalResult>>() {
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
                UserLoginInfo.loginOverdue(context, errorCode);

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
            final PickerView belong_pv;
            belong_pv= (PickerView) popView.findViewById(R.id.belong_pv);
            if (hospitalList2.size()>2){
                belong_pv.setData(hospitalList2);
                belong_pv.setSelected(2);
                hospitalName=hospitalList2.get(2);
                hospitalId=hospitalList.get(2).hospital_id;
            }else {
                belong_pv.setData(hospitalList2);
                belong_pv.setSelected(0);
                hospitalName=hospitalList2.get(0);
                hospitalId=hospitalList.get(0).hospital_id;
            }

            belong_pv.setOnSelectListener(new PickerView.onSelectListener() {
                @Override
                public void onSelect(String text) {
                    hospitalName=text;
                    for (int i=0;i<hospitalList.size();i++){
                        if (text.equals(hospitalList.get(i).hospital_name)){
                            hospitalId=hospitalList.get(i).hospital_id;
                            break;
                        }
                    }
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
                    hospitalEdt.setText(hospitalName);

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
    private String titleId;
    private List<QueryTitle>titleList=new ArrayList<>();
    private void getTitiltList(){
        hospitalList.clear();
        Map<String, String> params = new HashMap<String, String>();
        HttpTool.doPost(context, UrlConstant.QUERYTITLE, params, false, new TypeToken<BaseResult<GetHospitalResult>>() {
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
                UserLoginInfo.loginOverdue(context, errorCode);

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
            final PickerView belong_pv;
            belong_pv= (PickerView) popView.findViewById(R.id.belong_pv);
            belong_pv.setData(hospitalList);
            if (titleList.size()>2){
                belong_pv.setSelected(2);
                zhiweiName=hospitalList.get(2);
                titleId=titleList.get(2).titleId;
            }else {
                belong_pv.setSelected(0);
                zhiweiName=hospitalList.get(0);
                titleId=titleList.get(0).titleId;
            }

            belong_pv.setOnSelectListener(new PickerView.onSelectListener() {
                @Override
                public void onSelect(String text) {
                    zhiweiName=text;
                    for (int i=0;i<titleList.size();i++){
                        if (text.equals(titleList.get(i).titleName)){
                            titleId=titleList.get(i).titleId;
                            break;
                        }
                    }
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
                    zhichengEdt.setText(zhiweiName);

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
    private String departmentId;
    private List<QueryDepartment>departmentList=new ArrayList<>();
    private void getDepartmentList(){
        hospitalList.clear();
        Map<String, String> params = new HashMap<String, String>();
        HttpTool.doPost(context, UrlConstant.QUERYDEPARtTMENT, params, false, new TypeToken<BaseResult<GetHospitalResult>>() {
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
                UserLoginInfo.loginOverdue(context, errorCode);

            }
        });
    }
    private void showDepartmentPopWindow() {
        final List<String>hospitalList=new ArrayList<>();
        for (int i=0;i<departmentList.size();i++){
            hospitalList.add(departmentList.get(i).departmentName);
        }
        if (departmentPopWindow == null) {
            View popView = LayoutInflater.from(this).inflate(R.layout.view_belong_pop, rootLayout, false);
            View contentView = popView.findViewById(R.id.pop_conent);
            final PickerView belong_pv;
            belong_pv= (PickerView) popView.findViewById(R.id.belong_pv);
            belong_pv.setData(hospitalList);
            if (departmentList.size()>2){
                belong_pv.setSelected(2);
                departmentName=hospitalList.get(2);
                departmentId=departmentList.get(2).departmentId;
            }else {
                belong_pv.setSelected(0);
                departmentName=hospitalList.get(0);
                departmentId=departmentList.get(0).departmentId;
            }
            belong_pv.setOnSelectListener(new PickerView.onSelectListener() {
                @Override
                public void onSelect(String text) {
                    departmentName=text;
                    for (int i=0;i<departmentList.size();i++){
                        if (text.equals(departmentList.get(i).departmentName)){
                            departmentId=departmentList.get(i).departmentId;
                            break;
                        }
                    }
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
                    keshiEdt.setText(departmentName);

                }
            });
            departmentPopWindow = new BottomPopupWindow(this, popView);
        }
        departmentPopWindow.showAtLocation(rootLayout, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }
    private String typeName;

    /*
     * 状态判断
     */
    private void SetStatus(int status) {
        Drawable drawable;
        switch (status) {
            case CHECK_TYPE_NO:
               // instructionTv.setVisibility(View.INVISIBLE);
                typeTv.setVisibility(View.GONE);
                titleRightButton.setVisibility(View.VISIBLE);
                titleRightButton.setText("提交");
                titleRightButton.setTextColor(getResources().getColor(R.color.white));
                break;
            case CHECK_TYPE_ING:
                typeTv.setVisibility(View.VISIBLE);
                typeTv.setText("审核中");
              //  instructionTv.setVisibility(View.INVISIBLE);
                topMenuBar.hideRightButton();

                titleRightButton.setEnabled(false);
                break;
            case CHECK_TYPE_SUCCESS:
                typeTv.setVisibility(View.VISIBLE);
                typeTv.setTextColor(ContextCompat.getColor(context,R.color.green_s));
                typeTv.setText("已验证");
               // instructionTv.setVisibility(View.INVISIBLE);
                topMenuBar.hideRightButton();

                break;
            case CHECK_TYPE_FAIL:
                typeTv.setVisibility(View.VISIBLE);
                typeTv.setText("审核失败");
               // instructionTv.setVisibility(View.INVISIBLE);
                topMenuBar.showRightButton();
                titleRightButton.setText("重新申请");

                break;
            default:
                break;
        }
    }

    /**
     * 获取申请状态
     *
     * @author 包川 2016-6-3 下午6:24:04
     * @modificationHistory=========================创建
     * @modify by user: 包川 2016-6-3
     * @modify by reason: 原因
     */
    public void getCheckStatus() {
        Map<String, String> params = new HashMap<String, String>();

        params.put("token", userInfo.token);

        LogUtils.a("getCheckStatus","token"+userInfo.token+":"+UrlConstant.AUTHINFO);
        HttpTool.doPost(context, UrlConstant.AUTHINFO, params, true, new TypeToken<BaseResult<GetRealNameResult>>() {
        }.getType(), new HttpTool.OnResponseListener() {

            @Override
            public void onSuccess(BaseData data) {
                GetRealNameResult result= (GetRealNameResult) data;
                if (result!=null){
                    applyInfo=result.auth;
                    }

                if (applyInfo!=null){
                  LogUtils.a("getCheckStatus",applyInfo.toString());
                  //LogUtils.a("smyz",applyInfo.toString());
              }else {}
                initContent();
            }

            @Override
            public void onError(int errorCode) {
                UserLoginInfo.loginOverdue(context, errorCode);
            }
        });
    }

    /**
     * 提交申请
     *
     * @author 包川 2016-6-3 下午6:26:09
     * @modificationHistory=========================方法变更说明
     * @modify by user: 包川 2016-6-3
     * @modify by reason: 原因
     */
    public void submitApply() {
        name = nameEdt.getText().toString().trim();
        cardId = cardIDEdt.getText().toString().trim();
        if (photoList != null && photoList.size() > 0) {
            certificatesOne = photoList.get(0).path;
            Certificatestwo = photoList.get(1).path;
            certificatesThree = photoList.get(2).path;
        }
        if (TextUtils.isEmpty(name)) {
            ToastUtil.makeText(context, "请输入姓名", Toast.LENGTH_SHORT).show();
            nameEdt.requestFocus();
            return;
        }
        if (!Validator.isIDCard(cardId)) {
            ToastUtil.makeText(context, "您输入的身份证号码错误", Toast.LENGTH_SHORT).show();
            cardIDEdt.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(hospitalEdt.getText().toString().trim())) {
            ToastUtil.makeText(context, "请选择医院", Toast.LENGTH_SHORT).show();
            hospitalEdt.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(keshiEdt.getText().toString().trim())) {
            ToastUtil.makeText(context, "请选择科室", Toast.LENGTH_SHORT).show();
            keshiEdt.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(zhichengEdt.getText().toString().trim())) {
            zhichengEdt.requestFocus();
            ToastUtil.makeText(context, "请选择职称", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(certificatesOne)) {
            ToastUtil.makeText(context, "请上传身份证正面图片", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(Certificatestwo)) {
            ToastUtil.makeText(context, "请上传身份证背面图片", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(certificatesThree)) {
            ToastUtil.makeText(context, "请上传医生执照", Toast.LENGTH_SHORT).show();
            return;
        }
        titleRightButton.setTextColor(getResources().getColor(R.color.gray_pressed));
        dialogUpload = CustomProgressDialog.show(this, "正在上传...", false, null);
        applyLive(0);
    }

    public void applyLive(final int index) {

        if (index < 3) {
            LogUtils.a("filepath",photoList.get(index).path);
            HttpTool.doUpload(context, UrlConstant.UPLOADFILE, photoList.get(index).path, null, false, new TypeToken<BaseResult<GetFileResult>>() {
            }.getType(), new HttpTool.OnResponseListener() {
                @Override
                public void onSuccess(BaseData data) {
                    GetFileResult fileInfo = (GetFileResult) data;
                    LogUtils.a("filepath",photoList.get(index).path);
                    if (index == 0) {
                        certificatesOne = fileInfo.url;
                        LogUtils.a("filepath",certificatesOne);
                    } else if (index == 1) {
                        Certificatestwo = fileInfo.url;
                        LogUtils.a("filepath",Certificatestwo);
                    } else if (index == 2) {
                        certificatesThree = fileInfo.url;
                        LogUtils.a("filepath",certificatesThree);
                    }
                    applyLive(index + 1);
                }

                @Override
                public void onError(int errorCode) {
                    ToastUtil.makeText(context, "上传失败", Toast.LENGTH_SHORT).show();
                    if (isValidContext(context) && dialogUpload.isShowing()) {
                        dialogUpload.dismiss();
                    }
                }
            });
        } else {
            Map<String, String> params = new HashMap<String, String>();
            if (userInfo != null) {
                params.put("token", userInfo.token);
            }
            params.put("name", name);
            params.put("identitycard", cardId);
            params.put("hospitalName",hospitalEdt.getText().toString());
            params.put("departmentName",keshiEdt.getText().toString());
            params.put("titleName",zhichengEdt.getText().toString());

            params.put("IDFront", certificatesOne);
            params.put("IDCon", Certificatestwo);
            params.put("IDSeize", certificatesThree);
            LogUtils.i("sss",hospitalId+";"+departmentId+";"+titleId);
            HttpTool.doPost(context, UrlConstant.AUTHENTICATION, params, false, new TypeToken<BaseResult<BaseData>>() {
            }.getType(), new HttpTool.OnResponseListener() {

                @Override
                public void onSuccess(BaseData data) {
                    ToastUtil.makeText(context, "申请成功", Toast.LENGTH_SHORT).show();
                    if (isValidContext(context) && dialogUpload.isShowing()) {
                        dialogUpload.dismiss();
                    }
                    getCheckStatus();
                }

                @Override
                public void onError(int errorCode) {
                    UserLoginInfo.loginOverdue(context, errorCode);
                    ToastUtil.makeText(context, "申请失败", Toast.LENGTH_SHORT).show();
                    if (isValidContext(context) && dialogUpload.isShowing()) {
                        dialogUpload.dismiss();
                    }
                }
            });
        }
    }

    /**
     * 选择拍照或者相册ppw
     */
    public void showChoosePhotoPopupwindow() {
        View popView = inflater.inflate(R.layout.view_choose_photo, rootLayout, false);
        View contentView = popView.findViewById(R.id.pop_conent);
        LayoutParams params = (LayoutParams) contentView.getLayoutParams();
        params.gravity = Gravity.BOTTOM;
        contentView.setTag(1);
        contentView.setLayoutParams(params);
        popView.findViewById(R.id.take_photo).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isValidContext(context) && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
                capturePath = ImageUtils.takePhoto(RealNameCertificationActivity.this);

            }
        });
        popView.findViewById(R.id.album).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AlbumActivity.class);
                intent.putExtra("type", 1);
                intent.putExtra("maxSelectNumber", 1);
                startActivityForResult(intent, AlbumActivity.TO_ALBUM_REQUEST_CODE);
                if (isValidContext(context) && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
            }
        });
        popView.findViewById(R.id.cancel).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isValidContext(context) && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
            }
        });
        popupWindow = new BottomPopupWindow(this, popView);
        popupWindow.showAtLocation(rootLayout, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    @Override
    public void onDelete(PhotoInfo item) {
        for (PhotoInfo info : photoList) {
            if (info.name.equals(item.name)) {
                info.path = null;
                break;
            }
        }
        pictureAdapter.notifyDataSetChanged();
    }

    /**
     * 加载用户申请信息
     *
     * @author 包川 2016-6-7 下午9:19:17
     * @modificationHistory=========================创建
     * @modify by user: 包川 2016-6-7
     * @modify by reason: 原因
     */
    public void loadInfo(ApplyInfo applyInfo) {
        nameEdt.setText(applyInfo.userName);
        /*phoneEdt.setText(applyInfo.status == 2 ? "************" : applyInfo.phone);*/
        cardIDEdt.setText(applyInfo.identitycard);
        getIsEnabled(false);
        cardIDEdt.setText(applyInfo.identitycard);
        /*keshiEdt.setText(applyInfo.department);
        hospitalEdt.setText(applyInfo.hospital);
        zhichengEdt.setText(applyInfo.title);*/

        //imageGridView.setVisibility(applyInfo.status == 0 ? View.INVISIBLE : View.VISIBLE);
       // certificateTypeTv.setText(applyInfo.status == 2 ? "******" : applyInfo.certificateType == 1 ? "身份证" : "护照");
    }

    public void getIsEnabled(boolean isEnable) {
        nameEdt.setEnabled(isEnable);
        cardIDEdt.setEnabled(isEnable);
        if (isEnable==false){
            nameEdt.setFocusable(isEnable);
            cardIDEdt.setFocusable(isEnable);
            /*findViewById(R.id.zdyYy).setVisibility(View.GONE);
            findViewById(R.id.zdyKs).setVisibility(View.GONE);
            findViewById(R.id.zdyZc).setVisibility(View.GONE);*/
        }else {
            nameEdt.setFocusable(true);
            nameEdt.setFocusableInTouchMode(true);
            nameEdt.requestFocus();
            nameEdt.requestFocusFromTouch();

            cardIDEdt.setFocusable(true);
            cardIDEdt.setFocusableInTouchMode(true);
            cardIDEdt.requestFocus();
            cardIDEdt.requestFocusFromTouch();
            nameEdt.setText("");
            cardIDEdt.setText("");
          /*  hospitalEdt.setText("");
            keshiEdt.setText("");
            zhichengEdt.setText("");*/
            /*findViewById(R.id.zdyYy).setVisibility(View.VISIBLE);
            findViewById(R.id.zdyKs).setVisibility(View.VISIBLE);
            findViewById(R.id.zdyZc).setVisibility(View.VISIBLE);*/
        }


        hospitalEdt.setEnabled(isEnable);
        keshiEdt.setEnabled(isEnable);
        zhichengEdt.setEnabled(isEnable);
        //certificateTypeTv.setEnabled(isEnable);
    }

    private void dismissPop() {
        if (typePPw != null && typePPw.isShowing()) {
            typePPw.dismiss();
        }
    }
    //选择身份证或者护照
    private void showPopWindow() {
        if (typePPw == null) {
            View popView = LayoutInflater.from(this).inflate(R.layout.view_type_card, rootLayout, false);
            View contentView = popView.findViewById(R.id.pop_conent);
            LayoutParams params = (LayoutParams) contentView.getLayoutParams();
            params.gravity = Gravity.BOTTOM;
            contentView.setTag(1);
            contentView.setLayoutParams(params);
            popView.findViewById(R.id.take_photo).setOnClickListener(this);
            popView.findViewById(R.id.album).setOnClickListener(this);
            popView.findViewById(R.id.cancel).setOnClickListener(this);
            typePPw = new BottomPopupWindow(this, popView);
        }
        typePPw.showAtLocation(rootLayout, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

}
