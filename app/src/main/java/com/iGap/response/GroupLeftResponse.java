package com.iGap.response;

import android.util.Log;

import com.iGap.G;
import com.iGap.proto.ProtoError;
import com.iGap.proto.ProtoGroupLeft;
import com.iGap.realm.RealmClientCondition;
import com.iGap.realm.RealmClientConditionFields;
import com.iGap.realm.RealmRoom;
import com.iGap.realm.RealmRoomFields;
import com.iGap.realm.RealmRoomMessage;
import com.iGap.realm.RealmRoomMessageFields;

import io.realm.Realm;

public class GroupLeftResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public GroupLeftResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {

        ProtoGroupLeft.GroupLeftResponse.Builder builder =
                (ProtoGroupLeft.GroupLeftResponse.Builder) message;
        final long roomId = builder.getRoomId();
        final long memberId = builder.getMemberId();

        Realm realm = Realm.getDefaultInstance();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                RealmRoom realmRoom =
                        realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
                if (realmRoom != null) {
                    realmRoom.deleteFromRealm();

                    G.onGroupLeft.onGroupLeft(roomId, memberId);
                }

                RealmRoomMessage realmRoomMessage = realm.where(RealmRoomMessage.class)
                        .equalTo(RealmRoomMessageFields.ROOM_ID, roomId)
                        .findFirst();
                if (realmRoomMessage != null) {
                    realmRoomMessage.deleteFromRealm();
                }

                RealmClientCondition realmClientCondition = realm.where(RealmClientCondition.class)
                        .equalTo(RealmClientConditionFields.ROOM_ID, roomId)
                        .findFirst();
                if (realmClientCondition != null) {
                    realmClientCondition.deleteFromRealm();
                }
            }
        });

        realm.close();
    }

    @Override
    public void error() {

        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();
        Log.i("SOC", "GroupLeftResponse majorCode : " + majorCode);
        Log.i("SOC", "GroupLeftResponse minorCode : " + minorCode);

        G.onGroupLeft.onError(majorCode, minorCode);

    }
}
