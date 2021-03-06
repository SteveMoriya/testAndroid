package com.wehang.ystv.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;


import com.blankj.utilcode.util.LogUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tencent.TIMConversationType;
import com.tencent.TIMCustomElem;
import com.tencent.TIMElem;
import com.tencent.TIMElemType;
import com.tencent.TIMManager;
import com.tencent.TIMMessage;
import com.wehang.ystv.Constant;
import com.wehang.ystv.R;
import com.wehang.ystv.VideoApplication;
import com.wehang.ystv.bo.Lives;
import com.wehang.ystv.bo.QuestionMessages;
import com.wehang.ystv.bo.UserInfo;
import com.wehang.ystv.bo.VersionInfo;
import com.wehang.ystv.bo.YsMessage;
import com.wehang.ystv.dbutils.DownloadBean;
import com.wehang.ystv.interfaces.UrlConstant;
import com.wehang.ystv.interfaces.result.GetLvesResult;
import com.wehang.ystv.interfaces.result.GetSysMResult;
import com.wehang.ystv.logic.TCChatRoomMgr;
import com.whcd.base.interfaces.BaseData;
import com.whcd.base.interfaces.BaseResult;
import com.whcd.base.interfaces.HttpTool;
import com.whcd.base.widget.CustomProgressDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.wehang.ystv.R.id.blanceTv;

/**
 * @author ????????? 2017/1/3 9:28
 * @version V1.0
 * @ProjectName: ToLive
 * @Copyright: 2015 WHCD Co., Ltd. All Right Reserved.
 * @address:
 * @date: 2017/1/3 9:28
 * @Description: ???????????????????????????????????????????????????.
 * @instructions:{???????????????}
 * @modificationHistory=========================??????????????????
 * @modify by user: ????????? 2017/1/3
 */

public class Utils {
    /**
     * The BigDecimal class provides operations for arithmetic, scale manipulation, rounding, comparison, hashing, and format conversion.
     *
     * @param d
     * @return
     */
    public static double formatDouble2(double d) {
        // ??????????????????????????????????????????????????????RoundingMode.DOWN
        BigDecimal bg = new BigDecimal(d).setScale(2, RoundingMode.UP);
        return bg.doubleValue();
    }

    public static int dp2pxConvertInt(Context context, float dpValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, context.getResources().getDisplayMetrics());
    }

    public static float sp2px(Context context, float spValue) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue, context.getResources().getDisplayMetrics());
    }

    public static boolean supportLinkMic() {
        return Build.VERSION.SDK_INT >= 18;
    }

    /**
     * ??????????????????????????????????????????
     *
     * @param gear ??????
     * @param max  ?????????
     * @param curr ?????????
     * @return ?????????
     */
    public static int filtNumber(int gear, int max, int curr) {
        return curr / (max / gear);
    }

    /**
     * ?????????????????????????????????
     *
     * @param view ?????????view????????????
     * @param ev   ????????????
     * @return
     */
    public static boolean isInRangeOfView(View view, MotionEvent ev) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int x = location[0];
        int y = location[1];
        if (ev.getX() < x || ev.getX() > (x + view.getWidth()) || ev.getY() < y || ev.getY() > (y + view.getHeight())) {
            return false;
        }
        return true;
    }

    /**
     * / ?????????????????????
     *
     * @return ??????
     */
    public static int getTaskBarHeight(Context context) {
        int resourceId = context.getResources().getIdentifier("status_bar_height",
                "dimen", "android");
        if (resourceId > 0) {
            return context.getResources().getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    public static Bitmap drawableToBitamp(int resId) {
        Drawable drawable = VideoApplication.instance.getApplicationContext().getResources().getDrawable(resId);
        BitmapDrawable bd = (BitmapDrawable) drawable;
        return bd.getBitmap();
    }

    public static int getScreenWidth(Context context) {
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        return display.getWidth();
    }

    /**
     * ???int?????????????????????????????????byte???????????????????????????(???????????????????????????)???????????? ???bytesToInt??????????????????
     * @param value
     *            ????????????int???
     * @return byte??????
     */
    public static byte[] intToBytes( int value )
    {
        byte[] src = new byte[4];
        src[3] =  (byte) ((value>>24) & 0xFF);
        src[2] =  (byte) ((value>>16) & 0xFF);
        src[1] =  (byte) ((value>>8) & 0xFF);
        src[0] =  (byte) (value & 0xFF);
        return src;
    }
    /**
     * ???int?????????????????????????????????byte???????????????????????????(???????????????????????????)????????????  ???bytesToInt2??????????????????
     */
    public static byte[] intToBytes2(int value)
    {
        byte[] src = new byte[4];
        src[0] = (byte) ((value>>24) & 0xFF);
        src[1] = (byte) ((value>>16)& 0xFF);
        src[2] = (byte) ((value>>8)&0xFF);
        src[3] = (byte) (value & 0xFF);
        return src;
    }

    /**
     * byte????????????int???????????????????????????(???????????????????????????)??????????????????intToBytes??????????????????
     *
     * @param src
     *            byte??????
     * @param offset
     *            ???????????????offset?????????
     * @return int??????
     */
    public static int bytesToInt(byte[] src, int offset) {
        int value;
        value = (int) ((src[offset] & 0xFF)
                | ((src[offset+1] & 0xFF)<<8)
                | ((src[offset+2] & 0xFF)<<16)
                | ((src[offset+3] & 0xFF)<<24));
        return value;
    }

    /**
     * byte????????????int???????????????????????????(???????????????????????????)???????????????intToBytes2??????????????????
     */
    public static int bytesToInt2(byte[] src, int offset) {
        int value;
        value = (int) ( ((src[offset] & 0xFF)<<24)
                |((src[offset+1] & 0xFF)<<16)
                |((src[offset+2] & 0xFF)<<8)
                |(src[offset+3] & 0xFF));
        return value;
    }

    public static void addWatchHistory(Context context,String token,String sourceId){
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", token);
        params.put("sourceId",sourceId);
        HttpTool.doPost(context, UrlConstant.ADDVIEWRECORD, params, true, new TypeToken<BaseResult<VersionInfo>>() {
        }.getType(), new HttpTool.OnResponseListener() {

            @Override
            public void onSuccess(BaseData data) {
                LogUtils.i("addWatchHistory","11");
            }

            @Override
            public void onError(int errorCode) {

            }
        });
    }

    public static void sourceQuestions(Context context, String token, String sourceId, final TCChatRoomMgr timManager){

        Map<String, String> params = new HashMap<String, String>();
        params.put("token", token);
        params.put("sourceId",sourceId);
        params.put("page","1");
        params.put("pageSize",1000+"");
        HttpTool.doPost1(context, UrlConstant.SOURCEQUSETONS, params, true, new TypeToken<BaseResult<BaseData>>() {
        }.getType(), new HttpTool.OnResponseListener1() {


            @Override
            public void onSuccess(String string) {

                LogUtils.i("quxiao",string);
                timManager.sendQusetionMessage(string);
            }

            @Override
            public void onError(int errorCode) {
            }
        });
    }
    public static void sourceQuestions(Context context, String token, String sourceId, final Handler msgHandler, final List<QuestionMessages>list){

        Map<String, String> params = new HashMap<String, String>();
        params.put("token", token);
        params.put("sourceId",sourceId);
        params.put("page","1");
        params.put("pageSize",1000+"");
        HttpTool.doPost1(context, UrlConstant.SOURCEQUSETONS, params, true, new TypeToken<BaseResult<BaseData>>() {
        }.getType(), new HttpTool.OnResponseListener1() {


            @Override
            public void onSuccess(String string) {

                try {
                    JSONObject jsonObject=new JSONObject(string);
                    JSONObject jsonObject2=jsonObject.getJSONObject("data");

                    JSONArray array=jsonObject2.getJSONArray("questions");
                    list.clear();
                    for (int i=0;i<array.length();i++){
                        JSONObject jsonObject1=array.getJSONObject(i);
                        QuestionMessages questionMessages = new Gson().fromJson(jsonObject1.toString(), QuestionMessages.class);
                        LogUtils.i("IMCMD_QUESTION",questionMessages.toString());
                        list.add(questionMessages);
                    }
                    msgHandler.sendEmptyMessage(4);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(int errorCode) {
            }
        });
    }
    //???????????????????????????
    public static void getOnLine(Context context,String sourceId, final TCChatRoomMgr timManager) {
        Map<String, String> params;
        params = new HashMap<String, String>();
        params.put("token", UserLoginInfo.getUserToken());
        params.put("sourceId", sourceId);
        HttpTool.doPost(context, UrlConstant.SOURCEDETAIL, params, true, new TypeToken<BaseResult<GetLvesResult>>() {
        }.getType(), new HttpTool.OnResponseListener() {
            @Override
            public void onSuccess(BaseData data) {
                LogUtils.i("QUSERYCOURSEWARE", data.toString());
                GetLvesResult result = (GetLvesResult) data;
                String num = result.source.wacthNum;
                if (TextUtils.isEmpty(num)) {
                    num = "0";
                }
                timManager.sendOnlne(num);

            }

            @Override
            public void onError(int errorCode) {
                LogUtils.i("info", "no");
            }
        });
    }

    //???????????????????????????
    public static boolean isduihua(int action){
      /*  if (Constant.LINKMIC_CMD_REQUEST == action) {
            return false;
        } else if (Constant.LINKMIC_CMD_ACCEPT == action) {
            return false;
        } else if (Constant.LINKMIC_CMD_REJECT == action) {
            return false;
        } else if (Constant.LINKMIC_CMD_MEMBER_JOIN_NOTIFY == action) {
            return false;
        } else if (Constant.LINKMIC_CMD_MEMBER_EXIT_NOTIFY == action) {
            return false;
        } else if (Constant.LINKMIC_CMD_KICK_MEMBER == action) {
            return false;
        }else if (Constant.IMCMD_ROOM_COLLECT_INFO==action){
            //????????????
            return false;
        }*/
      if(action!=1){
          return false;
      }
        return true;
    }
    public static void isC2C(TIMMessage currMsg){
        if (currMsg==null){
            return ;
        }
        if (!currMsg.getConversation().getType().equals(TIMConversationType.C2C)){
            return ;
        }
        for (int j = 0; j < currMsg.getElementCount(); j++) {
            if (currMsg.getElement(j) == null) {
                continue;
            }

            TIMElem elem = currMsg.getElement(j);
            TIMElemType type = elem.getType();
            LogUtils.i("danliao", "1:"+type);
            if (type == TIMElemType.Custom) {
                currMsg.getConversation().setReadMessage(currMsg);
               return ;
            }

        }
    }
    public static List<DownloadBean> addAllDoxu(List<DownloadBean> list){
        List<DownloadBean>reList=new ArrayList<>();
        if (list.size()>0){
            for (int i=0;i<list.size();i++){
                reList.add(list.get(list.size()-i-1));
            }
        }

        return reList;
    }
    public static void endLive(String sourceId, final Activity activity){
        Map<String, String> params = new HashMap<String, String>();
        params.put("token",UserLoginInfo.getUserToken());
        params.put("sourceId", sourceId);
        final CustomProgressDialog dialog = CustomProgressDialog.show(activity, "?????????...", true, null);
        HttpTool.doPost(activity, UrlConstant.ENDLIVE, params, true, new TypeToken<BaseResult<BaseData>>() {
        }.getType(), new HttpTool.OnResponseListener() {
            @Override
            public void onSuccess(BaseData data) {
                dialog.dismiss();
                activity.finish();
            }

            @Override
            public void onError(int errorCode) {
                dialog.dismiss();
                ToastUtil.makeText(activity,"???????????????????????????",Toast.LENGTH_SHORT).show();
                activity.finish();
            }
        });

    }
    public static void outRoom(String sourceId, final Activity activity){
        Map<String, String> params = new HashMap<String, String>();
        params.put("token",UserLoginInfo.getUserToken());
        params.put("sourceId", sourceId);
        //final CustomProgressDialog dialog = CustomProgressDialog.show(activity, "?????????...", true, null);
        HttpTool.doPost(activity, UrlConstant.OUTROOM, params, true, new TypeToken<BaseResult<BaseData>>() {
        }.getType(), new HttpTool.OnResponseListener() {
            @Override
            public void onSuccess(BaseData data) {

            }

            @Override
            public void onError(int errorCode) {

                ToastUtil.makeText(activity,"???????????????????????????",Toast.LENGTH_SHORT).show();
            }
        });

    }
    public static void endLiveHome(String sourceId, Context context){
        Map<String, String> params = new HashMap<String, String>();
        params.put("token",UserLoginInfo.getUserToken());
        params.put("sourceId", sourceId);
        final CustomProgressDialog dialog = CustomProgressDialog.show(context, "?????????...", true, null);
        HttpTool.doPost(context, UrlConstant.ENDLIVE, params, true, new TypeToken<BaseResult<BaseData>>() {
        }.getType(), new HttpTool.OnResponseListener() {
            @Override
            public void onSuccess(BaseData data) {
                dialog.dismiss();
                //ToastUtil.makeText(context,"???????????????????????????",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(int errorCode) {
                dialog.dismiss();

            }
        });

    }
    public static void endLink(String sourceId, final Context context){
        Map<String, String> params = new HashMap<String, String>();
        params.put("sourceId", sourceId);
        final CustomProgressDialog dialog = CustomProgressDialog.show(context, "?????????...", true, null);
        HttpTool.doPost(context, UrlConstant.CLOSUREMICROPHONE, params, true, new TypeToken<BaseResult<BaseData>>() {
        }.getType(), new HttpTool.OnResponseListener() {
            @Override
            public void onSuccess(BaseData data) {
                dialog.dismiss();
                //ToastUtil.makeText(context,"????????????",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(int errorCode) {
                dialog.dismiss();

            }
        });

    }
    public static String getData(String dataStr) {
        SimpleDateFormat formart = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
        try {
            dataStr = sdf.format(formart.parse(dataStr));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dataStr;
    }
    //???????????????????????????
    public static String getHowTime(String dataStr) {
        SimpleDateFormat formart = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String result = "";
        try {
            long start = new Date().getTime();
            long end = formart.parse(dataStr).getTime();
            long diff = end - start;
            if (diff<0){
                return 0+"??????";
            }
            long nd = 1000 * 60 * 60 * 24;
            long nh = 1000 * 60 * 60;
            long nm = 1000 * 60;
            long ns = 1000;

            // ??????????????????
            long day = diff / nd;
            // ?????????????????????
            long hour = diff % nd / nh;
            // ??????????????????
            long min = diff % nh / nm;

            if(day > 0){
                result = day+"???"+hour+"??????"+min+"???";
            } else if (hour > 0){
                result = hour+"??????"+min+"???";
            } else {
                result = min+"???";
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }
    public static boolean isNumeric(String str){

        for (int i = str.length();--i>=0;){

            if (!Character.isDigit(str.charAt(i))){

                return false;

            }

        }

        return true;

    }
    private static int b=0;
    public static void linkSuccss(final Context context, final String sourceId, final String userId) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", UserLoginInfo.getUserToken());
        params.put("sourceId",sourceId);
        params.put("userId",userId);
        HttpTool.doPost(context, UrlConstant.SERVERMIXEDFLOW, params, false, new TypeToken<BaseResult<UserInfo>>() {
        }.getType(), new HttpTool.OnResponseListener() {
            @Override
            public void onSuccess(BaseData data) {
                b=0;
                LogUtils.i("linkSuccss","linkSuccss+++=");
            }

            @Override
            public void onError(int errorCode) {
                //????????????
                b++;
                if (b<3){
                    linkSuccss(context,sourceId,userId);
                }

            }
        });
    }
    //??????????????????

    /**
     *  token	String	Y	??????token
        userId	String	Y	?????????ID
        type	Int	Y	0????????? 1-????????????
     */
    public static void defrid(final Activity context, String userId, int type){
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", UserLoginInfo.getUserToken());
        params.put("userId",userId);
        params.put("type",type+"");
        HttpTool.doPost1(context, UrlConstant.DEFIREND, params, false, new TypeToken<BaseResult<BaseData>>() {
        }.getType(), new HttpTool.OnResponseListener1() {
            @Override
            public void onSuccess(String string) {
                try {
                    JSONObject jsonObject=new JSONObject(string);
                    ToastUtil.makeText(context,jsonObject.getString("msg"),Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(int errorCode) {

            }
        });
    }
    /**
     * ??????????????????
     */
    public static void getIsdefrend(final Context context, String userId) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", UserLoginInfo.getUserToken());
        params.put("userId",userId);
        HttpTool.doPost(context, UrlConstant.ISDEFRIEND, params, false, new TypeToken<BaseResult<YsMessage>>() {
        }.getType(), new HttpTool.OnResponseListener() {
            @Override
            public void onSuccess(BaseData data) {
                YsMessage result= ( YsMessage) data;
                ToastUtil.makeText(context,result.isDefriend+"",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(int errorCode) {


            }
        });
    }
}
