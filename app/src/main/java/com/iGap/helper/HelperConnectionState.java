package com.iGap.helper;

import com.iGap.Config;
import com.iGap.G;

/**
 * manage connection state for showing state in main page
 */
public class HelperConnectionState {

    public static void connectionState(Config.ConnectionState connectionState) {
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
    }
}
