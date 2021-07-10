/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

package net.iGap.response;

import android.os.Looper;

import net.iGap.Config;
import net.iGap.G;
import net.iGap.WebSocketClient;
import net.iGap.api.apiService.TokenContainer;
import net.iGap.helper.HelperConnectionState;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.module.accountManager.DbManager;
import net.iGap.module.enums.ConnectionState;
import net.iGap.network.RequestManager;
import net.iGap.proto.ProtoError;
import net.iGap.proto.ProtoUserLogin;
import net.iGap.realm.RealmCallConfig;
import net.iGap.realm.RealmClientCondition;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.realm.RealmUserInfo;
import net.iGap.request.RequestClientGetRoomList;
import net.iGap.request.RequestSignalingGetConfiguration;
import net.iGap.request.RequestWalletGetAccessToken;

import io.realm.Realm;

public class UserLoginResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;
    private boolean isDeprecated = false;
    private boolean isUpdateAvailable = false;

    public UserLoginResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.identity = identity;
        this.actionId = actionId;
    }


    @Override
    public void handler() {
        super.handler();
        HelperConnectionState.connectionState(ConnectionState.IGAP);
        ProtoUserLogin.UserLoginResponse.Builder builder = (ProtoUserLogin.UserLoginResponse.Builder) message;
        G.serverHashContact = builder.getContactHash();
      /*builder.getDeprecatedClient();
        builder.getSecondaryNodeName();
        builder.getUpdateAvailable();*/

        if (builder.getUpdateAvailable() && !isUpdateAvailable) {
            new android.os.Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (G.onVersionCallBack != null) {
                        G.onVersionCallBack.isUpdateAvailable();
                        isUpdateAvailable = true;
                    }
                }
            }, 1000);


        }

        if (builder.getDeprecatedClient() && !isDeprecated) {
            new android.os.Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (G.onVersionCallBack != null) {
                        G.onVersionCallBack.isDeprecated();
                        isDeprecated = true;
                        G.isDepricatedApp = true;
                    }
                }
            }, 1000);

        } else {
            G.isDepricatedApp = false;
        }


        G.isNeedToCheckProfileWallpaper = true;
        G.currentServerTime = builder.getResponse().getTimestamp();
        G.bothChatDeleteTime = builder.getChatDeleteMessageForBothPeriod() * 1000;
        RequestManager.getInstance(AccountManager.selectedAccount).setUserLogin(true);

        TokenContainer.getInstance().updateToken(builder.getAccessToken(),false);

        /**
         * get Signaling Configuration
         * (( hint : call following request after set G.userLogin=true ))
         */
        DbManager.getInstance().doRealmTask(realm -> {
            if (realm.where(RealmCallConfig.class).findFirst() == null) {
                new RequestSignalingGetConfiguration().signalingGetConfiguration();
            }
        });

        new Thread(() -> {
            G.clientConditionGlobal = RealmClientCondition.computeClientCondition(null);
            new RequestClientGetRoomList().clientGetRoomList(0, Config.LIMIT_LOAD_ROOM, "0");

        }).start();

        G.onUserLogin.onLogin();
        String FCMToken = DbManager.getInstance().doRealmTask(realm -> {
            return realm.where(RealmUserInfo.class)
                    .equalTo("userInfo.id", AccountManager.getInstance().getCurrentUser().getId())
                    .findFirst().getPushNotificationToken();
        }) ;

        if(!FCMToken.isEmpty()) {
            RealmUserInfo.sendPushNotificationToServer();
        }

        if (builder.getWalletActive() && builder.getWalletAgreementAccepted()) {
            new RequestWalletGetAccessToken().walletGetAccessToken();
        }

        DbManager.getInstance().doRealmTransaction(realm -> {
            RealmUserInfo realmUserInfo = realm.where(RealmUserInfo.class).findFirst();
            if (realmUserInfo != null) {
                realmUserInfo.setWalletActive(builder.getWalletActive());
                realmUserInfo.setMplActive(builder.getMplActive());
                realmUserInfo.setWalletRegister(builder.getWalletAgreementAccepted());
                realmUserInfo.setAccessToken(builder.getAccessToken());
            }
        });
    }

    @Override
    public void timeOut() {
        super.timeOut();

        if (RequestManager.getInstance(AccountManager.selectedAccount).isSecure()) {
            retryLogin();
        } else {
            WebSocketClient.getInstance().disconnectSocket(true);
        }
    }

    @Override
    public void error() {
        super.error();
        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();
        if (majorCode == 110 || (majorCode == 10 && minorCode == 100)) {
            retryLogin();
//            G.handler.postDelayed(this::retryLogin, 1000);
        }
        G.onUserLogin.onLoginError(majorCode, minorCode);
    }

    private void retryLogin() {
        if (WebSocketClient.getInstance().isConnect()) {
            WebSocketClient.getInstance().disconnectSocket(true);
        }
//        DbManager.getInstance().doRealmTask(realm -> {
//            RealmUserInfo userInfo = realm.where(RealmUserInfo.class).findFirst();
//            if (!G.userLogin && userInfo != null && userInfo.getUserRegistrationState()) {
//                new RequestUserLogin().userLogin(userInfo.getToken());
//            }
//        });
    }


}


