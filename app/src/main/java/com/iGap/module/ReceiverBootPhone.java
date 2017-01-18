package com.iGap.module;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ReceiverBootPhone extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            // run service for app
        }
    }
}