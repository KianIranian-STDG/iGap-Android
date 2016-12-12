package com.iGap.response;

import com.iGap.G;
import com.iGap.helper.HelperGetUserInfo;
import com.iGap.interfaces.OnGetUserInfo;
import com.iGap.proto.ProtoClientGetRoom;
import com.iGap.proto.ProtoError;
import com.iGap.proto.ProtoGlobal;
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
            public void execute(final Realm realm) {

                if (clientGetRoom.getRoom().getType() == ProtoGlobal.Room.Type.CHAT) {

                    new HelperGetUserInfo(new OnGetUserInfo() {
                        @Override
                        public void onGetUserInfo(ProtoGlobal.RegisteredUser registeredUser) {
                            G.handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Realm realm1 = Realm.getDefaultInstance();
                                    realm1.executeTransactionAsync(new Realm.Transaction() {
                                        @Override
                                        public void execute(Realm realm) {
                                            RealmRoom.putOrUpdate(clientGetRoom.getRoom());
                                        }
                                    }, new OnSuccess() {
                                        @Override
                                        public void onSuccess() {
                                            if (G.onClientGetRoomResponse != null) {
                                                G.onClientGetRoomResponse.onClientGetRoomResponse(clientGetRoom.getRoom(), clientGetRoom);
                                            }
                                        }
                                    });
                                    realm1.close();
                                }
                            });
                        }
                    }).getUserInfo(clientGetRoom.getRoom().getChatRoomExtra().getPeer().getId());

                } else {
                    RealmRoom.putOrUpdate(clientGetRoom.getRoom());

                    G.handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (G.onClientGetRoomResponse != null) {
                                G.onClientGetRoomResponse.onClientGetRoomResponse(clientGetRoom.getRoom(), clientGetRoom);
                            }
                        }
                    }, 500);
                }
            }
        });
        realm.close();
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


