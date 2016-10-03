package com.iGap.activitys;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.iGap.G;

public class ActivityEnhanced extends AppCompatActivity {
    @Override
    protected void onResume() {
        super.onResume();
        G.currentActivity = this;
    }

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    public static boolean isAppInFg = false;
    public static boolean isScrInFg = false;
    public static boolean isChangeScrFg = false;

    @Override
    protected void onStart() {
        if (!isAppInFg) {
            isAppInFg = true;
            isChangeScrFg = false;
            onAppStart();
        } else {
            isChangeScrFg = true;
        }
        isScrInFg = true;

        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (!isScrInFg || !isChangeScrFg) {
            isAppInFg = false;
            onAppPause();
        }
        isScrInFg = false;
    }

    public void onAppStart() {
        Toast.makeText(getApplicationContext(), "App in foreground", Toast.LENGTH_LONG).show();
        // TODO: 10/1/2016 (molareza) wgen app in foreground
    }

    public void onAppPause() {
        Toast.makeText(getApplicationContext(), "App in background", Toast.LENGTH_LONG).show();
        // TODO: 10/1/2016 (molareza) wgen app in background
    }
}
