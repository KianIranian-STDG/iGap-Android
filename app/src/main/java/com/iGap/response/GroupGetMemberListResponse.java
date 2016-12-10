package com.iGap.response;

import com.iGap.G;
import com.iGap.module.SUID;
import com.iGap.proto.ProtoGroupGetMemberList;
import com.iGap.realm.RealmMember;
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
        super.handler();
        final ProtoGroupGetMemberList.GroupGetMemberListResponse.Builder builder = (ProtoGroupGetMemberList.GroupGetMemberListResponse.Builder) message;
        final RealmList<RealmMember> newMembers = new RealmList<>();
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, Long.parseLong(identity)).findFirst();
                realmRoom.getGroupRoom().setParticipantsCountLabel(builder.getMemberCount() + "");
                RealmList<RealmMember> realmMembers = realmRoom.getGroupRoom().getMembers();

                for (ProtoGroupGetMemberList.GroupGetMemberListResponse.Member member : builder.getMemberList()) {
                    boolean newUser = true;
                    RealmMember realmMem = null;
                    for (RealmMember realmMember : realmMembers) {
                        if (realmMember.getPeerId() == member.getUserId()) {
                            newUser = false;
                            realmMem = realmMember;
                            break;
                        }
                    }

                    if (realmMem == null) {
                        realmMem = realm.createObject(RealmMember.class);
                    }
                    realmMem.setId(SUID.id().get());
                    realmMem.setRole(member.getRole().toString());
                    realmMem.setPeerId(member.getUserId());

                    if (newUser) {
                        newMembers.add(realmMem);
                    }
                }
                if (realmMembers != null && !realmMembers.isEmpty()) {
                    for (RealmMember member : realmMembers) {
                        newMembers.add(member);
                    }
                    realmRoom.getGroupRoom().setMembers(newMembers);
                } else {
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


