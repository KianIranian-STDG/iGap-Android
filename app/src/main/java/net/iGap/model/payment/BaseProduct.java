package net.iGap.model.payment;

import com.google.gson.annotations.SerializedName;

public class BaseProduct {
    @SerializedName("title")
    private String title;
    @SerializedName("description")
    private String description;

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
