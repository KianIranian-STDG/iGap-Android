package com.iGap.response;

import com.iGap.G;
import com.iGap.proto.ProtoChannelGetMemberList;
import com.iGap.realm.RealmMember;

public class ChannelGetMemberListResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public ChannelGetMemberListResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();

        ProtoChannelGetMemberList.ChannelGetMemberListResponse.Builder builder = (ProtoChannelGetMemberList.ChannelGetMemberListResponse.Builder) message;

        RealmMember.convertProtoMemberListToRealmMember(builder, identity);

        if (G.onChannelGetMemberList != null) {
            G.onChannelGetMemberList.onChannelGetMemberList(builder.getMemberList());
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


