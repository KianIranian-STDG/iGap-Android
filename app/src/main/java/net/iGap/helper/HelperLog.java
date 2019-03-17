package net.iGap.helper;

import android.util.Log;

import com.crashlytics.android.Crashlytics;

import net.iGap.BuildConfig;

import java.io.PrintWriter;
import java.io.StringWriter;

public class HelperLog {

    public static void setErrorLog(String message) {

        Crashlytics.logException(new Exception(message));

        if (BuildConfig.DEBUG) {
            Log.e("debug", message);
        }
    }

    public static void setErrorLog(Exception e) {

        Crashlytics.logException(e);

        if (BuildConfig.DEBUG) {
            e.printStackTrace();
        }
    }
}
