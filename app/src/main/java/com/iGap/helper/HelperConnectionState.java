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
    private static Snackbar snack;

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

        /**
         * detect change activity
         */
        if (G.currentActivity != G.latestActivity) {
            G.latestConnectionState = Config.ConnectionState.UPDATING;
        }

        /**
         * detect change connection state
         */
        if (G.latestConnectionState != connectionState) {

            G.latestActivity = G.currentActivity;
            G.latestConnectionState = connectionState;
            String message = G.context.getResources().getString(R.string.waiting_for_network);

            if (connectionState == Config.ConnectionState.WAITING_FOR_NETWORK) {
                message = G.context.getResources().getString(R.string.waiting_for_network);
            } else if (connectionState == Config.ConnectionState.CONNECTING) {
                message = G.context.getResources().getString(R.string.connecting);
            }

            final String finalMessage = message;
            if (G.currentActivity != null) {
                final String finalMessage2 = message;
                G.currentActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (snack == null) {
                            snack = Snackbar.make(G.currentActivity.findViewById(android.R.id.content), finalMessage, 10 * 60 * 1000);
                            snack.setAction(R.string.cancel, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    snack.dismiss();
                                }
                            });
                        }

                        if (G.currentActivity instanceof ActivityMain) {
                            /**
                             * don't show snack in ActivityMain
                             */
                            if (snack.isShown()) {
                                snack.dismiss();
                            }
                        } else if (connectionState == Config.ConnectionState.IGAP || connectionState == Config.ConnectionState.UPDATING) {
                            /**
                             * hide snack if connected to server
                             */
                            G.currentActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    snack.dismiss();
                                }
                            });
                        } else {
                            /**
                             * show snack in CONNECTING and WAITING_FOR_NETWORK state
                             */
                            if (!snack.isShown()) {
                                snack.setText(finalMessage2);
                                snack.show();
                            }
                        }
                    }
                });
            }
        }
    }
}
