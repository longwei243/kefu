package com.m7.imkfsdk;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.m7.imkfsdk.chat.ChatActivity;
import com.m7.imkfsdk.chat.ChatActivityTest;
import com.m7.imkfsdk.chat.OfflineMessageDialog;
import com.m7.imkfsdk.chat.PeerDialog;
import com.moor.imkf.GetPeersListener;
import com.moor.imkf.IMChatManager;
import com.moor.imkf.InitListener;
import com.moor.imkf.OnSessionBeginListener;
import com.moor.imkf.model.entity.Peer;

import java.io.Serializable;
import java.util.List;


public class MainActivity extends Activity {


    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0x444:
                    getPeers();
                    break;
                case 0x555:
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

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MobileApplication.isKFSDK) {
                    getPeers();
                } else {
                    startKFService();
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
                        handler.sendEmptyMessage(0x444);

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
                        handler.sendEmptyMessage(0x555);
                        Log.d("MobileApplication", "sdk初始化失败");
                    }
                });

                //初始化IMSdk,填入相关参数
//                IMChatManager.getInstance().init(MobileApplication.getInstance(), "com.moor.im.KEFU_NEW_MSG", "afedad20-8f46-11e5-8c27-f30d41978046", "噜啦啦测试", "999");
//                IMChatManager.getInstance().init(MobileApplication.getInstance(), "com.moor.im.KEFU_NEW_MSG", "138a8d20-973b-11e5-a562-e3a3c254a8e7", "yx测试", "456");
                IMChatManager.getInstance().init(MobileApplication.getInstance(), "com.moor.im.KEFU_NEW_MSG", "afedad20-8f46-11e5-8c27-f30d41978046", "Android测试", "99999");
//                IMChatManager.getInstance().init(MobileApplication.getInstance(), "com.moor.im.KEFU_NEW_MSG", "ca7a6380-c4a3-11e5-88b2-8b944508f3f7", "雅希方正", "456");

            }
        }.start();

    }

    private void startChatActivity(String peerId) {
        Intent chatIntent = new Intent(MainActivity.this, ChatActivityTest.class);
        chatIntent.putExtra("PeerId", peerId);
        startActivity(chatIntent);
    }


}
