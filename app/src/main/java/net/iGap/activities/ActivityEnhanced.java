/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

package net.iGap.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import net.iGap.module.accountManager.AccountManager;
import net.iGap.module.accountManager.DbManager;
import net.iGap.G;
import net.iGap.module.Theme;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperLog;
import net.iGap.helper.HelperPermission;
import net.iGap.helper.UserStatusController;
import net.iGap.helper.avatar.AvatarHandler;
import net.iGap.model.PassCode;
import net.iGap.module.AndroidUtils;
import net.iGap.module.AttachFile;
import net.iGap.module.SHP_SETTING;
import net.iGap.module.StartupActions;
import net.iGap.module.StatusBarUtil;

import java.io.File;
import java.io.IOException;

import io.realm.Realm;

import static net.iGap.G.updateResources;


public abstract class ActivityEnhanced extends AppCompatActivity {

    public AvatarHandler avatarHandler;
    public boolean isOnGetPermission = false;
    private static int ActivityCountInApp = 0;
    protected boolean canSetUserStatus = true;
    BroadcastReceiver myBroadcast = new BroadcastReceiver() {
        //When Event is published, onReceive method is called
        @Override
        public void onReceive(Context context, Intent intent) {
          /*  if (intent.getAction() != null && intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                if (G.isPassCode) ActivityMain.isLock = true;
            }*/
        }
    };

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(updateResources(newBase));
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateResources(getBaseContext());
        makeDirectoriesIfNotExist();
        G.currentActivity = this;
    }

    public void onCreate(Bundle savedInstanceState) {
        ActivityCountInApp++;
        /*Log.wtf("ActivityEnhanced","onCreate start");
        Log.wtf("ActivityEnhanced","setThemeSetting start");*/
        setThemeSetting();
        /*Log.wtf("ActivityEnhanced","setThemeSetting end");
        Log.wtf("ActivityEnhanced","super.onCreate start");*/
        super.onCreate(savedInstanceState);
        /*Log.wtf("ActivityEnhanced","super.onCreate end");*/
        if (G.ISRealmOK) {
            if (ActivityCountInApp == 1 || Realm.getLocalInstanceCount(AccountManager.getInstance().getCurrentUser().getRealmConfiguration()) == 0) {
                DbManager.getInstance().openUiRealm();
            }
            /*Log.wtf("ActivityEnhanced","AvatarHandler start");*/
            avatarHandler = new AvatarHandler();
            /*Log.wtf("ActivityEnhanced","AvatarHandler end");*/

            /*Log.wtf("ActivityEnhanced","screenStateFilter start");*/
            IntentFilter screenStateFilter = new IntentFilter();
            screenStateFilter.addAction(Intent.ACTION_SCREEN_ON);
            screenStateFilter.addAction(Intent.ACTION_SCREEN_OFF);
            registerReceiver(myBroadcast, screenStateFilter);
            /*Log.wtf("ActivityEnhanced","screenStateFilter end");*/

            /*Log.wtf("ActivityEnhanced","lock start");*/
            SharedPreferences sharedPreferences = getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);

            boolean allowScreen = sharedPreferences.getBoolean(SHP_SETTING.KEY_SCREEN_SHOT_LOCK, true);

            if (PassCode.getInstance().isPassCode() && !allowScreen) {
                try {
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
                } catch (Exception e) {
                    HelperLog.getInstance().setErrorLog(e);
                }
            } else {
                try {
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SECURE);
                } catch (Exception e) {
                    HelperLog.getInstance().setErrorLog(e);
                }
            }
            /*Log.wtf("ActivityEnhanced","lock end");*/

            /*Log.wtf("ActivityEnhanced","status bar start");*/
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                StatusBarUtil.setColor(this, Theme.getInstance().getPrimaryDarkColor(this), 50);
            }
            /*Log.wtf("ActivityEnhanced","status bar start");*/

            /*makeDirectoriesIfNotExist();*/

            /*boolean checkedEnableDataShams = sharedPreferences.getBoolean(SHP_SETTING.KEY_AUTO_ROTATE, true);
            if (!checkedEnableDataShams) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
            }*/

            int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                AndroidUtils.statusBarHeight = getResources().getDimensionPixelSize(resourceId);
                Log.i("abbasiKeyboard", "status height set ->  " + AndroidUtils.statusBarHeight);
            }
        }

        /*Log.wtf("ActivityEnhanced","onCreate end");*/

    }

    private void setThemeSetting() {
        this.setTheme(new Theme().getTheme(this));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        try {
            HelperPermission.onRequestPermissionsResult(requestCode, permissions, grantResults);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        if (G.ISRealmOK) {
            if (!G.isAppInFg) {
                G.isAppInFg = true;
                G.isChangeScrFg = false;

                /**
                 * if user isn't login and page come in foreground try for reconnect
                 */
            } else {
                G.isChangeScrFg = true;
            }
            G.isScrInFg = true;

            AttachFile.isInAttach = false;
            if (canSetUserStatus)
                UserStatusController.getInstance().setOnline();

            super.onStart();
            avatarHandler.registerChangeFromOtherAvatarHandler();
        } else {
            super.onStart();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (G.ISRealmOK) {
            avatarHandler.unregisterChangeFromOtherAvatarHandler();

            if (!G.isScrInFg || !G.isChangeScrFg) {
                G.isAppInFg = false;
            }
            G.isScrInFg = false;

            if (!AttachFile.isInAttach && canSetUserStatus) {
                UserStatusController.getInstance().setOffline();
            }
        }

    }

    private void makeDirectoriesIfNotExist() {
        StartupActions.makeFolder();

//        if (isOnGetPermission) {
//            return;
//        }
//
//        if (this instanceof ActivityRegisteration) {
//            return;
//        }
//
//        isOnGetPermission = true;
//
//        try {
//            HelperPermision.getStoragePermision(this, new OnGetPermission() {
//                @Override
//                public void Allow() throws IOException {
//                    checkIsDirectoryExist();
//                }
//
//                @Override
//                public void deny() {
//                    //don't need to finish app because we can continue use from app with private data folder
//                    //finish();
//                }
//            });
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    private void checkIsDirectoryExist() {

        isOnGetPermission = false;

        if (new File(G.DIR_APP).exists() && new File(G.DIR_IMAGES).exists() && new File(G.DIR_VIDEOS).exists() && new File(G.DIR_AUDIOS).exists() && new File(G.DIR_DOCUMENT).exists() && new File(G.DIR_CHAT_BACKGROUND).exists() && new File(G.DIR_IMAGE_USER).exists() && new File(G.DIR_TEMP).exists()) {
            return;
        } else {
            StartupActions.makeFolder();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCountInApp--;
        if (G.ISRealmOK) {
            if (ActivityCountInApp == 0) {
                DbManager.getInstance().closeUiRealm();
            }
            unregisterReceiver(myBroadcast);
        }
    }


    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        AndroidUtils.checkDisplaySize(this, newConfig);
        super.onConfigurationChanged(newConfig);
        updateResources(this);
    }

    public void onRefreshActivity(boolean changeColor, String language) {

        G.isUpdateNotificaionColorMain = changeColor;
        G.isUpdateNotificaionColorChannel = changeColor;
        G.isUpdateNotificaionColorGroup = changeColor;
        G.isUpdateNotificaionColorChat = changeColor;
        G.isUpdateNotificaionCall = changeColor;
        G.isRestartActivity = true;

        new HelperFragment(getSupportFragmentManager()).removeAll(false);

        this.recreate();

    }

}
