package com.iGap.response;

import android.util.Log;

import com.iGap.G;
import com.iGap.proto.ProtoError;
import com.iGap.proto.ProtoGroupUpdateStatus;
import com.iGap.proto.ProtoResponse;
import com.iGap.realm.RealmClientCondition;
import com.iGap.realm.RealmClientConditionFields;
import com.iGap.realm.RealmOfflineSeen;
import com.iGap.realm.RealmRoomMessage;
import com.iGap.realm.RealmRoomMessageFields;

import io.realm.Realm;
import io.realm.RealmList;

public class GroupUpdateStatusResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public GroupUpdateStatusResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }


    @Override
    public void handler() {
        final ProtoGroupUpdateStatus.GroupUpdateStatusResponse.Builder builder = (ProtoGroupUpdateStatus.GroupUpdateStatusResponse.Builder) message;
        final ProtoResponse.Response.Builder response = ProtoResponse.Response.newBuilder().mergeFrom(builder.getResponse());
        Log.i("SOC_CONDITION", "GroupUpdateStatusResponse response.getId() : " + response.getId());
        Log.i("SOC_CONDITION", "GroupUpdateStatusResponse response.getTimestamp() : " + response.getTimestamp());
        Log.i("SOC_CONDITION", "GroupUpdateStatusResponse chatUpdateStatus : " + builder.getStatus());

        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                if (!response.getId().isEmpty()) { // I'm sender

                    RealmClientCondition realmClientCondition = realm.where(RealmClientCondition.class).equalTo(RealmClientConditionFields.ROOM_ID, builder.getRoomId()).findFirst();
                    RealmList<RealmOfflineSeen> offlineSeen = realmClientCondition.getOfflineSeen();
                    for (int i = offlineSeen.size() - 1; i >= 0; i--) {
                        RealmOfflineSeen realmOfflineSeen = offlineSeen.get(i);
                        Log.i("SOC_CONDITION", "realmOfflineSeen 1 : " + realmOfflineSeen);
                        realmOfflineSeen.deleteFromRealm();
                    }

                } else { // I'm recipient

                    // find message from database and update its status
                    RealmRoomMessage roomMessage = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, builder.getMessageId()).findFirst();
                    Log.i("SOC_CONDITION", "I'm recipient 1");
                    if (roomMessage != null) {
                        Log.i(ChatUpdateStatusResponse.class.getSimpleName(), "oftad > " + builder.getStatus().toString());
                        roomMessage.setStatus(builder.getStatus().toString());
                        realm.copyToRealmOrUpdate(roomMessage);
                        Log.i("SOC_CONDITION", "I'm recipient ");
                        G.chatUpdateStatusUtil.onChatUpdateStatus(builder.getRoomId(), builder.getMessageId(), builder.getStatus(), builder.getStatusVersion());
                    }

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

        Log.i("SOC", "GroupUpdateStatusResponse response.majorCode() : " + majorCode);
        Log.i("SOC", "GroupUpdateStatusResponse response.minorCode() : " + minorCode);
    }

    @Override
    public void timeOut() {
        Log.i("SOC", "GroupUpdateStatusResponse timeout");
    }
}
