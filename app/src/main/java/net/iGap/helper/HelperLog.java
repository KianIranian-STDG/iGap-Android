package net.iGap.helper;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

import net.iGap.BuildConfig;
import net.iGap.Config;

public class HelperLog {

    public static void setErrorLog(Exception e) {

        FirebaseCrashlytics.getInstance().recordException(e);

        if (Config.FILE_LOG_ENABLE) {
            IGLog.e(e);
        }

        if (BuildConfig.DEBUG) {
            e.printStackTrace();
        }
    }
}
