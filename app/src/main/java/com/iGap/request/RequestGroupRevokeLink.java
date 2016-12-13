
package com.iGap.request;

import com.iGap.proto.ProtoGroupRevokeLink;

public class RequestGroupRevokeLink {

    public void groupRevokeLink(long roomId) {
        ProtoGroupRevokeLink.GroupRevokeLink.Builder builder = ProtoGroupRevokeLink.GroupRevokeLink.newBuilder();
        builder.setRoomId(roomId);

        RequestWrapper requestWrapper = new RequestWrapper(324, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
