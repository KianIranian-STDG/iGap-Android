package net.iGap.module;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import net.iGap.module.webrtc.CallService;

public class CallActionsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (CallService.getInstance() != null) {
            CallService.getInstance().onBroadcastReceived(intent);
        }
    }
}