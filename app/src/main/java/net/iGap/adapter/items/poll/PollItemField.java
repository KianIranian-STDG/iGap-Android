package net.iGap.adapter.items.poll;

import net.iGap.proto.ProtoGlobal;

import java.io.Serializable;

public class PollItemField implements Serializable {
    public int id;
    public int orderId;
    public String imageUrl;
    public String value;
    public boolean clicked;
    public boolean clickable;
    public long sum;
    public String label;

    public PollItemField(ProtoGlobal.PollField pollField) {
        id = pollField.getId();
        imageUrl = pollField.getImageurl();
        clickable = pollField.getClickable();
        clicked = pollField.getClicked();
        orderId = pollField.getOrderid();
        sum = pollField.getSum();
        label = pollField.getLabel();
    }

}
