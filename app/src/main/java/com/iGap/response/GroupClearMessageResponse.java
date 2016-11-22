package com.iGap.response;

import android.text.format.DateUtils;

import com.iGap.G;
import com.iGap.proto.ProtoGroupClearMessage;
import com.iGap.realm.RealmClientCondition;
import com.iGap.realm.RealmClientConditionFields;
import com.iGap.realm.RealmRoom;
import com.iGap.realm.RealmRoomFields;

import io.realm.Realm;

public class GroupClearMessageResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public GroupClearMessageResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        final ProtoGroupClearMessage.GroupClearMessageResponse.Builder builder =
                (ProtoGroupClearMessage.GroupClearMessageResponse.Builder) message;
        builder.getRoomId();
        builder.getClearId();

        Realm realm = Realm.getDefaultInstance();
        if (builder.getResponse().getId().isEmpty()) { // another account cleared message
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    final RealmClientCondition realmClientCondition =
                            realm.where(RealmClientCondition.class)
                                    .equalTo(RealmClientConditionFields.ROOM_ID,
                                            builder.getRoomId())
                                    .findFirst();
                    realmClientCondition.setClearId(builder.getClearId());
                }
            });
            G.clearMessagesUtil.onChatClearMessage(builder.getRoomId(),
                    builder.getClearId(), builder.getResponse());
        }

        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, builder.getRoomId()).findFirst();
        if (realmRoom != null) {
            realmRoom.setUpdatedTime(builder.getResponse().getTimestamp() * DateUtils.SECOND_IN_MILLIS);
        }

        realm.close();
    }

    @Override
    public void error() {

    }
}
