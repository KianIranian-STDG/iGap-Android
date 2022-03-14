package net.iGap.module.accountManager;

import android.content.Context;
import android.content.SharedPreferences;

import net.iGap.G;
import net.iGap.fragments.BottomNavigationFragment;
import net.iGap.helper.FileLog;
import net.iGap.observers.eventbus.EventManager;

public class AppConfig {
    private static boolean configLoaded;
    private static final Object sync = new Object();

    public static String servicesBaseUrl;
    //in Gateway socket is 1 and api is 0
    public static int fileGateway;
    public static int defaultTab;
    public static long defaultTimeout;
    public static long maxFileSize;
    public static long messageLengthMax;
    public static boolean optimizeMode;
    public static boolean showAdvertisement;

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
            defaultTab = preferences.getInt("default_tab", BottomNavigationFragment.CHAT_FRAGMENT);
            defaultTimeout = preferences.getLong("time_out", 10);
            maxFileSize = preferences.getLong("max_file_size", 100000000);
            messageLengthMax = preferences.getLong("message_length", 1024);
            optimizeMode = preferences.getBoolean("optimized_mode", false);
            showAdvertisement = preferences.getBoolean("show_advertisement", false);
            configLoaded = true;
        }
    }

    public static void saveConfig() {
        synchronized (sync) {
            try {
                SharedPreferences preferences = G.context.getSharedPreferences("app_config", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("services_base_url", servicesBaseUrl);
                editor.putInt("file_gateway", 0);
                editor.putInt("default_tab", defaultTab);
                editor.putLong("time_out", defaultTimeout);
                editor.putLong("max_file_size", maxFileSize);
                editor.putLong("message_length", messageLengthMax);
                editor.putBoolean("optimized_mode", optimizeMode);
                editor.putBoolean("show_advertisement", showAdvertisement);
                editor.apply();

                G.runOnUiThread(() -> EventManager.getInstance(AccountManager.selectedAccount).postEvent(EventManager.APP_CONFIG_CHANGED));

            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }
}
