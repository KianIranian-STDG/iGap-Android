package com.iGap.response;

import com.iGap.proto.ProtoChannelKickModerator;
import com.iGap.proto.ProtoError;
import com.iGap.proto.ProtoGlobal;
import com.iGap.realm.RealmMember;
import com.iGap.realm.RealmRoom;
import com.iGap.realm.RealmRoomFields;

import io.realm.Realm;
import io.realm.RealmList;

import static com.iGap.G.onChannelKickModerator;

public class ChannelKickModeratorResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public ChannelKickModeratorResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();

        ProtoChannelKickModerator.ChannelKickModeratorResponse.Builder builder = (ProtoChannelKickModerator.ChannelKickModeratorResponse.Builder) message;

        Realm realm = Realm.getDefaultInstance();
        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, builder.getRoomId()).findFirst();

        if (realmRoom != null) {
            RealmList<RealmMember> realmMembers = realmRoom.getChannelRoom().getMembers();

            for (final RealmMember member : realmMembers) {
                if (member.getPeerId() == builder.getMemberId()) {
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            member.setRole(ProtoGlobal.ChannelRoom.Role.MEMBER.toString());
                        }
                    });

                    if (onChannelKickModerator != null) {
                        onChannelKickModerator.onChannelKickModerator(builder.getRoomId(), builder.getMemberId());
                    }
                    break;
                }
            }
        }
        realm.close();

    }

    @Override
    public void timeOut() {
        super.timeOut();
        onChannelKickModerator.onTimeOut();
    }

    @Override
    public void error() {
        super.error();

        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();

        onChannelKickModerator.onError(majorCode, minorCode);
    }
}


