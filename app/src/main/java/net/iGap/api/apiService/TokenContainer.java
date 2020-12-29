package net.iGap.api.apiService;

import android.util.Log;

import net.iGap.BuildConfig;
import net.iGap.module.accountManager.DbManager;
import net.iGap.observers.interfaces.OnRefreshToken;
import net.iGap.realm.RealmUserInfo;
import net.iGap.request.RequestUserRefreshToken;

import java.io.IOException;

public class TokenContainer {
    private static TokenContainer instance;
    private String token;

    public static TokenContainer getInstance() {
        if (instance == null) {
            instance = new TokenContainer();
        }
        return instance;
    }

    public String getToken() {
        String result = "Bearer ";

        String dbToken = DbManager.getInstance().doRealmTask(realm -> {
            RealmUserInfo realmUserInfo = realm.where(RealmUserInfo.class).findFirst();

            if (realmUserInfo != null) {
                return realmUserInfo.getAccessToken();
            }

            return null;
        });

        if (dbToken != null) {
            token = dbToken;
        }

        result += token;

        if (BuildConfig.DEBUG)
            Log.i(getClass().getSimpleName(), "ApiToken: " + token);

        return result;
    }

    public void updateToken(String token, boolean isRefreshed) {
        if (token == null)
            return;
        if (!isRefreshed) {
            this.token = token;
        } else {
            DbManager.getInstance().doRealmTransaction(realm -> {
                RealmUserInfo realmUserInfo = realm.where(RealmUserInfo.class).findFirst();
                if (realmUserInfo != null) {
                    realmUserInfo.setAccessToken(token);
                }
            });
        }
    }

    void getRefreshToken(Delegate delegate) {
        new RequestUserRefreshToken().RefreshUserToken(new OnRefreshToken() {
            @Override
            public void onRefreshToken(String token) {
                updateToken(token, true);
                try {
                    delegate.onRefreshToken();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(int majorCode, int minorCode) {
                try {
                    delegate.onRefreshToken();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @FunctionalInterface
    public interface Delegate {
        void onRefreshToken() throws IOException;
    }

    public void clearInstance() {
        token = null;
    }
}
