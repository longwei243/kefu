package com.m7.imkfsdk;import android.app.Application;import android.util.Log;import com.m7.imkfsdk.utils.FaceConversionUtil;import com.moor.im.IMChatManager;import com.moor.im.InitListener;import com.moor.im.utils.LogUtil;/** * Created by longwei */public class MobileApplication extends Application {    private static MobileApplication mobileApplication;    @Override    public void onCreate() {        super.onCreate();        mobileApplication = this;        //这个回调数据在IMService那个进程中        IMChatManager.getInstance().setOnInitListener(new InitListener() {            @Override            public void oninitSuccess() {                Log.d("MobileApplication", "sdk初始化成功");            }            @Override            public void onInitFailed() {                Log.d("MobileApplication", "sdk初始化失败");            }        });        //初始化IMSdk,启动了IMService        IMChatManager.getInstance().init(mobileApplication, "f228f440-7882-11e5-944c-43cb6c167371", "龙伟测试号", "7788");//        IMChatManager.getInstance().init(mobileApplication, "8597d2c0-7b83-11e5-86f4-57f3ea28d6d3", "龙伟", "8888");        //初始化表情,界面效果需要        new Thread(new Runnable() {            @Override            public void run() {                FaceConversionUtil.getInstace().getFileText(                        getApplicationContext());            }        }).start();    }    public static MobileApplication getInstance() {        if (mobileApplication == null) {            mobileApplication = new MobileApplication();        }        return mobileApplication;    }}