package com.iGap.response;

import android.util.Log;
import com.iGap.G;
import com.iGap.proto.ProtoError;
import com.iGap.proto.ProtoGroupDelete;
import com.iGap.realm.RealmRoom;
import com.iGap.realm.RealmRoomFields;
import com.iGap.realm.RealmRoomMessage;
import com.iGap.realm.RealmRoomMessageFields;
import io.realm.Realm;
import io.realm.RealmResults;

public class GroupDeleteResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public GroupDeleteResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override public void handler() {
        ProtoGroupDelete.GroupDeleteResponse.Builder builder = (ProtoGroupDelete.GroupDeleteResponse.Builder) message;

        final long id = builder.getRoomId();

        Log.i("XXXC", "re1: " + id);
        final Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override public void execute(final Realm realm) {

                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, id).findFirst();
                if (realmRoom != null) {
                    realmRoom.deleteFromRealm();
                }
                RealmResults<RealmRoomMessage> realmRoomMessage = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, id).findAll();
                if (realmRoomMessage != null) {
                    realmRoomMessage.deleteAllFromRealm();
                }
                Log.i("XXXC", "re2: " + id);
            }
        });
        realm.close();
        G.onGroupDelete.onGroupDelete(id);
    }

    @Override public void timeOut() {
        super.timeOut();
    }

    @Override public void error() {
        super.error();

        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();

        Log.i("XXX", "GroupDeleteResponse response.majorCode() : " + majorCode);
        Log.i("XXX", "GroupDeleteResponse response.minorCode() : " + minorCode);

        G.onGroupDelete.Error(majorCode, minorCode);
    }
}


