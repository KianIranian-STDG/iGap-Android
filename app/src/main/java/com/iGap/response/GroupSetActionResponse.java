package com.iGap.response;

import com.iGap.G;
import com.iGap.helper.HelperGetAction;
import com.iGap.proto.ProtoGlobal;
import com.iGap.proto.ProtoGroupSetAction;
import com.iGap.realm.RealmRoom;
import com.iGap.realm.RealmRoomFields;
import com.iGap.realm.RealmUserInfo;
import io.realm.Realm;

public class GroupSetActionResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public GroupSetActionResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();
        final ProtoGroupSetAction.GroupSetActionResponse.Builder builder = (ProtoGroupSetAction.GroupSetActionResponse.Builder) message;


        G.handler.post(new Runnable() {
            @Override
            public void run() {
                final Realm realm = Realm.getDefaultInstance();
                RealmUserInfo realmUserInfo = realm.where(RealmUserInfo.class).findFirst();
                if (realmUserInfo != null && realmUserInfo.getUserId() != builder.getUserId()) {
                    realm.executeTransactionAsync(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            String action = HelperGetAction.getAction(builder.getRoomId(), ProtoGlobal.Room.Type.GROUP, builder.getAction());
                            RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, builder.getRoomId()).findFirst();
                            if (realmRoom != null) {
                                realmRoom.setActionState(action, builder.getUserId());
                            }
                        }
                    }, new Realm.Transaction.OnSuccess() {
                        @Override
                        public void onSuccess() {
                            RealmUserInfo realmUserInfo = realm.where(RealmUserInfo.class).findFirst();
                            if (realmUserInfo.getUserInfo().getId() != builder.getUserId()) {
                                HelperGetAction.fillOrClearAction(builder.getRoomId(), builder.getUserId(), builder.getAction());

                                if (G.onSetAction != null) {
                                    G.onSetAction.onSetAction(builder.getRoomId(), builder.getUserId(), builder.getAction());
                                }

                                if (G.onSetActionInRoom != null) {
                                    G.onSetActionInRoom.onSetAction(builder.getRoomId(), builder.getUserId(), builder.getAction());
                                }
                            }
                            realm.close();
                        }
                    });
                } else {
                    realm.close();
                }
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


