package com.m7.imkfsdk;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.m7.imkfsdk.chat.ChatActivity;
import com.m7.imkfsdk.chat.LoadingFragmentDialog;
import com.m7.imkfsdk.chat.PeerDialog;
import com.moor.imkf.GetPeersListener;
import com.moor.imkf.IMChatManager;
import com.moor.imkf.InitListener;
import com.moor.imkf.model.entity.Peer;

import java.io.Serializable;
import java.util.List;


public class MainActivity extends Activity {

    private SharedPreferences sp;
    private LoadingFragmentDialog loadingDialog;


    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0x444:
                    loadingDialog.dismiss();
                    getPeers();

                    break;
                case 0x555:
                    loadingDialog.dismiss();
                    Toast.makeText(MainActivity.this, "客服初始化失败", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        sp = getSharedPreferences("setting", 0);
        loadingDialog = new LoadingFragmentDialog();
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //判断版本若为6.0申请权限
                if(Build.VERSION.SDK_INT < 23) {
                    init();
                }else {
                    //6.0
                    if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                        //该权限已经有了
                        init();
                    }else {
                        //申请该权限
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, 0x1111);
                    }
                }
            }
        });
        findViewById(R.id.cancel_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MobileApplication.isKFSDK) {
                    IMChatManager.getInstance().quit();
                    MobileApplication.isKFSDK = false;
                }
            }
        });

        findViewById(R.id.setting_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingIntent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(settingIntent);
            }
        });
    }

    private void init() {
        //填写过参数才能登录
//                if(!"".equals(sp.getString("accessId", ""))) {
            loadingDialog.show(getFragmentManager(), "");
            if (MobileApplication.isKFSDK) {
                loadingDialog.dismiss();
                getPeers();
            } else {
                startKFService();
            }
//                }else {
//                    Toast.makeText(MainActivity.this, "请先设置参数", Toast.LENGTH_SHORT).show();
//                }
    }

    private void getPeers() {
        IMChatManager.getInstance().getPeers(new GetPeersListener() {
            @Override
            public void onSuccess(List<Peer> peers) {
                if (peers.size() > 1) {
                    PeerDialog dialog = new PeerDialog();
                    Bundle b = new Bundle();
                    b.putSerializable("Peers", (Serializable) peers);
                    dialog.setArguments(b);
                    dialog.show(getFragmentManager(), "");

                } else if (peers.size() == 1) {
                    startChatActivity(peers.get(0).getId());
                } else {
                    startChatActivity("");
                }
            }

            @Override
            public void onFailed() {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }



    private void startKFService() {

        new Thread() {
            @Override
            public void run() {
                IMChatManager.getInstance().setOnInitListener(new InitListener() {
                    @Override
                    public void oninitSuccess() {
                        MobileApplication.isKFSDK = true;
//                        handler.sendEmptyMessage(0x444);
                        loadingDialog.dismiss();
                        getPeers();
                        Log.d("MobileApplication", "sdk初始化成功");
                        //初始化表情,界面效果需要
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                com.m7.imkfsdk.utils.FaceConversionUtil.getInstace().getFileText(
                                        MobileApplication.getInstance());
                            }
                        }).start();
                    }

                    @Override
                    public void onInitFailed() {
                        MobileApplication.isKFSDK = false;
                        loadingDialog.dismiss();
                        Toast.makeText(MainActivity.this, "客服初始化失败", Toast.LENGTH_SHORT).show();
//                        handler.sendEmptyMessage(0x555);
                        Log.d("MobileApplication", "sdk初始化失败");
                    }
                });

//                String accessId = sp.getString("accessId", "");
//                String name = sp.getString("name", "");
//                String userId = sp.getString("userId", "");
                //初始化IMSdk,填入相关参数
//                IMChatManager.getInstance().init(MobileApplication.getInstance(), "com.moor.im.KEFU_NEW_MSG", "2ff6ebc0-e40c-11e5-82a5-51d279813f91", "游客", "999000");
                IMChatManager.getInstance().init(MobileApplication.getInstance(), "com.moor.im.KEFU_NEW_MSG", "1a407410-9ee1-11e5-a8e6-17b9721f92b3", "未知", "123456");
            }
        }.start();

    }

    private void startChatActivity(String peerId) {
        Intent chatIntent = new Intent(MainActivity.this, ChatActivity.class);
        chatIntent.putExtra("PeerId", peerId);
        startActivity(chatIntent);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 0x1111:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    init();
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                break;
        }
    }
}
