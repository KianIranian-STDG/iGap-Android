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

import net.iGap.controllers.MessageDataStorage;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.module.accountManager.DbManager;
import net.iGap.G;
import net.iGap.adapter.items.chat.AbstractMessage;
import net.iGap.fragments.FragmentChat;
import net.iGap.helper.HelperLogMessage;
import net.iGap.helper.LooperThreadHelper;
import net.iGap.observers.eventbus.EventManager;
import net.iGap.proto.ProtoError;
import net.iGap.proto.ProtoUserInfo;
import net.iGap.realm.RealmAvatar;
import net.iGap.realm.RealmContacts;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmStory;
import net.iGap.realm.RealmStoryProto;
import net.iGap.realm.RealmStoryViewInfo;
import net.iGap.request.RequestUserContactImport;
import net.iGap.request.RequestUserInfo;
import net.iGap.story.ViewUserDialogFragment;
import net.iGap.structs.MessageObject;

public class UserInfoResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public Object identity;

    public UserInfoResponse(int actionId, Object protoClass, Object identity) {
        super(actionId, protoClass, identity);
        this.message = protoClass;
        this.identity = identity;
        this.actionId = actionId;
    }

    @Override
    public void handler() {
        super.handler();
        final ProtoUserInfo.UserInfoResponse.Builder builder = (ProtoUserInfo.UserInfoResponse.Builder) message;

        DbManager.getInstance().doRealmTransaction(realm -> {
            if (identity != null && identity instanceof String) {
                if (identity.equals(RequestUserContactImport.KEY))
                    RealmContacts.putOrUpdate(realm, builder.getUser());
            }

            RealmRegisteredInfo.putOrUpdate(realm, builder.getUser());
            RealmAvatar.putOrUpdateAndManageDelete(realm, builder.getUser().getId(), builder.getUser().getAvatar());

            RealmStory realmStory = realm.where(RealmStory.class).equalTo("sessionId", AccountManager.getInstance().getCurrentUser().getId()).equalTo("userId", builder.getUser().getId()).findFirst();
            if (realmStory != null) {
                realmStory.setDisplayName(builder.getUser().getDisplayName());
                if (realmStory.getRealmStoryProtos() != null && realmStory.getRealmStoryProtos().size() > 0) {
                    for (int i = 0; i < realmStory.getRealmStoryProtos().size(); i++) {
                        if (realmStory.getRealmStoryProtos().get(i) != null) {
                            realmStory.getRealmStoryProtos().get(i).setDisplayName(builder.getUser().getDisplayName());
                        }
                    }
                }
            }

            G.runOnUiThread(() -> EventManager.getInstance(AccountManager.selectedAccount).postEvent(EventManager.STORY_USER_INFO, builder.getUser()));

            RealmStoryViewInfo realmStoryViewInfo = realm.where(RealmStoryViewInfo.class).equalTo("userId", builder.getUser().getId()).findFirst();
            if (realmStoryViewInfo != null) {
                realmStoryViewInfo.setDisplayName(builder.getUser().getDisplayName());
            }
        });
        LooperThreadHelper.getInstance().getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                RequestUserInfo.userIdArrayList.remove(String.valueOf(builder.getUser().getId()));
            }
        }, RequestUserInfo.CLEAR_ARRAY_TIME);


        if (identity != null) {
            if (identity.equals(RequestUserInfo.InfoType.UPDATE_ROOM.toString())) {
                RealmRoom.updateChatRoom(builder.getUser().getId());
            } else if (identity.equals(RequestUserInfo.InfoType.JUST_INFO.toString())) {
                G.onRegistrationInfo.onInfo(builder.getUser());
                return;
            }
        }
        if ((builder.getUser().getId() == AccountManager.getInstance().getCurrentUser().getId())) {
            if (G.onUserInfoMyClient != null) {
                G.onUserInfoMyClient.onUserInfoMyClient();
            }
        }

        if (G.onUserUpdateStatus != null) {
            G.onUserUpdateStatus.onUserUpdateStatus(builder.getUser().getId(), builder.getUser().getLastSeen(), builder.getUser().getStatus().toString());
        }

        G.handler.post(new Runnable() {
            @Override
            public void run() {
                if (identity instanceof String) {
                    if (G.onUserInfoResponse != null) {
                        G.onUserInfoResponse.onUserInfo(builder.getUser(), (String) identity);
                    }
                    if (identity.equals(RequestUserContactImport.KEY) && G.onContactImport != null) {
                        G.onContactImport.onContactInfo(builder.getUser());
                    }

                } else if (identity instanceof RequestUserInfo.UserInfoBody) {
                    if (((RequestUserInfo.UserInfoBody) identity).onComplete != null) {
                        ((RequestUserInfo.UserInfoBody) identity).onComplete.complete(true, "" + builder.getUser().getId(), "OK");
                    }

                    if (G.onUserInfoResponse != null) {
                        G.onUserInfoResponse.onUserInfo(builder.getUser(), ((RequestUserInfo.UserInfoBody) identity).identity);
                    }
                } else if (identity == null) {
                    if (G.onUserInfoResponse != null) {
                        G.onUserInfoResponse.onUserInfo(builder.getUser(), null);
                    }
                }

                // update chat message header forward after get user or room info
                if (AbstractMessage.updateForwardInfo != null) {
                    long _id = builder.getUser().getId();
                    if (AbstractMessage.updateForwardInfo.containsKey(_id)) {
                        String messageId = AbstractMessage.updateForwardInfo.get(_id);
                        AbstractMessage.updateForwardInfo.remove(_id);
                        if (FragmentChat.onUpdateUserOrRoomInfo != null) {
                            FragmentChat.onUpdateUserOrRoomInfo.onUpdateUserOrRoomInfo(messageId);
                        }
                    }
                }
            }
        });

        // update log message in realm room message after get user info
        LooperThreadHelper.getInstance().getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                HelperLogMessage.updateLogMessageAfterGetUserInfo(builder.getUser().getId());
            }
        }, 500);
    }

    @Override
    public void timeOut() {
        super.timeOut();
        G.onUserInfoResponse.onUserInfoTimeOut();
        if (G.onContactImport != null)
            G.onContactImport.onTimeOut();
    }

    @Override
    public void error() {
        super.error();
        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (identity != null && identity instanceof String) {
                    RequestUserInfo.userIdArrayList.remove(identity);
                } else {
                    RequestUserInfo.userIdArrayList.clear();
                }
            }
        }, RequestUserInfo.CLEAR_ARRAY_TIME);

        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();
        G.onUserInfoResponse.onUserInfoError(majorCode, minorCode);
        if (identity instanceof RequestUserInfo.UserInfoBody) {
            ((RequestUserInfo.UserInfoBody) identity).onComplete.complete(true, "", "ERROR");
            ((RequestUserInfo.UserInfoBody) identity).onComplete.complete(true, "", "");
        }
        if (G.onContactImport != null)
            G.onContactImport.onError(majorCode, minorCode);
    }
}


