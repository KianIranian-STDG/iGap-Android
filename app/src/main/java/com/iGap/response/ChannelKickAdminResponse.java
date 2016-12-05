package com.iGap.response;

import com.iGap.G;
import com.iGap.proto.ProtoChannelKickAdmin;
import com.iGap.proto.ProtoGlobal;
import com.iGap.realm.RealmMember;
import com.iGap.realm.RealmRoom;
import com.iGap.realm.RealmRoomFields;

import io.realm.Realm;
import io.realm.RealmList;

public class ChannelKickAdminResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public ChannelKickAdminResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();

        ProtoChannelKickAdmin.ChannelKickAdminResponse.Builder builder = (ProtoChannelKickAdmin.ChannelKickAdminResponse.Builder) message;

        Realm realm = Realm.getDefaultInstance();
        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, builder.getRoomId()).findFirst();
        if (realmRoom != null) {
            RealmList<RealmMember> realmMembers = realmRoom.getChannelRoom().getMembers();

            for (final RealmMember member : realmMembers) {
                if (member.getPeerId() == builder.getMemberId()) {
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            member.setRole(ProtoGlobal.GroupRoom.Role.MEMBER.toString());
                        }
                    });

                    if (G.onChannelKickAdmin != null) {
                        G.onChannelKickAdmin.onChannelKickAdmin(builder.getRoomId(), builder.getMemberId());
                    }
                    break;
                }
            }
        }
        realm.close();
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


