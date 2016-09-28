package com.iGap.response;

import android.util.Log;

import com.iGap.G;
import com.iGap.proto.ProtoGroupKickMember;
import com.iGap.realm.RealmMember;
import com.iGap.realm.RealmRoom;

import io.realm.Realm;
import io.realm.RealmList;

public class GroupKickMemberResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public GroupKickMemberResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }


    @Override
    public void handler() {

        ProtoGroupKickMember.GroupKickMemberResponse.Builder builder = (ProtoGroupKickMember.GroupKickMemberResponse.Builder) message;
        final long roomId = builder.getRoomId();
        final long memberId = builder.getMemberId();


        Realm realm = Realm.getDefaultInstance();
        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo("id", roomId).findFirst();
        if (realmRoom != null) {
            final RealmList<RealmMember> realmMembers = realmRoom.getGroupRoom().getMembers();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    for (int i = 0; i < realmMembers.size(); i++) {
                        RealmMember member = realmMembers.get(i);
                        if (member.getPeerId() == memberId) {
                            member.deleteFromRealm();          //delete member from database
                            G.onGroupKickMember.onGroupKickMember(roomId, memberId);
                            break;
                        }
                    }
                }
            });
        }

        realm.close();



    }

    @Override
    public void error() {
        Log.e("ddd", "hhhhhhhhhh      erore      " + message);
    }

    @Override
    public void timeOut() {

        Log.e("ddd", "hhhhhhhhhh      timout      " + message);
        super.timeOut();
    }
}
