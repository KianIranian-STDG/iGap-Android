package net.iGap.helper;

import com.crashlytics.android.Crashlytics;

import net.iGap.BuildConfig;
import net.iGap.Config;

public class HelperLog {

    public static void setErrorLog(Exception e) {

        Crashlytics.logException(e);

        if (Config.FILE_LOG_ENABLE) {
            IGLog.e(e);
        }

        if (BuildConfig.DEBUG) {
            e.printStackTrace();
        }
    }
}
