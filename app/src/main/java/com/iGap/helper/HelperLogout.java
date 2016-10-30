package com.iGap.helper;

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
            @Override public void run() {
                HelperRealm.realmTruncate();
                Intent intent = new Intent(G.context, ActivityIntroduce.class);
                G.context.startActivity(intent);
                G.currentActivity.finish();
            }
        });
    }
}
