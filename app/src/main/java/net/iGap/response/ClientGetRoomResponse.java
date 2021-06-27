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

import net.iGap.G;
import net.iGap.adapter.items.chat.AbstractMessage;
import net.iGap.fragments.FragmentChat;
import net.iGap.helper.HelperGetUserInfo;
import net.iGap.helper.HelperLogMessage;
import net.iGap.module.accountManager.DbManager;
import net.iGap.observers.interfaces.OnGetUserInfo;
import net.iGap.proto.ProtoClientGetRoom;
import net.iGap.proto.ProtoError;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmRoom;
import net.iGap.request.RequestClientGetRoom;
import net.iGap.request.RequestClientJoinByUsername;

import io.realm.Realm;

import static net.iGap.realm.RealmRoom.putOrUpdate;

public class ClientGetRoomResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public Object identity;

    public ClientGetRoomResponse(int actionId, Object protoClass, Object identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();

        final ProtoClientGetRoom.ClientGetRoomResponse.Builder clientGetRoom = (ProtoClientGetRoom.ClientGetRoomResponse.Builder) message;
        DbManager.getInstance().doRealmTransaction(realm -> {
            final RequestClientGetRoom.IdentityClientGetRoom identityClientGetRoom = ((RequestClientGetRoom.IdentityClientGetRoom) identity);
            final RequestClientGetRoom.CreateRoomMode roomMode = identityClientGetRoom.createRoomMode;

            if (roomMode != null && roomMode == RequestClientGetRoom.CreateRoomMode.justInfo) {
                if (!RealmRoom.isMainRoom(clientGetRoom.getRoom().getId())) {
                    RealmRoom realmRoom = RealmRoom.putOrUpdate(clientGetRoom.getRoom(), realm);
                    realmRoom.setDeleted(true);
                    realmRoom.setKeepRoom(true);
                }

                /**
                 * update log message in realm room message after get room info
                 */
                if (HelperLogMessage.logMessageUpdateList.containsKey(clientGetRoom.getRoom().getId())) {
                    G.handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            HelperLogMessage.updateLogMessageAfterGetUserInfo(clientGetRoom.getRoom().getId());
                        }
                    }, 500);
                }

                return;
            }

            if (roomMode != null && roomMode == RequestClientGetRoom.CreateRoomMode.getPromote) {
                if (!RealmRoom.isMainRoom(clientGetRoom.getRoom().getId())) {
                    RealmRoom realmRoom = RealmRoom.putOrUpdate(clientGetRoom.getRoom(), realm);
                    realmRoom.setFromPromote(true);
                    realmRoom.setPromoteId(clientGetRoom.getRoom().getId());
                    realmRoom.setDeleted(true);
                    realmRoom.setKeepRoom(false);
                    if (clientGetRoom.getRoom().hasChannelRoomExtra()) {
                        new RequestClientJoinByUsername().clientJoinByUsername(clientGetRoom.getRoom().getChannelRoomExtra().getPublicExtra().getUsername(), clientGetRoom.getRoom().getId());
                    } else {
                        new RequestClientJoinByUsername().clientJoinByUsername(clientGetRoom.getRoom().getGroupRoomExtra().getPublicExtra().getUsername(), clientGetRoom.getRoom().getId());
                    }

                    //
                }

                return;
            }

            if (clientGetRoom.getRoom().getType() == ProtoGlobal.Room.Type.CHAT) {

                new HelperGetUserInfo(new OnGetUserInfo() {
                    @Override
                    public void onGetUserInfo(ProtoGlobal.RegisteredUser registeredUser) {

                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                DbManager.getInstance().doRealmTask(realm1 -> {
                                    realm1.executeTransactionAsync(new Realm.Transaction() {
                                        @Override
                                        public void execute(Realm realm1) {
                                            putOrUpdate(clientGetRoom.getRoom(), realm1);
                                        }
                                    }, new Realm.Transaction.OnSuccess() {
                                        @Override
                                        public void onSuccess() {
                                            if (G.onClientGetRoomResponse != null) {
                                                G.onClientGetRoomResponse.onClientGetRoomResponse(clientGetRoom.getRoom(), clientGetRoom, identityClientGetRoom);
                                            }
                                        }
                                    });
                                });
                            }
                        });
                    }
                }).getUserInfo(clientGetRoom.getRoom().getChatRoomExtra().getPeer().getId());
            } else {
                putOrUpdate(clientGetRoom.getRoom(), realm);

                G.handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (G.onClientGetRoomResponse != null) {
                            G.onClientGetRoomResponse.onClientGetRoomResponse(clientGetRoom.getRoom(), clientGetRoom, identityClientGetRoom);
                        }

                    }
                }, 500);
            }
        });

        // update chat message header forward after get user or room info
        if (AbstractMessage.updateForwardInfo != null) {
            if (AbstractMessage.updateForwardInfo.containsKey(clientGetRoom.getRoom().getId())) {
                String messageId = AbstractMessage.updateForwardInfo.get(clientGetRoom.getRoom().getId());
                AbstractMessage.updateForwardInfo.remove(clientGetRoom.getRoom().getId());
                if (FragmentChat.onUpdateUserOrRoomInfo != null) {
                    FragmentChat.onUpdateUserOrRoomInfo.onUpdateUserOrRoomInfo(messageId);
                }
            }
        }
    }

    @Override
    public void timeOut() {
        super.timeOut();
        if (G.onClientGetRoomResponse != null) {
            G.onClientGetRoomResponse.onTimeOut();
        }
    }

    @Override
    public void error() {
        super.error();
        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();
        if (majorCode == 614 && minorCode == 1) {
            final RequestClientGetRoom.IdentityClientGetRoom identityClientGetRoom = ((RequestClientGetRoom.IdentityClientGetRoom) identity);
            RealmRoom.createEmptyRoom(identityClientGetRoom.roomId);
        }
        if (G.onClientGetRoomResponse != null) {
            G.onClientGetRoomResponse.onError(majorCode, minorCode);
        }
    }
}


