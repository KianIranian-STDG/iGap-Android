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

import net.iGap.module.ChatSendMessageUtil;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.module.accountManager.DbManager;
import net.iGap.module.structs.StructMessageOption;
import net.iGap.observers.eventbus.EventManager;
import net.iGap.proto.ProtoGlobal;
import net.iGap.proto.ProtoResponse;
import net.iGap.realm.RealmNotificationRoomMessage;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomMessage;
import net.iGap.realm.RealmUserInfo;
import net.iGap.request.RequestClientGetRoom;

/**
 * helper message response for get message and detect message that is for
 * chat, group or channel and after set it to the realm and update view
 */
public class HelperMessageResponse {


    public static void handleMessage(final long roomId, final ProtoGlobal.RoomMessage roomMessage, final ProtoGlobal.Room.Type roomType, final ProtoResponse.Response response, final String identity) {
        DbManager.getInstance().doRealmTransaction(realm -> {
            /**
             * put message to realm
             */

            FileLog.i("HelperMessageResponse roomType -> " + roomType.toString() + " messageId -> " + roomMessage.getMessageId() + " roomId -> " + roomId);

            RealmRoomMessage realmRoomMessage = RealmRoomMessage.putOrUpdate(realm, roomId, roomMessage, new StructMessageOption().setGap());
            final RealmRoom room = realm.where(RealmRoom.class).equalTo("id", roomId).findFirst();
            /**
             * because user may have more than one device, his another device should not
             * be recipient but sender. so I check current userId with room message user id,
             * and if not equals and response is null, so we sure recipient is another user
             */
            if (!roomMessage.getAuthor().getHash().equals(RealmUserInfo.getCurrentUserAuthorHash())) {
                /**
                 * i'm recipient
                 *
                 * if author has user check that client have latest info for this user or no
                 * if author don't have use this means that message is from channel so client
                 * don't have user id for message sender for get info
                 */
                if (roomMessage.getAuthor().hasUser()) {
                    RealmRegisteredInfo.needUpdateUser(roomMessage.getAuthor().getUser().getUserId(), roomMessage.getAuthor().getUser().getCacheId());
                }


                //} else if (!response.getId().isEmpty()) {
                //    /**
                //     * i'm the sender
                //     *
                //     * delete message that created with fake messageId as identity
                //     * because in new version of realm client can't update primary key
                //     */
                //    RealmRoomMessage.deleteMessage(realm, Long.parseLong(identity));
            }

            if (identity != null && identity.length() > 0) {
                RealmRoomMessage.deleteMessage(realm, Long.parseLong(identity), roomId);
            }

            if (room == null) {
                /**
                 * if first message received but the room doesn't exist, send request for create new room
                 */
                new RequestClientGetRoom().clientGetRoom(roomId, null);
            } else {
                if (room.getType() == ProtoGlobal.Room.Type.CHAT)
                    room.setDeleted(false);

                /**
                 * update unread count if new messageId that received is bigger than latest messageId that exist
                 */

                if (!roomMessage.getAuthor().getHash().equals(RealmUserInfo.getCurrentUserAuthorHash()) && (room.getLastMessage() == null || (room.getLastMessage() != null && room.getLastMessage().getMessageId() < roomMessage.getMessageId()))) {
                    room.setUnreadCount(room.getUnreadCount() + 1);
                }

                if (!roomMessage.getAuthor().getHash().equals(RealmUserInfo.getCurrentUserAuthorHash())) {

                    if (room.getFirstUnreadMessage() == null) {
                        room.setFirstUnreadMessage(realmRoomMessage);
                    }

                    if (roomMessage.getStatus() != ProtoGlobal.RoomMessageStatus.SEEN && RealmNotificationRoomMessage.canShowNotif(realm, roomMessage.getMessageId(), roomId)) {
                        RealmNotificationRoomMessage.putToDataBase(realm, roomMessage.getMessageId(), roomId);
                        HelperNotification.getInstance().addMessage(roomId, roomMessage, roomType, room, realm, AccountManager.getInstance().getCurrentUser());
                    }
                }

                /**
                 * update last message sent/received in room table
                 */
                if (room.getLastMessage() != null) {
                    if (room.getLastMessage().getMessageId() <= roomMessage.getMessageId()) {
                        room.setLastMessage(realmRoomMessage);
                    }
                } else {
                    room.setLastMessage(realmRoomMessage);
                }
            }
        });

        if (response.getId().isEmpty()) {
            /**
             * invoke following callback when i'm not the sender, because i already done everything after sending message
             */
            ChatSendMessageUtil.getInstance(AccountManager.selectedAccount).onMessageReceive(roomId, roomMessage.getMessage(), roomMessage.getMessageType(), roomMessage, roomType);
        } else {
            /**
             * invoke following callback when I'm the sender and the message has updated
             */
            ChatSendMessageUtil.getInstance(AccountManager.selectedAccount).onMessageUpdate(roomId, roomMessage.getMessageId(), roomMessage.getStatus(), identity, roomMessage);
        }

        if ((roomMessage.getAuthor().getUser().getUserId() == AccountManager.getInstance().getCurrentUser().getId()) && roomMessage.getAttachment() != null) {
            EventManager.getInstance().postEvent(EventManager.ON_UPLOAD_COMPLETED, roomMessage.getMessageType(), roomMessage.getMessageId(), roomMessage.getAttachment().getCacheId(), roomMessage.getAttachment().getToken());
        }
    }
}
