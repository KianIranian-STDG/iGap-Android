/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the Kianiranian Company - www.kianiranian.com
* All rights reserved.
*/

package net.iGap.response;

import net.iGap.proto.ProtoGroupUpdateDraft;
import net.iGap.realm.RealmRoom;

public class GroupUpdateDraftResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public GroupUpdateDraftResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);
        this.message = protoClass;
        this.identity = identity;
        this.actionId = actionId;
    }

    @Override
    public void handler() {
        super.handler();
        ProtoGroupUpdateDraft.GroupUpdateDraftResponse.Builder updateDraft = (ProtoGroupUpdateDraft.GroupUpdateDraftResponse.Builder) message;
        RealmRoom.convertAndSetDraft(updateDraft.getRoomId(), updateDraft.getDraft().getMessage(), updateDraft.getDraft().getReplyTo(), updateDraft.getDraft().getDraftTime());
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


