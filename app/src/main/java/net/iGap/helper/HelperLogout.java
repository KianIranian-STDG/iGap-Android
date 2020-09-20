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

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import net.iGap.G;
import net.iGap.model.AccountUser;
import net.iGap.module.AppUtils;
import net.iGap.module.LoginActions;
import net.iGap.module.SHP_SETTING;
import net.iGap.module.Theme;
import net.iGap.module.accountManager.AccountHelper;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.module.accountManager.DbManager;
import net.iGap.observers.interfaces.OnUserSessionLogout;
import net.iGap.request.RequestUserSessionLogout;


/**
 * truncate realm and go to ActivityIntroduce for register again
 */
public final class HelperLogout {

    /**
     * truncate realm and go to ActivityIntroduce for register again
     */
    private void logout() {
        DbManager.getInstance().doRealmTransaction(realm -> {
            realm.deleteAll();
        });

        AppUtils.cleanBadge();
        new LoginActions();
    }

    public boolean logoutAllUser() {
        boolean tmp = new AccountHelper().logoutAccount();
        if (tmp) {
            return logoutAllUser();
        } else {
            return false;
        }
    }

    public boolean logoutUser(AccountUser accountUser) {
        if (accountUser.isAssigned()) {
            logout();
            boolean tmp = AccountManager.getInstance().removeUser(accountUser);
            if (!tmp) {
                clearPreferences();
                resetStaticField();
            }
            return tmp;
        } else {
            return false;
        }
    }

    public boolean logoutUser() {
        Log.wtf(this.getClass().getName(), "logoutUser");
        return logoutUser(AccountManager.getInstance().getCurrentUser());
    }

    public void logoutUserWithRequest(LogOutUserCallBack logOutUserCallBack) {
        new RequestUserSessionLogout().userSessionLogout(new OnUserSessionLogout() {
            @Override
            public void onUserSessionLogout() {
                logOutUserCallBack.onLogOut();
                new LoginActions();
            }

            @Override
            public void onError() {
                logOutUserCallBack.onError();
            }

            @Override
            public void onTimeOut() {
                logOutUserCallBack.onError();
            }
        });
    }

    public interface LogOutUserCallBack {
        void onLogOut();

        void onError();
    }

    private void clearPreferences() {
        SharedPreferences sharedPreferencesFile = G.context.getSharedPreferences(SHP_SETTING.FILE_NAME, Context.MODE_PRIVATE);
        sharedPreferencesFile.edit().clear().apply();

        SharedPreferences sharedPreferencesTrackerFile = G.context.getSharedPreferences(SHP_SETTING.KEY_TRACKER_FILE, Context.MODE_PRIVATE);
        sharedPreferencesTrackerFile.edit().clear().apply();
    }


    private void resetStaticField() {
        RequestManager.getInstance(AccountManager.selectedAccount).setUserLogin(false);
        G.isTimeWhole = false;
        G.isFirstPassCode = false;
        G.isSaveToGallery = false;
        G.showSenderNameInGroup = false;
        G.themeColor = Theme.DEFAULT;
        G.context.getSharedPreferences(SHP_SETTING.FILE_NAME, Context.MODE_PRIVATE).edit()
                .putInt(SHP_SETTING.KEY_THEME_COLOR, Theme.DEFAULT)
                .putBoolean(SHP_SETTING.KEY_THEME_DARK, false)
                .apply();
    }
}
