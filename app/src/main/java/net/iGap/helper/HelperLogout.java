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

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import net.iGap.DbManager;
import net.iGap.G;
import net.iGap.Theme;
import net.iGap.activities.ActivityRegistration;
import net.iGap.fragments.FragmentMain;
import net.iGap.module.AppUtils;
import net.iGap.module.LoginActions;
import net.iGap.module.MusicPlayer;
import net.iGap.module.SHP_SETTING;

import io.realm.Realm;

import static net.iGap.request.RequestClientGetRoomList.pendingRequest;
import static org.paygear.utils.Utils.signOutWallet;


/**
 * truncate realm and go to ActivityIntroduce for register again
 */
public final class HelperLogout {

    /**
     * truncate realm and go to ActivityIntroduce for register again
     */
    public static void logout() {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                pendingRequest.remove(0);
                FragmentMain.mOffset = 0;
                signOutWallet();
                DbManager.getInstance().doRealmTask(realm -> {
                    realm.executeTransactionAsync(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            realm.deleteAll();
                        }
                    }, () -> {
                        clearPreferences();
                        resetStaticField();

                        AppUtils.cleanBadge();
                        new LoginActions();
                        Intent intent = new Intent(G.context, ActivityRegistration.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        G.context.startActivity(intent);
                        if (G.currentActivity != null) {
                            G.currentActivity.finish();
                        }

                        try {
                            NotificationManager nMgr = (NotificationManager) G.context.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                            nMgr.cancelAll();
                        } catch (Exception e) {
                            e.getStackTrace();
                        }

                        if (MusicPlayer.mp != null && MusicPlayer.mp.isPlaying()) {
                            MusicPlayer.stopSound();
                            MusicPlayer.closeLayoutMediaPlayer();
                        }
                    });
                });
            }
        });
    }

    private static void clearPreferences() {
        SharedPreferences sharedPreferencesFile = G.context.getSharedPreferences(SHP_SETTING.FILE_NAME, Context.MODE_PRIVATE);
        sharedPreferencesFile.edit().clear().apply();

        SharedPreferences sharedPreferencesTrackerFile = G.context.getSharedPreferences(SHP_SETTING.KEY_TRACKER_FILE, Context.MODE_PRIVATE);
        sharedPreferencesTrackerFile.edit().clear().apply();
    }


    private static void resetStaticField() {
        Theme.setThemeColor();
        G.userLogin = false;
        G.isTimeWhole = false;
        G.isFirstPassCode = false;
        G.isPassCode = false;
        G.isDarkTheme = false;
        G.isSaveToGallery = false;
        G.showSenderNameInGroup = false;
    }
}
