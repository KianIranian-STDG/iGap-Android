package net.iGap.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import net.iGap.G;
import net.iGap.module.SHP_SETTING;
import net.iGap.module.Theme;

public class AutoDarkModeSetter{


    /**
     * This method does auto assigning app theme to mobile theme
     */
    public static void setStartingTheme() {

        SharedPreferences sharedPreferences = G.context.getSharedPreferences(SHP_SETTING.FILE_NAME, Context.MODE_PRIVATE);

        if (sharedPreferences.getBoolean(SHP_SETTING.KEY_AUTO_DARK_MODE, true)) {

            switch (G.context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {

                case Configuration.UI_MODE_NIGHT_YES:
                    G.themeColor = Theme.DARK;
                    sharedPreferences.edit().putInt(SHP_SETTING.KEY_THEME_COLOR, Theme.DARK).apply();
                    break;

                case Configuration.UI_MODE_NIGHT_NO:
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
