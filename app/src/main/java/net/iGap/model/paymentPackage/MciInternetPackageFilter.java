package net.iGap.model.paymentPackage;

import com.google.gson.annotations.SerializedName;

public class MciInternetPackageFilter {

    @SerializedName("category")
    private FilterCategory category;
    @SerializedName("id")
    private String id;

    public FilterCategory getCategory() {
        return category;
    }

    public String getId() {
        return id;
    }
}
