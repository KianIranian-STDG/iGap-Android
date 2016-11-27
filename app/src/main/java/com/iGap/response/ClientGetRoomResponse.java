package com.iGap.response;

import com.iGap.G;
import com.iGap.proto.ProtoClientGetRoom;
import com.iGap.proto.ProtoError;
import com.iGap.realm.RealmRoom;

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
        super.handler();

        final ProtoClientGetRoom.ClientGetRoomResponse.Builder clientGetRoom = (ProtoClientGetRoom.ClientGetRoomResponse.Builder) message;

        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmRoom.putOrUpdate(clientGetRoom.getRoom());
            }
        });
        realm.close();

        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (G.onClientGetRoomResponse != null) {
                    G.onClientGetRoomResponse.onClientGetRoomResponse(clientGetRoom.getRoom(), clientGetRoom);
                }
            }
        }, 500);
    }

    @Override
    public void timeOut() {
        super.timeOut();
        G.onClientGetRoomResponse.onTimeOut();
    }


    @Override
    public void error() {
        super.error();
        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();

        G.onClientGetRoomResponse.onError(majorCode, minorCode);
    }
}


