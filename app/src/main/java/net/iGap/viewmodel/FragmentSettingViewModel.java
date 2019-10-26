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
import android.util.Log;
import android.view.View;

import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import net.iGap.AccountManager;
import net.iGap.helper.HelperLogout;
import net.iGap.module.SHP_SETTING;
import net.iGap.module.SingleLiveEvent;

import java.util.Locale;

public class FragmentSettingViewModel extends ViewModel {

    private MutableLiveData<Integer> showLoading = new MutableLiveData<>();
    private ObservableField<String> currentLanguage = new ObservableField<>();

    //ui
    public SingleLiveEvent<Boolean> showDialogDeleteAccount = new SingleLiveEvent<>();
    public SingleLiveEvent<Boolean> goToManageSpacePage = new SingleLiveEvent<>();
    public SingleLiveEvent<Boolean> goToLanguagePage = new SingleLiveEvent<>();
    public SingleLiveEvent<Boolean> goToNotificationAndSoundPage = new SingleLiveEvent<>();
    public SingleLiveEvent<Boolean> goToPrivacyAndSecurityPage = new SingleLiveEvent<>();
    public SingleLiveEvent<Boolean> goToChatSettingsPage = new SingleLiveEvent<>();
    public SingleLiveEvent<Boolean> showDialogLogout = new SingleLiveEvent<>();
    public SingleLiveEvent<Boolean> showError = new SingleLiveEvent<>();
    public SingleLiveEvent<Boolean> goBack = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> updateForOtherAccount = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> goToRegisterPage = new SingleLiveEvent<>();
    public ObservableField<Boolean> isCameraButtonSheet = new ObservableField<>(true);

    public String phoneNumber;
    public long userId;

    public FragmentSettingViewModel(SharedPreferences sharedPreferences) {
        currentLanguage.set(sharedPreferences.getString(SHP_SETTING.KEY_LANGUAGE, Locale.getDefault().getDisplayLanguage()));
        showLoading.postValue(View.GONE);
    }

    public ObservableField<String> getCurrentLanguage() {
        return currentLanguage;
    }

    public MutableLiveData<Integer> getShowLoading() {
        return showLoading;
    }

    public SingleLiveEvent<Boolean> getUpdateForOtherAccount() {
        return updateForOtherAccount;
    }

    public SingleLiveEvent<Boolean> getGoToRegisterPage() {
        return goToRegisterPage;
    }

    public void onLanguageClick() {
        goToLanguagePage.setValue(true);
    }

    public void onClickNotifyAndSound() {
        goToNotificationAndSoundPage.setValue(true);
    }

    public void onClickPrivacySecurity() {
        goToPrivacyAndSecurityPage.setValue(true);
    }

    public void onClickDataStorage() {
        goToManageSpacePage.setValue(true);
    }

    public void onChatSettingClick() {
        goToChatSettingsPage.setValue(true);
    }

    public void onLogoutClick() {
        showDialogLogout.setValue(true);
    }

    public void logout() {
        showLoading.setValue(View.VISIBLE);
        new HelperLogout().logoutUserWithRequest(new HelperLogout.LogOutUserCallBack() {
            @Override
            public void onLogOut(boolean haveAnotherAccount) {
                Log.wtf(this.getClass().getName(), "haveAnotherAccount: " + haveAnotherAccount);
                showLoading.postValue(View.GONE);
                Log.wtf(this.getClass().getName(), "current user: " + AccountManager.getInstance().getCurrentUser().toString());
                if (haveAnotherAccount) {
                    updateForOtherAccount.postValue(true);
                } else {
                    goToRegisterPage.postValue(true);
                }
            }

            @Override
            public void onError() {
                showLoading.postValue(View.GONE);
                showError.postValue(true);
            }
        });
    }

    public void onDeleteAccountClick() {
        showDialogDeleteAccount.setValue(true);
    }
}
