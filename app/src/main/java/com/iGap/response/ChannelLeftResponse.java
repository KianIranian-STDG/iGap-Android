package com.iGap.response;

import com.iGap.G;
import com.iGap.proto.ProtoChannelLeft;
import com.iGap.proto.ProtoError;
import com.iGap.realm.RealmClientCondition;
import com.iGap.realm.RealmClientConditionFields;
import com.iGap.realm.RealmRoom;
import com.iGap.realm.RealmRoomFields;
import com.iGap.realm.RealmRoomMessage;
import com.iGap.realm.RealmRoomMessageFields;

import io.realm.Realm;

import static com.iGap.module.MusicPlayer.roomId;

public class ChannelLeftResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public ChannelLeftResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();

        final ProtoChannelLeft.ChannelLeftResponse.Builder builder = (ProtoChannelLeft.ChannelLeftResponse.Builder) message;

        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
                if (realmRoom != null) {
                    realmRoom.deleteFromRealm();
                }

                if (!builder.getResponse().getId().isEmpty()) { // if own send request for left
                    RealmRoomMessage realmRoomMessage = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, roomId).findFirst();
                    if (realmRoomMessage != null) {
                        realmRoomMessage.deleteFromRealm();
                    }

                    RealmClientCondition realmClientCondition = realm.where(RealmClientCondition.class).equalTo(RealmClientConditionFields.ROOM_ID, roomId).findFirst();
                    if (realmClientCondition != null) {
                        realmClientCondition.deleteFromRealm();
                    }

                    if (G.onGroupLeft != null) {
                        G.onGroupLeft.onGroupLeft(roomId, builder.getMemberId());
                    }

                    if (G.onChannelLeft != null) {
                        G.onChannelLeft.onChannelLeft(builder.getRoomId(), builder.getMemberId());
                    }
                }
            }
        });
        realm.close();
    }

    @Override
    public void timeOut() {
        super.timeOut();
        if (G.onChannelLeft != null) {
            G.onChannelLeft.onTimeOut();
        }
    }

    @Override
    public void error() {
        super.error();
        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();

        if (G.onChannelLeft != null) {
            G.onChannelLeft.onError(majorCode, minorCode);
        }
    }
}


