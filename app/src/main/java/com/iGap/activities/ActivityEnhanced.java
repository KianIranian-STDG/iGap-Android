package com.iGap.activities;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import com.iGap.Config;
import com.iGap.G;
import com.iGap.helper.HelperPermision;
import com.iGap.module.AttachFile;
import com.iGap.proto.ProtoUserUpdateStatus;
import com.iGap.request.RequestUserUpdateStatus;
import java.io.IOException;
import java.util.Locale;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ActivityEnhanced extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onResume() {
        super.onResume();
        G.currentActivity = this;
    }

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        checkLanguage(this);



    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        try {
            HelperPermision.onRequestPermissionsResult(requestCode, permissions, grantResults);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {

        if (!G.isAppInFg) {
            G.isAppInFg = true;
            G.isChangeScrFg = false;
        } else {
            G.isChangeScrFg = true;
        }
        G.isScrInFg = true;

        AttachFile.isInAttach = false;

        if (!G.isUserStatusOnline) {
            new RequestUserUpdateStatus().userUpdateStatus(ProtoUserUpdateStatus.UserUpdateStatus.Status.ONLINE);
        }

        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (!G.isScrInFg || !G.isChangeScrFg) {
            G.isAppInFg = false;
        }
        G.isScrInFg = false;


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!G.isAppInFg && !AttachFile.isInAttach) {
                    new RequestUserUpdateStatus().userUpdateStatus(ProtoUserUpdateStatus.UserUpdateStatus.Status.OFFLINE);
                }
            }
        }, Config.UPDATE_STATUS_TIME);


    }

    /**
     * check the selected language user and set the language if change it
     */

    public static void checkLanguage(Context context) {

        try {
            String selectedLanguage = G.selectedLanguage;

            if (selectedLanguage == null) return;

            String currentLalnguage = Locale.getDefault().getLanguage();

            if (!selectedLanguage.equals(currentLalnguage)) {

                Locale locale = new Locale(selectedLanguage);
                Locale.setDefault(locale);
                Configuration config = new Configuration();
                config.locale = locale;
                context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
            }
        } catch (Exception e) {
        }

    }
}
