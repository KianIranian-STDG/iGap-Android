package net.iGap.controllers;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import net.iGap.G;

import static net.iGap.module.SHP_SETTING.FILE_NAME;

public class SharedManager {

    private SharedPreferences settingSharedPreferences;
    private SharedPreferences globalSharedPreferences;
    private SharedPreferences languageSharedPreferences;
    private SharedPreferences emojiSharedPreferences;
    private SharedPreferences notificationSharedPreferences;

    private static volatile SharedManager Instance = null;

    public static SharedManager getInstance() {
        SharedManager localInstance = Instance;
        if (localInstance == null) {
            synchronized (SharedManager.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    Instance = localInstance = new SharedManager();
                }
            }
        }
        return localInstance;
    }

    public SharedManager() {
        if (G.context != null) {
            settingSharedPreferences = G.context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
            emojiSharedPreferences = G.context.getSharedPreferences("emoji", Activity.MODE_PRIVATE);
        }
    }

    public SharedPreferences getSettingSharedPreferences() {
        return settingSharedPreferences;
    }

    public SharedPreferences getGlobalEmojiSettings() {
        return emojiSharedPreferences;
    }
}
