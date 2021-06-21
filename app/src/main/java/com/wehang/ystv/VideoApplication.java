package com.wehang.ystv;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.Utils;
import com.lzy.okgo.OkGo;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import com.tencent.TIMManager;
import com.tencent.bugly.crashreport.CrashReport;

import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.rtmp.TXLiveBase;
import com.tencent.rtmp.TXLiveConstants;
import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;
import com.wehang.ystv.logic.TCIMInitMgr;
import com.whcd.base.BaseApplication;


import java.util.List;
import java.util.Random;

import cn.jpush.android.api.JPushInterface;

public class VideoApplication extends BaseApplication {
    public static Context applicationContext;
    public final String PREF_USERNAME = "username";
    public static String currentUserNick = "";
    public static int status;
    protected String appName = "com.whcd.wayward";
    private LogUtils.Builder mBuilder;


    {
        // 微信 appid appsecret
        PlatformConfig.setWeixin("wx4ff4159ac940a0de", "473f108a05785f4d4eb17e719d5eafb4");
//        // 新浪微博 appkey appsecret
//        PlatformConfig.setSinaWeibo("1845195245", "3407db9e8418adeb09a6fd973d59c9d9");
//        Config.REDIRECT_URL = "http://sns.whalecloud.com/sina2/callback";
        // QQ和Qzone appid appkey
//        PlatformConfig.setQQZone("1106141815", "yC3HUul5HLTKL8UF");
        PlatformConfig.setQQZone("1106307457", "hbwj3wYT3CDhgG0S");
    }

    private IWXAPI api;

    @Override
    public void onCreate() {
        super.appName = "com.whcd.wayward";
        super.onCreate();

        applicationContext = this;
        //HXHelper.getInstance().init(applicationContext);
        Utils.init(applicationContext);

        OkGo.init(this);
        //初始化日志信息
        mBuilder = new LogUtils.Builder();
        mBuilder.setLogSwitch(Constant.LOG)
                .setGlobalTag(Constant.TAG);



        //七牛直播
      //  StreamingEnv.init(getApplicationContext());

//        LeakCanary.install(this);

        //有道
       // YouDaoApplication.init(this,"4cf11a7167a41e81");

        //蒲公英版本管理


        // 初始化ImageLoader
        @SuppressWarnings("deprecation")
        DisplayImageOptions options = new DisplayImageOptions.Builder().showStubImage(R.drawable.default_cover) // 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.drawable.default_cover) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.default_cover) // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisc(true) // 设置下载的图片是否缓存在SD卡中
                // .displayer(new RoundedBitmapDisplayer(20)) // 设置成圆角图片
                .build(); // 创建配置过得DisplayImageOption对象

        @SuppressWarnings("deprecation")
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext()).defaultDisplayImageOptions(options)
                .threadPriority(Thread.NORM_PRIORITY - 2).denyCacheImageMultipleSizesInMemory().discCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO).build();
        ImageLoader.getInstance().init(config);



        UMShareAPI.get(this);
		/*
         * Bugly SDK初始化 参数1：上下文对象 参数2：APPID，平台注册时得到,注意替换成你的appId
		 * 参数3：是否开启调试模式，调试模式下会输出'CrashReport'tag的日志
		 */
		//腾讯追踪报错
        CrashReport.initCrashReport(getApplicationContext(), "67deb4269f", false);


        JPushInterface.setDebugMode(true); // 设置开启日志,发布时请关闭日志
        JPushInterface.init(this); // 初始化 JPush
        // 在主进程设置信鸽相关的内容

        //初始化TIM腾讯IM
        TCIMInitMgr.init(getApplicationContext());

        String sdkver = TXLiveBase.getSDKVersionStr();


        Log.d("liteavsdk", "liteav sdk version is : " + sdkver);
        TXLiveBase.setConsoleEnabled(true);
        TXLiveBase.setLogLevel(TXLiveConstants.LOG_LEVEL_DEBUG);



    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }

    public static int getRandomStreamId() {
        Random random = new Random();
        int randint = (int) Math.floor((random.nextDouble() * 10000.0 + 10000.0));
        return randint;
    }

    public static VideoApplication getInstance() {
        return (VideoApplication) instance;
    }

    public boolean isMainProcess() {
        ActivityManager am = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
        List<RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = getPackageName();
        int myPid = android.os.Process.myPid();
        for (RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 程序是否在前台运行
     *
     * @return
     */
    public boolean isAppOnForeground() {
        ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = getApplicationContext().getPackageName();

        List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null)
            return false;

        for (RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(packageName) && appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }

        return false;
    }

}
