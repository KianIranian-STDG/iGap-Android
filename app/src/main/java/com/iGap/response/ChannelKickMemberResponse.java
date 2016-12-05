package com.iGap.response;

import com.iGap.G;
import com.iGap.proto.ProtoChannelKickMember;
import com.iGap.realm.RealmMember;
import com.iGap.realm.RealmRoom;
import com.iGap.realm.RealmRoomFields;

import io.realm.Realm;

import static com.iGap.module.MusicPlayer.roomId;

public class ChannelKickMemberResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public ChannelKickMemberResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();

        final ProtoChannelKickMember.ChannelKickMemberResponse.Builder builder = (ProtoChannelKickMember.ChannelKickMemberResponse.Builder) message;

        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
                for (RealmMember realmMember : realmRoom.getChannelRoom().getMembers()) {
                    if (realmMember.getPeerId() == builder.getMemberId()) {
                        realmMember.deleteFromRealm();
                        break;
                    }
                }
            }
        });
        realm.close();

        if (G.onChannelKickMember != null) {
            G.onChannelKickMember.onChannelKickMember(builder.getRoomId(), builder.getMemberId());
        }

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


