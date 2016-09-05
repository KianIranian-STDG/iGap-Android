package com.iGap.module;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.iGap.WebSocketClient;
import com.iGap.helper.HelperCheckInternetConnection;

public class ReceiverInternet extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (HelperCheckInternetConnection.hasActiveInternetConnection()) {
                    Log.i("III", "internet ok");
                    WebSocketClient.getInstance();
                } else {
                    Log.i("III", "no internet");
                }
            }
        });
        thread.start();
    }
}
