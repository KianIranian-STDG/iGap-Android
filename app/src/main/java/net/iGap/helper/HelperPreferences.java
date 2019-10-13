/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

package net.iGap.helper;

import android.content.SharedPreferences;

import net.iGap.G;

import static android.content.Context.MODE_PRIVATE;

/**
 * Use from current class for set preference in app and read data from shared preferences
 **/
public class HelperPreferences {

    private static HelperPreferences helperPreferences;
    private static SharedPreferences sharedPreferences;

    public static HelperPreferences getInstance() {
        if (helperPreferences == null) {
            helperPreferences = new HelperPreferences();
        }
        return helperPreferences;
    }

    private static SharedPreferences getSharedPreferencesInstance(String preferencesName) {
        if (sharedPreferences == null) {
            sharedPreferences = G.context.getSharedPreferences(preferencesName, MODE_PRIVATE);
        }
        return sharedPreferences;
    }


    public boolean readBoolean(String preferencesName, String key) {
        return getSharedPreferencesInstance(preferencesName).getBoolean(key, false);
    }

    public boolean readBoolean(String preferencesName, String key, boolean defValue) {
        return getSharedPreferencesInstance(preferencesName).getBoolean(key, defValue);
    }

    public void putBoolean(String preferencesName, String key, boolean value) {
        SharedPreferences.Editor editor = getSharedPreferencesInstance(preferencesName).edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public String readString(String preferencesName, String key) {
        return getSharedPreferencesInstance(preferencesName).getString(key, "-1");
    }

    public void putString(String preferencesName, String key, String value) {
        SharedPreferences.Editor editor = getSharedPreferencesInstance(preferencesName).edit();
        editor.putString(key, value);
        editor.apply();
    }
}
