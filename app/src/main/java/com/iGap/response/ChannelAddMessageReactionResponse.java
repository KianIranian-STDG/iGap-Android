package com.iGap.response;

import com.iGap.G;
import com.iGap.proto.ProtoChannelAddMessageReaction;
import com.iGap.proto.ProtoError;
import com.iGap.proto.ProtoGlobal;
import com.iGap.realm.RealmRoomMessage;
import com.iGap.realm.RealmRoomMessageFields;

import io.realm.Realm;

public class ChannelAddMessageReactionResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public ChannelAddMessageReactionResponse(int actionId, Object protoClass, String identity) { // here identity is roomId and messageId
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();

        final ProtoChannelAddMessageReaction.ChannelAddMessageReactionResponse.Builder builder = (ProtoChannelAddMessageReaction.ChannelAddMessageReactionResponse.Builder) message;
        if (G.onChannelAddMessageReaction != null && identity != null) {

            String[] identityParams = identity.split("\\*");
            String roomId = identityParams[0];
            final String messageId = identityParams[1];
            final String messageReaction = identityParams[2];

            ProtoGlobal.RoomMessageReaction reaction = null;
            if (messageReaction.equals(ProtoGlobal.RoomMessageReaction.THUMBS_UP.toString())) {
                reaction = ProtoGlobal.RoomMessageReaction.THUMBS_UP;
            } else if (messageReaction.equals(ProtoGlobal.RoomMessageReaction.THUMBS_DOWN.toString())) {
                reaction = ProtoGlobal.RoomMessageReaction.THUMBS_DOWN;
            }

            Realm realm = Realm.getDefaultInstance();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    ProtoGlobal.RoomMessageReaction reaction1 = null;
                    if (messageReaction.equals(ProtoGlobal.RoomMessageReaction.THUMBS_UP.toString())) {
                        reaction1 = ProtoGlobal.RoomMessageReaction.THUMBS_UP;
                    } else if (messageReaction.equals(ProtoGlobal.RoomMessageReaction.THUMBS_DOWN.toString())) {
                        reaction1 = ProtoGlobal.RoomMessageReaction.THUMBS_DOWN;
                    }
                    RealmRoomMessage realmRoomMessage = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, Long.parseLong(messageId)).findFirst();
                    if (realmRoomMessage != null) {
                        realmRoomMessage.setVote(reaction1, Integer.parseInt(builder.getReactionCounterLabel()));
                    }
                }
            });
            realm.close();

            G.onChannelAddMessageReaction.onChannelAddMessageReaction(Long.parseLong(roomId), Long.parseLong(messageId), Integer.parseInt(builder.getReactionCounterLabel()), reaction);
        }
    }

    @Override
    public void timeOut() {
        super.timeOut();
    }

    @Override
    public void error() {
        super.error();
        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();
        if (G.onChannelAddMessageReaction != null) {
            G.onChannelAddMessageReaction.onError(majorCode, minorCode);
        }
    }
}


