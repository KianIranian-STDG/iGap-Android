package com.iGap.module;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.iGap.Config;
import com.iGap.G;
import com.iGap.WebSocketClient;
import com.iGap.helper.HelperCheckInternetConnection;
import com.iGap.helper.HelperConnectionState;

public class ReceiverInternet extends BroadcastReceiver {

    @Override public void onReceive(Context context, Intent intent) {
        if (HelperCheckInternetConnection.hasNetwork()) {
            if (G.canRunReceiver) {
                G.canRunReceiver = false;
                WebSocketClient.getInstance();
            }
        } else {
            HelperConnectionState.connectionState(Config.ConnectionState.WAITING_FOR_NETWORK);
            G.socketConnection = false;
        }
    }
}
