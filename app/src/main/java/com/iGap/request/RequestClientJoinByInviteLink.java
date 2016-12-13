
package com.iGap.request;

import com.iGap.proto.ProtoClientJoinByInviteLink;

public class RequestClientJoinByInviteLink {

    public void clientJoinByInviteLink(String inviteToken) {
        ProtoClientJoinByInviteLink.ClientJoinByInviteLink.Builder builder = ProtoClientJoinByInviteLink.ClientJoinByInviteLink.newBuilder();
        builder.setInviteToken(inviteToken);

        RequestWrapper requestWrapper = new RequestWrapper(608, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
