package net.iGap.module.accountManager;

import android.content.Context;
import android.content.SharedPreferences;

import net.iGap.G;
import net.iGap.helper.FileLog;
import net.iGap.observers.eventbus.EventManager;

public class AppConfig {
    private static boolean configLoaded;
    private static final Object sync = new Object();

    public static String servicesBaseUrl;
    //in Gateway socket is 0 and api is 1
    public static int fileGateway;
    public static long defaultTimeout;
    public static long maxFileSize;
    public static long messageLengthMax;
    public static boolean optimizeMode;

    static {
        loadConfig();
    }

    public static void loadConfig() {
        synchronized (sync) {
            if (configLoaded) {
                return;
            }

            FileLog.i("loadConfig");

            SharedPreferences preferences = G.context.getSharedPreferences("app_config", Context.MODE_PRIVATE);
            servicesBaseUrl = preferences.getString("services_base_url", "https://gate.igap.net");
            fileGateway = preferences.getInt("file_gateway", 0);
            defaultTimeout = preferences.getLong("time_out", 10);
            maxFileSize = preferences.getLong("max_file_size", 100000000);
            messageLengthMax = preferences.getLong("message_length", 1024);
            optimizeMode = preferences.getBoolean("optimized_mode", false);
            configLoaded = true;
        }
    }

    public static void saveConfig() {
        synchronized (sync) {
            try {
                SharedPreferences preferences = G.context.getSharedPreferences("app_config", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("services_base_url", servicesBaseUrl);
                editor.putInt("file_gateway", fileGateway);
                editor.putLong("time_out", defaultTimeout);
                editor.putLong("max_file_size", maxFileSize);
                editor.putLong("message_length", messageLengthMax);
                editor.putBoolean("optimized_mode", optimizeMode);
                editor.apply();

                EventManager.getInstance().postEvent(EventManager.APP_CONFIG_CHANGED);

            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }
}
