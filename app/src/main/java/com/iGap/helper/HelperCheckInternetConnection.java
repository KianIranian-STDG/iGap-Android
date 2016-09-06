package com.iGap.helper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.iGap.G;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Helper Class for checking internet connection
 */
public class HelperCheckInternetConnection {

    public static boolean hasActiveInternetConnection() {

        ConnectivityManager cm = ((ConnectivityManager) G.context.getSystemService(Context.CONNECTIVITY_SERVICE));
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        if (networkInfo != null) {
            try {
                HttpURLConnection urlConnection = (HttpURLConnection) (new URL("http://www.google.com").openConnection());
                urlConnection.setRequestProperty("User-Agent", "Test");
                urlConnection.setRequestProperty("Connection", "close");
                urlConnection.setConnectTimeout(20000);

                urlConnection.connect();

                return (urlConnection.getResponseCode() == 200); // true
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

}
