package com.iGap.response;

import com.iGap.G;
import com.iGap.proto.ProtoChannelEdit;
import com.iGap.proto.ProtoError;
import com.iGap.realm.RealmRoom;
import com.iGap.realm.RealmRoomFields;

import io.realm.Realm;

import static com.iGap.module.MusicPlayer.roomId;

public class ChannelEditResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public ChannelEditResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();

        final ProtoChannelEdit.ChannelEditResponse.Builder builder = (ProtoChannelEdit.ChannelEditResponse.Builder) message;

        Realm realm = Realm.getDefaultInstance();
        final RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
        if (realmRoom != null) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realmRoom.setTitle(builder.getName());
                    realmRoom.getChannelRoom().setDescription(builder.getDescription());
                }
            });

            if (G.onChannelEdit != null) {
                G.onChannelEdit.onChannelEdit(builder.getRoomId(), builder.getName(), builder.getDescription());
            }
        }
        realm.close();
    }

    @Override
    public void timeOut() {
        super.timeOut();

        G.onChannelEdit.onTimeOut();
    }

    @Override
    public void error() {
        super.error();
        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();

        G.onChannelEdit.onError(majorCode, minorCode);
    }
}


