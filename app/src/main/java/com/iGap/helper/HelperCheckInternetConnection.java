package com.iGap.helper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.iGap.G;

/**
 * Helper Class for checking internet connection
 */
public class HelperCheckInternetConnection {

    public static boolean hasNetwork() {
        ConnectivityManager cm =
                ((ConnectivityManager) G.context.getSystemService(Context.CONNECTIVITY_SERVICE));
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null) {
            return true;
        }
        return false;
    }
}
