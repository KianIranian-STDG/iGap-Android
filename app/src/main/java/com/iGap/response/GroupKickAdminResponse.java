package com.iGap.response;

import com.iGap.G;
import com.iGap.proto.ProtoError;
import com.iGap.proto.ProtoGlobal;
import com.iGap.proto.ProtoGroupKickAdmin;
import com.iGap.realm.RealmMember;
import com.iGap.realm.RealmRoom;
import com.iGap.realm.RealmRoomFields;
import io.realm.Realm;
import io.realm.RealmList;

public class GroupKickAdminResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public GroupKickAdminResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();
        ProtoGroupKickAdmin.GroupKickAdminResponse.Builder builder =
                (ProtoGroupKickAdmin.GroupKickAdminResponse.Builder) message;
        builder.getRoomId();
        builder.getMemberId();

        Realm realm = Realm.getDefaultInstance();
        RealmRoom realmRoom = realm.where(RealmRoom.class)
                .equalTo(RealmRoomFields.ID, builder.getRoomId())
                .findFirst();

        RealmRoom.updateRole(ProtoGlobal.Room.Type.GROUP, builder.getRoomId(), builder.getMemberId(), ProtoGlobal.GroupRoom.Role.MEMBER.toString());
        if (realmRoom != null) {
            RealmList<RealmMember> realmMembers = realmRoom.getGroupRoom().getMembers();
            for (final RealmMember member : realmMembers) {
                if (member.getPeerId() == builder.getMemberId()) {
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            member.setRole(ProtoGlobal.GroupRoom.Role.MEMBER.toString());
                        }
                    });
                    G.onGroupKickAdmin.onGroupKickAdmin(builder.getRoomId(), builder.getMemberId());
                    break;
                }
            }
        }

        realm.close();
    }

    @Override
    public void error() {
        super.error();
        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();

        G.onGroupKickAdmin.onError(majorCode, minorCode);
    }

    @Override
    public void timeOut() {
        super.timeOut();

        G.onGroupKickAdmin.onTimeOut();
    }
}
