package com.m7.imkfsdk;import android.app.Application;import com.m7.imkfsdk.utils.FaceConversionUtil;import com.moor.im.IMChatManager;import com.moor.im.InitListener;import com.moor.im.utils.LogUtil;/** * Created by longwei */public class MobileApplication extends Application {    private static MobileApplication mobileApplication;    @Override    public void onCreate() {        super.onCreate();        mobileApplication = this;        //这个回调数据在IMService那个进程中        IMChatManager.getInstance().setOnInitListener(new InitListener() {            @Override            public void oninitSuccess() {                LogUtil.d("MobileApplication", "sdk初始化成功");            }            @Override            public void onInitFailed() {                LogUtil.d("MobileApplication", "sdk初始化失败");            }        });        //初始化IMSdk,启动了IMService        IMChatManager.getInstance().init(mobileApplication, "edf16500-76d5-11e5-bd17-85f0e3ff525f", "龙伟", "8888");        //初始化表情,界面效果需要        new Thread(new Runnable() {            @Override            public void run() {                FaceConversionUtil.getInstace().getFileText(                        getApplicationContext());            }        }).start();    }    public static MobileApplication getInstance() {        if (mobileApplication == null) {            mobileApplication = new MobileApplication();        }        return mobileApplication;    }}