package com.iGap.response;

import android.util.Log;
import com.iGap.G;
import com.iGap.proto.ProtoError;
import com.iGap.proto.ProtoGlobal;
import com.iGap.proto.ProtoGroupAddAdmin;
import com.iGap.realm.RealmGroupRoom;
import com.iGap.realm.RealmMember;
import com.iGap.realm.RealmRoom;
import com.iGap.realm.RealmRoomFields;
import io.realm.Realm;
import io.realm.RealmList;

public class GroupAddAdminResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public GroupAddAdminResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override public void handler() {

        final ProtoGroupAddAdmin.GroupAddAdminResponse.Builder builder =
            (ProtoGroupAddAdmin.GroupAddAdminResponse.Builder) message;
        builder.getRoomId();
        builder.getMemberId();

        // RealmRoom , RealmGroupRoom , RealmList<RealmMember> wher id =memberId , role = ADMIN


        Log.e("dddd", builder.getRoomId() + "    xxxxxxx");

        Realm realm = Realm.getDefaultInstance();

        final RealmRoom realmRoom = realm.where(RealmRoom.class)
            .equalTo(RealmRoomFields.ID, builder.getRoomId())
            .findFirst();

        if (realmRoom != null) {

            realm.executeTransaction(new Realm.Transaction() {
                @Override public void execute(Realm realm) {
                    RealmGroupRoom realmGroupRoom = realmRoom.getGroupRoom();
                    RealmList<RealmMember> realmMemberRealmList = realmGroupRoom.getMembers();

                    for (RealmMember member : realmMemberRealmList) {
                        if (member.getPeerId() == builder.getMemberId()) {
                            member.setRole(ProtoGlobal.GroupRoom.Role.ADMIN.toString());
                            G.onGroupAddAdmin.onGroupAddAdmin(builder.getRoomId(),
                                builder.getMemberId());
                            break;
                        }
                    }
                }
            });
        }

        realm.close();

    }

    @Override public void error() {
        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();
        Log.i("XXX", "GroupAddAdminResponse majorCode : " + majorCode);
        Log.i("XXX", "GroupAddAdminResponse minorCode : " + minorCode);

        G.onGroupAddAdmin.onError(majorCode, minorCode);
    }
}
