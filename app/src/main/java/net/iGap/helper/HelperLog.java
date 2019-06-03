package net.iGap.helper;

import com.crashlytics.android.Crashlytics;

import net.iGap.BuildConfig;

public class HelperLog {

    public static void setErrorLog(Exception e) {

        Crashlytics.logException(e);

        if (BuildConfig.DEBUG) {
            e.printStackTrace();
        }
    }
}
