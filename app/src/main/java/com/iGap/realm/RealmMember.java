package com.iGap.realm;

import com.iGap.module.SUID;
import com.iGap.proto.ProtoChannelGetMemberList;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RealmMember extends RealmObject {

    @PrimaryKey
    private long id;

    private long peerId;
    private String role;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getPeerId() {
        return peerId;
    }

    public void setPeerId(long peerId) {
        this.peerId = peerId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }


    public static void convertProtoMemberListToRealmMember(final ProtoChannelGetMemberList.ChannelGetMemberListResponse.Builder builder, final String identity) {
        final RealmList<RealmMember> newMembers = new RealmList<>();
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, Long.parseLong(identity)).findFirst();
                realmRoom.getChannelRoom().setParticipantsCountLabel(builder.getMemberCount() + "");
                RealmList<RealmMember> realmMembers = realmRoom.getChannelRoom().getMembers();

                for (ProtoChannelGetMemberList.ChannelGetMemberListResponse.Member member : builder.getMemberList()) {
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
                    realmRoom.getChannelRoom().setMembers(newMembers);
                } else {
                    realmRoom.getChannelRoom().setMembers(newMembers);
                }
            }
        });

        realm.close();
    }

}
