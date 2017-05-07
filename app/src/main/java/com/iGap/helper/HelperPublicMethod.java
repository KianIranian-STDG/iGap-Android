package com.iGap.helper;

import android.content.Intent;
import com.iGap.G;
import com.iGap.activities.ActivityChat;
import com.iGap.interfaces.OnChatGetRoom;
import com.iGap.interfaces.OnUserInfoResponse;
import com.iGap.proto.ProtoGlobal;
import com.iGap.realm.RealmAvatar;
import com.iGap.realm.RealmRegisteredInfo;
import com.iGap.realm.RealmRegisteredInfoFields;
import com.iGap.realm.RealmRoom;
import com.iGap.realm.RealmRoomFields;
import com.iGap.request.RequestChatGetRoom;
import com.iGap.request.RequestUserInfo;
import io.realm.Realm;

/**
 * Created by android3 on 4/18/2017.
 */

public class HelperPublicMethod {

    public interface Oncomplet {
        void complete();
    }

    public interface OnError {
        void error();
    }

    //**************************************************************************************************************************************

    public static void goToChatRoom(final long peerId, final Oncomplet oncomplet, final OnError onError) {

        final Realm realm = Realm.getDefaultInstance();
        final RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.CHAT_ROOM.PEER_ID, peerId).findFirst();

        if (realmRoom != null) {

            if (oncomplet != null) {
                oncomplet.complete();
            }

            goToRoom(realmRoom.getId(), -1);
        } else {
            G.onChatGetRoom = new OnChatGetRoom() {
                @Override public void onChatGetRoom(final long roomId) {

                    if (onError != null) {
                        onError.error();
                    }

                    getUserInfo(peerId, roomId, oncomplet, onError);
                }

                @Override public void onChatGetRoomCompletely(ProtoGlobal.Room room) {

                }

                @Override public void onChatGetRoomTimeOut() {

                    if (onError != null) {
                        onError.error();
                    }
                }

                @Override public void onChatGetRoomError(int majorCode, int minorCode) {

                    if (onError != null) {
                        onError.error();
                    }
                }
            };

            new RequestChatGetRoom().chatGetRoom(peerId);
        }
        realm.close();
    }

    private static void getUserInfo(final long peerId, final long roomId, final Oncomplet oncomplet, final OnError onError) {

        G.onUserInfoResponse = new OnUserInfoResponse() {
            @Override public void onUserInfo(final ProtoGlobal.RegisteredUser user, String identity) {

                G.handler.post(new Runnable() {
                    @Override public void run() {

                        if (user.getId() == peerId) {
                            Realm realm = Realm.getDefaultInstance();

                            realm.executeTransactionAsync(new Realm.Transaction() {
                                @Override public void execute(Realm realm) {
                                    RealmRegisteredInfo realmRegisteredInfo = realm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, user.getId()).findFirst();
                                    if (realmRegisteredInfo == null) {
                                        realmRegisteredInfo = realm.createObject(RealmRegisteredInfo.class);
                                        realmRegisteredInfo.setId(user.getId());
                                        realmRegisteredInfo.setDoNotshowSpamBar(false);
                                    }

                                    RealmAvatar.put(user.getId(), user.getAvatar(), true);
                                    realmRegisteredInfo.setUsername(user.getUsername());
                                    realmRegisteredInfo.setPhoneNumber(Long.toString(user.getPhone()));
                                    realmRegisteredInfo.setFirstName(user.getFirstName());
                                    realmRegisteredInfo.setLastName(user.getLastName());
                                    realmRegisteredInfo.setDisplayName(user.getDisplayName());
                                    realmRegisteredInfo.setInitials(user.getInitials());
                                    realmRegisteredInfo.setColor(user.getColor());
                                    realmRegisteredInfo.setStatus(user.getStatus().toString());
                                    realmRegisteredInfo.setAvatarCount(user.getAvatarCount());
                                    realmRegisteredInfo.setMutual(user.getMutual());
                                }
                            }, new Realm.Transaction.OnSuccess() {
                                @Override public void onSuccess() {
                                    try {

                                        if (oncomplet != null) {
                                            oncomplet.complete();
                                        }

                                        goToRoom(roomId, peerId);
                                    } catch (IllegalStateException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });

                            realm.close();
                        }
                    }
                });
            }

            @Override public void onUserInfoTimeOut() {

                if (onError != null) {
                    onError.error();
                }
            }

            @Override public void onUserInfoError(int majorCode, int minorCode) {

                if (onError != null) {
                    onError.error();
                }
            }
        };

        new RequestUserInfo().userInfo(peerId);
    }

    private static void goToRoom(long roomid, long peerId) {

        Intent intent = new Intent(G.currentActivity, ActivityChat.class);
        intent.putExtra("RoomId", roomid);

        if (peerId >= 0) {
            intent.putExtra("peerId", peerId);
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        G.currentActivity.startActivity(intent);
    }

    //**************************************************************************************************************************************
}
