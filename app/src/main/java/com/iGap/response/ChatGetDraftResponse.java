package com.iGap.response;

import android.util.Log;
import com.iGap.proto.ProtoChatGetDraft;
import com.iGap.realm.RealmRoom;

public class ChatGetDraftResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity; // here identity is roomId

    public ChatGetDraftResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);
        this.message = protoClass;
        this.identity = identity;
        this.actionId = actionId;
    }

    @Override public void handler() {

        ProtoChatGetDraft.ChatGetDraftResponse.Builder chatGetDraft =
            (ProtoChatGetDraft.ChatGetDraftResponse.Builder) message;

        Log.i("IIII", "chatGetDraft.getDraft() : " + chatGetDraft.getDraft());

        RealmRoom.convertAndSetDraft(Long.parseLong(identity), chatGetDraft.getDraft().getMessage(),
            chatGetDraft.getDraft().getReplyTo());
    }

    @Override public void timeOut() {
        super.timeOut();
        Log.i("IIII", "ChatGetDraftResponse timeOut : " + message);
    }

    @Override public void error() {
        super.error();
        Log.i("IIII", "ChatGetDraftResponse error : " + message);
    }
}


