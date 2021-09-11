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

import android.content.SharedPreferences;
import android.view.View;

import androidx.databinding.ObservableInt;
import androidx.lifecycle.ViewModel;

import net.iGap.G;
import net.iGap.fragments.FragmentLanguage;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperTracker;
import net.iGap.module.MusicPlayer;
import net.iGap.module.SHP_SETTING;
import net.iGap.module.SingleLiveEvent;

import java.util.Locale;

public class FragmentLanguageViewModel extends ViewModel {

    private SharedPreferences sharedPreferences;

    private ObservableInt isFarsi = new ObservableInt(View.GONE);
    private ObservableInt isEnglish = new ObservableInt(View.GONE);
    private ObservableInt isArabic = new ObservableInt(View.GONE);
    private ObservableInt isFrance = new ObservableInt(View.GONE);
    private ObservableInt isSpanish = new ObservableInt(View.GONE);
    private ObservableInt isRussian = new ObservableInt(View.GONE);
    private ObservableInt isKurdi = new ObservableInt(View.GONE);
    private ObservableInt isAzeri = new ObservableInt(View.GONE);
    private SingleLiveEvent<String> refreshActivityForChangeLanguage = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> goBack = new SingleLiveEvent<>();

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
                case "Français":
                    isFrance.set(View.VISIBLE);
                    break;
                case "Español":
                    isSpanish.set(View.VISIBLE);
                    break;
                case "Russian":
                    isRussian.set(View.VISIBLE);
                    break;
                case "کوردی":
                    isKurdi.set(View.VISIBLE);
                    break;
                case "آذری":
                    isAzeri.set(View.VISIBLE);
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

    public ObservableInt getIsFrance() {
        return isFrance;
    }

    public ObservableInt getIsSpanish() {
        return isSpanish;
    }

    public ObservableInt getIsRussian() {
        return isRussian;
    }

    public ObservableInt getIsKurdi() {
        return isKurdi;
    }

    public ObservableInt getIsAzeri() {
        return isAzeri;
    }

    public SingleLiveEvent<String> getRefreshActivityForChangeLanguage() {
        return refreshActivityForChangeLanguage;
    }

    public SingleLiveEvent<Boolean> getGoBack() {
        return goBack;
    }

    public void onClickEnglish() {
        if (!G.selectedLanguage.equals("en")) {
            HelperTracker.sendTracker(HelperTracker.TRACKER_CHANGE_LANGUAGE);
            sharedPreferences.edit().putString(SHP_SETTING.KEY_LANGUAGE, "English").apply();
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
            updateLocalDateTime();
        } else {
            goBack.setValue(true);
        }
    }

    public void onClickRussian() {
        if (!G.selectedLanguage.equals("ru")) {
            HelperTracker.sendTracker(HelperTracker.TRACKER_CHANGE_LANGUAGE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(SHP_SETTING.KEY_LANGUAGE, "Russian");
            editor.apply();
            G.selectedLanguage = "ru";
            HelperCalander.isPersianUnicode = false;
            HelperCalander.isLanguagePersian = false;
            HelperCalander.isLanguageArabic = false;
            G.isAppRtl = false;
            FragmentLanguage.languageChanged = true;
            refreshActivityForChangeLanguage.setValue("ru");
            if (MusicPlayer.updateName != null) {
                MusicPlayer.updateName.rename();
            }
            updateLocalDateTime();
        }
        goBack.setValue(true);
    }

    public void onClickFrance() {
        if (!G.selectedLanguage.equals("fr")) {
            HelperTracker.sendTracker(HelperTracker.TRACKER_CHANGE_LANGUAGE);
            sharedPreferences.edit().putString(SHP_SETTING.KEY_LANGUAGE, "Français").apply();
            G.selectedLanguage = "fr";
            HelperCalander.isPersianUnicode = false;
            HelperCalander.isLanguagePersian = false;
            HelperCalander.isLanguageArabic = false;
            G.isAppRtl = false;
            FragmentLanguage.languageChanged = true;
            refreshActivityForChangeLanguage.setValue("fr");
            if (MusicPlayer.updateName != null) {
                MusicPlayer.updateName.rename();
            }
            updateLocalDateTime();
        } else {
            goBack.setValue(true);
        }
    }

    public void onClickSpanish() {
        if (!G.selectedLanguage.equals("es")) {
            HelperTracker.sendTracker(HelperTracker.TRACKER_CHANGE_LANGUAGE);
            sharedPreferences.edit().putString(SHP_SETTING.KEY_LANGUAGE, "Español").apply();
            G.selectedLanguage = "es";
            HelperCalander.isPersianUnicode = false;
            HelperCalander.isLanguagePersian = false;
            HelperCalander.isLanguageArabic = false;
            G.isAppRtl = false;
            FragmentLanguage.languageChanged = true;
            refreshActivityForChangeLanguage.setValue("es");
            if (MusicPlayer.updateName != null) {
                MusicPlayer.updateName.rename();
            }
            updateLocalDateTime();
        } else {
            goBack.setValue(true);
        }
    }

    public void onClickFarsi() {
        if (!G.selectedLanguage.equals("fa")) {
            HelperTracker.sendTracker(HelperTracker.TRACKER_CHANGE_LANGUAGE);
            sharedPreferences.edit().putString(SHP_SETTING.KEY_LANGUAGE, "فارسی").apply();
            G.selectedLanguage = "fa";
            HelperCalander.isPersianUnicode = true;
            HelperCalander.isLanguagePersian = true;
            HelperCalander.isLanguageArabic = false;
            G.isAppRtl = true;
            FragmentLanguage.languageChanged = true;
            refreshActivityForChangeLanguage.setValue("fa");
            if (MusicPlayer.updateName != null) {
                MusicPlayer.updateName.rename();
            }
            updateLocalDateTime();
        } else {
            goBack.setValue(true);
        }
    }

    public void onClickArabic() {
        if (!G.selectedLanguage.equals("ar")) {
            HelperTracker.sendTracker(HelperTracker.TRACKER_CHANGE_LANGUAGE);
            sharedPreferences.edit().putString(SHP_SETTING.KEY_LANGUAGE, "العربی").apply();
            G.selectedLanguage = "ar";
            HelperCalander.isPersianUnicode = true;
            HelperCalander.isLanguagePersian = false;
            HelperCalander.isLanguageArabic = true;
            G.isAppRtl = true;
            FragmentLanguage.languageChanged = true;
            refreshActivityForChangeLanguage.setValue("ar");
            if (MusicPlayer.updateName != null) {
                MusicPlayer.updateName.rename();
            }
            updateLocalDateTime();
        } else {
            goBack.setValue(true);
        }
    }

    //کوردی لوکال از چپ به راست است و برای استفاده از این گویش از زبان های راست به چپ جایگزین استفاده شده است
    public void onClickKurdi() {
        if (!G.selectedLanguage.equals("ur")) {
            HelperTracker.sendTracker(HelperTracker.TRACKER_CHANGE_LANGUAGE);
            sharedPreferences.edit().putString(SHP_SETTING.KEY_LANGUAGE, "کوردی").apply();
            G.selectedLanguage = "ur";
            HelperCalander.isPersianUnicode = true;
            HelperCalander.isLanguagePersian = true;
            HelperCalander.isLanguageArabic = false;
            G.isAppRtl = true;
            FragmentLanguage.languageChanged = true;
            refreshActivityForChangeLanguage.setValue("ur");
            if (MusicPlayer.updateName != null) {
                MusicPlayer.updateName.rename();
            }
            updateLocalDateTime();
        } else {
            goBack.setValue(true);
        }
    }

    //آذری - باکو لوکال از چپ به راست است و برای استفاده از این گویش از زبان های راست به چپ جایگزین استفاده شده است
    public void onClickAzeri() {
        if (!G.selectedLanguage.equals("iw")) {
            HelperTracker.sendTracker(HelperTracker.TRACKER_CHANGE_LANGUAGE);
            sharedPreferences.edit().putString(SHP_SETTING.KEY_LANGUAGE, "آذری").apply();
            G.selectedLanguage = "iw";
            HelperCalander.isPersianUnicode = true;
            HelperCalander.isLanguagePersian = true;
            HelperCalander.isLanguageArabic = false;
            G.isAppRtl = true;
            FragmentLanguage.languageChanged = true;
            refreshActivityForChangeLanguage.setValue("iw");
            if (MusicPlayer.updateName != null) {
                MusicPlayer.updateName.rename();
            }
            updateLocalDateTime();
        } else {
            goBack.setValue(true);
        }
    }

    private void updateLocalDateTime() {

        switch (G.selectedLanguage) {
            case "fa":
            case "iw": //azeri
            case "ur"://kurdi
                sharedPreferences.edit().putInt(SHP_SETTING.KEY_DATA, 1).apply();//shamsi
                break;
            case "ar":
                sharedPreferences.edit().putInt(SHP_SETTING.KEY_DATA, 2).apply();//hijri
                break;
            default:
                sharedPreferences.edit().putInt(SHP_SETTING.KEY_DATA, 0).apply();//miladi
        }

        if (G.onDateChanged != null) {
            G.onDateChanged.onChange();
        }
    }

}
