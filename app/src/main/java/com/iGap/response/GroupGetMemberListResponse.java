/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package com.iGap.response;

import android.os.Handler;
import android.os.Looper;
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

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override public void run() {
                final Realm realm = Realm.getDefaultInstance();

                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override public void execute(Realm realm) {

                        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, Long.parseLong(identity)).findFirst();
                        if (realmRoom != null) {

                            realmRoom.getGroupRoom().setParticipantsCountLabel(builder.getMemberCount() + "");

                            int i = 0;
                            newMembers.addAll(realmRoom.getGroupRoom().getMembers());
                            for (ProtoGroupGetMemberList.GroupGetMemberListResponse.Member member : builder.getMemberList()) {
                                i += 1;
                                RealmMember realmMem = realm.createObject(RealmMember.class, SUID.id().get());
                                realmMem.setRole(member.getRole().toString());
                                realmMem.setPeerId(member.getUserId());
                                newMembers.add(realmMem);
                            }

                            realmRoom.getGroupRoom().setMembers(newMembers);
                        }
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override public void onSuccess() {
                        realm.close();
                        if (G.onGroupGetMemberList != null) G.onGroupGetMemberList.onGroupGetMemberList(builder.getMemberList());
                    }
                }, new Realm.Transaction.OnError() {
                    @Override public void onError(Throwable error) {
                        realm.close();
                    }
                });
            }
        });
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


