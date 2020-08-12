package net.iGap.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.google.firebase.iid.FirebaseInstanceId;

import net.iGap.Config;
import net.iGap.G;
import net.iGap.helper.FileLog;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.module.accountManager.DbManager;
import net.iGap.realm.RealmUserInfo;

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
                    String token = realmUserInfo.getPushNotificationToken();
                    if (token == null || token.length() < 2) {
                        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(this, instanceIdResult -> {
                            String mToken = instanceIdResult.getToken();
                            RealmUserInfo.setPushNotification(mToken);
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
