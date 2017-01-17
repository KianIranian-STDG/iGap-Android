package com.iGap.helper;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;

import com.iGap.G;
import com.iGap.activities.ActivityIntroduce;

/**
 * truncate realm and go to ActivityIntroduce for register again
 */
public final class HelperLogout {

    /**
     * truncate realm and go to ActivityIntroduce for register again
     */
    public static void logout() {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                HelperRealm.realmTruncate();
                Intent intent = new Intent(G.context, ActivityIntroduce.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                G.context.startActivity(intent);
                if (G.currentActivity != null) {
                    G.currentActivity.finish();
                }
                try {
                    NotificationManager nMgr = (NotificationManager) G.context.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    nMgr.cancelAll();
                } catch (Exception e) {
                    e.getStackTrace();
                }

            }
        });
    }
}
