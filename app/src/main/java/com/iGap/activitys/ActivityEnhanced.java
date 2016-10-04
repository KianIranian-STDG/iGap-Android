package com.iGap.activitys;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

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


    @Override
    protected void onStart() {
        if (!G.isAppInFg) {
            G.isAppInFg = true;
            G.isChangeScrFg = false;
            onAppStart();
        } else {
            G.isChangeScrFg = true;
        }
        G.isScrInFg = true;

        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (!G.isScrInFg || !G.isChangeScrFg) {
            G.isAppInFg = false;
            onAppPause();
        }
        G.isScrInFg = false;
    }

    public void onAppStart() {
        //  Toast.makeText(getApplicationContext(), "App in foreground", Toast.LENGTH_LONG).show();
        // TODO: 10/1/2016 (molareza) wgen app in foreground
    }

    public void onAppPause() {
        // Toast.makeText(getApplicationContext(), "App in background", Toast.LENGTH_LONG).show();
        // TODO: 10/1/2016 (molareza) wgen app in background
    }
}
