package com.iGap.activitys;

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

        //WebSocketClient.getInstance();
        G.realm = Realm.getInstance(G.realmConfig);
    }

}
