package com.iGap.response;

import android.util.Log;
import com.iGap.G;
import com.iGap.proto.ProtoError;
import com.iGap.proto.ProtoGlobal;
import com.iGap.proto.ProtoGroupKickModerator;
import com.iGap.realm.RealmMember;
import com.iGap.realm.RealmRoom;
import com.iGap.realm.RealmRoomFields;
import io.realm.Realm;
import io.realm.RealmList;

public class GroupKickModeratorResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public GroupKickModeratorResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override public void handler() {

        ProtoGroupKickModerator.GroupKickModeratorResponse.Builder builder =
            (ProtoGroupKickModerator.GroupKickModeratorResponse.Builder) message;
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
                        @Override public void execute(Realm realm) {
                            member.setRole(ProtoGlobal.GroupRoom.Role.MEMBER.toString());
                        }
                    });

                    G.onGroupKickModerator.onGroupKickModerator(builder.getRoomId(),
                        builder.getMemberId());
                    break;
                }
            }
        }
    }

    @Override public void error() {

        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();
        Log.i("XXX", "GroupKickModeratorResponse majorCode : " + majorCode);
        Log.i("XXX", "GroupKickModeratorResponse minorCode : " + minorCode);

        G.onGroupKickMember.onError(majorCode, minorCode);
    }
}
