package com.iGap.response;

import android.content.Intent;
import android.util.Log;

import com.iGap.G;
import com.iGap.activitys.ActivityChat;
import com.iGap.proto.ProtoChatGetRoom;

public class ChatGetRoomResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public ChatGetRoomResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.actionId = actionId;
        this.message = protoClass;
        this.identity = identity;
    }


    @Override
    public void handler() {
        Log.i("XXX", "ChatGetRoomResponse handler");
        ProtoChatGetRoom.ChatGetRoomResponse.Builder chatGetRoomResponse = (ProtoChatGetRoom.ChatGetRoomResponse.Builder) message;
        Intent intent = new Intent(G.context, ActivityChat.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("RoomId", chatGetRoomResponse.getRoomId());
        G.context.startActivity(intent);
    }

    @Override
    public void timeOut() {
    }

    @Override
    public void error() {
    }
}


