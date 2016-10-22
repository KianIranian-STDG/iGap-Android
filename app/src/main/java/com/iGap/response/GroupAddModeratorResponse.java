package com.iGap.response;

import android.util.Log;

import com.iGap.G;
import com.iGap.proto.ProtoGlobal;
import com.iGap.proto.ProtoGroupAddModerator;
import com.iGap.realm.RealmGroupRoom;
import com.iGap.realm.RealmMember;
import com.iGap.realm.RealmRoom;
import com.iGap.realm.RealmRoomFields;

import io.realm.Realm;
import io.realm.RealmList;

public class GroupAddModeratorResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public GroupAddModeratorResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }


    @Override
    public void handler() {

        Log.e("ddd", "handler : " + message);
        final ProtoGroupAddModerator.GroupAddModeratorResponse.Builder builder = (ProtoGroupAddModerator.GroupAddModeratorResponse.Builder) message;
        builder.getRoomId();
        builder.getMemberId();


        Realm realm = Realm.getDefaultInstance();

        final RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, builder.getRoomId()).findFirst();

        if (realmRoom != null) {

            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmGroupRoom realmGroupRoom = realmRoom.getGroupRoom();
                    RealmList<RealmMember> realmMemberRealmList = realmGroupRoom.getMembers();

                    for (RealmMember member : realmMemberRealmList) {
                        if (member.getPeerId() == builder.getMemberId()) {
                            member.setRole(ProtoGlobal.GroupRoom.Role.MODERATOR.toString());
                            G.onGroupAddModerator.onGroupAddModerator(builder.getRoomId(), builder.getMemberId());
                            break;
                        }
                    }
                }
            });
        }
        realm.close();


    }

    @Override
    public void timeOut() {
        Log.e("ddd", "timeOut : " + message);

    }

    @Override
    public void error() {
        Log.e("ddd", "error : " + message);
    }
}
