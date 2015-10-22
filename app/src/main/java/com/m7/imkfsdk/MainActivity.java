package com.m7.imkfsdk;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.m7.imkfsdk.chat.ChatActivity;
import com.m7.imkfsdk.chat.OfflineMessageDialog;
import com.moor.im.IMChatManager;
import com.moor.im.OnSessionBeginListener;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                IMChatManager.getInstance().beginSession(new OnSessionBeginListener() {
                    @Override
                    public void onSuccess() {
                        //联系客服
                        Intent chatIntent = new Intent(MainActivity.this, ChatActivity.class);
                        startActivity(chatIntent);

                    }

                    @Override
                    public void onFailed() {
                        Toast.makeText(MainActivity.this, "会话开始失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
