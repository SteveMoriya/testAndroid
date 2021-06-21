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
 * @author 钟林宏 2017/1/3 9:28
 * @version V1.0
 * @ProjectName: ToLive
 * @Copyright: 2015 WHCD Co., Ltd. All Right Reserved.
 * @address:
 * @date: 2017/1/3 9:28
 * @Description: 本内容仅限于公司内部使用，禁止转发.
 * @instructions:{类文件说明}
 * @modificationHistory=========================重大变更说明
 * @modify by user: 钟林宏 2017/1/3
 */

public class Utils {
    /**
     * The BigDecimal class provides operations for arithmetic, scale manipulation, rounding, comparison, hashing, and format conversion.
     *
     * @param d
     * @return
     */
    public static double formatDouble2(double d) {
        // 新方法，如果不需要四舍五入，可以使用RoundingMode.DOWN
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
     * 根据比例转化实际数值为相对值
     *
     * @param gear 档位
     * @param max  最大值
     * @param curr 当前值
     * @return 相对值
     */
    public static int filtNumber(int gear, int max, int curr) {
        return curr / (max / gear);
    }

    /**
     * 判断是否点击了某个区域
     *
     * @param view 点击的view所在位置
     * @param ev   触摸事件
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
     * / 获取任务栏高度
     *
     * @return 高度
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
     * 将int数值转换为占四个字节的byte数组，本方法适用于(低位在前，高位在后)的顺序。 和bytesToInt（）配套使用
     * @param value
     *            要转换的int值
     * @return byte数组
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
     * 将int数值转换为占四个字节的byte数组，本方法适用于(高位在前，低位在后)的顺序。  和bytesToInt2（）配套使用
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
     * byte数组中取int数值，本方法适用于(低位在前，高位在后)的顺序，和和intToBytes（）配套使用
     *
     * @param src
     *            byte数组
     * @param offset
     *            从数组的第offset位开始
     * @return int数值
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
     * byte数组中取int数值，本方法适用于(低位在后，高位在前)的顺序。和intToBytes2（）配套使用
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
    //进入时发送在线人数
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

    //判断是不是对话消息
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
            //房间信息
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
        final CustomProgressDialog dialog = CustomProgressDialog.show(activity, "加载中...", true, null);
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
                ToastUtil.makeText(activity,"服务器异常，请重试",Toast.LENGTH_SHORT).show();
                activity.finish();
            }
        });

    }
    public static void outRoom(String sourceId, final Activity activity){
        Map<String, String> params = new HashMap<String, String>();
        params.put("token",UserLoginInfo.getUserToken());
        params.put("sourceId", sourceId);
        //final CustomProgressDialog dialog = CustomProgressDialog.show(activity, "加载中...", true, null);
        HttpTool.doPost(activity, UrlConstant.OUTROOM, params, true, new TypeToken<BaseResult<BaseData>>() {
        }.getType(), new HttpTool.OnResponseListener() {
            @Override
            public void onSuccess(BaseData data) {

            }

            @Override
            public void onError(int errorCode) {

                ToastUtil.makeText(activity,"服务器异常，请重试",Toast.LENGTH_SHORT).show();
            }
        });

    }
    public static void endLiveHome(String sourceId, Context context){
        Map<String, String> params = new HashMap<String, String>();
        params.put("token",UserLoginInfo.getUserToken());
        params.put("sourceId", sourceId);
        final CustomProgressDialog dialog = CustomProgressDialog.show(context, "加载中...", true, null);
        HttpTool.doPost(context, UrlConstant.ENDLIVE, params, true, new TypeToken<BaseResult<BaseData>>() {
        }.getType(), new HttpTool.OnResponseListener() {
            @Override
            public void onSuccess(BaseData data) {
                dialog.dismiss();
                //ToastUtil.makeText(context,"服务器异常，请重试",Toast.LENGTH_SHORT).show();
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
        final CustomProgressDialog dialog = CustomProgressDialog.show(context, "加载中...", true, null);
        HttpTool.doPost(context, UrlConstant.CLOSUREMICROPHONE, params, true, new TypeToken<BaseResult<BaseData>>() {
        }.getType(), new HttpTool.OnResponseListener() {
            @Override
            public void onSuccess(BaseData data) {
                dialog.dismiss();
                //ToastUtil.makeText(context,"结束连麦",Toast.LENGTH_SHORT).show();
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
    //距离当前时间差不少
    public static String getHowTime(String dataStr) {
        SimpleDateFormat formart = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String result = "";
        try {
            long start = new Date().getTime();
            long end = formart.parse(dataStr).getTime();
            long diff = end - start;
            if (diff<0){
                return 0+"小时";
            }
            long nd = 1000 * 60 * 60 * 24;
            long nh = 1000 * 60 * 60;
            long nm = 1000 * 60;
            long ns = 1000;

            // 计算差多少天
            long day = diff / nd;
            // 计算差多少小时
            long hour = diff % nd / nh;
            // 计算差多少分
            long min = diff % nh / nm;

            if(day > 0){
                result = day+"天"+hour+"小时"+min+"分";
            } else if (hour > 0){
                result = hour+"小时"+min+"分";
            } else {
                result = min+"分";
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
                //必须成功
                b++;
                if (b<3){
                    linkSuccss(context,sourceId,userId);
                }

            }
        });
    }
    //拉黑取消拉黑

    /**
     *  token	String	Y	用户token
        userId	String	Y	来黑人ID
        type	Int	Y	0—啦黑 1-取消拉黑
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
     * 获取是否拉黑
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
