package net.iGap.adapter.items.poll;

import net.iGap.proto.ProtoGlobal;

import java.util.ArrayList;

public class PollItem {
    public String scale;
    public ArrayList<PollItemField> pollItemFields;
    public int model;

    public PollItem(ProtoGlobal.Poll poll) {
        model = poll.getModelValue();
        pollItemFields = new ArrayList<>();
        for (ProtoGlobal.PollField pollField : poll.getPollfieldsList()) {
            pollItemFields.add(new PollItemField(pollField));
        }
        scale = poll.getScale();
    }

}
