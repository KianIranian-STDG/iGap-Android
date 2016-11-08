package com.iGap.request;

import com.iGap.proto.ProtoGlobal;
import com.iGap.proto.ProtoGroupSetAction;

public class RequestGroupSetAction {

    public void groupSetAction(long roomId, ProtoGlobal.ClientAction clientAction, int actionId) {

        ProtoGroupSetAction.GroupSetAction.Builder builder = ProtoGroupSetAction.GroupSetAction.newBuilder();
        builder.setRoomId(roomId);
        builder.setAction(clientAction);
        builder.setActionId(actionId);

        RequestWrapper requestWrapper = new RequestWrapper(210, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
