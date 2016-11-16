package com.iGap.response;

import android.util.Log;

import com.iGap.G;
import com.iGap.proto.ProtoError;
import com.iGap.proto.ProtoGroupAddMember;
import com.iGap.realm.RealmGroupRoom;
import com.iGap.realm.RealmMember;
import com.iGap.realm.RealmRoom;
import com.iGap.realm.RealmRoomFields;

import io.realm.Realm;
import io.realm.RealmList;

public class GroupAddMemberResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public GroupAddMemberResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {

        final ProtoGroupAddMember.GroupAddMemberResponse.Builder response = (ProtoGroupAddMember.GroupAddMemberResponse.Builder) message;

        Long roomId = response.getRoomId();
        Long userId = response.getUserId();


        Realm realm = Realm.getDefaultInstance();
        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();

        if (realmRoom != null) {
            RealmGroupRoom realmGroupRoom = realmRoom.getGroupRoom();
            if (realmGroupRoom != null) {
                final RealmList<RealmMember> members = realmGroupRoom.getMembers();

                final RealmMember realmMember = new RealmMember();
                int autoIncrement = 0;
                if (realm.where(RealmMember.class).max("id") != null) {
                    autoIncrement = realm.where(RealmMember.class).max("id").intValue() + 1;
                }
                realmMember.setId(autoIncrement);
                realmMember.setPeerId(userId);
                realmMember.setRole(response.getRole().toString());
                // realmMember = realm.copyToRealm(realmMember);

                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        members.add(realmMember);
                    }
                });

                G.onGroupAddMember.onGroupAddMember(roomId, userId);


            }
        }


        realm.close();


        Log.i("XXX", "GroupAddMemberResponse handler : " + message);
    }

    @Override
    public void error() {

        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();
        Log.i("XXX", "GroupAddMemberResponse majorCode : " + majorCode);
        Log.i("XXX", "GroupAddMemberResponse minorCode : " + minorCode);

        G.onGroupAddMember.onError(majorCode, minorCode);
    }

    @Override
    public void timeOut() {
        super.timeOut();

        Log.i("XXX", "GroupAddMemberResponse timeout : ");

        G.onGroupAddMember.onError(0, 0); // for timeout


    }
}
