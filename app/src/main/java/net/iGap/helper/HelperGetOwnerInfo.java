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

import net.iGap.G;
import net.iGap.module.accountManager.DbManager;
import net.iGap.observers.interfaces.OnClientGetRoomResponse;
import net.iGap.observers.interfaces.OnUserInfoResponse;
import net.iGap.proto.ProtoClientGetRoom;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.realm.RealmRoom;
import net.iGap.request.RequestClientGetRoom;
import net.iGap.request.RequestUserInfo;

public class HelperGetOwnerInfo {

    public static void checkInfo(long id, RoomType roomType, Listener listener) {

        switch (roomType) {

            case Room:
                checkRoomExist(id, listener);
                break;
            case User:
                checkUserExist(id, listener);
                break;
        }
    }

    private static void checkRoomExist(long id, final Listener listener) {

        DbManager.getInstance().doRealmTask(realm -> {
            RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo("id", id).findFirst();

            if (realmRoom == null) {

                G.onClientGetRoomResponse = new OnClientGetRoomResponse() {
                    @Override
                    public void onClientGetRoomResponse(ProtoGlobal.Room room, ProtoClientGetRoom.ClientGetRoomResponse.Builder builder, RequestClientGetRoom.IdentityClientGetRoom identity) {

                        if (identity.createRoomMode == RequestClientGetRoom.CreateRoomMode.requestFromOwner) {
                            if (listener != null) {
                                listener.OnResponse();
                            }
                        }
                    }

                    @Override
                    public void onError(int majorCode, int minorCode) {

                    }

                    @Override
                    public void onTimeOut() {

                    }
                };

                new RequestClientGetRoom().clientGetRoom(id, RequestClientGetRoom.CreateRoomMode.requestFromOwner);
            } else {

                if (listener != null) {
                    listener.OnResponse();
                }
            }
        });
    }

    private static void checkUserExist(long userId, final Listener listener) {

        DbManager.getInstance().doRealmTask(realm -> {
            RealmRegisteredInfo registeredInfo = RealmRegisteredInfo.getRegistrationInfo(realm, userId);

            if (registeredInfo == null) {

                G.onUserInfoResponse = new OnUserInfoResponse() {
                    @Override
                    public void onUserInfo(ProtoGlobal.RegisteredUser user, String identity) {

                        if (listener != null) {
                            listener.OnResponse();
                        }
                    }

                    @Override
                    public void onUserInfoTimeOut() {

                    }

                    @Override
                    public void onUserInfoError(int majorCode, int minorCode) {

                    }
                };

                new RequestUserInfo().userInfo(userId);
            } else {

                if (listener != null) {
                    listener.OnResponse();
                }
            }
        });
    }

    enum RoomType {
        Room, User
    }

    public interface Listener {

        void OnResponse();
    }
}
