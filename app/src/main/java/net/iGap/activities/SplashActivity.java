package net.iGap.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.iid.FirebaseInstanceId;

import net.iGap.AccountManager;
import net.iGap.DbManager;
import net.iGap.G;
import net.iGap.realm.RealmUserInfo;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (G.ISRealmOK) {
            DbManager.getInstance().openUiRealm();
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
                if (AccountManager.getInstance().haveAccount()) {//Todo: this is fucking code and must find cause of this bug
                    startActivity(new Intent(this, ActivityMain.class));
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
                startActivity(new Intent(this, ActivityMain.class));
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DbManager.getInstance().closeUiRealm();
    }
}
