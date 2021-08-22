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

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import net.iGap.Config;
import net.iGap.G;
import net.iGap.WebSocketClient;
import net.iGap.api.apiService.TokenContainer;
import net.iGap.controllers.MessageDataStorage;
import net.iGap.helper.HelperConnectionState;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.module.accountManager.DbManager;
import net.iGap.module.enums.ConnectionState;
import net.iGap.network.AbstractObject;
import net.iGap.network.IG_RPC;
import net.iGap.network.RequestManager;
import net.iGap.observers.eventbus.EventManager;
import net.iGap.proto.ProtoError;
import net.iGap.proto.ProtoUserLogin;
import net.iGap.realm.RealmCallConfig;
import net.iGap.realm.RealmClientCondition;
import net.iGap.realm.RealmContacts;
import net.iGap.realm.RealmStory;
import net.iGap.realm.RealmStoryProto;
import net.iGap.realm.RealmUserInfo;
import net.iGap.request.RequestClientGetRoomList;
import net.iGap.request.RequestSignalingGetConfiguration;
import net.iGap.request.RequestWalletGetAccessToken;
import net.iGap.story.StoryObject;
import net.iGap.structs.MessageObject;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

public class UserLoginResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;
    private boolean isDeprecated = false;
    private boolean isUpdateAvailable = false;
    private int contactCount = 0;
    public static boolean isFetched = false;

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

        TokenContainer.getInstance().updateToken(builder.getAccessToken(), false);

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
        RealmUserInfo.sendPushNotificationToServer();

        if (builder.getWalletActive() && builder.getWalletAgreementAccepted()) {
            new RequestWalletGetAccessToken().walletGetAccessToken();
        }

        DbManager.getInstance().doRealmTransaction(realm -> {
            RealmUserInfo realmUserInfo = realm.where(RealmUserInfo.class).findFirst();
            contactCount = realm.where(RealmContacts.class).findAll().size();
            if (realmUserInfo != null) {
                realmUserInfo.setWalletActive(builder.getWalletActive());
                realmUserInfo.setMplActive(builder.getMplActive());
                realmUserInfo.setWalletRegister(builder.getWalletAgreementAccepted());
                realmUserInfo.setAccessToken(builder.getAccessToken());
            }
        });

        if (!isFetched) {
            AbstractObject req = null;
            IG_RPC.Get_Stories get_stories = new IG_RPC.Get_Stories();
            get_stories.offset = 0;
            get_stories.limit = contactCount;
            req = get_stories;
            RequestManager.getInstance(AccountManager.selectedAccount).sendRequest(req, (response, error) -> {
                if (error == null) {
                    IG_RPC.Res_Get_Stories res = (IG_RPC.Res_Get_Stories) response;


                    DbManager.getInstance().doRealmTransaction(realm -> {
                        List<StoryObject> storyObjects = new ArrayList<>();
                        if (res.stories.size() > 0 && res.stories.size() >= realm.where(RealmStory.class).findAll().size()) {
                            for (int i = 0; i < res.stories.size(); i++) {
                                for (int j = 0; j < res.stories.get(i).getStoriesList().size(); j++) {
                                    storyObjects.add(StoryObject.create(res.stories.get(i).getStoriesList().get(j)));
                                }
                                RealmStory.putOrUpdate(realm, res.stories.get(i).getSeenAllGroupStories(), res.stories.get(i).getUserId(), storyObjects);
                                storyObjects = new ArrayList<>();
                            }
                        } else if (res.stories.size() != 0 && res.stories.size() < realm.where(RealmStory.class).findAll().size()) {

                            boolean isExist = false;
                            List<RealmStory> realmStories = realm.where(RealmStory.class).findAll();
                            if (realmStories != null && realmStories.size() > 0) {
                                for (int i = 0; i < realmStories.size(); i++) {
                                    for (int j = 0; j < res.stories.size(); j++) {
                                        if (realmStories.get(i).getUserId() == res.stories.get(j).getUserId()) {
                                            isExist = true;
                                        }
                                    }
                                    if (!isExist) {

                                        if (realm.where(RealmStory.class).equalTo("userId", realmStories.get(i).getUserId()).findFirst().isSentAll()) {
                                            realm.where(RealmStory.class).equalTo("userId", realmStories.get(i).getUserId()).findFirst().deleteFromRealm();
                                        }
                                    }
                                    isExist = false;
                                }
                            }
                            for (int i = 0; i < res.stories.size(); i++) {
                                for (int j = 0; j < res.stories.get(i).getStoriesList().size(); j++) {
                                    storyObjects.add(StoryObject.create(res.stories.get(i).getStoriesList().get(j)));
                                }

                                RealmStory.putOrUpdate(realm, res.stories.get(i).getSeenAllGroupStories(), res.stories.get(i).getUserId(), storyObjects);
                                storyObjects = new ArrayList<>();
                            }

                        } else if (res.stories.size() == 0) {
                            realm.where(RealmStory.class).equalTo("isSentAll", true).findAll().deleteAllFromRealm();
                        }
                    });
                    Log.e("fdajhfjshf", "getRequestManager5: ");

                    G.refreshRealmUi();
                    isFetched = true;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            G.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    EventManager.getInstance(AccountManager.selectedAccount).postEvent(EventManager.STORY_LIST_FETCHED);
                                }
                            });
                        }
                    }, 2000);


                    Log.e("fdajhfjshf", "getRequestManager: ");

                } else {
                    Log.e("fdajhfjshf", "getRequestManager2: " + "/" + error);
                }
            });

        }
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


