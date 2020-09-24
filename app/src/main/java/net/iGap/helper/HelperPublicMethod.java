/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

package net.iGap.helper;

import android.content.Context;
import android.content.Intent;

import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityMain;
import net.iGap.module.ChatSendMessageUtil;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.module.accountManager.DbManager;
import net.iGap.network.RequestManager;
import net.iGap.observers.interfaces.OnChatGetRoom;
import net.iGap.observers.interfaces.OnUserInfoResponse;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomMessage;
import net.iGap.request.RequestChatGetRoom;
import net.iGap.request.RequestUserInfo;

import io.realm.Realm;


public class HelperPublicMethod {

    public static void goToChatRoom(final long peerId, final OnComplete onComplete, final OnError onError) {
        DbManager.getInstance().doRealmTask(realm -> {
            final RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo("chatRoom.peer_id", peerId).findFirst();

            if (realmRoom != null) {

                if (onComplete != null) {
                    onComplete.complete();
                }

                goToRoom(realmRoom.getId(), -1);
            } else {
                G.onChatGetRoom = new OnChatGetRoom() {
                    @Override
                    public void onChatGetRoom(final ProtoGlobal.Room room) {

                        if (onError != null) {
                            onError.error();
                        }
                        DbManager.getInstance().doRealmTransaction(realm1 -> {
                            RealmRoom room1 = RealmRoom.putOrUpdate(room, realm1);
                            room1.setDeleted(true);
                        });
                        getUserInfo(peerId, room.getId(), onComplete, onError);

                        G.onChatGetRoom = null;
                    }

                    @Override
                    public void onChatGetRoomTimeOut() {

                        if (onError != null) {
                            onError.error();
                        }
                    }

                    @Override
                    public void onChatGetRoomError(int majorCode, int minorCode) {

                        if (onError != null) {
                            onError.error();
                        }
                    }
                };

                if (RequestManager.getInstance(AccountManager.selectedAccount).isUserLogin()) {
                    new RequestChatGetRoom().chatGetRoom(peerId);
                } else {
                    HelperError.showSnackMessage(G.context.getString(R.string.there_is_no_connection_to_server), false);
                    if (onError != null) {
                        onError.error();
                    }
                }
            }
        });
    }


    public static void goToChatRoomWithMessage(final Context context, final long peerId, String message, final OnComplete onComplete, final OnError onError) {
        DbManager.getInstance().doRealmTask(realm -> {
            final RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo("chatRoom.peer_id", peerId).findFirst();

            if (realmRoom != null) {

                if (onComplete != null) {
                    onComplete.complete();
                }

                goToRoomWithTextMessage(context, realmRoom.getId(), message, realmRoom.getType(), -1);

            } else {
                G.onChatGetRoom = new OnChatGetRoom() {
                    @Override
                    public void onChatGetRoom(final ProtoGlobal.Room room) {

                        if (onError != null) {
                            onError.error();
                        }
                        DbManager.getInstance().doRealmTransaction(realm1 -> {
                            RealmRoom room1 = RealmRoom.putOrUpdate(room, realm1);
                            room1.setDeleted(true);
                            goToRoomWithTextMessage(context, room1.getId(), message, room1.getType(), -1);
                        });
                        getUserInfo(peerId, room.getId(), onComplete, onError);

                        G.onChatGetRoom = null;
                    }

                    @Override
                    public void onChatGetRoomTimeOut() {

                        if (onError != null) {
                            onError.error();
                        }
                    }

                    @Override
                    public void onChatGetRoomError(int majorCode, int minorCode) {

                        if (onError != null) {
                            onError.error();
                        }
                    }
                };

                if (RequestManager.getInstance(AccountManager.selectedAccount).isUserLogin()) {
                    new RequestChatGetRoom().chatGetRoom(peerId);
                } else {
                    HelperError.showSnackMessage(G.context.getString(R.string.there_is_no_connection_to_server), false);
                    if (onError != null) {
                        onError.error();
                    }
                }
            }
        });
    }

    public static void goToChatRoomFromFirstContact(final long peerId, final OnComplete onComplete, final OnError onError) {
        DbManager.getInstance().doRealmTask(realm -> {
            final RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo("chatRoom.peer_id", peerId).findFirst();

            if (realmRoom != null) {

                if (onComplete != null) {
                    onComplete.complete();
                }

                goToRoom(realmRoom.getId(), -1);
            } else {
                G.onChatGetRoom = new OnChatGetRoom() {
                    @Override
                    public void onChatGetRoom(final ProtoGlobal.Room room) {
                        DbManager.getInstance().doRealmTransaction(realm1 -> {
                            RealmRoom room1 = RealmRoom.putOrUpdate(room, realm1);
                            room1.setDeleted(true);
                        });
                        getUserInfo(peerId, room.getId(), onComplete, onError);

                        G.onChatGetRoom = null;
                    }

                    @Override
                    public void onChatGetRoomTimeOut() {
                        if (onError != null) {
                            onError.error();
                        }
                    }

                    @Override
                    public void onChatGetRoomError(int majorCode, int minorCode) {
                        if (onError != null) {
                            onError.error();
                        }
                    }
                };

                if (RequestManager.getInstance(AccountManager.selectedAccount).isUserLogin()) {
                    new RequestChatGetRoom().chatGetRoom(peerId);
                } else {
                    HelperError.showSnackMessage(G.context.getString(R.string.there_is_no_connection_to_server), false);
                    if (onError != null) {
                        onError.error();
                    }
                }
            }
        });
    }

    private static void getUserInfo(final long peerId, final long roomId, final OnComplete onComplete, final OnError onError) {
        G.onUserInfoResponse = new OnUserInfoResponse() {
            @Override
            public void onUserInfo(final ProtoGlobal.RegisteredUser user, String identity) {
                if (user.getId() == peerId) {
                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            DbManager.getInstance().doRealmTask(realm -> {
                                realm.executeTransactionAsync(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        RealmRegisteredInfo.putOrUpdate(realm, user);
                                    }
                                }, () -> {
                                    try {

                                        if (onComplete != null) {
                                            onComplete.complete();
                                        }

                                        goToRoom(roomId, peerId);

                                        G.onUserInfoResponse = null;
                                    } catch (IllegalStateException e) {
                                        e.printStackTrace();
                                    }


                                });
                            });
                        }
                    });
                }
            }

            @Override
            public void onUserInfoTimeOut() {

                if (onError != null) {
                    onError.error();
                }
            }

            @Override
            public void onUserInfoError(int majorCode, int minorCode) {

                if (onError != null) {
                    onError.error();
                }
            }
        };

        new RequestUserInfo().userInfo(peerId);
    }

    //**************************************************************************************************************************************

    private static void goToRoom(long roomid, long peerId) {

        Intent intent = new Intent(G.context, ActivityMain.class);
        intent.putExtra(ActivityMain.openChat, roomid);
        intent.putExtra(ActivityMain.userId, AccountManager.getInstance().getCurrentUser().getId());
        if (peerId >= 0) {
            intent.putExtra("PeerID", peerId);
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        G.context.startActivity(intent);
    }

    private static void goToRoomWithTextMessage(final Context context, long roomId, String message, ProtoGlobal.Room.Type type, long peerId) {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                DbManager.getInstance().doRealmTask(realm -> {
                    if (message != null && message.length() > 0 && roomId > 0) {
                        RealmRoomMessage roomMessage = RealmRoomMessage.makeTextMessage(roomId, message);
                        new ChatSendMessageUtil().newBuilder(type, ProtoGlobal.RoomMessageType.TEXT, roomId).message(message).sendMessage(roomMessage.getMessageId() + "");
                        AsyncTransaction.executeTransactionWithLoading(context, realm, realm1 -> realm1.copyToRealmOrUpdate(roomMessage), () -> {
                            Intent intent = new Intent(G.context, ActivityMain.class);
                            intent.putExtra(ActivityMain.openChat, roomId);
                            intent.putExtra(ActivityMain.userId, AccountManager.getInstance().getCurrentUser().getId());
                            if (peerId >= 0) {
                                intent.putExtra("PeerID", peerId);
                            }

                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            G.context.startActivity(intent);
                        });

                    }
                });
            }
        });
    }

    public interface OnComplete {
        void complete();
    }

    public interface OnError {
        void error();
    }

    //**************************************************************************************************************************************
}
