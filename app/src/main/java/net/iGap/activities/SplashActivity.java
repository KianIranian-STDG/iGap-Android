package net.iGap.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.iid.FirebaseInstanceId;

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
                Intent intent = new Intent(this, ActivityRegistration.class);
                startActivity(intent);
                finish();
                Log.wtf(this.getClass().getName(), "user registered before");
            } else if (userInfo.getUserInfo() == null || userInfo.getUserInfo().getDisplayName() == null || userInfo.getUserInfo().getDisplayName().isEmpty()) {
                Intent intent = new Intent(this, ActivityRegistration.class);
                intent.putExtra(ActivityRegistration.showProfile, true);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                Log.wtf(this.getClass().getName(), "getDisplayName is empty");
            } else {
                if (checkValidationForRealm(userInfo)) {
                    ActivityMain.userPhoneNumber = userInfo.getUserInfo().getPhoneNumber();
                }
                startActivity(new Intent(this, ActivityMain.class));
                finish();
                Log.wtf(this.getClass().getName(), "go to main activity");
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DbManager.getInstance().closeUiRealm();
    }

    private boolean checkValidationForRealm(RealmUserInfo realmUserInfo) {
        return realmUserInfo != null && realmUserInfo.isManaged() && realmUserInfo.isValid() && realmUserInfo.isLoaded();
    }
}
