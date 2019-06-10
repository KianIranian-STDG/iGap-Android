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

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.SharedPreferences;
import android.databinding.ObservableField;
import android.support.v4.app.Fragment;

import net.iGap.G;
import net.iGap.fragments.FragmentCall;
import net.iGap.fragments.FragmentChatSettings;
import net.iGap.fragments.FragmentLanguage;
import net.iGap.fragments.FragmentMain;
import net.iGap.fragments.FragmentNotificationAndSound;
import net.iGap.fragments.FragmentPrivacyAndSecurity;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperLog;
import net.iGap.helper.HelperLogout;
import net.iGap.interfaces.OnUserSessionLogout;
import net.iGap.module.SHP_SETTING;
import net.iGap.request.RequestUserSessionLogout;

import java.util.Locale;

public class FragmentSettingViewModel extends ViewModel {

    public MutableLiveData<Boolean> showLoading = new MutableLiveData<>();
    private MutableLiveData<String> currentLanguage = new MutableLiveData<>();

    //ui
    public MutableLiveData<Boolean> showDialogDeleteAccount = new MutableLiveData<>();
    public MutableLiveData<Boolean> goToManageSpacePage = new MutableLiveData<>();
    public MutableLiveData<Boolean> showDialogLogout = new MutableLiveData<>();
    public MutableLiveData<Boolean> showError = new MutableLiveData<>();
    public MutableLiveData<Boolean> goBack = new MutableLiveData<>();
    public ObservableField<Boolean> isCameraButtonSheet = new ObservableField<>(true);

    public String phoneNumber;
    public long userId;

    public LiveData<String> getCurrentLanguage() {
        return currentLanguage;
    }

    public FragmentSettingViewModel(SharedPreferences sharedPreferences) {
        currentLanguage.setValue(sharedPreferences.getString(SHP_SETTING.KEY_LANGUAGE, Locale.getDefault().getDisplayLanguage()));
    }

    public void onLanguageClick(){
        new HelperFragment(new FragmentLanguage()).setReplace(false).load();
    }

    public void onClickNotifyAndSound() {
        new HelperFragment(new FragmentNotificationAndSound()).setReplace(false).load();
    }

    public void onClickPrivacySecurity() {
        new HelperFragment(new FragmentPrivacyAndSecurity()).setReplace(false).load();
    }

    public void onClickDataStorage() {
        goToManageSpacePage.setValue(true);
    }

    public void onChatSettingClick() {
        new HelperFragment(new FragmentChatSettings()).setReplace(false).load();
    }

    public void onLogoutClick() {
        showDialogLogout.setValue(true);
    }

    public void logout() {
        showLoading.setValue(true);
        new RequestUserSessionLogout().userSessionLogout(new OnUserSessionLogout() {
            @Override
            public void onUserSessionLogout() {
                G.handler.post(() -> {
                    HelperLogout.logout();
                    showLoading.setValue(false);
                });
            }

            @Override
            public void onError() {

            }

            @Override
            public void onTimeOut() {
                G.handler.post(() -> {
                    showLoading.setValue(false);
                    showError.setValue(true);
                });
            }
        });
    }

    public void onDeleteAccountClick() {
        showDialogDeleteAccount.setValue(true);
    }

    public void onStop() {
        updateRoomListIfNeeded();
    }

    private void updateRoomListIfNeeded() {

        try {

            for (Fragment f : G.fragmentManager.getFragments()) {

                if (f == null) {
                    continue;
                }

                if (f instanceof FragmentMain || f instanceof FragmentCall) {
                    f.onResume();
                }
            }
        } catch (Exception e) {
            HelperLog.setErrorLog(e);
        }
    }
}
