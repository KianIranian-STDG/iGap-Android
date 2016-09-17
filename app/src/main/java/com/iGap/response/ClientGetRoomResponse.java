package com.iGap.response;

import android.text.format.DateUtils;
import android.util.Log;

import com.iGap.G;
import com.iGap.helper.HelperRealm;
import com.iGap.proto.ProtoClientGetRoom;
import com.iGap.proto.ProtoError;
import com.iGap.realm.RealmRoom;
import com.iGap.realm.RealmRoomMessage;

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
        final ProtoClientGetRoom.ClientGetRoomResponse.Builder clientGetRoom = (ProtoClientGetRoom.ClientGetRoomResponse.Builder) message;

        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                // check if room doesn't exist, add room to database
                RealmRoom room = realm.where(RealmRoom.class).equalTo("id", clientGetRoom.getRoom().getId()).findFirst();
                if (room == null) {
                    realm.copyToRealmOrUpdate(RealmRoom.convert(clientGetRoom.getRoom(), realm));
                }
            }
        });

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmRoom room = realm.where(RealmRoom.class).equalTo("id", clientGetRoom.getRoom().getId()).findFirst();
                // update last message sent/received in room table
                if (room != null) {
                    RealmRoomMessage roomMessage = HelperRealm.getLastMessage(room.getId());
                    if (room.getLastMessageTime() < roomMessage.getUpdateTime()) {
                        room.setUnreadCount(room.getUnreadCount() + 1);
                        room.setLastMessageId(roomMessage.getMessageId());
                        room.setLastMessageTime((int) (roomMessage.getUpdateTime() / DateUtils.SECOND_IN_MILLIS));

                        realm.copyToRealmOrUpdate(room);
                    }
                }
            }
        });

        realm.close();

        G.onClientGetRoomResponse.onClientGetRoomResponse(clientGetRoom.getRoom(), clientGetRoom);
    }

    @Override
    public void timeOut() {
        Log.i("SOC", "ClientGetRoomResponse timeout");
    }

    @Override
    public void error() {
        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();

        Log.i("SOC", "ClientGetRoomResponse response.majorCode() : " + majorCode);
        Log.i("SOC", "ClientGetRoomResponse response.minorCode() : " + minorCode);
    }
}


