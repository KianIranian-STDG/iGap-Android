/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package net.iGap.response;

import android.os.Handler;
import android.os.Looper;
import io.realm.Realm;
import io.realm.RealmList;
import java.util.ArrayList;
import java.util.List;
import net.iGap.G;
import net.iGap.module.SUID;
import net.iGap.proto.ProtoGroupGetMemberList;
import net.iGap.realm.RealmMember;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.realm.RealmRegisteredInfoFields;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomFields;

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

    @Override public void handler() {
        super.handler();

        final ProtoGroupGetMemberList.GroupGetMemberListResponse.Builder builder = (ProtoGroupGetMemberList.GroupGetMemberListResponse.Builder) message;

        final RealmList<RealmMember> newMembers = new RealmList<>();

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override public void run() {
                final Realm realm = Realm.getDefaultInstance();
                final List<ProtoGroupGetMemberList.GroupGetMemberListResponse.Member> members = new ArrayList<ProtoGroupGetMemberList.GroupGetMemberListResponse.Member>();
                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override public void execute(Realm realm) {

                        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, Long.parseLong(identity)).findFirst();
                        if (realmRoom != null) {

                            //   realmRoom.getGroupRoom().setParticipantsCountLabel(builder.getMemberCount() + "");

                            members.clear();
                            newMembers.clear();
                            for (ProtoGroupGetMemberList.GroupGetMemberListResponse.Member member : builder.getMemberList()) {

                                final RealmRegisteredInfo realmRegisteredInfo = realm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, member.getUserId()).findFirst();
                                if (realmRegisteredInfo != null) {
                                    RealmMember realmMem = realm.createObject(RealmMember.class, SUID.id().get());
                                    realmMem.setRole(member.getRole().toString());
                                    realmMem.setPeerId(member.getUserId());
                                    newMembers.add(realmMem);
                                } else {
                                    members.add(member);
                                }
                            }

                            newMembers.addAll(0, realmRoom.getGroupRoom().getMembers());
                            realmRoom.getGroupRoom().setMembers(newMembers);
                        }
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override public void onSuccess() {
                        realm.close();
                        if (G.onGroupGetMemberList != null) G.onGroupGetMemberList.onGroupGetMemberList(members);
                    }
                }, new Realm.Transaction.OnError() {
                    @Override public void onError(Throwable error) {
                        realm.close();
                    }
                });
            }
        });
    }

    @Override public void timeOut() {
        super.timeOut();
    }

    @Override public void error() {
        super.error();
    }
}


