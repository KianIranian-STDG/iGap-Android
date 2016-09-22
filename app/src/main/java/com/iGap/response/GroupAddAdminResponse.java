package com.iGap.response;

import android.util.Log;

import com.iGap.proto.ProtoGlobal;
import com.iGap.proto.ProtoGroupAddAdmin;
import com.iGap.realm.RealmGroupRoom;
import com.iGap.realm.RealmMember;
import com.iGap.realm.RealmRoom;

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


    @Override
    public void handler() {

        final ProtoGroupAddAdmin.GroupAddAdminResponse.Builder builder = (ProtoGroupAddAdmin.GroupAddAdminResponse.Builder) message;
        builder.getRoomId();
        builder.getMemberId();

        // RealmRoom , RealmGroupRoom , RealmList<RealmMember> wher id =memberId , role = ADMIN


        Log.e("dddd", builder.getRoomId() + "    xxxxxxx");

        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo("roomId", builder.getRoomId()).findFirst();
                RealmGroupRoom realmGroupRoom = realmRoom.getGroupRoom();
                RealmList<RealmMember> realmMemberRealmList = realmGroupRoom.getMembers();

                for (RealmMember member : realmMemberRealmList) {
                    if (member.getPeerId() == builder.getMemberId())
                        member.setRole(ProtoGlobal.GroupRoom.Role.ADMIN.toString());
                }
            }
        });

        realm.close();


    }

    @Override
    public void error() {

    }
}
