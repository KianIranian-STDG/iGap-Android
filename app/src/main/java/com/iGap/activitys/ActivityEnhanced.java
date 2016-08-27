package com.iGap.activitys;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.iGap.G;

import io.realm.Realm;

/**
 * Created by android on 8/1/2016.
 */


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
        G.realm.close();
        super.onDestroy();
    }
}
