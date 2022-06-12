package net.iGap.request;

import net.iGap.proto.ProtoClientRoomChangeOwner;
import net.iGap.proto.ProtoGroupAddAdmin;

public class RequestClientRoomChangeOwner {

    public void roomChangeOwner(long roomId, long userId) {

        ProtoClientRoomChangeOwner.ClientRoomChangeOwner.Builder builder = ProtoClientRoomChangeOwner.ClientRoomChangeOwner.newBuilder();
        builder.setRoomId(roomId);
        builder.setUserId(userId);



        RequestWrapper requestWrapper = new RequestWrapper(626, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
