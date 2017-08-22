/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package net.iGap.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import java.io.File;
import java.io.IOException;
import java.util.Locale;
import net.iGap.Config;
import net.iGap.G;
import net.iGap.WebSocketClient;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperPermision;
import net.iGap.interfaces.OnGetPermission;
import net.iGap.module.AttachFile;
import net.iGap.module.SHP_SETTING;
import net.iGap.module.StartupActions;
import net.iGap.module.StatusBarUtil;
import net.iGap.proto.ProtoUserUpdateStatus;
import net.iGap.request.RequestUserUpdateStatus;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ActivityEnhanced extends AppCompatActivity {

    private boolean isOnGetPermistion = false;

    @Override protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override protected void onResume() {
        super.onResume();

        makeDirectoriesIfNotExist();

        G.currentActivity = this;
    }

    public void onCreate(Bundle savedInstanceState) {
        checkLanguage(this);
        checkFont();
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            StatusBarUtil.setColor(this, Color.parseColor(G.appBarColor), 50);
        }

        makeDirectoriesIfNotExist();

        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        SharedPreferences sharedPreferences = getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
        boolean checkedEnableDataShams = sharedPreferences.getBoolean(SHP_SETTING.KEY_AUTO_ROTATE, true);
        if (!checkedEnableDataShams) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        }

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        try {
            HelperPermision.onRequestPermissionsResult(requestCode, permissions, grantResults);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override protected void onStart() {

        if (!G.isAppInFg) {
            G.isAppInFg = true;
            G.isChangeScrFg = false;

            /**
             * if user isn't login and page come in foreground try for reconnect
             */
            if (!G.userLogin) {
                WebSocketClient.reconnect(true);
            }
        } else {
            G.isChangeScrFg = true;
        }
        G.isScrInFg = true;

        AttachFile.isInAttach = false;

        if (!G.isUserStatusOnline && G.userLogin) {
            new RequestUserUpdateStatus().userUpdateStatus(ProtoUserUpdateStatus.UserUpdateStatus.Status.ONLINE);
        }

        super.onStart();
    }

    @Override protected void onStop() {
        super.onStop();

        if (!G.isScrInFg || !G.isChangeScrFg) {
            G.isAppInFg = false;
        }
        G.isScrInFg = false;

        new Handler().postDelayed(new Runnable() {
            @Override public void run() {
                if (!G.isAppInFg && !AttachFile.isInAttach && G.userLogin) {
                    new RequestUserUpdateStatus().userUpdateStatus(ProtoUserUpdateStatus.UserUpdateStatus.Status.OFFLINE);
                }
            }
        }, Config.UPDATE_STATUS_TIME);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        if (G.twoPaneMode) {

            boolean beforeState = G.isLandscape;

            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                G.isLandscape = true;
            } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                G.isLandscape = false;
            }

            if (beforeState != G.isLandscape) {
                HelperFragment.changeFragmentResourceContainer(getSupportFragmentManager());
            }
        }

        super.onConfigurationChanged(newConfig);

    }

    /**
     * check the selected language user and set the language if change it
     */

    private void checkFont() {

        if (G.typeface_IRANSansMobile == null) {
            G.typeface_IRANSansMobile = Typeface.createFromAsset(getAssets(), "fonts/IRANSansMobile.ttf");
        }

        if (G.typeface_IRANSansMobile_Bold == null) {
            G.typeface_IRANSansMobile_Bold = Typeface.createFromAsset(getAssets(), "fonts/IRANSansMobile_Bold.ttf");
        }

        if (G.typeface_Fontico == null) {
            G.typeface_Fontico = Typeface.createFromAsset(getAssets(), "fonts/iGap-Fontico.ttf");
        }

        if (G.typeface_neuropolitical == null) {
            G.typeface_neuropolitical = Typeface.createFromAsset(getAssets(), "fonts/neuropolitical.ttf");
        }
    }


    public static void checkLanguage(Context context) {

        try {
            String selectedLanguage = G.selectedLanguage;
            if (selectedLanguage == null) return;

            String currentLanguage = Locale.getDefault().getLanguage();
            if (!selectedLanguage.equals(currentLanguage)) {
                Locale locale = new Locale(selectedLanguage);
                Locale.setDefault(locale);
                Configuration config = new Configuration();
                config.locale = locale;
                context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void makeDirectoriesIfNotExist() {

        if (isOnGetPermistion) {
            return;
        }

        if (this instanceof ActivityIntroduce) {
            return;
        }

        isOnGetPermistion = true;

        try {
            HelperPermision.getStoragePermision(this, new OnGetPermission() {
                @Override
                public void Allow() throws IOException {
                    checkIsDirectoryExist();
                }

                @Override
                public void deny() {
                    finish();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void checkIsDirectoryExist() {

        isOnGetPermistion = false;

        if (new File(G.DIR_APP).exists() && new File(G.DIR_IMAGES).exists() && new File(G.DIR_VIDEOS).exists() && new File(G.DIR_AUDIOS).exists() && new File(G.DIR_DOCUMENT).exists() && new File(G.DIR_CHAT_BACKGROUND).exists() && new File(G.DIR_IMAGE_USER).exists() && new File(G.DIR_TEMP).exists()) {
            return;
        } else {
            StartupActions.makeFolder();
        }
    }



}
