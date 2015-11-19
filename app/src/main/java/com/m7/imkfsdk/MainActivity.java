package com.m7.imkfsdk;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.m7.imkfsdk.chat.ChatActivity;
import com.m7.imkfsdk.chat.OfflineMessageDialog;
import com.moor.imkf.IMChatManager;
import com.moor.imkf.OnSessionBeginListener;


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
                    public void onLeaveMessage() {
                        //提交离线留言
                        OfflineMessageDialog dialog = new OfflineMessageDialog();
                        dialog.show(getFragmentManager(), "OfflineMessageDialog");
                    }

                    @Override
                    public void onRobot() {
                        Intent chatIntent = new Intent(MainActivity.this, ChatActivity.class);
                        chatIntent.putExtra("isRobot", true);
                        startActivity(chatIntent);
                    }

                    @Override
                    public void onPeople() {
                        Intent chatIntent = new Intent(MainActivity.this, ChatActivity.class);
                        chatIntent.putExtra("isRobot", false);
                        startActivity(chatIntent);
                    }

                    @Override
                    public void onFailed() {
                        Toast.makeText(MainActivity.this, "由于网络原因等会话开始失败", Toast.LENGTH_SHORT).show();
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
