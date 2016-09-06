package com.iGap.module;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.iGap.G;
import com.iGap.WebSocketClient;
import com.iGap.helper.HelperCheckInternetConnection;

public class ReceiverInternet extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (HelperCheckInternetConnection.hasActiveInternetConnection()) {
                    G.internetConnection = true;
                    if (!G.socketConnectingOrConnected) {
                        G.socketConnectingOrConnected = true;
                        WebSocketClient.getInstance();
                    }
                } else {
                    G.internetConnection = false;
                }
            }
        });
        thread.start();
    }
}
