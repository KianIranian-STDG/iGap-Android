package com.iGap.response;

import com.iGap.proto.ProtoChatUpdateDraft;
import com.iGap.realm.RealmRoom;

public class ChatUpdateDraftResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public ChatUpdateDraftResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);
        this.message = protoClass;
        this.identity = identity;
        this.actionId = actionId;
    }

    @Override
    public void handler() {
        ProtoChatUpdateDraft.ChatUpdateDraftResponse.Builder updateDraft =
                (ProtoChatUpdateDraft.ChatUpdateDraftResponse.Builder) message;

       /*
        * if another account get UpdateDraftResponse set draft to RealmRoom
        */
        if (updateDraft.getResponse().getId().isEmpty()) {
            RealmRoom.convertAndSetDraft(updateDraft.getRoomId(),
                    updateDraft.getDraft().getMessage(), updateDraft.getDraft().getReplyTo());
        }
    }

    @Override
    public void timeOut() {
        super.timeOut();
    }

    @Override
    public void error() {
        super.error();
    }
}


