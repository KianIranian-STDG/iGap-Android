
package com.iGap.request;

import com.iGap.proto.ProtoClientCheckInviteLink;

public class RequestClientCheckInviteLink {

    public void clientCheckInviteLink(String inviteToken) {

        ProtoClientCheckInviteLink.ClientCheckInviteLink.Builder builder = ProtoClientCheckInviteLink.ClientCheckInviteLink.newBuilder();
        builder.setInviteToken(inviteToken);

        RequestWrapper requestWrapper = new RequestWrapper(607, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
