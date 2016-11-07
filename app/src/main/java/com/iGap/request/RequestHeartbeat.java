package com.iGap.request;

import com.iGap.proto.ProtoHeartbeat;

public class RequestHeartbeat {

    public void heartBeat() {
        ProtoHeartbeat.Heartbeat.Builder builder = ProtoHeartbeat.Heartbeat.newBuilder();
        RequestWrapper requestWrapper = new RequestWrapper(3, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}

