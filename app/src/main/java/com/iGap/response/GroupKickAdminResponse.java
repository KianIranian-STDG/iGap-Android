package com.iGap.response;

import android.util.Log;

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

        ProtoGroupKickAdmin.GroupKickAdminResponse.Builder builder =
                (ProtoGroupKickAdmin.GroupKickAdminResponse.Builder) message;
        builder.getRoomId();
        builder.getMemberId();

        Realm realm = Realm.getDefaultInstance();
        RealmRoom realmRoom = realm.where(RealmRoom.class)
                .equalTo(RealmRoomFields.ID, builder.getRoomId())
                .findFirst();

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

        Log.e("ddd", "hhhhhhhhhh      " + builder.getRoomId() + "   " + builder.getMemberId());
    }

    @Override
    public void error() {

        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();
        Log.i("XXX", "GroupKickAdminResponse majorCode : " + majorCode);
        Log.i("XXX", "GroupKickAdminResponse minorCode : " + minorCode);

        G.onGroupAddMember.onError(majorCode, minorCode);
    }

    @Override
    public void timeOut() {

        Log.e("XXX", "GroupKickAdminResponse      timout      " + message);
        super.timeOut();
    }
}
