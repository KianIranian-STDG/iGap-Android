/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/


package com.iGap.realm;

import android.os.Handler;
import com.iGap.G;
import com.iGap.module.SUID;
import com.iGap.proto.ProtoChannelGetMemberList;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RealmMember extends RealmObject {

    @PrimaryKey private long id;

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

        new Handler(G.currentActivity.getMainLooper()).post(new Runnable() {
            @Override public void run() {
                final Realm realm = Realm.getDefaultInstance();

                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override public void execute(Realm realm) {
                        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, Long.parseLong(identity)).findFirst();
                        realmRoom.getChannelRoom().setParticipantsCountLabel(builder.getMemberCount() + "");
                        RealmList<RealmMember> realmMembers = realmRoom.getChannelRoom().getMembers();

                        if (realmRoom != null) {
                            for (ProtoChannelGetMemberList.ChannelGetMemberListResponse.Member member : builder.getMemberList()) {

                                RealmMember realmMem = realm.createObject(RealmMember.class, SUID.id().get());
                                realmMem.setRole(member.getRole().toString());
                                realmMem.setPeerId(member.getUserId());
                                newMembers.add(realmMem);
                                realmRoom.getChannelRoom().setMembers(newMembers);
                            }
                        }

                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override public void onSuccess() {

                        realm.close();
                    }
                }, new Realm.Transaction.OnError() {
                    @Override public void onError(Throwable error) {
                        realm.close();
                    }
                });
            }
        });
    }

}
