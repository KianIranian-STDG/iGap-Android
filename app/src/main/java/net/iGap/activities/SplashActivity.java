package net.iGap.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import net.iGap.Config;
import net.iGap.G;
import net.iGap.firebase1.NotificationCenter;
import net.iGap.R;
import net.iGap.helper.FileLog;
import net.iGap.messenger.theme.Theme;
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
                    final String[] token = {realmUserInfo.getModuleToken()};
                    if (token[0] == null || token[0].length() < 2) {
                        NotificationCenter.getInstance().setOnTokenReceived(token1 -> {
                            token[0] = token1;
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

    @Nullable
    @Override
    public View onCreateView(@NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        return super.onCreateView(name, context, attrs);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (Config.FILE_LOG_ENABLE) {
            FileLog.i("Splash activity on destroy");
        }
    }
}
