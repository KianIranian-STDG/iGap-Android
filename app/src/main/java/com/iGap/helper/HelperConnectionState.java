package com.iGap.helper;

import android.support.design.widget.Snackbar;
import android.view.View;
import com.iGap.Config;
import com.iGap.G;
import com.iGap.R;
import com.iGap.activities.ActivityMain;

/**
 * manage connection state for showing state in main page
 */
public class HelperConnectionState {

    public static Snackbar snack = null;

    public static void connectionState(final Config.ConnectionState connectionState) {
        if (HelperCheckInternetConnection.hasNetwork()) {
            if (G.onConnectionChangeState != null) {
                G.onConnectionChangeState.onChangeState(connectionState);
            }
            G.connectionState = connectionState;
        } else {
            if (G.onConnectionChangeState != null) {
                G.onConnectionChangeState.onChangeState(Config.ConnectionState.WAITING_FOR_NETWORK);
            }
            G.connectionState = Config.ConnectionState.WAITING_FOR_NETWORK;
        }

        if (G.currentActivity != G.latestActivity) {
            G.latestConnectionState = Config.ConnectionState.UPDATING;
        }

        if (G.currentActivity instanceof ActivityMain || connectionState == Config.ConnectionState.IGAP || connectionState == Config.ConnectionState.UPDATING) {

            if (snack != null) {
                if (snack.isShown()) {
                    snack.dismiss();
                }
                snack = null;
            }
        } else {

            if (G.latestConnectionState != G.connectionState) {

                G.latestActivity = G.currentActivity;
                G.latestConnectionState = G.connectionState;
                String message = G.context.getResources().getString(R.string.waiting_for_network);

                if (G.connectionState == Config.ConnectionState.WAITING_FOR_NETWORK) {
                    message = G.context.getResources().getString(R.string.waiting_for_network);
                } else if (G.connectionState == Config.ConnectionState.CONNECTING) {
                    message = G.context.getResources().getString(R.string.connecting);
                }

                final String finalMessage = message;
                if (G.currentActivity != null) {
                    final String finalMessage2 = message;
                    G.currentActivity.runOnUiThread(new Runnable() {
                        @Override public void run() {

                            snack = null;
                            snack = Snackbar.make(G.currentActivity.findViewById(android.R.id.content), finalMessage, 600000);
                            snack.setAction(R.string.cancel, new View.OnClickListener() {
                                @Override public void onClick(View view) {
                                    snack.dismiss();
                                }
                            });
                            snack.setText(finalMessage2);
                            snack.show();
                        }
                    });
                }
            }
        }
    }
}
