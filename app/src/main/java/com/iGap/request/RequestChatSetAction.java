package com.iGap.request;

import android.util.Log;

import com.iGap.proto.ProtoChatSetAction;
import com.iGap.proto.ProtoGlobal;

public class RequestChatSetAction {

    public void chatSetAction(long roomId, ProtoGlobal.ClientAction clientAction, int actionId) {
        ProtoChatSetAction.ChatSetAction.Builder builder = ProtoChatSetAction.ChatSetAction.newBuilder();
        builder.setRoomId(roomId);
        builder.setAction(clientAction);
        builder.setActionId(actionId);
        Log.i("YYY", "RequestChatSetAction : " + clientAction);
        RequestWrapper requestWrapper = new RequestWrapper(210, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
