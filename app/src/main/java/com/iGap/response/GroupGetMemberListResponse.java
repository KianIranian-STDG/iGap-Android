package com.iGap.response;

import android.util.Log;

import com.iGap.G;
import com.iGap.module.SUID;
import com.iGap.proto.ProtoGroupGetMemberList;
import com.iGap.realm.RealmMember;
import com.iGap.realm.RealmMemberFields;
import com.iGap.realm.RealmRoom;
import com.iGap.realm.RealmRoomFields;

import io.realm.Realm;
import io.realm.RealmList;

public class GroupGetMemberListResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public GroupGetMemberListResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        final ProtoGroupGetMemberList.GroupGetMemberListResponse.Builder builder = (ProtoGroupGetMemberList.GroupGetMemberListResponse.Builder) message;
        final RealmList<RealmMember> newMembers = new RealmList<>();
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, Long.parseLong(identity)).findFirst();
                RealmList<RealmMember> realmMembers = realmRoom.getGroupRoom().getMembers();

                for (ProtoGroupGetMemberList.GroupGetMemberListResponse.Member member : builder.getMemberList()) {
                    RealmMember realmMember = realm.where(RealmMember.class).equalTo(RealmMemberFields.PEER_ID, member.getUserId()).findFirst();
                    boolean newUser = false;
                    if (realmMember == null) {
                        newUser = true;
                        realmMember = realm.createObject(RealmMember.class);
                    }
                    realmMember.setId(SUID.id().get());
                    realmMember.setRole(member.getRole().toString());
                    realmMember.setPeerId(member.getUserId());

                    if (newUser) {
                        newMembers.add(realmMember);
                    }
                }
                if (realmMembers != null && !realmMembers.isEmpty()) {
                    Log.i("III", "1 a realmMembers : " + realmMembers);
                    for (RealmMember member : newMembers) {
                        Log.i("III", "1 b member : " + member);
                        realmMembers.add(member);
                    }
                    realmRoom.getGroupRoom().setMembers(realmMembers);
                } else {
                    Log.i("III", "2 newMembers : " + newMembers);
                    realmRoom.getGroupRoom().setMembers(newMembers);
                }


            }
        });

        realm.close();

        G.onGroupGetMemberList.onGroupGetMemberList(builder.getMemberList());
    }

    @Override
    public void timeOut() {
        super.timeOut();
    }

    @Override
    public void error() {
        super.error();
    }
}


