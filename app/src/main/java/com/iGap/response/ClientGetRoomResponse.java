package com.iGap.response;

import android.util.Log;

import com.iGap.G;
import com.iGap.proto.ProtoClientGetRoom;
import com.iGap.proto.ProtoError;
import com.iGap.realm.RealmRoom;
import com.iGap.realm.RealmRoomFields;

import io.realm.Realm;

public class ClientGetRoomResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public ClientGetRoomResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {

        Log.i("SOC", "ClientGetRoomResponse handler : " + message);

        final ProtoClientGetRoom.ClientGetRoomResponse.Builder clientGetRoom =
                (ProtoClientGetRoom.ClientGetRoomResponse.Builder) message;

        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                // check if room doesn't exist, add room to database
                RealmRoom room = realm.where(RealmRoom.class)
                        .equalTo(RealmRoomFields.ID, clientGetRoom.getRoom().getId())
                        .findFirst();
                if (room == null) {
                    realm.copyToRealmOrUpdate(RealmRoom.convert(clientGetRoom.getRoom(), realm));
                }
            }
        });

        // FIXME: 11/6/2016 [Alireza] commented, chon ehsas mikonam ezafie
        /*realm.executeTransaction(new Realm.Transaction() {
            @Override public void execute(Realm realm) {
                RealmRoom room = realm.where(RealmRoom.class)
                    .equalTo(RealmRoomFields.ID, clientGetRoom.getRoom().getId())
                    .findFirst();
                // update last message sent/received in room table
                RealmRoomMessage roomMessage = HelperRealm.getLastMessage(room.getId());
                if (room.getLastMessageTime()
                    != 0) { //TODO [Saeed Mozaffari] [2016-09-19 12:50 PM] - clear this if
                    if (room.getLastMessageTime() < roomMessage.getUpdateTime()) {
                        room.setUnreadCount(room.getUnreadCount() + 1);
                        room.setLastMessageId(roomMessage.getMessageId());
                        room.setLastMessageTime(roomMessage.getUpdateTimeAsSeconds());

                        realm.copyToRealmOrUpdate(room);
                    }
                }
            }
        });*/

        realm.close();

        G.onClientGetRoomResponse.onClientGetRoomResponse(clientGetRoom.getRoom(), clientGetRoom);
    }

    @Override
    public void timeOut() {
        Log.i("SOC", "ClientGetRoomResponse timeout");
        G.onClientGetRoomResponse.onTimeOut();
    }


    @Override
    public void error() {
        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();

        Log.i("SOC", "ClientGetRoomResponse response.majorCode() : " + majorCode);
        Log.i("SOC", "ClientGetRoomResponse response.minorCode() : " + minorCode);

        G.onClientGetRoomResponse.onError(majorCode, minorCode);
    }
}


