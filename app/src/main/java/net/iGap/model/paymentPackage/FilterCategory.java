package net.iGap.model.paymentPackage;

import com.google.gson.annotations.SerializedName;

public class FilterCategory {
    @SerializedName("type")
    private String type;
    @SerializedName("sub_type")
    private String subType;
    @SerializedName("value")
    private float value;

    public String getType() {
        return type;
    }

    public String getSubType() {
        return subType;
    }

    public float getValue() {
        return value;
    }
}
