package net.iGap.adapter.items.discovery;

import com.google.gson.annotations.SerializedName;

import net.iGap.proto.ProtoGlobal;

import java.io.Serializable;

public class DiscoveryItemField implements Serializable {

    @SerializedName("id")
    public int id;

    @SerializedName("orderId")
    public int orderId;

    @SerializedName("imageUrl")
    public String imageUrl;

    @SerializedName("value")
    public String value;

    @SerializedName("actionType")
    public ProtoGlobal.DiscoveryField.ButtonActionType actionType;

    @SerializedName("refresh")
    public boolean refresh;

    @SerializedName("agreementSlug")
    public String agreementSlug;

    @SerializedName("agreement")
    public boolean agreement;

    @SerializedName("param")
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
