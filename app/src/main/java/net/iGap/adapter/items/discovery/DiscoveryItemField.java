package net.iGap.adapter.items.discovery;

import net.iGap.proto.ProtoGlobal;

public class DiscoveryItemField {
    public int id;
    public int orderId;
    public String imageUrl;
    public String value;
    public ProtoGlobal.DiscoveryField.ButtonActionType actionType;

    public DiscoveryItemField(ProtoGlobal.DiscoveryField discoveryField) {
        id = discoveryField.getId();
        imageUrl=  discoveryField.getImageurl();
        value = discoveryField.getValue();
        orderId = discoveryField.getOrderid();
        actionType = discoveryField.getActiontype();
    }
}
