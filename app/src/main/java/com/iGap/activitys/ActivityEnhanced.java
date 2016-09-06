package com.iGap.activitys;

import android.support.v7.app.AppCompatActivity;

import com.iGap.G;

public class ActivityEnhanced extends AppCompatActivity {
    @Override
    protected void onResume() {
        super.onResume();
        G.currentActivity = this;
    }
}
