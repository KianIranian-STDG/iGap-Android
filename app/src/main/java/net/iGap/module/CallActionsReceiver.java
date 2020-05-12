package net.iGap.module;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class CallActionsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (CallService.getInstance() != null) {
            CallService.getInstance().onBroadcastReceived(intent);
        }
    }
}