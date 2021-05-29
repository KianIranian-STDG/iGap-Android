package net.iGap.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.util.Log;

import net.iGap.G;
import net.iGap.module.SHP_SETTING;
import net.iGap.module.Theme;

public class AutoDarkModeSetter{


    public static final int DARK_SYSTEM_UI_NUMBER = 33;
    public static final int LIGHT_SYSTEM_UI_NUMBER = 17;

    /**
     * This method does auto assigning app theme to mobile theme
     */
    public static void setStartingTheme() {

        SharedPreferences sharedPreferences = G.context.getSharedPreferences(SHP_SETTING.FILE_NAME, Context.MODE_PRIVATE);

        if (sharedPreferences.getBoolean(SHP_SETTING.KEY_AUTO_DARK_MODE, true)) {

            int currentTheme = sharedPreferences.getInt(SHP_SETTING.KEY_SYSTEM_UI_MODE, 17);
            switch (currentTheme) {
                case DARK_SYSTEM_UI_NUMBER:

                    G.themeColor = Theme.DARK;
                    sharedPreferences.edit().putInt(SHP_SETTING.KEY_THEME_COLOR, Theme.DARK).apply();
                    break;

                case LIGHT_SYSTEM_UI_NUMBER:

                    G.themeColor = sharedPreferences.getInt(SHP_SETTING.KEY_USER_SELECTED_THEME_COLOR, Theme.DEFAULT);
                    sharedPreferences.edit().putInt(SHP_SETTING.KEY_THEME_COLOR, G.themeColor).apply();
                    break;
            }

        } else {
            G.themeColor = sharedPreferences.getInt(SHP_SETTING.KEY_USER_SELECTED_THEME_COLOR, Theme.DEFAULT);
            sharedPreferences.edit().putInt(SHP_SETTING.KEY_THEME_COLOR, G.themeColor).apply();
        }
    }
}
