package com.iGap.module;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.iGap.WebSocketClient;

public class ReceiverBootPhone extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("OOO", "onReceive");
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Log.i("OOO", "ACTION_BOOT_COMPLETED");
            WebSocketClient.getInstance();
        }
    }
}