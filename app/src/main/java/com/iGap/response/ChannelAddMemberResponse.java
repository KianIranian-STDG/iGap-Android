package com.iGap.response;

import android.util.Log;

import com.iGap.G;
import com.iGap.module.SUID;
import com.iGap.proto.ProtoChannelAddMember;
import com.iGap.proto.ProtoError;
import com.iGap.realm.RealmChannelRoom;
import com.iGap.realm.RealmMember;
import com.iGap.realm.RealmRoom;
import com.iGap.realm.RealmRoomFields;

import io.realm.Realm;
import io.realm.RealmList;

public class ChannelAddMemberResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public ChannelAddMemberResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();

        ProtoChannelAddMember.ChannelAddMemberResponse.Builder builder = (ProtoChannelAddMember.ChannelAddMemberResponse.Builder) message;
        Long roomId = builder.getRoomId();
        Long userId = builder.getUserId();
        builder.getRole();

        Log.i("GGGGGG", "ChannelAddMemberResponse 01handler: ");
        Realm realm = Realm.getDefaultInstance();
        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();

        if (realmRoom != null) {
            RealmChannelRoom realmChannelRoom = realmRoom.getChannelRoom();
            Log.i("GGGGGG", "ChannelAddMemberResponse 3handler: ");
            if (realmChannelRoom != null) {
                final RealmList<RealmMember> members = realmChannelRoom.getMembers();

                final RealmMember realmMember = new RealmMember();
                realmMember.setId(SUID.id().get());
                realmMember.setPeerId(userId);
                realmMember.setRole(builder.getRole().toString());
                // realmMember = realm.copyToRealm(realmMember);

                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        members.add(realmMember);
                    }
                });

                if (G.onChannelAddMember != null) {
                    Log.i("GGGGGG", "ChannelAddMemberResponse 3handler: ");
                    G.onChannelAddMember.onChannelAddMember(builder.getRoomId(), builder.getUserId(), builder.getRole());
                }
            }
        }

        Log.i("GGGGGG", "ChannelAddMemberResponse 0handler: ");
    }

    @Override
    public void timeOut() {
        super.timeOut();

        Log.i("GGGGGG", "ChannelAddMemberResponse timeOut: ");
        if (G.onChannelAddMember != null) {

            G.onChannelAddMember.onTimeOut();
        }

    }

    @Override
    public void error() {
        super.error();

        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        final int majorCode = errorResponse.getMajorCode();
        final int minorCode = errorResponse.getMinorCode();

        if (G.onChannelAddMember != null) {

            G.onChannelAddMember.onError(majorCode, minorCode);
        }

        Log.i("GGGGGG", "ChannelAddMemberResponse majorCode: " + majorCode);
        Log.i("GGGGGG", "ChannelAddMemberResponse minorCode: " + minorCode);
    }
}


