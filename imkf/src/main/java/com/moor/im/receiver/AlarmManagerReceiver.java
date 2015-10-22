package com.moor.im.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.moor.im.tcpservice.service.IMService;

public class AlarmManagerReceiver extends BroadcastReceiver {
    public AlarmManagerReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent imserviceIntent = new Intent(context, IMService.class);
        context.startService(imserviceIntent);
    }
}
