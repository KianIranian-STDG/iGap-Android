package net.iGap.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;

import com.caspian.otpsdk.context.ApplicationContext;
import com.google.firebase.iid.FirebaseInstanceId;

import net.iGap.Config;
import net.iGap.G;
import net.iGap.helper.FileLog;
import net.iGap.helper.HelperTracker;
import net.iGap.module.SHP_SETTING;
import net.iGap.module.Theme;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.module.accountManager.DbManager;
import net.iGap.realm.RealmUserInfo;

import ir.metrix.Metrix;

public class SplashActivity extends ActivityEnhanced {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Config.FILE_LOG_ENABLE) {
            FileLog.i("Splash activity on create");
        }

        if (G.ISRealmOK) {
            DbManager.getInstance().doRealmTask(realm -> {
                RealmUserInfo realmUserInfo = realm.where(RealmUserInfo.class).findFirst();
                if (realmUserInfo != null) {
                    final String[] token = {realmUserInfo.getPushNotificationToken()};
                    if (token[0] == null || token[0].length() < 2) {
                        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(this, instanceIdResult -> {
                            token[0] = instanceIdResult.getToken();
                            RealmUserInfo.setPushNotification(token[0]);
                            Metrix.setPushToken(token[0]);
                        });
                    }
                }
            });

            RealmUserInfo userInfo = DbManager.getInstance().doRealmTask(realm -> {
                return realm.where(RealmUserInfo.class).findFirst();
            });

            if (userInfo == null || !userInfo.getUserRegistrationState()) { // user registered before
                if (AccountManager.getInstance().haveAccount()) {//Todo: this is code and must find cause of this bug
                    Intent intent = new Intent(this, ActivityMain.class);

                    if (getIntent().getStringExtra(ActivityMain.DEEP_LINK) != null)
                        intent.putExtra(ActivityMain.DEEP_LINK, getIntent().getStringExtra(ActivityMain.DEEP_LINK));

                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(this, ActivityRegistration.class);
                    startActivity(intent);
                    finish();
                }
            } else if (userInfo.getUserInfo() == null || userInfo.getUserInfo().getDisplayName() == null || userInfo.getUserInfo().getDisplayName().isEmpty()) {
                Intent intent = new Intent(this, ActivityRegistration.class);
                intent.putExtra(ActivityRegistration.showProfile, true);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            } else {
                Intent intent = new Intent(this, ActivityMain.class);

                if (getIntent().getStringExtra(ActivityMain.DEEP_LINK) != null)
                    intent.putExtra(ActivityMain.DEEP_LINK, getIntent().getStringExtra(ActivityMain.DEEP_LINK));

                startActivity(intent);
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (Config.FILE_LOG_ENABLE) {
            FileLog.i("Splash activity on destroy");
        }
    }
}
