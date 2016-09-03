package com.iGap.activitys;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.iGap.G;

import io.realm.Realm;


public class ActivityEnhanced extends AppCompatActivity {


    @Override
    protected void onResume() {
        super.onResume();
        G.currentActivity = this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        G.realm = Realm.getInstance(G.realmConfig);
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onDestroy() {
        try {
            G.realm.close();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }
}
