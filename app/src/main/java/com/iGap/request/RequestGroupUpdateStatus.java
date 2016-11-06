package com.iGap.request;

import android.util.Log;
import com.iGap.proto.ProtoGlobal;
import com.iGap.proto.ProtoGroupUpdateStatus;

public class RequestGroupUpdateStatus {

    public void groupUpdateStatus(long roomId, long messageId, ProtoGlobal.RoomMessageStatus status) {

        ProtoGroupUpdateStatus.GroupUpdateStatus.Builder builder = ProtoGroupUpdateStatus.GroupUpdateStatus.newBuilder();
        builder.setRoomId(roomId);
        builder.setMessageId(messageId);
        builder.setStatus(status);

        Log.i("III", "roomId : " + roomId);
        Log.i("III", "messageId : " + messageId);

        RequestWrapper requestWrapper = new RequestWrapper(311, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}

