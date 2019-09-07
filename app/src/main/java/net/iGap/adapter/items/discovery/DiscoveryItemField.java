package net.iGap.adapter.items.discovery;

import net.iGap.proto.ProtoGlobal;

import java.io.Serializable;

public class DiscoveryItemField implements Serializable {
    public int id;
    public int orderId;
    public String imageUrl;
    public String value;
    public ProtoGlobal.DiscoveryField.ButtonActionType actionType;
    public boolean refresh;
    public String agreementSlug;
    public boolean agreement;
    public String param;

    public DiscoveryItemField(ProtoGlobal.DiscoveryField discoveryField) {
        id = discoveryField.getId();
        imageUrl = discoveryField.getImageurl();
        value = discoveryField.getValue();
        orderId = discoveryField.getOrderid();
        actionType = discoveryField.getActiontype();
        refresh = discoveryField.getRefresh();
        agreementSlug = discoveryField.getAgreementSlug();
        agreement = discoveryField.getAgreement();
        param = discoveryField.getParam();
    }
}
