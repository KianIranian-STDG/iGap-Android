package com.iGap.response;

import com.iGap.G;
import com.iGap.helper.HelperGetAction;
import com.iGap.proto.ProtoChatSetAction;
import com.iGap.proto.ProtoGlobal;
import com.iGap.realm.RealmRoom;
import com.iGap.realm.RealmRoomFields;
import com.iGap.realm.RealmUserInfo;
import io.realm.Realm;

public class ChatSetActionResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public ChatSetActionResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();
        final ProtoChatSetAction.ChatSetActionResponse.Builder builder = (ProtoChatSetAction.ChatSetActionResponse.Builder) message;

        G.handler.post(new Runnable() {
            @Override
            public void run() {
                Realm realm = Realm.getDefaultInstance();
                RealmUserInfo realmUserInfo = realm.where(RealmUserInfo.class).findFirst();
                //if (realmUserInfo != null && realmUserInfo.getUserId() != builder.getUserId()) {
                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        String action = HelperGetAction.getAction(builder.getRoomId(), ProtoGlobal.Room.Type.CHAT, builder.getAction());
                        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, builder.getRoomId()).findFirst();
                        if (realmRoom != null) {
                            realmRoom.setActionState(action, builder.getUserId());
                        }
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        if (G.onSetAction != null) {
                            G.onSetAction.onSetAction(builder.getRoomId(), builder.getUserId(), builder.getAction());
                        }

                        if (G.onSetActionInRoom != null) {
                            G.onSetActionInRoom.onSetAction(builder.getRoomId(), builder.getUserId(), builder.getAction());
                        }
                    }
                });
                //}
                realm.close();
            }
        });

    }

    @Override
    public void timeOut() {
        super.timeOut();
    }

    @Override
    public void error() {
        super.error();
    }
}


