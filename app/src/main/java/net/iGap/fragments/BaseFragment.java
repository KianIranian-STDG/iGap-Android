/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package net.iGap.fragments;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import java.io.File;
import java.io.IOException;
import java.util.Locale;
import net.iGap.Config;
import net.iGap.G;
import net.iGap.WebSocketClient;
import net.iGap.activities.ActivityMain;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperPermision;
import net.iGap.interfaces.OnGetPermission;
import net.iGap.module.AttachFile;
import net.iGap.module.StartupActions;
import net.iGap.proto.ProtoUserUpdateStatus;
import net.iGap.request.RequestUserUpdateStatus;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static net.iGap.G.context;
import static net.iGap.G.fragmentActivity;

public class BaseFragment extends Fragment {

    private boolean isOnGetPermission = false;
    //private FragmentActivity fragmentActivity;
    protected Fragment currentFragment;

    @Override
    public void onAttach(Context context) {
        //super.onAttach(context);
        super.onAttach(CalligraphyContextWrapper.wrap(context));
        G.fragmentActivity = (FragmentActivity) context;
        currentFragment = this;
        hideKeyboard();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        checkLanguage();
        checkFont();
        super.onCreate(savedInstanceState);
        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        //    StatusBarUtil.setColor(this, Color.parseColor(G.appBarColor), 50);
        //}
        makeDirectoriesIfNotExist();
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    public void onStart() {
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

    @Override
    public void onResume() {
        super.onResume();
        makeDirectoriesIfNotExist();
    }

    @Override
    public void onStop() {
        if (!G.isScrInFg || !G.isChangeScrFg) {
            G.isAppInFg = false;
        }
        G.isScrInFg = false;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!G.isAppInFg && !AttachFile.isInAttach && G.userLogin) {
                    new RequestUserUpdateStatus().userUpdateStatus(ProtoUserUpdateStatus.UserUpdateStatus.Status.OFFLINE);
                }
            }
        }, Config.UPDATE_STATUS_TIME);
        super.onStop();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    /**
     * check the selected language user and set the language if change it
     */
    private void checkFont() {

        if (G.typeface_IRANSansMobile == null) {
            G.typeface_IRANSansMobile = Typeface.createFromAsset(G.context.getAssets(), "fonts/IRANSansMobile.ttf");
        }

        if (G.typeface_IRANSansMobile_Bold == null) {
            G.typeface_IRANSansMobile_Bold = Typeface.createFromAsset(G.context.getAssets(), "fonts/IRANSansMobile_Bold.ttf");
        }

        if (G.typeface_Fontico == null) {
            G.typeface_Fontico = Typeface.createFromAsset(G.context.getAssets(), "fonts/iGap-Fontico.ttf");
        }

        if (G.typeface_neuropolitical == null) {
            G.typeface_neuropolitical = Typeface.createFromAsset(G.context.getAssets(), "fonts/neuropolitical.ttf");
        }
    }

    public void checkLanguage() {

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

        if (isOnGetPermission) {
            return;
        }

        //if (this instanceof ActivityIntroduce) {
        //    return;
        //}

        isOnGetPermission = true;

        try {
            HelperPermision.getStoragePermision(fragmentActivity, new OnGetPermission() {
                @Override
                public void Allow() throws IOException {
                    checkIsDirectoryExist();
                }

                @Override
                public void deny() {
                    removeFromBaseFragment();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void checkIsDirectoryExist() {

        isOnGetPermission = false;

        if (new File(G.DIR_APP).exists() && new File(G.DIR_IMAGES).exists() && new File(G.DIR_VIDEOS).exists() && new File(G.DIR_AUDIOS).exists() && new File(G.DIR_DOCUMENT).exists() && new File(G.DIR_CHAT_BACKGROUND).exists() && new File(G.DIR_IMAGE_USER).exists() && new File(G.DIR_TEMP).exists()) {
            return;
        } else {
            StartupActions.makeFolder();
        }
    }

    private void hideKeyboard() {
        View view = G.fragmentActivity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) G.context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void popBackStackFragment() {
        fragmentActivity.getSupportFragmentManager().popBackStack();
        ActivityMain.desighnLayout(ActivityMain.chatLayoutMode.none);
    }

    public void removeFromBaseFragment() {
        new HelperFragment(currentFragment).remove();
    }

    public void removeFromBaseFragment(Fragment fragment) {
        new HelperFragment(fragment).remove();
    }
}
