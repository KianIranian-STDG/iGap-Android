/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package com.iGap.helper;

import android.os.Handler;
import com.iGap.G;
import com.iGap.proto.ProtoGlobal;
import com.iGap.proto.ProtoResponse;
import com.iGap.realm.RealmRoom;
import com.iGap.realm.RealmRoomFields;
import com.iGap.realm.RealmRoomMessage;
import com.iGap.request.RequestClientGetRoom;
import io.realm.Realm;

import static com.iGap.G.authorHash;

/**
 * helper message response for get message and detect message that is for
 * chat, group or channel and after set it to the realm and update view
 */
public class HelperMessageResponse {

    private static long latestMessageTime;
    private static int delay;


    public static void handleMessage(final long roomId, final ProtoGlobal.RoomMessage roomMessage, final ProtoResponse.Response response, final String identity) {

        /**
         * if from latest message was time out don't need delay otherwise set delay
         */
        if (HelperTimeOut.timeoutChecking(0, latestMessageTime, 500)) {
            delay = 0;
        } else {
            delay++;
        }
        latestMessageTime = System.currentTimeMillis();

        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                new Handler(G.currentActivity.getMainLooper()).post(new Runnable() {
                    @Override public void run() {
                        final Realm realm = Realm.getDefaultInstance();

                        realm.executeTransactionAsync(new Realm.Transaction() {
                            @Override public void execute(Realm realm) {

                                /**
                                 * put message to realm
                                 */
                                RealmRoomMessage realmRoomMessage = RealmRoomMessage.putOrUpdate(roomMessage, roomId);
                                RealmRoom room = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
                                /**
                                 * because user may have more than one device, his another device should not
                                 * be recipient but sender. so I check current userId with room message user id,
                                 * and if not equals and response is null, so we sure recipient is another user
                                 */
                                if (!roomMessage.getAuthor().getHash().equals(authorHash)) {
                                    /**
                                     * i'm recipient
                                     *
                                     * if author has user check that client have latest info for this user or no
                                     * if author don't have use this means that message is from channel so client
                                     * don't have user id for message sender for get info
                                     */
                                    if (roomMessage.getAuthor().hasUser()) {
                                        HelperInfo.needUpdateUser(roomMessage.getAuthor().getUser().getUserId(), roomMessage.getAuthor().getUser().getCacheId());
                                    }
                                    ProtoGlobal.Room.Type type = ProtoGlobal.Room.Type.CHAT;
                                    if (room != null) {
                                        type = room.getType();
                                    }

                                    G.helperNotificationAndBadge.checkAlert(true, type, roomId);
                                } else if (!response.getId().isEmpty()) {
                                    /**
                                     * i'm the sender
                                     *
                                     * delete message that created with fake messageId as identity
                                     * because in new version of realm client can't update primary key
                                     */
                                    RealmRoomMessage.deleteMessage(realm, Long.parseLong(identity));
                                }

                                if (room == null) {
                                    /**
                                     * if first message received but the room doesn't exist, send request for create new room
                                     */
                                    new RequestClientGetRoom().clientGetRoom(roomId, null);
                                } else {

                                    /**
                                     * update unread count if new messageId that received is bigger than latest messageId that exist
                                     */
                                    if (!roomMessage.getAuthor().getHash().equals(authorHash) && (room.getLastMessage() == null || (room.getLastMessage() != null
                                        && room.getLastMessage().getMessageId() < roomMessage.getMessageId()))) {
                                        room.setUnreadCount(room.getUnreadCount() + 1);
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

                            }
                        }, new Realm.Transaction.OnSuccess() {
                            @Override public void onSuccess() {

                                if (response.getId().isEmpty()) {
                                    /**
                                     * invoke following callback when i'm not the sender, because i already done everything after sending message
                                     */
                                    G.chatSendMessageUtil.onMessageReceive(roomId, roomMessage.getMessage(), roomMessage.getMessageType(), roomMessage, ProtoGlobal.Room.Type.CHANNEL);
                                } else {
                                    /**
                                     * invoke following callback when I'm the sender and the message has updated
                                     */
                                    G.chatSendMessageUtil.onMessageUpdate(roomId, roomMessage.getMessageId(), roomMessage.getStatus(), identity, roomMessage);
                                }

                                realm.close();
                            }
                        }, new Realm.Transaction.OnError() {
                            @Override public void onError(Throwable error) {
                                realm.close();
                            }
                        });
                    }
                });
            }
        }, 500 * delay);
    }
}
