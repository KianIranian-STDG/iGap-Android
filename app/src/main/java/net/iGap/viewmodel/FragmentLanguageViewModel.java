package net.iGap.viewmodel;
/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import android.content.SharedPreferences;
import androidx.databinding.ObservableInt;

import android.view.View;

import net.iGap.G;
import net.iGap.fragments.FragmentLanguage;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperTracker;
import net.iGap.module.MusicPlayer;
import net.iGap.module.SHP_SETTING;

import java.util.Locale;

public class FragmentLanguageViewModel extends ViewModel {

    private SharedPreferences sharedPreferences;

    private ObservableInt isFarsi = new ObservableInt(View.GONE);
    private ObservableInt isEnglish = new ObservableInt(View.GONE);
    private ObservableInt isArabic = new ObservableInt(View.GONE);
    private MutableLiveData<String> refreshActivityForChangeLanguage = new MutableLiveData<>();
    private MutableLiveData<Boolean> goBack = new MutableLiveData<>();

    public FragmentLanguageViewModel(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
        String textLanguage = sharedPreferences.getString(SHP_SETTING.KEY_LANGUAGE, Locale.getDefault().getDisplayLanguage());
        if (textLanguage != null) {
            switch (textLanguage) {
                case "English":
                    isEnglish.set(View.VISIBLE);
                    break;
                case "فارسی":
                    isFarsi.set(View.VISIBLE);
                    break;
                case "العربی":
                    isArabic.set(View.VISIBLE);
                    break;
            }
        }
    }

    public ObservableInt getIsFarsi() {
        return isFarsi;
    }

    public ObservableInt getIsEnglish() {
        return isEnglish;
    }

    public ObservableInt getIsArabic() {
        return isArabic;
    }

    public MutableLiveData<String> getRefreshActivityForChangeLanguage() {
        return refreshActivityForChangeLanguage;
    }

    public MutableLiveData<Boolean> getGoBack() {
        return goBack;
    }

    public void onClickEnglish() {
        if (!G.selectedLanguage.equals("en")) {
            HelperTracker.sendTracker(HelperTracker.TRACKER_CHANGE_LANGUAGE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(SHP_SETTING.KEY_LANGUAGE, "English");
            editor.apply();
            G.selectedLanguage = "en";
            HelperCalander.isPersianUnicode = false;
            HelperCalander.isLanguagePersian = false;
            HelperCalander.isLanguageArabic = false;
            G.isAppRtl = false;
            FragmentLanguage.languageChanged = true;
            refreshActivityForChangeLanguage.setValue("en");
            if (MusicPlayer.updateName != null) {
                MusicPlayer.updateName.rename();
            }
        }
        goBack.setValue(true);
    }

    public void onClickFarsi() {
        if (!G.selectedLanguage.equals("fa")) {
            HelperTracker.sendTracker(HelperTracker.TRACKER_CHANGE_LANGUAGE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(SHP_SETTING.KEY_LANGUAGE, "فارسی");
            editor.apply();
            G.selectedLanguage = "fa";
            G.updateResources(G.currentActivity.getBaseContext());
            HelperCalander.isPersianUnicode = true;
            HelperCalander.isLanguagePersian = true;
            HelperCalander.isLanguageArabic = false;
            G.isAppRtl = true;
            FragmentLanguage.languageChanged = true;
            refreshActivityForChangeLanguage.setValue("fa");
            if (MusicPlayer.updateName != null) {
                MusicPlayer.updateName.rename();
            }
        }
        goBack.setValue(true);
    }

    public void onClickArabic() {
        if (!G.selectedLanguage.equals("ar")) {
            HelperTracker.sendTracker(HelperTracker.TRACKER_CHANGE_LANGUAGE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(SHP_SETTING.KEY_LANGUAGE, "العربی");
            editor.apply();
            G.selectedLanguage = "ar";
            G.updateResources(G.currentActivity.getBaseContext());
            HelperCalander.isPersianUnicode = true;
            HelperCalander.isLanguagePersian = false;
            HelperCalander.isLanguageArabic = true;
            G.isAppRtl = true;
            FragmentLanguage.languageChanged = true;
            refreshActivityForChangeLanguage.setValue("ar");
            if (MusicPlayer.updateName != null) {
                MusicPlayer.updateName.rename();
            }
        }
        goBack.setValue(true);
    }

}
