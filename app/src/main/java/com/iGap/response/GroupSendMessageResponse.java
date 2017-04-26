/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package com.iGap.response;

import com.iGap.G;
import com.iGap.helper.HelperInfo;
import com.iGap.proto.ProtoGlobal;
import com.iGap.proto.ProtoGroupSendMessage;
import com.iGap.realm.RealmRoom;
import com.iGap.realm.RealmRoomFields;
import com.iGap.realm.RealmRoomMessage;
import com.iGap.request.RequestClientGetRoom;
import io.realm.Realm;

import static com.iGap.G.authorHash;
import static com.iGap.G.userId;
import static com.iGap.realm.RealmRoomMessage.makeFailed;

public class GroupSendMessageResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public GroupSendMessageResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();
        final ProtoGroupSendMessage.GroupSendMessageResponse.Builder builder = (ProtoGroupSendMessage.GroupSendMessageResponse.Builder) message;
        Realm realm = Realm.getDefaultInstance();
        final ProtoGlobal.RoomMessage roomMessage = builder.getRoomMessage();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                /**
                 * put message to realm
                 */
                RealmRoomMessage realmRoomMessage = RealmRoomMessage.putOrUpdate(roomMessage, builder.getRoomId());

                /**
                 * because user may have more than one device, his another device should not
                 * be recipient but sender. so I check current userId with room message user id,
                 * and if not equals and response is null, so we sure recipient is another user
                 */

                if (userId != roomMessage.getAuthor().getUser().getUserId()) {
                    /**
                     * i'm the recipient
                     */
                    HelperInfo.needUpdateUser(roomMessage.getAuthor().getUser().getUserId(), roomMessage.getAuthor().getUser().getCacheId());
                    G.helperNotificationAndBadge.checkAlert(true, ProtoGlobal.Room.Type.GROUP, builder.getRoomId());
                } else {
                    /**
                     * i'm the sender
                     *
                     * delete message that created with fake messageId as identity
                     * because in new version of realm client can't update primary key
                     */
                    if (!builder.getResponse().getId().isEmpty()) {
                        RealmRoomMessage.deleteMessage(realm, Long.parseLong(identity));
                    }
                }

                /**
                 * if first message received but the room doesn't exist, create new room
                 */
                RealmRoom room = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, builder.getRoomId()).findFirst();
                if (room == null) {
                    new RequestClientGetRoom().clientGetRoom(builder.getRoomId(), null);
                } else {
                    if (!roomMessage.getAuthor().getHash().equals(authorHash) && (room.getLastMessage() == null || (room.getLastMessage().getMessageId() < roomMessage.getMessageId()))) {
                        room.setUnreadCount(room.getUnreadCount() + 1);
                    }
                    /**
                     *  update last message sent/received in room table
                     */
                    if (room.getLastMessage() != null) {
                        if (room.getLastMessage().getMessageId() <= roomMessage.getMessageId()) {
                            room.setLastMessage(realmRoomMessage);
                        }
                    } else {
                        room.setLastMessage(RealmRoomMessage.putOrUpdate(roomMessage, builder.getRoomId()));
                    }
                }
            }
        });

        if (builder.getResponse().getId().isEmpty()) {
            /**
             * invoke following callback when i'm not the sender, because I already done everything after sending message
             */
            G.chatSendMessageUtil.onMessageReceive(builder.getRoomId(), roomMessage.getMessage(), roomMessage.getMessageType(), roomMessage, ProtoGlobal.Room.Type.GROUP);
        } else {
            /**
             * invoke following callback when I'm the sender and the message has updated
             */
            G.chatSendMessageUtil.onMessageUpdate(builder.getRoomId(), roomMessage.getMessageId(), roomMessage.getStatus(), identity, roomMessage);
        }

        realm.close();
    }

    @Override
    public void error() {
        super.error();
        makeFailed(Long.parseLong(identity));
    }

    @Override
    public void timeOut() {
        super.timeOut();
    }
}
