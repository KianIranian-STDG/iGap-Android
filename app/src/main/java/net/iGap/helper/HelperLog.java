package net.iGap.helper;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

import net.iGap.BuildConfig;
import net.iGap.Config;
import net.iGap.module.accountManager.AccountManager;

public class HelperLog {

    private static HelperLog instance;
    private FirebaseCrashlytics crashlytics;

    private HelperLog() {
        crashlytics = FirebaseCrashlytics.getInstance();
        crashlytics.setUserId(String.valueOf(AccountManager.getInstance().getCurrentUser().getId()));
    }

    public static HelperLog getInstance() {
        if (instance == null) {
            synchronized (HelperLog.class) {
                if (instance == null) {
                    instance = new HelperLog();
                }
            }
        }
        return instance;
    }

    public void setErrorLog(Exception e) {
        crashlytics.recordException(e);

        if (Config.FILE_LOG_ENABLE) {
            FileLog.e(e);
        }

        if (BuildConfig.DEBUG) {
            e.printStackTrace();
        }
    }
}
